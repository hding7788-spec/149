package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class PdName implements Serializable, Comparable<PdName> {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 *
	 */
	private String oid;
	/**
	 * 工序名称
	 */
	private String name;
	/**
	 * 工序名称的首字母
	 */
	private String shortcut;

	/**
	 * 工种
	 */
	private List<ShopType> shopTypes;

	//备注
	private String remark;

	private String number;

	@Override
	public String toString() {
		return "name:"+name+"   number:"+number+"   remark:"+remark;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortcut() {
		return shortcut;
	}

	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}

	public List<ShopType> getShopTypes() {
		return shopTypes;
	}

	public void setShopTypes(List<ShopType> shopTypes) {
		this.shopTypes = shopTypes;
	}

	public int compareTo(PdName o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(), o.getName());
	}

}
