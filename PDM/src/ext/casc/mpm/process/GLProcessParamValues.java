package ext.casc.mpm.process;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 部件的工艺参数值
 */
public class GLProcessParamValues implements CmPersistable {

    private static final long serialVersionUID = 1L;

    private String keyId = "";
    private String templateId = "";
    private String templateName = "";
    private String cadName = "";
    private String cadVersion = "";
    private String partNumber = "";
    private String partVersion = "";
    private String gyParamName = "";
    private String gyParamNumber = "";
    private String sjParamName = "";
    private String paramValue = "";
    private String paramUnit = "";
    private String dataFrom = "";

    private String gongChengZhi = ""; //  公称值
    private String shangPianCha = ""; // 上偏差
    private String xiaPianCha = ""; // 下偏差
    private String fuHao = ""; // 符号
    private String jiZhun1 = ""; // 基准1
    private String jiZhun2 = ""; // 基准2
    private String jiZhun3 = ""; // 基准3

    public static final String TEMPLATE_ID = "templateId"; //工艺模板ID
    public static final String TEMPLATE_NAME = "templateName"; //工艺模板名称
    public static final String CAD_NAME = "cadName";       //CAD名称
    public static final String CAD_VERSION = "cadVersion";  //CAD版本
    public static final String PART_NUMBER = "partNumber"; //部件编号
    public static final String PART_VERSION = "partVersion"; //部件版本
    public static final String GY_PARAM_NAME = "gyParamName"; //工艺参数名称
    public static final String GY_PARAM_NUMBER = "gyParamNumber"; //工艺参数编号

    public static final String SJ_PARAM_NAME = "sjParamName"; //设计参数名称
    public static final String PARAM_VALUE = "paramValue";  //参数值
    public static final String PARAM_UNIT = "paramUnit";    //参数单位
    public static final String DATA_FROM = "dataFrom";     //数据来源

    public static final String GONG_CHENG_ZHI = "gongChengZhi"; // 公称值
    public static final String SHANG_PIAN_CHA = "shangPianCha"; // 上偏差
    public static final String XIA_PIAN_CHA = "xiaPianCha"; // 下偏差
    public static final String FU_HAO = "fuHao"; // 符号
    public static final String JI_ZHUN1 = "jiZhun1"; // 基准1
    public static final String JI_ZHUN2 = "jiZhun2"; // 基准2
    public static final String JI_ZHUN3 = "jiZhun3"; // 基准3

    public GLProcessParamValues() {

    }

    public GLProcessParamValues(String keyId, String templateId, String templateName, String cadName, String cadVersion, String partNumber, String partVersion, String gyParamName, String gyParamNumber) {
        this.keyId = keyId;
        this.templateId = templateId;
        this.templateName = templateName;
        this.cadName = cadName;
        this.cadVersion = cadVersion;
        this.partNumber = partNumber;
        this.partVersion = partVersion;
        this.gyParamName = gyParamName;
        this.gyParamNumber = gyParamNumber;

    }


    public GLProcessParamValues(String keyId, String templateId, String templateName, String cadName, String cadVersion, String partNumber, String partVersion, String gyParamName, String gyParamNumber, String sjParamName, String paramValue, String paramUnit, String dataFrom, String gongChengZhi, String shangPianCha, String xiaPianCha, String fuHao, String jiZhun1, String jiZhun2, String jiZhun3) {
        this.keyId = keyId;
        this.templateId = templateId;
        this.templateName = templateName;
        this.cadName = cadName;
        this.cadVersion = cadVersion;
        this.partNumber = partNumber;
        this.partVersion = partVersion;
        this.gyParamName = gyParamName;
        this.gyParamNumber = gyParamNumber;
        this.sjParamName = sjParamName;
        this.paramValue = paramValue;
        this.paramUnit = paramUnit;
        this.dataFrom = dataFrom;
        this.gongChengZhi = gongChengZhi;
        this.shangPianCha = shangPianCha;
        this.xiaPianCha = xiaPianCha;
        this.fuHao = fuHao;
        this.jiZhun1 = jiZhun1;
        this.jiZhun2 = jiZhun2;
        this.jiZhun3 = jiZhun3;
    }

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setTemplateId(rs.getString(TEMPLATE_ID));
            setTemplateName(rs.getString(TEMPLATE_NAME));
            setCadName(rs.getString(CAD_NAME));
            setCadVersion(rs.getString(CAD_VERSION));
            setPartNumber(rs.getString(PART_NUMBER));
            setPartVersion(rs.getString(PART_VERSION));
            setGyParamName(rs.getString(GY_PARAM_NAME));
            setGyParamNumber(rs.getString(GY_PARAM_NUMBER));
            setSjParamName(rs.getString(SJ_PARAM_NAME));
            setParamValue(rs.getString(PARAM_VALUE));
            setParamUnit(rs.getString(PARAM_UNIT));
            setDataFrom(rs.getString(DATA_FROM));
            setGongChengZhi(rs.getString(GONG_CHENG_ZHI));
            setShangPianCha(rs.getString(SHANG_PIAN_CHA));
            setXiaPianCha(rs.getString(XIA_PIAN_CHA));
            setFuHao(rs.getString(FU_HAO));
            setJiZhun1(rs.getString(JI_ZHUN1));
            setJiZhun2(rs.getString(JI_ZHUN2));
            setJiZhun3(rs.getString(JI_ZHUN2));
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
        ret.put(TEMPLATE_ID, templateId);
        ret.put(TEMPLATE_NAME, templateName);
        ret.put(CAD_NAME, cadName);
        ret.put(CAD_VERSION, cadVersion);
        ret.put(PART_NUMBER, partNumber);
        ret.put(PART_VERSION, partVersion);
        ret.put(GY_PARAM_NAME, gyParamName);
        ret.put(GY_PARAM_NUMBER, gyParamNumber);
        ret.put(SJ_PARAM_NAME, sjParamName);
        ret.put(PARAM_VALUE, paramValue);
        ret.put(PARAM_UNIT, paramUnit);
        ret.put(DATA_FROM, dataFrom);

