package com.glaway.mpm.mesDataSearch.ui;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dom4j.Element;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;

public class MesDataTechnicSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private MesDataSearchMainFrame frame;
	private MesDataSearchMainPanel parent;

	public MesDataTechnicSearchDialog(MesDataSearchMainFrame frame, MesDataSearchMainPanel parent) {
		this.frame = frame;
		this.parent = parent;
		newDialog();
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("请选择相关工艺");
		dialog.setSize(650, 450);
		SwingUtil.setMiddle(dialog);
	}

	public Technics showDialog() {
		Container container = dialog.getContentPane();
		MesDataTechnicSearchPanel panel = new MesDataTechnicSearchPanel(dialog, frame, parent);
		container.add(panel);
		dialog.setVisible(true);
		return panel.getBorrowTechnics();
	}

}