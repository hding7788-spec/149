package com.glaway.mpm.processplan.checkouttable.checkbean;

import java.util.List;
/**
 * 套表名类
 * @author jyx
 *
 */
public class TableBean {
	private String tableName;
	private List<ProjectNameBean> projectNameBeanlist;

	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<ProjectNameBean> getProjectNameBeanlist() {
		return projectNameBeanlist;
	}
	public void setProjectNameBeanlist(List<ProjectNameBean> projectNameBeanlist) {
		this.projectNameBeanlist = projectNameBeanlist;
	}

}
