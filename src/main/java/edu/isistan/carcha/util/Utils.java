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
package edu.isistan.carcha.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.isistan.carcha.concern.cdetector.DesignDecision;
import edu.isistan.carcha.lsa.GephiTraceability;
import edu.isistan.carcha.lsa.model.Entity;
import edu.isistan.carcha.lsa.model.Entity.NodeType;
import edu.isistan.carcha.lsa.model.TraceabilityDocument;
import edu.isistan.carcha.lsa.model.TraceabilityLink;

/**
 * The Class Utils.
 *
 * @author germanattanasio
 */
public class Utils {
	
	/** The Constant CARCHA_TYPE_SYSTEM. */
	public static final TypeSystemDescription CARCHA_TYPE_SYSTEM = TypeSystemDescriptionFactory.createTypeSystemDescription("TypeSystem"); 


	/** The logger. */
	private static final Log logger = LogFactory.getLog(Utils.class);

	/** The Constant TMP. */
	private static final String TMP = ".tmp";

	/**
	 * Calculate true negatives.
	 *
	 * @param golden the golden results
	 * @param result the result
	 * @return the number of true negatives
	 */
	public static int calculateTrueNegatives(TraceabilityDocument golden,TraceabilityDocument result) {
		
		int tp = 0;
		
		//add all the links we have in the golden file
		Set<Integer> goldenLinks = new HashSet<Integer>();
		for (TraceabilityLink tLink : golden.getLinks()) {
			goldenLinks.add(tLink.hashCode());
		}

		//add all the links we have in the result file
		Set<Integer> resultLinks = new HashSet<Integer>();
		for (TraceabilityLink tLink : result.getLinks()) {
			resultLinks.add(tLink.hashCode());
		}
		
		//for each possible ccc-ddd combination check if there is a link in golden or result
		for (Entity ccc : result.getConcerns()) {
			for (Entity ddd : result.getDesignDecisions()) {
				TraceabilityLink tl = new TraceabilityLink(ccc, ddd, 0.0);
				Integer tempLink = tl.hashCode();
				if (!goldenLinks.contains(tempLink) && !goldenLinks.contains(tempLink))
					tp++;
			}
		}
		
		return tp;
	}

	/**
	 * Creates the file from string list.
	 *
	 * @param documents the documents
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static File createFileFromStringList(List<String> documents) throws IOException {
		File file = null;
		try {
			file = File.createTempFile("documents_", TMP);
			FileWriter writer = new FileWriter(file);
			FileUtils.writeLines(file, documents, "\n");
			writer.close();
		} catch (IOException e) {
			logger.error("Error creating the temp file",e);
			throw e;
		}
		return file;
	}

	/**
	 * Extract the T annotation as a String list and remove duplicates.
	 *
	 * @param <T> the generic type
	 * @param filename the filename
	 * @param tsd the TypeSystems
	 * @param type the type
	 * @return the string list of coveredtext by the T annotations without duplicates
	 * @throws UIMAException the uIMA exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	 public static <T extends Annotation >List<String> extractCoveredTextAnnotations(String filename,final Class<T> type) throws UIMAException, IOException {
		 JCas jCas = JCasFactory.createJCas(filename, CARCHA_TYPE_SYSTEM);
		 List<String> ret = new ArrayList<String>();
		 HashSet<String> hs = new HashSet<String>();
		 
		 for (Annotation c : JCasUtil.select(jCas, type)) {
			 hs.add(c.getCoveredText());
		 }
		 ret.addAll(hs);
		 return ret;
	}
	
	/**
	 * Extracts crosscutting concerns from a DXMI file.
	 *
	 * @param reaFile the rea filename from where the crosscutting concerns will be extracted
	 * @return the crosscutting concerns list extracted from the dxmiFile
	 */
	public static List<Entity> extractCrosscuttingConcernsFromREA(String reaFile) {
		//TODO: get the crosscutting concerns from the REA file using REAssistant.
		return new ArrayList<Entity>();	
	}

	
	/**
	 * Extracts crosscutting concerns(ccc) from a text file where each ccc is<br>
	 * <code>
	 * (classification) \t\t (label)
	 * <code>
	 * @param fileName The file name
	 * @return The entity list
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static List<Entity> extractCrosscuttingConcernsFromTextFile(String fileName) throws IOException {
		List<Entity> ccc = Utils.transformedList(FileUtils.readLines(new File(fileName)),new String2Concern(true));
		return ccc;		
	}

	
	/**
	 * Extracts design decisions from a DXMI file.
	 *
	 * @param dxmiFile the dxmi filename from where the design decisions will be extracted
	 * @return the design decisions list extracted from the dxmiFile
	 */
	public static List<Entity> extractDesignDecisionsAsList(String dxmiFile) {
		List<Entity> ret = new ArrayList<Entity>();
		JCas jCas;

		try {
			File tempFile = File.createTempFile("annotations", ".xmi");
			FileUtils.copyFile(new File(dxmiFile), tempFile);
			jCas = JCasFactory.createJCas(tempFile.getAbsolutePath(),CARCHA_TYPE_SYSTEM);
			tempFile.delete();
		} catch (Exception e) {
			logger.error("There was an error with the DXMI file", e);
			return null;
		} 
		for (DesignDecision dd : JCasUtil.select(jCas, DesignDecision.class)) {
			Entity entity = new Entity(dd.getCoveredText(), dd.getTypex(), NodeType.DD);
			ret.add(entity);
        }
		return ret;		
	}
	
