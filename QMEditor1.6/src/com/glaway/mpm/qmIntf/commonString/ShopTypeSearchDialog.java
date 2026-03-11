package com.glaway.mpm.qmIntf.commonString;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class ShopTypeSearchDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private JDialog parentDialog;

	public ShopTypeSearchDialog(JDialog parentDialog) {
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(parentDialog);
		dialog.setTitle("请选择工种");
		SwingUtil.setMiddle(dialog);
		// dialog.setLocation(650, 300);
	}

	public String showDialog() {
		Container container = dialog.getContentPane();
		container.add(new ShopTypeSearchPanel(dialog));
		dialog.setVisible(true);
		String returnValue = ShopTypeSearchPanel.returnValue;
		ShopTypeSearchPanel.returnValue = null;
		return returnValue;
	}

}
