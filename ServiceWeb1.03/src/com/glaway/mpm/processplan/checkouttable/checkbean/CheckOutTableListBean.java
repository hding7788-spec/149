package com.glaway.mpm.processplan.checkouttable.checkbean;

import java.util.List;
import java.util.Map;
/**
 * 工步下面单元表类
 * @author jyx
 *
 */
public class CheckOutTableListBean {
	private String unitName;//单元表名
	private String projectName;//项目名
	private Map<String,CheckOutTableBean> checkOutTableBeanMap;//单元表集合
	private List<CheckOutTableBean> checkOutTableBeanList;
	private int tableUnitCount;//记算所占单元格的行数

	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Map<String, CheckOutTableBean> getCheckOutTableBeanMap() {
		return checkOutTableBeanMap;
	}
	public void setCheckOutTableBeanMap(Map<String, CheckOutTableBean> checkOutTableBeanMap) {
		this.checkOutTableBeanMap = checkOutTableBeanMap;
	}
	public List<CheckOutTableBean> getCheckOutTableBeanList() {
		return checkOutTableBeanList;
	}
	public void setCheckOutTableBeanList(List<CheckOutTableBean> checkOutTableBeanList) {
		this.checkOutTableBeanList = checkOutTableBeanList;
	}
	public int getTableUnitCount() {
		return tableUnitCount;
	}
	public void setTableUnitCount(int tableUnitCount) {
		this.tableUnitCount = tableUnitCount;
	}



}
