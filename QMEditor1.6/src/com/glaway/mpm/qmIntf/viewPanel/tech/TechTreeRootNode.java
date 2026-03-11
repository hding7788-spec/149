package com.glaway.mpm.qmIntf.viewPanel.tech;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class TechTreeRootNode extends DefaultMutableTreeNode implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private boolean isSelected;
	private String stepNumber;

	public TechTreeRootNode(String name, String stepNumber) {
		this.name = name;
		this.stepNumber = stepNumber;
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

	public String getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(String stepNumber) {
		this.stepNumber = stepNumber;
	}

}
