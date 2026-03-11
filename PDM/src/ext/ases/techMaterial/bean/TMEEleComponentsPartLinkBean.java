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
 * 元器件
 * 
 */
public class TMEEleComponentsPartLinkBean  implements GwPersistable {

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
	public static String XHGG = "XHGG";// 型号规格
	public static String ZLDJ = "ZLDJ";// 质量等级
	public static String ZGF = "ZGF";// 总规范
	public static String XXGF = "XXGF";// 详细规范
	public static String XH = "XH";// 型号
	public static String FZXS = "FZXS";// 封装形式
	public static String WXCC = "WXCC";// 外形尺寸
	public static String ZYTJ = "ZYTJ";// 专用条件
	public static String FJXY = "FJXY";// 附加协议
	public static String TSSM = "TSSM";// 特殊说明
	public static String SFJK = "SFJK";// 是否进口
	public static String KFSZBTID = "KFSZBTID";// 抗辐射指标TID
	public static String KFSZBSEE = "KFSZBSEE";// 抗辐射指标SEE
	public static String XNCS = "XNCS";// 性能参数
	public static String SFJDMG = "SFJDMG";// 是否静电敏感
	public static String JDMGDJ = "JDMGDJ";// 静电敏感等级
	public static String SMDJ = "SMDJ";// 湿敏等级
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
	public  String xhgg ;// 型号规格
	public  String zldj ;// 质量等级
	public  String zgf ;// 总规范
	public  String xxgf ;// 详细规范
	public  String xh;// 型号
	public  String fzxs ;// 封装形式
	public  String wxcc ;// 外形尺寸
	public  String zytj ;// 专用条件
	public  String fjxy ;// 附加协议
	public  String tssm ;// 特殊说明
	public  String sfjk ;// 是否进口
	public  String kfszbtid ;// 抗辐射指标TID
	public  String kfszbsee ;// 抗辐射指标SEE
	public  String xncs ;// 性能参数
	public  String sfjdmg ;// 是否静电敏感
	public  String jdmgdj ;// 静电敏感等级
	public  String smdj ;// 湿敏等级
	public  String sccj ;// 生产厂家
	public  String jldw ;// 计量单位
	public  String wzfl ;// 物资分类

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

	public String getWzfl() {
		return wzfl;
	}

	public void setWzfl(String wzfl) {
		this.wzfl = wzfl;
	}

	public String getJldw() {
		return jldw;
	}

