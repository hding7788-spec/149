package ext.casc.integrate.process;

import java.io.Serializable;
import java.util.List;

public class MPMOperationBean implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 8729899243132181463L;
	private MPMProcessPlanBean parent;
	private MPMOperationBean parentgongxu;
	private List<MPMOperationBean> childrengongbu;
	private String number;//工序编号
	private String name;//工序名称
	private String version;//工序版本
	private String zrcj;//车间
	private String zjgs;//准结工时
	private String degs;//单件工时
	private String keyoper;//是否关键工序
	private String isgb;//是否工步
	private String bsoId;

	public String getBsoId() {
		return bsoId;
	}
	public void setBsoId(String bsoId) {
		this.bsoId = bsoId;
	}
	public List<MPMOperationBean> getChildrengongbu() {
		return childrengongbu;
	}
	public void setChildrengongbu(List<MPMOperationBean> childrengongbu) {
		this.childrengongbu = childrengongbu;
	}
	public MPMOperationBean getParentgongxu() {
		return parentgongxu;
	}
	public void setParentgongxu(MPMOperationBean parentgongxu) {
		this.parentgongxu = parentgongxu;
	}
	public String getIsgb() {
		return isgb;
	}
	public void setIsgb(String isgb) {
		this.isgb = isgb;
	}
	public MPMProcessPlanBean getParent() {
		return parent;
	}
	public void setParent(MPMProcessPlanBean parent) {
		this.parent = parent;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getZrcj() {
		return zrcj;
	}
	public void setZrcj(String zrcj) {
		this.zrcj = zrcj;
	}
	public String getZjgs() {
		return zjgs;
	}
	public void setZjgs(String zjgs) {
		this.zjgs = zjgs;
	}
	public String getDegs() {
		return degs;
	}
	public void setDegs(String degs) {
		this.degs = degs;
	}
	public String getKeyoper() {
		return keyoper;
	}
	public void setKeyoper(String keyoper) {
		this.keyoper = keyoper;
	}

}
