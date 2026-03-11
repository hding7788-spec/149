package com.glaway.mpm.pbom.db;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ext.sast.center.util.RestMessageQueue;



public class MQListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		RestMessageQueue.startRestQueue();
	}
}
