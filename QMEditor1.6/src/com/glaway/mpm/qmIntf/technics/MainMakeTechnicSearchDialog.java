package com.glaway.mpm.qmIntf.technics;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dom4j.Element;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;

public class MainMakeTechnicSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private NewTechnicsPart frame;
	private Technics technics;
	private Element technicElement;
	private XWTreeNode node;

	public MainMakeTechnicSearchDialog(NewTechnicsPart frame) {
		this.frame = frame;
		newDialog();
	}
	public MainMakeTechnicSearchDialog(NewTechnicsPart frame,Technics technics, Element technicElement) {
		this(frame);
		this.technics = technics;
		this.technicElement = technicElement;
		this.node = node;
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("关联主制工艺");
		dialog.setSize(650, 450);
		SwingUtil.setMiddle(dialog);
	}

	public Technics showDialog() {
		Container container = dialog.getContentPane();
		MainMakeTechnicSearchPanel panel = new MainMakeTechnicSearchPanel(dialog, frame, technics, technicElement);
		container.add(panel);
		dialog.setVisible(true);
		return panel.getBorrowTechnics();
	}

}