package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.glaway.mpm.print.constants.PrintConstants;

public class AddFileOnBomDialog extends JFrame {

	private static final long serialVersionUID = 4094018302868107771L;
	private SearchFileOnBomConditionPanel searchFileConditionPanel;
	private FileListTable fileListTable1;
	private FileListTable fileListTable2;
	private FileListTable parentTablePanel;
	private ButtonPanel buttonPanel;

	public AddFileOnBomDialog(FileListTable parentTablePanel) {
		this.parentTablePanel = parentTablePanel;
		initComponents();
		initLayout();
		initUI();
	}

	private void initComponents() {
		fileListTable1 = new FileListTable(this, PrintConstants.TITLE_ADDONBOM_PART,null);
		fileListTable2 = new FileListTable(this, PrintConstants.TITLE_ADDONBOM_PART_RELATE,null);
		buttonPanel = new ButtonPanel(fileListTable2, PrintConstants.TITLE_ADDONBOM_PART_RELATE,null);
		searchFileConditionPanel = new SearchFileOnBomConditionPanel(fileListTable1, this);
		buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 35));
	}

	private void initLayout() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.NORTH, searchFileConditionPanel, 0, SpringLayout.NORTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, searchFileConditionPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, searchFileConditionPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, fileListTable1, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, fileListTable1, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, fileListTable2, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, fileListTable2, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.NORTH, fileListTable1, 5, SpringLayout.SOUTH, searchFileConditionPanel);
		springLayout.putConstraint(SpringLayout.NORTH, fileListTable2, 0, SpringLayout.SOUTH, fileListTable1);
		springLayout.putConstraint(SpringLayout.SOUTH, fileListTable2, 0, SpringLayout.NORTH, buttonPanel);

		add(searchFileConditionPanel);
		add(fileListTable1);
		add(fileListTable2);
		add(buttonPanel);
	}

	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(PrintConstants.TITLE_ADDFILE);
    	setResizable(true);
    	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		//设置全屏
		setSize((int) (width/1.2), (int) (height/1.2));
        setVisible(true);
	}

	public FileListTable getParentTablePanel() {
		return parentTablePanel;
	}

	public FileListTable getFileListTable2() {
		return fileListTable2;
	}

}
