package ext.casc.dfmRule.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.WTObject;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.pdmlink.PDMLinkProduct;
import wt.projmgmt.admin.Project2;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfProcess;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.envelope.ProcessEnvelope;
import ext.casc.constants.Constants;

/**

 * @author xuetao

 *
 */
public class GZBZuofeiProcessor extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        HashMap<String, WTDocument> data1 = new HashMap<String, WTDocument>();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        System.out.println("actionObj" + actionObj);
        String wf = Constants.WF_START_ERROR;
        try {
            if (actionObj instanceof WTDocument) {//文档提交签审
                WTDocument doc = (WTDocument) actionObj;
                WTContainer container = doc.getContainer();
                String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
              if(docType.indexOf("casc.sast.149.GYRULEFILE") != -1){// 工艺规则包签审流程
                    initiateWfProcess("工艺规则包作废流程", data1, container, doc);
                    wf = "工艺规则包作废流程" + Constants.WF_START_MSG;
                }

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
            String user = wt.auth.Authentication.getUserName();
            wt.org.WTUser wtuser = wt.org.OrganizationServicesHelper.manager.getAuthenticatedUser(user);
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
