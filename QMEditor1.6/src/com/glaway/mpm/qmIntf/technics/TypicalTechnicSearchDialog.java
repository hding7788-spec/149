package com.glaway.mpm.qmIntf.technics;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.dom4j.Element;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

public class TypicalTechnicSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private NewTechnicsPart frame;
	private Technics technics;
	private Element technicElement;
	private JFrame frame2;

	public TypicalTechnicSearchDialog(Technics technics, Element technicElement,JFrame frame2){
		this.technics = technics;
		this.technicElement = technicElement;
		this.frame2 = frame2;
		newDialog2();
	}
	public TypicalTechnicSearchDialog(NewTechnicsPart frame) {
		this.frame = frame;
		newDialog();
	}
	public TypicalTechnicSearchDialog(NewTechnicsPart frame,Technics technics, Element technicElement) {
		this(frame);
		this.technics = technics;
		this.technicElement = technicElement;
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("搜索典型工艺");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}
	public void newDialog2() {
		dialog = new CommonDialog(frame2);
		dialog.setTitle("搜索典型工艺");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Technics showDialog() {
		Container container = dialog.getContentPane();
		TypicalTechnicSearchPanel panel = new TypicalTechnicSearchPanel(dialog, frame, technics, technicElement);
		container.add(panel);
		dialog.setVisible(true);
		return panel.getBorrowTechnics();
	}

}