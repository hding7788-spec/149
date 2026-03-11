package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;


public class EpType implements Serializable, Comparable<EpType> {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields name :设备类型的名称
	 */
	private String name;
	/**
	 * @Fields commonStrings : 设备集合
	 */
	private List<Equipment> equipments;

	private List<EpType> epTypes;

	private String typePath;

	public EpType(String name) {
		super();
		this.name = name;
	}

	public EpType(String name, List<Equipment> equipments, List<EpType> epTypes) {
		super();
		this.name = name;
		this.equipments = equipments;
		this.epTypes = epTypes;
	}


	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Equipment> getEquipments() {
		return equipments;
	}

	public void setEquipments(List<Equipment> equipments) {
		this.equipments = equipments;
	}

	public List<EpType> getEpTypes() {
		return epTypes;
	}

	public void setEpTypes(List<EpType> epTypes) {
		this.epTypes = epTypes;
	}

	public int compareTo(EpType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.name, o.getName());
	}

}
