package ext.ases.techMaterial.bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import wt.fc.InvalidAttributeException;
import wt.fc.PersistInfo;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospectionException;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import ext.ases.techMaterial.gwpersistable.GwPersistable;

/**
 * 
 * @author Mchen
 *金属材料
 * 
 */
public class TMEMetallicPartLinkBean  implements GwPersistable {

	private static final long serialVersionUID = 1L;

	/**
	 * GWKEYID WFOID USERNAME WORKITEMOID SELECT ADVISE
	 */

	public static String GWKEYID = "GWKEYID";// id
	public static String TECHNICSMATERIALENTRIESID = "TECHNICSMATERIALENTRIESID";// 物资条目oid
	public static String SJBM = "SJBM";// 设计编码
	public static String WZBM = "WZBM";// 物资编码
	public static String WZMC = "WZMC";// 物资名称
	public static String WZJC = "WZJC";// 物资简称
	public static String BMYXJB = "BMYXJB";// 编码优选级别
	public static String BMZT = "BMZT";// 编码状态
	public static String BMLX = "BMLX";// 编码类型
	public static String BMDJ = "BMDJ";// 编码等级
	public static String HSL = "HSL";// 换算率
	public static String XS = "XS";// 系数
	public static String PH = "PH";// 牌号
	public static String GYZT = "GYZT";// 供应状态
	public static String CYBZ = "CYBZ";// 采用标准
	public static String JD = "JD";// 精度
	public static String ZLTZ = "ZLTZ";// 质量特征
	public static String PZGGBZ = "PZGGBZ";// 品种规格标准
	public static String TSSM = "TSSM";// 特殊说明
	public static String SFJK = "SFJK";// 是否进口
	public static String SCCJ = "SCCJ";// 生产厂家
	public static String GG = "GG";// 规格
	public static String JLDW = "JLDW";// 计量单位
	public static String GYDW = "GYDW";// 工艺单位
	public static String WZFL = "WZFL";//物资分类

	/**
	 */
	private String gwKey;

	private String technicsmaterialentriesid;
	public String sjbm;// 设计编码
	public String wzbm;// 物资编码
	public String wzmc;// 物资名称
	public String wzjc;// 物资简称
	public String bmyxjb;// 编码优选级别
	public String bmzt;// 编码状态
	public String bmlx;// 编码类型
	public String bmdj;// 编码等级
	public String hsl;// 换算率
	public String xs;// 系数
	public String ph;// 牌号
	public  String gyzt ;// 供应状态
	public  String cybz ;// 采用标准
	public  String jd ;// 精度
	public  String zltz ;// 质量特征
	public  String pzggbz ;// 品种规格标准
	public String tssm;// 特殊说明
	public String sfjk;// 是否进口
	public String sccj;// 生产厂家
	public  String gg ;// 规格
	public  String jldw ;// 计量单位
	public  String gydw ;// 工艺单位
	public  String wzfl ;// 物资分类

	public static int index = 1;

	public static String generateKeyId() {
		if (index >= 10000) {
			index = 1;
		}
		return String.valueOf(System.currentTimeMillis()) + index++;
	}

	public String getWzfl() {
		return wzfl;
	}

	public void setWzfl(String wzfl) {
		this.wzfl = wzfl;
	}

	public String getGg() {
		return gg;
	}

	public void setGg(String gg) {
		this.gg = gg;
	}

	public String getJldw() {
		return jldw;
	}

	public void setJldw(String jldw) {
		this.jldw = jldw;
	}

	public String getOid() {
		// TODO Auto-generated method stub
		return gwKey;

	}

	public String getWzbm() {
		return wzbm;
	}

	public void setWzbm(String wzbm) {
		this.wzbm = wzbm;
	}

