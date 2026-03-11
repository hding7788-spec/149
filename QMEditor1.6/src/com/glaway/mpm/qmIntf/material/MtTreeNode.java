package com.glaway.mpm.qmIntf.material;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class MtTreeNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private boolean isSelected;
	private String typePath;

	public MtTreeNode(String name, String typePath) {
		this.name = name;
		this.typePath = typePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
