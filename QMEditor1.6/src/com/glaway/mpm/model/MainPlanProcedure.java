package com.glaway.mpm.model;

import java.io.Serializable;

public class MainPlanProcedure implements Serializable {

	private static final long serialVersionUID = 1L;
	private String bsoid = null;
	private String stepLabel = null;
	public MainPlanProcedure(String bsoid, String label) {
		this.bsoid = bsoid;
		this.stepLabel = label;
	}
	
	@Override
	public String toString() {
		return stepLabel;
	}

	public String getBsoid() {
		return bsoid;
	}

	public void setBsoid(String bsoid) {
		this.bsoid = bsoid;
	}

	public String getStepLabel() {
		return stepLabel;
	}

	public void setStepLabel(String stepLabel) {
		this.stepLabel = stepLabel;
	}
}
