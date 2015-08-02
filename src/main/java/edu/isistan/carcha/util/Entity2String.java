package edu.isistan.carcha.util;

import org.apache.commons.collections.Transformer;

import edu.isistan.carcha.lsa.model.Entity;

public class Entity2String implements Transformer{

	@Override
	public Object transform(Object input) {
		Entity c = (Entity) input;
		return c.getFormattedLabel();
	}

}
