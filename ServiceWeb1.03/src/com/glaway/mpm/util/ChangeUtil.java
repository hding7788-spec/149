package com.glaway.mpm.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import wt.change2.WTChangeIssue;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.FolderHelper;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.struct.StructHelper;

import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

public class ChangeUtil {

	/**
	 * 通过工艺规程zip获取工艺规程的相关信息(PBOM的oid、创建者等)
	 * @author lbzhang
	 * @date  2012-12-14下午04:38:53
	 * @param mpmPartOid
	 * @return
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	public static HashMap<String, String> getTechnicInfo(String mpmPartOid) throws FileNotFoundException, WTException, PropertyVetoException, IOException{
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(mpmPartOid);
		if (part == null) {
			GLLogger.debug("part==null");
			return null;
		}
		List<WTDocument> list = WTPartUtil.getDescribedDocumentByPart(part, "com.nriet.零件工艺");
		if (list.size() == 0) {
			GLLogger.debug("the part has not 零件工艺");
			return null;
		}
		WTDocument doc = list.get(0);
		if (doc == null) {
			GLLogger.debug("the part's 零件工艺 is empty");
			return null;
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
			return null;
		}
		return readTechnicXMLInfo(fileXMLName);
	}

	/**
	 * 读取工艺规程对应的信息
	 * @author lbzhang
	 * @date  2012-12-14下午04:15:16
	 * @param fileXMLPath
	 * @return
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> readTechnicXMLInfo(String fileXMLPath) throws FileNotFoundException{
		InputStream inputStream = new FileInputStream(fileXMLPath);
		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element rootElement = xmlUtil.getRootElement();
		HashMap<String, String> map = new HashMap<String, String>();

		if ("technics".equals(rootElement.getName())) {
			for (Element rootAttrElement : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
				String topPartOid = rootAttrElement.getAttributeValue("parentPartOid");
				map.put("topPartOid", topPartOid);
				String version = rootAttrElement.getAttributeValue("version");
				map.put("version", version);
				String oid = rootAttrElement.getAttributeValue("oid");
				map.put("oid", oid);
				String creatorOid = rootAttrElement.getAttributeValue("creatorOid");
				map.put("creatorOid", creatorOid);
				String creator = rootAttrElement.getAttributeValue("creator");
				map.put("creator", creator);
			}
		}

		return map;
	}

	/**
	 * 对最新的工艺规程进行升版(即写到工艺规程的xml要升版的版本)
	 * @author lbzhang
	 * @date  2012-12-14下午07:52:45
	 * @param mpmOid
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static String setNewVersion(String mpmOid) throws WTRuntimeException, WTException, WTPropertyVetoException{
		MPMProcessPlan mpmprocessPlan = (MPMProcessPlan)ReferenceFactory.getObjectbyOid("OR:com.ptc.windchill.mpml.processplan.MPMProcessPlan:" + mpmOid);
		MPMProcessPlan mpmprocessPlanTemp = MPMProcessPlanUtil.getMPMProcessPlanByNumber(mpmprocessPlan.getNumber());
		String oldVersion = mpmprocessPlanTemp.getVersionIdentifier().getValue();

		int old = Integer.parseInt(oldVersion);
		String newVersion = String.valueOf((old + 1));

//		char version = oldVersion.charAt(0);
//		version += 1;
//		String newVersion = String.valueOf(version);
		GLLogger.debug("set new version:" + newVersion);
		return newVersion + ".1";
	}

	/**
	 * 获取工艺规程的创建者，并加入到流程的
	 * @author lbzhang
	 * @date  2012-12-14下午08:14:30
	 * @param mpmPartOid
	 * @param roleStr     TECHNICDIVISION角色应对应的为流程中的工艺师角色
	 * @param self
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	public static void addPrincipalToTechnicWorkflow(String mpmPartOid, String roleStr, Object self) throws FileNotFoundException, WTException, PropertyVetoException, IOException{
		HashMap<String, String> technicMap = getTechnicInfo(mpmPartOid);
		String creatorOid = technicMap.get("creatorOid");
		GLLogger.debug("creatorOid==>" + creatorOid);

		WorkflowUtil.addPrincipalToProcessActivity(creatorOid, roleStr, self);
	}

	/**
	 * 更新工艺规程的zip包进行升版
	 * @author lbzhang
	 * @date  2012-12-14下午08:35:15
	 * @param mpmPartOid
	 * @throws WTException
	 * @throws WTRuntimeException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 */
	public static String jumpTechnicVersion(String mpmPartOid, String mpmOid) throws WTRuntimeException, WTException, FileNotFoundException, PropertyVetoException, IOException{
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(mpmPartOid);
		String version = "";
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

		doc = checkoutAndCheckin(doc);

		WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
		PersistenceServerHelper.manager.insert(link);
		GLLogger.debug("link====>" + link);

		version = doc.getVersionIdentifier().getValue();
		GLLogger.debug("version====>" + version);
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
			return "";
		}

