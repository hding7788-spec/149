package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.listener.SearchFileButtonListener;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.util.CommonUtil;

public class SearchPrintInfoPanel extends JPanel {

	private static final long serialVersionUID = 3607355000282742912L;
	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 文件类型 */
	private JLabel fileTypeLabel;
	/** 文件编号文本*/
	private JTextField fileNumberValue;
	/** 文件名称文本*/
	private JTextField fileNameValue;
	/** 文件类型--下拉框 */
	private JComboBox fileTypeValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;

	private FileListTable fileListTable;

	private JFrame frame;

	public SearchPrintInfoPanel(FileListTable fileListTable, JFrame frame) {
		this.fileListTable = fileListTable;
		this.frame = frame;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}

	private void initComponents() {
		String[] fileType = LoadPrintConfigurations.getInstance().getPrintFileTypeValue();
		fileNumberLabel = new JLabel(PrintConstants.CONDITION_FILENUMBER);
		fileNameLabel = new JLabel(PrintConstants.CONDITION_FILENAME);
		fileTypeLabel = new JLabel(PrintConstants.CONDITION_FILETYPE);

		fileNumberValue = new JTextField();
		fileNumberValue.setPreferredSize(new Dimension(120, 25));
		fileNameValue = new JTextField();
		fileNameValue.setPreferredSize(new Dimension(120, 25));
		fileTypeValue = new JComboBox(fileType);
		fileTypeValue.setPreferredSize(new Dimension(120, 25));

		clearConditionButton = new JButton(PrintConstants.CONDITION_CLEARCONDITION);
		searchButton = new JButton(PrintConstants.CONDITION_SEARCH);
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
		tempPanel.add(fileTypeLabel, topGrid);

		topGrid.gridx = 5;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(fileTypeValue, topGrid);

		topGrid.gridx = 6;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 300, 5, 10);
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(clearConditionButton, topGrid);

		topGrid.gridx = 7;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 10, 5, 10);
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(searchButton, topGrid);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(tempPanel);
		setLayout(new VFlowLayout(FlowLayout.LEADING));
		add(topPanel);
	}

	private void initListener() {
		searchButton.addActionListener(new SearchFileButtonListener(this, frame));
		clearConditionButton.addActionListener(new SearchFileButtonListener(this, frame));
	}

	private void initUI() {

	}


	public void clearCondition(){
		fileNumberValue.setText("");
		fileNameValue.setText("");
		fileTypeValue.setSelectedIndex(0);
	}

	public CmPrintQueryBean getConditionValues(){
		CmPrintQueryBean cmPrintQueryBean = new CmPrintQueryBean();
		cmPrintQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintQueryBean.setFileType(CommonUtil.objectToString(fileTypeValue.getSelectedItem()));
		return cmPrintQueryBean;
	}

	public FileListTable getFileListTable() {
		return fileListTable;
	}

	public JButton getSearchButton(){
		return searchButton;
	}

	public JButton getClearConditionButton(){
		return clearConditionButton;
	}

}
