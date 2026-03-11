package com.glaway.mpm.qmIntf.workspace;

import java.awt.Container;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewWorkSpaceJPanel;

public class WorkSpaceSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private JFrame frame;
	private JDialog parentDialog;
	private NewWorkSpaceJPanel panel;

	public WorkSpaceSearchDialog(NewWorkSpaceJPanel panel, JFrame frame) {
		this.panel = panel;
		this.frame = frame;
		newDialog();
	}

	public WorkSpaceSearchDialog(NewWorkSpaceJPanel panel, JDialog parentDialog) {
		this.panel = panel;
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		if (parentDialog != null) {
			dialog = new CommonDialog(parentDialog);
		} else {
			dialog = new CommonDialog(frame);
		}
		dialog.setTitle("搜索工位");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new WorkSpaceSearchPanel(dialog, panel));
		dialog.setVisible(true);
		Vector<Map<String, String>> equipments = WorkSpaceSearchPanel.equipments;
		WorkSpaceSearchPanel.equipments = null;
		return equipments;
	}

}