package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class WorkShop implements Serializable, Comparable<WorkShop> {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 *
	 */
	private String oid;
	/**
	 * 制造单位编号
	 */
	private String number;
	/**
	 * 制造单位名称
	 */
	private String name;
	/**
	 * 工序名称
	 */
	private List<PdName> pdNames;
	/**
	 * 工位
	 */
	private List<WorkSpace> workSpaces;
	/**
	 * 设备
	 */
	private List<EpType> epTypes;

	/**
	 * 工具
	 */
	private List<ToolType> toolTypes;

	/**
	 * 量具
	 */
	private List<ToolType> measureTypes;

	/**
	 * 工种
	 */
	private List<Skill> skillList;



	public List<Skill> getSkillList() {
		return skillList;
	}

	public void setSkillList(List<Skill> skillList) {
		this.skillList = skillList;
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


	public List<PdName> getPdNames() {
		return pdNames;
	}

	public void setPdNames(List<PdName> pdNames) {
		this.pdNames = pdNames;
	}

	public List<WorkSpace> getWorkSpaces() {
		return workSpaces;
	}

	public void setWorkSpaces(List<WorkSpace> workSpaces) {
		this.workSpaces = workSpaces;
	}

	public List<EpType> getEpTypes() {
		return epTypes;
	}

	public void setEpTypes(List<EpType> epTypes) {
		this.epTypes = epTypes;
	}

	public List<ToolType> getToolTypes() {
		return toolTypes;
	}

	public void setToolTypes(List<ToolType> toolTypes) {
		this.toolTypes = toolTypes;
	}

	public List<ToolType> getMeasureTypes() {
		return measureTypes;
	}

	public void setMeasureTypes(List<ToolType> measureTypes) {
		this.measureTypes = measureTypes;
	}

	public int compareTo(WorkShop o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(), o.getName());
	}

}
