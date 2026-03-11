package com.glaway.mpm.intf;

import com.glaway.mpm.constants.WorkflowConstants;
import com.glaway.mpm.importdata.ImportPartUtil;
import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.pbom.db.ErpResult;
import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbom.helper.PBOMHelper;
import com.glaway.mpm.pbom.helper.SynchPbomXmlQueueHelper;
import com.glaway.mpm.pbom.helper.XmlReBuildStruct;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.*;
import com.ptc.wvs.common.util.WVSProperties;
import com.ptc.wvs.server.publish.PublishJob;
import com.ptc.wvs.server.publish.PublishQueueHelper;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.RepUpdateUtils;
import ext.casc.lifecycle.CmLifecycleHelper;
import ext.casc.part.CSCPart;
import ext.casc.product.model.Batch;
import ext.casc.util.DBUtil;
import ext.casc.util.Tools;
import org.dom4j.DocumentException;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.filter.NavigationCriteria;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.part.*;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.views.View;
import wt.viewmarkup.DerivedImage;
import wt.viewmarkup.ViewMarkUpHelper;
import wt.viewmarkup.Viewable;

import java.beans.PropertyVetoException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

public class PBOMEditorToWCIntfRMI implements RemoteAccess {
	private static String CLASSNAME = PBOMEditorToWCIntfRMI.class.getName();
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);
	private static final String SUCCESS = "success";
	private static final String FAILED = "failed";

	/**
	 * 通过零件获取POM XML文件
	 *
	 * @author qianlong
	 * @date 2012-11-1
	 * @param partNumber
	 * @return
	 *
	 */

	public static byte[] getPBOMXmlRMI(String oid) {
		GLLogger.debug(CLASSNAME, "--oid-" + oid);
		byte[] bytes = null;
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			if(part==null){
				System.out.println(oid+"零件已被删除");
				return null;
			}
			//part=	WTPartUtil.getLatestPartByNumberAndView(part, LoadConfig.getInstance().getPbomView());
			bytes = PBOMHelper.getBOMXml(part, Constants.pbomDocEndwith);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 通过零件上传PBOM xml
	 *
	 * @author qianlong
	 * @date 2012-11-5
	 * @param oid
	 * @param bytes
	 *
	 */
	public static boolean savePBOMXmlRMI(String oid, byte[] bytes) {
		GLLogger.debug(CLASSNAME, "--savePBOMXmlRMI--oid-" + oid );
		SessionServerHelper.manager.setAccessEnforced(false);
		boolean flag = false;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			PBOMHelper.saveBOMXmlWithOutCheckOut(part, bytes, Constants.pbomDocEndwith);
			transaction.commit();
			transaction = null;
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return flag;
	}
	public static boolean savePBOMXmlWithOutCheckOutRMI(String oid, byte[] bytes) {
		GLLogger.debug(CLASSNAME, "--savePBOMXmlRMI--oid-" + oid );
		boolean flag = false;
		SessionServerHelper.manager.setAccessEnforced(false);
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			PBOMHelper.saveBOMXmlWithOutCheckOut(part, bytes, Constants.pbomDocEndwith);
			transaction.commit();
			transaction = null;
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
			SessionServerHelper.manager.setAccessEnforced(true);

		}
		return flag;
	}

	/**
	 * 通过零件上传PBOM xml
	 *
	 * @author longxiuchuan
	 * @date 2014-1-14
	 * @param oid
	 * @param bytes
	 *
	 */
	public static boolean savePBOMXmlRMI2(String oid, byte[] bytes) {
		GLLogger.debug(CLASSNAME, "--savePBOMXmlRMI2--oid-" + oid );
		boolean flag = false;
		SessionServerHelper.manager.setAccessEnforced(false);
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			PBOMHelper.saveBOMXmlForNewVersion(part, bytes, Constants.pbomDocEndwith);
			transaction.commit();
			transaction = null;
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
			SessionServerHelper.manager.setAccessEnforced(true);

		}
		return flag;
	}

	/**
	 * 获取PBOM属性的默认值
	 *
	 * @author qianlong
	 * @date 2013-3-21
	 * @return
	 *
	 */
	public static Map<String, List<String>> getPBOMAttributesValueRMI() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> plantList = new ArrayList<String>();
		plantList.add("    ");
		String workShop = propertiesUtil.getProperty("part-iba-workShop-value");
		if (null != workShop && !"".equals(workShop)) {
			for (String str : workShop.split(";")) {
				plantList.add(str);
			}
		}
		map.put("workShop", plantList);

		List<String> materialTypeList = new ArrayList<String>();
		materialTypeList.add("    ");
		String materialType = propertiesUtil.getProperty("part-iba-materialType-value");
		if (null != materialType && !"".equals(materialType)) {
			for (String str : materialType.split(";")) {
				materialTypeList.add(str);
			}
		}
		map.put("materialType", materialTypeList);

		List<String> backupReasonList = new ArrayList<String>();
		backupReasonList.add("    ");
		String backupReason = propertiesUtil.getProperty("part-iba-backupReason-value");
		if (null != backupReason && !"".equals(backupReason)) {
			for (String str : backupReason.split(";")) {
				backupReasonList.add(str);
			}
		}
		map.put("backupReason", backupReasonList);

		List<String> outsourcingUnitsList = new ArrayList<String>();
		outsourcingUnitsList.add("    ");
		String outsourcingUnits = propertiesUtil.getProperty("part-iba-outsourcingUnits-value");
		if (null != outsourcingUnits && !"".equals(outsourcingUnits)) {
			for (String str : outsourcingUnits.split(";")) {
				outsourcingUnitsList.add(str);
			}
		}
		map.put("outsourcingUnits", outsourcingUnitsList);

		return map;
	}

	/**
	 * 获取零件的类型
	 *
	 * @author qianlong
	 * @date 2012-11-30
	 * @param part
	 * @return
	 *
	 */
	public static String getTypeNameRMI(WTPart part) {
		String typeName = TypedUtility.getTypeIdentifier(part).getTypename();
		return typeName.substring(typeName.lastIndexOf(".") + 1);
	}

	/**
	 * 通过Design零件获取Planning零件 在planning不存在的情况下 如果needNew为true就创建,如果为false就不创建
	 *
	 * @author qianlong
	 * @date 2012-12-10
	 * @return
	 *
	 */
	public static WTPart getPlanningPartRMI(String number, String oid, Long viewId, boolean needNew) {
		GLLogger.debug(CLASSNAME, "number--" + number + "--oid--" + oid + "--viewId--" + viewId);
		WTPart part = null;
		try {
			part = WTPartUtil.getPartByNumberAndView(number, viewId);
			if (needNew && part == null) {
				part = WTPartUtil.createPlanningPart(oid);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return part;
	}

	/**
	 * 通过view名称获取view oid
	 *
	 * @author qianlong
	 * @date 2012-12-10
	 * @param oid
	 * @return
	 *
	 */
	public static Long getViewByNameRMI(String viewName) {
		Long viewId = null;
		try {
			View view = WTPartUtil.getViewByName(viewName);
			if (view != null) {
				viewId = Util.getLongOid(view);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "--viewName-" + viewName + "--viewId-" + viewId);
		return viewId;
	}

	/**
	 * 通过零件获取零件的可视化图档使用creoView打开的url
	 *
	 * @author qianlong
	 * @date 2013-3-28
	 * @param partOid
	 *
	 */
	public static String get3DDocumentUrlRMI(String partOid) {
		String creoViewUrl = "";
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
			if (part != null) {
				creoViewUrl = PBOMHelper.getCreoViewUrl(part, part.getContainer());
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return creoViewUrl;
	}

	/**
	 * 获取二维图档url
	 *
	 * @author qianlong
	 * @date 2013-2-22
	 *
	 */
	public static String get2DDocumentUrlRMI(String oid, Long viewOid) {
		String creoViewUrl = "";
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			if (PBOMHelper.isPlanning(part)) {
				part = WTPartUtil.getPartByNumberAndView(part.getNumber(), viewOid);
			}
			EPMDocument epmDocument = WTPartUtil.get2DEPMDocumentByPart(part);
			if (epmDocument != null) {
				creoViewUrl = PBOMHelper.getCreoViewUrl(epmDocument, epmDocument.getContainer());
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return creoViewUrl;
	}

	/**
	 * 通过结构中的子皆获取最上皆
	 *
	 * @author qianlong
	 * @date 2013-2-22
	 *
	 */
	public static String getRootByChildRMI(String oid) {
		String returnOid = "";
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			returnOid = PBOMHelper.getParent(part);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return returnOid;
	}

	/**
	 *结构化PBOM
	 *
	 * @author qianlong
	 * @date 2013-3-19
	 * @return
	 *
	 */
	public static String pbomStructureRMI(List<List> list) {
		GLLogger.debug(CLASSNAME, "--data--list--" + list);
		String message = SUCCESS;
		Transaction transaction = new Transaction();

		try {
			transaction.start();
			for (List childList : list) {
				String tag = (String) (String) childList.get(0);
				// 新建并且修改属性
				if ("new".equals(tag)) {
					PBOMHelper.createPart((String) childList.get(1), (String) childList.get(2),  (String) childList.get(5),  (String) childList
							.get(3), (Map<String, String>) childList.get(4),String.valueOf(childList.get(6)));
				}
				// 删除
				else if ("delete".equals(tag)) {
					PBOMHelper.deletePart((String) childList.get(1), (String) childList.get(2), (String) childList.get(3));
				}
				// 移动并且修改属性
				else if ("move".equals(tag)) {
					PBOMHelper.modifyWTPartUsageLink((String) childList.get(1), (String) childList.get(2),
							(String) childList.get(3), (String) childList.get(6),(String) childList.get(4), (Map<String, String>) childList
									.get(5));
				}
				// 只修改属性
				else if ("modify".equals(tag)) {
					PBOMHelper.modifyPart((String) childList.get(1),(String) childList.get(2),(String) childList.get(3), (Map<String, String>) childList.get(4)
							,String.valueOf(childList.get(5)));
				}
			}
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			e.printStackTrace();
			message = FAILED;
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return message;
	}


	/**
	 *结构化PBOM
	 *
	 * @author qianlong
	 * @date 2013-3-19
	 * @return
	 *
	 */
	public static String pbomStructure2RMI(List<Map<String, Object>> byteslist,Map<String,String> gysls) {
		try {
			SynchPbomXmlQueueHelper.createProcessingQueue(byteslist,gysls);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 兼容老版本pbom
	 * @param byteslist
	 * @return
	 */

	public static String pbomStructure2RMI(List<Map<String, Object>> byteslist) {
		try {
			SynchPbomXmlQueueHelper.createProcessingQueue(byteslist,new HashMap<String,String>());
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static String synchPbomXml(List<Map<String, Object>> byteslist,Map<String,String> gysls) {
		GLLogger.debug(CLASSNAME, "--data--byteslist--" + byteslist);
		String message = SUCCESS;
		Transaction transaction = new Transaction();

		try {
			transaction.start();
			for(Map<String,Object> map:byteslist){
				rebuildStruct(String.valueOf(map.get("oid")),(byte[])map.get("bytes"),gysls);
			}
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			e.printStackTrace();
			message = FAILED;
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return message;
	}

	/**
	 * 同步结构
	 *
	 * @param oid
	 *            ida2a2
	 * @param gysls
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 */
	public static void rebuildStruct(String oid,byte[] bytes, Map<String, String> gysls) throws Exception {
		long startTime = System.currentTimeMillis();
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
		if (part == null) {
			return;
		}
		try {
			XmlReBuildStruct.execute(part, bytes,gysls);
		} catch(Exception e) {
			throw e;
		}
		long endTime = System.currentTimeMillis();
	}
	/**
	 * 更新零件的版本版序
	 *
	 * @author qianlong
	 * @date 2013-6-7
	 * @return
	 *
	 */
	public static Map<String, Object> updatePartVersion(List<Map<String, Object>> list) {
		GLLogger.debug(CLASSNAME, "--updatePartVersion--list----" + list);
		String errorMessage = "";
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			for (Map<String, Object> map : list) {
				try {
					WTPart part = PBOMHelper.modifyVersion(map.get("oid") + "", map.get("versionIndex") + "");
					List<Object> retrunList = new ArrayList<Object>();
					retrunList.add(part.getPersistInfo().getObjectIdentifier().getId() + "");
					retrunList.add(part.getVersionIdentifier().getValue() + "."
						+ part.getIterationIdentifier().getValue());
					returnMap.put(part.getNumber(), retrunList);
					if("2".equals(String.valueOf(map.get("versionIndex")))) {//转阶段时需要修改阶段标记
						IBAHelper ibaHelper = new IBAHelper(part);
						String state = part.getState().getState().toString();
						if(!"APPROVED".equals(state)){
							Map<String,String> ibaMap = new HashMap<String,String>();
							ibaMap.put("PHASE_CODE", String.valueOf(map.get("phase")));
							ibaHelper.setIBAValue(part, ibaMap);
						}

					}
				} catch (Exception e) {
					errorMessage = map.get("partNumber") + "升版失败！";
					returnMap.put("errorMessage", errorMessage);
					e.printStackTrace();
				}
			}
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return returnMap;
	}

	/**
	 * 及时创建中间件和辅件
	 *
	 * @author qianlong
	 * @date 2013-3-21
	 * @return
	 *
	 */
	public static List<Object> createMiddleORAssistantPartRMI(Map<String, String> map, Map<String, String> ibaMap) {
		GLLogger.debug(CLASSNAME, "--createPartRMI--map--" + map + "--ibaMap--" + ibaMap);
		String parentPartOid = map.get("parentPartOid");
		String number = map.get("number");
		String name = map.get("name");
		String typeName = map.get("typeName");
		String folderPath = "";
		List<Object> list = new ArrayList<Object>();
		String message = "";
		WTPart part = null;
		try {
			WTPart parentPart = (WTPart) Util.getObjectByOid(WTPart.class, parentPartOid);
			folderPath = parentPart.getLocation();
			if ("middle".equals(typeName) || "mp".equals(typeName)) {
				typeName = propertiesUtil.getProperty("process-middle-part-type-name");
			} else if ("assistant".equals(typeName)) {
				typeName = propertiesUtil.getProperty("process-assistant-part-type-name");
			}

			//检查是否存在相同编号的PBOM
			part = WTPartUtil.getLatestPartByNumberAndView(number, Constants.planning);
			if(part == null) {
				//检查是否存在相同编号的EBOM
				part = WTPartUtil.getLatestPartByNumberAndView(number, Constants.design);
				if( part != null) {//如果EBOM存在PBOM不存在，则创建PBOM
					part = WTPartUtil.createPlanningPart(String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId()));
				} else {
					//如果系统中没有相同编号的零部件
					part = WTPartUtil.createPart(number, name, parentPart.getContainer(), folderPath, Constants.planning, typeName);
				}
			}

			IBAHelper helper = new IBAHelper(part);
			helper.setIBAValue(part, ibaMap);
		} catch (Exception e) {
			message = e.getLocalizedMessage();
			e.printStackTrace();
		}
		list.add(message);
		list.add(part);
		return list;
	}

	/**
	 * 及时创建中间件和辅件
	 *
	 * @author qianlong
	 * @date 2013-3-21
	 * @return
	 *
	 */
	public static List<Object> createMPPartRMI(Map<String, String> map, Map<String, String> ibaMap) {
		GLLogger.debug(CLASSNAME, "--createMPPartRMI--map--" + map + "--ibaMap--" + ibaMap);
		String parentPartOid = map.get("parentPartOid");
		String number = map.get("number");
		String name = map.get("name");
		String typeName = map.get("typeName");
		String folderPath = "";
		List<Object> list = new ArrayList<Object>();
		String message = "";
		WTPart part = null;
		try {
			WTPart parentPart = (WTPart) Util.getObjectByOid(WTPart.class, parentPartOid);
			folderPath = parentPart.getLocation();
//			if ("middle".equals(typeName)) {
//				typeName = propertiesUtil.getProperty("process-middle-part-type-name");
//			} else if ("assistant".equals(typeName)) {
//				typeName = propertiesUtil.getProperty("process-assistant-part-type-name");
//			}

			part = WTPartUtil.createPart(number, name, parentPart.getContainer(), folderPath, Constants.planning,
					typeName);
			IBAHelper helper = new IBAHelper(part);
			helper.setIBAValue(part, ibaMap);
		} catch (Exception e) {
			message = e.getLocalizedMessage();
			e.printStackTrace();
		}
		list.add(message);
		list.add(part);
		return list;
	}

	/**
	 * 修改工艺辅件的使用数量
	 *
	 * @author qianlong
	 * @date 2013-4-11
	 *
	 */
	public static boolean modifyAssistantPartQuantityRMI(String oid, Map<String, String> ibaMap) {
		GLLogger.debug(CLASSNAME, "map---" + ibaMap);
		boolean tag = true;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			IBAHelper helper = new IBAHelper(part);
			helper.setIBAValue(part, ibaMap);
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			tag = false;
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return tag;
	}

	/**
	 * 查询所有的工艺辅件
	 *
	 * @author qianlong
	 * @date 2013-4-11
	 * @return
	 *
	 */
	public static List<Map<String, String>> getAllAssistantPartRMI(Map<String, String> map) {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String typeName = propertiesUtil.getProperty("process-assistant-part-type-name");
		GLLogger.debug(CLASSNAME, "map---" + map + "--typeName--" + typeName + "---");
		try {
			String number = Util.formatSearchString(map.get(AttributeConstants.number));
			String name = Util.formatSearchString(map.get(AttributeConstants.name));

			QueryResult queryResult = WTPartUtil.getPartByLikeNumberNameType(typeName, number, name);
			while (queryResult.hasMoreElements()) {
				Map<String, String> returnMap = new HashMap<String, String>();
				WTPart part = (WTPart) queryResult.nextElement();
				returnMap.put("oid", Util.getStringOid(part));
				returnMap.put("containerId", Util.getStringOid(part.getContainer()));
				returnMap.put("partNumber", part.getNumber());
				returnMap.put("partName", part.getName());
				IBAHelper helper = new IBAHelper(part);
				returnMap.put("iskey", helper.getIBAValue("iskey"));
				returnMap.put("isSpecial", helper.getIBAValue("isSpecial"));
				returnMap.put("workShop", helper.getIBAValue("workShop"));
				returnMap.put("remark", helper.getIBAValue("remark"));
				returnMap.put("materialType", helper.getIBAValue("materialType"));
				returnMap.put("backupRate", helper.getIBAValue("backupRate"));
				returnMap.put("maxBackupCount", helper.getIBAValue("maxBackupCount"));
				returnMap.put("backupReason", helper.getIBAValue("backupReason"));
				returnMap.put("outsourcingUnits", helper.getIBAValue("outsourcingUnits"));
				returnMap.put("productionRatio", helper.getIBAValue("productionRatio"));
				returnMap.put("productionQuantity", helper.getIBAValue("productionQuantity"));
				returnMap.put("version", part.getVersionIdentifier().getValue() + "."
						+ part.getIterationIdentifier().getValue());
				returnMap.put("lifecycle", part.getLifeCycleState().getDisplay(SessionHelper.getLocale()));
				list.add(returnMap);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 通过零件获取ebom XML文件
	 *
	 * @author fly
	 * @date 2013-05-17
	 * @param partNumber
	 * @return
	 *
	 */

	public static byte[] getEBOMXmlRMI(String oid) {
		GLLogger.debug(CLASSNAME, "getEBOMXmlRMI--oid-" + oid);
		byte[] bytes = null;
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			bytes = PBOMHelper.getBOMXml(part, Constants.ebomDocEndwith);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 通过零件上传PBOM xml
	 *
	 * @author qianlong
	 * @date 2012-11-5
	 * @param oid
	 * @param bytes
	 *
	 */
	public static boolean saveEBOMXmlRMI(String oid, byte[] bytes) {
		boolean flag = false;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			PBOMHelper.saveBOMXmlWithOutCheckOut(part, bytes, Constants.ebomDocEndwith);
			transaction.commit();
			transaction = null;
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return flag;
	}

	/**
	 * 获取PVS
	 */
	public static Map<String, byte[]> getPVSAndMarkupRMI(Map<String, String> map) {
		Map<String, byte[]> pvsMap = new HashMap<String, byte[]>();
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, map.get("oid"));
			String viewName = map.get("viewName");
			if (!part.getViewName().equals(viewName)) {
				part = WTPartUtil.getLatestPartByNumberAndView(part, viewName);
			}
			QueryResult repResult = PublishUtils.getRepresentations(part);
			if (repResult == null) {
				return pvsMap;
			}
			while (repResult.hasMoreElements()) {
				Object object = repResult.nextElement();

				if (object instanceof Representation) {
					if (object instanceof DerivedImage) {
						Representation representation = (Representation) ContentHelper.service
								.getContents((DerivedImage) object);
						Map<ContentRoleType, String> endWithMap = new HashMap<ContentRoleType, String>();
						endWithMap.put(ContentRoleType.SECONDARY, ".ol");
						endWithMap.put(ContentRoleType.PRODUCT_VIEW_ED, ".pvs");
						pvsMap = EPMDocumentUtil.getRepByRoleAndEndWith(representation, endWithMap);
					}
					String name = "";
					for (String str : pvsMap.keySet()) {
						if (str.endsWith(".pvs")) {
							name = str.replace(".pvs", "");
							break;
						}
					}
					if (!name.equals("") && object instanceof Viewable) {
						QueryResult markupResult = ViewMarkUpHelper.service.getMarkUps((Viewable) object);
						Map<String, byte[]> markupMap = EPMDocumentUtil.getMarkupImgNoSmallImg(markupResult, name
								+ ".etb", null, false);
						pvsMap.putAll(markupMap);
					}

				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return pvsMap;
	}

	/**
	 * @author fly
	 * @date 2013-5-31
	 * @param isCompared
	 *            ：YES| NO
	 *
	 */
	public static boolean saveEbomComparedFlageRMI(WTPart part) {
		boolean tag = false;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			tag = PBOMHelper.saveEbomComparedFlage(part, null);
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return tag;
	}

	/**
	 * @author
	 * @date 2013-6-5
	 * @return
	 *
	 */
	public static List<String> getCurrentUserInfoRMI() {
		return ProcessEditorToWCIntfRMI.getCurrentUserInfoRMI();
	}

	/**
	 * 保存 PBOM 表示
	 *
	 * @author qianlong
	 * @date 2013-9-12
	 * @param oid
	 * @param zipFileContent
	 * @param fileName
	 * @throws WTException
	 */
	public static void savePBomRepresentation(String oid, byte[] zipFileContent, String fileName) throws WTException {
		System.out.println("Try to save default representation");
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
		if (part == null)
			throw new WTException("Part not found with oid: " + oid + ".");

		Representation defaultRepresentation = RepresentationHelper.service.getDefaultRepresentation(part);
		if (defaultRepresentation != null) {
			Transaction tx = new Transaction();
			try {
				tx.start();
				RepresentationHelper.service.deleteRepresentation(defaultRepresentation, true);
				tx.commit();
			} catch (WTException e) {
				tx.rollback();
				throw e;
			}
		}

		WVSProperties wvsp = new WVSProperties();
		BufferedOutputStream bos = null;
		File tempFile = null;
		try {
			tempFile = File.createTempFile("_" + oid + "_" + System.currentTimeMillis(), ".zip", new File(wvsp
					.getPublishTempUploadDir()));
			bos = new BufferedOutputStream(new FileOutputStream(tempFile));
			bos.write(zipFileContent);
			bos.flush();
		} catch (IOException e) {
			throw new WTException(e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		PublishJob job = new PublishJob(true, PublishUtils.getRefFromObject(part), (NavigationCriteria) null,
				(NavigationCriteria) null, true, "Representation Name", "Representation Description", 0);
		job.setNumberNameVersion(part);
		job.setProcessType(PublishJob.JOB_PROCESS_NORMAL);
		job.setRequestType(PublishJob.JOB_REQUEST_DOCLOCAL);
		job.setRequestSource(PublishJob.JOB_SOURCE_MANUAL);
//		job.setSyncInfo(null);
		String publishOptions = "<wvsoptions>tempfile=" + tempFile.getName() + ",thumbnail=true" + ",origfile="
				+ fileName;
		job.setActionFileString(publishOptions);
		job.setSubmitTime(System.currentTimeMillis());
		job.setLogFile(new File(wvsp.getPublishTempUploadDir() + File.separator + "_epm_publish.log"));

		PublishQueueHelper.addPublishEntry(job);
	}

	public static boolean updatePartNumberRMI(WTPart part,String number){
		return WTPartUtil.updatePartNumber(part, number);
	}

	public static ErpResult saveErpCjPart2Wc(List<Wzk> dataList) {
		ErpResult result = new ErpResult();
		List<Wzk> partList = new ArrayList<Wzk>();
		Transaction transaction = new Transaction();
		StringBuffer msg = new StringBuffer();
		try {
			transaction.start();
			if (dataList == null || dataList.isEmpty())
				return result;
			WTPart part = null;
			WTPart ppart = null;
			String number = null;
			for (Wzk wzk : dataList) {
				number = wzk.getInvcode();
				ppart = wzk.getPpart();
				WTPart tempPart = WTPartUtil.getLatestPartByPartNumber(number);
				if (tempPart != null && isUsedWTPart(tempPart)) {
					msg.append("编号【" + number + "】已经被使用，不是零件\n");
					continue;
				} else if (tempPart != null) {
					part = tempPart;
				} else {
					part = WTPartUtil.createPlanningPartByWzlb(ppart, number, wzk.getInvname(), wzk.getInvclasscode());

					//设置IBA属性：存货编码、型号牌号、规格、技术条件
					IBAHelper ibaHelper = new IBAHelper(part);
					Map<String, String> ibaMap = new HashMap<String, String>();
					//ibaMap.put("CHBM", number);//存货编码
					ibaMap.put("XHPH", wzk.getInvtype());//型号牌号
					ibaMap.put("CSIZE", wzk.getInvspec());//规格
					ibaMap.put("JSTJ", wzk.getDef1());//技术条件

					//必填属性，赋予默认值
					ibaMap.put("PHASE_CODE", "Y");
					ibaMap.put("KEYCOMPONENT", "N");

					if(wzk.getWzlb() != null && wzk.getWzlb().startsWith("01")) {
						ibaMap.put("MTYPE", "元器件");
						ibaMap.put("CTYPE", "外购件");
					} else if (wzk.getWzlb() != null && wzk.getWzlb().startsWith("02")) {
						ibaMap.put("MTYPE", "标准件");
						ibaMap.put("CTYPE", "标准件");
					}
					GLLogger.debug(CLASSNAME, "----set IBA value---CHBM:"+number+"   XHPH:"+"  wzlb:"+wzk.getWzlb()
							+wzk.getInvtype()+"   CSIZE:"+wzk.getInvspec()+"  JSTJ:"+wzk.getDef1());
					ibaHelper.setIBAValue(part, ibaMap);

					//设置生命周期状态为"已批准"
					CmLifecycleHelper.setLifecycleState(part, "APPROVED");
				}

				wzk.setPart(part);
				partList.add(wzk);
			}
			transaction.commit();
		} catch (Exception e) {
			partList = null;
			e.printStackTrace();
		}
		if (!"".equals(msg.toString())) {
			partList = null;
		}
		result.setDataList(partList);
		result.setMsg(msg.toString());
		return result;
	}

	/**
	 * 查询部门(主制单位|辅制单位)
	 * @param partId
	 * @return
	 */
	public static String[] queryDept(String partId) {
		WTContainer wtContainer = null;
		WTPart part = (WTPart)  Util.searchRMI(WTPart.class,Long.parseLong(partId));
		wtContainer = part.getContainer();
		Map<String, String> bms = PbomUtil.getAllCheJianAndXiangMuBuMapGYZZ(wtContainer,PbomUtil.GROUP_GYRWFG_NAME);
		String[] sort = LoadConfig.getInstance().getMainPlant();
		List<String> notSortList = new ArrayList<String>();
		List<String> sortList = new ArrayList<String>();
		String[] bm = new String[bms.size()];

		for(int i=0;i<sort.length;i++){
			Iterator it = bms.keySet().iterator();
			boolean isSort = false;
			while (it.hasNext()) {
				String key =  (String) it.next();
				if("".equals(key)) {
					continue;
				}
				if(sort[i].equals(key)){
					sortList.add(key);
					isSort = true;
					break;
				}
			}
		}

		Iterator it = bms.keySet().iterator();
		while (it.hasNext()) {
			String key =  (String) it.next();
			if("".equals(key)) {
				continue;
			}
			boolean isSort = false;
			for(int i=0;i<sort.length;i++){
				if(sort[i].equals(key)){
					isSort = true;
					break;
				}
			}
			if(!isSort){
				notSortList.add(key);
			}
		}

		sortList.addAll(notSortList);

		for(int i=0;i<sortList.size();i++){
			bm[i] = sortList.get(i);
		}

		return bm;
	}

	public static Long queryLatestPartIdByNumberRMI(String number) throws WTException{
		long id  = -1;
		WTPart part = WTPartUtil.getLatestPartByPartNumber(number);
		if(part==null)return id;
		id = PersistenceHelper.getObjectIdentifier(part).getId();
		return id;
	}

	public static boolean isUsedWTPart(WTPart part) throws WTException{
		boolean flag = false;
		QueryResult  qr = WTPartHelper.service.getUsesWTPartMasters(part);
		if(qr.hasMoreElements()){
			flag = true;
		}
		return flag;
	}

	public static WTPart createPlanningPartRMI(WTPart parentPart , String number,String name) throws WTPropertyVetoException, RemoteException, WTException {
		return WTPartUtil.createPlanningPart(parentPart, number, name);
	}

	public static WTPart createPlanningPartByWzlbRMI(WTPart parentPart , String number,String name,String wzlbbm) throws WTPropertyVetoException, RemoteException, WTException {
		return WTPartUtil.createPlanningPartByWzlb(parentPart, number, name, wzlbbm);
	}

	 public static WTPart createPartForAddPart(String number, String name, HashMap attributes, HashMap ibaattributes,
	            WTContainer containerRef){
		 return ImportPartUtil.createPart(number, name, attributes, ibaattributes, containerRef, false);
	 }

	 public static  List<WTPart> queryPartByLikeNumberNameView(String view, String number, String name){
		List<WTPart> dataList = new ArrayList<WTPart>();
		try {
			QueryResult  qr = WTPartUtil.getPartByLikeNumberNameView(view, number, name);
			if(qr == null) return dataList;
			WTPart part ;
			while(qr.hasMoreElements()){
				part = (WTPart)qr.nextElement();
//				String containerName = part.getContainerName();
//				if ("八院标准紧固件库".equals(containerName) || "八院金属材料库".equals(containerName) || "八院非金属材料库".equals(containerName)
//						|| "八院复合材料库".equals(containerName) || "八院元器件库".equals(containerName)) {
					dataList.add(part);
				}
//			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	 }

	 public static ErpResult saveErpClAttributeToWcRMI(List<Wzk> wzkList){
		 ErpResult result = new ErpResult();
		 if(wzkList==null) return result;
		 StringBuffer msg = new StringBuffer();
		 Transaction transaction = new Transaction();
		 String number = null;
		 try {
			 transaction.start();
			 for(Wzk wzk: wzkList){
				 number = wzk.getTh();
				 if(number==null||"".equals(number))return result;
				 Long viewId = getViewByNameRMI(LoadConfig.getInstance().getPbomView());
					WTPart part = null;
					part = WTPartUtil.getPartByNumberAndView(number, viewId);
					part = (WTPart) WorkInProcessUtil.checkout(part);
					IBAHelper helper = new IBAHelper(part);
					Map<String,String> map = new HashMap<String,String>();
					ErpUtil.setPartByWzk(map, wzk, ErpUtil.ERP_OP_TYPE_CLBM);
					helper.setIBAValue(part, map);
					part = (WTPart) WorkInProcessUtil.checkin(part,"设置材料属性");
					msg.append("编号【"+number+"】属性设置成功\n");
		 	}
			transaction.commit();
		 } catch (WTPropertyVetoException e) {
			    transaction.rollback();
			 	msg = new StringBuffer();
			 	msg.append("编号【"+number+"】属性设置失败\n");
				GLLogger.error(e.getMessage());
				e.printStackTrace();
			} catch (RemoteException e) {
				 transaction.rollback();
				msg = new StringBuffer();
				msg.append("编号【"+number+"】属性设置失败\n");
				GLLogger.error(e.getMessage());
				e.printStackTrace();
			} catch (WTException e) {
				transaction.rollback();
				msg = new StringBuffer();
				msg.append("编号【"+number+"】属性设置失败\n");
				GLLogger.error(e.getMessage());
				e.printStackTrace();
			}

		 result.setMsg(msg.toString());
		 return result;

	 }

	 public static ErpResult saveLineAndTypeAttributeToWcRMI(Map<String,String> map){
		 GLLogger.debug(CLASSNAME, ">>>>>>saveLineAndTypeAttributeToWcRMI>>>>>map:"+map);
		 ErpResult result = new ErpResult();
		 String type = (String)map.get("type");
		 String number = (String)map.get("number");
		 Long viewId = getViewByNameRMI(LoadConfig.getInstance().getPbomView());
		 WTPart part = null;
		 Transaction transaction = new Transaction();
		 try {
			transaction.start();
			part = WTPartUtil.getPartByNumberAndView(number, viewId);
			//part = (WTPart) WorkInProcessUtil.checkout(part);
			IBAHelper helper = new IBAHelper(part);
			String back = "";
			Map<String,String> valueMap = new HashMap<String,String>();
			if("1".equals(type)){
				String mxlx = map.get("MTYPE");
				back = "设置零组件生产类型";
				valueMap.put("MTYPE", Util.formateString (mxlx));
			}else if("2".equals(type)){
				back = "设置工艺路线";
				String zzbm = map.get("zzcj");
				String fzbm = map.get("fzcj");
				valueMap.put("ZZCJ", Util.formateString (zzbm));
				valueMap.put("FZCJ", Util.formateString (fzbm));
			}

			helper.setIBAValue(part, valueMap);
			//part = (WTPart) WorkInProcessUtil.checkin(part,back);
			transaction.commit();
		} catch (WTException e) {
			transaction.rollback();
			GLLogger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			transaction.rollback();
			GLLogger.error(e.getMessage());
			e.printStackTrace();
		}

		 return result;
	 }

	 public static String getPartTypeByPartOid(long oid) {
		 try {
			return WTPartUtil.getPartTypeByPartOid(oid);
		} catch (WTException e) {
			e.printStackTrace();
		}
		 return "";
	 }

	 public static String getPartIBAValueByPartOid(long oid,String ibaKey) {
		 try {
			return WTPartUtil.getPartIBAValueByPartOid(oid, ibaKey);
		} catch (WTException e) {
			e.printStackTrace();
		}
		 return "";
	 }

	 public static void savePartIBAValue(long oid,Map<String,String> ibaMap) {
		 try {
			WTPart part = WTPartUtil.getPartByOid(oid);
			 if(part != null && ibaMap != null && !ibaMap.isEmpty()) {
				 IBAHelper ibaHelper = new IBAHelper(part);
				 ibaHelper.setIBAValue(part, ibaMap);
			 }
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	 }

	/**
	 * 保存存货编码在link上
	 *
	 * @param parentNumber
	 *            父件编号
	 * @param oid
	 * @param ibaMap
	 *            IBA属性键值对
	 */
	public static void saveCHBMIBAValueForPartLink(String parentNumber,
			long oid, Map<String, String> ibaMap) {
		try {
			Long viewId = getViewByNameRMI(LoadConfig.getInstance().getPbomView());
			WTPart parentPart = WTPartUtil.getPartByNumberAndView(parentNumber, viewId);
			if (parentPart != null) {
				WTPart part = WTPartUtil.getPartByOid(oid);
				if (part != null) {
					WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(parentPart, (WTPartMaster) part.getMaster());
					if (link != null) {
						IBAHelper ibaHelper = new IBAHelper(link);
						ibaHelper.setIBAValue(link, ibaMap);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static String getPartLinkIBAValueByPartOid(String parentPartNumber,
			long oid, String ibaKey) {
		try {
			if(parentPartNumber == null || "".equals(parentPartNumber)) {
				return null;
			}
			Long viewId = getViewByNameRMI(LoadConfig.getInstance().getPbomView());
			WTPart parentPart = WTPartUtil.getPartByNumberAndView(parentPartNumber, viewId);
			if (parentPart != null) {
				WTPart part = WTPartUtil.getPartByOid(oid);
				if (part != null) {
					WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(parentPart, (WTPartMaster) part.getMaster());
					if (link != null) {
						IBAHelper ibaHelper = new IBAHelper(link);
						return ibaHelper.getIBAValue(ibaKey);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过产品oid获取该产品下的所有批次号
	 *
	 * @author longxiuchuan
	 * @param oid 产品oid
	 * @return List<String> 批次号集合
	 */
	public static Vector<String> getBatchsByProductOid(long oid) {
		Vector<String> list = new Vector<String>();
		list.add("");
		try {
			PDMLinkProduct product = WTContainerUtil.getProductByOid(oid);
			if(product != null) {
				String productName = product.getName();
				List<Batch> allBatchs = DBUtil.getBatchesByProduct(String.valueOf(oid), productName);
				if(allBatchs != null) {
					for (Batch batch : allBatchs) {
						list.add(batch.getName());
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 设置零组件的生命周期状态
	 *
	 * @author longxiuchuan
	 * @param oid
	 * @param stateKey 生命周期状态的key值
	 */
	public static void setPartState(long oid,String stateKey) {
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTPart part = WTPartUtil.getPartByOid(oid);
			if(part == null) {
				GLLogger.debug(CLASSNAME, "------part is not exsit for " + oid);
				return;
			}
			State state = State.toState(stateKey);
			if(state == null) {
				GLLogger.debug(CLASSNAME, "------state is not exsit for " + stateKey);
				return;
			}
	        LifeCycleHelper.service.setLifeCycleState(part, state);
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
	}

	/**
	 * 设置零组件的生命周期状态
	 *
	 * @author longxiuchuan
	 * @param oid
	 * @param stateKey 生命周期状态的key值
	 */
	public static String setPartStateForPBOM(long oid,String stateKey,String batch) {
		String msg = "";
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTPart part = WTPartUtil.getPartByOid(oid);
			if(part == null) {
				GLLogger.debug(CLASSNAME, "------part is not exsit for " + oid);
				msg = part.getNumber() + "不存在！";
				return msg;
			}
			IBAHelper ibaHelper = new IBAHelper(part);
			ibaHelper.setIBAValue(part,"BATCH",batch);
            ibaHelper.updateAttributeContainer(part);
            ibaHelper.updateIBAHolder(part);
//			ibaHelper.setIBAValue("BATCH",batch);
//            ibaHelper.updateAttributeContainer(part);
            part = (WTPart) PersistenceHelper.manager.refresh(part);
			State state = State.toState(stateKey);
			if(state == null) {
				GLLogger.debug(CLASSNAME, "------state is not exsit for " + stateKey);
				msg = stateKey+ "  状态不存在！";
				return msg;
			}
			LifeCycleHelper.service.setLifeCycleState(part, state);
//			part = (WTPart) PersistenceHelper.manager.refresh(part);
//            PersistenceServerHelper.manager.update(part);
		}  catch (Exception e) {
            e.printStackTrace();
        } finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return msg;
	}

	/**
	 *
	 * 判断当前零部件下的工艺文件是否已经批准
	 *
	 * @param partOid
	 * @return
	 */
	public static boolean checkPartTechnicsIsReleased(long partOid) {
		try {
			WTPart part = WTPartUtil.getPartByOid(partOid);
			if(part == null) {
				GLLogger.debug(CLASSNAME, "------part is not exsit for " + partOid);
				return true;
			}
			List<WTDocument> list = ProcessPlanHelper.getAllProcessZipDoc(part);
			if(list != null) {
				for (WTDocument document : list) {
					String state = document.getState().getState().getDisplay(Locale.CHINA);
					if(!"已批准".equals(state)) {
						return false;
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static WTPart getLatestPart(String number,String viewName) {
		WTPart part = null;
		try {
			part = WTPartUtil.getLatestPartByNumberAndView(number, viewName);

		} catch (WTException e) {
			e.printStackTrace();
		}
		return part;
	}

	 public static List<String> getUp_PartsRMI(String number,String source){
		 List<String> list = null;
		try {
			list = PBOMEditorUtil.getUp_Parts(number, source);
		} catch (WTException e) {
			e.printStackTrace();
		}
		 return list;
	 }

	 public static HashMap<WTPart ,List<String>>  getDownPartOnlyStepFromPdmSystemRMI(String oid){
		 HashMap<WTPart ,List<String>>  retMap =  WTPartUtil.getDownPartOnlyStepFromPdmSystem(oid);
		 return retMap;
	 }

	public static Map<String, String> getReleasedInfo(long oid) {
		try {
			WTPart part = WTPartUtil.getPartByOid(oid);
			if (part == null) {
				GLLogger.debug(CLASSNAME, "------part is not exsit for " + oid);
				return null;
			}
			List<WTDocument> list = WTPartUtil.getTechnicsDocumentByPart(part);
			if(list != null) {
				ArrayList<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
				for (WTDocument wtDocument : list) {
					wtDocument = (WTDocument)VersionControlHelper.service.allVersionsOf(wtDocument).nextElement();
					Map<String, String> tempMap = SWXMLUtil.getReleasedInfo(wtDocument);
					if(tempMap == null) {
						continue;
					}
					Map<String, String> map = new HashMap<String,String>();
					map.put("CLDEZT", tempMap.get("CLDEZT"));
					map.put("technicsNumber", tempMap.get("pplanNumber"));
					map.put("state", wtDocument.getState().getState().getDisplay(Locale.CHINA));

					GLLogger.debug(CLASSNAME, "------map---" + map);
					mapList.add(map);
				}
				if(mapList.size()==1){
					return mapList.get(0);
				}else if(mapList.size()>1){
					for (Map<String, String> map : mapList) {
						String state = map.get("state");
						if("已批准".equals(state)){
							return map;
						}
					}
					return mapList.get(0);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean startPbomReleasedNoticeWorkflow(long oid) {
		boolean flag = false;
		try {
			WTPart part = WTPartUtil.getPartByOid(oid);
			if (part == null) {
				GLLogger.debug(CLASSNAME, "------part is not exsit for " + oid);
			}
			Map<String, String> variablesMap = new HashMap<String, String>();
			String partNumber = part.getNumber();
			String worlflowTemplate = WorkflowConstants.PBOMRELEASEDNOTICE;
			flag = WorkflowUtil.startProcess(part, worlflowTemplate, partNumber, variablesMap);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return flag;
	}


	public static String replacePartRepFromEBOMToMBOM(String number,Boolean isAll) {
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			if(isAll){
				String result = "";
				WTPart rootPart = CSCPart.getPartByNumberAndViewName(number, "Manufacturing");
				List<WTPart> allPart = new ArrayList<WTPart>();
	            allPart.add(rootPart);
				 getAllChildPart(rootPart,allPart);
				 for (WTPart part : allPart) {
					 String ctype = IBAHelper.getIBAValue(part, "CTYPE");
					 String mtype = IBAHelper.getIBAValue(part, "MTYPE");
					 if("标准件".equals(ctype)||"标准件".equals(mtype)) {
	            		continue;
					 }
					 String msg = replacePartRep(part.getNumber());
					 if(!"".equals(msg)){
						 result = result+msg+"\r\n";
					 }

				 }
	             return result;

			}else{
				return replacePartRep(number);
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

        return "";
	}
	public static void getAllChildPart(WTPart ppart,List<WTPart> list) throws WTException {
		QueryResult qr = WTPartHelper.service.getUsesWTParts(ppart, ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class));
		WTPart cpart = null;
		while(qr.hasMoreElements()) {
			Persistable[] per = (Persistable[])qr.nextElement();
			Persistable pper = per[1];
			if(pper instanceof WTPart) {
				cpart = (WTPart)per[1];
				String viewName = cpart.getViewName();
				if("Design".equals(viewName)) {
					cpart = CSCPart.getPartByNumberAndViewName(cpart.getNumber(),"Manufacturing");
				}
			} else if (pper instanceof WTPartMaster) {
				cpart = CSCPart.getPartByNumberAndViewName(((WTPartMaster)pper).getNumber(),"Manufacturing");
			}

			if(cpart != null) {
				if(!list.contains(cpart)){
					list.add(cpart);
				}

				getAllChildPart(cpart,list);
			}
		}
	}

	public static String replacePartRep(String number){
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
        	WTPart p1 = CSCPart.getPartByNumberAndViewName(number, "Design");
        	WTPart p2 = CSCPart.getPartByNumberAndViewName(number, "Manufacturing");
        	if(p1==null){
        		return number+"的EBOM不存在";
        	}
        	if(p2==null){
        		return number+"的PBOM不存在";
        	}
        	if(p1!=null&&p2!=null){
        		 Representation representation = RepresentationHelper.service.getDefaultRepresentation((Representable) p1);
        		 Representation representation2 = RepresentationHelper.service.getDefaultRepresentation((Representable) p2);
                 if(representation2!=null){
     				RepresentationHelper.service.deleteRepresentation(representation2);
                 }

                 if(representation==null){
                	 return number+"的EBOM不存在可视化";
                 }

                 String name = "default(来自"+p1.getNumber()+"."+p1.getVersionIdentifier().getValue()+"."+p1.getIterationIdentifier().getValue()+".Design)";
                 DerivedImage localDerivedImage2 = RepUpdateUtils.copyDerivedImage((DerivedImage) representation, p2, true, true, true,name, true, true);
     			 RepresentationHelper.service.setDefaultRepresentation(p2, representation, false);
        	}

			//RepsAndMarkupsClientHelper.pasteToRepresentable(representation.toString(), as)
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        return "";
	}
	public static String getGYSLFromWNC(String parentPartNumber, String partNumber) {
		try {
			WTPart ppart = WTPartUtil.getLatestPartByNumberAndView(parentPartNumber, Constants.planning);

			if(ppart!=null) {
				QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(ppart);
				//List<String> allMCildNumbers = new ArrayList<>();
				while (qr.hasMoreElements()) {
					WTPartUsageLink usageLink = (WTPartUsageLink) qr.nextElement();
					WTPartMaster mchild = (WTPartMaster) usageLink.getRoleBObject();
					//allMCildNumbers.add(mchild.getNumber());
					//找到M子件
					if(mchild.getNumber().equals(partNumber)) {
						IBAHelper linkHelper = new IBAHelper(usageLink);
						String ibasysl = linkHelper.getIBAValue("GYSL");
						if(Tools.isNull(ibasysl)){
							ibasysl =  (int)usageLink.getQuantity().getAmount()+"";
						}
						return ibasysl;
					}
				}
				//没找到M子件，则取ebom对应子件
				WTPart epart = WTPartUtil.getLatestPartByNumberAndView(parentPartNumber, Constants.design);
				WTPart eChildpart = WTPartUtil.getLatestPartByNumberAndView(partNumber, Constants.design);
				return getGysl(epart,eChildpart);

			}else{
				ppart = WTPartUtil.getLatestPartByNumberAndView(parentPartNumber, Constants.design);
				WTPart eChildpart = WTPartUtil.getLatestPartByNumberAndView(partNumber, Constants.design);
				return getGysl(ppart,eChildpart);
			}



		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static String getGysl(WTPart epart,WTPart eChildpart)throws WTException{
		if(epart!=null && eChildpart!=null){

			WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(epart,eChildpart.getMaster());
			if(link==null) return null;

			IBAHelper linkHelper = new IBAHelper(link);
			String ibasysl = linkHelper.getIBAValue("GYSL");
			if(Tools.isNull(ibasysl)){
				ibasysl =  (int)link.getQuantity().getAmount()+"";
			}
			return ibasysl;
		}
		return "";
	}

	/*
	 * 获取该零件属性信息
	 */
	public static Map<String,String> getProsFromWNC( String partNumber) {
		Map<String,String> pros = new HashMap<String,String>();

		try {
			WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber, Constants.planning);
			if(part!=null){
				String oid = part.getPersistInfo().getObjectIdentifier().getId()+"";
				String version = part.getVersionInfo().getIdentifier().getValue()+"."+part.getIterationInfo().getIdentifier().getValue();
				String PINDEX = IBAHelper.getIBAValue(part, "PINDEX");
				String MINDEX = IBAHelper.getIBAValue(part, "MINDEX");
				String CINDEX = IBAHelper.getIBAValue(part, "CINDEX");
				String PHASE_CODE = IBAHelper.getIBAValue(part, "PHASE_CODE");
				String BATCH = IBAHelper.getIBAValue(part, "BATCH");

				pros.put("oid", oid);
				pros.put("version", version);
				pros.put("PINDEX", PINDEX);
				pros.put("MINDEX", MINDEX);
				pros.put("CINDEX", CINDEX);
				pros.put("PHASE_CODE", PHASE_CODE);
				pros.put("BATCH", BATCH);
				return pros;
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pros;
	}

	public static boolean reName(String partNumber, String newName) {
		String user = "";
		try {
			user = wt.session.SessionHelper.manager.getPrincipal().getName();
			wt.session.SessionHelper.manager.setPrincipal("Administrator");
		} catch (Exception e) {
		}
		boolean flag = false;
		try {
			WTPart part = WTPartUtil.getLatestPartByPartNumber(partNumber);
			if(part!=null&&!part.getName().equals(newName)){
	            WTPartMaster master = (WTPartMaster) part.getMaster();
	            WTPartMasterIdentity idy = (WTPartMasterIdentity) master.getIdentificationObject();
	            try {
	                idy.setName(newName);
	            } catch (WTPropertyVetoException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            master = (WTPartMaster) IdentityHelper.service
	                    .changeIdentity(master, idy);
				flag = true;
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} finally {
			if (!"".equals(user)) {
				try {
					wt.session.SessionHelper.manager.setPrincipal(user);
				} catch (Exception e) {
				}
			}
		}

		return flag;
	}
	
	/**
	  *  获取部件新增软属性值 -- add by hz - 20191217
	 * @param partNumber
	 * @param viewName
	 * @param ibaNameList
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 */
	public static Map<String, String> getIbaValues(String partNumber, String viewName, List<String> ibaNameList) throws WTException, RemoteException {
		Map<String, String> result = new HashMap<String, String>();
		WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber, viewName);
		IBAHelper ibaHelper = new IBAHelper(part);
		String tempValue = null;
		for (String ibaName : ibaNameList) {
			tempValue = ibaHelper.getIBAValue(ibaName);
			result.put(ibaName, tempValue == null ? "" : tempValue);
		}
		return result;
	}

	/**
	 * 获取部件替代件编号列表
	 *
	 * @author cjh
	 * @date 2023-2-20
	 *
	 */
	public static List<String> getWTPartUsageListRMI(String oid) {
		List<String> list = new ArrayList<String>();
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			if(part != null) {
				QuerySpec qSpec = new QuerySpec(WTPartAlternateLink.class);
				int[] index = { 0 };
				long longId = PersistenceHelper.getObjectIdentifier(part.getMaster()).getId();
				SearchCondition scCondition = new SearchCondition(WTPartAlternateLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL,
						longId);
				qSpec.appendWhere(scCondition, index);
				QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
				while(qResult.hasMoreElements()) {
					WTPartAlternateLink link = (WTPartAlternateLink) qResult.nextElement();
					WTPartMaster used = (WTPartMaster) link.getRoleBObject();
					list.add(used.getNumber());
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}
}
