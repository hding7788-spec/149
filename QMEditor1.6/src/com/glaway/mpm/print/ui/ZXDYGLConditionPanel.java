package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.listener.SearchFileButtonListener;
import com.glaway.mpm.print.util.FilePrintUtil;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DateChooser;
import com.glaway.mpm.util.DateUtil;
/**
 *
 * @author lkc
 *
 *
 */
public class ZXDYGLConditionPanel extends JPanel {

	private static final long serialVersionUID = -2378036793200299966L;
	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 版本 */
	private JLabel versionLabel;
	/** 工艺类型 */
	private JLabel technicsTypeLabel;
	/** 文件类型 */
	private JLabel fileTypeLabel;
	/** 阶段标记 */
	private JLabel phaseCodeLabel;
	/** 产品代号 */
	private JLabel pindexLabel;
	/** 零部件编号 */
	private JLabel partNumberLabel;
	/** 零件图号 */
	private JLabel cindexLabel;
	/** 打印日期 */
	private JCheckBox printDateCheck;
	/** 打印开始日期 */
	private JLabel printStartDateLabel;
	/** 打印结束日期 */
	private JLabel printEndDateLabel;
	/** 文件编号--文本框 */
	private JTextField fileNumberValue;
	/** 文件名称--文本框 */
	private JTextField fileNameValue;
	/** 版本--文本框 */
	private JTextField versionValue;
	/** 工艺类型--下拉框 */
	private JComboBox technicsTypeValue;
	/** 文件类型--下拉框 */
	private JComboBox fileTypeValue;
	/** 阶段标记--下拉框 */
	private JComboBox phaseCodeValue;
	/** 产品代号--文本框 */
	private JTextField pindexValue;
	/** 零部件编号--文本框 */
	private JTextField partNumberValue;
	/** 零件图号--文本框 */
	private JTextField cindexValue;
	/** 打印开始日期 */
	private JTextField printStartDateValue;
	/** 打印结束日期 */
	private JTextField printEndDateValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;

	private FileListTable fileListTable;

	public JButton getClearConditionButton() {
		return clearConditionButton;
	}

	public JButton getSearchButton() {
		return searchButton;
	}


	public FileListTable getFileListTable() {
		return fileListTable;
	}


