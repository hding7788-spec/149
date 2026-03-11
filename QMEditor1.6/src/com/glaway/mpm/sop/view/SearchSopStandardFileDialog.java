package com.glaway.mpm.sop.view;

import java.awt.Container;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.glaway.mpm.sop.model.SopBean;
import com.glaway.mpm.util.SwingUtil;

public class SearchSopStandardFileDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JDialog parentDialog;
	private JFrame frame;
	private SopStandardFileTableJPanel sopFileTableJPanel;

	public SearchSopStandardFileDialog(SopStandardFileTableJPanel panel, JFrame frame) {
		super(frame, true);
		this.sopFileTableJPanel = panel;
		this.frame = frame;
		initDialog();
	}

	public SearchSopStandardFileDialog(SopStandardFileTableJPanel panel, JDialog parentDialog) {
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

	public SopStandardFileTableJPanel getSopFileTableJPanel() {
		return sopFileTableJPanel;
	}

	public Vector<SopBean> showDialog() {
		Container container = this.getContentPane();
		SearchSopStandardFilePanel searchSopPanel = new SearchSopStandardFilePanel(this, sopFileTableJPanel,frame);
		container.add(searchSopPanel);
		this.setVisible(true);
		Vector<SopBean> equipments = SearchSopStandardFilePanel.equipments;
		SearchSopPanel.equipments = null;
		return equipments;
	}
}