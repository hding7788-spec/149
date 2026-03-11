package com.glaway.mpm.pbom.db;

import java.io.Serializable;

import wt.part.WTPart;

/**
 * 物资库<br>
 * 名称 字段 备注<br>
 * 物资编码 Invcode <br>
 * 物资名称 Invname <br>
 * 型号/牌号/材料 Invtype <br>
 * 规格 Invspec <br>
 * 总规范/技术条件 Def2 <br>
 * 详细规范 Def3 <br>
 * 国产/进口 Def1 关联bd_cubasdoc中的pk_cubasdoc,取def1值（Y是进口，N是国产）<br>
 * 供应商 Def1 关联bd_cubasdoc中的pk_cubasdoc,取custname值<br>
 * 计量单位 Pk_measdoc 关联bd_measdoc中的pk_measdoc,取measname值<br>
 * 质量等级 Def4 <br>
 * 封装形式 Def5 <br>
 * 尺寸 Def6 <br>
 * 专用条件 Def7 <br>
 * 附加协议 Def8 <br>
 * 电参数特选要求 Def9 <br>
 * 存货分类编码 Pk_invcl 关联bd_invcl中的pk_invcl,取Invclasscode值<br>
 * 01: 元器件 02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品<br>
 * 存货分类名称 Pk_invcl 关联bd_invcl中的pk_invcl,取Invclassname值<br>
 *
 *
 */
public class Wzk implements Serializable {
  //已显示：存货编码、名称、型号牌号、规格、技术条件、生产厂家、主计量单位、附加条件、供应状态/热处理
	private String invcode;
	private String invname;

	private String clcode;// 材料编码
	private String clName;// 材料名称
	private String invtype;
	private String invspec;
	private String def2;//总规范/技术条件  （取值改为：jstjname）
	private String def3;//详细规范  不要
	private String def1;//国产/进口 不要
	private String custname;//生产厂家
	private String measname;//（主）计量单位
	private String def4;//质量等级 （取值改为：measname）
	private String def5;//产品代号 （缺）
	private String def6;//封装形式 （缺）
	private String def7;//型号 （缺）
	private String def8;// 精度等级 （缺）
	private String def9;//
	private String def10;//电参数特选要求 （缺）
	private String invclasscode;
	private String invclassname;//存货分类名称
	private String wzlb;//物资类别
	private String erprq; // 日期
	private boolean isUpdateAttribute = false; // 是否修改属性

	// @ TODO 149
	private String jsgfbz; // 技术条件名称 （取值改为：jstjname）
	private String fjtjname; // 附加条件名称
	private String def12; // 螺纹规格/公称尺寸 （缺）
	private String def14; // 机械性能等级 （缺）
	private String def13; // 供应状态/热处理
	private String zldj; // 质量等级名称 （缺）
	private String pk_measdoc;//计量单位 （取值改为：measname）

	private String jstjname;//技术条件
	public boolean isUpdateAttribute() {
		return isUpdateAttribute;
	}

	public void setUpdateAttribute(boolean isUpdateAttribute) {
		this.isUpdateAttribute = isUpdateAttribute;
	}

	public String getErprq() {
		return erprq;
	}

	public void setErprq(String erprq) {
		this.erprq = erprq;
	}

	// 扩展字段
	private String sjth; // 上级图号 -> pNumber
	private String th; // 图号 -> Number
	private String zxsl;// 子项数量
	private String mpcc;// 毛坯尺寸
	private String ppartNumber;

	// 生成PBOM模板代码
	private String gsdm; // 公司代码
	private String gcbm;// 工厂编码
	private String cpbm;// 产品编码
	private String version;
	private String fxsl;// 父项数量
	private String sjthflbm;// 上级图号分类编码
	private String sjthmc;// 上级图号名称
	private String sjthssxh;// 上级图号所属型号
	private String sjthyzjd; // 上级图号研制阶段
	private String sjthjldwmc;// 上级图号计量单位名称
	private String btbz;// 表头备注
	private String sfmr;// 是否默认
	private String clflbm;// 材料分类编码
	private String thmc;// 图号名称
	private String ssxh;// 所属型号
	private String yzjd;// 研制阶段
	private String clmc;// 材料名称
	private String djgyde;// 单机工艺定额
	private String mpkzjs;// 下料尺寸
	private String btibz;// 标体备注
	private String md; // 密度
	private String wzzyx1;// 物资自由项1
	private String wzzyx2;// 物资自由项2
	private String wzzyx3;// 物资自由项3

