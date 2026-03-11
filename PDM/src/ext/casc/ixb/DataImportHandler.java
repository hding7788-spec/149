package ext.casc.ixb;

import com.bjsasc.avidm.mq.message.Based;
import com.glaway.mpm.sjzyk.SjzykSchedule;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.UserUtil;
import com.ptc.extend.ixb.*;
import com.ptc.extend.util.ObjectProperty;
import com.ptc.wpcfg.utilities.PrincipalHelper;
import com.ptc.wvs.server.util.RepUpdateUtils;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changepackaged.ChangePackagedUtil;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.changerequest.ChangeRequestAffectLink;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.changeRequest.Change2WorkflowHelper;
import ext.casc.preview.PreMemberLink;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewObject;
import ext.casc.preview.PreviewUtil;
import ext.casc.securitymgr.SecurityLabelDataHelper;
import ext.casc.synch.SoapCall;
import ext.casc.util.DBConn;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import ext.casc.workflow.CmWorkflowHelper;
import org.apache.soap.SOAPException;
import org.apache.soap.rpc.Call;
import org.apache.soap.rpc.Parameter;
import org.apache.soap.rpc.Response;
import org.apache.soap.transport.http.SOAPHTTPConnection;
import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.*;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.project.Role;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.type.Typed;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.viewmarkup.DerivedImage;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfBallot;
import wt.workflow.work.WorkItem;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DataImportHandler implements RemoteAccess {

    public String importData(String fileName) {
        System.out.println("==================Server=======================" + fileName);
        if (!isProductExist()) {
            createProduct();
        }

        if (!isImportTargetExist()) {
            doImportData();
        } else {
            doUpdateData();
        }

        return "Success";
    }

    private String doImportData() {
        return "Success";
    }

    private void doUpdateData() {

    }

    // ////////////////
    private boolean isProductExist() {

        return true;
    }

    private PDMLinkProduct createProduct() {
        PDMLinkProduct product = null;

        return product;
    }

    private boolean isImportTargetExist() {

        return true;
    }

    public static void main(String[] args) throws WTException {
    	System.out.println("DataImportHandler");
    	String filePath = null;
		String userName = null;
		String password = null;
		int i = args.length;

		if (i ==0){
			System.out.println("Usage: windchill ext.casc.ixb.DataImportHandler -f <导入文件全路径> -u <用户名> -p <密码>");
			System.exit(1);
		}

		for(int j = 0; j < i; j++){
			if (args[j].equalsIgnoreCase("-f")) {
				if (++j < i) {

					filePath = new String(args[j]);
				}
				continue;
			}

			if (args[j].equalsIgnoreCase("-u")) {
				if (++j < i) {
					userName = new String(args[j]);
				}
				continue;
			}
			if (args[j].equalsIgnoreCase("-p")) {
				if (++j < i) {
					password = new String(args[j]);
				}
			}
		}

		if(filePath==null||filePath.length()<=0){
			System.out.println("请指定导入文件全路径.");
			System.exit(1);
		}
		if(!filePath.endsWith(".expimp")){
			System.out.println("导入文件格式不正确.");
			System.exit(1);
		}
		if((userName==null||userName.length()<=0)
				||(password==null||password.length()<=0)){
			System.out.println("用户名或密码不能为空.");
			System.exit(1);
		}
		RemoteMethodServer rms=RemoteMethodServer.getDefault();
		rms.setUserName(userName);
		rms.setPassword(password);
		Map<String,String> params = new HashMap<String,String>();
		params.put("fileName", filePath);
        processReceivedData(filePath,"wf",null, null, null, null,"1","no8","orderIID","longxiuchuan","");
		//processZYKReceivedData(filePath, "zyk");
    }

    public static String processReceivedData2(String fileName, String wfProcessOid, String activityTemplateID,String activityName ,String activityOid,
            String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser){
    	try {
			return processReceivedData(fileName, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser,"");
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    }

    public static String processReceivedData(String fileName, String wfProcessOid, String activityTemplateID,String activityName ,String activityOid,
            String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser,String dataImportStateID)
            throws WTException {
    	System.out.println("####processReceivedData");
        if (!RemoteMethodServer.ServerFlag) {
            String method = "processReceivedData";
            Class[] types = { String.class, String.class,String.class, String.class,String.class, String.class ,String.class,String.class,String.class,String.class,String.class};
            Object[] vals = { fileName, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID ,previewUser,dataImportStateID};

            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
				return (String)rms.invoke(method, DataImportHandler.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	System.out.println("####远程方法调用processReceivedData");
            ///pdm/ptc/Windchill10.0/Windchill/tmp/IXBExpImp/xxxx.expimp

        	 File file = new File(fileName);
             if(!file.exists()){
             	String bakFilePath = fileName.replaceAll("tmp", "bakIXBExpImp");
             	file = new File(bakFilePath);
             	if(!file.exists()){
             		 return "数据包异常删除";
             	}
             }
             return  processReceivedData(file, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser,dataImportStateID,"");
        }
		return "";
     }
    public static String processReceivedData(String fileName, String wfProcessOid, String activityTemplateID,String activityName ,String activityOid,
            String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser,String dataImportStateID,String mqmsg)
            throws WTException {
    	System.out.println("####processReceivedData");
        if (!RemoteMethodServer.ServerFlag) {
            String method = "processReceivedData";
            Class[] types = { String.class, String.class,String.class, String.class,String.class, String.class ,String.class,String.class,String.class,String.class,String.class,String.class};
            Object[] vals = { fileName, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID ,previewUser,dataImportStateID,mqmsg};

            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
				return (String)rms.invoke(method, DataImportHandler.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	System.out.println("####远程方法调用processReceivedData");
            ///pdm/ptc/Windchill10.0/Windchill/tmp/IXBExpImp/xxxx.expimp

        	 File file = new File(fileName);
             if(!file.exists()){
            	String bakFilePath = File.separator+"c"+File.separator+"IXBExpImp"+File.separator+"DataCenterReceive";
                String bakfileName =fileName.substring(fileName.lastIndexOf("/")+1);
                System.out.println("bakFilePath="+bakFilePath+File.separator+bakfileName);
                file = new File(bakFilePath+File.separator+bakfileName);
             	if(!file.exists()){
             		 return "数据包删除异常!";
             	}
             }
             return  processReceivedData(file, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser,dataImportStateID,mqmsg);
        }
		return "";
     }
    /**数据包导入接口
     * @param file 数据包文件
     * @param wfProcessOid 总体所流程oid (activityOid805)
     * @param activityOid 总装厂等待活动oid 值为 可能为null (activityOid149)
     * @param activityName 整体所等待活动 值为 外部工艺会签 技术会签等
     * @param reviewType  会签类型 值为 工艺预审、文档签审(WTDocument)、批量签审(ProcessEnvelope)、变更签审(ChangePacked) (approvedType)
     * @param workflowType 用于发放数据记录 值为 正式数据、工艺会签等 (isFormal)
     * @param sendFrom 总体所发放单位
     * @param activityTemplateID 只用于A4系统
     * @param orderIID A4系统送审单oid
     * @throws WTException
     */
    public static String processReceivedData(File file, String wfProcessOid,String activityTemplateID,String activityName, String activityOid, String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser,String dataImportStateID,String mqmsg)
            throws WTException {
    	String message = "";
    	String pboNumber = "";
    	String pboName = "";
        if (!RemoteMethodServer.ServerFlag)
            throw new WTException("This method must run in MethodServer.");
        CmImportHandler impHnd = null;
		ExpImpLogger logger = ExpImpLogger.getInstance();
		String errorMsgTemp = "";
		try{
            impHnd = new CmImportHandler(file);
        }catch (ExceptionInInitializerError e){
        	e.printStackTrace();
			errorMsgTemp = e.getLocalizedMessage();
			logger.log("数据包解析失败：" + errorMsgTemp);
			String receiveState = file.getAbsolutePath()+"数据包解析失败";
			updateDataReceiveRecord(dataImportStateID,reviewType,orderIID,workflowType,receiveState,e.getLocalizedMessage(),"","");
        }
        if(impHnd ==null){
        	return file.getAbsolutePath()+"数据包解析失败:"+errorMsgTemp;
        }
        ProcessEnvelope pe = null;
        ChangePackaged changePackaged = null;
        ChangeRequest request = null;
        Preview preview = null;
        StringBuilder sb = new StringBuilder("");
        List<ErrorImportObject> eios  =  new ArrayList<ErrorImportObject>();

		try{
			String msg_type = "";
			String msgId = "";
			if(mqmsg!=null &&!"".equals(mqmsg)) {
				JSONObject fromMsg = new JSONObject(mqmsg);
				msg_type = fromMsg.getString(Based.MSG_TYPE);
				msgId = fromMsg.optString(Based.MSG_ID);
			}

			ArrayList files = new ArrayList();
			try {
	            ArrayList list = impHnd.getAllTopObjectXmlFileInJar();
				if(list != null && list.size() > 0) {
					files.addAll(list);
				}
	            logger.log("All Ojbect Size Is: " + list.size());
	            Iterator it = list.iterator();
	            while (it.hasNext()) {
	                String fname = (String) it.next();
	                CmExpImpObject expimp = CmExpImpPersistable.newCmExpImpPersistable(impHnd, fname);
	                if (expimp == null){
	                	continue;
	                }
	                expimp.setSendFrom(sendFrom);
					expimp.setIxbFileName(file.getName());
					expimp.setMsgId(msgId);
	                String typename = fname.substring(fname.indexOf("TAG-") + 4);
	                if (typename.indexOf("-") > 0)
	                    typename = typename.substring(0, typename.indexOf("-"));
	                else typename = typename.substring(0, typename.indexOf(".xml"));
	                logger.log("Start To Import Ojbect Type " + typename);

					Object object = expimp.importObject();

					if(object !=null){
						try{
							SecurityLabelDataHelper.setSecret(object);
						}catch(Exception e) {
							logger.log("设置密级出错：" + e.getLocalizedMessage());
						}

					}
					if(object ==null){
						 ErrorImportObject eo = new ErrorImportObject();
						 eo.setMessage("未知错误；");
						logger.log("ERROR:数据导入失败：未知错误。");
					}else if(object instanceof ErrorImportObject){
						ErrorImportObject eo = (ErrorImportObject)object;
						eios.add(eo);
						logger.log("ERROR:数据导入失败：【"+eo.getNumber()+":"+eo.getMessage()+"】");
						message = message+"数据导入失败：【"+eo.getNumber()+":"+eo.getMessage()+"】";
					}else if(object instanceof WTPart){
						/*WTPart part = (WTPart)object;

						IBAUtility iba = new IBAUtility((IBAHolder) part);
						try {
							iba.setIBAValue("sendFrom", sendFrom);
							part = (WTPart) iba.updateAttributeContainer(part);
							iba.updateIBAHolder(part);
						} catch (WTPropertyVetoException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/

					}
					if (object instanceof ProcessEnvelope) {
						pe = (ProcessEnvelope) object;
						pboNumber = pe.getNumber();
						pboName = pe.getName();
						// 先断掉所有的link关系
						QueryResult qr = PersistenceHelper.manager.navigate(pe,
								"theRevisionControlled",
								EnvelopeMemberLink.class, false);
						if (qr.size() > 0) {
							Transaction tx = new Transaction();
							tx.start();
							while (qr.hasMoreElements()) {
								EnvelopeMemberLink link = (EnvelopeMemberLink) qr
										.nextElement();
								RevisionControlled revision = link
										.getRevisionControlled();
								String memberNumber = ObjectProperty
										.getNumber(revision);
								if (!(revision instanceof WTPart)) {
									impHnd.putIntExistedNumberObject(
											memberNumber, revision);
								}
								PersistenceHelper.manager.delete(link);
							}
							tx.commit();

						}

					}

					if (object instanceof Preview) {
						preview = (Preview) object;
						pboNumber = preview.getNumber();
						pboName = preview.getName();
						// 先断掉所有的link关系
						QueryResult qr = PersistenceHelper.manager.navigate(preview,
								"thePreviewObj", PreMemberLink.class, false);
						if (qr.size() > 0) {
							Transaction tx = new Transaction();
							tx.start();
							while (qr.hasMoreElements()) {
								PreMemberLink link = (PreMemberLink) qr.nextElement();
								PreviewObject previewObj = link.getPreviewObj();
								PersistenceHelper.manager.delete(previewObj);
								PersistenceHelper.manager.delete(link);
							}
							tx.commit();

						}
                      }


					if (object instanceof ChangeRequest) {
                        request = (ChangeRequest) object;
                        // 先断掉所有的link关系
                        QueryResult qr = PersistenceHelper.manager.navigate(request,
                                "theRevisionControlled",
                                ChangeRequestAffectLink.class, false);
                        if (qr.size() > 0) {
                            Transaction tx = new Transaction();
                            tx.start();
                            while (qr.hasMoreElements()) {
                                ChangeRequestAffectLink link = (ChangeRequestAffectLink) qr
                                        .nextElement();
                                RevisionControlled revision = link
                                        .getRevisionControlled();
                                String memberNumber = ObjectProperty
                                        .getNumber(revision);
                                if (!(revision instanceof WTPart)) {
                                    impHnd.putIntExistedNumberObject(
                                            memberNumber, revision);
                                }
                                PersistenceHelper.manager.delete(link);
                            }
                            tx.commit();

                        }

                    }

					if (object instanceof ChangePackaged) {
						if(!"DISORDER".equals(reviewType)){
							changePackaged = (ChangePackaged) object;
							pboNumber = changePackaged.getNumber();
							pboName = changePackaged.getName();
							// 先断掉所有的link关系
							QueryResult qr = PersistenceHelper.manager.navigate(
									changePackaged, "theRevisionControlled",
									ChangePackagedResultLink.class, false);
							if (qr.size() > 0) {
								Transaction tx = new Transaction();
								tx.start();
								while (qr.hasMoreElements()) {
									ChangePackagedResultLink link = (ChangePackagedResultLink) qr
											.nextElement();
									RevisionControlled revision = link
											.getRevisionControlled();
									String memberNumber = ObjectProperty
											.getNumber(revision);
									if (!(revision instanceof WTPart)) {
										impHnd.putIntExistedNumberObject(
												memberNumber, revision);
									}
									PersistenceHelper.manager.delete(link);
								}
								tx.commit();

							}
						}
					}
					if ("WTDocument".equals(reviewType)) {
						WTDocument document = (WTDocument) object;
						// 获取关联流程实例
						boolean isHasProcess = false;
						Enumeration enumeration = WfEngineHelper.service
								.getAssociatedProcesses(document, null);
						WfProcess process = null;
						while (enumeration.hasMoreElements()) {
							process = (WfProcess) enumeration.nextElement();
							ProcessData pd = process.getContext();
							pd.setValue("wfProcessOid", wfProcessOid);
							pd.setValue("activityName", activityName);
							pd.setValue("activityTemplateID", activityTemplateID);
							pd.setValue("sendFrom", sendFrom);
							PersistenceHelper.manager.save(process);
							isHasProcess = true;
						}
						if (!isHasProcess) {
							WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
									.getProcessDefinition("文档工艺会签流程");
							WfProcess wfprocess = null;
							wfprocess = WfEngineHelper.service.createProcess(
									wfprocessdefinition, null,
									document.getContainerReference());

							wfprocess.setName("文档工艺会签流程_"
									+ document.getNumber());

							ProcessData processdata = wfprocess.getContext();
							processdata.setValue("activityOid805",
									wfProcessOid);
							processdata.setValue("primaryBusinessObject",
									document);// 设置流程主对象
							WfEngineHelper.service.startProcess(wfprocess,
									processdata, 1);
						}

					}
					logger.log("End  To Import Ojbect Type " + typename + "\n");

	            }
	        } catch (WTException wet) {
	            logger.log(wet);
	            System.out.println("DataImportHandler错误信息："+wet.getLocalizedMessage());
	            throw wet;
	        }

	        if (pe !=null &&eios.isEmpty()) {
				//批量预审单
				if("REVIEWORDER".equals(reviewType)){
					// 获取关联流程实例
					boolean isHasProcess = false;
					Enumeration enumeration = WfEngineHelper.service.getAssociatedProcesses(pe, null);
					WfProcess process = null;
					if (enumeration.hasMoreElements()) {
						process = (WfProcess) enumeration.nextElement();
						ProcessData pd = process.getContext();
						// pd.setValue("wfProcessOid", wfProcessOid);
						// pd.setValue("activityName", activityName);
						// pd.setValue("activityTemplateID", activityTemplateID);
						pd.setValue("orderIID", orderIID);
						pd.setValue("sendFrom", sendFrom);
						WfVariable mqVar = pd.getVariable("mqmessage");
						if(mqVar != null){
							pd.setValue("mqmessage", mqmsg);
						}
						PersistenceHelper.manager.save(process);
						isHasProcess = true;

					}
					if (!isHasProcess) {
						WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
								.getProcessDefinition(IXBConstants.PREVIEWWORKFLOWNAME);
						process = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
								pe.getContainerReference());
						process.setName(IXBConstants.PREVIEWWORKFLOWNAME+"_" + pe.getNumber());
						ProcessData processdata = process.getContext();
						processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
						processdata.setValue("orderIID", orderIID);
						processdata.setValue("sendFrom", sendFrom);
						processdata.setValue("mqmessage", mqmsg);

						WfEngineHelper.service.startProcess(process, processdata, 1);
					}
				}else {
					// 获取关联流程实例
					boolean isHasProcess = false;
					Enumeration enumeration = WfEngineHelper.service.getAssociatedProcesses(pe, null);
					WfProcess process = null;
					if (enumeration.hasMoreElements()) {
						process = (WfProcess) enumeration.nextElement();
						ProcessData pd = process.getContext();

						pd.setValue("wfProcessOid", wfProcessOid);
						pd.setValue("activityOid805", wfProcessOid);
						pd.setValue("activityName", activityName);
						pd.setValue("activityTemplateID", activityTemplateID);
						pd.setValue("sendFrom", sendFrom);
						pd.setValue("orderIID", orderIID);
						WfVariable mqVar = pd.getVariable("mqmessage");
						if(mqVar != null){
							pd.setValue("mqmessage", mqmsg);
						}

						PersistenceHelper.manager.save(process);
						isHasProcess = true;
					}
					if (!isHasProcess) {
						try {
							String s = TypedUtilityServiceHelper.service.getExternalTypeIdentifier((Typed) pe);
							if (s.endsWith("APPROVEFORM")) {
								if (activityName == null ||"跨域工艺会签".equals(activityName)||"工艺会签".equals(activityName)) {

									WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
											.getProcessDefinition("149签审包工艺会签流程");
									WfProcess wfprocess = null;
									wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
											pe.getContainerReference());
									wfprocess.setName("149签审包工艺会签流程_" + pe.getNumber());
									ProcessData processdata = wfprocess.getContext();
									processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
									processdata.setValue("wfProcessOid", wfProcessOid);
									processdata.setValue("activityOid805", wfProcessOid);
									processdata.setValue("activityName", activityName);
									processdata.setValue("activityTemplateID", activityTemplateID);
									processdata.setValue("sendFrom", sendFrom);
									processdata.setValue("orderIID", orderIID);
									processdata.setValue("mqmessage", mqmsg);

									WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
								}
								if ("跨域技术会签".equals(activityName)||"外部会签".equals(activityName)) {

									WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
											.getProcessDefinition("149签审包技术会签流程");
									WfProcess wfprocess = null;
									wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
											pe.getContainerReference());
									wfprocess.setName("149签审包技术会签流程_" + pe.getNumber());
									ProcessData processdata = wfprocess.getContext();
									processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
									processdata.setValue("wfProcessOid", wfProcessOid);
									processdata.setValue("activityOid805", wfProcessOid);
									processdata.setValue("activityName", activityName);
									processdata.setValue("activityTemplateID", activityTemplateID);
									processdata.setValue("sendFrom", sendFrom);
									processdata.setValue("orderIID", orderIID);
									processdata.setValue("mqmessage", mqmsg);

									WfEngineHelper.service.startProcess(wfprocess, processdata, 1);

								}
							} else {
								WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
										.getProcessDefinition("149正式发放包流程");
								WfProcess wfprocess = null;
								wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
										pe.getContainerReference());
								wfprocess.setName("149正式发放包流程_" + pe.getNumber());
								ProcessData processdata = wfprocess.getContext();
								processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
								processdata.setValue("wfProcessOid", wfProcessOid);
								processdata.setValue("activityOid805", wfProcessOid);
								processdata.setValue("activityName", activityName);
								processdata.setValue("activityTemplateID", activityTemplateID);
								processdata.setValue("sendFrom", sendFrom);
								processdata.setValue("orderIID", orderIID);
								processdata.setValue("mqmessage", mqmsg);

								WfEngineHelper.service.startProcess(wfprocess, processdata, 1);

								//轮询包中是否带有更改单信息
								//带有更改单 并且来源单位是八部的
								//根据更改单编号查询有流程的更改单查询指派工艺组长者创建更改影响分析
								Iterator jars = files.iterator();
								while(jars.hasNext()){
									String fname = (String) jars.next();
									if(fname.contains("WTChangeOrder")){
										CmExpImpObject expimp = CmExpImpPersistable.newCmExpImpPersistable(impHnd, fname);
										if (expimp == null){
											continue;
										}
										if(expimp instanceof CmExpImpChangePackaged) {
											//如果没流程没人先给倪勇军
											WTUser user = null;
											CmExpImpChangePackaged packaged = (CmExpImpChangePackaged) expimp;
											String number = packaged.getNumber();
											changePackaged = CmExpImpSearchHelper.getChangePackagedByNumber(number);
											if(changePackaged != null && sendFrom != null && (sendFrom.contains("no8") || sendFrom.contains("八部") || sendFrom.contains("NO8"))) {
												if(!Change2WorkflowHelper.isHasWorkingAnalysisActivity(changePackaged)) {
													user = UserUtil.getWTUserByName("Administrator");
													QueryResult processes = WfEngineHelper.service.getAssociatedProcesses(changePackaged, null, null);
													if(processes.hasMoreElements()) {
														WfProcess wfProcess = (WfProcess) processes.nextElement();
														Role role = Role.toRole("ZHIPAIGONGYIZUZHANGZHE");
														Team team = (Team) wfProcess.getTeamId().getObject();
														Map map = team.getRolePrincipalMap();
														List tempUserList = (List) map.get(role);
														if(tempUserList != null && tempUserList.size() > 0) {
															user = (WTUser) ((WTPrincipalReference) tempUserList.get(0)).getObject();
														}
													}
												}
											}
											if(changePackaged != null && user != null) {
												Change2WorkflowHelper.createWTChangeRequest2Auto(changePackaged, user);
											}
										}
									}
								}
							}
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

	        if (request !=null&&eios.isEmpty()) {

				WfEngineHelper.service.terminateObjectsRunningWorkflows(request);

				if (activityName == null ||"跨域工艺会签".equals(activityName) ||"工艺会签".equals(activityName)) {
					WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
							.getProcessDefinition("149变更申请包工艺会签流程");
					WfProcess wfprocess = null;
					wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
							request.getContainerReference());
					wfprocess.setName("149变更申请包工艺会签流程_" + request.getNumber());
					ProcessData processdata = wfprocess.getContext();
					processdata.setValue("primaryBusinessObject", request);// 设置流程主对象
					processdata.setValue("wfProcessOid", wfProcessOid);
					processdata.setValue("activityOid805", wfProcessOid);
					processdata.setValue("activityName", activityName);
					processdata.setValue("activityTemplateID", activityTemplateID);
					processdata.setValue("sendFrom", sendFrom);
					processdata.setValue("orderIID", orderIID);
					processdata.setValue("mqmessage", mqmsg);

					WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
				}
				if ("跨域技术会签".equals(activityName) ||"外部会签".equals(activityName)) {
					WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
							.getProcessDefinition("149变更申请包技术会签流程");
					WfProcess wfprocess = null;
					wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
							request.getContainerReference());
					wfprocess.setName("149变更申请包技术会签流程_" + request.getNumber());
					ProcessData processdata = wfprocess.getContext();
					processdata.setValue("primaryBusinessObject", request);// 设置流程主对象
					processdata.setValue("wfProcessOid", wfProcessOid);
					processdata.setValue("activityOid805", wfProcessOid);
					processdata.setValue("activityName", activityName);
					processdata.setValue("activityTemplateID", activityTemplateID);
					processdata.setValue("sendFrom", sendFrom);
					processdata.setValue("orderIID", orderIID);
					processdata.setValue("mqmessage", mqmsg);

					WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
				}
	        }

	        if (changePackaged !=null &&eios.isEmpty()) {
	            // 获取关联流程实例
	            boolean isHasProcess = false;
	            Enumeration enumeration = WfEngineHelper.service.getAssociatedProcesses(changePackaged, null);
	            WfProcess process = null;
	            if (enumeration.hasMoreElements()) {
	                process = (WfProcess) enumeration.nextElement();
	                ProcessData pd = process.getContext();
					pd.setValue("wfProcessOid", wfProcessOid);
					pd.setValue("activityOid805", wfProcessOid);
					pd.setValue("activityName", activityName);
					pd.setValue("activityTemplateID", activityTemplateID);
					pd.setValue("sendFrom", sendFrom);
					pd.setValue("orderIID", orderIID);
					WfVariable mqVar = pd.getVariable("mqmessage");
					if(mqVar != null){
						pd.setValue("mqmessage", mqmsg);
					}
	                PersistenceHelper.manager.save(process);
	                isHasProcess = true;
	            }
	            if (!isHasProcess) {
					if(Based.DC_REQUEST_DISTRIBUTE_RECEIVER.equals(msg_type)){
						WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
								.getProcessDefinition("149正式发放包流程");
						WfProcess wfprocess = null;
						wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
								changePackaged.getContainerReference());
						wfprocess.setName("149正式发放包流程_" + changePackaged.getNumber());
						ProcessData processdata = wfprocess.getContext();
						processdata.setValue("primaryBusinessObject", changePackaged);// 设置流程主对象
						processdata.setValue("wfProcessOid", wfProcessOid);
						processdata.setValue("activityOid805", wfProcessOid);
						processdata.setValue("activityName", activityName);
						processdata.setValue("activityTemplateID", activityTemplateID);
						processdata.setValue("sendFrom", sendFrom);
						processdata.setValue("orderIID", orderIID);
						processdata.setValue("mqmessage", mqmsg);

						WfEngineHelper.service.startProcess(wfprocess, processdata, 1);

					}else{
						if (activityName == null ||"跨域工艺会签".equals(activityName)||"工艺会签".equals(activityName)) {
							WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
									.getProcessDefinition("149变更签审包工艺会签流程");
							WfProcess wfprocess = null;
							wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
									changePackaged.getContainerReference());
							wfprocess.setName("149变更签审包工艺会签流程_" + changePackaged.getNumber());
							ProcessData processdata = wfprocess.getContext();
							processdata.setValue("primaryBusinessObject", changePackaged);// 设置流程主对象
							processdata.setValue("wfProcessOid", wfProcessOid);
							processdata.setValue("activityOid805", wfProcessOid);
							processdata.setValue("activityName", activityName);
							processdata.setValue("activityTemplateID", activityTemplateID);
							processdata.setValue("sendFrom", sendFrom);
							processdata.setValue("orderIID", orderIID);
							processdata.setValue("mqmessage", mqmsg);

							WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
						}
						if ("跨域技术会签".equals(activityName)||"外部会签".equals(activityName)) {

							WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
									.getProcessDefinition("149变更签审包技术会签流程");
							WfProcess wfprocess = null;
							wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
									changePackaged.getContainerReference());
							wfprocess.setName("149变更签审包技术会签流程_" + changePackaged.getNumber());
							ProcessData processdata = wfprocess.getContext();
							processdata.setValue("primaryBusinessObject", changePackaged);// 设置流程主对象
							processdata.setValue("wfProcessOid", wfProcessOid);
							processdata.setValue("activityOid805", wfProcessOid);
							processdata.setValue("activityName", activityName);
							processdata.setValue("activityTemplateID", activityTemplateID);
							processdata.setValue("sendFrom", sendFrom);
							processdata.setValue("orderIID", orderIID);
							processdata.setValue("mqmessage", mqmsg);

							WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
						}
					}

	            }
			}
	        // boolean missingObjs=false;
	      //  boolean linkImportFailed = false;
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
	            if (list.size() > 0) {
	                Collections.sort(list, new CmImportHandler.CmJarFileNameComparator());
	            }
	            for (int i = 0; i < list.size(); i++) {
	                logger.log(list.get(i));
	            }
	            Iterator ite = list.iterator();
	            while (ite.hasNext()) {
	                String fname = (String) ite.next();
	                CmExpImpLink expimp = CmExpImpLink.newCmExpImpLink(p, impHnd, fname);
	                if (expimp == null)
	                    continue;
	                try {
						expimp.setSendFrom(sendFrom);
	                    expimp.importObject();
	                } catch (MissingObjectException moe) {
	                    impHnd.removeFromProperlyReceivedObjectSet(p);
	                    // missingObjs=true;
	                } catch (WTException wte) {
	                    wte.printStackTrace();

	                    impHnd.removeFromProperlyReceivedObjectSet(p);
	                    //linkImportFailed = true;
	                }
	            }
	            if(p instanceof EPMDocument){
	            	EPMDocument epm = (EPMDocument)p;
	            	if(epm.getCADName().endsWith(".asm")){
	            		linkRepWithWTPart(epm,logger);

	            	}
	            }
	        }



	        if (preview !=null) {
	        	Hashtable table = impHnd.getNewObjects();
	        	try {
					saveDataSendRecords(preview, table,workflowType);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }


	        if (pe !=null) {
	        	Hashtable table = impHnd.getNewObjects();
	        	try {
					saveDataSendRecords(pe, table,workflowType);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }

	        if (changePackaged !=null) {
	        	Hashtable table = impHnd.getNewObjects();
	        	try {
					saveDataSendRecords(changePackaged, table,workflowType);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }

	        System.out.println("######activityOid="+activityOid+"  reviewType="+reviewType);

	        if (activityOid != null && !activityOid.trim().equals("") && !"null".equals(activityOid)) {
	            // 专为发送变更包正式数据而完成流程活动而传的参数值pass
				WorkItem workitem = CmWorkflowHelper.getWorkItemByActivityOid(activityOid);
				if (workitem != null) {
					WfActivity wfactivity = (WfActivity) workitem.getSource().getObject();
					ProcessData pd = wfactivity.getContext();
					WfVariable mqVar = pd.getVariable("mqmessage");
					if(mqVar != null){
						pd.setValue("mqmessage", mqmsg);
					}
					PersistenceHelper.manager.save(wfactivity);
				}
	            if (reviewType.equals("pass")) {
	            	//if(eios.isEmpty()){
						CmWorkflowHelper.completeActivity(activityOid, "发放", "签审通过，程序完成活动");
					//}
	            } else {
	               CmWorkflowHelper.completeActivity(activityOid, "驳回", "签审驳回，程序完成活动");
	            }
	        }else{

		        	if(Based.DC_REQUEST_DISTRIBUTE_RECEIVER.equals(msg_type)){
						//if(eios.isEmpty()) {
							CmWorkflowHelper.completeActivity(pe, changePackaged, "发放", "签审通过，程序完成活动");
						//}
		        	}else{
			            CmWorkflowHelper.completeActivity(pe,changePackaged, "驳回", "签审驳回，程序完成活动");
		        	}


	        }
	       // if (linkImportFailed)
	           // throw new WTException("Not all Linkage of object imported properly.");
		}finally{
			if(dataImportStateID!=null&&!"".equals(dataImportStateID)){
				for(ErrorImportObject eo:eios){
					String errorMsg = eo.getMessage();
					if(errorMsg==null||"".equals(errorMsg)||"null".equals(errorMsg)){
						errorMsg="检出、版本或族表导致。";
					}
					if(sb.length()<=2000){
						sb.append("数据导入失败：").append(eo.getNumber()).append(",").append(errorMsg).append(";");
					}
				}
				if(eios.isEmpty()){
					String receiveState = "数据包导入成功";
					message ="";
					updateDataReceiveRecord(dataImportStateID,reviewType,orderIID,workflowType,receiveState,sb.toString(),pboNumber,pboName);
				}else{
					String receiveState = "数据包导入失败";
					updateDataReceiveRecord(dataImportStateID,reviewType,orderIID,workflowType,receiveState,sb.toString(),pboNumber,pboName);
					/*startImportFailWorkflow(file.getPath(),   wfProcessOid,  activityTemplateID, activityName , activityOid,
				             reviewType, workflowType, sendFrom, orderIID, previewUser, dataImportStateID,sb.toString(),mqmsg);*/
					message =message+ sb.toString();
				}
			}else{
				for(ErrorImportObject eo:eios){
					String errorMsg = eo.getMessage();
					if(errorMsg==null||"".equals(errorMsg)||"null".equals(errorMsg)){
						errorMsg="检出、小版本较大或族表导致。";
					}
					if(sb.length()<=2000){
						sb.append("数据导入失败：").append(eo.getNumber()).append(",").append(errorMsg).append(";");
					}
				}
				if(!eios.isEmpty()){
					/*startImportFailWorkflow(file.getPath(),   wfProcessOid,  activityTemplateID, activityName , activityOid,
				             reviewType, workflowType, sendFrom, orderIID, previewUser, dataImportStateID,sb.toString(),mqmsg);*/
					message =message+  sb.toString();
				}
			}

		}
        return message;
    }

    /**
	 * @param epm
     * @param logger
	 */
	public static void linkRepWithWTPart(EPMDocument epm, ExpImpLogger logger) {
		try {

			 Representation representation= RepresentationHelper.service.getDefaultRepresentation((Representable) epm);
			 if(representation==null){
				QueryResult qr = PersistenceHelper.manager.navigate(epm,EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,true);
				while (qr.hasMoreElements()) {
					WTPart part = (WTPart) qr.nextElement();
				    Representation partRep = RepresentationHelper.service.getDefaultRepresentation((Representable) part);
				    if(partRep!=null){
				    	  String name = "default(来自"+part.getNumber()+"."+part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue()+".Design)";
				          RepUpdateUtils.copyDerivedImage((DerivedImage) partRep, epm, true, true, true,name, true, true);
				 		  RepresentationHelper.service.setDefaultRepresentation(epm, partRep, false);
			              logger.log("关联可视化文件：" + epm.getCADName());
				    }

				}

			 }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private static void startImportFailWorkflow(String path, String wfProcessOid, String activityTemplateID, String activityName, String activityOid, String reviewType, String workflowType,
			String sendFrom, String orderIID, String previewUser, String dataImportStateID,String message,String mqmessage)  {
    	try{
    		WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
			WTOrganization  userOrg = currentUser.getOrganization();
			WTContainerRef  userConRef = null;
			if(userOrg==null){
				userConRef = WTContainerHelper.service.getExchangeRef();
			}else{
				userConRef = userOrg.getContainerReference();
			}
			WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
					.getProcessDefinition("149数据接收失败处理流程");
			WfProcess wfprocess = WfEngineHelper.service.createProcess(
					wfprocessdefinition, null,userConRef);
			ProcessData processdata = wfprocess.getContext();
			processdata.setValue("wfProcessOid",wfProcessOid);
			processdata.setValue("activityOid",activityOid);
			processdata.setValue("activityName", activityName);
			processdata.setValue("reviewType",reviewType);
			processdata.setValue("workflowType",workflowType);
			processdata.setValue("activityTemplateID", activityTemplateID);
			processdata.setValue("sendFrom", sendFrom);
	        processdata.setValue("orderIID", orderIID);
			processdata.setValue("fileName",path);
			processdata.setValue("previewUser",previewUser);
			processdata.setValue("dataImportStateID",dataImportStateID);
			processdata.setValue("prompt",message);
			processdata.setValue("mqmessage",mqmessage);
			WfEngineHelper.service.startProcess(wfprocess,processdata, 1);
    	}catch (WTException e){
    		e.printStackTrace();
    	}

	}

	public static void updateDataReceiveRecord(String gwkey,String reviewType,String orderIID,String workflowType,String receiveState,String message, String pboNumber, String pboName){
		if(gwkey!=null &&!"".equals(gwkey)){
			String columns = "GWKEY,ORDERIID,TYPE,RECEIVESTATE,WORKFLOWTYPE,MESSAGE,pboNumber,pboName";
			String values = "'"+gwkey+"','"+orderIID+"','"+reviewType+"','"+receiveState+"','"+workflowType+"','"+message+"','"+pboNumber+"','"+pboName+"'";
			DataImportHandler.updateDataReceiveState(gwkey,orderIID, reviewType, workflowType, columns, values);
			DataImportHandler.sendImportStateToNo8(gwkey, orderIID, receiveState,message);
		}

    }

    public static void setPrinciple2Role(WfProcess wfProcess, String roleName, String userName) throws WTException {
        Role role = Role.toRole(roleName);
        Team team = (Team) wfProcess.getTeamId().getObject();
        System.out.println(userName);
        String[]  name=userName.split(";");
        for (int i = 0; i < name.length; i++) {
        	if (!name.equals("")) {
        		WTPrincipal principal = (WTPrincipal) PrincipalHelper.getPrincipal(name[i]).getObject();
                team.addPrincipal(role, principal);
        	}
        }
        team = (Team) PersistenceHelper.manager.refresh(team);
        team = (Team) PersistenceHelper.manager.save(team);
    }

    /**
	 * 数据发放管理模块，当往149厂发放数据成功时，把包及包下面的数据写到数据库表中
	 *
	 * @param primaryBusinessObject
	 * @throws WTException
	 * @throws SQLException
	 */
	public static void saveDataSendRecords(WTObject primaryBusinessObject,Hashtable table,String workflowType) throws WTException, SQLException {
		ReferenceFactory rf = new ReferenceFactory();
		Set<Persistable> set = new HashSet<Persistable>();
		String pOid =null;
		String pNumber = null;
		String pName = null;
		if (primaryBusinessObject instanceof ProcessEnvelope) {
			ProcessEnvelope pe = (ProcessEnvelope) primaryBusinessObject;
			pOid = rf.getReferenceString(pe);
			pNumber = pe.getNumber();
			pName = pe.getName();
			QueryResult qr = PersistenceHelper.manager.navigate(pe,
					"theRevisionControlled", EnvelopeMemberLink.class, false);

			while (qr.hasMoreElements()) {
				EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
				set.add(link.getRevisionControlled());
			}
		} else if (primaryBusinessObject instanceof ChangePackaged) {
			ChangePackaged change = (ChangePackaged) primaryBusinessObject;
			pOid = rf.getReferenceString(change);
			pNumber = change.getNumber();
			pName = change.getName();
			QueryResult qr = PersistenceHelper.manager.navigate(change,
					ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
					ChangePackagedResultLink.class, true);
			while (qr.hasMoreElements()) {
				set.add((Persistable) qr.nextElement());
			}
		} else if (primaryBusinessObject instanceof WTDocument) {
			WTDocument document = (WTDocument) primaryBusinessObject;
			pOid = rf.getReferenceString(document);
			pNumber = document.getNumber();
			pName = document.getName();
			set.add(document);
		} else if (primaryBusinessObject instanceof Preview) {
			Preview preview = (Preview) primaryBusinessObject;
			pOid = rf.getReferenceString(preview);
			pNumber = preview.getNumber();
			pName = preview.getName();
			set.addAll(PreviewUtil.getAllMembers(preview));
		}

		DBConn conn = null;
		try {
			conn = new DBConn();

			Enumeration enumeration = table.elements();
			while (enumeration.hasMoreElements()) {
				Persistable persistable = (Persistable) enumeration.nextElement();
				if (!persistable.equals(primaryBusinessObject)) {

					Date date = new Date(System.currentTimeMillis());
					String underReview = "否";
					StringBuffer sb2 = new StringBuffer();
					String oNumber = ObjectProperty.getNumber(persistable);
					String oName = ObjectProperty.getName(persistable);
					String oVersion = ObjectProperty.getVersionIterationDisplay(persistable);
					String oOid = persistable.getPersistInfo().getObjectIdentifier().getStringValue();
					String pIndex = IBAHelper.getIBAStringValue((WTObject) persistable, "PINDEX");

					String containerOID = "";
					String containerName = "";
					if (persistable instanceof WTContained) {
						WTContained con = (WTContained) persistable;
						containerName = con.getContainerName();
						containerOID = con.getContainer().getPersistInfo().getObjectIdentifier().getStringValue();
					}
					if (set.contains(persistable)) {
						underReview = "是";
						set.remove(persistable);
					}
					sb2.append("insert into ASES_DATA_SEND_TABLE ");
					if (pIndex == null) {
						pIndex = "";
					}
					sb2.append("(PACKAGE_NUM,PACKAGE_NAME,PACKAGE_OID,OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,PRODUCT_CODE,SEND_TYPE,UNDERREVIEW,SEND_DATE,containerOID,containerName) ");
					sb2.append("values('" + pNumber + "','" + pName + "','" + pOid + "','" + oNumber + "','" + oName
							+ "','" + oVersion + "','" + oOid + "','" + pIndex + "','" + workflowType + "','" + underReview + "'," + "date '" + date + "','" + containerOID + "','" + containerName + "')");

					conn.executeUpdate(sb2.toString());
					conn.commit();

				}

			}

			Iterator<Persistable> iterator = set.iterator();
			while (iterator.hasNext()) {
				Persistable persistable = iterator.next();
				Date date = new Date(System.currentTimeMillis());
				String underReview = "是";
				StringBuffer sb2 = new StringBuffer();
				String oNumber = ObjectProperty.getNumber(persistable);
				String oName = ObjectProperty.getName(persistable);
				String oVersion = ObjectProperty.getVersionIterationDisplay(persistable);
				String oOid = persistable.getPersistInfo().getObjectIdentifier().getStringValue();
				String pIndex = IBAHelper.getIBAStringValue((WTObject) persistable, "PINDEX");
				sb2.append("insert into ASES_DATA_SEND_TABLE ");
				if (pIndex == null) {
					pIndex = "";
				}
				String containerOID = "";
				String containerName = "";
				if (persistable instanceof WTContained) {
					WTContained con = (WTContained) persistable;
					containerName = con.getContainerName();
					containerOID = WCUtil.getOid(con.getContainer());
				}
				sb2.append("(PACKAGE_NUM,PACKAGE_NAME,PACKAGE_OID,OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,PRODUCT_CODE,SEND_TYPE,UNDERREVIEW,SEND_DATE,containerOID,containerName) ");
				sb2.append("values('" + pNumber + "','" + pName + "','" + pOid + "','" + oNumber + "','" + oName
						+ "','" + oVersion + "','" + oOid + "','" + pIndex + "','" + workflowType + "','" + underReview + "'," + "date '" + date + "','" + containerOID + "','" + containerName + "')");

				conn.executeUpdate(sb2.toString());
				conn.commit();
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			conn.close();
		}
	}

	public static String processZYKReceivedData(String fileName,String sendFrom)
			throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "processZYKReceivedData";
			Class[] types = { String.class ,String.class};
			Object[] vals = { fileName ,sendFrom};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				return (String) rms.invoke(method,
						DataImportHandler.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("processZYKReceivedData2");
		String message = "";
		File file = new File(fileName);
		if (!file.exists()) {
			return "数据包异常删除";
		}
		List errorObjs = new ArrayList();
		try {
			CmImportHandler impHnd = new CmImportHandler(file);
			ExpImpLogger logger = ExpImpLogger.getInstance();
			List objs = new ArrayList();
			try {
				ArrayList list = impHnd.getAllTopObjectXmlFileInJar();
				logger.log("All Ojbect Size Is: " + list.size());
				Iterator it = list.iterator();
				while (it.hasNext()) {
					String fname = (String) it.next();
					CmExpImpObject expimp = CmExpImpPersistable.newCmExpImpZykPersistable(impHnd, fname);
					if (expimp == null) {
						continue;
					}
					expimp.setSendFrom(sendFrom);
					String typename = fname.substring(fname.indexOf("TAG-") + 4);
					if (typename.indexOf("-") > 0)
						typename = typename.substring(0, typename.indexOf("-"));
					else
						typename = typename.substring(0, typename.indexOf(".xml"));
					logger.log("Start To Import Ojbect Type " + typename);
					try {
						Object object = expimp.importObject();

						if (object != null) {
							if (object instanceof ErrorImportObject) {
								ErrorImportObject eo = (ErrorImportObject) object;
								eo.setFileName(fileName);
								logger.log("ERROR:数据导入失败：" + eo.getNumber());
								errorObjs.add(eo);

							} else if (object instanceof WTPart) {
								SjzykSchedule.updateSjzykMiddleTable((WTPart) object);
								SjzykSchedule.updateTechnicMaterialInfo((WTPart) object);

							}
						}
					} catch (WTException wet) {
						logger.log(wet);
					}

				}
			} catch (WTException wet) {
				logger.log(wet);
			}

			//boolean linkImportFailed = false;
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
				if (list.size() > 0) {
					Collections.sort(list, new CmImportHandler.CmJarFileNameComparator());
				}
				for (int i = 0; i < list.size(); i++) {
					logger.log(list.get(i));
				}
				Iterator ite = list.iterator();
				while (ite.hasNext()) {
					String fname = (String) ite.next();
					CmExpImpLink expimp = CmExpImpLink.newCmExpImpLink(p, impHnd, fname);
					if (expimp == null)
						continue;
					try {
						expimp.setSendFrom(sendFrom);
						expimp.importObject();
					} catch (MissingObjectException moe) {
						impHnd.removeFromProperlyReceivedObjectSet(p);
						// missingObjs=true;
					} catch (WTException wte) {
						wte.printStackTrace();

						impHnd.removeFromProperlyReceivedObjectSet(p);
						//linkImportFailed = true;
					}
				}
			}


			//if (linkImportFailed)
				//throw new WTException("Not all Linkage of object imported properly.");
		} finally {
			try {
				sendErrorObject(errorObjs, fileName);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// 返回成功信息
		}

		return message;
	}

	private static String sendErrorObject(List<ErrorImportObject> errorObjs,String fileName) throws SOAPException, MalformedURLException {
		//如果errorObjs里面没数据则返回成功信息
		URL url = new URL("http://10.112.1.93/Windchill/servlet/RPC");
		SOAPHTTPConnection st = new SOAPHTTPConnection();
		st.setUserName("wcadmin");
		st.setPassword("Xht123456");
		StringBuilder sb = new StringBuilder("");
		boolean isSuccess = false;

		if(errorObjs.isEmpty()){
			isSuccess = true;
		}else{
			isSuccess = false;
			sb.append("<ErrorImportObjects>");
			for(ErrorImportObject eo:errorObjs){
				sb.append("<ErrorImportObject>");
					sb.append("<number>");
					sb.append(eo.getNumber());
					sb.append("</number>");
					sb.append("<name>");
					sb.append(eo.getName());
					sb.append("</name>");
					sb.append("<cadName>");
					sb.append(eo.getCadName());
					sb.append("</cadName>");
					sb.append("<fileName>");
					sb.append(eo.getFileName());
					sb.append("</fileName>");
					sb.append("<message>");
					sb.append(eo.getMessage());
					sb.append("</message>");

					sb.append("<type>");
					sb.append(eo.getType());
					sb.append("</type>");

					sb.append("<version>");
					sb.append(eo.getVersion());
					sb.append("</version>");

					sb.append("<xmlName>");
					sb.append(eo.getXmlName());
					sb.append("</xmlName>");


				sb.append("</ErrorImportObject>");
			}
			sb.append("</ErrorImportObjects>");
		}
		System.out.println("ErrorImportObjectsXML:"+sb.toString());

		Vector params = new Vector();
		params.addElement(new Parameter("success", String.class, isSuccess, null));
		params.addElement(new Parameter("message", String.class, sb.toString(), null));
		params.addElement(new Parameter("fileName", String.class, fileName, null));
		params.addElement(new Parameter("sendFrom", String.class, "149", null));

		Call call = new Call();
		call.setSOAPTransport(st);
		call.setTargetObjectURI("urn:ie-soap-rpc:com.infoengine.soap");
		call.setMethodName("feedBack");
		call.setEncodingStyleURI("http://schemas.xmlsoap.org/soap/encoding/");
		call.setParams(params);
		Response resp = call.invoke(url, "urn:ie-soap-rpc:com.infoengine.soap!" + "feedBack");
		if (resp.generatedFault()) {
			org.apache.soap.Fault fault = resp.getFault();
			return fault.getFaultString();
		} else {
			Parameter ret = resp.getReturnValue();
			Object result = ret.getValue();
			String backStr = result.toString();
			return backStr;
		}

	}
	public static String getDataImportState(String orderIID, String type) {
		System.out.println("-----orderIID:"+orderIID + ",type:" + type);
		StringBuffer sb = new StringBuffer();
		String receiveState = "";
		String number = "";
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "select RECEIVESTATE,ENVELOPENUBMER from DATAIMPORTSTATERECORD where ORDERIID='"
					+ orderIID + "' and TYPE='" + type + "'";
			System.out.println(sql);
			ResultSet rs = conn.executeQuery(sql);

			if (rs.next()) {
				receiveState = rs.getString("RECEIVESTATE");
				number = rs.getString("ENVELOPENUBMER");
				sb.append("state=" + receiveState);
				System.out.println(number);
				if(number != null && !"".equals(number) && "数据包导入成功".equals(receiveState)){
					if("0".equals(type) || "2".equals(type)){
						QueryResult qrPe = ProcessEnvelopeUtil.getProcessEnvelopeByNumber(number);
						if(qrPe.hasMoreElements()){
							System.out.println("==========hasPE===========");
							ProcessEnvelope pe = (ProcessEnvelope) qrPe.nextElement();
							List<String> messages = getProcessStateInfo(pe);
							for(String str : messages){
								sb.append("|message=" + str);

							}
						}
					}
					if("1".equals(type)){
						QueryResult qrCp = ChangePackagedUtil.getChangePackagedByNumber(number);
						if(qrCp.hasMoreElements()){
							System.out.println("==========hasCP===========");
							ChangePackaged cp = (ChangePackaged) qrCp.nextElement();
							List<String> messages = getProcessStateInfo(cp);
							for(String str : messages){
								sb.append("|message=" + str);
							}
						}
					}
				}
			}else{
				sb.append("state=未收到数据包");
				return sb.toString();
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	/**
	 * 获取流程当前活动环节 add by liangbo
	 * @param pbo
	 * @return
	 */
	public static List<String> getProcessStateInfo(WTObject pbo){
		String message = "";
		List<String> activityStr = new ArrayList<String>();
		QueryResult qrProcs;
		try {
			qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
			WfProcess proc = null;
			while (qrProcs.hasMoreElements()) {
				WfProcess process = (WfProcess) qrProcs.nextElement();
				if(proc!=null){
					if(process.getStartTime().after(proc.getStartTime())){
						proc = process;
					}
				}else{
					proc = process;
				}
			}
			if(proc==null){
				return null;
			}
			List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
			activityList = getActivities(proc, activityList);
			Iterator iterator = activityList.iterator();
			while(iterator.hasNext()){
				int k = 0;
				WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
				String activityName = wfactivity.getName();
				System.out.println("==>>activityName"+activityName);
				Enumeration en1 = null;

				Enumeration en2 = null;

				en1 = ((WfAssignedActivity) wfactivity).getAssignments();

				String userName = "";

				for (int i = 0; en1 != null && en1.hasMoreElements(); i++) {
					WfAssignment wfassignment = (WfAssignment) en1
							.nextElement();

					en2 = wfassignment.checkBallotStatus().elements();

					for (int j = 0; en2 != null && en2.hasMoreElements(); j++) {
						WfBallot wfballot = (WfBallot) en2.nextElement();

						WTPrincipal wtp = wfballot.getVoter().getPrincipal();
						if(wtp instanceof WTUser){
							if(k == 0){
								userName = ((WTUser) wtp).getFullName().toString();
							}else{
								userName = userName + "," + ((WTUser) wtp).getFullName().toString();
							}
						}
						k++;
					}
				}
				message = activityName + "(" + userName + ")";
				activityStr.add(message);
			}
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return activityStr;
	}

	public static List getActivities(WfProcess wfprocess, List activityList) throws WTException {

        Enumeration enumeration = WfEngineHelper.service.getProcessSteps(wfprocess, null);

        while (enumeration.hasMoreElements()) {

            WfActivity wfactivity = (WfActivity) enumeration.nextElement();

            if (wfactivity instanceof WfAssignedActivity) {

                WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
                String state = wfassignedactivity.getState().getDisplay();
                System.out.println("state=======" + state);
                if("正在运行".equals(state)){
                	activityList.add(wfassignedactivity);
                }

            }

        }

        return activityList;

    }
	public static void updateDataReceiveState(String gwkey,String orderIID, String type, String workflowType, String columns,String values) {
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String deleteSql = "delete from DATAIMPORTSTATERECORD where gwkey='"+gwkey+"' ";
			conn.executeUpdate(deleteSql);
			String sql = "insert into DATAIMPORTSTATERECORD ("+columns+") values ("+values+")";
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendImportStateToNo8(String dataImportStateID, String orderIID, String receiveState,String message) {
		SoapCall sc = new SoapCall();
		HashMap inputparams = new HashMap();
		if(message.length()>300){
			message = message.substring(0,290);
		}
		inputparams.put("dataImportStateID", dataImportStateID);
		inputparams.put("orderIID", orderIID);
		inputparams.put("receiveState", receiveState);
		inputparams.put("message", message);
		inputparams.put("sendTo", "No8");
		try {
			String result = sc.setNo8ImportState("updateRecordState",inputparams);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SOAPException e) {
			e.printStackTrace();
		}
	}
}
