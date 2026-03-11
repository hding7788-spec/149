package com.glaway.mpm.print.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.glaway.mpm.model.data.CmUser;
import com.glaway.mpm.util.CommonUtil;

/**
 * 打印对象模型
 *
 */
public class CmPrintInfoBean implements Serializable, Comparable<CmPrintInfoBean>{

	private static final long serialVersionUID = -3252770357174849936L;
	/** 工艺文件oid */
	private String oid;
	/** 文件编号 */
	private String fileNumber;
	/** 工艺文件编号*/
	private String technicsNumber;
	/** 工艺文件名称 */
	private String fileName;
	/** 工艺文件编制人 */
	private String fileCreator;
	/** 版本 */
	private String version;
	/** 文件类型 */
	private String fileType;
	/** 阶段标记 */
	private String phaseCode;
	/** 产品代号 */
	private String pindex;
	/** 文件状态 */
	private String fileState;
	/** 修改者 */
	private String modifior;
	/** 密级 */
	private String secret;
	/** 外来单位 */
	private String outDept;
	/** 页数 */
	private String pageCount;
	/** 技术状态基线 */
	private String baseline;
	/** 更改单号 */
	private String ecnNumber;
	/** 分发部门及份数 */
	private String distributeDeptAndCount;
	/** 编制部门*/
	private String compileDept;
	/** 打印要求 */
	private String printDescription;
	/** 批准时间 */
	private String approveDate;
	/** 申请人 */
	private String applier;
	/** 申请时间 */
	private String applyDate;
	/** 打印状态 */
	private String printState;
	/** 打印人 */
	private String printer;
	/** 打印时间 */
	private String printDate;
	/** 二维码 */
	private CmQrCode cmQrCode;
	/** 二维码 */
	private String qrCode;
	/** 驳回备注 */
	private String rejectRemark;
	/** 驳回状态 */
	private String rejectState;
	/** 已领取份数*/
	private String receiptCount;
	/** 是否蓝卡 */
	private boolean isBlueCard;
	/** 领取人*/
	private String receipter;
	/** 领取日期*/
	private String receiptDate;
	/** 领取部门*/
	private String receiptDept;
	/** 文件打印单OID*/
	private long processPrintFileOid;
	/** 登录用户 */
	private CmUser cmUser;
	/** 领取人OID */
	private long receiptPersonID;
	/** 退回人 */
	private String recoverPerson;
	/** 退回备注 */
	private String recoverRemark;
	/** 退回份数 */
	private String recoverCount;
	/** 退回人OID */
	private long recoverPersonID;
	/** 退回部门 */
	private String recoverDept;
	/** 退回日期 */
	private String recoverDate;
	/** 收件人OID */
	private long receivePersonID;
	/** 收件人 */
	private String receivePerson;
	/** 收件部门 */
	private String receiveDept;
	/** 收件日期 */
	private String receiveDate;
	/** 回收份数 */
	private String receiveCount;
	/** 临时章 */
	private String temporarySeal;
	/** 技术状态标识 */
	private String ts_status;
	/** 修改时间 */
	private String modifyTime;
	/** 生命周期状态 */
	private String lifeCycle;
	/** 上下文名称 */
	private String containerName;
	/** 是否发起过打印申请*/
	private boolean isPrintRequest;//add by 20170517_jyx
	/** pbooid*/
	private String pbooid;
	/** DocVR*/
	private String docVR;
	/** 二维码名称 */
	private String QrName;
	/** 二维码路径 */
	private String QrPath;
	/** 加盖印章*/
	private String sealPlus;
	/** 更改单id*/
	private String changeNoticeID;
	/** 更改单号*/
	private String changeNoticeNumber;
	/** 更改日期*/
	private String changeDate;
	/** 更改内容*/
	private String changeContent;
	/** 成套图更改*/
	private String viewChange;
	/** 更改单单位*/
	private String changeNoticeDept;
	/** 是否工艺文件目录*/
	private boolean processfile;
	/** 是否是BOM添加*/
	private boolean addFormBOM;
	/** 来源*/
	private String printpath;
	/** 补打信息*/
	private String offSet;
	/** 主要零件工艺文件目录*/
	private String mainTechnics;
	/** 工艺文件编号*/
	private String processNumber;


