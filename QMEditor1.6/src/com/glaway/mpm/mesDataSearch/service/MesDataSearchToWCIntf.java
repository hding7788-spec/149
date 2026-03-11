package com.glaway.mpm.mesDataSearch.service;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.util.IntfUtil;

public class MesDataSearchToWCIntf {

	public static List<TempObject> getTechnicsNumberList(String lukahao, String productNumber, String batch, String tuhao) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class, String.class, String.class, String.class };
		Object[] objs = new Object[] { lukahao, productNumber, batch, tuhao };
		return (List<TempObject>) IntfUtil.mesDataSearchRemoteMethodInvoke("getTechnicsNumberList", cls, objs);
	}

	public static CmParamTableType getCommonParamTableTypeForDataSearch(String technicsNumber, String productNumber, String lukahao) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class, String.class, String.class };
		Object[] objs = new Object[] { technicsNumber, productNumber, lukahao };
		return (CmParamTableType) IntfUtil.mesDataSearchRemoteMethodInvoke("getCommonParamTableTypeForDataSearch", cls, objs);
	}

	public static List<CmParamTableType> getMesDataSearchParamTableTypes(String technicsNumber, String productNumber, String lukahao) throws RemoteException, InvocationTargetException{
		Class<?>[] cls = new Class[] { String.class, String.class, String.class };
		Object[] objs = new Object[] { technicsNumber, productNumber, lukahao };
		return (List<CmParamTableType>) IntfUtil.mesDataSearchRemoteMethodInvoke("getMesDataSearchParamTableTypes", cls, objs);
	}
}