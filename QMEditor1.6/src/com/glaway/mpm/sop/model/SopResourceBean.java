package com.glaway.mpm.sop.model;

import java.io.Serializable;

/**
 * SOP资源客户端bean对象
 */
public class SopResourceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public static String OID = "oid";
    public static String NUMBER = "number";
    public static String NAME = "name";
    public static String TYPE = "type";
    public static String SPECIALIZEDTYPE = "specializedType";
    public static String PROCEDURENAME = "procedureName";
    public static String MATERIALCATEGORY = "materialCategory";
    public static String CANSHUZHI = "canshuzhi";
    public static String DESCRIPTION = "description";
    public static String ISNEW = "isNew";

    //oid
    private String oid;
    //编号
    private String number;
    //名称
    private String name;
    //资源类型   工序项目参数（PARAMETERS)   操作岗位（OPERATIONJOB）       工序名称（PROCEDUCENAME）
    //         专业类别（SPECIALIZEDTYPE） 参数项目名称（PARAMETERSNAME）  定制区域（CUSTOMAREA）
    private String type;
    //专业类别
    private String specializedType;
    //工序名称
    private String procedureName;
    //物资类别
    private String materialCategory;
    //参数值
    private String canshuzhi;
    //说明
    private String description;
    //是否手动输入
    private String isNew;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpecializedType() {
        return specializedType;
    }

    public void setSpecializedType(String specializedType) {
        this.specializedType = specializedType;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getMaterialCategory() {
        return materialCategory;
    }

    public void setMaterialCategory(String materialCategory) {
        this.materialCategory = materialCategory;
    }

    public String getCanshuzhi() {
        return canshuzhi;
    }

    public void setCanshuzhi(String canshuzhi) {
        this.canshuzhi = canshuzhi;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}
}
