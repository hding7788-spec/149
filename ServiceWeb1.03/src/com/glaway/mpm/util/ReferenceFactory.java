package com.glaway.mpm.util;

import wt.fc.Persistable;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.Iterated;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;

public class ReferenceFactory {
	public static wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();

	private ReferenceFactory() {
	}

	public static wt.fc.ReferenceFactory getFactory() {
		return factory;
	}

	public static Persistable getObjectbyOid(String oid) throws WTRuntimeException, WTException {
		return factory.getReference(oid).getObject();
	}

	/**
	 * get oid start with VR
	 * 
	 * @author Fly
	 * @date 2012-5-29
	 * @param iterated
	 * @return
	 * @throws VersionControlException
	 * @throws WTException
	 */
	public static String getVR(Iterated iterated) throws VersionControlException, WTException {
		return factory.getReferenceString(VersionControlHelper.service.getLatestIteration(iterated, false));
	}
}