	/**
	 * Read the traceability object from a file.
	 *
	 * @param traceabilityFile the traceability file path
	 * @return the traceability document
	 * @throws ClassNotFoundException the class not found exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static TraceabilityDocument readTraceabilityFromFile(String traceabilityFile) 
			throws ClassNotFoundException, FileNotFoundException, IOException {
		File inputFile = new File(traceabilityFile);
		String traceJson = FileUtils.readFileToString(inputFile);
		Gson gson = new Gson();
		return gson.fromJson(traceJson,TraceabilityDocument.class);
	}

	/**
	 * Sum.
	 *
	 * @param concerns the concerns
	 * @param designDecision the design decision
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public static List<String> sum(List<Entity> concerns,List<Entity> designDecision) {
		List<String> ret = ListUtils.sum(Utils.transformedList(concerns, 
				new Entity2String()), 
				Utils.transformedList(designDecision, new Entity2String()));
		return ret;
	}

	
	/**
	 * Transformed list.
	 *
	 * @param list the list
	 * @param transformer the transformer
	 * @return the list
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List transformedList(List list,Transformer transformer) {
		List ret = new ArrayList<Entity>();
		for (Object item : list) {
			ret.add(transformer.transform(item));
		}
		return ret;
	}

	/**
	 * Write the traceability object to a file.
	 *
	 * @param doc the traceability object
	 * @param output the file path where the traceability will be written
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeTraceabilityToFile(TraceabilityDocument doc,String output) 
			throws FileNotFoundException, IOException {
		File outputFile = new File(output);
		Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
		String pretJson = prettyGson.toJson(doc);
		FileUtils.writeStringToFile(outputFile, pretJson);
	}

	/**
	 * Write traceability to gephi.
	 *
	 * @param result the result
	 * @param filename the filename
	 */
	public static void writeTraceabilityToGephi(TraceabilityDocument result, String filename) {
		GephiTraceability gephi = new GephiTraceability(filename);
		
		for (Entity c : result.getConcerns()) {
			gephi.addNode(c);
		} 
		for (Entity d : result.getDesignDecisions()) {
			gephi.addNode(d);
		}
		for(TraceabilityLink t : result.getLinks()) {
			gephi.addEdge(t.getConcernId(), t.getDesignDecisionId(), Double.valueOf(t.getWeight()));
		}
		gephi.saveGraph();
	}	
}
