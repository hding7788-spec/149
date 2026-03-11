package com.glaway.mpm.parameter.commonui;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.util.InputEnglishLimited;


public class CommonEnglishFiledEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	private JTextField textField;

	public CommonEnglishFiledEditor(JTextField textField) {
		super(textField);
		this.textField = textField;
		this.textField.setDocument(new InputEnglishLimited());
		this.textField.setBorder(null);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value == null) {
			textField.setText("");
		} else {
			textField.setText(value.toString());
		}
		return textField;
	}

}
