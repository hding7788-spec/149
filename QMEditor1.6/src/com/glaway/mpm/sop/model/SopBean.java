package com.glaway.mpm.sop.model;

import java.io.Serializable;
import java.util.List;

public class SopBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String oid;
    private String number;
    private String name;
    private String ppnumber;
    private String specializedType;
    private String proceduceName;
    private String operationJob;
    private String customArea;
    private String parameters;
    private String secret;
    private String version;
    private String professionalCod;
    private String gongXuJianHao;
    private String description;
    private String department;
    private List<ParametersBean> arameters;

    @Override
    public String toString() {
        return "SopBean [oid=" + oid + ", number=" + number + ", name=" + name + ", ppnumber=" + ppnumber
                + ", specializedType=" + specializedType + ", proceduceName="
                + proceduceName + ", operationJob=" + operationJob
                + ", customArea=" + customArea + ", parameters=" + parameters
                + ", secret=" + secret + ", version=" + version
                + ", professionalCod=" + professionalCod + ", gongXuJianHao="
                + gongXuJianHao + ", description=" + description
                + ", department=" + department + "]";
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

    public String getOperationJob() {
        return operationJob;
    }

    public void setOperationJob(String operationJob) {
        this.operationJob = operationJob;
    }

    public String getCustomArea() {
        return customArea;
    }

    public void setCustomArea(String customArea) {
        this.customArea = customArea;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProfessionalCod() {
        return professionalCod;
    }

    public void setProfessionalCod(String professionalCod) {
        this.professionalCod = professionalCod;
    }

    public String getGongXuJianHao() {
        return gongXuJianHao;
    }

    public void setGongXuJianHao(String gongXuJianHao) {
        this.gongXuJianHao = gongXuJianHao;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<ParametersBean> getArameters() {
        return arameters;
    }

    public void setArameters(List<ParametersBean> arameters) {
        this.arameters = arameters;
    }

    public String getPpnumber() {
        return ppnumber;
    }

    public void setPpnumber(String ppnumber) {
        this.ppnumber = ppnumber;
    }
}