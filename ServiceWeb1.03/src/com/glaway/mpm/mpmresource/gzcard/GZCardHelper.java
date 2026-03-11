package com.glaway.mpm.mpmresource.gzcard;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jdom.Element;

import wt.change2.WTChangeIssue;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.project.Role;
import wt.team.Team;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.definer.UserEventVector;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.constants.AttributeConstants;
import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.constants.TypeNameConstants;
import com.glaway.mpm.constants.WorkflowConstants;
import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.pbom.helper.PBOMHelper;
import com.glaway.mpm.util.FolderUtil;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.glaway.mpm.util.WTPrincipalUtil;
import com.glaway.mpm.util.WorkflowUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.ptc.windchill.mpml.resource.MPMTooling;

public class GZCardHelper implements RemoteAccess {
	private static final String CLASSNAME = GZCardHelper.class.getCanonicalName();

	private static PropertiesUtil typeToFolderPropertiesUtil = new PropertiesUtil(
			PropertiesConfigs.MPMRESOURCE_TYPE_TO_FOLDER_CONFIG_PATH);

	/**
	 * 获取显示工装申请卡的URL
	 *
	 * @author qianlong
	 * @date 2013-4-15
	 * @return
	 * @throws WTException
	 *
	 */
	public static String getShowGZCardUrl(WTDocument document) throws WTException {
		// ptc1/glaway/mpm/mpmresource/gz/gzcard/showGZCard1?oid=VR%3Awt.doc.WTDocument%3A1166040
		return "<a href=\"" + WorkflowUtil.getURLString() + "ptc1/glaway/mpm/showGZCard?oid=OR:" + document.toString()
				+ "\" target=_blank>查看工装申请卡</a>";
	}

