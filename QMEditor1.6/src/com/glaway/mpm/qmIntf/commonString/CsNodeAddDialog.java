package com.glaway.mpm.qmIntf.commonString;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class CsNodeAddDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private JDialog parentDialog;

	public CsNodeAddDialog(JDialog parentDialog) {
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(parentDialog);
		dialog.setTitle("新增常用语");
		dialog.setSize(350, 170);
		SwingUtil.setMiddle(dialog);
		// dialog.setLocation(650, 500);
	}

	public String showDialog() {
		Container container = dialog.getContentPane();
		container.add(new CsNodeAddPanel(dialog));
		dialog.setVisible(true);
		String returnValue = CsNodeAddPanel.returnValue;
		CsNodeAddPanel.returnValue = null;
		return returnValue;
	}
}
