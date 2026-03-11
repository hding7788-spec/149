package com.glaway.mpm.qmIntf.technics;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RelateTypicalTechnicDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private NewTechnicsPart frame;
	private List<Technics> technicsList;
	private Element technicElement;
	private String docNumber;

	public RelateTypicalTechnicDialog(NewTechnicsPart frame) {
		this.frame = frame;
		newDialog();
	}
	public RelateTypicalTechnicDialog(NewTechnicsPart frame, List<Technics> technicsList, Element technicElement) {
		this(frame);
		this.technicsList = technicsList;
		this.technicElement = technicElement;
		this.docNumber = docNumber;
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("与本工艺工序关联");
		dialog.setSize(650, 450);
		SwingUtil.setMiddle(dialog);
	}

	public void showDialog() {
		Container container = dialog.getContentPane();
		RelateTypicalTechnicPanel panel = new RelateTypicalTechnicPanel(dialog, frame, technicsList,technicElement);
		container.add(panel);

		dialog.setVisible(true);
//		return panel.getBorrowTechnics();
	}

}