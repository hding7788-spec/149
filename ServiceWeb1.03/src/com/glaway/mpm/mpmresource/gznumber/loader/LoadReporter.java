package com.glaway.mpm.mpmresource.gznumber.loader;

import java.util.ArrayList;

public class LoadReporter {
	private ArrayList logs;
	private ArrayList displayErrors;
	private ArrayList backErrors;
	
	public LoadReporter(){
		logs = new ArrayList();
		displayErrors = new ArrayList();
		backErrors = new ArrayList();
	}
	
	public void addLogs(String logDesc){
		logs.add(logDesc);
	}
	
	public void addDisplayErrors(String errorDesc){
		displayErrors.add(errorDesc);
	}
	
	public void addBackErrors(String errorDesc){
		backErrors.add(errorDesc);
	}
	
	public ArrayList getBackErrors() {
		return backErrors;
	}
	public void setBackErrors(ArrayList backErrors) {
		this.backErrors = backErrors;
	}
	public ArrayList getDisplayErrors() {
		return displayErrors;
	}
	public void setDisplayErrors(ArrayList displayErrors) {
		this.displayErrors = displayErrors;
	}

	public ArrayList getLogs() {
		return logs;
	}

	public void setLogs(ArrayList logs) {
		this.logs = logs;
	}
}
