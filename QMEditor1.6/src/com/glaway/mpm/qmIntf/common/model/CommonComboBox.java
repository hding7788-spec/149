package com.glaway.mpm.qmIntf.common.model;

import java.awt.Dimension;

import javax.swing.JComboBox;

public class CommonComboBox extends JComboBox {
	private static final long serialVersionUID = 1L;

	public CommonComboBox() {
		setMinimumSize(new Dimension(100, 23));
		setPreferredSize(new Dimension(100, 23));
	}
}
