package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.glaway.mpm.print.constants.PrintConstants;

public class AddFileDialog extends JFrame {

	private static final long serialVersionUID = 4094018302868107771L;
	private SearchFileConditionPanel searchFileConditionPanel;
	private FileListTable fileListTable;
	private FileListTable parentTablePanel;
	private ButtonPanel buttonPanel;

	public AddFileDialog(FileListTable parentTablePanel) {
		this.parentTablePanel = parentTablePanel;
		initComponents();
		initLayout();
		initUI();
	}

	private void initComponents() {
		fileListTable = new FileListTable(this, PrintConstants.TITLE_ADDFILE, null);
		buttonPanel = new ButtonPanel(fileListTable, PrintConstants.TITLE_ADDFILE,null);
		searchFileConditionPanel = new SearchFileConditionPanel(fileListTable,this);
		buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 35));
	}

	private void initLayout() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.NORTH, searchFileConditionPanel, 0, SpringLayout.NORTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, searchFileConditionPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, searchFileConditionPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, fileListTable, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, fileListTable, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.NORTH, fileListTable, 5, SpringLayout.SOUTH, searchFileConditionPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, fileListTable, 0, SpringLayout.NORTH, buttonPanel);

		add(searchFileConditionPanel);
		add(fileListTable);
		add(buttonPanel);
	}

	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(PrintConstants.TITLE_ADDFILE);
    	setResizable(true);
    	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		//设置全屏
		setSize(width/2, (int) (height/2.2));
        setVisible(true);
	}

	public FileListTable getParentTablePanel() {
		return parentTablePanel;
	}

}
