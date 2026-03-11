package com.glaway.mpm.processplan.checkouttable.checkbean;

import java.util.List;

/**
 * 工步号类
 * @author Administrator
 *
 */
public class PaceNoBean {
	private String peceNo;
	private String projectName;//项目名
	private List<CheckOutTableListBean> checkOutTableListBeanlist;
	private int tableUnitCount;//记算所占单元格的行数

	public String getPeceNo() {
		return peceNo;
	}
	public void setPeceNo(String peceNo) {
		this.peceNo = peceNo;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public List<CheckOutTableListBean> getCheckOutTableListBeanlist() {
		return checkOutTableListBeanlist;
	}
	public void setCheckOutTableListBeanlist(List<CheckOutTableListBean> checkOutTableListBeanlist) {
		this.checkOutTableListBeanlist = checkOutTableListBeanlist;
	}
	public int getTableUnitCount() {
		return tableUnitCount;
	}
	public void setTableUnitCount(int tableUnitCount) {
		this.tableUnitCount = tableUnitCount;
	}


}
