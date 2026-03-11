package com.glaway.mpm.model;

import java.io.Serializable;
import java.util.List;

public class PdNameType implements Serializable, Comparable<PdNameType> {

	private String name;
	private String typePath;

	private List<PdNameType> pdNameTypeList;
	private List<PdName> pdNameList;

	public PdNameType(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

	public List<PdNameType> getPdNameTypeList() {
		return pdNameTypeList;
	}

	public void setPdNameTypeList(List<PdNameType> pdNameTypeList) {
		this.pdNameTypeList = pdNameTypeList;
	}

	public List<PdName> getPdNameList() {
		return pdNameList;
	}

	public void setPdNameList(List<PdName> pdNameList) {
		this.pdNameList = pdNameList;
	}

	@Override
	public int compareTo(PdNameType o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
