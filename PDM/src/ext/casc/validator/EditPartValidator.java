package ext.casc.validator;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.util.IBAHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;

import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.access.AccessAdminUtil;

public class EditPartValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            String id = key.getComponentID();
            if (id.indexOf("setPartType") > -1 || id.indexOf("tempProAssignTask") > -1
                    || id.indexOf("PBOMProAssignTask") > -1 || id.indexOf("ChangeProAssignTask") > -1
                    || id.indexOf("PartProAssignTask") > -1 || id.indexOf("updatePartAttr") > -1|| id.indexOf("TechnicsReportAssignTask") > -1 ) {
                if (object instanceof WTPart) {
                    WTPart part = (WTPart) object;
                    String viewName = part.getViewName();
                    if ("Manufacturing".equals(viewName)) {
                    	//判断是有PBOM xml
                    	if(!WTPartUtil.isHasPbomXml(part)) {
                    		return UIValidationStatus.DISABLED;
                    	}

                        WTContainer wtContainer = part.getContainer();
                        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
                        Role role = Role.toRole("ZHURENGONGYISHI");
                        if (role == null) {
                            return UIValidationStatus.DISABLED;
                        }
                        ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                        for (WTPrincipalReference reference : arrayList) {
                            Object object2 = reference.getPrincipal();
                            if (object2 instanceof WTUser) {
                                WTUser user = (WTUser) object2;
                                if (user.getName().equals(currentUser.getName())) {
                                    return UIValidationStatus.ENABLED;
                                }
                            }else if (object2 instanceof WTGroup) {
    							WTGroup group = (WTGroup) object2;
    							if (group.isMember(currentUser)) {
                                    return UIValidationStatus.ENABLED;

    							}
    						}
                        }
                        if (id.indexOf("updatePartAttr") > -1) {
                            Role role2 = Role.toRole("XINXIHUABUSHUJUYUAN");
                            if (role2 == null) {
                                return UIValidationStatus.DISABLED;
                            }
                            ArrayList<WTPrincipalReference> arrayList2 = containerTeam.getAllPrincipalsForTarget(role2);
                            for (WTPrincipalReference reference : arrayList2) {
                                Object object2 = reference.getPrincipal();
                                if (object2 instanceof WTUser) {
                                    WTUser user = (WTUser) object2;
                                    if (user.getName().equals(currentUser.getName())) {
                                        return UIValidationStatus.ENABLED;
                                    }
                                }else if (object2 instanceof WTGroup) {
        							WTGroup group = (WTGroup) object2;
        							if (group.isMember(currentUser)) {
                                        return UIValidationStatus.ENABLED;

        							}
        						}
                            }
                        }
                    }
                }
            } else if (id.indexOf("setProcessPath") > -1){//隐藏设置工艺路线的功能
                return UIValidationStatus.HIDDEN;
            } else if (id.indexOf("start_pbom_edit") > -1) {
            	if (object instanceof WTPart) {
                    WTPart part = (WTPart) object;
                    String from = IBAHelper.getIBAStringValue(part,"FROM");
                    if("E3".equals(from)){
                        return UIValidationStatus.ENABLED;
                    }
                    WTContainer wtContainer = part.getContainer();
                    ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
                    Role role = Role.toRole("ZHURENGONGYISHI");
                    if (role == null) {
                        return UIValidationStatus.DISABLED;
                    }
                    ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                    for (WTPrincipalReference reference : arrayList) {
                        Object object2 = reference.getPrincipal();
                        if (object2 instanceof WTUser) {
                            WTUser user = (WTUser) object2;
                            if (user.getName().equals(currentUser.getName())) {
                                return UIValidationStatus.ENABLED;
                            }
                        }else if (object2 instanceof WTGroup) {
							WTGroup group = (WTGroup) object2;
							if (group.isMember(currentUser)) {
                                return UIValidationStatus.ENABLED;

							}
						}
                    }

                    //boolean b = isLatestLevel(part);
            	}
            } else if (id.indexOf("technicsReport") > -1) {
            	if (object instanceof WTPart) {
                    WTPart part = (WTPart) object;
                    String viewName = part.getViewName();
                    if ("Manufacturing".equals(viewName)) {
                    	return UIValidationStatus.ENABLED;
                    }
            	}
            } else if(id.indexOf("TableConfig")>-1 || id.indexOf("ProConfig")>-1){
            	if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
                    return UIValidationStatus.ENABLED;
                }else{
                	String userName = currentUser.getName();
                	if(userName.equals("niyongjun")){
                		return UIValidationStatus.ENABLED;
                	}
                }
            }else if(id.indexOf("sendToKR")>-1){
                if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
                    return UIValidationStatus.ENABLED;
                }else{
                    WTContainer wtContainer = null;
                    if (object instanceof ProcessEnvelope) {
                        ProcessEnvelope pe = (ProcessEnvelope) object;
                        wtContainer = pe.getContainer();
                    }else if (object instanceof ChangePackaged) {
                        ChangePackaged cp = (ChangePackaged) object;
                        wtContainer = cp.getContainer();
                    }
                    if(wtContainer!=null){
                        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
                        Role role = Role.toRole("ZHURENGONGYISHI");
                        if (role == null) {
                            return UIValidationStatus.DISABLED;
                        }
                        ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                        for (WTPrincipalReference reference : arrayList) {
                            Object object2 = reference.getPrincipal();
                            if (object2 instanceof WTUser) {
                                WTUser user = (WTUser) object2;
                                if (user.getName().equals(currentUser.getName())) {
                                    return UIValidationStatus.ENABLED;
                                }
                            }else if (object2 instanceof WTGroup) {
                                WTGroup group = (WTGroup) object2;
                                if (group.isMember(currentUser)) {
                                    return UIValidationStatus.ENABLED;

                                }
                            }
                        }
                    }
                }
            }
        } catch (WTInvalidParameterException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
			e.printStackTrace();
		} finally {
            SessionServerHelper.manager.setAccessEnforced(access);
        }
        return UIValidationStatus.HIDDEN;
    }

	private boolean isLatestLevel(WTPart part) throws WTException {
		QueryResult list = WTPartHelper.service.getUsesWTPartMasters(part);
		if(list.hasMoreElements()){
			return false;
		}
		list = WTPartHelper.service.getUsedByWTParts((WTPartMaster)part.getMaster());
		if(list.hasMoreElements()){
			return false;
		}
		return true;
	}

}
