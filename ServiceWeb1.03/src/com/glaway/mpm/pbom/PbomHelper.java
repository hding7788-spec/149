package com.glaway.mpm.pbom;

import wt.part.WTPart;
import wt.pom.Transaction;

import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.glaway.mpm.util.IBAHelper;

public class PbomHelper {
	public static void savePbomWlType(String pids, String wltypes) {
		WTPart part = null;
		String partid = null;
		String[] pid = pids.split(",");
		String[] wltype = wltypes.split(",");
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			for (int i = 0; i < pid.length; i++) {
				partid = pid[i];
				part = (WTPart) CmSearchHelper.search(WTPart.class,
						Long.parseLong(partid));
				IBAHelper helper = new IBAHelper(part);
				helper.setIBAAnyValue(part, "CTYPE", wltype[i]);
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			transaction = null;
		}
	}


	public static void savePbomLine(String pids, String zzdws,String fzdws) {
		WTPart part = null;
		String partid = null;
		String[] pid = pids.split(",");
		String[] fzdw = fzdws.split(",");
		String[] zzdw = zzdws.split(",");
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			for (int i = 0; i < pid.length; i++) {
				partid = pid[i];
				part = (WTPart) CmSearchHelper.search(WTPart.class,
						Long.parseLong(partid));
				IBAHelper helper = new IBAHelper(part);
				helper.setIBAAnyValue(part, "ZZBM", zzdw[i]);
				helper.setIBAAnyValue(part, "FZBM", fzdw[i]);
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			transaction = null;
		}
	}

}
