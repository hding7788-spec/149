package ext.casc.doc;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.constants.Constants;
import ext.casc.fileprint.FilePrintUtil;
import ext.casc.workflow.WorkflowHelper;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.projmgmt.admin.Project2;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.workflow.engine.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @describe used to submit approval workFlow process
 * @author Long,XiuChuan
 * @since 2012/7/23
 *
 */
public class QuickReCycleProcessor extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        System.out.println("actionObj" + actionObj);
        String wf = Constants.WF_START_ERROR;
        try {
            if (actionObj instanceof WTDocument) {//文档提交签审
                WTDocument doc = (WTDocument) actionObj;
                WorkflowHelper.setObjectLifeCycle(doc, "INWORK");
                //清空电子签名相关附件
                FilePrintUtil.removePrintRelatedAttachement(doc);

				QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(doc, null, null);
				while (qrProcs.hasMoreElements()) {
					WfProcess proc = (WfProcess) qrProcs.nextElement();
					WfEngineHelper.service.changeState(proc, WfTransition.TERMINATE);
				}

				//重新启动流程
				HashMap<String, WTDocument> data1 = new HashMap<String, WTDocument>();
				wf = submitProcess(doc, data1);

            }else if (actionObj instanceof WTChangeOrder2) {//文档提交签审
            	WTChangeOrder2 ecn = (WTChangeOrder2) actionObj;
                WorkflowHelper.setObjectLifeCycle(ecn, "REWORK");
                WorkflowHelper.setObjectState(ecn, "REWORK");
                //清空电子签名相关附件
                FilePrintUtil.removePrintRelatedAttachement(ecn);
                String type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(ecn);
                String templateName = "";
                if( type.contains("casc.sast.149.PROCESS_ECN")|| type.contains("casc.sast.149.Process_reportTechnics_ECN") ){
                     templateName = "工艺更改单签审流程";
                }else{
                     templateName = "文档更改单签审流程";
                }

				QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(ecn, WfState.OPEN_RUNNING , null);
				while (qrProcs.hasMoreElements()) {
					WfProcess proc = (WfProcess) qrProcs.nextElement();
                    //templateName = proc.getTemplate().getName();
					WfEngineHelper.service.changeState(proc, WfTransition.TERMINATE);
				}

				//重新启动流程
				HashMap<String, WTDocument> data1 = new HashMap<String, WTDocument>();
				wf = submitProcess(ecn, data1,templateName);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage(wf);
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }
    public static String  submitProcess(WTChangeOrder2 ecn, HashMap<String, WTDocument>  data1 ,String templateName) throws Exception{
        String wf = "";
        WTContainer container = ecn.getContainer();
        initiateWfProcess(templateName, data1, container, ecn);
        wf = templateName + Constants.WF_START_MSG;
        return wf;
    }
    public static String  submitProcess(WTDocument doc, HashMap<String, WTDocument>  data1 ) throws Exception{
    	WTContainer container = doc.getContainer();
        String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
        String wf = "";
        System.out.println("----------docType:" + docType);
        if (docType.indexOf("casc.sast.149.QA_REPORT") != -1) {// 质量报告签审流程
            initiateWfProcess(Constants.WF_ZILIANGBAOGAO_APPROVAL, data1, container, doc);
            wf = Constants.WF_ZILIANGBAOGAO_APPROVAL + Constants.WF_START_MSG;
        } else  if (docType.indexOf("casc.sast.149.PROCESS_NOTICE") != -1) {// 工艺通知单签审流程
            initiateWfProcess( Constants.WFN_PROCESS_DOCUMENT, data1, container, doc);
            wf = Constants.WFN_PROCESS_DOCUMENT + Constants.WF_START_MSG;
        } else if(docType.indexOf("casc.sast.149.TECHNOLOGY_AGREEMENT") != -1){// 工艺文件签审流程
//            initiateWfProcess("技术协议签审流程", data1, container, doc);
//            wf = "技术协议签审流程" + Constants.WF_START_MSG;
            initiateWfProcess("外协技术协议签审流程", data1, container, doc);
            wf = "外协技术协议签审流程" + Constants.WF_START_MSG;
        }else if (docType.indexOf("casc.sast.149.GONGYIZONGFANGAN") != -1||docType.indexOf("casc.sast.149.GONGYIFENFANGAN") != -1) {
        	 initiateWfProcess("工艺方案签审流程", data1, container, doc);
             wf = "工艺方案签审流程" + Constants.WF_START_MSG;
		}else if (docType.indexOf("casc.sast.149.QITALEIWENDANG") != -1) {
			initiateWfProcess("其他类文档签审流程", data1, container, doc);
            wf = "其他类文档签审流程" + Constants.WF_START_MSG;
		}else if (docType.indexOf("casc.sast.149.GONGZHUANGSHENQINGDAN") != -1) {
		    initiateWfProcess("工装申请单签审流程", data1, container, doc);
            wf = "工装申请单签审流程" + Constants.WF_START_MSG;
        }else {// 其他文档签审流程
        	          initiateWfProcess(Constants.WF_DOCUMENT_APPROVAL, data1, container, doc);
            wf = Constants.WF_DOCUMENT_APPROVAL + Constants.WF_START_MSG;
        }
        return wf;
    }
    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainer container,
            WTObject persistable) throws Exception {
    	      return initiateWfProcess(templateName, data, WTContainerRef.newWTContainerRef(container), persistable);
    }

    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainerRef conref,
            WTObject persistable) throws Exception {
        try {
            wt.workflow.definer.WfProcessDefinition wfProcessDef = wt.workflow.definer.WfDefinerHelper.service
                    .getProcessDefinition(templateName, conref);
            if (wfProcessDef == null) {
                System.out.println("the woflowtemplate named " + templateName + "doesn't exist");
                return null;
            }
            WTContainer container = conref.getReferencedContainer();
            wt.inf.team.ContainerTeam team = null;
            if (container instanceof PDMLinkProduct) {
                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((PDMLinkProduct) container);
            } else if (container instanceof WTLibrary) {
                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((WTLibrary) container);
            } else if (container instanceof Project2) {
                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((Project2) container);
            }
            wt.workflow.engine.WfProcess wfProcess = wt.workflow.engine.WfEngineHelper.service.createProcess(
                    wfProcessDef, team, conref);
            wfProcess.setName(templateName + "_" + System.currentTimeMillis());

            //String user = wt.auth.Authentication.getUserName();
           // wt.org.WTUser wtuser = wt.org.OrganizationServicesHelper.manager.getAuthenticatedUser(user);
            WTUser wtuser = null;
            if(persistable instanceof WTDocument){
            	WTDocument doc = (WTDocument)persistable;
            	wtuser =(WTUser) doc.getCreator().getObject();
            }else if (persistable instanceof WTChangeOrder2){
            	WTChangeOrder2 doc = (WTChangeOrder2)persistable;
            	wtuser =(WTUser) doc.getCreator().getObject();
            }

            wt.org.WTPrincipalReference ref = wt.org.WTPrincipalReference.newWTPrincipalReference(wtuser);
            wfProcess.setCreator(ref);
            wfProcess = WfEngineServerHelper.service.setPrimaryBusinessObject(wfProcess, persistable);
            wt.workflow.engine.ProcessData pData = wfProcess.getContext();
            Iterator keys = data.keySet().iterator();
            while (keys.hasNext()) {
                String paramName = (String) keys.next();
                Object paramValue = data.get(paramName);
                pData.setValue(paramName, paramValue);
            }
            wfProcess = wfProcess.start(pData, 0, true);
            return wfProcess;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

}
