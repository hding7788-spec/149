package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.listener.SearchBaselineListener;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.print.constants.SearchConstants;
import com.glaway.mpm.util.CommonUtil;

/**
 * 搜索工艺规程条件查询面板
 *
 * @author wxl
 *
 */
public class SearchBaselineConditionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	/** 基线名称 */
	private JLabel bsNameLabel;
	/** 基线编号 */
	private JLabel bsNumberLabel;
	/** 产品代号 */
	private JLabel pindexLabel;
	/** 阶段标记 */
	private JLabel phaseCodeLabel;
	/** 技术状态标识 */
	private JLabel statusLabel;
	/** 基线名称--文本框 */
	private JTextField bsNameValue;
	/** 基线编号--文本框 */
	private JTextField bsNumberValue;
	/** 产品代号--文本框 */
	private JTextField pindexValue;
	/** 阶段标记--下拉框 */
	private JComboBox phaseCodeValue;
	/** 技术状态标识--文本框 */
	private JTextField statusValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 搜索 */
	private JButton searchButton;
	private SearchBaselinePanel parent;

	public SearchBaselineConditionPanel(SearchBaselinePanel parent) {
		this.parent = parent;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}

	private void initComponents() {
		String[] phaseCodeList = LoadPrintConfigurations.getInstance().getPhaseCodeValue();

		bsNameLabel = new JLabel("基线名称：");
		bsNumberLabel = new JLabel("基线编号：");
		pindexLabel = new JLabel("产品代号：");
		phaseCodeLabel = new JLabel("阶段标记：");
		statusLabel = new JLabel("技术状态标识：");

		bsNameValue = new JTextField();
		bsNumberValue = new JTextField();
		pindexValue = new JTextField();
		phaseCodeValue = new JComboBox(phaseCodeList);
		statusValue = new JTextField();
		clearConditionButton = new JButton(SearchConstants.SEARCH_BUTTON_CLEAR);
		searchButton = new JButton(SearchConstants.SEARCH_BUTTON_SEARCH);

		int maxLength = 150;
		int maxHeight = 20;
		bsNameValue.setPreferredSize(new Dimension(maxLength, maxHeight));
		bsNumberValue.setPreferredSize(new Dimension(maxLength, maxHeight));
		pindexValue.setPreferredSize(new Dimension(maxLength, maxHeight));
		phaseCodeValue.setPreferredSize(new Dimension(maxLength, maxHeight));
		statusValue.setPreferredSize(new Dimension(maxLength, maxHeight));
	}

	private void initLayout() {
		//上半部分查询条件及按钮
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();

		//第一列
		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 10, 5, 0);
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(bsNumberLabel, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(phaseCodeLabel, topGrid);

		//第二列
		topGrid.gridx = 1;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(bsNumberValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(phaseCodeValue, topGrid);

		//第三列
		topGrid.gridx = 2;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(bsNameLabel, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(statusLabel, topGrid);


		//第四列
		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(bsNameValue, topGrid);
		topGrid.gridy = 1;
		tempPanel.add(statusValue, topGrid);


		//第五列
		topGrid.gridx = 4;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(pindexLabel, topGrid);
		topGrid.gridy = 1;
		topGrid.insets = new Insets(15, 10, 5, 0);
		tempPanel.add(clearConditionButton, topGrid);

		//第六列
		topGrid.gridx = 5;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 10, 5, 0);
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(pindexValue, topGrid);
		topGrid.gridy = 1;
		topGrid.insets = new Insets(15, 35, 5, 0);
		tempPanel.add(searchButton, topGrid);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(tempPanel);
		setLayout(new VFlowLayout(FlowLayout.LEADING));
		add(topPanel);
	}

	private void initListener() {
		clearConditionButton.addActionListener(new SearchBaselineListener(this));
		searchButton.addActionListener(new SearchBaselineListener(this));
	}

	private void initUI() {
		setBorder(BorderFactory.createTitledBorder("查询条件"));
	}

	public JButton getClearConditionButton() {
		return clearConditionButton;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public SearchBaselinePanel getParent() {
		return parent;
	}

	public void clearCondition() {
		bsNumberValue.setText("");
		bsNameValue.setText("");
		pindexValue.setText("");
		statusValue.setText("");
		phaseCodeValue.setSelectedIndex(0);
	}

	public CmPrintQueryBean getFilledInfo() {
		CmPrintQueryBean printQueryBean = new CmPrintQueryBean();

		String number = CommonUtil.objectToString(bsNumberValue.getText());
		String name = CommonUtil.objectToString(bsNameValue.getText());
		String pindex = CommonUtil.objectToString(pindexValue.getText());
		String status = CommonUtil.objectToString(statusValue.getText());
		String phaseCode = CommonUtil.objectToString(phaseCodeValue.getSelectedItem());

		printQueryBean.setFileNumber(number);
		printQueryBean.setFileName(name);
		printQueryBean.setPindex(pindex);
		printQueryBean.setTs_status(status);
		printQueryBean.setPhaseCode(phaseCode);

		return printQueryBean;
	}
}
