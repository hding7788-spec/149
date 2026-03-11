package com.glaway.mpm.tool;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

import com.glaway.mpm.pbom.db.ERPService;
import com.glaway.mpm.pbom.db.cache.CacheEngine;
import com.glaway.mpm.pbom.db.cache.DefaultCacheEngine;
import com.glaway.mpm.util.GLLogger;

public class ERPDBUpdateUtil  implements RemoteAccess {
	static ERPService service = null;
	static CacheEngine engine = null;
	public static String init() {
		/*if (!RemoteMethodServer.ServerFlag) {
            String method = "init";
            Class[] types = { };
            Object[] vals = { };
            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
				return (String)rms.invoke(method, ERPDBUpdateUtil.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }*/
		engine = new DefaultCacheEngine();//new EhCacheEngine();//new DefaultCacheEngine();
		engine.init();
		service = new ERPService();
		service.setCacheEngine(engine);
		GLLogger.info("重新加载缓存开始");
		long a = System.currentTimeMillis();
		try {
			service.loadWzk();
			service.loadWzkClass();
		} catch (Exception e1) {
			GLLogger.error("加载WZK缓存异常",e1);
			e1.printStackTrace();
			return "加载WZK缓存异常";
		}
		long b = System.currentTimeMillis();
		long c = (b-a)/1000;
		GLLogger.info("重新加载缓存结束,用时："+c +"秒");
		return "加载WZK缓存成功";
	}

	public static void main(String[] args) {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		try {
			methodServer.invoke("init", ERPDBUpdateUtil.class.getName(), null, new Class[] {}, new Object[] {});
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
