package com.glaway.mpm.model;

import java.io.Serializable;
import java.util.List;

/**
 * 工步对象
 * 
 * @author xuehu
 * 
 */
public class PaceObject implements Serializable{

	private static final long serialVersionUID = 1L;

	private String paceNumber;

	private List<Equipment> equipments;

	private List<Tool> tools;

	private List<Material> materials;

	public String getPaceNumber() {
		return paceNumber;
	}

	public void setPaceNumber(String paceNumber) {
		this.paceNumber = paceNumber;
	}

	public List<Equipment> getEquipments() {
		return equipments;
	}

	public void setEquipments(List<Equipment> equipments) {
		this.equipments = equipments;
	}

	public List<Tool> getTools() {
		return tools;
	}

	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}

}
