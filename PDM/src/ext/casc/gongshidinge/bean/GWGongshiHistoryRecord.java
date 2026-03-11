package ext.casc.gongshidinge.bean;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GWGongshiHistoryRecord implements CmPersistable {

    public GWGongshiHistoryRecord() {
        this.keyId = UUID.randomUUID().toString();
    }

    private static final long serialVersionUID = -6475172606280537274L;

    public static final String VEROID = "verOid";
    public static final String TECNUMBER = "tecNumber";
    public static final String TECNAME = "tecName";
    public static final String STEPID = "stepId";
    public static final String STEPNUMBER = "stepNumber";
    public static final String STEPNAME = "stepName";
    public static final String ZHUNJIE = "zhunjie";
    public static final String DANJIAN = "danjian";
    public static final String DANJIANSHEBEIGS = "danJianSheBeiGS";
    public static final String CREATOR = "creator";
    public static final String CREATETIME = "createTimeStamp";

    private String keyId = "";
    private String verOid = "";
    private String tecNumber = "";
    private String tecName = "";
    private String stepId = "";
    private String stepNumber = "";
    private String stepName = "";
    private String zhunjie = "";
    private String danjian = "";
    private String danJianSheBeiGS = "";
    private String creator = "";
    private Timestamp createTimeStamp;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getVerOid() {
        return verOid;
    }

    public void setVerOid(String verOid) {
        this.verOid = verOid;
    }

    public String getTecNumber() {
        return tecNumber;
    }

    public void setTecNumber(String tecNumber) {
        this.tecNumber = tecNumber;
    }

    public String getTecName() {
        return tecName;
    }

    public void setTecName(String tecName) {
        this.tecName = tecName;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getZhunjie() {
        return zhunjie;
    }

    public void setZhunjie(String zhunjie) {
        this.zhunjie = zhunjie;
    }

    public String getDanjian() {
        return danjian;
    }

    public void setDanjian(String danjian) {
        this.danjian = danjian;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDanJianSheBeiGS() {
        return danJianSheBeiGS == null ? "" : danJianSheBeiGS.trim();
    }

    public void setDanJianSheBeiGS(String danJianSheBeiGS) {
        this.danJianSheBeiGS = danJianSheBeiGS;
    }

    public Timestamp getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(Timestamp createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if(rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setVerOid(rs.getString(VEROID));
            setTecNumber(rs.getString(TECNUMBER));
            setTecName(rs.getString(TECNAME));
            setStepId(rs.getString(STEPID));
            setStepNumber(rs.getString(STEPNUMBER));
            setStepName(rs.getString(STEPNAME));
            setZhunjie(rs.getString(ZHUNJIE));
            setDanjian(rs.getString(DANJIAN));
            setDanJianSheBeiGS(rs.getString(DANJIANSHEBEIGS));
            setCreator(rs.getString(CREATOR));
            setCreateTimeStamp(rs.getTimestamp(CREATETIME));
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
        ret.put(VEROID, verOid);
        ret.put(TECNUMBER, tecNumber);
        ret.put(TECNAME, tecName);
        ret.put(STEPID, stepId);
        ret.put(STEPNUMBER, stepNumber);
        ret.put(STEPNAME, stepName);
        ret.put(ZHUNJIE, zhunjie);
        ret.put(DANJIAN, danjian);
        ret.put(DANJIANSHEBEIGS, danJianSheBeiGS);
        ret.put(CREATOR, creator);
        ret.put(CREATETIME, createTimeStamp);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(VEROID, verOid);
        ret.put(TECNUMBER, tecNumber);
        ret.put(TECNAME, tecName);
        ret.put(STEPID, stepId);
        ret.put(STEPNUMBER, stepNumber);
        ret.put(STEPNAME, stepName);
        ret.put(ZHUNJIE, zhunjie);
        ret.put(DANJIAN, danjian);
        ret.put(DANJIANSHEBEIGS, danJianSheBeiGS);
        ret.put(CREATOR, creator);
        ret.put(CREATETIME, createTimeStamp);
        return ret;
    }

    @Override
    public String toString() {
        return "GWDealProductRecord{" +
                "keyId='" + keyId + '\'' +
                ", verOid='" + verOid + '\'' +
                ", tecNumber='" + tecNumber + '\'' +
                ", tecName='" + tecName + '\'' +
                ", stepId='" + stepId + '\'' +
                ", stepNumber='" + stepNumber + '\'' +
                ", stepName='" + stepName + '\'' +
                ", zhunjie='" + zhunjie + '\'' +
                ", danJianSheBeiGS='" + danJianSheBeiGS + '\'' +
                ", danjian='" + danjian + '\'' +
                ", creator='" + creator + '\'' +
                ", createTimeStamp='" + createTimeStamp + '\'' +
                '}';
    }
}
