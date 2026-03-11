package com.glaway.mpm.qmIntf.frock;

import java.awt.Container;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewKnifeToolPanel;
import com.glaway.mpm.view.NewToolJPanel;
import com.glaway.mpm.view.WaiXieTecDescirbeJPanel;

public class FrockSearchDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private JFrame parentFrame;
	private JDialog parentDialog;
	private NewToolJPanel panel;
	private NewKnifeToolPanel knifePanel;
	private WaiXieTecDescirbeJPanel waiXieTecDescirbeJPanel;

	public FrockSearchDialog(JFrame parentFrame, WaiXieTecDescirbeJPanel panel) {
		this.waiXieTecDescirbeJPanel = panel;
		this.parentFrame = parentFrame;
		newDialog();
	}

	public FrockSearchDialog(JFrame parentFrame, NewToolJPanel panel) {
		this.panel = panel;
		this.parentFrame = parentFrame;
		newDialog();
	}

	public FrockSearchDialog(JDialog parentDialog, NewToolJPanel panel) {
		this.panel = panel;
		this.parentDialog = parentDialog;
		newDialog();
	}

	public FrockSearchDialog(JFrame parentFrame, NewKnifeToolPanel panel,String type) {
		this.knifePanel = panel;
		this.parentFrame = parentFrame;
		newDialog();
	}

	public FrockSearchDialog(JDialog parentDialog, NewKnifeToolPanel panel) {
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
		dialog.setTitle("搜索工装");
		dialog.setSize(650, 550);
		SwingUtil.setMiddle(dialog);
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = dialog.getContentPane();
		if(panel != null) {
			container.add(new FrockSearchPanel(dialog, panel));
		} else if (waiXieTecDescirbeJPanel != null) {
			container.add(new FrockSearchPanel(dialog, waiXieTecDescirbeJPanel));
		} else {
			container.add(new FrockSearchPanel(dialog, knifePanel));
		}
		dialog.setVisible(true);
		Vector<Map<String, String>> frocks = FrockSearchPanel.frocks;
		FrockSearchPanel.frocks = null;
		return frocks;
	}

}