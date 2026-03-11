package ext.casc.validator;

import java.rmi.RemoteException;
import java.util.ArrayList;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
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

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.mpml.resource.MPMTooling;

import ext.casc.access.AccessAdminUtil;
import ext.casc.sop.constants.SopConstants;

public class EditDocumentValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        String actionName = key.getComponentID();
        try {
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            if (object instanceof WTDocument) {
            	WTDocument doc = (WTDocument) object;
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
				if (docType.contains("casc.sast.149.PROCESS_PLAN")) {
					WTContainer wtContainer = doc.getContainer();
                    ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
                    Role role = Role.toRole("PRODUCT MANAGER");
                    if (role == null) {
                        return UIValidationStatus.HIDDEN;
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
                    if ( currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()){
                 	   return UIValidationStatus.ENABLED;
                     }
                    return UIValidationStatus.HIDDEN;

				}
            }else if (object instanceof WTChangeOrder2) {
            	WTChangeOrder2 ecn = (WTChangeOrder2) object;
            	boolean isModifier = false;
            	if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isSysAdmin()) {
            		return UIValidationStatus.ENABLED;
                }
 			    if(ecn.getModifier().getName().equals(currentUser.getName())||ecn.getCreator().getName().equals(currentUser.getName())){
                 	isModifier = true;
                }
 			    String state = ecn.getState().getState().toString();
 			    if("INWORK".equals(state)||"REWORK".equals(state)){
 				    if(isModifier){
 				    	return UIValidationStatus.ENABLED;
 				    }else{
 				    	return UIValidationStatus.HIDDEN;
 				    }
 			    }else{
 			    	return UIValidationStatus.DISABLED;
 			    }
            }else if ("MULTI_OBJ_DELETE".equals(actionName) || "list_delete".equals(actionName) || "MULTI_OBJ_DELETE_DETAILS".equals(actionName)) {
				if (object instanceof MPMTooling) {
					MPMTooling tooling = (MPMTooling) object;
					String toolingType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(tooling);
					if(toolingType.contains(SopConstants.SOP_TYPE_SPECIALIZEDTYPE) || toolingType.contains(SopConstants.SOP_TYPE_OPERATIONJOB) || toolingType.contains(SopConstants.SOP_TYPE_PROCEDUCENAME)
							 || toolingType.contains(SopConstants.SOP_TYPE_PARAMETERSNAME) || toolingType.contains(SopConstants.SOP_TYPE_PARAMETERS) || toolingType.contains(SopConstants.SOP_TYPE_CUSTOMAREA)
							 || toolingType.contains(SopConstants.SOP_TYPE_MATERIALCATEGORY) || toolingType.contains(SopConstants.SOP_TYPE_OPERATIONNAME)){
						String containerName = tooling.getContainerName();
						if(SopConstants.SOP_CONTAINER_GYZYK.equals(containerName)){
							WTContainer container = tooling.getContainer();
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.NOT_VALIDATED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(currentUser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(currentUser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						}
					}
				} else if (object instanceof SubFolder) {
					SubFolder sub = (SubFolder) object;
					if (SopConstants.SOP_CONTAINER_GYZYK.equals(sub.getContainerName())) {
						String name = sub.getName();
						if (name.contains("定制区域") || name.contains("参数项目") || name.contains("操作岗位") || name.contains("工序名称") || name.contains("物资类别") || name.contains("专业类别") || name.contains("操作名称")) {
							WTContainer container = sub.getContainer();
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.NOT_VALIDATED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(currentUser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(currentUser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						}
					}else if (SopConstants.SOP_CONTAINER_GYZSK.equals(sub.getContainerName())) {
						String folderPath = sub.getFolderPath();
//						String folderPath = sub.getName();
						if (folderPath.contains("SOP体系BOM")) {
							WTContainer container = sub.getContainer();
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.NOT_VALIDATED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(currentUser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(currentUser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						}
					}
					return UIValidationStatus.NOT_VALIDATED;
				}else if(object instanceof WTPart){
					WTPart part = (WTPart) object;
					String partType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(part);
					if(partType.contains("SOPPart")){
						String containerName = part.getContainerName();
						if(SopConstants.SOP_CONTAINER_GYZSK.equals(containerName)){
							WTContainer container = part.getContainer();
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.NOT_VALIDATED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(currentUser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(currentUser)) {
										return UIValidationStatus.ENABLED;
									}
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
        }catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return UIValidationStatus.NOT_VALIDATED;
    }

}
