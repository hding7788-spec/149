package com.glaway.mpm.parameter.model.data;

/**
 * 白羽通用表单列对象
 *
 * @author 陈建慧
 *
 */
public class CmBaiyuParamTableColumn extends CmTreeNode {

	private static final long serialVersionUID = 1L;
	/** 主对象oid */
	private String tableOid;
	/** 顺序号 */
	private String orderNo;
	/** 主对象ID */
	private String tableId;
	/** 主对象名称 */
	private String tableName;
	/** 主对象创建者*/
	private String tableCreator;
	/** 主对象修改者 */
	private String tableModifier;
	/** 主对象创建时间 */
	private String tableCreateTime;
	/** 主对象最后修改时间 */
	private String tableModifyTime;
	/** 启用状态 */
	private String isUsed;
	/** 版本 */
	private String version;
	/** 表格类型 */
	private String tableType;
	/** 部门 */
	private String dept;


	public String getTableOid() {
		return tableOid;
	}

	public void setTableOid(String tableOid) {
		this.tableOid = tableOid;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableCreator() {
		return tableCreator;
	}

	public void setTableCreator(String tableCreator) {
		this.tableCreator = tableCreator;
	}

	public String getTableModifier() {
		return tableModifier;
	}

	public void setTableModifier(String tableModifier) {
		this.tableModifier = tableModifier;
	}

	public String getTableCreateTime() {
		return tableCreateTime;
	}

	public void setTableCreateTime(String tableCreateTime) {
		this.tableCreateTime = tableCreateTime;
	}

	public String getTableModifyTime() {
		return tableModifyTime;
	}

	public void setTableModifyTime(String tableModifyTime) {
		this.tableModifyTime = tableModifyTime;
	}

	public String getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}
}
