package com.glaway.mpm.qmIntf.tecparam;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class TechnicsParamSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private JFrame frame;
	private JDialog parentDialog;

	public TechnicsParamSearchDialog(JFrame parent) {
		this.frame = parent;
		newDialog();
	}

	public TechnicsParamSearchDialog(JDialog parentDialog) {
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		if (parentDialog != null) {
			dialog = new CommonDialog(parentDialog);
		} else {
			dialog = new CommonDialog(frame);
		}
		dialog.setTitle("搜索工艺参数");
		dialog.setSize(1000, 800);
		SwingUtil.setMiddle(dialog);
	}

	public String showDialog() {
		Container container = dialog.getContentPane();
		container.add(new TechnicsParamSearchPanel(dialog));
		dialog.setVisible(true);
		String param = TechnicsParamSearchPanel.param;
		TechnicsParamSearchPanel.param = "";
		return param;
	}

}