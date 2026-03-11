package com.glaway.mpm.qmIntf.resourceTree.model;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.Frock;

public class FkNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String number;
	private String name;
	private Frock frock;
	private boolean isSelected;

	public FkNode(Frock frock) {
		super();
		this.number = frock.getFrockNum();
		this.name = frock.getFrockName();
		this.frock = frock;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Frock getFrock() {
		return frock;
	}

	public void setFrock(Frock frock) {
		this.frock = frock;
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
