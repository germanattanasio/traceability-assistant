/**
 * Copyright 2015 UNICEN. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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


/**
 * The Class LSARunner.
 */
public class LSARunner {

	/** The Constant SSPACE. */
	private static final String SSPACE = ".sspace";
	
	/** The concerns. */
	private List<Entity> concerns;
	
	/** The design decision. */
	private List<Entity> designDecision;
	
	/** The dimension. */
	private int dimension;
	
	/** The threshold. */
	private double threshold;
	
	/** The logger. */
	private static Log logger = LogFactory.getLog(LSARunner.class);
	
	/** The sspace. */
	private File sspace;
	
	/**
	 * Instantiates a new LSA runner.
	 *
	 * @param concerns the concerns
	 * @param designDecision the design decision
	 * @param dimension the dimension
	 * @param threshold the threshold
	 */
	public LSARunner(List<Entity> concerns, List<Entity> designDecision,int dimension, double threshold) {
		this.concerns = concerns;
		this.designDecision = designDecision;
		this.dimension = dimension;
		this.threshold = threshold;
	}

	
	/**
	 * Gets the traceability.
	 *
	 * @return the traceability
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws RuntimeException the runtime exception
	 */
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

	/**
	 * Discover traceability.
	 *
	 * @param sspaceFile the sspace file
	 * @return the traceability document
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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
			logger.info(sw.shortSummary());
			
			//save the traceability results like: untraced count, links, traced count, etc...
			ret.setLinks(links);
			ret.setUntracedConcernCount(untracedCount);
			ret.setTracedConcernCount(concerns.size()-untracedCount);
			return ret;
	}

	/**
	 * Prints the vector.
	 *
	 * @param vector1 the vector1
	 * @param size the size
	 * @return the string
	 */
	@SuppressWarnings("unused")
	private String printVector(final DoubleVector vector1, final int size) {
		String value = "";
		for (int i = 0; i < size; i++) {
			value+=((double)Math.round(vector1.getValue(i)*100) /100) + " ";
		}
		return value;
	}


	/**
	 * Creates the lsa space.
	 *
	 * @param documentsFile the documents file
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private File createLsaSpace(File documentsFile) throws IOException {
		File sspaceFile = File.createTempFile("sspace_", SSPACE);
		
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
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		List<Entity> testConcerns = Utils.transformedList(FileUtils.readLines(new File(args[0])), new String2Concern(true));
		List<Entity> testDesignDecisions = Utils.transformedList(FileUtils.readLines(new File(args[1])), new String2Concern(true));
		
		LSARunner runner = new LSARunner(testConcerns, testDesignDecisions, 100,0.75 );
		TraceabilityDocument doc = runner.getTraceability();
		System.out.println(doc);
    }
}
