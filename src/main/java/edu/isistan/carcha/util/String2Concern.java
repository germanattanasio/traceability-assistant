package edu.isistan.carcha.util;

import org.apache.commons.collections.Transformer;

import edu.isistan.carcha.lsa.model.Entity;
import edu.isistan.carcha.lsa.model.Entity.NodeType;

public class String2Concern implements Transformer {

	private boolean typeless = false;
	public String2Concern(boolean typeless) {
		super();
		this.typeless = typeless;
	}

	public String2Concern() {
	}

	@Override
	public Object transform(Object input) {
		return Entity.buildFromString((String)input,NodeType.CC,typeless);
	}

}
