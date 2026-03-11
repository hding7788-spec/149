package com.glaway.mpm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLUtil_UTF {
	
	private SAXBuilder builder;
	private Document document;
	
	public XMLUtil_UTF(InputStream inputStream) {
		try {
			builder = new SAXBuilder();
			document = builder.build(new InputStreamReader(inputStream, "utf-8"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public XMLUtil_UTF(){
		try {
			File file=File.createTempFile("temp", "xml");
			builder = new SAXBuilder();
			document = builder.build(new InputStreamReader(new FileInputStream(file) , "utf-8"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public List getChildren(Element Element, String string) {
		return Element.getChildren(string);

	}

	public Element getRootElement() {
		return document.getRootElement();
	}
	
	public Document getDocument(){
		return document;
	}

}
