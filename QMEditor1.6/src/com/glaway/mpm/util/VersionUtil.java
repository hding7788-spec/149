package com.glaway.mpm.util;

import org.dom4j.Document;
import org.dom4j.Element;

public class VersionUtil {
	public static String getVersionByTechnicsNumber(String technicsNumber)
			throws Exception {
		Document document = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
		if (document == null)
			return null;
		Element technicsElement = XmlUtility.getTechnicsElement(document);
		return technicsElement.attributeValue("version");
	}
}
