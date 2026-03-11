package com.glaway.mpm.view;

import com.glaway.mpm.util.IconUtil;

import javax.swing.*;
import java.awt.*;

public class GwIconButton extends JButton {
	private final int BUTTON_WIDTH = 100;
	private final int BUTTON_HEIGHT = 23;

	public GwIconButton(String iconName, String tip) {
		super();

		Dimension buttonDimension = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
		setPreferredSize(buttonDimension);
		setMinimumSize(buttonDimension);
		setMaximumSize(buttonDimension);
		this.setText(tip);
		 this.setIcon(IconUtil.getImageIcon(iconName));
		this.setToolTipText(tip);
	}
}