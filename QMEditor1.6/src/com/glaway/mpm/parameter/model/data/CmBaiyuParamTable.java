package com.glaway.mpm.parameter.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 白羽通用表单对象
 *
 * @author 陈建慧
 *
 */
public class CmBaiyuParamTable extends CmTreeNode {

	private static final long serialVersionUID = 1L;
	/** 启用状态 */
	private String isUsed;
	/** 白羽通用表单的列集合 */
	private List<CmBaiyuParamTableColumn> tableColumns;

	public String getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}

	public List<CmBaiyuParamTableColumn> getTableColumns() {
		return tableColumns;
	}

	public void setTableColumns(List<CmBaiyuParamTableColumn> tableColumns) {
		this.tableColumns = tableColumns;
	}
}
