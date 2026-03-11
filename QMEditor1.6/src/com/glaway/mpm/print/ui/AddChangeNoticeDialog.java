package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;

public class AddChangeNoticeDialog extends JFrame {

	private static final long serialVersionUID = 7958459210083578534L;
	private AfterChangeInfoPanel afterChangeInfoPanel;
	private ChangeNoticeInfoPanel changeNoticeInfoPanel;
	private FileListTable parentTablePanel;
	private ButtonPanel buttonPanel;
	private CmPrintInfoBean cmPrintInfoBean;
	private String category;

	public AddChangeNoticeDialog(FileListTable parentTablePanel, CmPrintInfoBean cmPrintInfoBean, String category) {
		this.parentTablePanel = parentTablePanel;
		this.cmPrintInfoBean = cmPrintInfoBean;
		this.category = category;
		initComponents();
		initLayout();
		initUI();
	}

	private void initComponents() {
		afterChangeInfoPanel = new AfterChangeInfoPanel(this,cmPrintInfoBean,category);
		changeNoticeInfoPanel = new ChangeNoticeInfoPanel(this,cmPrintInfoBean,category);
		buttonPanel = new ButtonPanel(changeNoticeInfoPanel, PrintConstants.TITLE_ADDFILECHANGEINFO,null);
		buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 35));
	}

	private void initLayout() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.WEST, afterChangeInfoPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, afterChangeInfoPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, changeNoticeInfoPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, changeNoticeInfoPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.NORTH, changeNoticeInfoPanel, 0, SpringLayout.SOUTH, afterChangeInfoPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, changeNoticeInfoPanel, 0, SpringLayout.NORTH, buttonPanel);

		add(afterChangeInfoPanel);
		add(changeNoticeInfoPanel);
		add(buttonPanel);
	}

	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(PrintConstants.TITLE_ADDFILECHANGEINFO);
    	setResizable(true);
    	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		//设置全屏
		setSize(width/2 - 100, height/2);
        setVisible(true);
	}

	public FileListTable getParentTablePanel() {
		return parentTablePanel;
	}

	public AfterChangeInfoPanel getAfterChangeInfoPanel(){
		return afterChangeInfoPanel;
	}

}
