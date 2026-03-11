package com.glaway.mpm.util;

import org.dom4j.Node;
import org.dom4j.xpath.DefaultXPath;

public class NumberXPath extends DefaultXPath {

	private static final long serialVersionUID = -1090882961226996499L;

	public NumberXPath(String text) {
		super(text);
	}

	@Override
	protected Object getCompareValue(Node node) {
		return numberValueOf(node);
	}
	
}
