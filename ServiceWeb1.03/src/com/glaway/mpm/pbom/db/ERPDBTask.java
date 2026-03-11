package com.glaway.mpm.pbom.db;

import java.util.TimerTask;

import com.glaway.mpm.pbom.db.cache.CacheEngine;
import com.glaway.mpm.util.GLLogger;

public class ERPDBTask extends TimerTask {

	private  ERPService service;
	private  CacheEngine engine;
	public ERPDBTask(ERPService service,CacheEngine engine){
		this.service = service;
		this.engine = engine;
	}
	public void run() {
		long a = System.currentTimeMillis();
		try {
			engine.clear();
			service.loadWzkAdd();
			//service.loadWzkClass();
		} catch (Exception e) {
			e.printStackTrace();
			GLLogger.error("重新加载缓存异常",e);
		}
		long b = System.currentTimeMillis();
		long c = (b-a)/1000;
		GLLogger.info("加载缓存结束,用时："+c +"秒");
	}

}
