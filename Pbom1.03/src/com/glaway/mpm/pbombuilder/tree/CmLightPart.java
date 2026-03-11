package com.glaway.mpm.pbombuilder.tree;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

import javax.vecmath.Matrix4d;
import java.io.Serializable;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created on 2012-10-23
 *
 * @author chenyunlong
 */
public class CmLightPart implements Serializable {
	private static final long serialVersionUID = 4761654884653326205L;

	public static final CmLightPart EMPTY_PART = newLightPart("", "", "", 0L);
	private String partNumber;
	private String partName;
	private String partType = "";//middle(中间件)|assistant(辅件)|normal|zuhe(组合件)|mp(毛坯件)
	                             //PurchasedPart（外购件）|StandardPart(标准件)|ALKPart(科研件)|SHPart（三化件）
								 //middle2(中间件)(149特有)|NS(表示通过PBOM编辑器新建的标准件)
	private long containerId;
	private int status = 0;

	private long masterOid = 0;
	private String state = "";
	private Timestamp modifyTime;
	private URL pvURL = null;
	private String cadObid;
	private String cadNumberFile;
	private HashMap<String, Matrix4d> locators = null;
	private long oid = 0;
	private String dutu;//镀涂
	private String remark;//备注
	private int useCount = 1;//1
	private String UseCount_805;
	private String rate; //工艺辅件比例
	private boolean isKey;//关键件true|false"
	private boolean isSpecial;//特殊件true|false
	private String materialType;//物料类型
	private String backupRate;//工艺备份比例
	private String maxBackupCount;//最大备份数
	private String backupReason;//备份原因
	private String workShop;//制造单位
	private String outsourcingUnits;//建议外协单位
	private String materialNumber;//材料编号
	private String materialName;//材料名称
	private String materialBrand;//材料牌号
	private String materialCrision;//材料标准号
	private String responser;//工艺负责人
	private String responserGroup;//工艺负责组
	private String lifecycle;//生命周期状态
	private String e_version;//EBOM零件版本
	private String eu_number;//零件的图号
	private String eu_version;//零件的EBOM图号版本
	private String version;

	private String e_lifecycle;

	private int productionQuantity;//投产数量--工艺辅件
	private String productionRatio;//投产比例--工艺辅件
	private boolean isReversion;//是否有版本更新
	private boolean isEbomKey;//"关键件true|false"
	private boolean isSelected;//是否进行批量修改
	private boolean isEdit;//是否编辑过
	private boolean isEditOfAssistCount;//辅件的数量是否有修改
	private boolean isMove;//是否移动过
	private boolean isSearch;//是否被搜索到
	private String operType;// 节点(中间件、辅件)类型：新增(new)、删除(移除、设置虚拟件、剪切)(delete)、粘贴（""）
	private String middleIndex;//相同的整件下的工艺中间件有多个，加以区分
	private boolean spaceBorneTable;//星载表格化
	private int echangeIndex;//EBOM变更类型  ：1-版本变化，2-新增，3-删除
	private boolean isChange;//工艺更改标识
	private boolean isChangeOfStructure;//结构是否改变
	private int versionIndex;//升版本类型：  0-不升版，1-升小版，2-升大版
	private String parentPartNumber;//父节点的partNumber
	private boolean isHasXml;//零组件是否有xml
	private String modifyXmlPartNumber;//记录哪些被修改的零件需要保存xml
	private String parentPath;//记录父节点的路径
	private boolean isEditWithChild;
	private boolean structureSaved;//修改后的结构是否保存

	private int dialogType;
	private boolean isPart;

	private String clszrq; //材料设置日期
	//物资库
	private Wzk wzk = new Wzk();

	private String firstPlant ; //主制车间
	private Map<String,Boolean> secondePlant = new HashMap<String,Boolean>();  // 辅助车间

	//begin 149属性
	private String cmat;//材料
	private String ptc_material_name;//材料名称
	private String cmat_up;//材料上标
	private String cmat_down;//材料下标
	private String setmark;//成套件标识
	private String phase_code;//当前阶段
	private String keycomponent;//关重件标识
	private String csize;//规格
	private String ctype;//零部件分类
	private String secret;//密级
	private String enditemin;//所属成品
	private String mindex;//所属型号
	private String cindex;//图号
	private String ptc_common_name;//中文名称
	private String routing;//工艺路线
	private String company;//设计单位
	private String product_index;//工装代号
	private String designer;//设计者
	private String zzcj;//主制车间
	private String fzcj;//辅制车间
	private String material;//材料牌号
	private String pzggbzh;//品种规格标准号
	private String jstjbzh;//技术条件标准号
	private String jddj;//精度等级
	private String zldj;//质量等级
	private String clzt;//材料状态
	private String cldw;//材料单位
	private String zqclmc;//增强材料名称
	private String jbclmc;//基体材料名称
	private String zqclbzh;//增强材料标准号
	private String pindex;//产品代号

