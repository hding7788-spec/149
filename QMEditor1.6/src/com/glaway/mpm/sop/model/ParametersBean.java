package com.glaway.mpm.sop.model;

import java.io.Serializable;

public class ParametersBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private long oid;
    private String number;
    private String name;
    private String specializedType;
    private String proceduceName;
    private String materialCategory;
    private String canShuZhi;
    private String description;

    @Override
    public String toString() {
        return "ParametersBean [oid=" + oid + ", number=" + number + ", name="
                + name + ", specializedType=" + specializedType
                + ", proceduceName=" + proceduceName + ", materialCategory="
                + materialCategory + ", canShuZhi=" + canShuZhi
                + ", description=" + description + "]";
    }

    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
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

    public String getSpecializedType() {
        return specializedType;
    }

    public void setSpecializedType(String specializedType) {
        this.specializedType = specializedType;
    }

    public String getProceduceName() {
        return proceduceName;
    }

    public void setProceduceName(String proceduceName) {
        this.proceduceName = proceduceName;
    }

    public String getMaterialCategory() {
        return materialCategory;
    }

    public void setMaterialCategory(String materialCategory) {
        this.materialCategory = materialCategory;
    }

    public String getCanShuZhi() {
        return canShuZhi;
    }

    public void setCanShuZhi(String canShuZhi) {
        this.canShuZhi = canShuZhi;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
