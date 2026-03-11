package com.glaway.mpm.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class SingleSizeCellRenderer implements TableCellRenderer {
	private JPanel panel = new JPanel();
	private JLabel prefix = new JLabel();
	private JTextField textField = new JTextField();

	public Rectangle getBound() {
		return this.panel.getBounds();
	}

	public SingleSizeCellRenderer() {
		this.textField.setBorder(null);
		prefix.setForeground(Color.RED);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		this.panel.setLayout(new BorderLayout());
		this.panel.add(this.prefix, BorderLayout.WEST);
		this.panel.add(this.textField, BorderLayout.CENTER);
		String prefixValue = table.getValueAt(row, column + 9).toString();
		// if (!prefixValue.endsWith("X")) {
		// prefixValue += "X";
		// }
		prefix.setText(prefixValue);

		if (value instanceof String && value != null) {
			String text = (String) value;
			int index = text.indexOf(prefixValue);
			if (index == -1) {
				index = 0;
			}
			if (text.length() < prefixValue.length()) {
				textField.setText(text.substring(index + prefixValue.length()
						- 1));
			} else {
				textField.setText(text.substring(index + prefixValue.length()));
			}
		}
		if (isSelected) {
			panel.setBackground(table.getSelectionBackground());
			textField.setBackground(table.getSelectionBackground());
		} else {
			panel.setBackground(table.getBackground());
			textField.setBackground(table.getBackground());
		}
		return this.panel;
	}
}
