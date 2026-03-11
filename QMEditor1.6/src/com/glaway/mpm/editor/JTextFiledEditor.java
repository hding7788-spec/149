package com.glaway.mpm.editor;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.glaway.mpm.util.InputFloatLimited;
import com.glaway.mpm.util.InputLimited;

public class JTextFiledEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;

	private JTextField textField = null;

	public JTextFiledEditor(JTextField textField, boolean isInt) {
		super(textField);
		this.textField = textField;
		if(isInt) {
			textField.setDocument(new InputLimited(10, true));
		} else {
			textField.setDocument(new InputFloatLimited(10, true));
		}
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
		String cellValue = textField.getText();
		int count = 0;
		for(int i = 0; i < cellValue.length(); i++){
			String str = String.valueOf(cellValue.charAt(i));
			if(str.equals(".")){
				count ++;
			}
		}
		if(count > 1){
			JOptionPane.showMessageDialog(null, "输入不合法，请重新输入！");
			return 1;
		}else{
			if(count == 1 && cellValue.startsWith(".")){
				return 0 + cellValue;
			}
			return textField.getText();
		}

	}
}