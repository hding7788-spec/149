package com.glaway.mpm.qmIntf.commonString;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class CsNodeTypeAddDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private String message;
	private JDialog parentDialog;

	public CsNodeTypeAddDialog(String message, JDialog parentDialog) {
		this.message = message;
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(parentDialog);
		dialog.setTitle("新增" + message);
		dialog.setSize(350, 170);
		SwingUtil.setMiddle(dialog);
	}

	public String showDialog() {
		Container container = dialog.getContentPane();
		container.add(new CsNodeTypeAddPanel(dialog, message));
		dialog.setVisible(true);
		String returnValue = CsNodeTypeAddPanel.returnValue;
		CsNodeTypeAddPanel.returnValue = null;
		return returnValue;
	}
}
