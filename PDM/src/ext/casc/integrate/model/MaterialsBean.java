package ext.casc.integrate.model;

import java.io.Serializable;

public class MaterialsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	// 工艺文件编号
	private String pplanNumber = "";
	// 工艺文件名称
	private String pplanName= "";
	// 工艺文件版本
	private String pplanVersion= "";
	// 工艺文件状态
	private String pplanState= "";
	// 材料类别：外购件
	private String materialType= "";
	// 材料定额类别：主要材料定额、原材料类别、试件材料定额
	private String usingType= "";
	// 材料定额状态
	private String materialState= "";
	// 存货编码
	private String chbm= "";
	// 存货名称
	private String chmc= "";
	// 下料尺寸
	private String xlcc= "";
	// 可制件数
	private String kzjs= "";
	// 试件尺寸
	private String sjcc= "";
	// 试件可制件数
	private String sjkzjs= "";
	// 试件数量
	private String sjsl= "";
	// 主要材料数量
	private String sl= "";
	// 主计量单位
	private String zjldw= "";
	//单位
	private String dw= "";

	//数据源
	private String dataFrom= "";
//备注
	private String comment= "";

	private String processFileNumber= "";

	private String pplanType= "";
	private String zfflag= "";
	private String dept= "";

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
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

	//批次
	private String batch;
	@Override
	public String toString() {
		return "pplanNumber:" + pplanNumber + "  pplanName:" + pplanName
				+ "  pplanVersion:" + pplanVersion + "  pplanState:"
				+ pplanState + "  materialType:" + materialType + "  usingType:"
				+ usingType + "  materialState:" + materialState + "  chbm:"
				+ chbm + "  chmc:" + chmc + "  xlcc:" + xlcc + "  kzjs:" + kzjs
				+ "  sjcc:" + sjcc + "  sjkzjs:" + sjkzjs + "  sjsl:" + sjsl
				+ "  sl:" + sl + "  zjldw:" + zjldw + "  dataFrom:" + dataFrom + " dw:"+ dw +" comment:" + comment;
	}


	public String getProcessFileNumber() {
		return processFileNumber;
	}


	public void setProcessFileNumber(String processFileNumber) {
		this.processFileNumber = processFileNumber;
	}


	public String getBatch() {
		return batch;
	}


	public void setBatch(String batch) {
		this.batch = batch;
	}


	public String getDw() {
		return dw;
	}

	public void setDw(String dw) {
		this.dw = dw;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public String getPplanVersion() {
		return pplanVersion;
	}

	public void setPplanVersion(String pplanVersion) {
		this.pplanVersion = pplanVersion;
	}

	public String getPplanState() {
		return pplanState;
	}

	public void setPplanState(String pplanState) {
		this.pplanState = pplanState;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public String getMaterialState() {
		return materialState;
	}

	public String getUsingType() {
		return usingType;
	}

	public void setUsingType(String usingType) {
		this.usingType = usingType;
	}

	public void setMaterialState(String materialState) {
		this.materialState = materialState;
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

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public String getZjldw() {
		return zjldw;
	}

	public void setZjldw(String zjldw) {
		this.zjldw = zjldw;
	}

	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

}
