package ext.casc.sop.validator;

import java.util.ArrayList;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.folder.Cabinet;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.resource.MPMTooling;

import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTaskItem;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;

/**
 * SOP权限控制类
 */
public class SopObjectValidator extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		String objectType = key.getObjectType();
		String actionName = key.getComponentID();
		Object object = criteria.getContextObject().getObject();
		Object pageObj = criteria.getPageObject().getObject();
		try {
			WTUser curentuser = (WTUser) SessionHelper.getPrincipal();
			if ("sopCustom".equals(objectType)) {
				if ("importSOPPart".equals(actionName)) {
					if (object instanceof WTLibrary) {
						WTLibrary library = (WTLibrary) object;
						String containerName = library.getName();
						if (SopConstants.SOP_CONTAINER_GYZSK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(library);
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					}
				} else if ("edit_sopziyuan".equals(actionName) || "createSOPCSXM".equals(actionName) || "createSOPDZQY".equals(actionName) || "createSOPCZGW".equals(actionName)
						|| "createSOPZYLB".equals(actionName) || "createSOPGXMC".equals(actionName) || "createSOPCSXMMC".equals(actionName) || "createSOPWZLB".equals(actionName)) {
					if (object instanceof WTLibrary) {
						WTLibrary library = (WTLibrary) object;
						String containerName = library.getName();
						if (SopConstants.SOP_CONTAINER_GYZYK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(library);
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					} else if (object instanceof Cabinet) {
						try {
							WTUser user1 = (WTUser) SessionHelper.getPrincipal();
							Cabinet cabinet = (Cabinet) object;
							if(SopConstants.SOP_CONTAINER_GYZYK.equals(cabinet.getContainerName())) {
								WTContainer container = cabinet.getContainer();
								ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
								Role role = Role.toRole("ZHURENGONGYISHI");
								if (role == null) {
									return UIValidationStatus.DISABLED;
								}
								ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
								for (WTPrincipalReference reference : arrayList) {
									Object object2 = reference.getPrincipal();
									if (object2 instanceof WTUser) {
										WTUser user = (WTUser) object2;
										if (user.getName().equals(user1.getName())) {
											return UIValidationStatus.ENABLED;
										}
									} else if (object2 instanceof WTGroup) {
										WTGroup group = (WTGroup) object2;
										if (group.isMember(user1)) {
											return UIValidationStatus.ENABLED;
										}
									}
								}
							}else{
								return UIValidationStatus.HIDDEN;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (object instanceof SubFolder) {
						try {
							WTUser user1 = (WTUser) SessionHelper.getPrincipal();
							SubFolder sub = (SubFolder) object;
							if(SopConstants.SOP_CONTAINER_GYZYK.equals(sub.getContainerName())) {
								WTContainer container = sub.getContainer();
								ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
								Role role = Role.toRole("ZHURENGONGYISHI");
								if (role == null) {
									return UIValidationStatus.DISABLED;
								}
								ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
								for (WTPrincipalReference reference : arrayList) {
									Object object2 = reference.getPrincipal();
									if (object2 instanceof WTUser) {
										WTUser user = (WTUser) object2;
										if (user.getName().equals(user1.getName())) {
											return UIValidationStatus.ENABLED;
										}
									} else if (object2 instanceof WTGroup) {
										WTGroup group = (WTGroup) object2;
										if (group.isMember(user1)) {
											return UIValidationStatus.ENABLED;
										}
									}
								}
							}else{
								return UIValidationStatus.HIDDEN;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if(object instanceof MPMTooling){
						WTUser user1 = (WTUser) SessionHelper.getPrincipal();
						MPMTooling part = (MPMTooling) object;
						if(SopConstants.SOP_CONTAINER_GYZYK.equals(part.getContainerName())) {
							WTContainer container = part.getContainer();
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(user1.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(user1)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						}else{
							return UIValidationStatus.HIDDEN;
						}
					}else{
						return UIValidationStatus.DISABLED;
					}
					return UIValidationStatus.DISABLED;
				} else if ("createSOPPart".equals(actionName)) {
					if (object instanceof WTLibrary) {
						WTLibrary library = (WTLibrary) object;
						String containerName = library.getName();
						if (SopConstants.SOP_CONTAINER_GYZSK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(library);
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					} else if (object instanceof Cabinet) {
						Cabinet cabinet = (Cabinet) object;
						String containerName = cabinet.getContainerName();
						if (SopConstants.SOP_CONTAINER_GYZSK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) cabinet.getContainer());
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					} else if (object instanceof SubFolder) {
						SubFolder subFolder = (SubFolder) object;
						String containerName = subFolder.getContainerName();
						if (SopConstants.SOP_CONTAINER_GYZSK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) subFolder.getContainer());
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					}

				} else if ("openSopProcecssEditor".equals(actionName)) {
					if (object instanceof WTDocument) {
						WTDocument doc = (WTDocument) object;
						String typeName = TypedUtility.getTypeIdentifier(doc).getTypename();
						if (typeName.contains(SopConstants.SOP_TYPE_SOPDOC)) {
							return UIValidationStatus.ENABLED;
						} else {
							return UIValidationStatus.HIDDEN;
						}
					} else if (object instanceof MPMProcessPlan) {
						MPMProcessPlan processPlan = (MPMProcessPlan) object;
						String typeName = TypedUtility.getTypeIdentifier(processPlan).getTypename();
						if (typeName.contains(SopConstants.SOP_TYPE_SOPPROCESSPLAN)) {
							return UIValidationStatus.ENABLED;
						} else {
							return UIValidationStatus.HIDDEN;
						}
					} else if (object instanceof WTPart) {
						if (pageObj instanceof ProcessTaskItem) {
							ProcessTaskItem taskItem = (ProcessTaskItem) pageObj;
							String state = taskItem.getTaskItemState();
							String executeRole = taskItem.getExecutorRole();
							if (!SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType()) && !SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())) {
								return UIValidationStatus.HIDDEN;
							}
							if (state.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN) && executeRole.equals(ProcessConstants.ROLE_GONGYIYUAN)) {
								return UIValidationStatus.ENABLED;
							}
						}
					}

				} else if ("sopProAssignTask".equals(key.getComponentID())) {
					// SOP设计任务分工：是SOP体系BOM，体系BOM下五SOP工艺，角色是主任工艺师
					if (object instanceof WTPart) {
						WTPart wtPart = (WTPart) object;
						String typeName = TypedUtility.getTypeIdentifier(wtPart).getTypename();
						if (typeName.contains(SopConstants.SOP_TYPE_SOPPART)) {
							QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(wtPart, true);
							LatestConfigSpec lcs = new LatestConfigSpec();
							qr = lcs.process(qr);
							boolean isHasSopDoc = false;
							while (qr.hasMoreElements()) {
								Object o = qr.nextElement();
								if (o instanceof WTDocument) {
									WTDocument document = (WTDocument) o;
									String docType = TypedUtility.getTypeIdentifier(document).getTypename();
									if (docType.contains(SopConstants.SOP_TYPE_SOPDOC)) {
										isHasSopDoc = true;
									}
								}
							}
							if (isHasSopDoc) {
								return UIValidationStatus.DISABLED;
							} else {
								ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtPart.getContainer());
								Role role = Role.toRole("ZHURENGONGYISHI");
								if (role == null) {
									return UIValidationStatus.DISABLED;
								}
								ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
								for (WTPrincipalReference reference : arrayList) {
									Object object2 = reference.getPrincipal();
									if (object2 instanceof WTUser) {
										WTUser user = (WTUser) object2;
										if (user.getName().equals(curentuser.getName())) {
											return UIValidationStatus.ENABLED;
										}
									} else if (object2 instanceof WTGroup) {
										WTGroup group = (WTGroup) object2;
										if (group.isMember(curentuser)) {
											return UIValidationStatus.ENABLED;
										}
									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					} else if (object instanceof Cabinet) {
						Cabinet cabinet = (Cabinet) object;
						String containerName = cabinet.getContainerName();
						if (SopConstants.SOP_CONTAINER_GYZSK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) cabinet.getContainer());
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;

									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					} else if (object instanceof SubFolder) {
						SubFolder subFolder = (SubFolder) object;
						String containerName = subFolder.getContainerName();
						if (SopConstants.SOP_CONTAINER_GYZSK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) subFolder.getContainer());
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					}
				} else if ("sopChangeProAssignTask".equals(key.getComponentID())) {
					// SOP更改任务分工：是SOP体系BOM，存在受控SOP工艺
					if (object instanceof WTPart) {
						WTPart wtPart = (WTPart) object;
						String typeName = TypedUtility.getTypeIdentifier(wtPart).getTypename();
						if (typeName.contains(SopConstants.SOP_TYPE_SOPPART)) {
							List<WTDocument> documentList = SopUtil.getAllApprovedSopTechnics(wtPart);
							if (documentList != null && documentList.size() > 0) {
								return UIValidationStatus.ENABLED;
							} else {
								return UIValidationStatus.DISABLED;
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					} else if (object instanceof Cabinet) {
						Cabinet cabinet = (Cabinet) object;
						String containerName = cabinet.getContainerName();
						if (SopConstants.SOP_CONTAINER_GYZSK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) cabinet.getContainer());
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					} else if (object instanceof SubFolder) {
						SubFolder subFolder = (SubFolder) object;
						String containerName = subFolder.getContainerName();
						if (SopConstants.SOP_CONTAINER_GYZSK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) subFolder.getContainer());
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;

									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					}
				} else if ("sopMPMResourceImport".equals(key.getComponentID())) {
					// SOP资源导入
					if (object instanceof WTLibrary) {
						WTLibrary library = (WTLibrary) object;
						String containerName = library.getName();
						if (SopConstants.SOP_CONTAINER_GYZYK.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(library);
							Role role = Role.toRole("ZHURENGONGYISHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						} else {
							return UIValidationStatus.HIDDEN;
						}
					}
				} else if ("relatedSopProcessPlan".equals(key.getComponentID())) {
					if (object instanceof WTDocument) {
						WTDocument document = (WTDocument) object;
						String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
						if (typeName.contains(SopConstants.SOP_TYPE_GUOJIABIAOZHUN)) {
							return UIValidationStatus.ENABLED;
						} else {
							return UIValidationStatus.HIDDEN;
						}
					}

				} else if ("createSOPCZMC".equals(actionName)) {
					return UIValidationStatus.ENABLED;
				} else if ("".equals(key.getComponentID())) {

				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return UIValidationStatus.DISABLED;
	}
}
