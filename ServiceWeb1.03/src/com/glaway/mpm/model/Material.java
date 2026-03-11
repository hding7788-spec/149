package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class Material implements Serializable, Comparable<Material> {
	private static final long serialVersionUID = 1L;

	private String oid;
	/**
	 * 编号
	 */
	private String number;

	/**
	 * 物资编码
	 */
	private String materialNumber;
	/**
	 * 材料名称
	 */
	private String materialName;
	/**
	 * 材料牌号
	 */
	private String materialBrand;

	//英文名称
	private String EnglishName;


	public String getEnglishName() {
        return EnglishName;
    }

    public void setEnglishName(String englishName) {
        EnglishName = englishName;
    }

    //812 属性
	private String clph; //材料牌号
	private String clgg; //材料规格
	private String clbz; //材料标准
	private String jldw; //计量单位

	//规格
	private String csize;
	//型号
	private String mindex;
	//技术条件
	private String jstj;
	//附件条件
	private String fjtj;

	/**
	 * 材料标准号
	 */
	private String materialCrision;
	/**
	 * 材料类型
	 */
	private String materialCategory;
	/**
	 * 材料密度
	 */
	private String materialDensity;
	/**
	 * 材料损耗系数
	 */
	private String attritionRate;
	/**
	 * 材料定额单位
	 */
	private String materialUnit;

	/**
	 * 材料规格
	 */
	private String materialSpec;

	public Material() {
		super();
	}

	/**
	 * Description: 假的材料,用来制作的材料树的加号
	 */

	public String getOid() {
		return oid;
	}

	public Material(String oid, String materialNumber, String materialName) {
		super();
		this.oid = oid;
		this.materialNumber = materialNumber;
		this.materialName = materialName;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialBrand() {
		return materialBrand;
	}

	public void setMaterialBrand(String materialBrand) {
		this.materialBrand = materialBrand;
	}

	public String getMaterialCrision() {
		return materialCrision;
	}

	public void setMaterialCrision(String materialCrision) {
		this.materialCrision = materialCrision;
	}

	public String getMaterialCategory() {
		return materialCategory;
	}

	public void setMaterialCategory(String materialCategory) {
		this.materialCategory = materialCategory;
	}

	public String getMaterialDensity() {
		return materialDensity;
	}

	public void setMaterialDensity(String materialDensity) {
		this.materialDensity = materialDensity;
	}

	public String getAttritionRate() {
		return attritionRate;
	}

	public void setAttritionRate(String attritionRate) {
		this.attritionRate = attritionRate;
	}

	public String getMaterialUnit() {
		return materialUnit;
	}

	public void setMaterialUnit(String materialUnit) {
		this.materialUnit = materialUnit;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMaterialSpec() {
		return materialSpec;
	}

	public void setMaterialSpec(String materialSpec) {
		this.materialSpec = materialSpec;
	}

	public String getClph() {
		return clph;
	}

	public void setClph(String clph) {
		this.clph = clph;
	}

	public String getClgg() {
		return clgg;
	}

	public void setClgg(String clgg) {
		this.clgg = clgg;
	}

	public String getClbz() {
		return clbz;
	}

	public void setClbz(String clbz) {
		this.clbz = clbz;
	}

	public String getJldw() {
		return jldw;
	}

	public void setJldw(String jldw) {
		this.jldw = jldw;
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

	public String getJstj() {
		return jstj;
	}

	public void setJstj(String jstj) {
		this.jstj = jstj;
	}

	public String getFjtj() {
		return fjtj;
	}

	public void setFjtj(String fjtj) {
		this.fjtj = fjtj;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int compareTo(Material o) {
		return Collator.getInstance(Locale.CHINA).compare(this.materialName, o.getMaterialName());
	}
}
