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

public class SearchOutFilePanel extends JPanel {

	private static final long serialVersionUID = 1872595563507526165L;
	/** 外来单位 */
	private JLabel outDeptLabel;
	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称*/
	private JLabel fileNameLabel;
	/** 版本 */
	private JLabel versionLabel;
	/** 阶段标记 */
	private JLabel phaseCodeLabel;
	/** 文件类型 */
	private JLabel fileTypeLabel;
	/** 外来单位文本*/
	private JComboBox outDeptValue;
	/** 文件编号文本*/
	private JTextField fileNumberValue;
	/** 文件名称文本*/
	private JTextField fileNameValue;
	/** 版本文本*/
	private JTextField versionValue;
	/** 阶段标记文本*/
	private JComboBox phaseCodeValue;
	/** 文件类型文本*/
	private JComboBox fileTypeValue;
	/** 查询 */
	private JButton searchButton;
	/** 清空 */
	private JButton clearConditionButton;

	private FileListTable fileListTable;

	private String category;

	private JFrame frame;

	public SearchOutFilePanel(FileListTable fileListTable, JFrame frame, String category) {
		this.fileListTable = fileListTable;
		this.frame = frame;
		this.category = category;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}

	private void initComponents() {
		String[] outDept = LoadPrintConfigurations.getInstance().getOutDeptValue();
		String[] fileType = LoadPrintConfigurations.getInstance().getPrintFileTypeValue();
		String[] phaseCode = LoadPrintConfigurations.getInstance().getPhaseCodeValue();

		if(!"ZZ".equals(category)){
			outDeptLabel = new JLabel(PrintConstants.CONDITION_OUTDEPT);
			outDeptValue = new JComboBox(outDept);
			outDeptValue.setPreferredSize(new Dimension(120, 25));
		}
		fileNumberLabel = new JLabel(PrintConstants.CONDITION_FILENUMBER);
		fileNameLabel = new JLabel(PrintConstants.CONDITION_FILENAME);
		versionLabel = new JLabel(PrintConstants.CONDITION_VERSION);
		phaseCodeLabel = new JLabel(PrintConstants.CONDITION_PHASECODE);
		fileTypeLabel = new JLabel(PrintConstants.CONDITION_FILETYPE);

		fileNumberValue = new JTextField();
		fileNumberValue.setPreferredSize(new Dimension(120, 25));
		fileNameValue = new JTextField();
		fileNameValue.setPreferredSize(new Dimension(120, 25));
		versionValue = new JTextField();
		versionValue.setPreferredSize(new Dimension(120, 25));
		phaseCodeValue = new JComboBox(phaseCode);
		phaseCodeValue.setPreferredSize(new Dimension(120, 25));
		fileTypeValue = new JComboBox(fileType);
		fileTypeValue.setPreferredSize(new Dimension(120, 25));
		clearConditionButton = new JButton(PrintConstants.CONDITION_CLEARCONDITION);
		searchButton = new JButton(PrintConstants.CONDITION_SEARCH);
	}

	private void initLayout() {
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();

		if(!"ZZ".equals(category)){
			topGrid.gridx = 0;
			topGrid.gridy = 0;
			topGrid.insets = new Insets(5, 10, 5, 0);
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(outDeptLabel, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(versionLabel, topGrid);

			topGrid.gridx = 1;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(outDeptValue, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(versionValue, topGrid);

			topGrid.gridx = 2;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(fileNumberLabel, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(phaseCodeLabel, topGrid);

			topGrid.gridx = 3;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(fileNumberValue, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(phaseCodeValue, topGrid);

			topGrid.gridx = 4;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(fileNameLabel, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(fileTypeLabel, topGrid);

			topGrid.gridx = 5;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(fileNameValue, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(fileTypeValue, topGrid);

			topGrid.gridx = 6;
			topGrid.gridy = 2;
			topGrid.insets = new Insets(5, 10, 5, 10);
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(clearConditionButton, topGrid);

			topGrid.gridx = 7;
			topGrid.gridy = 2;
			topGrid.insets = new Insets(5, 10, 5, 10);
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(searchButton, topGrid);
		}else{
			topGrid.gridx = 0;
			topGrid.gridy = 0;
			topGrid.insets = new Insets(5, 10, 5, 0);
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(fileNumberLabel, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(phaseCodeLabel, topGrid);

			topGrid.gridx = 1;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(fileNumberValue, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(phaseCodeValue, topGrid);

			topGrid.gridx = 2;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(fileNameLabel, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(fileTypeLabel, topGrid);

			topGrid.gridx = 3;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(fileNameValue, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(fileTypeValue, topGrid);

			topGrid.gridx = 4;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(versionLabel, topGrid);

			topGrid.gridx = 5;
			topGrid.gridy = 0;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(versionValue, topGrid);

			topGrid.gridx = 6;
			topGrid.gridy = 2;
			topGrid.insets = new Insets(5, 10, 5, 10);
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(clearConditionButton, topGrid);

			topGrid.gridx = 7;
			topGrid.gridy = 2;
			topGrid.insets = new Insets(5, 10, 5, 10);
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(searchButton, topGrid);
		}



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
		versionValue.setText("");
		phaseCodeValue.setSelectedIndex(0);
		fileTypeValue.setSelectedIndex(0);
	}

	public CmPrintQueryBean getConditionValues(){
		CmPrintQueryBean cmPrintQueryBean = new CmPrintQueryBean();
		if(!"ZZ".equals(category)){
			cmPrintQueryBean.setOutDept(CommonUtil.objectToString(outDeptValue.getSelectedItem()));
		}
		cmPrintQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintQueryBean.setVersion(CommonUtil.objectToString(versionValue.getText()));
		cmPrintQueryBean.setPhaseCode(CommonUtil.objectToString(phaseCodeValue.getSelectedItem()));
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
