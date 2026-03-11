package com.glaway.mpm.qmIntf.common.model;

import java.awt.Dimension;

import javax.swing.JLabel;

public class CommonLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	public CommonLabel(String text) {
		super(text);
		setPreferredSize(new Dimension(150, 25));
		setMinimumSize(new Dimension(150, 25));
	}

	public CommonLabel(Dimension dimension) {
		setPreferredSize(dimension);
		setMinimumSize(dimension);
	}
}
