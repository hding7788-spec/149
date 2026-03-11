package com.glaway.mpm.parameter.model.data;

import java.util.List;
import java.util.Vector;

/**
 * 生成参数表格的数据包。由参数表类型对象CmParamTableType转化得到。
 *
 * @author 龙秀川
 *
 */
public class CmParameterTablePackage extends CmTreeNode {

	private static final long serialVersionUID = 1L;
	/** 参数表类型对象 */
	private CmParamTableType paramTableType;
	/** 表各列的对象类型 */
	private Class<?>[] tableColumnClass;
	/** 表各列的数据库数据类型 */
	private String[] tableColumnDataType;
	/** 表头 */
	private String[] tableColumnName;
	/** 数据查询表头 */
	private String[] mesDataSearchTableColumnName;
	/** 可编辑的列 */
	private int[] editableColumns;
	/** 双击操作的Boolean检验列 */
	private List<Integer> doubleCilckBooleanColumns;
	/** 双击操作的MES Boolean检验列 */
	private List<Integer> doubleCilckMesBooleanColumns;
	/** 双击操作的图片检验列 */
	private List<Integer> doubleCilckPictureColumns;
	/** 双击操作的MES图片检验列 */
	private List<Integer> doubleCilckMesPictureColumns;
	/** 记录列 */
	private List<Integer> recordColumns;
	/** 可编辑MES的列 */
	private int[] editableMesColumns;
	/** 需要显示的列 */
	private int[] notShowColumns;
	/** 需要显示的MES列 */
	private int[] notShowMesColumns;
	/** 需要显示的MES数据搜索列 */
	private int[] notShowDataSearchColumns;
	/** 按照JTable表格组织的二维数据参数集合*/
	private Vector<Vector<String>> params;

	public CmParamTableType getParamTableType() {
		return paramTableType;
	}

	public void setParamTableType(CmParamTableType paramTableType) {
		this.paramTableType = paramTableType;
	}

	public Class<?>[] getTableColumnClass() {
		return tableColumnClass;
	}

	public void setTableColumnClass(Class<?>[] tableColumnClass) {
		this.tableColumnClass = tableColumnClass;
	}

	public String[] getTableColumnDataType() {
		return tableColumnDataType;
	}

	public void setTableColumnDataType(String[] tableColumnDataType) {
		this.tableColumnDataType = tableColumnDataType;
	}

	public String[] getTableColumnName() {
		return tableColumnName;
	}

	public void setTableColumnName(String[] tableColumnName) {
		this.tableColumnName = tableColumnName;
	}

	public int[] getEditableColumns() {
		return editableColumns;
	}

	public void setEditableColumns(int[] editableColumns) {
		this.editableColumns = editableColumns;
	}

	public int[] getNotShowColumns() {
		return notShowColumns;
	}

	public void setNotShowColumns(int[] notShowColumns) {
		this.notShowColumns = notShowColumns;
	}

	public Vector<Vector<String>> getParams() {
		if (params == null) {
			params = new Vector<Vector<String>>();
		}
		return params;
	}

	public void setParams(Vector<Vector<String>> params) {
		this.params = params;
	}

	public int[] getEditableMesColumns() {
		return editableMesColumns;
	}

	public void setEditableMesColumns(int[] editableMesColumns) {
		this.editableMesColumns = editableMesColumns;
	}

	public int[] getNotShowMesColumns() {
		return notShowMesColumns;
	}

	public void setNotShowMesColumns(int[] notShowMesColumns) {
		this.notShowMesColumns = notShowMesColumns;
	}

	public List<Integer> getDoubleCilckBooleanColumns() {
		return doubleCilckBooleanColumns;
	}

	public void setDoubleCilckBooleanColumns(List<Integer> doubleCilckBooleanColumns) {
		this.doubleCilckBooleanColumns = doubleCilckBooleanColumns;
	}

	public List<Integer> getDoubleCilckMesBooleanColumns() {
		return doubleCilckMesBooleanColumns;
	}

	public void setDoubleCilckMesBooleanColumns(List<Integer> doubleCilckMesBooleanColumns) {
		this.doubleCilckMesBooleanColumns = doubleCilckMesBooleanColumns;
	}

	public List<Integer> getDoubleCilckPictureColumns() {
		return doubleCilckPictureColumns;
	}

	public void setDoubleCilckPictureColumns(List<Integer> doubleCilckPictureColumns) {
		this.doubleCilckPictureColumns = doubleCilckPictureColumns;
	}

	public List<Integer> getDoubleCilckMesPictureColumns() {
		return doubleCilckMesPictureColumns;
	}

	public void setDoubleCilckMesPictureColumns(List<Integer> doubleCilckMesPictureColumns) {
		this.doubleCilckMesPictureColumns = doubleCilckMesPictureColumns;
	}

	public List<Integer> getRecordColumns() {
		return recordColumns;
	}

	public void setRecordColumns(List<Integer> recordColumns) {
		this.recordColumns = recordColumns;
	}

	public String[] getMesDataSearchTableColumnName() {
		return mesDataSearchTableColumnName;
	}

	public void setMesDataSearchTableColumnName(String[] mesDataSearchTableColumnName) {
		this.mesDataSearchTableColumnName = mesDataSearchTableColumnName;
	}

	public int[] getNotShowDataSearchColumns() {
		return notShowDataSearchColumns;
	}

	public void setNotShowDataSearchColumns(int[] notShowDataSearchColumns) {
		this.notShowDataSearchColumns = notShowDataSearchColumns;
	}


}
