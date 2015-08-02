package edu.isistan.carcha.annotator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.token.type.Sentence;

import edu.isistan.carcha.concern.cdetector.DesignDecision;

/**
 * Remove all the sentence annotations that are design decision
 * We will use sentences as false negatives
 * 
 * @author germanattanasio
 */
public class DesignDecisionSentenceRemover extends JCasAnnotator_ImplBase {

	private Set<String> designDecisionSet = null;

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 * @throws ResourceInitializationException
	 *             the resource initialization exception
	 */
	public static AnalysisEngineDescription getDescription() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(DesignDecisionSentenceRemover.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.fit.component.JCasAnnotator_ImplBase#initialize(org.apache
	 * .uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		designDecisionSet = new HashSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	  public void process(JCas jCas) throws AnalysisEngineProcessException {
		// save all the design decisions
		for (DesignDecision dd : JCasUtil.select(jCas, DesignDecision.class)) {
			if (!designDecisionSet.contains(dd.getCoveredText()))
				designDecisionSet.add(dd.getCoveredText());
		}
		// get all the sentences
		List<Sentence> sentences = new ArrayList<Sentence>(JCasUtil.select(jCas, Sentence.class));

		// loop sentences and remove those that are in the designDecisionSet
		for (Sentence sentence : sentences) {
			if (designDecisionSet.contains(sentence.getCoveredText()))
				sentence.removeFromIndexes();
		}
	}
}
