package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class WorkPlace implements Serializable, Comparable<WorkPlace> {
	private static final long serialVersionUID = 1L;

	private String oid;
	/**
	 * 工位编号
	 */
	private String number;
	/**
	 * 工位名称
	 */
	private String name;
	/**
	 * 工位位置
	 */
	private String space;
	/**
	 * 工位简述
	 */
	private String self;


	//英文名称
	private String EngLishName;


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


	public String getSpace() {
		return space;
	}


	public void setSpace(String space) {
		this.space = space;
	}


	public String getSelf() {
		return self;
	}


	public void setSelf(String self) {
		this.self = self;
	}


	public String getEngLishName() {
		return EngLishName;
	}


	public void setEngLishName(String engLishName) {
		EngLishName = engLishName;
	}

	public int compareTo(WorkPlace o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(),
				o.getName());
	}

}
