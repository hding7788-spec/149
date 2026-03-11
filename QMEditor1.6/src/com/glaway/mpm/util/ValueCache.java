package com.glaway.mpm.util;

import com.glaway.mpm.wcIntf.TechnicsIntf;
import wt.org.WTGroup;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class ValueCache {
	public static Map<String,String> allPartsType = new HashMap<String,String>();
	public static Map<String, WTGroup> aclGroupByGyType = null;

	public static Object[] typeValues= new String[]{"导管工艺", "钣金工艺", "焊接工艺"};
	public static String[] productTypes= new String[]{"导管", "桁条", "贮箱"};
	static {
		try {
			typeValues = TechnicsIntf.getMPMPPlanSubTypes().values().toArray();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
}
