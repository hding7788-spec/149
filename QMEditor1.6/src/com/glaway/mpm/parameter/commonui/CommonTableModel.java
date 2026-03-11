package com.glaway.mpm.parameter.commonui;

import javax.swing.table.DefaultTableModel;

public class CommonTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	/** 可编辑的列 */
	private int[] editableColumns;
	/** 表格每列的类型 */
	private Class<?>[] tableColumnClass;

	public CommonTableModel(String[] header, Class<?>[] tableColumnClass, int[] editableColumns) {
		super(null, header);
		this.tableColumnClass = tableColumnClass;
		this.editableColumns = editableColumns;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (tableColumnClass != null) {
			return tableColumnClass[columnIndex];
		} else {
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (editableColumns != null) {
			for (int i : editableColumns) {
				if (i == column) {
					return true;
				}
			}
		}
		return false;
	}

	public int[] getEditableColumns() {
		return editableColumns;
	}

	public void setEditableColumns(int[] editableColumns) {
		this.editableColumns = editableColumns;
	}

	public Class<?>[] getTableColumnClass() {
		return tableColumnClass;
	}

	public void setTableColumnClass(Class<?>[] tableColumnClass) {
		this.tableColumnClass = tableColumnClass;
	}
}
