package com.glaway.mpm.qmIntf.technics;

import java.awt.Container;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dom4j.Element;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

public class SetPreProdureDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private NewTechnicsPart frame;

	public SetPreProdureDialog(NewTechnicsPart frame) {
		this.frame = frame;
		newDialog();
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("设置前置工序");
		dialog.setSize(650, 450);
		SwingUtil.setMiddle(dialog);
	}

	public void showDialog() {
		Container container = dialog.getContentPane();
		SetPreProdurePanel panel = new SetPreProdurePanel(dialog, frame);
		container.add(panel);
		dialog.setVisible(true);
//		return panel.getBorrowTechnics();
	}

}