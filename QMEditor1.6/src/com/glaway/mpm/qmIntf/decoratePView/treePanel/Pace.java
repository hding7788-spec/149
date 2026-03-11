package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import java.util.List;

import com.glaway.mpm.model.TempObject;

public class Pace {
	private String oid;
	private String number;
	private String name;
	private String content;
	private List<DpPartNode> parts;

	public Pace(String oid, String number, String name, String content,
			List<DpPartNode> parts) {
		super(); 
		this.oid = oid;
		this.number = number;
		this.name = name;
		this.content = content;
		this.parts = parts;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<DpPartNode> getParts() {
		return parts;
	}

	public void setParts(List<DpPartNode> parts) {
		this.parts = parts;
	}

}
