package com.glaway.mpm.qmIntf.resourceTree;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class ResourceTreeNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private boolean isSelected;

	public ResourceTreeNode(String name) {
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
