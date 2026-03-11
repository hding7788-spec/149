package com.glaway.mpm.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class JTableUtil {

	/**
	 * 生成table的模型
	 * 
	 * @param tableValue
	 * @param header
	 * @return
	 */
	public static DefaultTableModel getModel(Object[][] tableValue,
			String[] header) {
		DefaultTableModel model = new DefaultTableModel(tableValue, header) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}

	/**
	 * 上移，下移功能
	 * @param up
	 * @param jTable1
	 */
	public static void changeRowValue(boolean up,JTable jTable1) {
		if (jTable1.getSelectedRowCount() > 1)
			return;
		int select = jTable1.getSelectedRow();
		if (select < 0)
			return;
		if (up && select == 0)
			return;
		if (!up && select == jTable1.getRowCount() - 1)
			return;
		Object[] obj1 = new Object[jTable1.getColumnCount()];
		Object[] obj2 = new Object[jTable1.getColumnCount()];
		int neighbor;
		if (up)
			neighbor = select - 1;
		else
			neighbor = select + 1;
		for (int i = 0; i < jTable1.getColumnCount(); i++) {
			obj1[i] = jTable1.getValueAt(select, i);
			obj2[i] = jTable1.getValueAt(neighbor, i);
		}
		for (int j = 0; j < jTable1.getColumnCount(); j++) {
			if(j == 0){ //控制不需要变化的列
				continue;
			}
			jTable1.setValueAt(obj2[j], select, j);
			jTable1.setValueAt(obj1[j], neighbor, j);
		}
		jTable1.setRowSelectionInterval(neighbor, neighbor);
	}
}
