package com.glaway.mpm.qmIntf.template;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class StepTemplateMaintainDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private String filePath;
	private JFrame frame;

	public StepTemplateMaintainDialog(String filePath, JFrame frame) {
		this.filePath = filePath;
		this.frame = frame;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("本地工序模板库维护");
		dialog.setSize(600,800);
		dialog.setResizable(true);
		SwingUtil.setMiddle(dialog);
	}

	public void showDialog() {
		Container container = dialog.getContentPane();
		container.add(new StepTemplateMaintainPanel(filePath, dialog));
		dialog.setVisible(true);
	}
}
