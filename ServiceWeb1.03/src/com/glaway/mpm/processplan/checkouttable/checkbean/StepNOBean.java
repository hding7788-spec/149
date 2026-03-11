package com.glaway.mpm.processplan.checkouttable.checkbean;

import java.util.List;
/**
 * 工序号类
 * @author jyx
 *
 */
public class StepNOBean {
	private String stepNo;
	private List<PaceNoBean> paceNoBeanlist;
	private String projectName;//项目名
	private int tableUnitCount;//记算所占单元格的行数
	public String getStepNo() {
		return stepNo;
	}
	public void setStepNo(String stepNo) {
		this.stepNo = stepNo;
	}
	public List<PaceNoBean> getPaceNoBeanlist() {
		return paceNoBeanlist;
	}
	public void setPaceNoBeanlist(List<PaceNoBean> paceNoBeanlist) {
		this.paceNoBeanlist = paceNoBeanlist;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public int getTableUnitCount() {
		return tableUnitCount;
	}
	public void setTableUnitCount(int tableUnitCount) {
		this.tableUnitCount = tableUnitCount;
	}


}
