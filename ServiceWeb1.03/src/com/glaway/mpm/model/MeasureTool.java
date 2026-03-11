package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class MeasureTool implements Serializable, Comparable<MeasureTool> {
	private static final long serialVersionUID = 1L;

	private String oid;
	/**
	 * 工装编号
	 */
	private String measureToolNum;
	/**
	 * 工装名称
	 */
	private String measureToolName;
	/**
	 * 工装标准号
	 */
	private String measureToolStdNumber;
	/**
	 * 工装规格
	 */
	private String measureToolSpec;

	public MeasureTool() {
		super();
	}

	public MeasureTool(String oid, String measureToolNum, String measureToolName, String measureToolStdNumber,
			String measureToolSpec) {
		super();
		this.oid = oid;
		this.measureToolNum = measureToolNum;
		this.measureToolName = measureToolName;
		this.measureToolStdNumber = measureToolStdNumber;
		this.measureToolSpec = measureToolSpec;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getMeasureToolNum() {
		return measureToolNum;
	}

	public void setMeasureToolNum(String measureToolNum) {
		this.measureToolNum = measureToolNum;
	}

	public String getMeasureToolName() {
		return measureToolName;
	}

	public void setMeasureToolName(String measureToolName) {
		this.measureToolName = measureToolName;
	}

	public String getMeasureToolStdNumber() {
		return measureToolStdNumber;
	}

	public void setMeasureToolStdNumber(String measureToolStdNumber) {
		this.measureToolStdNumber = measureToolStdNumber;
	}

	public String getMeasureToolSpec() {
		return measureToolSpec;
	}

	public void setMeasureToolSpec(String measureToolSpec) {
		this.measureToolSpec = measureToolSpec;
	}

	public int compareTo(MeasureTool o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getMeasureToolName(), o.getMeasureToolName());
	}

}
