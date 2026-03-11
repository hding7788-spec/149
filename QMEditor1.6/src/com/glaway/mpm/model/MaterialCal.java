package com.glaway.mpm.model;

import java.io.Serializable;

public class MaterialCal implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String number;
	private String materialDensity;
	private String attritionRate;
	private String singleSize;
	private String formula;
	private String materialUnit;

	public MaterialCal() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public String getSingleSize() {
		return singleSize;
	}

	public void setSingleSize(String singleSize) {
		this.singleSize = singleSize;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getMaterialUnit() {
		return materialUnit;
	}

	public void setMaterialUnit(String materialUnit) {
		this.materialUnit = materialUnit;
	}

	@Override
	public String toString() {
		return "{" + name + "," + number + "," + materialDensity + "," + attritionRate + "," + singleSize + ","
				+ formula + "," + materialUnit + "}";
	}
}
