package com.glaway.mpm.view;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dom4j.Element;

import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

public class LargeFileSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private NewTechnicsPart frame;
	private Element stepElement;
	private Container parent;

	public LargeFileSearchDialog(NewTechnicsPart frame,Container parent, Element stepElement) {
		this.frame = frame;
		this.stepElement = stepElement;
		this.parent = parent;
		newDialog();
	}
	public void newDialog() {
		dialog = new CommonDialog(frame);
		dialog.setTitle("关联已有大文件");
		dialog.setSize(650, 500);
		SwingUtil.setMiddle(dialog);
	}

	public void showDialog() {
		Container container = dialog.getContentPane();
		LargeFileSearchPanel panel = new LargeFileSearchPanel(dialog, parent, stepElement);
		container.add(panel);
		dialog.setVisible(true);
	}

}