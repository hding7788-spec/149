package com.glaway.mpm.qmIntf.common.model;

import javax.swing.Action;
import javax.swing.JButton;

public class CommonActionButton extends JButton {

	private static final long serialVersionUID = 1L;

	public CommonActionButton(Action action, String tooltip) {
		super(action);
		// setBorder(null);
		setBorderPainted(false);
		setToolTipText(tooltip);
		// setPreferredSize(new Dimension(100, 25));
		// setMinimumSize(new Dimension(100, 25));
	}
}
