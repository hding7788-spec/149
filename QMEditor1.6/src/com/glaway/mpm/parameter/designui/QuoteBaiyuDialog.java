package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class QuoteBaiyuDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JFrame frame;

	public QuoteBaiyuDialog(JFrame frame) {
		super(frame, true);
		this.frame = frame;
		initDialog();
	}

	public void initDialog() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("引用记录表模板");
		setResizable(true);
		setSize(1400, 700);
		SwingUtil.setMiddle(this);
	}

	public ArrayList<ArrayList<String>> showDialog() {
		Container container = this.getContentPane();
		QuoteBaiyuPanel quoteBaiyuPanel = new QuoteBaiyuPanel(this, frame);
		container.add(quoteBaiyuPanel);
		this.setVisible(true);
		ArrayList<ArrayList<String>> equipments = quoteBaiyuPanel.equipments;
		quoteBaiyuPanel.equipments = null;
		return equipments;
	}
}