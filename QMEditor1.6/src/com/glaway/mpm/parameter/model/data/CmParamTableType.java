package com.glaway.mpm.parameter.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 参数表类型对象
 *
 * @author 龙秀川
 *
 */
public class CmParamTableType extends CmTreeNode {

	private static final long serialVersionUID = 1L;
	/** 英文名称 */
	private String enName;
	/** 主对象ID */
	private String tableTypeMasterId;
	/** 工艺类别 */
	private String technicsType;
	/** 启用状态 */
	private String isUsed;
	/** 参数表定义的列集合 */
	private List<CmParameterTableColumn> tableColumns;
	/** 参数表的参数集合 */
	private Vector<Vector<Object>> parameters;

	public String getTableTypeMasterId() {
		return tableTypeMasterId;
	}

	public void setTableTypeMasterId(String tableTypeMasterId) {
		this.tableTypeMasterId = tableTypeMasterId;
	}

	public List<CmParameterTableColumn> getTableColumns() {
		if (tableColumns == null) {
			tableColumns = new ArrayList<CmParameterTableColumn>();
		}
		return tableColumns;
	}

	public void setTableColumns(List<CmParameterTableColumn> tableColumns) {
		this.tableColumns = tableColumns;
	}
	public String getTechnicsType() {
		return technicsType;
	}

	public void setTechnicsType(String technicsType) {
		this.technicsType = technicsType;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}

	public Vector<Vector<Object>> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<Vector<Object>> parameters) {
		this.parameters = parameters;
	}
}
