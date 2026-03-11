package ext.casc.mpm.process;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 工艺模板 与 参数之间关系的类
 */
public class GLProcessParamDefinition implements CmPersistable {

    private String keyId;
    private String templateId;   //模板ID
    private String gyParamName ="";  //工艺参数名字
    private String gyParamNumber ="";  //工艺参数名字
    private String gyParamType="";  //工艺参数类型,比如是text，还是comboBox
    private String enumValues ="";
    private String sjParamName =""; //设计参数名字

    public static final String TEMPLATEID = "templateId";
    public static final String GYPARAMNAME = "gyParamName";
    public static final String GYPARAMNUMBER = "gyParamNumber";
    public static final String GYPARAMTYPE  = "gyParamType";
    public static final String ENUMVALUES = "enumValues";
    public static final String SJPARAMNAME = "sjParamName";

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setTemplateId(rs.getString(TEMPLATEID));
            setGyParamName(rs.getString(GYPARAMNAME));
            setGyParamNumber(rs.getString(GYPARAMNUMBER));
            setGyParamType(rs.getString(GYPARAMTYPE));
            setEnumValues(rs.getString(ENUMVALUES));
            setSjParamName(rs.getString(SJPARAMNAME));
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
        ret.put(TEMPLATEID, templateId);
        ret.put(GYPARAMNAME, gyParamName);
        ret.put(GYPARAMNUMBER, gyParamNumber);
        ret.put(GYPARAMTYPE, gyParamType);
        ret.put(ENUMVALUES, enumValues);
        ret.put(SJPARAMNAME, sjParamName);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(TEMPLATEID, templateId);
        ret.put(GYPARAMNAME, gyParamName);
        ret.put(GYPARAMNUMBER, gyParamNumber);
        ret.put(GYPARAMTYPE, gyParamType);
        ret.put(ENUMVALUES, enumValues);
        ret.put(SJPARAMNAME, sjParamName);
        return ret;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getGyParamName() {
        return gyParamName;
    }

    public void setGyParamName(String gyParamName) {
        this.gyParamName = gyParamName;
    }

    public String getGyParamNumber() {
        return gyParamNumber;
    }

    public void setGyParamNumber(String gyParamNumber) {
        this.gyParamNumber = gyParamNumber;
    }

    public String getGyParamType() {
        return gyParamType;
    }

    public void setGyParamType(String gyParamType) {
        this.gyParamType = gyParamType;
    }

    public String getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(String enumValues) {
        this.enumValues = enumValues;
    }

    public String getSjParamName() {
        return sjParamName;
    }

    public void setSjParamName(String sjParamName) {
        this.sjParamName = sjParamName;
    }
}
