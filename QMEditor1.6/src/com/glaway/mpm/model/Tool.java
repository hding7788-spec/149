package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class Tool implements Serializable, Comparable<Tool> {
	private static final long serialVersionUID = 1L;

	private String oid;

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 工装编号
	 */
	private String toolNum;
	/**
	 * 工装名称
	 */
	private String toolName;
	/**
	 * 工装标准号
	 */
	private String toolStdNumber;
	/**
	 * 工装规格
	 */
	private String toolSpec;

	//TODO 149  型号
	private String mindex;
	//TODO 149 规格
	private String csize;

	//英文名称
    private String EngLishName;


    public String getEngLishName() {
        return EngLishName;
    }

    public void setEngLishName(String engLishName) {
        EngLishName = engLishName;
    }

	@Override
	public String toString() {
		return "Tool [oid=" + oid + ", type=" + type + ", toolNum=" + toolNum
				+ ", toolName=" + toolName + ", toolStdNumber=" + toolStdNumber
				+ ", toolSpec=" + toolSpec + ", mindex=" + mindex + ", csize="
				+ csize + "]";
	}

	public Tool() {
		super();
	}

	public Tool(String oid, String toolNum, String toolName,
			String toolStdNumber, String toolSpec) {
		super();
		this.oid = oid;
		this.toolNum = toolNum;
		this.toolName = toolName;
		this.toolStdNumber = toolStdNumber;
		this.toolSpec = toolSpec;
	}

	public String getToolNum() {
		return toolNum;
	}

	public void setToolNum(String toolNum) {
		this.toolNum = toolNum;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getToolStdNumber() {
		return toolStdNumber;
	}

	public void setToolStdNumber(String toolStdNumber) {
		this.toolStdNumber = toolStdNumber;
	}

	public String getToolSpec() {
		return toolSpec;
	}

	public void setToolSpec(String toolSpec) {
		this.toolSpec = toolSpec;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public int compareTo(Tool o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getToolName(),
				o.getToolName());
	}

	public String getMindex() {
		return mindex;
	}

	public void setMindex(String mindex) {
		this.mindex = mindex;
	}

	public String getCsize() {
		return csize;
	}

	public void setCsize(String csize) {
		this.csize = csize;
	}

}
