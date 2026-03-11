package com.glaway.mpm.qmIntf.template;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class TemplateSearchDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private String filePath;
	private String type;
	private JFrame frame;

	public TemplateSearchDialog(String filePath, String type, JFrame frame) {
		this.type = type;
		this.frame = frame;
		this.filePath = filePath;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("工艺模板库");
		dialog.setSize(600,800);
		SwingUtil.setMiddle(dialog);
		dialog.setResizable(true);
	}

	public Vector<?> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new TemplateSearchPanel(filePath, type, dialog));
		dialog.setVisible(true);
		Vector<Object> vector = TemplateSearchPanel.vector;
		TemplateSearchPanel.vector = null;
		return vector;
	}
}
