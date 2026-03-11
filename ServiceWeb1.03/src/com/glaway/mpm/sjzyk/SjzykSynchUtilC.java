package com.glaway.mpm.sjzyk;

import ext.casc.util.ExtQuerySpec;
import ext.casc.util.WCUtil;
import org.dom4j.DocumentException;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class SjzykSynchUtilC implements RemoteAccess {


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
						SjzykSynchUtilC.class.getName(), null, types, vals);
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
				QueryResult qr = queryPartByContainer(CONTAINERMAP.get(ss[i]));
				if (qr != null) {
					System.out.println(qr.size());
					while (qr.hasMoreElements()) {
						Persistable[] p2 = (Persistable[])qr.nextElement();
						SjzykSchedule.updateTechnicMaterialInfo((WTPart)p2[0]);
					}
				}
			}
		}

	}
	private static QueryResult queryPartByContainer(String containerName) throws WTException {
		WTLibrary wtlib = WCUtil.getLibraryByName(containerName);

		if(wtlib != null) {
			long libId = PersistenceHelper.getObjectIdentifier(wtlib).getId();
			QuerySpec qs = new QuerySpec();
			qs.setAdvancedQueryEnabled(true);
			int ibaHolderIndex = qs.appendClassList(WTPart.class, true);
			int[] index = { 0 };
			SearchCondition sc = new SearchCondition(WTPart.class,"containerReference.key.id",SearchCondition.EQUAL,libId);
			qs.appendWhere(sc, index);
			qs.appendAnd();
			ExtQuerySpec.appendIBACondition(qs,ibaHolderIndex,"BMDJ","C",SearchCondition.EQUAL);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);

			return qr;
		} else {
			System.out.println(containerName+" is not exsit");
		}
		return null;
	}

}
