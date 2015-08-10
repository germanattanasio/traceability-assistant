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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import edu.isistan.carcha.lsa.model.Entity;
import edu.isistan.carcha.lsa.model.TraceabilityDocument;
import edu.isistan.carcha.util.Utils;

/**
 * The Class LSARunnerTest.
 */
public class LSARunnerTest {
	
	/** The corpus folder. */
	private final String CORPUS_DDD_FOLDER = "src/test/resources/ddd/2annotated/";

	/** The corpus ccc folder. */
	private final String CORPUS_CCC_FOLDER = "src/test/resources/lsa/1corpus/ccc/";
	
	/** The golden folder. */
	private final String GOLDEN_FOLDER = "src/test/resources/lsa/0golden/";

	/** The logger. */
	private Log logger = LogFactory.getLog(LSARunnerTest.class);

	/** The tra. */
	private final String TRA = ".tra";

	/** The gephi. */
	@SuppressWarnings("unused")
	private final String GEPHI = ".gexf";

	/** The tra folder. */
    private final String TRA_FOLDER = "src/test/resources/lsa/2traceability/";
    
	/** The xmi. */
	private final String XMI = ".xmi";

	/** The ccc. */
	private final String CCC = ".ccc";

	/** The dimensions. */
	private final Integer []dimensions = {60}; //{ 30, 45, 60, 75};
	
	/** The test files. */
	private String[] testFiles = {
			//"adventure_builder", 
			//"mslite",
			"pet_store"
			};
	
	/** The threshold. */
	private final Double []threshold = { 0.20, 0.30, 0.40, 0.45, 0.50, 0.55, 0.60, 0.63, 0.66, 0.69, 0.71,0.73, 0.75, 0.77, 0.79, 0.81, 0.83, 0.85, 0.87, 0.89, 0.92 };
	
	/**
	 * Test calculate metrics.
	 *
	 * @param ccc the ccc
	 * @param ddd the ddd
	 * @param golden the golden
	 * @param output the output
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	private void testGenerateTraceabilityResults(List<Entity> ccc, List<Entity> ddd,
			String golden, String output) throws FileNotFoundException,
			IOException, ClassNotFoundException {

		TraceabilityDocument goldenTra = Utils.readTraceabilityFromFile(golden);
		
		LSARunner runner;
		TraceabilityDocument result;
		logger.info("ccc: " + ccc.size());
		logger.info("ddd: " + ddd.size());
		for (Double th : threshold) {
			for (Integer dim : dimensions) {
				runner = new LSARunner(ccc, ddd, dim, th);
				result = runner.getTraceability();
				logger.info("-----------------");
				logger.info("threshold: " + th);
				logger.info("dimension: " + dim);
				calculateMetrics(result,goldenTra);
				Utils.writeTraceabilityToFile(result, output+"_"+dim+"_"+th.toString()+TRA);
				//Utils.writeTraceabilityToGephi(result, output+"_"+dim+"_"+th.toString()+GEPHI);
				//Utils.writeMatrix(result, output+"_"+dim+"_"+th.toString()+MX);
				//Utils.writeTraceabilityToFile(result, output+"_"+dim+"_"+th.toString()+"-all"+TRA);
			}
		}
	}

	/**
	 * Test calculate metrics.
	 *
	 * @param result the result
	 * @param golden the golden
	 */
	public void calculateMetrics(TraceabilityDocument result, TraceabilityDocument golden) {
		
		double fn = ListUtils.removeAll(golden.getLinks(), result.getLinks()).size();
		double fp = ListUtils.removeAll(result.getLinks(), golden.getLinks()).size();
		double tn = Utils.calculateTrueNegatives(golden,result);
		double tp = ListUtils.intersection(result.getLinks(), golden.getLinks()).size();

		Double presicion = tp / (tp + fp+0.0000000001);
		double recall = tp / (tp + fn);
		double fMeasure = 5 * ((presicion * recall) / ((4 * presicion) + recall+0.0000000001));
		double accuracy = (tp + tn) / (fn + fp + tp + tn);

		NumberFormat df = new DecimalFormat("#0.00");

		logger.info("Golden TRAs:" + golden.getLinks().size());
		logger.info("Discovered TRAs:" + result.getLinks().size());
		logger.info("------------------");
		logger.info("False Negative:\t" + fn);
		logger.info("False Positive:\t" + fp);
		logger.info("True  Negative:\t" + tn);
		logger.info("True Positive:\t" + tp);
		logger.info("------------------");
		logger.info("Presition:\t" + df.format(presicion * 100));
		logger.info("Recall:\t" + df.format(recall * 100));
		logger.info("Acurracy:\t" + df.format(accuracy * 100));
		logger.info("F-Measure:\t" + df.format(fMeasure * 100));
		logger.info("");
		logger.info("");
	}

	/**
	 * Test traceability.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	@Test
	public void testTraceability() throws IOException, ClassNotFoundException {

		for (String testFile : testFiles) {
			String golden    = GOLDEN_FOLDER + testFile + TRA;
			String output    = TRA_FOLDER    + testFile +"/"+ testFile ;
			String input_ddd = CORPUS_DDD_FOLDER + testFile + XMI;
			String input_ccc = CORPUS_CCC_FOLDER + testFile + CCC;

			List<Entity> ccc = Utils.extractCrosscuttingConcernsFromTextFile(input_ccc);
			List<Entity> ddd = Utils.extractDesignDecisionsAsList(input_ddd);

			logger.info("Calculate Metrics for: " + testFile);
			testGenerateTraceabilityResults(ccc, ddd, golden, output);
		}

	}
}
