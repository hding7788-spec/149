package com.glaway.mpm.qmIntf.viewPanel;

import javax.swing.JTree;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpStepNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTecnicsNode;
import com.glaway.mpm.util.XmlUtil;

public class TechMiddleTreeXmlUtil {
//	private static Logger logger = LogUtil.getLogger(CmpTreeXmlUtil.class);
	public static JTree xmlToTechImgTree(Document document) {
		Element root = document.getRootElement();
		DpTecnicsNode rootNode = null;
		for (Element element : XmlUtil.getElementsByName(root, "QMFawTechnicsInfo")) {
			String number = parseName(element, "technicsNumber");
			String name = parseName(element, "technicsName");
			String version = parseName(element, "version");
			rootNode = new DpTecnicsNode(number + "_" + name + "_" + version);
			for (Element temp : XmlUtil.getElementsByName(element, "steps")) {
				for (Element temp1 : XmlUtil.getElementsByName(temp, "QMProcedureInfo")) {
					String oid = parseName(temp1, "bsoID");
					String stepNumber = parseName(temp1, "stepNumber");
					String stepName = parseName(temp1, "stepName");
					String workshopName = parseName(temp1, "workShop");
					String shopTypeName = parseName(temp1, "workType");
					
					IMCmpTreeNode stepNode = new IMCmpTreeNode(stepNumber,"","");
					rootNode.add(stepNode);
					for (Element temp2 : XmlUtil.getElementsByName(temp1, "images")) {
						for (Element temp3 : XmlUtil.getElementsByName(temp2, "PDrawingInfo")) {
							String oid1 = parseName(temp3, "bsoID");
							String drawingName = parseName(temp3, "drawingName");
							String path = parseName(temp3, "absolutePath");
							CmpTreeNode imgNode = new CmpTreeNode(drawingName, path, oid1);
							stepNode.add(imgNode);
							}
						}
					}
				}
			}
			return new JTree(rootNode);
		}
	
	public static String parseName(Element element, String type) {
		Attribute attr = element.attribute(type);
		return attr == null ? null : attr.getValue();
	}


}
