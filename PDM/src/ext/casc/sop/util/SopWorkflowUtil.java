package ext.casc.sop.util;

import com.glaway.mpm.constants.TypeNameConstants;
import com.glaway.mpm.intf.SopProcessEditorToWCIntfRMI;
import com.glaway.mpm.sop.util.SopProcessUtil;
import com.glaway.mpm.sop.util.SopXMLUtility;
import com.glaway.mpm.util.*;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.sop.bean.DocParametersLinkBean;
import ext.casc.sop.bean.DocSopLinkBean;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.process.StructureSopProcessPlan;
import ext.casc.workflow.PrintHelper;
import org.dom4j.Document;
import org.dom4j.Element;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.container.WTContainer;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfBallot;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.*;

public class SopWorkflowUtil {

	/**
	 * 更改流程工艺实例化表达式
	 *
	 * @param partOid
	 * @param docOid
	 * @throws WTException
	 */
	public static void structureSopChangeOrder(String partOid, String docOid) throws Exception {
		WTPart wtPart = (WTPart) QueryUtil.getObjectByOid(WTPart.class, Long.valueOf(partOid));
		WTDocument wtDocument = (WTDocument) QueryUtil.getObjectByOid(WTDocument.class, Long.valueOf(docOid));
		StructureSopProcessPlan structure = new StructureSopProcessPlan(wtDocument, wtPart);
		structure.structure();

	}

	/**
	 * SOP工艺实例化表达式
	 *
	 * @param pbo
	 * @param partOid
	 * @throws WTException
	 */
	public static void structureSopProcessPlan(Object pbo, String partOid) throws Exception {
		WTPart wtPart = (WTPart) QueryUtil.getObjectByOid(WTPart.class, Long.valueOf(partOid));
		StructureSopProcessPlan structure = new StructureSopProcessPlan((WTDocument) pbo, wtPart);
		structure.structure();
	}

