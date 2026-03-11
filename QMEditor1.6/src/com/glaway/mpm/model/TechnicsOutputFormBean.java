package com.glaway.mpm.model;

import java.io.Serializable;

public class TechnicsOutputFormBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String techType;
	private String techID;
	private String techForm;
	private String name;
	private String formName;
	private String formId;
	private String selectable;

	@Override
	public String toString() {
		return "techType:" + techType + ";techID:" + techID + ";techForm:"
				+ techForm + ";name:" + name + ";formName:" + formName
				+ ";formId:" + formId + ";selectable:" + selectable;
	}

	public String getTechType() {
		return techType;
	}

	public void setTechType(String techType) {
		this.techType = techType;
	}

	public String getTechID() {
		return techID;
	}

	public void setTechID(String techID) {
		this.techID = techID;
	}

	public String getTechForm() {
		return techForm;
	}

	public void setTechForm(String techForm) {
		this.techForm = techForm;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getSelectable() {
		return selectable;
	}

	public void setSelectable(String selectable) {
		this.selectable = selectable;
	}

}
