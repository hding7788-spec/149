package com.glaway.mpm.qmIntf.equipment;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class EpTreeNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private boolean isSelected;

	public EpTreeNode(String name) {
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
