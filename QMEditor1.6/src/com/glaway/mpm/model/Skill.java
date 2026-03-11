package com.glaway.mpm.model;

import java.io.Serializable;

public class Skill implements Serializable, Comparable<Skill>{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 *
	 */
	private String oid;
	/**
	 * 工种名称
	 */
	private String name;
	/**
	 * 工种编号
	 */
	private String number;


	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int compareTo(Skill o) {
		// TODO Auto-generated method stub
		return 0;
	}





}
