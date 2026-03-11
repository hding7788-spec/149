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
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Element;

import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;

import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.glaway.mpm.util.SWXMLUtil;

import freemarker.template.Template;

public class KeyTechnicStep implements RemoteAccess{

	/**
	 * 获取关键工序工步报表的html页面 参数是零件的oid
	 *
	 * @author lbzhang
	 * @date 2012-12-4下午08:35:45
	 * @param partOid
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	public static String getKeyTechnicUrl(String technicsOid) throws WTRuntimeException, WTException,
			FileNotFoundException, PropertyVetoException, IOException {
		String url = "";
		if((technicsOid == null)||("".equals(technicsOid))) {
			return url;
		}
		if(!technicsOid.contains("WTDocument")) {
			technicsOid = "wt.doc.WTDocument:" + technicsOid;
		}
//		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(technicsOid);
//		if (part == null) {
//			GLLogger.debug("part==null");
//			return "";
//		}
//		List<WTDocument> list = WTPartUtil.getDescribedDocumentByPart2(part, "");
//		if (list.size() == 0) {
//			GLLogger.debug("the part has not 零件工艺");
//			return "";
//		}
		WTDocument doc = (WTDocument)ReferenceFactory.getObjectbyOid(technicsOid);
		if (doc == null) {
			GLLogger.debug("the part's 零件工艺 is empty");
			return "";
		}
		QueryResult qr = VersionControlHelper.service.allVersionsFrom(doc);
		doc = (WTDocument)qr.nextElement();
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


		String currentTime = "" + System.currentTimeMillis();
		String buildPath = Util.getCodebasePath() + File.separatorChar + "temp" + File.separatorChar + currentTime;//目标路径
		File fileXML = new File(buildPath);
		if(!fileXML.exists()){
			fileXML.mkdir();
		}

		String fileContentPath = fileZipName + File.separatorChar + "content";
		File fileContent = new File(fileContentPath);
		if(fileContent.exists()){
			File fromFile = new File(fileContentPath);
			File toFile = new File(buildPath);

			FileUtil fu = new FileUtil();
			fu.dirFrom = fromFile;
			fu.dirTo = toFile;

			fu.listFileInDir(fromFile);
			GLLogger.debug("copy is over!");
		}

		ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String,String>>();//关键工序工步数据信息
		ArrayList<String> nameNumber = getKeyTS(fileXMLName, dataList, buildPath);//工艺名称

//		String path = Util.getCodebasePath() + File.separatorChar + "templates" + File.separatorChar + "resource";
		String path = Util.getCodebasePath() + File.separatorChar + "templates" + File.separatorChar + "resource";

		boolean buildHTMLFlag = makeReportKey(buildPath, path, nameNumber, dataList);

		if(buildHTMLFlag){
			url = buildPath + File.separatorChar + "reprotkey.html";
			GLLogger.debug("url==>" + url);
		}

		return url;
	}

	/**
	 * 获取关键工序工步
	 * @author lbzhang
	 * @date  2012-12-4下午08:38:29
	 * @param filePath
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getKeyTS(String filePath, ArrayList<HashMap<String, String>> list, String contentPath) throws IOException{
		ArrayList<String> nameNumber = new ArrayList<String>();
		InputStream inputStream = new FileInputStream(filePath);
		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element rootElement = xmlUtil.getRootElement();
		if ("technics".equals(rootElement.getName())) {
			for (Element rootAttrElement : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
				nameNumber.add(rootAttrElement.getAttributeValue("technicsName"));
				nameNumber.add(rootAttrElement.getAttributeValue("partNumber"));
				for (Element rootChildElement : (List<Element>) rootAttrElement.getChildren()) {
					if ("steps".equals(rootChildElement.getName())) {
						structureXML(rootChildElement, list, contentPath);
					}
				}
			}
		}
		inputStream.close();
		return nameNumber;
	}

	/**
	 * 工序
	 * @author lbzhang
	 * @date  2013-2-18下午04:08:14
	 * @param stepElement
	 * @param list
	 */
	@SuppressWarnings("unchecked")
	public static void structureXML(Element stepElement, ArrayList<HashMap<String, String>> list, String contentPath) {
		for (Element stepAttrElement : (List<Element>) stepElement.getChildren("QMProcedureInfo")) {
			String isKey = stepAttrElement.getAttributeValue("isKeyStep");
			String stepNumber = stepAttrElement.getAttributeValue("stepNumber");
			GLLogger.debug("stepNumber===>" + stepNumber);
			GLLogger.debug("isKey===>" + isKey);
			if("true".equals(isKey)){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("stepName", stepAttrElement.getAttributeValue("stepName"));
				map.put("workShop", stepAttrElement.getAttributeValue("workShop"));
				map.put("workType", stepAttrElement.getAttributeValue("workType"));

				map.put("isKey", stepAttrElement.getAttributeValue("isKey"));
				for(Element procedureElement : (List<Element>) stepAttrElement.getChildren("procedureContent")){
					String procedureContent = procedureElement.getText();
					GLLogger.debug("procedureContent=====>" + procedureContent);
					if(procedureContent == null){
						procedureContent = "";
					}
					map.put("procedureContent", procedureContent);
				}

				list.add(map);
			}
			for (Element stepChildElement : (List<Element>) stepAttrElement.getChildren()) {
				if ("paces".equals(stepChildElement.getName())) {
					structureSubXML(stepChildElement, list, stepNumber, contentPath);
				}
			}
		}
	}

