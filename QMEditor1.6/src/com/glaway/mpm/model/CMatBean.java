package com.glaway.mpm.model;

import java.io.Serializable;

public class CMatBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String pplanNumber;//工艺文件编号
	private String pplanName;//工艺文件名称
	private String technicsNumber;//工艺文件文档编号
	private String technicsName;//工艺文件文档名称
	private String partOid;//零部件oid
	private String technicsType;//工艺文件类型
	private String partUseCount;//零部件使用数量
	private String partName;//零件名称
	private String cindex;//零件图号

	private String cmatType;//材料类型：原材料,主要材料,试件原材料
	private String chbm;// "存货编码"
	private String chmc;// "存货名称"
	private String xlcc;// "下料尺寸"
	private String kzjs;// "可制件数"
	private String sl;// "数量"
	private String sjcc;// "试件尺寸"
	private String sjkzjs;// "试件可制件数"
	private String sjsl;// "试件数量"
	private String xhph;// "型号牌号"
	private String gg;// "规格"
	private String jstj;// "技术条件"
	private String dw;// "技术条件"
	private String sccj;// "生产厂家"
	private String zjldw;// "主计量单位"
	private String fjtj;// "附加条件"
	private String gyztrcl;// "供应状态/热处理"
	private String comment;//备注
	private String dataFrom;//数据源

	@Override
	public String toString() {
		return "CMatBean [pplanNumber=" + pplanNumber + ", pplanName="
				+ pplanName + ", technicsNumber=" + technicsNumber
				+ ", technicsName=" + technicsName + ", partOid=" + partOid
				+ ", technicsType=" + technicsType + ", partUseCount="
				+ partUseCount + ", partName=" + partName + ", cindex="
				+ cindex + ", cmatType=" + cmatType + ", chbm=" + chbm
				+ ", chmc=" + chmc + ", xlcc=" + xlcc + ", kzjs=" + kzjs
				+ ", sl=" + sl + ", sjcc=" + sjcc + ", sjkzjs=" + sjkzjs
				+ ", sjsl=" + sjsl + ", xhph=" + xhph + ", gg=" + gg
				+ ", jstj=" + jstj + ", sccj=" + sccj + ", zjldw=" + zjldw
				+ ", fjtj=" + fjtj + ", gyztrcl=" + gyztrcl + ", comment="
				+ comment + ", dataFrom=" + dataFrom + "]";
	}

	public String getDw() {
		return dw;
	}

	public void setDw(String dw) {
		this.dw = dw;
	}

	public String getPplanNumber() {
		return pplanNumber;
	}

	public void setPplanNumber(String pplanNumber) {
		this.pplanNumber = pplanNumber;
	}

	public String getPplanName() {
		return pplanName;
	}

	public void setPplanName(String pplanName) {
		this.pplanName = pplanName;
	}

	public String getCmatType() {
		return cmatType;
	}

	public void setCmatType(String cmatType) {
		this.cmatType = cmatType;
	}

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public void setTechnicsNumber(String technicsNumber) {
		this.technicsNumber = technicsNumber;
	}

	public String getTechnicsName() {
		return technicsName;
	}

	public void setTechnicsName(String technicsName) {
		this.technicsName = technicsName;
	}

	public String getPartOid() {
		return partOid;
	}

	public void setPartOid(String partOid) {
		this.partOid = partOid;
	}

	public String getTechnicsType() {
		return technicsType;
	}

	public void setTechnicsType(String technicsType) {
		this.technicsType = technicsType;
	}

	public String getPartUseCount() {
		return partUseCount;
	}

	public void setPartUseCount(String partUseCount) {
		this.partUseCount = partUseCount;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public String getCindex() {
		return cindex;
	}

	public void setCindex(String cindex) {
		this.cindex = cindex;
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

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public String getSjcc() {
		return sjcc;
	}

	public void setSjcc(String sjcc) {
		this.sjcc = sjcc;
	}

	public String getSjkzjs() {
		return sjkzjs;
	}

	public void setSjkzjs(String sjkzjs) {
		this.sjkzjs = sjkzjs;
	}

	public String getSjsl() {
		return sjsl;
	}

	public void setSjsl(String sjsl) {
		this.sjsl = sjsl;
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

	public String getZjldw() {
		return zjldw;
	}

	public void setZjldw(String zjldw) {
		this.zjldw = zjldw;
	}

	public String getFjtj() {
		return fjtj;
	}

	public void setFjtj(String fjtj) {
		this.fjtj = fjtj;
	}

	public String getGyztrcl() {
		return gyztrcl;
	}

	public void setGyztrcl(String gyztrcl) {
		this.gyztrcl = gyztrcl;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

}
