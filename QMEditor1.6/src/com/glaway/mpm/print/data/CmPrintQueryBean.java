package com.glaway.mpm.print.data;

import java.io.Serializable;

import com.glaway.mpm.model.data.CmUser;

/**
 * 打印查询对象模型
 *
 */
public class CmPrintQueryBean implements Serializable {

	private static final long serialVersionUID = -8828171802780556858L;
	/** 文件编号 */
	private String fileNumber;
	/** 文件名称 */
	private String fileName;
	/** 版本 */
	private String version;
	/** 工艺类型 */
	private String technicsType;
	/** 文件类型 */
	private String fileType;
	/** 阶段标记 */
	private String phaseCode;
	/** 密级 */
	private String secret;
	/** 产品代号 */
	private String pindex;
	/** 零部件编号 */
	private String partNumber;
	/** 零部件名称 */
	private String partName;
	/** 零件图号 */
	private String cindex;
	/** 文件状态 */
	private String fileState;
	/** 生命周期状态 */
	private String lifeCycleState;
	/** 修改者 */
	private String modifior;
	/** 二维码 */
	private String qrCode;
	/** 当前登录用户 */
	private CmUser user;
	/** 申请开始日期 */
	private String applyBeginDate;
	/** 申请结束日期 */
	private String applyOverDate;
	/** 打印开始日期 */
	private String printBeginDate;
	/** 打印结束日期 */
	private String printOverDate;
	/** 是否按申请日期查询 */
	private String applyDateCheck;
	/** 是否按打印日期查询 */
	private String printDateCheck;
	/** 型号 */
	private String mindex;
	/** 技术状态标识 */
	private String ts_status;
	/** 外来单位*/
	private String outDept;
	/** 页数*/
	private String pageCount;
	/** 更改日期*/
	private String changeData;
	/** 更改内容*/
	private String changeContent;
	/** 成套图更改*/
	private String viewChange;
	/** 改变单ID */
	private String changeNoticeID;
	/** 所属产品库 */
	private String container;

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

	public String getTechnicsType() {
		return technicsType;
	}

	public void setTechnicsType(String technicsType) {
		this.technicsType = technicsType;
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

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public String getCindex() {
		return cindex;
	}

	public void setCindex(String cindex) {
		this.cindex = cindex;
	}

	public String getFileState() {
		return fileState;
	}

	public void setFileState(String fileState) {
		this.fileState = fileState;
	}

	public String getLifeCycleState() {
		return lifeCycleState;
	}

	public void setLifeCycleState(String lifeCycleState) {
		this.lifeCycleState = lifeCycleState;
	}

	public String getModifior() {
		return modifior;
	}

	public void setModifior(String modifior) {
		this.modifior = modifior;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public CmUser getUser() {
		return user;
	}

	public void setUser(CmUser user) {
		this.user = user;
	}

	public String getApplyBeginDate() {
		return applyBeginDate;
	}

	public void setApplyBeginDate(String applyBeginDate) {
		this.applyBeginDate = applyBeginDate;
	}

	public String getApplyOverDate() {
		return applyOverDate;
	}

	public void setApplyOverDate(String applyOverDate) {
		this.applyOverDate = applyOverDate;
	}

	public String getPrintBeginDate() {
		return printBeginDate;
	}

	public void setPrintBeginDate(String printBeginDate) {
		this.printBeginDate = printBeginDate;
	}

	public String getPrintOverDate() {
		return printOverDate;
	}

	public void setPrintOverDate(String printOverDate) {
		this.printOverDate = printOverDate;
	}

	public String getApplyDateCheck() {
		return applyDateCheck;
	}

	public void setApplyDateCheck(String applyDateCheck) {
		this.applyDateCheck = applyDateCheck;
	}

	public String getPrintDateCheck() {
		return printDateCheck;
	}

	public void setPrintDateCheck(String printDateCheck) {
		this.printDateCheck = printDateCheck;
	}

	public String getMindex() {
		return mindex;
	}

	public void setMindex(String mindex) {
		this.mindex = mindex;
	}

	public String getTs_status() {
		return ts_status;
	}

	public void setTs_status(String ts_status) {
		this.ts_status = ts_status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getOutDept() {
		return outDept;
	}

	public void setOutDept(String outDept) {
		this.outDept = outDept;
	}

	public String getPageCount() {
		return pageCount;
	}

	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

	public String getChangeData() {
		return changeData;
	}

	public void setChangeData(String changeData) {
		this.changeData = changeData;
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

	public String getChangeNoticeID() {
		return changeNoticeID;
	}

	public void setChangeNoticeID(String changeNoticeID) {
		this.changeNoticeID = changeNoticeID;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	@Override
	public String toString() {
		return "CmPrintQueryBean [fileNumber=" + fileNumber + ", fileName=" + fileName + ", version=" + version + ", technicsType=" + technicsType + ", fileType=" + fileType + ", phaseCode="
				+ phaseCode + ", secret=" + secret + ", pindex=" + pindex + ", partNumber=" + partNumber + ", partName=" + partName + ", cindex=" + cindex + ", fileState=" + fileState
				+ ", lifeCycleState=" + lifeCycleState + ", modifior=" + modifior + ", qrCode=" + qrCode + ", user=" + user + ", applyBeginDate=" + applyBeginDate + ", applyOverDate=" + applyOverDate
				+ ", printBeginDate=" + printBeginDate + ", printOverDate=" + printOverDate + ", applyDateCheck=" + applyDateCheck + ", printDateCheck=" + printDateCheck + ", mindex=" + mindex
				+ ", ts_status=" + ts_status + ", outDept=" + outDept + ", pageCount=" + pageCount + ", changeData=" + changeData + ", changeContent=" + changeContent + ", viewChange=" + viewChange
				+ ", changeNoticeID=" + changeNoticeID + ", container=" + container + "]";
	}

}
