package com.glaway.mpm.view;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class GwPbomQuotaDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private JFrame parentFrame;
	private NewPartJPanel newPartJPanel;

	public GwPbomQuotaDialog(JFrame parentFrame,NewPartJPanel newPartJPanel) {
		this.parentFrame = parentFrame;
		this.newPartJPanel = newPartJPanel;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(parentFrame);
		dialog.setTitle("PBOM参装");
		dialog.setSize(1600, 800);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<VaTreeNode> showDialog() {
		Container container = dialog.getContentPane();
		container.add(new GwPbomQuotaPanel(dialog, newPartJPanel));
		dialog.setVisible(true);
		Vector<VaTreeNode> parts = GwPbomQuotaPanel.parts;
		GwPbomQuotaPanel.parts = null;
		return parts;
	}

}