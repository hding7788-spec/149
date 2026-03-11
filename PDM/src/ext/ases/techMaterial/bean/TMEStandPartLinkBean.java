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
 * 标准紧固件
 * 
 */
public class TMEStandPartLinkBean  implements GwPersistable {

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
	public static String BZH = "BZH";// 标准号
	public static String GG = "GG";// 规格
	public static String CL = "CL";// 材料
	public static String JXXNDJ = "JXXNDJ";// 机械性能等级或硬度
	public static String BMCL = "BMCL";// 表面处理
	public static String RCL = "RCL";// 热处理
	public static String CPXS = "CPXS";// 产品型式
	public static String CPDJ = "CPDJ";// 产品等级
	public static String NBXS = "NBXS";// 板拧形式
	public static String TSSM = "TSSM";// 特殊说明
	public static String SFJK = "SFJK";// 是否进口
	public static String SCCJ = "SCCJ";// 生产厂家
	public static String JLDW = "JLDW";// 计量单位
	public static String WZFL = "WZFL";//物资分类

	/**
	 */
	private String gwKey;
	private String technicsmaterialentriesid;
	public  String sjbm ;// 设计编码
	public  String wzbm ;// 物资编码
	public  String wzmc ;// 物资名称
	public  String wzjc ;// 物资简称
	public  String bmyxjb ;// 编码优选级别
	public  String bmzt ;// 编码状态
	public  String bmlx ;// 编码类型
	public  String bmdj ;// 编码等级
	public  String bzh;// 标准号
	public  String gg ;// 规格
	public  String cl ;// 材料
	public  String jxxndj ;// 机械性能等级或硬度
	public  String bmcl ;// 表面处理
	public  String rcl ;// 热处理
	public  String cpxs;// 产品型式
	public  String cpdj ;// 产品等级
	public  String nbxs ;// 板拧形式
	public  String tssm ;// 特殊说明
	public  String sfjk ;// 是否进口
	public  String sccj ;// 生产厂家
	public  String jldw ;//  计量单位
	public  String wzfl ;// 物资分类

	public String getSccj() {
		return sccj;
	}

	public void setSccj(String sccj) {
		this.sccj = sccj;
	}

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
			this.setWzjc(rs.getString(WZJC));
			this.setBmyxjb(rs.getString(BMYXJB));
			this.setBmzt(rs.getString(BMZT));
			this.setBmlx(rs.getString(BMLX));
			this.setBmdj(rs.getString(BMDJ));
			this.setBzh(rs.getString(BZH));
			this.setGg(rs.getString(GG));
			this.setCl(rs.getString(CL));
			this.setJxxndj(rs.getString(JXXNDJ));
			this.setBmcl(rs.getString(BMCL));
			this.setRcl(rs.getString(RCL));
			this.setCpxs(rs.getString(CPXS));
			this.setCpdj(rs.getString(CPDJ));
			this.setNbxs(rs.getString(NBXS));
			this.setTssm(rs.getString(TSSM));
			this.setSfjk(rs.getString(SFJK));
			this.setSccj(rs.getString(SCCJ));
			this.setJldw(rs.getString(JLDW));
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

	@Override
	public Map<String, Object> getUpdateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(GWKEYID, gwKey);
		ret.put(TECHNICSMATERIALENTRIESID, technicsmaterialentriesid);
		ret.put(SJBM, sjbm);
		ret.put(WZBM, wzbm);
		ret.put(WZMC,wzmc );
		ret.put(WZJC,wzjc );
		ret.put(BMYXJB, bmyxjb);
		ret.put(BMZT, bmzt);
		ret.put(BMLX,bmlx );
		ret.put(BMDJ,bmdj );
		ret.put(BZH,bzh );
		ret.put(GG,gg );
		ret.put(CL, cl);
		ret.put(JXXNDJ,jxxndj );
		ret.put(BMCL,bmcl );
		ret.put(RCL,rcl );
		ret.put(CPXS, cpxs);
		ret.put(CPDJ,cpdj );
		ret.put(NBXS, nbxs);
		ret.put(TSSM,tssm );
		ret.put(SFJK, sfjk);
		ret.put(SCCJ, sccj);
		ret.put(JLDW,jldw);
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
		ret.put(WZMC,wzmc );
		ret.put(WZJC,wzjc );
		ret.put(BMYXJB, bmyxjb);
		ret.put(BMZT, bmzt);
		ret.put(BMLX,bmlx );
		ret.put(BMDJ,bmdj );
		ret.put(BZH,bzh );
		ret.put(GG,gg );
		ret.put(CL, cl);
		ret.put(JXXNDJ,jxxndj );
		ret.put(BMCL,bmcl );
		ret.put(RCL,rcl );
		ret.put(CPXS, cpxs);
		ret.put(CPDJ,cpdj );
		ret.put(NBXS, nbxs);
		ret.put(TSSM,tssm );
		ret.put(SFJK, sfjk);
		ret.put(SCCJ, sccj);
		ret.put(JLDW,jldw);
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

	public String getWzbm() {
		return wzbm;
	}

	public void setWzbm(String wzbm) {
		this.wzbm = wzbm;
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

	public String getBzh() {
		return bzh;
	}

	public void setBzh(String bzh) {
		this.bzh = bzh;
	}

	public String getGg() {
		return gg;
	}

	public void setGg(String gg) {
		this.gg = gg;
	}

	public String getCl() {
		return cl;
	}

	public void setCl(String cl) {
		this.cl = cl;
	}

	public String getJxxndj() {
		return jxxndj;
	}

	public void setJxxndj(String jxxndj) {
		this.jxxndj = jxxndj;
	}

	public String getBmcl() {
		return bmcl;
	}

	public void setBmcl(String bmcl) {
		this.bmcl = bmcl;
	}

	public String getRcl() {
		return rcl;
	}

	public void setRcl(String rcl) {
		this.rcl = rcl;
	}

	public String getCpxs() {
		return cpxs;
	}

	public void setCpxs(String cpxs) {
		this.cpxs = cpxs;
	}

	public String getCpdj() {
		return cpdj;
	}

	public void setCpdj(String cpdj) {
		this.cpdj = cpdj;
	}

	public String getNbxs() {
		return nbxs;
	}

	public void setNbxs(String nbxs) {
		this.nbxs = nbxs;
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

	public String getJldw() {
		return jldw;
	}

	public void setJldw(String jldw) {
		this.jldw = jldw;
	}
}
