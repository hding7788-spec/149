package com.glaway.mpm.view;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class CommonComboBoxEditor extends DefaultCellEditor {
	
	private static final long serialVersionUID = 1L;

	public CommonComboBoxEditor(String[] items) {
		super(new JComboBox(items));
	}
}
