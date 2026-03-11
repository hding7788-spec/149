package com.glaway.mpm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.session.SessionServerHelper;

public class FillTime {
	public static void fillTimeXML(String partOid, HashMap<String, ArrayList<String>> map) throws Exception {
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(partOid);
		if (part == null) {
			GLLogger.debug("part==null");
			return;
		}
		List<WTDocument> list = WTPartUtil.getDescribedDocumentByPart(part, "com.nriet.零件工艺");
		if (list.size() == 0) {
			GLLogger.debug("the part has not 零件工艺");
			return;
		}
		WTDocument doc = list.get(0);
		if (doc == null) {
			GLLogger.debug("the part's 零件工艺 is empty");
			return;
		}
		String fileZipPath = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc);// 下载的零件工艺压缩包.zip
		GLLogger.debug("filePath===>" + fileZipPath);

		String fileZipName = fileZipPath.substring(0, fileZipPath.lastIndexOf("."));// 解压到的资料夹
		GLLogger.debug("fileZipName===>" + fileZipName);
		String fileName = fileZipPath.substring(fileZipPath.lastIndexOf("\\") + 1, fileZipPath.lastIndexOf("."));
		GLLogger.debug("fileName===>" + fileName);
		String fileXMLName = fileZipName + File.separatorChar + fileName + ".xml";// 构建xml文件
		GLLogger.debug("fileXMLName===>" + fileXMLName);

		// 解压zip工艺规程包
		boolean flag = ApacheZipUtil.decompress(fileZipPath, fileZipName);
		if (!flag) {
			GLLogger.debug(fileZipPath + "  解压不成功");
			return;
		}

		fileTimeUpdateXML(fileXMLName, map);
		
		flag = ApacheZipUtil.compress(fileZipName, fileZipPath);
		if(!flag){
			GLLogger.debug(fileZipName + " 压缩不成功");
			return;
		}
		
		InputStream inputStream = new FileInputStream(fileZipPath);
		if(inputStream == null){
			GLLogger.debug("压缩包文件流不存在");
			return;
		}
		String appFileName = fileZipPath.substring(fileZipPath.lastIndexOf("\\") + 1, fileZipPath.length());//更新文档主文件的文件名称
		GLLogger.debug("appFileName===>" + appFileName);
		
		doc = WTDocumentUtil.setPrimaryForDocument(doc, appFileName, inputStream);//更新文档的主物件
		
	}

	/**
	 * 更改工艺规程的XML文件
	 * 
	 * @author lbzhang
	 * @date 2012-11-30下午02:03:30
	 * @param filePath
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void fileTimeUpdateXML(String filePath, HashMap<String, ArrayList<String>> map) throws Exception {
		InputStream inputStream = new FileInputStream(filePath);
		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element rootElement = xmlUtil.getRootElement();
		if ("technics".equals(rootElement.getName())) {
			for (Element rootAttrElement : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
				for (Element rootChildElement : (List<Element>) rootAttrElement.getChildren()) {
					if ("steps".equals(rootChildElement.getName())) {
						structureXML(rootChildElement, map);
					}
				}
			}
		}

		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		Format format = Format.getPrettyFormat();
		format.setEncoding("GBK");

		XMLOutputter xmlOutput = new XMLOutputter(format);

		xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);
		fileOutputStream.close();
	}

	@SuppressWarnings("unchecked")
	public static void structureXML(Element stepElement, HashMap<String, ArrayList<String>> map) throws Exception {
		for (Element stepAttrElement : (List<Element>) stepElement.getChildren("QMProcedureInfo")) {
			String technicOid = stepAttrElement.getAttributeValue("oid");
			String technicBsoId = stepAttrElement.getAttributeValue("bsoID");
			GLLogger.debug("technicOid==>" + technicOid);
			GLLogger.debug("bsoID=======>" + technicBsoId);
			// 设置工步或者工序的工时
			ArrayList<String> attrList = map.get(technicBsoId);
			
			if (attrList != null && attrList.size() > 0) {
				stepAttrElement.setAttribute("PrepareWorkHours", attrList.get(0));
				MPMProcessPlanUtil.setMPMProcessPlanAttri(technicOid, "PrepareWorkHours", attrList.get(0));
				
				stepAttrElement.setAttribute("TaktTime", attrList.get(1));
				MPMProcessPlanUtil.setMPMProcessPlanAttri(technicOid, "TaktTime", attrList.get(1));
				
				stepAttrElement.setAttribute("NumberOfGroup", attrList.get(2));
				MPMProcessPlanUtil.setMPMProcessPlanAttri(technicOid, "NumberOfGroup", attrList.get(2));
			}

			for (Element stepChildElement : (List<Element>) stepAttrElement.getChildren()) {
				if ("paces".equals(stepChildElement.getName())) {
					structureSubXML(stepChildElement, map);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void structureSubXML(Element subStepElement, HashMap<String, ArrayList<String>> map) throws Exception {
		for (Element subStepAttrElement : (List<Element>) subStepElement.getChildren("QMProcedureInfo")) {
			String technicOid = subStepAttrElement.getAttributeValue("oid");
			String technicBsoId = subStepAttrElement.getAttributeValue("bsoID");
			System.out.println("		technicOid====>" + technicOid);
			System.out.println("		technicBsoId==>" + technicBsoId);
			
			// 设置工步或者工序的工时
			ArrayList<String> attrList = map.get(technicBsoId);
			boolean access = SessionServerHelper.manager.setAccessEnforced(false);
			try{
				if (attrList != null && attrList.size() > 0) {
					
					subStepAttrElement.setAttribute("PrepareWorkHours", attrList.get(0));
					MPMProcessPlanUtil.setMPMProcessPlanAttri(technicOid, "PrepareWorkHours", attrList.get(0));
					
					subStepAttrElement.setAttribute("TaktTime", attrList.get(1));
					MPMProcessPlanUtil.setMPMProcessPlanAttri(technicOid, "TaktTime", attrList.get(1));
					
					subStepAttrElement.setAttribute("NumberOfGroup", attrList.get(2));
					MPMProcessPlanUtil.setMPMProcessPlanAttri(technicOid, "NumberOfGroup", attrList.get(2));
				}
			}finally{
				SessionServerHelper.manager.setAccessEnforced(access);
			}
		}
	}
	
	/**
	 * 处理json数据格式
	 * @author lbzhang
	 * @date  2012-12-7下午08:09:45
	 * @param jsonStr
	 * @return
	 */
	public static String operationJSONStr(String jsonStr){
		String result1 = "";
		String[] result2 = jsonStr.split("}");
		for(int i = 0; i < result2.length; i++){
			String str = result2[i];
			String temp = "";
			if(str.indexOf("oid:") > 0){
				String first = str.substring(0, str.indexOf("oid:") + 4);
				String second = str.substring(str.indexOf("oid:") + 4, str.indexOf("zbgs:"));
				String third = str.substring(str.indexOf("zbgs:"));
				
				temp = first + second.replace(":", "|") + third + "}";
			}
			if(!"".equals(temp)){
				result1 += temp;
			}else{
				result1 += str;
			}
		}
		result1 = "{msg:" + result1 + "}";
		return result1;
	}
}
