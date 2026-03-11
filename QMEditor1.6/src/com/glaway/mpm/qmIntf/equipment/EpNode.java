package com.glaway.mpm.qmIntf.equipment;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.Equipment;

public class EpNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String number;
	private String name;
	private Equipment equipment;
	private boolean isSelected;
	private boolean isParent;

	public EpNode(Equipment equipment, boolean isParent) {
		super();
		this.number = equipment.getNumber();
		this.name = equipment.getName();
		this.equipment = equipment;
		this.isParent = isParent;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

}
