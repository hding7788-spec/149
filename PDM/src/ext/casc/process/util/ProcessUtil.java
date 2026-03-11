package ext.casc.process.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.ptc.netmarkets.group.NmGroup;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.uwgm.common.container.OrganizationHelper;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.constants.Constants;
import ext.casc.constants.PDMConfig;
import ext.casc.process.*;
import ext.casc.process.assign.ProProcessor;
import ext.casc.service.CascCacheManager;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;
import ext.casc.system.SystemConfigurationUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.VariableCache;
import ext.casc.util.WCUtil;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.Master;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.*;
import wt.fc.collections.WTArrayList;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.inf.container.*;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.log4j.LogR;
import wt.org.*;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.*;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.type.TypedUtilityServiceHelper;
import wt.util.*;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.WfProcess;

import javax.xml.namespace.QName;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProcessUtil {
	private final static String CREATE_DATE = "thePersistInfo.createStamp";
	private static final Logger LOG = LogR.getLogger(ProcessUtil.class.getName());

	public static List<String> getAllContaner() throws WTException {
		List<String> list = new ArrayList<String>();
		list.add(ProcessConstants.JSP_SEARCH_ALLCONTAINER);
		QueryResult qResult = getAllProduct();
		while (qResult.hasMoreElements()) {
			PDMLinkProduct product = (PDMLinkProduct) qResult.nextElement();
			list.add(product.getName());
		}
		qResult = getAllWTLibrary();
		while (qResult.hasMoreElements()) {
			WTLibrary library = (WTLibrary) qResult.nextElement();
			list.add(library.getName());
		}
		return list;
	}

	public static QueryResult getAllProduct() throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		QuerySpec qSpec = new QuerySpec(PDMLinkProduct.class);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		SessionServerHelper.manager.setAccessEnforced(flag);
		return qResult;
	}

	public static QueryResult getAllWTLibrary() throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		QuerySpec qSpec = new QuerySpec(WTLibrary.class);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		SessionServerHelper.manager.setAccessEnforced(flag);
		return qResult;
	}

	public static String getNotNullParam(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return obj.toString();
		}
	}

	public static WTUser getUserByName(String name) throws WTException {
		QuerySpec qSpec = new QuerySpec(WTUser.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, name);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		if (qResult.hasMoreElements()) {
			return (WTUser) qResult.nextElement();
		}
		return null;
	}

	public static List<WTPart> getChildPartsByPart(List parents) throws WTException {
		List<WTPart> list = new ArrayList<WTPart>();
		Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents), getDefaultConfigSpec());
		WTContainer childContainer = null;
		WTContainer parentContainer = null;
		for (ListIterator i = parents.listIterator(); i.hasNext();) {
			WTPart parent = (WTPart) i.next();
			list.add(parent);
			parentContainer = parent.getContainer();
			Persistable[][] branch = all_children[i.previousIndex()];
			if (branch == null) {
				continue;
			}
			List children = new ArrayList(branch.length);
			for (Persistable[] child : branch) {
				Persistable per = child[1];
				if (per instanceof WTPart) {
					WTPart childPart = (WTPart) per;
					childPart = WCUtil.getLatestPartByView((Master) childPart.getMaster(), "Manufacturing");
					if (childPart == null) {
						continue;
					}
					IBAUtility ibaUtility = new IBAUtility(childPart);
					String partType = ibaUtility.getIBAValue("MTYPE");
					if (partType != null && partType.equals(Constants.TYPE_ZIZHIJIAN)) {// 过滤不是自制件类型的零部件
						childContainer = childPart.getContainer();
						if (!parentContainer.getName().equals(childContainer.getName())) {// 过滤掉借用件，即是跟父件不在同一产品库下的零部件
							continue;
						}
						list.add(childPart);
					}
				}
			}
		}
		return list;
	}

	protected static ConfigSpec getDefaultConfigSpec() throws WTException {
		return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
	}

	public static WTPart getWtPart(long longId) throws WTException {
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		if (qResult.hasMoreElements()) {
			return (WTPart) qResult.nextElement();
		}
		return null;
	}

	public static List<EPMDocument> getRelatedEPM(WTPart part) throws WTException {
		List<EPMDocument> returnList = new ArrayList<EPMDocument>();
		QueryResult qr = PersistenceHelper.manager.navigate(part, EPMBuildRule.BUILD_SOURCE_ROLE, EPMBuildRule.class, false);
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof EPMBuildRule) {
				EPMBuildRule ebr = (EPMBuildRule) obj;
				if (ebr.getBuildSource() instanceof EPMDocument) {
					returnList.add((EPMDocument) ebr.getBuildSource());
				}
			}
		}
		return returnList;
	}

	/**
	 * 获取3维CAD模型对应的2维CAD图样文档最新小版本
	 *
	 * @param part
	 *            WTPart对象
	 * @return 设计文档集合
	 * @throws Exception
	 */
	public static EPMDocument getLastestIter2DesignDocs(EPMDocument epm3d) {
		EPMDocument epm2dBefore = null;
		// 找3d模型文件的2d图样文件
		try {
			QueryResult qr2d = PersistenceHelper.manager.navigate(epm3d.getMaster(), EPMReferenceLink.REFERENCED_BY_ROLE, EPMReferenceLink.class, false);
			while (qr2d.hasMoreElements()) {
				EPMReferenceLink link = (EPMReferenceLink) qr2d.nextElement();
				if (link.getDepType() != 4) // 不是图纸关联关系
					continue;
				EPMDocument epm2d = link.getReferencedBy();
				Mastered master = epm2d.getMaster();
				QueryResult qr3 = VersionControlHelper.service.allIterationsOf(master);
				if (qr3.hasMoreElements()) {
					epm2dBefore = (EPMDocument) qr3.nextElement();
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return epm2dBefore;
	}

	public static List<WTDocument> getDescribedDoc(WTPart part) throws WTException {
		List<WTDocument> list = new ArrayList<WTDocument>();
		QueryResult qResult = WTPartHelper.service.getDescribedByWTDocuments(part, false);
		WTPartDescribeLink wtPartDescribeLink = null;
		while (qResult.hasMoreElements()) {
			Object object = qResult.nextElement();
			wtPartDescribeLink = (WTPartDescribeLink) object;
			list.add(wtPartDescribeLink.getDescribedBy());
		}
		return list;
	}

	public static List<WTDocument> getReferencedDoc(WTPart part) throws WTException {
		List<WTDocument> list = new ArrayList<WTDocument>();
		QueryResult qResult = WTPartHelper.service.getReferencesWTDocumentMasters(part);
		while (qResult.hasMoreElements()) {
			WTDocumentMaster refDocMaster = (WTDocumentMaster) qResult.nextElement();
			WTDocument refDoc = WCUtil.getDoc(refDocMaster.getNumber());
			if (!list.contains(refDoc))
				list.add(refDoc);
		}
		return list;
	}

	public static List<Object> getRelatedObjByPart(WTPart part) throws WTException, RemoteException {
		List<Object> list = new ArrayList<Object>();
		boolean hasPP = false;
		List<EPMDocument> epmList = getRelatedEPM(part);
		// list.addAll(epmList);
		for (EPMDocument epmDocument : epmList) {
			list.add(epmDocument);
			EPMDocument epm2d = getLastestIter2DesignDocs(epmDocument);
			if (epm2d != null) {
				list.add(epm2d);
			}
		}

		List<WTDocument> desDocList = getDescribedDoc(part);
		// list.addAll(desDocList);
		for (WTDocument wtDocument : desDocList) {
			String softType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtDocument);
			if (!softType.contains("PBOM") && !softType.contains("PROCESS_DOC") && !softType.contains("AUTHORISE_FORM")) {
				list.add(wtDocument);
			}
			//TODO-SOP:增加SOP类型，需部署
			if (softType.contains("PROCESSPLAN") || softType.contains("reportTechnics") || softType.contains(SopConstants.SOP_TYPE_SOPDOC)) {
				QuerySpec qSpec = new QuerySpec(MPMProcessPlan.class);
				int[] index = { 0 };
				SearchCondition sCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, wtDocument.getNumber());
				qSpec.appendWhere(sCondition, index);
				QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
				LatestConfigSpec lcs = new LatestConfigSpec();
				qResult = lcs.process(qResult);
				if (qResult.hasMoreElements()) {
					MPMProcessPlan plan = (MPMProcessPlan) qResult.nextElement();
					hasPP = true;
					list.add(plan);
				}
			}
		}

		List<WTDocument> refDocList = getReferencedDoc(part);
		for (WTDocument wtDocument : refDocList) {
			String softType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtDocument);
			if (softType.contains("PROCESSPLAN") || softType.contains("reportTechnics")) {
				QuerySpec qSpec = new QuerySpec(MPMProcessPlan.class);
				int[] index = { 0 };
				SearchCondition sCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, wtDocument.getNumber());
				qSpec.appendWhere(sCondition, index);
				QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
				LatestConfigSpec lcs = new LatestConfigSpec();
				qResult = lcs.process(qResult);
				if (qResult.hasMoreElements()) {
					MPMProcessPlan plan = (MPMProcessPlan) qResult.nextElement();
					hasPP = true;
					list.add(plan);
				}
			}
		}
		Set set = new HashSet(list);

		if (!hasPP) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(part);
			while (qr.hasMoreElements()) {
				List<MPMProcessPlan> pps = getRelatedProcessPlanByPart((WTPart) qr.nextElement());
				if (!pps.isEmpty()) {
					set.addAll(pps);
					break;
				}
			}
		}

		// list.addAll(refDocList);
		return new ArrayList(set);
	}

	public static List<MPMProcessPlan> getRelatedProcessPlanByPart(WTPart part) throws WTException, RemoteException {
		List<MPMProcessPlan> resultList = new ArrayList<MPMProcessPlan>();
		List<WTDocument> desDocList = getDescribedDoc(part);
		for (WTDocument wtDocument : desDocList) {
			String softType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtDocument);
			if (softType.contains("PROCESSPLAN")) {
				QuerySpec qSpec = new QuerySpec(MPMProcessPlan.class);
				int[] index = { 0 };
				SearchCondition sCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, wtDocument.getNumber());
				qSpec.appendWhere(sCondition, index);
				QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
				LatestConfigSpec lcs = new LatestConfigSpec();
				qResult = lcs.process(qResult);
				if (qResult.hasMoreElements()) {
					MPMProcessPlan plan = (MPMProcessPlan) qResult.nextElement();
					resultList.add(plan);
				}
			}
		}

		List<WTDocument> refDocList = getReferencedDoc(part);
		for (WTDocument wtDocument : refDocList) {
			String softType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtDocument);
			if (softType.contains("PROCESSPLAN")) {
				QuerySpec qSpec = new QuerySpec(MPMProcessPlan.class);
				int[] index = { 0 };
				SearchCondition sCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, wtDocument.getNumber());
				qSpec.appendWhere(sCondition, index);
				QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
				LatestConfigSpec lcs = new LatestConfigSpec();
				qResult = lcs.process(qResult);
				if (qResult.hasMoreElements()) {
					MPMProcessPlan plan = (MPMProcessPlan) qResult.nextElement();
					resultList.add(plan);
				}
			}
		}
		return resultList;
	}

	public static WTPart getPartByNumber(String number, String viewName) throws WTException {
		View view = null;
		if (viewName != null && !"".equals(viewName)) {
			view = getViewByName(viewName);
		}
		if (view == null) {
			LOG.debug("-------View:" + viewName + " is not exist!");
			return null;
		}
		long viewId = PersistenceHelper.getObjectIdentifier(view).getId();
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number);
		qSpec.appendWhere(sCondition, index);
		qSpec.appendAnd();
		sCondition = new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qResult = lcs.process(qResult);
		if (qResult.hasMoreElements()) {
			return (WTPart) qResult.nextElement();
		}
		return null;
	}

	public static List<WTPart> getPartsLikeNumber(String number, String viewName) throws WTException {
		List<WTPart> resultList = new ArrayList<WTPart>();
		View view = null;
		if (viewName != null && !"".equals(viewName)) {
			view = getViewByName(viewName);
		}
		if (view == null) {
			LOG.debug("-------View:" + viewName + " is not exist!");
			return null;
		}
		long viewId = PersistenceHelper.getObjectIdentifier(view).getId();
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		int[] index = { 0 };
		SearchCondition sCondition = null;
		// if(number.contains("*")){
		// number = number.replaceAll("/*", "%");
		sCondition = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "%" + number + "%");
		// }else{
		// sCondition = new SearchCondition(WTPart.class,
		// WTPart.NUMBER,SearchCondition.EQUAL, number);
		// }

		qSpec.appendWhere(sCondition, index);
		qSpec.appendAnd();
		sCondition = new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qResult = lcs.process(qResult);
		while (qResult.hasMoreElements()) {
			resultList.add((WTPart) qResult.nextElement());
		}
		return resultList;
	}

	public static View getViewByName(String name) throws WTException {
		QuerySpec qSpec = new QuerySpec(View.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(View.class, View.NAME, SearchCondition.EQUAL, name);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		if (qResult.hasMoreElements()) {
			return (View) qResult.nextElement();
		}
		return null;
	}

	public static WTPart getPartByName(String name, String viewName) throws WTException {
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.EQUAL, name);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qResult = lcs.process(qResult);
		if (qResult.hasMoreElements()) {
			WTPart part = (WTPart) qResult.nextElement();
			String tempViewName = part.getViewName();
			if (tempViewName.equals(viewName)) {
				return part;
			}
		}
		return null;
	}

	public static List<WTPart> getPartsLikeName(String name, String viewName) throws WTException {
		List<WTPart> resultList = new ArrayList<WTPart>();
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		int[] index = { 0 };
		SearchCondition sCondition = null;
		// if(name.contains("*")){
		// name = name.replaceAll("*", "%");
		sCondition = new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, "%" + name + "%");
		// }else{
		sCondition = new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.EQUAL, name);
		// }
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qResult = lcs.process(qResult);
		while (qResult.hasMoreElements()) {
			WTPart part = (WTPart) qResult.nextElement();
			String tempViewName = part.getViewName();
			if (tempViewName.equals(viewName)) {
				resultList.add(part);
			}
		}
		return resultList;
	}

	/**
	 * 通过零部件查询ProcessTaskLink
	 *
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isExistProcessTask(WTPart part, String taskType) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskLink.class);
		int[] index = { 0 };
		long longId = PersistenceHelper.getObjectIdentifier(part).getId();
		SearchCondition sCondition = new SearchCondition(ProcessTaskLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		while (qResult.hasMoreElements()) {
			ProcessTaskLink link = (ProcessTaskLink) qResult.nextElement();
			ProcessTask processTask = (ProcessTask) link.getRoleBObject();
			if (!ProcessConstants.TASK_STATE_YIZUOFEI.equals(processTask.getTaskState())
					&& !ProcessConstants.TASK_STATE_YIWANGONG.equals(processTask.getTaskState())) {
				if (processTask.getTaskType().equals(taskType)) {
					return true;
				}

			}
		}
		return false;
	}

	public static boolean isExistProcessTask(WTPart part) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskLink.class);
		int[] index = { 0 };
		long longId = PersistenceHelper.getObjectIdentifier(part).getId();
		SearchCondition sCondition = new SearchCondition(ProcessTaskLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		while (qResult.hasMoreElements()) {
			ProcessTaskLink link = (ProcessTaskLink) qResult.nextElement();
			ProcessTask processTask = (ProcessTask) link.getRoleBObject();
			if (!ProcessConstants.TASK_STATE_YIZUOFEI.equals(processTask.getTaskState())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isRoleMember(WTUser user, String roleKey, WTContainer container) throws WTException {
		boolean flag = false;
		ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
		Role role = Role.toRole(roleKey);
		if (role != null) {
			ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
			for (WTPrincipalReference reference : arrayList) {
				Object object2 = reference.getPrincipal();
				if (object2 instanceof WTUser) {
					WTUser userTemp = (WTUser) object2;
					if (user.equals(userTemp)) {
						flag = true;
						break;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 通过零部件查询ProcessTaskLink
	 *
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static ProcessTask getProcessTaskByPart(WTPart part, String zfFlag) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskLink.class);
		int[] index = { 0 };
		long longId = PersistenceHelper.getObjectIdentifier(part).getId();
		SearchCondition sCondition = new SearchCondition(ProcessTaskLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		IBAUtility ibauti = null;
		while (qResult.hasMoreElements()) {
			ProcessTaskLink link = (ProcessTaskLink) qResult.nextElement();
			ProcessTask processTask = (ProcessTask) link.getRoleBObject();
			ibauti = new IBAUtility(processTask);
			String ibaValue = ibauti.getIBAValue("PPTASKTYPE");
			if ((ibaValue == null || "".equals(ibaValue)) || zfFlag.equals(ibaValue)) {// 旧数据没有主辅类别，所以此值为空
				return processTask;
			}
		}
		return null;
	}

	/**
	 * 查询所有辅制工艺任务
	 *
	 * @param part
	 * @param zfFlag
	 * @return
	 * @throws WTException
	 */
	public static List<ProcessTask> getAllProcessTaskByPart(WTPart part, String zfFlag) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskLink.class);
		int[] index = { 0 };
		long longId = PersistenceHelper.getObjectIdentifier(part).getId();
		SearchCondition sCondition = new SearchCondition(ProcessTaskLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		List<ProcessTask> list = new ArrayList<ProcessTask>();
		IBAUtility ibauti = null;
		while (qResult.hasMoreElements()) {
			ProcessTaskLink link = (ProcessTaskLink) qResult.nextElement();
			ProcessTask processTask = (ProcessTask) link.getRoleBObject();
			ibauti = new IBAUtility(processTask);
			String ibaValue = ibauti.getIBAValue("PPTASKTYPE");
			if (zfFlag.equals(ibaValue)) {
				list.add(processTask);
			}
		}
		return list;
	}

	public static WTPart getWtPartByProcessTask(long longId) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskLink.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(ProcessTaskLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		if (qResult.hasMoreElements()) {
			ProcessTaskLink link = (ProcessTaskLink) qResult.nextElement();
			return (WTPart) link.getRoleAObject();
		}
		return null;
	}

	public static ProcessTask getProcessTask(long longId) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTask.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(ProcessTask.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		if (qResult.hasMoreElements()) {
			return (ProcessTask) qResult.nextElement();
		}
		return null;
	}

	public static ProcessTaskItem getProcessTaskItem(long longId) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskItem.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(ProcessTaskItem.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		if (qResult.hasMoreElements()) {
			return (ProcessTaskItem) qResult.nextElement();
		}
		return null;
	}

	/**
	 * 通过指定工艺任务获取其下的所有任务活动条目
	 *
	 * @param longId
	 *            指定工艺任务的ida2a2
	 * @return QueryResult 指定工艺任务下的所有任务活动条目
	 * @throws WTException
	 */
	public static QueryResult getAllProcessTaskItemByPTask(long longId) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskItem.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(ProcessTaskItem.class, "ProcessTaskId", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		return qResult;
	}

	public static QueryResult queryProcessTaskForSearch(WTUser currentUser, String select, String[] par) throws WTException, ParseException {
		QuerySpec qSpec = new QuerySpec(ProcessTask.class);
		int[] index = { 0 };
		long longId = PersistenceHelper.getObjectIdentifier(currentUser).getId();
		SearchCondition sCondition = new SearchCondition(ProcessTask.class, "creator.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		if (select.equals(ProcessConstants.TASKITEM_STATE_YIWANCHENG) || select.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				|| select.equals(ProcessConstants.TASK_STATE_YIZUOFEI) || select.equals(ProcessConstants.TASKITEM_STATE_CANCLE)) {
			qSpec.appendAnd();
			SearchCondition sCondition2 = new SearchCondition(ProcessTask.class, "taskState", SearchCondition.EQUAL, select);
			qSpec.appendWhere(sCondition2, index);
		} else if (select.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI) || select.equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)
				|| select.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
			qSpec.appendAnd();
			SearchCondition sCondition3 = new SearchCondition(ProcessTask.class, "taskType", SearchCondition.EQUAL, select);
			qSpec.appendWhere(sCondition3, index);
		}
		if (par != null) {
			SearchCondition sCondition4 = null;
			// 编号
			String number = par[0];
			if (!"".equals(number)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				sCondition4 = new SearchCondition(ProcessTask.class, "number", SearchCondition.EQUAL, number);
				qSpec.appendWhere(sCondition4, index);
			}
			// 名称
			String name = par[1];
			if (!"".equals(name)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				sCondition4 = new SearchCondition(ProcessTask.class, "name", SearchCondition.EQUAL, name);
				qSpec.appendWhere(sCondition4, index);
			}
			// 车间
			String chejian = par[2];
			if (!"".equals(chejian)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				qSpec.appendOpenParen();
				sCondition4 = new SearchCondition(ProcessTask.class, "zhuzhichejian", SearchCondition.LIKE, "%" + chejian + "%");
				qSpec.appendWhere(sCondition4, index);
				qSpec.appendOr();
				sCondition4 = new SearchCondition(ProcessTask.class, "fuzhichejian", SearchCondition.LIKE, "%" + chejian + "%");
				qSpec.appendWhere(sCondition4, index);
				qSpec.appendCloseParen();
			}
			// 任务类型
			String tasktype = par[3];
			if (!"".equals(tasktype)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				sCondition4 = new SearchCondition(ProcessTask.class, "taskType", SearchCondition.EQUAL, tasktype);
				qSpec.appendWhere(sCondition4, index);
			}
			// 任务状态
			String taskState = par[4];
			if (!"".equals(taskState)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				sCondition4 = new SearchCondition(ProcessTask.class, "taskState", SearchCondition.EQUAL, taskState);
				qSpec.appendWhere(sCondition4, index);
			}
			// 最后更新时间起始条件
			TimeZone tz = WTContext.getContext().getTimeZone();
			Calendar ca = Calendar.getInstance(tz);
			String startDate = par[5];
			if (!"".equals(startDate)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				Date dateFrom = WTStandardDateFormat.parse(startDate, "yyyy/M/d");
				// 加入时区信息
				ca.setTime(dateFrom);
				dateFrom = ca.getTime();
				sCondition4 = new SearchCondition(ProcessTask.class, CREATE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime() + 8
						* 60 * 60 * 1000));
				qSpec.appendWhere(sCondition4, index);
			}
			String endDate = par[6];
			if (!"".equals(endDate)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				Date dateFrom = WTStandardDateFormat.parse(endDate, "yyyy/M/d");
				// 加入时区信息
				ca.setTime(dateFrom);
				dateFrom = ca.getTime();
				sCondition4 = new SearchCondition(ProcessTask.class, CREATE_DATE, SearchCondition.LESS_THAN, new Timestamp(dateFrom.getTime() + 8 * 60 * 60
						* 1000));
				qSpec.appendWhere(sCondition4, index);
			}
		}
		// LOG.debug("----------sql:"+qSpec.toString());
		return PersistenceHelper.manager.find((StatementSpec) qSpec);
	}

	public static QueryResult queryProcessTaskItemForSearch(WTUser currentUser, String select, String[] par, String role) throws WTException, ParseException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskItem.class);
		int[] index = { 0 };
		// long longId =
		// PersistenceHelper.getObjectIdentifier(currentUser).getId();
		// SearchCondition sCondition = new
		// SearchCondition(ProcessTaskItem.class, "creator.key.id",
		// SearchCondition.EQUAL,longId);
		// qSpec.appendWhere(sCondition, index);
		if (select.equals(ProcessConstants.TASKITEM_STATE_YIWANCHENG) || select.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				|| select.equals(ProcessConstants.TASK_STATE_YIZUOFEI) || select.equals(ProcessConstants.TASKITEM_STATE_CANCLE)) {
			// qSpec.appendAnd();
			SearchCondition sCondition2 = new SearchCondition(ProcessTaskItem.class, "taskItemState", SearchCondition.EQUAL, select);
			qSpec.appendWhere(sCondition2, index);
		} else if (select.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI) || select.equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)
				|| select.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
			// qSpec.appendAnd();
			SearchCondition sCondition3 = new SearchCondition(ProcessTaskItem.class, "taskType", SearchCondition.EQUAL, select);
			qSpec.appendWhere(sCondition3, index);
		}
		if (par != null) {
			SearchCondition sCondition4 = null;
			// 编号
			String number = par[0];
			if (!"".equals(number)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				sCondition4 = new SearchCondition(ProcessTaskItem.class, "number", SearchCondition.EQUAL, number);
				qSpec.appendWhere(sCondition4, index);
			}
			// 名称
			String name = par[1];
			if (!"".equals(name)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				sCondition4 = new SearchCondition(ProcessTaskItem.class, "name", SearchCondition.EQUAL, name);
				qSpec.appendWhere(sCondition4, index);
			}
			if (role.equals("gongyizuzhang")) {
				// 车间
				String chejian = par[2];
				if (!"".equals(chejian)) {
					if (qSpec.getConditionCount() != 0) {
						qSpec.appendAnd();
					}
					sCondition4 = new SearchCondition(ProcessTaskItem.class, "chejian", SearchCondition.EQUAL, chejian);
					qSpec.appendWhere(sCondition4, index);
				}
			} else {
				// 工艺员
				String gongyiyuan = par[2];
				if (!"".equals(gongyiyuan)) {
					if (qSpec.getConditionCount() != 0) {
						qSpec.appendAnd();
					}
					sCondition4 = new SearchCondition(ProcessTaskItem.class, "owner", SearchCondition.EQUAL, gongyiyuan);
					qSpec.appendWhere(sCondition4, index);
				}
			}
			// 任务类型
			String tasktype = par[3];
			if (!"".equals(tasktype)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				sCondition4 = new SearchCondition(ProcessTaskItem.class, "taskType", SearchCondition.EQUAL, tasktype);
				qSpec.appendWhere(sCondition4, index);
			}
			// 任务状态
			String taskState = par[4];
			if (!"".equals(taskState)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				sCondition4 = new SearchCondition(ProcessTaskItem.class, "taskItemState", SearchCondition.EQUAL, taskState);
				qSpec.appendWhere(sCondition4, index);
			}
			// 最后更新时间起始条件
			TimeZone tz = WTContext.getContext().getTimeZone();
			Calendar ca = Calendar.getInstance(tz);
			String startDate = par[5];
			if (!"".equals(startDate)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				Date dateFrom = WTStandardDateFormat.parse(startDate, "yyyy/M/d");
				// 加入时区信息
				ca.setTime(dateFrom);
				dateFrom = ca.getTime();
				sCondition4 = new SearchCondition(ProcessTaskItem.class, CREATE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime()
						+ 8 * 60 * 60 * 1000));
				qSpec.appendWhere(sCondition4, index);
			}
			String endDate = par[6];
			if (!"".equals(endDate)) {
				if (qSpec.getConditionCount() != 0) {
					qSpec.appendAnd();
				}
				Date dateFrom = WTStandardDateFormat.parse(endDate, "yyyy/M/d");
				// 加入时区信息
				ca.setTime(dateFrom);
				dateFrom = ca.getTime();
				sCondition4 = new SearchCondition(ProcessTaskItem.class, CREATE_DATE, SearchCondition.LESS_THAN, new Timestamp(dateFrom.getTime() + 8 * 60 * 60
						* 1000));
				qSpec.appendWhere(sCondition4, index);
			}
		}
		// LOG.debug("----------sql:"+qSpec.toString());
		return PersistenceHelper.manager.find((StatementSpec) qSpec);
	}

	public static QueryResult queryProcessTaskByUser(WTUser currentUser, String select) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTask.class);
		int[] index = { 0 };
		long longId = PersistenceHelper.getObjectIdentifier(currentUser).getId();
		SearchCondition sCondition = new SearchCondition(ProcessTask.class, "creator.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		if (select.equals(ProcessConstants.TASK_STATE_YIWANGONG) || select.equals(ProcessConstants.TASK_STATE_JINGXINZHONG)
				|| select.equals(ProcessConstants.TASK_STATE_YIZUOFEI)) {
			qSpec.appendAnd();
			SearchCondition sCondition2 = new SearchCondition(ProcessTask.class, "taskState", SearchCondition.EQUAL, select);
			qSpec.appendWhere(sCondition2, index);
		} else if (select.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI) || select.equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)
				|| select.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
			qSpec.appendAnd();
			SearchCondition sCondition3 = new SearchCondition(ProcessTask.class, "taskType", SearchCondition.EQUAL, select);
			qSpec.appendWhere(sCondition3, index);
		}
		return PersistenceHelper.manager.find((StatementSpec) qSpec);
	}

	public static QueryResult queryProcessTask(WTUser currentUser, String select) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskItem.class);
		int[] index = { 0 };
		SearchCondition sCondition1 = new SearchCondition(ProcessTaskItem.class, "owner", SearchCondition.EQUAL, currentUser.getName());
		qSpec.appendWhere(sCondition1, index);
		if (select.equals(ProcessConstants.TASKITEM_STATE_YIWANCHENG) || select.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				|| select.equals(ProcessConstants.TASK_STATE_YIZUOFEI) || select.equals(ProcessConstants.TASKITEM_STATE_CANCLE)) {
			SearchCondition sCondition2 = new SearchCondition(ProcessTaskItem.class, "taskItemState", SearchCondition.EQUAL, select);
			SearchCondition sCondition4 = new SearchCondition(ProcessTaskItem.class, "taskItemState", SearchCondition.EQUAL, "指派中");
			qSpec.appendAnd();

			qSpec.appendOpenParen();
			qSpec.appendWhere(sCondition2, index);
			qSpec.appendOr();
			qSpec.appendWhere(sCondition4, index);
			qSpec.appendCloseParen();

		} else if (select.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI) || select.equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)
				|| select.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
			SearchCondition sCondition3 = new SearchCondition(ProcessTaskItem.class, "taskType", SearchCondition.EQUAL, select);
			qSpec.appendAnd();
			qSpec.appendWhere(sCondition3, index);
		}
		/** 超过半年的不显示 add by liangbo 20170325 */
		// Calendar c = Calendar.getInstance();
		// c.setTime(new Date());
		// Date fend = c.getTime();
		// c.add(Calendar.MONTH, -6);
		// Date fbegin = c.getTime();
		// qSpec.appendAnd();
		// Timestamp tstartDate=new Timestamp(fbegin.getTime()); //当天凌晨
		// Timestamp tendDate=new Timestamp(fend.getTime()); //当前时间
		// AttributeRange arange=new AttributeRange(tstartDate,tendDate);
		// qSpec.appendWhere(new
		// SearchCondition(ProcessTaskItem.class,ProcessTaskItem.CREATE_TIMESTAMP,
		// true,arange));
		/***/

		qSpec.appendAnd();
		qSpec.appendOpenParen();
		qSpec.appendWhere(new SearchCondition(ProcessTaskItem.class, "taskItemState", SearchCondition.EQUAL, "正在进行"), index);
		qSpec.appendOr();
		qSpec.appendOpenParen();
		qSpec.appendWhere(new SearchCondition(ProcessTaskItem.class, "taskItemState", SearchCondition.NOT_EQUAL, "正在进行"), index);
		qSpec.appendAnd();
		String date = "2020/1/1";
		String value = SystemConfigurationUtil.getValue("PROCESSTASKITEMDATE");
		if(value != null && !"".equals(value)){
			date = value;
		}
		Date dateFrom = null;
		try {
			dateFrom = WTStandardDateFormat.parse(date, "yyyy/MM/dd");
		} catch(ParseException e) {
			e.printStackTrace();
		}
		SearchCondition sc11 = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.MODIFY_TIMESTAMP, SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime()));
		qSpec.appendWhere(sc11, index);
		qSpec.appendCloseParen();
		qSpec.appendCloseParen();

		ClassAttribute clsAttr = new ClassAttribute(ProcessTaskItem.class, ProcessTaskItem.CREATE_TIMESTAMP);
		OrderBy order = new OrderBy((OrderByExpression) clsAttr, true);
		qSpec.appendOrderBy(order);
		return PersistenceHelper.manager.find((StatementSpec) qSpec);
	}

	public static QueryResult queryPart(String productName, String number, String name, String viewName) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		int index[] = { 0 };
		View view = null;
		if (viewName != null && !"".equals(viewName)) {
			view = getViewByName(viewName);
		}
		if (view == null) {
			LOG.debug("-------View:" + viewName + " is not exist!");
			return null;
		}
		long viewId = PersistenceHelper.getObjectIdentifier(view).getId();
		SearchCondition sCondition = new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewId);
		qSpec.appendWhere(sCondition, index);
		if (number != null && !"".equals(number)) {
			if (number.equals("*")) {
				number = number.replaceAll("*", "%");
			} else {
				number = number + "%";
			}
			qSpec.appendAnd();
			sCondition = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, number);
			qSpec.appendWhere(sCondition, index);
		}
		if (name != null && !"".equals(name)) {
			if (name.equals("*")) {
				name = name.replaceAll("*", "%");
			} else {
				name = name + "%";
			}
			qSpec.appendAnd();
			sCondition = new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, name);
			qSpec.appendWhere(sCondition, index);
		}
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qResult = lcs.process(qResult);
		SessionServerHelper.manager.setAccessEnforced(flag);
		return qResult;
	}

	public static QueryResult queryPart(String productName, String number, String name, String viewName, boolean isAllLatestVersion, String version)
			throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		int index[] = { 0 };
		View view = null;
		if (viewName != null && !"".equals(viewName)) {
			view = getViewByName(viewName);
		}
		if (view == null) {
			LOG.debug("-------View:" + viewName + " is not exist!");
			return null;
		}
		long viewId = PersistenceHelper.getObjectIdentifier(view).getId();
		SearchCondition sCondition = new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewId);
		qSpec.appendWhere(sCondition, index);
		if (number != null && !"".equals(number)) {
			if (number.equals("*")) {
				number = number.replaceAll("*", "%");
			} else {
				number = number + "%";
			}
			qSpec.appendAnd();
			sCondition = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, number);
			qSpec.appendWhere(sCondition, index);
		}
		if (name != null && !"".equals(name)) {
			if (name.equals("*")) {
				name = name.replaceAll("*", "%");
			} else {
				name = name + "%";
			}
			qSpec.appendAnd();
			sCondition = new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, name);
			qSpec.appendWhere(sCondition, index);
		}

		if (version != null && !"".equals(version)) {
			qSpec.appendAnd();
			sCondition = new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", SearchCondition.EQUAL, version);
			qSpec.appendWhere(sCondition, index);
		}
		if (isAllLatestVersion) {
			qSpec.appendAnd();
			sCondition = new SearchCondition(WTPart.class, "iterationInfo.latest", "TRUE");
			qSpec.appendWhere(sCondition, index);
		}
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		if (!isAllLatestVersion) {
			LatestConfigSpec lcs = new LatestConfigSpec();
			qResult = lcs.process(qResult);
		}
		SessionServerHelper.manager.setAccessEnforced(flag);
		return qResult;
	}

	public static List<WTUser> getRoleUsersByWTContainer(String roleKey, WTContainer wtContainer) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		List<WTUser> list = new ArrayList<WTUser>();
		try {
			Role role = Role.toRole(roleKey);
			if(role == null) {
				return list;
			}
			ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
			ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
			for(WTPrincipalReference reference : arrayList) {
				Object object2 = reference.getPrincipal();
				if(object2 instanceof WTUser) {
					WTUser user = (WTUser) object2;
					list.add(user);
				}
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return list;
	}

	/**
	 * 获取工艺组长组及其所有人员
	 *
	 * @return Map<String, List<WTUser>> String为车间序号，List<WTUser>为该车间的工艺组长
	 * @throws WTException
	 */
	public static Map<String, List<WTUser>> getGroupAndUsersInOrgContainer() throws WTException {
		if(VariableCache.groupAndUsersInOrgContainerMap !=null ){
			return VariableCache.groupAndUsersInOrgContainerMap ;
		}
		Map<String, List<WTUser>> map = new HashMap<String, List<WTUser>>();
		OrgContainer orgContainer = getOrgContainer();
		List list = getNodes(orgContainer);
		if (list != null) {
			WTGroup group = null;
			for (int i = 0; i < list.size(); i++) {
				Object object = list.get(i);
				if (object instanceof WTGroup) {
					group = (WTGroup) object;
					String groupName = group.getDescription() == null ? group.getName() : group.getDescription();
					if (groupName != null && groupName.indexOf(ProcessConstants.GROUP_GONGYIZUZHANG_NAME) > -1) {
						String xuhao = groupName.substring(groupName.lastIndexOf("_") + 1, groupName.length());
						List<WTUser> allUsers = new ArrayList<WTUser>();
						Enumeration enumeration = group.members();
						while (enumeration.hasMoreElements()) {
							Object userObject = enumeration.nextElement();
							if (userObject instanceof WTUser) {
								allUsers.add((WTUser) userObject);
							}
						}
						map.put(xuhao, allUsers);
					}
				}
			}
		}
		VariableCache.groupAndUsersInOrgContainerMap = map;
		return map;
	}

	/**
	 * 获取车间组及其所有的工艺员
	 *
	 * @return Map<String, List<WTUser>> String为车间序号，List<WTUser>为车间工艺员
	 * @throws WTException
	 */
	public static Map<String, List<WTUser>> getCheJianGroupAndUsers() throws WTException {
		Map<String, List<WTUser>> map = new HashMap<String, List<WTUser>>();
		OrgContainer orgContainer = getOrgContainer();
		List list = getNodes(orgContainer);
		List<String> allList = getAllGongYiZuZhangGroup();
		Map<String, String> chejianMap = getAllCheJianGroupXuHao();
		if (list != null) {
			WTGroup group = null;
			for (int i = 0; i < list.size(); i++) {
				Object object = list.get(i);
				if (object instanceof WTGroup) {
					group = (WTGroup) object;
					String groupName = group.getDescription() == null ? group.getName() : group.getDescription();
					if (groupName != null && !groupName.contains(ProcessConstants.GROUP_GONGYIZUZHANG_NAME)) {
						for (String zuzhanggroup : allList) {
							if (zuzhanggroup.contains(groupName)) {
								String chejianXuHao = chejianMap.get(groupName);
								List<WTUser> allUsers = new ArrayList<WTUser>();
								Enumeration enumeration = group.members();
								while (enumeration.hasMoreElements()) {
									Object userObject = enumeration.nextElement();
									if (userObject instanceof WTUser) {
										allUsers.add((WTUser) userObject);
									}
								}
								map.put(chejianXuHao, allUsers);
							}
						}
					}
				}
			}
		}
		// LOG.debug("--------map:" + map);
		return map;
	}

	/**
	 * 通过指定的容器和车间角色获取所有工艺员
	 *
	 * @param wtContainer
	 *            产品容器
	 * @param chejian
	 *            指定车间
	 * @return List<WTUser> 用户集合
	 * @throws WTException
	 */
	public static List<WTUser> getUsersByCheJian(WTContainer wtContainer, String chejianRole) throws WTException {
		List<WTUser> list = new ArrayList<WTUser>();
		ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
		Role role = Role.toRole(chejianRole);
		if (role == null) {
			LOG.debug("------Role " + chejianRole + " is not exist");
			return list;
		}
		ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
		for (WTPrincipalReference reference : arrayList) {
			Object object2 = reference.getPrincipal();
			if (object2 instanceof WTUser) {
				WTUser user = (WTUser) object2;
				list.add(user);
			} else if (object2 instanceof WTGroup) {
				WTGroup group = (WTGroup) object2;
				getUserFromWTGroup(group, list);
			}
		}
		return list;
	}

	/**
	 * 循环组并且获取组里面的用户
	 *
	 * @param group
	 * @param list
	 * @throws WTException
	 */
	public static void getUserFromWTGroup(WTGroup group, List list) throws WTException {
		if (group == null || list == null) {
			return;
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
	}

	/**
	 * 通过指定的容器和车间获取所有工艺员
	 *
	 * @param wtContainer
	 *            产品容器
	 * @param chejianNumber
	 *            指定车间号，比如：1
	 * @return List<WTUser> 用户集合
	 * @throws WTException
	 */
	public static List<WTUser> getUsersByChanJianNumber(WTContainer wtContainer, String chejianNumber) throws WTException {
		String chejianRole = "";
		List<WTUser> list = new ArrayList<WTUser>();

		chejianRole = Constants.allChejianToWorkFlowGYYRoleMap.get(chejianNumber);
		/*
		 * if ("1".equals(chejianNumber)) { chejianRole =
		 * ProcessConstants.ROLE_CHEJIAN_1; } else if
		 * ("2".equals(chejianNumber)) { chejianRole =
		 * ProcessConstants.ROLE_CHEJIAN_2; } else if
		 * ("3".equals(chejianNumber)) { chejianRole =
		 * ProcessConstants.ROLE_CHEJIAN_3; } else if
		 * ("4".equals(chejianNumber)) { chejianRole =
		 * ProcessConstants.ROLE_CHEJIAN_4; } else if
		 * ("5".equals(chejianNumber)) { chejianRole =
		 * ProcessConstants.ROLE_CHEJIAN_5; } else if
		 * ("6".equals(chejianNumber)) { chejianRole =
		 * ProcessConstants.ROLE_CHEJIAN_6; } else if
		 * ("7".equals(chejianNumber)) { chejianRole =
		 * ProcessConstants.ROLE_CHEJIAN_7; } else if
		 * ("8".equals(chejianNumber)) { chejianRole =
		 * ProcessConstants.ROLE_CHEJIAN_8; } else if
		 * (Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN.equals(chejianNumber)) {
		 * chejianRole = ProcessConstants.ROLE_XIANGMUBUGONGYIYUAN; }
		 */
		if (chejianRole == null || "".equals(chejianRole)) {
			return list;
		}
		return getUsersByCheJian(wtContainer, chejianRole);
	}

	/**
	 * 获取所有工艺组长组<br>
	 * 格式为：部门_车间_工艺组长_序号
	 *
	 * @return List<String> 工艺组长组集合
	 * @throws WTException
	 */
	public static List<String> getAllGongYiZuZhangGroup() throws WTException {
		List<String> allGroup = new ArrayList<String>();
		OrgContainer orgContainer = getOrgContainer();
		List list = getNodes(orgContainer);
		if (list != null) {
			WTGroup group = null;
			for (int i = 0; i < list.size(); i++) {
				Object object = list.get(i);
				if (object instanceof WTGroup) {
					group = (WTGroup) object;
					String groupName = group.getDescription() == null ? group.getName() : group.getDescription();
					if (groupName != null && !groupName.contains(ProcessConstants.GROUP_GONGYIZUZHANG_NAME)) {
						continue;
					}
					allGroup.add(groupName);
				}
			}
		}
		return allGroup;
	}

	/**
	 * 获取所有车间组名及其序号<br>
	 * 格式为：车间组名:序号
	 *
	 * @return Map<String, String> 车间组名及其序号集合
	 * @throws WTException
	 */
	public static Map<String, String> getAllCheJianGroupXuHao() throws WTException {
		Map<String, String> map = new HashMap<String, String>();
		OrgContainer orgContainer = getOrgContainer();
		List list = getNodes(orgContainer);
		if (list != null) {
			WTGroup group = null;
			for (int i = 0; i < list.size(); i++) {
				Object object = list.get(i);
				if (object instanceof WTGroup) {
					group = (WTGroup) object;
					String groupName = group.getDescription() == null ? group.getName() : group.getDescription();
					if (!groupName.contains(ProcessConstants.GROUP_GONGYIZUZHANG_NAME)) {
						continue;
					}
					String chejian = groupName.substring(0, groupName.indexOf(ProcessConstants.GROUP_GONGYIZUZHANG_NAME_SPLIT));
					String xuhao = groupName.substring(groupName.lastIndexOf("_") + 1, groupName.length());
					map.put(chejian, xuhao);
				}
			}
		}
		return map;
	}

	public static ArrayList<String> getAllCheJian() throws RemoteException {
		ArrayList<String> list = new ArrayList<String>();
		try {
			CascCacheManager manager = new CascCacheManager();
			// Map<String, List<WTUser>> map = manager.getGroupAndUsers();
			Map<String, List<WTUser>> map = getGroupAndUsersInOrgContainer();
			Set<String> set = new TreeSet<String>();
			set.addAll(map.keySet());
			for (String chejian : set) {
				list.add(chejian);
			}
			list.add(Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN);
		} catch (WTException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 获取指定的用户所在的组名
	 *
	 * @param user
	 *            指定的用户
	 * @return 组名称
	 * @throws WTException
	 */
	public static String getUserGroupNameRMI(WTUser user) throws WTException {
		/*
		 * Enumeration enumeration = null; enumeration =
		 * user.parentGroups(true); if(null != enumeration){ do {
		 * if(!enumeration.hasMoreElements()) break; WTPrincipalReference
		 * wtprincipalreference =
		 * (WTPrincipalReference)enumeration.nextElement(); WTGroup group =
		 * (WTGroup)wtprincipalreference.getPrincipal(); if(!group.isInternal()
		 * && !(group instanceof WTOrganization)){ String groupName =
		 * group.getDescription()==null? group.getName():group.getDescription();
		 * if (groupName.indexOf(ProcessConstants.GROUP_GONGYIZUZHANG_NAME) ==
		 * -1) { return groupName; } } } while(true); }
		 */
		OrgContainer orgContainer = getOrgContainer();
		List list = getNodes(orgContainer);
		if (list != null) {
			WTGroup group = null;
			for (int i = 0; i < list.size(); i++) {
				Object object = list.get(i);
				if (object instanceof WTGroup) {
					group = (WTGroup) object;
					String groupName = group.getDescription() == null ? group.getName() : group.getDescription();
					if (groupName.indexOf(ProcessConstants.GROUP_GONGYIZUZHANG_NAME) == -1 && (groupName.contains("分厂") || groupName.contains("车间"))) {
						if (group.isMember(user)) {
							return groupName;
						}
						/*
						 * Enumeration enumeration = group.members(); while
						 * (enumeration.hasMoreElements()) { Object userObject =
						 * enumeration.nextElement(); if (userObject instanceof
						 * WTUser) { WTUser u = (WTUser)userObject;
						 * if(u.getName().equals(user.getName())) { return
						 * groupName; } } }
						 */
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取当前用户所在的组织
	 *
	 * @return OrgContainer 组织
	 * @throws WTException
	 */
	public static OrgContainer getOrgContainer() throws WTException {
		WTPrincipal currentUser = SessionHelper.getPrincipal();
		WTOrganization wtOrganization = OrganizationServicesHelper.manager.getOrganization(currentUser);
		OrgContainer orgContainer = WTContainerHelper.service.getOrgContainer(wtOrganization);
		return orgContainer;
	}

	/**
	 * 获取指定组织下的所有组
	 *
	 * @param obj
	 * @return List 组集合
	 * @throws WTException
	 */
	public static List getNodes(Object obj) throws WTException {
		ArrayList arraylist = new ArrayList();
		if (obj instanceof OrgContainer) {
			OrgContainer orgcontainer = (OrgContainer) obj;
			PrincipalSpec principalspec = new PrincipalSpec();
			try {
				principalspec.setContainerReference(newWTContainerRef(orgcontainer));
				principalspec.setPerformLookup(false);
				principalspec.setIncludeAllServices(false);
			} catch (WTPropertyVetoException wtpropertyvetoexception) {
				throw new WTException();
			}
			DirectoryContextProvider adirectorycontextprovider[] = getPublicContextProviders(principalspec);
			DirectoryContextProvider adirectorycontextprovider1[] = adirectorycontextprovider;
			int i = adirectorycontextprovider1.length;
			for (int j = 0; j < i; j++) {
				DirectoryContextProvider directorycontextprovider = adirectorycontextprovider1[j];
				directorycontextprovider.setInternalGroupsSearchCriteria(null);
			}

			WTGroup wtgroup1;
			for (Enumeration enumeration1 = findLikeGroups("*", adirectorycontextprovider[0]); enumeration1.hasMoreElements(); arraylist.add(wtgroup1))
				wtgroup1 = (WTGroup) enumeration1.nextElement();

		} else if (obj instanceof WTGroup) {
			WTGroup wtgroup = (WTGroup) obj;
			Enumeration enumeration = OrganizationServicesHelper.manager.members(wtgroup, false);
			Object obj1 = null;
			for (; enumeration.hasMoreElements(); arraylist.add(obj1)) {
				WTPrincipal wtprincipal = (WTPrincipal) enumeration.nextElement();
				wtprincipal = OrganizationServicesHelper.manager.inflate(wtprincipal);
				if (wtprincipal instanceof WTUser) {
					obj1 = (WTUser) wtprincipal;
					continue;
				}
				if (wtprincipal instanceof WTGroup)
					obj1 = ((WTGroup) wtprincipal).getOrganization() != null ? ((Object) (NmGroup.getNmGroup((WTGroup) wtprincipal)))
							: ((Object) (wtprincipal));
			}

		}
		return arraylist;
	}

	protected static Enumeration findLikeGroups(String s, DirectoryContextProvider directorycontextprovider) throws WTException {
		return OrganizationServicesHelper.manager.findLikeGroups(s, directorycontextprovider);
	}

	protected static WTContainerRef newWTContainerRef(WTContainer wtcontainer) throws WTException {
		return WTContainerRef.newWTContainerRef(wtcontainer);
	}

	protected static DirectoryContextProvider[] getPublicContextProviders(PrincipalSpec principalspec) throws WTException {
		return WTContainerHelper.service.getPublicContextProviders(principalspec);
	}

	public static String formatTime(long longTimes) {
		Date date = new Date(longTimes);
		String pattern = "yyyy/MM/dd HH:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		return sdf.format(date) + " GMT+08:00";
	}

	public static String formatTime2(long longTimes) {
		Date date = new Date(longTimes);
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		return sdf.format(date);
	}

	public static String formatTime3(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(time);
		String pattern = "yyyy/MM/dd HH:mm";
		SimpleDateFormat sdf2 = new SimpleDateFormat(pattern);
		sdf2.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String time2 = sdf2.format(date) + " GMT+08:00";
		return time2;
	}

	public static Persistable getPersistable(String oid) throws WTException {
		ReferenceFactory factory = new ReferenceFactory();
		WTReference reference = factory.getReference(oid);
		return reference.getObject();
	}

	public static String getPersistableOid(Persistable persistable) throws WTException {
		ReferenceFactory factory = new ReferenceFactory();
		return factory.getReferenceString(persistable);
	}

	/**
	 * 提起辅制工艺任务时，对于同一份工艺文件、同一个车间，应该进行如下容错：
	 * 1、如果是工艺设计任务：只允许有一个正在进行的任务，否则报错已存在（如果工艺任务已完成，可以重复） 2、如果是工艺更改任务：同上
	 * 3、如果是临时工艺任务：允许同时下达多个
	 *
	 * @param map
	 * @return
	 * @throws WTException
	 */
	public static boolean checkProcessTask(Map<String, String> map) throws WTException {
		LOG.debug("--createProcessTaskItem-------map:" + map);
		if (map == null || map.isEmpty()) {
			LOG.debug("--createProcessTaskItem-------map is null");
			return false;
		}
		ProcessTask processTask = getProcessTaskItemByTechnics(map);
		if (processTask == null) {
			LOG.debug("--createProcessTaskItem-------ProcessTask is not exsit");
			return false;
		}
		long id = PersistenceHelper.getObjectIdentifier(processTask).getId();
		WTPart part = getWtPartByProcessTask(id);
		if (part == null) {
			LOG.debug("--createProcessTaskItem-------WTPart is not exsit for ProcessTask oid:" + id);
			return false;
		}

		String fuzhichejian = map.get("FZCJ");
		String techNumber = map.get("pplanNumber");
		System.out.println("---------fuzhichejian------" + fuzhichejian);
		System.out.println("---------techNumber------" + techNumber);
		IBAUtility ibauti = null;
		List<ProcessTask> list = getAllProcessTaskByPart(part, ProcessConstants.TASK_TYPE_FZGYRW);
		System.out.println("---------list------" + list);
		for (ProcessTask task : list) {
			ibauti = new IBAUtility(task);
			String pplanNumber = ibauti.getIBAValue("PPNUMBER");
			String chejian = task.getFuzhichejian();
			System.out.println("---------pplanNumber------" + pplanNumber);
			System.out.println("---------chejian------" + chejian);
			System.out.println("---------task.getTaskState()------" + task.getTaskState());
			if (fuzhichejian.equals(chejian) && techNumber.equals(pplanNumber) && !task.getTaskState().equals(ProcessConstants.TASK_STATE_YIWANGONG)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 新建工艺任务
	 *
	 * @param map
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static boolean createProcessTaskItem(Map<String, String> map) throws WTException, WTPropertyVetoException {
		LOG.debug("--createProcessTaskItem-------map:" + map);
		if (map == null || map.isEmpty()) {
			LOG.debug("--createProcessTaskItem-------map is null");
			return false;
		}
		ProcessTask processTask = getProcessTaskItemByTechnics(map);
		if (processTask == null) {
			LOG.debug("--createProcessTaskItem-------ProcessTask is not exsit");
			return false;
		}
		long id = PersistenceHelper.getObjectIdentifier(processTask).getId();
		WTContainer container = processTask.getContainer();
		WTPart part = getWtPartByProcessTask(id);
		if (part == null) {
			LOG.debug("--createProcessTaskItem-------WTPart is not exsit for ProcessTask oid:" + id);
			return false;
		}
		Folder folder = getFolder("/Default", container);

//		Map<String, List<WTUser>> gongYiZuZhangmap = getGroupAndUsersInOrgContainer();
		// 获取"项目部型号主管"角色的人
//		List<WTUser> xmbUsers = ProcessUtil.getRoleUsersByWTContainer("XIANGMUBUXINGHAOZHUGUANG", container);
//		gongYiZuZhangmap.put(Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN, xmbUsers);

		String gongyiyuan = map.get("FZCJ");
//		List<WTUser> fzZuZhang = gongYiZuZhangmap.get(fuzhichejian);
		if (gongyiyuan == null || gongyiyuan.isEmpty()) {
			LOG.debug("--createProcessTaskItem-------gongyiyuan is null");
			return false;
		}
		WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
		// 新建辅制工艺任务对象
		ProcessTask newProcessTask = ProcessTask.newProcessTask();
		newProcessTask.setName(part.getName());
		newProcessTask.setNumber(part.getNumber());
		newProcessTask.setVersion(part.getIterationDisplayIdentifier().toString());
		newProcessTask.setZhuzhichejian(processTask.getZhuzhichejian());
//		newProcessTask.setFuzhichejian(fuzhichejian);
		newProcessTask.setRenwuyaoqiu(map.get("taskDescribe"));
		newProcessTask.setEndDate(Timestamp.valueOf(map.get("JHWCSJ")));
		newProcessTask.setTaskState(ProcessConstants.TASK_STATE_JINGXINZHONG);
		newProcessTask.setTaskType(map.get("taskType"));

		newProcessTask.setContainer(part.getContainer());

		FolderHelper.assignLocation((FolderEntry) newProcessTask, folder);

		newProcessTask = (ProcessTask) PersistenceHelper.manager.save(newProcessTask);

		// 创建辅制工艺任务与零部件的关联
		ProProcessor.createProcessTaskLink(newProcessTask, part);

		try {
			IBAUtility ibaUti = new IBAUtility(newProcessTask);
			ibaUti.setIBAValue("PPNUMBER", map.get("pplanNumber"));// 主制工艺文件编号
			ibaUti.setIBAValue("PPNAME", map.get("technicsName"));// 主制工艺文件名称
			ibaUti.setIBAValue("PPTASKTYPE", ProcessConstants.TASK_TYPE_FZGYRW);// 此工艺任务为辅制工艺任务
			ibaUti.setIBAValue("PPTASKCREATOR", currentUser.getName() + "(" + currentUser.getFullName() + ")");// 此工艺任务的提交者
			ibaUti.setIBAValue("ZTASKOID", PersistenceHelper.getObjectIdentifier(processTask).getId() + "");// 主制工艺任务OID
			ibaUti.setIBAValue("cldePlanTime", map.get("cldePlanTime"));// 主制工艺任务OID
			newProcessTask = (ProcessTask) ibaUti.updateAttributeContainer(newProcessTask);
			ibaUti.updateIBAHolder(newProcessTask);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		// add by hding 20150827 过滤重复用户
		/**Set<WTUser> users = new HashSet<WTUser>(fzZuZhang);

		for (WTUser wtUser : users) {

			// 为辅制车间创建辅制工艺任务活动
			ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
			processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(newProcessTask).getId());
			processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
			processTaskItem.setOwner(wtUser.getName());
			processTaskItem.setName(part.getName());
			processTaskItem.setNumber(part.getNumber());
			processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
			processTaskItem.setZhurengongyishi(newProcessTask.getCreatorName());
			processTaskItem.setChejian(fuzhichejian);
			processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
			processTaskItem.setIszhuzhi(false);
			processTaskItem.setRenwuyaoqiu(map.get("taskDescribe"));
			processTaskItem.setTaskType(map.get("taskType"));
			processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
			processTaskItem.setEndDate(Timestamp.valueOf(map.get("JHWCSJ")));
			if (map.get("taskType").equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
				processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FUZHILINSHIGONGYIRENWUZHIPAI);
			} else if (map.get("taskType").equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
				processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FUZHIZHIPAIGONGYIYUAN);
			} else if (map.get("taskType").equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
				processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FUZHIGONGYIGENGGAIRENWUZHIPAI);
			}
			processTaskItem.setContainer(part.getContainer());
			FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);

			processTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(processTaskItem);

			try {
				IBAUtility ibaUtility = new IBAUtility(processTaskItem);
				ibaUtility.setIBAValue("PPNUMBER", map.get("pplanNumber"));// 主制工艺文件编号
				ibaUtility.setIBAValue("PPNAME", map.get("technicsName"));// 主制工艺文件名称
				ibaUtility.setIBAValue("PPTASKTYPE", ProcessConstants.TASK_TYPE_FZGYRW);// 此工艺任务为辅制工艺任务
				ibaUtility.setIBAValue("PPTASKCREATOR", currentUser.getName() + "(" + currentUser.getFullName() + ")");// 此工艺任务的提交者
				ibaUtility.setIBAValue("ZTASKOID", map.get("ZGYOID"));// 主制工艺任务活动OID
				ibaUtility.setIBAValue("cldePlanTime", map.get("cldePlanTime"));// 主制工艺任务活动OID
				processTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(processTaskItem);
				ibaUtility.updateIBAHolder(processTaskItem);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}*/

		if(StrUtil.isNotEmpty(gongyiyuan)) {
			ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
			processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(newProcessTask).getId());
			processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
			processTaskItem.setOwner(gongyiyuan);
			processTaskItem.setName(part.getName());
			processTaskItem.setNumber(part.getNumber());
			processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
			processTaskItem.setZhurengongyishi(newProcessTask.getCreatorName());
//			processTaskItem.setChejian(fuzhichejian);
			processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
			processTaskItem.setIszhuzhi(false);
			processTaskItem.setRenwuyaoqiu(map.get("taskDescribe"));
			processTaskItem.setTaskType(map.get("taskType"));
			processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
			processTaskItem.setEndDate(Timestamp.valueOf(map.get("JHWCSJ")));
			if (map.get("taskType").equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
				processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FUZHILINSHIGONGYIRENWU);
			} else if (map.get("taskType").equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
				processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FUZHIBIANZHIGONGYI);
			} else if (map.get("taskType").equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
				processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FUZHIGONGYIGENGGAIRENWU);
			}
			processTaskItem.setContainer(part.getContainer());
			FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);

			processTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(processTaskItem);

			try {
				IBAUtility ibaUtility = new IBAUtility(processTaskItem);
				ibaUtility.setIBAValue("PPNUMBER", map.get("pplanNumber"));// 主制工艺文件编号
				ibaUtility.setIBAValue("PPNAME", map.get("technicsName"));// 主制工艺文件名称
				ibaUtility.setIBAValue("PPTASKTYPE", ProcessConstants.TASK_TYPE_FZGYRW);// 此工艺任务为辅制工艺任务
				ibaUtility.setIBAValue("PPTASKCREATOR", currentUser.getName() + "(" + currentUser.getFullName() + ")");// 此工艺任务的提交者
				if(StrUtil.isNotEmpty(map.get("ZGYOID"))){
					ibaUtility.setIBAValue("ZTASKOID", map.get("ZGYOID"));// 主制工艺任务活动OID
				}
				ibaUtility.setIBAValue("cldePlanTime", map.get("cldePlanTime"));// 主制工艺任务活动OID
				processTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(processTaskItem);
				ibaUtility.updateIBAHolder(processTaskItem);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static ProcessTask getProcessTaskItemByTechnics(Map<String, String> map) throws WTException {
		String zgyoid = map.get("ZGYOID");
		if (zgyoid == null || "".equals(zgyoid) || "null".equals(zgyoid)) {
			LOG.debug("--createProcessTaskItem-------zgyoid is null");

			String pplanNumber = map.get("technicsNumber");

			WTDocument technicsDoc = WCUtil.getDocumentByNumber(pplanNumber);
			if (technicsDoc == null) {
				LOG.debug("getProcessTaskItemByTechnics-------technicsDoc is not exsit for number:" + pplanNumber);
				return null;
			}
			QueryResult qr = WTPartHelper.service.getDescribesWTParts(technicsDoc);
			WTPart part = null;
			while (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
				ProcessTask processTask = getProcessTaskByPart(part, ProcessConstants.TASK_TYPE_ZZGYRW);
				if (processTask != null) {
					return processTask;
				}
			}
		} else {
			long ida2a2 = Long.valueOf(zgyoid);
			ProcessTaskItem taskItem = getProcessTaskItem(ida2a2);
			if (taskItem == null) {
				LOG.debug("--createProcessTaskItem-------ProcessTaskItem is not exsit for ProcessTaskItem oid:" + ida2a2);
				return null;
			}
			long id = taskItem.getProcessTaskId();
			return getProcessTask(id);
		}
		return null;
	}

	private static Folder getFolder(String path, WTContainer con) throws WTException {
		Folder folder = null;
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		String subPath = "";
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			subPath = subPath + "/" + token;
			if (subPath != null && !subPath.equalsIgnoreCase("")) {
				try {
					folder = FolderHelper.service.getFolder(subPath, WTContainerRef.newWTContainerRef(con));
				} catch (FolderNotFoundException e) {
					boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
					folder = FolderHelper.service.createSubFolder(subPath, WTContainerRef.newWTContainerRef(con));
					SessionServerHelper.manager.setAccessEnforced(flag);
				}
			}
		}
		return folder;
	}

	public static String objtect2String(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return String.valueOf(obj);
		}
	}

	public static boolean isZhuRenGongyiShi(WTContainer wtContainer) throws WTException {
		WTUser curentUser = (WTUser) SessionHelper.getPrincipal();
		ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
		Role role = Role.toRole("ZHURENGONGYISHI");
		if (role == null) {
			return false;
		}
		ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
		for (WTPrincipalReference reference : arrayList) {
			Object object2 = reference.getPrincipal();
			if (object2 instanceof WTUser) {
				WTUser user = (WTUser) object2;
				if (user.getName().equals(curentUser.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static void completeProcessTaskItem(String taskOid) {
		if (taskOid != null && !"".equals(taskOid)) {
			ReferenceFactory rf = new ReferenceFactory();
			try {
				Persistable p = rf.getReference("OR:ext.casc.process.ProcessTaskItem:" + taskOid).getObject();
				if (p instanceof ProcessTaskItem) {
					ProcessTaskItem taskItem = (ProcessTaskItem) p;
					taskItem.setCompletedBy(taskItem.getOwner());
					taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_WANCHENGRENWU);
					taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_YIWANCHENG);
					taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);

					//通知NC/MES返修工艺编制完成
					if(AnalysisConstant.RENWUYIJV_YIZHIPIN.equals(taskItem.getRenwuyiju()) || AnalysisConstant.RENWUYIJV_ZAIZHIPIN.equals(taskItem.getRenwuyiju())) {
						String processdocNum = IBAHelper.getIBAStringValue(taskItem, "PROCESSDOCNUM");
						String analysisNumber = IBAHelper.getIBAStringValue(taskItem, "ANALYSISNUMBER");
						WTPart part = getWtPart(taskItem.getPartId());
						if(StrUtil.isNotEmpty(processdocNum) && StrUtil.isNotEmpty(analysisNumber) && part != null) {
							long vrOid = part.getBranchIdentifier();
							String partId = WTPart.class.getName() + ":" + vrOid;
							WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(processdocNum);
							if(document != null){
								String version = document.getIterationDisplayIdentifier().toString();
								String ppNumber = IBAHelper.getIBAStringValue(document, "PPNUMBER");
								if(AnalysisConstant.RENWUYIJV_YIZHIPIN.equals(taskItem.getRenwuyiju())){
									String url = "http://10.125.237.4:80/service/PdmtoNCservlet";
									if(!PDMConfig.isZS){
										url = "http://10.125.237.23:8099/service/PdmtoNCservlet";
									}
									JSONArray array = new JSONArray();
									JSONObject object = new JSONObject();
									object.put("yxfxcode", analysisNumber);//影响分析编号
									object.put("partid", partId);//部件ID
									object.put("fxgycode", ppNumber);//返修工艺编号
									object.put("fxgyversion", version);//返修工艺版本
									object.put("fxgyfigurecode", part.getNumber());//返修图号
									array.put(object);
									String body = HttpRequest.post(url).body(array.toString(), "application/json").execute().body();
									JSONObject result = new JSONObject(body);
									System.out.println("制品返修工艺推送结果 result: " + result);
								} else if(AnalysisConstant.RENWUYIJV_ZAIZHIPIN.equals(taskItem.getRenwuyiju())) {
									JSONObject object = new JSONObject();
									object.put("WORKFLOW_SNID", document.getNumber());
									object.put("WORKFLOWNAME", ppNumber);
									object.put("WORKFLOWVERSION", version);
									object.put("ANALYSISID", analysisNumber);
									object.put("BJID", partId);
									object.put("GYY", document.getModifier().getName());
									object.put("TYPE", IBAHelper.getIBAStringValue(document, "PPLANTYPE"));
									//返回路卡号
									String renwuyaoqiu = taskItem.getRenwuyaoqiu();
									String card = "";
									if(StrUtil.isNotEmpty(renwuyaoqiu)) {
										String[] strs = renwuyaoqiu.split("；");
										for(String str : strs) {
											if(str.indexOf("路卡号：") > -1) {
												String mes = str.substring(str.indexOf("路卡号：") + 4);
												if(StrUtil.isNotEmpty(mes)) {
													card += mes + ",";
												}
											}
										}
										if(card.length() > 0) {
											card = card.substring(0, card.length() - 1);
										}
									}
									object.put("CARD", card);
									String url = "http://10.125.237.6/CamstarPortal/webservice.asmx";
									if(!PDMConfig.isZS){
										url = "http://10.125.192.60/CamstarPortal/webservice.asmx";
									}
									String namespace = "http://tempuri.org/";
									Service service = new Service();
									try {
										Call call = (Call) service.createCall();
										call.setTimeout(new Integer(60000));
										call.setTargetEndpointAddress(new URL(url));
										call.setOperationName(new QName(namespace, "ReceiveWorkFlowAnalysisInfo"));
										call.addParameter(new QName(namespace,"JsonData"),XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);
										call.setReturnType(XMLType.XSD_STRING);
										call.setUseSOAPAction(true);
										call.setSOAPActionURI(namespace + "ReceiveWorkFlowAnalysisInfo");
										String ret = call.invoke(new Object[] {object.toString()}).toString();
										System.out.println("制品返修工艺推送结果 result: " + ret);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					QueryResult qResult = ProcessUtil.getAllProcessTaskItemByPTask(taskItem.getProcessTaskId());
					boolean flag = true;
					while (qResult.hasMoreElements()) {
						ProcessTaskItem tempTaskItem = (ProcessTaskItem) qResult.nextElement();
						if (ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(tempTaskItem.getTaskItemState())) {
							flag = false;
							break;
						}
					}
					if (flag) {
						ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
						processTask.setTaskState(ProcessConstants.TASK_STATE_YIWANGONG);
						System.out.println("---------ProcessTask " + processTask.getNumber() + " completed!");
						PersistenceHelper.manager.save(processTask);
					}
				}
			} catch (WTRuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void setProcessTaskItem2Creator(String taskOid, WfProcess process) {
		if (taskOid != null && !"".equals(taskOid)) {
			ReferenceFactory rf = new ReferenceFactory();
			try {
				Persistable p = rf.getReference("OR:ext.casc.process.ProcessTaskItem:" + taskOid).getObject();
				if (p instanceof ProcessTaskItem) {
					ProcessTaskItem taskItem = (ProcessTaskItem) p;
					if (taskItem.getTaskItemName().contains("辅制")) {// 如果是辅制工艺任务提交的流程
						WTUser creatorUser = (WTUser) taskItem.getCreator().getObject();
						System.out.println("creatorUser=========>>" + creatorUser.getName());
						saveTeamRole(creatorUser, process);
					}

				}
			} catch (WTRuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void saveTeamRole(WTUser users, WfProcess process) {
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		Transaction tx = new Transaction();
		try {
			tx.start();
			// String processName = process.getName();
			Team team = (Team) process.getTeamId().getObject();
			Role role = Role.toRole(Constants.ROLE_SHOUJIANREN);
			// Persistable pbo = (Persistable)
			// process.getContext().getValue("primaryBusinessObject");

			// 将该用户保存至流程团队角色
			// for(WTUser user : userSet){
			// Role role=Role.toRole(Constants.ROLE_SHOUJIANREN);
			// System.out.println("roleR=======================>>"+role.getStringValue());
			team.addPrincipal(role, users);
			System.out.println("team====>>" + team.getName());
			// }

			team = (Team) PersistenceHelper.manager.refresh(team);
			team = (Team) PersistenceHelper.manager.save(team);

			tx.commit();
			tx = null;
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			if (tx != null) {
				tx.rollback();
				tx = null;
			}
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}

	}

	/*
	 * public static boolean isShowByAllParts(WTPart part) throws WTException {
	 * if (part != null) { QueryResult qr =
	 * WTPartHelper.service.getUsesWTParts((WTPart)
	 * part,ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class)); while
	 * (qr.hasMoreElements()) { Persistable[] per = (Persistable[])
	 * qr.nextElement(); Object obj = per[1]; if (obj instanceof WTPart) {
	 * WTPart part2 = (WTPart) obj; WTPart partTemp =
	 * getLatestPartByMaster((WTPartMaster)part2.getMaster()); IBAUtility
	 * ibaUtility = new IBAUtility(partTemp); String partType =
	 * ibaUtility.getIBAValue("MTYPE");
	 *
	 * //自制件、外配套件、带料委外件、不带料委外件类型的零部件 if
	 * (!Constants.TYPE_ZIZHIJIAN.equals(partType) &&
	 * !Constants.TYPE_WAIPEITAOJIAN.equals(partType) &&
	 * !Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType) &&
	 * !Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)) { continue; }
	 * if(isExistProcessTask(partTemp)){ return isShowByAllParts(partTemp);
	 * }else{ return true; } } else if (obj instanceof WTPartMaster) {
	 * WTPartMaster master = (WTPartMaster) obj; WTPart partTemp =
	 * getLatestPartByMaster(master); if(isExistProcessTask((WTPart) partTemp)){
	 * return isShowByAllParts((WTPart) partTemp); }else{ return true; } }
	 *
	 * } } return false; }
	 */
	public static WTPart getLatestPartByMaster(WTPartMaster partMaster) throws WTException {
		WTPart part = null;

		if (partMaster != null) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(partMaster);
			if (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();

			}
		}
		return part;
	}

	public static QueryResult queryProcessPlan(WTUser currentUser) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessPlan.class);
		int[] index = { 0 };

		long longId = PersistenceHelper.getObjectIdentifier(currentUser).getId();
		SearchCondition sCondition = new SearchCondition(ProcessPlan.class, "creator.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		return PersistenceHelper.manager.find((StatementSpec) qSpec);
	}

	public static QueryResult getAllProcessTaskByProcessPlanId(long longId) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTask.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(ProcessTask.class, "ProcessPlanId", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		return qResult;
	}

	// ext.casc.process.util.ProcessUtil.setProcessTaskItemIBAValue(taskOid);
	public static void setProcessTaskItemIBAValue(String taskOid) {
		if (taskOid != null && !"".equals(taskOid)) {
			ReferenceFactory rf = new ReferenceFactory();
			try {
				Persistable p = rf.getReference("OR:ext.casc.process.ProcessTaskItem:" + taskOid).getObject();
				if (p instanceof ProcessTaskItem) {
					IBAHolder op = (IBAHolder) p;
					IBAUtility iba = new IBAUtility((IBAHolder) op);
					iba.setIBAValue("PROCESSDOCNUM", "");
					op = iba.updateAttributeContainer(op);
					iba.updateIBAHolder(op);
				}
			} catch (WTRuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	public static ArrayList<String> getGongXuCheJian() throws RemoteException {
		ArrayList<String> list = new ArrayList<String>();
		Map<String, String> workShop = ResourceIntf.getWorkShops();
		if (workShop != null && workShop.size() > 0) {
			Collection<String> coll = workShop.values();
			Iterator<String> it = coll.iterator();
			while (it.hasNext()) {
				String temp = (String) it.next();
				if (temp != null && temp.trim().length() > 0) {
					list.add(temp);
				}
			}
			Collections.sort(list);
		}

		return list;
	}

	public static ArrayList<String> getSpecializedType() throws WTException {
		ArrayList<String> list = new ArrayList<String>();
		 List<WTPart> zylbList = null;
         try {
				zylbList = SopUtil.getSopResourceByType(SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		for (WTPart wtPart : zylbList) {
				list.add(wtPart.getName());
			}
		return list;
	}

	public static ArrayList<String> getProceduceName(){
		ArrayList<String> list = new ArrayList<String>();
		 List<WTPart> Lis = null;
        try {
				Lis = SopUtil.getSopResourceByType(SopConstants.SOP_TYPE_PROCEDUCENAME);
			} catch (Exception e) {
				e.printStackTrace();
			}
		for (WTPart wtPart : Lis) {
				list.add(wtPart.getName());
			}
		return list;
	}

	public static ArrayList<String> getParametersName(){
		ArrayList<String> list = new ArrayList<String>();
		 List<WTPart> Lis = null;
        try {
				Lis = SopUtil.getSopResourceByType(SopConstants.SOP_TYPE_PARAMETERSNAME);
			} catch (Exception e) {
				e.printStackTrace();
			}
		for (WTPart wtPart : Lis) {
				list.add(wtPart.getName());
			}
		return list;
	}

	public static ArrayList<String> getMaterialCategory(){
		ArrayList<String> list = new ArrayList<String>();
		 List<WTPart> Lis = null;
        try {
				Lis = SopUtil.getSopResourceByType(SopConstants.SOP_TYPE_MATERIALCATEGORY);
			} catch (Exception e) {
				e.printStackTrace();
			}
		for (WTPart wtPart : Lis) {
				list.add(wtPart.getName());
			}
		return list;
	}

	/**
	 * 方法功能: 通过工艺文件编号和工艺任务类型查询工艺任务
	 *
	 * @param processNumber
	 * @param taskType
	 * @return java.util.List<ext.casc.process.ProcessTask>
	 * @author LB
	 * @date 2020/9/6
	 */
	public static List<ProcessTask> getProcessTaskByProcessNumberAndTaskType(String processNumber, String taskType) throws Exception {
		List<ProcessTask> processTaskList = new ArrayList<ProcessTask>();
		QuerySpec qs = new QuerySpec(ProcessTask.class);
		qs.setAdvancedQueryEnabled(true);
		int index[] = {0};

		//查询工艺规程软属性工艺文件编号"PPNUMBER"
		ClassAttribute caId = new ClassAttribute(ProcessTask.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		SubSelectExpression subSelectExpression = getStringIBAQuery("PPNUMBER", processNumber);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		qs.appendAnd();
		SubSelectExpression subSelectExpression2 = getStringIBAQuery("PPTASKTYPE", taskType);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression2), index);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		ProcessTask processTask = null;
		while (qr.hasMoreElements()) {
			processTask = (ProcessTask) qr.nextElement();
			processTaskList.add(processTask);
		}
		return processTaskList;
	}

	public static SubSelectExpression getStringIBAQuery(String ibaName, String ibaValue) throws WTException,
			WTPropertyVetoException, RemoteException {
		// 获取IBA属性定义
		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(StringValue.class, false);
		qs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[]{idx},
				false);
		qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL,
				ibaDefId), new int[]{idx});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, ibaValue, false),
				new int[]{idx});
		return new SubSelectExpression(qs);
	}

	public static List<String> getWfActivityListByProcess(String processName) {
		List<String> list = new ArrayList<String>();
		Map<String,String> map = new HashMap<String, String>();
		try {
			WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition(processName);
			if(wfprocessdefinition == null){
				WTOrganization org;
				try {
					org = OrganizationHelper.getOrganizationByName("149");
					OrgContainer container = WTContainerHelper.service.getOrgContainer(org);
					WTContainerRef containerRef = WTContainerRef.newWTContainerRef(container);
					wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition(processName,containerRef);
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
			if(wfprocessdefinition != null) {
				Vector activities = wfprocessdefinition.getProcessTemplate().getAssignedActivities();
				if(activities != null) {
					for(Object activity : activities) {
						if(activity instanceof WfAssignedActivityTemplate){
							WfAssignedActivityTemplate template = (WfAssignedActivityTemplate) activity;
							if(!map.containsKey(template.getName())){
								list.add(template.getName());
								map.put(template.getName(), template.getName());
							}
						}
					}
				}
			}
		} catch(WTException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	public static ProcessTaskLink getProcessTaskLinkByProcessTask(long longId) throws WTException {
		QuerySpec qSpec = new QuerySpec(ProcessTaskLink.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(ProcessTaskLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, longId);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		if (qResult.hasMoreElements()) {
			ProcessTaskLink link = (ProcessTaskLink) qResult.nextElement();
			return link;
		}
		return null;
	}

}
