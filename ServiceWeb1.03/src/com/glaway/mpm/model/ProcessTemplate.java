package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class ProcessTemplate implements Serializable,
		Comparable<ProcessTemplate> {
	private static final long serialVersionUID = 1L;
	private String oid;
	private String number;
	private String name;

	public ProcessTemplate() {
		super();
	}

	public ProcessTemplate(String oid, String number, String name) {
		super();
		this.oid = oid;
		this.number = number;
		this.name = name;
	}

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

	
	public int compareTo(ProcessTemplate o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(),
				o.getName());
	}

}
