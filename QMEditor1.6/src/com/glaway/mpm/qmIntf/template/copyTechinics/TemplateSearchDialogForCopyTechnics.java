package com.glaway.mpm.qmIntf.template.copyTechinics;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import org.dom4j.Document;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class TemplateSearchDialogForCopyTechnics extends JPanel {
	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private Document document;
	private NewTechnicsPart frame;

	public TemplateSearchDialogForCopyTechnics(Document document, NewTechnicsPart frame) {
		this.document = document;
		this.frame = frame;
		newDialog();
	}

	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("工艺模板库");
		SwingUtil.setMiddle(dialog);
	}

	public void  showDialog() {
		Container container = dialog.getContentPane();
		container.add(new TemplateSearchPanelForCopyTechnics(document, dialog));
		dialog.setVisible(true);
	}
}
