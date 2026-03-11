package com.glaway.mpm.qmIntf.technics;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

public class TechnicsSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private NewTechnicsPart frame;
	private Technics technics;

	public TechnicsSearchDialog(NewTechnicsPart frame) {
		this.frame = frame;
		newDialog();
	}
	public TechnicsSearchDialog(NewTechnicsPart frame,Technics technics) {
		this(frame);
		this.technics = technics;
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("搜索工艺");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Technics showDialog() {
		Container container = dialog.getContentPane();
		TechnicsSearchPanel panel = new TechnicsSearchPanel(dialog, frame, technics);
		container.add(panel);
		dialog.setVisible(true);
		return panel.getBorrowTechnics();
	}

}