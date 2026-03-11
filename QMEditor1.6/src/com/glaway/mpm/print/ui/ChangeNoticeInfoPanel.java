package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DateChooser;

public class ChangeNoticeInfoPanel extends JPanel {

	private static final long serialVersionUID = 1872595563507526165L;
	/** 更改单信息 */
	private JLabel changeNoticeInfo;
	/** 外来单位*/
//	private JLabel outDeptLabel;
	/** 更改单编号 */
	private JLabel changeNoticeNumberLabel;
	/** 更改日期 */
	private JLabel changeDateLabel;
	/** 更改内容 */
	private JLabel changeContentLabel;
	/** 外来单位--下拉框 */
//	private JComboBox outDeptValue;
	/** 更改单编号--文本框 */
	private JTextField changeNoticeNumberValue;
	/** 更改日期--文本框 */
	private JTextField changeDateValue;
	/** 更改内容--文本域 */
	private JTextArea changeContentValue;

	private JScrollPane scroll;

	private JCheckBox keyBox = new JCheckBox();

	private AddChangeNoticeDialog addChangeNoticeDialog;

	private CmPrintInfoBean cmPrintInfoBean;

	private boolean isModify = false;

	private String category;

	public ChangeNoticeInfoPanel(AddChangeNoticeDialog addChangeNoticeDialog, CmPrintInfoBean cmPrintInfoBean, String category) {
		this.addChangeNoticeDialog = addChangeNoticeDialog;
		this.cmPrintInfoBean = cmPrintInfoBean;
		this.category = category;
		initComponents();
		initLayout();
		initListener();
		initUI();
		if(cmPrintInfoBean != null){
			setValues(cmPrintInfoBean);
			isModify = true;
		}
	}

	private void initComponents() {
//		String[] outDept = LoadPrintConfigurations.getInstance().getOutDeptValue();
		changeNoticeInfo = new JLabel(PrintConstants.TITLE_CHANGENOTICINFO);

//		if(!"ZZ".equals(category)){
//			outDeptLabel = new JLabel(PrintConstants.CONDITION_OUTDEPT);
//			outDeptValue = new JComboBox(outDept);
//			outDeptValue.setPreferredSize(new Dimension(120, 25));
//		}
		changeNoticeNumberLabel = new JLabel(PrintConstants.CONDITION_FILENUMBER);
		changeDateLabel = new JLabel(PrintConstants.CONDITION_CHANGEDATA);
		changeContentLabel = new JLabel(PrintConstants.CONDITION_CHANGECONTENT);

		changeNoticeNumberValue = new JTextField();
		changeDateValue = new JTextField();
		DateChooser changeDate = DateChooser.getInstance("yyyy/MM/dd");
		changeDate.register(changeDateValue);
		changeContentValue = new JTextArea(5,25);

		scroll = new JScrollPane(changeContentValue);

		keyBox.setText("成套图更改");

		changeNoticeNumberValue.setPreferredSize(new Dimension(120, 25));
		changeDateValue.setPreferredSize(new Dimension(120, 25));
		changeContentValue.setLineWrap(true);

	}

	private void initLayout() {
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();

		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 10, 5, 0);
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(changeNoticeInfo, topGrid);
		topGrid.gridy = 2;
		tempPanel.add(changeNoticeNumberLabel, topGrid);
		topGrid.gridy = 3;
		tempPanel.add(changeContentLabel, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 1;
		tempPanel.add(keyBox, topGrid);
		topGrid.anchor = GridBagConstraints.WEST;
		topGrid.gridy = 2;
		tempPanel.add(changeNoticeNumberValue, topGrid);
		topGrid.gridy = 3;
		tempPanel.add(scroll, topGrid);

		topGrid.gridx = 2;
		topGrid.gridy = 2;
		topGrid.anchor = GridBagConstraints.EAST;
		tempPanel.add(changeDateLabel, topGrid);

		topGrid.gridx = 3;
		topGrid.gridy = 2;
		topGrid.anchor = GridBagConstraints.WEST;
		tempPanel.add(changeDateValue, topGrid);

		scroll.setViewportView(changeContentValue);
		scroll.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(tempPanel);
		setLayout(new VFlowLayout(FlowLayout.LEADING));
		add(topPanel);
	}

	private void initListener() {

	}

	private void initUI() {

	}

	public void clearCondition(){

	}

	public CmPrintQueryBean getConditionValues(){
		CmPrintQueryBean cmPrintQueryBean = new CmPrintQueryBean();
		cmPrintQueryBean.setFileNumber(CommonUtil.objectToString(changeNoticeNumberValue.getText()));
		cmPrintQueryBean.setChangeData(CommonUtil.objectToString(changeDateValue.getText()));
		cmPrintQueryBean.setChangeContent(CommonUtil.objectToString(changeContentValue.getText()));
		cmPrintQueryBean.setViewChange(CommonUtil.objectToString(keyBox.isSelected()));
		return cmPrintQueryBean;
	}

	public AddChangeNoticeDialog getAddChangeNoticeDialog() {
		return addChangeNoticeDialog;
	}

	public void setValues(CmPrintInfoBean cmPrintInfoBean){
		changeNoticeNumberValue.setText(cmPrintInfoBean.getChangeNoticeNumber());
		changeDateValue.setText(cmPrintInfoBean.getChangeDate());
		changeContentValue.setText(cmPrintInfoBean.getChangeContent());
		keyBox.setSelected(Boolean.parseBoolean(cmPrintInfoBean.getViewChange()));
	}

	public boolean isModify(){
		return isModify;
	}

	public CmPrintInfoBean getCmPrintInfoBean(){
		return cmPrintInfoBean;
	}

	public String getCategory(){
		return category;
	}
}
