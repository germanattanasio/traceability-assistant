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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StopWatch;

import edu.isistan.carcha.lsa.model.Entity;
import edu.isistan.carcha.lsa.model.Entity.NodeType;
import edu.ucla.sspace.common.DocumentVectorBuilder;
import edu.ucla.sspace.common.Similarity;
import edu.ucla.sspace.common.StaticSemanticSpace;
import edu.ucla.sspace.vector.CompactSparseVector;
import edu.ucla.sspace.vector.DoubleVector;

/**
 * The Class GephiTraceabilityComparator.
 */
public abstract class TraceabilityComparator {
	
	/** The Constant GRAPH. */
	private static final String GRAPH = "gexf";

	/** The sspace file name. */
	private String sspaceFileName;
	
	/** The output directory. */
	protected File outputDirectory;
	/**
	 * Creates the traceability comparator.
	 *
	 * @param type the type
	 * @return the traceability comparator
	 */
	private static TraceabilityComparator createTraceabilityComparator(String type) {
		if (type != null && type.equals(GRAPH))
			return new GephiTraceabilityComparator();
		else
			return new TextTraceabilityComparator();
	}
	
	
	
	/**
	 * Gets the sspace file name.
	 *
	 * @return the sspace file name
	 */
	public String getSspaceFileName() {
		return sspaceFileName;
	}



	/**
	 * Sets the sspace file name.
	 *
	 * @param sspaceFileName the new sspace file name
	 */
	public void setSspaceFileName(String sspaceFileName) {
		this.sspaceFileName = sspaceFileName;
	}



	/**
	 * The main method.
	 *
	 * @param args the main arguments there should be 4 arguments as described above
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		if (args == null || args.length != 6) {
			System.err.println("Error parsing the arguments.");
			System.err.println("Usage: TraceabilityLinkComparator <type> <SSpace directory> <req-concern File> <arq-concern File> <output Directory>");
			System.exit(0);
		}

		TraceabilityComparator graphGenerator = createTraceabilityComparator(args[0]);
		if (graphGenerator == null) {
			System.err.println(args[0]+" is not a valid type.");
			System.exit(0);
		}
		
		graphGenerator.run(args);
	}

	/**
	 * Run.
	 *
	 * @param args the args
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void run(String[] args) throws IOException {
		//read the requirement concerns and the architectural concerns
				List<String> reqConcerns = FileUtils.readLines(new File(args[2]));
				List<String> archConcerns = FileUtils.readLines(new File(args[3]));
				
				//where we will put the results
				String outputDirectory = args[4];


				double[] thresholds = { 0.70, 0.75, 0.80, 0.90 };

				//check if the space is a file o a directory
				//directory: run the traceability for each file in the directory
				//file: run the traceability
				File sspaceInput = new File(args[1]);
				List<File> filesToAnalyze = new ArrayList<File>();
				
				if (sspaceInput.isDirectory()) {
					for (File sspaceFile : sspaceInput.listFiles()) {
						if (sspaceFile.isFile()) {
							filesToAnalyze.add(sspaceFile);
						}
					}
				} else {
					filesToAnalyze.add(sspaceInput);
				}
				
				for (File sspaceFile : filesToAnalyze) {
					//build the document vector to compare sentences
					StaticSemanticSpace sspace = new StaticSemanticSpace(sspaceFile);
					DocumentVectorBuilder builder = new DocumentVectorBuilder(sspace);
					this.setOutputDirectory(new File(outputDirectory+File.separator+sspaceFile.getName()));
					for (Double threshold : thresholds) {
						this.setSspaceFileName(sspaceFile.getName());
						this.setThreshold(threshold);
						this.run(builder,reqConcerns,archConcerns);
					}
				}
	}

	/**
	 * Gets the output directory.
	 *
	 * @return the output directory
	 */
	public File getOutputDirectory() {
		return outputDirectory;
	}

	/**
	 * Sets the output directory.
	 *
	 * @param outputDirectory the new output directory
	 */
	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}


	/** The threshold. */
	private double threshold = 0.7;

	/** The untraced count. */
	private int untracedCount = 0;

	/**
	 * Adds the edge.
	 *
	 * @param req the from
	 * @param arch the to
	 * @param linkWeight the link weight
	 * @return the edge
	 */
	protected abstract String addEdge(Entity req, Entity arch, Double linkWeight);
	
	/**
	 * Adds a node to the graph.
	 *
	 * @param req the req
	 * @return the node
	 */
	protected abstract String addNode(Entity req);

	/**
	 * Gets the threshold.
	 *
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * Gets the untraced count.
	 *
	 * @return the untraced count
	 */
	public int getUntracedCount() {
		return untracedCount;
	}

	/**
	 * Inits the graph.
	 */
	protected abstract void init();
	
	/**
	 * Run.
	 *
	 * @param builder the Vector builder to construct vector using the sspace
	 * @param reqConcerns the requirement concerns
	 * @param archConcerns the architectural concerns
	 */
  	@SuppressWarnings("unused")
	private void run(DocumentVectorBuilder builder, List<String> reqConcerns,List<String> archConcerns) {
		StopWatch sw = new StopWatch();
		sw.start("Start the traceability comparation");
		init();
		int i = 0;
		int count = reqConcerns.size();
		this.untracedCount = 0;
		for (String lineForVector1 : reqConcerns) {
			Entity req = Entity.buildFromString(lineForVector1, NodeType.CC);
			addNode(req);
			//create vector 1
	    	DoubleVector vector1 = new CompactSparseVector();
	    	vector1 = builder.buildVector(new BufferedReader(new StringReader(req.getFormattedLabel())) , vector1);
	    	boolean hasTrace = false;
	    	for (String lineForVector2 : archConcerns) {
				Entity arch = Entity.buildFromString(lineForVector2, NodeType.DD);
				addNode(arch);
	    		//create vector 2
	    		DoubleVector vector2 = new CompactSparseVector();
		    	vector2 = builder.buildVector(new BufferedReader(new StringReader(arch.getFormattedLabel())) , vector2);

		    	//Math round is WAY faster than DoubleFormat
		    	Double linkWeight = ((double)Math.round(Similarity.cosineSimilarity(vector1,vector2)*1000) /1000);
		    	
		    	//add the edge between the two nodes including the calculated weight
		    	if (linkWeight > threshold) {
		    		addEdge(req,arch,linkWeight);
		    		hasTrace = true;
		    	}
	    	}
	    	if (!hasTrace) {
	    		this.untracedCount++;
	    	} 
	    }
  		sw.stop();
		System.out.println(sw.shortSummary());
		String filename = saveGraph();
	}


	/**
	 * Save graph.
	 *
	 * @return the string
	 */
	protected abstract String saveGraph();

	/**
	 * Sets the threshold.
	 *
	 * @param threshold the new threshold
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * Builds the output filename.
	 *
	 * @return the string
	 */
	protected String buildOutputFilename() {
		StringBuilder filename = new StringBuilder();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm") ;
		filename.append(outputDirectory);
		filename.append("/");
		filename.append("threshold-");
		filename.append(getThreshold());
		filename.append("-untraced-");
		filename.append(getUntracedCount());
		filename.append("-date-");
		filename.append(dateFormat.format(new Date()));
		return filename.toString();
	}
	
	/**
	 * Sets the untraced count.
	 *
	 * @param untracedCount the new untraced count
	 */
	public void setUntracedCount(int untracedCount) {
		this.untracedCount = untracedCount;
	}

}