        ret.put(GONG_CHENG_ZHI, gongChengZhi);
        ret.put(SHANG_PIAN_CHA, shangPianCha);
        ret.put(XIA_PIAN_CHA, xiaPianCha);
        ret.put(FU_HAO, fuHao);
        ret.put(JI_ZHUN1, jiZhun1);
        ret.put(JI_ZHUN2, jiZhun2);
        ret.put(JI_ZHUN3, jiZhun3);

        return ret;
    }

    @Override
    public Map getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(TEMPLATE_ID, templateId);
        ret.put(TEMPLATE_NAME, templateName);
        ret.put(CAD_NAME, cadName);
        ret.put(CAD_VERSION, cadVersion);
        ret.put(PART_NUMBER, partNumber);
        ret.put(PART_VERSION, partVersion);
        ret.put(GY_PARAM_NAME, gyParamName);
        ret.put(GY_PARAM_NUMBER, gyParamNumber);
        ret.put(SJ_PARAM_NAME, sjParamName);
        ret.put(PARAM_VALUE, paramValue);
        ret.put(PARAM_UNIT, paramUnit);
        ret.put(DATA_FROM, dataFrom);

        ret.put(GONG_CHENG_ZHI, gongChengZhi);
        ret.put(SHANG_PIAN_CHA, shangPianCha);
        ret.put(XIA_PIAN_CHA, xiaPianCha);
        ret.put(FU_HAO, fuHao);
        ret.put(JI_ZHUN1, jiZhun1);
        ret.put(JI_ZHUN2, jiZhun2);
        ret.put(JI_ZHUN3, jiZhun3);


        return ret;
    }


    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getTemplateId() {
        return templateId == null ? "" : templateId.trim();
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getCadName() {
        return cadName == null ? "" : cadName.trim();
    }

    public void setCadName(String cadName) {
        this.cadName = cadName;
    }

    public String getCadVersion() {
        return cadVersion == null ? "" : cadVersion.trim();
    }

    public void setCadVersion(String cadVersion) {
        this.cadVersion = cadVersion;
    }

    public String getPartNumber() {
        return partNumber == null ? "" : partNumber.trim();
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartVersion() {
        return partVersion == null ? "" : partVersion.trim();
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public String getGyParamName() {
        return gyParamName == null ? "" : gyParamName.trim();
    }

    public void setGyParamName(String gyParamName) {
        this.gyParamName = gyParamName;
    }

    public String getSjParamName() {
        return sjParamName == null ? "" : sjParamName.trim();
    }

    public void setSjParamName(String sjParamName) {
        this.sjParamName = sjParamName;
    }

    public String getParamValue() {
        return paramValue == null ? "" : paramValue.trim();
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamUnit() {
        return paramUnit == null ? "" : paramUnit.trim();
    }

    public void setParamUnit(String paramUnit) {
        this.paramUnit = paramUnit;
    }

    public String getDataFrom() {
        return dataFrom == null ? "" : dataFrom.trim();
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }


    public String getShangPianCha() {
        return shangPianCha == null ? "" : shangPianCha.trim();
    }

    public void setShangPianCha(String shangPianCha) {
        this.shangPianCha = shangPianCha;
    }

    public String getXiaPianCha() {
        return xiaPianCha == null ? "" : xiaPianCha.trim();
    }

    public void setXiaPianCha(String xiaPianCha) {
        this.xiaPianCha = xiaPianCha;
    }

    public String getFuHao() {
        return fuHao == null ? "" : fuHao.trim();
    }

    public void setFuHao(String fuHao) {
        this.fuHao = fuHao;
    }

    public String getJiZhun1() {
        return jiZhun1 == null ? "" : jiZhun1.trim();
    }

    public void setJiZhun1(String jiZhun1) {
        this.jiZhun1 = jiZhun1;
    }

    public String getJiZhun2() {
        return jiZhun2 == null ? "" : jiZhun2.trim();
    }

    public void setJiZhun2(String jiZhun2) {
        this.jiZhun2 = jiZhun2;
    }

    public String getJiZhun3() {
        return jiZhun3 == null ? "" : jiZhun3.trim();
    }

    public void setJiZhun3(String jiZhun3) {
        this.jiZhun3 = jiZhun3;
    }

    public String getTemplateName() {
        return templateName == null ? "" : templateName.trim();
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getGyParamNumber() {
        return gyParamNumber == null ? "" : gyParamNumber.trim();
    }

    public void setGyParamNumber(String gyParamNumber) {
        this.gyParamNumber = gyParamNumber;
    }

	public String getGongChengZhi() {
		return gongChengZhi;
	}

	public void setGongChengZhi(String gongChengZhi) {
		this.gongChengZhi = gongChengZhi;
	}
}

