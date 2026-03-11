package com.glaway.mpm.qmIntf.common.model;

import java.awt.Dimension;

import javax.swing.JButton;

public class CommonButton extends JButton {

	private static final long serialVersionUID = 1L;

	public CommonButton(String text) {
		setText(text);
		setPreferredSize(new Dimension(50, 25));
		setMinimumSize(new Dimension(50, 25));
	}

	public CommonButton(String text, Dimension dimension) {
		setText(text);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
	}
}
