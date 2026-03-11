package com.glaway.mpm.parameter.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.glaway.mpm.parameter.ui.MPMParameterMainFrame;

public class ParameterFrameWindowListener extends WindowAdapter {
	private MPMParameterMainFrame mainFrame;

	public ParameterFrameWindowListener(MPMParameterMainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		int returnValue = JOptionPane.showConfirmDialog(mainFrame, "确定要退出吗？", "提示", JOptionPane.OK_CANCEL_OPTION);
		if (returnValue == JOptionPane.OK_OPTION) {
			mainFrame.closeWindow();
		} else {
			mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}
