package com.glaway.mpm.qmIntf.commonString;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class CsNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String value;
	private boolean isSelected;

	public CsNode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
