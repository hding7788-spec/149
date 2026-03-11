package ext.casc.integrate.model;

import java.io.Serializable;

public class PbomErpPartBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private String partNumber;
	private String chbm;
	private String chmc;
	private String useCount;
	private String gyCount;
	private String sl;
	private String xhph;
	private String xlcc;
	private String kzjs;
	private String sjkzjs;
	private String gg;
	private String jstj;
	private String sccj;
	private String dw;
	private String fjtj;
	private String lwgggccc;
	private String jxxndj;
	private String zldj;
	private String fzxs;
	private String jddj;

	private String techNumber;
	private String techName;
	private String pplanNumber;
	private String version="";
	private String dataFrom;
	private String type="";//
	private String pplanType="";

	private String zfflag="";
	private String dept="";

	//add by hz 2020/1/6
	private String bmdj;//编码等级
	private String kfzbtid;//抗辐指标TID
	private String kfzbsee;//抗辐指标SEE
	private String xncs;//性能参数
	private String jdmgdj_state;//是否静电敏感
	private String jdmgdj;//经典敏感等级
	private String smdj;//湿敏等级

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getSjkzjs() {
		return sjkzjs;
	}

	public void setSjkzjs(String sjkzjs) {
		this.sjkzjs = sjkzjs;
	}

	public String getZfflag() {
		return zfflag;
	}

	public void setZfflag(String zfflag) {
		this.zfflag = zfflag;
	}

	public String getPplanType() {
		return pplanType;
	}

	public String getXlcc() {
		return xlcc;
	}

	public void setXlcc(String xlcc) {
		this.xlcc = xlcc;
	}

	public String getKzjs() {
		return kzjs;
	}

	public void setKzjs(String kzjs) {
		this.kzjs = kzjs;
	}

	public void setPplanType(String pplanType) {
		this.pplanType = pplanType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getChbm() {
		return chbm;
	}

	public void setChbm(String chbm) {
		this.chbm = chbm;
	}

	public String getChmc() {
		return chmc;
	}

	public void setChmc(String chmc) {
		this.chmc = chmc;
	}

	public String getUseCount() {
		return useCount;
	}

	public void setUseCount(String useCount) {
		this.useCount = useCount;
	}

	public String getGyCount() {
		return gyCount;
	}

	public void setGyCount(String gyCount) {
		this.gyCount = gyCount;
	}

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public String getXhph() {
		return xhph;
	}

	public void setXhph(String xhph) {
		this.xhph = xhph;
	}

	public String getGg() {
		return gg;
	}

	public void setGg(String gg) {
		this.gg = gg;
	}

	public String getJstj() {
		return jstj;
	}

	public void setJstj(String jstj) {
		this.jstj = jstj;
	}

	public String getSccj() {
		return sccj;
	}

	public void setSccj(String sccj) {
		this.sccj = sccj;
	}

	public String getDw() {
		return dw;
	}

	public void setDw(String dw) {
		this.dw = dw;
	}

	public String getFjtj() {
		return fjtj;
	}

	public void setFjtj(String fjtj) {
		this.fjtj = fjtj;
	}

	public String getLwgggccc() {
		return lwgggccc;
	}

	public void setLwgggccc(String lwgggccc) {
		this.lwgggccc = lwgggccc;
	}

	public String getJxxndj() {
		return jxxndj;
	}

	public void setJxxndj(String jxxndj) {
		this.jxxndj = jxxndj;
	}

	public String getZldj() {
		return zldj;
	}

	public void setZldj(String zldj) {
		this.zldj = zldj;
	}

	public String getFzxs() {
		return fzxs;
	}

	public void setFzxs(String fzxs) {
		this.fzxs = fzxs;
	}

	public String getJddj() {
		return jddj;
	}

	public void setJddj(String jddj) {
		this.jddj = jddj;
	}

	public String getTechNumber() {
		return techNumber;
	}

	public void setTechNumber(String techNumber) {
		this.techNumber = techNumber;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	public String getPplanNumber() {
		return pplanNumber;
	}

	public void setPplanNumber(String pplanNumber) {
		this.pplanNumber = pplanNumber;
	}

	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

	public String getBmdj() {
		return bmdj;
	}

	public void setBmdj(String bmdj) {
		this.bmdj = bmdj;
	}

	public String getKfzbtid() {
		return kfzbtid;
	}

	public void setKfzbtid(String kfzbtid) {
		this.kfzbtid = kfzbtid;
	}

	public String getKfzbsee() {
		return kfzbsee;
	}

	public void setKfzbsee(String kfzbsee) {
		this.kfzbsee = kfzbsee;
	}

	public String getXncs() {
		return xncs;
	}

	public void setXncs(String xncs) {
		this.xncs = xncs;
	}

	public String getJdmgdj_state() {
		return jdmgdj_state;
	}

	public void setJdmgdj_state(String jdmgdj_state) {
		this.jdmgdj_state = jdmgdj_state;
	}

	public String getJdmgdj() {
		return jdmgdj;
	}

	public void setJdmgdj(String jdmgdj) {
		this.jdmgdj = jdmgdj;
	}

	public String getSmdj() {
		return smdj;
	}

	public void setSmdj(String smdj) {
		this.smdj = smdj;
	}

	@Override
	public String toString() {
		return "PbomErpPartBean [partNumber=" + partNumber + ", chbm=" + chbm + ", chmc=" + chmc + ", useCount=" + useCount + ", gyCount=" + gyCount + ", sl=" + sl + ", xhph=" + xhph + ", gg=" + gg
				+ ", jstj=" + jstj + ", sccj=" + sccj + ", dw=" + dw + ", fjtj=" + fjtj + ", lwgggccc=" + lwgggccc + ", jxxndj=" + jxxndj + ", zldj=" + zldj + ", fzxs=" + fzxs + ", jddj=" + jddj
				+ ", techNumber=" + techNumber + ", techName=" + techName + ", pplanNumber=" + pplanNumber + ", version=" + version + ", dataFrom=" + dataFrom + ", type=" + type + ", pplanType="
				+ pplanType + ", zfflag=" + zfflag + ", bmdj=" + bmdj + ", kfzbtid=" + kfzbtid + ", kfzbsee=" + kfzbsee + ", xncs=" + xncs + ", jdmgdj_state=" + jdmgdj_state + ", jdmgdj=" + jdmgdj
				+ ", smdj=" + smdj + "]";
	}
	
}
