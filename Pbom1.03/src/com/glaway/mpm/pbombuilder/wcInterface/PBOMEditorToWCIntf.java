package com.glaway.mpm.pbombuilder.wcInterface;

import com.glaway.mpm.pbom.db.ErpResult;
import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.util.ErpUtil;
import wt.inf.container.WTContainer;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PBOMEditorToWCIntf {
	private static final CmLogger log = CmLogger.getLogger(PBOMEditorToWCIntf.class.getName());
	public static Map<String,List<String>> map;

	/**
	 * 新建中间件和辅件
	 *
	 * @author chenyunlong
	 * @date 2013-3-28
	 * @param number
	 * @param name
	 * @param containerOid
	 * @param folderPath
	 * @param typeName
	 * @return
	 *
	 */
	public static List<Object> createMiddleORAssistantPart(Map<String,String> map,Map<String,String> ibaMap) {
		return (List<Object>) remoteMethodInvoke("createMiddleORAssistantPartRMI", new Class[] { Map.class,Map.class }, new Object[] { map,ibaMap });
	}

	/**
	 * 创建毛坯件
	 *
	 * @author longxiuchuan
	 * @param map
	 * @param ibaMap
	 * @return
	 */
	public static List<Object> createMPPart(Map<String,String> map,Map<String,String> ibaMap) {
		return (List<Object>) remoteMethodInvoke("createMPPartRMI", new Class[] { Map.class,Map.class }, new Object[] { map,ibaMap });
	}

	/**
	 * pbom 结构化
	 *
	 * @author chenyunlong
	 * @date 2013-3-20
	 * @param list
	 * @return
	 *
	 */
	public static String pbomStructure(List<List<Object>> list) {
		return (String) remoteMethodInvoke("pbomStructureRMI", new Class[] { List.class }, new Object[] { list });
	}

	/**
	 * 获取pbom xml
	 *
	 *
	 * @author chenyunlong
	 * @date 2013-3-28
	 * @param oid
	 * @return
	 *
	 */
	public static byte[] getPBOMXml(String oid) {
		return (byte[]) remoteMethodInvoke("getPBOMXmlRMI", new Class[] { String.class }, new Object[] { oid });
	}

	/**
	 * 保存pbom xml
	 *
	 * @author chenyunlong
	 * @date 2013-3-28
	 * @param oid
	 * @param bytes
	 * @return
	 *
	 */
	public static Boolean savePBOMXml(String oid, byte[] bytes) {
		log.debug("提交，正在向服务器提交数据");
		return (Boolean) remoteMethodInvoke("savePBOMXmlRMI", new Class[] { String.class, byte[].class }, new Object[] {
				oid, bytes });
	}

	/**
	 * 修订PBOM文档对象并保存pbom xml
	 *
	 * @author chenyunlong
	 * @date 2013-3-28
	 * @param oid
	 * @param bytes
	 * @return
	 *
	 */
	public static Boolean savePBOMXml2(String oid, byte[] bytes) {
		log.debug("提交，正在向服务器提交数据");
		return (Boolean) remoteMethodInvoke("savePBOMXmlRMI2", new Class[] { String.class, byte[].class }, new Object[] {
				oid, bytes });
	}

	/**
	 * 获取PBOM零件属性
	 *
	 * @author qianlong
	 */
	public static Map<String, List<String>> getPBOMAttributesValue() {
		if(null == map){
			map = (Map<String, List<String>>) remoteMethodInvoke("getPBOMAttributesValueRMI", null, null);
		}
		return map;
	}

	/**
	 * 获取零件类型
	 *
	 * @author qianlong
	 * @date 2012-11-30
	 * @param part
	 *
	 */
	public static String getTypeName(WTPart part) {
		return (String) remoteMethodInvoke("getTypeNameRMI", new Class[] { WTPart.class }, new Object[] { part });
	}

	/**
	 * 通过名称获取视图oid
	 *
	 * @author qianlong
	 * @date 2012-12-10
	 * @param name
	 * @return
	 *
	 */
	public static Long getViewByName(String name) {
		return (Long) remoteMethodInvoke("getViewByNameRMI", new Class[] { String.class }, new Object[] { name });
	}

	/**
	 * 获取Planning
	 *
	 * @author qianlong
	 * @date 2012-12-10
	 * @param oid
	 * @param viewId
	 * @return
	 *
	 */
	public static WTPart getPlanningPart(String number, String oid, Long viewId, boolean needNew) {
		return (WTPart) remoteMethodInvoke("getPlanningPartRMI", new Class[] { String.class, String.class, Long.class,
				boolean.class }, new Object[] { number, oid, viewId, needNew });
	}

	/**
	 * 获取产品结构
	 *
	 * @author qianlong
	 * @date 2013-2-22
	 * @param oid
	 * @return
	 *
	 */
	public static String getRootByChild(String oid) {
		return (String) remoteMethodInvoke("getRootByChildRMI", new Class[] { String.class }, new Object[] { oid });
	}

	/**
	 * 获取打开3D图档的Url
	 *
	 * @author chenyunlong
	 * @date 2013-3-28
	 * @param oid
	 * @return
	 *
	 */
	public static String get3DDocumentUrl(String oid) {
		return (String) remoteMethodInvoke("get3DDocumentUrlRMI", new Class[] { String.class }, new Object[] { oid });
	}

	/**
	 * 获取打开2D图档的Url
	 *
	 * @author chenyunlong
	 * @date 2013-3-28
	 * @param oid
	 * @return
	 *
	 */
	public static String get2DDocumentUrl(String oid, long viewOid) {
		return (String) remoteMethodInvoke("get2DDocumentUrlRMI", new Class[] { String.class, Long.class },
				new Object[] { oid, viewOid });
	}
	/**
	 * 获取辅件列表
	 * @author chenyunlong
	 * @date  2013-4-11
	 * @return
	 *
	 */
	public static List<Map<String,String>> getAllAssistantPart(Map<String, String> map){
		return (List<Map<String,String>>) remoteMethodInvoke("getAllAssistantPartRMI", new Class[] {Map.class },new Object[] {map});
	}

	/**
	 * 修改工艺辅件的使用数量
	 *
	 * @author qianlong
	 * @date 2013-4-11
	 *
	 */
	public static boolean modifyAssistantPartQuantity(String oid, Map<String, String> map) {
		return (Boolean) remoteMethodInvoke("modifyAssistantPartQuantityRMI", new Class[] { String.class, Map.class },
				new Object[] { oid, map });
	}

	/**
	 * 远端调用
	 *
	 * @author chenyunlong
	 * @date 2013-4-10
	 * @param methodName
	 * @param classArray
	 * @param objectArray
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	private static Object remoteMethodInvoke(String methodName, Class[] classArray, Object[] objectArray) {
		Object object = null;
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		if("reName".equals(methodName)){
			methodServer.setUserName("wcadmin");
			methodServer.setPassword("Admin@149");
		}

		try {
			object = methodServer.invoke(methodName, "com.glaway.mpm.intf.PBOMEditorToWCIntfRMI", null, classArray,objectArray);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return object;
	}
	/**
	 * 获取EBOM变更前的xml
	 * @author chenyunlong
	 * @date  2013-5-17
	 * @param oid
	 * @return
	 *
	 */
	public static byte[] getEBOMXml(String oid) {
		return (byte[]) remoteMethodInvoke("getEBOMXmlRMI", new Class[] { String.class }, new Object[] { oid });
	}

	/**
	 * 获取整件pvs
	 * @param map "oid" "viewName"
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, byte[]> getPVSAndMarkupRMI(Map<String, String> map) {
		return (Map<String, byte[]>) remoteMethodInvoke("getPVSAndMarkupRMI", new Class[] { Map.class },
				new Object[] { map });
	}

	/**
	 * 保存PBOM更改后的图形
	 *
	 * @author chenyunlong
	 * @date 2013-11-7
	 * @param partOid
	 * @param data
	 * @param fileName
	 *
	 */
	public static void savePBomRepresentation(String partOid, byte[] data, String fileName) {
		remoteMethodInvoke("savePBomRepresentation", new Class<?>[] { String.class, byte[].class, String.class },
				new Object[] { partOid, data, fileName });
	}

	/**
	 * PBOM同步EBOM后，修改当前的EBOM的xml的状态，防止再次打开
	 * @author chenyunlong
	 * @date  2013-6-5
	 * @param part
	 * @return
	 *
	 */
	public static boolean saveEbomComparedFlageRMI(WTPart part){
		return (Boolean) remoteMethodInvoke("saveEbomComparedFlageRMI", new Class[] { WTPart.class },
				new Object[] { part });
	}

	public static List<String> getCurrentUserInfoRMI() {
		return (List<String>) remoteMethodInvoke("getCurrentUserInfoRMI", new Class[] { },new Object[] {  });
	}

	/**
	 * 获取最新的planning零件
	 *
	 * @author qianlong
	 * @date 2013-6-7
	 * @return
	 *
	 */
	public static Map<String, Object> updatePartVersion(List<Map<String, Object>> list) {
		return (Map<String, Object>) remoteMethodInvoke("updatePartVersion", new Class[] {List.class },new Object[] { list });
	}



	/**
	 * 保存Erp创建的零部件到wc
	 * @param dataList
	 * @return
	 */
	public static ErpResult saveErpCjPart2Wc(List<Wzk> dataList){
		return (ErpResult) remoteMethodInvoke("saveErpCjPart2Wc", new Class[] { List.class },new Object[] {dataList });
	}

	public static Wzk setWzkByWTPart(WTPart part){
		String type = String.valueOf(ErpUtil.ERP_OP_TYPE_ALL);
		return (Wzk) remoteMethodInvoke("setWzkByWTPart", new Class[] {WTPart.class ,String.class},new Object[] { part,type });

	}

	public static String[] queryDept(String partId){
		return (String[]) remoteMethodInvoke("queryDept", new Class[] {String.class },new Object[] { partId });

	}
	public static WTPart createPlanningPartRMI(WTPart parentPart , String number,String name) {
		return (WTPart) remoteMethodInvoke("createPlanningPartRMI", new Class[] {WTPart.class ,String.class,String.class},new Object[] { parentPart, number,name});
	}
	public static WTPart createPlanningPartByWzlbRMI(WTPart parentPart , String number,String name,String wzlb){
		return (WTPart) remoteMethodInvoke("createPlanningPartByWzlbRMI", new Class[] {WTPart.class ,String.class,String.class,String.class},new Object[] { parentPart, number,name,wzlb});
	}


	public static Long queryLatestPartIdByNumberRMI(String number){
		return (Long) remoteMethodInvoke("queryLatestPartIdByNumberRMI", new Class[] {String.class },new Object[] {  number });
	}

	public static boolean updatePartNumberRMI(WTPart part,String number){
		return (Boolean) remoteMethodInvoke("updatePartNumberRMI", new Class[] {WTPart.class,String.class },new Object[] { part, number });

	}

	public static boolean isUsedWTPart(WTPart part) {
		return  (Boolean) remoteMethodInvoke("isUsedWTPart", new Class[] {WTPart.class },new Object[] { part });
	}

	/**
	 * 为创建零部件功能创建part
	 * @param number
	 * @param name
	 * @param attributes
	 * @param ibaattributes
	 * @param containerRef
	 * @return
	 */
    public static WTPart createPartForAddPart(String number, String name, HashMap attributes, HashMap ibaattributes,
            WTContainer containerRef){
    	return (WTPart) remoteMethodInvoke("createPartForAddPart", new Class[] {String.class, String.class, HashMap.class, HashMap.class,
                WTContainer.class },new Object[] {  number,  name,  attributes,  ibaattributes,containerRef });
    }

    public static List<WTPart> queryPartByLikeNumberNameView(String view, String number, String name){
    	return  (List<WTPart>) remoteMethodInvoke("queryPartByLikeNumberNameView", new Class[] {String.class ,String.class ,String.class },new Object[] { view,number,name });

    }

    public static ErpResult saveErpClAttributeToWc(List<Wzk> wzkList){
    	return  (ErpResult) remoteMethodInvoke("saveErpClAttributeToWcRMI", new Class[] {List.class },new Object[] { wzkList });
    }

    public static ErpResult saveLineAndTypeAttributeToWc(Map<String,String> map){
    	return  (ErpResult) remoteMethodInvoke("saveLineAndTypeAttributeToWcRMI", new Class[] {Map.class },new Object[] { map });
    }

    public static String getPartTypeByPartOid(long oid) {
    	return  (String) remoteMethodInvoke("getPartTypeByPartOid", new Class[] {long.class },new Object[] { oid });
    }

    public static String getPartIBAValueByPartOid(long oid,String ibaKey) {
    	return  (String) remoteMethodInvoke("getPartIBAValueByPartOid", new Class[] {long.class,String.class },new Object[] { oid,ibaKey });
    }

    public static String getPartLinkIBAValueByPartOid(String parentPartNumber,long oid,String ibaKey) {
    	return  (String) remoteMethodInvoke("getPartLinkIBAValueByPartOid", new Class[] {String.class,long.class,String.class },new Object[] { parentPartNumber,oid,ibaKey });
    }

    public static void savePartIBAValue(long oid,Map<String,String> ibaMap) {
    	remoteMethodInvoke("savePartIBAValue", new Class[] {long.class,Map.class },new Object[] { oid,ibaMap });
    }

    public static void saveCHBMIBAValueForPartLink(String parentNumber,long oid,Map<String,String> ibaMap) {
    	remoteMethodInvoke("saveCHBMIBAValueForPartLink", new Class[] {String.class,long.class,Map.class },new Object[] {parentNumber, oid,ibaMap });
    }

    public static Vector<String> getBatchsByProductOid(long oid) {
    	return (Vector<String>)remoteMethodInvoke("getBatchsByProductOid", new Class[] { long.class },new Object[] { oid });
    }

    public static void setPartState(long oid,String state) {
    	remoteMethodInvoke("setPartState", new Class[] {long.class,String.class },new Object[] { oid,state });
    }
	public static String setPartStateForPbom(long oid,String state,String batch) {
		return (String) remoteMethodInvoke("setPartStateForPBOM", new Class[] {long.class,String.class,String.class },new Object[] { oid,state,batch });
	}

    public static boolean checkPartTechnicsIsReleased(long partOid) {
    	return  (Boolean) remoteMethodInvoke("checkPartTechnicsIsReleased", new Class[] { long.class },new Object[] { partOid });
    }

    public static WTPart getLatestPart(String number,String viewName) {
    	return (WTPart)remoteMethodInvoke("getLatestPart", new Class[] {String.class,String.class },new Object[] { number,viewName });
    }

    public static List<String> getUp_Parts(String number,String source){
    	return  (List<String>) remoteMethodInvoke("getUp_PartsRMI", new Class[] {String.class ,String.class},new Object[] { number,source });
    }

    public static HashMap<WTPart,List<String>> getDownPartOnlyStepFromPdmSystem(String oid){
    	return (HashMap<WTPart,List<String>>) remoteMethodInvoke("getDownPartOnlyStepFromPdmSystemRMI", new Class[] {String.class },new Object[] { oid });
    }

    public static Map<String,String> getReleasedInfo(long oid) {
    	return (Map<String,String>) remoteMethodInvoke("getReleasedInfo", new Class[] {long.class },new Object[] { oid });
    }

    public static boolean startPbomReleasedNoticeWorkflow(long oid) {
    	return  (Boolean) remoteMethodInvoke("startPbomReleasedNoticeWorkflow", new Class[] { long.class },new Object[] { oid });
    }

	public static String pbomStructure2(List<Map<String, Object>> byteslist, Map<String, String> changeGysl) {
		return (String) remoteMethodInvoke("pbomStructure2RMI", new Class[] {List.class,Map.class}, new Object[] { byteslist,changeGysl });
	}
	public static String pbomStructure2(List<Map<String, Object>> byteslist) {
		return (String) remoteMethodInvoke("pbomStructure2RMI", new Class[] {List.class}, new Object[] { byteslist });
	}

	public static String replacePartRepFromEBOMToMBOM(String number,Boolean isAll) {
		return (String) remoteMethodInvoke("replacePartRepFromEBOMToMBOM", new Class[] {String.class,Boolean.class}, new Object[] { number,isAll });
	}

	public static String getGYSLFromWNC(String parentPartNumber, String partNumber) {
		return (String) remoteMethodInvoke("getGYSLFromWNC", new Class[] {String.class,String.class}, new Object[] { parentPartNumber,partNumber });

	}
	public static Map<String,String> getProsFromWNC( String partNumber) {
		return (Map<String,String>) remoteMethodInvoke("getProsFromWNC", new Class[] {String.class}, new Object[] { partNumber });

	}

	public static boolean reName(String partNumber, String sname) {
		return (Boolean)remoteMethodInvoke("reName", new Class[] {String.class,String.class}, new Object[] { partNumber,sname });
	}
	
	// 获取part增加的部分IBA名称及其对应值
	@SuppressWarnings("unchecked")
	public static Map<String, String> getIbaValues(String partNumber, String viewName, List<String> ibaNameList) {
		return (Map<String, String>) remoteMethodInvoke("getIbaValues", new Class[] { String.class, String.class, List.class }, new Object[] { partNumber, viewName, ibaNameList });
	}
}
