package edu.isistan.carcha.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;


/**
 * Remove all the annotations from the JCas leaving only: 
 * <b>edu.isistan.carcha.concern.cdetector.Concern</b>
 */
public class AnnotationRemover extends JCasAnnotator_ImplBase {

	
	private final String[] annotationToKeep = {
			"edu.isistan.carcha.concern.cdetector.DesignDecision",
			"org.cleartk.token.type.Sentence" };
	
  /**
   * Gets the description.
   *
   * @return the description
   * @throws ResourceInitializationException the resource initialization exception
   */
  public static AnalysisEngineDescription getDescription() throws ResourceInitializationException {
	    return AnalysisEngineFactory.createEngineDescription(AnnotationRemover.class);
	  }

	
	/* (non-Javadoc)
	 * @see org.apache.uima.fit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
	    super.initialize(context);
	}

	  /* (non-Javadoc)
  	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
  	 */
  	@Override
	  public void process(JCas jCas) throws AnalysisEngineProcessException {
		  List<TOP> tops = new ArrayList<TOP>(JCasUtil.selectAll(jCas));
		  for (TOP t : tops) {
			  if (!ArrayUtils.contains(annotationToKeep,t.getType().getName()))
				  t.removeFromIndexes();
		  }
	  }
}
