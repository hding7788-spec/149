package com.glaway.mpm.model;

import java.io.Serializable;

public class ProcessEditorBean implements Serializable{


	/**
	 *
	 */
	private static final long serialVersionUID = -1517602191808657148L;
	private String userName;
	private String swt;
	private String jwsRuntimeParameters;

	public ProcessEditorBean(String userName, String swt, String jwsRuntimeParameters) {
		this.userName = userName;
		this.swt = swt;
		this.jwsRuntimeParameters = jwsRuntimeParameters;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSwt() {
		return swt;
	}
	public void setSwt(String swt) {
		this.swt = swt;
	}
	public String getJwsRuntimeParameters() {
		return jwsRuntimeParameters;
	}
	public void setJwsRuntimeParameters(String jwsRuntimeParameters) {
		this.jwsRuntimeParameters = jwsRuntimeParameters;
	}
	@Override
	public String toString() {
		return "ProcessEditorBean [userName=" + userName + ", swt=" + swt + ", jwsRuntimeParameters=" + jwsRuntimeParameters + "]";
	}

}

