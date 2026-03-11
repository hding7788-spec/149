package com.glaway.mpm.parameter.model.data;

/**
 * 参数表类型的列对象
 *
 * @author 龙秀川
 *
 */
public class CmParameterTableColumn extends CmTreeNode {

	private static final long serialVersionUID = 1L;
	/** 顺序号 */
	private String orderno;
	/** 参数表类型ID */
	private String parametertabletypeid;
	/** 内部名称 */
	private String enName;
	/** 数据类型 */
	private String datatype;
	/** 最大长度 */
	private String maxlong;
	/** 显示宽度 */
	private String showwidth;
	/** 是否记录列 */
	private String isrecord;
	/** 是否特性库选取 */
	private String isfromparam;
	/** 范围值 */
	private String valueRange;
	/** 状态 */
	private String status;
	/** 不可见 */
	private String visiless;
	/** 不可见 */
	private String visilessInMes;

	/** 标识在JTable中该列是否隐藏 */
	private boolean isShow = true;
	/** 标识在JTable中该列是否可编辑 */
	private boolean isEditable = true;

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getParametertabletypeid() {
		return parametertabletypeid;
	}

	public void setParametertabletypeid(String parametertabletypeid) {
		this.parametertabletypeid = parametertabletypeid;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getMaxlong() {
		return maxlong;
	}

	public void setMaxlong(String maxlong) {
		this.maxlong = maxlong;
	}

	public String getShowwidth() {
		return showwidth;
	}

	public void setShowwidth(String showwidth) {
		this.showwidth = showwidth;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getIsrecord() {
		return isrecord;
	}

	public void setIsrecord(String isrecord) {
		this.isrecord = isrecord;
	}

	public String getIsfromparam() {
		return isfromparam;
	}

	public void setIsfromparam(String isfromparam) {
		this.isfromparam = isfromparam;
	}

	public String getValueRange() {
		return valueRange;
	}

	public void setValueRange(String valueRange) {
		this.valueRange = valueRange;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVisiless() {
		return visiless;
	}

	public void setVisiless(String visiless) {
		this.visiless = visiless;
	}

	public String getVisilessInMes() {
		return visilessInMes;
	}

	public void setVisilessInMes(String visilessInMes) {
		this.visilessInMes = visilessInMes;
	}

}
