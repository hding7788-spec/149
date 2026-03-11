package ext.ases.techMaterial.bean;

import ext.ases.techMaterial.gwpersistable.GwPersistable;
import wt.fc.InvalidAttributeException;
import wt.fc.PersistInfo;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospectionException;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Mchen
 * 火工品
 * 
 */
public class TMEExpDevicePartLinkBean implements GwPersistable {

	private static final long serialVersionUID = 1L;

	/**
	 * GWKEYID WFOID USERNAME WORKITEMOID SELECT ADVISE
	 */

	public static String GWKEYID = "GWKEYID";// id
	public static String TECHNICSMATERIALENTRIESID = "TECHNICSMATERIALENTRIESID";// 物资条目oid
	public static String SJBM = "SJBM";// 设计编码
	public static String WZBM = "WZBM";// 物资编码
	public static String WZMC = "WZMC";// 物资名称
	public static String BZH = "BZH";// 标准号
	public static String PH = "PH";// 牌号
	public static String SCCJ = "SCCJ";// 生产厂家
	public static String JLDW = "JLDW";// 计量单位
	public static String BMDJ = "BMDJ";// 编码等级
	public static String CPDH = "CPDH";// 产品代号
	public static String ZL = "ZL";// 重量
	public static String ZCSM = "ZCSM";// 贮存寿命
	public static String TNT = "TNT";// TNT
	public static String XNCS = "XNCS";// 性能参数
	public static String TSSM = "TSSM";// 特殊说明
	public static String BMZT = "BMZT";// 编码状态
	public static String WZFL = "WZFL";// 物资分类


	/**
	 */
	private String gwKey;
	private String technicsmaterialentriesid;
	public String sjbm;// 设计编码
	public String wzbm;// 物资编码
	public String wzmc;// 物资名称
	public String bzh;// 标准号
	public String ph;// 牌号
	public String sccj;// 生产厂家
	public String jldw;// 计量单位
	public String bmdj;// 编码等级
	public String cpdh;// 产品代号
	public String zl;// 重量
	public String zcsm;// 贮存寿命
	public String tnt;// TNT
	public String xncs;// 性能参数
	public String tssm;// 特殊说明
	public String bmzt;// 编码状态
	public String wzfl;// 物资分类

	public static int index = 1;

	public static String generateKeyId() {
		if (index >= 10000) {
			index = 1;
		}
		return String.valueOf(System.currentTimeMillis()) + index++;
	}

	public String getOid() {
		// TODO Auto-generated method stub
		return gwKey;

	}

