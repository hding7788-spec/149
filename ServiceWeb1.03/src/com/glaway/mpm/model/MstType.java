package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class MstType implements Serializable, Comparable<MstType> {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields name :设备类型的名称
	 */
	private String name;
	/**
	 * @Fields commonStrings : 设备集合
	 */
	private List<MeasureTool> measureTools;

	private List<MstType> mstTypes;

	public MstType(String name) {
		super();
		this.name = name;
	}

	public MstType(String name, List<MeasureTool> materialTools, List<MstType> mstTypes) {
		super();
		this.name = name;
		this.measureTools = materialTools;
		this.mstTypes = mstTypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MeasureTool> getMeasureTools() {
		return measureTools;
	}

	public void setMeasureTools(List<MeasureTool> measureTools) {
		this.measureTools = measureTools;
	}

	public List<MstType> getMstTypes() {
		return mstTypes;
	}

	public void setMstTypes(List<MstType> mstTypes) {
		this.mstTypes = mstTypes;
	}

	public int compareTo(MstType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(), o.getName());
	}

}