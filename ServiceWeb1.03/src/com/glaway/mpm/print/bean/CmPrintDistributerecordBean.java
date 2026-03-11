package com.glaway.mpm.print.bean;

public class CmPrintDistributerecordBean {
	private String gwkeyid;
	private String docVR;
	private String applyrecordid;
	private String distributedept;
	private String distributequantity;
	public String getGwkeyid() {
		return gwkeyid;
	}
	public void setGwkeyid(String gwkeyid) {
		this.gwkeyid = gwkeyid;
	}
	public String getDocVR() {
		return docVR;
	}
	public void setDocVR(String docVR) {
		this.docVR = docVR;
	}
	public String getApplyrecordid() {
		return applyrecordid;
	}
	public void setApplyrecordid(String applyrecordid) {
		this.applyrecordid = applyrecordid;
	}
	public String getDistributedept() {
		return distributedept;
	}
	public void setDistributedept(String distributedept) {
		this.distributedept = distributedept;
	}
	public String getDistributequantity() {
		return distributequantity;
	}
	public void setDistributequantity(String distributequantity) {
		this.distributequantity = distributequantity;
	}
	@Override
	public String toString() {
		return "CmPrintDistributerecordBean [gwkeyid=" + gwkeyid + ", docVR=" + docVR + ", applyrecordid=" + applyrecordid + ", distributedept=" + distributedept + ", distributequantity="
				+ distributequantity + "]";
	}



}
