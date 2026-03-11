package ext.casc.change.bean;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLEcnCollectData implements CmPersistable {
    public static final String PARENTDOCOID = "parentDocOid";
    public static final String CHILDDOCOID = "childDocOid";
    private String keyId = "";
    private String parentDocOid = "";
    private String childDocOid = "";

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    @Override
    public Object getKeyId() {
        return this.keyId;
    }

    public String getParentDocOid() {
        return parentDocOid;
    }

    public void setParentDocOid(String parentDocOid) {
        this.parentDocOid = parentDocOid;
    }

    public String getChildDocOid() {
        return childDocOid;
    }

    public void setChildDocOid(String childDocOid) {
        this.childDocOid = childDocOid;
    }

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setParentDocOid(rs.getString(PARENTDOCOID));
            setChildDocOid(rs.getString(CHILDDOCOID));
        }
        return this;
    }



    @Override
    public Map getUpdateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(PARENTDOCOID, parentDocOid);
        ret.put(CHILDDOCOID, childDocOid);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(PARENTDOCOID, parentDocOid);
        ret.put(CHILDDOCOID, childDocOid);
        return ret;
    }
}
