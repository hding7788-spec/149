package com.glaway.mpm.qmIntf.commonString;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class CsNodeTypeModifyDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private String modifyString;
	private JDialog parentDialog;

	public CsNodeTypeModifyDialog(String modifyString, JDialog parentDialog) {
		this.modifyString = modifyString;
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(parentDialog);
		dialog.setTitle("修改常用语类型");
		dialog.setSize(280, 170);
		SwingUtil.setMiddle(dialog);
	}

	public String showDialog() {
		Container container = dialog.getContentPane();
		container.add(new CsNodeTypeModifyPanel(modifyString, dialog));
		dialog.setVisible(true);
		String returnValue = CsNodeTypeModifyPanel.returnValue;
		CsNodeTypeModifyPanel.returnValue = null;
		return returnValue;
	}
}
