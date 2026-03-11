package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class KtType implements Serializable, Comparable<KtType> {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields name :设备类型的名称
	 */
	private String name;
	/**
	 * @Fields commonStrings : 设备集合
	 */
	private List<KnifeTool> knifeTools;

	private List<KtType> ktTypes;

	private String typePath;

	public KtType(String name) {
		super();
		this.name = name;
	}

	public KtType(String name, List<KnifeTool> knifeTools, List<KtType> ktTypes) {
		super();
		this.name = name;
		this.knifeTools = knifeTools;
		this.ktTypes = ktTypes;
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

	public List<KnifeTool> getKnifeTools() {
		return knifeTools;
	}

	public void setKnifeTools(List<KnifeTool> knifeTools) {
		this.knifeTools = knifeTools;
	}

	public List<KtType> getKtTypes() {
		return ktTypes;
	}

	public void setKtTypes(List<KtType> ktTypes) {
		this.ktTypes = ktTypes;
	}

	public int compareTo(KtType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(), o.getName());
	}

}
