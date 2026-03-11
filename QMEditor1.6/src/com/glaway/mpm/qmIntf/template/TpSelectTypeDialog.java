package com.glaway.mpm.qmIntf.template;

import java.awt.Container;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class TpSelectTypeDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private String type;
	private String useType;
	private JDialog parentDialog;

	public TpSelectTypeDialog(String type, String useType, JDialog parentDialog) {
		this.type = type;
		this.useType = useType;
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(parentDialog);
		dialog.setTitle("选择" + useType + "模板类型");
		dialog.setSize(400, 600);
		SwingUtil.setMiddle(dialog);
		// dialog.setLocation(550, 350);
	}

	public List<String> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new TpSelectTypePanel(type, dialog, useType));
		dialog.setVisible(true);
		List<String> list = TpSelectTypePanel.list;
		TpSelectTypePanel.list = null;
		return list;
	}
}
