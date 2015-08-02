package edu.isistan.carcha.lsa;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import edu.isistan.carcha.lsa.model.Entity;

/**
 * The Class TextTraceabilityComparator.
 */
public class TextTraceabilityComparator extends TraceabilityComparator {

	/** The sb. */
	private StringBuilder sb;
	private Set<Integer> reqConcerns = new HashSet<Integer>();
	
	/* (non-Javadoc)
	 * @see edu.isistan.carcha.lsa.TraceabilityComparator#addNode(edu.isistan.carcha.lsa.Concern)
	 */
	@Override
	protected String addNode(Entity concern) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	/* (non-Javadoc)
	 * @see edu.isistan.carcha.lsa.TraceabilityComparator#addEdge(edu.isistan.carcha.lsa.Concern, edu.isistan.carcha.lsa.Concern, java.lang.Double)
	 */
	@Override
	protected String addEdge(Entity from, Entity to, Double linkWeight) {
		if (!reqConcerns.contains(from.hashCode())) {
			sb.append("\n");
			sb.append(from.getClassification()+ " - "+from.getLabel());
			sb.append("\n");
			reqConcerns.add(from.hashCode());
		}
		sb.append(linkWeight);
		sb.append("\t");
		sb.append(to.getClassification()+ " - "+to.getLabel());
    	sb.append("\n");
    	return from.hashCode()+"-"+to.hashCode();
	}

	/* (non-Javadoc)
	 * @see edu.isistan.carcha.lsa.TraceabilityComparator#init()
	 */
	@Override
	protected void init() {
		sb = new StringBuilder();
		reqConcerns.clear();
	}

	/* (non-Javadoc)
	 * @see edu.isistan.carcha.lsa.TraceabilityComparator#saveGraph(java.io.File)
	 */
	@Override
	protected String saveGraph() {
		String filename = buildOutputFilename();
		filename+=".concern";
		File f = new File(filename);
	    try {
			FileUtils.writeStringToFile(f, sb.toString());
			return filename;
	    } catch (IOException e) {
			e.printStackTrace(System.err);
		}
	    return null;
	}

}
