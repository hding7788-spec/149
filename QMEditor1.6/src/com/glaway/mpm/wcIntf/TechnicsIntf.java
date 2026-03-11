package com.glaway.mpm.wcIntf;

import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.model.CMatBean;
import com.glaway.mpm.model.CheckTechnics;
import com.glaway.mpm.model.TechnicsOutputFormBean;
import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.model.data.CmTreeNode;
import com.glaway.mpm.sjzyk.SjzykBean;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.ValueCache;
import com.glaway.mpm.visual.log.VaLogger;
import ext.casc.sop.util.StringUtil;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.dom4j.Element;
import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import javax.vecmath.Matrix4d;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;


/**
 * @author ylshao
 * @ClassName: ProcessEditorToWCIntf
 * @Description:
 * @date 2012-11-28
 *
 */
public class TechnicsIntf implements RemoteAccess {
	private static VaLogger logger = VaLogger.getLogger(TechnicsIntf.class);
	private static String productNumberAndName = null;
	private static Map<String,String> systemConfigurationMap = null;
	private static Map<String, List<Vector>> cacheMap = new HashMap<String, List<Vector>>();

	/**
	 * 工艺上载
	 *
	 * @description category:(rework:返工工艺上载,temp:临时工艺上载)
	 * @author ylshao
	 * @date 2012-11-21
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static HashMap<String, String> uploadTechnics(
			byte[] bytes, HashMap<String, String> map, String xmlVersion,
			String note) throws RemoteException, InvocationTargetException {
//		logger.debug(">>>>>map=" + map);
//		logger.debug("oid=" + map.get("oid") + " partNumber="
//				+ map.get("partNumber") + " name=" + name + " partType="
//				+ map.get("partType") + " technicsType=" +map.get("technicsType") +" xmlVersion=" + xmlVersion+" technicsNumber="+map.get("technicsNumber")+" technicsName="+map.get("technicsName"));
//		logger.debug("note=" + note);
		return (HashMap<String, String>) remoteMethodInvoke(
				"uploadTechnicsRMI", new Class[] { byte[].class, Map.class, String.class, String.class},
				new Object[] {bytes, map, xmlVersion, note});
	}


	/**再次上传工艺文艺文件
	 * @author caolei
	 * @date 2012-11-21
	 * @param bytes
	 * @param map
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */


	public static HashMap<String, String> reUploadTechnics(
			byte[] bytes, HashMap<String, String> map) throws RemoteException, InvocationTargetException {
		return (HashMap<String, String>) remoteMethodInvoke(
				"reUploadTechnicsRMI", new Class[] { byte[].class, Map.class},
				new Object[] {bytes, map});
	}


	/**
	 * 工艺提交签审
	 *
	 * @author ylshao
	 * @date 2012-11-21
	 * @return
	 */
	public static HashMap submitSigned(Map<String, Object> inputMap,
			byte[] technicsZip) {
		logger.debug("inputMap= " + inputMap);
		HashMap map = null;
		try {
			map = (HashMap) remoteMethodInvoke("submitSignedRMI", new Class[] {
					Map.class, byte[].class }, new Object[] { inputMap,
					technicsZip });
		} catch (Exception e) {
			return null;
		}
		// startRouteConfirmUrl();
		return map;
	}

	/**
	 * 材料定额提交签审
	 *
	 * @param inputMap
	 * @param technicsZip
	 * @return
	 */
	public static HashMap<String,String> submitCMatSigned(List<CMatBean> list,String technicsNumber) {
//		logger.debug("list= " + list);
		HashMap<String,String> map = null;
		try {
			map = (HashMap<String,String>) remoteMethodInvoke("submitCMatSigned", new Class[] {List.class,String.class}, new Object[] { list,technicsNumber });
		} catch (Exception e) {
			return null;
		}
		return map;
	}


	/**
	 * 材料定额提交签审
	 * @param partNumber
	 *
	 * @param inputMap
	 * @param technicsZip
	 * @return
	 */
	public static HashMap<String,String> submitBatchCMatSigned(Map<String,List<CMatBean>> dataMap, Map<String, String> taskMap, String workItemOid, String partNumber) {
//		logger.debug("list= " + list);
		HashMap<String,String> map = null;
		try {
			map = (HashMap<String,String>) remoteMethodInvoke("submitBatchCMatSigned", new Class[] {Map.class, Map.class, String.class,String.class}, new Object[] { dataMap,taskMap,workItemOid,partNumber });
		} catch (Exception e) {
			return null;
		}
		return map;
	}

	/**
	 * 判断是否已经工艺提交签审
	 *
	 * @author LngXiuChuan
	 * @date 2013-10-30
	 * @return
	 */

	public static boolean isSubmited(String docNumber) {
//		logger.debug("docNumber= " + docNumber);
		boolean flag = false;
		try {
			flag = (Boolean) remoteMethodInvoke("isSubmited", new Class[] { String.class }, new Object[] { docNumber });
		} catch (Exception e) {
			return false;
		}
		// startRouteConfirmUrl();
		return flag;
	}
	
	/** 
	  * @Description: 设置文档状态
	  * @date 2025年10月29日下午6:40:50
	  * @author Liluwen
	  * @param docNumber
	  * @return  
	  * @return 
	*/
	public static boolean setDocumentStatus(String docNumber,String status) {
		boolean flag = false;
		try {
			flag = (Boolean) remoteMethodInvoke("setDocumentStatus", new Class[] { String.class,String.class }, new Object[] { docNumber ,status});
		} catch (Exception e) {
			return false;
		}
		return flag;
	}

	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @Description: 查看工艺历史版本
	 * @param @param type:rework,temp
	 * @param @return
	 * @return List<List<String>>
	 * @throws
	 */
	public static List<List<String>> getProcessPlanHistory(String oid,
			String type, String processName,String technicsType) throws RemoteException, InvocationTargetException {
		return (List<List<String>>) remoteMethodInvoke(
				"getProcessPlanHistoryRMI", new Class[] { String.class,
						String.class, String.class, String.class }, new Object[] { oid, type,
						processName,technicsType });
	}

	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @Description: 获取历史工艺
	 * @param @param partOid
	 * @param @param processPlanOid
	 * @param @param xmlVersion
	 * @param @return
	 * @return Vector<Object>
	 * @throws
	 */
	public static Vector<Object> getProcessPlanbyOid(String partOid,
			String processPlanOid, String xmlVersion) throws RemoteException, InvocationTargetException {
		return (Vector<Object>) remoteMethodInvoke("getProcessPlanbyOidRMI",
				new Class[] { String.class, String.class, String.class },
				new Object[] { partOid, processPlanOid, xmlVersion });
	}

