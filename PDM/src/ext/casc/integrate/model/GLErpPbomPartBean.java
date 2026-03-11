package ext.casc.integrate.model;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLErpPbomPartBean implements CmPersistable {

	private static final long serialVersionUID = 1L;


	public static final String DOCOID = "docoid";
	public static final String PARTNUMBER = "partNumber";
	public static final String PARENTNUMBER = "parentNumber";
	public static final String TECHNUMBER="techNumber";
	//public static final String TECHNVERSION="technVersion";
	public static final String TECHNAME="techName";
	public static final String PPLANNUMBER="pplanNumber";
	public static final String VERSION="version";
	public static final String DATAFROM="dataFrom";
	public static final String TYPE="type";
	public static final String PPLANTYPE="pplanType";

	public static final String ZFFLAG="zfflag";
	public static final String CHBM="chbm";
	public static final String CHMC="chmc";
	public static final String USECOUNT="useCount";
	public static final String GYCOUNT="gyCount";
	public static final String SL="sl";
	public static final String XHPH="xhph";
	public static final String GG="gg";
	public static final String JSTJ="jstj";
	public static final String SCCJ="sccj";
	public static final String DW="dw";
	public static final String FJTJ="fjtj";
	public static final String LWGGGCCC="lwgggccc";
	public static final String JXXNDJ="jxxndj";
	public static final String ZLDJ="zldj";
	public static final String FZXS="fzxs";
	public static final String JDDJ="jddj";

	public static final String BMDJ="bmdj";//编码等级
	public static final String KFZBTID="kfzbtid";//抗辐指标TID
	public static final String KFZBSEE="kfzbsee";//抗辐指标SEE
	public static final String XNCS="xncs";//性能参数
	public static final String JDMGDJ_STATE="jdmgdj_state";//是否静电敏感
	public static final String JDMGDJ="jdmgdj";//经典敏感等级
	public static final String SMDJ="smdj";//湿敏等级
	public static final String ISMATCH="isMatch";//湿敏等级
	public static final String DEPT= "dept";
	public static final String XLCC= "xlcc";
	public static final String KZJS= "kzjs";
	public static final String USINGTYPE= "usingType";
	public static final String SYNCHTIME= "synchTime";

	private String keyId = "";
	private String docoid= "";
	private String parentNumber= "";
	private String partNumber= "";
	private String techNumber= "";
	private String techName= "";
	private String pplanNumber= "";
	private String version="";
	private String dataFrom= "";
	private String type="";//
	private String pplanType="";

	private String zfflag="";
	private String chbm= "";
	private String chmc= "";
	private String useCount= "";
	private String gyCount= "";
	private String sl= "";
	private String xhph= "";
	private String gg= "";
	private String jstj= "";
	private String sccj= "";
	private String dw= "";
	private String fjtj= "";
	private String lwgggccc= "";
	private String jxxndj= "";
	private String zldj= "";
	private String fzxs= "";
	private String jddj= "";

	private String bmdj= "";//编码等级
	private String kfzbtid= "";//抗辐指标TID
	private String kfzbsee= "";//抗辐指标SEE
	private String xncs= "";//性能参数
	private String jdmgdj_state= "";//是否静电敏感
	private String jdmgdj= "";//经典敏感等级
	private String smdj= "";//湿敏等级
	private String isMatch= "";//1:MATCH  0:NEW

	private String dept= "";
	private String xlcc= "";
	private String kzjs= "";
	private String synchTime= "";

	public String getSynchTime() {
		return synchTime;
	}

	public void setSynchTime(String synchTime) {
		this.synchTime = synchTime;
	}

	public String getUsingType() {
		return usingType;
	}

	public void setUsingType(String usingType) {
		this.usingType = usingType;
	}

	private String usingType= "";


	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getDocoid() {
        return docoid;
    }

    public void setDocoid(String docoid) {
        this.docoid = docoid;
    }

    public String getIsMatch() {
        return isMatch;
    }

    public void setIsMatch(String isMatch) {
        this.isMatch = isMatch;
    }

    public String getParentNumber() {
		return parentNumber;
	}

	public void setParentNumber(String parentNumber) {
		this.parentNumber = parentNumber;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
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
		return "partNumber=" + partNumber + ", chbm=" + chbm + ", chmc=" + chmc + ", useCount=" + useCount + ", gyCount=" + gyCount + ", sl=" + sl + ", xhph=" + xhph + ", gg=" + gg
				+ ", jstj=" + jstj + ", sccj=" + sccj + ", dw=" + dw + ", fjtj=" + fjtj + ", lwgggccc=" + lwgggccc + ", jxxndj=" + jxxndj + ", zldj=" + zldj + ", fzxs=" + fzxs + ", jddj=" + jddj
				+ ", techNumber=" + techNumber + ", techName=" + techName + ", pplanNumber=" + pplanNumber + ", version=" + version + ", dataFrom=" + dataFrom + ", type=" + type + ", pplanType=";
	}

	@Override
	public CmPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			setKeyId(rs.getString(KEY_ID));
			setDocoid(rs.getString(DOCOID));
			setParentNumber(rs.getString(PARENTNUMBER));
			setPartNumber(rs.getString(PARTNUMBER));
			//setTechNumber(rs.getString(TECHNVERSION));
			setTechNumber(rs.getString(TECHNUMBER));
			setTechName(rs.getString(TECHNAME));
			setPplanNumber(rs.getString(PPLANNUMBER));
			setVersion(rs.getString(VERSION));
			setType(rs.getString(TYPE));
			setPplanType(rs.getString(PPLANTYPE));
			setZfflag(rs.getString(ZFFLAG));
			setChbm(rs.getString(CHBM));
			setChmc(rs.getString(CHMC));
			setUseCount(rs.getString(USECOUNT));
			setGyCount(rs.getString(GYCOUNT));
			setSl(rs.getString(SL));
			setXhph(rs.getString(XHPH));
			setGg(rs.getString(GG ));
			setJstj(rs.getString(JSTJ));
			setSccj(rs.getString(SCCJ));
			setDw(rs.getString(DW));
			setFjtj(rs.getString(FJTJ));
			setLwgggccc(rs.getString(LWGGGCCC));
			setJxxndj(rs.getString(JXXNDJ));
			setZldj(rs.getString(ZLDJ));
			setFzxs(rs.getString(FZXS));
			setJddj(rs.getString(JDDJ));
			setBmdj(rs.getString(BMDJ));
			setKfzbtid(rs.getString(KFZBTID));
			setKfzbsee(rs.getString(KFZBSEE));
			setXncs(rs.getString(XNCS));
			setJdmgdj_state(rs.getString(JDMGDJ_STATE));
			setJdmgdj(rs.getString(JDMGDJ));
			setSmdj(rs.getString(SMDJ));
			setDataFrom(rs.getString(DATAFROM));
			setIsMatch(rs.getString(ISMATCH));
			setDept(rs.getString(DEPT));
			setXlcc(rs.getString(XLCC));
			setKzjs(rs.getString(KZJS));
			setUsingType(rs.getString(USINGTYPE));
			setSynchTime(rs.getString(SYNCHTIME));


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
		ret.put(DOCOID, docoid);
		ret.put(PARENTNUMBER, parentNumber);
		ret.put(PARTNUMBER, partNumber);
		ret.put(TECHNUMBER, techNumber);
		ret.put(TECHNAME, techName);
		ret.put(PPLANNUMBER, pplanNumber);
		ret.put(VERSION, version);
		ret.put(DATAFROM, dataFrom);
		ret.put(TYPE, type);
		ret.put(PPLANTYPE, pplanType);
		ret.put(ZFFLAG, zfflag);
		ret.put(CHBM, chbm);
		ret.put(CHMC, chmc);
		ret.put(USECOUNT, useCount);
		ret.put(GYCOUNT, gyCount);
		ret.put(SL, sl);
		ret.put(XHPH, xhph);
		ret.put(GG, gg);
		ret.put(JSTJ, jstj);
		ret.put(SCCJ, sccj);
		ret.put(DW,dw);
		ret.put(FJTJ, fjtj);
		ret.put(LWGGGCCC, lwgggccc);
		ret.put(JXXNDJ, jxxndj);
		ret.put(ZLDJ,zldj);
		ret.put(FZXS, fzxs);
		ret.put(JDDJ, jddj);
		ret.put(BMDJ, bmdj);
		ret.put(KFZBTID, kfzbtid);
		ret.put(KFZBSEE, kfzbsee);
		ret.put(XNCS, xncs);
		ret.put(JDMGDJ_STATE, jdmgdj_state);
		ret.put(JDMGDJ, jdmgdj);
		ret.put(SMDJ, smdj);
		ret.put(ISMATCH, isMatch);
		ret.put(DEPT, dept);
		ret.put(XLCC, xlcc);
		ret.put(KZJS, kzjs);
		ret.put(USINGTYPE,usingType);
		ret.put(SYNCHTIME,synchTime);

		return ret;

	}

	@Override
	public Map getCreateMap() {
		HashMap ret = new HashMap();
		ret.put(KEY_ID, keyId);
		ret.put(DOCOID, docoid);
		ret.put(PARENTNUMBER, parentNumber);
		ret.put(PARTNUMBER, partNumber);
		ret.put(TECHNUMBER, techNumber);
		ret.put(TECHNAME, techName);
		ret.put(PPLANNUMBER, pplanNumber);
		ret.put(VERSION, version);
		ret.put(DATAFROM, dataFrom);
		ret.put(TYPE, type);
		ret.put(PPLANTYPE, pplanType);
		ret.put(ZFFLAG, zfflag);
		ret.put(CHBM, chbm);
		ret.put(CHMC, chmc);
		ret.put(USECOUNT, useCount);
		ret.put(GYCOUNT, gyCount);
		ret.put(SL, sl);
		ret.put(XHPH, xhph);
		ret.put(GG, gg);
		ret.put(JSTJ, jstj);
		ret.put(SCCJ, sccj);
		ret.put(DW,dw);
		ret.put(FJTJ, fjtj);
		ret.put(LWGGGCCC, lwgggccc);
		ret.put(JXXNDJ, jxxndj);
		ret.put(ZLDJ,zldj);
		ret.put(FZXS, fzxs);
		ret.put(JDDJ, jddj);
		ret.put(BMDJ, bmdj);
		ret.put(KFZBTID, kfzbtid);
		ret.put(KFZBSEE, kfzbsee);
		ret.put(XNCS, xncs);
		ret.put(JDMGDJ_STATE, jdmgdj_state);
		ret.put(JDMGDJ, jdmgdj);
		ret.put(SMDJ, smdj);
		ret.put(ISMATCH, isMatch);
		ret.put(DEPT, dept);
		ret.put(XLCC, xlcc);
		ret.put(KZJS, kzjs);
		ret.put(USINGTYPE,usingType);
		ret.put(SYNCHTIME,synchTime);

		return ret;
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
}
