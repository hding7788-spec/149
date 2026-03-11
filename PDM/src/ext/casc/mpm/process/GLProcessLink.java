package ext.casc.mpm.process;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 存放只是参数与工艺知识的关联关系，包括sheet中的表头
 */
public class GLProcessLink implements CmPersistable {
    public GLProcessLink() {

    }

    private String keyId = "";
    private String paraName = "";  //知识参数名字
    private String heads = "";       //表头,记录Excel的表头，以英文逗号相隔

    public static final String PARANAME = "paraName";
    public static final String HEADS = "heads";

    @Override
    public GLProcessLink getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setParaName(rs.getString(PARANAME));
            setHeads(rs.getString(HEADS));
        }
        return this;
    }


    @Override
    public Map<String, Object> getUpdateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(PARANAME, paraName);
        ret.put(HEADS, heads);
        return ret;
    }

    @Override
    public Map<String, Object> getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(PARANAME, paraName);
        ret.put(HEADS, heads);
        return ret;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    @Override
    public Object getKeyId() {
        return this.keyId;
    }

    public String getParaName() {
        return paraName;
    }

    public void setParaName(String paraName) {
        this.paraName = paraName;
    }

    public String getHeads() {
        return heads;
    }

    public void setHeads(String heads) {
        this.heads = heads;
    }
}

