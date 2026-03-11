package com.glaway.mpm.processplan.checkouttable.checkbean;

public class CheckOutTableBean {
	private String projectName;//项目名
	private String type;//类型  检测类还是记录类
	private String name;//单元表表名
	private String tableName;//套表名
	private String jlxValue;//记录项
	private String yqzValue;//要求值
	private String sczValue;//实测值
	private String spcValue;//上偏差
	private String xpcValue;//下偏差

	private String qsxxValue;//签审信息
	private String stepNO;//工序号
	private String stepNumber;//工序编号
	private String paceNo;//工步号
	private String paceNumber;//工步编号
	private String checkmethodContent;//检验方法描述
	private String checkcontentContent;//检验内容描述

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getJlxValue() {
		return jlxValue;
	}
	public void setJlxValue(String jlxValue) {
		this.jlxValue = jlxValue;
	}
	public String getYqzValue() {
		return yqzValue;
	}
	public void setYqzValue(String yqzValue) {
		this.yqzValue = yqzValue;
	}
	public String getSczValue() {
		return sczValue;
	}
	public void setSczValue(String sczValue) {
		this.sczValue = sczValue;
	}
	public String getSpcValue() {
		return spcValue;
	}
	public void setSpcValue(String spcValue) {
		this.spcValue = spcValue;
	}
	public String getXpcValue() {
		return xpcValue;
	}
	public void setXpcValue(String xpcValue) {
		this.xpcValue = xpcValue;
	}
	public String getQsxxValue() {
		return qsxxValue;
	}
	public void setQsxxValue(String qsxxValue) {
		this.qsxxValue = qsxxValue;
	}
	public String getStepNO() {
		return stepNO;
	}
	public void setStepNO(String stepNO) {
		this.stepNO = stepNO;
	}
	public String getStepNumber() {
		return stepNumber;
	}
	public void setStepNumber(String stepNumber) {
		this.stepNumber = stepNumber;
	}
	public String getPaceNo() {
		return paceNo;
	}
	public void setPaceNo(String paceNo) {
		this.paceNo = paceNo;
	}
	public String getPaceNumber() {
		return paceNumber;
	}
	public void setPaceNumber(String paceNumber) {
		this.paceNumber = paceNumber;
	}
	public String getCheckmethodContent() {
		return checkmethodContent;
	}
	public void setCheckmethodContent(String checkmethodContent) {
		this.checkmethodContent = checkmethodContent;
	}
	public String getCheckcontentContent() {
		return checkcontentContent;
	}
	public void setCheckcontentContent(String checkcontentContent) {
		this.checkcontentContent = checkcontentContent;
	}


}
