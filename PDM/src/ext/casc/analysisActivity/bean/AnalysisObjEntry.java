package ext.casc.analysisActivity.bean;

import cn.hutool.core.util.StrUtil;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnalysisObjEntry implements CmPersistable {

    public AnalysisObjEntry() {
    }

    public AnalysisObjEntry(String analysisNumber, String verOid, String dataType) {
        this.keyId = UUID.randomUUID().toString();
        this.analysisNumber = analysisNumber;
        this.verOid = verOid;
        this.dataType = dataType;
        this.dealStatus = AnalysisConstant.DEAL_STATUS_WORKING;
    }

    private static final long serialVersionUID = -6195311955227693714L;
    //影响分析编号
    public static final String ANALYSISNUMBER = "analysisNumber";
    //对象大版本oid
    public static final String VEROID = "verOid";
    //对象类型：pbom technics zproduct yproduct
    public static final String DATATYPE = "dataType";
    //主任师意见
    public static final String AFFECTED = "affected";
    //工艺员意见
    public static final String AFFECTEDGYY = "affectedGyy";
    //责任人
    public static final String RESPONSER = "responser";
    //责任部门
    public static final String UNIT = "unit";
    //制品处理意见
    public static final String PRODUCT = "product";
    //制品数量
    public static final String COUNT = "count";
    //制品实际返修数量
    public static final String REPAIRCOUNT = "repaircount";
    //整件外协数量
    public static final String ZJWXCOUNT = "zjwxcount";
    //更改要求
    public static final String REQUIREMENT = "requirement";
    //要求完成时间
    public static final String COMPLETETIME = "completeTime";
    //关联更改单/工艺
    public static final String RELATEDORDER = "relatedOrder";
    //完成情况
    public static final String DEALSTATUS = "dealStatus";
    //通知情况
    public static final String SENDSTATUS = "sendStatus";
    //制品是否已接收数量标识
    public static final String RECEIVESTATUS = "receiveStatus";
    //任务下发时间
    public static final String TASKTIME = "taskTime";
    //工艺员备注
    public static final String REMARKGYY = "remarkGyy";

    private String keyId = "";
    private String analysisNumber = "";
    private String verOid = "";
    private String dataType = "";
    private String affected = "";
    private String affectedGyy = "";
    private String responser = "";
    private String unit = "";
    private String product = "";
    private Integer count = 0;
    private Integer repaircount = 0;
    private Integer zjwxcount = 0;
    private String requirement = "";
    private String completeTime = "";
    private String relatedOrder = "";
    private String dealStatus = "";
    private String sendStatus = "";
    private String receiveStatus = "";
    private Timestamp taskTime;
    private String remarkGyy = "";

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getAnalysisNumber() {
        return StrUtil.isEmpty(analysisNumber) ? "" : analysisNumber;
    }

    public void setAnalysisNumber(String analysisNumber) {
        this.analysisNumber = analysisNumber;
    }

    public String getVerOid() {
        return StrUtil.isEmpty(verOid) ? "" : verOid;
    }

    public void setVerOid(String verOid) {
        this.verOid = verOid;
    }

    public String getDataType() {
        return StrUtil.isEmpty(dataType) ? "" : dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAffected() {
        return StrUtil.isEmpty(affected) ? "" : affected;
    }

    public void setAffected(String affected) {
        this.affected = affected;
    }

    public String getAffectedGyy() {
        return StrUtil.isEmpty(affectedGyy) ? "" : affectedGyy;
    }

    public void setAffectedGyy(String affectedGyy) {
        this.affectedGyy = affectedGyy;
    }

    public String getResponser() {
        return StrUtil.isEmpty(responser) ? "" : responser;
    }

    public void setResponser(String responser) {
        this.responser = responser;
    }

    public String getUnit() {
        return StrUtil.isEmpty(unit) ? "" : unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProduct() {
        return StrUtil.isEmpty(product) ? "" : product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getCount() {
        return count == null ? 0 : count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getRepaircount() {
        return repaircount;
    }

    public void setRepaircount(Integer repaircount) {
        this.repaircount = repaircount;
    }

    public Integer getZjwxcount() {
        return zjwxcount == null ? 0 : zjwxcount;
    }

    public void setZjwxcount(Integer zjwxcount) {
        this.zjwxcount = zjwxcount;
    }

    public String getRequirement() {
        return StrUtil.isEmpty(requirement) ? "" : requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getCompleteTime() {
        return StrUtil.isEmpty(completeTime) ? "" : completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getRelatedOrder() {
        return StrUtil.isEmpty(relatedOrder) ? "" : relatedOrder;
    }

    public void setRelatedOrder(String relatedOrder) {
        this.relatedOrder = relatedOrder;
    }

    public String getDealStatus() {
        return StrUtil.isEmpty(dealStatus) ? "" : dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getSendStatus() {
        return StrUtil.isEmpty(sendStatus) ? "" : sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(String receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public Timestamp getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(Timestamp taskTime) {
        this.taskTime = taskTime;
    }

    public String getRemarkGyy() {
        return StrUtil.isEmpty(remarkGyy) ? "" : remarkGyy;
    }

    public void setRemarkGyy(String remarkGyy) {
        this.remarkGyy = remarkGyy;
    }

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if(rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setAnalysisNumber(rs.getString(ANALYSISNUMBER));
            setVerOid(rs.getString(VEROID));
            setDataType(rs.getString(DATATYPE));
            setAffected(rs.getString(AFFECTED));
            setAffectedGyy(rs.getString(AFFECTEDGYY));
            setResponser(rs.getString(RESPONSER));
            setUnit(rs.getString(UNIT));
            setProduct(rs.getString(PRODUCT));
            setCount(rs.getInt(COUNT));
            setRepaircount(rs.getInt(REPAIRCOUNT));
            setZjwxcount(rs.getInt(ZJWXCOUNT));
            setRequirement(rs.getString(REQUIREMENT));
            setCompleteTime(rs.getString(COMPLETETIME));
            setRelatedOrder(rs.getString(RELATEDORDER));
            setDealStatus(rs.getString(DEALSTATUS));
            setSendStatus(rs.getString(SENDSTATUS));
            setReceiveStatus(rs.getString(RECEIVESTATUS));
            setTaskTime(rs.getTimestamp(TASKTIME));
            setRemarkGyy(rs.getString(REMARKGYY));
        }
        return this;
    }

    @Override
    public Object getKeyId() {
        return this.keyId;
    }

    @Override
    public Map getUpdateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(ANALYSISNUMBER, analysisNumber);
        ret.put(VEROID, verOid);
        ret.put(DATATYPE, dataType);
        ret.put(AFFECTED, affected);
        ret.put(AFFECTEDGYY, affectedGyy);
        ret.put(RESPONSER, responser);
        ret.put(UNIT, unit);
        ret.put(PRODUCT, product);
        ret.put(COUNT, count);
        ret.put(REPAIRCOUNT, repaircount);
        ret.put(ZJWXCOUNT, zjwxcount);
        ret.put(REQUIREMENT, requirement);
        ret.put(COMPLETETIME, completeTime);
        ret.put(RELATEDORDER, relatedOrder);
        ret.put(DEALSTATUS, dealStatus);
        ret.put(SENDSTATUS, sendStatus);
        ret.put(RECEIVESTATUS, receiveStatus);
        ret.put(TASKTIME, taskTime);
        ret.put(REMARKGYY, remarkGyy);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(ANALYSISNUMBER, analysisNumber);
        ret.put(VEROID, verOid);
        ret.put(DATATYPE, dataType);
        ret.put(AFFECTED, affected);
        ret.put(AFFECTEDGYY, affectedGyy);
        ret.put(RESPONSER, responser);
        ret.put(UNIT, unit);
        ret.put(PRODUCT, product);
        ret.put(COUNT, count);
        ret.put(REPAIRCOUNT, repaircount);
        ret.put(ZJWXCOUNT, zjwxcount);
        ret.put(REQUIREMENT, requirement);
        ret.put(COMPLETETIME, completeTime);
        ret.put(RELATEDORDER, relatedOrder);
        ret.put(DEALSTATUS, dealStatus);
        ret.put(SENDSTATUS, sendStatus);
        ret.put(RECEIVESTATUS, receiveStatus);
        ret.put(TASKTIME, taskTime);
        ret.put(REMARKGYY, remarkGyy);
        return ret;
    }

    @Override
    public String toString() {
        return "AnalysisObjEntry{" +
                "keyId='" + keyId + '\'' +
                ", analysisNumber='" + analysisNumber + '\'' +
                ", verOid='" + verOid + '\'' +
                ", dataType='" + dataType + '\'' +
                ", affected='" + affected + '\'' +
                ", affectedGyy='" + affectedGyy + '\'' +
                ", responser='" + responser + '\'' +
                ", unit='" + unit + '\'' +
                ", product='" + product + '\'' +
                ", count='" + count + '\'' +
                ", repaircount='" + repaircount + '\'' +
                ", zjwxcount='" + zjwxcount + '\'' +
                ", requirement='" + requirement + '\'' +
                ", completeTime='" + completeTime + '\'' +
                ", relatedOrder='" + relatedOrder + '\'' +
                ", dealStatus='" + dealStatus + '\'' +
                ", sendStatus='" + sendStatus + '\'' +
                ", receiveStatus='" + receiveStatus + '\'' +
                ", taskTime='" + taskTime + '\'' +
                ", remarkGyy='" + remarkGyy + '\'' +
                '}';
    }
}
