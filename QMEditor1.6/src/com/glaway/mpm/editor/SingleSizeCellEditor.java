package com.glaway.mpm.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.glaway.mpm.qmIntf.material.MaterialUtil;

public class SingleSizeCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;

	private JPanel panel = new JPanel();
	private JLabel prefix = new JLabel();
	private JTextField textField = null;
	private JTable editingTable;
	private int editingColumn;
	private int editingRow;

	public SingleSizeCellEditor(JTextField textField) {
		super(textField);
		this.textField = textField;
		this.textField.setBorder(null);
		prefix.setForeground(Color.RED);
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		editingColumn = column;
		editingRow = row;
		editingTable = table;
		this.panel.setLayout(new BorderLayout());
		this.panel.add(this.prefix, BorderLayout.WEST);
		this.panel.add(this.textField, BorderLayout.CENTER);

		String prefixValue = table.getValueAt(row, column + 9).toString();
		// if (!prefixValue.endsWith("X")) {
		// prefixValue += "X";
		// }
		prefix.setText(prefixValue);
		if ((value != null) && ((value instanceof String))) {
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
		panel.setBackground(table.getBackground());
		return this.panel;
	}

	public Object getCellEditorValue() {
		String text = "";
		if (textField != null) {
			text = textField.getText();
		}
		Map<String, String> calMap = MaterialUtil.getOneRowMap(editingRow,
				editingTable);
		calMap.put("singleSize", prefix.getText() + text);
		Map<String, String> map = MaterialUtil.calQuota(calMap);
		if (map != null) {
			String quota = map.get("quota");
			String unit = map.get("unit");
			editingTable.setValueAt(quota, editingRow, 10);
			editingTable.setValueAt(unit, editingRow, 11);
		}
		return prefix.getText() + text;
	}
}