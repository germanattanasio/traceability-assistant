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
package edu.isistan.carcha.lsa.model;

import java.util.List;

/**
 * The Class TraceabilityDocument.
 */
public class TraceabilityDocument {
	
	/** The links. */
	private List<Entity> concerns;
	
	/** The design decisions. */
	private List<Entity> designDecisions;
	
	/** The links. */
	private List<TraceabilityLink> links;
	
	/** The traced concern count. */
	private int tracedConcernCount;
	
	/** The untraced concern count. */
	private int untracedConcernCount;
	
	/**
	 * Instantiates a new traceability document.
	 *
	 * @param concerns the concerns
	 * @param designDecision the design decision
	 */
	public TraceabilityDocument(List<Entity> concerns,List<Entity> designDecision) {
		super();
		this.concerns = concerns;
		this.designDecisions = designDecision;
	}

	/**
	 * Gets the concerns.
	 *
	 * @return the concerns
	 */
	public List<Entity> getConcerns() {
		return concerns;
	}

	/**
	 * Gets the design decisions.
	 *
	 * @return the design decisions
	 */
	public List<Entity> getDesignDecisions() {
		return designDecisions;
	}

	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<TraceabilityLink> getLinks() {
		return links;
	}

	/**
	 * Gets the traced concern count.
	 *
	 * @return the traced concern count
	 */
	public int getTracedConcernCount() {
		return tracedConcernCount;
	}

	/**
	 * Gets the untraced concern count.
	 *
	 * @return the untraced concern count
	 */
	public int getUntracedConcernCount() {
		return untracedConcernCount;
	}

	/**
	 * Sets the links.
	 *
	 * @param links the new links
	 */
	public void setLinks(List<TraceabilityLink> links) {
		this.links = links;
	}

	/**
	 * Sets the traced concern count.
	 *
	 * @param tracedCount the new traced concern count
	 */
	public void setTracedConcernCount(int tracedCount) {
		this.tracedConcernCount = tracedCount;
	}

	/**
	 * Sets the untraced concern count.
	 *
	 * @param untracedCount the new untraced concern count
	 */
	public void setUntracedConcernCount(int untracedCount) {
		this.untracedConcernCount = untracedCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TraceabilityDocument [links=");
		builder.append(links.size());
		builder.append(", untracedCount=");
		builder.append(untracedConcernCount);
		builder.append(", tracedCount=");
		builder.append(tracedConcernCount);
		builder.append("]");
		return builder.toString();
	}
}
