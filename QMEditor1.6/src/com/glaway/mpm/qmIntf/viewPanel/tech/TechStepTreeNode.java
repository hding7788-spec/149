package com.glaway.mpm.qmIntf.viewPanel.tech;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class TechStepTreeNode extends DefaultMutableTreeNode implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private String oid;
	private String stepNumber;
	private boolean isStep;

	public TechStepTreeNode(String oid, String stepNumber, boolean isStep) {
		this.oid = oid;
		this.stepNumber = stepNumber;
		this.isStep = isStep;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(String stepNumber) {
		this.stepNumber = stepNumber;
	}

	public boolean isStep() {
		return isStep;
	}

	public void setStep(boolean isStep) {
		this.isStep = isStep;
	}


}
