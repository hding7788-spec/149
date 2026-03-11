/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.technics.entity;

/**
 *
 * @author hywang
 */
public class Part {
    private String oid;
    private String partNumber="";
    private String partName="";
    private String material="";
    private String dutu="";
    private String remark="";
    private String useCount;


    public String getDutu() {
        return dutu;
    }

    public void setDutu(String dutu) {
        this.dutu = dutu;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public void setUseCount(String useCount) {
		this.useCount = useCount;
	}

	public String getUseCount() {
		return useCount;
	}
    
    
}
