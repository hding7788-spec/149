package com.glaway.mpm.print.model;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.parameter.service.gwpersistable.GwPersistable;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.util.PrintUtil;

/**
 * 打印申请记录
 *
 * @author TUWENBIN
 *
 */
public class GWPrintApplyRecord implements GwPersistable {

	private static final long serialVersionUID = 1L;

	/**
	 * 表列名
	 */
	public static String BARCODE = "BARCODE";							//二维码
	public static String PROCESSOID = "PROCESSOID";						//工艺文件OID
	public static String PROCESSPRINTFILEOID = "PROCESSPRINTFILEOID";	//打印文件单OID
	public static String PROCESSNUMBER = "PROCESSNUMBER";				//工艺文件编号
	public static String PROCESSNAME = "PROCESSNAME";					//工艺文件名称
	public static String VERSION = "VERSION";							//版本
	public static String PHASECODE = "PHASECODE";						//阶段标记
	public static String FILETYPE = "FILETYPE";							//文件类型
	public static String SECRET = "SECRET";								//密级
	public static String PINDEX = "PINDEX";								//产品代号
	public static String TS_BASELINE = "TS_BASELINE";					//技术状态基线
	public static String PAGE = "PAGE";									//页数
	public static String ISBLUECARD = "ISBLUECARD";						//是否蓝卡
	public static String ECNNUMBER = "ECNNUMBER";						//更改单编号
	public static String PRINTREQUIRE = "PRINTREQUIRE";					//打印要求
	public static String REJECTREMARK = "REJECTREMARK";					//驳回备注
	public static String REJECTSTATUS = "REJECTSTATUS";					//驳回状态
	public static String PRINTSTATUS = "PRINTSTATUS";					//打印状态
	public static String PRINTOR = "PRINTOR";							//打印人
	public static String APPLIER = "APPLIER";							//申请人
	public static String APPLYDATE = "APPLYDATE";						//申请时间
	public static String PRINTDATE = "PRINTDATE";						//打印时间
	public static String TEMPORARYSEAL = "TEMPORARYSEAL";			   	//临时章

	/**
	 * 属性字段
	 */
	private String gwKey; // 主键
	private String barCode; // 主键
	private String processOid;
	private long processPrintFileOid;
	public String processNumber;
	public String processName;
	public String version;
	public String phaseCode;
	public String fileType;
	public String secret;
	public String pindex;
	public String ts_Baseline;
	public String page;
	public String isBlueCard;
	public String ecnNumber;
	public String printRequire;
	public String rejectRemark;
	public String rejectStatus;
	public String printStatus;
	public long printor;
	public long applier;
	public Date applyDate;
	public Date printDate;
	public String temporarySeal;

	public static int index = 1;

	public static String generateKeyId() {
		return PrintUtil.getPrintObjNumber(PrintServerConstants.NUMBER_OBJTYPE_PRINTAPPLY, PrintServerConstants.NUMBER_OBJTYPE_PRINTAPPLY_PREFIX);
	}

