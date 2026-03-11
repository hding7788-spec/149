package com.glaway.mpm.processplan.checkouttable.checkbean;

public class SummaryRecordBean {

    /**工序*/
    private String stepNumber;

    /**工步*/
    private String paceNumber;

    /**名称*/
    private String number;

    /**名称*/
    private String name;

    //照片样张汇总表 begin
    /**拍摄要求*/
    private String psyq;

    /**判别准则*/
    private String pbzz;

    /**照片样张url*/
    private String photoUrl;
    //照片样张汇总表 end


    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getPaceNumber() {
        return paceNumber;
    }

    public void setPaceNumber(String paceNumber) {
        this.paceNumber = paceNumber;
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

    public String getPsyq() {
        return psyq;
    }

    public void setPsyq(String psyq) {
        this.psyq = psyq;
    }

    public String getPbzz() {
        return pbzz;
    }

    public void setPbzz(String pbzz) {
        this.pbzz = pbzz;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
