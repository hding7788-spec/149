package com.glaway.mpm.qmIntf.pdName;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.PdName;

public class PdNameNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private PdName pdName;
	private boolean isSelected;

	public PdNameNode(PdName pdName) {
		super();
		this.name = pdName.getName();
		this.pdName = pdName;
	}

	public PdName getPdName() {
		return pdName;
	}

	public void setPdName(PdName pdName) {
		this.pdName = pdName;
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
