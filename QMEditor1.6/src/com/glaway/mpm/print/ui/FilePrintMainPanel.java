package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;

public class FilePrintMainPanel extends JPanel {

	private static final long serialVersionUID = -3637835351215208709L;
	private FileListTable fileTable;
	private FileListTable searchFileTable;
	private ButtonPanel buttonPanel;
	private RecipientsInfoPanel recipientsInfoPanel;
	private SearchSealPlusPanel searchSealPlusPanel;
	private SearchOutFilePanel searchOutFilePanel;
	private MPMPrintFileFrame frame;
	private String type;
	private String category;
	private ButtonPanel addButtonPanel;
//	private SearchPrintInfoPanel searchPrintInfoPanel;

	public FilePrintMainPanel(MPMPrintFileFrame frame, String type, String category) {
		this.frame = frame;
		this.type = type;
		this.category = category;
		initComponents();
		initUI();
	}

	private void initComponents() {
		fileTable = new FileListTable(this, type, category);
		buttonPanel = new ButtonPanel(fileTable, type, category);
		buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 35));
		if (type.equals(PrintConstants.TITLE_MAINPANEL_LQZZWJ)) {
			recipientsInfoPanel = new RecipientsInfoPanel(fileTable);
		} else if (type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL)) {
			searchFileTable = new FileListTable(this, PrintConstants.TITLE_MAINPANEL_JGYZGL_RELATE,category);
			addButtonPanel = new ButtonPanel(searchFileTable, type + "add", category);
			addButtonPanel.setPreferredSize(new Dimension(this.getWidth(), 35));
			searchSealPlusPanel = new SearchSealPlusPanel(fileTable, frame, searchFileTable);
		} else if (type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)) {
			searchOutFilePanel = new SearchOutFilePanel(fileTable, frame, category);
		}
	}

	private void initUI() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		if(type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)){//文件打印申请
			springLayout.putConstraint(SpringLayout.NORTH, fileTable, 10, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, fileTable, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, fileTable, -5, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, fileTable, -45, SpringLayout.SOUTH, this);

			springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, -5, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, buttonPanel, -10, SpringLayout.EAST, this);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_ZXDYSQ)){
			springLayout.putConstraint(SpringLayout.NORTH, fileTable, 10, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, fileTable, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, fileTable, -5, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, fileTable, -45, SpringLayout.SOUTH, this);

			springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, -5, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, buttonPanel, -10, SpringLayout.EAST, this);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_YLDYSQ)){
			springLayout.putConstraint(SpringLayout.NORTH, fileTable, 10, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, fileTable, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, fileTable, -5, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, fileTable, -45, SpringLayout.SOUTH, this);

			springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, -5, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, buttonPanel, -10, SpringLayout.EAST, this);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_LQZZWJ)){
			springLayout.putConstraint(SpringLayout.NORTH, recipientsInfoPanel, 10, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, recipientsInfoPanel, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, recipientsInfoPanel, -5, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.WEST, fileTable, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, fileTable, -5, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, -5, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, buttonPanel, -10, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.NORTH, fileTable, 5, SpringLayout.SOUTH, recipientsInfoPanel);
			springLayout.putConstraint(SpringLayout.SOUTH, fileTable, -5, SpringLayout.NORTH, buttonPanel);
			add(recipientsInfoPanel);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL)){
			springLayout.putConstraint(SpringLayout.NORTH, searchSealPlusPanel, 0, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, searchSealPlusPanel, 0, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, searchSealPlusPanel, 0, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.WEST, searchFileTable, 0, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, searchFileTable, 0, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.WEST, addButtonPanel, 0, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, addButtonPanel, 0, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.WEST, fileTable, 0, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, fileTable, 0, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 0, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, buttonPanel, 0, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.NORTH, searchFileTable, 5, SpringLayout.SOUTH, searchSealPlusPanel);
			springLayout.putConstraint(SpringLayout.NORTH, addButtonPanel, 0, SpringLayout.SOUTH, searchFileTable);
			springLayout.putConstraint(SpringLayout.NORTH, fileTable, 0, SpringLayout.SOUTH, addButtonPanel);
			springLayout.putConstraint(SpringLayout.SOUTH, fileTable, 0, SpringLayout.NORTH, buttonPanel);
			add(searchSealPlusPanel);
			add(searchFileTable);
			add(addButtonPanel);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_JGYZQR)){
			springLayout.putConstraint(SpringLayout.NORTH, fileTable, 10, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, fileTable, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, fileTable, -5, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, fileTable, -45, SpringLayout.SOUTH, this);

			springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, -5, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, buttonPanel, -10, SpringLayout.EAST, this);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)){
			springLayout.putConstraint(SpringLayout.NORTH, searchOutFilePanel, 10, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, searchOutFilePanel, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, searchOutFilePanel, -5, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.WEST, fileTable, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, fileTable, -5, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, -5, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, buttonPanel, -10, SpringLayout.EAST, this);

			springLayout.putConstraint(SpringLayout.NORTH, fileTable, 5, SpringLayout.SOUTH, searchOutFilePanel);
			springLayout.putConstraint(SpringLayout.SOUTH, fileTable, -5, SpringLayout.NORTH, buttonPanel);
			add(searchOutFilePanel);
		} else if(type.equals(PrintConstants.TITLE_MAINPANEL_WJBDSQ)){
			springLayout.putConstraint(SpringLayout.NORTH, fileTable, 10, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, fileTable, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, fileTable, -5, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, fileTable, -45, SpringLayout.SOUTH, this);

			springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, -5, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, buttonPanel, -10, SpringLayout.EAST, this);
		}
		add(fileTable);
		add(buttonPanel);
		if(MPMPrintFileFrame.isComplete){
			buttonPanel.getSureButton().setEnabled(false);
			buttonPanel.getPrintButton().setEnabled(false);
		}else{
			buttonPanel.getSureButton().setEnabled(true);
			buttonPanel.getPrintButton().setEnabled(true);
		}
	}


	public void setUIValues(List<CmPrintInfoBean> printInfoBeanList) {
		fileTable.setUIValues(printInfoBeanList);
	}

	public MPMPrintFileFrame getFrame() {
		return frame;
	}

	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	public FileListTable getFileTable() {
		return fileTable;
	}

	public FileListTable getSearchFileTable() {
		return searchFileTable;
	}

	public RecipientsInfoPanel getRecipientsInfoPanel(){
		return recipientsInfoPanel;
	}

	public ButtonPanel getAddButtonPanel() {
		return addButtonPanel;
	}

	public void setAddButtonPanel(ButtonPanel addButtonPanel) {
		this.addButtonPanel = addButtonPanel;
	}


}
