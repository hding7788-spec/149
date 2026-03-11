/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.e3;


import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.util.*;
import ext.casc.common.PartCommonHelper;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.sop.util.StringUtil;
import ext.casc.util.CSCPrincipal;
import ext.casc.util.Tools;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

import java.io.*;
import java.util.List;
import java.util.Locale;


public class UpdateE3ProcessPlanCommand implements WebServiceCommand, InitializingBean {
	static Logger LOGGER = Logger.getLogger(UpdateE3ProcessPlanCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "updateE3ProcessPlan";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
        JSONObject jrtnObj = new JSONObject();
		JSONObject jparams;
		InputStream inputStream = null;
		InputStream inputStream2 = null;
		Transaction trans = null;
		String version = "";
		try {
			jparams = new JSONObject(params);
			String userName =jparams.optString("loginUser");
			String partNumber =jparams.optString("partNumber");
			String fileContent =jparams.optString("fileContent");
			String technicsNumber =jparams.optString("technicsNumber");
			String fileName =jparams.optString("fileName");
			if(Tools.isNull(technicsNumber)){
				technicsNumber = fileName.substring(0,fileName.indexOf(".zip"));
			}
			String processPlanNumber =jparams.optString("processPlanNumber");
			//String processPlanName =jparams.optString("processPlanName");
			String DEPT =jparams.optString("DEPT");
			String CINDEX =jparams.optString("CINDEX");
			String PINDEX =jparams.optString("PINDEX");
			String SECRET =jparams.optString("SECRET");
			String ZFFLAG =jparams.optString("ZFFLAG");
			if(Tools.isNull(SECRET)){
				SECRET = "公开";
			}
			if(Tools.isNull(ZFFLAG)){
				ZFFLAG = "Z";
			}
			String PHASE_CODE =jparams.optString("PHASE_CODE");
			WTUser user= CSCPrincipal.getUserByName(userName);
			String technicsType = "电装工艺";
			String doctype = "DIANZHUANG_PROCESSPLAN";
			String pdfFileName = "PDFPreview.pdf";
			byte[] fileBase64 = Base64.decodeBase64(fileContent);
			String tempPath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
					+  java.util.UUID.randomUUID().toString()+ File.separator;
			String pdfPath = tempPath + File.separator + pdfFileName;
			ZipUtil.unZip(fileBase64, tempPath);
			File pdfFile = new File(pdfPath);
			if(user!=null){
				wt.session.SessionHelper.manager.setPrincipal(userName);
				WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber, "Manufacturing");
				if(part!=null){
					List<WTDocument> docList  = PartCommonHelper.getLatestDescribedByWTDocuments(part,"PROCESS_PLAN");
					if(!docList.isEmpty()){
						for (WTDocument tempDoc :docList){
							String state =tempDoc.getState().getState().toString();
							if("INWORK".equals(state)||"REWORK".equals(state)){
								technicsNumber = tempDoc.getNumber();
								break;
							}else{
								if(!"OBSOLESCENCE".equals(state)) {
									rtnCode = "N";
									rtnMsg = "已存在正在审批中的工艺，无法覆盖更新。";
									break;
								}
							}

						}
					}

					if(!StringUtil.isEmpty(fileContent) &&  !StringUtil.isEmpty(processPlanNumber)&&  !StringUtil.isEmpty(technicsNumber) ){
						trans = new Transaction();
						trans.start();
						WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
						if (document != null) {
							document = (WTDocument) WorkInProcessUtil.checkout(document);
							String tempFilePath = updateProcessZip(document, fileBase64, technicsNumber);
							byte[] bytes = FileUtil.fileToBytes(new File(tempFilePath + technicsNumber + ".zip"));

							document = WTDocumentUtil.setPrimaryForDocument(document, technicsNumber + ".zip", bytes);
							document = (WTDocument) WorkInProgressHelper.service.checkin(document, "");
							//删除临时文件
							FileUtil.deleteFile(new File(tempFilePath));
						} else {
							String folderPath = "";
							folderPath = LoadConfig.getInstance().getTechnicsDocPrefixPath()+technicsType;
							String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
							document = WTDocumentUtil.createDocument(technicsNumber,technicsType+"("+processPlanNumber+")", part.getContainer(), folderPath,LoadConfig.getInstance().getLocalDomainName()+"."+doctype);
							document = WTDocumentUtil.setPrimaryForDocument(document, technicsNumber + ".zip", fileBase64);
							WTPartUtil.createWTPartDescribeLink(part, document);
						}

						//替换pdf
						document = (WTDocument) PersistenceHelper.manager.refresh(document);

						//更新iba属性
						document.setDescription("E3工艺");
						IBAHelper.setIBAStringValue(document,"DEPT",DEPT);
						IBAHelper.setIBAStringValue(document,"CINDEX",CINDEX);
						IBAHelper.setIBAStringValue(document,"PINDEX",PINDEX);
						IBAHelper.setIBAStringValue(document,"SECRET",SECRET);
						IBAHelper.setIBAStringValue(document,"PHASE_CODE",PHASE_CODE);
						IBAHelper.setIBAStringValue(document,"PPLANTYPE",jparams.optString("PPLANTYPE"));
						IBAHelper.setIBAStringValue(document,"ZFFLAG",ZFFLAG);
						IBAHelper.setIBAStringValue(document,"MINDEX",jparams.optString("MINDEX"));
						IBAHelper.setIBAStringValue(document,"PHASE_CODE",jparams.optString("PHASE_CODE"));
						IBAHelper.setIBAStringValue(document,"KEYCOMPONENT",jparams.optString("KEYCOMPONENT"));
						IBAHelper.setIBAStringValue(document,"BATCH",jparams.optString("BATCH"));

						ApplicationData appData = null;
						QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
						while(qr.hasMoreElements()){
							appData = (ApplicationData)qr.nextElement();
							String filename = appData.getFileName();
							if("PDFPreview.pdf".equals(filename)){
								PersistenceHelper.manager.delete(appData);
								PersistenceServerHelper.manager.update(document);
							}
						}
						appData = ApplicationData.newApplicationData(document);
						appData.setRole(ContentRoleType.SECONDARY);
						appData.setFileName(pdfFileName);

						inputStream = new FileInputStream(pdfFile);
						inputStream2 = new FileInputStream(pdfFile);

						//设置默认表示法
						RepUtils.saveFileRep2(document, inputStream, pdfFileName);

						appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, inputStream2,true); // 更新内容

						PersistenceServerHelper.manager.update(document);
						document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式

						trans.commit();
						trans = null;
						rtnCode = "S";
						version = document.getVersionIdentifier().getValue()+"."+document.getIterationIdentifier().getValue();


					}else{
						rtnCode = "N";
						rtnMsg = "关键参数不能为空";
					}
				}else{
					rtnCode = "N";
					rtnMsg = "没找到相应编号的Manufacturing视图的部件";
				}
			}else{
				rtnCode = "N";
				rtnMsg = "没找到相应的用户";
			}


		} catch (JSONException e) {
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		}  catch (Exception e) {
			rtnCode = "N";
			rtnMsg = e.getLocalizedMessage();
			e.printStackTrace();
		} finally {
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inputStream2 != null){
				try {
					inputStream2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(trans != null){
				trans.rollback();
			}
		}
		try {
	           jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
	           jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
			   jrtnObj.put("version", version);
	     } catch (JSONException ex) {
	            LOGGER.error("Error building JSON: ", ex);
	     }

		return jrtnObj.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}

	private static String updateProcessZip(WTDocument wtDocument, byte[] bytes, String processZipDocName)
			throws Exception {
		String subpath = java.util.UUID.randomUUID().toString();
		String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
				+ subpath + File.separator;
		FileUtil.writeBytes(tempFilePath, processZipDocName + ".zip", bytes);
		ApacheZipUtil.decompress(tempFilePath + processZipDocName + ".zip", tempFilePath + processZipDocName);
		File xmlFile = new File(tempFilePath + processZipDocName + File.separator + processZipDocName + ".xml");
		InputStream inputStream = new FileInputStream(xmlFile);

		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element element = xmlUtil.getRootElement().getChild(XMLConstants.QMFawTechnicsInfo);
		if(element == null ){
			//报表类工艺文件
			element = xmlUtil.getRootElement().getChild(XMLConstants.XWReportTechnicsInfo);
		}
		element.setAttribute("version", wtDocument.getVersionIdentifier().getValue() + "."
				+ VersionControlHelper.nextIterationId(wtDocument).getValue());
		element.setAttribute("lifecycle", wtDocument.getState().getState().getDisplay(Locale.CHINA));

		// 替换工艺xml，打包工艺文件夹，上传工艺压缩包
		FileOutputStream fileOutputStream = new FileOutputStream(new File(tempFilePath + processZipDocName
				+ File.separator + processZipDocName + ".xml"), false);
		Format format = Format.getPrettyFormat();
		format.setEncoding("GBK");
		XMLOutputter xmlOutput = new XMLOutputter(format);
		xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);
		ApacheZipUtil.compress(tempFilePath + processZipDocName, tempFilePath + processZipDocName + ".zip");

		xmlOutput.clone();
		fileOutputStream.close();
		inputStream.close();

		return tempFilePath;
	}

}
