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

/**
 * The Class TraceabilityLink.
 */
public class TraceabilityLink {
	
	/** The concern. */
	private int concern;
	
	/** The design decision. */
	private int designDecision;
	
	/** The weight. */
	private double weight;

	/**
	 * Gets the concern.
	 *
	 * @return the concern
	 */
	public int getConcern() {
		return concern;
	}

	/**
	 * Gets the design decision.
	 *
	 * @return the design decision
	 */
	public int getDesignDecision() {
		return designDecision;
	}

	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Instantiates a new traceability link.
	 *
	 * @param concern the concern
	 * @param designDecision the design decision
	 * @param weight the weight
	 */
	private TraceabilityLink(int concern, int designDecision, double weight) {
		super();
		this.concern = concern;
		this.designDecision = designDecision;
		this.weight = weight;
	}

	/**
	 * Create a new Traceability link by using the hash code of each entity.
	 *
	 * @param concern the concern
	 * @param designDecision the design decision
	 * @param linkWeight the link weight
	 */
	public TraceabilityLink(Entity concern, Entity designDecision, Double linkWeight) {
		this(concern.hashCode(),designDecision.hashCode(),linkWeight);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + concern;
		result = prime * result + designDecision;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TraceabilityLink other = (TraceabilityLink) obj;
		if (concern != other.concern)
			return false;
		if (designDecision != other.designDecision)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TraceabilityLink [concern=");
		builder.append(concern);
		builder.append(", designDecision=");
		builder.append(designDecision);
		builder.append(", weight=");
		builder.append(weight);
		builder.append("]");
		return builder.toString();
	}
	
}
