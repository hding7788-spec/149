package com.glaway.mpm.qmIntf.template;

import java.awt.Container;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class PaceSearchDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private String filePath;
	private JFrame frame;

	public PaceSearchDialog(String filePath, JFrame frame) {
		this.filePath = filePath;
		this.frame = frame;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("工步模板库");
		SwingUtil.setMiddle(dialog);
	}

	public Vector<?> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new PaceSearchPanel(filePath, dialog));
		dialog.setVisible(true);
		Vector<Object> vector = PaceSearchPanel.vector;
		PaceSearchPanel.vector = null;
		return vector;
	}
}
