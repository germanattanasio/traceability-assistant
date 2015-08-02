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
	
	/** The req concerns. */
	private Set<Integer> reqConcerns = new HashSet<Integer>();
	
	/* (non-Javadoc)
	 * @see edu.isistan.carcha.lsa.TraceabilityComparator#addNode(edu.isistan.carcha.lsa.Concern)
	 */
	@Override
	protected String addNode(Entity concern) {
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
		filename+= ".concern";
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
