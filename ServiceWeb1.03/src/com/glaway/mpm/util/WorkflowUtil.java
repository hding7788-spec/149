package com.glaway.mpm.util;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import ext.casc.util.IBAUtility;
import wt.associativity.NCServerHolder;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeRecord2;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.httpgw.URLFactory;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.print.PrintUserCodeProcessor;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;

import ext.ases.envelope.ProcessEnvelope;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;

public class WorkflowUtil {
	private static final String CLASSNAME = WorkflowUtil.class.getName();

	public static void main(String[] args) throws WTException {
		//getWfVoteWithProcess((WfProcess) Util.getObjectByOid(WfProcess.class, "1182458"));

	}

	/**
	 * 获取pn签申包的整件对象
	 *
	 * @author lbzhang
	 * @date 2012-10-17 上午10:51:05
	 * @modifier
	 * @param pn
	 * @return
	 * @throws MaturityException
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static WTPart getALZJPartFromPN(Object pbo) throws MaturityException, WTException {
		// 获取签申包pn对象内容
		PromotionNotice pn = (PromotionNotice) pbo;
		QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				if (Util.isALZJPart(part.getNumber())) {
					return part;
				}
			}
		}

		return null;
	}

	/**
	 * 获取当前系统的域http://pds.nriet.com/Windchill/
	 *
	 * @author lbzhang
	 * @date 2012-10-17 上午11:52:30
	 * @modifier
	 * @return
	 * @throws WTException
	 */
	public static String getURLString() throws WTException {
		URLFactory urlf = new URLFactory();
		String urlStr = urlf.getBaseHREF();
		return urlStr;
	}

	/**
	 * 获取计划员工艺派工工具的链接
	 *
	 * @author lbzhang
	 * @date 2012-10-24下午04:54:33
	 * @modifier
	 * @return
	 * @throws WTException
	 */
	public static String getStartTechnicToolURLStr(WTPart part) throws WTException {
		String oid = part.toString();
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/glaway/mpm/pbomTaskAllocate.jsp?oid=" + oid
				+ "\" target=_blank>启动工艺派工工具</a>";
	}

	/**
	 * 获取组长工艺派工工具的链接
	 *
	 * @author lbzhang
	 * @date 2012-12-19下午03:33:46
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getStartTechnicTollURLStrOfLeader(WTPart part) throws WTException {
		String oid = part.toString();
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/ext/glaway/mpm/LeaderAllocate.jsp?oid=" + oid
				+ "\" target=_blank>启动工艺派工工具</a>";
	}

	/**
	 * 获取查看提交签审PBOM的链接
	 *
	 * @author lbzhang
	 * @date 2012-12-5下午02:23:59
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getTechnicBrowseURLStr(String topPartOid) throws WTException {
		GLLogger.debug("top part addrress is :" + topPartOid);
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/ext/glaway/mpm/technicBrowsePBOM.jsp?oid=" + topPartOid
				+ "\" target=_blank>查看提交签审PBOM</a>";

	}

	/**
	 * 工艺合编获取启动工艺编辑器的链接
	 *
	 * @author qianlong
	 * @date 2012-10-26
	 * @return
	 * @throws WTException
	 *
	 */
	public static String getStartProcessEditorURL(String oid, Object object) {

		String url = "";
		String poid = "";// 零件的oid
		if (object instanceof WTPart) {
			poid = Util.getStringOid((WTPart) object);
		}
		try {
			oid = Util.getStringOid(WTPartUtil.getLatestPartByNumberAndView((WTPart) Util.getObjectByOid(WTPart.class,
					oid), Constants.planning));

			url = getURLString() + "ptc1/glaway/mpm/startPE?oid=" + oid + "&poid=" + poid + "&ty=2";
		} catch (WTException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "URL-getStartProcessEditorURL-" + url);
		return url;
	}

	/**
	 * 工艺变更获取启动工艺编辑器的链接
	 *
	 * @author qianlong
	 * @date 2012-10-26
	 * @return
	 * @throws WTException
	 *
	 */
	public static String getStartProcessEditorURL(WTPart wholePart, WTPart currentPart, String type) {
		String url = "";
		try {
			String oid = "";
			if (wholePart != null && wholePart.getViewName().equals(Constants.design)) {
				oid = Util.getStringOid(WTPartUtil.getLatestPartByNumberAndView(wholePart, Constants.planning));
			} else {
				oid = Util.getStringOid(wholePart);
			}
			String coid = "";
			if (currentPart != null && currentPart.getViewName().equals(Constants.design)) {
				coid = Util.getStringOid(WTPartUtil.getLatestPartByNumberAndView(currentPart, Constants.planning));
			} else {
				coid = Util.getStringOid(currentPart);
			}
			url = getURLString() + "ptc1/glaway/mpm/startPE?oid=" + oid + "&coid=" + coid + "&ty=" + type;
		} catch (WTException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "URL-getStartProcessEditorURL-" + url);
		return url;
	}

	/**
	 * 工艺变更获取启动工艺编辑器的链接
	 *
	 * @author qianlong
	 * @date 2012-10-26
	 * @return
	 * @throws WTException
	 *
	 */
	public static String getStartProcessEditorURL(WTPart wholePart, WTPart currentPart, WTDocument document, String type) {
		String url = "";
		try {
			String wholePartOid = "";
			if (wholePart != null) {
				if (wholePart.getViewName().equals(Constants.design)) {
					wholePartOid = Util.getStringOid(WTPartUtil.getLatestPartByNumberAndView(wholePart,
							Constants.planning));
				} else {
					wholePartOid = Util.getStringOid(wholePart);
				}
			}
			String currentPartOid = "";
			if (currentPart != null) {
				if (currentPart.getViewName().equals(Constants.design)) {
					currentPartOid = Util.getStringOid(WTPartUtil.getLatestPartByNumberAndView(currentPart,
							Constants.planning));
				} else {
					currentPartOid = Util.getStringOid(currentPart);
				}
			}
			String documentOid = "";
			if (document != null) {
				documentOid = Util.getStringOid(VersionControlHelper.service.getLatestIteration(document, true));
			}
			url =getURLString() + "ptc1/glaway/mpm/startPE?O1=" + wholePartOid + "&O2=" + currentPartOid + "&O3=" + documentOid + "&ty="
					+ type;
		} catch (WTException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "URL-getStartProcessEditorURL-" + url);
		return url;
	}

	/**
	 * 获取工时填写的链接地址
	 *
	 * @author lbzhang
	 * @date 2012-11-29下午03:31:59
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getFillTimeUrlStr(WTPart part) throws WTException {
		String oid = part.toString();
		GLLogger.debug("part addrress is :" + oid);
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/glaway/mpm/fillTime.jsp?oid=" + oid
				+ "\" target=_blank>工时定额填写</a>";
	}

	/**
	 * 获取工时填写的链接地址
	 *
	 * @author lbzhang
	 * @date 2012-12-15下午02:22:42
	 * @param partOid
	 * @return
	 * @throws WTException
	 */
	public static String getFillTimeUrlStr(String partOid) throws WTException {
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/glaway/mpm/fillTime.jsp?oid=" + partOid
				+ "\" target=_blank>工时定额填写</a>";
	}

	/**
	 * 获取工时定额查看的链接地址
	 *
	 * @author lbzhang
	 * @date 2012-12-3上午10:00:08
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getFillTimeViewStr(WTPart part) throws WTException {
		String oid = part.toString();
		GLLogger.debug("part addrress is :" + oid);
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/glaway/mpm/fillTime.jsp?oid=" + oid
				+ "\" target=_blank>工时定额查看</a>";
	}

	/**
	 * 获取工时定额查看的链接地址
	 *
	 * @author lbzhang
	 * @date 2012-12-15下午02:23:47
	 * @param partOid
	 * @return
	 * @throws WTException
	 */
	public static String getFillTimeViewStr(String partOid) throws WTException {
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/ext/glaway/mpm/fillTime.jsp?oid=" + partOid
				+ "\" target=_blank>工时定额查看</a>";
	}

