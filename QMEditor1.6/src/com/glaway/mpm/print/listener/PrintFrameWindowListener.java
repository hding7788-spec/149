package com.glaway.mpm.print.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.glaway.mpm.print.ui.MPMPrintFileFrame;
import com.glaway.mpm.util.CommonUIUtil;

public class PrintFrameWindowListener extends WindowAdapter {
	private MPMPrintFileFrame mainFrame;

	public PrintFrameWindowListener(MPMPrintFileFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		int returnValue = CommonUIUtil.showConfirmDialog(mainFrame, "确定要退出吗？", "提示", JOptionPane.OK_CANCEL_OPTION);
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
