package com.glaway.mpm.parameter.commonui;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.util.InputFloatLimited;
import com.glaway.mpm.parameter.util.InputIntegerLimited;

public class CommonTextFiledEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	private JTextField textField;

	public CommonTextFiledEditor(JTextField textField, boolean isInt) {
		super(textField);
		this.textField = textField;
		if (!isInt) {
			textField.setDocument(new InputFloatLimited(10, true));
		} else {
			textField.setDocument(new InputIntegerLimited(10, isInt));
		}
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

	@Override
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
