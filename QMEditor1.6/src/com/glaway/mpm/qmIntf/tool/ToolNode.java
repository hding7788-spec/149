package com.glaway.mpm.qmIntf.tool;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.Tool;

public class ToolNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String number;
	private String name;
	private Tool tool;
	private boolean isSelected;

	public ToolNode(Tool tool) {
		super();
		this.number = tool.getToolNum();
		this.name = tool.getToolName();
		this.tool = tool;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Tool getTool() {
		return tool;
	}

	public void setTool(Tool tool) {
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
