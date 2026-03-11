package com.glaway.mpm.parameter.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class ParameterTableRenderer implements TableCellRenderer{

	public static final DefaultTableCellRenderer DEFAULT_RENDER = new DefaultTableCellRenderer();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		Component render = DEFAULT_RENDER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		String cellValue = String.valueOf(table.getValueAt(row, 9));
		if(cellValue.equals("冻结")){
			render.setForeground(Color.lightGray);
		}else{
			render.setForeground(Color.black);
		}
		return render;
	}

}
