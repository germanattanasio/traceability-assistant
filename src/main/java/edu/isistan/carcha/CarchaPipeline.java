package edu.isistan.carcha;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.cleartk.stanford.StanfordCoreNLPAnnotator;
import org.cleartk.token.stem.snowball.DefaultSnowballStemmer;
import org.cleartk.token.type.Sentence;
import org.cleartk.util.ae.UriToDocumentTextAnnotator;
import org.cleartk.util.ae.UriToXmiCasAnnotator;
import org.cleartk.util.ae.linewriter.LineWriter;
import org.cleartk.util.ae.linewriter.annotation.CoveredTextAnnotationWriter;
import org.cleartk.util.cr.UriCollectionReader;
import org.springframework.util.StopWatch;

import com.google.common.base.Stopwatch;

import edu.isistan.carcha.annotator.AnnotationRemover;
import edu.isistan.carcha.annotator.DesignDecisionSentenceRemover;
import edu.isistan.carcha.writer.DesignDecisionWriter;
import edu.isistan.carcha.writer.XCasWriter;

/**
 * <br>
 * Copyright (c) 2013, Regents of the UNICEN <br>
 * All rights reserved.
 * 
 * @author German Attanasio Ruiz
 * 
 */
public class CarchaPipeline {

  /** The Constant UIMA_RUTA_SCRIPT. */
  private static final String UIMA_RUTA_SCRIPT = "edu.isistan.carcha.concern.cdetectorEngine";

  private Log logger = LogFactory.getLog(CarchaPipeline.class);

private AnalysisEngineDescription stanfordNLP;
  
