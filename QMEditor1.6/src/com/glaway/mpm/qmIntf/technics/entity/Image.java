/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.technics.entity;

/**
 *
 * @author hywang
 */
public class Image {
    private String oid;
    private String drawingNumber="";
    private String drawingName="";
    private String drawingType="";
    private String drawingLink="";

    public String getDrawingLink() {
        return drawingLink;
    }

    public void setDrawingLink(String drawingLink) {
        this.drawingLink = drawingLink;
    }

    public String getDrawingName() {
        return drawingName;
    }

    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }

    public String getDrawingNumber() {
        return drawingNumber;
    }

    public void setDrawingNumber(String drawingNumber) {
        this.drawingNumber = drawingNumber;
    }

    public String getDrawingType() {
        return drawingType;
    }

    public void setDrawingType(String drawingType) {
        this.drawingType = drawingType;
    }

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
    
    
}
