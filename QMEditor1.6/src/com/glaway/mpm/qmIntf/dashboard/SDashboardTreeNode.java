package com.glaway.mpm.qmIntf.dashboard;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class SDashboardTreeNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private boolean isSelected;

	@Override
	public String toString() {
		return "SDashboardTreeNode [name=" + name + ", isSelected="
				+ isSelected + "]";
	}

	public SDashboardTreeNode(String name) {
		this.name = name;
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
