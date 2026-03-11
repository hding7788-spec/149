package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.glaway.mpm.print.constants.PrintConstants;

public class AddPrintApplicationDialog extends JFrame {

	private static final long serialVersionUID = 6996895704234550595L;
	private SearchPrintApplicationPanel searchPrintApplicationPanel;
	private FileListTable fileListTable;
	private FileListTable parentTablePanel;
	private ButtonPanel buttonPanel;
	private String category;

	public AddPrintApplicationDialog(FileListTable parentTablePanel, String category) {
		this.parentTablePanel = parentTablePanel;
		this.category = category;
		initComponents();
		initLayout();
		initUI();
	}

	private void initComponents() {
		fileListTable = new FileListTable(this, PrintConstants.TITLE_MAINPANEL_ADDREQUEST,category);
		buttonPanel = new ButtonPanel(fileListTable, PrintConstants.TITLE_MAINPANEL_ADDREQUEST, category);
		searchPrintApplicationPanel = new SearchPrintApplicationPanel(fileListTable, this, category);
		buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 35));
	}

	private void initLayout() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.NORTH, searchPrintApplicationPanel, 0, SpringLayout.NORTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, searchPrintApplicationPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, searchPrintApplicationPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, fileListTable, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, fileListTable, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.NORTH, fileListTable, 5, SpringLayout.SOUTH, searchPrintApplicationPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, fileListTable, 0, SpringLayout.NORTH, buttonPanel);

		add(searchPrintApplicationPanel);
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
		setSize((int) (width/1.2), (int) (height/1.8));
        setVisible(true);
	}

	public FileListTable getParentTablePanel() {
		return parentTablePanel;
	}

	public String getCategory(){
		return category;
	}

}
