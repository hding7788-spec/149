package ext.sast.center.synch;

import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import com.ptc.extend.ixb.*;
import com.ptc.extend.ixb.center.MQExpImpLink;
import com.ptc.extend.ixb.center.MQExpImpPersistable;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.ixb.CmExpImpHelper;
import ext.casc.ixb.CmImportHandler;
import ext.casc.ixb.ExpImpLogger;
import ext.casc.util.Tools;
import org.json.JSONObject;
import wt.admin.AdministrativeDomainHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTValuedHashMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionAuthenticator;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * 专业所导入单元测试
 * @author wyq
 *
 */
public class MQDataImportHandler implements RemoteAccess{

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		try {
			String sendFrom = args[1];

			Class[] types = {String.class,String.class};
			Object[] vls  = {args[0],sendFrom};
			rms.invoke("processMQReceivedData", MQDataImportHandler.class.getName(), null, types, vls);

			System.out.println("*******导入结束 *******");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用于专业所导入数据
	 * @param filePath
	 * @param sendFrom
	 */
	@SuppressWarnings("rawtypes")
	public static void processMQReceivedData(String filePath,String sendFrom) {
		CmImportHandler impHnd = null;
		JSONObject msg = new JSONObject();
		try {
			File file = new File(filePath);
			impHnd = new CmImportHandler(file);

			ExpImpLogger logger = ExpImpLogger.getInstance();
			ChangePackaged change = null;
			ProcessEnvelope pe = null;

			System.out.println("******开始进入到接口processMQReceivedData111*****");
			System.out.println("******数据包名称*****"+file.getName());
			ArrayList list = impHnd.getAllTopObjectXmlFileInJar();
			Iterator it = list.iterator();
			while(it.hasNext()) {
				String fname = (String) it.next();
				CmExpImpObject expimp = MQExpImpPersistable.newMQExpImpPersistable(impHnd, fname);
				if (expimp == null)
					continue;
				expimp.setSendFrom(sendFrom);
				String typename = fname.substring(fname.indexOf("TAG-")+4);
				if(typename.indexOf("-")>0) {
					typename = typename.substring(0,typename.indexOf("-"));
				}else {
					typename = typename.substring(0,typename.indexOf(".xml"));
				}
				logger.log("Start to Import Object Type " + typename);
				try {
					Object object = expimp.importObject();
					if(null == object) {
						//ErrorImportObject
					}else if(object instanceof ChangePackaged) {
						change = (ChangePackaged) object;
						System.out.println("导入对象 @@@@@@@@@@ = "+change.getNumber());
//						WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
//								.getProcessDefinition("149签审包工艺会签流程");
//						WfProcess wfprocess = null;
//						wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
//								pe.getContainerReference());
//						wfprocess.setName("149签审包工艺会签流程_" + pe.getNumber());
//						ProcessData processdata = wfprocess.getContext();
//						processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
//						processdata.setValue("sendFrom", sendFrom);
//						processdata.setValue("mqmessage", msg.toString());
//						WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
					}else if(object instanceof ProcessEnvelope){
						pe = (ProcessEnvelope) object;
						System.out.println("导入对象 @@@@@@@@@@ = "+pe.getNumber());
//						WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
//								.getProcessDefinition("149变更签审包工艺会签流程");
//						WfProcess wfprocess = null;
//						wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
//								pe.getContainerReference());
//						wfprocess.setName("149变更签审包工艺会签流程_" + pe.getNumber());
//						ProcessData processdata = wfprocess.getContext();
//						processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
//						processdata.setValue("sendFrom", sendFrom);
//						processdata.setValue("mqmessage", msg.toString());

					}
				} catch (Exception e) {
					logger.log(e);
					throw e;
				}
			}
			//导入link关系
			System.out.println("@@@@@@导入对象link关系@@@@@@");
			improtLink(impHnd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("rawtypes")
	private static void improtLink(CmImportHandler impHnd) throws WTException{
		ArrayList wtal = impHnd.getAllObjects();
		Iterator ite = wtal.iterator();
		while (ite.hasNext()) {
			Object obj = ite.next();
			if(obj instanceof ErrorImportObject){
				continue;
			}
			Persistable p = (Persistable) obj;
			String remoteId = (String)impHnd.getImportedObjectRemoteId(obj);
			String dir = CmExpImpHelper.getObjectLocalIdSavePathInJar(remoteId);
			ArrayList list = impHnd.getXmlDocumentsUnderLikelyDirInJar(dir);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				String fname = (String) it.next();
				CmExpImpLink expimp = MQExpImpLink.newCmExpImpLink(p, impHnd,
						fname);
				if (expimp == null)
					continue;
				try {
					expimp.importObject();
				} catch (MissingObjectException moe) {
					impHnd.removeFromProperlyReceivedObjectSet((Persistable)p);
				} catch (WTException wte) {
					wte.printStackTrace();
					impHnd.removeFromProperlyReceivedObjectSet((Persistable)p);
				}
			}
		}
	}

	public static void importProcessEnvelope(String docNumber,String sendFromName,String version,JSONObject msg,String type){
		MethodContext mc = null;
		try {
			mc = MethodContext.getContext(Thread.currentThread());
			if (mc == null) {
				System.out.println("*********mc is null,new MethodContext*********");
				mc = new MethodContext(null, null);
			}
			if (mc.getAuthentication() == null) {
				System.out.println("*********mc.getAuthentication() is null,new SessionAuthenticator*********");
				SessionAuthenticator sa = new SessionAuthenticator();
				mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
			}
			System.out.println("测试单文档会签：importProcessEnvelope  "+docNumber);
			String peNumber = "";
			if("signature".equals(type)){
				peNumber = docNumber+ "_SSD_"+sendFromName;
			}else{
				peNumber = docNumber+ "_FFD_"+sendFromName;
			}
			try {
				String msgType = msg.optString(Based.MSG_TYPE);
				ProcessEnvelope	pe = createNewObjectAndLink(docNumber,peNumber,version,type,msg);
				if(pe != null){
					String sendFrom =msg.optString("sendFrom");
					JSONObject j_src_site = msg.optJSONObject(Based.J_SRC_SITE);
					if(j_src_site!=null){
						String newSendFrom =j_src_site.optString("id");
						if(Tools.isNull(sendFrom)){
							sendFrom = newSendFrom;
						}
					}


					JSONObject paramMsg = MQDataReceiveHelper.lightMsg(msg);
					if (pe !=null) {
						WfEngineHelper.service.terminateObjectsRunningWorkflows(pe);

						if(Based.DC_REQUEST_DISTRIBUTE_RECEIVER.equals(msgType)){
							WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
									.getProcessDefinition("149正式发放包流程");
							WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
									pe.getContainerReference());
							wfprocess.setName("149正式发放包流程_" + pe.getNumber());
							ProcessData processdata = wfprocess.getContext();
							processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
							processdata.setValue("sendFrom", sendFrom);
							processdata.setValue("mqmessage",  paramMsg.toString());

							WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
						}else{
							WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
									.getProcessDefinition("149签审包工艺会签流程");
							WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
									pe.getContainerReference());
							wfprocess.setName("149签审包工艺会签流程_" + pe.getNumber());
							ProcessData processdata = wfprocess.getContext();
							processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
							processdata.setValue("sendFrom", sendFrom);
							processdata.setValue("mqmessage", paramMsg.toString());
							WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
						}


					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			}
		}catch (Throwable t) {
			t.printStackTrace();
			System.err.println("Error create service session context.");
		}finally{
			if(mc!=null){
				mc.unregister();
			}
		}

	}
	public static WTDocument getLatestDocumentByNumber(String docNumber) throws WTException {
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, docNumber),
				new int[] { 0 });
		qs.setAdvancedQueryEnabled(true);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		LatestConfigSpec lc = new LatestConfigSpec();
		qr = lc.process(qr);
		if (qr.hasMoreElements()) {
			return (WTDocument) qr.nextElement();
		} else {
			return null;
		}
	}
	private static ProcessEnvelope createNewObjectAndLink(String number,String peNumber,String version,String type,JSONObject msg){
		ProcessEnvelope pe = null;
		Transaction tx = null;
		try {
			String[] versionArray = null;
			WTDocument doc = null;
			if(version.contains(".")){
				versionArray = version.split("\\.");
				String versionInfo =versionArray[0];
				versionInfo = MQExpImpUtil.attrConvertValue("versionInfo", versionInfo, "IMP");
				doc = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTDocument.class, number,versionInfo , versionArray[1]);
			}
			if(doc == null){
				doc = getLatestDocumentByNumber(number);
			}
			if(doc == null){
				Sender sender = Sender.getInstance();
				LogMessage logMessage = new LogMessage(msg.getString(Based.MSG_ID),MQConstants.SITENAME_149, MQConstants.SITEIID_149,"创建签审包时出错，未获取到文档对象【"+number+"】");
				sender.addLog(logMessage);

				MetaMessage metaMessage = new MetaMessage();
				metaMessage.setMsgId(msg.getString(Based.MSG_ID));
				metaMessage.setMsgStatus(Based.MSG_STATUS_FAILED);
				sender.updateMetaMessage(metaMessage);

				return null;
			}
			pe = CmExpImpSearchHelper.getProcessEnvelopeByNumber(peNumber);

			tx = new Transaction();
			tx.start();

			if (pe == null) {
				WTContainerRef wtcontainerref = doc.getContainerReference();

				pe = ProcessEnvelope.newProcessEnvelope();
				pe.setNumber(peNumber);
				if("signature".equals(type)){
					pe.setName(doc.getName()+"_送审单");
				}else{
					pe.setName(doc.getName()+"_发放单");
				}
				pe.setContainer(doc.getContainer());
				pe.setContainerReference(wtcontainerref);
				pe.setDescription("单文档接收时由系统自动创建");


				Folder location = doc.getFolderingInfo().getFolder();

				if(location==null){
					location = FolderHelper.service.getFolder("/Default",
							wtcontainerref);
				}

				if (location != null) {
					WTValuedHashMap map = new WTValuedHashMap();
					map.put(pe, location);
					FolderHelper.assignLocations(map);
				}
				pe = (ProcessEnvelope) PersistenceHelper.manager.save(pe);
				EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(pe, doc);
				PersistenceHelper.manager.save(envelopememberlink);

			}else{
				// 先断掉所有的link关系
				QueryResult qr = PersistenceHelper.manager.navigate(pe,
						"theRevisionControlled",
						EnvelopeMemberLink.class, false);
				if (qr.size() > 0) {
					while (qr.hasMoreElements()) {
						EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
						PersistenceHelper.manager.delete(link);
					}
				}
				EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(pe, doc);
				PersistenceHelper.manager.save(envelopememberlink);
			}
			tx.commit();
			tx = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tx != null) {
				tx.rollback();
			}
		}
		return pe;
	}

	public static ProcessEnvelope createNewObjectAndLinkTest(String number,String peNumber,String version,String type){
		ProcessEnvelope pe = null;
		Transaction tx = null;
		try {
			String[] versionArray = null;
			WTDocument doc = null;
			if(version.contains(".")){
				versionArray = version.split("\\.");
				String versionInfo =versionArray[0];
				versionInfo = MQExpImpUtil.attrConvertValue("versionInfo", versionInfo, "IMP");
				doc = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTDocument.class, number,versionInfo , versionArray[1]);
			}
			if(doc == null){
				doc = getLatestDocumentByNumber(number);
			}

			pe = CmExpImpSearchHelper.getProcessEnvelopeByNumber(peNumber);

			tx = new Transaction();
			tx.start();

			if (pe == null) {
				WTContainerRef wtcontainerref = doc.getContainerReference();

				pe = ProcessEnvelope.newProcessEnvelope();
				pe.setNumber(peNumber);
				if("signature".equals(type)){
					pe.setName(doc.getName()+"_送审单");
				}else{
					pe.setName(doc.getName()+"_发放单");
				}
				pe.setContainer(doc.getContainer());
				pe.setContainerReference(wtcontainerref);
				pe.setDescription("单文档接收时由系统自动创建");

				Folder location = doc.getFolderingInfo().getFolder();
				if(location==null){
					location = FolderHelper.service.getFolder("/Default",
							wtcontainerref);
				}

				if (location != null) {
					WTValuedHashMap map = new WTValuedHashMap();
					map.put(pe, location);
					FolderHelper.assignLocations(map);
				}
				pe = (ProcessEnvelope) PersistenceHelper.manager.save(pe);

			
				EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(pe, doc);
				PersistenceHelper.manager.save(envelopememberlink);

			}else{
				// 先断掉所有的link关系
				QueryResult qr = PersistenceHelper.manager.navigate(pe,
						"theRevisionControlled",
						EnvelopeMemberLink.class, false);
				if (qr.size() > 0) {
					while (qr.hasMoreElements()) {
						EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
						PersistenceHelper.manager.delete(link);
					}
				}
				EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(pe, doc);
				PersistenceHelper.manager.save(envelopememberlink);
			}
			tx.commit();
			tx = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tx != null) {
				tx.rollback();
			}
		}
		return pe;
	}
}
