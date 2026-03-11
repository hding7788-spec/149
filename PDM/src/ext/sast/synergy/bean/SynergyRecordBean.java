package ext.sast.synergy.bean;

import java.util.Date;

public class SynergyRecordBean {
    //送审单编号
    private String ssdBh = "";
    //送审对象编号
    private String ssdxBh = "";
    //送审对象名称
    private String ssdxMx = "";
    //流程名称
    private String lcmc = "";
    //送审人
    private String ssr = "";
    //所属产品
    private String sscp = "";
    //发起站点
    private String fqzd = "";
    //接收站点
    private String jszd = "";
    //发送状态
    private String fszt = "";
    //类型
    private String lx = "";
    //活动名称
    private String hdmc = "";
    //发送时间
    private Date fssj = null;
    //更新时间
    private Date gxsj = null;
    //消息状态
    private String xxzt = "";
    //错误信息
    private String cwxx = "";
    //全流程监控
    private String qlcjk = "";
    //操作
    private String cz = "";

    public String getSsdBh() {
        return ssdBh;
    }

    public void setSsdBh(String ssdBh) {
        this.ssdBh = ssdBh;
    }

    public String getSsdxBh() {
        return ssdxBh;
    }

    public void setSsdxBh(String ssdxBh) {
        this.ssdxBh = ssdxBh;
    }

    public String getSsdxMx() {
        return ssdxMx;
    }

    public void setSsdxMx(String ssdxMx) {
        this.ssdxMx = ssdxMx;
    }

    public String getLcmc() {
        return lcmc;
    }

    public void setLcmc(String lcmc) {
        this.lcmc = lcmc;
    }

    public String getSsr() {
        return ssr;
    }

    public void setSsr(String ssr) {
        this.ssr = ssr;
    }

    public String getSscp() {
        return sscp;
    }

    public void setSscp(String sscp) {
        this.sscp = sscp;
    }

    public String getFqzd() {
        return fqzd;
    }

    public void setFqzd(String fqzd) {
        this.fqzd = fqzd;
    }

    public String getJszd() {
        return jszd;
    }

    public void setJszd(String jszd) {
        this.jszd = jszd;
    }

    public String getFszt() {
        return fszt;
    }

    public void setFszt(String fszt) {
        this.fszt = fszt;
    }

    public String getLx() {
        return lx;
    }

    public void setLx(String lx) {
        this.lx = lx;
    }

    public String getHdmc() {
        return hdmc;
    }

    public void setHdmc(String hdmc) {
        this.hdmc = hdmc;
    }

    public Date getFssj() {
        return fssj;
    }

    public void setFssj(Date fssj) {
        this.fssj = fssj;
    }

    public Date getGxsj() {
        return gxsj;
    }

    public void setGxsj(Date gxsj) {
        this.gxsj = gxsj;
    }

    public String getXxzt() {
        return xxzt;
    }

    public void setXxzt(String xxzt) {
        this.xxzt = xxzt;
    }

    public String getCwxx() {
        return cwxx;
    }

    public void setCwxx(String cwxx) {
        this.cwxx = cwxx;
    }

    public String getQlcjk() {
        return qlcjk;
    }

    public void setQlcjk(String qlcjk) {
        this.qlcjk = qlcjk;
    }

    public String getCz() {
        return cz;
    }

    public void setCz(String cz) {
        this.cz = cz;
    }
}