	private List<CmPrintInfoBean> ecnList;

	public String getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getPhaseCode() {
		return phaseCode;
	}

	public void setPhaseCode(String phaseCode) {
		this.phaseCode = phaseCode;
	}

	public String getPindex() {
		return pindex;
	}

	public void setPindex(String pindex) {
		this.pindex = pindex;
	}

	public String getFileState() {
		return fileState;
	}

	public void setFileState(String fileState) {
		this.fileState = fileState;
	}

	public String getModifior() {
		return modifior;
	}

	public void setModifior(String modifior) {
		this.modifior = modifior;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getBaseline() {
		return baseline;
	}

	public void setBaseline(String baseline) {
		this.baseline = baseline;
	}

	public String getEcnNumber() {
		return ecnNumber;
	}

	public void setEcnNumber(String ecnNumber) {
		this.ecnNumber = ecnNumber;
	}

	public String getDistributeDeptAndCount() {
		return distributeDeptAndCount;
	}

	public void setDistributeDeptAndCount(String distributeDeptAndCount) {
		this.distributeDeptAndCount = distributeDeptAndCount;
	}

	public String getPrintDescription() {
		return printDescription;
	}

	public void setPrintDescription(String printDescription) {
		this.printDescription = printDescription;
	}

	public String getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(String approveDate) {
		this.approveDate = approveDate;
	}

	public String getPrintState() {
		return printState;
	}

	public void setPrintState(String printState) {
		this.printState = printState;
	}

	public String getPrinter() {
		return printer;
	}

	public void setPrinter(String printer) {
		this.printer = printer;
	}

	public String getPrintDate() {
		return printDate;
	}

	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}

	public String getRejectRemark() {
		return rejectRemark;
	}

	public void setRejectRemark(String rejectRemark) {
		this.rejectRemark = rejectRemark;
	}

	public String getRejectState() {
		return rejectState;
	}

	public void setRejectState(String rejectState) {
		this.rejectState = rejectState;
	}

	public String getReceiptCount() {
		return receiptCount;
	}

	public void setReceiptCount(String receiptCount) {
		this.receiptCount = receiptCount;
	}

	public String getPageCount() {
		return pageCount;
	}

	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

	public boolean isBlueCard() {
		return isBlueCard;
	}

