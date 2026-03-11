package com.glaway.mpm.mesParameter.model;

import java.io.Serializable;

public class GLZhuFuLink implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String gwkey;
    private String fztechnicsnumber;
    private String fztechnicsversion;
    private String fzprocedurenumber;
    private String zztechnicsnumber;
    private String zztechnicsversion;
    private String zzprocedurenumber;
    private String picihao;
    private String zstepNumber;
    private String zstepName;
    private String zstepBsoid;
    private String fppanNumber;
    private String ftechnicsName;
    private String creator;
    private String createTime;
    private String operation;



    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GLZhuFuLink) {
            GLZhuFuLink glZhuFuLink = (GLZhuFuLink) obj;
            return glZhuFuLink.getZztechnicsnumber().equals(this.zztechnicsnumber)
                    && glZhuFuLink.getZztechnicsversion().equals(this.zztechnicsversion)
                    && glZhuFuLink.getZstepNumber().equals(this.zstepNumber)
                    && glZhuFuLink.getFztechnicsnumber().equals(this.fztechnicsnumber)
                    && glZhuFuLink.getFztechnicsversion().equals(this.fztechnicsversion);

        } else {
            return false;
        }
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getZstepNumber() {
        return zstepNumber;
    }

    public void setZstepNumber(String zstepNumber) {
        this.zstepNumber = zstepNumber;
    }

    public String getZstepName() {
        return zstepName;
    }

    public void setZstepName(String zstepName) {
        this.zstepName = zstepName;
    }

    public String getFppanNumber() {
        return fppanNumber;
    }

    public void setFppanNumber(String fppanNumber) {
        this.fppanNumber = fppanNumber;
    }

    public String getFtechnicsName() {
        return ftechnicsName;
    }

    public void setFtechnicsName(String ftechnicsName) {
        this.ftechnicsName = ftechnicsName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGwkey() {
        return gwkey;
    }

    public void setGwkey(String gwkey) {
        this.gwkey = gwkey;
    }

    public String getFztechnicsnumber() {
        return fztechnicsnumber;
    }

    public void setFztechnicsnumber(String fztechnicsnumber) {
        this.fztechnicsnumber = fztechnicsnumber;
    }

    public String getFztechnicsversion() {
        return fztechnicsversion;
    }

    public void setFztechnicsversion(String fztechnicsversion) {
        this.fztechnicsversion = fztechnicsversion;
    }

    public String getFzprocedurenumber() {
        return fzprocedurenumber;
    }

    public void setFzprocedurenumber(String fzprocedurenumber) {
        this.fzprocedurenumber = fzprocedurenumber;
    }

    public String getZztechnicsnumber() {
        return zztechnicsnumber;
    }

    public void setZztechnicsnumber(String zztechnicsnumber) {
        this.zztechnicsnumber = zztechnicsnumber;
    }

    public String getZztechnicsversion() {
        return zztechnicsversion;
    }

    public void setZztechnicsversion(String zztechnicsversion) {
        this.zztechnicsversion = zztechnicsversion;
    }

    public String getZzprocedurenumber() {
        return zzprocedurenumber;
    }

    public void setZzprocedurenumber(String zzprocedurenumber) {
        this.zzprocedurenumber = zzprocedurenumber;
    }

    public String getPicihao() {
        return picihao;
    }

    public void setPicihao(String picihao) {
        this.picihao = picihao;
    }

    public String getZstepBsoid() {
        return zstepBsoid;
    }

    public void setZstepBsoid(String zstepBsoid) {
        this.zstepBsoid = zstepBsoid;
    }
}
