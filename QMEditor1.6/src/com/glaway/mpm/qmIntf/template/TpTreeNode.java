package com.glaway.mpm.qmIntf.template;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class TpTreeNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String STEP_TYPE_PUBLIC = "STEP_PUBLIC";
	public static final String STEP_TYPE_LOCAL = "STEP_LOCAL";

	private String name;
	private String stepType = "";
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public TpTreeNode(String name) {
		super();
		this.name = name;
	}
	public TpTreeNode(String name,String stepType) {
		this.name = name;
		this.stepType = stepType;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStepType() {
		return stepType;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

}
