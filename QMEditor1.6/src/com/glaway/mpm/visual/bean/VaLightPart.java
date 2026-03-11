package com.glaway.mpm.visual.bean;

import javax.vecmath.Matrix4d;
import java.io.Serializable;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * ��¼WTPart��Ϣ������������ <br>
 * Created on 2010-10-23
 *
 * @author Dennis Huang - ���ٽ�
 */
public class VaLightPart implements Serializable,Cloneable {
	private static final long serialVersionUID = 4761654884653326205L;

	public static final VaLightPart EMPTY_PART = newLightPart("", "", "", 0L);
	private String number;
	private String name;
	private String version;
	private long containerId;
	private String type = "";
	private int status = 0;

	private long oid = 0;
	private long masterOid = 0;
	private String state = "";
	private Timestamp modifyTime;
	private URL pvURL = null;
	private String cadObid;
	private String cadNumberFile;
	private HashMap<String, Matrix4d> locators = null;
	private String firstCount;
	private String secondCount;
	private String rootType;


	private String view;
	private String zcmark;
	public String getZcmark() {
        return zcmark;
    }
    public void setZcmark(String zcmark) {
        this.zcmark = zcmark;
    }
	//bom properties
	private String dutu;//镀涂
	private String remark;//备注
	private int useCount;//1
	private String rate; //工艺辅件比例
	private boolean isKey;//关键件true|false"
	private boolean isSpecial;//特殊件true|false
	private String materialType;//物料类型
	private String backupRate;//工艺备份比例
	private String maxBackupCount;//最大备份数
	private String backupReason;//备份原因
	private String workShop;//制造单位
	private String outsourcingUnits;//建议外协单位
	private String 	materialNumber;//材料编号
	private String materialName;//材料名称
	private String materialBrand;//材料牌号
	private String materialCrision;//材料标准号
	private String responser;//工艺负责人
	private String responserGroup;//工艺负责组
	private String lifecycle;//生命周期状态
	private String e_version;//EBOM零件版本
	private String eu_number;//零件的图号
	private String eu_version;//零件的EBOM图号版本
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
	private double amount;
	//可装数量
	private double zCount;
	//可拆数量
	private double cCount;
	private String bzh;
	private String dataType;
	private String cl;
	//可调整
	private String adjustable;


	public String getCl() {
		return cl;
	}

	public double getzCount() {
		return zCount;
	}

	public void setzCount(double zCount) {
		this.zCount = zCount;
	}


	public double getcCount() {
		return cCount;
	}

	public void setcCount(double cCount) {
		this.cCount = cCount;
	}

	public void setCl(String cl) {
		this.cl = cl;
	}

	public String getBzh() {
		return bzh;
	}

