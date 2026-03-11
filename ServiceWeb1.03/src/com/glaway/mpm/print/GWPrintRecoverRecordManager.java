package com.glaway.mpm.print;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.data.CmUser;
import com.glaway.mpm.print.model.GWPrintDistributeRecord;
import com.glaway.mpm.print.model.GWPrintRecoverRecord;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.util.MPMUtil;
import com.glaway.mpm.parameter.util.PersistableUtil;

public class GWPrintRecoverRecordManager{

	private static VaLogger logger = VaLogger.getLogger(GWPrintRecoverRecordManager.class.getName());
	public static void setDocType(WTDocument doc) throws RemoteException, WTException{
		TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.RECOVERAPPLYFORM");
		try {
			doc.setTypeDefinitionReference(tdr);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}
//	public static WTDocument creatRecoverApplyFrom(){
//
//	}
	public static GWPrintRecoverRecord queryGWPrintRecoverRecordByBarCodeAndDept(String barCode, String recoverDept) throws Exception{
		GwQuerySpec qs = new GwQuerySpec(GWPrintRecoverRecord.class);
		qs.appendWhere(GWPrintRecoverRecord.BARCODE, GwQuerySpec.EQUAL, barCode);
		qs.appendAnd();
		qs.appendWhere(GWPrintRecoverRecord.RECOVERDEPT, GwQuerySpec.EQUAL, recoverDept);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWPrintRecoverRecord gwPrintRecoverRecord = null;
		if(qr.hasNext()) {
			gwPrintRecoverRecord = (GWPrintRecoverRecord) qr.next();
		}
		return gwPrintRecoverRecord;
	}

	public static WTDocument createProcessPrintRecoverDoc(List<CmPrintInfoBean> list)
			throws Exception {
		CmPrintInfoBean cmPrintInfoBean = list.get(0);
		WTDocument doc = null;
		try {
			Persistable per = PersistableUtil.getPersistable(cmPrintInfoBean.getOid());
			WTContainer wtContainer = null;
			if(per instanceof MPMProcessPlan){
				MPMProcessPlan mpmProcessPlan = (MPMProcessPlan)per;
				wtContainer = mpmProcessPlan.getContainer();
			}else if(per instanceof WTChangeOrder2){
				WTChangeOrder2 ecn = (WTChangeOrder2)per;
				wtContainer = ecn.getContainer();
			}else if(per instanceof WTDocument){
				WTDocument document = (WTDocument)per;
				wtContainer = document.getContainer();
			}
			doc = WTDocumentUtil.createDocument(null, PrintServerConstants.OBJTYPE_PROCESSPRINTDOC_RECOVER_DISPLAY, wtContainer, PrintServerConstants.PROCESSPRINTDOC_LOCATION, PrintServerConstants.OBJTYPE_PROCESSPRINTDOC);
		} catch (WTException e) {
			logger.error(e);
		} catch (WTPropertyVetoException e) {
			logger.error(e);
		} catch (RemoteException e) {
			logger.error(e);
		}
		return doc;
	}

	public static String createGWPrintRecoverRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception {
		String result = "";
		try {
			if(cmPrintInfoBean != null){
				GWPrintRecoverRecord gwPrintRecoverRecord = new GWPrintRecoverRecord();

				GWPrintDistributeRecord gwPrintDistributeRecord = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCodeAndDept(cmPrintInfoBean.getQrCode(), cmPrintInfoBean.getRecoverDept());
				long receiptCount = gwPrintDistributeRecord.getGetQuantity();
				long recoverCount = Long.valueOf(cmPrintInfoBean.getRecoverCount());
				if (recoverCount > receiptCount) {
					result = cmPrintInfoBean.getFileNumber() + "退回份数大于已领取份数!\n";
					return result;
				}
				gwPrintRecoverRecord.setBarCode(cmPrintInfoBean.getQrCode());
				gwPrintRecoverRecord.setProcessOid(cmPrintInfoBean.getOid());
				gwPrintRecoverRecord.setProcessPrintFileOid(cmPrintInfoBean.getProcessPrintFileOid());
				gwPrintRecoverRecord.setRecoverUser(cmPrintInfoBean.getRecoverPersonID());
				gwPrintRecoverRecord.setRecoverDept(cmPrintInfoBean.getRecoverDept());
				gwPrintRecoverRecord.setRecoverDate(new Timestamp(new Date().getTime()));
				gwPrintRecoverRecord.setRecoverQuantity(recoverCount);
				gwPrintRecoverRecord.setRecoverRemark(cmPrintInfoBean.getRecoverRemark());

				GwPersistenceHelper.manager.save(gwPrintRecoverRecord);
			}
		} catch (Exception e) {
			result = "工艺文件" + cmPrintInfoBean.getFileNumber() + "退回申请失败！\n";
			e.printStackTrace();
		}
		return result;
	}

	public static List<GWPrintRecoverRecord> queryGWPrintRecoverRecordByPrintFileOid(long oid) throws Exception{
		List<GWPrintRecoverRecord> list = new ArrayList<GWPrintRecoverRecord>();
		GwQuerySpec qs = new GwQuerySpec(GWPrintRecoverRecord.class);
		qs.appendWhere(GWPrintRecoverRecord.PROCESSPRINTFILEOID, GwQuerySpec.EQUAL, oid);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		while(qr.hasNext()) {
			GWPrintRecoverRecord gwPrintRecoverRecord = (GWPrintRecoverRecord) qr.next();
			list.add(gwPrintRecoverRecord);
		}
		return list;
	}

	public static WTUser getReceiveUser(long oid) throws WTException{
		return (WTUser)PersistableUtil.getPersistable(PrintServerConstants.OID_WTUSER + oid);
	}

	public static String updateRecoverInfo(CmPrintInfoBean cmPrintInfoBean) throws Exception {
		String result = "";
		try {
			if(cmPrintInfoBean != null){
				String recoverDept = cmPrintInfoBean.getRecoverDept();
				String barCode = cmPrintInfoBean.getQrCode();
				GWPrintRecoverRecord gwPrintRecoverRecord = queryGWPrintRecoverRecordByBarCodeAndDept(barCode, recoverDept);
				long lastReceiveCount = gwPrintRecoverRecord.getReceiveQuantity();
				long receiveCount = Long.valueOf(cmPrintInfoBean.getReceiveCount());
				if (receiveCount > lastReceiveCount) {
					CmUser cmUser = cmPrintInfoBean.getCmUser();
					WTUser user = (WTUser) PersistableUtil.getPersistable(PrintServerConstants.OID_WTUSER + cmUser.getOid());
					gwPrintRecoverRecord.setReceiveQuantity(receiveCount);
					gwPrintRecoverRecord.setReceiveDate(new Timestamp(new Date().getTime()));

					WTGroup wtGroup = null;
			    	QueryResult qResult = MPMUtil.queryGroup();
					while (qResult.hasMoreElements()) {
						wtGroup = (WTGroup) qResult.nextElement();
						if(wtGroup.isMember(user) && wtGroup.getName().equals("资料员_信息档案处")) {
							gwPrintRecoverRecord.setReceiveDept(wtGroup.getName().split("_")[1]);
							gwPrintRecoverRecord.setReceiveUser(cmUser.getOid());
							GwPersistenceHelper.manager.save(gwPrintRecoverRecord);
							break;
						}
					}
					if(wtGroup == null){
						result = "更新工艺文件回收信息失败， 用户" + user.getFullName() + "不在资料员_信息档案处组内！\n";
					}
				}
			}
		} catch (Exception e) {
			result = "更新工艺文件" + cmPrintInfoBean.getFileNumber() + "回收信息失败！\n";
			throw e;
		}
		return result;
	}
}
