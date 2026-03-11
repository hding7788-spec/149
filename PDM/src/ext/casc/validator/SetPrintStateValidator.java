package ext.casc.validator;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.PDMConfig;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessParams;
import ext.casc.process.util.ProcessUtil;
import wt.doc.WTDocument;
import wt.inf.container.OrgContainer;
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
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

public class SetPrintStateValidator extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {

		if("processKnowledgeManage".equals(key.getComponentID())){
			//parentcontext -> processParams$processParamsManage$OR:wt.inf.library.WTLibrary:198039$JCCS-000002!*
            Field field = null;
            try {
                field = criteria.getClass().getDeclaredField("nmCommandBean");
				// 设置为可访问
				field.setAccessible(true);
				// 获取私有变量的值
				NmCommandBean value = (NmCommandBean)field.get(criteria);
				String parentcontext =value.getCompContext();
				//String parentcontext =(String) value.getParameterMap().get("parentcontext");
				String[] ss = parentcontext.split("\\$");
				String tempParamNumber =  ss[ss.length-1];
				String paramNumber = tempParamNumber.substring(0, tempParamNumber.length()-2);//processParams$processParamsManage$OR:wt.inf.library.WTLibrary:198039$JCCS-000023>
				GLProcessParams param = GyCsServerHelper.getProcessParamDefinition(paramNumber);
				if(param!=null){
					if("知识参数".equals(param.getParameterCategory())){
						return 	UIValidationStatus.ENABLED;
					}else{
						return  UIValidationStatus.DISABLED;
					}
				}else{
					return  UIValidationStatus.DISABLED;

				}
			} catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }




		}
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		Object object = criteria.getContextObject().getObject();
		WTContainer contained = null;
		if (object instanceof WTDocument) {
			WTDocument doc = (WTDocument) object;
			contained = doc.getContainer();
		} else if (object instanceof WTPart) {
			WTPart part = (WTPart) object;
			contained = part.getContainer();
		}
		try {
			WTUser currentuser = (WTUser) SessionHelper.manager.getPrincipal();

			ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
			ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
			if (key.getComponentID().equals("synchDangan")) {
				Role role = Role.toRole("DANGANYUAN");
				if (role == null) {
					return UIValidationStatus.DISABLED;
				}
				@SuppressWarnings("unchecked")
				ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
				if (allUser == null || allUser.isEmpty()) {// 从共享团队里获取档案员
					OrgContainer orgCon = ProcessUtil.getOrgContainer();
					ContainerTeam shareContainerTeam = ContainerTeamHelper.service.getSharedTeamByName(orgCon, "产品共享团队");
					allUser = shareContainerTeam.getAllPrincipalsForTarget(role);
				}
				System.out.println("-----------allUser:" + allUser);
				for (WTPrincipalReference reference : allUser) {
					Object object2 = reference.getPrincipal();
					if (object2 instanceof WTUser) {
						WTUser user = (WTUser) object2;
						if (user.getName().equals(currentuser.getName())) {
							return UIValidationStatus.ENABLED;
						}
					}else if (object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if (group.isMember(currentuser)) {
							return UIValidationStatus.ENABLED;
						}
					}
				}
			} else if (key.getComponentID().equals("exportDNC")) {
				if (object instanceof WTDocument) {
					WTDocument doc = (WTDocument) object;
					String modifier = doc.getModifier().getName();
					String state = doc.getState().toString();
					if(modifier.equals(currentuser.getName()) && state.equals("APPROVED")){
						return UIValidationStatus.ENABLED;
					}else{
						return UIValidationStatus.DISABLED;
					}
				}

			} else if(key.getComponentID().equals("uploadAttachment4SK")) {
				if (object instanceof WTDocument) {
					WTDocument doc = (WTDocument) object;
					String modifier = doc.getModifier().getName();
					String typeName = TypedUtility.getLocalizedTypeName(doc, Locale.SIMPLIFIED_CHINESE);
					if(typeName.indexOf("数控工艺") > -1 && (modifier.equals(currentuser.getName()) || AccessAdminUtil.isAdmin())){
						return UIValidationStatus.ENABLED;
					}else {
						return UIValidationStatus.HIDDEN;
					}
				}
			} else if (key.getComponentID().equals("editPrintStateWizard")) {
				Role role = Role.toRole("DANGANYUAN");
				if (role == null) {
					return UIValidationStatus.DISABLED;
				}
				@SuppressWarnings("unchecked")
				ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
				if (allUser == null || allUser.isEmpty()) {// 从共享团队里获取档案员
					OrgContainer orgCon = ProcessUtil.getOrgContainer();
					ContainerTeam shareContainerTeam = ContainerTeamHelper.service.getSharedTeamByName(orgCon, "产品共享团队");
					allUser = shareContainerTeam.getAllPrincipalsForTarget(role);
				}
				System.out.println("-----------allUser:" + allUser);
				for (WTPrincipalReference reference : allUser) {
					Object object2 = reference.getPrincipal();
					if (object2 instanceof WTUser) {
						WTUser user = (WTUser) object2;
						if (user.getName().equals(currentuser.getName())) {
							return UIValidationStatus.ENABLED;
						}
					}else if (object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if (group.isMember(currentuser)) {
							return UIValidationStatus.ENABLED;
						}
					}
				}

				role = Role.toRole("ZHURENGONGYISHI");
				if (role == null) {
					return UIValidationStatus.DISABLED;
				}
				allUser = containerTeam.getAllPrincipalsForTarget(role);
				for (WTPrincipalReference reference : allUser) {
					Object object2 = reference.getPrincipal();
					if (object2 instanceof WTUser) {
						WTUser user = (WTUser) object2;
						if (user.getName().equals(currentuser.getName())) {
							return UIValidationStatus.ENABLED;
						}
					}else if (object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if (group.isMember(currentuser)) {
							return UIValidationStatus.ENABLED;
						}
					}
				}
			}else if (key.getComponentID().equals("DownloadPrint")) {
			    Role role = Role.toRole("DANGANYUAN");
                if (role == null) {
                    return UIValidationStatus.DISABLED;
                }
                @SuppressWarnings("unchecked")
                ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
                if (allUser == null || allUser.isEmpty()) {// 从共享团队里获取档案员
                    OrgContainer orgCon = ProcessUtil.getOrgContainer();
                    ContainerTeam shareContainerTeam = ContainerTeamHelper.service.getSharedTeamByName(orgCon, "产品共享团队");
                    allUser = shareContainerTeam.getAllPrincipalsForTarget(role);
                }
                System.out.println("-----------allUser:" + allUser);
                for (WTPrincipalReference reference : allUser) {
                    Object object2 = reference.getPrincipal();
                    if (object2 instanceof WTUser) {
                        WTUser user = (WTUser) object2;
                        if (user.getName().equals(currentuser.getName())) {
                            return UIValidationStatus.ENABLED;
                        }
                    }else if (object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if (group.isMember(currentuser)) {
							return UIValidationStatus.ENABLED;
						}
					}
                }
                role = Role.toRole("ZHURENGONGYISHI");
                if (role == null) {
                    return UIValidationStatus.DISABLED;
                }
                allUser = containerTeam.getAllPrincipalsForTarget(role);
                for (WTPrincipalReference reference : allUser) {
                    Object object2 = reference.getPrincipal();
                    if (object2 instanceof WTUser) {
                        WTUser user = (WTUser) object2;
                        if (user.getName().equals(currentuser.getName())) {
                            return UIValidationStatus.ENABLED;
                        }
                    }else if (object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if (group.isMember(currentuser)) {
							return UIValidationStatus.ENABLED;
						}
					}
                }
            }else if (key.getComponentID().equals("signature")) {
				if(PDMConfig.isZS){
					return  UIValidationStatus.HIDDEN;
				}else{
					return UIValidationStatus.ENABLED;
				}
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return UIValidationStatus.HIDDEN;

	}
}
