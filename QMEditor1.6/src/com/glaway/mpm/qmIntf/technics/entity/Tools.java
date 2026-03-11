/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.technics.entity;

/**
 *
 * @author hywang
 */
public class Tools {
    private String oid;
    private String toolNum="";
    private String toolName="";
    private String toolStdNum="";
    private String toolSpec="";
    private String useCount;

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getToolNum() {
        return toolNum;
    }

    public void setToolNum(String toolNum) {
        this.toolNum = toolNum;
    }

    public String getToolSpec() {
        return toolSpec;
    }

    public void setToolSpec(String toolSpec) {
        this.toolSpec = toolSpec;
    }

    public String getToolStdNum() {
        return toolStdNum;
    }

    public void setToolStdNum(String toolStdNum) {
        this.toolStdNum = toolStdNum;
    }

    public String getUseCount() {
        return useCount;
    }

    public void setUseCount(String useCount) {
        this.useCount = useCount;
    }

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
    
}