		updateTechnicXML(fileXMLName, mpmOid);

		flag = ApacheZipUtil.compress(fileZipName, fileZipPath);
		if(!flag){
			GLLogger.debug(fileZipName + " 压缩不成功");
			return "";
		}

		InputStream inputStream = new FileInputStream(fileZipPath);
		if(inputStream == null){
			GLLogger.debug("压缩包文件流不存在");
			return "";
		}
		String appFileName = fileZipPath.substring(fileZipPath.lastIndexOf("\\") + 1, fileZipPath.length());//更新文档主文件的文件名称
		GLLogger.debug("appFileName===>" + appFileName);

		doc = WTDocumentUtil.setPrimaryForDocument(doc, appFileName, inputStream);//更新文档的主物件
		return version;
	}

	/**
	 * 工艺规程对应的零件关联的零件工艺文档升版序
	 * @author lbzhang
	 * @date  2012-12-15下午02:44:36
	 * @param mpmPartOid
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void jumpTechnicDocument(String mpmPartOid) throws WTRuntimeException, WTException, WTPropertyVetoException{
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(mpmPartOid);
		if (part == null) {
			GLLogger.debug("part==null");
			return ;
		}
		List<WTDocument> list = WTPartUtil.getDescribedDocumentByPart(part, "com.nriet.零件工艺");
		if (list.size() == 0) {
			GLLogger.debug("the part has not 零件工艺");
			return ;
		}
		WTDocument doc = list.get(0);
		if (doc == null) {
			GLLogger.debug("the part's 零件工艺 is empty");
			return ;
		}

		doc = checkoutAndCheckin(doc);
	}

	/**
	 * 更新工艺规程的版本
	 * @author lbzhang
	 * @date  2012-12-14下午10:46:26
	 * @param fileXMLPath
	 * @param mpmOid
	 * @throws WTRuntimeException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void updateTechnicXML(String fileXMLPath, String mpmOid) throws WTRuntimeException, WTPropertyVetoException, WTException, IOException{
		String newVersion = setNewVersion(mpmOid);//新版本
		InputStream inputStream = new FileInputStream(fileXMLPath);
		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element rootElement = xmlUtil.getRootElement();

		if ("technics".equals(rootElement.getName())) {
			for (Element rootAttrElement : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
				rootAttrElement.setAttribute("version", newVersion);
			}
		}

		FileOutputStream fileOutputStream = new FileOutputStream(fileXMLPath);
		Format format = Format.getPrettyFormat();
		format.setEncoding("GBK");

		XMLOutputter xmlOutput = new XMLOutputter(format);

		xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);
		fileOutputStream.close();
	}

	/**
	 * 对文档对象进行升版本
	 * @author lbzhang
	 * @date  2012-12-14下午09:30:30
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("deprecation")
	public static WTDocument checkoutAndCheckin(WTDocument doc) throws WTException, WTPropertyVetoException{
//		boolean isCheckout = WorkInProgressHelper.isCheckedOut(doc);
//		GLLogger.debug("isCheckout:" + isCheckout);
//
//		if(!isCheckout){
//			Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
//			doc = (WTDocument)WorkInProgressHelper.service.checkout(doc, folder, "").getWorkingCopy();
//		}
//		if(!WorkInProgressHelper.isWorkingCopy(doc)){
//			doc = (WTDocument)WorkInProgressHelper.service.workingCopyOf(doc);
//		}
//
//		if(!isCheckout){
//			doc = (WTDocument)WorkInProgressHelper.service.checkin(doc, "");
//		}

		WTDocument temp = (WTDocument)VersionControlHelper.service.newVersion(doc);
		FolderHelper.assignFolder(temp, FolderHelper.service.getFolder(doc));
		temp = (WTDocument) PersistenceHelper.manager.store(temp);
		return temp;
	}

	/**
	 * 写excel
	 * @author lbzhang
	 * @date  2013-1-5下午03:47:49
	 * @param sheet
	 * @param ci
	 * @param attachMap
	 */
	public static void wrietInfoOfChangeBill(HSSFSheet sheet, WTChangeIssue ci, HashMap<String, String> attachMap){

		//写入工艺文件更改单编号
		HSSFRow row1 = sheet.getRow(2);
		HSSFCell cell1 = row1.getCell(1);
		cell1.setCellValue(ci.getNumber());

		//写入更改原因
		HSSFCell cell2 = row1.getCell(2);
		cell2.setCellValue(attachMap.get("technicChangeReason"));

		//写入零件名称
		HSSFCell cell3 = row1.getCell(12);
		cell3.setCellValue(attachMap.get("partName"));

		//写入零件图号
		HSSFCell cell4 = row1.getCell(16);
		cell4.setCellValue(attachMap.get("partNumber"));

		//写入整件图号
		HSSFRow row2 = sheet.getRow(1);
		HSSFCell cell5 = row2.getCell(16);
		cell5.setCellValue(attachMap.get("parentPartNumber"));

		//写入拟制者
		HSSFRow row3 = sheet.getRow(18);
		HSSFCell cell6 = row3.getCell(1);
		cell6.setCellValue(ci.getCreatorFullName());

		//写入原版
		HSSFCell cell7 = row3.getCell(9);
		cell7.setCellValue(attachMap.get("version"));

		//写入完工件是否改
		HSSFCell cell8 = row3.getCell(11);
		String finishedPartIsChanged = attachMap.get("finishedPartIsChanged");
		if("true".equals(finishedPartIsChanged)){
			cell8.setCellValue("是");
		}else{
			cell8.setCellValue("否");
		}

		//写入关键过程变动
		HSSFCell cell9 = row3.getCell(13);
		String keyProcessChanged = attachMap.get("keyProcessChanged");
		if("true".equals(keyProcessChanged)){
			cell9.setCellValue("是");
		}else{
			cell9.setCellValue("否");
		}

		//写入原材料变动
		HSSFCell cell10 = row3.getCell(15);
		String rawMaterialChanged = attachMap.get("rawMaterialChanged");
		if("true".equals(rawMaterialChanged)){
			cell10.setCellValue("是");
		}else{
			cell10.setCellValue("否");
		}


		//写入新版
		HSSFRow row4 = sheet.getRow(19);
		HSSFCell cell11 = row4.getCell(9);
		String oldversion = attachMap.get("version");
		int version = Integer.parseInt(oldversion);
		version += 1;
		GLLogger.debug("version ====>" + version);
		String newVersion = String.valueOf(version);
		cell11.setCellValue(newVersion);
	}

	/**
	 * 审核终止后，工艺规程回归压缩包到原来版本
	 * @author lbzhang
	 * @date  2013-1-30上午11:18:24
	 * @param mpmPartOid
	 * @param oldVersion
	 * @throws WTRuntimeException
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static void recoverTechnic(String mpmPartOid, String oldVersion) throws WTRuntimeException, WTException{
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(mpmPartOid);
		QueryResult qr = StructHelper.service.navigateDescribedBy(part, WTPartDescribeLink.class, false);
		while(qr.hasMoreElements()){
			WTPartDescribeLink link = (WTPartDescribeLink)qr.nextElement();
			WTDocument doc = (WTDocument)link.getRoleBObject();
			String version = doc.getVersionIdentifier().getValue();
			GLLogger.debug("doc===>" + doc.getName() + "   " + doc.getNumber());
			if(TypedUtility.getTypeIdentifier(doc).getTypename().contains("com.nriet.零件工艺") && oldVersion.equals(version)){
				PersistenceServerHelper.manager.remove(link);
				GLLogger.debug("delete link!");
				PersistenceHelper.manager.delete(doc);
				GLLogger.debug("delete doc!");
			}
		}
	}

	/**
	 * 审核终止后，工艺规程版本(xml)回归
	 * @author lbzhang
	 * @date  2013-1-30上午11:30:47
	 * @param mpmPartOid
	 * @param version
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	public static void recoverTechnicVersion(String mpmPartOid, String version) throws WTRuntimeException, WTException, FileNotFoundException, PropertyVetoException, IOException{
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(mpmPartOid);
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

		GLLogger.debug("old version====>" + version);
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

		recoverVersion(fileXMLName, version);

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
	 * 获取工艺规程未改之前的版本
	 * @author lbzhang
	 * @date  2013-1-30上午11:22:26
	 * @param mpmPartOid
	 * @return
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	public static String getTechnicOrignalVersion(String mpmPartOid) throws FileNotFoundException, WTException, PropertyVetoException, IOException{
		HashMap<String, String> map = getTechnicInfo(mpmPartOid);
		String oldVersion = map.get("version");
		if(oldVersion == null){
			return "";
		}
		return oldVersion;
	}

	/**
	 * 还原工艺版本
	 * @author lbzhang
	 * @date  2013-1-30上午11:29:07
	 * @param fileXMLPath
	 * @param version
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void recoverVersion(String fileXMLPath, String version) throws IOException{
		InputStream inputStream = new FileInputStream(fileXMLPath);
		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element rootElement = xmlUtil.getRootElement();

		if ("technics".equals(rootElement.getName())) {
			for (Element rootAttrElement : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
				rootAttrElement.setAttribute("version", version);
			}
		}

		FileOutputStream fileOutputStream = new FileOutputStream(fileXMLPath);
		Format format = Format.getPrettyFormat();
		format.setEncoding("GBK");

		XMLOutputter xmlOutput = new XMLOutputter(format);

		xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);
		fileOutputStream.close();
	}
}