	private WTPart ppart;
	private WTPart part;
	private int opType = 0;// 操作类型 0：没有操作 1:物资编码操作 2：材料操作 3：添加零部件操作


	@Override
	public String toString() {
		return "invcode:"+this.invcode+"    invname:"+this.invname;
     }
	public String getDef10() {
		return def10;
	}

	public void setDef10(String def10) {
		this.def10 = def10;
	}

	public String getPk_measdoc() {
		return pk_measdoc;
	}

	public void setPk_measdoc(String pk_measdoc) {
		this.pk_measdoc = pk_measdoc;
	}

	public String getJstjname() {
		return jstjname;
	}

	public void setJstjname(String jstjname) {
		this.jstjname = jstjname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((th == null) ? 0 : th.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wzk other = (Wzk) obj;
		if (invcode == null) {
			if (other.invcode != null)
				return false;
		} else if (!invcode.equals(other.invcode))
			return false;
		return true;
	}

	public String getInvcode() {
		return invcode;
	}

	public void setInvcode(String invcode) {
		this.invcode = invcode;
	}

	public String getInvname() {
		return invname;
	}

	public void setInvname(String invname) {
		this.invname = invname;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getInvspec() {
		return invspec;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

	public String getDef2() {
		return def2;
	}

	public void setDef2(String def2) {
		this.def2 = def2;
	}

	public String getDef3() {
		return def3;
	}

	public void setDef3(String def3) {
		this.def3 = def3;
	}

	public String getDef1() {
		return def1;
	}

	public void setDef1(String def1) {
		this.def1 = def1;
	}

	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}

	public String getMeasname() {
		return measname;
	}

	public void setMeasname(String measname) {
		this.measname = measname;
	}

	public String getDef4() {
		return def4;
	}

	public void setDef4(String def4) {
		this.def4 = def4;
	}

	public String getDef5() {
		return def5;
	}

	public void setDef5(String def5) {
		this.def5 = def5;
	}

	public String getDef6() {
		return def6;
	}

	public void setDef6(String def6) {
		this.def6 = def6;
	}

	public String getDef7() {
		return def7;
	}

	public void setDef7(String def7) {
		this.def7 = def7;
	}

	public String getDef8() {
		return def8;
	}

	public void setDef8(String def8) {
		this.def8 = def8;
	}

	public String getDef9() {
		return def9;
	}

	public void setDef9(String def9) {
		this.def9 = def9;
	}

	public String getInvclasscode() {
		return invclasscode;
	}

	public void setInvclasscode(String invclasscode) {
		this.invclasscode = invclasscode;
	}

	public String getInvclassname() {
		return invclassname;
	}

	public void setInvclassname(String invclassname) {
		this.invclassname = invclassname;
	}

	public String getWzlb() {
		return wzlb;
	}

	public void setWzlb(String wzlb) {
		this.wzlb = wzlb;
	}

	public String getIsJinKou() {
		String tem = "国产";
		if ("Y".equals(this.getDef1())) {
			tem = "进口";
		}
		return tem;
	}

	public String getSjth() {
		return sjth;
	}

	public void setSjth(String sjth) {
		this.sjth = sjth;
	}

	public String getTh() {
		return th;
	}

	public void setTh(String th) {
		this.th = th;
	}

	public String getZxsl() {
		return zxsl;
	}

	public void setZxsl(String zxsl) {
		this.zxsl = zxsl;
	}

	public String getMpcc() {
		return mpcc;
	}

	public void setMpcc(String mpcc) {
		this.mpcc = mpcc;
	}

	public String getGsdm() {
		return gsdm;
	}

	public void setGsdm(String gsdm) {
		this.gsdm = gsdm;
	}

	public String getGcbm() {
		return gcbm;
	}

	public void setGcbm(String gcbm) {
		this.gcbm = gcbm;
	}

	public String getCpbm() {
		return cpbm;
	}

	public void setCpbm(String cpbm) {
		this.cpbm = cpbm;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFxsl() {
		return fxsl;
	}

	public void setFxsl(String fxsl) {
		this.fxsl = fxsl;
	}

	public String getSjthflbm() {
		return sjthflbm;
	}

	public void setSjthflbm(String sjthflbm) {
		this.sjthflbm = sjthflbm;
	}

	public String getSjthmc() {
		return sjthmc;
	}

	public void setSjthmc(String sjthmc) {
		this.sjthmc = sjthmc;
	}

	public String getSjthssxh() {
		return sjthssxh;
	}

	public void setSjthssxh(String sjthssxh) {
		this.sjthssxh = sjthssxh;
	}

	public String getSjthyzjd() {
		return sjthyzjd;
	}

	public void setSjthyzjd(String sjthyzjd) {
		this.sjthyzjd = sjthyzjd;
	}

	public String getSjthjldwmc() {
		return sjthjldwmc;
	}

	public void setSjthjldwmc(String sjthjldwmc) {
		this.sjthjldwmc = sjthjldwmc;
	}

	public String getBtbz() {
		return btbz;
	}

	public void setBtbz(String btbz) {
		this.btbz = btbz;
	}

	public String getSfmr() {
		return sfmr;
	}

	public void setSfmr(String sfmr) {
		this.sfmr = sfmr;
	}

	public String getClflbm() {
		return clflbm;
	}

	public void setClflbm(String clflbm) {
		this.clflbm = clflbm;
	}

	public String getThmc() {
		return thmc;
	}

	public void setThmc(String thmc) {
		this.thmc = thmc;
	}

	public String getSsxh() {
		return ssxh;
	}

	public void setSsxh(String ssxh) {
		this.ssxh = ssxh;
	}

	public String getYzjd() {
		return yzjd;
	}

	public void setYzjd(String yzjd) {
		this.yzjd = yzjd;
	}

	public String getClmc() {
		if ("01".equals(this.wzlb) || "02".equals(this.wzlb)) {
			return this.invname;
		}
		return clmc;
	}

	public void setClmc(String clmc) {
		this.clmc = clmc;
	}

	public String getDjgyde() {
		return djgyde;
	}

	public void setDjgyde(String djgyde) {
		this.djgyde = djgyde;
	}

	public String getMpkzjs() {
		return mpkzjs;
	}

	public void setMpkzjs(String mpkzjs) {
		this.mpkzjs = mpkzjs;
	}

	public String getBtibz() {
		return btibz;
	}

	public void setBtibz(String btibz) {
		this.btibz = btibz;
	}

	public String getMd() {
		return md;
	}

	public void setMd(String md) {
		this.md = md;
	}

	public String getWzzyx1() {
		return wzzyx1;
	}

	public void setWzzyx1(String wzzyx1) {
		this.wzzyx1 = wzzyx1;
	}

	public String getWzzyx2() {
		return wzzyx2;
	}

	public void setWzzyx2(String wzzyx2) {
		this.wzzyx2 = wzzyx2;
	}

	public String getWzzyx3() {
		return wzzyx3;
	}

	public void setWzzyx3(String wzzyx3) {
		this.wzzyx3 = wzzyx3;
	}

	public String getPpartNumber() {
		return ppartNumber;
	}

	public void setPpartNumber(String ppartNumber) {
		this.ppartNumber = ppartNumber;
	}

	public WTPart getPpart() {
		return ppart;
	}

	public void setPpart(WTPart ppart) {
		this.ppart = ppart;
	}

	public WTPart getPart() {
		return part;
	}

	public void setPart(WTPart part) {
		this.part = part;
	}

	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}

	public String getClcode() {
		return clcode;
	}

	public void setClcode(String clcode) {
		this.clcode = clcode;
	}

	public String getClName() {
		return clName;
	}

	public void setClName(String clName) {
		this.clName = clName;
	}

	public String getJsgfbz() {
		return jsgfbz;
	}

	public void setJsgfbz(String jsgfbz) {
		this.jsgfbz = jsgfbz;
	}

	public String getFjtjname() {
		return fjtjname;
	}

	public void setFjtjname(String fjtjname) {
		this.fjtjname = fjtjname;
	}

	public String getDef12() {
		return def12;
	}

	public void setDef12(String def12) {
		this.def12 = def12;
	}

	public String getDef14() {
		return def14;
	}

	public void setDef14(String def14) {
		this.def14 = def14;
	}

	public String getZldj() {
		return zldj;
	}

	public void setZldj(String zldj) {
		this.zldj = zldj;
	}

	public String getDef13() {
		return def13;
	}

	public void setDef13(String def13) {
		this.def13 = def13;
	}

}
