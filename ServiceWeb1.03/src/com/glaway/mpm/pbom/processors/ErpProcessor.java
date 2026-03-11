package com.glaway.mpm.pbom.processors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import wt.auth.Authentication;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.httpgw.URLFactory;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeamHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.projmgmt.admin.Project2;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.util.WTException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfProcess;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class ErpProcessor extends DefaultObjectFormProcessor {
    private final static String PBOM_FLOW_NAME = "PBOM构建流程";
    private final static String PBOM_FLOW_MSG = "已经启动！";

    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(clientData, objectBeans);
        boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            Object actionObj = clientData.getActionOid().getRefObject();
            if (actionObj instanceof WTPart) {
                HashMap<String, WTPart> data1 = new HashMap<String, WTPart>();
                WTPart part = (WTPart) actionObj;
                WTContainer container = clientData.getContainer();

                WfProcess process = initiateWfProcess(PBOM_FLOW_NAME, data1, container, part);
                
                //设置当前用户到流程团队的主任工艺师角色
                Team team = (Team) process.getTeamId().getObject();
                WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
                Role role = Role.toRole("ZHURENGONGYISHI");
                team.addPrincipal(role, currentUser);
                team = (Team) PersistenceHelper.manager.refresh(team);
                team = (Team) PersistenceHelper.manager.save(team);

                FeedbackMessage message = new FeedbackMessage();
                URLFactory urlFactory = new URLFactory();
                String url = urlFactory.getBaseHREF();
                message.addMessage(PBOM_FLOW_NAME+PBOM_FLOW_MSG);
                formresult.addFeedbackMessage(message);
                formresult.setStatus(FormProcessingStatus.SUCCESS);
                formresult.setURL(url + "app/#ptc1/homepage");
                formresult.setNextAction(FormResultAction.FORWARD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(bool);
        }

        return formresult;
    }

    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainer container,
            WTObject persistable) throws Exception {
        return initiateWfProcess(templateName, data, WTContainerRef.newWTContainerRef(container), persistable);
    }

    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainerRef conref, WTObject persistable) throws Exception {
        try {
            WfProcessDefinition wfProcessDef = WfDefinerHelper.service.getProcessDefinition(templateName, conref);
            if (wfProcessDef == null) {
                return null;
            }
            WTContainer container = conref.getReferencedContainer();
            wt.inf.team.ContainerTeam team = null;
            if (container instanceof PDMLinkProduct) {
                team = ContainerTeamHelper.service.getContainerTeam((PDMLinkProduct) container);
            } else if (container instanceof WTLibrary) {
                team = ContainerTeamHelper.service.getContainerTeam((WTLibrary) container);
            } else if (container instanceof Project2) {
                team = ContainerTeamHelper.service.getContainerTeam((Project2) container);
            }
            WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDef, team, conref);
            wfProcess.setName(templateName + "_" + System.currentTimeMillis());
            String user = Authentication.getUserName();
            WTUser wtuser = OrganizationServicesHelper.manager.getAuthenticatedUser(user);
            WTPrincipalReference ref = WTPrincipalReference.newWTPrincipalReference(wtuser);
            wfProcess.setCreator(ref);
            wfProcess = WfEngineServerHelper.service.setPrimaryBusinessObject(wfProcess, persistable);
            ProcessData pData = wfProcess.getContext();
            Iterator keys = data.keySet().iterator();
            while (keys.hasNext()) {
                String paramName = (String) keys.next();
                Object paramValue = data.get(paramName);
                pData.setValue(paramName, paramValue);
            }
            wfProcess = wfProcess.start(pData, true, conref);
            return wfProcess;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
