package com.glaway.mpm.qmIntf.dashboard;

import java.awt.Container;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewStandardDashboardJPanel;

public class SDashboardSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;

	private JDialog parentDialog;
	private JFrame frame;
	private NewStandardDashboardJPanel panel;

	public SDashboardSearchDialog(NewStandardDashboardJPanel panel, JFrame frame) {
		this.panel = panel;
		this.frame = frame;
		newDialog();
	}

	public SDashboardSearchDialog(NewStandardDashboardJPanel panel, JDialog parentDialog) {
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
		dialog.setTitle("搜索标准仪器仪表");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new SDashboardSearchPanel(dialog, panel));
		dialog.setVisible(true);
		Vector<Map<String, String>> equipments = SDashboardSearchPanel.equipments;
		SDashboardSearchPanel.equipments = null;
		return equipments;
	}

}