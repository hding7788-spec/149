package com.glaway.mpm.qmIntf.viewPanel.tech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;


import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.util.CappJavaUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.util.MiddleModelUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewDrawingJPanel;

public class TechMiddleTreeXmlUtil {
	private static VaLogger logger = VaLogger.getLogger();

	public static List<Object> xmlToTechImgTree(Document document) {
		Map<String, String> numbers = new HashMap<String, String>();
		List<Object> list = new ArrayList<Object>();
		Map<String, List<TechImageNode>> childNodes = new HashMap<String, List<TechImageNode>>();

		Element techElement = XmlUtility.getTechnicsElement(document);
		String number = parseName(techElement, "technicsNumber");
		String partNumber = parseName(techElement, "partNumber");
		String name = parseName(techElement, "technicsName");
		String version = parseName(techElement, "version");
		TechTreeRootNode rootNode = new TechTreeRootNode(number + "_" + name
				+ "_" + version, partNumber);

		List<Element> steps = XmlUtility.getAllSteps(techElement);

		if (steps != null && steps.size() != 0) {
			for (Element stepElement : steps) {
				String stepOid = stepElement.attributeValue("bsoID");
				String stepNumber = stepElement.attributeValue("stepNumber");
				stepOid = stepOid.substring(stepOid.length() - MiddleModelUtil.ID_COUNT);
				numbers.put(CappJavaUtil.toHexString(stepOid), stepNumber);

				List<Element> paces = XmlUtility.getAllPaces(stepElement);
				if (paces != null && paces.size() != 0) {
					for (Element paceElement : paces) {
						String paceOid = paceElement.attributeValue("bsoID");
						String paceNumber = paceElement
								.attributeValue("stepNumber");
						paceOid = paceOid.substring(paceOid.length() - MiddleModelUtil.ID_COUNT);
						numbers.put(CappJavaUtil.toHexString(paceOid), stepNumber
								+ " " + paceNumber);
					}
				}

			}
		}

		generateImages(steps, rootNode, childNodes, true);

		TechTree techTree = new TechTree(numbers);
		techTree.setRoot(rootNode);
		list.add(techTree);
		list.add(childNodes);
		list.add(numbers);
		return list;
	}

	private static void generateImages(List<Element> elements,
			DefaultMutableTreeNode parentNode,
			Map<String, List<TechImageNode>> childNodes, boolean getChild) {
		if (elements != null && elements.size() != 0) {
			for (Element element : elements) {
				String stepOid = parseName(element, "bsoID");
				String stepNumber = parseName(element, "stepNumber");
				TechStepTreeNode stepNode = new TechStepTreeNode(stepOid,
						stepNumber, getChild);
				parentNode.add(stepNode);

				List<Element> images = XmlUtility.getDrawings(element);
				if (images != null) {
					List<TechImageNode> nodes = new ArrayList<TechImageNode>();
					for (Element image : images) {
						String type = parseName(image, "type");
						if (NewDrawingJPanel.MIDDLEMODEL_TYPE_NAME.equals(type)) {
							String modelName = parseName(image, "modelName");
							String drawingName = parseName(image, "drawingName");
							String path = parseName(image, "absolutePath");
							String imageOid = parseName(image, "bsoID");
							String version = parseName(image, "version");
							TechImageNode imgNode = new TechImageNode(
									modelName, drawingName, path, version,
									imageOid);
							nodes.add(imgNode);
							stepNode.add(imgNode);
						}
					}
					childNodes.put(stepOid, nodes);

					List<Element> paces = XmlUtility.getAllPaces(element);
					if (paces != null && paces.size() != 0) {
						generateImages(paces, stepNode, childNodes, false);
					}
				}
			}
		}
	}

	public static String parseName(Element element, String type) {
		Attribute attr = element.attribute(type);
		return attr == null ? null : attr.getValue();
	}

}
