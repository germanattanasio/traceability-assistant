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
package edu.isistan.carcha.util;

import org.apache.commons.collections.Transformer;

import edu.isistan.carcha.lsa.model.Entity;
import edu.isistan.carcha.lsa.model.Entity.NodeType;

/**
 * The Class String2Concern.
 */
public class String2Concern implements Transformer {

	/** The typeless. */
	private boolean typeless = false;
	
	/**
	 * Instantiates a new string2 concern.
	 *
	 * @param typeless the typeless
	 */
	public String2Concern(boolean typeless) {
		super();
		this.typeless = typeless;
	}

	/**
	 * Instantiates a new string2 concern.
	 */
	public String2Concern() {
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
	 */
	@Override
	public Object transform(Object input) {
		return Entity.buildFromString((String)input,NodeType.CC,typeless);
	}

}