	/**
	 * 获取关键工序工步的链接地址
	 *
	 * @author lbzhang
	 * @date 2012-12-5下午02:23:01
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getKeyStepPaceURL(WTPart part) throws WTException {
		String oid = part.toString();
		GLLogger.debug("part step pace:" + oid);
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/ext/glaway/mpm/report/keyStepPace.jsp?oid=" + oid
				+ "\" target=_blank>关键工序工步</a>";
	}

	/**
	 * 获取关键工序工步的链接地址
	 *
	 * @author lbzhang
	 * @date 2012-12-15上午11:11:21
	 * @param partOid
	 * @return
	 * @throws WTException
	 */
	public static String getKeyStepPaceURL(String partOid) throws WTException {
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/ext/glaway/mpm/report/keyStepPace.jsp?oid=" + partOid
				+ "\" target=_blank>关键工序工步</a>";
	}

	/**
	 * 获取启动PBOM编辑器的链接
	 *
	 * @author qianlong
	 * @date 2012-11-2
	 * @param object
	 * @return
	 * @throws Exception
	 *
	 */
	public static String getStartPBOMEditorURL(Object object) throws Exception {
		String oid = "";
//		if (object instanceof WTPart) {
//			WTPart part = (WTPart) object;
//			String viewName = part.getViewName();
//			GLLogger.debug(CLASSNAME, "viewName--" + viewName);
//			if (Constants.planning.equals(viewName)) {
//				View view = WTPartUtil.getViewByName(Constants.design);
//				if (null != view) {
//					WTPart p = WTPartUtil.getPartByNumberAndView(part.getNumber(), view.getPersistInfo()
//							.getObjectIdentifier().getId());
//					if (null != p) {
//						part = p;
//					}
//				}
//			}
//			GLLogger.debug(CLASSNAME, "viewName--" + part.getViewName());
//			oid = Util.getStringOid(part);
//		} else if (object instanceof WTChangeRequest2) {
//			WTChangeRequest2 request2 = (WTChangeRequest2) object;
//			QueryResult result = QChangeHelper.getChangeablesAfterFromECR(request2);
//			while (result.hasMoreElements()) {
//				WTObject object2 = (WTObject) result.nextElement();
//				if (object2 instanceof WTPart) {
//					String typeName = TypedUtility.getTypeIdentifier(object2).getTypename();
//					if (typeName.contains(TypeNameConstants.gzRootPartTypeName)) {
//						oid = Util.getStringOid(object2);
//						break;
//					}
//				}
//			}
//		}
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/glaway/mpm/startPBOME.jsp?oid=" + oid
				+ "\" target=_blank>启动PBOM编辑器</a>";


	}

	/**
	 * 获取编辑PBOM属性的链接
	 *
	 * @author qianlong
	 * @date 2012-11-2
	 * @param object
	 * @return
	 * @throws WTException
	 *
	 */
	public static String getEditPBOMURL(Object object) throws WTException {
		String oid = "";
		if (object instanceof WTPart) {
			WTPart part = (WTPart) object;
			String viewName = part.getViewName();
			GLLogger.debug(CLASSNAME, "viewName--" + viewName);
			if (Constants.planning.equals(viewName)) {
				View view = WTPartUtil.getViewByName(Constants.design);
				if (null != view) {
					WTPart p = WTPartUtil.getPartByNumberAndView(part.getNumber(), view.getPersistInfo()
							.getObjectIdentifier().getId());
					if (null != p) {
						part = p;
					}
				}
			}
			GLLogger.debug(CLASSNAME, "viewName--" + part.getViewName());
			oid = Util.getStringOid((WTPart) object);
		}
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/glaway/mpm/editPBOM.jsp?oid=" + oid
				+ "\" target=_blank>编辑PBOM属性</a>";

	}

	/**
	 * 获取工艺预览的链接
	 *
	 * @author lbzhang
	 * @date 2012-11-14上午10:41:59
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getTechnicPreview(WTPart part, String type) throws WTException {
		String oid = part.toString();
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/ext/glaway/mpm/technicPreview.jsp?oid=" + oid + "&tp="
				+ type + "\" target=_blank>工艺预览</a>";
	}

	/**
	 * 获取临时工艺和返工工艺的工艺预览的链接
	 *
	 * @author lbzhang
	 * @date 2013-7-16
	 * @param part
	 * @param technicName
	 * @param type
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws WTRuntimeException
	 *
	 */
	public static String getTechnicPreview(WTPart part, String technicName, String type) throws WTException,
			WTRuntimeException, WTPropertyVetoException {
//		WTDocument doc = TechnicPreview.getZipDoc(part, technicName, type);
//		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/glaway/mpm/technicPreview.jsp?oid="
//				+ doc.toString() + "\" target=_blank>工艺预览</a>";
		return null;
	}

	/**
	 * 获取工艺预览的链接
	 *
	 * @author lbzhang
	 * @date 2012-12-15上午11:00:15
	 * @param partOid
	 * @return
	 * @throws WTException
	 */
	public static String getTechnicPreview(String partOid, String type) throws WTException {
		GLLogger.debug("partOid====>");
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/ext/glaway/mpm/technicPreview.jsp?oid=" + partOid
				+ "&tp=" + type + "\" target=_blank>工艺预览</a>";
	}

	/**
	 * 获取临时工艺和返工工艺的工艺预览的链接
	 *
	 * @author lbzhang
	 * @date 2013-7-16
	 * @param partOid
	 * @param technicName
	 * @param type
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws WTRuntimeException
	 *
	 */
	public static String getTechnicPreview(String partOid, String technicName, String type) throws WTException,
			WTRuntimeException, WTPropertyVetoException {
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(partOid);
//		WTDocument doc = TechnicPreview.getZipDoc(part, technicName, type);
//		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/glaway/mpm/technicPreview.jsp?oid="
//				+ doc.toString() + "\" target=_blank>工艺预览</a>";
		return null;
	}

	/**
	 * 计划员对工艺合编派工时的链接地址
	 *
	 * @author fly
	 * @date 2012-11-29
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getProcessplanUnitURL(WTObject primaryBusinessObject, String groupId) throws WTException {
		String oid = Util.getStringOid(primaryBusinessObject);
		return "<a href=\"" + getURLString() + "app/#netmarkets/jsp/glaway/mpm/punite.jsp?oid=" + oid + "&groupId="
				+ groupId + "\" target=_blank>合编工艺派工</a>";
	}

	/**
	 * 代码启动流程
	 *
	 * @author lbzhang
	 * @date 2012-10-18 上午11:39:16
	 * @modifier
	 * @param pbo
	 *            流程主对象pbo
	 * @param workFlowName
	 *            工作流程模板
	 * @param processName
	 *            启动流程的流程名称
	 */
	public static boolean startProcess(WTObject pbo, String workFlowName, String processName) {
		boolean flag = false;
		System.out.println("start process:" + workFlowName);
		long WORKFLOW_PRIORITY = 1;
		boolean enfore = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTContainerRef containerRef = WTContainerHelper.service.getExchangeRef();
			if (pbo instanceof WTContained) {
				WTContained contained = (WTContained) pbo;
				containerRef = contained.getContainerReference();
				GLLogger.debug("containerRef:" + containerRef.getName());
			}

			WTProperties wtproperties = WTProperties.getLocalProperties();
			WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty("wt.lifecycle.defaultWfProcessPriority", "1"));
			WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition(workFlowName);
			if (null != wfprocessdefinition) {
				WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null, containerRef);
				if (pbo != null) {
					wfprocess.setName(processName);
				}
				if (pbo instanceof WTPart) {
					WTPart part = (WTPart) pbo;
					wfprocess.setTeamTemplateId(part.getTeamTemplateId());
				}
				if (pbo instanceof WTDocument) {
					WTDocument document = (WTDocument) pbo;
					wfprocess.setTeamTemplateId(document.getTeamTemplateId());
				}
				if (pbo instanceof MPMProcessPlan) {
					MPMProcessPlan processPlan = (MPMProcessPlan) pbo;
					WTPart temp = MPMProcessPlanUtil.getMPMProcessplanRelatedPart(processPlan);
					GLLogger.debug("temp===>" + temp);
					wfprocess.setTeamTemplateId(temp.getTeamTemplateId());
				}

