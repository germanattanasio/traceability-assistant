package edu.isistan.carcha.lsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StopWatch;

import edu.isistan.carcha.lsa.model.Entity;
import edu.isistan.carcha.lsa.model.TraceabilityDocument;
import edu.isistan.carcha.lsa.model.TraceabilityLink;
import edu.isistan.carcha.util.String2Concern;
import edu.isistan.carcha.util.Utils;
import edu.ucla.sspace.common.DocumentVectorBuilder;
import edu.ucla.sspace.common.Similarity;
import edu.ucla.sspace.common.StaticSemanticSpace;
import edu.ucla.sspace.vector.CompactSparseVector;
import edu.ucla.sspace.vector.DoubleVector;


public class LSARunner {

	private static final String SSPACE = ".sspace";
	private List<Entity> concerns;
	private List<Entity> designDecision;
	private int dimension;
	private double threshold;
	
	private static Log logger = LogFactory.getLog(LSARunner.class);
	private File sspace;
	public LSARunner(List<Entity> concerns, List<Entity> designDecision,int dimension, double threshold) {
		this.concerns = concerns;
		this.designDecision = designDecision;
		this.dimension = dimension;
		this.threshold = threshold;
	}

	
	public TraceabilityDocument getTraceability() throws IllegalArgumentException, RuntimeException{
		
		if (designDecision == null || designDecision.isEmpty())
			throw new IllegalArgumentException("designDecision can't be null or empty");
	
		if (concerns == null || concerns.isEmpty())
			throw new IllegalArgumentException("concerns can't be null or empty");
		
		//create the documents to build the LSA Space
		//the document will contain a line for each concern and design decision
		File documentsFile;
		try {
			documentsFile = Utils.createFileFromStringList(Utils.sum(concerns,designDecision));
			if (sspace == null) {
				try {
					sspace = File.createTempFile("lsa-"+concerns.hashCode()+designDecision.hashCode(),"-space.txt");
					FileUtils.copyFile(documentsFile,this.sspace);
					logger.info("sspace="+this.sspace.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			

		} catch (IOException e) {
			throw new RuntimeException("Error creating the temporary file with the documents for the SSpace",e);
		}
		
		//build the LSA Space using the documents generated above
		File sspaceFile = null;
		try {
			sspaceFile = createLsaSpace(documentsFile);
		} catch(Exception e) {
			throw new RuntimeException("Error generating the SSpace",e);
		}
		//Measure the relation between concerns and design decisions
		//count the untraced and traced concerns
		TraceabilityDocument ret = null;
		try {
			ret = discoverTraceability(sspaceFile);
		} catch(Exception e) {
			throw new RuntimeException("Error discovering the traceability links",e);
		}
		//remove temporary files
		documentsFile.delete();
		sspaceFile.delete();
		
		return ret;
    }

	private TraceabilityDocument discoverTraceability(File sspaceFile) throws IOException {
			
			TraceabilityDocument ret = new TraceabilityDocument(concerns,designDecision);
			List<TraceabilityLink> links = new ArrayList<TraceabilityLink>();

			StaticSemanticSpace sspace = new StaticSemanticSpace(sspaceFile);
			
			//build the document vector to compare sentences
			DocumentVectorBuilder builder = new DocumentVectorBuilder(sspace);
			
			StopWatch sw = new StopWatch();
			sw.start("Start the traceability comparation");

			int untracedCount = 0;
			for (Entity req : concerns) {
				//create vector1
		    	DoubleVector vector1 = builder.buildVector(new BufferedReader(new StringReader(req.getFormattedLabel())) , new CompactSparseVector());
				
		    	boolean hasTrace = false;
		    	for (Entity arch : designDecision) {
		    		//create vector2
		    		DoubleVector vector2 = builder.buildVector(new BufferedReader(new StringReader(arch.getFormattedLabel())) , new CompactSparseVector());
					
			    	//Math round is WAY faster than DoubleFormat
			    	Double linkWeight = ((double)Math.round(Similarity.cosineSimilarity(vector1,vector2)*100) /100);
			    	
			    	//add the edge between the two nodes including the calculated weight
			    	if (linkWeight > threshold) {
			    		links.add(new TraceabilityLink(req, arch, linkWeight));
			    		hasTrace = true;
			    	}
		    	}
		    	if (!hasTrace) {
		    		untracedCount++;
		    	} 
		    }
	  		sw.stop();
			//logger.info(sw.shortSummary());
			
			//save the traceability results like: untraced count, links, traced count, etc...
			ret.setLinks(links);
			ret.setUntracedConcernCount(untracedCount);
			ret.setTracedConcernCount(concerns.size()-untracedCount);
			return ret;
	}

	@SuppressWarnings("unused")
	private String printVector(final DoubleVector vector1, final int size) {
		String value = "";
		for (int i = 0; i < size; i++) {
			value+=((double)Math.round(vector1.getValue(i)*100) /100) + " ";
		}
		return value;
	}


	private File createLsaSpace(File documentsFile) throws IOException {
		File sspaceFile = File.createTempFile("sspace_", SSPACE);
		
		//XXX:FIXME:TODO: add the stopwords!!!
    	String []params = {
    			"-d" , documentsFile.getAbsolutePath(),
    			"-n" , String.valueOf(dimension),
    			sspaceFile.getAbsolutePath()
    	};
    	
		LSA lsa = new LSA();
		try {
			lsa.run(params);
		} catch (Exception e) {
			throw new IOException("Error generating the sspace with LSA.",e);
		}
		return sspaceFile;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		List<Entity> testConcerns = Utils.transformedList(FileUtils.readLines(new File(args[0])), new String2Concern(true));
		List<Entity> testDesignDecisions = Utils.transformedList(FileUtils.readLines(new File(args[1])), new String2Concern(true));
		
		LSARunner runner = new LSARunner(testConcerns, testDesignDecisions, 100,0.75 );
		TraceabilityDocument doc = runner.getTraceability();
		System.out.println(doc);
    }
}
