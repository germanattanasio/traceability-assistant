package edu.isistan.carcha.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
 * @author germanattanasio
 *
 */
public class Utils {
	
	private static final String CARCHA_TYPE_SYSTEM = "TypeSystem";

	/** The Constant TMP. */
	private static final String TMP = ".tmp";
	
	/** The logger. */
	private static Log logger = LogFactory.getLog(Utils.class);

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
	 * Extracts annotations from a DXMI file.
	 *
	 * @param dxmiFile the dxmi filename from where the annotation will be extracted
	 * @return the annotation list extracted from the dxmiFile
	 */
	public static <T extends Annotation> List<Entity> annotationAsList(String dxmiFile,Class<T> Annotation, boolean typeLess) {
		List<Entity> ret = new ArrayList<Entity>();
		JCas jCas;

		try {
			File tempFile = File.createTempFile("annotations", ".xmi");
			FileUtils.copyFile(new File(dxmiFile), tempFile);
			jCas = JCasFactory.createJCas(tempFile.getAbsolutePath(), 
					TypeSystemDescriptionFactory
					.createTypeSystemDescription(CARCHA_TYPE_SYSTEM));
			tempFile.delete();
		} catch (Exception e) {
			logger.error("There was an error with the DXMI file", e);
			return null;
		} 
		for (T c : JCasUtil.select(jCas, Annotation)) {
			Entity entity = null;
			if ( c instanceof DesignDecision) {
				DesignDecision dd = (DesignDecision) c;
				entity = new Entity(dd.getCoveredText(), typeLess ? "tactic" : dd.getTypex(), NodeType.DD);
			} else {
				entity = new Entity(c.getCoveredText(),"tactic",NodeType.DD);
			}
			ret.add(entity);
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
	 * Read the traceability object from a file.
	 *
	 * @param traceabilityFile the traceability file path
	 * @return the traceability document
	 * @throws ClassNotFoundException the class not found exception
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static TraceabilityDocument readTraceabilityFromFile(String traceabilityFile) 
			throws ClassNotFoundException, FileNotFoundException, IOException {
		File inputFile = new File(traceabilityFile);
		String traceJson = FileUtils.readFileToString(inputFile);
		Gson gson = new Gson();
		return gson.fromJson(traceJson,TraceabilityDocument.class);
	}

	/**
	 * Calculate true negatives
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
	 * Extract the T annotation as a String list and remove duplicates.
	 *
	 * @param filename the filename
	 * @param tsd the TypeSystems
	 * @return the string list of coveredtext by the T annotations without duplicates
	 * @throws UIMAException the uIMA exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	 public static <T extends Annotation >List<String> extractAnnotations(String filename,
			 TypeSystemDescription tsd,final Class<T> type) throws UIMAException, IOException {
		 JCas jCas = JCasFactory.createJCas(filename, tsd);
		 List<String> ret = new ArrayList<String>();
		 HashSet<String> hs = new HashSet<String>();
		 
		 for (Annotation c : JCasUtil.select(jCas, type)) {
			 hs.add(c.getCoveredText());
		 }
		 ret.addAll(hs);
		 return ret;
	}

	public static void writeTraceabilityToGephi(TraceabilityDocument result, String filename) {
		GephiTraceability gephi = new GephiTraceability(filename);
		
		for (Entity c : result.getConcerns()) {
			gephi.addNode(c);
		} 
		for (Entity d : result.getDesignDecisions()) {
			gephi.addNode(d);
		}
		for(TraceabilityLink t : result.getLinks()) {
			gephi.addEdge(t.getConcern(), t.getDesignDecision(), Double.valueOf(t.getWeight()));
		}
		gephi.saveGraph();
	}

	public static void writeMatrix(TraceabilityDocument result, String filename) {
        try {
        	PrintStream writer = new PrintStream(new File(filename));
        	Map<Integer, TraceabilityLink> links = new HashMap<Integer, TraceabilityLink>();
        	
        	for (TraceabilityLink tl : result.getLinks()) {
        		links.put(tl.hashCode(),tl);
        	}
        	
        	for (Entity ccc : result.getConcerns()) {
        		writer.println(ccc.getId()+"\t"+ccc.getLabel());
        		for (Entity ddd : result.getDesignDecisions()) {
        			TraceabilityLink pl = links.get(new TraceabilityLink(ccc, ddd, 0.0).hashCode());
					if (pl != null) {
						writer.println("\t" +ddd.getId()+"\t"+ddd.getLabel() + " " +pl.getWeight());
					}
        		}
        	}
        	writer.close();
        	
        } catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public static void writeConcerns(List<Entity> entities, String filename) {
        try {
        	PrintStream writer = new PrintStream(new File(filename));

			for (Entity entity : entities) {
				writer.println(entity.getId()+" === "+entity.getLabel());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }	
}
