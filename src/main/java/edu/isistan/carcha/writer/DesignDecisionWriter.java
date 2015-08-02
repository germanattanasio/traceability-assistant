package edu.isistan.carcha.writer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.util.ae.linewriter.AnnotationWriter;
import org.cleartk.util.ae.linewriter.LineWriter;

import edu.isistan.carcha.concern.cdetector.DesignDecision;


/**
 * The Class DesignDecisionWriter.
 */
public class DesignDecisionWriter implements AnnotationWriter<DesignDecision> {

	/* (non-Javadoc)
	 * @see org.cleartk.util.ae.linewriter.AnnotationWriter#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
	}
	
	

	/* (non-Javadoc)
	 * @see org.cleartk.util.ae.linewriter.AnnotationWriter#writeAnnotation(org.apache.uima.jcas.JCas, org.apache.uima.jcas.tcas.Annotation)
	 */
	@Override
	public String writeAnnotation(JCas jCas, DesignDecision dd) throws AnalysisEngineProcessException {
	      return dd.getTypex() +"\t"+ dd.getCoveredText().replaceAll("[\\t\\n\\r ]+"," ");
	}

	
	/**
	 * create a LineWrite and export all the design decisions to a directory
	 *
	 * @param outputDirectory the output file directory
	 * @return the writer description
	 * @throws ResourceInitializationException the resource initialization exception
	 */
	public static AnalysisEngineDescription getDesignDecisionWriterDescription(String outputDirectory) throws ResourceInitializationException{
		return AnalysisEngineFactory.createEngineDescription(
	        LineWriter.class,
	        LineWriter.PARAM_OUTPUT_DIRECTORY_NAME,
	        outputDirectory,
	        LineWriter.PARAM_OUTPUT_ANNOTATION_CLASS_NAME,
	        DesignDecision.class.getName(),
	        LineWriter.PARAM_ANNOTATION_WRITER_CLASS_NAME,
	        DesignDecisionWriter.class.getName());
	}
}