	public ZXDYGLConditionPanel(FileListTable fileListTable) {
		this.fileListTable = fileListTable;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}
	private void initComponents() {
		String[] fileType = LoadPrintConfigurations.getInstance().getFileTypeValue();
		String[] phaseCode = LoadPrintConfigurations.getInstance().getPhaseCodeValue();
		Vector<String> technicsType = FilePrintUtil.getAllMPMSkill();
		technicsType.add(0, "");

		fileNumberLabel = new JLabel(PrintConstants.CONDITION_FILENUMBER);
		fileNameLabel = new JLabel(PrintConstants.CONDITION_FILENAME);
		versionLabel = new JLabel(PrintConstants.CONDITION_VERSION);
		technicsTypeLabel = new JLabel(PrintConstants.CONDITION_TECHNICSTYPE);
		fileTypeLabel = new JLabel(PrintConstants.CONDITION_FILETYPE);
		phaseCodeLabel = new JLabel(PrintConstants.CONDITION_PHASECODE);
		pindexLabel = new JLabel(PrintConstants.CONDITION_PINDEX);
		partNumberLabel = new JLabel(PrintConstants.CONDITION_PARTNUMBER);
		cindexLabel = new JLabel(PrintConstants.CONDITION_CINDEX);
		printDateCheck = new JCheckBox(PrintConstants.CONDITION_PRINTDATE);
		printStartDateLabel = new JLabel(PrintConstants.CONDITION_STARTDATE);
		printEndDateLabel = new JLabel(PrintConstants.CONDITION_ENDDATE);

		technicsTypeValue = new JComboBox(technicsType);
		fileTypeValue = new JComboBox(fileType);
		phaseCodeValue = new JComboBox(phaseCode);
		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		versionValue = new JTextField();
		pindexValue = new JTextField();
		partNumberValue = new JTextField();
		cindexValue = new JTextField();
		printStartDateValue = new JTextField("1970-01-01");
		printEndDateValue = new JTextField(DateUtil.getTodayDate(DateUtil.DATE_FORMAT));
		clearConditionButton = new JButton(PrintConstants.CONDITION_CLEARCONDITION);
		searchButton = new JButton(PrintConstants.CONDITION_SEARCH);

        DateChooser printStartChooser = DateChooser.getInstance("yyyy-MM-dd");
        DateChooser printEndChooser = DateChooser.getInstance("yyyy-MM-dd");
        printStartChooser.register(printStartDateValue);
        printEndChooser.register(printEndDateValue);

        fileNumberValue.setPreferredSize(new Dimension(120, 25));
		fileNameValue.setPreferredSize(new Dimension(120, 25));
		versionValue.setPreferredSize(new Dimension(120, 25));
		fileTypeValue.setPreferredSize(new Dimension(120, 25));
		phaseCodeValue.setPreferredSize(new Dimension(120, 25));
		technicsTypeValue.setPreferredSize(new Dimension(120, 25));
		cindexValue.setPreferredSize(new Dimension(120, 25));
		partNumberValue.setPreferredSize(new Dimension(120, 25));
		pindexValue.setPreferredSize(new Dimension(120, 25));
		printStartDateValue.setPreferredSize(new Dimension(120, 25));
		printEndDateValue.setPreferredSize(new Dimension(120, 25));
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
		topGrid.gridy = 1;
		tempPanel.add(fileNameLabel, topGrid);
		topGrid.gridy = 2;
		tempPanel.add(versionLabel, topGrid);
		topGrid.gridy = 3;
		tempPanel.add(printDateCheck, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(fileNumberValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(fileNameValue, topGrid);
		topGrid.gridy = 2;
		tempPanel.add(versionValue, topGrid);
		topGrid.gridy = 3;
		topGrid.gridwidth = 2;
		JPanel printStartDate = new JPanel();
		printStartDate.add(printStartDateLabel);
		printStartDate.add(printStartDateValue);
		tempPanel.add(printStartDate, topGrid);

		topGrid.gridx = 2;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(technicsTypeLabel, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(fileTypeLabel, topGrid);
		topGrid.gridy = 2;
		tempPanel.add(phaseCodeLabel, topGrid);

		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(technicsTypeValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(fileTypeValue, topGrid);
		topGrid.gridy = 2;
		tempPanel.add(phaseCodeValue, topGrid);
		topGrid.gridy = 3;
		topGrid.gridwidth = 2;
		JPanel printEndDate = new JPanel();
		printEndDate.add(printEndDateLabel);
		printEndDate.add(printEndDateValue);
		tempPanel.add(printEndDate, topGrid);

		topGrid.gridx = 4;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(pindexLabel, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(partNumberLabel, topGrid);
		topGrid.gridy = 2;
		tempPanel.add(cindexLabel, topGrid);

		topGrid.gridx = 5;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(pindexValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(partNumberValue, topGrid);
		topGrid.gridy = 2;
		tempPanel.add(cindexValue, topGrid);

		topGrid.gridx = 6;
		topGrid.gridy = 2;
		tempPanel.add(clearConditionButton, topGrid);

		topGrid.gridx = 7;
		topGrid.gridy = 2;
		tempPanel.add(searchButton, topGrid);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(tempPanel);
		setLayout(new VFlowLayout(FlowLayout.LEADING));
		add(topPanel);
	}

	private void initListener() {
//		searchButton.addActionListener(new SearchFileButtonListener(this));
//		clearConditionButton.addActionListener(new SearchFileButtonListener(this));
	}

	private void initUI() {

	}
	public void clearCondition(){
		fileNumberValue.setText("");
		fileNameValue.setText("");
		versionValue.setText("");
		technicsTypeValue.setSelectedIndex(0);
		fileTypeValue.setSelectedIndex(0);
		phaseCodeValue.setSelectedIndex(0);
		pindexValue.setText("");
		partNumberValue.setText("");
		cindexValue.setText("");
	}
	public CmPrintQueryBean getConditionValues(){
		CmPrintQueryBean cmPrintQueryBean = new CmPrintQueryBean();
		cmPrintQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintQueryBean.setVersion(CommonUtil.objectToString(versionValue.getText()));
		cmPrintQueryBean.setTechnicsType(CommonUtil.objectToString(technicsTypeValue.getSelectedItem()));
		cmPrintQueryBean.setFileType(CommonUtil.objectToString(fileTypeValue.getSelectedItem()));
		cmPrintQueryBean.setPhaseCode(CommonUtil.objectToString(phaseCodeValue.getSelectedItem()));
		cmPrintQueryBean.setPindex(CommonUtil.objectToString(pindexValue.getText()));
		cmPrintQueryBean.setPartNumber(CommonUtil.objectToString(partNumberValue.getText()));
		cmPrintQueryBean.setCindex(CommonUtil.objectToString(cindexValue.getText()));

		cmPrintQueryBean.setPrintBeginDate(CommonUtil.objectToString(printStartDateValue.getText()));
		cmPrintQueryBean.setPrintOverDate(CommonUtil.objectToString(printEndDateValue.getText()));
		cmPrintQueryBean.setPrintDateCheck(CommonUtil.objectToString(printDateCheck.isSelected()));

		cmPrintQueryBean.setApplyBeginDate(CommonUtil.objectToString(""));
		cmPrintQueryBean.setApplyOverDate((CommonUtil.objectToString("")));
		cmPrintQueryBean.setApplyDateCheck(CommonUtil.objectToString(""));


		return cmPrintQueryBean;
	}
}
