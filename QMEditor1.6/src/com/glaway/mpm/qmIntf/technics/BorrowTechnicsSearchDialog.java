package com.glaway.mpm.qmIntf.technics;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

import javax.swing.*;
import java.awt.*;

public class BorrowTechnicsSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private NewTechnicsPart frame;
	private Technics technics;
	private Container parentPanel;

	public BorrowTechnicsSearchDialog(NewTechnicsPart frame) {
		this.frame = frame;
		newDialog();
	}
	public BorrowTechnicsSearchDialog(NewTechnicsPart frame,Technics technics,Container parentPanel) {
		this(frame);
		this.technics = technics;
		this.parentPanel = parentPanel;
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("搜索工艺");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Technics showDialog() {
		Container container = dialog.getContentPane();
		BorrowTechnicsSearchPanel panel = new BorrowTechnicsSearchPanel(dialog, frame, technics,parentPanel);
		container.add(panel);
		dialog.setVisible(true);
		return panel.getBorrowTechnics();
	}

}