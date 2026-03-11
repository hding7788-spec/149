package ext.casc.workflow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.CMatBean;
import com.glaway.mpm.print.util.PrintUtil;
import com.glaway.mpm.processplan.helper.ZhuFuLinkUtil;
import com.glaway.mpm.util.*;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.wvs.server.util.Util;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.cadsign.wcserver.CADSignHelper;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.doc.CSCDoc;
import ext.casc.fileprint.FilePrintUtil;
import ext.casc.lifecycle.CmLifecycleHelper;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.*;
import ext.casc.util.IBAHelper;
import ext.casc.workflow.tree.mvc.builder.SetListGongShiDingEBuilder;
import ext.csc.utilities.principal.CSCPrincipal;
import ext.ptc.ViewWIHelper;
import org.jdom.Element;
import wt.change2.*;
import wt.content.*;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.*;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTValuedMap;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.PhaseTemplate;
import wt.lifecycle.State;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.*;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class WorkflowHelper {
	static VaLogger logger = VaLogger.getLogger(WorkflowHelper.class);

	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.GLAWAY_149_CONFIG_PATH);

	public static void setTeamRoleUserToWfTeamRole(ObjectReference self,WTObject pbo,String roleName) throws WTException {
		Role role = Role.toRole(roleName);
		if(role == null) {
			GLLogger.debug("role===>" + role);
			return;
		}
		List<WTUser> list = new ArrayList<WTUser>();
		if (pbo instanceof WTPart) {
			WTPart part = (WTPart) pbo;
			WTContainer container = part.getContainer();
			ContainerTeam containerTeam = WorkflowUtil.getContainerTeam(container);
			Vector<Role> vec = containerTeam.getRoles();

			for (int i = 0; i < vec.size(); i++) {
				Role roleTemp = vec.get(i);
				GLLogger.debug("roleTemp===>" + roleTemp.getDisplay());
				if(!roleTemp.equals(role)) {
					continue;
				}
				Enumeration<WTPrincipalReference> eprin = containerTeam.getPrincipalTarget(roleTemp);
				while (eprin.hasMoreElements()) {
					WTPrincipal temp = eprin.nextElement().getPrincipal();
					if (temp instanceof WTUser) {
						list.add((WTUser)temp);
					}else if(temp instanceof WTGroup){
						WTGroup group = (WTGroup) temp;
						PrintUtil.searchGroupUserList(group, list);
					}
				}
			}
		}

		Persistable persistable = self.getObject();
		if (persistable instanceof WfProcess) {
			Transaction tx = new Transaction();
			WfProcess process = (WfProcess) persistable;
			Team team = (Team) process.getTeamId().getObject();
			for (WTUser wtUser : list) {
				GLLogger.debug("wtUser===>" + wtUser.getFullName());
				team.addPrincipal(role, wtUser);
			}
			team = (Team) PersistenceHelper.manager.refresh(team);
			team = (Team) PersistenceHelper.manager.save(team);
			tx.commit();
			tx = null;
		}
	}

	public static boolean setUserToTeamRole(ObjectReference self, String userName, String roleValue) {
		boolean flag = false;
		try {
			Persistable persistable = self.getObject();
			if (persistable instanceof WfProcess) {
				Transaction tx = new Transaction();
				WfProcess process = (WfProcess) persistable;
				Team team = (Team) process.getTeamId().getObject();
				Map map = team.getRolePrincipalMap();
				Role role = Role.toRole(roleValue);
				WTUser user = CSCPrincipal.getUserByName(userName);
				if (user != null) {
					team.addPrincipal(role, user);
				}
				team = (Team) PersistenceHelper.manager.refresh(team);
				team = (Team) PersistenceHelper.manager.save(team);
				tx.commit();
				tx = null;
			}

		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (TeamException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 判断ECR是否添加了附件，即是是否添加了“变更通知单”
	 *
	 * @param pbo
	 *            主要业务对象
	 * @return boolean
	 * @throws WTException
	 */
	public static String checkWhetherHasAttachmentForECN(WTObject pbo) {
		String msg = "ok";
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			// 判断是否有附件
			ContentHolder contentholder = null;
			try {
				contentholder = ContentHelper.service.getContents(changeOrder2);
				Vector vector = ContentHelper.getApplicationData(contentholder);
				if (vector == null || vector.isEmpty()) {
					msg = Constants.WF_CHECK_ECN_NOATTACHENT;
				}
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	/**
	 * 判断问题报告是否添加了附件，即是是否添加了“变更通知单”
	 *
	 * @param pbo
	 *            主要业务对象
	 * @return boolean
	 * @throws WTException
	 */
	public static String checkWhetherHasAttachmentForPR(WTObject pbo) {
		String msg = "ok";
		if (pbo instanceof WTChangeIssue) {
			WTChangeIssue changeIssue = (WTChangeIssue) pbo;
			// 判断是否有附件
			ContentHolder contentholder = null;
			try {
				contentholder = ContentHelper.service.getContents(changeIssue);
				Vector vector = ContentHelper.getApplicationData(contentholder);
				if (vector == null || vector.isEmpty()) {
					msg = Constants.WF_CHECK_PR_NOATTACHENT;
				}
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	/**
	 * 检查变更通过在编制任务环节是否正确添加了修改前和修改后的数据
	 *
	 * @param pbo
	 * @throws Exception
	 */
	public static String checkChangeObject(WTObject pbo) throws Exception {
		String msg = "ok";
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			QueryResult beforeResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			QueryResult afterResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			if (beforeResult == null || beforeResult.size() == 0) {
				msg = Constants.WF_CHECK_NO_BEFOREOBJ;
			}
			if (afterResult == null || afterResult.size() == 0) {
				if ("ok".equals(msg)) {
					msg = Constants.WF_CHECK_NO_AFTEROBJ;
				} else {
					msg = msg + "," + Constants.WF_CHECK_NO_AFTEROBJ;
				}
			}
		}
		return msg;
	}

	public static String checkChangeDataState(WTObject pbo) throws WTException {
		StringBuffer msg = new StringBuffer();
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			QueryResult beforeResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			while (beforeResult.hasMoreElements()) {
				Object object = beforeResult.nextElement();
				if (object instanceof EPMDocument) {
					EPMDocument epmDocument = (EPMDocument) object;
					String state = epmDocument.getState().getState().getDisplay(Locale.CHINA);
					if (!Constants.STATE_YIFABU.equals(state) && !Constants.STATE_YIGUIDANG.equals(state)) {
						msg.append(epmDocument.getNumber()).append(",");
					}
				} else if (object instanceof WTPart) {
					WTPart part = (WTPart) object;
					String state = part.getState().getState().getDisplay(Locale.CHINA);
					if (!Constants.STATE_YIFABU.equals(state) && !Constants.STATE_YIGUIDANG.equals(state)) {
						msg.append(part.getNumber()).append(",");
					}
				} else if (object instanceof WTDocument) {
					WTDocument document = (WTDocument) object;
					String state = document.getState().getState().getDisplay(Locale.CHINA);
					if (!Constants.STATE_YIFABU.equals(state) && !Constants.STATE_YIGUIDANG.equals(state)) {
						msg.append(document.getNumber()).append(",");
					}
				}
			}
		}
		if (msg != null && !"".equals(msg.toString())) {
			msg.append(" 以上编号的对象状态不是已发布或已归档，不能变更。");
		} else {
			msg.append("ok");
		}
		return msg.toString();
	}

	public static boolean checkIsXuBianZhi(ObjectReference self, String roleValue) {
	    boolean flag = false;

        try {
            Persistable persistable = self.getObject();
            if (persistable instanceof WfProcess) {
                WfProcess process = (WfProcess) persistable;
                ProcessData data = process.getContext();
                WfVariable variable = data.getVariable("GZSJXQYJ");
                Boolean value = (Boolean) variable.getValue();
                return value;

            }
            }catch(Exception e){
                e.printStackTrace();
            }
        return flag;
    }

	public static boolean checkWhetherExsitUser(ObjectReference self, String roleValue) {
		boolean flag = false;

		try {
			Persistable persistable = self.getObject();
			if (persistable instanceof WfProcess) {
				WfProcess process = (WfProcess) persistable;
				Team team = (Team) process.getTeamId().getObject();
				Map map = team.getRolePrincipalMap();
				Role role = Role.toRole(roleValue);
				Object object = map.get(role);
				if (object != null) {
					if (object instanceof List) {
						ArrayList list = (ArrayList) object;
						if (list != null && !list.isEmpty()) {
							flag = true;
						}
					}
				}
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (TeamException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return flag;
	}

	public static void terminateWorkItem(ObjectReference self) {
		try {
			Persistable persistable = self.getObject();
			if (persistable instanceof WfProcess) {
				WfProcess process = (WfProcess) persistable;
				// 终止所有的会签相关活动
				// WfBlock wfblock = PrintHelper.getBlock(process);
				List<WfBlock> allWfBlocks = PrintHelper.getAllBlock(process);
				List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
				for (WfBlock wfblock : allWfBlocks) {
					PrintHelper.getActivities(wfblock, activityList);
				}

				for (WfAssignedActivity wfAssignedActivity : activityList) {
					String name = wfAssignedActivity.getName();
					String state = wfAssignedActivity.getState().toString();
					// System.out.println("-----------wfAssignedActivity name:"
					// + name);
					if ("OPEN_RUNNING".equals(state)) {
						if (Constants.TASK_GONGYIHUIQIAN.equals(name) || Constants.TASK_ZHIPAIGONGYIYUAN.equals(name)
								|| Constants.TASK_ZHIPAIGONGYIHUIQIAN.equals(name)
								|| Constants.TASK_ZHIPAIGONGYIYUSHEN.equals(name)
								|| Constants.TASK_GONGYYUSHEN.equals(name)) {
							WfEngineHelper.service.changeState(wfAssignedActivity, WfTransition.TERMINATE);
						}
					}
				}

				for (WfBlock wfblock : allWfBlocks) {
					String name = wfblock.getName();
					String state = wfblock.getState().toString();
					System.out.println("-----------wfblock name:" + name);
					System.out.println("-----------wfblock state:" + state);
					if ("OPEN_RUNNING".equals(state)) {
						if (name.contains(Constants.TASK_WFBLOCK_YICHEJIAN)
								|| name.contains(Constants.TASK_WFBLOCK_ERCHEJIAN)
								|| name.contains(Constants.TASK_WFBLOCK_SANCHEJIAN)
								|| name.contains(Constants.TASK_WFBLOCK_SICHEJIAN)
								|| name.contains(Constants.TASK_WFBLOCK_WUCHEJIAN)
								|| name.contains(Constants.TASK_WFBLOCK_LIUCHEJIAN)
								|| name.contains(Constants.TASK_WFBLOCK_QICHEJIAN)
								|| name.contains(Constants.TASK_WFBLOCK_BACHEJIAN)
								|| name.contains(Constants.TASK_WFBLOCK_XIANGMUBU)
								|| name.contains(Constants.TASK_WFBLOCK_JIUCHEJIAN)) {
							System.out.println("-----------wfblock TERMINATE:");
							WfEngineHelper.service.changeState(wfblock, WfTransition.TERMINATE);
						}
					}
				}

				// 清空所有车间工艺组长和工艺员角色里的参与者
				Team team = (Team) process.getTeamId().getObject();
				Persistable pbo = (Persistable) process.getContext().getValue("primaryBusinessObject");
				HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
				List<String> allList = new ArrayList<String>();
				allList.add("YICHEJIANGONGYIYUAN");
				allList.add("ERCHEJIANGONGYIYUAN");
				allList.add("SANCHEJIANGONGYIYUAN");
				allList.add("SICHEJIANGONGYIYUAN");
				allList.add("WUCHEJIANGONGYIYUAN");
				allList.add("LIUCHEJIANGONGYIYUAN");
				allList.add("QICHEJIANGONGYIYUAN");
				allList.add("BACHEJIANGONGYIYUAN");
				allList.add("XIANGMUBUGONGYIYUAN");

				allList.add("XIANGMUBUGONGYIZUZHANG");
				allList.add("YICHEJIANGONGYIZUZHANG");
				allList.add("ERCHEJIANGONGYIZUZHANG");
				allList.add("SANCHEJIANGONGYIZUZHANG");
				allList.add("SICHEJIANGONGYIZUZHANG");
				allList.add("WUCHEJIANGONGYIZUZHANG");
				allList.add("LIUCHEJIANGONGYIZUZHANG");
				allList.add("QICHEJIANGONGYIZUZHANG");
				allList.add("BACHEJIANGONGYIZUZHANG");
				for (String roleName : allList) {
					Role role = Role.toRole(roleName);
					if (role != null) {
						List tempUserList = (List) rolePrincipalListMap.get(role);
						for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
							WTUser user1 = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
							team.deletePrincipalTarget(role, user1);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static QueryResult getWfAsActivityByWfProcess(WfProcess process) throws WTException {
		QuerySpec qSpec = new QuerySpec(WfAssignedActivity.class);
		int index[] = { 0 };
		long longId = PersistenceHelper.getObjectIdentifier(process).getId();
		SearchCondition sCondition = new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
				SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		qSpec.appendAnd();
		qSpec.appendOpenParen();
		sCondition = new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.NAME, SearchCondition.EQUAL,
				Constants.ACTIVITYNAME_ZPGYHQ);
		qSpec.appendWhere(sCondition, index);
		qSpec.appendOr();
		sCondition = new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.NAME, SearchCondition.EQUAL,
				"工艺会签");
		qSpec.appendWhere(sCondition, index);
		qSpec.appendOr();
		sCondition = new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.NAME, SearchCondition.EQUAL,
				"指派工艺员");
		qSpec.appendWhere(sCondition, index);
		qSpec.appendCloseParen();

		return PersistenceHelper.manager.find((StatementSpec) qSpec);
	}

	public static WfProcess getWfProcessByName(String name) throws WTException {
		QuerySpec qSpec = new QuerySpec(WfProcess.class);
		int index[] = { 0 };
		SearchCondition sCondition = new SearchCondition(WfProcess.class, WfProcess.NAME, SearchCondition.EQUAL, name);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		if (qResult.hasMoreElements()) {
			return (WfProcess) qResult.nextElement();
		}
		return null;
	}

	/**
	 * 设置对象软属性的值
	 *
	 * @param ibaHolder
	 *            对象
	 * @param ibaName
	 *            属性名
	 * @param ibaValue
	 *            属性值
	 */
	public static void setIBAValue(IBAHolder ibaHolder, String ibaName, String ibaValue) {
		try {
			IBAUtility ibaUtility = new IBAUtility(ibaHolder);
			ibaUtility.setIBAValue(ibaName, ibaValue);
			ibaHolder = ibaUtility.updateAttributeContainer(ibaHolder);
			ibaUtility.updateIBAHolder(ibaHolder);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void setObjectLifeCycle(LifeCycleManaged obj, String lf) {
		// SessionContext previous = SessionContext.getContext();
		boolean check = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			// SessionHelper.manager.setAdministrator();
			Vector phaseVector = LifeCycleHelper.service.getPhaseTemplates(LifeCycleHelper.service
					.getLifeCycleTemplate(obj));
			for (int j = 0; j < phaseVector.size(); j++) {
				PhaseTemplate phase = (PhaseTemplate) phaseVector.get(j);
				if (phase.getPhaseState().equals(State.toState(lf))) {
					LifeCycleHelper.service.setLifeCycleState(obj, State.toState(lf));
				}
			}
		} catch (WTException wte) {
			wte.printStackTrace();
		} finally {
			// SessionContext.setContext(previous);
			SessionServerHelper.manager.setAccessEnforced(check);
		}
	}

	/**
	 * 设置流程主要业务对象的生命周期状态
	 *
	 * @param pbo
	 *            流程主要业务对象
	 * @param state
	 *            对象状态
	 */
	public static void setObjectState(WTObject pbo, String state) {

		try {
			State lfState = State.toState(state);
			if (lfState == null) {
				System.out.println("--------state:" + state + " is not exist!");
				return;
			}
			boolean check = SessionServerHelper.manager.setAccessEnforced(false);
			if (pbo instanceof ProcessEnvelope) {
				ProcessEnvelope processEnvelope = (ProcessEnvelope) pbo;
				List list = ProcessEnvelopeUtil.getAllMemberLinks(processEnvelope);
				for (Object object : list) {
					if (object instanceof WTPart) {
						WTPart part = (WTPart) object;
						LifeCycleHelper.service.setLifeCycleState(part, lfState);
					} else if (object instanceof WTDocument) {
						WTDocument document = (WTDocument) object;
						LifeCycleHelper.service.setLifeCycleState(document, lfState);
					} else if (object instanceof EPMDocument) {
						EPMDocument epmDocument = (EPMDocument) object;
						LifeCycleHelper.service.setLifeCycleState(epmDocument, lfState);
					}
				}
			} else if (pbo instanceof WTChangeOrder2) {
				WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
				//LifeCycleHelper.service.setLifeCycleState(changeOrder2, lfState);

				/*WTObject targetObj = PrintHelper.getReleatedDocByECN(changeOrder2);
	            if(targetObj != null){
	            	if (targetObj instanceof WTDocument) {
						WTDocument document = (WTDocument) targetObj;
						LifeCycleHelper.service.setLifeCycleState(document, lfState);
					}
	            }*/
				String type = IBAHelper.getSoftType(changeOrder2);
				QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
				while (qResult.hasMoreElements()) {
					Object object = qResult.nextElement();
					if (object instanceof WTPart) {
						WTPart part = (WTPart) object;
						LifeCycleHelper.service.setLifeCycleState(part, lfState);
					} else if (object instanceof WTDocument) {
						WTDocument document = (WTDocument) object;
						LifeCycleHelper.service.setLifeCycleState(document, lfState);
					} else if (object instanceof EPMDocument) {
						EPMDocument epmDocument = (EPMDocument) object;
						LifeCycleHelper.service.setLifeCycleState(epmDocument, lfState);
					} else if (object instanceof MPMProcessPlan) {
						MPMProcessPlan processPlan = (MPMProcessPlan) object;
						LifeCycleHelper.service.setLifeCycleState(processPlan, lfState);
						if (type.equalsIgnoreCase("PROCESS_ECN")) {
							List<MPMOperation> list = WCUtil.getAllMpmOperationsByMPMProPlan(processPlan);
							if (list != null && !list.isEmpty()) {
								for (MPMOperation mpmOperation : list) {
									mpmOperation = (MPMOperation) WCUtil.getLatestObject((Master) mpmOperation
											.getMaster());
									LifeCycleHelper.service.setLifeCycleState(mpmOperation, lfState);
								}
							}
						}
					}
				}
			} else if (pbo instanceof WTPart) {
				WTPart part = (WTPart) pbo;
				LifeCycleHelper.service.setLifeCycleState(part, lfState);
			} else if (pbo instanceof WTDocument) {
				WTDocument document = (WTDocument) pbo;
				LifeCycleHelper.service.setLifeCycleState(document, lfState);
			} else if (pbo instanceof EPMDocument) {
				EPMDocument epmDocument = (EPMDocument) pbo;
				LifeCycleHelper.service.setLifeCycleState(epmDocument, lfState);
			} else if (pbo instanceof MPMProcessPlan) {
				MPMProcessPlan plan = (MPMProcessPlan) pbo;
				List<MPMOperation> list = WCUtil.getAllMpmOperationsByMPMProPlan(plan);
				if (list != null && !list.isEmpty()) {
					for (MPMOperation mpmOperation : list) {
						LifeCycleHelper.service.setLifeCycleState(mpmOperation, lfState);
					}
				}
			}
			SessionServerHelper.manager.setAccessEnforced(check);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置流程活动中指定角色的用户
	 *
	 * @param pbo
	 * @param reference
	 * @throws WTException
	 */
	public static void setupUserForReviewer(WTObject pbo, Object self, String RoleName) throws WTException {
		List<WTUser> allUsers = getUserByRole(pbo, Constants.ROLE_GONGYIZUZHANG);
		Team team = null;
		ObjectReference reference = (ObjectReference) self;
		WfProcess process = (WfProcess) reference.getObject();
		team = (Team) process.getTeamId().getObject();
		if (team != null) {
			Role role = Role.toRole(RoleName);
			for (WTUser wtUser : allUsers) {
				TeamHelper.service.addRolePrincipalMap(role, wtUser, team);
			}
			team = (Team) PersistenceHelper.manager.refresh(team);
			team = (Team) PersistenceHelper.manager.save(team);
		}
	}

	/**
	 * 设置流程活动中指定角色的用户
	 *
	 * @param pbo
	 * @param reference
	 * @throws WTException
	 */
	public static void setContainerUserToWorkFlowUserByRole(WTObject pbo, Object self, String roleKey)
			throws WTException {
		WTContainer container = null;
		System.out.println("-------getUserForManager pbo:" + pbo);
		if (pbo instanceof WTDocument) {
			WTDocument document = (WTDocument) pbo;
			container = document.getContainer();
		} else if (pbo instanceof ManagedBaseline) {
			ManagedBaseline bl = (ManagedBaseline) pbo;
			container = bl.getContainer();
		} else if (pbo instanceof WTPart) {
			WTPart part = (WTPart) pbo;
			container = part.getContainer();
		} else if (pbo instanceof ProcessEnvelope) {
			ProcessEnvelope pe = (ProcessEnvelope) pbo;
			container = pe.getContainer();
		} else if (pbo instanceof ChangePackaged) {
			ChangePackaged pe = (ChangePackaged) pbo;
			container = pe.getContainer();
		} else if (pbo instanceof ChangeRequest) {
			ChangeRequest pe = (ChangeRequest) pbo;
			container = pe.getContainer();
		}
		Role role = Role.toRole(roleKey);
		List<WTUser> allUsers = getRoleUsersByWTContainer(roleKey, container);
		Team team = null;
		ObjectReference reference = (ObjectReference) self;
		WfProcess process = (WfProcess) reference.getObject();
		team = (Team) process.getTeamId().getObject();
		if (team != null) {
			for (WTUser wtUser : allUsers) {
				TeamHelper.service.addRolePrincipalMap(role, wtUser, team);
			}
			team = (Team) PersistenceHelper.manager.refresh(team);
			team = (Team) PersistenceHelper.manager.save(team);
		}
	}

	/**
	 * 将"指派工艺组长者"角色里面的人添加到"数据管理员"角色里面
	 *
	 * @param self
	 *            流程
	 * @param roleValue
	 *            数据管理员角色
	 */
	public static void setDataManagerUsers(ObjectReference self, String roleValue) {
		Transaction tx = new Transaction();
		try {
			tx.start();
			Persistable persistable = self.getObject();
			if (persistable instanceof WfProcess) {
				WfProcess process = (WfProcess) persistable;
				Team team = (Team) process.getTeamId().getObject();
				HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
				Role zhurengongyishi = Role.toRole("ZHIPAIGONGYIZUZHANGZHE");
				Role datamanager = Role.toRole(roleValue);
				if (zhurengongyishi == null || datamanager == null) {
					System.out.println("***************Role is not exist");
					return;
				}
				List tempUserList = (List) rolePrincipalListMap.get(zhurengongyishi);
				if (tempUserList != null) {
					for (Object object2 : tempUserList) {
						if (object2 instanceof WTPrincipalReference) {
							WTPrincipalReference wtPrincipalReference = (WTPrincipalReference) object2;
							Persistable persistable2 = wtPrincipalReference.getObject();
							if (persistable2 instanceof WTUser) {
								WTUser user = (WTUser) persistable2;
								team.addPrincipal(datamanager, user);
							}
						}
					}
					team = (Team) PersistenceHelper.manager.refresh(team);
					team = (Team) PersistenceHelper.manager.save(team);
				}
			}

			tx.commit();
			tx = null;
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (TeamException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			if (tx != null) {
				tx.rollback();
				tx = null;
			}
		}
	}

	/**
	 * 变更批准后三维模型的所有子件、关联的二维图纸状态设为“已批准”。
	 *
	 * @param pbo
	 * @throws Exception
	 */
	public static void processApproveData(WTObject pbo) throws Exception {
		if (pbo instanceof EPMDocument) {
			EPMDocument epmDocument = (EPMDocument) pbo;
			Set<EPMDocument> cad2w = WCUtil.get2DesignDocs(epmDocument);
			Iterator<EPMDocument> iterator = cad2w.iterator();
			while (iterator.hasNext()) {
				EPMDocument epm2 = (EPMDocument) iterator.next();
				CmLifecycleHelper.setLifecycleState(epm2, Constants.STATE_APPROVED);
				QueryResult qResult = WCUtil.getEPMBuildRoles(epm2);
				while (qResult.hasMoreElements()) {
					EPMBuildRule rule = (EPMBuildRule) qResult.nextElement();
					WTPart part = (WTPart) rule.getRoleBObject();
					CmLifecycleHelper.setLifecycleState(part, Constants.STATE_APPROVED);
				}
			}
			HashSet hashSet = new HashSet();
			Set<EPMDocument> cad3w = WCUtil.getMemberEPMDoc(epmDocument, hashSet);
			Iterator<EPMDocument> iterator2 = cad3w.iterator();
			while (iterator2.hasNext()) {
				EPMDocument epm3 = (EPMDocument) iterator2.next();
				CmLifecycleHelper.setLifecycleState(epm3, Constants.STATE_APPROVED);
				QueryResult qResult = WCUtil.getEPMBuildRoles(epm3);
				while (qResult.hasMoreElements()) {
					EPMBuildRule rule = (EPMBuildRule) qResult.nextElement();
					WTPart part = (WTPart) rule.getRoleBObject();
					CmLifecycleHelper.setLifecycleState(part, Constants.STATE_APPROVED);
				}
			}
			// 设置其相关连部件的生命周期状态到“已批准”
			QueryResult qResult = WCUtil.getEPMBuildRoles(epmDocument);
			while (qResult.hasMoreElements()) {
				EPMBuildRule rule = (EPMBuildRule) qResult.nextElement();
				WTPart part = (WTPart) rule.getRoleBObject();
				CmLifecycleHelper.setLifecycleState(part, Constants.STATE_APPROVED);
			}
		}
	}

	/**
	 * 修订图档
	 *
	 * @param pbo
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static void newVersion(WTObject pbo) throws WTException, WTPropertyVetoException, RemoteException,
			InvocationTargetException {
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;

			// 获取ECA
			QueryResult ecaResult = ChangeHelper2.service.getChangeActivities(changeOrder2);
			List<WTChangeActivity2> list = new ArrayList<WTChangeActivity2>();
			while (ecaResult.hasMoreElements()) {
				WTChangeActivity2 activity2 = (WTChangeActivity2) ecaResult.nextElement();
				list.add(activity2);
			}

			QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			while (qResult.hasMoreElements()) {
				Object object = qResult.nextElement();
				if (WorkInProgressHelper.isCheckedOut((Workable) object)) {
					continue;
				}
				if (object instanceof EPMDocument) {
					EPMDocument epmDocument = (EPMDocument) object;
					revise(epmDocument, list);
				} else if (object instanceof WTPart) {
					WTPart part = (WTPart) object;
					revise(part, list);
				} else if (object instanceof WTDocument) {
					WTDocument document = (WTDocument) object;
					revise(document, list);
				} else if (object instanceof MPMProcessPlan) {
					MPMProcessPlan processPlan = (MPMProcessPlan) object;
					revise(processPlan, list);
				}
			}
		}
	}

	/**
	 * 修订变更受影响 数据
	 *
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static String collectAndreviseChangeBefore(WTObject object) throws Exception {
		String result = "SUCCESS";
		WTChangeOrder2 co2 = null;
		WTChangeActivity2 activity = null;
		WTCollection col = null;
		// ChangeItemIfc cii = null;
		Class<?> class2 = Class.forName("wt.change2.AffectedActivityData");

		if (object instanceof WTChangeOrder2) {
			co2 = (WTChangeOrder2) object;
			ArrayList<Changeable2> relatedPartList = new ArrayList<Changeable2>();
			ArrayList<Changeable2> relatedDocList = new ArrayList<Changeable2>();
			ArrayList<Changeable2> relatedEPMDocList = new ArrayList<Changeable2>();
			QueryResult qrBefore1 = ChangeHelper2.service.getChangeablesBefore(co2);
			QueryResult caqr = ChangeHelper2.service.getChangeActivities(co2);

			while (caqr.hasMoreElements()) {
				activity = (WTChangeActivity2) caqr.nextElement();
			}
			if (activity != null) {
				while (qrBefore1.hasMoreElements()) {
					Object changeBefore = qrBefore1.nextElement();
					if (changeBefore instanceof EPMDocument) {
						EPMDocument oldepm = (EPMDocument) changeBefore;
						Set<EPMDocument> set2 = WCUtil.get2DesignDocs(oldepm);
						Vector<Persistable> vector = new Vector<Persistable>();
						vector.addAll(set2);
						ChangeHelper2.service.storeAssociations(class2, activity, vector);
					} else if (changeBefore instanceof WTPart) {
						WTPart part = (WTPart) changeBefore;
						QueryResult qResult = WCUtil.getEPMBuildRoles(part);
						Vector<Persistable> vector = new Vector<Persistable>();
						while(qResult.hasMoreElements()) {
							EPMBuildRule rule = (EPMBuildRule) qResult.nextElement();
							Object ruleA = rule.getRoleAObject();
							if (ruleA instanceof EPMDocument) {
								EPMDocument epmDocument = (EPMDocument) ruleA;
								vector.add(epmDocument);
								Set<EPMDocument> set2 = WCUtil.get2DesignDocs(epmDocument);
								vector.addAll(set2);
							}
						}
						ChangeHelper2.service.storeAssociations(class2, activity, vector);
					}
				}
			}

			try {
				// QueryResult qr =
				// ChangeHelper2.service.getChangeActivities(co2);
				// while (qr.hasMoreElements()) {
				// cii = (ChangeItemIfc)qr.nextElement();
				// activity = (WTChangeActivity2) qr.nextElement();
				QueryResult qrBefore = ChangeHelper2.service.getChangeablesBefore(activity);
				while (qrBefore.hasMoreElements()) {
					Changeable2 changeable = (Changeable2) qrBefore.nextElement();
					if (changeable instanceof WTPart) {
						relatedPartList.add(changeable);
					} else if (changeable instanceof EPMDocument) {
						relatedEPMDocList.add(changeable);
					} else {
						relatedDocList.add(changeable);
					}
				}
				// }
				WTCollection wtCol = new WTArrayList();
				wtCol.addAll(relatedPartList);
				wtCol.addAll(relatedEPMDocList);
				wtCol.addAll(relatedDocList);
				WTValuedMap map = VersionControlHelper.service.newVersions(wtCol);
				wtCol.clear();
				Set keySet = map.keySet();
				for (Iterator iter = keySet.iterator(); iter.hasNext();) {
					ObjectReference key = (ObjectReference) iter.next();
					RevisionControlled ver = (RevisionControlled) key.getObject();
					ObjectReference value = (ObjectReference) map.get(key);
					RevisionControlled newVer = (RevisionControlled) value.getObject();
					Folder folder = FolderHelper.getFolder(ver);
					FolderHelper.assignLocation(newVer, folder);
					wtCol.add(newVer);
				}
				col = PersistenceHelper.manager.save(wtCol);
			} catch (Exception e) {
				e.printStackTrace();
				result = "ERROR";
				return result;
			}
			// 处理修订后的数据
			Vector<Persistable> vectorAfter = new Vector<Persistable>();

			for (Iterator iter = col.iterator(); iter.hasNext();) {
				ObjectReference objRef = (ObjectReference) iter.next();
				WTObject tempObj = (WTObject) objRef.getObject();
				vectorAfter.add(tempObj);

				// addChangeableAfterToChangeNotice(co2, (Changeable2) tempObj, "");
			}
			Class<?> class1 = Class.forName("wt.change2.ChangeRecord2");
			ChangeHelper2.service.storeAssociations(class1, activity, vectorAfter);
		}
		return result;

	}

	/**
	 * 修订对象 1、如果是图纸，将修订后的图纸和部件添加到ECN对象产生对象列表中。
	 * 2、如果是部件，将修订后的图纸和部件添加到ECN对象产生对象列表中。 3、如果是文档，则将修订后的文档添加到ECN对象产生对象列表中。
	 *
	 * @param versioned
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void revise(Versioned versioned, List<WTChangeActivity2> list) throws WTException,
			WTPropertyVetoException {
		Transaction tx = new Transaction();
		try {
			tx.start();
			Vector<Object> vector = new Vector<Object>();
			WTUser currentUser = null;
			boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
			if (versioned instanceof EPMDocument) {
				EPMDocument epmDocument = (EPMDocument) versioned;
				currentUser = (WTUser) epmDocument.getCreator().getObject();
				SessionHelper.manager.setPrincipal(currentUser.getName());
				// 修订图档
				EPMDocument newDocument = (EPMDocument) VersionControlHelper.service.newVersion(epmDocument);
				newDocument = (EPMDocument) PersistenceHelper.manager.save(newDocument);
				vector.add(newDocument);

				// 获得2维子件
				Set<EPMDocument> set2 = WCUtil.get2DesignDocs(newDocument);
				Iterator<EPMDocument> iterator = set2.iterator();
				while (iterator.hasNext()) {
					EPMDocument epm2 = (EPMDocument) iterator.next();
					if (WorkInProgressHelper.isCheckedOut((Workable) epm2)) {
						continue;
					}
					currentUser = (WTUser) epm2.getCreator().getObject();
					SessionHelper.manager.setPrincipal(currentUser.getName());
					epm2 = (EPMDocument) VersionControlHelper.service.newVersion(epm2);
					epm2 = (EPMDocument) PersistenceHelper.manager.save(epm2);
					vector.add(epm2);
				}
			} else if (versioned instanceof WTPart) {
				WTPart part = (WTPart) versioned;
				QueryResult qResult = WCUtil.getEPMBuildRoles(part);
				if (qResult.hasMoreElements()) {
					Object object = qResult.nextElement();
					if (object instanceof EPMBuildRule) {
						EPMBuildRule rule = (EPMBuildRule) object;
						Object ruleA = rule.getRoleAObject();
						if (ruleA instanceof EPMDocument) {
							EPMDocument epmDocument = (EPMDocument) ruleA;

							currentUser = (WTUser) part.getCreator().getObject();
							SessionHelper.manager.setPrincipal(currentUser.getName());
							// 修订关连的部件
							WTPart newPart = (WTPart) VersionControlHelper.service.newVersion(part);
							newPart = (WTPart) PersistenceHelper.manager.save(newPart);
							vector.add(newPart);
							// 修订图档

							epmDocument = (EPMDocument) WCUtil.getLatestObject((Master) epmDocument.getMaster());

							currentUser = (WTUser) epmDocument.getCreator().getObject();
							SessionHelper.manager.setPrincipal(currentUser.getName());
							EPMDocument newDocument = (EPMDocument) VersionControlHelper.service
									.newVersion(epmDocument);
							newDocument = (EPMDocument) PersistenceHelper.manager.save(newDocument);
							vector.add(newDocument);

							// 获得2维子件
							Set<EPMDocument> set2 = WCUtil.get2DesignDocs(newDocument);
							Iterator<EPMDocument> iterator = set2.iterator();
							List<String> allDrwList = new ArrayList<String>();
							while (iterator.hasNext()) {
								EPMDocument epm2 = (EPMDocument) iterator.next();
								epm2 = (EPMDocument) WCUtil.getLatestObject((Master) epm2.getMaster());
								if (WorkInProgressHelper.isCheckedOut((Workable) epm2)) {
									continue;
								}
								if (allDrwList.contains(epm2.getNumber())) {
									continue;
								}
								allDrwList.add(epm2.getNumber());

								currentUser = (WTUser) epm2.getCreator().getObject();
								SessionHelper.manager.setPrincipal(currentUser.getName());
								epm2 = (EPMDocument) VersionControlHelper.service.newVersion(epm2);
								epm2 = (EPMDocument) PersistenceHelper.manager.save(epm2);
								vector.add(epm2);
							}

							// 新建修订后的部件和图档的Link
							// EPMBuildRule epmBuildRule =
							// EPMBuildRule.newEPMBuildRule(newDocument,
							// newPart);
							// PersistenceHelper.manager.save(epmBuildRule);
						}
					}
				} else {
					currentUser = (WTUser) part.getCreator().getObject();
					SessionHelper.manager.setPrincipal(currentUser.getName());
					// 修订关连的部件
					WTPart newPart = (WTPart) VersionControlHelper.service.newVersion(part);
					newPart = (WTPart) PersistenceHelper.manager.save(newPart);
					vector.add(newPart);
				}
			} else if (versioned instanceof WTDocument) {
				currentUser = (WTUser) versioned.getCreator().getObject();
				SessionHelper.manager.setPrincipal(currentUser.getName());
				WTDocument newDocument = (WTDocument) VersionControlHelper.service.newVersion(versioned);
				newDocument = (WTDocument) PersistenceHelper.manager.save(newDocument);
				vector.add(newDocument);
			} else if (versioned instanceof MPMProcessPlan) {
				currentUser = (WTUser) versioned.getCreator().getObject();
				SessionHelper.manager.setPrincipal(currentUser.getName());
				MPMProcessPlan processPlan = (MPMProcessPlan) VersionControlHelper.service.newVersion(versioned);
				processPlan = (MPMProcessPlan) PersistenceHelper.manager.save(processPlan);
				vector.add(processPlan);

				List<MPMOperation> allList = WCUtil.getAllMpmOperationsByMPMProPlan(processPlan);
				if (allList != null && !allList.isEmpty()) {
					List<String> tempList = new ArrayList<String>();
					for (MPMOperation mpmOperation : allList) {
						mpmOperation = (MPMOperation) WCUtil.getLatestObject((Master) mpmOperation.getMaster());
						if (WorkInProgressHelper.isCheckedOut((Workable) mpmOperation)) {
							continue;
						}
						if (tempList.contains(mpmOperation.getNumber())) {
							continue;
						}
						tempList.add(mpmOperation.getNumber());
						currentUser = (WTUser) mpmOperation.getCreator().getObject();
						SessionHelper.manager.setPrincipal(currentUser.getName());
						mpmOperation = (MPMOperation) VersionControlHelper.service.newVersion(mpmOperation);
						mpmOperation = (MPMOperation) PersistenceHelper.manager.save(mpmOperation);
						// vector.add(mpmOperation);
					}
				}
			}

			// 添加到产生对象列表中
			for (WTChangeActivity2 wtChangeActivity2 : list) {
				ChangeHelper2.service.storeAssociations(ChangeRecord2.class, wtChangeActivity2, vector);
			}
			SessionServerHelper.manager.setAccessEnforced(flag);
			tx.commit();
			tx = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tx != null) {
				tx.rollback();
			}
		}
	}

	public static boolean isPackagedPart(String oid) throws WTRuntimeException, WTException {
		boolean flag = false;
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
		if (pbo instanceof ProcessEnvelope) {
			ProcessEnvelope pe = (ProcessEnvelope) pbo;
			RevisionControlled rc = ProcessEnvelopeUtil.getTopObject(pe);
			if (rc != null) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 获取工艺指示的URL
	 *
	 * @param oid
	 *            任务活动的OID
	 * @return 工艺指示的URL
	 */
	public static String getWorkInstructionsUrl(String oid) {
		try {
			ReferenceFactory rf = new ReferenceFactory();
			WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
			WfActivity activity = (WfActivity) wi.getSource().getObject();
			WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
			if (wfcont instanceof WfBlock) {
				WfBlock wb = (WfBlock) wfcont;
				wfcont = wb.getParentProcess();
			}
			WfProcess process = (WfProcess) wfcont;
			WTReference reference = process.getBusinessObjectReference(new ReferenceFactory());
			Object object = reference.getObject();
			if (object instanceof WTChangeOrder2) {
				WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
				ArrayList list = ChangeHelper.getChangeResultItem(changeOrder2);
				for (Object object2 : list) {
					if (object2 instanceof MPMProcessPlan) {
						MPMProcessPlan plan = (MPMProcessPlan) object2;
						WTProperties prop = WTProperties.getLocalProperties();
						String hName = prop.getProperty("wt.server.hostname");
						String webPort = prop.getProperty("wt.webserver.port");
						ReferenceFactory factory = new ReferenceFactory();
						String planOid = factory.getReferenceString(plan);
						String containerOid = factory.getReferenceString(plan.getContainer());
						String url = "http://" + hName + ":" + webPort
								+ "/Windchill/netmarkets/jsp/mpml/LaunchWIforProcessplan.jsp?" + "oid=" + planOid
								+ "&ContainerOid=" + containerOid;
						System.out.println(">>>>>>url:" + url);
						return url;
					}
				}
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getMPMProcessPlanNumber(String oid) {
		try {
			ReferenceFactory rf = new ReferenceFactory();
			WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
			WfActivity activity = (WfActivity) wi.getSource().getObject();
			WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
			if (wfcont instanceof WfBlock) {
				WfBlock wb = (WfBlock) wfcont;
				wfcont = wb.getParentProcess();
			}
			WfProcess process = (WfProcess) wfcont;
			WTReference reference = process.getBusinessObjectReference(new ReferenceFactory());
			Object object = reference.getObject();
			if (object instanceof WTChangeOrder2) {
				WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
				ArrayList list = ChangeHelper.getChangeResultItem(changeOrder2);
				for (Object object2 : list) {
					if (object2 instanceof MPMProcessPlan) {
						MPMProcessPlan plan = (MPMProcessPlan) object2;
						return plan.getNumber();
					}
				}
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取产品团队下的指定角色下的用户
	 *
	 * @param pbo
	 * @return
	 * @throws WTException
	 */
	public static List<WTUser> getUserByRole(WTObject pbo, String roleName) throws WTException {
		WTContained contained = null;
		System.out.println("-------getUserForManager pbo:" + pbo);
		if (pbo instanceof WTDocument) {
			WTDocument document = (WTDocument) pbo;
			contained = document.getContainer();
		} else if (pbo instanceof ManagedBaseline) {
			ManagedBaseline bl = (ManagedBaseline) pbo;
			contained = bl.getContainer();
		} else if (pbo instanceof WTPart) {
			WTPart part = (WTPart) pbo;
			contained = part.getContainer();
		} else if (pbo instanceof ProcessEnvelope) {
			ProcessEnvelope pe = (ProcessEnvelope) pbo;
			contained = pe.getContainer();
		} else if (pbo instanceof ChangePackaged) {
			ChangePackaged pe = (ChangePackaged) pbo;
			contained = pe.getContainer();
		} else if (pbo instanceof ChangeRequest) {
			ChangeRequest pe = (ChangeRequest) pbo;
			contained = pe.getContainer();
		}
		System.out.println("-------contained:" + contained);
		ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
		ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
		Vector<Role> vector = containerTeam.getRoles();
		System.out.println("-------vector:" + vector);
		Iterator<Role> iterator = vector.iterator();
		Role role = null;
		ArrayList<WTPrincipalReference> allUser = new ArrayList<WTPrincipalReference>();
		List<WTUser> list = new ArrayList<WTUser>();
		while (iterator.hasNext()) {
			role = iterator.next();
			// 过滤角色
			String name = role.getDisplay(Locale.CHINA);
			// 获取工艺组长角色的用户
			if (contained instanceof PDMLinkProduct) {
				if (roleName.equals(name)) {
					allUser = containerTeam.getAllPrincipalsForTarget(role);
					for (WTPrincipalReference ref : allUser) {
						Persistable per = ref.getObject();
						if (per instanceof WTUser) {
							WTUser user = (WTUser) per;
							list.add(user);
						} else if (per instanceof WTGroup) {
							WTGroup group = (WTGroup) per;
							list = getUserFromWTGroup(group, list);
						}
					}
					break;
				}
			}
		}
		System.out.println("------>>>>>ReturnList=" + list);
		return list;
	}

	public static List<WTUser> getUserFromLibrary(WTPart part, String roleName) throws WTException {
		WTContained contained = null;
		contained = part.getContainer();
		System.out.println("-------contained:" + contained);
		ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
		ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
		Vector<Role> vector = containerTeam.getRoles();
		System.out.println("-------vector:" + vector);
		Iterator<Role> iterator = vector.iterator();
		Role role = null;
		ArrayList<WTPrincipalReference> allUser = new ArrayList<WTPrincipalReference>();
		List<WTUser> list = new ArrayList<WTUser>();
		while (iterator.hasNext()) {
			role = iterator.next();
			// 过滤角色
			String name = role.getDisplay(Locale.CHINA);
			// 获取工艺组长角色的用户
			if (contained instanceof WTLibrary) {
				if (roleName.equals(name)) {
					allUser = containerTeam.getAllPrincipalsForTarget(role);
					for (WTPrincipalReference ref : allUser) {
						Persistable per = ref.getObject();
						if (per instanceof WTUser) {
							WTUser user = (WTUser) per;
							list.add(user);
						} else if (per instanceof WTGroup) {
							WTGroup group = (WTGroup) per;
							list = getUserFromWTGroup(group, list);
						}
					}
					break;
				}
			}
		}
		System.out.println("------>>>>>ReturnList=" + list);
		return list;
	}

	/**
	 * get users from group
	 *
	 * @param group
	 * @param list
	 * @throws WTException
	 */
	public static List getUserFromWTGroup(WTGroup group, List list) throws WTException {
		if (group == null || list == null) {
			return list;
		}
		Enumeration member = group.members();
		while (member.hasMoreElements()) {
			WTPrincipal principal = (WTPrincipal) member.nextElement();
			if (principal instanceof WTUser) {
				list.add((WTUser) principal);
			} else if (principal instanceof WTGroup) {
				getUserFromWTGroup((WTGroup) principal, list);
			}
		}
		return list;
	}

	public static List<WTUser> getRoleUsersByWTContainer(String roleKey, WTContainer wtContainer) throws WTException {
		List<WTUser> list = new ArrayList<WTUser>();
		Role role = Role.toRole(roleKey);
		if (role == null) {
			return list;
		}
		ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
		ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
		for (WTPrincipalReference reference : arrayList) {
			Object object2 = reference.getPrincipal();
			if (object2 instanceof WTUser) {
				WTUser user = (WTUser) object2;
				list.add(user);
			}
		}
		return list;
	}

	private static ApplicationData getPDFRepressentFile(WTDocument doc) throws WTException, PropertyVetoException {
		Representation representation = RepresentationHelper.service.getDefaultRepresentation(doc);
		if (representation != null) {
			representation = (Representation) ContentHelper.service.getContents(representation);
			Vector vector1 = ContentHelper.getContentList(representation);
			for (int l = 0; l < vector1.size(); l++) {
				ContentItem contentitem = (ContentItem) vector1.elementAt(l);
				if (contentitem instanceof ApplicationData) {
					ApplicationData applicationdata = (ApplicationData) contentitem;
					String filename = applicationdata.getFileName();
					String extention = Util.getExtension(filename);
					String removeExtention = Util.removeExtension(filename);
					if (extention.equalsIgnoreCase("PDF")) {
						return applicationdata;
					}
				}
			}
		}
		 ContentHolder holder = ContentHelper.service.getContents(doc);
         Vector apps = ContentHelper.getApplicationData(holder);

         for (Enumeration e = apps.elements(); e.hasMoreElements();) {
             ApplicationData contentItem = (ApplicationData) e.nextElement();
             String applicationdataRole = contentItem.getRole().toString();
             if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
                 continue;// 不是附件

             if (contentItem.getFileName().startsWith("PDFPreview")) {
            	 return contentItem;
             }
         }
		return null;
	}

	public static String checkPublishFile(Object o) throws WTException, PropertyVetoException, RemoteException {
		String result = "";
		if (o instanceof WTDocument) {
			WTDocument doc = (WTDocument) o;
			ApplicationData app = getPDFRepressentFile(doc);
			String docInfo = doc.getNumber() + "_" + doc.getVersionIdentifier().getValue() + "."
					+ doc.getIterationIdentifier().getValue();
			if (o instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) o)) {
				result = docInfo + "被检出,请检入之后再做此操作!";
				return result;
			}
			if (app == null) {
				if (result.equals("")) {
					result = docInfo + "发布失败，请联系管理员查看发布情况，或者手工上载PDF附件！";
				} else {
					result = result + "," + docInfo + "发布失败，请联系管理员查看发布情况，或者手工上载PDF附件！";
				}
			}
			boolean addPDFFlag = false;
			ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) o);
			ApplicationData primaryFile = (ApplicationData) ContentHelper.service.getPrimary((FormatContentHolder) o);
			if (primaryFile != null) {
				String fileName = primaryFile.getFileName();
				String fileNameStart = fileName.substring(0, fileName.lastIndexOf("."));
				String pdfFileName = fileNameStart + ".pdf";
				Vector apps = ContentHelper.getApplicationData(contentHolder);
				for (Object app1 : apps) {
					if (app1 instanceof ApplicationData) {
						ApplicationData ad = (ApplicationData) app1;
						if (ad.getFileName().equalsIgnoreCase(pdfFileName)) {
							addPDFFlag = true;
							break;
						}
					}
				}
				if (addPDFFlag) {
					result = "";
				}
			} else {
				if (result.equals("")) {
					result = docInfo + "没有主要内容！";
				} else {
					result = result + "," + docInfo + "没有主要内容！";
				}
			}
		}
		return result;
	}

	public static String setPDFToECN(WTObject pbo) throws WTException, WTPropertyVetoException, RemoteException,
			InvocationTargetException {
		String result = "";
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			ApplicationData docApp = null;
			Transaction tx = null;
			try {
				SessionServerHelper.manager.setAccessEnforced(false);
				tx = new Transaction();
				tx.start();
				// 添加附件之前先删除ECN所有pdf附件
				ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) changeOrder2);
				Vector apps = ContentHelper.getApplicationData(contentHolder);
				QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
				WTDocument afterDoc = (WTDocument) PrintHelper.getReleatedDocByECN((WTChangeOrder2) pbo);
				if (afterDoc != null) {
					String docInfo = afterDoc.getNumber() + "_" + afterDoc.getVersionIdentifier().getValue() + "."
							+ afterDoc.getIterationIdentifier().getValue();
					if (afterDoc.getIterationIdentifier().getValue().equals("1")) {
						result = "工艺未修改,请修改工艺后再完成任务!";
						tx.commit();
						tx = null;
						return result;
					}
					if (afterDoc instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) afterDoc)) {
						result = docInfo + "被检出,请检入之后再做此操作!";
						tx.commit();
						tx = null;
						return result;
					}
				}
				while (qResult.hasMoreElements()) {
					Object object = qResult.nextElement();
					if (object instanceof WTDocument) {
						afterDoc = (WTDocument) object;
						String docInfo = afterDoc.getNumber() + "_" + afterDoc.getVersionIdentifier().getValue() + "."
								+ afterDoc.getIterationIdentifier().getValue();
						if (afterDoc.getIterationIdentifier().getValue().equals("1")) {
							result = "请修改文档(" + docInfo + ")后再完成任务!";
							tx.commit();
							tx = null;
							return result;
						}
						result = checkPublishFile(afterDoc);
						if (result != null && !"".equals(result)) {
							result = "文档" + docInfo + result;
							return result;
						}
					}
				}
				String printSourceFileName = "";
				WTDocument doc = WCUtil.getDoc(changeOrder2.getNumber() + "ECN");
				if (doc != null) {
					// ApplicationData primaryFile = (ApplicationData)
					// ContentHelper
					// .getPrimary(doc);
					ApplicationData primaryFile = (ApplicationData) ContentHelper.service.getPrimary(doc);
					if (primaryFile != null) {
						String fullName = primaryFile.getFileName();
						String appName = "ForPrint_" + fullName.substring(0, fullName.lastIndexOf(".")) + ".pdf";
						printSourceFileName = appName;
						docApp = getPDFRepressentFile(doc);
						// System.out.println("docApp==="+docApp);
						if (docApp != null) {
							for (Object app : apps) {
								if (app instanceof ApplicationData) {
									ApplicationData ad = (ApplicationData) app;
									if (ad.getFileName().endsWith(".pdf")) {
										ContentServerHelper.service.deleteContent(contentHolder, ad);
										PersistenceHelper.manager.refresh(contentHolder);
									}
								}
							}
							InputStream inputstream = ContentServerHelper.service.findContentStream(docApp);
							ApplicationData applicationdata = ApplicationData.newApplicationData(changeOrder2);
							applicationdata.setRole(ContentRoleType.SECONDARY);
							applicationdata.setFileName(appName);
							ContentServerHelper.service.updateContent(changeOrder2, applicationdata, inputstream);
							changeOrder2 = (WTChangeOrder2) PersistenceServerHelper.manager.restore(changeOrder2);
						} else {
							result = "变更单附件尚未发布完成，请等待发布完成！";
						}
					}
					System.out.println("result===" + result);
					if (result.length() == 0) {
						String userName = SessionHelper.manager.getPrincipal().getName();
						try {
							SessionHelper.manager.setAdministrator();
							PersistenceHelper.manager.delete(doc);
						} catch (Exception e) {
							throw e;
						} finally {
							SessionHelper.manager.setPrincipal(userName);
						}
					}
				}
				// else{
				// publishPdf(pbo);
				// }
				boolean addPDFFlag = false;
				contentHolder = ContentHelper.service.getContents((ContentHolder) changeOrder2);
				apps = ContentHelper.getApplicationData(contentHolder);

				String pdfName = "";
				for (Object app : apps) {
					if (app instanceof ApplicationData) {
						ApplicationData ad = (ApplicationData) app;
						String fullName = ad.getFileName();
						if (fullName.toUpperCase().endsWith(".DOC")||fullName.toUpperCase().endsWith(".DOCX")) {
							pdfName = fullName.substring(0, fullName.lastIndexOf(".")) + ".pdf";
							break;
						}
					}
				}
				for (Object app : apps) {
					if (app instanceof ApplicationData) {
						ApplicationData ad = (ApplicationData) app;
						if (ad.getFileName().equalsIgnoreCase(printSourceFileName)) {
							addPDFFlag = true;
							break;
						} else if (ad.getFileName().equalsIgnoreCase(pdfName)) {
							// 可视化不可用的时候,上传了和word文件一样名称的pdf文件
							addPDFFlag = true;
							break;
						}
					}
				}
				if (!addPDFFlag) {
					result = "附件未生成PDF，请手工上传PDF！PDF文件名与 WORD文件名一致即可。";
				} else {
					result = "";
				}
				tx.commit();
				tx = null;
			} catch (Exception e) {
				e.printStackTrace();
				throw new WTException(e);
			} finally {
				if (tx != null) {
					tx.rollback();
				}
				SessionServerHelper.manager.setAccessEnforced(true);
			}
		}
		return result;
	}

	/**
	 * 检查变更后的数据是否有修改
	 *
	 * @param pbo
	 * @return
	 * @throws WTException
	 */
	public static String checkRevisedObj(WTObject pbo) throws WTException {
		String result = "";
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			try {
				SessionServerHelper.manager.setAccessEnforced(false);

				ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) changeOrder2);
				Vector apps = ContentHelper.getApplicationData(contentHolder);
				boolean  hasDoc = false;
				for (Object app : apps) {
					if (app instanceof ApplicationData) {
						ApplicationData ad = (ApplicationData) app;
						String fullName = ad.getFileName();
						if (fullName.toUpperCase().endsWith(".DOC")||fullName.toUpperCase().endsWith(".DOCX")) {
							hasDoc = true;

						}
					}
				}
				if(!hasDoc){
					return "工艺更改单没有上传WORD附件，请上传后再完成任务！";
				}

				WTDocument afterDoc = (WTDocument) PrintHelper.getReleatedDocByECN((WTChangeOrder2) pbo);
				if (afterDoc != null) {
					String docInfo = afterDoc.getNumber() + "_" + afterDoc.getVersionIdentifier().getValue() + "."
							+ afterDoc.getIterationIdentifier().getValue();
					//if (afterDoc.getIterationIdentifier().getValue().equals("1")) {
						//result = "工艺未修改,请修改工艺后再完成任务!";
						//return result;
					//}
					if (afterDoc instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) afterDoc)) {
						result = docInfo + "被检出,请检入之后再做此操作!";
						return result;
					}
				}
				QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
				while (qResult.hasMoreElements()) {
					Object object = qResult.nextElement();
					if (object instanceof WTDocument) {
						afterDoc = (WTDocument) object;
						String docInfo = afterDoc.getNumber() + "_" + afterDoc.getVersionIdentifier().getValue() + "."
								+ afterDoc.getIterationIdentifier().getValue();
						/*if (afterDoc.getIterationIdentifier().getValue().equals("1")) {
							result = "请修改文档(" + docInfo + ")后再完成任务!";
							return result;
						}*/
						if (afterDoc instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) afterDoc)) {
							result = docInfo + "被检出,请检入之后再做此操作!";
							return result;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new WTException(e);
			} finally {
				SessionServerHelper.manager.setAccessEnforced(true);
			}
		}
		return result;
	}

	public static void publishPdf(WTObject pbo) throws WTException, WTPropertyVetoException, RemoteException,
			InvocationTargetException {

		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			ApplicationData docApp = null;
			String userName = "";
			Transaction tx = null;
			try {
				tx = new Transaction();
				tx.start();
				ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) changeOrder2);
				Vector apps = ContentHelper.getApplicationData(contentHolder);
				for (Object app : apps) {
					if (app instanceof ApplicationData) {
						ApplicationData ad = (ApplicationData) app;
						if (ad.getFileName().endsWith(".doc")||ad.getFileName().endsWith(".docx")) {
							docApp = ad;
							break;
						}
					}
				}
				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
				userName = user.getName();
				if (docApp != null) {
					SessionHelper.manager.setAdministrator();
					String docNumber = changeOrder2.getNumber() + "ECN";
					WTDocument newDoc = null;
					WTDocument oldDoc = WCUtil.getDoc(docNumber);
					if (oldDoc != null) {
						updateDocContent(oldDoc, docApp);
					} else {
						WTDocument beforeDoc = null;
						QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
						while (qResult.hasMoreElements()) {
							Object object = qResult.nextElement();
							if (object instanceof WTDocument) {
								beforeDoc = (WTDocument) object;
								break;
							} else if (object instanceof MPMProcessPlan) {
								beforeDoc = (WTDocument) PrintHelper.getReleatedDocByECN((WTChangeOrder2) pbo);
							}
						}
						newDoc = WTDocument.newWTDocument();
						newDoc.setNumber(docNumber);
						newDoc.setName(changeOrder2.getName() + "( for publish PDF file)");
						FolderHelper.assignLocation(newDoc, FolderHelper.service.getFolder(beforeDoc));
						PersistenceHelper.manager.save(newDoc);
						InputStream inputstream = ContentServerHelper.service.findContentStream(docApp);
						ApplicationData applicationdata = ApplicationData.newApplicationData(newDoc);
						applicationdata.setRole(ContentRoleType.PRIMARY);
						applicationdata.setFileName(docApp.getFileName());
						ContentServerHelper.service.updateContent(newDoc, applicationdata, inputstream);
						newDoc = (WTDocument) PersistenceServerHelper.manager.restore(newDoc);
					}
				}
				tx.commit();
				tx = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// SessionServerHelper.manager.setAccessEnforced(true);
				if (userName.length() > 0) {
					SessionHelper.manager.setPrincipal(userName);
				}
				if (tx != null) {
					tx.rollback();
				}
			}
		}
	}

	public static WTDocument updateDocContent(WTDocument doc, ApplicationData app) {
		try {
			if (doc == null)
				return null;
			InputStream inputstream = ContentServerHelper.service.findContentStream(app);
			String finaleTargetFileName = FilePrintUtil.wtProperties.getProperty("wt.temp") + File.separator
					+ app.getFileName();
			File tempFile = new File(finaleTargetFileName);
			String fileAbsolutePath = tempFile.getAbsolutePath();
			FileOutputStream tout = new FileOutputStream(fileAbsolutePath);
			byte abyte1[] = new byte[2048];
			int k;
			while ((k = inputstream.read(abyte1, 0, abyte1.length)) >= 0) {
				tout.write(abyte1, 0, k);
			}
			tout.close();
			// CSCDoc.updateDocContent(doc, finaleTargetFileName);
			// tempFile.delete();
			doc = CSCDoc.getWorkingCopyOfDoc(doc);
			CADSignHelper.uploadDocPriFile(tempFile, doc, true);
			if (doc != null) {
				if (WorkInProgressHelper.isCheckedOut(doc, SessionHelper.manager.getPrincipal()))
					doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, "update primary file");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return doc;
	}

	public static ProcessEnvelope createCLDEProcessEnvelope(Map<String,List<CMatBean>> datas,  String workitemOid, String partNumber){
		Set<Entry<String, List<CMatBean>>>  set =datas.entrySet();
		List<WTDocument> docs = new ArrayList<WTDocument>();
		StringBuffer docNumberBuffer = new StringBuffer();
		List<CMatBean> beans = new ArrayList<CMatBean>();
		WTContainer container = null;
		for(Entry<String, List<CMatBean>> entry:set){
			String number = entry.getKey();
			beans.addAll(entry.getValue());
			WTDocument doc = DocUtil.getDoc(number, false);
			if(doc!=null&&container==null){
				container = doc.getContainer();
			}
			docs.add(doc);
		}
		Transaction  trx = null;
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			trx = new Transaction();
			trx.start();
			ProcessEnvelope pbo = ProcessEnvelope.newProcessEnvelope();
			pbo.setNumber(new Date().getTime()+"");
			pbo.setName("材料定额签审包"+"-"+partNumber+"-"+pbo.getCreatorFullName());
			String type = "WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.CLDE";
            TypeIdentifier id = TypeHelper.getTypeIdentifier(type);
            pbo = (ProcessEnvelope) CoreMetaUtility.setType(pbo, id);

			Folder folder = FolderHelper.service.getFolder("/Default", WTContainerRef.newWTContainerRef(container));
			if (folder != null){
              FolderHelper.assignLocation((FolderEntry) pbo, folder);
			}
			pbo.setContainer(container);
			PersistenceHelper.manager.save(pbo);

			for(WTDocument doc :docs){
				if(docNumberBuffer.toString().isEmpty()){
					docNumberBuffer.append(doc.getNumber());
				}else{
					docNumberBuffer.append(",").append(doc.getNumber());
				}
				EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(pbo,doc);
				envelopememberlink.setDescription("工艺文件材料定关联关系");
				PersistenceHelper.manager.save(envelopememberlink);
			}
			//           TECHNICSNUMBERS太长，超过500会报错，暂时注释掉
//			String reg = "[0-9]+";
//			if (workitemOid != null && !workitemOid.isEmpty() && workitemOid.matches(reg)) {
//				ProcessTaskItem processTaskItem = ProcessUtil.getProcessTaskItem(Long.valueOf(workitemOid));
//				if (processTaskItem != null) {
//					ProcessTask processTask = ProcessUtil.getProcessTask(processTaskItem.getProcessTaskId());
//					if (processTask != null) {
//						IBAUtility ibaUtility = new IBAUtility(processTask);
//						ibaUtility.setIBAValue("TECHNICSNUMBERS", docNumberBuffer.toString());
//						processTask = (ProcessTask) ibaUtility.updateAttributeContainer(processTask);
//						ibaUtility.updateIBAHolder(processTask);
//					}
//				}
//			}
			File file = exportGSDE(beans);

	        ContentHolder holder = ContentHelper.service.getContents(pbo);
	        Vector vector = ContentHelper.getContentList(holder);
	        if (vector != null) {
	            for (int i = 0; i < vector.size(); i++) {
	                ContentItem contentitem1 = (ContentItem) vector
	                        .elementAt(i);
	                if (contentitem1 instanceof ApplicationData) {
	                    ApplicationData data = (ApplicationData) contentitem1;
	                    if (data.getFileName().equals(file.getName())) {
	                        ContentServerHelper.service.deleteContent(holder,
	                                contentitem1);
	                        break;
	                    }
	                }

	            }
	        }
	        PersistenceHelper.manager.lockAndRefresh(pbo);
	        ApplicationData appData = ApplicationData.newApplicationData(pbo);
	        appData.setFileName(file.getName());
	        appData.setRole(ContentRoleType.SECONDARY);
	        appData.setDescription(String.valueOf(Calendar.getInstance()
	                .getTimeInMillis()));
	        appData.setComments("自动生成");
	        appData = ContentServerHelper.service.updateContent(pbo, appData,
	                new FileInputStream(file));
	        PersistenceHelper.manager.refresh(pbo);

			trx.commit();
	        trx = null;
			return pbo;

		} catch (Exception e) {
			if(trx!=null){
				trx.rollback();
			}
			e.printStackTrace();
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enforce);
			 trx = null;
		}
		return null;
	}

	public static void genExcelAndAddToDocument(WTDocument doc,List<CMatBean> beans) throws Exception{
		Transaction  trx = new Transaction();
        trx.start();
		File file = exportGSDE( beans);
        boolean enforce = SessionServerHelper.manager
                .setAccessEnforced(false);
        ContentHolder holder = ContentHelper.service.getContents(doc);
        Vector vector = ContentHelper.getContentList(holder);
        if (vector != null) {
            for (int i = 0; i < vector.size(); i++) {
                ContentItem contentitem1 = (ContentItem) vector
                        .elementAt(i);
                if (contentitem1 instanceof ApplicationData) {
                    ApplicationData data = (ApplicationData) contentitem1;
                    if (data.getFileName().equals(file.getName())) {
                        ContentServerHelper.service.deleteContent(holder,
                                contentitem1);
                        break;
                    }
                }

            }
        }
        PersistenceHelper.manager.lockAndRefresh(doc);
        ApplicationData appData = ApplicationData.newApplicationData(doc);
        appData.setFileName(file.getName());
        appData.setRole(ContentRoleType.SECONDARY);
        appData.setDescription(String.valueOf(Calendar.getInstance()
                .getTimeInMillis()));
        appData.setComments("自动生成");
        appData = ContentServerHelper.service.updateContent(doc, appData,
                new FileInputStream(file));
        PersistenceHelper.manager.refresh(doc);
        SessionServerHelper.manager.setAccessEnforced(enforce);
		trx.commit();
        trx = null;
	}

	public static File exportGSDE(List<CMatBean> beans) throws Exception{
		File file = null;
		ArrayList<String> titles = buildTitles();
		ArrayList<ArrayList<String>> values = buildValues(beans);
		ExcelFileGenerator gen = new ExcelFileGenerator(titles,values);
		WTProperties wtp = WTProperties.getLocalProperties();
		String temp = wtp.getProperty("wt.temp");
		file = new File(temp + File.separator + "材料定额表.xls");
		gen.expordExcel(new FileOutputStream(file));


		return file;
	}

	private static ArrayList<ArrayList<String>> buildValues(List<CMatBean> beans) throws WTRuntimeException, RemoteException, WTException {
		ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();
		for(CMatBean bean:beans){
			 ArrayList<String> list = new ArrayList<String>();
			 list.add(bean.getCindex());
			 list.add(bean.getPartName());
			 list.add(bean.getSl());
			 list.add(bean.getPplanNumber());
			 list.add(bean.getPplanName());

			 list.add(bean.getCmatType());
			 list.add(bean.getChbm());
			 list.add(bean.getChmc());
			 list.add(bean.getXhph());
			 list.add(bean.getGg());
			 list.add(bean.getJstj());
			 list.add(bean.getXlcc());
			 list.add(bean.getKzjs());
			 list.add(bean.getZjldw());
			 list.add(bean.getDw());
			 list.add("");
			 list.add("");
			 list.add("");
			 list.add("");
			 list.add(bean.getSjcc());
			 list.add(bean.getSjsl());
			 list.add(bean.getComment());

			 allList.add(list);
		 }
		return allList;
	}

	private static ArrayList<String> buildTitles(){
		ArrayList<String> titles = new ArrayList<String>();
		titles.add("代号");
		titles.add("名称");
		titles.add("使用数量");
		titles.add("工艺文件编号");
		titles.add("工艺文件名称");

		titles.add("材料类型");
		titles.add("工艺选码");//存货编码
		titles.add("存货名称");
		titles.add("型号牌号");
		titles.add("规格");
		titles.add("技术条件");
		titles.add("下料尺寸");
		titles.add("可制件数");
		titles.add("主计量单位");
		titles.add("单位");
		titles.add("毛重");
		titles.add("总毛重");
		titles.add("工艺定额");
		titles.add("总工艺定额");
		titles.add("试件尺寸");
		titles.add("试件数量");
		titles.add("备注");
		return titles;
	}


	public static List<MPMOperation> getMPMOperationsByMpmPr(MPMProcessPlan plan) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(plan).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        List<MPMOperation> list = new ArrayList<MPMOperation>();
        while (qResult.hasMoreElements()) {
            MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
            MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
            MPMOperation operation = getMpmOperation(master.getNumber());
            list.add(operation);
        }
        return list;
    }
	public static MPMOperation getMpmOperation(String number) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperation.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(MPMOperation.class, MPMOperation.NUMBER,
                SearchCondition.EQUAL, number);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        qResult = new LatestConfigSpec().process(qResult);
        if (qResult.hasMoreElements()) {
            MPMOperation mpmOperation = (MPMOperation) qResult.nextElement();
            return mpmOperation;
        }
        return null;
    }

	public static void updateBatchCLDEState(Object object,String state){
		if(object instanceof ProcessEnvelope){
			ProcessEnvelope processEnvelope = (ProcessEnvelope)object;
			try {
				QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope,
				        "theRevisionControlled",
				        EnvelopeMemberLink.class, false);
				 RevisionControlled obj = null;
			      for (; qr.hasMoreElements();) {
			            obj = ((EnvelopeMemberLink) qr.nextElement())
			                    .getRevisionControlled();
			            if (obj != null) {
			                if (obj instanceof WTDocument) {
			                	IBAHolder op = (IBAHolder)obj;
			                	IBAUtility iba = new IBAUtility(op);
								try {
									iba.setIBAValue( "CLDEZT", state);
									op = iba.updateAttributeContainer(op);
									iba.updateIBAHolder(op);
								} catch (WTPropertyVetoException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			                }
			            }
			      }
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else if(object instanceof WTDocument){
			IBAHolder op = (IBAHolder)object;
			try {
				IBAUtility iba = new IBAUtility(op);
				iba.setIBAValue( "CLDEZT", state);
				op = iba.updateAttributeContainer(op);
				iba.updateIBAHolder(op);
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void bohuiWorkItem(String workItemOid) {
		ReferenceFactory rf = new ReferenceFactory();
		boolean enforce = SessionServerHelper.manager
                .setAccessEnforced(false);
        try {
			WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
			WfActivity activity = (WfActivity) wi.getSource().getObject();
			if(activity.getName().equals("内部会签")){
				List<WorkItem> items = getWorkItemsByActivityOid(activity.getPersistInfo().getObjectIdentifier().getId());
				for(WorkItem workitem :items){
					if(wi.getPersistInfo().getObjectIdentifier().getId()!=workitem.getPersistInfo().getObjectIdentifier().getId()){
						ProcessData pd = activity.getContext();
		                ProcessData pdc = pd.copy();
		                if (pdc != null) {
		                    pdc.setTaskComments("自动驳回");
		                    workitem.setContext(pdc);
		                    workitem = (WorkItem) PersistenceHelper.manager
		                            .save(workitem);
		                }
		                Vector vector = new Vector();
		                vector.addElement("驳回");
		                wt.workflow.work.WorkflowHelper.service.workComplete(workitem, workitem
		                        .getOwnership().getOwner(), vector);
		                WfEventHelper.createVotingEvent(null, activity, workitem,
		                        workitem.getOwnership().getOwner(), "自动驳回", vector,
		                        false, workitem.isRequired());
					}

				}
			}

		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			 SessionServerHelper.manager.setAccessEnforced(enforce);
		}

	}
	public static void bohuiWorkItem(ObjectReference self,String pname) {
		try {
			Persistable persistable = self.getObject();
			if (persistable instanceof WfProcess) {
				WfProcess process = (WfProcess) persistable;
				// 终止所有的会签相关活动
				// WfBlock wfblock = PrintHelper.getBlock(process);
				//List<WfBlock> allWfBlocks = PrintHelper.getAllBlock(process);
				Enumeration enumeration = WfEngineHelper.service.getProcessSteps(process, null);
		        while (enumeration.hasMoreElements()) {
		            WfActivity wfactivity = (WfActivity) enumeration.nextElement();
		            if (wfactivity instanceof WfAssignedActivity) {
		                WfAssignedActivity wfAssignedActivity = (WfAssignedActivity) wfactivity;
						String name = wfAssignedActivity.getName();
						String state = wfAssignedActivity.getState().toString();
						// System.out.println("-----------wfAssignedActivity name:"
						// + name);
						if ("OPEN_RUNNING".equals(state)) {
							if(name.equals(pname)){
								List<WorkItem> items = getWorkItemsByActivityOid(wfAssignedActivity.getPersistInfo().getObjectIdentifier().getId());
								for(WorkItem workitem :items){
									 if (workitem != null) {
										 	wfactivity = (WfActivity) workitem.getSource().getObject();
							                ProcessData pd = wfactivity.getContext();
							                ProcessData pdc = pd.copy();
							                if (pdc != null) {
							                    pdc.setTaskComments("自动驳回");
							                    workitem.setContext(pdc);
							                    workitem = (WorkItem) PersistenceHelper.manager
							                            .save(workitem);
							                }
							                Vector vector = new Vector();
							                vector.addElement("驳回");
							                wt.workflow.work.WorkflowHelper.service.workComplete(workitem, workitem
							                        .getOwnership().getOwner(), vector);
							                WfEventHelper.createVotingEvent(null, wfAssignedActivity, workitem,
							                        workitem.getOwnership().getOwner(), "自动驳回", vector,
							                        false, workitem.isRequired());
							            }
								}
								break;
							}

						}
		            }
		        }

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 public static List<WorkItem> getWorkItemsByActivityOid(long ida3a4)
	            throws WTException {
	        List<WorkItem> workitems = new ArrayList<WorkItem>();
	        QueryResult queryresult;
	        QuerySpec queryspec = new QuerySpec(WorkItem.class);
	        queryspec.setAdvancedQueryEnabled(true);
	        SearchCondition searchcondition = new SearchCondition(WorkItem.class,
	                "source.key.id", "=", ida3a4);
	        queryspec.appendWhere(searchcondition, 0);
	        queryspec.appendAnd();
	        queryspec.appendWhere(new SearchCondition(WorkItem.class, "status",
	                "=", "POTENTIAL"), 0);
	        TableColumn ca = new TableColumn("A0", "createstampa2");
	        OrderBy orderBy = new OrderBy(ca, false);
	        queryspec.appendOrderBy(orderBy, new int[0]);
	        queryresult = PersistenceServerHelper.manager.query(queryspec);
	        while (queryresult.hasMoreElements()) {
	        	WorkItem  workitem = (WorkItem) queryresult.nextElement();
	        	workitems.add(workitem);
	        }
	        return workitems;

	    }

	 /**
	  * 校验工时定额编制页面是否全部填写工时
	  * @param pbo
	  * @return
	  */
	 public static boolean isGYDE(Object pbo) {
		 boolean isGYDE = true;
		 try {
			 List<MPMOperation> ops = new ArrayList<MPMOperation>();
			 if(pbo instanceof WTDocument) {
				 WTDocument doc = (WTDocument) pbo;
				 MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
				 ops = WorkflowHelper.getMPMOperationsByMpmPr(plan);

			 } else if(pbo instanceof WTChangeOrder2) {
				 WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
				 MPMProcessPlan beforePlan = null;
				 MPMProcessPlan afterPlan = null;
				 QueryResult qr = ChangeHelper2.service.getChangeablesBefore(ecn);
				 while(qr.hasMoreElements()) {
					 Object object = qr.nextElement();
					 if(object instanceof WTDocument) {
						 if(beforePlan == null) {
							 WTDocument doc = (WTDocument) object;
							 beforePlan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
						 }
					 } else if(object instanceof MPMProcessPlan) {
						 beforePlan = (MPMProcessPlan) object;
						 break;
					 }
				 }
				 QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(ecn);
				 while(qResult.hasMoreElements()) {
					 Object object = qResult.nextElement();
					 if(object instanceof WTDocument) {
						 if(afterPlan == null) {
							 WTDocument doc = (WTDocument) object;
							 afterPlan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
						 }
					 } else if(object instanceof MPMProcessPlan) {
						 afterPlan = (MPMProcessPlan) object;
						 break;
					 }
				 }

				 if(afterPlan != null) {
					 ops.addAll(getOperationChangeList(afterPlan, beforePlan));
				 }
			 } else if(pbo instanceof ProcessEnvelope) {
				 ProcessEnvelope pe = (ProcessEnvelope) pbo;
				 ArrayList members = ProcessEnvelopeUtil.getAllMembers(pe);
				 for(Object member : members) {
					 if(member instanceof MPMOperation) {
						 ops.add((MPMOperation) member);
					 }
				 }
			 }
			 if(ops.isEmpty()) {
				 return true;
			 }

			 for(MPMOperation op : ops) {//ZJGS,DJGS
				 String ZJGS = IBAHelper.getIBAStringValue(op, "ZJGS");
				 if(StrUtil.isEmpty(ZJGS)) {
					 return false;
				 }
				 String DJGS = IBAHelper.getIBAStringValue(op, "DJGS");
				 if(StrUtil.isEmpty(DJGS)) {
					 return false;
				 }

				 //add by yfn 20250618 check 单件设备工时定额是否设置了值， 如果是其关联的【工序名称】的软属性IsSheBeiGS值=是，则必须设置DanJianSheBeiGS
				 try {
					 MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(op.getName(), SopConstants.SOP_TYPE_PROCEDUCENAME);
                     if(tooling != null) {
						 String IsSheBeiGS = IBAHelper.getIBAStringValue(tooling, "IsSheBeiGS");
						 if ("是".equals(IsSheBeiGS)) {
							 String DanJianSheBeiGS = IBAHelper.getIBAStringValue(op, "DanJianSheBeiGS");
							 if(StrUtil.isEmpty(DanJianSheBeiGS)) {
								 return false;
							 }
						 }
                     }
				 } catch (Exception e) {
					 throw new RuntimeException(e);
				 }

			 }
		 } catch(QueryException e) {
			 isGYDE = false;
			 e.printStackTrace();
		 } catch(WTException e) {
			 isGYDE = false;
			 e.printStackTrace();
		 }
		 return isGYDE;
	 }

	public static List<MPMOperation> getOperationChangeList(MPMProcessPlan plan,MPMProcessPlan beforePlan) throws WTException {
		if(plan == null) {
			return new ArrayList<MPMOperation>();
		}
		MPMOperationMaster master = null;
		MPMOperation operation = null;
		List<MPMOperation> list = new ArrayList<MPMOperation>();
		List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
		Map<String, String> stepIdMap = new HashMap<String, String>();
		if(beforePlan != null) {
			List<MPMOperationUsageLink> beforeLinks = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(beforePlan);
			Map<String,String> idMap = new HashMap<String, String>();
			for(MPMOperationUsageLink beforeLink : beforeLinks) {
				MPMOperationMaster beforeMaster = (MPMOperationMaster) beforeLink.getRoleBObject();
				MPMOperation beforeOperation = ViewWIHelper.getMpmOperation(beforeMaster.getNumber());
				String stepNumber = com.glaway.mpm.util.Util.formateInteger(beforeLink.getOperationLabel());
				String stepId = IBAHelper.getIBAStringValue(beforeOperation, "BIAOSHI");
				if(StrUtil.isEmpty(stepId) || "null".equals(stepId)) {
					if(CollUtil.isEmpty(idMap)){
						idMap = getStepIdMapByPlan(beforePlan);
					}
					stepId = idMap.get(stepNumber);
					IBAHelper.setIBAStringValue(beforeOperation, "BIAOSHI", stepId);
				}
				stepIdMap.put(stepId, stepId);
			}
		}
		Map<String,String> afterIdMap = new HashMap<String,String>();
		for(MPMOperationUsageLink link : links) {
			master = (MPMOperationMaster) link.getRoleBObject();
			operation = ViewWIHelper.getMpmOperation(master.getNumber());
			String stepNumber = com.glaway.mpm.util.Util.formateInteger(link.getOperationLabel());

			if(beforePlan != null) {
				String bsoID = IBAHelper.getIBAStringValue(operation, "BIAOSHI");
				if(StrUtil.isEmpty(bsoID) || "null".equals(bsoID)) {
					if(CollUtil.isEmpty(afterIdMap)){
						afterIdMap = getStepIdMapByPlan(plan);
					}
					bsoID = afterIdMap.get(stepNumber);
					IBAHelper.setIBAStringValue(operation, "BIAOSHI", bsoID);
				}
				if(StrUtil.isNotEmpty(bsoID) && !stepIdMap.containsKey(bsoID)){
					list.add(operation);
				}
			}
		}
		return list;
	}

	private static Map<String,String> getStepIdMapByPlan(MPMProcessPlan plan) throws WTException {
		Map<String, String> map = new HashMap<String, String>();
		WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
		if(document != null) {
			InputStream is = null;
			try {
				String tempFilePath = PropertiesUtil.getTempPath() + File.separator + IdUtil.randomUUID() + File.separator;
				String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
				String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
				ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
				File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
				is = new FileInputStream(xmlFile);
				SWXMLUtil xmlUtil = new SWXMLUtil(is);
				Element rootElement = xmlUtil.getRootElement();
				Element QMFawTechnicsInfo = rootElement.getChild("QMFawTechnicsInfo");
				Element steps = QMFawTechnicsInfo.getChild("steps");
				List<Element> proceduresList = steps.getChildren("QMProcedureInfo");
				for(Element procedure : proceduresList) {
					String stepNumber = procedure.getAttributeValue("stepNumber");
					String bsoID = procedure.getAttributeValue("bsoID");
					map.put(stepNumber, bsoID);
				}
			} catch(PropertyVetoException e) {
				e.printStackTrace();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				if(is != null){
					try {
						is.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return map;
	}

	 public static String  getUserByRole(Object self,String role){
		 System.out.println(self);
		 ObjectReference rf = (ObjectReference)self;
		 try {
			 WfAssignedActivity a = (WfAssignedActivity) rf.getObject();
			 WfProcess process = a.getParentProcess();
			 Team team = (Team) process.getTeamId().getObject();
			 HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
			 System.out.println(rolePrincipalListMap);
			 Role  r = Role.toRole(role);
			 ArrayList<WTPrincipalReference> users = (ArrayList<WTPrincipalReference>)rolePrincipalListMap.get(r);
			 System.out.println("user:"+users);
			 if(!users.isEmpty()){
				 return users.get(0).getFullName();
			 }

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return "";

	 }
/*
 * ext.casc.workflow.WorkflowHelper.setReplaceObjectLifeCycle(primaryBusinessObject,"OBSOLESCENCE");
 */
	public static void setReplaceObjectLifeCycle(WTObject obj, String lf) {
		// SessionContext previous = SessionContext.getContext();
		boolean check = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				String replace = IBAHelper.getIBAStringValue(doc, "REPLACE");
				if (replace != null && !"".equals(replace)) {
					WTDocument replaceDoc = CSCDoc.getDoc(replace);
					if (replaceDoc != null) {
						LifeCycleHelper.service.setLifeCycleState(replaceDoc, State.toState(lf));
					}
				}

			}
		} catch (WTException wte) {
			wte.printStackTrace();
		} finally {
			// SessionContext.setContext(previous);
			SessionServerHelper.manager.setAccessEnforced(check);
		}
	}

	public static String getEcnType(WTObject pbo) {
		String ecnType = "";
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			try {
				ecnType = IBAHelper.getIBAStringValue(changeOrder2, "ECNTYPE");
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return ecnType;
	}
	public static boolean checkIsSetRole(ObjectReference self) {
        try {
            Persistable persistable = self.getObject();
            if (persistable instanceof WfAssignedActivity) {
            	WfAssignedActivity activity = (WfAssignedActivity)persistable;
                WfProcess process = (WfProcess) activity.getParentProcess();
                Team team = (Team) process.getTeamId().getObject();
                HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
                Role jiaodui = Role.toRole("JIAODUIZHE");
                List tempUserList = (List) rolePrincipalListMap.get(jiaodui);
                if(tempUserList==null||tempUserList.isEmpty()){
                	return false;
                }
            }
            }catch(Exception e){
                e.printStackTrace();
            }
        return true;
    }

	/**
	 * 工艺更改继承主辅关联
	 * @param pbo
	 * @throws WTException
	 * @throws ChangeException2
	 */
	public static void copyZFLink(WTObject pbo) throws WTException {
		if (pbo != null && pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
            QueryResult afters = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
            WTDocument doc = null;
            String versionInfo = null;
            while (afters.hasMoreElements()) {
            	Object nextElement = afters.nextElement();
            	if (nextElement instanceof WTDocument) {
            		doc = (WTDocument)nextElement;
            		if (TypeUtil.getSoftType(doc, true).contains("casc.sast.149.PROCESS_PLAN")) {
            			versionInfo = doc.getVersionIdentifier().getValue();
            			String beforeVersion = null;
            			WTDocument temp = null;
            			QueryResult allVersions = VersionControlHelper.service.allVersionsOf(doc);
            			while (allVersions.hasMoreElements()) {
            				temp = (WTDocument) allVersions.nextElement();
            				if (versionInfo.equals(temp.getVersionIdentifier().getValue())) {
            					if (allVersions.hasMoreElements()) {
            						temp = (WTDocument) allVersions.nextElement();
            						beforeVersion = temp.getVersionIdentifier().getValue();
            						break;
            					}
            				}
            			}
            			if (beforeVersion != null && beforeVersion.length() > 0) {
            				String flag = IBAHelper.getIBAStringValue(doc, "ZFFLAG");
            				if ("F".equals(flag)) {
            					ZhuFuLinkUtil.copyZFLinkF(versionInfo, beforeVersion, doc);
            				} else if ("Z".equals(flag)) {
            					ZhuFuLinkUtil.copyZFLinkZ(versionInfo, beforeVersion, doc);
            				} else {
            					logger.error("copyZFLink===>工艺规程主辅标记为空，未复制--" + doc.getName() + doc.getIterationDisplayIdentifier());
            				}
            			} else {
            				logger.error("copyZFLink===>未发现上一版本，未复制--" + doc.getName() + doc.getIterationDisplayIdentifier());
            			}
            		}
            	}
            }
		}
	}

	/**
	 * 工艺更改单流程撤销更改后删除已继承的主辅link记录
	 * @param pbo
	 * @throws WTException
	 */
	public static void deleteZFLink(WTObject pbo)  throws WTException {
		if (pbo != null && pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
            QueryResult afters = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
            WTDocument doc = null;
            while (afters.hasMoreElements()) {
            	Object nextElement = afters.nextElement();
            	if (nextElement instanceof WTDocument) {
            		doc = (WTDocument)nextElement;
            		if (TypeUtil.getSoftType(doc, true).contains("casc.sast.149.PROCESS_PLAN")) {
            			String flag = IBAHelper.getIBAStringValue(doc, "ZFFLAG");
            			Map<String, String> params = null;
            			if ("F".equals(flag)) {
            				params = new HashMap<String, String>();
            				String version = doc.getVersionIdentifier().getValue();
            				params.put("FZTECHNICSNUMBER", doc.getNumber());
            				params.put("FZTECHNICSVERSION", version);
            			} else if ("Z".equals(flag)) {
            				params = new HashMap<String, String>();
            				params.put("ZZTECHNICSNUMBER", doc.getNumber());
            				String version = doc.getVersionIdentifier().getValue();
            				params.put("ZZTECHNICSVERSION", version);
            			} else {
            				logger.debug("deleteZFLink===>工艺规程主辅标记为空 : " + doc.getName());
            			}
            			if (params != null && params.size() > 0) {
            				DBConnUtil dbUtil = null;
            				try {
								dbUtil = new DBConnUtil();
								dbUtil.start();
								ZhuFuLinkUtil.delete(dbUtil, params);
								ZhuFuLinkUtil.deleteZFLink(dbUtil, params);
								dbUtil.commit();
							} catch (Exception e) {
								try {
									dbUtil.rollback();
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
								throw new WTException(e);
							} finally {
								if (dbUtil != null) {
									try {
										dbUtil.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
							}
            			}
            		}
            	}
            }
		}
	}

	/**
	 * 材料定额批准完成后调用，记录材料定额的实际完成时间
	 * @param self
	 * @throws Exception
	 */
	public static void recordCldeEndTime(Object self) throws Exception {
		IBAUtility ibaUtility;
		ProcessTaskItem taskItem;
		SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
		String date = dataFormate.format(new Date());
		WfProcess wfProcess = WorkflowUtil.getWfProcessBySelf(self);
		ProcessData context = wfProcess.getContext();
		String taskItemOid = (String) context.getValue("taskItemOid");
		if(taskItemOid != null && !taskItemOid.isEmpty() && !"null".equals(taskItemOid)){
			String[] taskItems = taskItemOid.split(",");
			for(String taskOid : taskItems){
				ProcessTask processTask = ProcessUtil.getProcessTask(Long.valueOf(taskOid));
				if(processTask!=null){
					QueryResult queryResult = ProcessUtil.getAllProcessTaskItemByPTask(Long.valueOf(taskOid));
					while (queryResult.hasMoreElements()) {
						Object o = queryResult.nextElement();
						if (o instanceof ProcessTaskItem) {
							taskItem = (ProcessTaskItem) o;
							ibaUtility = new IBAUtility(taskItem);
							ibaUtility.setIBAValue("cldeEndTime", date);
							taskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(taskItem);
							ibaUtility.updateIBAHolder(taskItem);
						}
					}
					ibaUtility = new IBAUtility(processTask);
					ibaUtility.setIBAValue("cldeEndTime", date);
					processTask = (ProcessTask) ibaUtility.updateAttributeContainer(processTask);
					ibaUtility.updateIBAHolder(processTask);
				}
			}
//			ProcessTaskItem processTaskItem = ProcessUtil.getProcessTaskItem(Long.valueOf(taskItemOid));
//			ProcessTask processTask = ProcessUtil.getProcessTask(processTaskItem.getProcessTaskId());
		}

	}


	/**
	 * 文件转移流程批准完成后调用，修改领取部门
	 * @param self
	 * @throws Exception
	 */
	public static void modifyPrintDept(Object self) throws Exception {
		WfProcess wfProcess = WorkflowUtil.getWfProcessBySelf(self);
		ProcessData context = wfProcess.getContext();
		String transferInfo = (String) context.getValue("transferInfo");
		String[] infos = transferInfo.split(",");
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			for(String info : infos) {
				String[] str = info.split("&");
				String oid = str[0];
				String dept = str[1];
				String querySQL = "select a.DISMESSAGE,c.GDEPT from GWPRINTAPPLYRECORD a,GWPRINTDISTRIBUTERECORD b,GWPRINTBARCODE c where a.GWKEYID = b.APPLYRECORDID and b.GWKEYID = c.APPLYRECORDID and c.GWKEYID = '" + oid + "'";
				ResultSet rt = conn.executeQuery(querySQL);
				String newDismessage = "";
				if (rt.next()) {
					String dismessage = rt.getString("DISMESSAGE");
					String gdept = rt.getString("GDEPT");
					boolean has = false;
					if(dismessage != null && !"".equals(dismessage)){
						String[] depts = dismessage.split(",");
						for(String d : depts) {
							if(d.contains(gdept)){
								int count = Integer.parseInt(d.substring(d.indexOf(":")+1, d.length() - 1));
								count--;
								if(count == 0){
									d = "";
								}else{
									d = gdept + ":" + count + "份";
								}
							}
							if(d.contains(dept)){
								has = true;
								int count = Integer.parseInt(d.substring(d.indexOf(":")+1, d.length() - 1));
								count++;
								d = dept + ":" + count + "份";
							}
							if(!"".equals(newDismessage) && !"".equals(d)){
								newDismessage += "," + d;
							}else{
								newDismessage += d;
							}
						}
					}
					if(!has){
						if(!"".equals(newDismessage)){
							newDismessage += "," + dept + ":1份";
						}else{
							newDismessage += dept + ":1份";
						}
					}
				}
				String updateSQL1 = "UPDATE GWPRINTDISTRIBUTERECORD b SET b.DISTRIBUTEDEPT = '" + dept + "' where b.GWKEYID in (select c.APPLYRECORDID GWKEYID from GWPRINTBARCODE c where c.GWKEYID = '"+ oid +"')";
				conn.executeUpdate(updateSQL1);
				String updateSQL2 = "UPDATE GWPRINTBARCODE c SET c.GDEPT = '" + dept + "' where c.GWKEYID='" + oid + "'";
				conn.executeUpdate(updateSQL2);
				if(!"".equals(newDismessage)){
					String updateSQL3 = "UPDATE GWPRINTAPPLYRECORD a SET a.DISMESSAGE = '" + newDismessage + "' where a.GWKEYID in (select b.APPLYRECORDID GWKEYID from GWPRINTDISTRIBUTERECORD b,GWPRINTBARCODE c where b.GWKEYID = c.APPLYRECORDID and c.GWKEYID = '" + oid + "')";
					conn.executeUpdate(updateSQL3);
				}
				conn.commit();
			}
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	public static void setGongShiDingeToWfTeamRole(ObjectReference self,WTObject pbo) throws WTException {
		Role role = Role.toRole("GONGSHIDINGEYUAN");
		if(role == null) {
			return;
		}
		List<WTUser> list = new ArrayList<WTUser>();
		WTDocument document = null;
		if (pbo instanceof WTDocument) {
			document = (WTDocument) pbo;
		}else if(pbo instanceof WTChangeOrder2){
			WTChangeOrder2 order2 = (WTChangeOrder2) pbo;
			QueryResult qr = ChangeHelper2.service.getChangeablesAfter(order2);
			while (qr.hasMoreElements()) {
				Object object = qr.nextElement();
				if (object instanceof WTDocument) {
					document = (WTDocument) object;
					break;
				}
			}
		}
		if(document != null) {
			String dept = IBAHelper.getIBAStringValue(document, "DEPT");
			String roleName = getRoleNameByDept(dept);

			if(StrUtil.isEmpty(roleName)){
				return;
			}
			Role dingeRole = Role.toRole(roleName);
			if(dingeRole == null) {
				return;
			}
			WTContainer container = document.getContainer();
			ContainerTeam containerTeam = WorkflowUtil.getContainerTeam(container);
			Vector<Role> vec = containerTeam.getRoles();
			for (int i = 0; i < vec.size(); i++) {
				Role roleTemp = vec.get(i);
				if(!roleTemp.equals(dingeRole)) {
					continue;
				}
				Enumeration<WTPrincipalReference> eprin = containerTeam.getPrincipalTarget(roleTemp);
				while (eprin.hasMoreElements()) {
					WTPrincipal temp = eprin.nextElement().getPrincipal();
					if (temp instanceof WTUser) {
						list.add((WTUser)temp);
					}else if(temp instanceof WTGroup){
						WTGroup group = (WTGroup) temp;
						PrintUtil.searchGroupUserList(group, list);
					}
				}
			}
		}

		Persistable persistable = self.getObject();
		if (persistable instanceof WfProcess) {
			Transaction tx = new Transaction();
			WfProcess process = (WfProcess) persistable;
			Team team = (Team) process.getTeamId().getObject();
			for (WTUser wtUser : list) {
				team.addPrincipal(role, wtUser);
			}
			team = (Team) PersistenceHelper.manager.refresh(team);
			team = (Team) PersistenceHelper.manager.save(team);
			tx.commit();
			tx = null;
		}
	}

	public static String getRoleNameByDept(String dept){
		String roleName = "";
		if(StrUtil.isNotEmpty(dept)) {
			roleName = propertiesUtil.getProperty(dept);
		}
		return roleName;
	}

	public static boolean checkHasGongShiData(WTObject pbo) {
		boolean flag = false;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			if(pbo instanceof WTDocument) {
				WTDocument document = (WTDocument) pbo;
				MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(document);
				list.addAll(SetListGongShiDingEBuilder.getOperationMapByWTDocument(plan,null, true));
			} else if(pbo instanceof WTChangeOrder2) {
				WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
				MPMProcessPlan beforePlan = null;
				MPMProcessPlan afterPlan = null;
				QueryResult qr = ChangeHelper2.service.getChangeablesBefore(ecn);
				while(qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if(object instanceof WTDocument) {
						if(beforePlan == null) {
							WTDocument doc = (WTDocument) object;
							beforePlan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
						}
					} else if(object instanceof MPMProcessPlan) {
						beforePlan = (MPMProcessPlan) object;
						break;
					}
				}
				QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(ecn);
				while(qResult.hasMoreElements()) {
					Object object = qResult.nextElement();
					if(object instanceof WTDocument) {
						if(afterPlan == null) {
							WTDocument doc = (WTDocument) object;
							afterPlan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
						}
					} else if(object instanceof MPMProcessPlan) {
						afterPlan = (MPMProcessPlan) object;
						break;
					}
				}
				if(afterPlan != null) {
					list.addAll(SetListGongShiDingEBuilder.getOperationMapByWTDocument(afterPlan, beforePlan,true));
				}
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (TeamException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		if(list.size() > 0) {
			flag = true;
		}

		return flag;
	}
}
