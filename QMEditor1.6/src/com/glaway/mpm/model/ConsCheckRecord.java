package com.glaway.mpm.model;

import java.io.Serializable;

public class ConsCheckRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String gwkeyid;

	public ConsCheckRecord(String name, String gwkeyid) {
		this.name = name;
		this.gwkeyid = gwkeyid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGwkeyid() {
		return gwkeyid;
	}

	public void setGwkeyid(String gwkeyid) {
		this.gwkeyid = gwkeyid;
	}
}