	/**
	 * 通过零件的oid和工艺规程的名称查询最新的工艺规程,工艺更新时候使用
	 *
	 * @author fly
	 * @date 2013-06-07
	 * @param
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @throws WTException
	 * @throws PropertyVetoException
	 */
	public static Vector getLastProcessPlan(String oid, String processName) throws RemoteException, InvocationTargetException {
//		logger.debug("oid= " + oid + "  processName= " + processName);
		return (Vector) remoteMethodInvoke("getLastProcessPlanRMI",
				new Class[] { String.class, String.class }, new Object[] { oid,
						processName });
	}


	public static Vector getLastProcessDocument(String technicsNumber) throws RemoteException, InvocationTargetException {
//		logger.debug("technicsNumber= " + technicsNumber);
		return (Vector) remoteMethodInvoke("getLastProcessDocumentRMI",
				new Class[] { String.class}, new Object[] {technicsNumber});
	}

	/**
	 * 根据part的oid，partNumber，category获取工艺
	 *
	 * @param map
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<Vector> getTechnicsByPart(Map map) throws RemoteException, InvocationTargetException {
		String oid = String.valueOf(map.get("oid"));
		if(cacheMap.containsKey(oid)) {
			return cacheMap.get(oid);
		}

		Class<?>[] cla = new Class[] { Map.class,Map.class };
		Object[] obj = new Object[] { map,getAclGroupByGyType()};
		List<Vector> list = (List<Vector>) remoteMethodInvoke("getTechnicsByPartRMI", cla, obj);
		cacheMap.put(oid, list);
		return list;
	}

	public static Map<String, WTGroup> getAclGroupByGyType() throws RemoteException, InvocationTargetException {
		if(ValueCache.aclGroupByGyType==null){
			Class<?>[] cla = new Class[] {  };
			Object[] obj = new Object[] {};
			ValueCache.aclGroupByGyType = (Map<String, WTGroup>)remoteMethodInvoke("getAclGroupByGyType", cla, obj);
		}
		return ValueCache.aclGroupByGyType;


	}

	public static void removeFromCache(String oid){
		cacheMap.remove(oid);
	}

	/**
	 * 获取返工工艺,临时工艺;
	 *
	 * @param taskId
	 * @return List<0>:oid;List<1>partNumber
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<String> getTaskById(String taskId) throws RemoteException, InvocationTargetException {
		return (List<String>) remoteMethodInvoke("getTaskByIdRMI",
				new Class[] { String.class }, new Object[] { taskId });
	}

	/**
	 * 工艺合编
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 *
	 */
	public static HashMap<String, String> routeConfirm(String topPartOid,
			String partOid, String technicsName, String technicsType,byte[] technicsZip,
			String xmlVersion,String pplanNumber) throws RemoteException, InvocationTargetException {
		return (HashMap<String, String>) remoteMethodInvoke("routeConfirmRMI",
				new Class[] { String.class, String.class, String.class,String.class,
						byte[].class, String.class,String.class }, new Object[] {
						topPartOid, partOid, technicsName, technicsType,technicsZip,
						xmlVersion,pplanNumber });
	}

	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * 工艺确认路线
	 *
	 * @Title: routeConfirm
	 * @return boolean
	 * @throws
	 */
	private static void startRouteConfirmUrl() throws RemoteException, InvocationTargetException {
		CommonUtil.openURL((String) remoteMethodInvoke("getRouteConfirmUrlRMI",
				new Class[] {}, new Object[] {}));

	}

	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @Title: getAlltechnicsGroupNames
	 * @Description: 获取所有工艺组名称
	 * @param @return
	 * @return List<String>
	 * @throws
	 */
	public static List<String> getAlltechnicsGroupNames(String partOid) throws RemoteException, InvocationTargetException {
		return (List<String>) remoteMethodInvoke("getAlltechnicsGroupNamesRMI",
				new Class[] { String.class }, new Object[] { partOid });
	}

	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @Title: startTechnicsUnite
	 * @Description: 发起合编
	 * @return boolean
	 * @throws
	 */
	public static HashMap<String, String> startTechnicsUnite(String topPartOid,
			String partOid, String technicsName, byte[] technicsZip,
			String xmlVersion,String pplanNumber) throws RemoteException, InvocationTargetException {
		HashMap<String, String> map = null;
		try {
			map = (HashMap<String, String>) remoteMethodInvoke(
					"startTechnicsUniteRMI", new Class[] { String.class,
							String.class, String.class, byte[].class,
							String.class,String.class }, new Object[] { topPartOid, partOid,
							technicsName, technicsZip, xmlVersion,pplanNumber });
		} catch (Exception e) {
			return null;
		}
		startRouteConfirmUrl();
		return map;
	}

	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @Title: getUsertechnicsGroupName
	 * @Description: 获取当前用户所在工艺组
	 * @param @param partOid
	 * @return String
	 * @throws
	 */
	public static String getUsertechnicsGroupName(String partOid) throws RemoteException, InvocationTargetException {
//		logger.debug("partOid= " + partOid);
		return (String) remoteMethodInvoke("getUsertechnicsGroupNameRMI",
				new Class[] { String.class }, new Object[] { partOid });
	}

	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @Title: getUsertechnicsGroupName
	 * @Description: 获取当前用户所在工艺组
	 * @param @param partOid
	 * @return String
	 * @throws
	 */
	public static String getUsertechnicsGroupName() throws RemoteException, InvocationTargetException {
		return (String) remoteMethodInvoke("getUsertechnicsGroupNameRMI",
				new Class[] { }, new Object[] { });
	}

	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @Title: uploadUniteTechnics
	 * @Description: 提交合编工艺
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static HashMap uploadUniteTechnics(String topPartOid,
			String partOid, String technicsName, String unite,
			byte[] technicsZip, String xmlVersion) throws RemoteException, InvocationTargetException {
		HashMap map = null;
		try {
			map = (HashMap) remoteMethodInvoke("uploadUniteTechnicsRMI",
					new Class[] { String.class, String.class, String.class,
							String.class, byte[].class, String.class },
					new Object[] { topPartOid, partOid, technicsName, unite,
							technicsZip, xmlVersion });
		} catch (Exception e) {
			return null;
		}
		startRouteConfirmUrl();
		return map;
	}

