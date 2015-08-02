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

import java.util.regex.Pattern;

import org.tartarus.snowball.ext.EnglishStemmer;

/**
 * The Class Entity.
 */
public class Entity {
	
	/** The stop words. */
	private static transient Pattern stopWords = Pattern.compile("\\b(?:a|about|above|after|again|against|all|am|an|and|any|are|aren't|as|at|be|because|been|before|being|below|between|both|but|by|can't|cannot|could|couldn't|did|didn't|do|does|doesn't|doing|don't|down|during|each|few|for|from|further|had|hadn't|has|hasn't|have|haven't|having|he|he'd|he'll|he's|her|here|here's|hers|herself|him|himself|his|how|how's|i|i'd|i'll|i'm|i've|if|in|into|is|isn't|it|it's|its|itself|let's|me|more|most|mustn't|my|myself|no|nor|not|of|off|on|once|only|or|other|ought|our|ours|ourselves|out|over|own|same|shan't|she|she'd|she'll|she's|should|shouldn't|so|some|such|than|that|that's|the|their|theirs|them|themselves|then|there|there's|these|they|they'd|they'll|they're|they've|this|those|through|to|too|under|until|up|very|was|wasn't|we|we'd|we'll|we're|we've|were|weren't|what|what's|when|when's|where|where's|which|while|who|who's|whom|why|why's|with|won't|would|wouldn't|you|you'd|you'll|you're|you've|your|yours|yourself|yourselves)\\b\\s*", Pattern.CASE_INSENSITIVE);
	
	/** The stemmer. */
	private static transient EnglishStemmer stemmer = new EnglishStemmer();
	
	
	/**
	 * The Enum NodeType.
	 */
	public enum NodeType {
		
		/** The rc. */
		CC("Concern"), 
		
		/** The ac. */
		DD("DesignDecision");
	 
		/** The type. */
		private String type;
	 
		/**
		 * Node Type.
		 *
		 * @param type the type
		 */
		private NodeType(String type) {
			this.type = type;
		}
	 
		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public String getType() {
			return type;
		}
	 
	}
	
	/** The classification. */
	private String label,classification;
	
	/** The space label. */
	private transient String spaceLabel;
	
	/** The type. */
	private transient NodeType type;
	
	/** The id. */
	private int id;
	
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Instantiates a new Entity.
	 *
	 * @param label the label
	 * @param classification the classification
	 * @param type the type
	 */
	public Entity(String label, String classification, NodeType type) {
		super();
		this.label = label;
		this.classification = classification;
		this.type = type;
		this.id = hashCode();
		this.spaceLabel = formatLabel();
	}


	/**
	 * Gets the classification.
	 *
	 * @return the classification
	 */
	public String getClassification() {
		return classification;
	}
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public NodeType getType() {
		return type;
	}
	

	/**
	 * Builds the from string.
	 *
	 * @param line the line
	 * @param type the type
	 * @param typeless  include the entity type
	 * @return the Entity
	 */
	public static Entity buildFromString(String line,NodeType type, boolean typeless) {
		String [] requirementVector = line.split("\\t",2);
			return new Entity(requirementVector[1], typeless ? "typeless" : requirementVector[0],type);
	}

	/**
	 * Builds the from string.
	 *
	 * @param line the line
	 * @param type the type
	 * @return the Entity
	 */
	public static Entity buildFromString(String line,NodeType type) {
			return buildFromString(line, type,false);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((classification == null) ? 0 : classification.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		Entity other = (Entity) obj;
		if (classification == null) {
			if (other.classification != null)
				return false;
		} else if (!classification.equals(other.classification))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(type);
		builder.append(" \t");
		builder.append(classification);
		builder.append("\t");
		builder.append(label);
		return builder.toString();
	}

	/**
	 * Gets the formatted label.
	 *
	 * @return the formatted label
	 */
	public String getFormattedLabel() {
		return spaceLabel;
	}

	/**
	 * Stemming.
	 *
	 * @param label the label
	 * @return the string
	 */
	public String stemming(String label) {		
		StringBuffer sb = new StringBuffer();
		String [] words = label.split("\\s+");

		for(String word : words){
			stemmer.setCurrent(word);
			stemmer.stem();
			sb.append(stemmer.getCurrent());
			sb.append(" ");
		}
		return sb.toString().trim();
	}
	
	
	/**
	 * Format label.
	 *
	 * @return the string
	 */
	private String formatLabel() {
		if (label == null) return label;
		String clean  = label;
		clean = stopWords.matcher(label).replaceAll("");
		clean = clean.replaceAll("-"," ").replaceAll("[^a-zA-Z\\s]", "").toLowerCase().trim();
		
		String result = stemming(clean);
		//logger.info("OR= "+clean);
		//logger.info("ST= "+result);
		return result;	
	}
}
