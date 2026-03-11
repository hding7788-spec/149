package ext.casc.mpm.process;

import com.ptc.netmarkets.model.NmObject;
import com.ptc.netmarkets.model.NmSimpleOid;
import ext.sast.common.fc.CmPersistable;
import wt.util.WTException;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 工艺参数
 * 基础参数、枚举参数、知识参数都用此表存储
 */
public class GLProcessParams implements CmPersistable {
    public GLProcessParams() {
    }

    private static final long serialVersionUID = 4007533850133929576L;

    public static final String GYNUMBER = "gyNumber";
    public static final String GYNAME = "gyName";
    public static final String PARAMETER_CATEGORY = "parameterCategory";
    public static final String UNIT = "unit";
    public static final String PROCESS_CATEGORY = "processCategory";
    public static final String ENUM_VALUES = "enumValues";
    public static final String KNOWLEDGE_INFERENCE_PARA = "knowledgeInferencePara";
    public static final String KNOWLEDGE_OUTPUT_PARA = "knowledgeOutputPara";
    public static final String OUTPUT_RULES = "outputRules";
    public static final String KNOWLEDGETYPE = "knowledgeType";
    public static final String ISCANZHUANG = "isCanZhuang";
    public static final String ISONLYVALUE = "isOnlyValue";

    public static final String SOURCE = "source";

    private String keyId = "";
    private String gyNumber = "";           // 编号 (改为String类型)
    private String gyName = "";            // 名称
    private String parameterCategory = "";// 参数类别
    private String unit = "";// 计量单位
    private String processCategory = ""; // 工艺类别
    private String enumValues = "";    // 枚举值
    private String knowledgeInferencePara = "";// 知识推理参数 (改为String类型)
    private String knowledgeOutputPara = "";   // 知识输出参数 (改为String类型)
    private String outputRules = "";         // 输出规则
    private String knowledgeType = "";         // 知识类型
    private String isCanZhuang = "";         // 是否参装
    private String isOnlyValue = "";         // 是否值输出值

    public String getIsOnlyValue() {
        return isOnlyValue;
    }

    public void setIsOnlyValue(String isOnlyValue) {
        this.isOnlyValue = isOnlyValue;
    }

    public String getIsCanZhuang() {
        return isCanZhuang;
    }

    public void setIsCanZhuang(String isCanZhuang) {
        this.isCanZhuang = isCanZhuang;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private String source = "";         // 知识类型

    public String getKnowledgeType() {
        return knowledgeType;
    }

    public void setKnowledgeType(String knowledgeType) {
        this.knowledgeType = knowledgeType;
    }

    @Override
    public GLProcessParams getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setGyNumber(rs.getString(GYNUMBER));
            setGyName(rs.getString(GYNAME));
            setUnit(rs.getString(UNIT));
            setParameterCategory(rs.getString(PARAMETER_CATEGORY));
            setProcessCategory(rs.getString(PROCESS_CATEGORY));
            setEnumValues(rs.getString(ENUM_VALUES));
            setKnowledgeInferencePara(rs.getString(KNOWLEDGE_INFERENCE_PARA));
            setKnowledgeOutputPara(rs.getString(KNOWLEDGE_OUTPUT_PARA));
            setOutputRules(rs.getString(OUTPUT_RULES));
            setKnowledgeType(rs.getString(KNOWLEDGETYPE));
            setIsCanZhuang(rs.getString(ISCANZHUANG));
            setIsOnlyValue(rs.getString(ISONLYVALUE));
            setSource(rs.getString(SOURCE));
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
        ret.put(GYNUMBER, gyNumber);
        ret.put(GYNAME, gyName);
        ret.put(UNIT, unit);
        ret.put(PARAMETER_CATEGORY, parameterCategory);
        ret.put(PROCESS_CATEGORY, processCategory);
        ret.put(ENUM_VALUES, enumValues);
        ret.put(KNOWLEDGE_INFERENCE_PARA, knowledgeInferencePara);
        ret.put(KNOWLEDGE_OUTPUT_PARA, knowledgeOutputPara);
        ret.put(OUTPUT_RULES, outputRules);
        ret.put(KNOWLEDGETYPE, knowledgeType);
        ret.put(ISCANZHUANG, isCanZhuang);
        ret.put(ISONLYVALUE, isOnlyValue);
        ret.put(SOURCE, source);
        return ret;
    }

    @Override
    public Map<String, Object> getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(GYNUMBER, gyNumber);
        ret.put(GYNAME, gyName);
        ret.put(UNIT, unit);
        ret.put(PARAMETER_CATEGORY, parameterCategory);
        ret.put(PROCESS_CATEGORY, processCategory);
        ret.put(ENUM_VALUES, enumValues);
        ret.put(KNOWLEDGE_INFERENCE_PARA, knowledgeInferencePara);
        ret.put(KNOWLEDGE_OUTPUT_PARA, knowledgeOutputPara);
        ret.put(OUTPUT_RULES, outputRules);
        ret.put(KNOWLEDGETYPE, knowledgeType);
        ret.put(ISCANZHUANG, isCanZhuang);
        ret.put(ISONLYVALUE, isOnlyValue);
        ret.put(SOURCE, source);
        return ret;
    }

    @Override
    public String toString() {
        return "GLProcessParams{" +
                "keyId='" + keyId + '\'' +
                ", gyNumber='" + gyNumber + '\'' +
                ", gyName='" + gyName + '\'' +
                ", parameterCategory='" + parameterCategory + '\'' +
                ", unit='" + unit + '\'' +
                ", processCategory='" + processCategory + '\'' +
                ", enumValues='" + enumValues + '\'' +
                ", knowledgeInferencePara='" + knowledgeInferencePara + '\'' +
                ", knowledgeOutputPara='" + knowledgeOutputPara + '\'' +
                ", outputRules='" + outputRules + '\'' +
                '}';
    }

    public String getGyNumber() {
        return gyNumber;
    }

    public void setGyNumber(String gyNumber) {
        this.gyNumber = gyNumber;
    }

    public String getGyName() {
        return gyName;
    }

    public void setGyName(String gyName) {
        this.gyName = gyName;
    }

    public String getParameterCategory() {
        return parameterCategory;
    }

    public void setParameterCategory(String parameterCategory) {
        this.parameterCategory = parameterCategory;
    }

    public String getProcessCategory() {
        return processCategory;
    }

    public void setProcessCategory(String processCategory) {
        this.processCategory = processCategory;
    }

    public String getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(String enumValues) {
        this.enumValues = enumValues;
    }

    public String getKnowledgeInferencePara() {
        return knowledgeInferencePara;
    }

    public void setKnowledgeInferencePara(String knowledgeInferencePara) {
        this.knowledgeInferencePara = knowledgeInferencePara;
    }

    public String getKnowledgeOutputPara() {
        return knowledgeOutputPara;
    }

    public void setKnowledgeOutputPara(String knowledgeOutputPara) {
        this.knowledgeOutputPara = knowledgeOutputPara;
    }

    public String getOutputRules() {
        return outputRules;
    }

    public void setOutputRules(String outputRules) {
        this.outputRules = outputRules;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getUnit() {
        return unit == null ? "" : unit.trim();
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String getOid() {
        return this.gyNumber;
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

}