	public void setJldw(String jldw) {
		this.jldw = jldw;
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
			this.setXhgg(rs.getString(XHGG));
			this.setZldj(rs.getString(ZLDJ));
			this.setZgf(rs.getString(ZGF));
			this.setXxgf(rs.getString(XXGF));
			this.setXh(rs.getString(XH));
			this.setFzxs(rs.getString(FZXS));
			this.setWxcc(rs.getString(WXCC));
			this.setZytj(rs.getString(ZYTJ));
			this.setFjxy(rs.getString(FJXY));
			this.setTssm(rs.getString(TSSM));
			this.setSfjk(rs.getString(SFJK));
			this.setKfszbtid(rs.getString(KFSZBTID));
			this.setKfszbsee(rs.getString(KFSZBSEE));
			this.setXncs(rs.getString(XNCS));
			this.setSfjdmg(rs.getString(SFJDMG));
			this.setJdmgdj(rs.getString(JDMGDJ));
			this.setSmdj(rs.getString(SMDJ));
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
		ret.put(WZBM,wzbm );
		ret.put(WZMC,wzmc );
		ret.put(WZJC,wzjc );
		ret.put(BMYXJB, bmyxjb);
		ret.put(BMZT, bmzt);
		ret.put(BMLX,bmlx );
		ret.put(BMDJ,bmdj );
		ret.put(XHGG,xhgg );
		ret.put(ZLDJ, zldj);
		ret.put(ZGF,zgf );
		ret.put(XXGF,xxgf );
		ret.put(XH, xh);
		ret.put(FZXS, fzxs);
		ret.put(WXCC,wxcc );
		ret.put(ZYTJ, zytj);
		ret.put(FJXY,fjxy );
		ret.put(TSSM,tssm );
		ret.put(SFJK, sfjk);
		ret.put(KFSZBTID, kfszbtid);
		ret.put(KFSZBSEE,kfszbsee );
		ret.put(XNCS, xncs);
		ret.put(SFJDMG,sfjdmg );
		ret.put(JDMGDJ,jdmgdj );
		ret.put(SMDJ, smdj);
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
		ret.put(WZBM,wzbm );
		ret.put(WZMC,wzmc );
		ret.put(WZJC,wzjc );
		ret.put(BMYXJB, bmyxjb);
		ret.put(BMZT, bmzt);
		ret.put(BMLX,bmlx );
		ret.put(BMDJ,bmdj );
		ret.put(XHGG,xhgg );
		ret.put(ZLDJ, zldj);
		ret.put(ZGF,zgf );
		ret.put(XXGF,xxgf );
		ret.put(XH, xh);
		ret.put(FZXS, fzxs);
		ret.put(WXCC,wxcc );
		ret.put(ZYTJ, zytj);
		ret.put(FJXY,fjxy );
		ret.put(TSSM,tssm );
		ret.put(SFJK, sfjk);
		ret.put(KFSZBTID, kfszbtid);
		ret.put(KFSZBSEE,kfszbsee );
		ret.put(XNCS, xncs);
		ret.put(SFJDMG,sfjdmg );
		ret.put(JDMGDJ,jdmgdj );
		ret.put(SMDJ, smdj);
		ret.put(SCCJ, sccj);
		ret.put(JLDW,jldw);
		ret.put(WZFL,wzfl);
		return ret;
	}
	public String getSccj() {
		return sccj;
	}

	public void setSccj(String sccj) {
		this.sccj = sccj;
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

	public String getWzbm() {
		return wzbm;
	}

	public void setWzbm(String wzbm) {
		this.wzbm = wzbm;
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

	public String getXhgg() {
		return xhgg;
	}

	public void setXhgg(String xhgg) {
		this.xhgg = xhgg;
	}

	public String getZldj() {
		return zldj;
	}

	public void setZldj(String zldj) {
		this.zldj = zldj;
	}

	public String getZgf() {
		return zgf;
	}

	public void setZgf(String zgf) {
		this.zgf = zgf;
	}

	public String getXxgf() {
		return xxgf;
	}

	public void setXxgf(String xxgf) {
		this.xxgf = xxgf;
	}

	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public String getFzxs() {
		return fzxs;
	}

	public void setFzxs(String fzxs) {
		this.fzxs = fzxs;
	}

	public String getWxcc() {
		return wxcc;
	}

	public void setWxcc(String wxcc) {
		this.wxcc = wxcc;
	}

	public String getZytj() {
		return zytj;
	}

	public void setZytj(String zytj) {
		this.zytj = zytj;
	}

	public String getFjxy() {
		return fjxy;
	}

	public void setFjxy(String fjxy) {
		this.fjxy = fjxy;
	}

	public String getKfszbtid() {
		return kfszbtid;
	}

	public void setKfszbtid(String kfszbtid) {
		this.kfszbtid = kfszbtid;
	}

	public String getKfszbsee() {
		return kfszbsee;
	}

	public void setKfszbsee(String kfszbsee) {
		this.kfszbsee = kfszbsee;
	}

	public String getXncs() {
		return xncs;
	}

	public void setXncs(String xncs) {
		this.xncs = xncs;
	}

	public String getSfjdmg() {
		return sfjdmg;
	}

	public void setSfjdmg(String sfjdmg) {
		this.sfjdmg = sfjdmg;
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

	public static void main(String[] args) {
	}


	


}
