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
import java.util.TimerTask;

import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.intf.PrintToWCIntfRMI;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.print.util.FolderUtil;

public class OverTime extends TimerTask{
	private static VaLogger logger = VaLogger.getLogger(OverTime.class.getName());
	@Override
	public void run() {
		Class<?>[] cls = new Class[] {};
		Object[] objs = new Object[] {};
		try {
			remoteMethodInvoke("execute", cls, objs);
		} catch (RemoteException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static final String PRINT_SERVERNAME = "com.glaway.mpm.intf.PrintToWCIntfRMI";

	public static void remoteMethodInvoke(String mentodName,
			Class<?>[] classArray, Object[] objectArray) throws RemoteException,
			InvocationTargetException {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		try {
			methodServer.invoke(mentodName, PRINT_SERVERNAME, null, classArray, objectArray);
		} catch (Exception e) {
			logger.error(e);
		}
	}

//	private List<String> list = new ArrayList<String>();
//
//	public void execute(){
//		List<String> listNumber = FolderUtil.execute();
//		List<String> listID = PrintToWCIntfRMI.getDelayInfoByNumber(listNumber);
//		if(listID.size() > 0 ){
//			Map<String, String> map = PrintToWCIntfRMI.getDelayDateByID(listID);
//			for (String id : map.keySet()) {
//				String delayDate = map.get(id);
//				String time = getCurrentTime();
//				Boolean flag;
//				try {
//					flag = FolderUtil.CompareTime(time, delayDate);
//					if(flag){
//						list.add(id);
//					}
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//			}
//			System.out.println(list.size());
//			try {
//				PrintToWCIntfRMI.startProcessOfOverTime(list);
//			} catch (WTPropertyVetoException e) {
//				e.printStackTrace();
//			} catch (WTException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	public String getCurrentTime(){
//		Date nowDate = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//		String time = sdf.format(nowDate);
//		return time;
//	}
//	public static void main(String[] args) {
//		//Boolean overTimeFlag = OverTimeListener
//	}
}
