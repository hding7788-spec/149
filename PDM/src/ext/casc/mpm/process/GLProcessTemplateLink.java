package ext.casc.mpm.process;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 工艺模板映射
 */
public class GLProcessTemplateLink implements CmPersistable {
    public GLProcessTemplateLink() {
    }

    private String keyId = "";
    private String xuHao = "";   //序号
    private String caiLiaoFenLei = "";  //材料分类
    private String yaXiaXian = "";    //压下陷
    private String reChuLi = "";      //热处理
    private String biaoMianChuLi = "";  //表面处理
    private String processTemplate = "";  //工艺模板
    private String templateNumber = "";  //工艺模板编号

    public static final String CAILIAOFENLEI = "caiLiaoFenLei";
    public static final String YAXIAXIAN = "yaXiaXian";
    public static final String RECHULI = "reChuLi";
    public static final String BIAOMIANCHULI = "biaoMianChuLi";
    public static final String PROCESSTEMPLATE = "processTemplate";  //工艺模板
    public static final String TEMPLATENUMBER = "templateNumber";  //工艺模板编号

    public static final String XUHAO = "xuHao";  //序号

    @Override
    public GLProcessTemplateLink getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setXuHao(rs.getString(XUHAO));
            setCaiLiaoFenLei(rs.getString(CAILIAOFENLEI));
            setYaXiaXian(rs.getString(YAXIAXIAN));
            setReChuLi(rs.getString(RECHULI));
            setBiaoMianChuLi(rs.getString(BIAOMIANCHULI));
            setProcessTemplate(rs.getString(PROCESSTEMPLATE));
            setTemplateNumber(rs.getString(TEMPLATENUMBER));
        }
        return this;
    }


    @Override
    public Map<String, Object> getUpdateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(XUHAO, xuHao);
        ret.put(CAILIAOFENLEI, caiLiaoFenLei);
        ret.put(YAXIAXIAN, yaXiaXian);
        ret.put(RECHULI, reChuLi);
        ret.put(BIAOMIANCHULI, biaoMianChuLi);
        ret.put(PROCESSTEMPLATE, processTemplate);
        ret.put(TEMPLATENUMBER, templateNumber);
        return ret;
    }

    @Override
    public Map<String, Object> getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(XUHAO, xuHao);
        ret.put(CAILIAOFENLEI, caiLiaoFenLei);
        ret.put(YAXIAXIAN, yaXiaXian);
        ret.put(RECHULI, reChuLi);
        ret.put(BIAOMIANCHULI, biaoMianChuLi);
        ret.put(PROCESSTEMPLATE, processTemplate);
        ret.put(TEMPLATENUMBER, templateNumber);
        return ret;
    }

    @Override
    public String toString() {
        return "GLProcessTemplateLink{" + "keyId='" + keyId + '\'' + ", xuHao='" + xuHao + '\'' + ", caiLiaoFenLei='" + caiLiaoFenLei + '\'' + ", yaXiaXian='" + yaXiaXian + '\'' + ", reChuLi='" + reChuLi + '\'' + ", biaoMianChuLi='" + biaoMianChuLi + '\'' + ", processTemplate='" + processTemplate + '\'' + '}';
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    @Override
    public Object getKeyId() {
        return this.keyId;
    }

    public String getCaiLiaoFenLei() {
        return caiLiaoFenLei;
    }

    public void setCaiLiaoFenLei(String caiLiaoFenLei) {
        this.caiLiaoFenLei = caiLiaoFenLei;
    }

    public String getYaXiaXian() {
        return yaXiaXian;
    }

    public void setYaXiaXian(String yaXiaXian) {
        this.yaXiaXian = yaXiaXian;
    }

    public String getReChuLi() {
        return reChuLi;
    }

    public void setReChuLi(String reChuLi) {
        this.reChuLi = reChuLi;
    }

    public String getBiaoMianChuLi() {
        return biaoMianChuLi;
    }

    public void setBiaoMianChuLi(String biaoMianChuLi) {
        this.biaoMianChuLi = biaoMianChuLi;
    }

    public String getXuHao() {
        return xuHao;
    }

    public void setXuHao(String xuHao) {
        this.xuHao = xuHao;
    }

    public String getTemplateNumber() {
        return templateNumber == null ? "" : templateNumber.trim();
    }

    public void setTemplateNumber(String templateNumber) {
        this.templateNumber = templateNumber;
    }

    public String getProcessTemplate() {
        return processTemplate == null ? "" : processTemplate.trim();
    }

    public void setProcessTemplate(String processTemplate) {
        this.processTemplate = processTemplate;
    }

}

