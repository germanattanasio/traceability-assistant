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
package edu.isistan.carcha.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.UIMAException;
import org.cleartk.token.type.Sentence;

import edu.isistan.carcha.concern.cdetector.DesignDecision;
import edu.isistan.carcha.util.Utils;

/**
 * The Class CarchaEvaluator.
 * Make sure you run the test cases with at least 2GB of memory.
 */
public class CarchaEvaluator {
	
	  /** The Constant logger. */
	  private static final Log logger = LogFactory.getLog(CarchaEvaluator.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws UIMAException the uIMA exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws UIMAException, IOException {
		// check the parameters to be directories and have the same number of files
		File golden = new File("src/test/resources/golden/annotated");
		File annotated = new File("src/test/resources/input/3annotated");

		CarchaEvaluator evaluator = new CarchaEvaluator();
		evaluator.evaluate(golden,annotated);
	}

	
	/**
	 * Evaluate.
	 *
	 * @param goldenDirectory the golden directory
	 * @param annotatedDirectory the annotated directory
	 * @throws UIMAException the uIMA exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void evaluate (File goldenDirectory,File annotatedDirectory) throws UIMAException, IOException {

		File [] goldens = FileUtils.listFiles(goldenDirectory, new String []{"xmi"}, false).toArray(new File[]{});		
		File [] annotated = FileUtils.listFiles(annotatedDirectory, new String []{"xmi"}, false).toArray(new File[]{});	
		
		for (int i = 0; i < goldens.length; i++) {
			String goldenFileName = goldens[i].getName();
			String annotatedFileName = annotated[i].getName();

			logger.info("\n----------------\nComparing "+goldenFileName+" with "+annotatedFileName+"\n----------------\n");

			List<String> goldenSentences = Utils.extractCoveredTextAnnotations(goldens[i].getAbsolutePath(),Sentence.class);
			List<String> goldenConcern= Utils.extractCoveredTextAnnotations(goldens[i].getAbsolutePath(),DesignDecision.class);
			List<String> discoveredConcern= Utils.extractCoveredTextAnnotations(annotated[i].getAbsolutePath(),DesignDecision.class);
			
			goldenSentences.removeAll(discoveredConcern);
			goldenConcern.removeAll(discoveredConcern);
			
			logger.info("untraced concerns "+goldenConcern.size());
			logger.info("untraced sentences "+goldenSentences.size());

		}
	}
		

}
