/**
 *
 */
package ext.sast.center.synch;

import com.bjsasc.avidm.mq.fileserver.FSUtil;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.sender.Sender;
import com.bjsasc.avidm.mq.util.StackTraceUtil;
import com.ptc.extend.ixb.*;
import com.ptc.extend.ixb.center.MQExpImpLink;
import com.ptc.extend.ixb.center.MQExpImpPersistable;
import com.ptc.netmarkets.model.NmOid;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.part.ASESHuiqianSignature;
import ext.ases.part.SignLink;
import ext.casc.constants.Constants;
import ext.casc.ixb.CmExpImpHelper;
import ext.casc.ixb.CmImportHandler;
import ext.casc.ixb.DataImportHandler;
import ext.casc.ixb.ExpImpLogger;
import ext.casc.securitymgr.SecurityLabelDataHelper;
import ext.casc.synch.CustomCall;
import ext.casc.util.Tools;
import ext.casc.util.WTUtil;
import ext.casc.workflow.CmWorkflowHelper;
import ext.sast.center.ixb.util.Deserialize;
import ext.sast.center.util.JsonConvertUtil;
import ext.sast.center.util.ProductConvertUtil;
import ext.sast.center.util.PropertiesUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.admin.AdministrativeDomainHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionAuthenticator;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author cfire
 *
 */
