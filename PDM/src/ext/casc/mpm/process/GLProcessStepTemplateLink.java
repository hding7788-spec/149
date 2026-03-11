package ext.casc.mpm.process;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 工艺模板映射
 */
public class GLProcessStepTemplateLink implements CmPersistable {
    public GLProcessStepTemplateLink() {
    }

    private String keyId = "";
    private String xuHao = "";   //序号
    private String stepName = "";   //工序名字
    private String caiLiaoFenLei = "";  //材料分类
    private String guiGe = "";      //规格
    private String biaoMianChuLi = "";  //表面处理
    private String processStepTemplate = "";  //工艺模板
    private String templateNumber = "";  //工序模板编号

    public static final String CAILIAOFENLEI = "caiLiaoFenLei";
    public static final String STEPNAME = "stepName";
    public static final String GUIGE = "guiGe";
    public static final String BIAOMIANCHULI = "biaoMianChuLi";
    public static final String PROCESSSTEPTEMPLATE = "processStepTemplate";  //工艺模板
    public static final String TEMPLATENUMBER = "templateNumber";  //工艺模板编号

    public static final String XUHAO = "xuHao";  //序号

    @Override
    public GLProcessStepTemplateLink getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setXuHao(rs.getString(XUHAO));
            setCaiLiaoFenLei(rs.getString(CAILIAOFENLEI));
            setStepName(rs.getString(STEPNAME));
            setGuiGe(rs.getString(GUIGE));
            setBiaoMianChuLi(rs.getString(BIAOMIANCHULI));
            setProcessStepTemplate(rs.getString(PROCESSSTEPTEMPLATE));
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
        ret.put(STEPNAME, stepName);
        ret.put(GUIGE, guiGe);
        ret.put(BIAOMIANCHULI, biaoMianChuLi);
        ret.put(PROCESSSTEPTEMPLATE, processStepTemplate);
        ret.put(TEMPLATENUMBER, templateNumber);
        return ret;
    }

    @Override
    public Map<String, Object> getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(XUHAO, xuHao);
        ret.put(CAILIAOFENLEI, caiLiaoFenLei);
        ret.put(STEPNAME, stepName);
        ret.put(GUIGE, guiGe);
        ret.put(BIAOMIANCHULI, biaoMianChuLi);
        ret.put(PROCESSSTEPTEMPLATE, processStepTemplate);
        ret.put(TEMPLATENUMBER, templateNumber);
        return ret;
    }

    @Override
    public String toString() {
        return "GLProcessStepTemplateLink{" + "keyId='" + keyId + '\'' + ", xuHao='" + xuHao + '\'' + ", stepName='" + stepName + '\'' + ", caiLiaoFenLei='" + caiLiaoFenLei + '\'' + ", guiGe='" + guiGe + '\'' + ", biaoMianChuLi='" + biaoMianChuLi + '\'' + ", processStepTemplate='" + processStepTemplate + '\'' + '}';
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


    public String getStepName() {
        return stepName == null ? "" : stepName.trim();
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getGuiGe() {
        return guiGe == null ? "" : guiGe.trim();
    }

    public void setGuiGe(String guiGe) {
        this.guiGe = guiGe;
    }

    public String getProcessStepTemplate() {
        return processStepTemplate == null ? "" : processStepTemplate.trim();
    }

    public void setProcessStepTemplate(String processStepTemplate) {
        this.processStepTemplate = processStepTemplate;
    }

    public String getTemplateNumber() {
        return templateNumber == null ? "" : templateNumber.trim();
    }

    public void setTemplateNumber(String templateNumber) {
        this.templateNumber = templateNumber;
    }
}

