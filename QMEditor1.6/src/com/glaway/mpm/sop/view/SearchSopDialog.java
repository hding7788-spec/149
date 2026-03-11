package com.glaway.mpm.sop.view;

import java.awt.Container;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.glaway.mpm.util.SwingUtil;

public class SearchSopDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JDialog parentDialog;
	private JFrame frame;
	private SopFileTableJPanel sopFileTableJPanel;

	public SearchSopDialog(SopFileTableJPanel panel, JFrame frame,JFrame parentJFrame) {
		super(parentJFrame, true);
		this.sopFileTableJPanel = panel;
		this.frame = frame;
		initDialog();
	}

	public SearchSopDialog(SopFileTableJPanel panel, JDialog parentDialog) {
		super(parentDialog, true);
		this.sopFileTableJPanel = panel;
		this.parentDialog = parentDialog;
		initDialog();
	}

	public void initDialog() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("搜索SOP文件");
		setResizable(true);
		setSize(1200, 900);
		SwingUtil.setMiddle(this);
	}

	public SopFileTableJPanel getSopFileTableJPanel() {
		return sopFileTableJPanel;
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = this.getContentPane();
		SearchSopPanel searchSopPanel = new SearchSopPanel(this, sopFileTableJPanel,frame);
		container.add(searchSopPanel);
		this.setVisible(true);
		Vector<Map<String, String>> equipments = SearchSopPanel.equipments;
		SearchSopPanel.equipments = null;
		return equipments;
	}
}