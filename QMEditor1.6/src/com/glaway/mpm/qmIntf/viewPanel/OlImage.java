package com.glaway.mpm.qmIntf.viewPanel;

public class OlImage {
	private String oid;
	private String name;
	private String fullName;
	
	public OlImage(String name,String fullName,String oid) {
		super();
		this.oid = oid;
		this.name = name;
		this.fullName=fullName;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
