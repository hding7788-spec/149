package com.glaway.mpm.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LabelRender extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public LabelRender() {
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);
		if (column % 2 == 0) {
			label.setBackground(Color.LIGHT_GRAY);
		} else {
			label.setBackground(Color.WHITE);
		}

		if (column == 0) {
		}
		return label;
	}

}
