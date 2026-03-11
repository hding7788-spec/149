package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class Dashboard implements Serializable, Comparable<Dashboard> {
	private static final long serialVersionUID = 1L;

	private String oid;
	/**
	 * 编号
	 */
	private String number;
	/**
	 * 名称
	 */
	private String name;


	private String equipmentType; //类别

	private String csize; //规格

	private String mindex; //型号

	/**
	 * 设备编号
	 */
	private String equipmentNumber;
	/**
	 * 型号
	 */
	private String modelNumber;
	/**
	 * 设备数量
	 */
	private String equipmentAmount;
	/**
	 * 设备厂家
	 */
	private String equipmentVender;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 子设备
	 */
	private List<Dashboard> equipments;

	/**
	 * 是否标准仪器仪表
	 */
	private boolean isStandard;


    //英文名称
	private String EnglishName;



	public String getEnglishName() {
        return EnglishName;
    }

    public void setEnglishName(String englishName) {
        EnglishName = englishName;
    }

    public Dashboard() {
		super();
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEquipmentNumber() {
		return equipmentNumber;
	}

	public void setEquipmentNumber(String equipmentNumber) {
		this.equipmentNumber = equipmentNumber;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getEquipmentAmount() {
		return equipmentAmount;
	}

	public void setEquipmentAmount(String equipmentAmount) {
		this.equipmentAmount = equipmentAmount;
	}

	public String getEquipmentVender() {
		return equipmentVender;
	}

	public void setEquipmentVender(String equipmentVender) {
		this.equipmentVender = equipmentVender;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<Dashboard> getEquipments() {
		return equipments;
	}

	public void setEquipments(List<Dashboard> equipments) {
		this.equipments = equipments;
	}

	public int compareTo(Dashboard o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(),
				o.getName());
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	public String getCsize() {
		return csize;
	}

	public void setCsize(String csize) {
		this.csize = csize;
	}

	public String getMindex() {
		return mindex;
	}

	public void setMindex(String mindex) {
		this.mindex = mindex;
	}

	public boolean isStandard() {
		return isStandard;
	}

	public void setStandard(boolean isStandard) {
		this.isStandard = isStandard;
	}

}
