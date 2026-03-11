package com.glaway.mpm.parameter.commonui;

import java.awt.Component;
import java.util.Arrays;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * 表格单元格下拉框编辑器
 * 
 * @author 龙秀川
 *
 */
public class CommonComboxTableCellDitor extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = 1L;
	/** 当前操作的行索引 */
	private int row;
	private int column;
	private JTable table;
	private JComboBox comboBox;
	
	public CommonComboxTableCellDitor() {
		comboBox = new JComboBox();
	}
	
	public void updateItem(String[] comBoxValues, JTable table, int row, int column) {
		String oldValue = "";
		if (table != null) {
			oldValue = String.valueOf(table.getValueAt(row, column));
		}
		
		//清空所有下拉选项
		comboBox.removeAllItems();
		
		if (oldValue != null && !"".equals(oldValue)) {
			if (comBoxValues.length>0 && Arrays.asList(comBoxValues).contains(oldValue)) {
				//如果已填值包含在当前下拉可选值范围内，则将已有值填写回去
				comboBox.addItem(oldValue);
			} else {
				if (table != null && row > -1 && column > -1) {
					table.setValueAt("", row, column);
				}
			}
		}
		
		for (String item : comBoxValues) {
			comboBox.addItem(item);
		}
		
		comboBox.setSelectedIndex(0);
	}
	
	@Override
	public Object getCellEditorValue() {
		String value = String.valueOf(comboBox.getSelectedItem());;
		if (table != null && row > -1 && column > -1) {
			table.setValueAt(value, row, column);
		}
		return value;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		this.table = table;
		this.row = row;
		this.column = column;
		return this.comboBox;
	}

}
