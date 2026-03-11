package ext.casc.validator;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.Constants;
import ext.casc.constants.PDMConfig;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Cabinet;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import java.util.ArrayList;
import java.util.List;

public class EditPartValidator2 extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		Object object = criteria.getContextObject().getObject();
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
			if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
				return UIValidationStatus.ENABLED;
			}
			if (key.getComponentID().equals("createPartWizard")) {
				WTContainer wtContainer = null;
				if (object instanceof Cabinet) {
					Cabinet cab = (Cabinet) object;
					wtContainer = cab.getContainer();
				} else if (object instanceof SubFolder) {
					SubFolder folder = (SubFolder) object;
					wtContainer = folder.getContainer();
				}
				if (wtContainer == null) {
					return UIValidationStatus.HIDDEN;
				}

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

				role = Role.toRole("ZHURENGONGYISHI");
				if (role == null) {
					return UIValidationStatus.HIDDEN;
				}
				arrayList = containerTeam.getAllPrincipalsForTarget(role);
				for (WTPrincipalReference reference : arrayList) {
					Object object2 = reference.getPrincipal();
					if (object2 instanceof WTUser) {
						WTUser user = (WTUser) object2;
						if (user.getName().equals(currentUser.getName())) {
							return UIValidationStatus.HIDDEN;
						}
					}else if (object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if (group.isMember(currentUser)) {
							return UIValidationStatus.HIDDEN;

						}
					}
				}
				if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
					return UIValidationStatus.ENABLED;
				}
			} else if (key.getComponentID().equals("CustomRename")||key.getComponentID().equals("importBomStructure")) {

				if (object instanceof WTPart) {
					WTPart part = (WTPart) object;
					if(key.getComponentID().equals("importBomStructure")
							&&"Design".equals(part.getViewName())){
						return UIValidationStatus.HIDDEN;
					}
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
						}else if (object2 instanceof WTGroup) {
							WTGroup group = (WTGroup) object2;
							if (group.isMember(currentUser)) {
								return UIValidationStatus.ENABLED;

							}
						}
					}

					role = Role.toRole("ZHURENGONGYISHI");
					if (role == null) {
						return UIValidationStatus.HIDDEN;
					}
					arrayList = containerTeam.getAllPrincipalsForTarget(role);
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
					if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
						return UIValidationStatus.ENABLED;
					}
				}
			} else if (key.getComponentID().equals("synchDangan2")) {
				WTContainer wtContainer = null;
				if (object instanceof WTPart) {
					WTPart part = (WTPart) object;
					wtContainer = part.getContainer();
				} else if (object instanceof WTDocument) {
					WTDocument doc = (WTDocument) object;
					wtContainer = doc.getContainer();
				}

				if (wtContainer == null) {
					return UIValidationStatus.HIDDEN;
				}
				// 资料员组可用
				List<WTGroup> groups = searchGroupByName("同步到预立卷人员组");
				if (groups != null && groups.size() > 0) {
					for (WTGroup group : groups) {
						if (group.isMember(currentUser)) {
							return UIValidationStatus.ENABLED;
						}
					}
				}
				if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
					return UIValidationStatus.ENABLED;
				}
				return UIValidationStatus.HIDDEN;
			} else if (key.getComponentID().equals("exchangeProcessDoc")) {
				if(!PDMConfig.isZS){
					return UIValidationStatus.HIDDEN;
				}
				if (object instanceof WTPart) {
					WTPart part = (WTPart) object;
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
						}else if (object2 instanceof WTGroup) {
							WTGroup group = (WTGroup) object2;
							if (group.isMember(currentUser)) {
								return UIValidationStatus.ENABLED;

							}
						}
					}

					role = Role.toRole("ZHURENGONGYISHI");
					if (role == null) {
						return UIValidationStatus.HIDDEN;
					}
					arrayList = containerTeam.getAllPrincipalsForTarget(role);
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
					if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
						return UIValidationStatus.ENABLED;
					}
				}
			}else if (key.getComponentID().equals("exchangeProcessDocCS")) {
				if(PDMConfig.isZS){
					return UIValidationStatus.HIDDEN;
				}

				if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
					return UIValidationStatus.ENABLED;
				}
			}
			if (object instanceof WTPart) {
				WTPart part = (WTPart) object;

				String viewName = part.getViewName();
				if ("Manufacturing".equals(viewName)) {
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
						}else if (object2 instanceof WTGroup) {
							WTGroup group = (WTGroup) object2;
							if (group.isMember(currentUser)) {
								return UIValidationStatus.ENABLED;
							}
						}
					}

					role = Role.toRole("ZHURENGONGYISHI");
					if (role == null) {
						return UIValidationStatus.HIDDEN;
					}
					arrayList = containerTeam.getAllPrincipalsForTarget(role);
					for (WTPrincipalReference reference : arrayList) {
						Object object2 = reference.getPrincipal();
						if (object2 instanceof WTUser) {
							WTUser user = (WTUser) object2;
							if (user.getName().equals(currentUser.getName())) {
								return UIValidationStatus.HIDDEN;

							}
						}else if (object2 instanceof WTGroup) {
							WTGroup group = (WTGroup) object2;
							if (group.isMember(currentUser)) {
								return UIValidationStatus.HIDDEN;

							}
						}
					}
					if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
						return UIValidationStatus.ENABLED;
					}

				} else {
					String version = part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue();
					if ("space.0".equals(version)) {
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
							}else if (object2 instanceof WTGroup) {
								WTGroup group = (WTGroup) object2;
								if (group.isMember(currentUser)) {
									return UIValidationStatus.ENABLED;
								}
							}
						}

						role = Role.toRole("ZHURENGONGYISHI");
						if (role == null) {
							return UIValidationStatus.HIDDEN;
						}
						arrayList = containerTeam.getAllPrincipalsForTarget(role);
						for (WTPrincipalReference reference : arrayList) {
							Object object2 = reference.getPrincipal();
							if (object2 instanceof WTUser) {
								WTUser user = (WTUser) object2;
								if (user.getName().equals(currentUser.getName())) {
									return UIValidationStatus.HIDDEN;
								}
							}else if (object2 instanceof WTGroup) {
								WTGroup group = (WTGroup) object2;
								if (group.isMember(currentUser)) {
									return UIValidationStatus.HIDDEN;

								}
							}
						}
						if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
							return UIValidationStatus.ENABLED;
						}
					}

				}
			} else if (object instanceof WTDocument) {
				WTDocument d = (WTDocument) object;
				String typeName = TypedUtility.getTypeIdentifier(object).getTypename();
				String state = d.getState().getState().toString();

				if (typeName.contains("PROCESS_NOTICE") && state.equals("APPROVED")) {
					return UIValidationStatus.ENABLED;
				}
				if (typeName.contains("TECHNOLOGY_AGREEMENT") && state.equals("APPROVED")) {
					return UIValidationStatus.ENABLED;
				}

				if (typeName.contains("PROCESS_PLAN") && state.equals("APPROVED")) {
					if (d.getModifier().getPrincipal().getName().equals(currentUser.getName())) {
						QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(d, null, null);
						if (qrProcs.hasMoreElements()) {
							WfProcess proc = (WfProcess) qrProcs.nextElement();
							if (proc.getTemplate().getName().equals(Constants.WFN_SANJIPROCESSWF) || proc.getTemplate().getName().equals(Constants.WFN_SANJIGENGGAIWF)) {
								return UIValidationStatus.ENABLED;
							}

						}

					}

				}
			}
		} catch (WTInvalidParameterException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return UIValidationStatus.HIDDEN;
	}

	 public static List<WTGroup> searchGroupByName(String groupName) throws WTException {
	        List<WTGroup> groupList = new ArrayList<WTGroup>();
	        QuerySpec querySpec = new QuerySpec(WTGroup.class);
	        querySpec.appendWhere(new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.LIKE, groupName, false), new int[]{0});
	        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);
	        WTGroup group = null;
	        while (queryResult.hasMoreElements()) {
	            group = (WTGroup) queryResult.nextElement();
	            groupList.add(group);
	        }
	        return groupList;
	    }
}