	@Override
	public GwPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			this.setGwKey(rs.getString(GWKEYID));
			this.setTechnicsmaterialentriesid(rs.getString(TECHNICSMATERIALENTRIESID));
			this.setSjbm(rs.getString(SJBM));
			this.setWzbm(rs.getString(WZBM));
			this.setWzmc(rs.getString(WZMC));
			this.setBzh(rs.getString(BZH));
			this.setPh(rs.getString(PH));
			this.setSccj(rs.getString(SCCJ));
			this.setJldw(rs.getString(JLDW));
			this.setBmdj(rs.getString(BMDJ));
			this.setCpdh(rs.getString(CPDH));
			this.setZl(rs.getString(ZL));
			this.setZcsm(rs.getString(ZCSM));
			this.setTnt(rs.getString(TNT));
			this.setXncs(rs.getString(XNCS));
			this.setTssm(rs.getString(TSSM));
			this.setBmzt(rs.getString(BMZT));
			this.setWzfl(rs.getString(WZFL));
		}
		return this;
	}

	@Override
	public Object getKeyId() {
		if (this.gwKey != null && !this.gwKey.equals("")) {
			return this.gwKey;
		} else {
			String keyId = generateKeyId();
			this.setGwKey(keyId);
			return keyId;
		}
	}

	public String getSjbm() {
		return sjbm;
	}

	public void setSjbm(String sjbm) {
		this.sjbm = sjbm;
	}

	public String getWzmc() {
		return wzmc;
	}

	public void setWzmc(String wzmc) {
		this.wzmc = wzmc;
	}

	public String getBzh() {
		return bzh;
	}

	public void setBzh(String bzh) {
		this.bzh = bzh;
	}

	public String getPh() {
		return ph;
	}

	public void setPh(String ph) {
		this.ph = ph;
	}

	public String getSccj() {
		return sccj;
	}

	public void setSccj(String sccj) {
		this.sccj = sccj;
	}

	@Override
	public Map<String, Object> getUpdateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(GWKEYID, gwKey);
		ret.put(TECHNICSMATERIALENTRIESID, technicsmaterialentriesid);
		ret.put(SJBM, sjbm);
		ret.put(WZBM, wzbm);
		ret.put(WZMC, wzmc);
		ret.put(BZH, bzh);
		ret.put(PH, ph);
		ret.put(SCCJ, sccj);
		ret.put(JLDW, jldw);
		ret.put(BMDJ, bmdj);
		ret.put(CPDH, cpdh);
		ret.put(ZL, zl);
		ret.put(ZCSM, zcsm);
		ret.put(TNT, tnt);
		ret.put(XNCS, xncs);
		ret.put(TSSM, tssm);
		ret.put(BMZT, bmzt);
		ret.put(WZFL, wzfl);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(GWKEYID, gwKey);
		ret.put(TECHNICSMATERIALENTRIESID, technicsmaterialentriesid);
		ret.put(SJBM, sjbm);
		ret.put(WZBM, wzbm);
		ret.put(WZMC, wzmc);
		ret.put(BZH, bzh);
		ret.put(PH, ph);
		ret.put(SCCJ, sccj);
		ret.put(JLDW, jldw);
		ret.put(BMDJ, bmdj);
		ret.put(CPDH, cpdh);
		ret.put(ZL, zl);
		ret.put(ZCSM, zcsm);
		ret.put(TNT, tnt);
		ret.put(XNCS, xncs);
		ret.put(TSSM, tssm);
		ret.put(BMZT, bmzt);
		ret.put(WZFL, wzfl);
		return ret;
	}

	public String getGwKey() {
		return gwKey;
	}

	public void setGwKey(String gwKey) {
		this.gwKey = gwKey;
	}

	public void checkAttributes() throws InvalidAttributeException {
		// TODO Auto-generated method stub
	}

	public String getIdentity() {
		// TODO Auto-generated method stub
		return this.gwKey;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public PersistInfo getPersistInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPersistInfo(PersistInfo var1) {
		// TODO Auto-generated method stub

	}

	public void readExternal(PersistentRetrieveIfc var1) throws SQLException, DatastoreException {
		// TODO Auto-generated method stub

	}

	public void writeExternal(PersistentStoreIfc var1) throws SQLException, DatastoreException {
		// TODO Auto-generated method stub

	}

	public ClassInfo getClassInfo() throws WTIntrospectionException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getWzbm() {
		return wzbm;
	}

	public void setWzbm(String wzbm) {
		this.wzbm = wzbm;
	}

	public String getConceptualClassname() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTechnicsmaterialentriesid() {
		return technicsmaterialentriesid;
	}

	public void setTechnicsmaterialentriesid(String technicsmaterialentriesid) {
		this.technicsmaterialentriesid = technicsmaterialentriesid;
	}
	public String getJldw() {
		return jldw;
	}

	public void setJldw(String jldw) {
		this.jldw = jldw;
	}

	public String getBmdj() {
		return bmdj;
	}

	public void setBmdj(String bmdj) {
		this.bmdj = bmdj;
	}

	public String getCpdh() {
		return cpdh;
	}

	public void setCpdh(String cpdh) {
		this.cpdh = cpdh;
	}

	public String getZl() {
		return zl;
	}

	public void setZl(String zl) {
		this.zl = zl;
	}

	public String getZcsm() {
		return zcsm;
	}

	public void setZcsm(String zcsm) {
		this.zcsm = zcsm;
	}

	public String getTnt() {
		return tnt;
	}

	public void setTnt(String tnt) {
		this.tnt = tnt;
	}

	public String getXncs() {
		return xncs;
	}

	public void setXncs(String xncs) {
		this.xncs = xncs;
	}

	public String getTssm() {
		return tssm;
	}

	public void setTssm(String tssm) {
		this.tssm = tssm;
	}

	public String getBmzt() {
		return bmzt;
	}

	public void setBmzt(String bmzt) {
		this.bmzt = bmzt;
	}

	public String getWzfl() {
		return wzfl;
	}

	public void setWzfl(String wzfl) {
		this.wzfl = wzfl;
	}
}
