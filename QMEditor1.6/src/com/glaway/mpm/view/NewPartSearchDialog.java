package com.glaway.mpm.view;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.measure.MeasureSearchPanel;
import com.glaway.mpm.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Vector;

public class NewPartSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;

	private JDialog parentDialog;
	private JFrame frame;
	private NewPartJPanel panel;

	public NewPartSearchDialog(NewPartJPanel panel, JFrame frame) {
		this.panel = panel;
		this.frame = frame;
		newDialog();
	}

	public NewPartSearchDialog(NewPartJPanel panel, JDialog parentDialog) {
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
		dialog.setTitle("搜索量具");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = dialog.getContentPane();
//		container.add(new MeasureSearchPanel(dialog, panel));
		dialog.setVisible(true);
		Vector<Map<String, String>> equipments = MeasureSearchPanel.equipments;
		MeasureSearchPanel.equipments = null;
		return equipments;
	}

}