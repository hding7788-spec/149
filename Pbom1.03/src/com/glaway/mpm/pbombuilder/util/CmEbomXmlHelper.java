package com.glaway.mpm.pbombuilder.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.glaway.mpm.pbombuilder.tree.CmTreeNode;

public class CmEbomXmlHelper {
	
	
	
	
	public static  byte[] getEbomTreeToBytes(CmTreeNode node) throws IOException {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		Element product = DocumentHelper.createElement("product");
		product.addAttribute("productName", "");
		product.addAttribute("isCompared", "needCompare");
		Element partInfo1 = structEbomElement(node);
		product.add(partInfo1);
		doc.add(product);
		String xml = null;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		StringWriter w = new StringWriter();
		XMLWriter writer = new XMLWriter(w, format);
		writer.write(doc);
		writer.flush();
		xml =  w.getBuffer().toString();
		return xml.getBytes();
	}
	

	
	private static Element structEbomElement(CmTreeNode node){
		Element partInfo = getPartInfoElementByPart(node);
		Element subChilds = getsubChildsElement();
		partInfo.add(subChilds);
		Enumeration<CmTreeNode> children = node.children();
		while(children.hasMoreElements()){
				CmTreeNode child = children.nextElement();
				Element subPartInfo = getPartInfoElementByPart(child);
				subChilds.add(subPartInfo);
				structSubEbomElement(child, subPartInfo);
		}
		return partInfo;

	}
	
	private static Element getPartInfoElementByPart(CmTreeNode node) {
		Element element = DocumentHelper.createElement("partInfo");
		element.addAttribute("partNumber", CmCommonStringUtil.emptyToString(node.getPart().getPartNumber()));
		element.addAttribute("partName", CmCommonStringUtil.emptyToString(node.getPart().getPartName()));
		element.addAttribute("occId", node.getOccId());
		element.addAttribute("occpath", node.getOccpath());
		element.addAttribute("version", CmCommonStringUtil.emptyToString(node.getPart().getVersion()));
		return element;
	}
	
	private static Element getsubChildsElement() {
		return DocumentHelper.createElement("subChilds");
	}
	
	private static void structSubEbomElement(CmTreeNode node, Element el) {
		Enumeration<CmTreeNode> children= node.children();
		if(children.hasMoreElements()){
			Element subChilds = getsubChildsElement();
			el.add(subChilds);
			while(children.hasMoreElements()){
				CmTreeNode child = children.nextElement();
				Element subPartInfo = getPartInfoElementByPart(child);

				subChilds.add(subPartInfo);

				structSubEbomElement(child, subPartInfo);
				
			}
		}
	}
}
