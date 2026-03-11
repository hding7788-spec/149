package ext.casc.system;

import com.ptc.netmarkets.model.NmObject;
import com.ptc.netmarkets.model.NmSimpleOid;
import ext.sast.common.fc.CmPersistable;
import wt.util.WTException;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SystemConfigurationBean implements CmPersistable {

    public SystemConfigurationBean() {
    }

    public SystemConfigurationBean(String key, String value, String remark) {
        this.keyId = UUID.randomUUID().toString();
        this.key = key;
        this.value = value;
        this.remark = remark;
    }

    private static final long serialVersionUID = -4982264816403500454L;
    //配置名称
    public static final String KEY = "key";
    //配置值
    public static final String VALUE = "value";
    //备注
    public static final String REMARK = "remark";

    private String keyId = "";
    private String key = "";
    private String value = "";
    private String remark = "";

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if(rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setKey(rs.getString(KEY));
            setValue(rs.getString(VALUE));
            setRemark(rs.getString(REMARK));
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
        ret.put(KEY, key);
        ret.put(VALUE, value);
        ret.put(REMARK, remark);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(KEY, key);
        ret.put(VALUE, value);
        ret.put(REMARK, remark);
        return ret;
    }

    @Override
    public String toString() {
        return "SystemConfigurationBean{" +
                "keyId='" + keyId + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    public static String getBusinessObjectReferenceFromOid(String s) {
        String s1 = null;
        if (s.contains("_")) {
            int i = s.indexOf("_");
            if (i != -1)
                s1 = s.substring(0, i);
        } else {
            s1 = s;
        }
        return s1;
    }

    public NmObject getNmObject() throws WTException {
        NmSimpleOid nmsimpleoid = new NmSimpleOid();
        nmsimpleoid.setInternalName(getOid());
        return NmObject.newNmObject(nmsimpleoid);
    }

    public String getOid() {
        return this.key;
    }
}