	@Override
	public GwPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			this.setGwKey(rs.getString(GWKEYID));
			this.setTechnicsmaterialentriesid(rs.getString(TECHNICSMATERIALENTRIESID));
			this.setSjbm(rs.getString(SJBM));
			this.setWzbm(rs.getString(WZBM));
			this.setWzmc(rs.getString(WZMC));
			this.setWzjc(rs.getString(WZJC));
			this.setBmyxjb(rs.getString(BMYXJB));
			this.setBmzt(rs.getString(BMZT));
			this.setBmlx(rs.getString(BMLX));
			this.setBmdj(rs.getString(BMDJ));
			this.setHsl(rs.getString(HSL));
			this.setXs(rs.getString(XS));
			this.setPh(rs.getString(PH));
			this.setGyzt(rs.getString(GYZT));
			this.setCybz(rs.getString(CYBZ));
			this.setJd(rs.getString(JD));
			this.setZltz(rs.getString(ZLTZ));
			this.setPzggbz(rs.getString(PZGGBZ));
			this.setTssm(rs.getString(TSSM));
			this.setSfjk(rs.getString(SFJK));
			this.setSccj(rs.getString(SCCJ));
			this.setGg(rs.getString(GG));
			this.setJldw(rs.getString(JLDW));
			this.setGydw(rs.getString(GYDW));
			this.setWzfl(rs.getString(WZFL));
		}
		return this;
	}
	public String getSccj() {
		return sccj;
	}

	public void setSccj(String sccj) {
		this.sccj = sccj;
	}
	public String getGyzt() {
		return gyzt;
	}

	public void setGyzt(String gyzt) {
		this.gyzt = gyzt;
	}

	public String getJd() {
		return jd;
	}

	public void setJd(String jd) {
		this.jd = jd;
	}

	public String getZltz() {
		return zltz;
	}

	public void setZltz(String zltz) {
		this.zltz = zltz;
	}

	public String getPzggbz() {
		return pzggbz;
	}

	public void setPzggbz(String pzggbz) {
		this.pzggbz = pzggbz;
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

	@Override
	public Map<String, Object> getUpdateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(GWKEYID, gwKey);
		ret.put(TECHNICSMATERIALENTRIESID, technicsmaterialentriesid);
		ret.put(SJBM, sjbm);
		ret.put(WZBM, wzbm);
		ret.put(WZMC, wzmc);
		ret.put(WZJC, wzjc);
		ret.put(BMYXJB, bmyxjb);
		ret.put(BMZT, bmzt);
		ret.put(BMLX, bmlx);
		ret.put(BMDJ, bmdj);
		ret.put(HSL, hsl);
		ret.put(XS, xs);
		ret.put(PH, ph);
		ret.put(GYZT, gyzt);
		ret.put(CYBZ, cybz);
		ret.put(JD, jd);
		ret.put(ZLTZ,zltz);
		ret.put(PZGGBZ, pzggbz);
		ret.put(SJBM, sjbm);
		ret.put(TSSM, tssm);
		ret.put(SFJK, sfjk);
		ret.put(SCCJ, sccj);
		ret.put(GG,gg);
		ret.put(JLDW,jldw);
		ret.put(GYDW,gydw);
		ret.put(WZFL,wzfl);
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
		ret.put(WZJC, wzjc);
		ret.put(BMYXJB, bmyxjb);
		ret.put(BMZT, bmzt);
		ret.put(BMLX, bmlx);
		ret.put(BMDJ, bmdj);
		ret.put(HSL, hsl);
		ret.put(XS, xs);
		ret.put(PH, ph);
		ret.put(GYZT, gyzt);
		ret.put(CYBZ, cybz);
		ret.put(JD, jd);
		ret.put(ZLTZ,zltz);
		ret.put(PZGGBZ, pzggbz);
		ret.put(SJBM, sjbm);
		ret.put(TSSM, tssm);
		ret.put(SFJK, sfjk);
		ret.put(SCCJ, sccj);
		ret.put(GG,gg);
		ret.put(JLDW,jldw);
		ret.put(GYDW,gydw);
		ret.put(WZFL,wzfl);
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

	public String getWzjc() {
		return wzjc;
	}

	public void setWzjc(String wzjc) {
		this.wzjc = wzjc;
	}

	public String getBmyxjb() {
		return bmyxjb;
	}

	public void setBmyxjb(String bmyxjb) {
		this.bmyxjb = bmyxjb;
	}

	public String getBmzt() {
		return bmzt;
	}

	public void setBmzt(String bmzt) {
		this.bmzt = bmzt;
	}

	public String getBmlx() {
		return bmlx;
	}

	public void setBmlx(String bmlx) {
		this.bmlx = bmlx;
	}

	public String getBmdj() {
		return bmdj;
	}

	public void setBmdj(String bmdj) {
		this.bmdj = bmdj;
	}


	public String getTssm() {
		return tssm;
	}

	public void setTssm(String tssm) {
		this.tssm = tssm;
	}

	public String getSfjk() {
		return sfjk;
	}

	public void setSfjk(String sfjk) {
		this.sfjk = sfjk;
	}

	public String getHsl() {
		return hsl;
	}

	public void setHsl(String hsl) {
		this.hsl = hsl;
	}

	public String getXs() {
		return xs;
	}

	public void setXs(String xs) {
		this.xs = xs;
	}

	public String getPh() {
		return ph;
	}

	public void setPh(String ph) {
		this.ph = ph;
	}

	public String getCybz() {
		return cybz;
	}

	public void setCybz(String cybz) {
		this.cybz = cybz;
	}
	public String getGydw() {
		return gydw;
	}

	public void setGydw(String gydw) {
		this.gydw = gydw;
	}

}
