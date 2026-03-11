package ext.casc.workflow.tree;

import cn.hutool.core.util.StrUtil;
import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GWActivityRecord implements CmPersistable {


    public GWActivityRecord() {
        this.keyId = UUID.randomUUID().toString();
    }

    private static final long serialVersionUID = -1011768284026570719L;

    public static final String WORKITEMOID = "workitemOid";
    public static final String VEROID = "verOid";
    public static final String RESULT = "result";
    public static final String ADVISE = "advise";

    private String keyId = "";
    private String workitemOid = "";
    private String verOid = "";
    private String result = "";
    private String advise = "";

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getWorkitemOid() {
        return workitemOid;
    }

    public void setWorkitemOid(String workitemOid) {
        this.workitemOid = workitemOid;
    }

    public String getVerOid() {
        return verOid;
    }

    public void setVerOid(String verOid) {
        this.verOid = verOid;
    }

    public String getResult() {
        return StrUtil.isEmpty(result) ? "" : result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAdvise() {
        return StrUtil.isEmpty(advise) ? "" : advise;
    }

    public void setAdvise(String advise) {
        this.advise = advise;
    }

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if(rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setWorkitemOid(rs.getString(WORKITEMOID));
            setVerOid(rs.getString(VEROID));
            setResult(rs.getString(RESULT));
            setAdvise(rs.getString(ADVISE));
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
        ret.put(WORKITEMOID, workitemOid);
        ret.put(VEROID, verOid);
        ret.put(RESULT, result);
        ret.put(ADVISE, advise);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(WORKITEMOID, workitemOid);
        ret.put(VEROID, verOid);
        ret.put(RESULT, result);
        ret.put(ADVISE, advise);
        return ret;
    }

    @Override
    public String toString() {
        return "GWDealProductRecord{" +
                "keyId='" + keyId + '\'' +
                ", workitemOid='" + workitemOid + '\'' +
                ", verOid='" + verOid + '\'' +
                ", result='" + result + '\'' +
                ", advise='" + advise + '\'' +
                '}';
    }
}
