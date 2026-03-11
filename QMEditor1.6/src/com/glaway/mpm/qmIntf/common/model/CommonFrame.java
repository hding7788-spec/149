package com.glaway.mpm.qmIntf.common.model;

import javax.swing.JFrame;

import com.glaway.mpm.resource.Images;

public class CommonFrame extends JFrame {
	private static final long serialVersionUID = -3433690340895229996L;

	public CommonFrame() {
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Images.technicsIcon.getImage());
	}

}
