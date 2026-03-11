package com.glaway.mpm.model;

import java.io.Serializable;
import java.util.List;

public class CheckTechnics implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<StepObject> stepObjects;

	public List<StepObject> getStepObjects() {
		return stepObjects;
	}

	public void setStepObjects(List<StepObject> stepObjects) {
		this.stepObjects = stepObjects;
	}

}