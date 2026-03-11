package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

public class KnifeTool implements Serializable, Comparable<KnifeTool> {
	private static final long serialVersionUID = 1L;

	private String oid;

	private String type;
	/**
	 * 工装编号
	 */
	private String knifeToolNum;
	/**
	 * 工装名称
	 */
	private String knifeToolName;
	/**
	 * 工装标准号
	 */
	private String knifeToolStdNumber;
	/**
	 * 工装规格
	 */
	private String knifeToolSpec;
	//英文名称
    private String EngLishName;


    public String getEngLishName() {
        return EngLishName;
    }

    public void setEngLishName(String engLishName) {
        EngLishName = engLishName;
    }

	//TODO 149  材料
	private String cmat;
	//TODO 149  刃口直径
	private String rkzj;
	//TODO 149  夹持直径
	private String jczj;
	//TODO 149  刃口长度
	private String rkcd;
	//TODO 149  总长度
	private String zcd;
	//TODO 149  公差
	private String gc;
	//TODO 149  最小加工尺寸
	private String zxjgcc;
	//TODO 149  最大加工尺寸
	private String zdjgcc;
	//TODO 149  结构形式
	private String jgxs;
	//TODO 149  接口类型
	private String jklx;
	//TODO 149  技术备注
	private String jsbz;
	//TODO 149  刃口圆角半径
	private String rkyjbj;
	//TODO 149  齿数
	private String cs;
	//TODO 149  类别
	private String knifetype;
	//TODO 149 规格
	private String csize;

	public KnifeTool() {
		super();
	}

	public KnifeTool(String oid, String knifeToolNum, String knifeToolName, String knifeToolStdNumber,
			String knifeToolSpec) {
		super();
		this.oid = oid;
		this.knifeToolNum = knifeToolNum;
		this.knifeToolName = knifeToolName;
		this.knifeToolStdNumber = knifeToolStdNumber;
		this.knifeToolSpec = knifeToolSpec;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKnifeToolNum() {
		return knifeToolNum;
	}

	public void setKnifeToolNum(String knifeToolNum) {
		this.knifeToolNum = knifeToolNum;
	}

	public String getKnifeToolName() {
		return knifeToolName;
	}

	public void setKnifeToolName(String knifeToolName) {
		this.knifeToolName = knifeToolName;
	}

	public String getKnifeToolStdNumber() {
		return knifeToolStdNumber;
	}

	public void setKnifeToolStdNumber(String knifeToolStdNumber) {
		this.knifeToolStdNumber = knifeToolStdNumber;
	}

	public String getKnifeToolSpec() {
		return knifeToolSpec;
	}

	public void setKnifeToolSpec(String knifeToolSpec) {
		this.knifeToolSpec = knifeToolSpec;
	}

	public int compareTo(KnifeTool o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getKnifeToolName(), o.getKnifeToolName());
	}

	public String getKnifetype() {
		return knifetype;
	}

	public void setKnifetype(String knifetype) {
		this.knifetype = knifetype;
	}

	public String getCsize() {
		return csize;
	}

	public void setCsize(String csize) {
		this.csize = csize;
	}

	public String getCmat() {
		return cmat;
	}

	public void setCmat(String cmat) {
		this.cmat = cmat;
	}

	public String getRkzj() {
		return rkzj;
	}

	public void setRkzj(String rkzj) {
		this.rkzj = rkzj;
	}

	public String getJczj() {
		return jczj;
	}

	public void setJczj(String jczj) {
		this.jczj = jczj;
	}

	public String getRkcd() {
		return rkcd;
	}

	public void setRkcd(String rkcd) {
		this.rkcd = rkcd;
	}

	public String getZcd() {
		return zcd;
	}

	public void setZcd(String zcd) {
		this.zcd = zcd;
	}

	public String getGc() {
		return gc;
	}

	public void setGc(String gc) {
		this.gc = gc;
	}

	public String getZxjgcc() {
		return zxjgcc;
	}

	public void setZxjgcc(String zxjgcc) {
		this.zxjgcc = zxjgcc;
	}

	public String getZdjgcc() {
		return zdjgcc;
	}

	public void setZdjgcc(String zdjgcc) {
		this.zdjgcc = zdjgcc;
	}

	public String getJgxs() {
		return jgxs;
	}

	public void setJgxs(String jgxs) {
		this.jgxs = jgxs;
	}

	public String getJklx() {
		return jklx;
	}

	public void setJklx(String jklx) {
		this.jklx = jklx;
	}

	public String getJsbz() {
		return jsbz;
	}

	public void setJsbz(String jsbz) {
		this.jsbz = jsbz;
	}

	public String getRkyjbj() {
		return rkyjbj;
	}

	public void setRkyjbj(String rkyjbj) {
		this.rkyjbj = rkyjbj;
	}

	public String getCs() {
		return cs;
	}

	public void setCs(String cs) {
		this.cs = cs;
	}

}
