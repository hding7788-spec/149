package ext.casc.validator;

import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.access.AccessAdminUtil;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class AdminValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        try {
            WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
            if(key.getComponentID().equals("quickReCycle")){
            	boolean flag;
				try {
					flag = quickReCycleVal(key,criteria);
					if(flag){
	            		return UIValidationStatus.ENABLED;
	            	}else{
	            		return UIValidationStatus.HIDDEN;
	            	}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            }else if(key.getComponentID().equals("quickApproved")){

            	Object object = criteria.getContextObject().getObject();
        		WTDocument d = (WTDocument)object;
            	String typeName = TypedUtility.getTypeIdentifier(object).getTypename();
				if(typeName.contains("PROCESS_PLAN")) {
					MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(d);
					if(plan!=null ){
						  return UIValidationStatus.DISABLED;
					}
				}


            }else if(key.getComponentID().equals("processParamsManage")){

				Object object = criteria.getContextObject().getObject();



			}else if(key.getComponentID().equals("relateTypecialProcess")){
				if (currentUser.getName().equals("xuhui") || currentUser.getName().equals("xiapeiyun") || currentUser.getName().equals("xukui") || currentUser.getName().equals("mengyao")){
					return UIValidationStatus.ENABLED;
				}
			}else if(key.getComponentID().equals("addTableConfig") || key.getComponentID().equals("deleteTableConfig") || key.getComponentID().equals("saveTableConfig")
					|| key.getComponentID().equals("addProConfig") || key.getComponentID().equals("deleteProConfig") || key.getComponentID().equals("saveProConfig")){
				if(currentUser.getName().equals("niyongjun")){
					return UIValidationStatus.ENABLED;
				}
			}else if(key.getComponentID().equals("setTemplateEnum") ) {
				Object object = criteria.getContextObject().getObject();
				if (object instanceof WTDocument) {
					String docType = null;
					try {
						WTDocument doc = (WTDocument) object;
						docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
						if (docType.indexOf("casc.sast.149.TechnicsParamTemplate") != -1) {
							if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()
									||currentUser.getName().equals(doc.getModifierName())||currentUser.getName().equals(doc.getCreatorName())) {
								return UIValidationStatus.ENABLED;
							}
						}
					} catch (WTException e) {
						throw new RuntimeException(e);
					} catch (RemoteException e) {
						throw new RuntimeException(e);
					}
				}
				return UIValidationStatus.HIDDEN;

			}else if(key.getComponentID().equals("openBaiYuTool") ) {
				Object object = criteria.getContextObject().getObject();
				if (object instanceof WTDocument) {
					String docType = null;
					try {
						WTDocument doc = (WTDocument) object;
						docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
						if (docType.indexOf("casc.sast.149.BaiYuDocument") != -1) {
							if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()
									||currentUser.getName().equals(doc.getModifierName())||currentUser.getName().equals(doc.getCreatorName())) {
								return UIValidationStatus.ENABLED;
							}
						}
					} catch (WTException e) {
						throw new RuntimeException(e);
					} catch (RemoteException e) {
						throw new RuntimeException(e);
					}
				}
				return UIValidationStatus.HIDDEN;

			}else if(key.getComponentID().equals("matchHistoryQuota") ) {
				boolean hasAccsss = AccessAdminUtil.isGroup("历史物资匹配权限组");
				if(hasAccsss){
					return UIValidationStatus.ENABLED;
				}
				return UIValidationStatus.HIDDEN;

			} else if(key.getComponentID().equals("customSynergyManage")) {
				boolean hasAccsss = AccessAdminUtil.isGroup("业务管理员");
				if(currentUser.getName().equals("Administrator") || hasAccsss) {
					return UIValidationStatus.ENABLED;
				}
				return UIValidationStatus.HIDDEN;
			} else if(key.getComponentID().equals("WFSAVEAS")) {
				boolean hasAccsss = AccessAdminUtil.isGroup("部件另存权限组");
				if(hasAccsss){
					return UIValidationStatus.ENABLED;
				}
				return UIValidationStatus.HIDDEN;
			}


			if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
                return UIValidationStatus.ENABLED;
            }else{
            	Object object = criteria.getContextObject().getObject();
            	if(object instanceof WTPart){
            		WTPart part = (WTPart)object;
            		WTContainer wtContainer = part.getContainer();
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
						}
                    }

            	}else if(object instanceof WTDocument){
            		WTDocument d = (WTDocument)object;
                	String typeName = TypedUtility.getTypeIdentifier(object).getTypename();
    				if(typeName.contains("PROCESS_PLAN")) {
    					boolean isGongyiguanliyuan = AccessAdminUtil.isGroup(ext.casc.constants.Constants.GROUP_NAME_GONGYIGUANLIZU);
    					if(isGongyiguanliyuan){
    						if(key.getComponentID().equals("quickApproved")){
    	            			String version = d.getVersionInfo().getIdentifier().getValue();
    	            			String state = d.getState().getState().toString();
    	            			if("space".equals(version)&&"INWORK".equals(state)){
    	            				QueryResult qrProcs = WfEngineHelper.service
    	    		 						.getAssociatedProcesses(d, null, null);
    	            				boolean  flag = false;
    	    		 				while (qrProcs.hasMoreElements()) {
    	    		 					WfProcess proc = (WfProcess) qrProcs.nextElement();
    	    		 					if(proc.getState().equals(WfState.OPEN_RUNNING)){
    	    		 						flag = true;
    	    		 					}
    	    		 				}
    	    		 				if(flag){
    	    		 					return UIValidationStatus.DISABLED;
    	    		 				}else{
    	    		 					return UIValidationStatus.ENABLED;
    	    		 				}

    	            			}else{
    	            				return UIValidationStatus.DISABLED;
    	            			}
    	            		}else{
    	            			return UIValidationStatus.ENABLED;
    	            		}

    					}
    				}

            	}
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return UIValidationStatus.HIDDEN;
    }

	private boolean quickReCycleVal(UIValidationKey key, UIValidationCriteria criteria) throws RemoteException, WTException {
		Object object = criteria.getContextObject().getObject();
		boolean objVal = false;
    	if(object instanceof WTChangeOrder2){
    		objVal = true;


    	}else if(object instanceof WTDocument){
    		WTDocument d = (WTDocument)object;
    		String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(d);
    		if (docType.indexOf("casc.sast.149.PROCESS_NOTICE") != -1){
    			objVal = true;
    		}
    	}
    	if(objVal){
    		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
   		 	boolean isGongyiguanliyuan = AccessAdminUtil.isGroup(ext.casc.constants.Constants.GROUP_NAME_GONGYIGUANLIZU);
    		if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()||isGongyiguanliyuan) {
                return true;
            }
    	}
    	return false;
	}
}