	/**
	 * 获取工装申请卡审核信息
	 *
	 * @author qianlong
	 * @date 2013-4-17
	 *
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getAuditGZCardInfo(WTDocument document) {
		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		try {
			WfProcess process = null;
			Enumeration<Object> queryResult = WfEngineHelper.service.getAssociatedProcesses(document, null, null);
			while (queryResult.hasMoreElements()) {
				Object object = queryResult.nextElement();
				if (object instanceof WfProcess) {
					process = (WfProcess) object;
					String templateName = process.getTemplate().getName();
					if (WorkflowConstants.gzCardAuditWorkflowTemplateName.equals(templateName)) {
						break;
					}
				}
			}
			if (process != null) {
				getAuditGZCardInfo(process, list);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取工装申请卡审核信息
	 *
	 * @author qianlong
	 * @date 2013-4-17
	 *
	 */
	public static void getAuditGZCardInfo(Object self, List<String> list) {
		try {
			WfProcess process = WorkflowUtil.getWfProcessBySelf(self);
			QueryResult queryResult = WorkflowUtil.getWfVoteWithProcess(process);
			while (queryResult.hasMoreElements()) {
				WfVotingEventAudit audit = (WfVotingEventAudit) ((Persistable[])queryResult.nextElement())[0];
				UserEventVector eventVector = audit.getEventList();
				if (eventVector != null && eventVector.size() != 0) {
					String eventName = eventVector.get(0).toString();
					if (!eventName.equals(WorkflowConstants.TongGuo)) {
						continue;
					}
				}
				String activityName = audit.getActivityName();
				if (activityName.contains(WorkflowConstants.GZCardLiZhi)) {
					list.set(0, audit.getUserRef().getFullName());
				} else if (activityName.contains(WorkflowConstants.GZCardShenHe)) {
					list.set(1, audit.getUserRef().getFullName());
				} else if (activityName.contains(WorkflowConstants.GZCardHuiQian)) {
					list.set(2, audit.getUserRef().getFullName());
				} else if (activityName.contains(WorkflowConstants.GZCardPiZun)) {
					list.set(3, audit.getUserRef().getFullName());
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动工装设计派工任务
	 *
	 * @author qianlong
	 * @date 2013-4-17
	 *
	 */
	public static void startGZDesignWorkflow(WTDocument document, String folderPath) {
//		try {
//			IBAHelper helper = new IBAHelper(document);
//			String productNumber = Util.formateString(helper.getIBAValue(AttributeConstants.productNumber));
//			WTPrincipalReference principalReference = getProcessPlanPeople(document.getContainer(), productNumber);
//			ToolUtil.createToolDsnTask(document.getContainer(), productNumber, document.getNumber(),
//					principalReference, "1", folderPath + "/" + Constants.gzFolderName[7]);
//		} catch (WTException e) {
//			e.printStackTrace();
//		} catch (WTPropertyVetoException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 启动工装评审条目任务
	 *
	 * @author qianlong
	 * @date 2013-4-17
	 *
	 */
	public static String startGZAuditWorkflow(WTDocument document, String folderPath) {
//		try {
//			IBAHelper helper = new IBAHelper(document);
//			String productNumber = Util.formateString(helper.getIBAValue(AttributeConstants.productNumber));
//			WTPrincipalReference principalReference = getProcessPlanPeople(document.getContainer(), productNumber);
//			ToolUtil.createGMToolingReviewTask(document.getContainer(), productNumber, document.getNumber(),
//					principalReference, folderPath + "/" + Constants.gzFolderName[7], document.getName() + "_"
//							+ document.getNumber());
//
//		} catch (WTException e) {
//			e.printStackTrace();
//		} catch (WTPropertyVetoException e) {
//			e.printStackTrace();
//		} catch (WTRuntimeException e) {
//			e.printStackTrace();
//		}
		return "";
	}

	/**
	 * 启动工装复查条目任务
	 *
	 * @author qianlong
	 * @date 2013-4-17
	 *
	 */
	public static String startGZCheckWorkflow(WTChangeIssue changeIssue) {
//		try {
//			WTPart part = null;
//			QueryResult queryResult = ChangeHelper2.service.getChangeables(changeIssue);
//			while (queryResult.hasMoreElements()) {
//				Object object = queryResult.nextElement();
//				if (object instanceof WTPart) {
//					part = (WTPart) object;
//					break;
//				}
//			}
//			IBAHelper helper = new IBAHelper(part);
//			String productNumber = Util.formateString(helper.getIBAValue(AttributeConstants.productNumber));
//			WTPrincipalReference principalReference = getProcessPlanPeople(part.getContainer(), productNumber);
//			String folderPath = ((SubFolder) part.getParentFolder().getObject()).getLocation() + "/"
//					+ Constants.gzFolderName[7];
//			ToolUtil.createGMToolingCheckTask(part.getContainer(), productNumber, part.getNumber(), principalReference,
//					folderPath, part.getName() + "_" + part.getNumber());
//		} catch (WTException e) {
//			e.printStackTrace();
//		} catch (WTPropertyVetoException e) {
//			e.printStackTrace();
//		} catch (WTRuntimeException e) {
//			e.printStackTrace();
//		}
		return "";
	}

	/**
	 * 启动工装工艺任务
	 *
	 * @author qianlong
	 * @date 2013-4-17
	 *
	 */
	public static String startGZProcessWorkflow(WTPart part) {
//		try {
//			if (Constants.design.equals(part.getViewName())) {
//				View view = WTPartUtil.getViewByName(Constants.planning);
//				if (null != view) {
//					WTPart p = WTPartUtil.getPartByNumberAndView(part.getNumber(), view.getPersistInfo()
//							.getObjectIdentifier().getId());
//					if (null != p) {
//						part = p;
//					}
//				}
//			}
//			IBAHelper helper = new IBAHelper(part);
//			String productNumber = Util.formateString(helper.getIBAValue(AttributeConstants.productNumber));
//			WTPrincipalReference principalReference = getProcessPlanPeople(part.getContainer(), productNumber);
//			String folderPath = ((SubFolder) part.getParentFolder().getObject()).getLocation() + "/"
//					+ Constants.gzFolderName[7];
//			ToolUtil.createToolTechDsnTask(part.getContainer(), productNumber, part.getNumber(), principalReference,
//					getNeedCreateProcessPlanPart(part) + "", part, folderPath);
//
//		} catch (WTException e) {
//			e.printStackTrace();
//		} catch (WTPropertyVetoException e) {
//			e.printStackTrace();
//		} catch (PropertyVetoException e) {
//			e.printStackTrace();
//		}
		return "";
	}

	/**
	 * 获取需要做工艺的零件的个数
	 *
	 * @author qianlong
	 * @throws PropertyVetoException
	 * @throws WTException
	 * @date 2013-8-14
	 */
	private static int getNeedCreateProcessPlanPart(WTPart part) throws WTException, PropertyVetoException {

		List<Element> list = new ArrayList<Element>();
		WTDocument doc = PBOMHelper.getBOMXmlDoc(part, Constants.pbomDocEndwith);
		ApplicationData data = (ApplicationData) ContentHelper.service.getPrimary(doc);
		SWXMLUtil xmlUtil = new SWXMLUtil(ContentServerHelper.service.findContentStream(data));
		Element rootElement = xmlUtil.getRootElement();
		Element attrElement = rootElement.getChild(XMLConstants.parts).getChild(XMLConstants.QMPartInfo);
		getSubPart(attrElement, list);

		return list.size();
	}

	private static void getSubPart(Element attrElement, List<Element> list) throws WTException {
		String partType = attrElement.getAttributeValue("partType");
		if ("已归档".equals(attrElement.getAttributeValue("lifecycle")) || "PurchasedPart".equals(partType)
				|| "StandardPart".equals(partType) || "ALKPart".equals(partType) || "SHPart".equals(partType)) {
		} else {
			list.add(attrElement);
		}
		Element childElement = attrElement.getChild(XMLConstants.childs);
		if (childElement == null) {
			return;
		}
		for (Element childAttrElement : (List<Element>) childElement.getChildren()) {
			getSubPart(childAttrElement, list);
		}
	}

	/**
	 * 获取工艺部计划员
	 *
	 * @author qianlong
	 * @date 2013-4-17
	 * @return
	 * @throws WTException
	 *
	 */
	private static WTPrincipalReference getProcessPlanPeople(WTContainer thisContainer, String productNumber)
			throws WTException {
		WTContainer container = null;
		// 当产品代号存在，就取该产品的专案团队中的工艺计划员
		// 当产品代号不存在，就取该工装设计库中的工艺计划员
		if (null != productNumber && !"".equals(productNumber)) {
			container = WTContainerUtil.getContainerByName(productNumber);
		}
		if (null == container) {
			container = thisContainer;
		}
		return WTPrincipalUtil.getGONGYIBUJIHUAYUAN(container);
	}

	/**
	 * 创建工装设计库文件夹结构 和工装顶层part
	 *
	 * @author qianlong
	 * @date 2013-4-2
	 *
	 */
	@SuppressWarnings("deprecation")
	public static String createGZFolder(WTDocument document) {
		if (document == null) {
			return "";
		}
		String path = Constants.rootFolder;
		String number = document.getNumber();
		String name = document.getName();
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			WTContainer container = null;
			if (number.startsWith(Constants.alGZ)) {
				container = WTContainerUtil.getContainerByName(Constants.alGZLibraryName);
			} else if (number.startsWith(Constants.kGZ)) {
				container = WTContainerUtil.getContainerByName(Constants.kGZLibraryName);
			} else if (number.startsWith(Constants.tGZ)) {
				container = WTContainerUtil.getContainerByName(Constants.tGZLibraryName);
			}
			if (container == null) {
				throw new Exception("上下文不存在!!");
			}
			WTContainerRef containerRef = WTContainerRef.newWTContainerRef(container);
			path = path + "/" + number.split("\\.")[0] + "/" + number + "_" + name;
			GLLogger.debug(CLASSNAME, "--path--" + path);
			for (String str : Constants.gzFolderName) {
				FolderUtil.getFolder(path + "/" + str, containerRef);
			}
			transaction.commit();
			transaction = null;

			WTPart rootPart = WTPart.newWTPart();
			rootPart.setNumber(number);
			rootPart.setName(name);
			rootPart.setContainer(container);
			Folder folder = FolderUtil.getFolder(path + "/" + Constants.gzFolderName[0], WTContainerRef
					.newWTContainerRef(container));
			FolderHelper.assignFolder(rootPart, folder);
			TypeUtil.setType(rootPart, TypeNameConstants.gzRootPartTypeName);
			rootPart.setEndItem(true);
			PersistenceHelper.manager.save(rootPart);

			IBAHelper docIBAHelper = new IBAHelper(document);
			IBAHelper partIBAHelper = new IBAHelper(rootPart);
			Map<String, String> ibaMap = new HashMap<String, String>();
			String productNumber = docIBAHelper.getIBAValue(AttributeConstants.productNumber);
			ibaMap.put(AttributeConstants.productNumber, Util.formateString(productNumber));
			String insertPart = docIBAHelper.getIBAValue(AttributeConstants.insertPart);
			ibaMap.put(AttributeConstants.insertPart, Util.formateBoolean(insertPart));
			String productionNumber = docIBAHelper.getIBAValue(AttributeConstants.productionNumber);
			ibaMap.put(AttributeConstants.productionNumber, Util.formateInteger(productionNumber));
			String isReview = docIBAHelper.getIBAValue(AttributeConstants.isReview);
			ibaMap.put(AttributeConstants.isReview, Util.formateBoolean(isReview));
			String isCommonTools = docIBAHelper.getIBAValue(AttributeConstants.isCommonTools);
			ibaMap.put(AttributeConstants.isCommonTools, Util.formateString(isCommonTools));
			String isTestPart = docIBAHelper.getIBAValue(AttributeConstants.isTestPart);
			ibaMap.put(AttributeConstants.isTestPart, Util.formateBoolean(isTestPart));
			String partNumber = docIBAHelper.getIBAValue(AttributeConstants.partNumber);
			ibaMap.put(AttributeConstants.partNumber, Util.formateString(partNumber));
			String wholePartNumber = docIBAHelper.getIBAValue(AttributeConstants.wholePartNumber);
			ibaMap.put(AttributeConstants.wholePartNumber, Util.formateString(wholePartNumber));
			String isRegularlyTools = docIBAHelper.getIBAValue(AttributeConstants.isRegularlyTools);
			ibaMap.put(AttributeConstants.isRegularlyTools, Util.formateBoolean(isRegularlyTools));

			partIBAHelper.setIBAValue(rootPart, ibaMap);

			// 关联工装申请卡和工装成品

			WTPartUtil.createWTPartDescribeLink(rootPart, document);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return path;
	}

	/**
	 * 通过组员获取组长 并把组长放到流程角色中
	 *
	 * @author qianlong
	 * @date 2013-4-7
	 *
	 */
	public static boolean setLeaderByMember(Object self, String wfRoleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--wfRoleName--" + wfRoleName);
		boolean flag = true;
		try {
			// 获取当前流程
			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);
			// 获取当前流程创建人的组长所在角色
			List<String> filterRole = new ArrayList<String>();
			filterRole.add("LIBRARY MANAGER");
			Role leaderRole = getLeaderByMember(wf, filterRole);
			GLLogger.debug(CLASSNAME, "leaderRole------" + leaderRole);
			// 把组长添加到流程中的角色中
			setUserToWfRole(leaderRole, wfRoleName, wf);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	// ------------------------------------------------------------------------------------------------------------------

	/**
	 * 把工装申请卡的申请人加入到'工装设计审核流程'
	 *
	 * @author qianlong
	 * @date 2013-6-4
	 * @param self
	 * @param part
	 * @param wfRoleName
	 * @return
	 *
	 */
	public static boolean setApplicantToWfRole(Object self, WTPart part, String wfRoleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--wfRoleName--" + wfRoleName);
		boolean flag = true;
		try {
			// 获取当前流程
			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);
			WTDocument document = WTDocumentUtil.getDocumentByNumber(part.getNumber());
			if (null == document) {
				throw new WTException("工装申请卡'" + part.getNumber() + "'不存在！");
			}
			// 把工装组长添加到流程中的角色中
			setUserToWfRole((WTPrincipal) document.getCreator().getObject(), wfRoleName, wf);

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 把工装设计任务中的'审核者'加入到'工装设计审核流程'
	 *
	 * @author qianlong
	 * @date 2013-7-12
	 * @param self
	 * @param wfRoleName
	 * @return
	 */
	public static boolean setGZDesignAuditorToWfRole(Object self, WTPart part, String wfRoleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--part--" + part);
		boolean flag = true;
//		try {
//			// 获取当前流程
//			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);
//			// 获取工装设计任务
//			GMToolingDesignTask task = getGMToolingDesignTaskByPartNumber(part.getNumber());
//			System.out.println("task--" + task);
//			// 把工装组长添加到流程中的角色中
//			setUserToWfRole(task.getReviewer().getPrincipal(), wfRoleName, wf);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			flag = false;
//		}
		return flag;
	}

	/**
	 * 把工艺任务中的'审核者'加入到'工装申请卡审核流程'
	 *
	 * @author qianlong
	 * @date 2013-7-12
	 * @param self
	 * @param wfRoleName
	 * @return
	 */
	public static boolean setProcessAuditorToWfRole(Object self, WTDocument document, String wfRoleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--document--" + document);
		boolean flag = true;
//		try {
//			// 获取当前流程
//			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);
//			String parOid = IBAHelper.getAnyIBAValueOfObject(document, AttributeConstants.partOid);
//			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, parOid);
//			if (null == part) {
//				throw new WTException("工装申请卡 " + document.getNumber() + " 没有对应的零件！");
//			}
//			// 获取工艺任务
//			GMTechnicTask task = TaskUtil.getTechnicTaskByPart(part);
//			if (null == task) {
//				throw new WTException("工装申请卡 " + document.getNumber() + " 没有找到对应的工艺任务！");
//			}
//			// 把工装组长添加到流程中的角色中
//			setUserToWfRole(task.getReviewer().getPrincipal(), wfRoleName, wf);
//		} catch (Exception e) {
//			e.printStackTrace();
//			flag = false;
//		}
		return flag;
	}

	/**
	 * 把工装组组长加入到'工装设计审核流程'和'工装设计更改通知单审核流程'
	 *
	 * @author qianlong
	 * @date 2013-6-4
	 * @param self
	 * @param wfRoleName
	 * @return
	 *
	 */
	public static boolean setGZLeaderToWfRole(Object self, String wfRoleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--wfRoleName--" + wfRoleName);
		boolean flag = true;
		try {
			// 获取当前流程
			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);
			WTGroup gzLeaderGroup = WTPrincipalUtil.getGroupByName(Constants.GongZhuangZuLeader);
			if (null == gzLeaderGroup) {
				throw new WTException("不存在'" + Constants.GongZhuangZuLeader + "'群组！");
			}

			// 把工装组长添加到流程中的角色中
			setUserToWfRole(gzLeaderGroup, wfRoleName, wf);

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 通过组员获取组长 并把组长放到'工装申请卡审核流程'
	 *
	 * @author qianlong
	 * @date 2013-6-4
	 * @param self
	 * @param wfRoleName
	 * @return
	 *
	 */
	public static boolean setLeaderToWfRole(Object self, String wfRoleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--wfRoleName--" + wfRoleName);
		boolean flag = true;
		try {
			// 获取当前流程
			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);
			// 获取当前流程创建人的组长所在群组
			WTGroup leaderGroup = getLeaderByUser(wf);
			// 把组长添加到流程中的角色中
			setUserToWfRole(leaderGroup, wfRoleName, wf);

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 在流程活动角色中添加人员
	 *
	 * @author qianlong
	 * @date 2013-4-7
	 * @param self
	 * @param wfRoleName
	 *
	 */
	public static boolean setUserToWfRole(Object self, String wfRoleName, String roleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--wfRoleName--" + wfRoleName + "--roleName--" + roleName);
		boolean flag = true;
		try {
			// 获取当前流程
			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);

			// 把人员添加到流程中的角色中
			Role role = Role.toRole(roleName);
			setUserToWfRole(role, wfRoleName, wf);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 在流程活动角色中添加人员
	 *
	 * @author qianlong
	 * @throws Exception
	 * @date 2013-4-7
	 *
	 */
	private static void setUserToWfRole(Role role, String wfRoleName, WfProcess wf) throws Exception {
		// 获取当前上下文的 专案团队
		WTContainer container = wf.getContainer();
		ContainerTeam containerTeam = WorkflowUtil.getContainerTeam(container);
		// 获取流程的专案团队
		Team wfTeam = (Team) wf.getTeamId().getObject();

		// 给流程中的专案团队下的角色设置人员
		Role wfRole = Role.toRole(wfRoleName);
		Enumeration<WTPrincipalReference> prinEnumRole = containerTeam.getPrincipalTarget(role);
		if (prinEnumRole == null || prinEnumRole.hasMoreElements() == false) {
			throw new Exception("角色'" + wfRole + "'不存在，或者角色中不存在人员！");
		}
		while (prinEnumRole.hasMoreElements()) {
			WTPrincipal principal = (WTPrincipal) prinEnumRole.nextElement().getObject();
			wfTeam.addPrincipal(wfRole, principal);
		}
	}

	/**
	 * 在流程活动角色中添加人员
	 *
	 * @author qianlong
	 * @date 2013-6-4
	 * @param group
	 * @param wfRoleName
	 * @param wf
	 * @throws Exception
	 *
	 */
	public static void setUserToWfRole(WTGroup group, String wfRoleName, WfProcess wf) throws Exception {
		// 获取流程的专案团队
		Team wfTeam = (Team) wf.getTeamId().getObject();
		Role wfRole = Role.toRole(wfRoleName);

		// 给流程中的专案团队下的角色设置人员
		Enumeration enumeration = WTPrincipalUtil.getChildUser(group);
		if (!enumeration.hasMoreElements()) {
			throw new WTException("'" + group.getName() + "' 下面没有添加人员！");
		}
		while (enumeration.hasMoreElements()) {
			Object object = enumeration.nextElement();
			if (object instanceof WTUser) {
				wfTeam.addPrincipal(wfRole, (WTUser) object);
			}
		}
	}

	/**
	 * 在流程活动角色中添加人员
	 *
	 * @author qianlong
	 * @throws Exception
	 * @date 2013-4-7
	 *
	 */
	public static void setUserToWfRole(WTPrincipal principal, String wfRoleName, WfProcess wf) throws Exception {
		// 获取流程的专案团队
		Team wfTeam = (Team) wf.getTeamId().getObject();

		// 给流程中的专案团队下的角色设置人员
		Role wfRole = Role.toRole(wfRoleName);
		wfTeam.addPrincipal(wfRole, principal);
	}

	/**
	 * 工装审核流程，根据人员获取当前的专业组长
	 *
	 * @author qianlong
	 * @date 2013-6-4
	 * @param wf
	 * @return
	 * @throws WTException
	 *
	 */
	public static WTGroup getLeaderByUser(WfProcess wf) throws WTException {
		WTGroup leaderGroup = null;
		WTGroup userGroup = null;

		WTPrincipal principal = (WTPrincipal) wf.getCreator().getObject();

		// 判断创建人员是 专业组下面的哪个组的人员
		Enumeration enumeration = WTPrincipalUtil.getAllMembers(WTPrincipalUtil.getGroupByName(Constants.ZhuanYeZu));
		while (enumeration.hasMoreElements()) {
			Object object = enumeration.nextElement();
			if (object instanceof WTGroup) {
				if (((WTGroup) object).isMember(principal)) {
					userGroup = (WTGroup) object;
					break;
				}
			}
		}
		// 当创建者为组长时返回该组
		// 当创建者为成员时返回该成员组的组长
		if (null != userGroup) {
			if (userGroup.getName().contains("组长")) {
				leaderGroup = userGroup;
			} else {
				leaderGroup = WTPrincipalUtil.getGroupByName(userGroup.getName() + "组长");
				if (null == leaderGroup) {
					throw new WTException("'" + userGroup.getName() + "' 没有对应的组长群组！");
				}
			}
		} else {
			throw new WTException("'" + principal.getName() + "' 不是工艺师,或者没有加入到专业组下面的子群组中！");
		}
		return leaderGroup;
	}

	/**
	 * 获取当前流程的创建者所在组长角色
	 *
	 * @author qianlong
	 * @date 2013-4-7
	 * @param self
	 * @throws Exception
	 *
	 */
	private static Role getLeaderByMember(WfProcess wf, List<String> filterRole) throws Exception {
		Role leaderRole = null;
		Role memberRole = null;
		// 获取当前上下文的 专案团队
		WTContainer container = wf.getContainer();
		ContainerTeam containerTeam = WorkflowUtil.getContainerTeam(container);

		WTPrincipalReference prinRef = wf.getCreator();
		WTPrincipal principal = (WTPrincipal) prinRef.getObject();

		Vector<Role> vec = containerTeam.getRoles();
		for (int i = 0; i < vec.size(); i++) {
			Role roleTemp = vec.get(i);
			if (filterRole.contains(roleTemp.toString().toUpperCase())) {
				continue;
			}
			Enumeration<WTPrincipalReference> eprin = containerTeam.getPrincipalTarget(roleTemp);
			while (eprin.hasMoreElements()) {
				WTPrincipal temp = eprin.nextElement().getPrincipal();
				if (temp != null && temp.equals(principal)) {
					memberRole = roleTemp;
				}
			}
		}
		GLLogger.debug(CLASSNAME, "memberRole------" + memberRole);
		if (memberRole == null) {
			throw new Exception("流程创建人'" + prinRef.getFullName() + "'没有被加入团队角色!");
		}

		String rolename = memberRole.toString();
		leaderRole = Role.toRole(rolename + "MPMLEADER");
		return leaderRole;
	}

	/**
	 * 判断是否评审
	 *
	 * @author qianlong
	 * @date 2013-4-12
	 * @return
	 *
	 */
	public static boolean isReview(WTDocument document) {
		boolean tag = true;
		try {
			IBAHelper helper = new IBAHelper(document);
			String isReview = helper.getIBAValue(AttributeConstants.isReview);
			tag = Boolean.valueOf(isReview);
		} catch (WTException e) {
			tag = false;
		}
		return tag;
	}

	/**
	 * 工装编辑器中创建工装申请卡
	 *
	 * @author qianlong
	 * @date 2013-4-25
	 * @param number
	 * @param name
	 * @param ibaMap
	 * @param fileName
	 * @param inputStream
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 * @throws IOException
	 *
	 */
	public static WTDocument createGZCard(String number, String name, Map<String, String> ibaMap,
			Map<String, InputStream> fileMap) throws WTException, PropertyVetoException, FileNotFoundException,
			IOException {
		String folderPath = Constants.rootFolder + "/" + Constants.gzCardFolderName;
		String type = TypeNameConstants.gzCardTypeName;
		String containerName = "";
		if (number.startsWith(Constants.alGZ)) {
			containerName = Constants.alGZLibraryName;
		} else if (number.startsWith(Constants.kGZ)) {
			containerName = Constants.kGZLibraryName;
		} else if (number.startsWith(Constants.tGZ)) {
			containerName = Constants.tGZLibraryName;
		}
		WTContainer container = WTContainerUtil.getContainerByName(containerName);

		WTDocument document = WTDocument.newWTDocument();
		document.setNumber(number);
		document.setName(name);
		document.setContainer(container);

		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
		FolderHelper.assignFolder(document, folder);

		TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(type);
		document.setTypeDefinitionReference(typeRef);

		document = (WTDocument) PersistenceHelper.manager.save(document);

		IBAHelper helper = new IBAHelper(document);
		helper.setIBAValue(document, ibaMap);

		// if (null != fileName && !"".equals(fileName) && null != inputStream)
		// {
		//
		// document = (WTDocument) PersistenceHelper.manager.refresh(document);
		// ApplicationData appData = (ApplicationData)
		// ContentHelper.service.getPrimary(document);
		// if (appData != null) {
		// PersistenceHelper.manager.delete(appData);
		// PersistenceServerHelper.manager.update(document);
		// }
		// appData = ApplicationData.newApplicationData(document);
		// // 主物件和附件
		// // ContentRoleType.SECONDARY 表示 附件
		// // ContentRoleType.PRIMARY 表示主物件
		// appData.setRole(ContentRoleType.PRIMARY);
		// // 设置文件名称
		// appData.setFileName(fileName);
		// appData = ContentServerHelper.service.updateContent((ContentHolder)
		// document, appData, inputStream); // 更新内容
		// PersistenceServerHelper.manager.update(document);
		// document = (WTDocument)
		// ContentServerHelper.service.updateHolderFormat((FormatContentHolder)
		// document); // 更新格式
		// }
		document = (WTDocument) PersistenceHelper.manager.refresh(document);
		for (String fileName : fileMap.keySet()) {
			ApplicationData appData = ApplicationData.newApplicationData(document);
			appData.setRole(ContentRoleType.SECONDARY);
			appData.setFileName(fileName);
			appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, fileMap
					.get(fileName)); // 更新内容
		}
		PersistenceServerHelper.manager.update(document);
		document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
		return document;
	}

	/**
	 * 创建工装资源
	 *
	 * @author qianlong
	 * @date 2013-5-30
	 * @param number
	 * @param name
	 * @param ibaMap
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 * @throws IOException
	 *
	 */
	public static MPMTooling createGZResource(String number, String name, Map<String, String> ibaMap)
			throws WTException, PropertyVetoException, FileNotFoundException, IOException {

		WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
		String objectType = TypeNameConstants.GZhuang;
		String folderPath = typeToFolderPropertiesUtil.getProperty(objectType);
		MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, objectType,"");
		IBAHelper helper = new IBAHelper(tooling);
		helper.setIBAValue(tooling, ibaMap);
		return tooling;
	}

	/**
	 * 归档整个工装BOM ，同时归档资源
	 *
	 * @author qianlong
	 * @date 2013-4-24
	 *
	 */
	public static void setGZObjectLifeCycle(WTPart part, String state) {
//		try {
//			setAllPartLifeCycle(part, state);
//			MPMTooling tooling = MPMResourceUtil.getMPMToolingByNumber(part.getNumber().replace(".", "-"),
//					TypeNameConstants.GZhuang);
//			LifeCycleHelper.service.setLifeCycleState(tooling, State.toState(state));
//			GMToolingDesignTask task = getGMToolingDesignTaskByPartNumber(part.getNumber());
//			LifeCycleHelper.service.setLifeCycleState(task, State.toState(state));
//		} catch (WTException e) {
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		} catch (WTPropertyVetoException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 设置工装零件生命周期状态
	 *
	 * @author qianlong
	 * @date 2013-6-3
	 * @param part
	 * @param state
	 *
	 */
	public static void setAllPartLifeCycle(WTPart part, String state) {
		try {
			Util.setLifecycle(part, state);
			QueryResult qr1 = WTPartUtil.getEPMDocumentByPart(part);
			while (qr1 != null && qr1.hasMoreElements()) {
				EPMDocument document = (EPMDocument) qr1.nextElement();
				Util.setLifecycle(document, state);
			}

			QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part, WTPartUtil.getConfigSpec());
			while (qr.hasMoreElements()) {
				Persistable[] per = (Persistable[]) qr.nextElement();
				if (per[1] instanceof WTPart) {
					WTPart childPart = (WTPart) per[1];
					setAllPartLifeCycle(childPart, state);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 工艺编辑器中获取工装申请卡的信息
	 *
	 * @author qianlong
	 * @date 2013-5-30
	 * @param document
	 * @param tag
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 *
	 */
	public static Map<String, Object> getShowFrockCardinfo(WTDocument document, boolean tag) throws WTException,
			PropertyVetoException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
//		IBAHelper helper = new IBAHelper(document);
//		returnMap.put(AttributeConstants.number, document.getNumber());
//		returnMap.put(AttributeConstants.name, document.getName());
//		returnMap.put(AttributeConstants.insertPart, helper.getIBAValue(AttributeConstants.insertPart));
//		returnMap.put(AttributeConstants.isReview, helper.getIBAValue(AttributeConstants.isReview));
//		returnMap.put(AttributeConstants.isCommonTools, helper.getIBAValue(AttributeConstants.isCommonTools));
//		returnMap.put(AttributeConstants.isTestPart, helper.getIBAValue(AttributeConstants.isTestPart));
//		returnMap.put(AttributeConstants.workShop, helper.getIBAValue(AttributeConstants.workShop));
//		returnMap.put(AttributeConstants.productNumber, helper.getIBAValue(AttributeConstants.productNumber));
//		returnMap.put(AttributeConstants.productionNumber, helper.getIBAValue(AttributeConstants.productionNumber));
//		returnMap.put(AttributeConstants.partNumber, helper.getIBAValue(AttributeConstants.partNumber));
//		returnMap.put(AttributeConstants.wholePartNumber, helper.getIBAValue(AttributeConstants.wholePartNumber));
//		returnMap.put(AttributeConstants.toolingRequirements, helper
//				.getIBAValue(AttributeConstants.toolingRequirements));
//		returnMap.put(AttributeConstants.isRegularlyTools, helper.getIBAValue(AttributeConstants.isRegularlyTools));
//		returnMap.put("applyTime", document.getCreateTimestamp());
//		if (tag) {
//			QueryResult result = WTDocumentUtil.getSecondaryByDocument(document);
//			Map<String, byte[]> imageMap = new HashMap<String, byte[]>();
//			Map<String, byte[]> secondaryMap = new HashMap<String, byte[]>();
//			while (result.hasMoreElements()) {
//				ApplicationData data = (ApplicationData) result.nextElement();
//				String fileName = data.getFileName();
//				if (fileName.contains("secondaryfile")) {
//					secondaryMap.put(fileName.replace("secondaryfile-", ""), WTDocumentUtil.applicationDataToByte(data));
//				} else {
//					imageMap.put(fileName, WTDocumentUtil.applicationDataToByte(data));
//				}
//			}
//			returnMap.put("fileBytes", imageMap);
//			returnMap.put("secondaryFileBytes", secondaryMap);
//			List<String> auditList = GZCardHelper.getAuditGZCardInfo(document);
//			returnMap.put("audit", auditList);
//		}
		return returnMap;
	}

	/**
	 *
	 * @author qianlong
	 * @date 2013-7-12
	 * @param object
	 * @return
	 */
	public static WTDocument getGZCardDocument(Object object) {
		WTDocument document = null;
		if (object instanceof WTDocument) {
			document = (WTDocument) object;
		} else if (object instanceof WorkItem) {
			WorkItem workItem = (WorkItem) object;
			document = (WTDocument) workItem.getPrimaryBusinessObject().getObject();
		}
		return document;
	}

	/**
	 * 获取工装设计任务
	 *
	 * @author qianlong
	 * @date 2013-7-12
	 * @param partNumber
	 * @throws WTException
	 */
//	public static GMToolingDesignTask getGMToolingDesignTaskByPartNumber(String partNumber) throws WTException {
//		GMToolingDesignTask task = null;
//		QuerySpec querySpec = new QuerySpec(GMToolingDesignTask.class);
//		querySpec.appendWhere(new SearchCondition(GMToolingDesignTask.class, GMToolingDesignTask.PART_NUMBER,
//				SearchCondition.EQUAL, partNumber), new int[] { 0 });
//		QueryResult result = PersistenceHelper.manager.find(querySpec);
//		result = new LatestConfigSpec().process(result);
//		if (result.hasMoreElements()) {
//			task = (GMToolingDesignTask) result.nextElement();
//		}
//		return task;
//	}

	public static void create() throws RemoteException, WTPropertyVetoException, WTException {
		QueryResult queryResult = WTPartUtil
				.getPartByLikeNumberNameType("com.nriet.ToolingProduct", "GAL015.0006", "%");
		WTPart part = (WTPart) queryResult.nextElement();
		System.out.println(part);
		for (int i = 1; i < 5; i++) {

			WTPart part1 = WTPartUtil.createPart("GAL015.0006-6.00" + i, "GAL015.0006-6.00" + i, part.getContainer(),
					part.getLocation(), "Design", "com.nriet.ToolingPart");

			WTPartUtil.createWTPartUsageLink(part, (WTPartMaster) part1.getMaster());
			WTPart part2 = WTPartUtil.createPart("GAL015.0006-8.00" + i, "GAL015.0006-8.00" + i, part.getContainer(),
					part.getLocation(), "Design", "com.nriet.ToolingPart");

			WTPartUtil.createWTPartUsageLink(part1, (WTPartMaster) part2.getMaster());
		}
	}

	public static void test() throws WTException, PropertyVetoException {
		System.out.println(getNeedCreateProcessPlanPart((WTPart) Util.getObjectByOid(WTPart.class, "1459419")));
	}

	public static void main(String[] args) {
		RemoteMethodServer server = RemoteMethodServer.getDefault();
		server.setUserName("wcadmin");
		server.setPassword("wcadmin");
		try {
			server.invoke("test", GZCardHelper.class.getName(), null, null, null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