	/**
	 * 工步
	 * @author lbzhang
	 * @date  2013-2-18下午04:08:31
	 * @param subStepElement
	 * @param list
	 */
	@SuppressWarnings("unchecked")
	public static void structureSubXML(Element subStepElement, ArrayList<HashMap<String, String>> list, String parentNumber, String contentPath) {
		for (Element subStepAttrElement : (List<Element>) subStepElement.getChildren("QMProcedureInfo")) {
			String isKey = subStepAttrElement.getAttributeValue("isKey");
			String subNumber = subStepAttrElement.getAttributeValue("stepNumber");
			GLLogger.debug("subNumber===>" + subNumber);
			GLLogger.debug("	isKey===>" + isKey);
			if("true".equals(isKey)){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("stepName", subStepAttrElement.getAttributeValue("stepName"));
				map.put("workShop", subStepAttrElement.getAttributeValue("workShop"));
				map.put("workType", subStepAttrElement.getAttributeValue("workType"));
				for(Element procedureElement : (List<Element>) subStepAttrElement.getChildren("procedureContent")){
					String procedureContent = procedureElement.getText();
					GLLogger.debug("procedureContent=====>" + procedureContent);
					if(procedureContent == null){
						procedureContent = "";
					}

					FileUtil fu = new FileUtil();
					String jpg = parentNumber + "&&" + subNumber + ".JPG";
					GLLogger.debug("jpg===>" + jpg);
					if(fu.isexistJPG(contentPath, jpg)){
						map.put("procedureContent", jpg);
					}else{
						map.put("procedureContent", procedureContent);
					}
				}

				list.add(map);
			}
		}
	}

	public static boolean makeReportKey(String buildPath, String path, ArrayList<String> nameNumber ,ArrayList<HashMap<String, String>> list) {
    	try {
            Template temp = TechnicWorkHour.getTemplate("templateReportkey.jsp");
            HashMap<String, String> root = new HashMap<String, String>();
            root.put("tableskey", setReportKey(nameNumber, list));
            root.put("path","../../templates/resource/glaway.PNG" );
            File file = new File(buildPath +File.separator +"reprotkey.html");
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
            temp.process(root, out);
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	public static String setReportKey(ArrayList<String> nameNumber, ArrayList<HashMap<String, String>> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if ((i + 1) % 2 == 0) {
				sb.append("<tr  bgcolor=\"#E6E6FA\" align=\"center\" > ");
			} else {
				sb.append("<tr align=\"center\"> ");
			}

			String content = list.get(i).get("procedureContent");
			sb.append("<td  height=\"30px\" >" + nameNumber.get(0) + "</td>");
			sb.append("<td  height=\"30px\" >" + list.get(i).get("stepName") + "</td>");
			sb.append("<td  height=\"30px\" >" + list.get(i).get("workShop") + "</td>");
			sb.append("<td  height=\"30px\" >" + list.get(i).get("workType") + "</td>");
			sb.append("<td  height=\"30px\" >" + nameNumber.get(1) + "</td>");

			if(content.indexOf(".JPG") != -1){
				sb.append("<td  height=\"30px\" >" + "<img src=\"" + content + "\" align=\"center\"/>" + "</td>");
			}else{
				sb.append("<td  height=\"30px\" >" + list.get(i).get("procedureContent") + "</td>");
			}
			sb.append("<td  height=\"30px\" >是</td>");
			sb.append("</tr>");
		}
		return sb.toString();
	}

	public static void main(String[] args) throws RemoteException, InvocationTargetException{
		RemoteMethodServer server = RemoteMethodServer.getDefault();
		server.setUserName("wcadmin");
		server.setPassword("wcadmin");
		server.invoke("test", KeyTechnicStep.class.getName(), null, null, null);

	}

	public static void test() throws WTRuntimeException, FileNotFoundException, WTException, PropertyVetoException, IOException{
		getKeyTechnicUrl("wt.part.WTPart:482222");
	}
}