@SuppressWarnings("rawtypes")
public class MQDataReceiveHelper implements RemoteAccess{
	/**
	 * 上载中心域数据包
	 *
	 * @return 数据包本地路径
	 */
	public static JSONObject upload(String file_path) {
		String url = MQConstants.DC_UPLOAD;
		JSONObject json = FSUtil.upload(file_path, url);
		return json;
	}
	/**
	 * 下载中心域数据包
	 *
	 * @return 数据包本地路径
	 */
	public static String downloadData(JSONObject j_file) {
		//调用中心域rest接口下载数据包
		String file_name = j_file.getString(Based.FILE_NAME);
		String downloadPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator +"DataCenterReceive";
		File downloadFilePath = new File(downloadPath);
		if(!downloadFilePath.exists()){
			downloadFilePath.mkdir();
		}
		String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator +"DataCenterReceive"+ File.separator + file_name;
		String url = MQConstants.DC_DOWNLOAD;
		String file_id = j_file.getString(Based.FILE_ID);
		String key = j_file.getString(Based.KEY);
		String ivkey = j_file.getString(Based.IVKEY);
		//localFile 保存到本地的文件，如：d:/download.zip
		//endpoint	下载文件的REST地址，如：http://10.112.1.213:8081/avidm/rest/dc/attach/download
		//file_id	文件的唯一标识
		//key		AES加密的key
		//ivkey		AES解密的ivkey
		FSUtil.download(localPath, url, file_id, key, ivkey);
		return localPath;
	}
	/**
	 * 导入数据
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	public static String receiveData(JSONObject msg) throws IOException {
		//Sender sender = Sender.getInstance();
       // LogMessage logMsg = null;
	//	String id = msg.getString(Based.MSG_ID);
		System.out.println("***********开始处理接收信息***********");
		String context = "Unknown";
		MethodContext mc = null;
		String result = "";

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
            context = mc.getId().toString();

    		JSONObject j_src_site = msg.getJSONObject(Based.J_SRC_SITE);

    		String sendFrom =j_src_site.getString("id");

    		try{
    			String newSendFrom = msg.getString("sendFrom");
    			if(!Tools.isNull(newSendFrom)){
    				sendFrom = newSendFrom;
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}


    		//如果是805、八部发来，则按照老的稳定的数据包结构解析
    		if(sendFrom!=null&&MQExpImpUtil.isCmPackage(sendFrom)&&msg.has("j_soapparams")){
    			String soapparamsStr = "";
    			JSONArray j_soapparams = msg.getJSONArray("j_soapparams");
    			for(int i=0;i<j_soapparams.length();i++) {
    				JSONObject soapparam = j_soapparams.getJSONObject(i);
    				String siteiid = soapparam.getString(Based.SITE_IID);
    				if(MQConstants.SITEIID_149.equals(siteiid)) {
    					soapparamsStr =  soapparam.getString("soapparams");
    					break;
    				}
    			}
    			System.out.println("soapparamsStr @@@@ = "+soapparamsStr);
    			HashMap inputparams = (HashMap) Deserialize.deserializeMap(soapparamsStr);

    			String pre_sendFrom = (String) inputparams.get("sendFrom");
    			if(Tools.isNull(pre_sendFrom)){
    				pre_sendFrom = sendFrom;
    			}

    			String wfProcessOid = (String) inputparams.get("wfProcessOid");
    			String activityTemplateID = (String) inputparams.get("activityTemplateID");
    			String activityName = (String) inputparams.get("activityName");
    			String activityOid = (String) inputparams.get("activityOid");
    			String reviewType = (String) inputparams.get("reviewType");
    			String workflowType =(String) inputparams.get("workflowType");
    			String orderIID = (String) inputparams.get("orderIID");
    			String previewUser = (String) inputparams.get("previewUser");
    			String pbonumber = (String) inputparams.get("pbonumber");
    			String dataImportStateID = (String) inputparams.get("ReceiveStateID");

    			System.out.println("reviewType @@@ = "+reviewType);
    			if (reviewType.equals("approvedCompleted")) {
    				try {
    					ext.casc.workflow.CmWorkflowHelper.completeActivityBy805(activityOid);
    				} catch (wt.util.WTException e) {
    					result = e.getLocalizedMessage();
    					e.printStackTrace();
    				}

    			} else if (reviewType.equals("previewFeedback")) {
    				try {
    					String implementStr = (String) inputparams.get("implementStr");
    					System.out.println("implementStr @@ = " + implementStr);
    					ext.casc.workflow.CmWorkflowHelper.implementFeedback(activityOid, implementStr);
    				} catch (wt.util.WTException e) {
    					result = e.getLocalizedMessage();
    					e.printStackTrace();
    				} catch (wt.util.WTPropertyVetoException e) {
    					result = e.getLocalizedMessage();
    					e.printStackTrace();
    				}

    			} else if (reviewType.equals("processFeedback")) {
    				try {
    					String processReviewStr = (String) inputparams.get("processReviewStr");
    					System.out.println("processReviewStr @@ = " + processReviewStr);
    					ext.casc.workflow.CmWorkflowHelper.processNoticeFeedback(processReviewStr);
    				} catch (wt.util.WTException e) {
    					result = e.getLocalizedMessage();
    					e.printStackTrace();
    				} catch (wt.util.WTPropertyVetoException e) {
    					result = e.getLocalizedMessage();
    					e.printStackTrace();
    				}

    			} else if (reviewType.equals("processDelete")) {
    				try {
    					ext.casc.workflow.CmWorkflowHelper.deletePeOrCp(pbonumber, workflowType);
    				} catch (Exception e) {
    					result = e.getLocalizedMessage();
    					e.printStackTrace();
    				}

    			} else {
//    				logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"正在进行数据导入");
//    				sender.addLog(logMsg);
    				System.out.println("******进行数据导入******");
    				try {
    					//为了适应将来多个包导入（超过10G）
    					JSONObject j_file = null;
						List<String> importFiles = new ArrayList<String>();
						JSONObject paramMsg = lightMsg(msg);
    					if(msg.has("j_files")){
							JSONArray j_files = msg.getJSONArray("j_files");
							if(j_files.length()>0){
								for(int i=0;i<j_files.length();i++) {
									j_file = j_files.getJSONObject(i);
									String siteiid = j_file.getString(Based.SITE_IID);
									if(!MQConstants.SITEIID_149.equals(siteiid)) {
										continue;
									}
									System.out.println("j_file @@@@ = "+j_file);
									String filePath =  getQuickFilePath(j_file);
									importFiles.add(filePath);
								}
							}else{
								j_file = msg.getJSONObject(Based.J_FILE);
								System.out.println("j_file @@@@ = "+j_file);
								String filePath =  getQuickFilePath(j_file);
								importFiles.add(filePath);
							}


						}else{
							j_file = msg.getJSONObject(Based.J_FILE);
							System.out.println("j_file @@@@ = "+j_file);
							String filePath =  getQuickFilePath(j_file);
							importFiles.add(filePath);

						}

						for(String filePath:importFiles){
							System.out.println("DataImportHandler processReceivedData  params:filePath"+filePath+","+ wfProcessOid+","+ activityTemplateID+","
									+ activityName+","+ activityOid+","+ reviewType+","+ workflowType+","
									+ pre_sendFrom+","+ orderIID+","+ previewUser+","+ dataImportStateID+","+ paramMsg);
							String tempresult = DataImportHandler.processReceivedData(filePath, wfProcessOid, activityTemplateID, activityName, activityOid, reviewType, workflowType, pre_sendFrom, orderIID, previewUser,
									dataImportStateID,paramMsg.toString());
							if(!"".equals(tempresult)){
								result  = result+tempresult;
							}
						}

    				} catch (Exception e) {
    					e.printStackTrace();
    					if("".equals(result)){
							result = e.getLocalizedMessage();
						}
    				}
    			}
    		}else{
    			//如果是非805、八部发来，则按照新的与神软制定数据包结构解析
    			System.out.println("******外院发送来的数据******");
//    			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"正在进行数据导入");
//    			sender.addLog(logMsg);

    			System.out.println("******进行数据导入******");
    			try {
    				//为了适应将来多个包导入（超过10G）
    				JSONObject j_file = msg.getJSONObject("j_file");
    				if(j_file==null){
    					JSONArray j_files = msg.getJSONArray("j_files");
    					for(int i=0;i<j_files.length();i++) {

    						j_file = j_files.getJSONObject(i);
    						String siteiid = j_file.getString(Based.SITE_IID);
    						if(!MQConstants.SITEIID_149.equals(siteiid)) {
    							continue;
    						}
    						System.out.println("j_file @@@@ = "+j_file);
							String filePath =  getQuickFilePath(j_file);

    						JSONObject paramMsg = lightMsg(msg);
							String tempresult = processMQReceivedData(filePath,paramMsg,sendFrom);
							if(!"".equals(tempresult)){
								result  = result+tempresult;
							}
    					}
    				}else{
    					System.out.println("j_file @@@@ = "+j_file);
						String filePath =  getQuickFilePath(j_file);

    					JSONObject paramMsg = lightMsg(msg);
						String tempresult = processMQReceivedData(filePath,paramMsg,sendFrom);
						if(!"".equals(tempresult)){
							result  = result+tempresult;
						}
    				}

    			} catch (Exception e) {
    				e.printStackTrace();
					if("".equals(result)){
						result = e.getLocalizedMessage();
					}
					System.out.println("错误信息："+result);
    			}
    		}

    		sendImportStateTo805(result,msg);
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("Error create service session context.");
        }finally{
        	if(mc!=null){
        		mc.unregister();
        	}
        }


		return result;
	}

	private static String getQuickFilePath(JSONObject j_file) {
		String fileName = j_file.getString(Based.FILE_NAME);
		String downloadPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator +"DataCenterReceive";
		String filePath = downloadPath +File.separator + fileName;
		File checkFile = new File(filePath);
		if(checkFile.exists()){
			return filePath;
		}
		filePath = File.separator +"c"+File.separator + "IXBExpImp"+ File.separator +"DataCenterReceive"+File.separator + fileName;
		checkFile = new File(filePath);
		if(checkFile.exists()){
			return filePath;
		}
		filePath = downloadData(j_file);
		return filePath;
	}

	public static String sendImportStateTo805(String result, JSONObject msg) {
		String msg_type = msg.getString(Based.MSG_TYPE);
		if(Based.DC_REQUEST_DISTRIBUTE_RECEIVER.equals(msg_type)){
			String msgId = msg.getString(Based.MSG_ID);
			String orderName = msg.getString(Based.NAME);
			String dsoState = "";
			if("".equals(result)){
				dsoState="已发放";
			}else{
				dsoState="发放失败";
			}
			String dsoNumber ="";
			String dsoType = "";
			if((orderName!=null && orderName.contains("更改单"))||(msgId.endsWith("_FF"))){
				dsoType="更改单";
				dsoNumber = msgId.replaceAll("_805_FF","");
			}else{
				dsoType="数据发放单";
				dsoNumber = msgId.replaceAll("_805","");
			}

			return CustomCall.synDSOStateTo805(dsoNumber,dsoType,dsoState);
		}
		return "";
	}

	/**
	 * @param msg
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static String addReceiveLogs(JSONObject msg) {

		String receiverStr = "";
		MethodContext mc = null;
		String context = "Unknown";
		try{
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
			context = mc.getId().toString();
			System.out.println("**********设置上下文**********"+context);

			JSONObject stdProduct = msg.getJSONObject(Based.J_STD_PRODUCT);
			String productId = stdProduct.getString(Based.ID);
	        String localProductName = ProductConvertUtil.getLocalProductName(productId);

	        QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
	        SearchCondition sc = new SearchCondition(PDMLinkProduct.class,
	                WTContainer.NAME, SearchCondition.EQUAL, localProductName, false);
	        qs.appendSearchCondition(sc);
	        QueryResult qr = PersistenceHelper.manager.find(qs);
	        if (qr.hasMoreElements()) {
	        	PDMLinkProduct  product = (PDMLinkProduct) qr.nextElement();

	        	ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) product);
                Role role = Role.toRole("ZHURENGONGYISHI");
                if (role != null) {
                	 Set<String> userNameSet = new HashSet<String>();
                	 ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                     for (WTPrincipalReference reference : arrayList) {
                         Object object2 = reference.getPrincipal();
                         if (object2 instanceof WTUser) {
                             WTUser user = (WTUser) object2;
                             if(!userNameSet.contains(user.getFullName())){
                            	 if(receiverStr == null||"".equals(receiverStr)) {
                            		 receiverStr = user.getFullName();
                            	 }else {
                            		 receiverStr = receiverStr + "," + user.getFullName();
                            	 }
                             }
                             userNameSet.add(user.getFullName());
                         }else if (object2 instanceof WTGroup) {
     						WTGroup group = (WTGroup) object2;
     						List<WTUser> users =  WTUtil.getGroupMembersOfUser(group);
     						for(WTUser user:users){
     							if(!userNameSet.contains(user.getFullName())){
     								if(receiverStr == null||"".equals(receiverStr)) {
                               		 receiverStr = user.getFullName();
                               	 }else {
                               		 receiverStr = receiverStr + "," + user.getFullName();
                               	 }
                                }
                                userNameSet.add(user.getFullName());
     						}
     					}
                     }
                     if("".equals(receiverStr)){
                    	 receiverStr = "协同管理员";
                     }
                }
	        }
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			if(mc != null){
				mc.unregister();
			}
		}
		return receiverStr;
	}
	/**轻量化json数据防止流程变量存不进去
	 * @param fromMsg
	 * @return
	 */
	public static JSONObject lightMsg(JSONObject fromMsg) {
		JSONObject lightMsg = new JSONObject();
		lightMsg.put(Based.MSG_ID, fromMsg.getString(Based.MSG_ID));
		lightMsg.put(Based.ID, fromMsg.getString(Based.ID));
		lightMsg.put(Based.NAME, fromMsg.getString(Based.NAME));
		try{
			lightMsg.put(Based.MSG_TYPE, fromMsg.optString(Based.MSG_TYPE));
			lightMsg.put(Based.J_SRC_SITE, fromMsg.optJSONObject(Based.J_SRC_SITE));
			lightMsg.put("sendFrom", fromMsg.optString("sendFrom"));
		}catch(Exception e){
			e.printStackTrace();
		}
		return lightMsg;
	}
	/**
	 * @param filePath
	 * @throws WTException
	 */
	private static String processMQReceivedData(String filePath,JSONObject msg,String sendFrom) throws WTException {
		ExpImpLogger logger = ExpImpLogger.getInstance();
		String result = "";
		File file = new File(filePath);
        if(!file.exists()){
        	String bakFilePath = filePath.replaceAll("tmp", "bakIXBExpImp");
        	file = new File(bakFilePath);
        	if(!file.exists()){
        		logger.log("File do not exist <" + filePath + ">");
        		return "File do not exist <" + filePath + ">";
        	}
        }
        if (!RemoteMethodServer.ServerFlag) {
            throw new WTException("This method must run in MethodServer.");
        }
        CmImportHandler impHnd = null;
        try{

            impHnd = new CmImportHandler(file);
            ArrayList list = impHnd.getAllTopObjectXmlFileInJar();
            Iterator it = list.iterator();
			WfProcess wfprocess = null;
			ProcessEnvelope pe = null;
			ChangePackaged change = null;
			while (it.hasNext()) {
                String fname = (String) it.next();
                CmExpImpObject expimp = MQExpImpPersistable.newMQExpImpPersistable(impHnd, fname);
                if (expimp == null){
                	continue;
                }
                expimp.setSendFrom(sendFrom);
                String typename = fname.substring(fname.indexOf("TAG-") + 4);
                if (typename.indexOf("-") > 0) {
                    typename = typename.substring(0, typename.indexOf("-"));
                }else{
                	typename = typename.substring(0, typename.indexOf(".xml"));
                }
                logger.log("Start To Import Ojbect Type " + typename);
				try {
					Object object = expimp.importObject();
					if(object == null) {
						continue;
					}
					if(object !=null){
						try{
							SecurityLabelDataHelper.setSecret(object);
						}catch(Exception e) {
							logger.log("设置密级出错：" + e.getLocalizedMessage());
						}

					}
					if(object instanceof ErrorImportObject) {
						ErrorImportObject eo = (ErrorImportObject) object;
						logger.log("ERROR:数据导入失败：【" + eo.getNumber() + ":" + eo.getMessage() + "】");
						result = result + "数据导入失败：【" + eo.getNumber() + ":" + eo.getMessage() + "】";
					}
					if (object instanceof ProcessEnvelope) {
						 pe = (ProcessEnvelope) object;

					}else if(object instanceof ChangePackaged){
						change = (ChangePackaged) object;

					}
				}catch (WTException wet) {
					wet.printStackTrace();
		            logger.log(wet);
		            throw wet;
		        }
            }
            importLinks(impHnd,sendFrom);
			if(pe!=null){
				WfEngineHelper.service.terminateObjectsRunningWorkflows(pe);
				String msgType = msg.optString(Based.MSG_TYPE);
				if(Based.DC_REQUEST_DISTRIBUTE_RECEIVER.equals(msgType)){
					WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
							.getProcessDefinition("149正式发放包流程");
					wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
							pe.getContainerReference());
					wfprocess.setName("149正式发放包流程_" + pe.getNumber());
					ProcessData processdata = wfprocess.getContext();
					processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
					processdata.setValue("sendFrom", sendFrom);
					processdata.setValue("mqmessage",  msg.toString());

					WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
				}else{
					WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
							.getProcessDefinition("149签审包工艺会签流程");
					wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
							pe.getContainerReference());
					wfprocess.setName("149签审包工艺会签流程_" + pe.getNumber());
					ProcessData processdata = wfprocess.getContext();
					processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
					processdata.setValue("sendFrom", sendFrom);
					processdata.setValue("mqmessage", msg.toString());
					WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
				}

			}else if(change!=null){
				String msgType = msg.optString(Based.MSG_TYPE);
				if(Based.DC_REQUEST_DISTRIBUTE_RECEIVER.equals(msgType)){
					WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
							.getProcessDefinition("149正式发放包流程");
					wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
							change.getContainerReference());
					wfprocess.setName("149正式发放包流程_" + change.getNumber());
					ProcessData processdata = wfprocess.getContext();
					processdata.setValue("primaryBusinessObject", change);// 设置流程主对象
					processdata.setValue("sendFrom", sendFrom);
					processdata.setValue("mqmessage",  msg.toString());

					WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
				}else{
					WfEngineHelper.service.terminateObjectsRunningWorkflows(change);

					WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
							.getProcessDefinition("149变更签审包工艺会签流程");
					wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
							change.getContainerReference());
					wfprocess.setName("149变更签审包工艺会签流程_" + change.getNumber());
					ProcessData processdata = wfprocess.getContext();
					processdata.setValue("primaryBusinessObject", change);// 设置流程主对象
					processdata.setValue("sendFrom", sendFrom);
					processdata.setValue("mqmessage", msg.toString());
					WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
				}


			}
        } catch (Exception e){
        	e.printStackTrace();
			if("".equals(result)){
				result = e.getLocalizedMessage();
			}
        }
		return result;

	}

	/**
	 * @param impHnd
	 * @throws WTException
	 *
	 */
	private static void importLinks(CmImportHandler impHnd,String sendFrom) throws WTException {
		// TODO Auto-generated method stub
		ArrayList wtal = impHnd.getAllObjects();
        Iterator it = wtal.iterator();
        while (it.hasNext()) {
            Object o =  it.next();
			if(o instanceof ErrorImportObject){
				continue;
			}
			Persistable p = (Persistable) o;
            String remoteId = (String) impHnd.getImportedObjectRemoteId(p);
            String dir = CmExpImpHelper.getObjectLocalIdSavePathInJar(remoteId);
            ArrayList list = impHnd.getXmlDocumentsUnderLikelyDirInJar(dir);
            Iterator ite = list.iterator();
            while (ite.hasNext()) {
                String fname = (String) ite.next();
                CmExpImpLink expimp =MQExpImpLink.newCmExpImpLink(p, impHnd, fname);
                if (expimp == null)
                    continue;
                try {
					expimp.setSendFrom(sendFrom);
					expimp.importObject();
                } catch (MissingObjectException moe) {
                    impHnd.removeFromProperlyReceivedObjectSet(p);
                } catch (WTException wte) {
                    wte.printStackTrace();
                    impHnd.removeFromProperlyReceivedObjectSet(p);
                    //linkImportFailed = true;
                }
            }
        }
	}


	/**
	 * 接收会签反馈会签信息
	 * @param msg
	 */
	public static String receiveProcessNoticeFeedbackData(JSONObject msg) {
		System.out.println("***********开始处理接收信息***********");
		String context = "Unknown";
		MethodContext mc = null;
		Sender sender = Sender.getInstance();
		String result="";
		String id = msg.getString(Based.MSG_ID);

		LogMessage logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.STATUS_7);
		sender.addLog(logMsg);


		JSONArray ja_signs_responses = msg.getJSONArray(Based.JA_SIGNS_RESPONSE);
		JSONObject ja_signs_response= null;
		JSONArray ja_tasks_responses = msg.getJSONArray(Based.JA_TASKS_RESPONSE);
		JSONObject ja_tasks_response= null;



		String processNumber =  msg.getString(Based.MSG_ID);
		try {
			mc = MethodContext.getContext(Thread.currentThread());
            if (mc == null)
                mc = new MethodContext(null, null);
            if (mc.getAuthentication() == null) {
                SessionAuthenticator sa = new SessionAuthenticator();
                mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
            }
            context = mc.getId().toString();
            System.out.println("**********设置上下文**********"+context);
			WTDocument document = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTDocument.class, processNumber);


			Enumeration enumeration = WfEngineHelper.service.getAssociatedProcesses(document,WfState.OPEN_RUNNING);
			WfProcess process = null;
			while (enumeration.hasMoreElements()) {
				process = (WfProcess) enumeration.nextElement();
				WfAssignedActivity wfAa = getOpenRunningActivity(process);

				if(wfAa!=null &&"外部会签".equals(wfAa.getName())){

					if(Constants.WFN_PROCESS_ECN.equals(process.getTemplate().getName())
							||Constants.WFN_WUJIPROCESSWF.equals(process.getTemplate().getName())
							||Constants.WFN_WUJIREPORTPROCESSWF.equals(process.getTemplate().getName())
							||Constants.WFN_SANJIPROCESSWF.equals(process.getTemplate().getName())){
						commonProcess(wfAa,process,document,ja_signs_responses,ja_tasks_responses);
					}else{
						String activityOid = "wt.workflow.work.WfAssignedActivity:"+wfAa.getPersistInfo().getObjectIdentifier().getId()+"";
						String route = "通过";
						String allYijian = "";
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
						ProcessData pd = wfAa.getContext();
						List outSignInfo = (List)pd.getValue("outSignInfo");
						if(outSignInfo == null){
							outSignInfo = new ArrayList();
						}
						for(int i=0;i<ja_signs_responses.length();i++){
							ja_signs_response = ja_signs_responses.getJSONObject(i);
							ja_tasks_response = ja_tasks_responses.getJSONObject(i);
							String sign_content = ja_signs_response.getString("sign_content");
							String is_agree = ja_signs_response.getString("is_agree");
							String div_name = ja_signs_response.getString("div_name");
							String task_user_name = ja_tasks_response.getString("task_user_name");
							String yijian = task_user_name+":"+sign_content;
							allYijian = allYijian + yijian+";";
							ASESHuiqianSignature signature = ASESHuiqianSignature.newASESHuiqianSignature();
							if("1".equals(is_agree)){
								signature.setOpinion("同意");
							}else{
								signature.setOpinion("不同意");
							}
							signature.setActivity(activityOid);
							signature.setSignature(yijian);
							PersistenceHelper.manager.save(signature);
							SignLink sl = SignLink.newSignLink(document, signature);
							PersistenceHelper.manager.save(sl);

							NmOid oid = new NmOid();
							HashMap<String,Object> map = new HashMap<String,Object>();
							map.put("signName",task_user_name );
							map.put("signCompany",div_name );

							String sign_time = ja_signs_response.getString("sign_time");
							Date date = new Date(Long.parseLong(sign_time));
							String signDate =sdf.format(date);
							map.put("signDate",signDate);
							oid.setAdditionalInfo(map);

							if(!"驳回".equals(route)){
								if("1".equals(is_agree)){
									route = "通过";
								}else{
									route = "驳回";
								}
							}

							outSignInfo.add(oid);
						}
						pd.setValue("outSignInfo", outSignInfo);
						PersistenceHelper.manager.save(wfAa);
//			        String signStr = "陈若飞/一室/2013-11-19;廖钧/一室/2013-11-19";
						CmWorkflowHelper.completeActivity(activityOid, route, allYijian);
					}

				}else{
					System.out.println("无等待任务，流程已经流转结束！");
				}
			}
			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_15);
			sender.addLog(logMsg);
		} catch (Exception e) {
			e.printStackTrace();

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.STATUS_16);
			logMsg.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(logMsg);
		} finally {
		if(mc!=null){
			mc.unregister();
		}
	}

		return result;

	}

	private static void commonProcess(WfAssignedActivity wfAa, WfProcess process, WTDocument document,JSONArray ja_signs_responses,JSONArray ja_tasks_responses ) throws WTException, WTPropertyVetoException {
		String activityOid = "wt.workflow.work.WfAssignedActivity:"+wfAa.getPersistInfo().getObjectIdentifier().getId()+"";
		String route = "通过";
		String allYijian = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		ProcessData pd = process.getContext();
		//String  outSignInfos = (String)pd.getValue("outSignInfos");
		//			        String signStr = "1556304983453~805:1111:desc1;149:222:desc2@";
		String outSignInfos =document.getNumber()+ "~";
		JSONObject ja_signs_response= null;
		JSONObject ja_tasks_response= null;
		for(int i=0;i<ja_signs_responses.length();i++){
			ja_signs_response = ja_signs_responses.getJSONObject(i);
			ja_tasks_response = ja_tasks_responses.getJSONObject(i);
			String sign_content = ja_signs_response.getString("sign_content");
			String is_agree = ja_signs_response.getString("is_agree");
			String div_name = ja_signs_response.getString("div_name");
			String task_user_name = ja_tasks_response.getString("task_user_name");
			String yijian = task_user_name+":"+sign_content;
			allYijian = allYijian + yijian+";";
			ASESHuiqianSignature signature = ASESHuiqianSignature.newASESHuiqianSignature();
			if("1".equals(is_agree)){
				signature.setOpinion("同意");
			}else{
				signature.setOpinion("不同意");
			}
			signature.setActivity(activityOid);
			signature.setSignature(yijian);
			PersistenceHelper.manager.save(signature);
			SignLink sl = SignLink.newSignLink(document, signature);
			PersistenceHelper.manager.save(sl);

			String sign_time = ja_signs_response.getString("sign_time");
			Date date = new Date(Long.parseLong(sign_time));
			String signDate =sdf.format(date);
			if(!"驳回".equals(route)){
				if("1".equals(is_agree)){
					route = "通过";
				}else{
					route = "驳回";
				}
			}
			if("通过".equals(route)){
				outSignInfos = outSignInfos + div_name+":"+task_user_name+":"+signDate+";";
			}

		}
		pd.setValue("outSignInfos", outSignInfos);
		PersistenceHelper.manager.save(process);
//			        String signStr = "1556304983453~805:1111:desc1;149:222:desc2@";
		CmWorkflowHelper.completeActivity(activityOid, route, allYijian);
	}

	public static WfAssignedActivity getOpenRunningActivity(WfProcess wfprocess) throws WTException {
		Enumeration enumeration = WfEngineHelper.service.getProcessSteps(wfprocess, null);
		while (enumeration.hasMoreElements()) {
			WfActivity wfactivity = (WfActivity) enumeration.nextElement();
			if (wfactivity instanceof WfAssignedActivity) {
				WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
				String state = wfassignedactivity.getState().toString();
				if ("OPEN_RUNNING".equals(state)) {
					return wfassignedactivity;
				}

			}
		}
		return null;

	}


	/**
	 * 手动导入数据
	 * @param
	 * @return
	 * @throws IOException
	 */
	public static void receiveData(String msgId,String fileName) throws Exception {
		//Sender sender = Sender.getInstance();
		// LogMessage logMsg = null;
		//	String id = msg.getString(Based.MSG_ID);
		File file = JsonConvertUtil.getJsonFromFile(msgId);
		JSONObject msg = null;
		if(file!=null){
			String smsg = JsonConvertUtil.fileRead(file);
			if(file.getAbsolutePath().contains(JsonConvertUtil.DcDistributeRequestHandler)){
				msg = new JSONObject(smsg);
			}else if(file.getAbsolutePath().contains(JsonConvertUtil.DcShareRequestHandler)){
				msg = new JSONObject(smsg);
			}
			else if(file.getAbsolutePath().contains(JsonConvertUtil.DcSignRequestHandler)){
				msg = new JSONObject(smsg);
			}
			else if(file.getAbsolutePath().contains(JsonConvertUtil.DcSignTaskSynResponseHandler)){
				msg = new JSONObject(smsg);
			}
			else if(file.getAbsolutePath().contains(JsonConvertUtil.DcSignTeminateRequestHandler)){
				msg = new JSONObject(smsg);
			}
			if(msg!=null) {
				JSONObject j_src_site = msg.getJSONObject(Based.J_SRC_SITE);
				String sendFrom =j_src_site.getString("id");
				try{
					String newSendFrom = msg.getString("sendFrom");
					if(!Tools.isNull(newSendFrom)){
						sendFrom = newSendFrom;
					}
				}catch(Exception e){
					e.printStackTrace();
				}

				//如果是805、八部发来，则按照老的稳定的数据包结构解析
				if(sendFrom!=null&&MQExpImpUtil.isCmPackage(sendFrom)&&msg.has("j_soapparams")){
					String soapparamsStr = "";
					JSONArray j_soapparams = msg.getJSONArray("j_soapparams");
					for(int i=0;i<j_soapparams.length();i++) {
						JSONObject soapparam = j_soapparams.getJSONObject(i);
						String siteiid = soapparam.getString(Based.SITE_IID);
						if(MQConstants.SITEIID_149.equals(siteiid)) {
							soapparamsStr =  soapparam.getString("soapparams");
							break;
						}
					}
					System.out.println("soapparamsStr @@@@ = "+soapparamsStr);
					HashMap inputparams = (HashMap) Deserialize.deserializeMap(soapparamsStr);

					String pre_sendFrom = (String) inputparams.get("sendFrom");
					if(Tools.isNull(pre_sendFrom)){
						pre_sendFrom = sendFrom;
					}

					String wfProcessOid = (String) inputparams.get("wfProcessOid");
					String activityTemplateID = (String) inputparams.get("activityTemplateID");
					String activityName = (String) inputparams.get("activityName");
					String activityOid = (String) inputparams.get("activityOid");
					String reviewType = (String) inputparams.get("reviewType");
					String workflowType =(String) inputparams.get("workflowType");
					String orderIID = (String) inputparams.get("orderIID");
					String previewUser = (String) inputparams.get("previewUser");
					String pbonumber = (String) inputparams.get("pbonumber");
					String dataImportStateID = (String) inputparams.get("ReceiveStateID");

					if (reviewType.equals("approvedCompleted")) {
						try {
							ext.casc.workflow.CmWorkflowHelper.completeActivityBy805(activityOid);
						} catch (wt.util.WTException e) {
							e.printStackTrace();
						}

					} else if (reviewType.equals("previewFeedback")) {
						try {
							String implementStr = (String) inputparams.get("implementStr");
							System.out.println("implementStr @@ = " + implementStr);
							ext.casc.workflow.CmWorkflowHelper.implementFeedback(activityOid, implementStr);
						} catch (wt.util.WTException e) {
							e.printStackTrace();
						} catch (wt.util.WTPropertyVetoException e) {
							e.printStackTrace();
						}

					} else if (reviewType.equals("processFeedback")) {
						try {
							String processReviewStr = (String) inputparams.get("processReviewStr");
							System.out.println("processReviewStr @@ = " + processReviewStr);
							ext.casc.workflow.CmWorkflowHelper.processNoticeFeedback(processReviewStr);
						} catch (wt.util.WTException e) {
							e.printStackTrace();
						} catch (wt.util.WTPropertyVetoException e) {
							e.printStackTrace();
						}

					} else if (reviewType.equals("processDelete")) {
						try {
							ext.casc.workflow.CmWorkflowHelper.deletePeOrCp(pbonumber, workflowType);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {

						System.out.println("******进行数据导入******");
						JSONObject paramMsg = lightMsg(msg);
						String filePath = "/pdm/ptc/Windchill_11.0/Windchill/tmp/IXBExpImp/DataCenterReceive/"+fileName;
						String tempresult = DataImportHandler.processReceivedData(filePath, wfProcessOid, activityTemplateID, activityName, activityOid, reviewType, workflowType, pre_sendFrom, orderIID, previewUser,
								dataImportStateID,paramMsg.toString());
						if(!"".equals(tempresult)){
						}
					}
				}else{
					//如果是非805、八部发来，则按照新的与神软制定数据包结构解析
					System.out.println("******外院发送来的数据******");

					System.out.println("******进行数据导入******");
					try {
						//为了适应将来多个包导入（超过10G）
						JSONObject j_file = msg.getJSONObject("j_file");
						if(j_file==null){
							JSONArray j_files = msg.getJSONArray("j_files");
							for(int i=0;i<j_files.length();i++) {

								j_file = j_files.getJSONObject(i);
								String siteiid = j_file.getString(Based.SITE_IID);
								if(!MQConstants.SITEIID_149.equals(siteiid)) {
									continue;
								}
								System.out.println("j_file @@@@ = "+j_file);
								String filePath =  getQuickFilePath(j_file);

								JSONObject paramMsg = lightMsg(msg);
								String tempresult = processMQReceivedData(filePath,paramMsg,sendFrom);

							}
						}else{
							System.out.println("j_file @@@@ = "+j_file);
							String filePath =  getQuickFilePath(j_file);

							JSONObject paramMsg = lightMsg(msg);
							processMQReceivedData(filePath,paramMsg,sendFrom);

						}

					} catch (Exception e) {
						e.printStackTrace();

					}
				}
			}

		}
	}
}
