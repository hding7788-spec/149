package com.glaway.mpm.view;

import java.awt.Dimension;

import javax.swing.JButton;

public class IconButton extends JButton {
	private final int BUTTON_WIDTH = 80;
	private final int BUTTON_HEIGHT = 23;

	public IconButton(String iconName, String tip) {
		super();

		Dimension buttonDimension = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
		setPreferredSize(buttonDimension);
		setMinimumSize(buttonDimension);
		setMaximumSize(buttonDimension);
		this.setText(tip);
		// this.setIcon(IconUtil.getImageIcon(iconName));
		this.setToolTipText(tip);
	}
}