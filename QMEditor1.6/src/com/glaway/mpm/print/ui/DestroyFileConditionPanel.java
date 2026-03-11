package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.listener.SearchFileButtonListener;
import com.glaway.mpm.util.CommonUtil;

public class DestroyFileConditionPanel extends JPanel {

	private static final long serialVersionUID = 7298500228109594939L;
	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 二维码 */
	private JLabel QRCodeLabel;
	/** 文件编号--文本框 */
	private JTextField fileNumberValue;
	/** 文件名称--文本框 */
	private JTextField fileNameValue;
	/** 二维码--文本框 */
	private JTextField QRCodeValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;

	private FileListTable fileListTable;

	public DestroyFileConditionPanel(FileListTable fileListTable) {
		this.fileListTable = fileListTable;
		initComponents();
		initLayout();
		initListener();
	}

	private void initComponents() {

		fileNumberLabel = new JLabel(PrintConstants.CONDITION_FILENUMBER);
		fileNameLabel = new JLabel(PrintConstants.CONDITION_FILENAME);
		QRCodeLabel = new JLabel(PrintConstants.CONDITION_QRCODE);
		clearConditionButton = new JButton(PrintConstants.CONDITION_CLEARCONDITION);
		searchButton = new JButton(PrintConstants.CONDITION_SEARCH);
		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		QRCodeValue = new JTextField();

		fileNumberValue.setPreferredSize(new Dimension(120, 25));
		fileNameValue.setPreferredSize(new Dimension(120, 25));
		QRCodeValue.setPreferredSize(new Dimension(120, 25));
	}

	private void initLayout() {
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();

		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 10, 5, 0);
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(fileNumberLabel, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(fileNumberValue, topGrid);

		topGrid.gridx = 2;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(fileNameLabel, topGrid);

		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(fileNameValue, topGrid);

		topGrid.gridx = 4;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(QRCodeLabel, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(clearConditionButton, topGrid);

		topGrid.gridx = 5;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(QRCodeValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(searchButton, topGrid);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(tempPanel);
		setLayout(new VFlowLayout(FlowLayout.LEADING));
		add(topPanel);
	}

	public JButton getClearConditionButton() {
		return clearConditionButton;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	private void initListener() {
//		searchButton.addActionListener(new SearchFileButtonListener(this));
//		clearConditionButton.addActionListener(new SearchFileButtonListener(this));
	}

	public FileListTable getFileListTable() {
		return fileListTable;
	}

	public CmPrintQueryBean getConditionValues(){
		CmPrintQueryBean cmPrintQueryBean = new CmPrintQueryBean();
		cmPrintQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintQueryBean.setQrCode(CommonUtil.objectToString(QRCodeValue.getText()));
		cmPrintQueryBean.setUser(MPMPrintFileFrame.getCurrentUser());
		return cmPrintQueryBean;
	}

	public void clearCondition(){
		fileNumberValue.setText("");
		fileNameValue.setText("");
		QRCodeValue.setText("");
	}

}
