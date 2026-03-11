package com.glaway.mpm.report;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Element;

import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.glaway.mpm.util.SWXMLUtil;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class TechnicWorkHour{

	/**
	 * 获取工时报表的html页面
	 * 参数是零件的oid
	 * @author lbzhang
	 * @date  2012-12-4上午11:43:46
	 * @param zipPath
	 * @return
	 * @throws WTException 
	 * @throws WTRuntimeException 
	 * @throws IOException 
	 * @throws PropertyVetoException 
	 * @throws FileNotFoundException 
	 */
	public static String getWorkHoursUrl(String partOid) throws WTRuntimeException, WTException, FileNotFoundException, PropertyVetoException, IOException{
		String url = "";
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(partOid);
		if (part == null) {
			GLLogger.debug("part==null");
			return "";
		}
		List<WTDocument> list = WTPartUtil.getDescribedDocumentByPart(part, "com.nriet.零件工艺");
		if (list.size() == 0) {
			GLLogger.debug("the part has not 零件工艺");
			return "";
		}
		WTDocument doc = list.get(0);
		if (doc == null) {
			GLLogger.debug("the part's 零件工艺 is empty");
			return "";
		}
		String fileZipPath = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc);// 下载的零件工艺压缩包.zip
		GLLogger.debug("filePath===>" + fileZipPath);
		
		String fileZipName = fileZipPath.substring(0, fileZipPath.lastIndexOf("."));// 解压到的资料夹
		GLLogger.debug("fileZipName===>" + fileZipName);
		String fileName = fileZipPath.substring(fileZipPath.lastIndexOf("\\") + 1, fileZipPath.lastIndexOf("."));
		GLLogger.debug("fileName===>" + fileName);
		String fileXMLName = fileZipName + File.separatorChar + fileName + ".xml";// 构建xml文件
		GLLogger.debug("fileXMLName===>" + fileXMLName);
		
		//解压工艺包zip
		boolean flag = ApacheZipUtil.decompress(fileZipPath, fileZipName);
		if (!flag) {
			GLLogger.debug(fileZipPath + "  解压不成功");
			return "";
		}
		
		//fileXMLName 为工艺规程的XML
		ArrayList<HashMap<String, String>> hourList = new ArrayList<HashMap<String,String>>();
		getWorkHours(fileXMLName, hourList);
		
		for(int i = 0; i < hourList.size(); i++){
			GLLogger.debug(i + "=====" + hourList.get(i).get("PrepareWorkHours"));
			GLLogger.debug(i + "=====" + hourList.get(i).get("TaktTime"));
			GLLogger.debug(i + "=====" + hourList.get(i).get("NumberOfGroup"));
		}
		
		String currentTime = "" + System.currentTimeMillis();
		String buildPath = Util.getCodebasePath() + File.separatorChar + "temp" + File.separatorChar + currentTime;
		File fileXML = new File(buildPath);
		if(!fileXML.exists()){
			fileXML.mkdir();
		}
		String path = Util.getCodebasePath() + File.separatorChar + "templates" + File.separatorChar + "resource";
		boolean buildHTMLFlag = makeReportHour(buildPath, path, part.getNumber(), hourList);
		if(buildHTMLFlag){
			url = buildPath + File.separatorChar + "reprothour.html";
			GLLogger.debug("url==>" + url);
		}
		return url;
	}
	
	/**
	 * 获取工艺规程的工时
	 * @author lbzhang
	 * @date  2012-12-4上午11:52:11
	 * @param filePath
	 * @param map
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public static void getWorkHours(String filePath, ArrayList<HashMap<String, String>> list) throws IOException{
		InputStream inputStream = new FileInputStream(filePath);
		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element rootElement = xmlUtil.getRootElement();
		if ("technics".equals(rootElement.getName())) {
			for (Element rootAttrElement : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
				for (Element rootChildElement : (List<Element>) rootAttrElement.getChildren()) {
					if ("steps".equals(rootChildElement.getName())) {
						structureXML(rootChildElement, list);
					}
				}
			}
		}
		inputStream.close();
	}
	
	@SuppressWarnings("unchecked")
	public static void structureXML(Element stepElement, ArrayList<HashMap<String, String>> list) {
		for (Element stepAttrElement : (List<Element>) stepElement.getChildren("QMProcedureInfo")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("PrepareWorkHours", stepAttrElement.getAttributeValue("PrepareWorkHours"));
			map.put("TaktTime", stepAttrElement.getAttributeValue("TaktTime"));
			map.put("NumberOfGroup", stepAttrElement.getAttributeValue("NumberOfGroup"));
			map.put("workShop", stepAttrElement.getAttributeValue("workShop"));
			list.add(map);
			for (Element stepChildElement : (List<Element>) stepAttrElement.getChildren()) {
				if ("paces".equals(stepChildElement.getName())) {
					structureSubXML(stepChildElement, list);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void structureSubXML(Element subStepElement, ArrayList<HashMap<String, String>> list) {
		for (Element subStepAttrElement : (List<Element>) subStepElement.getChildren("QMProcedureInfo")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("PrepareWorkHours", subStepAttrElement.getAttributeValue("PrepareWorkHours"));
			map.put("TaktTime", subStepAttrElement.getAttributeValue("TaktTime"));
			map.put("NumberOfGroup", subStepAttrElement.getAttributeValue("NumberOfGroup"));
			map.put("workShop", subStepAttrElement.getAttributeValue("workShop"));
			list.add(map);
		}
	}
	
	public static boolean makeReportHour(String buildPath,String path, String partNumber, ArrayList<HashMap<String, String>> list) {
    	try {
    		GLLogger.debug("buildPath=====>" + buildPath);
    		GLLogger.debug("path==========>" + path);
            Template temp = getTemplate("templateReporthour.jsp");
            HashMap<String, String> root = new HashMap<String, String>();
            root.put("tableshour", setReportHour(list, partNumber));
            root.put("path","../../templates/resource/glaway.PNG" );
            File file = new File(buildPath +File.separator+"reprothour.html");
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8")); 
            temp.process(root, out);
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public static Template getTemplate(String tempName) {
        try {
            Configuration cfg = new Configuration();

            cfg.setDirectoryForTemplateLoading(new File(Util.getCodebasePath() + File.separatorChar + "templates" + File.separatorChar + "jspTemplate"));

            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setDefaultEncoding("UTF-8");
            Template temp = cfg.getTemplate(tempName);
            temp.setEncoding("UTF-8");

            return temp;
        } catch (Exception e) {
            return null;
        }
    }
	
	public static String setReportHour(ArrayList<HashMap<String, String>> list, String partNumber) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if ((i + 1) % 2 == 0) {
				sb.append("<tr  bgcolor=\"#E6E6FA\" align=\"center\" > ");
			} else {
				sb.append("<tr align=\"center\"> ");
			}
			sb.append("<td  height=\"30px\" >" + list.get(i).get("workShop") + "</td>");
			sb.append("<td  height=\"30px\" >" + partNumber + "</td>");
			sb.append("<td  height=\"30px\" >" + list.get(i).get("PrepareWorkHours") + "</td>");
			sb.append("<td  height=\"30px\" >" + list.get(i).get("TaktTime") + "</td>");
			sb.append("<td  height=\"30px\" >" + list.get(i).get("NumberOfGroup") + "</td>");
			sb.append("</tr>");
		}
		return sb.toString();
	}
}
