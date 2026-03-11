/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.util.*;
import ext.casc.integrate.mes.ProcessInfoService;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.content.*;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;

/**
 * 类功能：接收白羽数据文件接口
 *
 * @author chenjianhui
 * @date 2021/04/07
 */

public class PDMWithBYDataCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(PDMWithBYDataCommand.class.getName());
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);
	// 方法标识
	public static final String METHOD_NAME = "baiyu";
	private static String PARA_QBYCONTENT = "qbyContent";
	private static String PARA_MOREJSONCONTENT = "moreJsonContent";
	private static String PARA_ONEJSONCONTENT = "oneJsonContent";
	private static String PARA_QBYFILENAME = "qbyFileName";
	private static String PARA_MOREJSONFILENAME = "moreJsonFileName";
	private static String PARA_ONEJSONFILENAME = "oneJsonFileName";
	private static String PARA_TABLEID = "tableId";
	private static String PARA_TABLENAME = "tableName";
	private static String PARA_TECNUMBER = "technicsNumber";
	private static String PARA_STEPNUM = "stepNum";
	private static String PARA_PACENUM = "paceNum";
	private static String PARA_TABLETYPE = "tableType";
	@Override
	public String execute(String params) {
		String errorMsg = null;
		String docNumber = null;

		JSONObject jparams = null;
		try {
			jparams = new JSONObject(params);
		} catch (JSONException e) {
			errorMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error("", e);
		}
		if(errorMsg==null||"".equals(errorMsg)){
			String qbyContent = jparams.optString(PARA_QBYCONTENT);
			String mJsonContent = jparams.optString(PARA_MOREJSONCONTENT);
			String oJsonContent = jparams.optString(PARA_ONEJSONCONTENT);
			String qbyFileName = jparams.optString(PARA_QBYFILENAME);
			String mJsonFileName = jparams.optString(PARA_MOREJSONFILENAME);
			String oJsonFileName = jparams.optString(PARA_ONEJSONFILENAME);
			String tableId = jparams.optString(PARA_TABLEID);
			String tableName = jparams.optString(PARA_TABLENAME);
			String technicsNumber = jparams.optString(PARA_TECNUMBER);
			String stepNum = jparams.optString(PARA_STEPNUM);
			String paceNum = jparams.optString(PARA_PACENUM);
			String tableType = jparams.optString(PARA_TABLETYPE);
			String matchState = jparams.optString(" matchState");

			System.out.println("baiyu tableId="+tableId+"---tableName="+tableName+"---technicsNumber="+technicsNumber+"---stepNum="+stepNum+"---paceNum="+paceNum+"---matchState="+matchState);

			byte[] qbyBase64 = Base64.decodeBase64(qbyContent);
			byte[] mJsonBase64 = Base64.decodeBase64(mJsonContent);
			byte[] oJsonBase64 = Base64.decodeBase64(oJsonContent);
			try {
				if(tableId != null && !"".equals(tableId) && WTDocumentUtil.getDocumentByNumber(tableId) != null){
					WTDocument doc = WTDocumentUtil.getDocumentByNumber(tableId);
					docNumber = tableId;
					boolean b = deleteAttach(doc);
					if(b){
						String status = updateDoc(doc, qbyBase64,mJsonBase64,oJsonBase64,qbyFileName, mJsonFileName, oJsonFileName
								, tableName, technicsNumber, stepNum, paceNum,tableType,matchState);
						if(status.equals("N")){
							errorMsg = errorMsg + " 同步失败";
						}
					}
				}else{
					docNumber = createDoc(qbyBase64,mJsonBase64,oJsonBase64,qbyFileName, mJsonFileName, oJsonFileName, tableName, technicsNumber, stepNum, paceNum, tableType,matchState);
					if(Tools.isNull(docNumber)){
						errorMsg = errorMsg + " 同步失败";
					}
				}
			} catch(WTException e) {
				throw new RuntimeException(e);
			} catch(RemoteException e) {
				throw new RuntimeException(e);
			}
		}
		JSONObject rtnMsgObj = new JSONObject();
		try {
			if(errorMsg!=null&&!"".equals(errorMsg)){
				rtnMsgObj.put("status", "N");
				rtnMsgObj.put("result", errorMsg);
			}else{
				rtnMsgObj.put("status", "Y");
				rtnMsgObj.put("result", docNumber);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("baiyu End============");
		return rtnMsgObj.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}

	public static boolean deleteAttach(WTDocument document) throws WTException {
		Transaction trans = new Transaction();
		trans.start();
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
		while(qr.hasMoreElements()){
			appData = (ApplicationData)qr.nextElement();
			PersistenceHelper.manager.delete(appData);
			PersistenceServerHelper.manager.update(document);
		}
		trans.commit();
		return true;
	}

	public static WTDocument uploadAttach(WTDocument document, String fileName, InputStream inputStream)
			throws WTException, PropertyVetoException, IOException {
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		ApplicationData appData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
		while(qr.hasMoreElements()){
			appData = (ApplicationData)qr.nextElement();
			if(fileName.equals(appData.getFileName())){
				PersistenceHelper.manager.delete(appData);
				PersistenceServerHelper.manager.update(document);
			}
		}
		appData = ApplicationData.newApplicationData(document);
		// ContentRoleType.SECONDARY 表示附件
		appData.setRole(ContentRoleType.SECONDARY);
		// 设置文件名称
		appData.setFileName(fileName);
		appData = ContentServerHelper.service.updateContent(document, appData, inputStream,true); // 更新内容
		PersistenceServerHelper.manager.update(document);
		document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
		if (null != inputStream) {
			inputStream.close();
		}
		return document;
	}

	public static String createDoc(byte[] qbyContent, byte[] mJsonContent, byte[] oJsonContent
			, String qbyFileName, String mJsonFileName, String oJsonFileName, String tableName
			, String technicsNumber, String stepNum, String paceNum, String tableType, String matchState){
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			String folderName = propertiesUtil.getProperty("baiyu-document-save-folder");
			String containerName = propertiesUtil.getProperty("baiyu-document-save-container");
			WTContainer container = WTContainerUtil.getContainerByName(containerName);
			String folderPath = folderName;
			Folder folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
			String documentType = "casc.sast.149.BaiYuDocument";
			WTDocument document = null;
			if (folder != null) {
				document = createDocument(tableName, container, folderPath, documentType,"APPROVED");
				if(qbyContent!=null){
					InputStream is = new ByteArrayInputStream(qbyContent);
					uploadAttach(document,qbyFileName,is);
				}
				if(mJsonContent!=null){
					InputStream is = new ByteArrayInputStream(mJsonContent);
					uploadAttach(document,mJsonFileName,is);
				}
				if(oJsonContent!=null){
					InputStream is = new ByteArrayInputStream(oJsonContent);
					uploadAttach(document,oJsonFileName,is);
				}
				PersistenceHelper.manager.refresh(document);
				IBAHelper.setIBAStringValue(document,"qbyName",qbyFileName);
				IBAHelper.setIBAStringValue(document,"moreJsonName",mJsonFileName);
				IBAHelper.setIBAStringValue(document,"oneJsonName",oJsonFileName);
				IBAHelper.setIBAStringValue(document,"PPNUMBER",technicsNumber);
				IBAHelper.setIBAStringValue(document,"stepNum",stepNum);
				IBAHelper.setIBAStringValue(document,"paceNum", paceNum);
				if(!Tools.isTrimNull(matchState)){
					IBAHelper.setIBAStringValue(document,"matchState",matchState);
				}



				//用户自建白羽表单的表格类型属性为“专用表”
				if(Tools.isTrimNull(stepNum) && Tools.isTrimNull(paceNum)){
					if(!Tools.isTrimNull(matchState)){
						if("部分匹配".equals(matchState)||"全部匹配".equals(matchState)){
							IBAHelper.setIBAStringValue(document, "tableType", "全局表");
						}
					} else{
						boolean isQuanJuTable = isQuanJuTable(mJsonContent);
						if(isQuanJuTable){
							IBAHelper.setIBAStringValue(document,"tableType","全局表");
						}else{
							IBAHelper.setIBAStringValue(document,"tableType","周期表");
						}

					}
				}else{
					IBAHelper.setIBAStringValue(document,"tableType","专用表");
				}

				WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
				if(doc != null){
					WTUser user = (WTUser) doc.getModifier().getObject();
					String group = ProcessEditorToWCIntfRMI.getGroupNameByUser(user);
					IBAHelper.setIBAStringValue(document,"DEPT",group);
				}
			}
			tx.commit();
			tx = null;
			if(document != null){
				return document.getNumber();
			}else {
				return null;
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}finally {
			if(tx!=null){
				tx.rollback();
			}
		}
	}

	public  static boolean isQuanJuTable(byte[] oJsonContent) {
		boolean isQuanJuTable = false;
		try {
			String jsonString = new String(oJsonContent, "UTF-8");

			JSONObject tableJson =  new JSONObject(jsonString);
			if(tableJson != null) {
				JSONArray data = tableJson.getJSONArray("data2");
				if (data != null) {
					for(int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = data.getJSONObject(i);
						Iterator<String> keys = jsonObject.keys();
						while(keys.hasNext()) {
							String key = keys.next();
							System.out.println(key+"=" +jsonObject.optString(key));
							if(key.endsWith(ProcessInfoService.KEY9_END)||key.endsWith(ProcessInfoService.KEY10_END)||key.endsWith(ProcessInfoService.KEY11_END)||
									key.endsWith(ProcessInfoService.KEY12_END)||key.endsWith(ProcessInfoService.KEY13_END)||key.endsWith(ProcessInfoService.KEY14_END)) {
								if(!Tools.isNull(jsonObject.optString(key))){
									return true;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isQuanJuTable;
	}

	public  String updateDoc(WTDocument document, byte[] qbyContent, byte[] mJsonContent, byte[] oJsonContent
			, String qbyFileName, String mJsonFileName, String oJsonFileName, String tableName
			, String technicsNumber, String stepNum, String paceNum, String  tableType, String matchState){
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			if(qbyContent!=null){
				InputStream is = new ByteArrayInputStream(qbyContent);
				uploadAttach(document,qbyFileName,is);
			}
			if(mJsonContent!=null){
				InputStream is = new ByteArrayInputStream(mJsonContent);
				uploadAttach(document,mJsonFileName,is);
			}
			if(oJsonContent!=null){
				InputStream is = new ByteArrayInputStream(oJsonContent);
				uploadAttach(document,oJsonFileName,is);
			}
			if(!tableName.equals(document.getName())){
				changeName(document,tableName);
			}

			PersistenceHelper.manager.refresh(document);
			IBAHelper.setIBAStringValue(document,"qbyName",qbyFileName);
			IBAHelper.setIBAStringValue(document,"moreJsonName",mJsonFileName);
			IBAHelper.setIBAStringValue(document,"oneJsonName",oJsonFileName);
			IBAHelper.setIBAStringValue(document,"PPNUMBER",technicsNumber);
			IBAHelper.setIBAStringValue(document,"stepNum",stepNum);
			IBAHelper.setIBAStringValue(document,"paceNum",paceNum);
			if(!Tools.isTrimNull(matchState)){
				IBAHelper.setIBAStringValue(document,"matchState",matchState);
			}
			if(Tools.isTrimNull(stepNum) && Tools.isTrimNull(paceNum)){//工艺端白羽表
				if(!Tools.isTrimNull(matchState)){
					if("部分匹配".equals(matchState)||"全部匹配".equals(matchState)){
						IBAHelper.setIBAStringValue(document, "tableType", "全局表");
					}
				} else {
					boolean isQuanJuTable = isQuanJuTable(mJsonContent);
					if (isQuanJuTable) {
						IBAHelper.setIBAStringValue(document, "tableType", "全局表");
					}
				}
			}

			tx.commit();
			tx = null;
			return "Y";
		}catch (Exception e){
			e.printStackTrace();
			return "N";
		}finally {
			if(tx!=null){
				tx.rollback();
			}
		}
	}
	private void changeName(WTDocument doc, String name) throws WTException, WTPropertyVetoException {
		WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
		WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
		idy.setName(name);
		master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
	}

	public static WTDocument createDocument(String name, WTContainer container, String folderPath, String type,String stateStr)
			throws WTPropertyVetoException, WTException, RemoteException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		Transaction transaction = new Transaction();
		transaction.start();
		WTDocument document = WTDocument.newWTDocument();
		document.setName(name);
		document.setContainer(container);
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
		FolderHelper.assignFolder(document, folder);
		TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(type);
		document.setTypeDefinitionReference(typeRef);
		LifeCycleState state = LifeCycleState.newLifeCycleState();
		state.setState(State.toState(stateStr));
		document.setState(state);
		document = (WTDocument) PersistenceHelper.manager.save(document);
		transaction.commit();
		SessionServerHelper.manager.setAccessEnforced(flag);
		return document;
	}

	public static void setSecret(WTDocument document) throws WTPropertyVetoException, WTException, RemoteException {
		com.glaway.mpm.util.IBAHelper helper = new com.glaway.mpm.util.IBAHelper(document);
		helper.setIBAValue("SECRET", "公开");
		helper.updateAttributeContainer(document);
	}
}