	public void setBzh(String bzh) {
		this.bzh = bzh;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	private String mtype;
	private String csize;//
	//设计资源库，工艺定额属性信息
	private String flag ;
	//单位
	private String dw2 ;
    //主计量单位
	private String dw ;
    //型号牌号
	private String xhph;
    //规格
	private String gg;
    //生产厂家
	private String sccj;
    //质量等级
	private String zldj;
    //封装形式
	private String fzxs;
    //电参数特选要求
	private String dcstxyq ;
    //备注
	private String comment;
	//技术条件
	private String jstj;



	public String getMtype() {
		return mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}

	public String getCsize() {
		return csize;
	}

	public void setCsize(String csize) {
		this.csize = csize;
	}

	public String getXhph() {
		return xhph;
	}

	public void setXhph(String xhph) {
		this.xhph = xhph;
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

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public static VaLightPart newLightPart(String number, String name, String version, long containerId) {
		VaLightPart ret = new VaLightPart();

		ret.setNumber(number);
		ret.setName(name);
		ret.setVersion(version);
		ret.setContainerId(containerId);

		return ret;
	}

	public static VaLightPart newLightPart(String number, String name, String version, long containerId, String xhph, String gg) {
		VaLightPart ret = new VaLightPart();
		ret.setNumber(number);
		ret.setName(name);
		ret.setVersion(version);
		ret.setContainerId(containerId);
		ret.setXhph(xhph);
		ret.setGg(gg);

		return ret;
	}

	public static VaLightPart newLightPart(String number, String name, String version, long containerId, String xhph, String gg,String bzh,String dataType) {
		VaLightPart ret = new VaLightPart();
		ret.setNumber(number);
		ret.setName(name);
		ret.setVersion(version);
		ret.setContainerId(containerId);
		ret.setXhph(xhph);
		ret.setGg(gg);
		ret.setBzh(bzh);
		ret.setDataType(dataType);
		return ret;
	}

	public void setRootType(String rootType){
		this.rootType = rootType;
	}

	public VaLightPart() {
	}

	public String getIdentifier() {
		return "wt.part.WTPart:" + getOid();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRealBzh(){
		if(this.bzh!=null&&!"".equals(this.bzh)&&!"null".equals(bzh)){
			return bzh;
		}else{
			return jstj;
		}
	}
	public String toString() {
		if("FPBOM".equals(rootType)){
			if(amount > 1){
				if("标准件".equals(dataType)||"标准紧固件".equals(dataType)){
					return this.number + "(" + this.name + ") " + this.version + "(" + getRealBzh() + ")" + "(" + this.gg + ")" + " ("+amount+") *" + zCount;
				}
				return this.number + "(" + this.name + ") " + this.version + "(" + this.xhph + ")" + "(" + this.gg + ")" + " ("+amount+") *" + zCount;
			}
			if("标准件".equals(dataType)||"标准紧固件".equals(dataType)){
				return this.number + "(" + this.name + ") " + this.version + "(" + getRealBzh()+ ")" + "(" + this.gg + ")";
			}
			return this.number + "(" + this.name + ") " + this.version + "(" + this.xhph + ")" + "(" + this.gg + ")";
		}else if("TECHNICS".equals(rootType)){
//			if("标准件".equals(dataType)){
//				return this.number + "(" + this.name + ") " + " ("+amount+") *" + CommonUtil.subDouble(amount,zCount);
//			}else{
				return this.number + "(" + this.name + ") "+"_"+ zcmark + " *" + amount;
//			}
		}else if ("ZPBOM".equals(rootType)) {
			return this.number + "(" + this.name + ") " + this.version + " ("+amount+") *" + zCount;
		}
		if(amount > 1){
				return this.number + "(" + this.name + ") " + this.version + " *" + amount;
		}
		return this.number + "(" + this.name + ") " + this.version;// + " ("+this.view+")";
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

	public String getFirstCount() {
		return firstCount;
	}

	public void setFirstCount(String firstCount) {
		this.firstCount = firstCount;
	}

	public String getSecondCount() {
		return secondCount;
	}

	public void setSecondCount(String secondCount) {
		this.secondCount = secondCount;
	}

	public String getAdjustable() {
		return adjustable;
	}

	public void setAdjustable(String adjustable) {
		this.adjustable = adjustable;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (containerId ^ (containerId >>> 32));
		result = PRIME * result + (int) (masterOid ^ (masterOid >>> 32));
		result = PRIME * result + ((modifyTime == null) ? 0 : modifyTime.hashCode());
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result = PRIME * result + ((number == null) ? 0 : number.hashCode());
		result = PRIME * result + (int) (oid ^ (oid >>> 32));
		result = PRIME * result + ((pvURL == null) ? 0 : pvURL.hashCode());
		result = PRIME * result + ((state == null) ? 0 : state.hashCode());
		result = PRIME * result + status;
		result = PRIME * result + ((type == null) ? 0 : type.hashCode());
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
		final VaLightPart other = (VaLightPart) obj;
		if (containerId != other.containerId)
			return false;
		if (masterOid != other.masterOid)
			return false;
		if (modifyTime == null) {
			if (other.modifyTime != null)
				return false;
		} else if (!modifyTime.equals(other.modifyTime))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
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
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;


		return true;
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
		return materialType;
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

	public String getOutsourcingUnits() {
		return outsourcingUnits;
	}

	public void setOutsourcingUnits(String outsourcingUnits) {
		this.outsourcingUnits = outsourcingUnits;
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

	public void setE_version(String e_version) {
		this.e_version = e_version;
	}

	public String getEu_number() {
		return eu_number;
	}

	public void setEu_number(String eu_number) {
		this.eu_number = eu_number;
	}

	public String getEu_version() {
		return eu_version;
	}

	public void setEu_version(String eu_version) {
		this.eu_version = eu_version;
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

	public boolean isReversion() {
		return isReversion;
	}

	public void setReversion(boolean isReversion) {
		this.isReversion = isReversion;
	}

	public boolean isEbomKey() {
		return isEbomKey;
	}

	public void setEbomKey(boolean isEbomKey) {
		this.isEbomKey = isEbomKey;
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
    public String getDw2() {
        return dw2;
    }
    public void setDw2(String dw2) {
        this.dw2 = dw2;
    }
    public String getDw() {
        return dw;
    }
    public void setDw(String dw) {
        this.dw = dw;
    }
    public String getGg() {
        return gg;
    }
    public void setGg(String gg) {
        this.gg = gg;
    }
    public String getSccj() {
        return sccj;
    }
    public void setSccj(String sccj) {
        this.sccj = sccj;
    }
    public String getZldj() {
        return zldj;
    }
    public void setZldj(String zldj) {
        this.zldj = zldj;
    }
    public String getFzxs() {
        return fzxs;
    }
    public void setFzxs(String fzxs) {
        this.fzxs = fzxs;
    }
    public String getDcstxyq() {
        return dcstxyq;
    }
    public void setDcstxyq(String dcstxyq) {
        this.dcstxyq = dcstxyq;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getFlag() {
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
    public String getJstj() {
    	return jstj;
    }
	public void setJstj(String jstj) {
		this.jstj = jstj;
	}

	@Override
	public Object clone() {
		VaLightPart vaLightPart = null;
		try {
			vaLightPart = (VaLightPart) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return vaLightPart;
	}


	//


}