	private String chbm;//存货编码
	private String mtype;//零组件生产类型
	private String xhph;//型号牌号
	private String jstj;//技术条件
	private String batch;
	private String adjustable;//可调整

	private boolean isBorrowedPart = false; // 是否借用件
	//end

	/*
	 * 用于PBOM发布时记录PBOM的校验信息
	 */
	private String zgyNumber;// "主工艺编号"
	private String zgyzt;// "主工艺状态"
	private String zldezt;// "材料定额状态"
	private String isOk;

	private String gysl;//记录工艺数量

	//设计资源库应用改造新增属性
	//start
	private String shortname;//物资简称
	private String yxjb;//编码优选级别
	private String bmzt;//编码状态
	private String bmlx;//编码类型
	private String standardnumber;//标准号
	private String mechanicalpropertyorhardness;//机械性能等级或硬度
	private String surfacetreatment;//表面处理
	private String heattreatment;//热处理
	private String productform;//产品型式
	private String productlevel;//产品等级
	private String platecscrewform;//板拧形式
	private String isimport;//是否进口
	private String specialinstruction;//特殊说明
	private String measureunit;//计量单位
	private String type;//型号
	private String typestandard;//型号规格
	private String qualitylevel;//质量等级
	private String totalstandard;//总规范
	private String detailstandard;//详细规范
	private String packagingform;//封装形式
	private String outlinesize;//外形尺寸
	private String specialcondition;//专用条件
	private String extracondition;//附加协议
	private String mattype;//材料类型

	private String cmatnumber;//材料编号
	private String marknumber;//牌号
	private String supplystate;//供应状态
	private String usestandard;//采用标准
	//end


	public String toString() {
		return this.partNumber + "(" + this.partName + ") " + this.version
				+" cmat="+cmat+" cmat_up="+cmat_up+" cmat_down="+cmat_down+" phase_code="+phase_code+" keycomponent="+keycomponent+" csize="+csize
				+" ctype="+ctype+" zzcj="+zzcj+" fzcj="+fzcj+" material="+material+" pzggbzh="+pzggbzh+" jstjbzh="+jstjbzh
				+" mtype="+mtype+" version="+version+" versionIndex="+versionIndex;
	}

	public static CmLightPart newLightPart(String number, String name, String version, long containerId) {
		CmLightPart ret = new CmLightPart();

		ret.setPartNumber(number);
		ret.setPartName(name);
		ret.setVersion(version);
		ret.setContainerId(containerId);

		return ret;
	}

	public CmLightPart() {
	}

	public String getIdentifier() {
		return "wt.part.WTPart:" + getOid();
	}

	public String getUseCount_805() {
		return UseCount_805;
	}

