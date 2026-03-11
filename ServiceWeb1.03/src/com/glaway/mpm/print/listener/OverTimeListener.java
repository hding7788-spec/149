package com.glaway.mpm.print.listener;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.intf.PrintToWCIntfRMI;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.pbom.db.ERPDBTask;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.FolderUtil;
import com.glaway.mpm.util.GLLogger;

public class OverTimeListener implements ServletContextListener{
	private static VaLogger logger = VaLogger.getLogger(OverTimeListener.class.getName());
	private Timer timer = null;
//	private ServletContext context = null;
//	private Boolean overTimeFlag = false;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
//		overTimeFlag = (Boolean) arg0.getServletContext().getAttribute("overTimeFlag");
//		overTimeFlag = false;
//		this.context = null;
		timer.cancel();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
//		context = arg0.getServletContext();
//		context.setAttribute("overTimeFlag", true);
		logger.debug("======================我是监听===========================");
//		long b = System.currentTimeMillis();
//		long c = (b-a)/1000;
		timer = new Timer();
		OverTime over = new OverTime();
		timer.schedule(over, 20*1000);
//		if(RemoteMethodServer.ServerFlag){
//			RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
////			methodServer.setUserName("wcadmin");
////			methodServer.setPassword("wcadmin");
//
//			try {
//				methodServer.invoke("execute", "com.glaway.mpm.intf.PrintToWCIntfRMI", null, new Class[]{}, new Object[]{});
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//
//				e.printStackTrace();
//			}

//		}
//
//	List<String> listNumber = FolderUtil.execute();
//	List<String> listID = PrintToWCIntfRMI.getDelayInfoByNumber(listNumber);
//	if(listID.size() > 0 ){
//		Map<String, String> map = PrintToWCIntfRMI.getDelayDateByID(listID);
//		for (String id : map.keySet()) {
//			String delayDate = map.get(id);
//			String time = getCurrentTime();
//			Boolean flag;
//			try {
//				flag = FolderUtil.CompareTime(time, delayDate);
//				if(flag){
//					list.add(id);
//				}
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}
//		System.out.println(list.size());
//		try {
//			PrintToWCIntfRMI.startProcessOfOverTime(list);
//		} catch (WTPropertyVetoException e) {
//			e.printStackTrace();
//		} catch (WTException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	}
}
