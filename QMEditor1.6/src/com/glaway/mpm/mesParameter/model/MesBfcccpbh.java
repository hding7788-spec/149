package com.glaway.mpm.mesParameter.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class MesBfcccpbh implements Serializable, Comparable<MesBfcccpbh>{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String bfccpbhno;
	private String gygckno;

	public String getGygckno() {
		return gygckno;
	}
	public void setGygckno(String gygckno) {
		this.gygckno = gygckno;
	}
	public String getBfccpbhno() {
		return bfccpbhno;
	}
	public void setBfccpbhno(String bfccpbhno) {
		this.bfccpbhno = bfccpbhno;
	}
	@Override
	public int compareTo(MesBfcccpbh o) {
		// TODO Auto-generated method stub
		return Collator.getInstance(Locale.CHINA).compare(this.getBfccpbhno(),
				o.getBfccpbhno());
	}

}
