package com.glaway.mpm.model;

import java.io.Serializable;
import java.util.List;

/**
 * 工序对象
 * 
 * @author xuehu
 * 
 */
public class StepObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private String stepNumber;

	private List<Equipment> equipments;

	private List<Tool> tools;

	private List<Material> materials;

	private List<PaceObject> paceObjects;

	private String preStep;

	public String getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(String stepNumber) {
		this.stepNumber = stepNumber;
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

	public List<PaceObject> getPaceObjects() {
		return paceObjects;
	}

	public void setPaceObjects(List<PaceObject> paceObjects) {
		this.paceObjects = paceObjects;
	}

	public void setPreStep(String preStep) {
		this.preStep = preStep;
	}

	public String getPreStep() {
		return preStep;
	}

}
