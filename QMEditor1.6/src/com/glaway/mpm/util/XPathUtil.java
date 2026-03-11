package com.glaway.mpm.util;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

public class XPathUtil {

	/**
	 * 获取xpath对象
	 * 
	 * @param path
	 * @param document
	 * @return
	 */
	public static List<Element> getElements(String path, Document document) {
		Element rootElement = document.getRootElement();
		String uri = rootElement.getNamespaceURI();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("xx", uri);
		XPath xpath = DocumentHelper.createXPath(path);
		xpath.setNamespaceURIs(map);
		return xpath.selectNodes(document);
	}
}