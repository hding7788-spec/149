package com.glaway.mpm.print.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DateChooser;
import com.glaway.mpm.util.DateUtil;
/**
 *
 * @author lkc
 *
 */
public class InFactoryFileStorageManagementPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 2120036619777295111L;
	private static final String NAME1 = "文件封存";
	private static final String NAME2 = "文件启封";
	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 文件类型 */
	private JLabel fileTypeLabel;
	/** 分发状态 */
	private JLabel distributeLabel;
	/** 版本*/
	private JLabel fileVersionLabel;
	/** 阶段标记*/
	private JLabel phaseCodeLabel;
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
	/** 文件类型--下拉框 */
	private JComboBox fileTypeValue;
	/** 分发状态--下拉框 */
	private JComboBox distributeValue;
	/** 文件版本 */
	private JTextField fileVersionValue;
	/** 阶段标记--下拉框 */
	private JComboBox phaseCodeValue;
	/** 打印开始日期 */
	private JTextField printStartDateValue;
	/** 打印结束日期 */
	private JTextField printEndDateValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;
	/** 文件封存 */
	private JButton storeButton;
	/** 关闭 */
	private JButton closeButton;
	/** 文件启封 */
	private JButton openStoreButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private JPanel topPanel;
	private JScrollPane tablePanel;
	private JPanel downPanel;
	private JComboBox comboBox;
	private JCheckBox allCheck;
	private String category;

	private List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
	public JButton getClearConditionButton() {
		return clearConditionButton;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public InFactoryFileStorageManagementPanel(String category) {
		this.category = category;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}
	public void initComponents(){
		topPanel = new JPanel();
		tablePanel = new JScrollPane();
		downPanel = new JPanel();
		String[] fileType = LoadPrintConfigurations.getInstance().getPrintFileTypeValue();
		String[] distribute = LoadPrintConfigurations.getInstance().getStorageFileTypeValue();
		String[] phaseCode = LoadPrintConfigurations.getInstance().getPhaseCodeValue();


		fileNumberLabel = new JLabel(PrintConstants.CONDITION_FILENUMBER);
		fileNameLabel = new JLabel(PrintConstants.CONDITION_FILENAME);
		fileTypeLabel = new JLabel(PrintConstants.CONDITION_FILETYPE);
		distributeLabel = new JLabel(PrintConstants.CONDITION_DISTRIBUTESTATUS);
		fileVersionLabel = new JLabel(PrintConstants.CONDITION_VERSION);
		phaseCodeLabel = new JLabel(PrintConstants.CONDITION_PHASECODE);
		printDateCheck = new JCheckBox(PrintConstants.CONDITION_PRINTDATE);
		printDateCheck.setSelected(true);
		printStartDateLabel = new JLabel(PrintConstants.CONDITION_STARTDATE);
		printEndDateLabel = new JLabel(PrintConstants.CONDITION_ENDDATE);
		comboBox = new JComboBox(new String[]{"", "1年", "2年", "3年", "4年", "5年", "6年", "7年", "8年", "9年", "10年"});

		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		fileTypeValue = new JComboBox(fileType);
		distributeValue = new JComboBox(distribute);
		distributeValue.setSelectedIndex(0);
		fileVersionValue = new JTextField();
		phaseCodeValue = new JComboBox(phaseCode);
		printStartDateValue = new JTextField("1970/01/01");
		printEndDateValue = new JTextField(DateUtil.getTodayDate());

		clearConditionButton = new JButton(PrintConstants.CONDITION_CLEARCONDITION);
		searchButton = new JButton(PrintConstants.CONDITION_SEARCH);
		storeButton = new JButton(PrintConstants.CONDITION_WJFC);
		openStoreButton = new JButton(PrintConstants.CONDITION_WJQF);
		closeButton = new JButton(PrintConstants.CONDITION_CLOSE);
		allCheck = new JCheckBox();
		allCheck.setText(PrintConstants.CONDITION_ALLCHECK);

		DateChooser printStartChooser = DateChooser.getInstance("yyyy/MM/dd");
		DateChooser printEndChooser = DateChooser.getInstance("yyyy/MM/dd");
		printStartChooser.register(printStartDateValue);
		printEndChooser.register(printEndDateValue);

        String[] tableColumn = {"", "id", "编号", "名称", "版本", "阶段标记", "文件类型", "密级", "分发部门", "申请人", "申请时间", "打印人", "打印时间", "领取人", "领取时间", "状态", "封存时间", "条码", "所属产品库"};
		String[][] tableData = {};
		tableModel = new DefaultTableModel(tableData,tableColumn)
		{
			private static final long serialVersionUID = -5817421228650378228L;

			public boolean isCellEditable(int row, int column) {
				if(column == 0 || column == 16){
					return true;
				}else{
					return false;
				}
			}

		};
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
		table.setRowHeight(30);
		DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
		render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		table.getColumnModel().getColumn(16).setCellEditor(new MyComEditor(comboBox));
		table.getColumnModel().getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
		table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setMaxWidth(0);
		table.getColumnModel().getColumn(1).setMinWidth(0);
		table.getColumnModel().getColumn(1).setPreferredWidth(0);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setPreferredWidth(120);
		table.getColumnModel().getColumn(3).setPreferredWidth(180);
		table.getColumnModel().getColumn(4).setPreferredWidth(50);
		table.getColumnModel().getColumn(5).setPreferredWidth(30);
		table.getColumnModel().getColumn(6).setPreferredWidth(50);
		table.getColumnModel().getColumn(7).setPreferredWidth(50);
		table.getColumnModel().getColumn(8).setPreferredWidth(50);
		table.getColumnModel().getColumn(9).setPreferredWidth(50);
		table.getColumnModel().getColumn(10).setPreferredWidth(80);
		table.getColumnModel().getColumn(11).setPreferredWidth(50);
		table.getColumnModel().getColumn(12).setPreferredWidth(80);
		table.getColumnModel().getColumn(13).setPreferredWidth(50);
		table.getColumnModel().getColumn(14).setPreferredWidth(80);
		table.getColumnModel().getColumn(15).setPreferredWidth(50);
		table.getColumnModel().getColumn(16).setPreferredWidth(60);
		table.getColumnModel().getColumn(17).setPreferredWidth(140);
		table.getColumnModel().getColumn(18).setPreferredWidth(80);
		tablePanel.setViewportView(table);
	}

	public void initLayout(){

		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();

		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 20, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(fileNumberLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(fileVersionLabel, topGrid);
		topGrid.gridy = 4;
		topPanel.add(printDateCheck, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 5);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(fileNumberValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(fileVersionValue, topGrid);
		topGrid.gridy = 4;
		topGrid.gridwidth = 2;
		JPanel printStartDate = new JPanel();
		printStartDate.add(printStartDateLabel);
		printStartDate.add(printStartDateValue);
		topPanel.add(printStartDate, topGrid);


		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.gridwidth = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(fileNameLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(phaseCodeLabel, topGrid);

		topGrid.gridx = 4;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 5);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(fileNameValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(phaseCodeValue, topGrid);
		topGrid.gridy = 4;
		topGrid.insets = new Insets(10, -50, 10, 5);
		JPanel printEndDate = new JPanel();
		printEndDate.add(printEndDateLabel);
		printEndDate.add(printEndDateValue);
		topPanel.add(printEndDate, topGrid);

		topGrid.gridx = 6;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(fileTypeLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(distributeLabel, topGrid);

		topGrid.gridx = 7;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(fileTypeValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(distributeValue, topGrid);


		topGrid.gridx = 8;
		topGrid.gridy = 2;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 0, 10, 2);
		topPanel.add(clearConditionButton, topGrid);

		topGrid.gridx = 9;
		topGrid.gridy = 2;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 2, 10, 2);
		topPanel.add(searchButton, topGrid);

		topGrid.gridx = 10;
		topGrid.gridy = 2;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 2, 10, 2);
		topPanel.add(closeButton, topGrid);

		downPanel.setLayout(new GridBagLayout());
		GridBagConstraints downGrid = new GridBagConstraints();

		downGrid.gridx = 0;
		downGrid.gridy = 0;
		downGrid.gridwidth = 1;
		downGrid.gridheight = 1;
		downGrid.fill = GridBagConstraints.BOTH;
		downGrid.anchor = GridBagConstraints.WEST;
		downGrid.insets = new Insets(5, 5, 5, 5);
		downPanel.add(storeButton, downGrid);

		downGrid.gridx = 1;
		downGrid.gridy = 0;
		downGrid.gridwidth = 1;
		downGrid.gridheight = 1;
		downGrid.fill = GridBagConstraints.BOTH;
		downGrid.anchor = GridBagConstraints.WEST;
		downGrid.insets = new Insets(5, 5, 5, 5);
		downPanel.add(openStoreButton, downGrid);

		downGrid.gridx = 0;
		downGrid.gridy = 1;
		downGrid.gridwidth = 10;
		downGrid.gridheight = 1;
		downGrid.weightx = 1;
		downGrid.weighty = 1;
		downGrid.fill = GridBagConstraints.BOTH;
		downGrid.insets = new Insets(5, 5, 5, 5);
		downPanel.add(tablePanel, downGrid);

		downGrid.gridx = 0;
		downGrid.gridy = 2;
		downGrid.gridwidth = 10;
		downGrid.gridheight = 1;
		downGrid.fill = GridBagConstraints.NORTHWEST;
		downGrid.insets = new Insets(5, 5, 5, 5);
		downPanel.add(allCheck, downGrid);

	    this.add(topPanel);
        this.add(downPanel);
		SpringLayout springLayout = new SpringLayout();
        springLayout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, downPanel, 0, SpringLayout.SOUTH, topPanel);
        springLayout.putConstraint(SpringLayout.WEST, downPanel, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, downPanel, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, downPanel, 0, SpringLayout.SOUTH, this.getContentPane());
        this.setLayout(springLayout);

        fileTypeValue.setPreferredSize(new Dimension(120, 25));
        fileNumberValue.setPreferredSize(new Dimension(120, 25));
        fileNameValue.setPreferredSize(new Dimension(120, 25));
        distributeValue.setPreferredSize(new Dimension(120, 25));
        fileVersionValue.setPreferredSize(new Dimension(120, 25));
        phaseCodeValue.setPreferredSize(new Dimension(100, 25));
        printStartDateValue.setPreferredSize(new Dimension(120, 25));
		printEndDateValue.setPreferredSize(new Dimension(120, 25));
        storeButton.setPreferredSize(new Dimension(120, 25));
        openStoreButton.setPreferredSize(new Dimension(120, 25));
	}

	public void initListener(){

		clearConditionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearCondition();
			}
		});

		allCheck.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(allCheck.isSelected()){
					for(int i=0;i<table.getRowCount();i++){
						table.getModel().setValueAt(true, i, 0);
					}
				}else{
					for(int i=0;i<table.getRowCount();i++){
						table.getModel().setValueAt(false, i, 0);
					}
				}
			}
		});

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.setRowCount(0);

				CmPrintRecordInfoBean cmPrintRecordQueryBean = getConditionValues();

				try {
					listBean = getQueryDistributeInfoOfStore(cmPrintRecordQueryBean);
					for (CmPrintRecordInfoBean cmPrintRecordQueryBean1 : listBean) {
						String id = cmPrintRecordQueryBean1.getBarTableID();
						String docVersion = cmPrintRecordQueryBean1.getDocVersion();
						String fileType = cmPrintRecordQueryBean1.getFileType();
						String fileNumber = cmPrintRecordQueryBean1.getFileNumber();
						String fileName = cmPrintRecordQueryBean1.getFileName();
						String printDate = cmPrintRecordQueryBean1.getPrintDate();
						String getDate = cmPrintRecordQueryBean1.getGetDate();
						String getDept = cmPrintRecordQueryBean1.getGetDept();
						String getUser = cmPrintRecordQueryBean1.getGetUser();
						String type = cmPrintRecordQueryBean1.getPhaseCode();
						String secret = cmPrintRecordQueryBean1.getSecret();
						String applyUser = cmPrintRecordQueryBean1.getApplyUser();
						String applyDate = cmPrintRecordQueryBean1.getApplyDate();
						String printUser = cmPrintRecordQueryBean1.getPrintUser();
						String distributeStatus = cmPrintRecordQueryBean1.getDistributeStatus();
						String barCode = cmPrintRecordQueryBean1.getBarCode();
						String storeTime = cmPrintRecordQueryBean1.getStoreTime();
						String containerName = cmPrintRecordQueryBean1.getContainerName();

						String[] value = {Boolean(false), id, fileNumber, fileName, docVersion, type, fileType, secret, getDept, applyUser, applyDate, printUser, printDate, getUser, getDate, distributeStatus, storeTime, barCode, containerName};
						tableModel.addRow(value);
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
		}
		});

		storeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					CommonUIUtil.stopTableCellEditing(table);
					String containerName = "";
					for(int row = 0; row < table.getRowCount(); row++){
						if("true".equals(String.valueOf(table.getModel().getValueAt(row, 0)))){
							if("".equals(containerName)){
								containerName = CommonUtil.objectToString(table.getValueAt(row, 18));
							}
							if(!containerName.equals(CommonUtil.objectToString(table.getValueAt(row, 18)))){
								JOptionPane.showMessageDialog(getContentPane(), "存在存储库不相同的文档");
								return;
							}
						}
					}
					if(!"".equals(containerName) && !"WL".equals(category) && !"ZZ".equals(category)){
						boolean checkContainer = MPMPrintProcessor.checkContainerRole(containerName);
						if(!checkContainer){
							CommonUIUtil.showMessageDialog(null, "当前用户在列表内文档的产品库内不是主任工艺师,无法进行此操作");
							return ;
						}
					}
					List<String> list = new ArrayList<String>();
					List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
					Boolean status = checkStoreStatus(table);
					if(status){
						Boolean flag = checkStoreTime(table);
						if(flag){
							listBean = getStoreInfo(table);
							if(listBean.size() > 0){
								list = getID(table);
								Boolean processFlag;
								String str;
								try {
									str = getSaveInfoOfStore(listBean);
									if("".equals(str) || null == str){
										processFlag = startProcessOfStore(NAME1, list);
										if(processFlag){
											JOptionPane.showMessageDialog(getContentPane(), "已成功启动封存流程！");
										}else{
											JOptionPane.showMessageDialog(getContentPane(), "流程启动失败！");
										}
									}else{
										JOptionPane.showMessageDialog(getContentPane(), "所选择需要封存的文件编号为" + str + "已存在封存流程中,请重新发起！");
										return;
									}
								} catch (RemoteException e1) {
									e1.printStackTrace();
								} catch (InvocationTargetException e1) {
									e1.printStackTrace();
								}
							}else{
								JOptionPane.showMessageDialog(getContentPane(), "未选择需要封存的文件！");
								return;
							}
						}else{
							JOptionPane.showMessageDialog(getContentPane(), "存在所选择需要封存的文件，封存时间未填！");
							return;
						}
					}else{
						JOptionPane.showMessageDialog(getContentPane(), "存在对非已入库文件发起封存申请！");
						return;
					}
				}
			});

		openStoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CommonUIUtil.stopTableCellEditing(table);
				String containerName = "";
				for(int row = 0; row < table.getRowCount(); row++){
					if("true".equals(String.valueOf(table.getModel().getValueAt(row, 0)))){
						if("".equals(containerName)){
							containerName = CommonUtil.objectToString(table.getValueAt(row, 18));
						}
						if(!containerName.equals(CommonUtil.objectToString(table.getValueAt(row, 18)))){
							JOptionPane.showMessageDialog(getContentPane(), "存在存储库不相同的文档");
							return;
						}
					}
				}
				if(!"".equals(containerName) && !"WL".equals(category) && !"ZZ".equals(category)){
					boolean checkContainer = MPMPrintProcessor.checkContainerRole(containerName);
					if(!checkContainer){
						CommonUIUtil.showMessageDialog(null, "当前用户在列表内文档的产品库内不是主任工艺师,无法进行此操作");
						return ;
					}
				}
				List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
				List<String> list = new ArrayList<String>();
				Boolean status = checkOpenStoreStatus(table);
				if(status){
					listBean = getOpenStoreInfo(table);
					if(listBean.size() > 0){
						list = getID(table);
						Boolean processFlag;
						String str;
						try {
							str = checkInfoOfOpenStore(listBean);
							if("".equals(str) || null == str){
								processFlag = startProcessOfStore(NAME2, list);
								if(processFlag == true){
									updateOpenStoreStatus(list);
									JOptionPane.showMessageDialog(null, "已成功启动启存流程！");
								} else {
									JOptionPane.showMessageDialog(null, "流程启动失败！");
								}
							}else{
								JOptionPane.showMessageDialog(getContentPane(), "所选择需要启封的文件编号为" + str + "已存在启封流程中,请重新发起！");
								return;
							}
						} catch (RemoteException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}
					}else{
						JOptionPane.showMessageDialog(getContentPane(), "未选择需要启封的文件！");
						return;
					}
				}else{
					JOptionPane.showMessageDialog(getContentPane(), "存在对非已封存文件发起启封申请！");
					return;
				}
			}
		});

		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = e.getActionCommand();
				if(str == "关闭"){
				   int result = JOptionPane.showConfirmDialog(getContentPane(),"是否关闭当前页面？","提示",JOptionPane.YES_NO_OPTION);
				   if(result == JOptionPane.YES_OPTION){
					   System.exit(0);
				   }
				}
			}
		});

		this.addWindowListener(new WindowAdapter() {
			   public void windowClosing(WindowEvent e) {
			   int close = JOptionPane.showConfirmDialog(getContentPane(), "是否关闭当前页面？", "提示",JOptionPane.YES_NO_OPTION);
			   if (close == JOptionPane.YES_OPTION) {
			     System.exit(0);
			    }else{
					   setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				   }
			 }
		});
	}

	public void initUI(){
		this.setTitle("文件封存与启封管理");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	protected String Boolean(boolean b) {
		return null;
	}

	public void clearCondition(){
		fileNumberValue.setText("");
		fileNameValue.setText("");
		fileVersionValue.setText("");
		phaseCodeValue.setSelectedIndex(0);
		printDateCheck.setSelected(false);
		printStartDateValue.setText("");
		printEndDateValue.setText("");

	}

	public CmPrintRecordInfoBean getConditionValues(){
		CmPrintRecordInfoBean cmPrintRecordQueryBean = new CmPrintRecordInfoBean();
		cmPrintRecordQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintRecordQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintRecordQueryBean.setFileType(CommonUtil.objectToString(fileTypeValue.getSelectedItem()));
		cmPrintRecordQueryBean.setDistributeStatus(CommonUtil.objectToString(distributeValue.getSelectedItem()));
		cmPrintRecordQueryBean.setDocVersion(CommonUtil.objectToString(fileVersionValue.getText()));
		cmPrintRecordQueryBean.setPhaseCode(CommonUtil.objectToString(phaseCodeValue.getSelectedItem()));
		cmPrintRecordQueryBean.setDistributeState(CommonUtil.objectToString(distributeValue.getSelectedItem()));
		if("true".equals(String.valueOf(printDateCheck.isSelected()))){
			cmPrintRecordQueryBean.setPrintStartDate(CommonUtil.objectToString(printStartDateValue.getText()));
			cmPrintRecordQueryBean.setPrintEndDate(CommonUtil.objectToString(printEndDateValue.getText()));
		}
		return cmPrintRecordQueryBean;
	}

	public List<String> getID(JTable table){
		List<String> list = new ArrayList<String>();
		 for (int i = 0; i < table.getRowCount(); i++) {
			   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				  list.add((String)table.getModel().getValueAt(i, 1));
			   }
		}
		 return list;
	}
	public List<CmPrintRecordInfoBean> getStoreInfo(JTable table){
		List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		try {
			String userName = getUserName();
			String userDept = getUserDepartment();
			String time = getCurrentTime();
			 for (int i = 0; i < table.getRowCount(); i++) {
				   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
					   CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
					   cmPrintRecordInfoBean.setUuid(UUID.randomUUID().toString());
					   cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 1));
					   cmPrintRecordInfoBean.setFileNumber((String)table.getModel().getValueAt(i, 2));
					   cmPrintRecordInfoBean.setCurrentUser(userName);
					   cmPrintRecordInfoBean.setCurrentDept(userDept);
					   cmPrintRecordInfoBean.setCurrentDate(time);
					   cmPrintRecordInfoBean.setStoreTime((String)table.getCellEditor(i, 16).getCellEditorValue());
					   list.add(cmPrintRecordInfoBean);
				   }
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		 return list;
	}
	public List<CmPrintRecordInfoBean> getOpenStoreInfo(JTable table){
		List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		 for (int i = 0; i < table.getRowCount(); i++) {
			   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				   CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
				   cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 1));
				   cmPrintRecordInfoBean.setFileNumber((String)table.getModel().getValueAt(i, 2));
				   list.add(cmPrintRecordInfoBean);
			   }
		}
		 return list;
	}
	public List<CmPrintRecordInfoBean> getDelayInfo(JTable table){
		List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		 for (int i = 0; i < table.getRowCount(); i++) {
			   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				   CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
				   cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 1));
				   cmPrintRecordInfoBean.setFileNumber((String)table.getModel().getValueAt(i, 2));
				   list.add(cmPrintRecordInfoBean);
			   }
		}
		 return list;
	}
	public Boolean checkStoreTime(JTable table){
		 for (int i = 0; i < table.getRowCount(); i++) {
			 if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				 if("".equals(table.getModel().getValueAt(i, 16)) || null == table.getModel().getValueAt(i, 16)){
					 return false;
				 }
			 }
		 }
		 return true;
	}
	public Boolean checkStoreStatus(JTable table){
		 for (int i = 0; i < table.getRowCount(); i++) {
			 if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				 if(!"已入库".equals(table.getModel().getValueAt(i, 15))){
					 return false;
				 }
			 }
		 }
		 return true;
	}
	public Boolean checkOpenStoreStatus(JTable table){
		 for (int i = 0; i < table.getRowCount(); i++) {
			 if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				 if(!"已封存".equals(table.getModel().getValueAt(i, 15))){
					 return false;
				 }
			 }
		 }
		 return true;
	}
	public Boolean checkDelayStatusByRecover(JTable table){
		 String dept;
		 try {
			 dept = getUserDepartment();
			 for (int i = 0; i < table.getRowCount(); i++) {
				 if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
					 if(!dept.equals(table.getModel().getValueAt(i, 8)) || !"延迟回收".equals(table.getModel().getValueAt(i, 15))){
						 return false;
					 }
				 }
			 }
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		 return true;
	}
	public Boolean checkDelayStatusByStore(JTable table){
		 String dept;
		 try {
			 dept = getUserDepartment();
			 for (int i = 0; i < table.getRowCount(); i++) {
				 if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
					 if(!dept.equals(table.getModel().getValueAt(i, 8)) || !"延迟封存".equals(table.getModel().getValueAt(i, 15))){
						 return false;
					 }
				 }
			 }
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		 return true;
	}
	public List<CmPrintRecordInfoBean> getQueryDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.inFactoryQueryDistributeInfoOfStore(cmPrintRecordQueryBean, category);
	}
	public String getSaveInfoOfStore(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.saveInfoOfStore(list);
	}
	public String checkInfoOfOpenStore(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.checkInfoOfOpenStore(list);
	}
	public String checkInfoOfDelayByRecover(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.checkInfoOfDelayByRecover(list);
	}
	public String checkInfoOfDelayByStore(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.checkInfoOfDelayByStore(list);
	}
	public Boolean startProcessOfStore(String name, List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.startProcessOfStore(name, list);
	}
	public Boolean startProcessOfDelay(String name, List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.startProcessOfRecover(name, list);
	}
	public void updateOpenStoreStatus(List<String> list) throws RemoteException, InvocationTargetException{
		PrintToWCIntf.updateOpenStoreStatus(list);
	}
	public void updateDelayStatusByRecover(List<String> list, String userName, String userDept) throws RemoteException, InvocationTargetException{
		PrintToWCIntf.updateDelayStatusByRecover(list, userName, userDept);
	}
	public void updateDelayStatusByStore(List<String> list, String userName, String userDept) throws RemoteException, InvocationTargetException{
		PrintToWCIntf.updateDelayStatusByStore(list, userName, userDept);
	}
	public String getUserGroup() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserGroup();
	}
	public String getUserDepartment() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserDepartment();
	}
	public String getUserName() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserName();
	}
	public String getCurrentTime(){
		Date nowDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String time = sdf.format(nowDate);
		return time;
	}
	class MyComEditor extends DefaultCellEditor {


		/**
		 *
		 */
		private static final long serialVersionUID = -5005348929536969485L;
		private JComboBox comboBox;
		private int currentCol = -1;
//		private int currentRow = -1;
		MyComEditor(JComboBox comboBox) {
			super(comboBox);
			this.comboBox = comboBox;
		}
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.currentCol = column;
//			this.currentRow = row;
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}

		public Object getCellEditorValue() {
			String text = this.comboBox.getSelectedItem().toString();
			int clickRow = table.getSelectedRow();
			boolean isClick = false;
			if(table.getModel().getValueAt(clickRow, 0)==null){
				isClick = false;
			} else{
				isClick = (Boolean) table.getModel().getValueAt(clickRow, 0);
			}
			if(isClick){
				for(int i=0;i<table.getRowCount();i++){ //批量编辑
					String isChecked = String.valueOf(table.getValueAt(i, 0));
					if("true".equals(isChecked)){
						table.getModel().setValueAt(text, i, currentCol);
					}
				}
			}
			return text;
		}
	}
}
