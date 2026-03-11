package com.glaway.mpm.util;

import java.rmi.server.UID;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.Element;

import com.glaway.mpm.parameter.helper.MPMParameterProcessor;

public class CopyUtil {
	public static void copyTechnics(Element sourceElement, Element targetElement) {
		List deleteProcedures = targetElement.element("steps").elements();
		for (int i = 0; i < deleteProcedures.size(); i++) {
			deleteProcedures.remove(i--);
		}
		List addProcedures = sourceElement.element("steps").elements();
		for (int i = 0; i < addProcedures.size(); i++) {
			deleteProcedures.add((Element) ((Element) addProcedures.get(i))
					.clone());
		}
	}

	public static void copyStep(Element sourceStepElement, Element targetTechnicsElement) {
		sourceStepElement.attribute("bsoID").setValue(new UID().toString());
		Element paces = sourceStepElement.element("paces");
		if(paces != null){
			List paceLists = paces.elements();
			for (int i = 0; i < paceLists.size(); i++) {
				Element element = (Element) paceLists.get(i);
				element.attribute("bsoID").setValue(new UID().toString());
			}
		}
		int stepinterval = 10;
		int max = 0;
		List procedures = targetTechnicsElement.element("steps").elements();
		for (int i = 0; i < procedures.size(); i++) {
			Element element = (Element) procedures.get(i);
			/**?????,?????????????????  add by liangbo 20180403*/
			MPMParameterProcessor.removeComSpeElements(element);
			int temp = XmlUtility.getSubFigure(element.attributeValue("stepNumber"));
			if (temp > max)
				max = temp;
		}
		sourceStepElement.attribute("stepNumber").setValue(String.valueOf(max + stepinterval));
		targetTechnicsElement.element("steps").add(sourceStepElement);
	}

	public static void copyTechnics(Element sourceElement,
			Element targetElement, boolean isOverWrite) {
		List targeElementtList = targetElement.element("steps").elements(
				"QMProcedureInfo");
		Vector targeIndexVector = new Vector();
		for (int i = 0; i < targeElementtList.size(); i++) {
			targeIndexVector.add(((Element) targeElementtList.get(i))
					.attributeValue("stepNumber"));
		}

		Element sourceSteps = sourceElement.element("steps");

		for (Iterator it = sourceSteps.elementIterator("QMProcedureInfo"); it
				.hasNext();) {
			Element sourceProcedureElement = (Element) it.next();

			String sourceStepNumber = sourceProcedureElement
					.attributeValue("stepNumber");

			if (targeIndexVector.contains(sourceStepNumber)) {
				if (isOverWrite) {
					int index = targeIndexVector.indexOf(sourceStepNumber);

					targeElementtList.remove(index);

					Element newProcedureElement = getNewProcedureElement(sourceProcedureElement);

					targeElementtList.add(index, newProcedureElement);
				}

			} else {
				Element newProcedureElement = getNewProcedureElement(sourceProcedureElement);

				XmlUtility.addProcedure(targetElement, newProcedureElement);
			}

		}

		XmlUtility.orderSteps(targetElement);
	}

	private static Element getNewProcedureElement(Element oldProcedureElement) {
		Element newProcedureElement = XmlUtility.createProcedure();

		setProcedureMessage(oldProcedureElement, newProcedureElement);

		addAdditionalMessage(oldProcedureElement, newProcedureElement);

		for (Iterator pace = oldProcedureElement.element("paces")
				.elementIterator("QMProcedureInfo"); pace.hasNext();) {
			Element oldPaceElement = (Element) pace.next();
			Element newPaceElement = XmlUtility.createProcedure();

			setProcedureMessage(oldPaceElement, newPaceElement);

			addAdditionalMessage(oldPaceElement, newPaceElement);
			newProcedureElement.element("paces").add(newPaceElement);
		}
		return newProcedureElement;
	}

	private static void setProcedureMessage(Element oldE, Element newE) {
		newE.setAttributeValue("bsoID", oldE.attributeValue("bsoID"));
		newE.setAttributeValue("stepNumber", oldE.attributeValue("stepNumber"));
		newE.setAttributeValue("stepName", oldE.attributeValue("stepName"));
		newE.setAttributeValue("workType", oldE.attributeValue("workType"));
		newE.setAttributeValue("workShop", oldE.attributeValue("workShop"));
		newE.setAttributeValue("stepHour", oldE.attributeValue("stepHour"));
		newE.setAttributeValue("isKey", oldE.attributeValue("isKey"));
		newE.setAttributeValue("creoView", oldE.attributeValue("creoView"));
		newE.setAttributeValue("cortonaID", oldE.attributeValue("cortonaID"));
		newE.setAttributeValue("procedureType",
				oldE.attributeValue("procedureType"));
		newE.setAttributeValue("preBsoID", oldE.attributeValue("preBsoID"));
		newE.setAttributeValue("nextBsoID", oldE.attributeValue("nextBsoID"));
		newE.element("procedureContent").setText(
				oldE.element("procedureContent").getText());
	}

