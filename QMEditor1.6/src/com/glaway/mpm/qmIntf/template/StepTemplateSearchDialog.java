package com.glaway.mpm.qmIntf.template;

import java.awt.Container;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class StepTemplateSearchDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private String filePath;
	private String type;
	private JFrame frame;
	public StepTree stTree;
	public static TreePath currentTreePath;

	public StepTemplateSearchDialog(String filePath, String type, JFrame frame) {
		this.type = type;
		this.filePath = filePath;
		this.frame = frame;
		this.frame.setResizable(true);
		currentTreePath=null;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("工序模板库");
		dialog.setResizable(true);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<?> showDialog() {
		Container container = dialog.getContentPane();
		StepTemplateSearchPanel stepTemplateSearchPanel = new StepTemplateSearchPanel(filePath, type, dialog);
		this.stTree = stepTemplateSearchPanel.stTree;
		container.add(stepTemplateSearchPanel);
		dialog.setVisible(true);
		Vector<Object> vector = StepTemplateSearchPanel.vector;
		StepTemplateSearchPanel.vector = null;
		return vector;
	}
}