	@Override
	public GwPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			this.setGwKey(rs.getString(KEY_ID));
			this.setBarCode(rs.getString(BARCODE));
			this.setProcessOid(rs.getString(PROCESSOID));
			this.setProcessPrintFileOid(rs.getLong(PROCESSPRINTFILEOID));
			this.setProcessNumber(rs.getString(PROCESSNUMBER));
			this.setProcessName(rs.getString(PROCESSNAME));
			this.setVersion(rs.getString(VERSION));
			this.setPhaseCode(rs.getString(PHASECODE));
			this.setFileType(rs.getString(FILETYPE));
			this.setSecret(rs.getString(SECRET));
			this.setPindex(rs.getString(PINDEX));
			this.setTs_Baseline(rs.getString(TS_BASELINE));
			this.setPage(rs.getString(PAGE));
			this.setIsBlueCard(rs.getString(ISBLUECARD));
			this.setEcnNumber(rs.getString(ECNNUMBER));
			this.setPrintRequire(rs.getString(PRINTREQUIRE));
			this.setRejectRemark(rs.getString(REJECTREMARK));
			this.setRejectStatus(rs.getString(REJECTSTATUS));
			this.setPrintStatus(rs.getString(PRINTSTATUS));
			this.setPrintor(rs.getLong(PRINTOR));
			this.setApplier(rs.getLong(APPLIER));
			this.setApplyDate(rs.getDate(APPLYDATE));
			this.setPrintDate(rs.getDate(PRINTDATE));
			this.setTemporarySeal(rs.getString(TEMPORARYSEAL));
		}
		return this;
	}

	@Override
	public Object getKeyId() {
		if (this.gwKey != null && !this.gwKey.equals("")) {
			return this.gwKey;
		} else {
			String keyId = generateKeyId();
			this.setGwKey(keyId);
			return keyId;
		}
	}

	@Override
	public Map<String, Object> getUpdateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(BARCODE, barCode);
		ret.put(PROCESSOID, processOid);
		ret.put(PROCESSPRINTFILEOID, processPrintFileOid);
		ret.put(PROCESSNUMBER, processNumber);
		ret.put(PROCESSNAME, processName);
		ret.put(VERSION, version);
		ret.put(PHASECODE, phaseCode);
		ret.put(FILETYPE, fileType);
		ret.put(SECRET, secret);
		ret.put(PINDEX, pindex);
		ret.put(TS_BASELINE, ts_Baseline);
		ret.put(PAGE, page);
		ret.put(ISBLUECARD, isBlueCard);
		ret.put(ECNNUMBER, ecnNumber);
		ret.put(PRINTREQUIRE, printRequire);
		ret.put(REJECTREMARK, rejectRemark);
		ret.put(REJECTSTATUS, rejectStatus);
		ret.put(PRINTSTATUS, printStatus);
		ret.put(PRINTOR, printor);
		ret.put(APPLIER, applier);
		ret.put(APPLYDATE, applyDate);
		ret.put(PRINTDATE, printDate);
		ret.put(TEMPORARYSEAL, temporarySeal);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(BARCODE, barCode);
		ret.put(PROCESSOID, processOid);
		ret.put(PROCESSPRINTFILEOID, processPrintFileOid);
		ret.put(PROCESSNUMBER, processNumber);
		ret.put(PROCESSNAME, processName);
		ret.put(VERSION, version);
		ret.put(PHASECODE, phaseCode);
		ret.put(FILETYPE, fileType);
		ret.put(SECRET, secret);
		ret.put(PINDEX, pindex);
		ret.put(TS_BASELINE, ts_Baseline);
		ret.put(PAGE, page);
		ret.put(ISBLUECARD, isBlueCard);
		ret.put(ECNNUMBER, ecnNumber);
		ret.put(PRINTREQUIRE, printRequire);
		ret.put(REJECTREMARK, rejectRemark);
		ret.put(REJECTSTATUS, rejectStatus);
		ret.put(PRINTSTATUS, printStatus);
		ret.put(PRINTOR, printor);
		ret.put(APPLIER, applier);
		ret.put(APPLYDATE, applyDate);
		ret.put(PRINTDATE, printDate);
		ret.put(TEMPORARYSEAL, temporarySeal);
		return ret;
	}

	public String getGwKey() {
		return gwKey;
	}

	public void setGwKey(String gwKey) {
		this.gwKey = gwKey;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getProcessOid() {
		return processOid;
	}

	public void setProcessOid(String processOid) {
		this.processOid = processOid;
	}

	public long getProcessPrintFileOid() {
		return processPrintFileOid;
	}

	public void setProcessPrintFileOid(long processPrintFileOid) {
		this.processPrintFileOid = processPrintFileOid;
	}

	public String getProcessNumber() {
		return processNumber;
	}

	public void setProcessNumber(String processNumber) {
		this.processNumber = processNumber;
	}

	public String getPindex() {
		return pindex;
	}

	public void setPindex(String pindex) {
		this.pindex = pindex;
	}

	public String getTs_Baseline() {
		return ts_Baseline;
	}

	public void setTs_Baseline(String ts_Baseline) {
		this.ts_Baseline = ts_Baseline;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getIsBlueCard() {
		return isBlueCard;
	}

	public void setIsBlueCard(String isBlueCard) {
		this.isBlueCard = isBlueCard;
	}

	public String getEcnNumber() {
		return ecnNumber;
	}

	public void setEcnNumber(String ecnNumber) {
		this.ecnNumber = ecnNumber;
	}

	public String getPrintRequire() {
		return printRequire;
	}

	public void setPrintRequire(String printRequire) {
		this.printRequire = printRequire;
	}

	public String getRejectRemark() {
		return rejectRemark;
	}

	public void setRejectRemark(String rejectRemark) {
		this.rejectRemark = rejectRemark;
	}

	public String getRejectStatus() {
		return rejectStatus;
	}

	public void setRejectStatus(String rejectStatus) {
		this.rejectStatus = rejectStatus;
	}

	public String getPrintStatus() {
		return printStatus;
	}

	public void setPrintStatus(String printStatus) {
		this.printStatus = printStatus;
	}

	public long getPrintor() {
		return printor;
	}

	public void setPrintor(long printor) {
		this.printor = printor;
	}

	public Date getPrintDate() {
		return printDate;
	}

	public void setPrintDate(Date printDate) {
		this.printDate = printDate;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public long getApplier() {
		return applier;
	}

	public void setApplier(long applier) {
		this.applier = applier;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPhaseCode() {
		return phaseCode;
	}

	public void setPhaseCode(String phaseCode) {
		this.phaseCode = phaseCode;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getTemporarySeal() {
		return temporarySeal;
	}

	public void setTemporarySeal(String temporarySeal) {
		this.temporarySeal = temporarySeal;
	}

	@Override
	public CmPrintInfoBean clone() {
		CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
//		cmPrintInfoBean.setQrCode(this.barCode);
//		cmPrintInfoBean.setOid(this.processOid);
//		cmPrintInfoBean.setFileNumber(this.processNumber);
//		cmPrintInfoBean.setPindex(this.pindex);
//		cmPrintInfoBean.setBaseline(this.ts_Baseline);
//		cmPrintInfoBean.setPageCount(this.page);
//		cmPrintInfoBean.setBlueCard(this.isBlueCard);
//		cmPrintInfoBean.setEcnNumber(this.ecnNumber);
//		cmPrintInfoBean.setPrintDescription(this.printRequire);
//		cmPrintInfoBean.setRejectRemark(this.rejectRemark);
//		cmPrintInfoBean.setRejectState(this.rejectStatus);
//		cmPrintInfoBean.setPrintState(this.printStatus);
//		cmPrintInfoBean.setPrinter(this.printor);
//		cmPrintInfoBean.setPrintDate(this.printDate);
		return cmPrintInfoBean;
	}
}
