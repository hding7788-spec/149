package com.glaway.mpm.print.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.util.CommonUtil;

public class AfterChangeInfoPanel extends JPanel {

	private static final long serialVersionUID = 1872595563507526165L;
	/** 更改后文件信息 */
	private JLabel afterChangeInfo;
	/** 外来单位*/
	private JLabel outDeptLabel;
	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 版本 */
	private JLabel versionLabel;
	/** 文件类型 */
	private JLabel fileTypeLabel;
	/** 阶段标记 */
	private JLabel phaseCodeLabel;
	/** 密级 */
	private JLabel secretLabel;
	/** 页数*/
	private JLabel pageCountLabel;
	/** 所属产品库*/
	private JLabel containerLabel;
	/** 外来单位--下拉框 */
	private JComboBox outDeptValue;
	/** 文件编号--文本框 */
	private JTextField fileNumberValue;
	/** 文件名称--文本框 */
	private JTextField fileNameValue;
	/** 版本--文本框 */
	private JTextField versionValue;
	/** 文件类型--下拉框 */
	private JComboBox fileTypeValue;
	/** 阶段标记--下拉框 */
	private JComboBox phaseCodeValue;
	/** 密级--文本框*/
	private JTextField secretValue;
	/** 页数--文本框 */
	private JTextField pageCountValue;
	/** 所属产品库--下拉框*/
	private JComboBox containerValue;

	private AddChangeNoticeDialog addChangeNoticeDialog;

	private CmPrintInfoBean cmPrintInfoBean;

	private boolean isModify = false;

	private String category;