	/**
	 * 判断是否是基于SOP发起的更改单
	 *
	 * @param pbo
	 * @return isSop
	 */
	public static boolean isSop(Object pbo) throws WTException {
		boolean isSop = false;
		try {
			if (pbo != null && pbo instanceof WTChangeOrder2) {
				WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
				QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
				String type = "";
				if (qResult.hasMoreElements()) {
					Object object = qResult.nextElement();
					if (object instanceof WTDocument) {
						WTDocument document = (WTDocument) object;
						type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
						if (type.contains(SopConstants.SOP_TYPE_SOPDOC)) {
							isSop = true;
						}
					} else if (object instanceof WTPart) {
						WTPart part = (WTPart) object;
						type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(part);
						if (type.contains(SopConstants.SOP_TYPE_SOPPART)) {
							isSop = true;
						}
					} else if (object instanceof MPMProcessPlan) {
						MPMProcessPlan processPlan = (MPMProcessPlan) object;
						type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(processPlan);
						if (type.contains(SopConstants.SOP_TYPE_SOPPROCESSPLAN)) {
							isSop = true;
						}
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return isSop;

	}

	/**
	 * 保存工艺与SOP文件关联关系
	 *
	 * @param pbo
	 * @throws Exception
	 */
	public static void saveTecSopLinks(Object pbo) throws Exception {
		System.out.println("=========>>>>>>>>saveTecSopLinks starts");
		boolean isSop = false;
		WTDocument document = null;
		if (pbo instanceof WTDocument) {
			document = (WTDocument) pbo;
		} else if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 order2 = (WTChangeOrder2) pbo;
			QueryResult result = ChangeHelper2.service.getChangeablesAfter(order2);
			while (result.hasMoreElements()) {
				Object object = result.nextElement();
				if (object instanceof WTDocument) {
					document = (WTDocument) object;

				}
			}
		}
		String docType1 = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
		if (docType1.contains(SopConstants.SOP_TYPE_SOPDOC)) {
			isSop = true;
		}
		if (document != null && !isSop) {
			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
			if (docType.contains("casc.sast.149.PROCESS_PLAN") && !docType.contains("casc.sast.149.reportTechnics")) {
				DocSopLinkBean linkBean;
				List<DocSopLinkBean> linkBeanList = new ArrayList<DocSopLinkBean>();
				// 获取SOP文件
				byte[] bytes = null;
				ApplicationData data;
				data = WTDocumentUtil.getPrimaryByDocument(document);
				bytes = WTDocumentUtil.applicationDataToByte(data);
				String fileName = data.getFileName();
				if (fileName.toLowerCase().endsWith(".zip")) {
					fileName = fileName.substring(0, fileName.length() - 4);
				}
				String filepath = PropertiesUtil.getTempPath() + File.separator + fileName;
				File dir = new File(filepath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				ZipUtil.unZip(bytes, filepath);
				File xmlFile = new File(filepath + File.separator + fileName + ".xml");
				Document doc = XmlUtility.getDocument(xmlFile);
				Element rootElement = doc.getRootElement();
				if(rootElement!=null){
					String technicNumber = ((Element) rootElement.elements().get(0)).attributeValue("pplanNumber");
					String technicNum = ((Element) rootElement.elements().get(0)).attributeValue("technicsNumber");
					String technicsName = ((Element) rootElement.elements().get(0)).attributeValue("technicsName");
					String productName = ((Element) rootElement.elements().get(0)).attributeValue("productName");
					String technicsVersion = VersionControlHelper.getVersionIdentifier(document).getValue();
					Element technics = rootElement.element("QMFawTechnicsInfo");
					Element steps = technics.element("steps");
					if(steps!=null){
						List<Element> step = steps.elements();
						for (Element stp : step) {
							String bsOid = stp.attributeValue("bsoID");
							String stepNumber = stp.attributeValue("stepNumber");
							String stepName = stp.attributeValue("stepName");
							Element sops = stp.element("sops");
							if(sops!=null){
								List<Element> sopList = sops.elements();
								if (sopList != null && sopList.size() > 0) {
									Element sop = sopList.get(0);
									String ppnumber = sop.attributeValue("ppnumber");
									String number = sop.attributeValue("number");
									String name = sop.attributeValue("name");
									linkBean = new DocSopLinkBean();
									linkBean.setDocNumber(technicNumber);
									linkBean.setDocNum(technicNum);
									linkBean.setDocName(technicsName);
									linkBean.setDocContainer(productName);
									linkBean.setDocVersion(technicsVersion);
									linkBean.setDocStepName(stepName);
									linkBean.setDocStepNumber(stepNumber);
									linkBean.setDodStepBsOid(bsOid);
									linkBean.setSopNumber(ppnumber);
									linkBean.setSopName(name);
									linkBean.setSopNum(number);
									WTDocument sopDoc = WTDocumentUtil.getDocumentByNumber(number);
									String sopVersion = VersionControlHelper.getVersionIdentifier(sopDoc).getValue();
									linkBean.setSopVersion(sopVersion);
									linkBeanList.add(linkBean);
								}
							}
							Element pace = stp.element("paces");
							if(pace!=null){
								List<Element> paces = pace.elements();
								for (Element pae : paces) {
									String paebsOid = pae.attributeValue("bsoID");
									String paestepNumber = pae.attributeValue("stepNumber");
									String paestepName = pae.attributeValue("stepName");
									Element paesops = pae.element("sops");
									if(paesops!=null){
										List<Element> paesopList = paesops.elements();
										if (paesopList != null && paesopList.size() > 0) {
											Element sop = paesopList.get(0);
											String ppnumber = sop.attributeValue("ppnumber");
											String number = sop.attributeValue("number");
											String name = sop.attributeValue("name");
											linkBean = new DocSopLinkBean();
											linkBean.setDocNumber(technicNumber);
											linkBean.setDocNum(technicNum);
											linkBean.setDocName(technicsName);
											linkBean.setDocContainer(productName);
											linkBean.setDocVersion(technicsVersion);
											linkBean.setDocStepName(paestepName);
											linkBean.setDocStepNumber(paestepNumber);
											linkBean.setDodStepBsOid(paebsOid);
											linkBean.setSopNumber(ppnumber);
											linkBean.setSopName(name);
											linkBean.setSopNum(number);
											WTDocument sopDoc = WTDocumentUtil.getDocumentByNumber(number);
											if(sopDoc!=null) {
												String sopVersion = VersionControlHelper.getVersionIdentifier(sopDoc).getValue();
												linkBean.setSopVersion(sopVersion);
												linkBeanList.add(linkBean);
											}
										}
									}
								}
							}
						}
						saveDocSopLinksToDB(linkBeanList);
					}
				}
			}
		}
	}

	private static void saveDocSopLinksToDB(List<DocSopLinkBean> linkBeanList) {
		DBConnUtil dbConnUtil;
		try {
			if (linkBeanList != null && linkBeanList.size() > 0) {
				String number = linkBeanList.get(0).getDocNumber();
				String version = linkBeanList.get(0).getDocVersion();
				deleteDocSopLink(number, version);
				StringBuilder sb;
				dbConnUtil = new DBConnUtil();
				for (DocSopLinkBean linkBean : linkBeanList) {
					String docNumber = linkBean.getDocNumber();
					String docNum = linkBean.getDocNum();
					String docName = linkBean.getDocName();
					String docContainer = linkBean.getDocContainer();
					String docVersion = linkBean.getDocVersion();
					String docStepNmuber = linkBean.getDocStepNumber();
					String docStepName = linkBean.getDocStepName();
					String docStepBsOid = linkBean.getDodStepBsOid();
					String sopNumber = linkBean.getSopNumber();
					String sopName = linkBean.getSopName();
					String sopNum = linkBean.getSopNum();
					String sopVersion = linkBean.getSopVersion();
					sb = new StringBuilder(
							"insert into GL_SOPDOCLINK (DOCNUMBER,DOCNUM,DOCName,DOCCONTAINER,DOCVERSION,DOCSTEPNUMBER,DOCSTEPNAME,DOCSTEPBSOID,SOPNUMBER,SOPNAME,SOPNUM,SOPVERSION) values ");
					/*
					 * DOCNUMBER VARCHAR2(100), DOCName VARCHAR2(100),
					 * DOCCONTAINER VARCHAR2(100), DOCVERSION VARCHAR2(50),
					 * DOCSTEPNUMBER VARCHAR2(50), DOCSTEPNAME VARCHAR2(50),
					 * DOCSTEPBSOID VARCHAR2(100), SOPNUMBER VARCHAR2(100),
					 * SOPNUM VARCHAR2(100), SOPVERSION VARCHAR2(50)
					 */
					sb.append("('");
					sb.append(docNumber).append("','");
					sb.append(docNum).append("','");
					sb.append(docName).append("','");
					sb.append(docContainer).append("','");
					sb.append(docVersion).append("','");
					sb.append(docStepNmuber).append("','");
					sb.append(docStepName).append("','");
					sb.append(docStepBsOid).append("','");
					sb.append(sopNumber).append("','");
					sb.append(sopName).append("','");
					sb.append(sopNum).append("','");
					sb.append(sopVersion).append("'");
					sb.append(")");
					dbConnUtil.executeQuery(sb.toString());
				}
				dbConnUtil.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void deleteDocSopLink(String technicsNumber, String technicsVersion) {
		DBConnUtil dbConnUtil = null;
		try {
			dbConnUtil = new DBConnUtil();
			String sql = "delete from GL_SOPDOCLINK where DOCNUMBER='" + technicsNumber + "' and DOCVERSION='" + technicsVersion + "'";
			dbConnUtil.executeQuery(sql);
			dbConnUtil.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dbConnUtil != null) {
					dbConnUtil.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 方法功能:保存工艺与参数项目/国家标准关联关系
	 *
	 * @param pbo
	 * @return void
	 * @author LB
	 * @date 2019/12/23
	 */
	// TODO-SOP:签审流程和更改流程都需要添加表达式
	public static void saveDocParametersLinks(Object pbo) throws Exception {
		System.out.println("=======saveDocParametersLinks====start");
		WTDocument wtDocument = null;
		if (pbo instanceof WTDocument) {
			wtDocument = (WTDocument) pbo;
		} else if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			while (qResult.hasMoreElements()) {
				Object object = qResult.nextElement();
				if (object instanceof WTDocument) {
					wtDocument = (WTDocument) object;
				}
			}
		}
		if (wtDocument != null) {
			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtDocument);
			if (docType.contains(SopConstants.SOP_TYPE_SOPDOC)) {
				DocParametersLinkBean linkBean;
				List<DocParametersLinkBean> linkBeanList = new ArrayList<DocParametersLinkBean>();
				long docOid = wtDocument.getPersistInfo().getObjectIdentifier().getId();
				Element technicsElement = SopUtil.getTechnicsElement(wtDocument);
				String technicNumber = technicsElement.attributeValue("technicsNumber");
				String ppnumber = technicsElement.attributeValue("SopNumber");
				String technicName = technicsElement.attributeValue("technicsName");
				String tecType = "SOP";
				WTDocument document = WTDocumentUtil.getDocumentByNumber(technicNumber);
				String bianzhizhe = document.getModifierFullName();
				String technicsVersion = VersionControlHelper.getVersionIdentifier(wtDocument).getValue();
				// 获取参数项目
				Element sopElement = SopXMLUtility.getSOPElement(technicsElement);
				List<Element> parameterInfo = SopXMLUtility.getParameterInfo(sopElement);
				for (Element element : parameterInfo) {
					String isNew = element.attributeValue("isNew");
					String name = element.attributeValue("name");
					String SpecializedType = element.attributeValue("specializedType");
					String procedureName = element.attributeValue("procedureName");
					String materialCategory = element.attributeValue("materialCategory");
					Map<String, String> ibaMap = new HashMap<String, String>();
					ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, SpecializedType);
					ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME, procedureName);
					ibaMap.put(SopConstants.SOP_IBA_MATERIALCATEGORY, materialCategory);
					ibaMap.put(SopConstants.SOP_IBA_PARAMETERSNAME, name);
					linkBean = new DocParametersLinkBean();
					long ooid;
					if("true".equals(isNew)){
						List<WTPart> list = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, "", name, null, ibaMap, true, SopConstants.SOP_TYPE_PARAMETERS);
						if(!list.isEmpty()) {
							WTPart part = list.get(0);
							ooid = part.getPersistInfo().getObjectIdentifier().getId();
							linkBean.setParameterOid(ooid);
						}
					}else{
						String oid = element.attributeValue("oid");
						linkBean.setParameterOid(Long.valueOf(oid));
					}
					linkBean.setDocOid(docOid);
					linkBean.setTechnicsNumber(technicNumber);
					linkBean.setPpnumber(ppnumber);
					linkBean.setTechnicsName(technicName);
					linkBean.setTechnicsVersion(technicsVersion);
					linkBean.setType(SopConstants.SOP_TYPE_PARAMETERS);
					linkBean.setBianzhizhe(bianzhizhe);
					linkBean.setTecType("");
					linkBeanList.add(linkBean);
				}
				// 获取国家标准
				List<Element> borrowTechnics = XmlUtility.getBorrowTechnics(technicsElement);
				for (Element borrowTechnic : borrowTechnics) {
					String type = borrowTechnic.attributeValue("technicsType");
					String number = borrowTechnic.attributeValue("technicsNumber");
					String name = borrowTechnic.attributeValue("technicsName");
					if (type != null && "国家标准".equals(type)) {
						String oid = borrowTechnic.attributeValue("oid");
						linkBean = new DocParametersLinkBean();
						linkBean.setDocOid(docOid);
						linkBean.setParameterOid(Long.valueOf(oid));
						linkBean.setTechnicsNumber(technicNumber);
						linkBean.setPpnumber(ppnumber);
						linkBean.setTechnicsName(technicName);
						linkBean.setTechnicsVersion(technicsVersion);
						linkBean.setGistNumber(number);
						linkBean.setGistName(name);
						linkBean.setBianzhizhe(bianzhizhe);
						linkBean.setTecType(tecType);
						linkBean.setType(SopConstants.SOP_TYPE_GUOJIABIAOZHUN);
						linkBeanList.add(linkBean);
					}
				}
				saveLinksToDB(linkBeanList);
			}
		}
	}

	/**
	 * 方法功能:保存关联信息至数据库
	 *
	 * @param linkBeanList
	 * @return void
	 * @author LB
	 * @date 2019/12/26
	 */
	private static void saveLinksToDB(List<DocParametersLinkBean> linkBeanList) {
		DBConnUtil dbConnUtil = null;
		try {
			if (linkBeanList != null && linkBeanList.size() > 0) {
				long docOid = linkBeanList.get(0).getDocOid();
				String number = linkBeanList.get(0).getTechnicsNumber();
				String version = linkBeanList.get(0).getTechnicsVersion();
				deleteDocParametersLink(number, version);
				StringBuilder sb;
				dbConnUtil = new DBConnUtil();
				for (DocParametersLinkBean linkBean : linkBeanList) {
					long parameterOid = linkBean.getParameterOid();
					String technicsNumber = linkBean.getTechnicsNumber();
					String ppNumber = linkBean.getPpnumber();
					String technicsName = linkBean.getTechnicsName();
					String technicsVersion = linkBean.getTechnicsVersion();
					String objType = linkBean.getType();
					String gistNumber = linkBean.getGistNumber();
					String gistName = linkBean.getGistName();
					String bianzhizhe = linkBean.getBianzhizhe();
					String tecType = linkBean.getTecType();
					sb = new StringBuilder(
							"insert into GL_DOCPARAMETERSLINK (DOCOID,PARAMETEROID,TECHNICSNUMBER,PPNUMBER,TECHNICSNAME,TECHNICSVERSION,OBJTYPE,GISTNUMBER,GISTNAME,BIANZHIZHE,TECTYPE) values ");
					sb.append("(");
					sb.append(docOid).append(",");
					sb.append(parameterOid).append(",'");
					sb.append(technicsNumber).append("','");
					sb.append(ppNumber).append("','");
					sb.append(technicsName).append("','");
					sb.append(technicsVersion).append("','");
					sb.append(objType).append("','");
					sb.append(gistNumber).append("','");
					sb.append(gistName).append("','");
					sb.append(bianzhizhe).append("','");
					sb.append(tecType).append("'");
					sb.append(")");
					dbConnUtil.executeQuery(sb.toString());
				}
				dbConnUtil.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(dbConnUtil!=null)
					dbConnUtil.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void deleteDocParametersLink(String technicsNumber, String technicsVersion) {
		DBConnUtil dbConnUtil = null;
		try {
			dbConnUtil = new DBConnUtil();
			String sql = "delete from GL_DOCPARAMETERSLINK where TECHNICSNUMBER='" + technicsNumber + "' and TECHNICSVERSION='" + technicsVersion + "'";
			dbConnUtil.executeQuery(sql);
			dbConnUtil.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dbConnUtil != null) {
					dbConnUtil.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 方法功能:保存工艺与国家标准关联关系
	 *
	 * @param pbo
	 * @return void
	 * @author CJH
	 * @date 2019/12/23
	 */
	public static void saveDocGJBZLinks(Object pbo) throws Exception {
		System.out.println("=======saveDocGJBZLinks====start");
		WTDocument wtDocument = null;
		if (pbo instanceof WTDocument) {
			wtDocument = (WTDocument) pbo;
		} else if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			while (qResult.hasMoreElements()) {
				Object object = qResult.nextElement();
				if (object instanceof WTDocument) {
					wtDocument = (WTDocument) object;
				}
			}
		}
		System.out.println("wtDocument=================="+wtDocument.getName());
		if (wtDocument != null) {
			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtDocument);
			if (!docType.contains(SopConstants.SOP_TYPE_SOPDOC) && !docType.contains("casc.sast.149.reportTechnics")) {
				DocParametersLinkBean linkBean;
				List<DocParametersLinkBean> linkBeanList = new ArrayList<DocParametersLinkBean>();
				long docOid = wtDocument.getPersistInfo().getObjectIdentifier().getId();
				Element technicsElement = SopUtil.getTechnicsElement(wtDocument);
				String technicNumber = technicsElement.attributeValue("technicsNumber");
				String technicName = technicsElement.attributeValue("technicsName");
				WTDocument document = WTDocumentUtil.getDocumentByNumber(technicNumber);
				String bianzhizhe = document.getModifierFullName();
				String tecType = "GIST";
				String technicsVersion = VersionControlHelper.getVersionIdentifier(wtDocument).getValue();
				// 获取国家标准
				List<Element> borrowTechnics = XmlUtility.getBorrowTechnics(technicsElement);
				for (Element borrowTechnic : borrowTechnics) {
					String type = borrowTechnic.attributeValue("technicsType");
					String number = borrowTechnic.attributeValue("technicsNumber");
					String name = borrowTechnic.attributeValue("technicsName");
					if (type != null && "国家标准".equals(type)) {
						String oid = borrowTechnic.attributeValue("oid");
						linkBean = new DocParametersLinkBean();
						linkBean.setDocOid(docOid);
						linkBean.setParameterOid(Long.valueOf(oid));
						linkBean.setTechnicsNumber(technicNumber);
						linkBean.setPpnumber(technicNumber);
						linkBean.setTechnicsName(technicName);
						linkBean.setTechnicsVersion(technicsVersion);
						linkBean.setGistNumber(number);
						linkBean.setGistName(name);
						linkBean.setBianzhizhe(bianzhizhe);
						linkBean.setTecType(tecType);
						linkBean.setType(SopConstants.SOP_TYPE_GUOJIABIAOZHUN);
						linkBeanList.add(linkBean);
					}
				}
				saveLinksToDB(linkBeanList);
			}

		}
	}

	public static WfAssignedActivity getWfActivity(WTObject pbo, String activityName) throws WTException {
		WfAssignedActivity activity = null;
		QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
		WfProcess proc = null;
		while (qrProcs.hasMoreElements()) {
			WfProcess process = (WfProcess) qrProcs.nextElement();
			if (proc != null) {
				if (process.getStartTime().after(proc.getStartTime())) {
					proc = process;
				}
			} else {
				proc = process;
			}
		}
		if (proc == null) {
			return null;
		}
		List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
		activityList = PrintHelper.getActivities(proc, activityList);
		Iterator iterator = activityList.iterator();
		while (iterator.hasNext()) {
			WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
			String wfactivityName = wfactivity.getName();
			if (wfactivityName.equals(activityName)) {
				activity = wfactivity;
			}
		}
		return activity;
	}

	public static WTPrincipal getActivityPrincipal(WfAssignedActivity wfActivity) throws WTException {
		WTPrincipal principal = null;
		Enumeration en1 = wfActivity.getAssignments();
		Enumeration en2;
		while (en1.hasMoreElements()) {
			WfAssignment wfassignment = (WfAssignment) en1.nextElement();
			en2 = wfassignment.checkBallotStatus().elements();
			while (en2.hasMoreElements()) {
				WfBallot wfballot = (WfBallot) en2.nextElement();
				principal = wfballot.getVoter().getPrincipal();
			}
		}
		return principal;
	}

	/**
	 * 方法功能:流程中校验工艺文件关联的SOP文件要求值是否都填写
	 *
	 * @param pbo
	 * @return boolean
	 * @author LB
	 * @date 2019/12/27
	 */
	public static String checkYQZ(Object pbo) throws Exception {
		String msg = "";
		WTDocument wtDocument = null;
		if (pbo instanceof WTDocument) {
			wtDocument = (WTDocument) pbo;
		} else if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			while (qResult.hasMoreElements()) {
				Object object = qResult.nextElement();
				if (object instanceof WTDocument) {
					wtDocument = (WTDocument) object;
				}
			}
		}
		if (wtDocument != null) {
			Element technicsElement = SopUtil.getTechnicsElement(wtDocument);
			if (technicsElement != null) {
				msg = SopProcessUtil.checkYQZ(technicsElement);
			}
		}
		return msg;
	}

	/**
	 * @创建手动录入的参数项目
	 * @author chenjianhui
	 * @param pbo
	 * @throws Exception
	 */
	public static void saveInputParameters(Object pbo) throws Exception {
		System.out.println("=======saveInputParameters====start");
		WTDocument wtDocument = null;
		if (pbo instanceof WTDocument) {
			wtDocument = (WTDocument) pbo;
		} else if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			while (qResult.hasMoreElements()) {
				Object object = qResult.nextElement();
				if (object instanceof WTDocument) {
					wtDocument = (WTDocument) object;
				}
			}
		}
		if (wtDocument != null) {
			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtDocument);
			if (docType.contains(SopConstants.SOP_TYPE_SOPDOC)) {
				Element technicsElement = SopUtil.getTechnicsElement(wtDocument);
				Element sopElement = SopXMLUtility.getSOPElement(technicsElement);
				List<Element> parameterInfo = SopXMLUtility.getParameterInfo(sopElement);
				for (Element parameter : parameterInfo) {
					String isNew = parameter.attributeValue("isNew");
					if ("true".equals(isNew)) {
						String number = SopProcessEditorToWCIntfRMI.getLastestNumber(SopConstants.SOP_STR_CSXM, SopConstants.SOP_TYPE_PARAMETERS, "");
						String pName = parameter.attributeValue("name").trim();
						String zylb = parameter.attributeValue("specializedType").trim();
						String gxmc = parameter.attributeValue("procedureName").trim();
						String wzlb = parameter.attributeValue("materialCategory");
						String csz = parameter.attributeValue("canshuzhi");
						String desc = parameter.attributeValue("description");
						List<String> typeList = new ArrayList<String>();
						Map<String, String> ibaMap = null;
						WTContainer container = WTContainerUtil.getContainerByName(SopConstants.SOP_CONTAINER_GYZYK);
						// 新建物资类别
						MPMTooling wzlbTooling = null;
						String wzlbNumber = SopProcessEditorToWCIntfRMI.getLastestNumber(SopConstants.SOP_STR_WZLB, SopConstants.SOP_TYPE_MATERIALCATEGORY, "");
						ibaMap = new HashMap<String, String>();
						ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
						if(wzlb!=null && !"".equals(wzlb)){
							List<WTPart> wzlbList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, wzlb, null, ibaMap, true, SopConstants.SOP_TYPE_MATERIALCATEGORY);
							if (wzlbList == null || wzlbList.size() == 0) {
								String typeName = TypeNameConstants.getTypeName(SopConstants.SOP_STR_WZLB);
								PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.MPMRESOURCE_TYPE_TO_FOLDER_CONFIG_PATH);
								String folderPath = propertiesUtil.getProperty(typeName);
								wzlbTooling = MPMResourceUtil.createSOPTooling(wzlbNumber, wzlb, container, folderPath, typeName);
								if (wzlbTooling != null && ibaMap != null && ibaMap.size() > 0) {
									IBAHelper helper = new IBAHelper(wzlbTooling);
									helper.setIBAValue(wzlbTooling, ibaMap);
									typeList.add(SopConstants.SOP_STR_WZLB);
								}
							}
						}
						// 新建参数项目名称
						MPMTooling csxmmcTooling = null;
						String csxmmcNumber = SopProcessEditorToWCIntfRMI.getLastestNumber(SopConstants.SOP_STR_CSXMMC, SopConstants.SOP_TYPE_PARAMETERSNAME, "");
						ibaMap = new HashMap<String, String>();
						ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
						List<WTPart> csxmmcList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, pName, null, ibaMap, true, SopConstants.SOP_TYPE_PARAMETERSNAME);
						if (csxmmcList == null || csxmmcList.size() == 0) {
							String typeName = TypeNameConstants.getTypeName(SopConstants.SOP_STR_CSXMMC);
							PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.MPMRESOURCE_TYPE_TO_FOLDER_CONFIG_PATH);
							String folderPath = propertiesUtil.getProperty(typeName);
							csxmmcTooling = MPMResourceUtil.createSOPTooling(csxmmcNumber, pName, container, folderPath, typeName);
							if (csxmmcTooling != null && ibaMap != null && ibaMap.size() > 0) {
								IBAHelper helper = new IBAHelper(csxmmcTooling);
								helper.setIBAValue(csxmmcTooling, ibaMap);
								typeList.add(SopConstants.SOP_STR_CSXMMC);
							}
						}
						Transaction tx = null;
						try {
							tx = new Transaction();
							tx.start();
							MPMTooling tooling = null;
							// 参数项目：名称+专业类别+工序名称+物资类别+参数项目名称
							ibaMap = new HashMap<String, String>();
							ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, zylb);
							ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME, gxmc);
							ibaMap.put(SopConstants.SOP_IBA_PARAMETERSNAME, pName);
							List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, pName, null, ibaMap, true, SopConstants.SOP_TYPE_PARAMETERS);
							if (wtPartList == null || wtPartList.size() == 0) {
								ibaMap.put(SopConstants.SOP_ATTR_REMARK, desc);
								String wuzi = wzlb;
								if (wuzi != null) {
									wuzi = wuzi.trim();
									ibaMap.put(SopConstants.SOP_IBA_MATERIALCATEGORY, wuzi);
								}
								String canshuzhi = csz;
								if (canshuzhi != null) {
									canshuzhi = canshuzhi.trim();
									ibaMap.put(SopConstants.SOP_IBA_CANSHUZHI, canshuzhi);
								}
								String typeName = TypeNameConstants.getTypeName(SopConstants.SOP_STR_CSXM);
								PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.MPMRESOURCE_TYPE_TO_FOLDER_CONFIG_PATH);
								String folderPath = propertiesUtil.getProperty(typeName);
								tooling = MPMResourceUtil.createSOPTooling(number, pName, container, folderPath, typeName);
								if (tooling != null && ibaMap != null && ibaMap.size() > 0) {
									IBAHelper helper = new IBAHelper(tooling);
									helper.setIBAValue(tooling, ibaMap);
									typeList.add(SopConstants.SOP_STR_CSXM);
								}
							}
							tx.commit();
							tx = null;
						} finally {
							if (tx != null) {
								tx.rollback();
							}
							for (String sopZYType : typeList) {
								SopUtil.getSopZYSeqNumber(2, sopZYType);
							}
						}
					}
				}
			}

		}
	}
}
