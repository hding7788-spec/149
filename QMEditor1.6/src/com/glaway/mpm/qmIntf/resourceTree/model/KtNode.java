package com.glaway.mpm.qmIntf.resourceTree.model;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.KnifeTool;

public class KtNode extends DefaultMutableTreeNode implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private String number;
	private String name;
	private KnifeTool knifeTool;
	private boolean isSelected;

	public KtNode(KnifeTool knifeTool) {
		super();
		this.number = knifeTool.getKnifeToolNum();
		this.name = knifeTool.getKnifeToolName();
		this.knifeTool = knifeTool;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public KnifeTool getKnifeTool() {
		return knifeTool;
	}

	public void setKnifeTool(KnifeTool knifeTool) {
		this.knifeTool = knifeTool;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
