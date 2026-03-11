package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.listener.SearchFileButtonListener;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class SearchFileConditionPanel extends JPanel {

	private static final long serialVersionUID = 1872595563507526165L;
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
	/** 文件状态 */
	private JLabel fileStateLabel;
	/** 修改者 */
	private JLabel modifiorLabel;
	/** 型号 */
	private JLabel mindexLabel;
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
	/** 文件状态 --下拉框 */
	private JComboBox fileStateValue;
	/** 修改者--文本框*/
	private JTextField modifiorValue;
	/** 型号查询 --下拉框 */
	private JComboBox mindexValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;

	private FileListTable fileListTable;

	private JFrame frame;

	public SearchFileConditionPanel(FileListTable fileListTable, JFrame frame) {
		this.fileListTable = fileListTable;
		this.frame = frame;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}

	private void initComponents() {
//		Vector<String> technicsType = MPMPrintProcessor.getAllMPMSkill();
//		technicsType.add(0, "");
		String[] technicsType = LoadPrintConfigurations.getInstance().getTechnics_Type_Desc();
		Vector<String> mindexList = MPMPrintProcessor.getProductMindex();
		mindexList.add(0, "");
		String[] fileType = LoadPrintConfigurations.getInstance().getFileTypeValue();
		String[] phaseCode = LoadPrintConfigurations.getInstance().getPhaseCodeValue();
		String[] fileState = LoadPrintConfigurations.getInstance().getFileStateValue();

		fileNumberLabel = new JLabel(PrintConstants.CONDITION_FILENUMBER);
		fileNameLabel = new JLabel(PrintConstants.CONDITION_FILENAME);
		versionLabel = new JLabel(PrintConstants.CONDITION_VERSION);
		technicsTypeLabel = new JLabel(PrintConstants.CONDITION_TECHNICSTYPE);
		fileTypeLabel = new JLabel(PrintConstants.CONDITION_FILETYPE);
		phaseCodeLabel = new JLabel(PrintConstants.CONDITION_PHASECODE);
		pindexLabel = new JLabel(PrintConstants.CONDITION_PINDEX);
		partNumberLabel = new JLabel(PrintConstants.CONDITION_PARTNUMBER);
		cindexLabel = new JLabel(PrintConstants.CONDITION_CINDEX);
		fileStateLabel = new JLabel(PrintConstants.CONDITION_FILESTATE);
		modifiorLabel = new JLabel(PrintConstants.CONDITION_MODIFIOR);
		mindexLabel = new JLabel(PrintConstants.CONDITION_MINDEX);
		clearConditionButton = new JButton(PrintConstants.CONDITION_CLEARCONDITION);
		searchButton = new JButton(PrintConstants.CONDITION_SEARCH);
		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		versionValue = new JTextField();
		pindexValue = new JTextField();
		partNumberValue = new JTextField();
		cindexValue = new JTextField();
		modifiorValue = new JTextField();
		fileStateValue = new JComboBox(fileState);
		technicsTypeValue = new JComboBox(technicsType);
		fileTypeValue = new JComboBox(fileType);
		phaseCodeValue = new JComboBox(phaseCode);
		mindexValue = new JComboBox(mindexList);

		fileNumberValue.setPreferredSize(new Dimension(120, 25));
		fileNameValue.setPreferredSize(new Dimension(120, 25));
		versionValue.setPreferredSize(new Dimension(120, 25));
		technicsTypeValue.setPreferredSize(new Dimension(120, 25));
		fileTypeValue.setPreferredSize(new Dimension(120, 25));
		phaseCodeValue.setPreferredSize(new Dimension(120, 25));
		pindexValue.setPreferredSize(new Dimension(120, 25));
		partNumberValue.setPreferredSize(new Dimension(120, 25));
		cindexValue.setPreferredSize(new Dimension(120, 25));
		fileStateValue.setPreferredSize(new Dimension(120, 25));
		modifiorValue.setPreferredSize(new Dimension(120, 25));
		mindexValue.setPreferredSize(new Dimension(120, 25));
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
		tempPanel.add(versionLabel, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(fileNumberValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(versionValue, topGrid);

		topGrid.gridx = 2;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(fileNameLabel, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(phaseCodeLabel, topGrid);

		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(fileNameValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(phaseCodeValue, topGrid);

		topGrid.gridx = 4;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(fileTypeLabel, topGrid);

		topGrid.gridx = 5;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
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

	public JButton getClearConditionButton() {
		return clearConditionButton;
	}

	public JButton getSearchButton() {
		return searchButton;
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
		fileStateValue.setSelectedIndex(0);
		modifiorValue.setText("");
		mindexValue.setSelectedIndex(0);
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
		cmPrintQueryBean.setFileState(CommonUtil.objectToString(fileStateValue.getSelectedItem()));
		cmPrintQueryBean.setModifior(CommonUtil.objectToString(modifiorValue.getText()));
		cmPrintQueryBean.setMindex(CommonUtil.objectToString(mindexValue.getSelectedItem()));

		String type = MPMPrintFileFrame.getTypeStr();
		if(type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)){
			//查询生命周期状态为已批准的数据
			cmPrintQueryBean.setLifeCycleState(PrintConstants.LIFECYCLE_EN_APPROVED);
		}

		cmPrintQueryBean.setFileState(CommonUtil.objectToString(fileStateValue.getSelectedItem()));

		return cmPrintQueryBean;
	}

	public FileListTable getFileListTable() {
		return fileListTable;
	}

}
