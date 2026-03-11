package com.glaway.mpm.qmIntf.dashboard;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class UnSDashboardTreeNode extends DefaultMutableTreeNode implements Serializable {

	
	private String name;
	private boolean isSelected;

	@Override
	public String toString() {
		return "UnSDashboardTreeNode [name=" + name + ", isSelected="
				+ isSelected + "]";
	}

	public UnSDashboardTreeNode(String name) {
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
