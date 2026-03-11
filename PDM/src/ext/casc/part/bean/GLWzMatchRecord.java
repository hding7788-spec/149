package ext.casc.part.bean;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLWzMatchRecord implements CmPersistable {
    public GLWzMatchRecord() {
    }

    private static final long serialVersionUID = 4007533850133929576L;

    public static final String DOCOID = "docOid";
    public static final String DOCNUMBER = "docNumber";
    public static final String DOCVERSION = "docVersion";
    public static final String MATCHWZNUMBER = "matchWZNumber";
    public static final String GONGYIWZNUMBER = "gongYiWZNumber";
    public static final String ORGWZNUMBER = "orgWZNumber";
    public static final String CREATOR = "creator";
    public static final String SYNCHTIME = "synchtime";


    private String keyId = "";
    private String docOid;
    private String docNumber;
    private String docVersion;
    private String matchWZNumber;
    private String gongYiWZNumber;
    private String orgWZNumber;
    private String creator;
    private String synchtime;


    @Override
    public GLWzMatchRecord getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setDocOid(rs.getString(DOCOID));
            setDocNumber(rs.getString(DOCNUMBER));
            setDocVersion(rs.getString(DOCVERSION));
            setMatchWZNumber(rs.getString(MATCHWZNUMBER));
            setGongYiWZNumber(rs.getString(GONGYIWZNUMBER));
            setOrgWZNumber(rs.getString(ORGWZNUMBER));
            setCreator(rs.getString(CREATOR));
            setSynchtime(rs.getString(SYNCHTIME));

        }
        return this;
    }


    @Override
    public Object getKeyId() {
        return this.keyId;
    }

    @Override
    public Map<String, Object> getUpdateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(DOCOID, docOid);
        ret.put(DOCNUMBER, docNumber);
        ret.put(DOCVERSION, docVersion);
        ret.put(MATCHWZNUMBER, matchWZNumber);
        ret.put(GONGYIWZNUMBER, gongYiWZNumber);
        ret.put(ORGWZNUMBER, orgWZNumber);
        ret.put(CREATOR, creator);
        ret.put(SYNCHTIME, synchtime);

        return ret;
    }

    @Override
    public Map<String, Object> getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(DOCOID, docOid);
        ret.put(DOCNUMBER, docNumber);
        ret.put(DOCVERSION, docVersion);
        ret.put(MATCHWZNUMBER, matchWZNumber);
        ret.put(GONGYIWZNUMBER, gongYiWZNumber);
        ret.put(ORGWZNUMBER, orgWZNumber);
        ret.put(CREATOR, creator);
        ret.put(SYNCHTIME, synchtime);
        return ret;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getMatchWZNumber() {
        return matchWZNumber;
    }

    public void setMatchWZNumber(String matchWZNumber) {
        this.matchWZNumber = matchWZNumber;
    }

    @Override
    public String toString() {
        return "GLWzMatchRecord{" +
                "keyId='" + keyId + '}';
    }

    public String getDocOid() {
        return docOid;
    }

    public void setDocOid(String docOid) {
        this.docOid = docOid;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getGongYiWZNumber() {
        return gongYiWZNumber;
    }

    public void setGongYiWZNumber(String gongYiWZNumber) {
        this.gongYiWZNumber = gongYiWZNumber;
    }

    public String getOrgWZNumber() {
        return orgWZNumber;
    }

    public void setOrgWZNumber(String orgWZNumber) {
        this.orgWZNumber = orgWZNumber;
    }

    public String getSynchtime() {
        return synchtime;
    }

    public void setSynchtime(String synchtime) {
        this.synchtime = synchtime;
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
}

