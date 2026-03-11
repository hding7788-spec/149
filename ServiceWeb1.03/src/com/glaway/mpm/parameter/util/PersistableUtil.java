package com.glaway.mpm.parameter.util;

import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.util.WTException;

public class PersistableUtil {

	/**
	 * 通过oid获取对象.oid格式为类名加ida2a2.
	 * <br>比如：wt.part.WTPart:oid
	 *
	 *
	 * @param oid
	 * @return Persistable
	 * @throws WTException
	 */
	public static Persistable getPersistable(String oid) throws WTException {
		ReferenceFactory factory = new ReferenceFactory();
		WTReference reference = factory.getReference(oid);
		return reference.getObject();
	}

	/**
	 * 获取多谢的oid
	 *
	 * @param persistable
	 * @return
	 * @throws WTException
	 */
	public static String getOid(Persistable persistable) throws WTException {
		ReferenceFactory factory = new ReferenceFactory();
		return factory.getReferenceString(persistable);
	}

	public static String getVersion(RevisionControlled revisionControlled) {
		String version = revisionControlled.getVersionIdentifier().getValue()
				+ "." + revisionControlled.getIterationIdentifier().getValue();
		return version;
	}
}
