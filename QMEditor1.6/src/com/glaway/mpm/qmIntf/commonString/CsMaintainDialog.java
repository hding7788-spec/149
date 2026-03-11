package com.glaway.mpm.qmIntf.commonString;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.resource.Constants;
import com.glaway.mpm.util.SwingUtil;

public class CsMaintainDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private String filePath;
	private JFrame frame;

	public CsMaintainDialog(String filePath, JFrame frame) {
		this.filePath = filePath;
		this.frame = frame;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle(Constants.PERSONAL_CS_MAINTAIN);
		dialog.setSize(600, 600);
		dialog.setResizable(true);
		SwingUtil.setMiddle(dialog);
	}

	public void showDialog() {
		Container container = dialog.getContentPane();
		container.add(new CsMaintainPanel(filePath, dialog));
		dialog.setVisible(true);
	}

}
