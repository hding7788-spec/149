package com.glaway.mpm.processplan.helper;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.*;

import com.glaway.mpm.print.constants.PrintConstants;
import ext.casc.access.AccessAdminUtil;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import wt.associativity.NCServerHolder;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.URLFactory;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.constants.AttributeConstants;
import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.constants.TypeNameConstants;
import com.glaway.mpm.constants.WorkflowConstants;
import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.model.CheckTechnics;
import com.glaway.mpm.model.Equipment;
import com.glaway.mpm.model.Material;
import com.glaway.mpm.model.PaceObject;
import com.glaway.mpm.model.StepObject;
import com.glaway.mpm.model.Tool;
import com.glaway.mpm.pbom.helper.PBOMHelper;
import com.glaway.mpm.processplan.ProcessPlanStructure;
import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.glaway.mpm.util.WorkInProcessUtil;
import com.glaway.mpm.util.WorkflowUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMResourceHelper;
import com.ptc.windchill.mpml.resource.MPMTooling;

import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;

public class ProcessPlanHelper {
	public static void main(String[] args) {
		WTPart newpart = (WTPart)searchLatestIteratedByNumberVersionView(WTPart.class,"96.SD08-1A(X)/00-20ZZ","space","Manufacturing");
		System.out.println(newpart.getVersionInfo().getIdentifier().getValue());
		System.out.println(newpart.getIterationInfo().getIdentifier().getValue());

	}
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);
	public static Iterated searchLatestIteratedByNumberVersionView(Class klass, String number, String version,String viewname) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number), new int[1]);
            if(version!=null&&!"".equals(version)){
            	qs.appendAnd();
            	qs.appendWhere(new SearchCondition(klass, "versionInfo.identifier.versionId", "=", version), new int[1]);
            }
            if ((viewname != null) && (!viewname.equals(""))) {
                qs.appendAnd();
                View view = ViewHelper.service.getView(viewname);
                qs.appendWhere(
                        new SearchCondition(WTPart.class, "view.key.id",
                                "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            }

            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = VersionControlHelper.getLatestIteration((Iterated) qr.nextElement(), true);
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }

        return null;
    }
	public static QueryResult searchAllIteratedByNumberVersionView(Class klass, String number, String version,String viewname) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number), new int[1]);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(klass, "versionInfo.identifier.versionId", "=", version), new int[1]);

            if ((viewname != null) && (!viewname.equals(""))) {
                qs.appendAnd();
                View view = ViewHelper.service.getView(viewname);
                qs.appendWhere(
                        new SearchCondition(WTPart.class, "view.key.id",
                                "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            }
            ClassAttribute ca = new ClassAttribute(klass, "iterationInfo.identifier.iterationId");
            OrderBy orderby = new OrderBy(ca, true);
            qs.appendOrderBy(orderby, 0);
            //qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            return qr;
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

	// 正常工艺不考虑多个，考虑升版本
	/**
	 * 获取最新的工艺压缩包文件
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param part
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static List<WTDocument> getProcessZipDoc(WTPart part, String processZipDocName, String technicsType, String processType)
			throws WTRuntimeException, WTException, WTPropertyVetoException {
		String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
		String doctype = null;
		if(technicsType.equals(LoadConfig.getInstance().getReportTechnicsType())) {
			doctype = technicsType;
		} else {
			for(int i=0;i<technicsTypes[1].length;i++){
				if(technicsTypes[1][i].equals(technicsType)){
					doctype = technicsTypes[5][i];
				}
			}
		}
		List<WTDocument> docList  = new ArrayList<WTDocument>();
		WTPart newpart = (WTPart)searchLatestIteratedByNumberVersionView(WTPart.class,part.getNumber(),part.getVersionInfo().getIdentifier().getValue(),"Manufacturing");
		WTPartUtil.getDescribedDocumentByPart(newpart, LoadConfig.getInstance().getLocalDomainName()+"."+doctype,docList);

		return docList;
		/*QueryResult qr = searchAllIteratedByNumberVersionView(WTPart.class,part.getNumber(),part.getVersionInfo().getIdentifier().getValue(),"Manufacturing");
		while(qr.hasMoreElements()){
			WTPart newpart = (WTPart)qr.nextElement();
			WTPartUtil.getDescribedDocumentByPart(newpart, LoadConfig.getInstance().getLocalDomainName()+"."+doctype,docList);
		}*/

		/*List<WTDocument> returnDocList = new ArrayList<WTDocument>();
		// 没有名称就根据类型去所有的工艺
		if (null == processZipDocName || "".equals(processZipDocName.trim())) {
			returnDocList = getProcess(docList, processType);
		}
		// 有名称 就根据名称获取工艺并且取最新版本
		else {
			WTDocument document = null;
			for (WTDocument doc : docList) {
				if (doc.getNumber().equals(processZipDocName)) {
					if (null != document) {
						if (document.getVersionIdentifier().getValue().compareTo(doc.getVersionIdentifier().getValue()) < 0) {
							document = doc;
						}
					} else {
						document = doc;
					}
				}
			}
			if (null != document) {
				returnDocList.add(document);
			}
		}
		return returnDocList;*/
	}

	// 正常工艺不考虑多个，考虑升版本
		/**
		 * 获取最新的工艺压缩包文件
		 *
		 * @author qianlong
		 * @date 2013-5-23
		 * @param part
		 * @return
		 * @throws WTRuntimeException
		 * @throws WTException
		 * @throws WTPropertyVetoException
		 *
		 */
		public static List<WTDocument> getProcessZipDoc(WTPart part, String processZipDocName, String processType)
				throws WTRuntimeException, WTException, WTPropertyVetoException {
			List<WTDocument> docList = WTPartUtil
					.getDescribedDocumentByPart(part, TypeNameConstants.assembleProcessDocTypeName);
			List<WTDocument> returnDocList = new ArrayList<WTDocument>();
			// 没有名称就根据类型去所有的工艺
			if (null == processZipDocName || "".equals(processZipDocName.trim())) {
				returnDocList = getProcess(docList, processType);
			}
			// 有名称 就根据名称获取工艺并且取最新版本
			else {
				WTDocument document = null;
				for (WTDocument doc : docList) {
					if (doc.getName().contains(processZipDocName)) {
						if (null != document) {
							if (document.getVersionIdentifier().getValue().compareTo(doc.getVersionIdentifier().getValue()) < 0) {
								document = doc;
							}
						} else {
							document = doc;
						}
					}
				}
				if (null != document) {
					returnDocList.add(document);
				}
			}
			return returnDocList;
		}

	public static WTDocument getProcessZipDoc(WTPart part) throws WTException {
	    QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        while (qr.hasMoreElements()) {
            WTDocument document = (WTDocument) qr.nextElement();
            String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
            if (typeName.contains("PROCESS_PLAN")) {
                return document;
            }
        }
        return null;
	}

	public static WTDocument getProcessZipDoc2(WTPart part,String number) throws WTException {
		QueryResult allIter = VersionControlHelper.service.allIterationsOf(part.getMaster());
		while(allIter.hasMoreElements()) {
			part = (WTPart)allIter.nextElement();
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
	        while (qr.hasMoreElements()) {
	            WTDocument document = (WTDocument) qr.nextElement();
	            String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
	            if (typeName.contains("PROCESS_PLAN") || typeName.contains("SOPDoc")) {
	            	String docNumber = document.getNumber();
	            	if(docNumber.equals(number)) {
	            		return document;
	            	}
	            }
	        }
		}
        return null;
	}

	public static List<WTDocument> getAllProcessZipDoc(WTPart part) throws WTException {
		List<WTDocument> list = new ArrayList<WTDocument>();
		QueryResult allIter = VersionControlHelper.service.allIterationsOf(part.getMaster());
		while(allIter.hasMoreElements()) {
			part = (WTPart)allIter.nextElement();
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
	        while (qr.hasMoreElements()) {
	            WTDocument document = (WTDocument) qr.nextElement();
	            String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
	            if (typeName.contains("PROCESS_PLAN")) {
	            	list.add(document);
	            }
	        }
		}
        return list;
	}

	private static List<WTDocument> getProcess(List<WTDocument> docList, String processType) {
		List<WTDocument> returnDocList = new ArrayList<WTDocument>();
		Map<String, WTDocument> map = new HashMap<String, WTDocument>();
		// 正常工艺
		if (Constants.normalProcess.equals(processType)) {
			for (WTDocument doc : docList) {
				if (!doc.getNumber().contains(Constants.reworkProcessDocEndwith)
						&& !doc.getNumber().contains(Constants.tempProcessDocEndwith)) {
					WTDocument document = map.get(doc.getNumber());
					if (null != document) {
						if (document.getVersionIdentifier().getValue().compareTo(doc.getVersionIdentifier().getValue()) < 0) {
							map.put(doc.getNumber(), doc);
						}
					} else {
						map.put(doc.getNumber(), doc);
					}
				}
			}
		}
		// 返工工艺
		else if (Constants.reworkProcess.equals(processType)) {
			for (WTDocument doc : docList) {
				if (doc.getNumber().contains(Constants.reworkProcessDocEndwith)) {
					WTDocument document = map.get(doc.getNumber());
					if (null != document) {
						if (document.getVersionIdentifier().getValue().compareTo(doc.getVersionIdentifier().getValue()) < 0) {
							map.put(doc.getNumber(), doc);
						}
					} else {
						map.put(doc.getNumber(), doc);
					}
				}
			}
		}
		// 临时工艺
		else if (Constants.tempProcess.equals(processType)) {
			for (WTDocument doc : docList) {
				if (doc.getNumber().contains(Constants.tempProcessDocEndwith)) {
					WTDocument document = map.get(doc.getNumber());
					if (null != document) {
						if (document.getVersionIdentifier().getValue().compareTo(doc.getVersionIdentifier().getValue()) < 0) {
							map.put(doc.getNumber(), doc);
						}
					} else {
						map.put(doc.getNumber(), doc);
					}
				}
			}
		}
		// 所有工艺
		else {
			for (WTDocument doc : docList) {
				WTDocument document = map.get(doc.getNumber());
				if (null != document) {
					if (document.getVersionIdentifier().getValue().compareTo(doc.getVersionIdentifier().getValue()) < 0) {
						map.put(doc.getNumber(), doc);
					}
				} else {
					map.put(doc.getNumber(), doc);
				}
			}
		}

		returnDocList.addAll(map.values());
		/*for (String str : map.keySet()) {
			returnDocList.add(map.get(str));
		}*/
		return returnDocList;
	}

	/**
	 * 获取最新的工艺压缩包
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param part
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws PropertyVetoException
	 *
	 */
	public static List<Vector<Object>> getProcessZip(WTPart part,String processZipDocName,String technicsType, String processType, String isEditable)
			throws WTRuntimeException, WTException, PropertyVetoException {
		return getProcessZip(part, processZipDocName, technicsType, processType, isEditable,null);
	}

	public static List<Vector<Object>> getProcessZip(WTPart part,String processZipDocName,String technicsType, String processType, String isEditable,Map<String,WTGroup> aclGroups)
			throws WTRuntimeException, WTException, PropertyVetoException {
		List<Vector<Object>> list = new ArrayList<Vector<Object>>();
		List<WTDocument> docList=null;
		if ("report".equals(processZipDocName)) {
			docList = getProcessZipDoc(part, null, technicsType, processType);
		}else{
			docList = getProcessZipDoc(part, processZipDocName, technicsType, processType);

		}
		WTUser currentUser = (WTUser)SessionHelper.manager.getPrincipal();



		for (WTDocument document : docList) {
			//1.如果是管理员wcadmin，则表示是ERP端调用，不需判断是否是创建者。修改时间：2015/02/02
			//2.如果isEditable值是true，则表示是该Part是启动工艺编辑器时当前可编辑的零部件，则需要下载所有的工艺文件。修改时间：2015/4/16
			//3.判断文档对象的创建者是否是当前用户，如果不是则不下载。修改时间：2014/9/17

			if ("report".equals(processZipDocName)) {
//				if(SessionHelper.manager.getAdministrator().equals(SessionHelper.manager.getPrincipal())
//						)
//						 {
				byte[] bytes = null;
				Vector<Object> vector = new Vector<Object>();
				if (null != document) {
					ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
					bytes = WTDocumentUtil.applicationDataToByte(data);
					vector.add(data.getFileName());
					vector.add(bytes);
					vector.add(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
					vector.add(document.getIterationDisplayIdentifier().toString());
					vector.add(document.getNumber());
					vector.add(document.getName());
					list.add(vector);
//					}
				}

			}else{

				collectAppDatas(isEditable, list, currentUser, document,aclGroups);
				//collectAppDatas(isEditable, list, currentUser, document);
			}
		}
		return list;
	}


	public static List<Vector<Object>> getApprovedProcessZip(WTPart part,String processZipDocName,String technicsType, String processType, String isEditable)
			throws WTRuntimeException, WTException, PropertyVetoException {
		List<Vector<Object>> list = new ArrayList<Vector<Object>>();
		List<WTDocument> docList=null;
		if ("report".equals(processZipDocName)) {
			 docList = getProcessZipDoc(part, null, technicsType, processType);
		}else{
		     docList = getProcessZipDoc(part, processZipDocName, technicsType, processType);
		}
		WTUser currentUser = (WTUser)SessionHelper.manager.getPrincipal();
		for (WTDocument document : docList) {
			//1.如果是管理员wcadmin，则表示是ERP端调用，不需判断是否是创建者。修改时间：2015/02/02
			//2.如果isEditable值是true，则表示是该Part是启动工艺编辑器时当前可编辑的零部件，则需要下载所有的工艺文件。修改时间：2015/4/16
			//3.判断文档对象的创建者是否是当前用户，如果不是则不下载。修改时间：2014/9/17

			if ("report".equals(processZipDocName)) {
//				if(SessionHelper.manager.getAdministrator().equals(SessionHelper.manager.getPrincipal())
//						)
//						 {
					byte[] bytes = null;
					Vector<Object> vector = new Vector<Object>();
					if (null != document) {
						ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
						bytes = WTDocumentUtil.applicationDataToByte(data);
						vector.add(data.getFileName());
						vector.add(bytes);
						vector.add(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
						vector.add(document.getIterationDisplayIdentifier().toString());
						vector.add(document.getNumber());
						vector.add(document.getName());
						list.add(vector);
//					}
				}

			}else{
				String state =document.getState().getState().toString();
				if("OBSOLESCENCE".equals(state)){
					//过滤到已作废
					continue;
				}
				ext.casc.util.IBAHelper helper = new ext.casc.util.IBAHelper();
				String CLDEZT = helper.getIBAStringValue(document, "CLDEZT");
				if(document.getState().getState().toString().equals("APPROVED")||"已批准".equals(CLDEZT)){
					collectAppDatas(isEditable, list, currentUser, document);
				}else if(!"space".equals(document.getVersionIdentifier().getValue())){
					//如果存在上一个版本，则取上一个受控版本
					QueryResult qr2 = VersionControlHelper.service.allVersionsOf(document.getMaster());
					int index = 0;
					while (qr2.hasMoreElements()) {
						WTDocument docVersion = (WTDocument) qr2.nextElement();
						if(index ==0){
							index++;
							continue;
						}else{
							CLDEZT = helper.getIBAStringValue(docVersion, "CLDEZT");
							if (docVersion.getState().getState().toString().equals("APPROVED")||"已批准".equals(CLDEZT)) {
								collectAppDatas(isEditable, list, currentUser, docVersion);
								break;
							}
							index ++;
						}
					}

				}

			}
		}
		return list;
	}

	private static void collectAppDatas(String isEditable, List<Vector<Object>> list, WTUser currentUser, WTDocument document) throws WTException, PropertyVetoException {
		if (SessionHelper.manager.getAdministrator().equals(SessionHelper.manager.getPrincipal())
				|| "true".equals(isEditable)
				|| document.getCreatorName().equals(currentUser.getName()) || document.getModifierName().equals(currentUser.getName())) {
			byte[] bytes = null;
			Vector<Object> vector = new Vector<Object>();
			if (null != document) {
				ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
				bytes = WTDocumentUtil.applicationDataToByte(data);
				vector.add(data.getFileName());
				vector.add(bytes);
				vector.add(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
				vector.add(document.getIterationDisplayIdentifier().toString());
				vector.add(document.getNumber());
				vector.add(document.getName());
				list.add(vector);
			}
		}

	}

	private static void collectAppDatas(String isEditable, List<Vector<Object>> list, WTUser currentUser, WTDocument document,Map<String,WTGroup> aclGroups) throws WTException, PropertyVetoException {
		if (SessionHelper.manager.getAdministrator().equals(SessionHelper.manager.getPrincipal())
				|| "true".equals(isEditable)
				|| document.getCreatorName().equals(currentUser.getName()) || document.getModifierName().equals(currentUser.getName())) {

			byte[] bytes = null;
			Vector<Object> vector = new Vector<Object>();
			if (null != document) {

				boolean addFlag = true;
				if(aclGroups!=null&&!aclGroups.isEmpty()){
					String processType =  TypedUtility.getLocalizedTypeName(document, Locale.CHINA);
					String groupName = processType+"_迁移权限组";
					WTGroup wtGroup = aclGroups.get(groupName);
					if(wtGroup!=null ){
						addFlag = true;
					}else{
						if(document.getCreatorName().equals(currentUser.getName()) || document.getModifierName().equals(currentUser.getName())){
							addFlag = true;
						}else{
							addFlag = false;
						}
					}

				}

				if(addFlag){
					ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
					bytes = WTDocumentUtil.applicationDataToByte(data);
					vector.add(data.getFileName());
					vector.add(bytes);
					vector.add(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
					vector.add(document.getIterationDisplayIdentifier().toString());
					vector.add(document.getNumber());
					vector.add(document.getName());
					list.add(vector);
				}

			}
		}

	}

	public static Map<String,WTGroup> getAclGroupByGyType(WTUser currentUser) throws WTException {

		Enumeration groups = currentUser.parentGroups(false);
		Map<String,WTGroup> groupMap = new HashMap<>();
		while (groups.hasMoreElements()) {
			WTPrincipalReference principalRef = (WTPrincipalReference) groups.nextElement();
			WTGroup group = (WTGroup) principalRef.getPrincipal();
			System.out.println(group.getName());
			if(group.getName().endsWith("_迁移权限组")){
				groupMap.put(group.getName(), group);
			}
		}
		return groupMap;
	}
	/**
	 * 获取最新的工艺压缩包
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws PropertyVetoException
	 *
	 */
	public static List<Vector<Object>> getProcessZip(WTDocument document) throws WTException, PropertyVetoException {
		List<Vector<Object>> list = new ArrayList<Vector<Object>>();
		byte[] bytes = null;
		Vector<Object> vector = new Vector<Object>();
		if (null != document) {
			document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
			ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
			bytes = WTDocumentUtil.applicationDataToByte(data);
			vector.add(data.getFileName());
			vector.add(bytes);
			vector.add(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
			vector.add(document.getIterationDisplayIdentifier().toString());
			list.add(vector);
		}
		return list;
	}

	/**
	 * 上传工艺压缩包
	 *
	 * @author qianlong
	 * @date 2012-11-21
	 * @param oid
	 * @param name
	 * @param bytes
	 * @return
	 * @throws Exception
	 *
	 */
	public static WTDocument uploadProcessZip(WTPart part,String technicsNumber, String technicsName, String technicsType,byte[] bytes, String note)
			throws Exception {

//		List<WTDocument> docList = getProcessZipDoc(part,technicsNumber, processZipDocName,technicsType, null);
//		WTDocument document = null;
//		if (docList.size() > 0) {
//			document = docList.get(0);
//		}
		WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
		if (document != null) {
			document = (WTDocument) WorkInProcessUtil.checkout(document);

			//bytes = updateProcessZip(document, bytes, technicsNumber);

			String tempFilePath = updateProcessZip2(document, bytes, technicsNumber);
			bytes = FileUtil.fileToBytes(new File(tempFilePath + technicsNumber + ".zip"));

			document = WTDocumentUtil.setPrimaryForDocument(document, technicsNumber + ".zip", bytes);
			document = (WTDocument) WorkInProgressHelper.service.checkin(document, note);

			//删除临时文件
			FileUtil.deleteFile(new File(tempFilePath));
		} else {
			String containerName = part.getContainerName();
			String folderPath = "";
			folderPath = LoadConfig.getInstance().getTechnicsDocPrefixPath()+technicsType;
			String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
			String doctype = null;

			for(int i=0;i<technicsTypes[1].length;i++){
				if(technicsTypes[1][i].equals(technicsType)){
					doctype = technicsTypes[5][i];
				}
			}
			if("SOPDoc".equals(doctype)){
				folderPath = part.getFolderPath().trim();
				String[] str = folderPath.split("/");
				folderPath = SopConstants.SOP_FOLDOR_PROCESS+str[3]+"/"+str[4];
			}

			//如果doctype为空，则为报表类工艺文件
			if(doctype == null) {
				doctype = LoadConfig.getInstance().getReportTechnicsType();
				folderPath = LoadConfig.getInstance().getReportTechnicsFolderPath();
			}

			document = WTDocumentUtil.createDocument(technicsNumber,technicsName, part.getContainer(), folderPath,LoadConfig.getInstance().getLocalDomainName()+"."+doctype);
			document = WTDocumentUtil.setPrimaryForDocument(document, technicsNumber + ".zip", bytes);
			WTPartUtil.createWTPartDescribeLink(part, document);

			long partid = part.getPersistInfo().getObjectIdentifier().getId();
			//WTPart newpart = (WTPart)CmExpImpSearchHelper.searchLatestIteratedByNumberAndView(WTPart.class,part.getNumber(),"Manufacturing");
			WTPart newpart = (WTPart)searchLatestIteratedByNumberVersionView(WTPart.class,part.getNumber(),part.getVersionInfo().getIdentifier().getValue(),"Manufacturing");

			//如果最新版本部件和上一个部件不是一个部件则也需要创建关联
			if(partid!= newpart.getPersistInfo().getObjectIdentifier().getId()){
				WTPartUtil.createWTPartDescribeLink(newpart, document);
			}

		}
		return document;
	}



	/**
	 * 再次上传工艺压缩包
	 *
	 * @author caolei
	 * @date 2015-11-04
	 * @param oid
	 * @param name
	 * @param bytes
	 * @return
	 * @throws Exception
	 *
	 */




	public static WTDocument reUploadProcessZip(WTPart part,String technicsNumber, String technicsName, String technicsType,byte[] bytes)
			throws Exception {

		WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
		if (null != document) {
//			document = (WTDocument) WorkInProcessUtil.checkout(document);

			//bytes = updateProcessZip(document, bytes, technicsNumber);

			String tempFilePath = reUpdateProcessZip2(document, bytes, technicsNumber);
			bytes = FileUtil.fileToBytes(new File(tempFilePath + technicsNumber + ".zip"));

			document = WTDocumentUtil.setPrimaryForDocument(document, technicsNumber + ".zip", bytes);
//			document = (WTDocument) WorkInProgressHelper.service.checkin(document, note);

			//删除临时文件
			FileUtil.deleteFile(new File(tempFilePath));
		} else {
			String containerName = part.getContainerName();
			String folderPath = "";
			folderPath = LoadConfig.getInstance().getTechnicsDocPrefixPath()+technicsType;
			String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
			String doctype = null;

			for(int i=0;i<technicsTypes[1].length;i++){
				if(technicsTypes[1][i].equals(technicsType)){
					doctype = technicsTypes[5][i];
				}
			}
			if("SOPDoc".equals(doctype)){
				folderPath = part.getFolderPath().trim();
				String[] str = folderPath.split("/");
				folderPath = SopConstants.SOP_FOLDOR_PROCESS+str[3]+"/"+str[4];
			}

			//如果doctype为空，则为报表类工艺文件
			if(doctype == null) {
				doctype = LoadConfig.getInstance().getReportTechnicsType();
				folderPath = LoadConfig.getInstance().getReportTechnicsFolderPath();
			}

			document = WTDocumentUtil.createDocument(technicsNumber,technicsName, part.getContainer(), folderPath,LoadConfig.getInstance().getLocalDomainName()+"."+doctype);
			document = WTDocumentUtil.setPrimaryForDocument(document, technicsNumber + ".zip", bytes);
			WTPartUtil.createWTPartDescribeLink(part, document);

			long partid = part.getPersistInfo().getObjectIdentifier().getId();
			//WTPart newpart = (WTPart)CmExpImpSearchHelper.searchLatestIteratedByNumberAndView(WTPart.class,part.getNumber(),"Manufacturing");
			WTPart newpart = (WTPart)searchLatestIteratedByNumberVersionView(WTPart.class,part.getNumber(),part.getVersionInfo().getIdentifier().getValue(),"Manufacturing");
			//如果最新版本部件和上一个部件不是一个部件则也需要创建关联
			if(partid!= newpart.getPersistInfo().getObjectIdentifier().getId()){
				WTPartUtil.createWTPartDescribeLink(newpart, document);
			}
		}
		return document;
	}







	private static String updateProcessZip2(WTDocument wtDocument, byte[] bytes, String processZipDocName)
			throws Exception {
		String subpath = java.util.UUID.randomUUID().toString();
		String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
				+ subpath + File.separator;
		FileUtil.writeBytes(tempFilePath, processZipDocName + ".zip", bytes);
		ApacheZipUtil.decompress(tempFilePath + processZipDocName + ".zip", tempFilePath + processZipDocName);
		File xmlFile = new File(tempFilePath + processZipDocName + File.separator + processZipDocName + ".xml");
		InputStream inputStream = new FileInputStream(xmlFile);

		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element element = xmlUtil.getRootElement().getChild(XMLConstants.QMFawTechnicsInfo);
		if(element == null ){
			//报表类工艺文件
			element = xmlUtil.getRootElement().getChild(XMLConstants.XWReportTechnicsInfo);
		}
		element.setAttribute("version", wtDocument.getVersionIdentifier().getValue() + "."
				+ VersionControlHelper.nextIterationId(wtDocument).getValue());
		element.setAttribute("lifecycle", wtDocument.getState().getState().getDisplay(Locale.CHINA));

		// 替换工艺xml，打包工艺文件夹，上传工艺压缩包
		FileOutputStream fileOutputStream = new FileOutputStream(new File(tempFilePath + processZipDocName
				+ File.separator + processZipDocName + ".xml"), false);
		Format format = Format.getPrettyFormat();
		format.setEncoding("GBK");
		XMLOutputter xmlOutput = new XMLOutputter(format);
		xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);
		ApacheZipUtil.compress(tempFilePath + processZipDocName, tempFilePath + processZipDocName + ".zip");

		xmlOutput.clone();
		fileOutputStream.close();
		inputStream.close();


		return tempFilePath;
		//return FileUtil.fileToBytes(new File(tempFilePath + processZipDocName + ".zip"));
	}


	/**
	 *
	 * 再次解压缩工艺文件
	 *
	 * modify by caolei
	 *
	 * 2015-11-04
	 *
	 *
	 */
	private static String reUpdateProcessZip2(WTDocument wtDocument, byte[] bytes, String processZipDocName)
			throws Exception {
		String subpath = java.util.UUID.randomUUID().toString();
		String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "reupload" + File.separator
				+ subpath + File.separator;
		FileUtil.writeBytes(tempFilePath, processZipDocName + ".zip", bytes);
		ApacheZipUtil.decompress(tempFilePath + processZipDocName + ".zip", tempFilePath + processZipDocName);
		File xmlFile = new File(tempFilePath + processZipDocName + File.separator + processZipDocName + ".xml");
		//File newAttach = new File(tempFilePath + processZipDocName + File.separator + "attachs");
		InputStream inputStream = new FileInputStream(xmlFile);

		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
//		Element element = xmlUtil.getRootElement().getChild(XMLConstants.QMFawTechnicsInfo);
//		if(element == null ){
//			//报表类工艺文件
//			element = xmlUtil.getRootElement().getChild(XMLConstants.XWReportTechnicsInfo);
//		}
//		element.setAttribute("version", wtDocument.getVersionIdentifier().getValue() + "."
//				+ VersionControlHelper.nextIterationId(wtDocument).getValue());
//		element.setAttribute("lifecycle", wtDocument.getState().getState().getDisplay(Locale.CHINA));

		// 替换工艺xml，打包工艺文件夹，上传工艺压缩包
		FileOutputStream fileOutputStream = new FileOutputStream(new File(tempFilePath + processZipDocName
				+ File.separator + processZipDocName + ".xml"), false);
		Format format = Format.getPrettyFormat();
		format.setEncoding("GBK");
		XMLOutputter xmlOutput = new XMLOutputter(format);
		xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);
		//FileUtil.deleteFile(newAttach);
		ApacheZipUtil.compress(tempFilePath + processZipDocName, tempFilePath + processZipDocName + ".zip");

		xmlOutput.clone();
		fileOutputStream.close();
		inputStream.close();


		return tempFilePath;
		//return FileUtil.fileToBytes(new File(tempFilePath + processZipDocName + ".zip"));
	}



	/**
	 * 实例化工艺
	 *
	 * @author qianlong
	 * @date 2013-5-24
	 * @param partOid
	 * @throws Exception
	 *
	 */
	public static void structureProcessPlan(String partOid, String documentOid, String processType) throws Exception {
		WTPart wtPart = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
		WTDocument wtDocument = (WTDocument) Util.getObjectByOid(WTDocument.class, documentOid);
		wtDocument = (WTDocument) VersionControlHelper.service.getLatestIteration(wtDocument, true);
		ProcessPlanStructure structure = new ProcessPlanStructure(wtPart, wtDocument, processType);
		structure.structureProcessPlan();
	}

	/**
	 * 实例化工艺
	 *
	 * @author qianlong
	 * @date 2013-5-24
	 * @param partOid
	 * @throws Exception
	 * @modify lbzhang
	 *
	 */
	public static MPMProcessPlan structureProcessPlan(WTPart wtPart, String documentOid, String processType)
			throws Exception {
		WTDocument wtDocument = (WTDocument) Util.getObjectByOid(WTDocument.class, documentOid);
		wtDocument = (WTDocument) VersionControlHelper.service.getLatestIteration(wtDocument, true);
		ProcessPlanStructure structure = new ProcessPlanStructure(wtPart, wtDocument, processType);
		return structure.structureProcessPlan();
	}

	/**
	 * 设置工艺包文件状态
	 *
	 * @author qianlong
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws WTRuntimeException
	 * @date 2013-5-31
	 *
	 */
	public static void setProcessZipDocLifeCycle(String documnetOid, String stateName) throws WTException,
			WTRuntimeException, WTPropertyVetoException {
		WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, documnetOid);
		document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
		LifeCycleHelper.service.setLifeCycleState(document, State.toState(stateName));
	}

//	/**
//	 * 设置工艺包文件状态
//	 *
//	 * @author qianlong
//	 * @throws WTException
//	 * @throws WTPropertyVetoException
//	 * @throws WTRuntimeException
//	 * @date 2013-5-31
//	 *
//	 */
//	public static void setProcessTask(String taskOid, String stateName) throws WTException, WTRuntimeException,
//			WTPropertyVetoException {
//		GMReworkProcessTask reworkTask = (GMReworkProcessTask) ReferenceFactory.getObjectbyOid(taskOid);
//		LifeCycleHelper.service.setLifeCycleState(reworkTask, State.toState(stateName));
//	}

	/**
	 * 工艺预览
	 *
	 * @author qianlong
	 * @date 2013-7-23
	 * @param partOid
	 * @param technicName
	 * @param type
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 * @throws WTPropertyVetoException
	 */
	public static String getTechnicPreview(String documentOid) throws WTException, WTRuntimeException,
			WTPropertyVetoException {
		WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, documentOid);
		document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
		return "<a href=\"" + new URLFactory().getBaseHREF() + "app/#netmarkets/jsp/glaway/mpm/technicPreview.jsp?oid="
				+ document.toString() + "\" target=_blank>工艺预览</a>";
	}

	/**
	 * 启动工艺编辑器时根据流程活动获取对应的workitem
	 *
	 * @author qianlong
	 * @date 2013-7-26
	 * @param activity
	 * @return
	 * @throws WTException
	 */
	public static String getWorkItemByActivity(String activityOid) throws WTException {
		String workitemOid = "";
		WfAssignedActivity activity = null;
		if (activityOid != null) {
			activity = (WfAssignedActivity) Util.getObjectByOid(WfAssignedActivity.class, activityOid);
		}
		if (activity != null) {
			ArrayList<WorkItem> workItemList = WorkflowUtil.getWorkItemFromActivity(activity);
			if (workItemList.size() != 0) {
				WorkItem workItem = workItemList.get(0);
				workitemOid = Util.getStringOid(workItem);
			}
		}
		return workitemOid;
	}

	/**
	 * 启动工艺编辑器
	 *
	 * @author lbzhang
	 * @date 2013-7-26
	 * @param topOid
	 * @param oid
	 * @param technicsOid
	 * @param self
	 * @param type
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 *
	 */
	public static String getStartProcessEditorURL(String topOid, WTPart part, String technicsOid, String type)
			throws WTRuntimeException, WTException {
		WTPart topPart = (WTPart) ReferenceFactory.getObjectbyOid("wt.part.WTPart:" + topOid);
		WTDocument document = (WTDocument) ReferenceFactory.getObjectbyOid("wt.doc.WTDocument:" + technicsOid);

		return WorkflowUtil.getStartProcessEditorURL(topPart, part, document, type);
	}

	/**
	 * 设置在制令号
	 *
	 * @author qianlong
	 * @date 2013-7-31
	 * @param processPlanOid
	 */
	public static void setOnBuildNumber(String processPlanOid, String processPlanZipOid, String onBuildNumber) {
		FileInputStream inputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			MPMProcessPlan processPlan = (MPMProcessPlan) ReferenceFactory.getObjectbyOid(processPlanOid);
			IBAHelper.setIBAAnyValue(processPlan, AttributeConstants.onBuildNumber, onBuildNumber);
			WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, processPlanZipOid);
			document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);

			String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
					+ String.valueOf(new Date().getTime()) + File.separator;
			// 解压工艺压缩包，获取工艺xml文件的流
			String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
			String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
			ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
			File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
			inputStream = new FileInputStream(xmlFile);

			// 读取xml 创建结构

			SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
			Element rootElement = xmlUtil.getRootElement();
			if (XMLConstants.technics.equals(rootElement.getName())) {
				for (Element rootAttrElement : (List<Element>) rootElement.getChildren(XMLConstants.QMFawTechnicsInfo)) {
					rootAttrElement.setAttribute("onBuildNumber", onBuildNumber);
				}
			}
			// 替换工艺xml，打包工艺文件夹，上传工艺压缩包
			fileOutputStream = new FileOutputStream(new File(tempFilePath + subFileName + File.separator + subFileName
					+ ".xml"), false);
			Format format = Format.getPrettyFormat();
			format.setEncoding("GBK");
			XMLOutputter xmlOutput = new XMLOutputter(format);
			xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);

			ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);

			// 重新上传PBOM的XML
			WTDocumentUtil.setPrimaryForDocument(document, zipFileName, new FileInputStream(new File(tempFilePath
					+ zipFileName)));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 *
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-8-17
	 */
	public static void checkProcessPlan(CheckTechnics checkTechnics) throws WTException {
		List<StepObject> stepList = checkTechnics.getStepObjects();
		if (stepList == null) {
			return;
		}
		for (int i = 0; i < stepList.size(); i++) {
			checkStep(stepList.get(i));
		}
	}

	private static void checkStep(StepObject step) throws WTException {
		List<Equipment> equipments = step.getEquipments();
		equipments.clear();
		for (int i = 0; i < equipments.size(); i++) {
			Equipment equipment = equipments.get(i);
			MPMTooling tooling = (MPMTooling) Util.getObjectByOid(MPMTooling.class, equipment.getOid());
			if (tooling == null || !tooling.getState().toString().equals(Constants.YZF)) {
				equipments.remove(i);
			}
		}
		List<Material> materials = step.getMaterials();
		materials.clear();
		for (int i = materials.size() - 1; i >= materials.size(); i--) {
			Material material = materials.get(i);
			MPMProcessMaterial processMaterial = (MPMProcessMaterial) Util.getObjectByOid(MPMProcessMaterial.class,
					material.getOid());
			if (processMaterial == null ||! processMaterial.getState().toString().equals(Constants.YZF)) {
				materials.remove(i);
			}
		}
		List<Tool> tools = step.getTools();
		tools.clear();
		for (int i = tools.size() - 1; i >= tools.size(); i--) {
			Tool tool = tools.get(i);
			MPMTooling tooling = (MPMTooling) Util.getObjectByOid(MPMTooling.class, tool.getOid());
			if (tooling == null ||! tooling.getState().toString().equals(Constants.YZF)) {
				tools.remove(i);
			}
		}
		List<PaceObject> paceList = step.getPaceObjects();
		for (int i = 0; i < paceList.size(); i++) {
			checkPace(paceList.get(i));
		}
	}

	private static void checkPace(PaceObject pace) throws WTException {
		List<Equipment> equipments = pace.getEquipments();
		equipments.clear();
		for (int i = equipments.size() - 1; i >= equipments.size(); i--) {
			Equipment equipment = equipments.get(i);
			MPMTooling tooling = (MPMTooling) Util.getObjectByOid(MPMTooling.class, equipment.getOid());
			if (tooling == null || !tooling.getState().toString().equals(Constants.YZF)) {
				equipments.remove(i);
			}
		}
		List<Material> materials = pace.getMaterials();
		materials.clear();
		for (int i = materials.size() - 1; i >= materials.size(); i--) {
			Material material = materials.get(i);
			MPMProcessMaterial processMaterial = (MPMProcessMaterial) Util.getObjectByOid(MPMProcessMaterial.class,
					material.getOid());
			if (processMaterial == null || !processMaterial.getState().toString().equals(Constants.YZF)) {
				materials.remove(i);
			}
		}
		List<Tool> tools = pace.getTools();
		tools.clear();
		for (int i = tools.size() - 1; i >= tools.size(); i--) {
			Tool tool = tools.get(i);
			MPMTooling tooling = (MPMTooling) Util.getObjectByOid(MPMTooling.class, tool.getOid());
			if (tooling == null || !tooling.getState().toString().equals(Constants.YZF)) {
				tools.remove(i);
			}
		}
	}

	/**
	 * 提交工艺文件签审流程
	 *
	 * @author LongXiuChuan
	 * @param docNumber 工艺文档的编号
	 * @throws WTException
	 * @throws RemoteException
	 */
	public static void startProcessPlanWorkflow(String docNumber) throws WTException, RemoteException {
	    WTDocument document = WTDocumentUtil.getDocumentByNumber(docNumber);
	    if(document != null) {
	        String number = document.getNumber();
	        String name = document.getName();
	        WorkflowUtil.startProcess(document, WorkflowConstants.SANJIGONGYIQIANSHENLIUCHENG, name);
	    }
	}

	public static WTDocument getTechnicsDocByMPMPPlan(String oid) throws WTException, PropertyVetoException {
		MPMProcessPlan pplan = (MPMProcessPlan)ReferenceFactory.getObjectbyOid(oid);
		Collection collection = MPMResourceHelper.service.getAssociatedDescribeDocuments(pplan);
		Iterator iterator = collection.iterator();
		while(iterator.hasNext()) {
			ObjectReference oref = (ObjectReference)iterator.next();
			WTDocument doc = (WTDocument)oref.getObject();
			if(pplan.getNumber().equals(doc.getNumber())) {
				return (WTDocument) VersionControlHelper.service.getLatestIteration(doc, true);
			}
		}
		return null;
	}

	public static Map getPartIda2a2ByWorkItem(MPMProcessPlan pplan) throws WTException, PropertyVetoException {
		String ida2a2 = "";
		String partOid="";
		HashMap<String,String> map=new HashMap<String,String>();
		QueryResult qr = MPMProcessPlanHelper.service.getWTParts(pplan, NCServerHolder.makeForLatestConfigSpec());
		if(qr.hasMoreElements()) {
			WTPart part = (WTPart)qr.nextElement();
			WTDocument document = PBOMHelper.getBOMXmlDoc(part, Constants.pbomDocEndwith);
			partOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
			if(document != null) {
				ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
			} else {
				ida2a2 = WTPartUtil.getHasPbomXmlParentPartIda2a2(part, ida2a2);
			}
			map.put("partOid", partOid);
			map.put("ida2a2", ida2a2);

		}
		return map;
	}

	public static String getPartIda2a2ByWorkItem(WorkItem workItem) throws ChangeException2, WTException, PropertyVetoException {
		Object obj = workItem.getPrimaryBusinessObject().getObject();
		String ida2a2 = "";
		if(obj instanceof WTChangeOrder2) {
			WTChangeOrder2 ecn = (WTChangeOrder2)obj;
			QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(ecn);
            while (qResult.hasMoreElements()) {
            	Object object = qResult.nextElement();
            	if (object instanceof MPMProcessPlan) {
            		MPMProcessPlan pplan = (MPMProcessPlan)object;
        			QueryResult qr = MPMProcessPlanHelper.service.getWTParts(pplan, NCServerHolder.makeForLatestConfigSpec());
        			if(qr.hasMoreElements()) {
        				WTPart part = (WTPart)qr.nextElement();
        				WTDocument document = PBOMHelper.getBOMXmlDoc(part, Constants.pbomDocEndwith);
        				if(document != null) {
        					ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
        				} else {
        					ida2a2 = WTPartUtil.getHasPbomXmlParentPartIda2a2(part, ida2a2);
        				}
        			}
            	}
            }
		} else if( obj instanceof WTPart) {
			WTPart part = (WTPart)obj;
			WTDocument document = PBOMHelper.getBOMXmlDoc(part, Constants.pbomDocEndwith);
			if(document != null) {
				ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
			} else {
				ida2a2 = WTPartUtil.getHasPbomXmlParentPartIda2a2(part, ida2a2);
			}
		}
		return ida2a2;
	}

	public static List<Vector<Object>> getReportProcessZip(WTPart part) throws WTException, PropertyVetoException {
		List<Vector<Object>> list = new ArrayList<Vector<Object>>();

		WTPart newpart = (WTPart) searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			String typename = TypedUtility.getTypeIdentifier(document).getTypename();
			if (typename.contains("report")) {
				// document =
				// (WTDocument)VersionControlHelper.service.getLatestIteration(document,
				// true);
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					byte[] bytes = null;
					Vector<Object> vector = new Vector<Object>();
					if (null != document) {
						ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
						bytes = WTDocumentUtil.applicationDataToByte(data);
						vector.add(data.getFileName());
						vector.add(bytes);
						vector.add(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
						vector.add(document.getIterationDisplayIdentifier().toString());
						vector.add(document.getNumber());
						vector.add(document.getName());
						list.add(vector);
					}

				}
			}
		}
		return list;
	}
}
