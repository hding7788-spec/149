package com.glaway.mpm.editor;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.glaway.mpm.util.InputLimited;

public class JTextFiledEditor2 extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;

	private JTextField textField = null;

	public JTextFiledEditor2(JTextField textField) {
		super(textField);
		this.textField = textField;
		//textField.setDocument(new InputLimited(3, true));
		this.textField.setBorder(null);
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value == null) {
			textField.setText("");
		} else {
			textField.setText(value.toString());
		}
		return this.textField;
	}

	public Object getCellEditorValue() {
		return textField.getText();
	}
}