package ext.casc.analysisActivity.bean;

import cn.hutool.core.util.StrUtil;
import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GWDealProductRecord implements CmPersistable {


    public GWDealProductRecord() {
        this.keyId = UUID.randomUUID().toString();
    }

    private static final long serialVersionUID = -1011768284026570719L;

    public static final String SOURCE = "source";
    public static final String ANALYSISNUMBER = "analysisNumber";
    public static final String VEROID = "verOid";
    public static final String DEALTYPE = "dealType";
    public static final String COUNT = "count";
    public static final String REPAIRCOUNT = "repairCount";
    public static final String CARD = "card";
    public static final String CODE = "code";
    public static final String DOCNUMBER = "docNumber";
    public static final String DOCVERSION = "docVersion";
    public static final String COMMENTS = "comments";
    public static final String RESPONSER = "responser";
    public static final String RESPONSIBLEUNIT = "responsibleUnit";
    public static final String STATUS = "status";
    public static final String TASKSTATUS = "taskStatus";
    public static final String FINISHTIME = "finishTime";
    public static final String CREATETIME = "createTimeStamp";
    public static final String UPDATETIME = "updateTimeStamp";

    private String keyId = "";
    private String source = "";
    private String analysisNumber = "";
    private String verOid = "";
    private String dealType = "";
    private Integer count = 0;
    private Integer repairCount = 0;
    private String card = "";
    private String code = "";
    private String docNumber = "";
    private String docVersion = "";
    private String comments = "";
    private String responser = "";
    private String responsibleUnit = "";
    private String status = "";
    private String taskStatus = "";
    private String finishTime = "";
    private Timestamp createTimeStamp;
    private Timestamp updateTimeStamp;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAnalysisNumber() {
        return analysisNumber;
    }

    public void setAnalysisNumber(String analysisNumber) {
        this.analysisNumber = analysisNumber;
    }

    public String getVerOid() {
        return verOid;
    }

    public void setVerOid(String verOid) {
        this.verOid = verOid;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public Integer getCount() {
        return count == null ? 0 : count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getRepairCount() {
        return repairCount == null ? 0 : repairCount;
    }

    public void setRepairCount(Integer repairCount) {
        this.repairCount = repairCount;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(String docVersion) {
        this.docVersion = docVersion;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getResponser() {
        return responser;
    }

    public void setResponser(String responser) {
        this.responser = responser;
    }

    public String getResponsibleUnit() {
        return responsibleUnit;
    }

    public void setResponsibleUnit(String responsibleUnit) {
        this.responsibleUnit = responsibleUnit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getFinishTime() {
        return StrUtil.isEmpty(finishTime) ? "" : finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Timestamp getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(Timestamp createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if(rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setSource(rs.getString(SOURCE));
            setAnalysisNumber(rs.getString(ANALYSISNUMBER));
            setVerOid(rs.getString(VEROID));
            setDealType(rs.getString(DEALTYPE));
            setCount(rs.getInt(COUNT));
            setRepairCount(rs.getInt(REPAIRCOUNT));
            setCard(rs.getString(CARD));
            setCode(rs.getString(CODE));
            setDocNumber(rs.getString(DOCNUMBER));
            setDocVersion(rs.getString(DOCVERSION));
            setComments(rs.getString(COMMENTS));
            setResponser(rs.getString(RESPONSER));
            setResponsibleUnit(rs.getString(RESPONSIBLEUNIT));
            setStatus(rs.getString(STATUS));
            setTaskStatus(rs.getString(TASKSTATUS));
            setFinishTime(rs.getString(FINISHTIME));
            setCreateTimeStamp(rs.getTimestamp(CREATETIME));
            setUpdateTimeStamp(rs.getTimestamp(UPDATETIME));
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
        ret.put(SOURCE, source);
        ret.put(ANALYSISNUMBER, analysisNumber);
        ret.put(VEROID, verOid);
        ret.put(DEALTYPE, dealType);
        ret.put(COUNT, count);
        ret.put(REPAIRCOUNT, repairCount);
        ret.put(CARD, card);
        ret.put(CODE, code);
        ret.put(DOCNUMBER, docNumber);
        ret.put(DOCVERSION, docVersion);
        ret.put(COMMENTS, comments);
        ret.put(RESPONSER, responser);
        ret.put(RESPONSIBLEUNIT, responsibleUnit);
        ret.put(STATUS, status);
        ret.put(TASKSTATUS, taskStatus);
        ret.put(FINISHTIME, finishTime);
        ret.put(CREATETIME, createTimeStamp);
        ret.put(UPDATETIME, updateTimeStamp);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(SOURCE, source);
        ret.put(ANALYSISNUMBER, analysisNumber);
        ret.put(VEROID, verOid);
        ret.put(DEALTYPE, dealType);
        ret.put(COUNT, count);
        ret.put(REPAIRCOUNT, repairCount);
        ret.put(CARD, card);
        ret.put(CODE, code);
        ret.put(DOCNUMBER, docNumber);
        ret.put(DOCVERSION, docVersion);
        ret.put(COMMENTS, comments);
        ret.put(RESPONSER, responser);
        ret.put(RESPONSIBLEUNIT, responsibleUnit);
        ret.put(STATUS, status);
        ret.put(TASKSTATUS, taskStatus);
        ret.put(FINISHTIME, finishTime);
        ret.put(CREATETIME, createTimeStamp);
        ret.put(UPDATETIME, updateTimeStamp);
        return ret;
    }

    @Override
    public String toString() {
        return "GWDealProductRecord{" +
                "keyId='" + keyId + '\'' +
                ", source='" + source + '\'' +
                ", analysisNumber='" + analysisNumber + '\'' +
                ", verOid='" + verOid + '\'' +
                ", dealType='" + dealType + '\'' +
                ", count='" + count + '\'' +
                ", repairCount='" + repairCount + '\'' +
                ", card='" + card + '\'' +
                ", code='" + code + '\'' +
                ", docNumber='" + docNumber + '\'' +
                ", docVersion='" + docVersion + '\'' +
                ", comments='" + comments + '\'' +
                ", responser='" + responser + '\'' +
                ", responsibleUnit='" + responsibleUnit + '\'' +
                ", status='" + status + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", createTimeStamp='" + createTimeStamp + '\'' +
                ", updateTimeStamp='" + updateTimeStamp + '\'' +
                '}';
    }
}
