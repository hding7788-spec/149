package com.glaway.mpm.util;

import java.util.Comparator;
import org.dom4j.Element;

public class ElementComparator implements Comparator {
	public int compare(Object arg0, Object arg1) {
		if (((arg0 instanceof Element)) && ((arg1 instanceof Element))) {
			Element ele1 = (Element) arg0;
			Element ele2 = (Element) arg1;
			if (ele1.getName().equals(ele2.getName())) {
				if (ele1.getName().equals("QMProcedureInfo")) {
					return compareProcedure(ele1, ele2);
				}
			}
		}
		return 0;
	}

	public int compareProcedure(Element step1, Element step2) {
		String number1 = XmlUtility.getAttributeValue(step1, "stepNumber");
		String number2 = XmlUtility.getAttributeValue(step2, "stepNumber");
		return XmlUtility.compareStepNumber(number1, number2);
	}
}