	public AfterChangeInfoPanel(AddChangeNoticeDialog addChangeNoticeDialog, CmPrintInfoBean cmPrintInfoBean, String category) {
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
		String[] fileType = LoadPrintConfigurations.getInstance().getPrintFileTypeValue();
		String[] phaseCode = LoadPrintConfigurations.getInstance().getPhaseCodeValue();
		String[] outDept = LoadPrintConfigurations.getInstance().getOutDeptValue();
		String[] container = LoadPrintConfigurations.getInstance().getContainerValue();
		afterChangeInfo = new JLabel(PrintConstants.TITLE_AFTERCHANGEINFO);

		if(!"ZZ".equals(category)){
			outDeptLabel = new JLabel(PrintConstants.CONDITION_OUTDEPT);
			outDeptValue = new JComboBox(outDept);
			outDeptValue.setPreferredSize(new Dimension(120, 25));
		}
		fileNumberLabel = new JLabel(PrintConstants.CONDITION_FILENUMBER);
		fileNameLabel = new JLabel(PrintConstants.CONDITION_FILENAME);
		versionLabel = new JLabel(PrintConstants.CONDITION_VERSION);
		fileTypeLabel = new JLabel(PrintConstants.CONDITION_FILETYPE);
		phaseCodeLabel = new JLabel(PrintConstants.CONDITION_PHASECODE);
		secretLabel = new JLabel(PrintConstants.CONDITION_SECRET);
		pageCountLabel = new JLabel(PrintConstants.CONDITION_PAGECOUNT);
		containerLabel = new JLabel(PrintConstants.CONDITION_CONTAINER);

		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		versionValue = new JTextField();
		fileTypeValue = new JComboBox(fileType);
		phaseCodeValue = new JComboBox(phaseCode);
		secretValue = new JTextField();
		pageCountValue = new JTextField();
		containerValue =new JComboBox(container);

		fileNumberValue.setPreferredSize(new Dimension(120, 25));
		fileNameValue.setPreferredSize(new Dimension(120, 25));
		versionValue.setPreferredSize(new Dimension(120, 25));
		fileTypeValue.setPreferredSize(new Dimension(120, 25));
		phaseCodeValue.setPreferredSize(new Dimension(120, 25));
		secretValue.setPreferredSize(new Dimension(120, 25));
		pageCountValue.setPreferredSize(new Dimension(120, 25));
		containerValue.setPreferredSize(new Dimension(120, 25));
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
			tempPanel.add(afterChangeInfo, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(outDeptLabel, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(versionLabel, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(pageCountLabel, topGrid);

			topGrid.gridx = 1;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(outDeptValue, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(versionValue, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(pageCountValue, topGrid);

			topGrid.gridx = 2;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(fileNumberLabel, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(phaseCodeLabel, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(secretLabel, topGrid);

			topGrid.gridx = 3;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(fileNumberValue, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(phaseCodeValue, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(secretValue, topGrid);

			topGrid.gridx = 4;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(fileNameLabel, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(fileTypeLabel, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(containerLabel, topGrid);

			topGrid.gridx = 5;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(fileNameValue, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(fileTypeValue, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(containerValue, topGrid);
		}else{
			topGrid.gridx = 0;
			topGrid.gridy = 0;
			topGrid.insets = new Insets(5, 10, 5, 0);
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(afterChangeInfo, topGrid);
			topGrid.gridy = 1;
			tempPanel.add(fileNumberLabel, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(phaseCodeLabel, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(secretLabel, topGrid);

			topGrid.gridx = 1;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(fileNumberValue, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(phaseCodeValue, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(secretValue, topGrid);

			topGrid.gridx = 2;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(fileNameLabel, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(fileTypeLabel, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(containerLabel, topGrid);

			topGrid.gridx = 3;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(fileNameValue, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(fileTypeValue, topGrid);
			topGrid.gridy = 3;
			tempPanel.add(containerValue, topGrid);

			topGrid.gridx = 4;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.EAST;
			tempPanel.add(versionLabel, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(pageCountLabel, topGrid);

			topGrid.gridx = 5;
			topGrid.gridy = 1;
			topGrid.anchor = GridBagConstraints.WEST;
			tempPanel.add(versionValue, topGrid);
			topGrid.gridy = 2;
			tempPanel.add(pageCountValue, topGrid);
		}



		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(tempPanel);
		setLayout(new VFlowLayout(FlowLayout.LEADING));
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
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
		if(!"ZZ".equals(category)){
			cmPrintQueryBean.setOutDept(CommonUtil.objectToString(outDeptValue.getSelectedItem()));
		}
		cmPrintQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintQueryBean.setVersion(CommonUtil.objectToString(versionValue.getText()));
		cmPrintQueryBean.setFileType(CommonUtil.objectToString(fileTypeValue.getSelectedItem()));
		cmPrintQueryBean.setPhaseCode(CommonUtil.objectToString(phaseCodeValue.getSelectedItem()));
		cmPrintQueryBean.setSecret(CommonUtil.objectToString(secretValue.getText()));
		cmPrintQueryBean.setPageCount(CommonUtil.objectToString(pageCountValue.getText()));
		cmPrintQueryBean.setChangeNoticeID(CommonUtil.objectToString(UUID.randomUUID().toString()));
		cmPrintQueryBean.setContainer(CommonUtil.objectToString(containerValue.getSelectedItem()));

		return cmPrintQueryBean;
	}

	public void setValues(CmPrintInfoBean cmPrintInfoBean){
		if(!"ZZ".equals(category)){
			outDeptValue.setSelectedItem(cmPrintInfoBean.getOutDept());
		}
		fileNumberValue.setText(cmPrintInfoBean.getFileNumber());
		fileNameValue.setText(cmPrintInfoBean.getFileName());
		versionValue.setText(cmPrintInfoBean.getVersion());
		fileTypeValue.setSelectedItem(cmPrintInfoBean.getFileType());
		phaseCodeValue.setSelectedItem(cmPrintInfoBean.getPhaseCode());
		secretValue.setText(cmPrintInfoBean.getSecret());
		pageCountValue.setText(cmPrintInfoBean.getPageCount());
	}

	public AddChangeNoticeDialog getASddChangeNoticeDialog() {
		return addChangeNoticeDialog;
	}

	public boolean isModify(){
		return isModify;
	}

	public CmPrintInfoBean getCmPrintInfoBean(){
		return cmPrintInfoBean;
	}

}
