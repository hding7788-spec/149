package com.glaway.mpm.print.data;

import java.io.Serializable;

public class CmSealBean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 966303581142448262L;
	private String gwKeyId;
	private String name;
	private String number;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getGwKeyId() {
		return gwKeyId;
	}
	public void setGwKeyId(String gwKeyId) {
		this.gwKeyId = gwKeyId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "CmSealBean [gwKeyId=" + gwKeyId + ", name=" + name + ", number=" + number + "]";
	}


}
