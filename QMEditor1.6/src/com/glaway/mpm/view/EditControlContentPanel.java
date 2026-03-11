package com.glaway.mpm.view;

import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class EditControlContentPanel extends JDialog {
	private SpecialWordPanel editBigCharPanel = null;
	private NewTechnicsPart frame;
	private String kznrFlag;
	private String type;
	private int row;
	private JPanel mainPanel;
	private JPanel buttonPanel;
	private JButton saveButton;
	private JButton closeButton;
	private DefaultTableModel tableModel;
	public EditControlContentPanel(String kznrFlag, NewTechnicsPart frame, String type, int row, DefaultTableModel tableModel) {
		super(frame, true);
		this.frame = frame;
		this.type = type;
		this.kznrFlag = kznrFlag;
		this.row = row;
		this.tableModel = tableModel;
		initComponents();
		initLayout();
		initListener();
		setEditBigCharPanelValue();
		initUI();
		setEnabled();
	}

	private void initComponents() {
		editBigCharPanel = new SpecialWordPanel(frame, true, null);
		saveButton = new JButton("保存");
		closeButton = new JButton("关闭");
		mainPanel = new JPanel();
		buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 35));

	}

	private void initLayout() {
		buttonPanel.add(saveButton);
		buttonPanel.add(closeButton);

		SpringLayout springLayout = new SpringLayout();
		mainPanel.setLayout(springLayout);
		springLayout.putConstraint(SpringLayout.NORTH, editBigCharPanel, 10, SpringLayout.NORTH,mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, editBigCharPanel, 5, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, editBigCharPanel, -5, SpringLayout.EAST, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, editBigCharPanel, -45, SpringLayout.SOUTH, mainPanel);

		springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 5, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, -5, SpringLayout.SOUTH, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, buttonPanel, -10, SpringLayout.EAST, mainPanel);

		mainPanel.add(editBigCharPanel);
		mainPanel.add(buttonPanel);

		SpringLayout springLayout1 = new SpringLayout();
		setLayout(springLayout1);

		springLayout1.putConstraint(SpringLayout.NORTH, mainPanel, 0, SpringLayout.NORTH, this.getContentPane());
		springLayout1.putConstraint(SpringLayout.WEST, mainPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout1.putConstraint(SpringLayout.EAST, mainPanel, 0, SpringLayout.EAST, this.getContentPane());
		springLayout1.putConstraint(SpringLayout.SOUTH, mainPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		add(mainPanel);
	}
	private void initListener() {
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = e.getActionCommand();
				if(str == "关闭"){
				   int result = JOptionPane.showConfirmDialog(getContentPane(),"是否关闭当前页面？","提示",JOptionPane.YES_NO_OPTION);
				   if(result == JOptionPane.YES_OPTION){
					   dispose();
				   }
				}
			}
		});
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String str = e.getActionCommand();
				if(str == "保存"){
				   	int result = JOptionPane.showConfirmDialog(getContentPane(),"是否保存当前页面？","提示",JOptionPane.YES_NO_OPTION);
				   	if(result == JOptionPane.YES_OPTION){
					   	String s = editBigCharPanel.getText();
						tableModel.setValueAt(s, row,9);
						dispose();
				   }
				}
			}
		});
	}

	private void initUI() {
		this.setTitle(type);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.7), (int)(height*0.6));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void setEnabled() {

	}

	private void setEditBigCharPanelValue() {
		editBigCharPanel.setText(kznrFlag);
		// 特殊符号
		XWTreeNode xwTreeNode = frame.xwPartTreePanel.getSelectedTreeNode();
		Element techEle = xwTreeNode.getObject().getTreeCellData();
		String imageFolder = WorkSpaceUtil.getTechnicsPath(techEle);
//		String technicsPath = speCharPanel.getTechnicsPath();
		editBigCharPanel.setTechnicsPath(imageFolder);
	}

}
