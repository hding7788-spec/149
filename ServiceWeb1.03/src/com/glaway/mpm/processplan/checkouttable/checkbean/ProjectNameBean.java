package com.glaway.mpm.processplan.checkouttable.checkbean;

import java.util.List;
/**
 * 项目名类
 * @author Administrator
 *
 */
public class ProjectNameBean {
	private String projectName;
	private List<StepNOBean> stepNOBeanlist;
	private int tableUnitCount;//记算所占单元格的行数


	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public List<StepNOBean> getStepNOBeanlist() {
		return stepNOBeanlist;
	}
	public void setStepNOBeanlist(List<StepNOBean> stepNOBeanlist) {
		this.stepNOBeanlist = stepNOBeanlist;
	}
	public int getTableUnitCount() {
		return tableUnitCount;
	}
	public void setTableUnitCount(int tableUnitCount) {
		this.tableUnitCount = tableUnitCount;
	}



}
