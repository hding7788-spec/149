package com.glaway.mpm.parameter.model.data;

import java.io.Serializable;
import java.util.Date;

/**
 * 搜索条件的
 * @author Liluwen
 * @date 2025年10月21日下午5:45:46
 */
public class CmSearchCondition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String status;
	private String conditionID;
	private String conditionName;
	private String creator;
	private String modifier;
	private String formType;
	private String conditionDepartment;
	private Date createDateFrom;
	private Date createDateTo;
	private Date modifyDateFrom;
	private Date modifyDateTo;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getConditionID() {
		return conditionID;
	}
	public void setConditionID(String conditionID) {
		this.conditionID = conditionID;
	}
	public String getConditionName() {
		return conditionName;
	}
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getConditionDepartment() {
		return conditionDepartment;
	}
	public void setConditionDepartment(String conditionDepartment) {
		this.conditionDepartment = conditionDepartment;
	}
	public Date getCreateDateFrom() {
		return createDateFrom;
	}
	public void setCreateDateFrom(Date createDateFrom) {
		this.createDateFrom = createDateFrom;
	}
	public Date getCreateDateTo() {
		return createDateTo;
	}
	public void setCreateDateTo(Date createDateTo) {
		this.createDateTo = createDateTo;
	}
	public Date getModifyDateFrom() {
		return modifyDateFrom;
	}
	public void setModifyDateFrom(Date modifyDateFrom) {
		this.modifyDateFrom = modifyDateFrom;
	}
	public Date getModifyDateTo() {
		return modifyDateTo;
	}
	public void setModifyDateTo(Date modifyDateTo) {
		this.modifyDateTo = modifyDateTo;
	}
	@Override
	public String toString() {
		return "CmSearchCondition [status=" + status + ", conditionID=" + conditionID + ", conditionName="
				+ conditionName + ", creator=" + creator + ", modifier=" + modifier + ", formType=" + formType
				+ ", conditionDepartment=" + conditionDepartment + ", createDateFrom=" + createDateFrom
				+ ", createDateTo=" + createDateTo + ", modifyDateFrom=" + modifyDateFrom + ", modifyDateTo="
				+ modifyDateTo + "]";
	}

	

	
}
