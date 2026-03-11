package com.glaway.mpm.print.data;

import java.io.Serializable;

public class CmPrintRecordInfoBean implements Serializable{


	/**
	 *
	 */
	private static final long serialVersionUID = -1446419048522141255L;
	private Boolean flag;
	private String docVR;
	private String uuid;
	private String unit;
	private String type;
	/** 阶段标记 */
	private String phaseCode;
	private String fileType;
	private String fileNumber;
	private String fileName;
	private String distributeStatus;
	private String printDate;
	private String getDate;
	private String getDept;
	private String getUser;
	private String recoverDate;
	private String docVersion;
	private String secret;
	private String batch;
	private String barCode;
	private String delayStatus;
	private String delayReason;
	private String delayDate;
	private String applyUser;
	private String applyDate;
	private String printUser;
	private String storeTime;
	private String barTableID;
	private String loseReason;
	private String currentUser;
	private String middleStatus;
	private String pboOid;
	private String disMessage;
	private String currentDept;
	private String currentDate;
	private String targetDept;

	/** 生命周期状态 */
	private String lifeCycleState;
	/** 上下文名称 */
	private String containerName;
	/** 分发状态 */
	private String distributeState;
	/** 回收人*/
	private String recycler;
	/** 回收时间*/
	private String recycleTime;
	/** 回收人部门*/
	private String recyclerDept;
	/** 文件状态*/
	private String fileState;
	/** 打印开始时间*/
	private String printStartDate;
	/** 打印结束时间*/
	private String printEndDate;
	/** 封存申请人*/
	private String storeUser;
	/** 封存时间*/
	private String storeDate;
	/** 封存部门*/
	private String storeDept;



	public String getDisMessage() {
		return disMessage;
	}
	public void setDisMessage(String disMessage) {
		this.disMessage = disMessage;
	}
	public String getDocVR() {
		return docVR;
	}
	public void setDocVR(String docVR) {
		this.docVR = docVR;
	}
	public String getPboOid() {
		return pboOid;
	}
	public void setPboOid(String pboOid) {
		this.pboOid = pboOid;
	}
	public String getMiddleStatus() {
		return middleStatus;
	}
	public void setMiddleStatus(String middleStatus) {
		this.middleStatus = middleStatus;
	}
	public String getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}
	public String getCurrentDept() {
		return currentDept;
	}
	public void setCurrentDept(String currentDept) {
		this.currentDept = currentDept;
	}
	public String getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	public String getLoseReason() {
		return loseReason;
	}
	public void setLoseReason(String loseReason) {
		this.loseReason = loseReason;
	}
	public String getBarTableID() {
		return barTableID;
	}
	public void setBarTableID(String barTableID) {
		this.barTableID = barTableID;
	}
	public String getStoreTime() {
		return storeTime;
	}
	public void setStoreTime(String storeTime) {
		this.storeTime = storeTime;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getDocVersion() {
		return docVersion;
	}
	public void setDocVersion(String docVersion) {
		this.docVersion = docVersion;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getPhaseCode() {
		return phaseCode;
	}
	public void setPhaseCode(String phaseCode) {
		this.phaseCode = phaseCode;
	}

	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public String getDelayStatus() {
		return delayStatus;
	}
	public void setDelayStatus(String delayStatus) {
		this.delayStatus = delayStatus;
	}
	public String getDelayReason() {
		return delayReason;
	}
	public void setDelayReason(String delayReason) {
		this.delayReason = delayReason;
	}
	public String getDelayDate() {
		return delayDate;
	}
	public void setDelayDate(String delayDate) {
		this.delayDate = delayDate;
	}
	public String getApplyUser() {
		return applyUser;
	}
	public void setApplyUser(String applyUser) {
		this.applyUser = applyUser;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getPrintUser() {
		return printUser;
	}
	public void setPrintUser(String printUser) {
		this.printUser = printUser;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLifeCycleState() {
		return lifeCycleState;
	}
	public void setLifeCycleState(String lifeCycleState) {
		this.lifeCycleState = lifeCycleState;
	}
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getDistributeState() {
		return distributeState;
	}
	public void setDistributeState(String distributeState) {
		this.distributeState = distributeState;
	}
	public String getRecycler() {
		return recycler;
	}
	public void setRecycler(String recycler) {
		this.recycler = recycler;
	}
	public String getRecycleTime() {
		return recycleTime;
	}
	public void setRecycleTime(String recycleTime) {
		this.recycleTime = recycleTime;
	}
	public String getRecyclerDept() {
		return recyclerDept;
	}
	public void setRecyclerDept(String recyclerDept) {
		this.recyclerDept = recyclerDept;
	}
	public String getFileState() {
		return fileState;
	}
	public void setFileState(String fileState) {
		this.fileState = fileState;
	}
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
	public String getStoreUser() {
		return storeUser;
	}
	public void setStoreUser(String storeUser) {
		this.storeUser = storeUser;
	}
	public String getStoreDate() {
		return storeDate;
	}
	public void setStoreDate(String storeDate) {
		this.storeDate = storeDate;
	}
	public String getStoreDept() {
		return storeDept;
	}
	public void setStoreDept(String storeDept) {
		this.storeDept = storeDept;
	}

	public String getTargetDept() {
		return targetDept;
	}

	public void setTargetDept(String targetDept) {
		this.targetDept = targetDept;
	}

	@Override
	public String toString() {
		return "CmPrintRecordInfoBean [flag=" + flag + ", docVR=" + docVR + ", uuid=" + uuid + ", unit=" + unit + ", type=" + type + ", phaseCode=" + phaseCode + ", fileType=" + fileType
				+ ", fileNumber=" + fileNumber + ", fileName=" + fileName + ", distributeStatus=" + distributeStatus + ", printDate=" + printDate + ", getDate=" + getDate + ", getDept=" + getDept
				+ ", getUser=" + getUser + ", recoverDate=" + recoverDate + ", docVersion=" + docVersion + ", secret=" + secret + ", batch=" + batch + ", barCode=" + barCode + ", delayStatus="
				+ delayStatus + ", delayReason=" + delayReason + ", delayDate=" + delayDate + ", applyUser=" + applyUser + ", applyDate=" + applyDate + ", printUser=" + printUser + ", storeTime="
				+ storeTime + ", barTableID=" + barTableID + ", loseReason=" + loseReason + ", currentUser=" + currentUser + ", middleStatus=" + middleStatus + ", pboOid=" + pboOid + ", disMessage="
				+ disMessage + ", currentDept=" + currentDept + ", currentDate=" + currentDate + ", lifeCycleState=" + lifeCycleState + ", containerName=" + containerName + ", distributeState="
				+ distributeState + ", recycler=" + recycler + ", recycleTime=" + recycleTime + ", recyclerDept=" + recyclerDept + ", fileState=" + fileState + ", printStartDate=" + printStartDate
				+ ", printEndDate=" + printEndDate + "]";
	}

}
