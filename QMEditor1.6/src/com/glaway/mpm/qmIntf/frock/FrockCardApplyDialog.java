package com.glaway.mpm.qmIntf.frock;

import java.awt.Container;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.glaway.mpm.model.Frock;
import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class FrockCardApplyDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private Map<String, String> map;
	public Frock frock;
	private JFrame frame;

	public FrockCardApplyDialog(Map<String, String> map, JFrame frame) {
		this.map = map;
		this.frame = frame;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("新建工装申请卡");
		dialog.setSize(650, 550);
		dialog.setResizable(false);
		SwingUtil.setMiddle(dialog);
	}

	public Frock showDialog() {
		Container container = dialog.getContentPane();
		FrockCardApplyPanel panel = new FrockCardApplyPanel(map, this, dialog);
		panel.setBorder(new TitledBorder(null, "基本信息",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		container.add(panel);
		dialog.setVisible(true);
		return this.frock;
	}
}
