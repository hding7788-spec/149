package com.glaway.mpm.sjzyk;

import org.dom4j.DocumentException;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class SjzykSynchUtil implements RemoteAccess {


	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = null;
		String passwd = null;
		String libs = null;

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
		try {
			process(libs);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void process(String libs) throws WTException, IOException, PropertyVetoException, DocumentException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process";
			Class[] types = {String.class};
			Object[] vals = {libs };

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				rms.invoke(method,
						SjzykSynchUtil.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{

			Map<String,String> CONTAINERMAP  = new HashMap<String, String>();
			CONTAINERMAP.put("1","八院元器件库");
			CONTAINERMAP.put("2","八院标准紧固件库");
			CONTAINERMAP.put("3","八院金属材料库");
			CONTAINERMAP.put("4","八院非金属材料库");
			CONTAINERMAP.put("5","八院复合材料库");
			CONTAINERMAP.put("6","八院机电产品库");
			CONTAINERMAP.put("7","八院火工品库");
			String[] ss = libs.split(",");
			for (int i = 0; i < ss.length ; i++) {
				System.out.println(CONTAINERMAP.get(ss[i]));
				QueryResult qr = SjzykScheduleAll.queryPartByContainer(CONTAINERMAP.get(ss[i]));
				if (qr != null) {
					System.out.println(qr.size());
					while (qr.hasMoreElements()) {
						SjzykSchedule.updateTechnicMaterialInfo((WTPart)qr.nextElement());
					}
				}
			}
		}

	}

}
