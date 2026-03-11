package com.glaway.mpm.qmIntf.resourceTree.model;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.MeasureTool;

public class MstNode extends DefaultMutableTreeNode implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private String number;
	private String name;
	private MeasureTool tool;
	private boolean isSelected;

	public MstNode(MeasureTool tool) {
		super();
		this.number = tool.getMeasureToolNum();
		this.name = tool.getMeasureToolName();
		this.tool = tool;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public MeasureTool getMeasureTool() {
		return tool;
	}

	public void setMeasureTool(MeasureTool tool) {
		this.tool = tool;
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
