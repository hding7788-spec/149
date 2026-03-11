package com.glaway.mpm.qmIntf.dashboard;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.UnSDashboard;

public class UnSDashboardNode extends DefaultMutableTreeNode implements Serializable {

	private String number;
	private String name;
	private UnSDashboard dashboard;
	private boolean isSelected;
	private boolean isParent;

	@Override
	public String toString() {
		return "UnSDashboardNode [number=" + number + ", name=" + name
				+ ", dashboard=" + dashboard + ", isSelected=" + isSelected
				+ ", isParent=" + isParent + "]";
	}

	public UnSDashboardNode(UnSDashboard dashboard, boolean isParent) {
		super();
		this.number = dashboard.getNumber();
		this.name = dashboard.getName();
		this.dashboard = dashboard;
		this.isParent = isParent;
	}

	public UnSDashboard getUnSDashboard() {
		return dashboard;
	}

	public void setUnSDashboard(UnSDashboard dashboard) {
		this.dashboard = dashboard;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

}
