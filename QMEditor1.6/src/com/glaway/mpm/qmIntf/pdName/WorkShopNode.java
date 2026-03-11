package com.glaway.mpm.qmIntf.pdName;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.WorkShop;

public class WorkShopNode extends DefaultMutableTreeNode implements
		Serializable {
	private static final long serialVersionUID = 1L;
	private String number;
	private String name;
	private boolean isSelected;
	private WorkShop workShop;

	public WorkShopNode(WorkShop workShop) {
		this.number = workShop.getNumber();
		this.name = workShop.getName();
		this.workShop = workShop;
	}

	public WorkShop getWorkShop() {
		return workShop;
	}

	public void setWorkShop(WorkShop workShop) {
		this.workShop = workShop;
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

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
