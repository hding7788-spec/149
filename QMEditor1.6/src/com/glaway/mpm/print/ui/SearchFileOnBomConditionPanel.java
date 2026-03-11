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

public class SearchFileOnBomConditionPanel extends JPanel {

	private static final long serialVersionUID = 1872595563507526165L;
	/** 零部件编号 */
	private JLabel partNumberLabel;
	/** 零部件名称 */
	private JLabel partNameLabel;
	/** 零部件编号--文本框 */
	private JTextField partNumberValue;
	/** 零部件名称--文本框*/
	private JTextField partNameValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;

	private FileListTable fileListTable;

	private JFrame frame;

	public SearchFileOnBomConditionPanel(FileListTable fileListTable, JFrame frame) {
		this.fileListTable = fileListTable;
		this.frame = frame;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}

	private void initComponents() {
		//String[] technicsType = LoadPrintConfigurations.getInstance().getTechnics_Type_Desc();
		Vector<String> mindexList = MPMPrintProcessor.getProductMindex();
		mindexList.add(0, "");
		//String[] fileType = LoadPrintConfigurations.getInstance().getFileTypeValue();
		//String[] phaseCode = LoadPrintConfigurations.getInstance().getPhaseCodeValue();
		//String[] fileState = LoadPrintConfigurations.getInstance().getFileStateValue();

		partNumberLabel = new JLabel(PrintConstants.CONDITION_PARTNUMBER);
		partNameLabel = new JLabel(PrintConstants.CONDITION_PARTNAME);
		clearConditionButton = new JButton(PrintConstants.CONDITION_CLEARCONDITION);
		searchButton = new JButton(PrintConstants.CONDITION_SEARCH);
		partNumberValue = new JTextField();
		partNameValue = new JTextField();

		partNumberValue.setPreferredSize(new Dimension(120, 25));
		partNameValue.setPreferredSize(new Dimension(120,25));
	}

	private void initLayout() {
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();

		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 10, 5, 0);
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(partNumberLabel, topGrid);
		topGrid.gridx = 1;
		tempPanel.add(partNumberValue, topGrid);

		topGrid.gridx = 2;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(partNameLabel, topGrid);
		topGrid.gridx = 3;
		tempPanel.add(partNameValue, topGrid);

		topGrid.gridx = 4;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(clearConditionButton, topGrid);
		topGrid.gridx = 5;
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
		partNumberValue.setText("");
		partNameValue.setText("");
	}

	public CmPrintQueryBean getConditionValues(){
		CmPrintQueryBean cmPrintQueryBean = new CmPrintQueryBean();
		cmPrintQueryBean.setPartNumber(CommonUtil.objectToString(partNumberValue.getText()));
		cmPrintQueryBean.setPartName(CommonUtil.objectToString(partNameValue.getText()));

		String type = MPMPrintFileFrame.getTypeStr();
		if(type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)){
			//查询生命周期状态为已批准的数据
			cmPrintQueryBean.setLifeCycleState(PrintConstants.LIFECYCLE_EN_APPROVED);
		}

		return cmPrintQueryBean;
	}

	public FileListTable getFileListTable() {
		return fileListTable;
	}

}
