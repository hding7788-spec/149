package com.glaway.mpm.qmIntf.common.model;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.glaway.mpm.resource.Images;

public class CommonDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public CommonDialog(JFrame frame) {
		super(frame, true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 500);
		setIconImage(Images.technicsIcon.getImage());
		setResizable(false);
	}

	public CommonDialog(JDialog dialog) {
		super(dialog, true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(400, 500);
		setIconImage(Images.technicsIcon.getImage());
		setResizable(false);
	}

}
