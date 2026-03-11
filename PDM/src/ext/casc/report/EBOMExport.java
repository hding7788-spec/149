package ext.casc.report;

import java.util.Hashtable;

public class EBOMExport {
	/*零部件编号*/
	private String partNumber;
	/*零部件名称*/
	private String partName;
	/*所在产品库*/
	private String containerName;
	/*生命周期状态*/
	private String state;
	/*版本*/
	private String version;
	/*创建时间*/
	private String createTime;
	/*上次修改时间*/
	private String modifyTime;
	/*父件编号*/
	private String fPartNumber;
	/*父件名称*/
	private String fPartName;
	/*装配数量*/
	private double amount;
	/*IBA属性*/
	private Hashtable ibaMap;

	/*成套件标识	SETMARK*/
	/*板拧形式	PLATECSCREWRORM*/
	/*八部更改标识	changeType*/
	/*备注	REMARK*/
	/*编码类型	BMLX*/
	/*编码状态	BMZT*/
	/*标准号	STANDARDNUMBER*/
	/*表面处理	SURFACETREATMENT*/
	/*材料	CMAT*/
	/*材料编号	NUMBER*/
	/*材料单位	CLDW*/
	/*材料类型	MATTYPE*/
	/*材料名称	PTC_MATERIAL_NAME*/
	/*材料牌号	MATERIAL*/
	/*材料上标	CMAT_UP*/
	/*材料下标	CMAT_DOWN*/
	/*产品代号	P_INDEX*/
	/*产品等级	PRODUCT_LEVEL*/
	/*产品型式	PRODUCT_FORM*/
	/*当前阶段	PHASE_CODE*/
	/*二维工程图图幅	size*/
	/*封装形式	PACKAGINGFORM*/
	/*辅制车间	FZCJ*/
	/*附加协议	EXTRACONDITION*/
	/*工艺路线	ROUTING*/
	/*工装代号	PRODUCT_INDEX*/
	/*供应商	SUPPLIERS*/
	/*关重件标识	KEYCOMPONENT*/
	/*规格	CSIZE*/
	/*机械性能等级或硬度	MECHANICALPROPERTYORHARDNESS*/
	/*基体材料名称	JBCLMC*/
	/*计量单位	MEASURENIT*/
	/*技术条件	JSTJ*/
	/*技术条件标准号	JSTJBZH*/
	/*精度等级	JDDJ*/
	/*零部件分类	CTYPE*/
	/*零组件生产类型	MTYPE*/
	/*密级	SECRET*/
	/*名称	name*/
	/*批次	BATCH*/
	/*品种规格标准号	PZGGBZH*/
	/*热处理	HEATTRATMENT*/
	/*设计单位	COMPANY*/
	/*设计者	DESIGNER*/
	/*是否进口	ISIMPORT*/
	/*适用范围	SCOPE*/
	/*所属成品	ENDITEMIN*/
	/*所属型号	MINDEX*/
	/*特殊说明	SPECAILINSTRUCTION*/
	/*图号	CINDEX*/
	/*外形尺寸	OUTLINESIZE*/
	/*物资简称	SHORTNAME*/
	/*详细规范	DETAILSTANDARD*/
	/*型号	TYPE*/
	/*型号规格	TYPESTANDARD*/
	/*型号牌号	XHPH*/
	/*增强材料标准号	ZQCLBZH*/
	/*增强材料名称	ZQCLMC*/
	/*质量等级	QUALITYLEVEL*/
	/*质量等级（旧）	ZLDJ*/
	/*中文名称	PTC_COMMON_NAME*/
	/*主制车间	ZZCJ*/
	/*专用条件	SPECIALCONDITION*/
	/*总规范	TOLALSTANDARD*/
	/*分类	classification*/

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getfPartNumber() {
		return fPartNumber;
	}

	public void setfPartNumber(String fPartNumber) {
		this.fPartNumber = fPartNumber;
	}

	public String getfPartName() {
		return fPartName;
	}

	public void setfPartName(String fPartName) {
		this.fPartName = fPartName;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Hashtable getIbaMap() {
		return ibaMap;
	}

	public void setIbaMap(Hashtable ibaMap) {
		this.ibaMap = ibaMap;
	}

}
