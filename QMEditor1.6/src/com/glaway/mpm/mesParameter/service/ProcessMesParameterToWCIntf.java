package com.glaway.mpm.mesParameter.service;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.glaway.mpm.mesParameter.model.Bdpsndoc;
import com.glaway.mpm.mesParameter.model.MesBfcccpbh;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.util.IntfUtil;

public class ProcessMesParameterToWCIntf {

	@SuppressWarnings("unchecked")
	public static Vector<Object> getTechnicsByTechnicNumber(String technicNumber) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { technicNumber };
		return (Vector) IntfUtil.mesParameterRemoteMethodInvoke("getTechnicsByTechnicNumber", cls, objs);
	}

	public static CmParamTableType getMESCommonParamTableType(boolean isApproved, Map<String, String> paramsMap) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { boolean.class, Map.class };
		Object[] objs = new Object[] { isApproved, paramsMap };
		return (CmParamTableType) IntfUtil.mesParameterRemoteMethodInvoke("getMESCommonParamTableType", cls, objs);
	}
	public static CmParamTableType getDataPackageCommonParamTableType(String productNumber, String technicsNumber, String objType, String objNumber, boolean isApproved, String lukahao, String gxPK) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, String.class, boolean.class, String.class, String.class };
		Object[] objs = new Object[] { productNumber, technicsNumber, objType, objNumber, isApproved, lukahao, gxPK };
		return (CmParamTableType) IntfUtil.mesParameterRemoteMethodInvoke("getDataPackageCommonParamTableType", cls, objs);
	}

	public static void saveMesParameters(CmParamTableType paramTableType, Map<String, String> paramsMap) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmParamTableType.class, Map.class };
		Object[] objs = new Object[] { paramTableType, paramsMap };
		IntfUtil.mesParameterRemoteMethodInvoke("saveMesParameters", cls, objs);
	}
	public static boolean isMesDataExist(CmParamTableType paramTableType, Map<String, String> paramsMap) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { CmParamTableType.class, Map.class};
		Object[] objs = new Object[] { paramTableType, paramsMap };
		return (Boolean) IntfUtil.mesParameterRemoteMethodInvoke("isMesDataExist", cls, objs);
	}
	public static boolean isDataPackageMesDataExist(CmParamTableType paramTableType, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { CmParamTableType.class, String.class, String.class, String.class, String.class, String.class, String.class};
		Object[] objs = new Object[] { paramTableType, productNumber, technicsNumber, objType, objNumber, lukahao, gxPK };
		return (Boolean) IntfUtil.mesParameterRemoteMethodInvoke("isDataPackageMesDataExist", cls, objs);
	}

	@SuppressWarnings("unchecked")
	public static List<CmParamTableType> getMesParamTableTypes(boolean isApproved, Map<String,String> paramsMap) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { boolean.class, Map.class };
		Object[] objs = new Object[] { isApproved, paramsMap };
		return (List<CmParamTableType>) IntfUtil.mesParameterRemoteMethodInvoke("getMesParamTableTypes", cls, objs);
	}
	public static boolean isMesSpecialDataExist(List<CmParamTableType> list, String productNumber, String technicsNumber, String objType, String objNumber) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { List.class, String.class, String.class, String.class, String.class};
		Object[] objs = new Object[] { list, productNumber, technicsNumber, objType, objNumber };
		return (Boolean) IntfUtil.mesParameterRemoteMethodInvoke("isMesSpecialDataExist", cls, objs);
	}
	public static void updataTechnicsPrimary(String technicNumber, byte[] bytes) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class, byte[].class};
		Object[] objs = new Object[] { technicNumber, bytes };
		IntfUtil.mesParameterRemoteMethodInvoke("updataTechnicsPrimary", cls, objs);
	}
	public static List<Bdpsndoc> getCampPersonInfo(String name, String number) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class, String.class};
		Object[] objs = new Object[] { name, number };
		return (List<Bdpsndoc>)IntfUtil.mesParameterRemoteMethodInvoke("getCampPersonInfo", cls, objs);
	}
	public static Object[] getZFTechnics(String technicNumber) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class};
		Object[] objs = new Object[] { technicNumber };
		return (Object[])IntfUtil.mesParameterRemoteMethodInvoke("getZFTechnics", cls, objs);
	}
	public static List<String[]> getFzTechnicsNumberList(String technicNumber) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class};
		Object[] objs = new Object[] { technicNumber };
		return (List<String[]>)IntfUtil.mesParameterRemoteMethodInvoke("getFzTechnicsNumberList", cls, objs);
	}
	public static Vector<Object> getTechnicDocumentByNumberAndVersion(String technicNumber, String version) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class, String.class};
		Object[] objs = new Object[] { technicNumber, version };
		return (Vector<Object>)IntfUtil.mesParameterRemoteMethodInvoke("getTechnicDocumentByNumberAndVersion", cls, objs);
	}
	public static List<MesBfcccpbh> getProcessNumberValues(String lukahao) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class};
		Object[] objs = new Object[] { lukahao };
		return (List<MesBfcccpbh>)IntfUtil.mesParameterRemoteMethodInvoke("getProcessNumberValues", cls, objs);
	}

	public static String getDefaultValues(Map<String, String> paramsMap) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { Map.class};
		Object[] objs = new Object[] { paramsMap };
		return (String)IntfUtil.mesParameterRemoteMethodInvoke("getDefaultValues", cls, objs);
	}
	public static Map<String, String> getProcessNumberGroup(Map<String, String> paramsMap) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { Map.class};
		Object[] objs = new Object[] { paramsMap };
		return (Map<String, String>)IntfUtil.mesParameterRemoteMethodInvoke("getProcessNumberGroup", cls, objs);
	}

}
