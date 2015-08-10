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
package edu.isistan.carcha;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.UIMAException;
import org.cleartk.token.type.Sentence;
import org.junit.Test;

import edu.isistan.carcha.concern.cdetector.DesignDecision;
import edu.isistan.carcha.util.Utils;

/**
 * The Class CarchaPipelineTest.
 */
public class CarchaPipelineTest {
	
	/** The logger. */
	private static final Log logger = LogFactory.getLog(CarchaPipelineTest.class);
	
	/** The golden folder. */
	private final String GOLDEN_FOLDER = "src/test/resources/ddd/0golden/";
	
	/** The raw folder. */
	private final String RAW_FOLDER = "src/test/resources/ddd/1raw/";
	
	/** The annotated folder. */
	private final String ANNOTATED_FOLDER = "src/test/resources/ddd/2annotated/";
	
	/** The sentence folder. */
	private final String SENTENCE_FOLDER = "src/test/resources/ddd/4sentences/";

	
	/** The xmi. */
	private final String XMI = ".xmi";
	
	
	/**
	 * Ejecuta una evaluacion entre los golden y los documentos que anota carcha.
	 *
	 * @throws UIMAException the UIMA exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public final void testExecuteUIMAAnnotator() throws UIMAException, IOException {
		CarchaPipeline pipeline = new CarchaPipeline();
		
		String [] testFiles = {"adventure_builder", "pet_store","mslite"};
		
		for (String testFile : testFiles) {
			String golden = GOLDEN_FOLDER+testFile+XMI;
			String output = ANNOTATED_FOLDER+testFile+XMI;
			String input  = RAW_FOLDER+testFile;
			
			pipeline.executeUIMAAnnotator(input,output);
			logger.info("Calculate Metrics for: "+testFile);
			testCalculateMetrics(output,golden);			
		}
	}
	
	/**
	 * Marca las sentencias en los documentos para la evaluacion del 
	 * detector de decisiones de diseno sobre todo los doc. de arq.
	 *
	 * @throws UIMAException the UIMA exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public final void testExecuteSentenceAnnotator() throws UIMAException, IOException {
		CarchaPipeline pipeline = new CarchaPipeline();
		
		String [] testFiles = {"adventure_builder","pet_store","mslite"};
		
		for (String testFile : testFiles) {
			String output = SENTENCE_FOLDER+testFile+XMI;
			String input  = RAW_FOLDER+testFile;
			
			pipeline.executeSentenceAnnotator(input,output);			
		}
	}
	
	/**
	 * Test calculate metrics.
	 *
	 * @param output the result file from executing the annotator
	 * @param golden the golden file annotated by experts
	 * @throws UIMAException the UIMA exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void testCalculateMetrics(String output, String golden) throws UIMAException, IOException {

		List<String> goldenDesignDecisions     = Utils.extractCoveredTextAnnotations(golden,DesignDecision.class);
		List<String> goldenSentences           = Utils.extractCoveredTextAnnotations(golden,Sentence.class);
		
		List<String> discoveredDesignDecisions = Utils.extractCoveredTextAnnotations(output,DesignDecision.class);
		List<String> discoveredSentences       = Utils.extractCoveredTextAnnotations(output,Sentence.class);
		
		//Golden design decision that were not discovered
		double fn = ListUtils.removeAll(goldenDesignDecisions, discoveredDesignDecisions).size();
		
		//Sentences that were discovered but are not design decisions
		double fp = ListUtils.removeAll(discoveredDesignDecisions, goldenDesignDecisions).size();
		
		//Discovered Design Decisions.
		double tp = ListUtils.intersection(discoveredDesignDecisions, goldenDesignDecisions).size();
		
		//non design decision that were not marked as design decision
		double tn = ListUtils.intersection(discoveredSentences, goldenSentences).size();

		
		Double presicion = tp / (tp + fp);
		double recall = tp / (tp + fn);
		double fMeasure = 5 * ((presicion * recall) / ((4 * presicion) + recall));
		double accuracy = (tp + tn) / (fn + fp + tp + tn);

		NumberFormat df = new DecimalFormat("#0.00");

		logger.info("Golden DDs:           " + goldenDesignDecisions.size());
		logger.info("Golden Sentences:     " + goldenSentences.size());
		logger.info("Discovered DDs:       " + discoveredDesignDecisions.size());
		logger.info("Discovered Sentences: " + discoveredSentences.size());
		logger.info("------------------");
		logger.info("False Negative:   " + fn);
		logger.info("False Positive:   " + fp);
		logger.info("True  Negative:   " + tn);
		logger.info("True Positive:    " + tp);
		logger.info("------------------");
		logger.info("Presition: " + df.format(presicion * 100) + "%");
		logger.info("Recall:    " + df.format(recall * 100) + "%");
		logger.info("Acurracy:  " + df.format(accuracy * 100) + "%");
		logger.info("F-Measure: " + df.format(fMeasure * 100) + "%");
	}

}
