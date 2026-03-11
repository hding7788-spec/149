package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class FkType implements Serializable, Comparable<FkType> {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields name :设备类型的名称
	 */
	private String name;
	private String typePath;

	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

	/**
	 * @Fields commonStrings : 设备集合
	 */
	private List<Frock> frocks;

	private List<FkType> fkTypes;

	public FkType(String name) {
		super();
		this.name = name;
	}

	public FkType(String name, List<Frock> frocks, List<FkType> fkTypes) {
		super();
		this.name = name;
		this.frocks = frocks;
		this.fkTypes = fkTypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Frock> getFrocks() {
		return frocks;
	}

	public void setFrocks(List<Frock> frocks) {
		this.frocks = frocks;
	}

	public List<FkType> getFkTypes() {
		return fkTypes;
	}

	public void setFkTypes(List<FkType> fkTypes) {
		this.fkTypes = fkTypes;
	}

	public int compareTo(FkType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(), o.getName());
	}

}
