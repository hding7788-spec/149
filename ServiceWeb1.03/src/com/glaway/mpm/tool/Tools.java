package com.glaway.mpm.tool;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.meta.common.TypeIdentifierHelper;

public class Tools {
	public static void main(String[] args) throws RemoteException, WTException {
		if (args.length == 0) {
			System.out.println(" mpm tools usage... ");
			System.out.println("1. reassign_all_purchasedPart_Lifecycle() ");
			System.out.println("2. reassign_part_lifecycle(String partOid) ");
			System.out.println("3. resertBOM_lifecycle(String partNumber) ");

		}
		if (args.length >= 1) {
			String methodName = args[0];
			if ("reassign_all_purchasedPart_Lifecycle".equalsIgnoreCase(methodName)) {
				reassign_all_purchasedPart_Lifecycle();
			}
			if ("reassign_part_lifecycle".equalsIgnoreCase(methodName)) {
				reassign_part_lifecycle(args[1]);
			}
			if("resertBOM_lifecycle".equalsIgnoreCase(methodName)){
				resertBOM_lifecycle(args[1]);
			}
		}

	}

	public static void resertBOM_lifecycle(String partNumber) throws WTException{
		WTPart part=WTPartUtil.getLatestPartByNumberAndView(partNumber, "Design");
		if(part==null){
			System.out.println("part==null"+ partNumber);
			return;
		}
		resetBOMLc(part);
	}
	public static void resetBOMLc(WTPart part) throws WTException{
		List<WTPart> parts=WTPartUtil.getChildPart(part);
		for(WTPart p:parts){
			System.out.println("p="+p.getNumber());
			LifeCycleState state=p.getState();
			LifeCycleTemplate lct=(LifeCycleTemplate)p.getLifeCycleTemplate().getObject();
			lct=(LifeCycleTemplate)VersionControlHelper.service.getLatestIteration(lct, true);
			LifeCycleManaged md=LifeCycleHelper.service.reassign(p, LifeCycleTemplateReference.newLifeCycleTemplateReference(lct));
			md=LifeCycleHelper.service.setLifeCycleState(md, state.getState());
			resetBOMLc(p);
		}
	}

	public static void reassign_part_lifecycle(String partOid) throws WTRuntimeException, WTException {
		System.out.println("reassign_part_lifecycle start...");
		System.out.println("partOid=" + partOid);
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(partOid);
		System.out.println("part number=" + part.getName() + " " + part.getNumber());
		LifeCycleTemplate lc = null;
		lc = (LifeCycleTemplate) part.getLifeCycleTemplate().getObject();
		lc = (LifeCycleTemplate) VersionControlHelper.service.getLatestIteration(lc, true);
		System.out.println(lc.getName() + " " + lc.getIterationInfo());
		LifeCycleManaged md = LifeCycleHelper.service.reassign(part, LifeCycleTemplateReference
				.newLifeCycleTemplateReference(lc));
		LifeCycleHelper.service.setLifeCycleState(md, part.getState().getState());
		System.out.println("reassign_part_lifecycle end...");
	}

	public static void reassign_all_purchasedPart_Lifecycle() throws RemoteException, WTException {
		System.out.println("reassign_all_purchasedPart_Lifecycle start...");
		String waigou = "wt.part.WTPart|com.nriet.PurchasedPart";
		QueryResult qr = getPartByLikeNumberNameType(waigou);
		LifeCycleTemplate lc = null;
		while (qr.hasMoreElements()) {
			WTPart part = (WTPart) qr.nextElement();
			System.out.println("partNumber=" + part.getNumber());
			System.out.println("type=" + TypeIdentifierHelper.getType(part));
			LifeCycleState state = part.getState();
			lc = (LifeCycleTemplate) part.getLifeCycleTemplate().getObject();
			lc = (LifeCycleTemplate) VersionControlHelper.service.getLatestIteration(lc, true);
			System.out.println(lc.getName() + " " + lc.getIterationInfo());
			if (!lc.getName().equals("十四所器件生命周期")) {
				continue;
			}
			LifeCycleManaged md = LifeCycleHelper.service.reassign(part, LifeCycleTemplateReference
					.newLifeCycleTemplateReference(lc));
			LifeCycleHelper.service.setLifeCycleState(md, state.getState());
			System.out.println("reassign_all_purchasedPart_Lifecycle end....");
		}
	}

	/**
	 *
	 * @author qianlong
	 * @date 2013-4-12
	 * @param typeName
	 * @param number
	 * @param name
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 *
	 */
	public static QueryResult getPartByLikeNumberNameType(String typeName) throws WTException, RemoteException {
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		TypeUtil.getTypeQuery(WTPart.class, typeName, qSpec);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		return qResult;
	}

	public static String getTime(Date date, boolean withMils) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if (withMils)
            pattern += ".SSS";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return sdf.format(date);
    }
}
