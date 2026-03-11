package com.glaway.mpm.sjzyk;

import ext.casc.part.CSCPart;
import ext.casc.util.DBConn;
import ext.casc.util.WCUtil;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SjzykScheduleUpdate  implements RemoteAccess {

	private static final String[] CONTAINERS = {"八院标准紧固件库","八院元器件库","八院金属材料库","八院非金属材料库","八院复合材料库"};


	public static void process() {
		DBConn conn =null;
		try {
			conn = new DBConn();
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < CONTAINERS.length ; i++) {
				WTLibrary wtlib = WCUtil.getLibraryByName(CONTAINERS[i]);
				ids.append(wtlib.getPersistInfo().getObjectIdentifier().getId()).append(",");
			}
			ids.deleteCharAt(ids.length()-1);
			String sql = "select wtpartnumber from wtpartmaster  where  ida3containerreference in ("+ids+") and wtpartnumber not like 'ML%'  and  wtpartnumber not in (select wtpartnumber from  QUERYMIDDLE_TABLE )";
			ResultSet rs = conn.executeQuery(sql);
			String synchTime = String.valueOf(System.currentTimeMillis());
			while(rs.next()){
				String number = rs.getString("wtpartnumber");
				System.out.println("starting query sjyzk add data ......"+number);
				WTPart part = CSCPart.getPartByNumberAndViewName(number,"Design");
				if(part!=null){
					SjzykBean bean = SjzykSchedule.getSjzykBean("",part,synchTime);
					SjzykSchedule.insertData(bean);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("end Inert data into QUERYMIDDLE_TABLE ......");
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = "wcadmin";
		String passwd = "Admin@149.941";
		if (args.length >= 2) {
			username = args[0];
			passwd = args[1];
			if (username == null)
				username = "wcadmin";

			if (passwd == null)
				passwd = "Admin@149.941";
		}
		System.out.println("------user:"+username+"    password:"+passwd);
		rms.setUserName(username);
		rms.setPassword(passwd);
		if (!RemoteMethodServer.ServerFlag) {
			Class<?>[] types = null;
			Object[] vals = null;
			types = new Class<?>[] { };
			vals = new Object[] { };
			if (types != null && vals != null) {
				try {
					rms.invoke("process", SjzykScheduleUpdate.class.getName(), null, types, vals);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}else{
			SjzykScheduleUpdate.process();

		}
	}



}
