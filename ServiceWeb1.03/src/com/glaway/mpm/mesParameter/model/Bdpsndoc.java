package com.glaway.mpm.mesParameter.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class Bdpsndoc implements Serializable, Comparable<Bdpsndoc>{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String psnname;
	private String psncode;
	private String clerkcode;
	public String getPsnname() {
		return psnname;
	}
	public void setPsnname(String psnname) {
		this.psnname = psnname;
	}
	public String getClerkcode() {
		return clerkcode;
	}
	public void setClerkcode(String clerkcode) {
		this.clerkcode = clerkcode;
	}
	public String getPsncode() {
		return psncode;
	}
	public void setPsncode(String psncode) {
		this.psncode = psncode;
	}
	@Override
	public int compareTo(Bdpsndoc o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getPsnname(),
				o.getPsnname());
	}





}
