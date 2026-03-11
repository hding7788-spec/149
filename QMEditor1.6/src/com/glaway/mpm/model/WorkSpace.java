package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class WorkSpace implements Serializable, Comparable<WorkSpace> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String oid;

	/**
	 * 工位编号
	 */
	private String number;
	/**
	 * 工位名称
	 */
	private String name;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(WorkSpace o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(), o.getName());
	}

}
