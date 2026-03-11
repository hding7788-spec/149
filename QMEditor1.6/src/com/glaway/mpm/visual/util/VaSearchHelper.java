package com.glaway.mpm.visual.util;

import java.util.Vector;

import wt.fc.Persistable;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.vc.views.View;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.server.VaSearchSvrHelper;
import com.ptc.core.meta.common.TypeIdentifier;

public class VaSearchHelper implements RemoteAccess {
	private static final VaLogger log = VaLogger.getLogger(VaSearchHelper.class);
	

	private static final String SERVER_CLASS = VaSearchSvrHelper.class.getName();

	public static Persistable search(Class klass, long idA2A2) throws Exception {
		String method = "searchRMI";
		Class[] argTypes = { Class.class,long.class};
		Object[] argValues = { klass,idA2A2 };
		return (Persistable) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}
	
	public static WTPart getPartByNumberAndView(String number, long viewId) throws Exception {
		String method = "getPartByNumberAndViewRMI";
		Class[] argTypes = { String.class,long.class};
		Object[] argValues = { number,viewId };
		return (WTPart) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}

	
	public static View getViewByName(String viewName) throws Exception {
		String method = "getViewByNameRMI";
		Class[] argTypes = { String.class};
		Object[] argValues = { viewName };
		return (View) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}

	
	public static WTContainer getContainer(String name) throws Exception {
		String method = "getContainerRMI";
		Class[] argTypes = { String.class};
		Object[] argValues = { name };
		return (WTContainer) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}
	

	public static Vector searchDoc(TypeIdentifier ti, int queryLimit, String number, String name,
			String modifierFullName, String modifyTimeFrom, String modifyTimeTo) throws Exception {
		String method = "searchDoc";
		Class[] argTypes = { TypeIdentifier.class, Integer.TYPE, String.class, String.class, String.class,
				String.class, String.class };
		Object[] argValues = { ti, new Integer(queryLimit), number, name, modifierFullName, modifyTimeFrom,
				modifyTimeTo };

		return (Vector) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}

	public static Vector searchPart(TypeIdentifier ti, int queryLimit, String number, String name,
			String modifierFullName, String modifyTimeFrom, String modifyTimeTo) throws Exception {
		String method = "searchPart";
		Class[] argTypes = { TypeIdentifier.class, Integer.TYPE, String.class, String.class, String.class,
				String.class, String.class };
		Object[] argValues = { ti, new Integer(queryLimit), number, name, modifierFullName, modifyTimeFrom,
				modifyTimeTo };

		return (Vector) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}

	public static Vector searchEPM(TypeIdentifier ti, int queryLimit, String number, String name,
			String modifierFullName, String modifyTimeFrom, String modifyTimeTo) throws Exception {
		String method = "searchEPM";
		Class[] argTypes = { TypeIdentifier.class, Integer.TYPE, String.class, String.class, String.class,
				String.class, String.class };
		Object[] argValues = { ti, new Integer(queryLimit), number, name, modifierFullName, modifyTimeFrom,
				modifyTimeTo };

		return (Vector) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}

	public static Vector searchDoc(TypeIdentifier[] tis, int queryLimit, String number, String name,
			String modifierFullName, String modifyTimeFrom, String modifyTimeTo) throws Exception {
		String method = "searchDoc";
		Class[] argTypes = { TypeIdentifier[].class, Integer.TYPE, String.class, String.class, String.class,
				String.class, String.class };
		Object[] argValues = { tis, new Integer(queryLimit), number, name, modifierFullName, modifyTimeFrom,
				modifyTimeTo };

		return (Vector) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}

	public static Vector searchPart(TypeIdentifier[] tis, int queryLimit, String number, String name,
			String modifierFullName, String modifyTimeFrom, String modifyTimeTo) throws Exception {
		String method = "searchPart";
		Class[] argTypes = { TypeIdentifier[].class, Integer.TYPE, String.class, String.class, String.class,
				String.class, String.class };
		Object[] argValues = { tis, new Integer(queryLimit), number, name, modifierFullName, modifyTimeFrom,
				modifyTimeTo };

		return (Vector) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}

	public static Vector searchEPM(TypeIdentifier[] tis, int queryLimit, String number, String name,
			String modifierFullName, String modifyTimeFrom, String modifyTimeTo) throws Exception {
		String method = "searchEPM";
		Class[] argTypes = { TypeIdentifier[].class, Integer.TYPE, String.class, String.class, String.class,
				String.class, String.class };
		Object[] argValues = { tis, new Integer(queryLimit), number, name, modifierFullName, modifyTimeFrom,
				modifyTimeTo };

		return (Vector) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}
}