	public void setUseCount_805(String useCount_805) {
		UseCount_805 = useCount_805;
	}

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

	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}

	public long getOid() {
		return oid;
	}

	public void setOid(long oid) {
		this.oid = oid;
	}

	public URL getPvURL() {
		return pvURL;
	}

	public void setPvURL(URL pvURL) {
		this.pvURL = pvURL;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Timestamp getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	public long getContainerId() {
		return containerId;
	}

	public void setContainerId(long containerId) {
		this.containerId = containerId;
	}

	public long getMasterOid() {
		return masterOid;
	}

	public void setMasterOid(long masterOid) {
		this.masterOid = masterOid;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public boolean isReversion() {
		return isReversion;
	}

	public void setReversion(boolean isReversion) {
		this.isReversion = isReversion;
	}

	public String getMaterialBrand() {
		return materialBrand;
	}

	public void setMaterialBrand(String materialBrand) {
		this.materialBrand = materialBrand;
	}

	public String getMaterialCrision() {
		return materialCrision;
	}

	public void setMaterialCrision(String materialCrision) {
		this.materialCrision = materialCrision;
	}

	public String getResponser() {
		return responser;
	}

	public void setResponser(String responser) {
		this.responser = responser;
	}

	public String getResponserGroup() {
		return responserGroup;
	}

	public void setResponserGroup(String responserGroup) {
		this.responserGroup = responserGroup;
	}

	public String getLifecycle() {
		return lifecycle;
	}

	public void setLifecycle(String lifecycle) {
		this.lifecycle = lifecycle;
	}

	public String getE_version() {
		return e_version;
	}

	public void setE_version(String eVersion) {
		e_version = eVersion;
	}


	public String getEu_number() {
		return eu_number;
	}

	public void setEu_number(String euNumber) {
		eu_number = euNumber;
	}

	public String getEu_version() {
		return eu_version;
	}

	public void setEu_version(String euVersion) {
		eu_version = euVersion;
	}

	public String getOutsourcingUnits() {
		return outsourcingUnits;
	}

	public void setOutsourcingUnits(String outsourcingUnits) {
		this.outsourcingUnits = outsourcingUnits;
	}


	public int getProductionQuantity() {
		return productionQuantity;
	}

	public void setProductionQuantity(int productionQuantity) {
		this.productionQuantity = productionQuantity;
	}

	public String getProductionRatio() {
		return productionRatio;
	}

	public void setProductionRatio(String productionRatio) {
		this.productionRatio = productionRatio;
	}

	/**
	 * Sets the value of the attribute: cadObid;
	 *
	 * @param a_CadObid
	 **/
	public void setCadObid(String a_CadObid) {
		cadObid = a_CadObid;
	}

	public String getCadObid() {
		return cadObid;
	}

	public void setCadNumberFile(String a_CadNumberFile) {
		cadNumberFile = a_CadNumberFile;
	}

	public String getCadNumberFile() {
		return cadNumberFile;
	}

	public HashMap<String, Matrix4d> getLocators() {
		return locators;
	}

	public void setLocators(HashMap<String, Matrix4d> locMap) {
		this.locators = locMap;
	}

	public String getDutu() {
		return dutu;
	}

	public void setDutu(String dutu) {
		this.dutu = dutu;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public String getMaterialType() {
		return CmCommonStringUtil.isEmpty(materialType) ? "自制件":materialType ;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public String getBackupRate() {
		return backupRate;
	}

	public void setBackupRate(String backupRate) {
		this.backupRate = backupRate;
	}

	public String getMaxBackupCount() {
		return maxBackupCount;
	}

	public void setMaxBackupCount(String maxBackupCount) {
		this.maxBackupCount = maxBackupCount;
	}

	public String getBackupReason() {
		return backupReason;
	}

	public void setBackupReason(String backupReason) {
		this.backupReason = backupReason;
	}

	public String getWorkShop() {
		return workShop;
	}

	public void setWorkShop(String workShop) {
		this.workShop = workShop;
	}

	public boolean isEbomKey() {
		return isEbomKey;
	}

	public void setEbomKey(boolean isEbomKey) {
		this.isEbomKey = isEbomKey;
	}

	public boolean isEditOfAssistCount() {
		return isEditOfAssistCount;
	}

	public void setEditOfAssistCount(boolean isEditOfAssistCount) {
		this.isEditOfAssistCount = isEditOfAssistCount;
	}

	public boolean isMove() {
		return isMove;
	}

	public void setMove(boolean isMove) {
		this.isMove = isMove;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (containerId ^ (containerId >>> 32));
		result = PRIME * result + (int) (masterOid ^ (masterOid >>> 32));
		result = PRIME * result + ((modifyTime == null) ? 0 : modifyTime.hashCode());
		result = PRIME * result + ((partName == null) ? 0 : partName.hashCode());
		result = PRIME * result + ((partNumber == null) ? 0 : partNumber.hashCode());
		result = PRIME * result + (int) (oid ^ (oid >>> 32));
		result = PRIME * result + ((pvURL == null) ? 0 : pvURL.hashCode());
		result = PRIME * result + ((state == null) ? 0 : state.hashCode());
		result = PRIME * result + status;
		result = PRIME * result + ((partType == null) ? 0 : partType.hashCode());
		result = PRIME * result + ((version == null) ? 0 : version.hashCode());
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
		final CmLightPart other = (CmLightPart) obj;
		if (containerId != other.containerId)
			return false;
		if (masterOid != other.masterOid)
			return false;
		if (modifyTime == null) {
			if (other.modifyTime != null)
				return false;
		} else if (!modifyTime.equals(other.modifyTime))
			return false;
		if (partName == null) {
			if (other.partName != null)
				return false;
		} else if (!partName.equals(other.partName))
			return false;
		if (partNumber == null) {
			if (other.partNumber != null)
				return false;
		} else if (!partNumber.equals(other.partNumber))
			return false;
		if (oid != other.oid)
			return false;
		if (pvURL == null) {
			if (other.pvURL != null)
				return false;
		} else if (!pvURL.equals(other.pvURL))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (status != other.status)
			return false;
		if (partType == null) {
			if (other.partType != null)
				return false;
		} else if (!partType.equals(other.partType))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public boolean isSearch() {
		return isSearch;
	}

	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getMiddleIndex() {
		return middleIndex;
	}

	public void setMiddleIndex(String middleIndex) {
		this.middleIndex = middleIndex;
	}

	public boolean isSpaceBorneTable() {
		return spaceBorneTable;
	}

	public void setSpaceBorneTable(boolean spaceBorneTable) {
		this.spaceBorneTable = spaceBorneTable;
	}

	public int getEchangeIndex() {
		return echangeIndex;
	}

	public void setEchangeIndex(int echangeIndex) {
		this.echangeIndex = echangeIndex;
	}

	public boolean isChange() {
		return isChange;
	}

	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}

	public boolean isChangeOfStructure() {
		return isChangeOfStructure;
	}

	public void setChangeOfStructure(boolean isChangeOfStructure) {
		this.isChangeOfStructure = isChangeOfStructure;
	}

	public int getVersionIndex() {
		return versionIndex;
	}

	public void setVersionIndex(int versionIndex) {
		this.versionIndex = versionIndex;
	}

	public String getParentPartNumber() {
		return parentPartNumber;
	}

	public void setParentPartNumber(String parentPartNumber) {
		this.parentPartNumber = parentPartNumber;
	}

	public boolean isHasXml() {
		return isHasXml;
	}

	public void setHasXml(boolean isHasXml) {
		this.isHasXml = isHasXml;
	}

	public String getModifyXmlPartNumber() {
		return modifyXmlPartNumber;
	}

	public void setModifyXmlPartNumber(String modifyXmlPartNumber) {
		this.modifyXmlPartNumber = modifyXmlPartNumber;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public boolean isEditWithChild() {
		return isEditWithChild;
	}

	public void setEditWithChild(boolean isEditWithChild) {
		this.isEditWithChild = isEditWithChild;
	}

	public boolean isStructureSaved() {
		return this.structureSaved;
	}

	public void setStructureSaved(boolean structureSaved) {
		this.structureSaved = structureSaved;
	}

	public int getDialogType() {
		return dialogType;
	}

	public void setDialogType(int dialogType) {
		this.dialogType = dialogType;
	}

	public boolean isPart() {
		return isPart;
	}

	public void setPart(boolean isPart) {
		this.isPart = isPart;
	}

	public Wzk getWzk() {
		return wzk;
	}

	public void setWzk(Wzk wzk) {
		this.wzk = wzk;
	}

	public String getFirstPlant() {
		return firstPlant;
	}

	public void setFirstPlant(String firstPlant) {
		this.firstPlant = firstPlant;
	}



	public String getClszrq() {
		return clszrq;
	}

	public void setClszrq(String clszrq) {
		this.clszrq = clszrq;
	}

	public Map<String, Boolean> getSecondePlant() {
		return secondePlant;
	}

	public void setSecondePlant(Map<String, Boolean> secondePlant) {
		this.secondePlant = secondePlant;
	}

	private String fzbm;;
	public void setFzbm(String fzbm){
		this.fzbm = fzbm;
	}
	public String getFzbm(){
		return this.fzbm;
	}

	public String getSecondePlantStr(){
		String ret = null;
		Set<Map.Entry<String, Boolean>> set = secondePlant.entrySet();
		Iterator<Map.Entry<String, Boolean>> it = set.iterator();
		while(it.hasNext()){
			Map.Entry<String, Boolean> entry = it.next();
			if(entry.getValue()){
				if(ret==null){
					ret = entry.getKey();
				}else{
					ret = ret+"-"+entry.getKey();
				}
			}
		}
		return ret;
	}

	public String getCmat() {
		return cmat;
	}

	public void setCmat(String cmat) {
		this.cmat = cmat;
	}

	public String getPtc_material_name() {
		return ptc_material_name;
	}

	public void setPtc_material_name(String ptc_material_name) {
		this.ptc_material_name = ptc_material_name;
	}

	public String getCmat_up() {
		return cmat_up;
	}

	public void setCmat_up(String cmat_up) {
		this.cmat_up = cmat_up;
	}

	public String getCmat_down() {
		return cmat_down;
	}

	public void setCmat_down(String cmat_down) {
		this.cmat_down = cmat_down;
	}

	public String getSetmark() {
		return setmark;
	}

	public void setSetmark(String setmark) {
		this.setmark = setmark;
	}

	public String getPhase_code() {
		return phase_code;
	}

	public void setPhase_code(String phase_code) {
		this.phase_code = phase_code;
	}

	public String getKeycomponent() {
		return keycomponent;
	}

	public void setKeycomponent(String keycomponent) {
		this.keycomponent = keycomponent;
	}

	public String getCsize() {
		return csize;
	}

	public void setCsize(String csize) {
		this.csize = csize;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getEnditemin() {
		return enditemin;
	}

	public void setEnditemin(String enditemin) {
		this.enditemin = enditemin;
	}

	public String getMindex() {
		return mindex;
	}

	public void setMindex(String mindex) {
		this.mindex = mindex;
	}

	public String getCindex() {
		return cindex;
	}

	public void setCindex(String cindex) {
		this.cindex = cindex;
	}

	public String getPtc_common_name() {
		return ptc_common_name;
	}

	public void setPtc_common_name(String ptc_common_name) {
		this.ptc_common_name = ptc_common_name;
	}

	public String getRouting() {
		return routing;
	}

	public void setRouting(String routing) {
		this.routing = routing;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getProduct_index() {
		return product_index;
	}

	public void setProduct_index(String product_index) {
		this.product_index = product_index;
	}

	public String getDesigner() {
		return designer;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public String getZzcj() {
		return zzcj;
	}

	public void setZzcj(String zzcj) {
		this.zzcj = zzcj;
	}

	public String getFzcj() {
		return fzcj;
	}

	public void setFzcj(String fzcj) {
		this.fzcj = fzcj;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getPzggbzh() {
		return pzggbzh;
	}

	public void setPzggbzh(String pzggbzh) {
		this.pzggbzh = pzggbzh;
	}

	public String getJstjbzh() {
		return jstjbzh;
	}

	public void setJstjbzh(String jstjbzh) {
		this.jstjbzh = jstjbzh;
	}

	public String getJddj() {
		return jddj;
	}

	public void setJddj(String jddj) {
		this.jddj = jddj;
	}

	public String getZldj() {
		return zldj;
	}

	public void setZldj(String zldj) {
		this.zldj = zldj;
	}

	public String getClzt() {
		return clzt;
	}

	public void setClzt(String clzt) {
		this.clzt = clzt;
	}

	public String getCldw() {
		return cldw;
	}

	public void setCldw(String cldw) {
		this.cldw = cldw;
	}

	public String getZqclmc() {
		return zqclmc;
	}

	public void setZqclmc(String zqclmc) {
		this.zqclmc = zqclmc;
	}

	public String getJbclmc() {
		return jbclmc;
	}

	public void setJbclmc(String jbclmc) {
		this.jbclmc = jbclmc;
	}

	public String getZqclbzh() {
		return zqclbzh;
	}

	public void setZqclbzh(String zqclbzh) {
		this.zqclbzh = zqclbzh;
	}

	public String getPindex() {
		return pindex;
	}

	public void setPindex(String pindex) {
		this.pindex = pindex;
	}

	public String getChbm() {
		return chbm;
	}

	public void setChbm(String chbm) {
		this.chbm = chbm;
	}

	public String getMtype() {
		return mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}

	public String getXhph() {
		return xhph;
	}

	public void setXhph(String xhph) {
		this.xhph = xhph;
	}

	public String getJstj() {
		return jstj;
	}

	public void setJstj(String jstj) {
		this.jstj = jstj;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getZgyNumber() {
		return zgyNumber;
	}

	public void setZgyNumber(String zgyNumber) {
		this.zgyNumber = zgyNumber;
	}

	public String getZgyzt() {
		return zgyzt;
	}

	public void setZgyzt(String zgyzt) {
		this.zgyzt = zgyzt;
	}

	public String getZldezt() {
		return zldezt;
	}

	public void setZldezt(String zldezt) {
		this.zldezt = zldezt;
	}

	public String getIsOk() {
		return isOk;
	}

	public void setIsOk(String isOk) {
		this.isOk = isOk;
	}

	public String getE_lifecycle() {
		return e_lifecycle;
	}

	public void setE_lifecycle(String e_lifecycle) {
		this.e_lifecycle = e_lifecycle;
	}

	public String getGysl() {
		return gysl;
	}

	public void setGysl(String gysl) {
		this.gysl = gysl;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getYxjb() {
		return yxjb;
	}

	public void setYxjb(String yxjb) {
		this.yxjb = yxjb;
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

	public String getStandardnumber() {
		return standardnumber;
	}

	public void setStandardnumber(String standardnumber) {
		this.standardnumber = standardnumber;
	}

	public String getMechanicalpropertyorhardness() {
		return mechanicalpropertyorhardness;
	}

	public void setMechanicalpropertyorhardness(String mechanicalpropertyorhardness) {
		this.mechanicalpropertyorhardness = mechanicalpropertyorhardness;
	}

	public String getSurfacetreatment() {
		return surfacetreatment;
	}

	public void setSurfacetreatment(String surfacetreatment) {
		this.surfacetreatment = surfacetreatment;
	}

	public String getHeattreatment() {
		return heattreatment;
	}

	public void setHeattreatment(String heattreatment) {
		this.heattreatment = heattreatment;
	}

	public String getProductform() {
		return productform;
	}

	public void setProductform(String productform) {
		this.productform = productform;
	}

	public String getProductlevel() {
		return productlevel;
	}

	public void setProductlevel(String productlevel) {
		this.productlevel = productlevel;
	}

	public String getPlatecscrewform() {
		return platecscrewform;
	}

	public void setPlatecscrewform(String platecscrewform) {
		this.platecscrewform = platecscrewform;
	}

	public String getIsimport() {
		return isimport;
	}

	public void setIsimport(String isimport) {
		this.isimport = isimport;
	}

	public String getSpecialinstruction() {
		return specialinstruction;
	}

	public void setSpecialinstruction(String specialinstruction) {
		this.specialinstruction = specialinstruction;
	}

	public String getMeasureunit() {
		return measureunit;
	}

	public void setMeasureunit(String measureunit) {
		this.measureunit = measureunit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypestandard() {
		return typestandard;
	}

	public void setTypestandard(String typestandard) {
		this.typestandard = typestandard;
	}

	public String getQualitylevel() {
		return qualitylevel;
	}

	public void setQualitylevel(String qualitylevel) {
		this.qualitylevel = qualitylevel;
	}

	public String getTotalstandard() {
		return totalstandard;
	}

	public void setTotalstandard(String totalstandard) {
		this.totalstandard = totalstandard;
	}

	public String getDetailstandard() {
		return detailstandard;
	}

	public void setDetailstandard(String detailstandard) {
		this.detailstandard = detailstandard;
	}

	public String getPackagingform() {
		return packagingform;
	}

	public void setPackagingform(String packagingform) {
		this.packagingform = packagingform;
	}

	public String getOutlinesize() {
		return outlinesize;
	}

	public void setOutlinesize(String outlinesize) {
		this.outlinesize = outlinesize;
	}

	public String getSpecialcondition() {
		return specialcondition;
	}

	public void setSpecialcondition(String specialcondition) {
		this.specialcondition = specialcondition;
	}

	public String getExtracondition() {
		return extracondition;
	}

	public void setExtracondition(String extracondition) {
		this.extracondition = extracondition;
	}

	public String getMattype() {
		return mattype;
	}

	public void setMattype(String mattype) {
		this.mattype = mattype;
	}

	public String getCmatnumber() {
		return cmatnumber;
	}

	public void setCmatnumber(String cmatnumber) {
		this.cmatnumber = cmatnumber;
	}

	public String getMarknumber() {
		return marknumber;
	}

	public void setMarknumber(String marknumber) {
		this.marknumber = marknumber;
	}

	public String getSupplystate() {
		return supplystate;
	}

	public void setSupplystate(String supplystate) {
		this.supplystate = supplystate;
	}

	public String getUsestandard() {
		return usestandard;
	}

	public void setUsestandard(String usestandard) {
		this.usestandard = usestandard;
	}

	public String getAdjustable() {
		return adjustable;
	}

	public void setAdjustable(String adjustable) {
		this.adjustable = adjustable;
	}

	public boolean isBorrowedPart() {
		return isBorrowedPart;
	}

	public void setBorrowedPart(boolean borrowedPart) {
		isBorrowedPart = borrowedPart;
	}
}
