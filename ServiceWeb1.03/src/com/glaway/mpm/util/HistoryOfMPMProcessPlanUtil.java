package com.glaway.mpm.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.glaway.mpm.mpmresource.Constants;

import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.util.WTException;

public class HistoryOfMPMProcessPlanUtil {

	/**
	 * 记录工艺规程的签审记录,写到发布页面中
	 * @author lbzhang
	 * @date  2012-12-27下午11:53:03
	 * @param part
	 * @param self
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	public static void setTechnicHistoryOfWorkflow(WTPart part, Object self, String type) throws WTException, FileNotFoundException, PropertyVetoException, IOException{

//		WTDocument doc = TechnicPreview.getZipDoc(part, null, type);
//
//		String fileZipPath = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc);// 下载的零件工艺压缩包.zip
//		GLLogger.debug("history filePath===>" + fileZipPath);
//
//		String fileZipName = fileZipPath.substring(0, fileZipPath.lastIndexOf("."));// 解压到的资料夹
//		String fileName = fileZipPath.substring(fileZipPath.lastIndexOf(File.separator) + 1, fileZipPath.lastIndexOf("."));
//		String fileXMLName = fileZipName + File.separatorChar + fileName + ".xml";// 构建xml文件
//
//		// 解压zip工艺规程包
//		boolean flag = ApacheZipUtil.decompress(fileZipPath, fileZipName);
//		if (!flag) {
//			GLLogger.debug(fileZipPath + "  解压不成功");
//			return;
//		}
//
//		ArrayList<HashMap<String, String>> historyList = WorkflowUtil.getWfProcessRecordes(self);
//
//		setHistoryOfTechnic(fileXMLName, historyList);
//
//		flag = ApacheZipUtil.compress(fileZipName, fileZipPath);
//		if(!flag){
//			GLLogger.debug(fileZipName + " 压缩不成功");
//			return;
//		}
//
//		InputStream inputStream = new FileInputStream(fileZipPath);
//		if(inputStream == null){
//			GLLogger.debug("压缩包文件流不存在");
//			return;
//		}
//
//		String appFileName = fileZipPath.substring(fileZipPath.lastIndexOf(File.separator) + 1, fileZipPath.length());//更新文档主文件的文件名称
//		GLLogger.debug("appFileName===>" + appFileName);
//
//		doc = WTDocumentUtil.setPrimaryForDocument(doc, appFileName, inputStream);//更新文档的主物件
	}

	/**
	 * 流程的签审记录写回到工艺规程的xml中，并重新上传
	 * @author lbzhang
	 * @date  2012-12-27下午11:51:04
	 * @param filePath
	 * @param historyList
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void setHistoryOfTechnic(String filePath, ArrayList<HashMap<String, String>> historyList) throws IOException{
		InputStream inputStream = new FileInputStream(filePath);
		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element rootElement = xmlUtil.getRootElement();

		if ("technics".equals(rootElement.getName())) {
			for (Element rootAttrElement : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
				for(int i = 0; i < historyList.size(); i++){
					HashMap<String, String> historyMap = historyList.get(i);
					String actName = historyMap.get("actName");
					GLLogger.debug("actName===>" + actName);
					String actUserName = historyMap.get("actUserName");
					GLLogger.debug("actUserName==>" + actUserName);
					String userComment = historyMap.get("userComment");
					GLLogger.debug("userComment==>" + userComment);
					String actTime = historyMap.get("actTime");
					GLLogger.debug("actTime===>" + actTime);
					if(actName == null){
						continue;
					}
					if( actName.indexOf(Constants.TechnicInworkRevise) > 0){
						rootAttrElement.setAttribute("name1", actName);
						rootAttrElement.setAttribute("user1", actUserName);
						rootAttrElement.setAttribute("date1", actTime);
						rootAttrElement.setAttribute("comment1", userComment);
					}
					if( actName.indexOf(Constants.TechnicShenhe) > 0){
						rootAttrElement.setAttribute("name2", actName);
						rootAttrElement.setAttribute("user2", actUserName);
						rootAttrElement.setAttribute("date2", actTime);
						rootAttrElement.setAttribute("comment2", userComment);
					}
					if( actName.indexOf(Constants.TechnicPizhun) > 0){
						rootAttrElement.setAttribute("name3", actName);
						rootAttrElement.setAttribute("user3", actUserName);
						rootAttrElement.setAttribute("date3", actTime);
						rootAttrElement.setAttribute("comment3", userComment);
					}
					if(actName.indexOf(Constants.TempTechnicShenhe) > 0){
						rootAttrElement.setAttribute("name2", actName);
						rootAttrElement.setAttribute("user2", actUserName);
						rootAttrElement.setAttribute("date2", actTime);
						rootAttrElement.setAttribute("comment2", userComment);
					}
					if(actName.indexOf(Constants.TempTechnicBohui) > 0){
						rootAttrElement.setAttribute("name2", actName);
						rootAttrElement.setAttribute("user2", actUserName);
						rootAttrElement.setAttribute("date2", actTime);
						rootAttrElement.setAttribute("comment2", userComment);
					}
					if(actName.indexOf(Constants.ReworkTechnicShenhe) > 0){
						rootAttrElement.setAttribute("name2", actName);
						rootAttrElement.setAttribute("user2", actUserName);
						rootAttrElement.setAttribute("date2", actTime);
						rootAttrElement.setAttribute("comment2", userComment);
					}
					if(actName.indexOf(Constants.ReworkTechnicPizhun) > 0){
						rootAttrElement.setAttribute("name3", actName);
						rootAttrElement.setAttribute("user3", actUserName);
						rootAttrElement.setAttribute("date3", actTime);
						rootAttrElement.setAttribute("comment3", userComment);
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
}
