package com.glaway.mpm.sop.intf;

import com.glaway.mpm.sop.model.ParametersBean;
import com.glaway.mpm.sop.model.SopBean;
import com.glaway.mpm.sop.model.SopResourceBean;
import com.glaway.mpm.sop.util.SopIntfUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.ptc.windchill.mpml.resource.MPMTooling;
import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.method.RemoteAccess;
import wt.part.WTPart;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;


/**
 * @author chenjianhui
 * @version 2019-11-13
 */
public class SopIntf implements RemoteAccess {
    private static VaLogger logger = VaLogger.getLogger(SopIntf.class);
    private static Map<String, List<String>> sopResourceCache = new HashMap<String, List<String>>();

    /**
     * 调用远程方法
     *
     * @param mentodName  方法名
     * @param classArray  参数类型
     * @param objectArray 参数值
     * @return object
     */
    private static Object remoteMethodInvoke(String mentodName, Class[] classArray, Object[] objectArray) {
        return SopIntfUtil.getRemoteMethodInvoke(mentodName, classArray, objectArray);
    }

    public static void initSopResource(){
        sopResourceCache = (Map<String, List<String>>) remoteMethodInvoke("initSopResource", new Class[]{}, new Object[]{});
    }
    public static List<String> getAllCSXM(String zylb) {
        return (List<String>) remoteMethodInvoke("getAllCSXM", new Class[]{String.class}, new Object[]{zylb});
    }

    public static List<String> getSopResourceByType(String objType) {
        if(sopResourceCache != null && sopResourceCache.containsKey(objType)){
            return sopResourceCache.get(objType);
        }
        return (List<String>) remoteMethodInvoke("getSopResourceByType", new Class[]{String.class}, new Object[]{objType});
    }

    public static String getProductNumberAndName(String partNumber) {
        return (String) remoteMethodInvoke("getProductNumberAndName", new Class[]{String.class}, new Object[]{partNumber});
    }

    /**
     * 获取工艺计划对象的IBA属性
     *
     * @param typeName 类名称
     * @return Map<String   ,   String> key:属性名称,value：显示名称@默认值
     */
    public static Map<String, String> getAttrByMPMPlan(String typeName) {
        return (Map<String, String>) remoteMethodInvoke("getAttrByMPMPlan", new Class[]{String.class}, new Object[]{typeName});
    }

    /**
     * 根据物资类别获取工序名称
     *
     * @param zylb 专业类别
     * @return 工序名称map集合
     */
    public static Map<String, String> getSopProcessStepName(String zylb) {
        return (Map<String, String>) remoteMethodInvoke("getSopProcessStepName", new Class[]{String.class}, new Object[]{zylb});
    }

    public static Map<String, String> getAllSopProcessStepName(String type) {
        return (Map<String, String>) remoteMethodInvoke("getAllSopProcessStepName",  new Class[]{String.class}, new Object[]{type});
    }

    public static List<MPMTooling> getSopResourceListByType(String type) {
        return (List<MPMTooling>) remoteMethodInvoke("getSopResourceListByType",  new Class[]{String.class}, new Object[]{type});
    }

    public static Map<String, String> getAllCZMCByType(String type) {
        return (Map<String, String>) remoteMethodInvoke("getAllCZMCByType",  new Class[]{String.class}, new Object[]{type});
    }

    @SuppressWarnings("unchecked")
	public static List<String> getSopResourceName(String objType) throws InvocationTargetException, RemoteException {
		return (List<String>) remoteMethodInvoke("getSopResourceName", new Class[]{ String.class }, new Object[]{ objType });
    }

	@SuppressWarnings("unchecked")
	public static List<SopBean> querySop(SopBean sopBeanInfo) throws InvocationTargetException, RemoteException {
		return (List<SopBean>) remoteMethodInvoke("querySop", new Class[]{ SopBean.class }, new Object[]{ sopBeanInfo });
	}