	/**
	 * 搜索工艺名称和编号
	 *
	 * @author
	 * @date 2012-11-1
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<TempObject> searchTechnicsAttributes(String nubmer,
			String name, String type) throws RemoteException, InvocationTargetException {
		return (List<TempObject>) remoteMethodInvoke(
				"searchTechnicsAttributesRMI2", new Class[] { String.class,
						String.class, String.class }, new Object[] { nubmer,
						name, type });
	}

	/**
	 * 搜索工艺名称和编号
	 *
	 * @author
	 * @date 2012-11-1
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<TempObject> searchTechnicsAttributes2(String nubmer,
			String name, String type,boolean isGX) throws RemoteException, InvocationTargetException {
		return (List<TempObject>) remoteMethodInvoke(
				"searchTechnicsAttributesRMI2", new Class[] { String.class,
						String.class, String.class,boolean.class }, new Object[] { nubmer,
						name, type,isGX });
	}
	/**
	 * 搜索主制工艺
	 * @param nubmer
	 * @param name
	 * @param type
	 * @param technicsType
	 * @return
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static List<TempObject> searchMainMakeTechnics(String nubmer,
			String name, int typeIndex, String technicsType) throws RemoteException, InvocationTargetException {
		return (List<TempObject>) remoteMethodInvoke("searchMainMakeTechnics",
				new Class[] { String.class, String.class, int.class, String.class },
				new Object[] { nubmer, name, typeIndex, technicsType });
	}

	/**
	 * 搜索工艺时，根据工艺oid获取zip包
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<Object> searchTechnics(String oid) throws RemoteException, InvocationTargetException {
		return (List<Object>) remoteMethodInvoke("searchTechnicsRMI",
				new Class[] { String.class }, new Object[] { oid });
	}

	/**
	 * 获取查看历史工艺URL
	 *
	 * @author ylshao
	 * @date 2012-11-1
	 * @return
	 */
	public static String getHistoryUrl() {
		return "http://www.baidu.com";
		// return (String) remoteMethodInvoke("getHistoryUrlRMI", new Class[]
		// {},
		// new Object[] {});
	}

	/**
	 * 获取活动的变量和路由
	 *
	 * @param wiOid
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @throws WTRuntimeException
	 * @throws WTException
	 *
	 */
	public static List<Object> getCompleteCondition(String wiOid) throws RemoteException, InvocationTargetException {
		return (List<Object>) remoteMethodInvoke("getWorkItemInfoRMI",
				new Class[] { String.class }, new Object[] { wiOid });
	}

	/**
	 * 自动完成活动
	 *
	 * @param wiOid
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @throws WTRuntimeException
	 * @throws WTException
	 *
	 */
	public static HashMap<String, String> completeWorkItem(
			Map<String, Object> map, byte[] technicsZip) throws RemoteException, InvocationTargetException {
		return (HashMap<String, String>) remoteMethodInvoke(
				"completeWorkItemRMI", new Class[] { Map.class, byte[].class },
				new Object[] { map, technicsZip });
	}

	public static HashMap<Long, Matrix4d> getMatrix4dByPartOid(Map<Long, List<Long>> map) throws RemoteException, InvocationTargetException {
		return (HashMap<Long, Matrix4d>) remoteMethodInvoke(
				"getMatrix4dByPartOid", new Class[] { Map.class }, new Object[] { map });
	}

	/**
	 * 检查工艺
	 *
	 * @param
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @throws WTRuntimeException
	 * @throws WTException
	 *
	 */
	public static CheckTechnics checkTechnics(CheckTechnics checkTechnics) throws RemoteException, InvocationTargetException {
		return (CheckTechnics) remoteMethodInvoke("checkProcessPlanRMI",
				new Class[] { CheckTechnics.class },
				new Object[] { checkTechnics });
	}

