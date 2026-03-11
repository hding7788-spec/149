package com.glaway.mpm.print.data;

import java.io.Serializable;

public class CmImportBean implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -6081443170990253730L;
	private String distributeDept;
	private String distributeQuantity;
	/** 外来单位*/
	private String outDept;
	/** 文件编号*/
	private String fileNumber;
	/** 文件名称*/
	private String fileName;
	/** 版本*/
	private String version;
	/** 阶段标记*/
	private String phaseCode;
	/** 文件类型*/
	private String fileType;
	/** 页数*/
	private String pageCount;
	/** 密级*/
	private String secret;
	/** 更改单外来单位*/
	private String changeNoticeOutDept;
	/** 成套图更改*/
	private String viewChange;
	/** 更改单编号*/
	private String changeNoticeNumber;
	/** 更改日期*/
	private String changeDate;
	/** 更改内容*/
	private String changeContent;
	/** 型号类型*/
	private String xhlx;
	/**oid*/
	private String oid;
	/**批次*/
	private String batch;
	private String index;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getOutDept() {
		return outDept;
	}
	public void setOutDept(String outDept) {
		this.outDept = outDept;
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDistributeDept() {
		return distributeDept;
	}
	public void setDistributeDept(String distributeDept) {
		this.distributeDept = distributeDept;
	}
	public String getDistributeQuantity() {
		return distributeQuantity;
	}
	public void setDistributeQuantity(String distributeQuantity) {
		this.distributeQuantity = distributeQuantity;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getPhaseCode() {
		return phaseCode;
	}
	public void setPhaseCode(String phaseCode) {
		this.phaseCode = phaseCode;
	}
	public String getPageCount() {
		return pageCount;
	}
	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getChangeNoticeOutDept() {
		return changeNoticeOutDept;
	}
	public void setChangeNoticeOutDept(String changeNoticeOutDept) {
		this.changeNoticeOutDept = changeNoticeOutDept;
	}
	public String getViewChange() {
		return viewChange;
	}
	public void setViewChange(String viewChange) {
		this.viewChange = viewChange;
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
	public String getXhlx() {
		return xhlx;
	}
	public void setXhlx(String xhlx) {
		this.xhlx = xhlx;
	}
	@Override
	public String toString() {
		return "CmImportBean [distributeDept=" + distributeDept + ", distributeQuantity=" + distributeQuantity + ", outDept=" + outDept + ", fileNumber=" + fileNumber + ", fileName=" + fileName
				+ ", version=" + version + ", phaseCode=" + phaseCode + ", fileType=" + fileType + ", pageCount=" + pageCount + ", secret=" + secret + ", changeNoticeOutDept=" + changeNoticeOutDept
				+ ", viewChange=" + viewChange + ", changeNoticeNumber=" + changeNoticeNumber + ", changeDate=" + changeDate + ", changeContent=" + changeContent + ", xhlx=" + xhlx + "]";
	}

}