	public void setBlueCard(boolean isBlueCard) {
		this.isBlueCard = isBlueCard;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getReceipter() {
		return receipter;
	}

	public void setReceipter(String receipter) {
		this.receipter = receipter;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptDept() {
		return receiptDept;
	}

	public void setReceiptDept(String receiptDept) {
		this.receiptDept = receiptDept;
	}

	public CmQrCode getCmQrCode() {
		return cmQrCode;
	}

	public void setCmQrCode(CmQrCode cmQrCode) {
		this.cmQrCode = cmQrCode;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getFileCreator() {
		return fileCreator;
	}

	public void setFileCreator(String fileCreator) {
		this.fileCreator = fileCreator;
	}

	public long getProcessPrintFileOid() {
		return processPrintFileOid;
	}

	public void setProcessPrintFileOid(long processPrintFileOid) {
		this.processPrintFileOid = processPrintFileOid;
	}

	public CmUser getCmUser() {
		return cmUser;
	}

	public void setCmUser(CmUser cmUser) {
		this.cmUser = cmUser;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public long getReceiptPersonID() {
		return receiptPersonID;
	}

	public void setReceiptPersonID(long receiptPersonID) {
		this.receiptPersonID = receiptPersonID;
	}

	public String getRecoverRemark() {
		return recoverRemark;
	}

	public void setRecoverRemark(String recoverRemark) {
		this.recoverRemark = recoverRemark;
	}

	public String getRecoverCount() {
		return recoverCount;
	}

	public void setRecoverCount(String recoverCount) {
		this.recoverCount = recoverCount;
	}

	public long getRecoverPersonID() {
		return recoverPersonID;
	}

	public void setRecoverPersonID(long recoverPersonID) {
		this.recoverPersonID = recoverPersonID;
	}

	public String getRecoverDept() {
		return recoverDept;
	}

	public void setRecoverDept(String recoverDept) {
		this.recoverDept = recoverDept;
	}

	public String getRecoverDate() {
		return recoverDate;
	}

	public void setRecoverDate(String recoverDate) {
		this.recoverDate = recoverDate;
	}

	public String getRecoverPerson() {
		return recoverPerson;
	}

	public void setRecoverPerson(String recoverPerson) {
		this.recoverPerson = recoverPerson;
	}

	public String getApplier() {
		return applier;
	}

	public void setApplier(String applier) {
		this.applier = applier;
	}

	public long getReceivePersonID() {
		return receivePersonID;
	}

	public void setReceivePersonID(long receivePersonID) {
		this.receivePersonID = receivePersonID;
	}

	public String getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}

	public String getReceiveCount() {
		return receiveCount;
	}

	public void setReceiveCount(String receiveCount) {
		this.receiveCount = receiveCount;
	}

	public String getReceivePerson() {
		return receivePerson;
	}

	public void setReceivePerson(String receivePerson) {
		this.receivePerson = receivePerson;
	}

	public String getReceiveDept() {
		return receiveDept;
	}

	public void setReceiveDept(String receiveDept) {
		this.receiveDept = receiveDept;
	}

	public String getTemporarySeal() {
		return temporarySeal;
	}

	public void setTemporarySeal(String temporarySeal) {
		this.temporarySeal = temporarySeal;
	}

	public List<CmPrintInfoBean> getEcnList() {
		if (ecnList == null) {
			ecnList = new ArrayList<CmPrintInfoBean>();
		}
		return ecnList;
	}

	public void setEcnList(List<CmPrintInfoBean> ecnList) {
		this.ecnList = ecnList;
	}

	public String getTs_status() {
		return ts_status;
	}

	public void setTs_status(String ts_status) {
		this.ts_status = ts_status;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(String lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public boolean isPrintRequest() {
		return isPrintRequest;
	}

	public void setPrintRequest(boolean isPrintRequest) {
		this.isPrintRequest = isPrintRequest;
	}

	public String getPbooid() {
		return pbooid;
	}

	public void setPbooid(String pbooid) {
		this.pbooid = pbooid;
	}

	public String getDocVR() {
		return docVR;
	}

	public void setDocVR(String docVR) {
		this.docVR = docVR;
	}

	public String getQrName() {
		return QrName;
	}

	public void setQrName(String qrName) {
		QrName = qrName;
	}

	public String getQrPath() {
		return QrPath;
	}

	public void setQrPath(String qrPath) {
		QrPath = qrPath;
	}

	public String getSealPlus() {
		return sealPlus;
	}

	public void setSealPlus(String sealPlus) {
		this.sealPlus = sealPlus;
	}

	public String getCompileDept() {
		return compileDept;
	}

	public void setCompileDept(String compileDept) {
		this.compileDept = compileDept;
	}

	public String getOutDept() {
		return outDept;
	}

	public void setOutDept(String outDept) {
		this.outDept = outDept;
	}

	public String getChangeNoticeID() {
		return changeNoticeID;
	}

	public void setChangeNoticeID(String changeNoticeID) {
		this.changeNoticeID = changeNoticeID;
	}

	public String getChangeNoticeNumber() {
		return changeNoticeNumber;
	}

	public void setChangeNoticeNumber(String changeNoticeNumber) {
		this.changeNoticeNumber = changeNoticeNumber;
	}

	public String getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}

	public String getChangeContent() {
		return changeContent;
	}

	public void setChangeContent(String changeContent) {
		this.changeContent = changeContent;
	}

	public String getViewChange() {
		return viewChange;
	}

	public void setViewChange(String viewChange) {
		this.viewChange = viewChange;
	}

	public String getChangeNoticeDept() {
		return changeNoticeDept;
	}

	public void setChangeNoticeDept(String changeNoticeDept) {
		this.changeNoticeDept = changeNoticeDept;
	}

	public boolean isProcessfile() {
		return processfile;
	}

	public void setProcessfile(boolean processfile) {
		this.processfile = processfile;
	}

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public void setTechnicsNumber(String technicsNumber) {
		this.technicsNumber = technicsNumber;
	}

	public String getPrintpath() {
		return printpath;
	}

	public void setPrintpath(String printpath) {
		this.printpath = printpath;
	}

	public String getOffSet() {
		return offSet;
	}

	public void setOffSet(String offSet) {
		this.offSet = offSet;
	}

	public String getMainTechnics() {
		return mainTechnics;
	}

	public void setMainTechnics(String mainTechnics) {
		this.mainTechnics = mainTechnics;
	}

	public boolean isAddFormBOM() {
		return addFormBOM;
	}

	public void setAddFormBOM(boolean addFormBOM) {
		this.addFormBOM = addFormBOM;
	}

	public String getProcessNumber() {
		return processNumber;
	}

	public void setProcessNumber(String processNumber) {
		this.processNumber = processNumber;
	}
	@Override
	public String toString() {
		return "CmPrintInfoBean [oid=" + oid + ", fileNumber=" + fileNumber + ", technicsNumber=" + technicsNumber + ", fileName=" + fileName + ", fileCreator=" + fileCreator + ", version=" + version
				+ ", fileType=" + fileType + ", phaseCode=" + phaseCode + ", pindex=" + pindex + ", fileState=" + fileState + ", modifior=" + modifior + ", secret=" + secret + ", outDept=" + outDept
				+ ", pageCount=" + pageCount + ", baseline=" + baseline + ", ecnNumber=" + ecnNumber + ", distributeDeptAndCount=" + distributeDeptAndCount + ", compileDept=" + compileDept
				+ ", printDescription=" + printDescription + ", approveDate=" + approveDate + ", applier=" + applier + ", applyDate=" + applyDate + ", printState=" + printState + ", printer="
				+ printer + ", printDate=" + printDate + ", cmQrCode=" + cmQrCode + ", qrCode=" + qrCode + ", rejectRemark=" + rejectRemark + ", rejectState=" + rejectState + ", receiptCount="
				+ receiptCount + ", isBlueCard=" + isBlueCard + ", receipter=" + receipter + ", receiptDate=" + receiptDate + ", receiptDept=" + receiptDept + ", processPrintFileOid="
				+ processPrintFileOid + ", cmUser=" + cmUser + ", receiptPersonID=" + receiptPersonID + ", recoverPerson=" + recoverPerson + ", recoverRemark=" + recoverRemark + ", recoverCount="
				+ recoverCount + ", recoverPersonID=" + recoverPersonID + ", recoverDept=" + recoverDept + ", recoverDate=" + recoverDate + ", receivePersonID=" + receivePersonID + ", receivePerson="
				+ receivePerson + ", receiveDept=" + receiveDept + ", receiveDate=" + receiveDate + ", receiveCount=" + receiveCount + ", temporarySeal=" + temporarySeal + ", ts_status=" + ts_status
				+ ", modifyTime=" + modifyTime + ", lifeCycle=" + lifeCycle + ", containerName=" + containerName + ", isPrintRequest=" + isPrintRequest + ", pbooid=" + pbooid + ", docVR=" + docVR
				+ ", QrName=" + QrName + ", QrPath=" + QrPath + ", sealPlus=" + sealPlus + ", changeNoticeID=" + changeNoticeID + ", changeNoticeNumber=" + changeNoticeNumber + ", changeDate="
				+ changeDate + ", changeContent=" + changeContent + ", viewChange=" + viewChange + ", changeNoticeDept=" + changeNoticeDept + ", processfile=" + processfile + ", addFormBOM="
				+ addFormBOM + ", printpath=" + printpath + ", offSet=" + offSet + ", mainTechnics=" + mainTechnics + ", processNumber=" + processNumber + ", ecnList=" + ecnList + "]";
	}

	@Override
	public int compareTo(CmPrintInfoBean o) {
		return CommonUtil.objectToString(this.processNumber).compareTo(CommonUtil.objectToString(o.getProcessNumber()));
	}

}
