package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

/**
 * @author ylshao
 * @ClassName: CsType
 * @Description: 常用语类型的对象
 * @date 2012-11-20
 *
 */
public class CsType implements Serializable, Comparable<CsType> {
	private static final long serialVersionUID = 1L;

	/**
	 * @Fields commonStrings : 常用语集合
	 */
	private List<String> commonStrings;

	private List<CsType> csTypes;

	private String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 工种对象，用于查找csTreePanel
	 */
	private ShopType shopType;

	public CsType() {
		super();
	}

	public CsType(String name){
		super();
        this.name=name;
	}

	public CsType(ShopType shopType, List<String> commonStrings,
			List<CsType> csTypes) {
		super();
		this.commonStrings = commonStrings;
		this.csTypes = csTypes;
		this.shopType = shopType;
	}

	public ShopType getShopType() {
		return shopType;
	}

	public void setShopType(ShopType shopType) {
		this.shopType = shopType;
	}

	public List<String> getCommonStrings() {
		return commonStrings;
	}

	public void setCommonStrings(List<String> commonStrings) {
		this.commonStrings = commonStrings;
	}

	public List<CsType> getCsTypes() {
		return csTypes;
	}

	public void setCsTypes(List<CsType> csTypes) {
		this.csTypes = csTypes;
	}

	public int compareTo(CsType o) {

		return Collator.getInstance(Locale.CHINA).compare(
				this.shopType.getName(), o.getShopType().getName());
	}
}
