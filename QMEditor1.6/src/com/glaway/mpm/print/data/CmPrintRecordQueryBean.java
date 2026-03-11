package com.glaway.mpm.print.data;

import java.io.Serializable;

public class CmPrintRecordQueryBean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -7448130778309810157L;
	//单位
	private String unit;
	//型号
	private String type;
	//文件类型
	private String fileType;
	//文件编号
	private String fileNumber;
	//文件名称
	private String fileName;
	//版本
	private String version;
	//分发状态
	private String distributeStatus;
	//阶段标记
	private String phaseCode;
	//打印时间
	private String printDate;
	//打印开始时间
	private String printStartDate;
	//打印结束时间
	private String printEndDate;
	//领取时间
	private String getDate;
	//领取部门
	private String getDept;
	//领取人
	private String getUser;
	//回收时间
	private String recoverDate;
	//版本
	private String docVersion;
	//bom
	private String BOMValue;
	//工艺文件目录
	private String ProcessDirectoryValue;
	//打印人
	private String printer;

	public String getPrintStartDate() {
		return printStartDate;
	}
	public void setPrintStartDate(String printStartDate) {
		this.printStartDate = printStartDate;
	}
	public String getPrintEndDate() {
		return printEndDate;
	}
	public void setPrintEndDate(String printEndDate) {
		this.printEndDate = printEndDate;
	}
	public String getPhaseCode() {
		return phaseCode;
	}
	public void setPhaseCode(String phaseCode) {
		this.phaseCode = phaseCode;
	}
	public String getDocVersion() {
		return docVersion;
	}
	public void setDocVersion(String docVersion) {
		this.docVersion = docVersion;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
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
	public String getDistributeStatus() {
		return distributeStatus;
	}
	public void setDistributeStatus(String distributeStatus) {
		this.distributeStatus = distributeStatus;
	}
	public String getPrintDate() {
		return printDate;
	}
	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}
	public String getGetDate() {
		return getDate;
	}
	public void setGetDate(String getDate) {
		this.getDate = getDate;
	}
	public String getGetDept() {
		return getDept;
	}
	public void setGetDept(String getDept) {
		this.getDept = getDept;
	}
	public String getGetUser() {
		return getUser;
	}
	public void setGetUser(String getUser) {
		this.getUser = getUser;
	}
	public String getRecoverDate() {
		return recoverDate;
	}
	public void setRecoverDate(String recoverDate) {
		this.recoverDate = recoverDate;
	}
	public String getBOMValue() {
		return BOMValue;
	}
	public void setBOMValue(String bOMValue) {
		BOMValue = bOMValue;
	}
	public String getProcessDirectoryValue() {
		return ProcessDirectoryValue;
	}
	public void setProcessDirectoryValue(String processDirectoryValue) {
		ProcessDirectoryValue = processDirectoryValue;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public String getPrinter() {
		return printer;
	}

	public void setPrinter(String printer) {
		this.printer = printer;
	}

	@Override
	public String toString() {
		return "CmPrintRecordQueryBean [unit=" + unit + ", type=" + type + ", fileType=" + fileType + ", fileNumber=" + fileNumber + ", fileName=" + fileName + ", distributeStatus="
				+ distributeStatus + ", phaseCode=" + phaseCode + ", printDate=" + printDate + ", printStartDate=" + printStartDate + ", version=" + version + ", printEndDate=" + printEndDate
				+ ", getDate=" + getDate + ", getDept=" + getDept + ", getUser=" + getUser+ ", printer=" + printer + ", recoverDate=" + recoverDate + ", docVersion=" + docVersion + ", BOMValue=" + BOMValue
				+ ", ProcessDirectoryValue=" + ProcessDirectoryValue + "]";
	}

}
