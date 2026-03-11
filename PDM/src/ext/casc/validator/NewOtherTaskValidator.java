package ext.casc.validator;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeConstants;
import ext.casc.access.AccessAdminUtil;
import ext.casc.analysisActivity.bean.AnalysisToSourceLink;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.changeRequest.Change2WorkflowHelper;
import ext.casc.constants.Constants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import ext.casc.workflow.PrintHelper;
import wt.change2.ChangeRequest2;
import wt.change2.WTAnalysisActivity;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.folder.Cabinet;
import wt.folder.SubFolder;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewOtherTaskValidator extends DefaultSimpleValidationFilter {

    private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.GLAWAY_149_CONFIG_PATH);

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        try {
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            if(key.getComponentID().equals("newOtherTask")) {
                WTContainer wtContainer = null;
                if(object instanceof Cabinet) {
                    Cabinet cab = (Cabinet) object;
                    wtContainer = cab.getContainer();
                } else if(object instanceof SubFolder) {
                    SubFolder folder = (SubFolder) object;
                    wtContainer = folder.getContainer();
                }
                if(wtContainer == null) {
                    return UIValidationStatus.NOT_VALIDATED;
                }

                ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
                Role role = Role.toRole("PRODUCT MANAGER");
                if(role == null) {
                    return UIValidationStatus.HIDDEN;
                }
                ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                for(WTPrincipalReference reference : arrayList) {
                    Object object2 = reference.getPrincipal();
                    if(object2 instanceof WTUser) {
                        WTUser user = (WTUser) object2;
                        if(user.getName().equals(currentUser.getName())) {
                            return UIValidationStatus.ENABLED;
                        }
                    }
                }

                role = Role.toRole("ZHURENGONGYISHI");
                if(role == null) {
                    return UIValidationStatus.HIDDEN;
                }
                arrayList = containerTeam.getAllPrincipalsForTarget(role);
                for(WTPrincipalReference reference : arrayList) {
                    Object object2 = reference.getPrincipal();
                    if(object2 instanceof WTUser) {
                        WTUser user = (WTUser) object2;
                        if(user.getName().equals(currentUser.getName())) {
                            return UIValidationStatus.ENABLED;
                        }
                    }
                }
                if(currentUser.getName().equals("Administrator") || AccessAdminUtil.isAdmin() || AccessAdminUtil.isSysAdmin()) {
                    return UIValidationStatus.ENABLED;
                }
            } else if(key.getComponentID().equals("hideAllMindex")) {
                if(currentUser.getName().equals("Administrator") || AccessAdminUtil.isAdmin() || AccessAdminUtil.isSysAdmin()) {
                    return UIValidationStatus.ENABLED;
                }
                if(currentUser.getName().equals("niyongjun")) {
                    return UIValidationStatus.ENABLED;
                }
                return UIValidationStatus.HIDDEN;
            } else if(key.getComponentID().equals("refreshBatches")) {
                WTContainer wtContainer = null;
                if(object instanceof WTPart) {
                    WTPart part = (WTPart) object;
                    wtContainer = part.getContainer();
                }
                if(wtContainer == null) {
                    return UIValidationStatus.NOT_VALIDATED;
                }
                ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
                Role role = Role.toRole("ZHURENGONGYISHI");
                ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                for(WTPrincipalReference reference : arrayList) {
                    Object object2 = reference.getPrincipal();
                    if(object2 instanceof WTUser) {
                        WTUser user = (WTUser) object2;
                        if(user.getName().equals(currentUser.getName())) {
                            return UIValidationStatus.ENABLED;
                        }
                    } else if(object2 instanceof WTGroup) {
                        WTGroup group = (WTGroup) object2;
                        if(group.isMember(currentUser)) {
                            return UIValidationStatus.ENABLED;
                        }
                    }
                }
                if(currentUser.getName().equals("Administrator") || AccessAdminUtil.isAdmin() || AccessAdminUtil.isSysAdmin()) {
                    return UIValidationStatus.ENABLED;
                }
                return UIValidationStatus.HIDDEN;
            } else if(key.getComponentID().equals("createPhotoTemplate") || key.getComponentID().equals("importPhotoTemplate")) {
                if(object instanceof WTLibrary || object instanceof SubFolder || object instanceof Cabinet) {
                    String containerName = "";
                    WTContainer library = null;
                    if(object instanceof WTLibrary) {
                        library = (WTLibrary) object;
                        containerName = library.getName();
                    } else if(object instanceof SubFolder) {
                        SubFolder sub = (SubFolder) object;
                        library = sub.getContainer();
                        containerName = sub.getContainerName();
                    } else if(object instanceof Cabinet) {
                        Cabinet cabinet = (Cabinet) object;
                        library = cabinet.getContainer();
                        containerName = cabinet.getContainerName();
                    }
                    if(!(library instanceof WTLibrary)) {
                        return UIValidationStatus.HIDDEN;
                    }
                    if(SopConstants.SOP_CONTAINER_GYZYK.equals(containerName)) {
                        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) library);
                        Role role = Role.toRole("BIAOZHUNHUASHI");
                        if(role == null) {
                            return UIValidationStatus.DISABLED;
                        }
                        ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                        for(WTPrincipalReference reference : arrayList) {
                            Object object2 = reference.getPrincipal();
                            if(object2 instanceof WTUser) {
                                WTUser user = (WTUser) object2;
                                if(user.getName().equals(currentUser.getName())) {
                                    return UIValidationStatus.ENABLED;
                                }
                            } else if(object2 instanceof WTGroup) {
                                WTGroup group = (WTGroup) object2;
                                if(group.isMember(currentUser)) {
                                    return UIValidationStatus.ENABLED;
                                }
                            }
                        }
                    } else {
                        return UIValidationStatus.HIDDEN;
                    }
                }
                return UIValidationStatus.HIDDEN;
            } else if(key.getComponentID().equals("createAnalysisActivity")) {
                if(object instanceof ChangePackaged || object instanceof ChangeRequest2 || object instanceof ProcessEnvelope) {
                    String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(object).toString();
                    if(object instanceof ProcessEnvelope) {
                        if(objectType.indexOf(ProcessEnvelopeConstants.APPROVEFORM) < 0 && objectType.indexOf(ProcessEnvelopeConstants.RELEASEFORM) < 0) {
                            return UIValidationStatus.HIDDEN;
                        }
                    }
                    WTContainer container = ((WTContained) object).getContainer();
                    if(ProcessUtil.isZhuRenGongyiShi(container) || WCUtil.isAdmin()) {
                        boolean isHas = Change2WorkflowHelper.isHasWorkingAnalysisActivity((WTObject) object);
                        if(isHas) {
                            return UIValidationStatus.DISABLED;
                        }
                        return UIValidationStatus.ENABLED;
                    }
                } else if(object instanceof WorkItem) {
                    WorkItem workItem = (WorkItem) object;
                    Persistable persistable = null;
                    try {
                        persistable = workItem.getPrimaryBusinessObject().getObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(persistable != null && persistable instanceof ChangePackaged || persistable instanceof ChangeRequest2 || persistable instanceof ProcessEnvelope) {
                        WTContainer container = ((WTContained) persistable).getContainer();
                        if(ProcessUtil.isZhuRenGongyiShi(container)) {
                            boolean isHas = Change2WorkflowHelper.isHasWorkingAnalysisActivity((WTObject) persistable);
                            if(isHas) {
                                return UIValidationStatus.HIDDEN;
                            }
                            return UIValidationStatus.ENABLED;
                        }
                    }
                }
                return UIValidationStatus.HIDDEN;
            } else if(key.getComponentID().equals("replaceAnalysis")) {
                WTAnalysisActivity activity = null;
                if(object instanceof WorkItem) {
                    WorkItem workItem = (WorkItem) object;
                    activity = (WTAnalysisActivity) workItem.getPrimaryBusinessObject().getObject();
                } else if(object instanceof WTAnalysisActivity) {
                    activity = (WTAnalysisActivity) object;
                }
                if(activity != null) {
                    QueryResult links = PersistenceHelper.manager.navigate(activity, "sourceObject", AnalysisToSourceLink.class, false);
                    if(links.size() > 0) {
                        QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(activity, null, null);
                        if(qr.hasMoreElements()) {
                            WfProcess process = (WfProcess) qr.nextElement();
                            List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
                            activityList = PrintHelper.getActivities(process, activityList);
                            Iterator iterator = activityList.iterator();
                            while(iterator.hasNext()) {
                                WfAssignedActivity assignedActivity = (WfAssignedActivity) iterator.next();
                                String wfactivityName = assignedActivity.getName();
                                if(wfactivityName.equals("更改影响分析") && "OPEN_RUNNING".equals(assignedActivity.getState().toString()) && links.size() > 0) {
                                    return UIValidationStatus.ENABLED;

                                }
                            }
                        }
                    }
                }
                return UIValidationStatus.HIDDEN;
            } else if(key.getComponentID().equals("gongshiDingeManage")) {
                boolean hasAccsss = AccessAdminUtil.isGroup("执行经理");
                if(hasAccsss){
                    return UIValidationStatus.ENABLED;
                }else {
                    if(object instanceof WTPart) {
                        WTPart part = (WTPart) object;
                        WTContainer container = part.getContainer();
                        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
                        String roles = propertiesUtil.getProperty("roles");
                        for(String roleName : roles.split(",")) {
                            roleName = propertiesUtil.getProperty(roleName);
                            Role role = Role.toRole(roleName);
                            if(role == null) {
                                continue;
                            }
                            ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                            for(WTPrincipalReference reference : arrayList) {
                                Object object2 = reference.getPrincipal();
                                if(object2 instanceof WTUser) {
                                    WTUser user = (WTUser) object2;
                                    if(user.getName().equals(currentUser.getName())) {
                                        return UIValidationStatus.ENABLED;
                                    }
                                } else if(object2 instanceof WTGroup) {
                                    WTGroup group = (WTGroup) object2;
                                    if(group.isMember(currentUser)) {
                                        return UIValidationStatus.ENABLED;
                                    }
                                }
                            }
                        }
                    }
                }
                return UIValidationStatus.DISABLED;
            } else if(key.getComponentID().equals("deleteAnalysis")) {
                if(object instanceof WTAnalysisActivity) {
                    WTAnalysisActivity activity = (WTAnalysisActivity) object;
                    WTContainer container = activity.getContainer();
                    if(ProcessUtil.isZhuRenGongyiShi(container) || WCUtil.isAdmin()) {
                        QueryResult result = WfEngineHelper.service.getAssociatedProcesses(activity, WfState.OPEN_RUNNING, null);
                        if(result.size() > 0) {
                            WfProcess process = (WfProcess) result.nextElement();
                            List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
                            activityList = PrintHelper.getActivities(process, activityList);
                            Iterator iterator = activityList.iterator();
                            while(iterator.hasNext()) {
                                WfAssignedActivity assignedActivity = (WfAssignedActivity) iterator.next();
                                String wfactivityName = assignedActivity.getName();
                                if(wfactivityName.equals(AnalysisConstant.ACTIVITY_NAME_ANALYSIS) && "OPEN_RUNNING".equals(assignedActivity.getState().toString())) {
                                    return UIValidationStatus.ENABLED;
                                }
                            }
                        } else {
                            result = WfEngineHelper.service.getAssociatedProcesses(activity, null, null);
                            if(result.size() == 1){
                                WfProcess process = (WfProcess) result.nextElement();
                                if(WfState.CLOSED_TERMINATED.equals(process.getState())){
                                    return UIValidationStatus.ENABLED;
                                }
                            }
                        }
                    }
                }
                return UIValidationStatus.DISABLED;
            } else if("startGongShiSign".equals(key.getComponentID())) {
                if(object instanceof WTDocument) {
                    WTDocument document = (WTDocument) object;
                    String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
                    if (!docType.contains("casc.sast.149.PROCESS_PLAN")) {
                        return UIValidationStatus.HIDDEN;
                    }
                    if(!Constants.STATE_APPROVED.equals(document.getLifeCycleState().toString())){
                        return UIValidationStatus.DISABLED;
                    }
                    boolean hasAccsss = AccessAdminUtil.isGroup("执行经理");
                    if(hasAccsss) {
                        return UIValidationStatus.ENABLED;
                    } else {
                        String dept = IBAHelper.getIBAStringValue(document, "DEPT");
                        if(StrUtil.isNotEmpty(dept)) {
                            String roleName = propertiesUtil.getProperty(dept);
                            if(StrUtil.isNotEmpty(roleName)) {
                                Role role = Role.toRole(roleName);
                                if(role != null) {
                                    WTContainer container = document.getContainer();
                                    ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
                                    ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                                    for(WTPrincipalReference reference : arrayList) {
                                        Object object2 = reference.getPrincipal();
                                        if(object2 instanceof WTUser) {
                                            WTUser user = (WTUser) object2;
                                            if(user.getName().equals(currentUser.getName())) {
                                                return UIValidationStatus.ENABLED;
                                            }
                                        } else if(object2 instanceof WTGroup) {
                                            WTGroup group = (WTGroup) object2;
                                            if(group.isMember(currentUser)) {
                                                return UIValidationStatus.ENABLED;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return UIValidationStatus.DISABLED;
            } else if("requestAuthorization".equals(key.getComponentID())) {
                if(object instanceof WTDocument) {
//                    WTDocument document = (WTDocument) object;
//                    String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
//                    if (docType.contains("casc.sast.149.PROCESS_PLAN") || docType.contains("casc.sast.149.SOPDoc")) {
//                    }
                    return UIValidationStatus.ENABLED;
                }
                return UIValidationStatus.HIDDEN;
            } else if("sjzykPartReferencePrice".equals(key.getComponentID())) {
                if(object instanceof WTPart) {
                    WTPart part = (WTPart) object;
                    if(part.getContainerName().contains("八院")) {
                        return UIValidationStatus.ENABLED;
                    }
                }
                return UIValidationStatus.HIDDEN;
            }
        } catch(WTInvalidParameterException e) {
            e.printStackTrace();
        } catch(WTException e) {
            e.printStackTrace();
        } catch(RemoteException e) {
            e.printStackTrace();
        }
        return UIValidationStatus.NOT_VALIDATED;
    }

}

