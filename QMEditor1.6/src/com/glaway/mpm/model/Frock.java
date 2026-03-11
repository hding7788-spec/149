package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class Frock implements Serializable, Comparable<Frock> {
	private static final long serialVersionUID = 1L;

	private String oid;
	/**
	 * 工装编号
	 */
	private String frockNum;

	private String type;

	/**
	 * 工装名称
	 */
	private String frockName;
	/**
	 * 工装标准号
	 */
	private String frockStdNum;
	/**
	 * 工装规格
	 */
	private String frockSpec;

	/**
	 * 工装申请卡编号
	 */
	private String frockCardNum;

	//英文名称
	private String EngLishName;


	public String getEngLishName() {
        return EngLishName;
    }

    public void setEngLishName(String engLishName) {
        EngLishName = engLishName;
    }

    private String pindex;
	private String typeno;
	private String levle;

	//TODO 149 工装类别
	private String frockType;

	//专用工装

	private String enabledDate;
	private String latestStatus;
	private String applyNo;
	private String applyName;
	private String applyDate;
	private String qty;
	//通用工装
	private String csize;
	private String factory;
	private String resource;
	private String manuno;
	private String manageStatus;
	private String qualityStatus;
	private String manageLevel;


	public Frock() {
		super();

	}

	public Frock(String oid, String frockNum, String frockName,
			String frockStdNum, String frockSpec) {
		super();
		this.oid = oid;
		this.frockNum = frockNum;
		this.frockName = frockName;
		this.frockStdNum = frockStdNum;
		this.frockSpec = frockSpec;
	}

	public String getFrockCardNum() {
		return frockCardNum;
	}

	public void setFrockCardNum(String frockCardNum) {
		this.frockCardNum = frockCardNum;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getFrockNum() {
		return frockNum;
	}

	public void setFrockNum(String frockNum) {
		this.frockNum = frockNum;
	}

	public String getFrockName() {
		return frockName;
	}

	public void setFrockName(String frockName) {
		this.frockName = frockName;
	}

	public String getFrockStdNum() {
		return frockStdNum;
	}

	public void setFrockStdNum(String frockStdNum) {
		this.frockStdNum = frockStdNum;
	}

	public String getFrockSpec() {
		return frockSpec;
	}

	public void setFrockSpec(String frockSpec) {
		this.frockSpec = frockSpec;
	}


	public String getPindex() {
		return pindex;
	}

	public void setPindex(String pindex) {
		this.pindex = pindex;
	}

	public String getTypeno() {
		return typeno;
	}

	public void setTypeno(String typeno) {
		this.typeno = typeno;
	}

	public String getLevle() {
		return levle;
	}

	public void setLevle(String levle) {
		this.levle = levle;
	}

	public String getEnabledDate() {
		return enabledDate;
	}

	public void setEnabledDate(String enabledDate) {
		this.enabledDate = enabledDate;
	}

	public String getLatestStatus() {
		return latestStatus;
	}

	public void setLatestStatus(String latestStatus) {
		this.latestStatus = latestStatus;
	}

	public String getApplyNo() {
		return applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	public String getApplyName() {
		return applyName;
	}

	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getCsize() {
		return csize;
	}

	public void setCsize(String csize) {
		this.csize = csize;
	}

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getManuno() {
		return manuno;
	}

	public void setManuno(String manuno) {
		this.manuno = manuno;
	}

	public String getManageStatus() {
		return manageStatus;
	}

	public void setManageStatus(String manageStatus) {
		this.manageStatus = manageStatus;
	}

	public String getQualityStatus() {
		return qualityStatus;
	}

	public void setQualityStatus(String qualityStatus) {
		this.qualityStatus = qualityStatus;
	}

	public String getManageLevel() {
		return manageLevel;
	}

	public void setManageLevel(String manageLevel) {
		this.manageLevel = manageLevel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public int compareTo(Frock o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getFrockName(),
				o.getFrockName());
	}

	public String getFrockType() {
		return frockType;
	}

	public void setFrockType(String frockType) {
		this.frockType = frockType;
	}
}
