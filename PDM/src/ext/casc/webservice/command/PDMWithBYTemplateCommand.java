/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;

import com.glaway.mpm.util.*;
import ext.casc.util.IBAHelper;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.content.*;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

/**
 * 类功能：接收白羽模板文件接口
 *
 * @author chenjianhui
 * @date 2021/11/07
 */

public class PDMWithBYTemplateCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(PDMWithBYTemplateCommand.class.getName());
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);
	// 方法标识
	public static final String METHOD_NAME = "baiyuTemplate";
	private static String PARA_QBYCONTENT = "qbyContent";
	private static String PARA_MOREJSONCONTENT = "moreJsonContent";
	private static String PARA_ONEJSONCONTENT = "oneJsonContent";
	private static String PARA_QBYFILENAME = "qbyFileName";
	private static String PARA_MOREJSONFILENAME = "moreJsonFileName";
	private static String PARA_ONEJSONFILENAME = "oneJsonFileName";
	private static String PARA_TABLENAME = "tableName";
	private static String BY_TYPE = "type";
	private static String TEMPLATE_OID = "templateOid";
	@Override
	public String execute(String params) {
		System.out.println("baiyuTemplate Start============");
		String errorMsg = null;

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
			String tableName = jparams.optString(PARA_TABLENAME);
			String type = jparams.optString(BY_TYPE);
			String templateOid = jparams.optString(TEMPLATE_OID);

			byte[] qbyBase64 = Base64.decodeBase64(qbyContent);
			byte[] mJsonBase64 = Base64.decodeBase64(mJsonContent);
			byte[] oJsonBase64 = Base64.decodeBase64(oJsonContent);

			if("templateCreate".equals(type)){
				WTDocument doc = getTemplateDocByIBA(tableName);
				if(doc==null){
					System.out.println("baiyuTemplate createDoc start!!!");
					String status = createTemplateDoc(qbyBase64,mJsonBase64,oJsonBase64,qbyFileName, mJsonFileName, oJsonFileName, tableName);
					if(status.equals("N")){
						errorMsg = errorMsg + " 同步失败";
					}
				}else{
					try {
						boolean b = deleteAttach(doc);
						if(b){
							System.out.println("baiyuTemplate updateDoc start!!!");
							String status = updateDoc(doc, qbyBase64,mJsonBase64,oJsonBase64,qbyFileName, mJsonFileName, oJsonFileName
									, tableName);
							if(status.equals("N")){
								errorMsg = errorMsg + " 同步失败";
							}
						}
					} catch (WTException e) {
						errorMsg = errorMsg + " 同步失败";
						e.printStackTrace();
					}
				}
			}else if("templateUpdate".equals(type)){
				try {
					WTDocument doc = WTDocumentUtil.getWTDocumentByOid(templateOid);
					if(doc != null){
						try {
							boolean b = deleteAttach(doc);
							if(b){
								System.out.println("baiyuTemplate updateDoc start!!!");
								String status = updateDoc(doc, qbyBase64,mJsonBase64,oJsonBase64,qbyFileName, mJsonFileName, oJsonFileName
										, tableName);
								if(status.equals("N")){
									errorMsg = errorMsg + " 同步失败";
								}
							}
						} catch (WTException e) {
							errorMsg = errorMsg + " 同步失败";
							e.printStackTrace();
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		JSONObject rtnMsgObj = new JSONObject();
		try {
			if(errorMsg!=null&&!"".equals(errorMsg)){
				rtnMsgObj.put("status", "N");
				rtnMsgObj.put("result", errorMsg);
			}else{
				rtnMsgObj.put("status", "Y");
				rtnMsgObj.put("result", errorMsg);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("baiyuTemplate End============");
		return rtnMsgObj.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}

	public static WTDocument getTemplateDocByIBA(String tableName){
		try {
			int[] index = { 0 };
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.EQUAL, tableName);
			qs.appendWhere(sc, new int[] { 0 });
			qs.appendAnd();
			TypeUtil.getTypeQuery(WTDocument.class, "casc.sast.149.BaiYuDocument", qs);
			qs.setAdvancedQueryEnabled(true);
			String folderName = propertiesUtil.getProperty("baiyuTemplate-document-save-folder");
			String containerName = propertiesUtil.getProperty("baiyu-document-save-container");
			WTContainer container = WTContainerUtil.getContainerByName(containerName);
			Folder folder = FolderUtil.getFolder(folderName, WTContainerRef.newWTContainerRef(container));
			if(folder != null){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL,PersistenceHelper.getObjectIdentifier(folder).getId()), index);
			}
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				if(doc!=null){
					return doc;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

	public static String createTemplateDoc(byte[] qbyContent,byte[] mJsonContent,byte[] oJsonContent
			,String qbyFileName,String mJsonFileName,String oJsonFileName,String tableName){
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			String folderName = propertiesUtil.getProperty("baiyuTemplate-document-save-folder");
			String containerName = propertiesUtil.getProperty("baiyu-document-save-container");
			WTContainer container = WTContainerUtil.getContainerByName(containerName);
			String folderPath = folderName;
			Folder folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
			String documentType = "casc.sast.149.BaiYuDocument";
			if (folder != null) {
				WTDocument document = createDocument(tableName, container, folderPath, documentType,"APPROVED");
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
				IBAHelper.setIBAStringValue(document,"templateState","启用");
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

	public static String updateDoc(WTDocument document,byte[] qbyContent,byte[] mJsonContent,byte[] oJsonContent
			,String qbyFileName,String mJsonFileName,String oJsonFileName,String tableName){
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
			IBAHelper.setIBAStringValue(document,"templateState","启用");
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

	private static  void changeName(WTDocument doc, String name) throws WTException, WTPropertyVetoException {
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
