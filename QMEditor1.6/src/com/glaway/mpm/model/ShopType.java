package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class ShopType implements Serializable, Comparable<ShopType> {
	private static final long serialVersionUID = 1L;

	private String oid;
	/**
	 * 工种编号
	 */
	private String number;
	/**
	 * 工种名称
	 */
	private String name;

	private List<String> css;

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

	public List<String> getCss() {
		return css;
	}

	public void setCss(List<String> css) {
		this.css = css;
	}

	public int compareTo(ShopType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(), o.getName());
	}

}
