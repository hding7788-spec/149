package com.glaway.mpm.qmIntf.frock;

import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewKnifeToolPanel;
import com.glaway.mpm.view.NewToolJPanel;

public class FrockCardSearchDialog extends CommonDialog {
	private static final long serialVersionUID = 1L;

	private FrockCardSearchPanel frockCardSearchPanel;
	private JFrame frame;
	private NewToolJPanel toolPanel;
	private NewKnifeToolPanel knifeToolPanel;

	public FrockCardSearchDialog(JFrame frame, NewToolJPanel toolPanel) {
		super(frame);
		this.frame = frame;
		this.toolPanel = toolPanel;
	}

	public FrockCardSearchDialog(JDialog dialog, NewToolJPanel toolPanel) {
		super(dialog);
		this.toolPanel = toolPanel;
	}

	public FrockCardSearchDialog(JFrame frame, NewKnifeToolPanel toolPanel,String type) {
		super(frame);
		this.frame = frame;
		this.knifeToolPanel = toolPanel;
	}

	public FrockCardSearchDialog(JDialog dialog, NewKnifeToolPanel toolPanel) {
		super(dialog);
		this.knifeToolPanel = toolPanel;
	}

	public void showDialog(Map<String, String> map) {
		if(toolPanel != null) {
			frockCardSearchPanel = new FrockCardSearchPanel(map, toolPanel, frame);
		} else {
			frockCardSearchPanel = new FrockCardSearchPanel(map, knifeToolPanel, frame);
		}
		this.getContentPane().add(frockCardSearchPanel);
		if (map == null) {
			setSize(850, 720);
		} else {
			setSize(850, 570);
		}
		setResizable(false);
		SwingUtil.setMiddle(this);
		setVisible(true);
	}
}
