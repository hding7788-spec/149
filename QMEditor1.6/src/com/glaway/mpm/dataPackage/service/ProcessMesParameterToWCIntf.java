package com.glaway.mpm.dataPackage.service;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.util.IntfUtil;

public class ProcessMesParameterToWCIntf {

	@SuppressWarnings("unchecked")
	public static Vector<Object> getTechnicsByTechnicNumber(String technicNumber) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { technicNumber };
		return (Vector) IntfUtil.mesParameterRemoteMethodInvoke("getTechnicsByTechnicNumber", cls, objs);
	}

	public static String getTechnicsNumberByProductNumber(String productNumber) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class };
		Object[] objs = new Object[] { productNumber };
		return (String) IntfUtil.mesParameterRemoteMethodInvoke("getTechnicsNumberByProductNumber", cls, objs);
	}

	public static CmParamTableType getMESCommonParamTableType(String productNumber, String technicsNumber, String objType, String objNumber, boolean isApproved, String lukahao, String gxPK) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, String.class, boolean.class, String.class, String.class };
		Object[] objs = new Object[] { productNumber, technicsNumber, objType, objNumber, isApproved, lukahao, gxPK };
		return (CmParamTableType) IntfUtil.mesParameterRemoteMethodInvoke("getMESCommonParamTableType", cls, objs);
	}

	public static void saveMesParameters(CmParamTableType paramTableType, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { CmParamTableType.class, String.class, String.class, String.class, String.class, String.class, String.class };
		Object[] objs = new Object[] { paramTableType, productNumber, technicsNumber, objType, objNumber, lukahao, gxPK };
		IntfUtil.mesParameterRemoteMethodInvoke("saveMesParameters", cls, objs);
	}
	public static boolean isMesDataExist(CmParamTableType paramTableType, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { CmParamTableType.class, String.class, String.class, String.class, String.class, String.class, String.class};
		Object[] objs = new Object[] { paramTableType, productNumber, technicsNumber, objType, objNumber, lukahao, gxPK };
		return (Boolean) IntfUtil.mesParameterRemoteMethodInvoke("isMesDataExist", cls, objs);
	}
	@SuppressWarnings("unchecked")
	public static List<CmParamTableType> getMesParamTableTypes(String productNumber, String technicsNumber, String objType, String objNumber, boolean isApproved, String lukahao, String gxPK) throws RemoteException, InvocationTargetException {
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, String.class, boolean.class, String.class, String.class };
		Object[] objs = new Object[] { productNumber, technicsNumber, objType, objNumber, isApproved, lukahao, gxPK };
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
	public static String getZzTechnicsNumber(String technicsNumber) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class};
		Object[] objs = new Object[] { technicsNumber };
		return (String)IntfUtil.mesParameterRemoteMethodInvoke("getZzTechnicsNumber", cls, objs);
	}
}