  public CarchaPipeline() {
	try {
		this.stanfordNLP = StanfordCoreNLPAnnotator.getDescription();
	} catch (ResourceInitializationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
  /**
   * The main method.
   *
   * @param args the arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    String inputDirectory = args[0];
    String output = args[1];
    //TODO: check args to have input and output defined.
    CarchaPipeline carcha = new CarchaPipeline();
    StopWatch sw = new StopWatch();
    sw.start("executeStanfordAnnotators");
    if (args.length > 2 && args[2].equals("write")) {
    	System.out.println("Write Design Decision to file");
    	carcha.writeAnnotations(inputDirectory, output);
    } else if (args.length > 2 && args[2].equals("sentence"))
    	carcha.writeSentences(inputDirectory, output);
    else if (args.length > 2 && args[2].equals("sentence-annotator"))
    	carcha.executeSentenceAnnotator(inputDirectory, output);
    else
    	carcha.executeUIMAAnnotator(inputDirectory,output);    	
    sw.stop();
    sw.prettyPrint();
  }
      
  /**
   * Write the concern annotations into @param outputFilename.
   *
   * @param inputDirectory the input directory
   * @param outputDirectory the output directory
   * @throws ResourceInitializationException the resource initialization exception
   * @throws UIMAException the uIMA exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void writeAnnotations(String inputDirectory,String outputDirectory) throws ResourceInitializationException, UIMAException, IOException {
	  File filesDirectory = new File(inputDirectory);	  
	    SimplePipeline.runPipeline(
	    	UriCollectionReader.getDescriptionFromDirectory(filesDirectory),
	    	UriToXmiCasAnnotator.getDescription(),
	    	DesignDecisionWriter.getDesignDecisionWriterDescription(outputDirectory)
    	);
 }
  
  /**
   * Write the concern annotations into @param outputFilename.
   *
   * @param inputDirectory the input directory
   * @param outputFilename the output filename
   * @throws ResourceInitializationException the resource initialization exception
   * @throws UIMAException the uIMA exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void writeSentences(String inputDirectory,String outputFilename) throws ResourceInitializationException, UIMAException, IOException {
	  File filesDirectory = new File(inputDirectory);	  
	    SimplePipeline.runPipeline(
	    	UriCollectionReader.getDescriptionFromDirectory(filesDirectory),
	    	UriToXmiCasAnnotator.getDescription(),
	    	
	    	AnalysisEngineFactory.createEngineDescription(
	    	        LineWriter.class,
	    	        LineWriter.PARAM_OUTPUT_FILE_NAME,
	    	        outputFilename,
	    	        LineWriter.PARAM_OUTPUT_ANNOTATION_CLASS_NAME,
	    	        Sentence.class.getName(),
	    	        LineWriter.PARAM_ANNOTATION_WRITER_CLASS_NAME,
	    	        CoveredTextAnnotationWriter.class.getName())    	
	    );
 }
  
/**
 * Creates the ae description.
 *
 * @param script the script
 * @return the analysis engine description
 * @throws InvalidXMLException the invalid xml exception
 * @throws IOException Signals that an I/O exception has occurred.
 * @throws ResourceInitializationException the resource initialization exception
 */
private AnalysisEngineDescription createAEDescription(String script) throws InvalidXMLException, IOException, ResourceInitializationException {
	final AnalysisEngineDescription ruta = AnalysisEngineFactory.createEngineDescription(script);
	return ruta;
}

/**
 * Execute Stanford annotators.
 * The annotators are: Tokenize, SSplit, POS, Lemma, NER, Parse, Dcoref + Snowball Stemmer
 *
 * @param _inputDirectory the _input directory
 * @param _uimaFile the _uima file
 */
public void executeUIMAAnnotator(String _inputFile, String _outputFile) {
	logger.info("Running executeUIMAAnnotator with: "+_inputFile+" and "+_outputFile);  
	File file = new File(_inputFile);
	Stopwatch stopwatch = new Stopwatch().start();
	  try {
		SimplePipeline.runPipeline(
			UriCollectionReader.getCollectionReaderFromFiles(Arrays.asList(file)),
			UriToDocumentTextAnnotator.getDescription(),
			this.stanfordNLP, //stanford tokenize, ssplit, pos, lemma, ner, parse, dcoref
		    DefaultSnowballStemmer.getDescription("English"), //stemmer
		    createAEDescription(UIMA_RUTA_SCRIPT), //RUTA Analysis Engine
		    AnnotationRemover.getDescription(), //Remove useless annotations
		    DesignDecisionSentenceRemover.getDescription(), //Remove sentence annotations that are also designdecisions
		    AnalysisEngineFactory.createEngineDescription(//result files
	            XCasWriter.class,
	            XCasWriter.PARAM_OUTPUT_FILE_NAME,
	            _outputFile)
		);
	} catch (Exception e) {
		logger.error("Error executing the uima annotator.",e);
	}
	stopwatch.stop(); // optional
    logger.info("executeUIMAAnnotator took: " + stopwatch); // formatted string like "12.3 ms"
}
 
public void executeSentenceAnnotator(String _inputFile, String _outputFile) {
	logger.info("Running executeSentenceAnnotator with: "+_inputFile+" and "+_outputFile);  
	File file = new File(_inputFile);
	Stopwatch stopwatch = new Stopwatch().start();
	  try {
		SimplePipeline.runPipeline(
			UriCollectionReader.getCollectionReaderFromFiles(Arrays.asList(file)),
			UriToDocumentTextAnnotator.getDescription(),
		    this.stanfordNLP, //stanford tokenize, ssplit, pos, lemma, ner, parse, dcoref
		    AnnotationRemover.getDescription(), //Remove useless annotations
		    AnalysisEngineFactory.createEngineDescription(//result files
	            XCasWriter.class,
	            XCasWriter.PARAM_OUTPUT_FILE_NAME,
	            _outputFile)
		);
	} catch (Exception e) {
		logger.error("Error executing the uima annotator.",e);
	}
	stopwatch.stop(); // optional
    logger.info("executeSentenceAnnotator took: " + stopwatch); // formatted string like "12.3 ms"
}

}