				ProcessData processdata = wfprocess.getContext();
				processdata.setValue("primaryBusinessObject", pbo);
				WfEngineHelper.service.startProcessImmediate(wfprocess, processdata, WORKFLOW_PRIORITY);
			}
			flag = true;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enfore);
		}
		return flag;
	}

	/**
	 * 开启流程
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param pbo
	 * @param workFlowName
	 * @param processName
	 * @return
	 *
	 */
	public static boolean startProcess(WTObject pbo, String workFlowName, String processName, Map<String, String> map) {
		boolean flag = false;
		System.out.println("start process:" + workFlowName);
		long WORKFLOW_PRIORITY = 1;
		boolean enfore = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTContainerRef containerRef = WTContainerHelper.service.getExchangeRef();
			if (pbo instanceof WTContained) {
				WTContained contained = (WTContained) pbo;
				containerRef = contained.getContainerReference();
				GLLogger.debug("containerRef:" + containerRef.getName());
			}

			WTProperties wtproperties = WTProperties.getLocalProperties();
			WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty("wt.lifecycle.defaultWfProcessPriority", "1"));
			WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition(workFlowName);
			if (null != wfprocessdefinition) {
				WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null, containerRef);
				if (pbo != null) {
					wfprocess.setName(workFlowName + "-" + processName);
				}
				if (pbo instanceof WTPart) {
					WTPart part = (WTPart) pbo;
					wfprocess.setTeamTemplateId(part.getTeamTemplateId());
				}
				if (pbo instanceof WTDocument) {
					WTDocument document = (WTDocument) pbo;
					wfprocess.setTeamTemplateId(document.getTeamTemplateId());
				}
				if (pbo instanceof MPMProcessPlan) {
					MPMProcessPlan processPlan = (MPMProcessPlan) pbo;
					WTPart temp = MPMProcessPlanUtil.getMPMProcessplanRelatedPart(processPlan);
					GLLogger.debug("temp===>" + temp);
					wfprocess.setTeamTemplateId(temp.getTeamTemplateId());
				}
				if (pbo instanceof WTChangeIssue) {
					WTChangeIssue ci = (WTChangeIssue) pbo;
					wfprocess.setTeamTemplateId(ci.getTeamTemplateId());
				}

				ProcessData processdata = wfprocess.getContext();
				processdata.setValue("primaryBusinessObject", pbo);
				for (String str : map.keySet()) {
					processdata.setValue(str, map.get(str));
				}
				WfEngineHelper.service.startProcessImmediate(wfprocess, processdata, WORKFLOW_PRIORITY);
			}
			flag = true;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enfore);
		}
		return flag;
	}

	/**
	 * 自动将参与者添加到流程相对的活动中,如工艺组长或者工艺任务
	 *
	 * @author lbzhang
	 * @date 2012-11-15下午12:53:20
	 * @param users
	 * @param roleStr
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static void addPrincipalToProcessActivity(String users, String roleStr, Object self) throws WTException {
		// 转换角色对象
		GLLogger.debug("add users:" + users + " to process");
		Role role = Role.toRole(roleStr);
		if (users == null) {
			users = "";
		}

		String[] user = users.split(",");
		GLLogger.debug("user.length:" + user.length + "   " + users);

		// 获取此流程
		WfProcess wf = getWfProcessBySelf(self);
		Team team = (Team) wf.getTeamId().getObject();
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < user.length; i++) {
			String userTemp = user[i].trim();
			if (!"".equals(userTemp) && !list.contains(userTemp)) {
				list.add(userTemp);
				GLLogger.debug("userTeam===>" + userTemp);
				WTUser wtuser = (WTUser) ReferenceFactory.getObjectbyOid(userTemp);
				WTPrincipal principal = OrganizationServicesHelper.manager.getPrincipal(wtuser.getName());
				GLLogger.debug("principal:" + principal);
				team.addPrincipal(role, principal);
			}
		}
	}

	/**
	 * 自动将参与者添加到流程相对的活动中,如工艺组长或者工艺任务
	 *
	 * @author lbzhang
	 * @date 2013-5-13
	 * @param prinRef
	 * @param roleStr
	 * @param self
	 * @throws WTException
	 *
	 */
	public static void addPrincipalToProcessActivity(WTPrincipalReference prinRef, String roleStr, Object self)
			throws WTException {
		// 获取此流程
		WfProcess wf = getWfProcessBySelf(self);
		Team team = (Team) wf.getTeamId().getObject();
		Role role = Role.toRole(roleStr);
		team.addPrincipal(role, prinRef.getPrincipal());
	}

	/**
	 * 自动将参与者添加到流程相对的活动中,如工艺组长或者工艺任务
	 *
	 * @author lbzhang
	 * @date 2013-6-6
	 * @param prinList
	 * @param roleStr
	 * @param self
	 * @throws WTException
	 *
	 */
	public static void addPrincipalToProcessActivity(ArrayList<WTPrincipal> prinList, String roleStr, Object self)
			throws WTException {
		WfProcess wf = getWfProcessBySelf(self);
		Team team = (Team) wf.getTeamId().getObject();
		Role role = Role.toRole(roleStr);
		for (WTPrincipal prin : prinList) {
			team.addPrincipal(role, prin);
		}
	}

	/**
	 * 获取流程创建者
	 *
	 * @author lbzhang
	 * @date 2013-6-6
	 * @param self
	 * @return
	 * @throws WTException
	 *
	 */
	public static WTPrincipal getCreatorOfWfProcess(Object self) throws WTException {
		WfProcess wf = getWfProcessBySelf(self);
		return wf.getCreator().getPrincipal();
	}

	/**
	 * PBOM将随着对应工艺规程的签署同步签署及归档
	 *
	 * @author lbzhang
	 * @date 2012-11-23上午10:42:52
	 * @param epart
	 * @throws WTException
	 */
	public static void setTechnicPBOMToReleased(WTPart epart, String state) throws WTException {
		List<WTPart> wtlist = WTPartUtil.getPlanningStructureByEPart(epart);
		for (int i = 0; i < wtlist.size(); i++) {
			WTPart part = wtlist.get(i);
			LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
		}
		GLLogger.debug("==setTechnicPBOMToReleased==");
	}

	/**
	 * 工艺合编流程中，把组长添加到流程的相应的角色中
	 *
	 * @author qianlong
	 * @date 2012-12-27
	 * @param groupId
	 * @param roleStr
	 * @param self
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void addUniteProcessTeam(String groupId, Object self) throws WTException, WTPropertyVetoException {
//		QueryResult qr = ProcessPlanUniteUtil.getUniteGroupById(groupId);
//		WfProcess wf = getWfProcessBySelf(self);
//		WTContainer container = wf.getContainer();
//		ContainerTeam containerTeam = getContainerTeam(container);
//		Team team = (Team) wf.getTeamId().getObject();
//		while (qr.hasMoreElements()) {
//			UniteGroup group = (UniteGroup) qr.nextElement();
//			Role role = Role.toRole(group.getName() + "MPMLEADER");
//			Role wfRole = Role.toRole("TECHNICALLEADER");
//			Enumeration<WTPrincipalReference> prinEnumRole = containerTeam.getPrincipalTarget(role);
//			while (prinEnumRole.hasMoreElements()) {
//				WTPrincipal principal = (WTPrincipal) prinEnumRole.nextElement().getObject();
//				team.addPrincipal(wfRole, principal);
//				group.setLeader(principal.getPersistInfo().getObjectIdentifier().toString());
//				PersistenceHelper.manager.save(group);
//				break;
//			}
//		}
	}

	/**
	 * 工艺合编流程中，把选择后的组员添加到流程的相应的角色中
	 *
	 * @author qianlong
	 * @date 2012-12-27
	 * @param groupId
	 * @param roleStr
	 * @param self
	 * @throws WTException
	 *
	 */
	public static void addUniteProcessTeam(String groupId, String roleStr, Object self) throws WTException {
//		QueryResult qr = ProcessPlanUniteUtil.getUniteGroupById(groupId);
//		Role role = Role.toRole(roleStr);
//		WfProcess wf = getWfProcessBySelf(self);
//
//		Team team = (Team) wf.getTeamId().getObject();
//		while (qr.hasMoreElements()) {
//			UniteGroup ug = (UniteGroup) qr.nextElement();
//			String responser = ug.getResponser();
//			if (null == responser || "".equals(responser)) {
//				team.addPrincipal(role, (WTUser) ReferenceFactory.getObjectbyOid(ug.getLeader()));
//
//			} else {
//				team.addPrincipal(role, (WTUser) ReferenceFactory.getObjectbyOid(responser));
//
//			}
//
//		}
	}

	/**
	 * 转换流程对象，如果是活动就获取对应的流程
	 *
	 * @author qianlong
	 * @date 2012-12-5
	 * @param self
	 * @return
	 * @throws WTException
	 *
	 */
	public static WfProcess getWfProcessBySelf(Object self) throws WTException {

		if (self instanceof ObjectReference) {
			self = ((ObjectReference) self).getObject();
		}
		WfProcess wf = null;
		if (self instanceof WfAssignedActivity) {
			wf = ((WfAssignedActivity) self).getParentProcess();
		} else {
			wf = (WfProcess) self;
		}
		return wf;
	}

	/**
	 * 通过流程获取流程的签审信息
	 *
	 * @author lbzhang
	 * @date 2012-12-27下午08:41:39
	 * @param self
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<HashMap<String, String>> getWfProcessRecordes(Object self) throws WTException {
		ArrayList<HashMap<String, String>> historyList = new ArrayList<HashMap<String, String>>();

		ArrayList<WorkItem> wiList = getWorkItemFromProcess(self);
		GLLogger.debug("wiList=======>" + wiList.size());
		for (int i = 0; i < wiList.size(); i++) {
			WorkItem wi = wiList.get(i);
			WfVotingEventAudit wfvotevent = getWfVoteWithWorkItem(wi);
			GLLogger.debug("==========================================");
			if (wfvotevent != null) {
				String actName = wfvotevent.getActivityName();
				GLLogger.debug("actName===>" + actName);// 活动名称
				WfActivity localWfActivity = (WfActivity) wi.getSource().getObject();

				Timestamp ts = localWfActivity.getEndTime();
				Date date = new Date(ts.getTime());
				int hour = date.getHours() + 8;
				date.setHours(hour);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String actTime = sdf.format(date);// 活动完成的时间
				GLLogger.debug("actTime===>" + actTime);

				WTPrincipalReference localWTPrincipalReference = wfvotevent.getAssigneeRef();
				WTPrincipal localWTPrincipal = (WTPrincipal) localWTPrincipalReference.getObject();
				WTUser user = (WTUser) localWTPrincipal;
				String actUserName = user.getFullName();// 活动完成者
				GLLogger.debug("actUserName==>" + actUserName);

				Role workRole = wfvotevent.getRole();
				String roleName = workRole.getFullDisplay();// 活动的角色
				GLLogger.debug("roleName=====>" + roleName);

				String userComment = wfvotevent.getUserComment();// 活动的注解\备注
				GLLogger.debug("userComment==>" + userComment);

				HashMap<String, String> historyMap = new HashMap<String, String>();
				historyMap.put("actName", actName);
				historyMap.put("actTime", actTime);
				historyMap.put("actUserName", actUserName);
				historyMap.put("roleName", roleName);
				historyMap.put("userComment", userComment);

				historyList.add(historyMap);
			}
		}

		return historyList;
	}

	/**
	 * 根据流程获取WorkItem
	 *
	 * @author lbzhang
	 * @date 2012-12-27下午08:19:05
	 * @param self
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<WorkItem> getWorkItemFromActivity(WfAssignedActivity activity) throws WTException {
		ArrayList<WorkItem> wiList = new ArrayList<WorkItem>();

		QuerySpec qs = new QuerySpec();
		int wfiIdx = qs.appendClassList(WorkItem.class, true);
		int wfaIdx = qs.appendClassList(WfAssignedActivity.class, false);

		qs.setAdvancedQueryEnabled(true);

		qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "thePersistInfo.theObjectIdentifier.id",
				SearchCondition.EQUAL, Util.getLongOid(activity)), wfaIdx);
		qs.appendAnd();

		qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", WfAssignedActivity.class,
				"thePersistInfo.theObjectIdentifier.id"), wfiIdx, wfaIdx);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		GLLogger.debug("qr.size====>" + qr.size());
		while (qr.hasMoreElements()) {
			Persistable[] ps = (Persistable[]) qr.nextElement();
			WorkItem worki = (WorkItem) ps[0];
			wiList.add(worki);
		}

		return wiList;
	}

	/**
	 * 根据流程获取WorkItem
	 *
	 * @author qianlong
	 * @date 2013-7-26
	 * @param activity
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<WorkItem> getWorkItemFromActivityAndState(WfAssignedActivity activity, String state)
			throws WTException {
		ArrayList<WorkItem> wiList = new ArrayList<WorkItem>();

		QuerySpec qs = new QuerySpec();
		int wfiIdx = qs.appendClassList(WorkItem.class, true);
		int wfaIdx = qs.appendClassList(WfAssignedActivity.class, false);

		qs.setAdvancedQueryEnabled(true);
		qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "thePersistInfo.theObjectIdentifier.id",
				SearchCondition.EQUAL, Util.getLongOid(activity)), wfaIdx);
		qs.appendAnd();

		qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", WfAssignedActivity.class,
				"thePersistInfo.theObjectIdentifier.id"), wfiIdx, wfaIdx);
		qs.appendAnd();

		qs.appendWhere(new SearchCondition(WorkItem.class, WorkItem.STATUS, SearchCondition.EQUAL, state), wfiIdx);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			Persistable[] ps = (Persistable[]) qr.nextElement();
			WorkItem worki = (WorkItem) ps[0];
			wiList.add(worki);
		}

		return wiList;
	}

	/**
	 * 根据流程获取WorkItem
	 *
	 * @author lbzhang
	 * @date 2012-12-27下午08:19:05
	 * @param self
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<WorkItem> getWorkItemFromProcess(Object self) throws WTException {
		ArrayList<WorkItem> wiList = new ArrayList<WorkItem>();

		QuerySpec qs = new QuerySpec();
		int wfpIdx = qs.appendClassList(WfProcess.class, false);
		int wfiIdx = qs.appendClassList(WorkItem.class, true);
		int wfaIdx = qs.appendClassList(WfAssignedActivity.class, false);

		WfProcess process = getWfProcessBySelf(self);
		GLLogger.debug("process===>" + process.getName());

		qs.setAdvancedQueryEnabled(true);

		qs.appendWhere(new SearchCondition(WfProcess.class, "thePersistInfo.theObjectIdentifier.id",
				SearchCondition.EQUAL, Util.getLongOid(process)), wfpIdx);
		qs.appendAnd();

		qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", WfAssignedActivity.class,
				"thePersistInfo.theObjectIdentifier.id"), wfiIdx, wfaIdx);
		qs.appendAnd();

		qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id", WfProcess.class,
				"thePersistInfo.theObjectIdentifier.id"), wfaIdx, wfpIdx);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		GLLogger.debug("qr.size====>" + qr.size());
		while (qr.hasMoreElements()) {
			Persistable[] ps = (Persistable[]) qr.nextElement();
			WorkItem worki = (WorkItem) ps[0];
			wiList.add(worki);
		}

		return wiList;
	}

	/**
	 * 根据WorkItem的id获取WfVotingEventAudit
	 *
	 * @author lbzhang
	 * @date 2012-12-27下午08:21:32
	 * @param wi
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static WfVotingEventAudit getWfVoteWithWorkItem(WorkItem wi) throws WTException {
		WfVotingEventAudit wfVoteEvent = null;
		QuerySpec qs = new QuerySpec();
		int wfvoteIdx = qs.addClassList(WfVotingEventAudit.class, true);

		qs.setAdvancedQueryEnabled(true);
		qs.appendWhere(new SearchCondition(WfVotingEventAudit.class, "theWorkItemReference.key.id",
				SearchCondition.EQUAL, Util.getLongOid(wi)), wfvoteIdx);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			Persistable[] ps = (Persistable[]) qr.nextElement();
			wfVoteEvent = (WfVotingEventAudit) ps[0];
		}
		return wfVoteEvent;
	}

	/**
	 * 根据wfprocess获取WfVotingEventAudit
	 *
	 * @author qianlong
	 * @date 2013-4-23
	 * @param process
	 * @return
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static QueryResult getWfVoteWithProcess(WfProcess process) throws WTException {

		QuerySpec qs = new QuerySpec();
		int wfpIdx = qs.appendClassList(WfProcess.class, false);
		int wfaIdx = qs.appendClassList(WfAssignedActivity.class, false);
		int wfiIdx = qs.appendClassList(WorkItem.class, false);
		int wfvIdx = qs.appendClassList(WfVotingEventAudit.class, true);

		qs.setAdvancedQueryEnabled(true);
		qs.appendWhere(new SearchCondition(WfProcess.class, "thePersistInfo.theObjectIdentifier.id",
				SearchCondition.EQUAL, Util.getLongOid(process)), wfpIdx);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id", WfProcess.class,
				"thePersistInfo.theObjectIdentifier.id"), wfaIdx, wfpIdx);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", WfAssignedActivity.class,
				"thePersistInfo.theObjectIdentifier.id"), wfiIdx, wfaIdx);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WfVotingEventAudit.class, "theWorkItemReference.key.id", WorkItem.class,
				"thePersistInfo.theObjectIdentifier.id"), wfvIdx, wfiIdx);
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WfVotingEventAudit.class, WfVotingEventAudit.CREATE_TIMESTAMP),
				false));

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		return qr;
	}

	/**
	 * 查看当前人员是属于哪个工艺组的人员
	 *
	 * @author lbzhang
	 * @date 2013-1-28下午05:05:28
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public static String getTheTeam(Object obj, Object self) throws WTException {

		WfProcess wf = getWfProcessBySelf(self);
		WTPrincipalReference prinRef = wf.getCreator();
		WTPrincipal prin = (WTPrincipal) prinRef.getObject();
		GLLogger.debug("the current user is :" + prin.getName());
		ArrayList<String> orgrole = new ArrayList<String>();
		orgrole.add("MACHINE");
		orgrole.add("HEATWORKER");
		orgrole.add("FACEDISPOSE");
		orgrole.add("MATERIAL");
		orgrole.add("PRINTBOARD");
		orgrole.add("LOADJOIN");
		orgrole.add("MICROELECTRONIC");

		if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			WTContainer container = part.getContainer();
			ContainerTeam containerTeam = getContainerTeam(container);
			Vector<Role> vec = containerTeam.getRoles();

			for (int i = 0; i < vec.size(); i++) {
				Role roleTemp = vec.get(i);
				GLLogger.debug("roleTemp===>" + roleTemp.getDisplay());
				Enumeration<WTPrincipalReference> eprin = containerTeam.getPrincipalTarget(roleTemp);
				while (eprin.hasMoreElements()) {
					WTPrincipal temp = eprin.nextElement().getPrincipal();
					if (temp != null && temp.equals(prin)) {
						String rolename = roleTemp.getStringValue();
						rolename = rolename.substring(rolename.lastIndexOf(".") + 1, rolename.length());
						GLLogger.debug("rolestr===>" + rolename);
						if (orgrole.contains(rolename)) {
							return rolename;
						}
					}
				}
			}
			return "";
		} else {
			return "";
		}
	}

	/**
	 * 获取专案团
	 *
	 * @author qianlong
	 * @date 2013-4-17
	 * @return
	 *
	 */
	public static ContainerTeam getContainerTeam(WTContainer container) {
		ContainerTeam containerTeam = null;
		if (container instanceof PDMLinkProduct) {
			PDMLinkProduct product = (PDMLinkProduct) container;
			containerTeam = (ContainerTeam) product.getContainerTeamReference().getObject();
		} else if (container instanceof WTLibrary) {
			WTLibrary library = (WTLibrary) container;
			containerTeam = (ContainerTeam) library.getContainerTeamReference().getObject();
		}
		return containerTeam;
	}

	/**
	 * 获取用户的workiteml
	 *
	 * @author tfwang
	 * @date 2013-7-24
	 * @param user
	 * @param nameList
	 * @param status
	 * @return
	 * @throws WTException
	 * @throws IOException
	 *
	 */
	@SuppressWarnings("deprecation")
	public static QuerySpec getListWorkItem(WTUser user, String[] nameList, String[] status) throws WTException,
			IOException {
		QuerySpec qs = new QuerySpec();
		int wfpIdx = qs.appendClassList(WfProcess.class, false);
		int wfaIdx = qs.appendClassList(WfAssignedActivity.class, false);
		int wfiIdx = qs.appendClassList(WorkItem.class, true);

		qs.appendWhere(new SearchCondition(WorkItem.class, "ownership.owner.key.id", SearchCondition.EQUAL, user
				.getPersistInfo().getObjectIdentifier().getId()), new int[] { wfiIdx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", WfAssignedActivity.class,
				"thePersistInfo.theObjectIdentifier.id"), wfiIdx, wfaIdx);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id", WfProcess.class,
				"thePersistInfo.theObjectIdentifier.id"), wfaIdx, wfpIdx);
		qs.appendAnd();
		qs.setAdvancedQueryEnabled(true);
		qs.appendWhere(new SearchCondition(new ClassAttribute(WfProcess.class, "template.key.id"), SearchCondition.IN,
				new SubSelectExpression(getWfProcessQuerySpec(nameList))), new int[] { wfpIdx });
		if (status != null && status.length > 0) {
			// GLLogger.debug("---hiiii status==>>>" + status[0]);
			if (status[0] == null || status[0].trim().length() == 0)
				return qs;
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(new ClassAttribute(WorkItem.class, "status"), SearchCondition.NOT_IN,
					new ArrayExpression(status)), new int[] { wfiIdx });
		}
		GLLogger.debug("qs======" + qs);
		return qs;
	}

	/**
	 * 获取查询符合名称的流程模板ID的SQL
	 *
	 * @author yqliu
	 * @date 2012-6-18
	 * @modify xzheng
	 * @param nameList
	 * @return
	 * @throws QueryException
	 * @throws VersionControlException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static QuerySpec getWfProcessQuerySpec(String[] nameList) throws QueryException, VersionControlException,
			IOException {
		Class wftemClass = WfProcessTemplate.class;
		QuerySpec qs = new QuerySpec();
		int wftemIdx = qs.addClassList(WfProcessTemplate.class, false);

		qs.appendSelect(new ClassAttribute(wftemClass, "thePersistInfo.theObjectIdentifier.id"), true);
		qs.setAdvancedQueryEnabled(true);
		ClassAttribute classAttribute = new ClassAttribute(wftemClass, WfProcessTemplate.NAME);

		ArrayExpression arrayExpression = new ArrayExpression(nameList);
		qs
				.appendWhere(new SearchCondition(classAttribute, SearchCondition.IN, arrayExpression),
						new int[] { wftemIdx });
		GLLogger.debug("subQuery====" + qs);
		return qs;
	}

	public static void resertBOM_lifecycle(String partNumber) throws WTException{
		WTPart part=WTPartUtil.getLatestPartByNumberAndView(partNumber, "Design");
		if(part==null){
			System.out.println("part==null"+ partNumber);
			return;
		}
		resetBOMLc(part);
	}
	/**
	 *判断延迟回收流程是否在提交环节
	* @author jyx
	* @date 2018-5-15
	* @param oid
	* @throws WTException
	 */
	public static boolean isSubmitDelayByWfProcessOid(String oid){
		 Persistable obj = null;
		try {
			obj = ReferenceFactory.getObjectbyOid(oid);
			WfProcess wfProcess = null;
			if(obj != null){
				if (obj instanceof WfProcess) {
					wfProcess = (WfProcess) obj;
				}
			}
			if(wfProcess != null){
				//获取工作流活动
				Enumeration<?> enums =WfEngineHelper.service.getProcessSteps(wfProcess, null);
				while(enums.hasMoreElements()) {
					WfActivity wfactivity = (WfActivity) enums.nextElement();
					if (wfactivity instanceof WfAssignedActivity) {
						WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
						String activityName = wfassignedactivity.getName();
						String activityState = wfassignedactivity.getState().getDisplay();
						if((activityName.equals("提交")||activityName.equals("提交延迟申请") ||activityName.equals("发起遗失申请")) && activityState.equals("正在运行")){
							return true;
						}
					}

				}
			}
		}catch (WTException e) {
			e.printStackTrace();
		}
		 return false;
	}

	/**
	 *判断延迟回收流程是否在提交环节
	* @author jyx
	* @date 2018-5-15
	* @param oid
	* @throws WTException
	 */
	public static boolean isSubmitDelayByWfProcessOidAndActivityNames(String oid,String activityNames){
		 Persistable obj = null;
		try {
			obj = ReferenceFactory.getObjectbyOid(oid);
			WfProcess wfProcess = null;
			if(obj != null){
				if (obj instanceof WfProcess) {
					wfProcess = (WfProcess) obj;
				}
			}
			if(wfProcess != null){
				//获取工作流活动
				Enumeration<?> enums =WfEngineHelper.service.getProcessSteps(wfProcess, null);
				while(enums.hasMoreElements()) {
					WfActivity wfactivity = (WfActivity) enums.nextElement();
					if (wfactivity instanceof WfAssignedActivity) {
						WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
						String activityName = wfassignedactivity.getName();
						String activityState = wfassignedactivity.getState().getDisplay();
						if(activityName.equals(activityNames) && activityState.equals("正在运行")){
							return true;
						}
					}

				}
			}
		}catch (WTException e) {
			e.printStackTrace();
		}
		 return false;
	}
	/**
	 * 判断延迟流程提交环节完成任务时，延迟原因和延迟时间是否全部已经填写
	* @author jyx
	* @date 2018-5-15
	* @param oid
	* @return
	 * @throws WTException
	 */
	public static String checkDelayWfProcessOid(ObjectReference self) throws WTException{
		WfProcess process = null;
		Object obj =self.getObject();
		if (obj instanceof WfProcess) {
			process = (WfProcess) obj;
		} else if (obj instanceof WfActivity) {
			WfActivity wfActivity = (WfActivity) obj;
			process = wfActivity.getParentProcess();
		}else if (obj instanceof WfAssignedActivity) {
			WfAssignedActivity wfActivity = (WfAssignedActivity) obj;
			process = wfActivity.getParentProcess();
		}
		if(process != null ){
			String infor = (String)process.getContext().getValue("infor");
			System.out.println("infor------>"+infor);
			Boolean isFromRecover = (Boolean)process.getContext().getValue("isFromRecover");
			List<String> list = new ArrayList<String>();
			String[] temp = infor.split(":");
			for (String string : temp) {
				list.add(string);
			}
			List<String> list2 = PrintUserCodeProcessor.queryBarTableIDByDelay(list, isFromRecover);
			List<CmPrintRecordInfoBean> cmPrintRecordInfoBeans = PrintUserCodeProcessor.queryStoreTableByDelay(list2, isFromRecover);
			if(cmPrintRecordInfoBeans != null){
				for (CmPrintRecordInfoBean cmPrintRecordInfoBean : cmPrintRecordInfoBeans) {
					String delayDate = CommonUtil.objectToString(cmPrintRecordInfoBean.getDelayDate());
					String delayReason = CommonUtil.objectToString(cmPrintRecordInfoBean.getDelayReason());
					if("".equals(delayDate)||"".equals(delayReason)){
						return "NOPASS";
					}
				}
			}
		}else{
			System.out.println("process=null------>");
		}
		return "";
	}
	/**
	 * 判断遗失流程提交环节完成任务时，遗失原因是否全部已经填写
	* @author jyx
	* @date 2018-5-15
	* @param oid
	* @return
	 * @throws WTException
	 */
	public static String checkLoseWfProcessOid(ObjectReference self) throws WTException{
		WfProcess process = null;
		Object obj =self.getObject();
		if (obj instanceof WfProcess) {
			process = (WfProcess) obj;
		} else if (obj instanceof WfActivity) {
			WfActivity wfActivity = (WfActivity) obj;
			process = wfActivity.getParentProcess();
		}else if (obj instanceof WfAssignedActivity) {
			WfAssignedActivity wfActivity = (WfAssignedActivity) obj;
			process = wfActivity.getParentProcess();
		}
		if(process != null ){
			String infor = (String)process.getContext().getValue("infor");
			List<String> list = new ArrayList<String>();
			String[] temp = infor.split(":");
			for (String string : temp) {
				list.add(string);
			}
			List<CmPrintRecordInfoBean> cmPrintRecordInfoBeans = PrintUserCodeProcessor.queryLoseInfo(list);
			if(cmPrintRecordInfoBeans != null){
				for (CmPrintRecordInfoBean cmPrintRecordInfoBean : cmPrintRecordInfoBeans) {
					String loseReason = CommonUtil.objectToString(cmPrintRecordInfoBean.getLoseReason());
					if("".equals(loseReason)){
						return "NOPASS";
					}
				}
			}
		}
		return "";
	}

	/**
	 * 判断延迟流程确认环节完成任务时，状态是否全部是回收中
	* @author jyx
	* @date 2018-5-15
	* @param oid
	* @return
	 * @throws WTException
	 */
	public static String checkDelayStateWfProcessOid(ObjectReference self) throws WTException{
		WfProcess process = null;
		Object obj =self.getObject();
		if (obj instanceof WfProcess) {
			process = (WfProcess) obj;
		} else if (obj instanceof WfActivity) {
			WfActivity wfActivity = (WfActivity) obj;
			process = wfActivity.getParentProcess();
		}else if (obj instanceof WfAssignedActivity) {
			WfAssignedActivity wfActivity = (WfAssignedActivity) obj;
			process = wfActivity.getParentProcess();
		}
		if(process != null ){
			String infor = (String)process.getContext().getValue("infor");
			System.out.println("infor------>"+infor);
			Boolean isFromRecover = (Boolean)process.getContext().getValue("isFromRecover");
			List<String> list = new ArrayList<String>();
			String[] temp = infor.split(":");
			for (String string : temp) {
				list.add(string);
			}
			List<String> list2 = PrintUserCodeProcessor.queryBarTableIDByDelay(list, isFromRecover);
			List<CmPrintRecordInfoBean> cmPrintRecordInfoBeans = PrintUserCodeProcessor.queryStoreTableByDelay(list2, isFromRecover);
			if(cmPrintRecordInfoBeans != null){
				for (CmPrintRecordInfoBean cmPrintRecordInfoBean : cmPrintRecordInfoBeans) {
					String middleStatus = CommonUtil.objectToString(cmPrintRecordInfoBean.getMiddleStatus());
					if(!"回收中".equals(middleStatus)){
						return "NOPASS";
					}
				}
			}
		}else{
			System.out.println("process=null------>");
		}
		return "";
	}
	/**
	 * 判断遗失流程确认环节完成任务时，状态是否全部是回收中
	* @author jyx
	* @date 2018-5-15
	* @param oid
	* @return
	 * @throws WTException
	 */
	public static String checkLoseStateWfProcessOid(ObjectReference self) throws WTException{
		WfProcess process = null;
		Object obj =self.getObject();
		if (obj instanceof WfProcess) {
			process = (WfProcess) obj;
		} else if (obj instanceof WfActivity) {
			WfActivity wfActivity = (WfActivity) obj;
			process = wfActivity.getParentProcess();
		}else if (obj instanceof WfAssignedActivity) {
			WfAssignedActivity wfActivity = (WfAssignedActivity) obj;
			process = wfActivity.getParentProcess();
		}
		if(process != null ){
			String infor = (String)process.getContext().getValue("infor");
			List<String> list = new ArrayList<String>();
			String[] temp = infor.split(":");
			for (String string : temp) {
				list.add(string);
			}
			List<CmPrintRecordInfoBean> cmPrintRecordInfoBeans = PrintUserCodeProcessor.queryLoseInfo(list);
			if(cmPrintRecordInfoBeans != null){
				for (CmPrintRecordInfoBean cmPrintRecordInfoBean : cmPrintRecordInfoBeans) {
					String middleStatus = CommonUtil.objectToString(cmPrintRecordInfoBean.getMiddleStatus());
					if(!"回收中".equals(middleStatus)){
						return "NOPASS";
					}
				}
			}
		}
		return "";
	}
	/**
	 * 判断封存流程确认环节完成任务时，状态是否全部是已封存
	* @author jyx
	* @date 2018-5-15
	* @param oid
	* @return
	 * @throws WTException
	 */
	public static String checkStorestateWfProcess(ObjectReference self) throws WTException{
		WfProcess process = null;
		Object obj =self.getObject();
		if (obj instanceof WfProcess) {
			process = (WfProcess) obj;
		} else if (obj instanceof WfActivity) {
			WfActivity wfActivity = (WfActivity) obj;
			process = wfActivity.getParentProcess();
		}else if (obj instanceof WfAssignedActivity) {
			WfAssignedActivity wfActivity = (WfAssignedActivity) obj;
			process = wfActivity.getParentProcess();
		}
		if(process != null ){
			String infor = (String)process.getContext().getValue("infor");
			List<String> list = new ArrayList<String>();
			String[] temp = infor.split(":");
			for (String string : temp) {
				list.add(string);
			}
			List<CmPrintRecordInfoBean> cmPrintRecordInfoBeans = PrintUserCodeProcessor.queryStoreTable(list);
			if(cmPrintRecordInfoBeans != null){
				for (CmPrintRecordInfoBean cmPrintRecordInfoBean : cmPrintRecordInfoBeans) {
					String middleStatus = CommonUtil.objectToString(cmPrintRecordInfoBean.getMiddleStatus());
					if(!"已封存".equals(middleStatus)){
						return "NOPASS";
					}
				}
			}
		}
		return "";
	}
	public static void resetBOMLc(WTPart part) throws WTException{
		List<WTPart> parts=WTPartUtil.getChildPart(part);
		for(WTPart p:parts){
			System.out.println("p="+p.getNumber());
			LifeCycleState state=p.getState();
			LifeCycleTemplate lct=(LifeCycleTemplate)p.getLifeCycleTemplate().getObject();
			lct=(LifeCycleTemplate)VersionControlHelper.service.getLatestIteration(lct, true);
			LifeCycleManaged md=LifeCycleHelper.service.reassign(p, LifeCycleTemplateReference.newLifeCycleTemplateReference(lct));
			md=LifeCycleHelper.service.setLifeCycleState(md, state.getState());
			resetBOMLc(p);
		}
	}

	/**
	 * 重新设置EBOM生命周期
	 * @author lbzhang
	 * @date  2013-7-29
	 * @param part
	 * @throws WTException
	 *
	 */
	public static void resetBOM_lifecycle(WTPart part) throws WTException{
		resertBOM_lifecycle(part.getNumber());
	}

	public static String getWorkflowVarValue(ObjectReference self,WTObject pbo,String key) throws WTException {
		System.out.println("------key:"+key);
		MPMProcessPlan pplan = null;
		if(pbo instanceof MPMProcessPlan) {
			pplan = (MPMProcessPlan)pbo;
		} else if(pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 ecn = (WTChangeOrder2)pbo;
			QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(ecn);
            while (qResult.hasMoreElements()) {
            	Object object = qResult.nextElement();
            	if (object instanceof MPMProcessPlan) {
            		pplan = (MPMProcessPlan)object;
            	}else if(object instanceof WTDocument) {
            		WTDocument doc = (WTDocument)object;
            		try {
						pplan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(doc.getNumber());
					} catch (WTPropertyVetoException e) {
						e.printStackTrace();
					}
            	}
            }
		}
		if(pplan == null) {
			return "";
		}
		QueryResult qr = MPMProcessPlanHelper.service.getWTParts(pplan, NCServerHolder.makeForLatestConfigSpec());
		if(qr.hasMoreElements()) {
			WTPart part = (WTPart)qr.nextElement();
			String ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
			if("technicsOid".equals(key)) {
				WTDocument document = ProcessPlanHelper.getProcessZipDoc2(part,pplan.getNumber());
				if(document != null) {
					QueryResult qeuryResult = VersionControlHelper.service.allVersionsOf(document.getMaster());
					document = (WTDocument)qeuryResult.nextElement();
					String technicsOid = String.valueOf(PersistenceHelper.getObjectIdentifier(document).getId());
					return technicsOid;
				}
			} else if("topPartOid".equals(key)) {
				return ida2a2;
			} else if ("processType".equals(key)) {
				return Constants.normalProcess;
			}
		}
		return "";
	}
	/**
	 * 启动签审工作流
	 * @param ref
	 * @param persistable  PBO
	 * @param workflowName 工作流模板名称
	 * @throws WTException
	 * @throws IOException
	 */
	public static WfProcess startWfProcess(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startChangeRecoverWfProcess(WTContainerRef ref,Persistable persistable,String workflowName, String ecnOid, String docOid, String xhlx) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("ecnOid", ecnOid);
		processData.setValue("docOid", docOid);
		processData.setValue("isFromRecover", false);
		processData.setValue("xhlx", xhlx);
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startRecoverWfProcess(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("isFromRecover", true);
		processData.setValue("infor", infor);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startDelayWfProcessOfRecover(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		processData.setValue("isFromRecover", true);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startDelayWfProcessOfStore(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		processData.setValue("isFromRecover", false);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startSubmitDelayWfProcessByRecover(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		processData.setValue("isFromRecover", true);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startSubmitDelayWfProcessByStore(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		processData.setValue("isFromRecover", false);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startStoreWfProcess(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startOpenStoreWfProcess(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startOutTimeWfProcess(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startLoseWfProcessOfRecover(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		processData.setValue("isFromRecover", true);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startLoseWfProcessOfStore(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		processData.setValue("isFromStore", true);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	public static WfProcess startLoseWfProcess(WTContainerRef ref,Persistable persistable,String workflowName, Map<String, String> map, List<String> list) throws WTException, IOException{
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
		WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
		StringBuffer sb  = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size() -1){
				sb.append(list.get(i) + ":");
			}else{
				sb.append(list.get(i));
			}
		}
		String infor = sb.toString();
		ProcessData processData = wfProcess.getContext();
		processData.setValue("primaryBusinessObject", persistable);
		processData.setValue("infor", infor);
		for(String dataName : map.keySet()){
			processData.setValue(dataName, map.get(dataName));
		}
		WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
		SessionServerHelper.manager.setAccessEnforced(access);
		return wfProcess;
	}

	/**
	 * 获取关联的更改单
	 * @param per
	 * @return
	 * @throws ChangeException2
	 * @throws WTException
	 */
	public static List<WTChangeOrder2> getEcnByPersistable(Persistable per) throws ChangeException2, WTException{
		List<WTChangeOrder2> ecnList = new ArrayList<WTChangeOrder2>();

		//改后数据
		QueryResult ecrQr1 = ChangeHelper2.service.getChangingChangeActivities((Changeable2) per, false);
		while(ecrQr1.hasMoreElements()){
			ChangeRecord2 changeRecord2 = (ChangeRecord2) ecrQr1.nextElement();
			WTChangeActivity2 eca = (WTChangeActivity2) changeRecord2.getChangeActivity2();
			QueryResult ecnQr = ChangeHelper2.service.getChangeOrder(eca);
			while(ecnQr.hasMoreElements()){
				WTChangeOrder2 ecn = (WTChangeOrder2) ecnQr.nextElement();
				if(!ecnList.contains(ecn)){
					ecnList.add(ecn);
				}
			}
		}
		return ecnList;
	}

	public static String setTongZhiZhe(ObjectReference self, WTObject pbo, String roleKey) {
		String resultMsg = "ok";
		try {
			WfProcess process = (WfProcess)self.getObject();
			Role role = Role.toRole(roleKey);
			if(role == null) {
				resultMsg = "系统中不存在该角色：" + roleKey;
				return resultMsg;
			}
			List<WTUser> userList = new ArrayList<WTUser>();

			//getZhuRenGongYiShi(pbo, role, userList);
			getZhuRenGongYiShi(process, pbo, userList);
//			if(userList.size() == 0){
//				getZhuRenGongYiShi(pbo, role, userList);
//			}

			//如果怎么都获取不到，就给管理员
			if(userList == null || userList.isEmpty()) {
				WTUser admin = (WTUser) SessionHelper.manager.getAdministrator();
				userList.add(admin);
			}

			Team team = (Team) process.getTeamId().getObject();
			for (WTUser user : userList) {
				team.addPrincipal(role, user);
			}
			team = (Team) PersistenceHelper.manager.refresh(team);
			team = (Team) PersistenceHelper.manager.save(team);
		} catch (Exception e) {
			resultMsg = e.getLocalizedMessage();
			e.printStackTrace();
		}
		return resultMsg;
	}

	private static void getZhuRenGongYiShi(WfProcess process, WTObject pbo, List<WTUser> userList) throws Exception {
		String processName = process.getName();
		if(processName.contains(ext.casc.constants.Constants.WFN_WUJIPROCESSWF)
    			|| processName.contains(ext.casc.constants.Constants.WFN_PROCESS_ECN)
    			|| processName.contains(ext.casc.constants.Constants.WFN_SANJIPROCESSWF)
    			|| processName.contains(ext.casc.constants.Constants.WFN_SANJIGENGGAIWF)) {
			ProcessData processData = process.getContext();
			String taskItemOid = object2String(processData.getValue("taskOid"));
			if(taskItemOid != null && !"".equals(taskItemOid)) {
				ProcessTaskItem taskItem = ProcessUtil.getProcessTaskItem(Long.valueOf(taskItemOid));
				if(taskItem != null) {
					String zhuRenGongYiShi = taskItem.getZhurengongyishi();
					if(zhuRenGongYiShi != null) {
						WTUser user = UserUtil.getUser(zhuRenGongYiShi);
						userList.add(user);
					} else {
						IBAUtility ibaUtility = new IBAUtility(taskItem);
						String gongyizuzhang = ibaUtility.getIBAValue("GONGYIZUZHANG");
						if(gongyizuzhang != null){
							WTUser user = UserUtil.getUser(gongyizuzhang);
							userList.add(user);
						}
					}
				}
			} else {
				if(pbo instanceof WTChangeOrder2){
					WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
                    QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
                    String docNumber = null;
                    while (qResult.hasMoreElements()) {
                        Object object = qResult.nextElement();
                        if (object instanceof WTDocument) {
                            WTDocument doc = (WTDocument) object;
                            docNumber = doc.getNumber();
                        } else if (object instanceof MPMProcessPlan) {
                            MPMProcessPlan plan = (MPMProcessPlan) object;
                            docNumber = plan.getNumber();
                        }
                    }
                    if(docNumber != null && !docNumber.isEmpty()){
                        ProcessTaskItem processTaskItem = getProcesstaskItemByIBANumber(docNumber);
                        if(processTaskItem != null){
                            String zhuRenGongYiShi = processTaskItem.getZhurengongyishi();
                            if(zhuRenGongYiShi != null) {
                                WTUser user = UserUtil.getUser(zhuRenGongYiShi);
                                userList.add(user);
                            }else{
								IBAUtility ibaUtility = new IBAUtility(processTaskItem);
								String gongyizuzhang = ibaUtility.getIBAValue("GONGYIZUZHANG");
								if(gongyizuzhang != null){
									WTUser user = UserUtil.getUser(gongyizuzhang);
									userList.add(user);
								}
							}
                        }

                    }

				} else if(pbo instanceof WTDocument){
					WTDocument document = (WTDocument) pbo;
					ProcessTaskItem processTaskItem = getProcesstaskItemByIBANumber(document.getNumber());
					if(processTaskItem != null){
						String zhuRenGongYiShi = processTaskItem.getZhurengongyishi();
						if(zhuRenGongYiShi != null) {
							WTUser user = UserUtil.getUser(zhuRenGongYiShi);
							userList.add(user);
						}else{
							IBAUtility ibaUtility = new IBAUtility(processTaskItem);
							String gongyizuzhang = ibaUtility.getIBAValue("GONGYIZUZHANG");
							if(gongyizuzhang != null){
								WTUser user = UserUtil.getUser(gongyizuzhang);
								userList.add(user);
							}
						}
					}
				}
			}
		}
	}

    /**
     * 根据文档软属性PPNUMBER的值查询文档对象
     *
     * @param number
     * @return WTDocument
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     */
    public static ProcessTaskItem getProcesstaskItemByIBANumber(String number) throws WTException, WTPropertyVetoException, RemoteException {
        QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
        qs.setAdvancedQueryEnabled(true);
        int index[] = { 0 };

        ClassAttribute caId = new ClassAttribute(ProcessTaskItem.class, Persistable.PERSIST_INFO + "."
                + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
        SubSelectExpression subSelectExpression = getStringIBAQuery("PROCESSDOCNUM", number);
        qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);

        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        ProcessTaskItem processTaskItem = null;
        while (qr.hasMoreElements()) {
            processTaskItem = (ProcessTaskItem)qr.nextElement();
        }
       return processTaskItem;
    }
    /**
     * 构建根据String软属性查询的子查询语句
     *
     * @param ibaName
     * @param ibaValue
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     *
     */
    public static SubSelectExpression getStringIBAQuery(String ibaName, String ibaValue) throws WTException,
            WTPropertyVetoException, RemoteException {
        // 获取IBA属性定义
        AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
        if (addv == null)
            throw new IBADefinitionException("No IBA Definition: " + ibaName);
        long ibaDefId = addv.getObjectID().getId();
        QuerySpec qs = new QuerySpec();
        int idx = qs.appendClassList(StringValue.class, false);
        qs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx },
                false);
        qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL,
                ibaDefId), new int[] { idx });
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, ibaValue, false),
                new int[] { idx });
        return new SubSelectExpression(qs);
    }

	public static String object2String(Object object) {
		if(object == null || "".equals(object)) {
			return "";
		} else {
			return object.toString();
		}
	}

	private static List<WTUser> getZhuRenGongYiShi(WTObject pbo, Role role, List<WTUser> allUsers) throws WTException {
        WTContainer wfcont = null;
        if (pbo instanceof WTDocument) {
            WTDocument document = (WTDocument) pbo;
            wfcont = document.getContainer();
        } else if (pbo instanceof EPMDocument) {
            EPMDocument epmdoc = (EPMDocument) pbo;
            wfcont = epmdoc.getContainer();
        } else if (pbo instanceof WTPart) {
            WTPart part = (WTPart) pbo;
            wfcont = part.getContainer();
        } else if (pbo instanceof ManagedBaseline) {
            ManagedBaseline bl = (ManagedBaseline) pbo;
            wfcont = bl.getContainer();
        } else if (pbo instanceof ProcessEnvelope) {
            ProcessEnvelope processEnvelope = (ProcessEnvelope) pbo;
            wfcont = processEnvelope.getContainer();
        } else if (pbo instanceof MPMProcessPlan) {
            MPMProcessPlan processPlan = (MPMProcessPlan) pbo;
            wfcont = processPlan.getContainer();
        }

        if(wfcont != null) {
        	ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wfcont);
            List<WTPrincipalReference> list = containerTeam.getAllPrincipalsForTarget(role);
            for (WTPrincipalReference wtPrincipalReference : list) {
                WTPrincipal principal = (WTPrincipal) wtPrincipalReference.getObject();
                if (principal instanceof WTUser) {
                	allUsers.add((WTUser)principal);
                } else if (principal instanceof WTGroup) {
                    WTGroup group = (WTGroup) principal;
                    loopGroup(group, allUsers);
                }
            }
        }

        return allUsers;
    }

	private static void loopGroup(WTGroup group, List<WTUser> allUsers) throws WTException {
		Enumeration<?> enumeration = group.members();
		while (enumeration.hasMoreElements()) {
            WTPrincipal principal = (WTPrincipal) enumeration.nextElement();
            if(principal instanceof WTUser) {
            	allUsers.add((WTUser)principal);
            } else if (principal instanceof WTGroup) {
            	WTGroup tempGroup = (WTGroup)principal;
            	loopGroup(tempGroup, allUsers);
            }
        }
	}
}
