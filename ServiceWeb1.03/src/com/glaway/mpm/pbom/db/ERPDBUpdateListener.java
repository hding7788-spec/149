package com.glaway.mpm.pbom.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.glaway.mpm.pbom.db.cache.CacheEngine;
import com.glaway.mpm.pbom.db.cache.DefaultCacheEngine;
import com.glaway.mpm.util.DateUtil;
import com.glaway.mpm.util.GLLogger;



public class ERPDBUpdateListener implements ServletContextListener {
	private Timer timer = null;
	ERPService service = null;
	CacheEngine engine = null;
	CacheEngine engineAdd = null;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ERPDBUpdateListener.contextDestroyed");
		timer.cancel();
		engine.stop();
		engineAdd.stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		engine = new DefaultCacheEngine();//new EhCacheEngine();//new DefaultCacheEngine();
		engineAdd = new DefaultCacheEngine();
		engine.init();
		engineAdd.init();
		service = new ERPService();
		service.setCacheEngine(engine);
		service.setCacheEngineAdd(engineAdd);

		GLLogger.info("加载缓存开始");
		long a = System.currentTimeMillis();
		try {
			service.loadWzk();
			service.loadWzkClass();
		} catch (Exception e1) {
			GLLogger.error("加载WZK缓存异常",e1);
		}
		long b = System.currentTimeMillis();
		long c = (b-a)/1000;
		GLLogger.info("加载缓存结束,用时："+c +"秒");
		timer = new Timer();
		//Date d = null;
		/*try {
			d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateUtil.getDateAfter(DateUtil.getCurrentDate("yyyy-MM-dd"), 1)+" 00:00:01");
		} catch (ParseException e) {
			e.printStackTrace();
		}*/

		ERPDBTask task = new ERPDBTask(service, engineAdd);
		timer.schedule(task, 10*60*60*1000,2*60*60*1000);
		//timer.schedule(task, 5*60*1000,3*60*1000);
	}
}