	private static void addAdditionalMessage(Element oldElement,
			Element newElement) {
		for (Iterator part = oldElement.element("parts").elementIterator(
				"QMPartInfo"); part.hasNext();) {
			Element next = (Element) part.next();
			Element element = XmlUtility.createPart();
			setPartMessage(next, element);
			newElement.element("parts").add(element);
		}

		for (Iterator tool = oldElement.element("tools").elementIterator(
				"QMToolInfo"); tool.hasNext();) {
			Element next = (Element) tool.next();
			Element element = XmlUtility.createTool();
			setToolMessage(next, element);
			newElement.element("tools").add(element);
		}

		for (Iterator material = oldElement.element("materials")
				.elementIterator("QMMaterialInfo"); material.hasNext();) {
			Element next = (Element) material.next();
			Element element = XmlUtility.createMaterial();
			setMaterialMessage(next, element);
			newElement.element("QMMaterialInfo").add(element);
		}

		for (Iterator equip = oldElement.element("equips").elementIterator(
				"QMEquipmentInfo"); equip.hasNext();) {
			Element next = (Element) equip.next();
			Element element = XmlUtility.createEquip();
			setEquipMessage(next, element);
			newElement.element("QMEquipmentInfo").add(element);
		}

		for (Iterator image = oldElement.element("images").elementIterator(
				"PDrawingInfo"); image.hasNext();) {
			Element next = (Element) image.next();
			Element element = XmlUtility.createImage();
			setImageMessage(next, element);
			newElement.element("PDrawingInfo").add(element);
		}
	}

	private static Element setPartMessage(Element oldE, Element newE) {
		newE.setAttributeValue("bsoID", oldE.attributeValue("bsoID"));
		newE.setAttributeValue("partNumber", oldE.attributeValue("partNumber"));
		newE.setAttributeValue("partName", oldE.attributeValue("partName"));
		newE.setAttributeValue("material", oldE.attributeValue("material"));
		newE.setAttributeValue("dutu", oldE.attributeValue("dutu"));
		newE.setAttributeValue("remark", oldE.attributeValue("remark"));
		newE.setAttributeValue("useCount", oldE.attributeValue("useCount"));
		return newE;
	}

	private static Element setToolMessage(Element oldE, Element newE) {
		newE.setAttributeValue("bsoID", oldE.attributeValue("bsoID"));
		newE.setAttributeValue("toolNum", oldE.attributeValue("toolNum"));
		newE.setAttributeValue("toolName", oldE.attributeValue("toolName"));
		newE.setAttributeValue("toolStdNum", oldE.attributeValue("toolStdNum"));
		newE.setAttributeValue("toolSpec", oldE.attributeValue("toolSpec"));
		newE.setAttributeValue("useCount", oldE.attributeValue("useCount"));
		return newE;
	}

	private static Element setMaterialMessage(Element oldE, Element newE) {
		newE.setAttributeValue("bsoID", oldE.attributeValue("bsoID"));
		newE.setAttributeValue("materialNumber",
				oldE.attributeValue("materialNumber"));
		newE.setAttributeValue("materialName",
				oldE.attributeValue("materialName"));
		newE.setAttributeValue("materialCrision",
				oldE.attributeValue("materialCrision"));
		newE.setAttributeValue("materialState",
				oldE.attributeValue("materialState"));
		newE.setAttributeValue("materialCode",
				oldE.attributeValue("materialCode"));
		newE.setAttributeValue("useCount", oldE.attributeValue("useCount"));
		return newE;
	}

	private static Element setEquipMessage(Element oldE, Element newE) {
		newE.setAttributeValue("bsoID", oldE.attributeValue("bsoID"));
		newE.setAttributeValue("eqNum", oldE.attributeValue("eqNum"));
		newE.setAttributeValue("eqName", oldE.attributeValue("eqName"));
		newE.setAttributeValue("eqModel", oldE.attributeValue("eqModel"));
		newE.setAttributeValue("useCount", oldE.attributeValue("useCount"));
		return newE;
	}

	private static Element setImageMessage(Element oldE, Element newE) {
		newE.setAttributeValue("bsoID", oldE.attributeValue("bsoID"));
		newE.setAttributeValue("drawingName",
				oldE.attributeValue("drawingName"));
		newE.setAttributeValue("drawingType",
				oldE.attributeValue("drawingType"));
		newE.setAttributeValue("drawingSize",
				oldE.attributeValue("drawingSize"));
		newE.setAttributeValue("absolutePath",
				oldE.attributeValue("absolutePath"));
		return newE;
	}
	/**
	 * 复制工步节点
	 * @author 王鑫磊、杨青
	 * @校对           马崇奇
	 * @param sourcePaceElement
	 * @param targetStepElement
	 * @date 2015-6-3
	 * @return
	 */

	public static Element copyPace(Element sourcePaceElement, Element targetStepElement) {
		sourcePaceElement.attribute("bsoID").setValue(new UID().toString());
		int paceinterval = 1;
		int max = 0;
		List paces = targetStepElement.element("paces").elements();
		for (int i = 0; i < paces.size(); i++) {
			Element element = (Element) paces.get(i);
			int temp = XmlUtility.getSubFigure(element.attributeValue("stepNumber"));
			if (temp > max)
				max = temp;
		}
		sourcePaceElement.attribute("stepNumber").setValue(String.valueOf(max + paceinterval));
		targetStepElement.element("paces").add(sourcePaceElement);
		return sourcePaceElement;
	}



}
