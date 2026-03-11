package com.glaway.mpm.erp.controller;

import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @author fly
 * 
 */
public class XMLHelper {

	
	
	/**
	 * 获取一个返回信息为成功的document对象
	 * @author fly
	 * @date  2013-5-15
	 * @return
	 *   
	 */
	public static Document getDefaultDocument() {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		Element returnMesage = DocumentHelper.createElement("Messsage");
		returnMesage.addAttribute("returnMSG", "success");
		doc.add(returnMesage);
		return doc;
	}
	/**
	 * 获取一个返回信息为成功的document对象
	 * @author fly
	 * @date  2013-5-15
	 * @return
	 *   
	 */
	public static Document getErrorInfoDocument(String errorMsg) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		Element returnMesage = DocumentHelper.createElement("Messsage");
		returnMesage.addAttribute("returnMSG", errorMsg);
		doc.add(returnMesage);
		return doc;
	}
	/**
	 * dom对象转换为XmlString
	 * 
	 * @author fly
	 * @date 2013-5-14
	 * @param doc
	 * @return
	 * 
	 */
	public static String doucmnetToXMLString(Document doc) {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");
			StringWriter w = new StringWriter();
			XMLWriter writer = new XMLWriter(w, format);
			writer.write(doc);
			writer.flush();
			return w.getBuffer().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * dom对象转换为XmlString
	 * 
	 * @author fly
	 * @date 2013-5-14
	 * @param doc
	 * @return
	 * 
	 */
	public static String doucmnetToXMLStringUTF8(Document doc) {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			StringWriter w = new StringWriter();
			XMLWriter writer = new XMLWriter(w, format);
			writer.write(doc);
			writer.flush();
			return w.getBuffer().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @author fly
	 * @date 2013-5-14
	 * @param args
	 * 
	 */
	public static void main(String[] args) {

	}
}
