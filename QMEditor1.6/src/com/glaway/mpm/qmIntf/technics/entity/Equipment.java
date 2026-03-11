/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.technics.entity;

/**
 *
 * @author hywang
 */
public class Equipment {
    private String oid;
    private String eqNum="";
    private String eqName="";
    private String eqModel="";
    private String useCount;


    public String getEqModel() {
        return eqModel;
    }

    public void setEqModel(String eqModel) {
        this.eqModel = eqModel;
    }

    public String getEqName() {
        return eqName;
    }

    public void setEqName(String eqName) {
        this.eqName = eqName;
    }

    public String getEqNum() {
        return eqNum;
    }

    public void setEqNum(String eqNum) {
        this.eqNum = eqNum;
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
    
//    public String toFormatString(){
//        
//    }
}
