package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;

public class AddOutFileDialog extends JFrame {

	private static final long serialVersionUID = 4094018302868107771L;
	private OutFileConditionPanel outFileConditionPanel;
	private FileListTable parentTablePanel;
	private ButtonPanel buttonPanel;
	private CmPrintInfoBean cmPrintInfoBean;
	private String category;

	public AddOutFileDialog(FileListTable parentTablePanel, CmPrintInfoBean cmPrintInfoBean, String category) {
		this.parentTablePanel = parentTablePanel;
		this.cmPrintInfoBean = cmPrintInfoBean;
		this.category = category;
		initComponents();
		initLayout();
		initUI();
	}

	private void initComponents() {
		outFileConditionPanel = new OutFileConditionPanel(this,cmPrintInfoBean,category);
		buttonPanel = new ButtonPanel(outFileConditionPanel, PrintConstants.TITLE_MAINPANEL_WLWJTJ,null);
		buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 35));
	}

	private void initLayout() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.NORTH, outFileConditionPanel, 0, SpringLayout.NORTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, outFileConditionPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, outFileConditionPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, outFileConditionPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, outFileConditionPanel, 0, SpringLayout.EAST, this.getContentPane());

		springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonPanel, 0, SpringLayout.EAST, this.getContentPane());

//		springLayout.putConstraint(SpringLayout.NORTH, outFileConditionPanel, 5, SpringLayout.SOUTH, searchFileConditionPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, outFileConditionPanel, 0, SpringLayout.NORTH, buttonPanel);

//		add(searchFileConditionPanel);
		add(outFileConditionPanel);
		add(buttonPanel);
	}

	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if(cmPrintInfoBean != null){
			setTitle(PrintConstants.TITLE_MODIFYOUTFILE);
			if("ZZ".equals(category)){
				setTitle(PrintConstants.TITLE_MODIFYPAPERFILE);
			}
		}else {
			setTitle(PrintConstants.TITLE_ADDOUTFILE);
			if("ZZ".equals(category)){
				setTitle(PrintConstants.TITLE_ADDPAPERFILE);
			}
		}
    	setResizable(true);
    	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		//设置全屏
		setSize((int) (width/2.2), (int) (height/2.2));
        setVisible(true);
	}

	public FileListTable getParentTablePanel() {
		return parentTablePanel;
	}

}
