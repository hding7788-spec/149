package com.glaway.mpm.qmIntf.commonString;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.ShopType;

public class CsTreeNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String value;
	private boolean isSelected;
	private ShopType shopType;

	public CsTreeNode(String value) {
		this.value = value;
	}

	public CsTreeNode(ShopType shopType) {
		this.shopType = shopType;
		this.value = shopType.getName();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ShopType getShopType() {
		return shopType;
	}

	public void setShopType(ShopType shopType) {
		this.shopType = shopType;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
