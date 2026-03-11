package ext.casc.nc.bean;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLMaterialQuotaRecord implements CmPersistable {
    public GLMaterialQuotaRecord() {
    }
    private static final long serialVersionUID = 1L;
    public static final String DOCNUMBER = "docNumber";//工艺流水号
    public static final String VERSION = "version";//版本
    private String keyId = "";
    private String docNumber ="";
    private String version="";

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setDocNumber(rs.getString(DOCNUMBER));
            setVersion(rs.getString(VERSION));
        }
        return this;
    }

    @Override
    public Object getKeyId() {
        return this.keyId;
    }


    @Override
    public Map getUpdateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(DOCNUMBER, docNumber);
        ret.put(VERSION, version);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(DOCNUMBER, docNumber);
        ret.put(VERSION, version);
        return ret;
    }
}