	/**
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 * @Title: remoteMethodInvoke
	 * @Description:
	 * @return Object
	 * @throws
	 */
	public static Object remoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray) throws RemoteException, InvocationTargetException {
		return IntfUtil.getRemoteMethodInvoke(mentodName, classArray,
				objectArray);
	}
	public static String[] queryDept(String partId) throws RemoteException, InvocationTargetException{
		return (String[]) remoteMethodInvoke("queryDept", new Class[] {String.class },new Object[] { partId });

	}
	/**
	 * 删除工艺压缩包
	 * @param docNumber
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static void deleteWTPartDescribeDocLink(String docNumber) throws RemoteException, InvocationTargetException {
		 remoteMethodInvoke("deleteWTPartDescribeDocLink", new Class[] {String.class },new Object[] { docNumber });
	}

	/**
	 * 获取工艺计划对象的所有子类型
	 *
	 * @return Map<String,String>
	 * key:子类(com.ptc.windchill.mpml.processplan.MPMProcessPlan|casc.sast.149.Process_AP)
	 * value:显示名称
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Map<String,String> getMPMPPlanSubTypes() throws RemoteException, InvocationTargetException {
		return (Map<String,String>) remoteMethodInvoke("getMPMPPlanSubTypes", new Class[] {},new Object[] {});
	}

	/**
	 *获取工艺计划对象的IBA属性
	 *
	 * @param typeName 类名称
	 * @return Map<String,String> key:属性名称,value：显示名称@默认值
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 *
	 */
	public static Map<String,String> getAttrByMPMPlan(String typeName) throws RemoteException, InvocationTargetException {
		return (Map<String,String>) remoteMethodInvoke("getAttrByMPMPlan", new Class[] {String.class},new Object[] {typeName});
	}

	/**
	 * 获取指定工艺计划的工序的IBA属性
	 *
	 * @param typeName
	 * @return Map<String,String> key:属性名称,value：显示名称@默认值
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Map<String,String> getMPMOperAttrByMPMPlan(String typeName) throws RemoteException, InvocationTargetException {
		return (Map<String,String>) remoteMethodInvoke("getMPMOperAttrByMPMPlan", new Class[] {String.class},new Object[] {typeName});
	}

	/**
	 * 获取工艺类型的特征码映射关系
	 *
	 * @return Map<String,String> key:工艺类型，value:特征码
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Map<String,String> getPPlanID() throws RemoteException, InvocationTargetException {
		return (Map<String,String>) remoteMethodInvoke("getPPlanID", new Class[] {},new Object[] {});
	}

	/**
	 *通过零部件编号查找到零部件对象，并返回需要获取的IBA属性的值
	 *
	 * @param number 零部件编号
	 * @param list 属性获取的IBA属性的名称集合
	 * @return Map<String,String> key:属性名称,value:属性值
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Map<String,String> getPartIBAValuesByNumber(String number,List<String> list) throws RemoteException, InvocationTargetException {
		return (Map<String,String>) remoteMethodInvoke("getPartIBAValuesByNumber", new Class[] {String.class,List.class},new Object[] {number,list});
	}

	/**
	 *通过零部件编号查找到零部件对象，并返回需要获取的IBA属性的值
	 *
	 * @param number 零部件编号
	 * @param list 属性获取的IBA属性的名称集合
	 * @return Map<String,String> key:属性名称,value:属性值
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Map<String,String> getPartIBAValuesByNumber2(String number,List<List<String>> list) throws RemoteException, InvocationTargetException {
		return (Map<String,String>) remoteMethodInvoke("getPartIBAValuesByNumber2", new Class[] {String.class,List.class},new Object[] {number,list});
	}

	/**
	 *从配置文件XML获取所有工艺类型的属性
	 *
	 * @return Map<String,List<List<String>>>
	 * key:工艺名称
	 * value：List<List<String>>每一个工艺类型的所有属性,List<String>每一个属性的属性值
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Map<String,List<List<String>>> getMPMPPlanTypeAttrByXML() throws RemoteException, InvocationTargetException {
		return (Map<String,List<List<String>>>) remoteMethodInvoke("getMPMPPlanTypeAttrByXML", new Class[] {},new Object[] {});
	}

	/**
	 *从配置文件XML获取所有工艺类型的工序属性
	 *
	 * @return Map<String,List<List<String>>>
	 * key:工艺名称
	 * value：List<List<String>>每一个工艺类型的所有属性,List<String>每一个类型的所有工序属性值
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Map<String,List<List<String>>> getMPMPPlanStepAttrByXML() throws RemoteException, InvocationTargetException {
		return (Map<String,List<List<String>>>) remoteMethodInvoke("getMPMPPlanStepAttrByXML", new Class[] {},new Object[] {});
	}


	/**
	 * 方法功能: 校验工艺是否存在辅制工艺任务
	 *
	 * @param processNumber
	 * @param taskType
	 * @return boolean
	 * @author LB
	 * @date 2020/9/6
	 */
	public static Integer checkHasFzProcessTask(String processNumber, String taskType) throws RemoteException, InvocationTargetException {
		return (Integer) remoteMethodInvoke("checkHasFzProcessTask", new Class[] { String.class, String.class },new Object[] { processNumber, taskType });
	}


	/**
	 * 提交辅制工艺任务
	 *
	 * @param map
	 * @return Boolean
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static boolean createTaskItem(Map<String,String> map) throws RemoteException, InvocationTargetException {
		return (Boolean) remoteMethodInvoke("createTaskItem", new Class[] { Map.class },new Object[] { map });
	}

	/**
     * 提起辅制工艺任务时，对于同一份工艺文件、同一个车间，应该进行如下容错：
	 *	1、如果是工艺设计任务：只允许有一个正在进行的任务，否则报错已存在（如果工艺任务已完成，可以重复）
	 *	2、如果是工艺更改任务：同上
	 *	3、如果是临时工艺任务：允许同时下达多个
	 *
     * @param map
     * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
     */
	public static boolean checkProcessTask(Map<String,String> map) throws RemoteException, InvocationTargetException {
		return (Boolean) remoteMethodInvoke("checkProcessTask", new Class[] { Map.class },new Object[] { map });
	}

	/**
	 *获取当前用户的所有工艺任务关联的part编号
	 *
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<String> getCurrentUserTaskPartInfo() throws RemoteException, InvocationTargetException {
		return (List<String>) remoteMethodInvoke("getCurrentUserTaskPartInfo", new Class[] {},new Object[] {});
	}

	/**
	 * 通过产品名称获取该产品下的所有批次号
	 *
	 * @author longxiuchuan
	 * @param name 产品名称
	 * @return List<String> 批次号集合
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Vector<String> getBatchsByProductOid(long oid) throws RemoteException, InvocationTargetException {
		return (Vector<String>) remoteMethodInvoke("getBatchsByProductOid", new Class[] { long.class },new Object[] { oid });
	}

	/**
	 * 通过产品名称获取该产品下的所有批次号
	 *
	 * @author longxiuchuan
	 * @param name 产品名称
	 * @return List<String> 批次号集合
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Vector<String> getBatchsByProductName(String name) throws RemoteException, InvocationTargetException {
		return (Vector<String>) remoteMethodInvoke("getBatchsByProductName", new Class[] { String.class },new Object[] { name });
	}

	/**
	 *
	 * 通过指定PBOM零部件的编号，查询该PBOM零部件的批次信息
	 *
	 * @author longxiuchuan
	 * @param number 指定的PBOM零部件的编号
	 * @return List<List<String>> 指定PBOM零部件的批次集合信息
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<List<String>> getPbomBatchsByPartNumber(String number) throws RemoteException, InvocationTargetException {
		return (List<List<String>>) remoteMethodInvoke("getPbomBatchsByPartNumber", new Class[] { String.class },new Object[] { number });
	}

	/**
	 * 根据MBOM oid 查询 PBOM oid
	 * @param mbomOid
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static long getEbomOidByMbomOid(String mbomOid) throws RemoteException, InvocationTargetException{
		return (Long) remoteMethodInvoke("getEbomOidByMbomOidRMI", new Class[] {String.class },new Object[] { mbomOid });
	}

	/**
	 * 获取当前用户的工业任务活动的部件OID
	 *
	 * @param list
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<String> getCurrentUserTaskPartOidInfo(List<String> list) throws RemoteException, InvocationTargetException {
		return (List<String>) remoteMethodInvoke("getCurrentUserTaskPartOidInfo", new Class[] {List.class },new Object[] { list });
	}

	/**
	 * 获取指定工艺输出的格式
	 *
	 * @param techType 工艺类型
	 * @param techID 工艺特征码
	 * @param techForm 工艺形式
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<TechnicsOutputFormBean> getMPMPPlanOutputFormsXml(String techType,String techID,String techForm) throws RemoteException, InvocationTargetException {
		return (List<TechnicsOutputFormBean>) remoteMethodInvoke("getMPMPPlanOutputFormsXml",
				new Class[] {String.class,String.class,String.class },new Object[] { techType,techID,techForm });
	}

	/**
	 * 获取工艺文档材料定额状态
	 *
	 * @param techNumber
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static String getCLDEState(String techNumber) throws RemoteException, InvocationTargetException {
		return (String) remoteMethodInvoke("getCLDEState", new Class[] { String.class },new Object[] { techNumber });
	}

	public static Boolean uploadAttachForDocument(String documentNumber, String fileName, byte[] bytes)
			throws WTException, RemoteException, InvocationTargetException {
		return (Boolean)remoteMethodInvoke("uploadAttachForDocumentRMI", new Class[] {String.class,String.class,byte[].class },new Object[] { documentNumber,fileName,bytes });
	}
	public static Boolean uploadAttachForSOP(String documentNumber, String fileName, byte[] bytes)
			throws WTException, RemoteException, InvocationTargetException {
		return (Boolean)remoteMethodInvoke("uploadAttachForSOPRMI", new Class[] {String.class,String.class,byte[].class },new Object[] { documentNumber,fileName,bytes });
	}
	public static Boolean deleteAttachForSOP(String documentNumber)
			throws  RemoteException, InvocationTargetException {
		return (Boolean)remoteMethodInvoke("deleteAttachForSOPRMI", new Class[] {String.class },new Object[] { documentNumber });
	}

	public static String getDocumentStateByNumber(String number) throws WTException, RemoteException, InvocationTargetException {
		return (String)remoteMethodInvoke("getDocumentStateByNumber", new Class[] {String.class },new Object[] { number });
	}

	public static List<WTDocument> getAllDocumentByNumber(String number) throws RemoteException, InvocationTargetException{
		return (List<WTDocument>)remoteMethodInvoke("getAllDocumentByNumber", new Class[] {String.class },new Object[] { number });
	}

	/**
	 * 获取文档对象主内容
	 *
	 * @author LongXiuChuan
	 * @date 2014-5-28
	 * @param partNumber
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 *
	 */
	public static byte[] getDocumentPrimary(String number) throws RemoteException, InvocationTargetException {
		byte[] bytes = (byte[]) remoteMethodInvoke("getDocumentPrimary",
				new Class[] { String.class }, new Object[] { number });
		return bytes;
	}
	public static byte[] getPrintPdf(String number) throws RemoteException, InvocationTargetException{
		byte[] bytes = (byte[])remoteMethodInvoke("getPrintPdf",
				new Class[] {String.class}, new Object[] { number });
		return bytes;
	}

	public static WTPart getPartByOid(String oid) throws RemoteException, InvocationTargetException {
		return (WTPart)remoteMethodInvoke("getPartByOid", new Class[] {String.class },new Object[] { oid });
	}

	public static String getPartStateByOid(String oid) throws RemoteException, InvocationTargetException {
		return (String)remoteMethodInvoke("getPartStateByOid", new Class[] {String.class },new Object[] { oid });
	}

	/**
	 * 获取分类顶层节点
	 *
	 * @return List<String> 分类顶层节点集合
	 */
	public static List<String> getTopGlClassificationNode() throws RemoteException, InvocationTargetException {
		return (List<String>)remoteMethodInvoke("getTopGlClassificationNode", new Class[] { },new Object[] { });
	}

	/**
	 * 获取所有选用目录名称及该选用目录的类型集合
	 *
	 * @return Map<String,List<String>> key:选用目录名称，value:List<String>为该选用目录对应的类型
	 */
	public static Map<String,List<String>> getGlcataLog() throws RemoteException, InvocationTargetException {
		return (Map<String,List<String>>)remoteMethodInvoke("getGlcataLog", new Class[] { },new Object[] { });
	}

	/**
	 * 工艺编辑器中工艺定额，材料定额查询资源库数据接口
	 * 通过指定的条件查询数据并返回数据集合
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 编号
	 * @param name 名称
	 * @param ibaMap IBA属性
	 * @return List<SjzykBean> 数据集合
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static List<SjzykBean> queryData(String type, String xyml, String xzfl, String number,
			String name, Map<String,String> ibaMap, String containerName)
			throws RemoteException, InvocationTargetException {
		System.out.println("queryData  type:"+type+"  xyml:"+xyml+"  xzfl:"+xzfl+"  number:"+number+"  name:"+name+"  ibaMap:"+ibaMap+"  containerName:"+containerName);
		Class[] cls = new Class[] { String.class, String.class, String.class, String.class, String.class, Map.class, String.class };
		Object[] objs = new Object[] { type, xyml, xzfl, number, name, ibaMap, containerName };
		return (List<SjzykBean>)remoteMethodInvoke("queryData", cls, objs);
	}

	/**
	 * 工艺编辑器中工艺定额，材料定额查询资源库数据接口
	 * 通过指定的条件查询数据并返回数据集合
	 *
	 * @param xyml 选用目录
	 * @param xzfl 选择分类
	 * @param number 编号
	 * @param name 名称
	 * @param ibaMap IBA属性
	 * @return List<SjzykBean> 数据集合
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static List<SjzykBean> queryData2(String type, String xyml, String xzfl, String number,
			String name, Map<String,String> ibaMap, String containerName)
			throws RemoteException, InvocationTargetException {
		System.out.println("queryData2  type:"+type+"  xyml:"+xyml+"  xzfl:"+xzfl+"  number:"+number+"  name:"+name+"  ibaMap:"+ibaMap+"  containerName:"+containerName);
		Class[] cls = new Class[] { String.class, String.class, String.class, String.class, String.class, Map.class, String.class };
		Object[] objs = new Object[] { type, xyml, xzfl, number, name, ibaMap, containerName };
		return (List<SjzykBean>)remoteMethodInvoke("queryData2", cls, objs);
	}

	/**
    *
    * 读取每种类型零部件的属性
    *
    * @return map
    */
	public static Map<String, List<Map<String,String>>> getAttributes() throws RemoteException, InvocationTargetException {
		Class[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (Map<String, List<Map<String,String>>>)remoteMethodInvoke("getAttributes", cls, objs);
	}

	/**
	 * 通过输入的前缀值查询相似的所有的值
	 *
	 * @param tableColName 中间表QUERYMIDDLE_TABLE的列名
	 * @param prefix 输入的前缀
	 * @return List<String>
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<String> getValues(String tableColName) throws RemoteException, InvocationTargetException {
		Class[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { tableColName };
		return (List<String>)remoteMethodInvoke("getValues", cls, objs);
	}

	/**
	 * 创建报表类工艺文件时获取工艺文件后五位顺序号
	 *
	 * @param userName
	 * @param type
	 * @param pindex
	 * @return
	 */
	public static String getReportTechnicsSequenceNumber(String userName, String type, String pindex) throws RemoteException, InvocationTargetException {
		Class[] cls = new Class[] { String.class, String.class, String.class };
		Object[] objs = new Object[] { userName, type, pindex };
		return (String)remoteMethodInvoke("getReportTechnicsSequenceNumber", cls, objs);
	}

	/**
	 *从配置文件XML获取所有报表类工艺类型及其所有属性
	 *
	 * @return Map<String,List<List<String>>>
	 * key:工艺名称
	 * value：List<List<String>>每一个工艺类型的所有属性,List<String>每一个属性参数
	 */
	public static Map<String,List<List<String>>> getReportMPMPPlanAttrByXML() throws RemoteException, InvocationTargetException {
		Class[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (Map<String,List<List<String>>>)remoteMethodInvoke("getReportMPMPPlanAttrByXML", cls, objs);
	}

	/**
	 * 从配置文件XML获取所有报表类工艺类型及其PDF输出时的模板ID
	 *
	 * @return Map<String,String>
	 */
	public static Map<String,String> getReportMPMPPlanFormIdByXML() throws RemoteException, InvocationTargetException {
		Class[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (Map<String,String>)remoteMethodInvoke("getReportMPMPPlanFormIdByXML", cls, objs);
	}

	/**
	 * 调用服务器端接口，查询该零部件及其所有子件下的所有的工艺文件。
	 * 将工艺文件的Element存放在List中返回
	 *
	 * @param oid partOid
	 * @return List<Element> 工艺文件Element集合
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static List<Element> getAllTechnicsElementByPart(String oid) throws RemoteException, InvocationTargetException {
		Class[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { oid };
		return (List<Element>)remoteMethodInvoke("getAllTechnicsElementByPart", cls, objs);
	}

	public static WTPart getLatestPartByPartNumber(String partNumber) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { partNumber };
		return (WTPart)remoteMethodInvoke("getLatestPartByPartNumber", cls, objs);
	}

	public static String getPartCTypeByNumberAndView(String partNumber, String design) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class,String.class };
		Object[] objs = new Object[] { partNumber,design };
		return (String)remoteMethodInvoke("getPartCTypeByNumberAndView", cls, objs);
	}

	public static String getAllPartOid(String number) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { number };
		return (String)remoteMethodInvoke("getAllPartOid", cls, objs);
	}

	public static String getAllPartOidByOid(String oid) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { oid };
		return (String)remoteMethodInvoke("getAllPartOidByOid", cls, objs);
	}

	public static JSONObject getFilePrintJson(WTObject pbo) throws RemoteException, InvocationTargetException{

		Class[] cls = new Class[] { WTObject.class };
		Object[] objs = new Object[] { pbo };
		return (JSONObject)remoteMethodInvoke("getFilePrintJson", cls, objs);
	}

	public static Hashtable getFilePrintinf(WTObject pbo) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { WTObject.class };
		Object[] objs = new Object[] { pbo };
		return (Hashtable)remoteMethodInvoke("getFilePrintinf", cls, objs);
	}

	public static WTPart getLatestMPartByPartNumber(String partNumber)  throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { partNumber };
		return (WTPart)remoteMethodInvoke("getLatestMPartByPartNumber", cls, objs);
	}

	public static WTPart getMPartByNumberAndVersion(String partNumber, String partVersion)  throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class, String.class };
		Object[] objs = new Object[] { partNumber, partVersion };
		return (WTPart)remoteMethodInvoke("getMPartByNumberAndVersion", cls, objs);
	}

	public static List<Element> getProceduresOfTechnic(String number) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { number };
		return (List<Element>)remoteMethodInvoke("getProceduresOfTechnic", cls, objs);
	}

	public static Boolean hasMainMakeTechnics(String fzTechnicsNumber, String fzVersion, String picihao) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class, String.class, String.class };
		Object[] objs = new Object[] { fzTechnicsNumber, fzVersion, picihao };
		return (Boolean)remoteMethodInvoke("hasMainMakeTechnics", cls, objs);
	}

	public static Boolean saveZhuFuLink(String fzTechnicsNumber, String fzVersion, String zzTechnicsNumber, String zzVersion, String picihao, Map<String, String> procedureMap) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class, String.class, String.class, String.class, String.class, Map.class };
		Object[] objs = new Object[] { fzTechnicsNumber, fzVersion, zzTechnicsNumber, zzVersion, picihao, procedureMap };
		return (Boolean)remoteMethodInvoke("saveZhuFuLink", cls, objs);
	}
	public static Map<String, String> getZhuFuLink(String fzTechnicsNumber, String fzVersion, String picihao, String zzTechnicsNumber, String zzTechnicsVersion) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] { String.class, String.class, String.class, String.class, String.class };
		Object[] objs = new Object[] { fzTechnicsNumber, fzVersion, picihao, zzTechnicsNumber, zzTechnicsVersion };
		return (Map)remoteMethodInvoke("getZhuFuLink", cls, objs);
	}

	public static String getUsageLinkCountByNumber(String parentNumber, String childNumber) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] {String.class, String.class};
		Object[] objs = new Object[] {parentNumber, childNumber};
		return (String)remoteMethodInvoke("getUsageLinkCountByNumber", cls, objs);
	}
	public static List<Map<String,String>> getHasPbomXmlParentPartIda2a2List(String partOid) throws RemoteException, InvocationTargetException{
		Class[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {partOid};
		return (List<Map<String,String>>)remoteMethodInvoke("getHasPbomXmlParentPartIda2a2List", cls, objs);
	}


	public static Object[][] getReportDatasRMI(List<String> partOids, String type) throws RemoteException, InvocationTargetException {
		Class[] cls = new Class[] {List.class,String.class};
		Object[] objs = new Object[] {partOids,type};
		return (Object[][])remoteMethodInvoke("getReportDatas", cls, objs);
	}
	public static Object[][] getReportDatasRMI(String  partOid, String type) throws RemoteException, InvocationTargetException {
		Class[] cls = new Class[] {String.class,String.class};
		Object[] objs = new Object[] {partOid,type};
		return (Object[][])remoteMethodInvoke("getReportDatas", cls, objs);
	}

	public static Object[][] getReportDatasRMI(String  partOid, String type,String startType) throws RemoteException, InvocationTargetException {
		Class[] cls = new Class[] {String.class,String.class,String.class};
		Object[] objs = new Object[] {partOid,type,startType};
		return (Object[][])remoteMethodInvoke("getReportDatas", cls, objs);
	}
	/**
	 * 获取所有的工艺类型
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	@SuppressWarnings("unchecked")
	public static Vector<String> getAllMPMSkill() throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (Vector<String>) remoteMethodInvoke("getAllMPMSkill", cls, objs);
    }
	/**
	 * 获得所有产品的型号代号值
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	@SuppressWarnings("unchecked")
	public static Vector<String> getProductMindex() throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (Vector<String>) remoteMethodInvoke("getProductMindex", cls, objs);
    }
	public static boolean uploadPrimaryOfDocument(byte[] bytes, String technicsNumber, String version) throws RemoteException, InvocationTargetException {
		return (Boolean) remoteMethodInvoke("uploadPrimaryOfDocument", new Class[] { byte[].class, String.class,String.class},new Object[] {bytes, technicsNumber, version});
	}
	/**
	 * 获取检验汇总表的地址
	* @author jyx
	* @date 2018-5-9
	* @param technicsNumber
	* @return
	* @throws RemoteException
	* @throws InvocationTargetException
	 */
	public static String getshowCheckOutTableUrl(String technicsNumber) throws RemoteException, InvocationTargetException {
		return (String)remoteMethodInvoke("getshowCheckOutTableUrl", new Class[] {String.class },new Object[] { technicsNumber });
	}

	/**
	 * 判断工艺是否为最新版本
	 * @param techncicsMap
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static String checkTechnicsIsLatest(Map<String, String> techncicsMap) throws WTException, RemoteException, InvocationTargetException {
		return (String)remoteMethodInvoke("checkTechnicsIsLatest", new Class[] {Map.class },new Object[] { techncicsMap });
	}
	public static List<CmTreeNode> getPartRelatedTechnics(String partNumber, String partVersion) throws InvocationTargetException, RemoteException {
		return (List<CmTreeNode>)remoteMethodInvoke("getPartRelatedTechnics", new Class[] {String.class, String.class },new Object[] { partNumber,partVersion });
	}
	public static List<CmAttachment> getTechnicsCmAttachment(List<String> technicsNumberList) throws InvocationTargetException, RemoteException {
		return (List<CmAttachment>)remoteMethodInvoke("getTechnicsCmAttachment", new Class[] {List.class },new Object[] { technicsNumberList });
	}

    public static byte[] getPbomBytesByTechnicsOid(String technicsOid) throws InvocationTargetException, RemoteException {
		return (byte[])remoteMethodInvoke("getPbomBytesByTechnicsOid", new Class[] {String.class },new Object[] { technicsOid });
    }
    public static List<CmAttachment> getPbomRelatedTechnics(String partNumber, String technicsOid) throws InvocationTargetException, RemoteException {
		return (List<CmAttachment>)remoteMethodInvoke("getPbomRelatedTechnics", new Class[] {String.class, String.class },new Object[] { partNumber, technicsOid });
	}
	public static List<GLZhuFuLink> getZFLinks(String zzTechnicsNumber, String version) throws InvocationTargetException, RemoteException {
		return (List<GLZhuFuLink>)remoteMethodInvoke("getZFLinks",new Class[]{String.class, String.class},new Object[]{zzTechnicsNumber,version});
	}
	public static TempObject getSamePplanNumberDxPlan(String pplanNumber) throws InvocationTargetException, RemoteException {
		return (TempObject)remoteMethodInvoke("getSamePplanNumberDxPlan",new Class[]{String.class},new Object[]{pplanNumber});
	}

	public static Boolean checkTechnicsState(String technicsNumber) throws InvocationTargetException, RemoteException {
		return (Boolean)remoteMethodInvoke("checkTechnicsState",new Class[]{String.class},new Object[]{technicsNumber});
	}

	public static List<Map<String, Object>> searchZhuFuLinkByFPlan (String docNumber, String version) throws RemoteException, InvocationTargetException {
		return (List<Map<String, Object>>) remoteMethodInvoke("getZhuFuLinkByFPlan",new Class[]{String.class, String.class},new Object[]{docNumber, version});
	}

	public static List<TempObject> searchTechnics (String number, String name, String technicType, String zfFlag, String[] states) throws RemoteException, InvocationTargetException {
		return (List<TempObject>) remoteMethodInvoke("searchTechnics",new Class[]{String.class, String.class, String.class, String.class, String[].class },new Object[]{number, name, technicType, zfFlag, states});
	}

	public static void deleteZhuFuLinkByID(String[] idArray) throws RemoteException, InvocationTargetException {
		remoteMethodInvoke("deleteZhuFuLinkByID",new Class[]{ String[].class },new Object[]{ idArray });
	}

	 public static String saveZhuFuLink(List<Map<String, String>> dataMap) throws RemoteException, InvocationTargetException {
		 return (String)remoteMethodInvoke("saveZhuFuLink",new Class[]{ List.class },new Object[]{ dataMap });
	 }

	 /**
     * 主制关联复制加载现有关联
     * @param params
	 * @throws InvocationTargetException
	 * @throws RemoteException
     * @throws Exception
     */
	public static Map<String, Map<String, String>> searchZFLinkByMainPlan(Map<String, String> params) throws RemoteException, InvocationTargetException {
		return (Map<String, Map<String, String>>) remoteMethodInvoke("searchZFLinkByMainPlan",new Class[]{ Map.class },new Object[]{ params });
	}
	/**
	 * 主制关联辅制工艺
	 * @param params
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static String addZhuFuLinkByZplan(Map<String, String> params) throws RemoteException, InvocationTargetException {
		return (String)remoteMethodInvoke("addZhuFuLinkByZplan",new Class[]{ Map.class },new Object[]{ params });
	}

	/**
	 * 主制关联辅制工艺关联关系记录
	 * @param params
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static String recordZhuFuLink(Map<String, String> params) throws RemoteException, InvocationTargetException {
		return (String)remoteMethodInvoke("recordZhuFuLink",new Class[]{ Map.class },new Object[]{ params });
	}

	public static String recordZhuFuLink(GLZhuFuLink glZhuFuLink) throws RemoteException, InvocationTargetException {
		return (String)remoteMethodInvoke("recordZhuFuLink",new Class[]{ GLZhuFuLink.class },new Object[]{ glZhuFuLink });
	}

	public static List<GLZhuFuLink> getRecordList(String technicsNumber,String version,String stepNumber) throws RemoteException, InvocationTargetException {
		return (List<GLZhuFuLink>)remoteMethodInvoke("getRecordList",new Class[]{ String.class, String.class, String.class },new Object[]{ technicsNumber, version, stepNumber });
	}

	public static String deleteZhuFuLink(Map<String, String> params) throws RemoteException, InvocationTargetException {
		return (String)remoteMethodInvoke("deleteZhuFuLink",new Class[]{ Map.class },new Object[]{ params });
	}

	public static String checkDocExitAndModifier(String number) throws RemoteException, InvocationTargetException {
		return (String) remoteMethodInvoke("checkDocExitAndModifier",new Class[]{ String.class },new Object[]{ number });
	}

    public static List<GLZhuFuLink> getAllZhufuLink(String fDocNum, String fVersion) throws InvocationTargetException, RemoteException {
		return (List<GLZhuFuLink>) remoteMethodInvoke("getAllZhufuLink",new Class[]{ String.class,String.class },new Object[]{ fDocNum,fVersion });
    }

    public static String getProductNumberAndName(String partNumber) throws RemoteException, InvocationTargetException{
    	if(productNumberAndName == null) {
    		productNumberAndName = (String) remoteMethodInvoke("getProductNumberAndName",new Class[]{ String.class },new Object[]{partNumber });
    	}
		return productNumberAndName;
    }

    public static Map<String, String> getAllProcessTask(String docNumber,String partOid, String user) throws InvocationTargetException, RemoteException {
		return (Map<String, String>) remoteMethodInvoke("getAllProcessTask",new Class[]{ String.class,String.class,String.class },new Object[]{docNumber, partOid, user });
    }

    public static String getDefaultTaskItem(String technicsNumber,String partNumber, String users) throws InvocationTargetException, RemoteException {
		return (String) remoteMethodInvoke("getDefaultTaskItem",new Class[]{ String.class,String.class,String.class },new Object[]{technicsNumber, partNumber, users });
    }

    public static boolean isHasChangeOrder(String docNumber) throws InvocationTargetException, RemoteException {
		return (Boolean) remoteMethodInvoke("isHasChangeOrder", new Class[]{ String.class }, new Object[]{ docNumber });
    }

    public static String getNextVersion(String technicsNumber) throws InvocationTargetException, RemoteException {
		return (String) remoteMethodInvoke("getNextVersion", new Class[]{ String.class }, new Object[]{ technicsNumber });
    }

    public static boolean checkIsHasEpm(String partOid) throws InvocationTargetException, RemoteException {
		return (Boolean) remoteMethodInvoke("checkIsHasEpm", new Class[]{ String.class }, new Object[]{ partOid });
    }

    public static String getUnMindexByProductName(String name) throws InvocationTargetException, RemoteException {
		return (String) remoteMethodInvoke("getUnMindexByProductName", new Class[]{ String.class }, new Object[]{ name });
    }

	public static List<Element> getAllCldeProcessPlan(String partOid, String currentUser) throws InvocationTargetException, RemoteException {
		return (List<Element>) remoteMethodInvoke("getAllCldeProcessPlan", new Class[]{ String.class, String.class }, new Object[]{ partOid, currentUser });
	}

	public static void saveTechnicaQuotaInfo(Set<String> set, String technicsDocNumber) throws InvocationTargetException, RemoteException {
		 remoteMethodInvoke("saveTechnicaQuotaInfo", new Class[]{ Set.class, String.class }, new Object[]{ set, technicsDocNumber });
	}

	public static ArrayList<ArrayList<String>> getCheckFileList(String type, String technicsNumber,String stepNumber,String paceNumber) throws InvocationTargetException, RemoteException {
		return (ArrayList<ArrayList<String>>) remoteMethodInvoke("getCheckFileList", new Class[]{ String.class,String.class, String.class,String.class }, new Object[]{type, technicsNumber, stepNumber , paceNumber });
	}

	public static Map<String,String> getFileURLByDocNumber(String docNumber) throws InvocationTargetException, RemoteException {
		return (Map<String,String>) remoteMethodInvoke("getFileURLByDocNumber", new Class[]{String.class }, new Object[]{ docNumber});
	}

	public static Map<String,String> getFileURLByDocOid(String oid) throws InvocationTargetException, RemoteException {
		return (Map<String,String>) remoteMethodInvoke("getFileURLByDocOid", new Class[]{String.class }, new Object[]{ oid});
	}

	public static String deleteSchemaDataByNumber(String number) throws InvocationTargetException, RemoteException {
		return (String) remoteMethodInvoke("deleteSchemaDataByNumber", new Class[]{String.class }, new Object[]{ number});
	}

	public static HashMap<String,ArrayList<HashMap<String,String>>> getPhotoRecordList(Map<String, String> map) throws InvocationTargetException, RemoteException {
		return (HashMap<String,ArrayList<HashMap<String,String>>>) remoteMethodInvoke("getPhotoRecordList", new Class[]{Map.class }, new Object[]{ map});
	}

	public static byte[] getImageBytesByNumberAndVersion(String number, String version) throws InvocationTargetException, RemoteException {
		return (byte[]) remoteMethodInvoke("getImageBytesByNumberAndVersion", new Class[]{String.class,String.class }, new Object[]{ number,version});
	}

	public static String getImageNameByNumberAndVersion(String number, String version) throws InvocationTargetException, RemoteException {
		return (String) remoteMethodInvoke("getImageNameByNumberAndVersion", new Class[]{String.class,String.class }, new Object[]{ number,version});
	}

    public static Map<String, Object> getPhotoConfig() throws InvocationTargetException, RemoteException {
		return (Map<String,Object>) remoteMethodInvoke("getPhotoConfig", new Class[]{}, new Object[]{ });
	}

    public static void reName(String technicsNumber, String newName)throws InvocationTargetException, RemoteException {
		remoteMethodInvoke("reName", new Class[]{String.class,String.class }, new Object[]{technicsNumber,newName});
    }

	public static String getBaiYuImageBytesByNumber(String number) throws InvocationTargetException, RemoteException {
		return (String) remoteMethodInvoke("getBaiYuImageBytesByNumber", new Class[]{String.class}, new Object[]{ number});
	}

	public static List<String> getZDGongyiFuJia() throws InvocationTargetException, RemoteException {
		return (List<String>) remoteMethodInvoke("getZDGongyiFuJia", new Class[]{}, new Object[]{ });
	}

	public static List<String[]> queryTechnicsParsms(String numberValue,String nameValue,String type,String special) throws InvocationTargetException, RemoteException {
		return (List<String[]>) remoteMethodInvoke("queryTechnicsParsms", new Class[]{String.class,String.class,String.class,String.class},
				new Object[]{numberValue,nameValue,type,special });
	}

	public static String genTechnicsNumber() throws InvocationTargetException, RemoteException{
		return (String) remoteMethodInvoke("genTechnicsNumber", new Class[]{}, new Object[]{ });
	}

	public static String getSystemConfiguration(String key) {
		try {
			if(systemConfigurationMap == null) {
				systemConfigurationMap = (Map<String, String>) remoteMethodInvoke("getSystemConfiguration", new Class[]{}, new Object[]{});
			}
			return StringUtil.object2String(systemConfigurationMap.get(key));
		} catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取本人所有主制工艺
	 *
	 * @param partOid
	 * @param currentUser
	 * @return List<Element>
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<Element> getAllZhuZhiProcessPlan(String partOid, String currentUser) throws InvocationTargetException, RemoteException {
		return (List<Element>) remoteMethodInvoke("getAllZhuZhiProcessPlan", new Class[]{ String.class, String.class }, new Object[]{ partOid, currentUser });
	}

	/**
	 * 批量提交辅制工艺任务
	 *
	 * @param map
	 * @return Boolean
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<String> createTaskItemBatch(Map<String, Map<String, String>> allMap) throws RemoteException, InvocationTargetException {
		return (List<String>) remoteMethodInvoke("createTaskItemBatch", new Class[] { Map.class },new Object[] { allMap });
	}

	/**
	 * 获取本人所有可以编辑的工艺
	 *
	 * @param partOid
	 * @param currentUser
	 * @param signedType
	 * @return List<Element>
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static List<Element> getAllInWorkProcessPlan(String partOid, String currentUser, String signedType) throws InvocationTargetException, RemoteException {
		return (List<Element>) remoteMethodInvoke("getAllInWorkProcessPlan", new Class[]{ String.class, String.class,String.class }, new Object[]{ partOid, currentUser,signedType });
	}

	/**
	 * 工艺批量提交签审
	 *
	 * @author ylshao
	 * @date 2012-11-21
	 * @return
	 */
	public static HashMap<String, Map<String, String>> submitSignedBatch(Map<String, Map<String, Object>> inputMaps) {
		HashMap<String, Map<String, String>> map = null;
		try {
			map = (HashMap<String, Map<String, String>>) remoteMethodInvoke("submitSignedBatchRMI", new Class[]{Map.class}, new Object[]{inputMaps});
		} catch (Exception e) {
			return null;
		}
		return map;
	}


	public static List<Map<String, String>> searchUsers(String name) {
		List<Map<String, String>> list = null;
		try {
			list = (List<Map<String, String>>) remoteMethodInvoke("searchUsers", new Class[]{String.class}, new Object[]{name});
		} catch (Exception e) {
			return null;
		}
		return list;
	}
}








