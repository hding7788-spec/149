package com.glaway.mpm.util;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;

import com.faw_qm.speChar.speChar.SpeIcon;

public class ProcedurePictureCreateUtil {
	private static boolean isAllCreate = false;

	public static String createProcedurePicture(String technicsNumber,
			String technicsName, String technicsCategory) throws Exception {
		String technicsDirectory = "";
		Document document = null;
		if ("rework".equals(technicsCategory)) {
			technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
			document = WorkSpaceUtil.getReWorkTechnicsDocumentByTechnicsName(technicsNumber, technicsName);
		} else if ("temp".equals(technicsCategory)) {
			technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
			document = WorkSpaceUtil.getTempTechnicsDocumentByTechnicsName(technicsNumber, technicsName);
		} else {
			technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
			document = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
		}
		if (technicsDirectory != null && document != null) {
			createPictureDirectory(technicsDirectory);
			operateDocument(document, technicsDirectory);
		}
		return technicsDirectory;
	}

	public static void createPictureDirectory(String technicsDirectory) {
		File file = new File(technicsDirectory + File.separator + "content");
		if (file.exists()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				//files[i].delete();
			}
		} else {
			file.mkdir();
		}
	}

	public static void operateDocument(Document document, String technicsDirectory) {
		Element rootElement = document.getRootElement();
		for (Iterator technicsIterator = rootElement.elementIterator(); technicsIterator
				.hasNext();) {
			Element technicsElement = (Element) technicsIterator.next();
			for (Iterator stepIterator = technicsElement.element("steps")
					.elementIterator(); stepIterator.hasNext();) {
				Element stepElement = (Element) stepIterator.next();
				String stepContent = XmlUtility.getProcedureContent(stepElement);
				String stepNumber = stepElement.attributeValue("stepNumber");
				String pictureDirectory = technicsDirectory + "\\" + "content" + "\\";
				String fileName = pictureDirectory + stepNumber + "_" + ".JPG";
				// if (isCreatePicture(stepContent)) {
				//SpecialCharUtil.generateImage(stepContent, 500, fileName);
				// }
				for (Iterator paceIterator = stepElement.element("paces")
						.elementIterator(); paceIterator.hasNext();) {
					Element paceElement = (Element) paceIterator.next();
					String paceContent = XmlUtility.getProcedureContent(paceElement);
					String paceNumber = paceElement.attributeValue("stepNumber");
					fileName = pictureDirectory + stepNumber + "_" + paceNumber + ".JPG";
					// if (isCreatePicture(paceContent))
					//SpecialCharUtil.generateImage(paceContent, 500, fileName);
				}
			}
		}
	}

	private static boolean isCreatePicture(String content) {
		boolean b = isHasSpecialChar(content);
		return (b) || (isAllCreate);
	}

	private static boolean isHasSpecialChar(String content) {
		if ((content == null) || (content.trim().equals("")))
			return false;
		if (content.indexOf(SpeIcon.speItem) >= 0)
			return true;
		return false;
	}
}
