package com.glaway.mpm.parameter.service;

import com.glaway.mpm.model.ConsCheckTree;
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.data.*;
import com.glaway.mpm.util.IntfUtil;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ProcessParameterToWCIntf {

	@SuppressWarnings("unchecked")
	public static List<CmParameterType> loadParamTypeData()
			throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (List<CmParameterType>) IntfUtil.parameterRemoteMethodInvoke("loadParamTypeData", cls, objs);
	}

	public static CmParameterType loadParamTypeData(String technicsType)
			throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { technicsType };
		return (CmParameterType) IntfUtil.parameterRemoteMethodInvoke("loadParamTypeData", cls, objs);
	}

	public static CmParameterType createParameterType(CmParameterType parameterType)
			throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { CmParameterType.class };
		Object[] objs = new Object[] { parameterType };
		return (CmParameterType) IntfUtil.parameterRemoteMethodInvoke("createParameterType", cls, objs);
	}

	public static boolean hasChinaName(CmParameterType parameterType)
			throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { CmParameterType.class };
		Object[] objs = new Object[] { parameterType };
		return (Boolean) IntfUtil.parameterRemoteMethodInvoke("hasChinaName", cls, objs);
	}

	public static CmParameterType saveParameterType(CmParameterType parameterType)
			throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { CmParameterType.class };
		Object[] objs = new Object[] { parameterType };
		return (CmParameterType) IntfUtil.parameterRemoteMethodInvoke("saveParameterType", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmTechnicsType> queryTechnicsTypes()
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		return (List<CmTechnicsType>) IntfUtil.parameterRemoteMethodInvoke("queryTechnicsTypes", cls, objs);
	}

	public static CmParamTableType createParamTableType(CmParamTableType paramTableType)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmParamTableType.class };
		Object[] objs = new Object[] { paramTableType };
		return (CmParamTableType) IntfUtil.parameterRemoteMethodInvoke("createParamTableType", cls, objs);
	}

	public static CmParamTableType saveParamTableType(CmParamTableType paramTableType)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmParamTableType.class };
		Object[] objs = new Object[] { paramTableType };
		return (CmParamTableType) IntfUtil.parameterRemoteMethodInvoke("saveParamTableType", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmParamTableType> queryParamTableTypes(Map<String, String> map)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { Map.class };
		Object[] objs = new Object[] { map };
		return (List<CmParamTableType>) IntfUtil.parameterRemoteMethodInvoke("queryParamTableTypes", cls, objs);
	}

	public static CmTemplateParamTable createTemplateParamTable(CmTemplateParamTable cmTemplateParamTable)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmTemplateParamTable.class };
		Object[] objs = new Object[] { cmTemplateParamTable };
		return (CmTemplateParamTable) IntfUtil.parameterRemoteMethodInvoke("createTemplateParamTable", cls, objs);
	}

	public static CmTemplateParamTable saveTemplateParamTable(CmTemplateParamTable cmTemplateParamTable)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmTemplateParamTable.class };
		Object[] objs = new Object[] { cmTemplateParamTable };
		return (CmTemplateParamTable) IntfUtil.parameterRemoteMethodInvoke("saveTemplateParamTable", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmTemplateParamTable> queryTemplateParamTable(String chinaName)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { chinaName };
		return (List<CmTemplateParamTable>) IntfUtil.parameterRemoteMethodInvoke("queryTemplateParamTable", cls, objs);
	}

	public static CmParamTableType queryCmParameterTableType(String gwkey)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { gwkey };
		return (CmParamTableType) IntfUtil.parameterRemoteMethodInvoke("queryCmParameterTableType", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<GWParamTableTypeMaster> queryAllParamTableTypeMasters()
			throws RemoteException, InvocationTargetException  {
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		return (List<GWParamTableTypeMaster>) IntfUtil.parameterRemoteMethodInvoke("queryAllParamTableTypeMasters", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static Vector<Vector<String>> queryParamsByTableId(String tableName, String tableId, String paramTableTypeIid)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class, String.class};
		Object[] objs = new Object[] {tableName, tableId, paramTableTypeIid};
		return (Vector<Vector<String>>) IntfUtil.parameterRemoteMethodInvoke("queryParamsByTableId", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static Vector<Vector<String>> queryTemplateTableParamsByTableId(String tableName, String tableId, String paramTableTypeIid)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class, String.class};
		Object[] objs = new Object[] {tableName, tableId, paramTableTypeIid};
		return (Vector<Vector<String>>) IntfUtil.parameterRemoteMethodInvoke("queryTemplateTableParamsByTableId", cls, objs);
	}

	public static CmTemplateParamTable queryCmTemplateParamTableByChinaName(String chinaName)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class};
		Object[] objs = new Object[] {chinaName};
		return (CmTemplateParamTable) IntfUtil.parameterRemoteMethodInvoke("queryCmTemplateParamTableByChinaName", cls, objs);
	}

	public static String createParamTableLink(String oid, String tableId, String dataType)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class, String.class};
		Object[] objs = new Object[] {oid, tableId, dataType};
		return (String) IntfUtil.parameterRemoteMethodInvoke("createParamTableLink", cls, objs);
	}

	public static String deleteParamTableLink(String oid, String tableId, String dataType)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class, String.class};
		Object[] objs = new Object[] {oid, tableId, dataType};
		return (String) IntfUtil.parameterRemoteMethodInvoke("deleteParamTableLink", cls, objs);
	}

	public static String deleteTemplateTableParamsByTableId(String tableName, String tableId, List<String> paramTypeIdList)
			throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] {String.class, String.class, List.class};
		Object[] objs = new Object[] {tableName, tableId, paramTypeIdList};
		return (String) IntfUtil.parameterRemoteMethodInvoke("deleteTemplateTableParamsByTableId", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmTechnicsType> loadParamTableTypeData()
			throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { };
		Object[] objs = new Object[] { };
		return (List<CmTechnicsType>) IntfUtil.parameterRemoteMethodInvoke("loadParamTableTypeData", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static CmTechnicsType loadSimpleParamTableTypeData(String technicsType)
			throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { technicsType };
		return (CmTechnicsType) IntfUtil.parameterRemoteMethodInvoke("loadSimpleParamTableTypeData", cls, objs);
	}

	public static CmParamTableType getCommonParamTableType(CmParamTableType paramTableType) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmParamTableType.class };
		Object[] objs = new Object[] { paramTableType };
		return (CmParamTableType) IntfUtil.parameterRemoteMethodInvoke("getCommonParamTableType", cls, objs);
	}

	public static CmParamTableType getCommonParamTableType(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, boolean.class, String.class, String.class };
		Object[] objs = new Object[] { technicsNumber, objType, objNumber, isApproved, bsoID, version };
		return (CmParamTableType) IntfUtil.parameterRemoteMethodInvoke("getCommonParamTableType", cls, objs);
	}

	public static CmParamTableType getCommonParamTableTypeForMes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, boolean.class, String.class, String.class };
		Object[] objs = new Object[] { technicsNumber, objType, objNumber, isApproved, bsoID, version };
		return (CmParamTableType) IntfUtil.parameterRemoteMethodInvoke("getCommonParamTableTypeForMes", cls, objs);
	}

	public static void saveParameters(CmParamTableType paramTableType, String technicsNumber, String objType, String objNumber, String bsoID, String version) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmParamTableType.class, String.class, String.class, String.class, String.class, String.class };
		Object[] objs = new Object[] { paramTableType, technicsNumber, objType, objNumber, bsoID, version };
		IntfUtil.parameterRemoteMethodInvoke("saveParameters", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmParamTableType> getParamTableTypes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, boolean.class, String.class, String.class };
		Object[] objs = new Object[] { technicsNumber, objType, objNumber, isApproved, bsoID, version };
		return (List<CmParamTableType>) IntfUtil.parameterRemoteMethodInvoke("getParamTableTypes", cls, objs);
	}

	public static void setSpecialParamTableIndex(String tableOid, String technicsNumber, String objType, String objNumber, String index) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class,String.class, String.class, String.class, String.class };
		Object[] objs = new Object[] { tableOid, technicsNumber, objType, objNumber, index };
		IntfUtil.parameterRemoteMethodInvoke("setSpecialParamTableIndex", cls, objs);
	}

	public static List<CmParamTableType> getParamTableTypesForMes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, boolean.class, String.class, String.class };
		Object[] objs = new Object[] { technicsNumber, objType, objNumber, isApproved, bsoID, version };
		return (List<CmParamTableType>) IntfUtil.parameterRemoteMethodInvoke("getParamTableTypesForMes", cls, objs);
	}

	public static void createObjToParamTableLink(String technicsNumber, String objType, String objNumber, String tableId, String tableIndex, String bsoID, String version) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class };
		Object[] objs = new Object[] { technicsNumber, objType, objNumber, tableId, tableIndex, bsoID, version};
		IntfUtil.parameterRemoteMethodInvoke("createObjToParamTableLink", cls, objs);
	}

	public static void deleteObjToParamTableLink(String technicsNumber, String objType, String objNumber, String tableId, String bsoID, String version) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, String.class, String.class, String.class };
		Object[] objs = new Object[] { technicsNumber, objType, objNumber, tableId,  bsoID, version };
		IntfUtil.parameterRemoteMethodInvoke("deleteObjToParamTableLink", cls, objs);
	}

	public static void deleteParameterType(CmParameterType parameterType) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmParameterType.class };
		Object[] objs = new Object[] { parameterType };
		IntfUtil.parameterRemoteMethodInvoke("deleteParameterType", cls, objs);
	}
	public static String queryMaxEnname() throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		return (String) IntfUtil.parameterRemoteMethodInvoke("queryMaxEnname", cls, objs);
	}
	public static String queryMaxNameFromTypeMaster() throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		return (String) IntfUtil.parameterRemoteMethodInvoke("queryMaxNameFromTypeMaster", cls, objs);
	}
	public static void uploadImage(byte[] bytes, String time, String uuid) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] {byte[].class, String.class, String.class};
		Object[] objs = new Object[] {bytes, time, uuid};
		IntfUtil.parameterRemoteMethodInvoke("uploadImage", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, byte[]>> getAllImages() throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		return (Map<String, Map<String, byte[]>>) IntfUtil.parameterRemoteMethodInvoke("getAllImages", cls, objs);
	}
	public static void deleteOldUUIDFiles(List<String> oldUUIDFileNameList) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { List.class };
		Object[] objs = new Object[] { oldUUIDFileNameList };
		IntfUtil.parameterRemoteMethodInvoke("deleteOldUUIDFiles", cls, objs);
	}
	public static void deleteTableParams(String tableName, String technicsNumber, String objType, String objNumber, String bsoID, String version) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, String.class, String.class, String.class };
		Object[] objs = new Object[] { tableName, technicsNumber, objType, objNumber, bsoID, version };
		IntfUtil.parameterRemoteMethodInvoke("deleteTableParams", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<String> searhTableConfigName() throws RemoteException, InvocationTargetException{
		Class<?>[] cls = null;
		Object[] objs = null;
		return (List<String>)IntfUtil.parameterRemoteMethodInvoke("searhTableConfigName", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<String> searhObjConfigName() throws RemoteException, InvocationTargetException{
		Class<?>[] cls = null;
		Object[] objs = null;
 		return (List<String>)IntfUtil.parameterRemoteMethodInvoke("searhObjConfigName", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<String> searhObjConfigNameJC() throws RemoteException, InvocationTargetException{
		Class<?>[] cls = null;
		Object[] objs = null;
		return (List<String>)IntfUtil.parameterRemoteMethodInvoke("searhObjConfigNameJC", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<String> searhObjConfigNameJL() throws RemoteException, InvocationTargetException{
		Class<?>[] cls = null;
		Object[] objs = null;
		return (List<String>)IntfUtil.parameterRemoteMethodInvoke("searhObjConfigNameJL", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmBaiyuParamTableColumn> getBaiyuParamLists(String isUsed) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { isUsed };
		return (List<CmBaiyuParamTableColumn>) IntfUtil.parameterRemoteMethodInvoke("getBaiyuParamLists", cls, objs);
	}
	
	@SuppressWarnings("unchecked")
	public static List<CmBaiyuParamTableColumn> getBaiyuParamListsByCondition(String id,String name,String creator,String modifier,String status,String department,String formType,Date createfrom,Date createTo,Date modifyFrom,Date modifyTo) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class,String.class,String.class,String.class,String.class,String.class,String.class,Date.class,Date.class,Date.class,Date.class };
		Object[] objs = new Object[] { id,name,creator,modifier,status,department,formType,createfrom,createTo,modifyFrom,modifyTo };
		return (List<CmBaiyuParamTableColumn>) IntfUtil.parameterRemoteMethodInvoke("getBaiyuParamListsByCondition", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static String deleteBaiyuTemplateByOid(String oid) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { oid };
		return (String) IntfUtil.parameterRemoteMethodInvoke("deleteBaiyuTemplateByOid", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static String changeBaiyuTemplateStateByOid(String oid,String status) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class };
		Object[] objs = new Object[] { oid ,status};
		return (String) IntfUtil.parameterRemoteMethodInvoke("changeBaiyuTemplateStateByOid", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<String>> quoteBaiyuTemplate(ArrayList<ArrayList<String>> lists, String technicsNumber, String stepNumber, String paceNumber) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { ArrayList.class, String.class, String.class, String.class };
		Object[] objs = new Object[] { lists,technicsNumber,stepNumber,paceNumber};
		return (ArrayList<ArrayList<String>>) IntfUtil.parameterRemoteMethodInvoke("quoteBaiyuTemplate", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmBaiyuParamTableColumn> getBaiyuParamListsByName(String name, String tableType, String dept) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class,String.class,String.class };
		Object[] objs = new Object[] { name,tableType,dept };
		return (List<CmBaiyuParamTableColumn>) IntfUtil.parameterRemoteMethodInvoke("getBaiyuParamListsByName", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static String updateBaiyuTemplateByOid(String oid,CmBaiyuParamTableColumn tableColumn) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, CmBaiyuParamTableColumn.class };
		Object[] objs = new Object[] { oid,tableColumn };
		return (String) IntfUtil.parameterRemoteMethodInvoke("updateBaiyuTemplateByOid", cls, objs);
	}

	public static ConsCheckTree packageConsCheckTree(String type) throws InvocationTargetException, RemoteException {
		return (ConsCheckTree) IntfUtil.parameterRemoteMethodInvoke("packageConsCheckTree", new Class[]{String.class}, new Object[]{ type});
	}

	public static ConsCheckTree packageConsCheckDetailTree(String type) throws InvocationTargetException, RemoteException {
		return (ConsCheckTree) IntfUtil.parameterRemoteMethodInvoke("packageConsCheckDetailTree", new Class[]{String.class}, new Object[]{ type});
	}

	public static String addConsCheckTree(String gwkeyid, String fName) throws InvocationTargetException, RemoteException {
		return (String) IntfUtil.parameterRemoteMethodInvoke("addConsCheckTree", new Class[]{String.class,String.class}, new Object[]{ gwkeyid,fName});
	}

	public static String modifyConsCheckTree(String gwkeyid, String fName) throws InvocationTargetException, RemoteException {
		return (String) IntfUtil.parameterRemoteMethodInvoke("modifyConsCheckTree", new Class[]{String.class,String.class}, new Object[]{ gwkeyid,fName});
	}

	public static String addConsCheckNode(String gwkeyid, String fName,String xmType, String type) throws InvocationTargetException, RemoteException {
		return (String) IntfUtil.parameterRemoteMethodInvoke("addConsCheckNode", new Class[]{String.class,String.class,String.class,String.class}, new Object[]{ gwkeyid,fName,xmType,type});
	}

	public static String modifyConsCheckNode(String gwkeyid, String fName,String type) throws InvocationTargetException, RemoteException {
		return (String) IntfUtil.parameterRemoteMethodInvoke("modifyConsCheckNode", new Class[]{String.class,String.class,String.class}, new Object[]{ gwkeyid,fName,type});
	}

	public static Vector getAllConsCheckRecords(String type) throws InvocationTargetException, RemoteException {
		return (Vector) IntfUtil.parameterRemoteMethodInvoke("getAllConsCheckRecords", new Class[]{String.class}, new Object[]{type});
	}

	public static String deleteConsCheckTree(String gwkeyid, String type) throws InvocationTargetException, RemoteException {
		return (String) IntfUtil.parameterRemoteMethodInvoke("deleteConsCheckTree", new Class[]{String.class,String.class}, new Object[]{ gwkeyid,type});
	}

	public static String deleteConsCheckNode(String gwkeyid, String type) throws InvocationTargetException, RemoteException {
		return (String) IntfUtil.parameterRemoteMethodInvoke("deleteConsCheckNode", new Class[]{String.class,String.class}, new Object[]{ gwkeyid,type});
	}
}
