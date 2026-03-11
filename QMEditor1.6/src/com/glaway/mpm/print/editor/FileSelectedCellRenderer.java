package com.glaway.mpm.print.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import com.glaway.mpm.util.CommonUtil;

public class FileSelectedCellRenderer implements TableCellRenderer {

	private JPanel panel;
	private JTextField textField;
	private JButton button;

	public FileSelectedCellRenderer(JTable table) {
		textField = new JTextField();
		button = new JButton("设 置");

		textField.setPreferredSize(new Dimension(180, 20));
		textField.setEditable(false);
		panel = new JPanel();

		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setBackground(table.getBackground());
		panel.add(textField);
		panel.add(button);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		textField.setText(CommonUtil.objectToString(value));
		return panel;
	}

}
