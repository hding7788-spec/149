package com.glaway.mpm.qmIntf.dashboard;

import java.awt.Container;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewUnStandardDashboardJPanel;

public class UnSDashboardSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;

	private JDialog parentDialog;
	private JFrame frame;
	private NewUnStandardDashboardJPanel panel;

	public UnSDashboardSearchDialog(NewUnStandardDashboardJPanel panel, JFrame frame) {
		this.panel = panel;
		this.frame = frame;
		newDialog();
	}

	public UnSDashboardSearchDialog(NewUnStandardDashboardJPanel panel, JDialog parentDialog) {
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
		dialog.setTitle("搜索非标准仪器仪表");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new UnSDashboardSearchPanel(dialog, panel));
		dialog.setVisible(true);
		Vector<Map<String, String>> equipments = UnSDashboardSearchPanel.equipments;
		UnSDashboardSearchPanel.equipments = null;
		return equipments;
	}

}