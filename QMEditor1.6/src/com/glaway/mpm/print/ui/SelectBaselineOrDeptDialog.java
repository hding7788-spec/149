package com.glaway.mpm.print.ui;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.util.CommonUIUtil;

public class SelectBaselineOrDeptDialog extends JDialog {

	private static final long serialVersionUID = 8525215508770340677L;
	private SelectBaselineOrDeptPanel selectBaselineOrDeptPanel;
	private SearchBaselineDialog searchBaselineDialog;
	private ButtonPanel buttonPanel;
	private String type;
	private JTable table;
	private int column;

	public SelectBaselineOrDeptDialog(String type, JTable table, int column) {
		this.type = type;
		this.table = table;
		this.column = column;
		initComponents();
		initLayout();
		loadData();
		initUI();
	}

	public void loadData() {
		selectBaselineOrDeptPanel.loadData();
	}

	private void initComponents() {
		selectBaselineOrDeptPanel = new SelectBaselineOrDeptPanel(this, type, table ,column);
		// buttonPanel = new ButtonPanel(selectBaselineOrDeptPanel, type);
		if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
			buttonPanel = new ButtonPanel(selectBaselineOrDeptPanel, PrintConstants.TITLE_DIALOG_DEPT,null);
		} else if (type.equals(PrintConstants.TITLE_DIALOG_SEAL)) {
			buttonPanel = new ButtonPanel(selectBaselineOrDeptPanel, PrintConstants.TITLE_DIALOG_SEAL,null);
		}else if (type.equals(PrintConstants.TITLE_ALLDIALOG_DEPT)) {
			buttonPanel = new ButtonPanel(selectBaselineOrDeptPanel, PrintConstants.TITLE_ALLDIALOG_DEPT,null);
		} else if (type.equals(PrintConstants.TITLE_ALLDIALOG_SEAL)) {
			buttonPanel = new ButtonPanel(selectBaselineOrDeptPanel, PrintConstants.TITLE_ALLDIALOG_SEAL,null);
		}
		buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 30));
	}

	private void initLayout() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.NORTH, selectBaselineOrDeptPanel, 0, SpringLayout.NORTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, selectBaselineOrDeptPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, selectBaselineOrDeptPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.SOUTH, selectBaselineOrDeptPanel, 5, SpringLayout.NORTH, buttonPanel);

		add(selectBaselineOrDeptPanel);
		add(buttonPanel);
	}

	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(type);
		//setModal(true);
		setResizable(false);
		setSize(420, 300);
        CommonUIUtil.setMiddleOnScreenWithDialog(this);
        setVisible(true);
	}

	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	public SearchBaselineDialog getSearchBaselineDialog() {
		return searchBaselineDialog;
	}

	public void setSearchBaselineDialog(SearchBaselineDialog searchBaselineDialog) {
		this.searchBaselineDialog = searchBaselineDialog;
	}

	public JTable getTable(){
		return table;
	}

	public int getColumn(){
		return column;
	}


}
