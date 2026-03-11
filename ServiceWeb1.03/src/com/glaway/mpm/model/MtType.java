package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class MtType implements Serializable, Comparable<MtType> {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields name :材料类型的显示名称
	 */
	private String name;
	/**
	 * @Fields typePath :材料类型的文件夹路径
	 */
	private String typePath;
	/**
	 * @Fields materials : 材料集合
	 */
	private List<Material> materials;
	/**
	 * @Fields materials : 材料类型集合
	 */
	private List<MtType> mtTypes;

	public MtType(String name) {
		super();
		this.name = name;
	}

	public MtType(String name, List<Material> materials, List<MtType> mtTypes) {
		super();
		this.name = name;
		this.materials = materials;
		this.mtTypes = mtTypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}

	public List<MtType> getMtTypes() {
		return mtTypes;
	}

	public void setMtTypes(List<MtType> mtTypes) {
		this.mtTypes = mtTypes;
	}

	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

	public int compareTo(MtType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(), o.getName());
	}
}