	@SuppressWarnings("unchecked")
	public static List<ParametersBean> queryParameters(ParametersBean parametersBean) throws InvocationTargetException, RemoteException {
		return (List<ParametersBean>) remoteMethodInvoke("queryParameters", new Class[]{ ParametersBean.class }, new Object[]{ parametersBean });
	}

	@SuppressWarnings({ "unchecked" })
	public static List<Vector<Object>> downloadTechnics(String sopOid) throws InvocationTargetException, RemoteException {
		return (List<Vector<Object>>) remoteMethodInvoke("downloadTechnics", new Class[]{ String.class }, new Object[]{ sopOid });
	}

    /**
     * 根据专业类别获取物资类别
     * @param zylb 专业类别
     * @return 物资类别
     */
    private static Map<String,Map<String, String>> cacheMap = new HashMap<String, Map<String, String>>();
    public static Map<String, String> getMaterialCategoryByWzlb(String zylb){
        Map<String, String> map;
        if(cacheMap.containsKey(zylb)){
            map = cacheMap.get(zylb);
        }else{
            map = (Map<String, String>)remoteMethodInvoke("getMaterialCategoryByWzlb", new Class[]{String.class}, new Object[]{zylb});
            cacheMap.put(zylb,map);
        }
        return map;
    }

    public static List<SopResourceBean> searchParameters(String zylb, String csxmmc, String gxmc, String wzlb) {
        return (List<SopResourceBean>) remoteMethodInvoke("searchParameters", new Class[]{String.class,String.class,String.class,String.class}, new Object[]{zylb, csxmmc, gxmc, wzlb});
    }

    public static String getSopSeqNumber(Integer type, String pre){
        return (String) remoteMethodInvoke("getSopSeqNumber", new Class[]{ Integer.class, String.class }, new Object[]{ type, pre });
    }

    public static boolean checkProcessTaskItem(String workItemOid, String currentUser){
        return (Boolean)remoteMethodInvoke("checkProcessTaskItem", new Class[]{ String.class,String.class }, new Object[]{ workItemOid,currentUser });
    }

    public static boolean checkIsBiaoShenZhe(String technicsNumber, String technicsVersion,  String currentUser){
        return (Boolean) remoteMethodInvoke("checkIsBiaoShenZhe", new Class[]{ String.class,String.class,String.class }, new Object[]{ technicsNumber, technicsVersion, currentUser });
    }

    public static JSONObject getPrintInfo(String technicNumber, String version) {
        return (JSONObject) remoteMethodInvoke("getPrintInfo", new Class[]{ String.class,String.class }, new Object[]{ technicNumber, version });
    }

    public static SopBean buildSopBean(WTDocument document) {
        return (SopBean) remoteMethodInvoke("buildSopBean", new Class[]{WTDocument.class}, new Object[]{document});
    }

    public static String getLastestNumber(String pre, String typeName, String lastestNumber) {
        return (String) remoteMethodInvoke("getLastestNumber", new Class[]{String.class,String.class,String.class}, new Object[]{pre,typeName,lastestNumber});
    }

    public static List<WTPart> searchLatestPartList(String containerName, String number, String name, String viewName, Map<String, String> ibaMap,
            boolean isEqual, String type) {
        return (List<WTPart>) remoteMethodInvoke("searchLatestPartList", new Class[]{String.class,String.class,String.class,String.class,Map.class,boolean.class,String.class}, new Object[]{containerName,number,name,viewName,ibaMap,isEqual,type});
    }

    public static Map<String,String> getIBAMapByTooling(MPMTooling tooling) {
        return (Map<String,String>) remoteMethodInvoke("getIBAMapByTooling", new Class[]{MPMTooling.class}, new Object[]{tooling});
    }

    public static SopBean buildSopBeanByNumber(String number) {
        return (SopBean) remoteMethodInvoke("buildSopBeanByNumber", new Class[]{String.class}, new Object[]{number});
    }

}