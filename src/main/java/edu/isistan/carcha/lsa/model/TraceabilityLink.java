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
/**
 * @author German Attanasio Ruiz <germanatt@us.ibm.com>
 *
 */
public class TraceabilityLink {
	
	/**  The concern id. */
	private int concernId;
	
	/**  The design decision id. */
	private int designDecisionId;
	
	/** The weight. */
	private double weight;

	/** The concern. */
	private transient Entity concern;

	/** The design decision. */
	private transient Entity designDecision;

	
	/**
	 * Gets the concern id.
	 *
	 * @return the concern id
	 */
	public int getConcernId() {
		return concernId;
	}

	/**
	 * Sets the concern id.
	 *
	 * @param concernId the new concern id
	 */
	public void setConcernId(int concernId) {
		this.concernId = concernId;
	}

	/**
	 * Gets the design decision id.
	 *
	 * @return the design decision id
	 */
	public int getDesignDecisionId() {
		return designDecisionId;
	}

	/**
	 * Sets the design decision id.
	 *
	 * @param designDecisionId the new design decision id
	 */
	public void setDesignDecisionId(int designDecisionId) {
		this.designDecisionId = designDecisionId;
	}

	/**
	 * Sets the weight.
	 *
	 * @param weight the new weight
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * Sets the concern.
	 *
	 * @param concern the new concern
	 */
	public void setConcern(Entity concern) {
		this.concern = concern;
	}

	/**
	 * Sets the design decision.
	 *
	 * @param designDecision the new design decision
	 */
	public void setDesignDecision(Entity designDecision) {
		this.designDecision = designDecision;
	}

	/**
	 * Gets the concern.
	 *
	 * @return the concern
	 */
	public Entity getConcern() {
		return concern;
	}

	/**
	 * Gets the design decision.
	 *
	 * @return the design decision
	 */
	public Entity getDesignDecision() {
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
	 * @param ccId the concern hascode
	 * @param ddId the design decision hashcode
	 * @param weight the weight
	 */
	private TraceabilityLink(int ccId, int ddId, double weight) {
		super();
		this.concernId = ccId;
		this.designDecisionId = ddId;
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
		this.concern = concern;
		this.designDecision = designDecision;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + concernId;
		result = prime * result + designDecisionId;
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
		if (concernId != other.concernId)
			return false;
		if (designDecisionId != other.designDecisionId)
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
		builder.append(concernId);
		builder.append(", designDecision=");
		builder.append(designDecisionId);
		builder.append(", weight=");
		builder.append(weight);
		builder.append("]");
		return builder.toString();
	}
	
}
