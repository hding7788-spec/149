package ext.casc.nc.bean;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLMaterialQuota implements CmPersistable {
    public GLMaterialQuota() {
    }

    private static final long serialVersionUID = 1L;

    public static final String GYWJBH = "gywjbh";//工艺流水号
    public static final String TH = "th";//图号
    public static final String VERSION = "version";//版本
    public static final String GYWJH = "gywjh";//工艺文件号
    public static final String GXNAME = "gxname";//工序名称
    public static final String GXNUM = "gxnum";//工序号
    public static final String WORKFLOWID = "workflowid";//工序Bosid
    public static final String WZSHBM = "wzshbm";//物资所属部门
    public static final String MATERIALCODE = "materialcode";//物料编码
    public static final String GYWZBM = "gywzbm";//工艺物资内部编码
    public static final String GYFJXX = "gyfjxx";//工艺附加信息
    public static final String QTYREQUIRED = "qtyrequired";//数量
    public static final String XLCC = "xlcc";//下料尺寸
    public static final String GZDW = "gzdw";//工艺计量单位
    public static final String SJXLCC = "sjxlcc";//试件下料尺寸
    public static final String KZJS = "kzjs";//可制件数
    public static final String DETYPE = "detype";//定额类型


    private String keyId = "";
    private String gywjbh ="";
    private String th="";
    private String version="";
    private String gywjh="";
    private String gxname="";
    private String gxnum="";
    private String workflowid="";
    private String wzshbm="";
    private String materialcode="";
    private String gywzbm="";
    private String gyfjxx="";
    private String qtyrequired="";
    private String xlcc="";
    private String gzdw="";
    private String sjxlcc="";
    private String kzjs="";
    private String detype="";



    @Override
    public GLMaterialQuota getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setGywjbh(rs.getString(GYWJBH));
            setTh(rs.getString(TH));
            setVersion(rs.getString(VERSION));
            setGywjh(rs.getString(GYWJH));
            setGxname(rs.getString(GXNAME));
            setGxnum(rs.getString(GXNUM));
            setWorkflowid(rs.getString(WORKFLOWID));
            setWzshbm(rs.getString(WZSHBM));
            setMaterialcode(rs.getString(MATERIALCODE));
            setGywzbm(rs.getString(GYWZBM));
            setGyfjxx(rs.getString(GYFJXX));
            setQtyrequired(rs.getString(QTYREQUIRED));
            setXlcc(rs.getString(XLCC));
            setGzdw(rs.getString(GZDW));
            setSjxlcc(rs.getString(SJXLCC));
            setKzjs(rs.getString(KZJS));
            setDetype(rs.getString(DETYPE));
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
        ret.put(GYWJBH, gywjbh);
        ret.put(TH, th);
        ret.put(VERSION, version);
        ret.put(GYWJH, gywjh);
        ret.put(GXNAME, gxname);
        ret.put(GXNUM, gxnum);
        ret.put(WZSHBM, wzshbm);
        ret.put(WORKFLOWID, workflowid);
        ret.put(MATERIALCODE, materialcode);
        ret.put(GYWZBM, gywzbm);
        ret.put(GYFJXX, gyfjxx);
        ret.put(QTYREQUIRED, qtyrequired);
        ret.put(XLCC, xlcc);
        ret.put(GZDW, gzdw);
        ret.put(SJXLCC, sjxlcc);
        ret.put(KZJS, kzjs);
        ret.put(DETYPE, detype);
        return ret;
    }

    @Override
    public Map<String, Object> getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(GYWJBH, gywjbh);
        ret.put(TH, th);
        ret.put(VERSION, version);
        ret.put(GYWJH, gywjh);
        ret.put(GXNAME, gxname);
        ret.put(GXNUM, gxnum);
        ret.put(WZSHBM, wzshbm);
        ret.put(WORKFLOWID, workflowid);
        ret.put(MATERIALCODE, materialcode);
        ret.put(GYWZBM, gywzbm);
        ret.put(GYFJXX, gyfjxx);
        ret.put(QTYREQUIRED, qtyrequired);
        ret.put(XLCC, xlcc);
        ret.put(GZDW, gzdw);
        ret.put(SJXLCC, sjxlcc);
        ret.put(KZJS, kzjs);
        ret.put(DETYPE, detype);
        return ret;
    }

    @Override
    public String toString() {
        return "GLMaterialQuota{" +
                "keyId='" + keyId + '\'' +
                ", GYWJBH='" + gywjbh + '\'' +
                ", VERSION='" + version + '\'' +
                ", MATERIALCODE='" + materialcode + '\'' +
                ", GZDW='" + gzdw + '\'' +
                '}';
    }

    public String getGywjbh() {
        return gywjbh;
    }

    public void setGywjbh(String gywjbh) {
        this.gywjbh = gywjbh;
    }

    public String getTh() {
        return th;
    }

    public void setTh(String th) {
        this.th = th;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGywjh() {
        return gywjh;
    }

    public void setGywjh(String gywjh) {
        this.gywjh = gywjh;
    }

    public String getGxname() {
        return gxname;
    }

    public void setGxname(String gxname) {
        this.gxname = gxname;
    }

    public String getGxnum() {
        return gxnum;
    }

    public void setGxnum(String gxnum) {
        this.gxnum = gxnum;
    }

    public String getWorkflowid() {
        return workflowid;
    }

    public void setWorkflowid(String workflowid) {
        this.workflowid = workflowid;
    }

    public String getWzshbm() {
        return wzshbm;
    }

    public void setWzshbm(String wzshbm) {
        this.wzshbm = wzshbm;
    }

    public String getMaterialcode() {
        return materialcode;
    }

    public void setMaterialcode(String materialcode) {
        this.materialcode = materialcode;
    }

    public String getGywzbm() {
        return gywzbm;
    }

    public void setGywzbm(String gywzbm) {
        this.gywzbm = gywzbm;
    }

    public String getGyfjxx() {
        return gyfjxx;
    }

    public void setGyfjxx(String gyfjxx) {
        this.gyfjxx = gyfjxx;
    }

    public String getQtyrequired() {
        return qtyrequired;
    }

    public void setQtyrequired(String qtyrequired) {
        this.qtyrequired = qtyrequired;
    }

    public String getXlcc() {
        return xlcc;
    }

    public void setXlcc(String xlcc) {
        this.xlcc = xlcc;
    }

    public String getGzdw() {
        return gzdw;
    }

    public void setGzdw(String gzdw) {
        this.gzdw = gzdw;
    }

    public String getSjxlcc() {
        return sjxlcc;
    }

    public void setSjxlcc(String sjxlcc) {
        this.sjxlcc = sjxlcc;
    }

    public String getKzjs() {
        return kzjs;
    }

    public void setKzjs(String kzjs) {
        this.kzjs = kzjs;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getDetype() {
        return detype;
    }

    public void setDetype(String detype) {
        this.detype = detype;
    }
}

