package com.glaway.mpm.qmIntf.equipment;

import java.awt.Container;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewEquipJPanel;

public class EquipmentSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;

	private JDialog parentDialog;
	private JFrame frame;
	private NewEquipJPanel panel;

	public EquipmentSearchDialog(NewEquipJPanel panel, JFrame frame) {
		this.panel = panel;
		this.frame = frame;
		newDialog();
	}

	public EquipmentSearchDialog(NewEquipJPanel panel, JDialog parentDialog) {
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
		dialog.setTitle("搜索设备");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new EquipmentSearchPanel(dialog, panel));
		dialog.setVisible(true);
		Vector<Map<String, String>> equipments = EquipmentSearchPanel.equipments;
		EquipmentSearchPanel.equipments = null;
		return equipments;
	}

}