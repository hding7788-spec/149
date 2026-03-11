package com.glaway.mpm.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import wt.change2.AffectedActivityData;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

import com.glaway.mpm.constants.ProcessPlanConstants;
import com.glaway.mpm.log.VaLogger;
import com.ptc.windchill.enterprise.history.HistoryTablesCommands;
import com.ptc.windchill.enterprise.history.MaturityHistoryInfo;
import com.ptc.windchill.mpml.processplan.MPMPartToProcessPlanLink;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.util.IBAUtility;

public class ProcessPlanUtil {
	private static final String CLASSNAME=ProcessPlanUtil.class.getName();
	private static VaLogger logger = VaLogger.getLogger(ProcessPlanUtil.class.getName());
	public static void main(String[] args) throws WTException {
		RemoteMethodServer server = RemoteMethodServer.getDefault();
		server.setUserName("wcadmin");
		server.setPassword("wcadmin");

	MPMProcessPlan mp=(MPMProcessPlan)ReferenceFactory.getObjectbyOid("com.ptc.windchill.mpml.processplan.MPMProcessPlan:1139569");
	WTChangeIssue  iss=(WTChangeIssue)ReferenceFactory.getObjectbyOid("wt.change2.WTChangeIssue:1164069");
	System.out.println(iss.getName());

	WTArrayList list=new WTArrayList();
	list.add(mp);
//	Vector<
//	ChangeHelper2.service.storeAssociations(iss, list);
//	ChangeHelper2.service.storeAssociations(WTChangeIssue.class, iss, list);


	QueryResult  qr=	ChangeHelper2.service.getChangeables(iss,true);


	System.out.println("qr.size="+qr.size());
	while(qr.hasMoreElements()){
		 mp=(MPMProcessPlan)qr.nextElement();
		 System.out.println(mp.getName());
	}
//	ChangeHelper2.service

//		WTPart part=	WTPartUtil.getLatestPartByNumberAndView("AL7.015.0551", "Planning");
//		List<MPMProcessPlan> pps= getProcessPlanByPart( part);

	}

	/**
	 * 通过零件获得所关联的工艺规程
	 *
	 * @author fly
	 * @date 2013-5-16
	 * @return
	 * @throws WTException
	 *
	 */
	public static List<MPMProcessPlan> getProcessPlanByPart(WTPart part) throws WTException {
		List<MPMProcessPlan> list =new ArrayList<MPMProcessPlan>();
		QuerySpec qs = new QuerySpec(MPMPartToProcessPlanLink.class);
		long roleALongID = PersistenceHelper.getObjectIdentifier(part).getId();
		int[] index = { 0 };
		SearchCondition scCondition = new SearchCondition(MPMPartToProcessPlanLink.class, "roleAObjectRef.key.id",
				SearchCondition.EQUAL, roleALongID);
		qs.appendWhere(scCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qs);
		MPMPartToProcessPlanLink link = null;
		while (qResult.hasMoreElements()) {
			MPMProcessPlan mp=(MPMProcessPlan)((MPMPartToProcessPlanLink) qResult.nextElement()).getRoleBObject();
//			System.out.println(mp.getName());
//			System.out.println(mp.getNumber());
//			System.out.println(TypeIdentifierHelper.getType(mp).getTypename());
			GLLogger.debug(CLASSNAME, "mp name="+mp.getName());
			list.add(mp);
		}
		return list;
	}
//	/**
//	 * @author fly
//	 * @date  2013-5-20
//	 * @param partNumber
//	 * @return
//	 *
//	 */
//	public static WTChangeRequest2 getWTChangeRequest2ByPartNumber(String partNumber){
//
//	}

	public static Timestamp getWTObjectApproveDate(Persistable persistable){
		Timestamp approveTime = null;
		try {
			QueryResult qr = HistoryTablesCommands.maturityHistory((LifeCycleManaged) persistable);
			while (qr.hasMoreElements()) {
				MaturityHistoryInfo historyInfo = (MaturityHistoryInfo) qr.nextElement();
				String historyState = historyInfo.getLifecycle().getState().getDisplay(Locale.SIMPLIFIED_CHINESE);
				if (ProcessPlanConstants.LIFECYCLE_APPROVE.equals(historyState)) {
					Persistable historyPer = historyInfo.getIteration();
					long historyId = PersistenceHelper.getObjectIdentifier(historyPer).getId();
					long persistableId = PersistenceHelper.getObjectIdentifier(persistable).getId();
					if (persistableId == historyId) {
						List<Timestamp> timeList = historyInfo.getPromotedDate();
						approveTime = timeList.get(timeList.size() - 1);
						break;
					}
				}
			}
		} catch (WTException e1) {
			e1.printStackTrace();
		}

		return approveTime;
	}
	/**
	 * 根据改前对象获取关联的工艺更改单
	 * @param per
	 * @return
	 * @throws ChangeException2
	 * @throws WTException
	 */
	public static List<WTChangeOrder2> getEcnByPersistable(Persistable per) throws ChangeException2, WTException{
		List<WTChangeOrder2> list = new ArrayList<WTChangeOrder2>();
		QueryResult ecrQr2 = ChangeHelper2.service.getAffectingChangeActivities((Changeable2) per, false);
		while(ecrQr2.hasMoreElements()){
			AffectedActivityData data = (AffectedActivityData) ecrQr2.nextElement();
			WTChangeActivity2 eca = (WTChangeActivity2) data.getChangeActivity2();
			QueryResult ecnQr = ChangeHelper2.service.getChangeOrder(eca);
			while(ecnQr.hasMoreElements()){
				WTChangeOrder2 ecn = (WTChangeOrder2) ecnQr.nextElement();
				String type = IBAUtility.getSoftType(ecn);
				if (ProcessPlanConstants.SOFT_PROCESSECFORM.equals(type)) {
					list.add(ecn);
				}
			}
		}
		return list;
	}

	/**
	 * 移除指定附件
	 * @param content
	 * @param fileName
	 */
	public static boolean delete(ContentHolder content, String fileName){
		boolean flag = false;
		Transaction tran = new Transaction();
		try {
			tran.start();
			content = (ContentHolder) PersistenceHelper.manager.refresh(content);

			ApplicationData appData = null;
			QueryResult qr = ContentHelper.service.getContentsByRole(content, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				appData = (ApplicationData) qr.nextElement();
				String name = appData.getFileName();
				if (fileName.equals(name)) {
					ContentServerHelper.service.deleteContent(content, appData);
				}
			}

			tran.commit();
			flag = true;
		} catch (Exception e) {
			tran.rollback();
			flag = false;
			logger.error(e);
		}
		return flag;
	}

}
