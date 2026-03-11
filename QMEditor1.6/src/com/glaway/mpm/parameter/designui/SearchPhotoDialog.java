package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.util.SwingUtil;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Vector;

public class SearchPhotoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private PhotoRecordTablePanel photoRecordTablePanel;

	public SearchPhotoDialog(PhotoRecordTablePanel panel, JFrame frame) {
		super(frame, true);
		this.photoRecordTablePanel = panel;
		this.frame = frame;
		initDialog();
	}

	public void initDialog() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("搜索照片样张");
		setResizable(true);
		setSize(1400, 700);
		SwingUtil.setMiddle(this);
	}

	public PhotoRecordTablePanel getPhotoRecordTablePanel() {
		return photoRecordTablePanel;
	}

	public Vector<Map<String, String>> showDialog() {
		Container container = this.getContentPane();
		SearchPhotoPanel searchPhotoPanel = new SearchPhotoPanel(this, photoRecordTablePanel,frame);
		container.add(searchPhotoPanel);
		this.setVisible(true);
		Vector<Map<String, String>> equipments = SearchPhotoPanel.equipments;
		SearchPhotoPanel.equipments = null;
		return equipments;
	}
}