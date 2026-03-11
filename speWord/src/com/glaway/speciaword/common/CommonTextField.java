package com.glaway.speciaword.common;

import java.awt.Dimension;

import javax.swing.JTextField;

public class CommonTextField extends JTextField {

	private static final long serialVersionUID = 1L;

	public CommonTextField() {
		super();
		setPreferredSize(new Dimension(100, 25));
		setMinimumSize(new Dimension(100, 25));
	}

	public CommonTextField(Dimension dimension) {
		super();
		setPreferredSize(dimension);
		setMinimumSize(dimension);
	}

	public CommonTextField(Dimension dimension, boolean editable) {
		super();
		setEditable(editable);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
	}
}
