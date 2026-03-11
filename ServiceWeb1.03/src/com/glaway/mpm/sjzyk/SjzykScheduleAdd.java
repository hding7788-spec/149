package com.glaway.mpm.sjzyk;

import com.glaway.mpm.util.IBAHelper;
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
import java.util.HashMap;
import java.util.Map;

public class SjzykScheduleAdd implements RemoteAccess {

	public static void process(String libs) {
		DBConn conn =null;
		try {
			conn = new DBConn();
			Map<String,String> CONTAINERMAP  = new HashMap<String, String>();
			CONTAINERMAP.put("1","八院元器件库");
			CONTAINERMAP.put("2","八院标准紧固件库");
			CONTAINERMAP.put("3","八院金属材料库");
			CONTAINERMAP.put("4","八院非金属材料库");
			CONTAINERMAP.put("5","八院复合材料库");
			CONTAINERMAP.put("6","八院机电产品库");
			CONTAINERMAP.put("7","八院火工品库");
			StringBuffer ids = new StringBuffer();
			String[] ss = libs.split(",");
			for (int i = 0; i < ss.length ; i++) {
				if(!"".equals(ss[i])){
					WTLibrary wtlib = WCUtil.getLibraryByName(CONTAINERMAP.get(ss[i]));
					ids.append(wtlib.getPersistInfo().getObjectIdentifier().getId()).append(",");
				}
			}

			ids.deleteCharAt(ids.length()-1);
			String synchTime = String.valueOf(System.currentTimeMillis());

			String sql = "select wtpartnumber from wtpartmaster  where  ida3containerreference in ("+ids+") and wtpartnumber not like 'ML%'  and  wtpartnumber not in (select wtpartnumber from  QUERYMIDDLE_TABLE )";
			ResultSet rs = conn.executeQuery(sql);
			while(rs.next()){
				String number = rs.getString("wtpartnumber");
				System.out.println("starting query sjyzk add data ......"+number);
				WTPart part = CSCPart.getPartByNumberAndViewName(number,"Design");
				if(part!=null){
					IBAHelper ibaHelper = new IBAHelper(part);
					String fl = ibaHelper.getIBAValue("ClassificationNode");
					if(fl==null||"".equals(fl)||"null".equals(fl)) {
						continue;
					}
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
		String libs = "";
		if (args.length >= 2) {
			username = args[0];
			passwd = args[1];
			libs = args[2];
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
			types = new Class<?>[] {String.class };
			vals = new Object[] { libs};
			if (types != null && vals != null) {
				try {
					rms.invoke("process", SjzykScheduleAdd.class.getName(), null, types, vals);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}else{
			SjzykScheduleAdd.process(libs);

		}
	}



}
