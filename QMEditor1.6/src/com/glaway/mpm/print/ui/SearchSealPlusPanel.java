package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.listener.SearchFileButtonListener;
import com.glaway.mpm.util.CommonUtil;

public class SearchSealPlusPanel extends JPanel {

	private static final long serialVersionUID = 723440971368408924L;
	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 版本 */
	private JLabel versionLabel;
	/** 文件编号--文本框 */
	private JTextField fileNumberValue;
	/** 文件名称--文本框 */
	private JTextField fileNameValue;
	/** 版本--文本框 */
	private JTextField versionValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;

	private FileListTable fileListTable;

	private FileListTable searchFileTable;

	private JFrame frame;

	public SearchSealPlusPanel(FileListTable fileListTable, JFrame frame, FileListTable searchFileTable) {
		this.fileListTable = fileListTable;
		this.searchFileTable = searchFileTable;
		this.frame = frame;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}

	private void initComponents() {
		Vector<String> mindexList = MPMPrintProcessor.getProductMindex();
		mindexList.add(0, "");
		fileNumberLabel = new JLabel(PrintConstants.CONDITION_FILENUMBER);
		fileNameLabel = new JLabel(PrintConstants.CONDITION_FILENAME);
		versionLabel = new JLabel(PrintConstants.CONDITION_VERSION);
		clearConditionButton = new JButton(PrintConstants.CONDITION_CLEARCONDITION);
		searchButton = new JButton(PrintConstants.CONDITION_SEARCH);
		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		versionValue = new JTextField();

		fileNumberValue.setPreferredSize(new Dimension(120, 25));
		fileNameValue.setPreferredSize(new Dimension(120, 25));
		versionValue.setPreferredSize(new Dimension(120, 25));
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
		tempPanel.add(fileNameLabel, topGrid);

		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(fileNameValue, topGrid);

		topGrid.gridx = 4;
		topGrid.gridy = 0;
		tempPanel.add(versionLabel, topGrid);

		topGrid.gridx = 5;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(versionValue, topGrid);

		topGrid.gridx = 6;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 10, 5, 10);
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
	}

	public CmPrintQueryBean getConditionValues(){
		CmPrintQueryBean cmPrintQueryBean = new CmPrintQueryBean();
		cmPrintQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintQueryBean.setVersion(CommonUtil.objectToString(versionValue.getText()));
//		String type = MPMPrintFileFrame.getType();
//		if(type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)){
//			//查询生命周期状态为已批准的数据
//			cmPrintQueryBean.setLifeCycleState(PrintConstants.LIFECYCLE_EN_APPROVED);
//		}else if(type.equals(PrintConstants.TITLE_MAINPANEL_SELFPRINT)){
//			cmPrintQueryBean.setLifeCycleState("");
//		}
		return cmPrintQueryBean;
	}

	public FileListTable getFileListTable() {
		return fileListTable;
	}
	public FileListTable getSearchFileTable() {
		return searchFileTable;
	}

}
