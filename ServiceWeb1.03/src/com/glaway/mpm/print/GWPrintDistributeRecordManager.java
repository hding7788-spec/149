package com.glaway.mpm.print;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import wt.org.WTGroup;
import wt.org.WTUser;
import wt.util.WTException;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.data.CmUser;
import com.glaway.mpm.print.model.GWPrintApplyRecord;
import com.glaway.mpm.print.model.GWPrintDistributeRecord;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;

import com.glaway.mpm.print.util.PrintDataBuildUtil;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.print.util.LoadPropertiesConfig;
import com.glaway.mpm.parameter.util.PersistableUtil;
import com.glaway.mpm.util.UserUtil;

public class GWPrintDistributeRecordManager {

	private static VaLogger logger = VaLogger.getLogger(GWPrintDistributeRecordManager.class.getName());

	public static String createGwPrintDistributeRecord(CmPrintInfoBean cmPrintInfoBean) {
		String result = "";
		try {
			if(cmPrintInfoBean != null){
				String deptAndCountStr = cmPrintInfoBean.getDistributeDeptAndCount();
				String[] deptAndCounts = deptAndCountStr.split(",");
				for(String deptAndCount : deptAndCounts){
					String dept = deptAndCount.split("-")[0].trim();
					String count = deptAndCount.split("-")[1];
					count = count.substring(0, count.length()-1);
					GWPrintDistributeRecord gwPrintDistributeRecord = new GWPrintDistributeRecord();
					gwPrintDistributeRecord.setBarCode(cmPrintInfoBean.getQrCode());
					gwPrintDistributeRecord.setProcessOid(cmPrintInfoBean.getOid());
					gwPrintDistributeRecord.setDistributeDept(dept);
					gwPrintDistributeRecord.setDistributeQuantity(Long.parseLong(count));
					GwPersistenceHelper.manager.save(gwPrintDistributeRecord);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result = "工艺文件" + cmPrintInfoBean.getFileNumber() + "打印申请失败！\n";
			e.printStackTrace();
		}
		return result;
	}

	public static String updateGwPrintDistributeRecord(CmPrintInfoBean cmPrintInfoBean) {
		String result = "";
		try {
			if(cmPrintInfoBean != null){
				String deptAndCountStr = cmPrintInfoBean.getDistributeDeptAndCount();
				String[] deptAndCounts = deptAndCountStr.split(",");
				for(String deptAndCount : deptAndCounts){
					String dept = deptAndCount.split("-")[0].trim();
					String count = deptAndCount.split("-")[1];
					count = count.substring(0, count.length()-1);
					GWPrintDistributeRecord gwPrintDistributeRecord = queryGWPrintDistributeRecordByBarCodeAndProcessOid(cmPrintInfoBean.getQrCode(), cmPrintInfoBean.getOid());
					gwPrintDistributeRecord.setDistributeDept(dept);
					gwPrintDistributeRecord.setDistributeQuantity(Long.parseLong(count));
					GwPersistenceHelper.manager.save(gwPrintDistributeRecord);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result = "更新工艺文件" + cmPrintInfoBean.getFileNumber() + "打印申请失败！\n";
			e.printStackTrace();
		}
		return result;
	}

	public static List<GWPrintDistributeRecord> queryGWPrintDistributeRecordByBarCode(String barCode) throws Exception{
		List<GWPrintDistributeRecord> list = new ArrayList<GWPrintDistributeRecord>();
		GwQuerySpec qs = new GwQuerySpec(GWPrintDistributeRecord.class);
		qs.appendWhere(GWPrintDistributeRecord.BARCODE, GwQuerySpec.EQUAL, barCode);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		while(qr.hasNext()) {
			GWPrintDistributeRecord gwPrintDistributeRecord = (GWPrintDistributeRecord) qr.next();
			list.add(gwPrintDistributeRecord);
		}
		return list;
	}

	public static GWPrintDistributeRecord queryGWPrintDistributeRecordByBarCodeAndProcessOid(String barCode, String processOid) throws Exception{
		GWPrintDistributeRecord gwPrintDistributeRecord = null;
		GwQuerySpec qs = new GwQuerySpec(GWPrintDistributeRecord.class);
		qs.appendWhere(GWPrintDistributeRecord.BARCODE, GwQuerySpec.EQUAL, barCode);
		qs.appendAnd();
		qs.appendWhere(GWPrintDistributeRecord.PROCESSOID, GwQuerySpec.EQUAL, processOid);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		while(qr.hasNext()) {
			gwPrintDistributeRecord = (GWPrintDistributeRecord) qr.next();
		}
		return gwPrintDistributeRecord;
	}

	public static String getDistributeDeptAndQuanity(List<GWPrintDistributeRecord> list) throws WTException{
		if(list.size() == 0)
			return "";

		StringBuffer buf = new StringBuffer();
		for(GWPrintDistributeRecord gwPrintDistributeRecord : list){
			String distributeDept = gwPrintDistributeRecord.getDistributeDept();
			long distributeQuantity = gwPrintDistributeRecord.getDistributeQuantity();
			buf.append(distributeDept).append("-").append(distributeQuantity).append("份").append(",");
		}

		return buf.toString().substring(0, buf.length()-1);
	}

	public static boolean getDistributeDept(List<GWPrintDistributeRecord> list, CmUser user) throws WTException{
		for(GWPrintDistributeRecord gwPrintDistributeRecord : list){
			String distributeDept = gwPrintDistributeRecord.getDistributeDept();
			WTGroup group = UserUtil.queryGroup("资料员_" + distributeDept);
			if(group == null){
				group = UserUtil.queryGroup(PrintServerConstants.GROUP_XXDAC);
			}
			WTUser wtUser = (WTUser)PersistableUtil.getPersistable(PrintServerConstants.OID_WTUSER + user.getOid());
			if(group.isMember(wtUser)){
				return true;
			}
		}
		return false;
	}

	public static GWPrintDistributeRecord queryGWPrintDistributeRecordByBarCodeAndDept(String barCode, String receiptDept) throws Exception{
		GwQuerySpec qs = new GwQuerySpec(GWPrintDistributeRecord.class);
		qs.appendWhere(GWPrintDistributeRecord.BARCODE, GwQuerySpec.EQUAL, barCode);
		qs.appendAnd();
		if (receiptDept.equals(PrintServerConstants.GROUP_XXDAC)) {
			Vector<String> vec = LoadPropertiesConfig.getInstance(4).getOutsideDept();
			vec.add(receiptDept);
			for (int i = 0; i < vec.size(); i++) {
				qs.appendWhere(GWPrintDistributeRecord.DISTRIBUTEDEPT, GwQuerySpec.EQUAL, vec.get(i));
				if (i < vec.size() - 1) {
					qs.appendOr();
				}
			}
		} else {
			qs.appendWhere(GWPrintDistributeRecord.DISTRIBUTEDEPT, GwQuerySpec.EQUAL, receiptDept);
		}
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWPrintDistributeRecord gwPrintDistributeRecord = null;
		if(qr.hasNext()) {
			gwPrintDistributeRecord = (GWPrintDistributeRecord) qr.next();
		}
		return gwPrintDistributeRecord;
	}

	public static String updateReceiptInfo(CmPrintInfoBean cmPrintInfoBean) throws Exception {
		String result = "";
		try {
			if(cmPrintInfoBean != null){
				String receiptDept = cmPrintInfoBean.getReceiptDept();
				String barCode = cmPrintInfoBean.getQrCode();
				GWPrintDistributeRecord gwPrintDistributeRecord = queryGWPrintDistributeRecordByBarCodeAndDept(barCode, receiptDept);

				long distributeCount = gwPrintDistributeRecord.getDistributeQuantity();
				long getCount = Long.valueOf(cmPrintInfoBean.getReceiptCount());
				if (getCount > distributeCount) {
					result = cmPrintInfoBean.getFileNumber() + "领取份数大于分发份数！\n";
					return result;
				}
				gwPrintDistributeRecord.setGetDept(receiptDept);
				gwPrintDistributeRecord.setGetQuantity(getCount);
				gwPrintDistributeRecord.setGetUser(cmPrintInfoBean.getReceiptPersonID());
				gwPrintDistributeRecord.setGetDate(new Timestamp(new Date().getTime()));
				gwPrintDistributeRecord.setDistributeUser(cmPrintInfoBean.getCmUser().getOid());
				gwPrintDistributeRecord.setDistributeDate(new Timestamp(new Date().getTime()));
				GwPersistenceHelper.manager.save(gwPrintDistributeRecord);
			}
		} catch (Exception e) {
			result = "更新工艺文件" + cmPrintInfoBean.getFileNumber() + "领取信息失败！\n";
			throw e;
		}
		return result;
	}

	public static List<CmPrintInfoBean> queryYlqGWPrintDistributeRecord(CmPrintQueryBean cmPrintQueryBean) throws Exception{
 		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		String fileNumber = cmPrintQueryBean.getFileNumber().toUpperCase();
		String fileName = cmPrintQueryBean.getFileName().toUpperCase();
		String qrCode = cmPrintQueryBean.getQrCode();
		CmUser cmUser = cmPrintQueryBean.getUser();
		WTUser user = (WTUser) PersistableUtil.getPersistable(PrintServerConstants.OID_WTUSER + cmUser.getOid());
		WTGroup group = UserUtil.getZlyGroupsByUser(user);
		if(group == null){
			return list;
		}

		GwQuerySpec qs = new GwQuerySpec(GWPrintDistributeRecord.class);
		qs.appendWhere(GWPrintDistributeRecord.GETQUANTITY, GwQuerySpec.IS_NOT_NULL, "");
		qs.appendAnd();
		qs.appendWhere(GWPrintDistributeRecord.GETDEPT, GwQuerySpec.EQUAL, group.getName().split("_")[1]);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		while(qr.hasNext()) {
			GWPrintDistributeRecord gwPrintDistributeRecord = (GWPrintDistributeRecord) qr.next();
			GWPrintApplyRecord gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByQRCode(gwPrintDistributeRecord.getBarCode());
			if(fileNumber.equals("") && fileName.equals("") && qrCode.equals("")){
				CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBeanByYlqwj(gwPrintDistributeRecord, gwPrintApplyRecord);
				list.add(cmPrintInfoBean);
			}else{
				if(!fileNumber.equals("") && fileName.equals("") && qrCode.equals("")){
					if(gwPrintApplyRecord.getProcessNumber().toUpperCase().contains(fileNumber)){
						CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBeanByYlqwj(gwPrintDistributeRecord, gwPrintApplyRecord);
						list.add(cmPrintInfoBean);
						continue;
					}
				}

				if(fileNumber.equals("") && !fileName.equals("") && qrCode.equals("")){
					if(gwPrintApplyRecord.getProcessName().toUpperCase().contains(fileName)){
						CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBeanByYlqwj(gwPrintDistributeRecord, gwPrintApplyRecord);
						list.add(cmPrintInfoBean);
						continue;
					}
				}

				if(fileNumber.equals("") && fileName.equals("") && !qrCode.equals("")){
					if(gwPrintApplyRecord.getBarCode().toUpperCase().contains(qrCode)){
						CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBeanByYlqwj(gwPrintDistributeRecord, gwPrintApplyRecord);
						list.add(cmPrintInfoBean);
						continue;
					}
				}

				if(!fileNumber.equals("") && !fileName.equals("") && qrCode.equals("")){
					if(gwPrintApplyRecord.getProcessNumber().toUpperCase().contains(fileNumber) && gwPrintApplyRecord.getProcessName().toUpperCase().contains(fileName)){
						CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBeanByYlqwj(gwPrintDistributeRecord, gwPrintApplyRecord);
						list.add(cmPrintInfoBean);
						continue;
					}
				}

				if(!fileNumber.equals("") && fileName.equals("") && !qrCode.equals("")){
					if(gwPrintApplyRecord.getProcessNumber().toUpperCase().contains(fileNumber) && gwPrintApplyRecord.getBarCode().toUpperCase().contains(qrCode)){
						CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBeanByYlqwj(gwPrintDistributeRecord, gwPrintApplyRecord);
						list.add(cmPrintInfoBean);
						continue;
					}
				}

				if(fileNumber.equals("") && !fileName.equals("") && !qrCode.equals("")){
					if(gwPrintApplyRecord.getProcessName().toUpperCase().contains(fileName) && gwPrintApplyRecord.getBarCode().toUpperCase().contains(qrCode)){
						CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBeanByYlqwj(gwPrintDistributeRecord, gwPrintApplyRecord);
						list.add(cmPrintInfoBean);
						continue;
					}
				}

				if(!fileNumber.equals("") && !fileName.equals("") && !qrCode.equals("")){
					if(gwPrintApplyRecord.getProcessNumber().toUpperCase().contains(fileNumber) && gwPrintApplyRecord.getProcessName().toUpperCase().contains(fileName) && gwPrintApplyRecord.getBarCode().toUpperCase().contains(qrCode)){
						CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBeanByYlqwj(gwPrintDistributeRecord, gwPrintApplyRecord);
						list.add(cmPrintInfoBean);
						continue;
					}
				}
			}
		}
		return list;
	}

}