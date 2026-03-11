/**
 *
 */
package com.glaway.mpm.pbom.db.cache;

import java.rmi.RemoteException;

import wt.cache.CacheManager;

/**
 * @author cfire
 *
 */
public class ERPCache extends CacheManager {

	/**
	 * @throws RemoteException
	 */
	public ERPCache() throws RemoteException {
		super();
		this.put("1", "A");
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

}
