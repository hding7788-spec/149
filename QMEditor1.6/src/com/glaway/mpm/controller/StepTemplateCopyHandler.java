package com.glaway.mpm.controller;

import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StepTemplateCopyHandler {
	public static void createStepTemplate(String templetDirectory,
			String templateName, Element stepElement) throws Exception {
		if ((templetDirectory == null)
				|| (templetDirectory.trim().length() == 0))
			return;
		if ((templateName == null) || (templateName.trim().length() == 0))
			return;
		if ((stepElement == null) || (stepElement.getDocument() == null)) {
			return;
		}

		File file = new File(templetDirectory);
		if (!file.exists())
			file.mkdirs();

		Document techDoc = stepElement.getDocument();
		Element techEle = XmlUtility.getTechnicsElement(techDoc);

		String techPath = WorkSpaceUtil.getTechnicsDirectory(techEle
				.attributeValue("technicsNumber"));

		copyOneStepDataToTemplateFolder(techPath, templetDirectory, stepElement);
		List list = XmlUtility.getAllPaces(stepElement);
		if ((list != null) && (list.size() > 0)) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element pace = (Element) it.next();
				copyOneStepDataToTemplateFolder(techPath, templetDirectory,
						pace);
			}

		}

		String xmlFilePath = templetDirectory + "\\" + templateName + ".xml";
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GBK");
		doc.add((Element) stepElement.clone());
		XmlUtility.saveDocument(doc, xmlFilePath);
	}

	private static void copyOneStepDataToTemplateFolder(String source,
			String target, Element stepElement) throws IOException {
		String procedureContent = XmlUtility.getProcedureContent(stepElement);
		String  htmlContent =  XmlUtil.decodeHtmlEntities(procedureContent);
		List<String> imageFiles =  XmlUtil.extractImageFilenames(htmlContent);
		if(!imageFiles.isEmpty()){
			String contentPath = target+File.separator+"content";
			File contentPathFile = new File(contentPath);
			if(!contentPathFile.exists()){
				contentPathFile.mkdirs();
			}
			String srcContentPath = source+File.separator+"content";

			for(String imiageFile :imageFiles){
				FilesUtil.copyFile(new File(srcContentPath+File.separator+imiageFile), new File(contentPath+File.separator+imiageFile));
			}
		}
		Element images = stepElement.element("images");
		if (images != null) {
			List imgList = images.elements();
			if ((imgList != null) && (imgList.size() > 0)) {
				Iterator it = imgList.iterator();
				while (it.hasNext()) {
					Element img = (Element) it.next();
					String absolutePath = img.attributeValue("absolutePath");
					if ((absolutePath != null) && (absolutePath.length() > 0)) {
						absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
						String imageSourcePath = source + "/" + absolutePath;
						String imageTargetPath = target + "/" + absolutePath;
						FilesUtil.copyDirectiory(imageSourcePath, imageTargetPath);
					}
				}

			}

		}

		Element attachs = stepElement.element("attachs");
		if (attachs != null) {
			List list = attachs.elements();
			if ((list != null) && (list.size() > 0)) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Element att = (Element) it.next();
					String temp = att.attributeValue("absolutePath");
					if ((temp != null) && (temp.length() > 0)) {
						temp = temp.substring(0, temp.lastIndexOf("/"));
						String attachSourcePath = source + "/" + temp;
						String attachTargetPath = target + "/" + temp;
						FilesUtil.copyDirectiory(attachSourcePath, attachTargetPath);
					}
				}
			}
		}

		Element additiontables = stepElement.element("additiontables");
		if (additiontables != null) {
			List list = additiontables.elements();
			if ((list != null) && (list.size() > 0)) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Element add = (Element) it.next();
					String temp = add.attributeValue("absolutePath");
					if ((temp != null) && (temp.length() > 0)) {
						if(temp.contains("/")) {
							temp = temp.substring(0, temp.lastIndexOf("/"));
						} else if(temp.contains("\\")) {
							temp = temp.substring(0, temp.lastIndexOf("\\"));
						}
						String sPath = source + "/" + temp;
						String tPath = target + "/" + temp;
						FilesUtil.copyDirectiory(sPath, tPath);
					}

				}
			}
		}
	}

	public static void generateStepFromTemplate(String technicsFolderPath,
			String templateFolder, Element stepElement) {
		if ((technicsFolderPath == null)
				|| (technicsFolderPath.trim().length() == 0))
			return;
		if ((templateFolder == null) || (templateFolder.trim().length() == 0))
			return;
		if (stepElement == null) {
			return;
		}
		copyOneStepDataToTechnicsFolder(stepElement, templateFolder,
				technicsFolderPath);
		List list = XmlUtility.getAllPaces(stepElement);
		if ((list != null) && (list.size() > 0)) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element pace = (Element) it.next();
				copyOneStepDataToTechnicsFolder(pace, templateFolder,
						technicsFolderPath);
			}
		}
	}

	private static void copyOneStepDataToTechnicsFolder(Element stepElement,
			String source, String target) {
		Element images = stepElement.element("images");
		if (images != null) {
			List imgList = images.elements();
			if ((imgList != null) && (imgList.size() > 0)) {
				Iterator it = imgList.iterator();
				while (it.hasNext()) {
					Element img = (Element) it.next();
					String absolutePath = img.attributeValue("absolutePath");
					if ((absolutePath != null) && (absolutePath.length() > 0)) {
						String imageTemp = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
						absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
						String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
						String imageSourcePath = source + "/" + absolutePath;
						String imageTargetPath = target + "/" + timeFolder;
						FilesUtil.copyDirectiory(imageSourcePath, imageTargetPath);
						img.setAttributeValue("absolutePath", timeFolder + "/" + imageTemp);
					}
				}
			}

		}

		Element attachs = stepElement.element("attachs");
		if (attachs != null) {
			List list = attachs.elements();
			if ((list != null) && (list.size() > 0)) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Element att = (Element) it.next();
					String temp = att.attributeValue("absolutePath");
					if ((temp != null) && (temp.length() > 0)) {
						String attachTemp = temp.substring(temp.lastIndexOf("/") + 1);
						String attachRoot = temp.substring(0, temp.indexOf("/"));
						temp = temp.substring(0, temp.lastIndexOf("/"));
						String attachSourcePath = source + "/" + temp;
						String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
						String attachTargetPath = target + "/" + attachRoot + "/" + timeFolder;
						FilesUtil.copyDirectiory(attachSourcePath, attachTargetPath);
						att.setAttributeValue("absolutePath", attachRoot + "/" + timeFolder + "/" + attachTemp);
					}
				}
			}
		}

		Element additiontables = stepElement.element("additiontables");
		if (additiontables != null) {
			List list = additiontables.elements();
			if ((list != null) && (list.size() > 0)) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Element att = (Element) it.next();
					String temp = att.attributeValue("absolutePath").replaceAll("\\\\", "/");
					String attachName = att.attributeValue("attachName");
					String bsoID = att.attributeValue("bsoID");
					if ((temp != null) && (temp.length() > 0)) {
						temp = temp.substring(0, temp.lastIndexOf("/"));
						String attachSourcePath = source + "/" + temp;
						String timeFolder = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
						String attachTargetPath = target + "/additionaltable/" + timeFolder;
						FilesUtil.copyDirectiory(attachSourcePath, attachTargetPath);

						String fn = UUID.randomUUID().toString().replaceAll("-", "");
						String fn2 = attachName + "_" + fn + ".doc";
						String path = "additionaltable\\" + timeFolder + "\\" + fn2;
						String path2 = path.replace(".doc", ".mht");
						att.setAttributeValue("bsoID", fn);
						att.setAttributeValue("absolutePath", path);
						att.setAttributeValue("absolutePath2", path2);

						File file = new File(attachTargetPath);
						File[] files = file.listFiles();
						for (File f : files) {
							if(f.getName().contains(bsoID)) {
								File destFile = new File(f.getAbsolutePath().replaceAll(bsoID, fn));
								f.renameTo(destFile);
							}
						}
					}
				}
			}
		}
	}
}
