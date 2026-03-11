package com.glaway.mpm.qmIntf.frock;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewKnifeToolPanel;
import com.glaway.mpm.view.NewToolJPanel;

public class KnifeSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private JFrame parentFrame;
	private JDialog parentDialog;
	private NewToolJPanel panel;
	private NewKnifeToolPanel knifePanel;

	public KnifeSearchDialog(JFrame parentFrame, NewToolJPanel panel) {
		this.panel = panel;
		this.parentFrame = parentFrame;
		newDialog();
	}

	public KnifeSearchDialog(JDialog parentDialog, NewToolJPanel panel) {
		this.panel = panel;
		this.parentDialog = parentDialog;
		newDialog();
	}

	public KnifeSearchDialog(JFrame parentFrame, NewKnifeToolPanel panel,String type) {
		this.knifePanel = panel;
		this.parentFrame = parentFrame;
		newDialog();
	}

	public KnifeSearchDialog(JDialog parentDialog, NewKnifeToolPanel panel) {
		this.knifePanel = panel;
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		if (parentFrame != null) {
			dialog = new CommonDialog(parentFrame);
		} else {
			dialog = new CommonDialog(parentDialog);
		}
		dialog.setTitle("搜索刀具");

		Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setSize(scrSize.width-100,scrSize.height-100);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = dialog.getContentPane();
		if(panel != null) {
			container.add(new KnifeSearchPanel(dialog, panel));
		} else {
			container.add(new KnifeSearchPanel(dialog, knifePanel));
		}
		dialog.setVisible(true);
		Vector<Map<String, String>> frocks = KnifeSearchPanel.frocks;
		KnifeSearchPanel.frocks = null;
		return frocks;
	}

}