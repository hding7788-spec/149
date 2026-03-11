package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmDistributionBean;
import com.glaway.mpm.print.listener.ScanInputKeyListener;
import com.glaway.mpm.util.CommonUtil;

public class RecipientsInfoPanel extends JPanel {

	private static final long serialVersionUID = 1872595563507526165L;
	/** 领用人 */
	private JLabel GUser;
	/** 领用时间 */
	private JLabel GTime;
	/** 领用部门*/
	private JLabel GDept;
	/** 领用文件 */
	private JLabel GFile;
	/** 领用人文本*/
	private JTextField GUserValue;
	/** 领用时间文本*/
	private JTextField GTimeValue;
	/** 领用部门文本*/
	private JTextField GDeptValue;
	/** 领用文件文本*/
	private JTextField GFileValue;

	private FileListTable fileListTable;

	public RecipientsInfoPanel(FileListTable fileListTable) {
		this.fileListTable = fileListTable;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}

	private void initComponents() {

		GUser = new JLabel(PrintConstants.PAPER_GUSER);
		GTime = new JLabel(PrintConstants.PAPER_GTIME);
		GDept = new JLabel(PrintConstants.PAPER_GDEPT);
		GFile = new JLabel(PrintConstants.PAPER_GFILE);

		GUserValue = new JTextField();
		GUserValue.setPreferredSize(new Dimension(120, 25));
		GUserValue.setEditable(false);
		GTimeValue = new JTextField();
		GTimeValue.setPreferredSize(new Dimension(120, 25));
		GTimeValue.setEditable(false);
		GDeptValue = new JTextField();
		GDeptValue.setPreferredSize(new Dimension(120, 25));
		GDeptValue.setEditable(false);
		GFileValue = new JTextField();
		GFileValue.setPreferredSize(new Dimension(120, 25));
	}

	private void initLayout() {
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();

		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 10, 5, 0);
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(GUser, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(GTime, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(GUserValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(GTimeValue, topGrid);

		topGrid.gridx = 2;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(GDept, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(GFile, topGrid);

		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(GDeptValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(GFileValue, topGrid);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(tempPanel);
		setLayout(new VFlowLayout(FlowLayout.LEADING));
		add(topPanel);
	}

	private void initListener() {
//		searchButton.addActionListener(new SearchFileButtonListener(this));
//		clearConditionButton.addActionListener(new SearchFileButtonListener(this));
		GUserValue.addKeyListener(new ScanInputKeyListener());
		GTimeValue.addKeyListener(new ScanInputKeyListener());
		GDeptValue.addKeyListener(new ScanInputKeyListener());
		GFileValue.addKeyListener(new ScanInputKeyListener());
	}

	private void initUI() {

	}


	public void clearCondition(){

	}

	public void setValue(CmDistributionBean cmDistributionBean){
		if(cmDistributionBean == null){
			GUserValue.setText("");
			GTimeValue.setText("");
			GDeptValue.setText("");
			GFileValue.setText("");
		}else{
			GUserValue.setText(cmDistributionBean.getReceiptor());
			GTimeValue.setText(cmDistributionBean.getReceiveTime());
			GDeptValue.setText(cmDistributionBean.getReceiveDept());
			GFileValue.setText(cmDistributionBean.getReceiveFile());
		}
	}

	public CmDistributionBean getConditionValues(){
		CmDistributionBean cmDistributionBean = new CmDistributionBean();
		cmDistributionBean.setReceiptor(CommonUtil.objectToString(GUserValue.getText()));
		cmDistributionBean.setReceiveDept(CommonUtil.objectToString(GDeptValue.getText()));
		cmDistributionBean.setReceiveTime(CommonUtil.objectToString(GTimeValue.getText()));
		cmDistributionBean.setReceiveFile(CommonUtil.objectToString(GFileValue.getText()));
		return cmDistributionBean;
	}

	public FileListTable getFileListTable() {
		return fileListTable;
	}

}
