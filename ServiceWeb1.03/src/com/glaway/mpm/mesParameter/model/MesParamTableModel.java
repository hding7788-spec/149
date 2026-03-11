package com.glaway.mpm.mesParameter.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class MesParamTableModel implements Serializable, Comparable<MesParamTableModel>{

	private String productnumber;
	private String lukahao;
	private String gxpk;
	private String technicsnumber;
	private String objtype;
	private String objnumber;
	private String iszf;
	public String getProductnumber() {
		return productnumber;
	}
	public void setProductnumber(String productnumber) {
		this.productnumber = productnumber;
	}
	public String getLukahao() {
		return lukahao;
	}
	public void setLukahao(String lukahao) {
		this.lukahao = lukahao;
	}
	public String getGxpk() {
		return gxpk;
	}
	public void setGxpk(String gxpk) {
		this.gxpk = gxpk;
	}
	public String getTechnicsnumber() {
		return technicsnumber;
	}
	public void setTechnicsnumber(String technicsnumber) {
		this.technicsnumber = technicsnumber;
	}
	public String getObjtype() {
		return objtype;
	}
	public void setObjtype(String objtype) {
		this.objtype = objtype;
	}
	public String getObjnumber() {
		return objnumber;
	}
	public void setObjnumber(String objnumber) {
		this.objnumber = objnumber;
	}
	public String getIszf() {
		return iszf;
	}
	public void setIszf(String iszf) {
		this.iszf = iszf;
	}
	@Override
	public int compareTo(MesParamTableModel o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getProductnumber(),
				o.getProductnumber());
	}

}
