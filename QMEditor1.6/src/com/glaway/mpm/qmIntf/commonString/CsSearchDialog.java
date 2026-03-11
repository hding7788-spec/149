package com.glaway.mpm.qmIntf.commonString;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class CsSearchDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private String filePath;
	private JDialog parentDialog;
	private JFrame parentFrame;

	public CsSearchDialog(String filePath, JDialog parentDialog) {
		this.filePath = filePath;
		this.parentDialog = parentDialog;
		newDialog();
	}

	public CsSearchDialog(String filePath, JFrame parentFrame) {
		this.filePath = filePath;
		this.parentFrame = parentFrame;
		newDialog();
	}

	public void newDialog() {
		if (parentFrame != null) {
			dialog = new CommonDialog(parentFrame);
		} else {
			dialog = new CommonDialog(parentDialog);
		}
		dialog.setTitle("典型工艺常用语库");
		SwingUtil.setMiddle(dialog);
	}

	public String showDialog() {
		Container container = dialog.getContentPane();
		container.add(new CsSearchPanel(filePath, dialog));
		dialog.setVisible(true);
		String returnValue = CsSearchPanel.returnValue;
		CsSearchPanel.returnValue = null;
		return returnValue;
	}

}
