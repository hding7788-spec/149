package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.view.NewTechnicsPart;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.data.CmPrintRecordQueryBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.LoadPrintConfigurations;

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DateChooser;
import com.glaway.mpm.util.DateUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;


/**
 *
 * @author lkc
 *
 */

public class PrintRecordQueryMainPanel extends JFrame {

	private static final long serialVersionUID = 7409033894462591206L;
	/** 单位 */
	private JLabel unitLabel;
	/** 型号 */
	private JLabel typeLabel;
	/** 文件类型 */
	private JLabel fileTypeLabel;
	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 分发状态 */
	private JLabel distributeLabel;
	/** 打印日期 */
	private JCheckBox printDateCheck;
	/** 打印开始日期 */
	private JLabel printStartDateLabel;
	/** 打印结束日期 */
	private JLabel printEndDateLabel;
	/** 领取人 */
	private JLabel receivePersonLabel;
	/** 领取部门 */
	private JLabel receiveDeptLabel;
	/** 基于BOM查询*/
	private JLabel queryOnBOMLabel;
	/** 基于工艺文件目录查询*/
	private JLabel queryOnProcessDirectoryLabel;


	/** 单位--下拉框 */
	private JComboBox unitValue;
	/** 型号--文本框*/
	private JTextField typeValue;
	/** 文件类型--下拉框 */
	private JComboBox fileTypeValue;
	/** 文件编号--文本框 */
	private JTextField fileNumberValue;
	/** 文件名称--文本框 */
	private JTextField fileNameValue;
	/** 分发状态--下拉框 */
	private JComboBox distributeValue;
	/** 打印开始日期 */
	private JTextField printStartDateValue;
	/** 打印结束日期 */
	private JTextField printEndDateValue;
	/** 领取人--文本框 */
	private JTextField receivePersonValue;
	/** 领取部门--下拉框 */
	private JComboBox receiveDeptValue;
	/** 基于BOM查询 -- 文本框*/
	private JTextField queryOnBOMValue;
	/** 基于工艺文件目录查询*/
	private JTextField queryOnProcessDirectoryValue;

	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;
	/** 导出Excel */
	private JButton exportButton;
//	/**遗失申请 */
//	private JButton loseButton;
	/**关闭*/
	private JButton closeButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private boolean isWindowClose;
	private JPanel topPanel;
	private JPanel downPanel;
	private JScrollPane tablePanel;
	private List<CmPrintRecordInfoBean> list;

	public JButton getClearConditionButton() {
		return clearConditionButton;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public PrintRecordQueryMainPanel() {
		initComponents();
		initLayout();
		initListener(this);
		initUI();
	}

	public void initComponents(){
		topPanel = new JPanel();
		downPanel = new JPanel();
		tablePanel = new JScrollPane();
		list = new ArrayList<CmPrintRecordInfoBean>();
		String[] unitTypeSeal = LoadPrintConfigurations.getInstance().getUnitTypeSeal();
		String[] fileTypeSeal = LoadPrintConfigurations.getInstance().getPrintFileTypeValue();
		String[] distributeSeal = LoadPrintConfigurations.getInstance().getDistributeSeal();
		String[] deptSeal = LoadPrintConfigurations.getInstance().getDeptValue();

		unitLabel = new JLabel("单位：");
		typeLabel = new JLabel("所在产品库：");
		fileTypeLabel = new JLabel("文件类型：");
		fileNumberLabel = new JLabel("文件编号：");
		fileNameLabel = new JLabel("文件名称(图号)：");
		distributeLabel = new JLabel("分发状态：");
		printDateCheck = new JCheckBox("打印日期：");
		printDateCheck.setSelected(true);
		printStartDateLabel = new JLabel("开始日期：");
		printEndDateLabel = new JLabel("结束日期：");
		receivePersonLabel = new JLabel("领取人：");
		receiveDeptLabel = new JLabel("领取部门：");
		queryOnBOMLabel = new JLabel("基于BOM查询：");
		queryOnProcessDirectoryLabel = new JLabel("基于工艺文件目录查询：");
		exportButton = new JButton("导出到Excel");
		closeButton = new JButton("关闭");

		unitValue = new JComboBox(unitTypeSeal);
		unitValue.setSelectedIndex(1);
		typeValue = new JTextField();
		fileTypeValue = new JComboBox(fileTypeSeal);
		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		distributeValue = new JComboBox(distributeSeal);
		printStartDateValue = new JTextField("1970/01/01");
		printEndDateValue = new JTextField(DateUtil.getTodayDate());
		receivePersonValue = new JTextField();
		receiveDeptValue = new JComboBox(deptSeal);
		queryOnBOMValue = new JTextField();
		queryOnProcessDirectoryValue = new JTextField();
		clearConditionButton = new JButton("清空搜索条件");
		searchButton = new JButton("查询");
//		loseButton = new JButton("遗失申请");
//		try {
//			String userGroup = getUserGroup();
//			if(!"部门资料员".equals(userGroup)){
//				loseButton.setVisible(false);
//			}else{
//				loseButton.setVisible(true);
//			}
//		} catch (RemoteException e2) {
//			e2.printStackTrace();
//		} catch (InvocationTargetException e2) {
//			e2.printStackTrace();
//		}

	    DateChooser printStartChooser = DateChooser.getInstance("yyyy/MM/dd");
        DateChooser printEndChooser = DateChooser.getInstance("yyyy/MM/dd");
        printStartChooser.register(printStartDateValue);
        printEndChooser.register(printEndDateValue);

        String[] tableColumn = {"id", "单位", "所在产品库", "文件类型", "文件编号", "文件名称", "版本", "阶段标记", "密级", "分发状态", "打印时间","打印人", "领取时间", "领取部门", "领取人", "回收时间", "条码","遗失申请单","延迟回收申请单"};
		String[][] tableData = {};
		tableModel = new DefaultTableModel(tableData,tableColumn)
		{
			private static final long serialVersionUID = -5817421228650378228L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
		table.setRowHeight(30);
		DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
		render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setPreferredWidth(80);
		table.getColumnModel().getColumn(2).setPreferredWidth(120);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(4).setPreferredWidth(150);
		table.getColumnModel().getColumn(5).setPreferredWidth(220);
		table.getColumnModel().getColumn(6).setPreferredWidth(80);
		table.getColumnModel().getColumn(7).setPreferredWidth(80);
		table.getColumnModel().getColumn(8).setPreferredWidth(80);
		table.getColumnModel().getColumn(9).setPreferredWidth(80);
		table.getColumnModel().getColumn(10).setPreferredWidth(80);
		table.getColumnModel().getColumn(11).setPreferredWidth(80);//增加打印人一列，后面列数依次加1
		table.getColumnModel().getColumn(12).setPreferredWidth(80);
		table.getColumnModel().getColumn(13).setPreferredWidth(80);
		table.getColumnModel().getColumn(14).setPreferredWidth(120);
		table.getColumnModel().getColumn(15).setPreferredWidth(80);
		table.getColumnModel().getColumn(16).setPreferredWidth(180);
		table.getColumnModel().getColumn(17).setPreferredWidth(100);
		table.getColumnModel().getColumn(18).setPreferredWidth(100);
		table.getColumnModel().getColumn(18).setResizable(false);
		tablePanel.setViewportView(table);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		tablePanel.setPreferredSize(new Dimension((int)(width*0.9), (int)(height*0.1)));
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
		topPanel.add(unitLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(typeLabel, topGrid);
		topGrid.gridy = 4;
		topPanel.add(fileTypeLabel, topGrid);

		topGrid.gridy = 6;
		topPanel.add(printDateCheck, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 5);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(unitValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(typeValue, topGrid);
		topGrid.gridy = 4;
		topPanel.add(fileTypeValue, topGrid);

		topGrid.gridy = 6;
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
		topGrid.insets = new Insets(10, -100, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(fileNameLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(distributeLabel, topGrid);
		topGrid.gridy = 4;
		topPanel.add(receiveDeptLabel, topGrid);

		topGrid.gridx = 4;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 5);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(fileNameValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(distributeValue, topGrid);
		topGrid.gridy = 4;
		topPanel.add(receiveDeptValue, topGrid);
		topGrid.gridy = 6;
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
		topPanel.add(fileNumberLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(receivePersonLabel, topGrid);

		topGrid.gridx = 7;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(fileNumberValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(receivePersonValue, topGrid);
		topGrid.gridy = 4;
		topGrid.insets = new Insets(10, 2, 10, 2);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(clearConditionButton, topGrid);

		topGrid.gridx = 8;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(queryOnBOMLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(queryOnProcessDirectoryLabel, topGrid);
		topGrid.gridy = 4;
		topGrid.insets = new Insets(10, 100, 10, 2);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(searchButton, topGrid);
		topGrid.gridy = 6;
		topGrid.insets = new Insets(10, 100, 10, 2);
		topPanel.add(closeButton, topGrid);

		topGrid.gridx = 9;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(queryOnBOMValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(queryOnProcessDirectoryValue, topGrid);

		topGrid.gridx = 0;
		topGrid.gridy = 7;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(exportButton, topGrid);

		downPanel.setLayout(new VFlowLayout(0, 0, 0, true, true));
		downPanel.add(tablePanel);
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

        unitValue.setPreferredSize(new Dimension(120, 25));
        typeValue.setPreferredSize(new Dimension(120, 25));
        fileTypeValue.setPreferredSize(new Dimension(120, 25));
        fileNumberValue.setPreferredSize(new Dimension(120, 25));
        fileNameValue.setPreferredSize(new Dimension(120, 25));
        distributeValue.setPreferredSize(new Dimension(120, 25));
        printStartDateValue.setPreferredSize(new Dimension(120, 25));
		printEndDateValue.setPreferredSize(new Dimension(120, 25));
        receivePersonValue.setPreferredSize(new Dimension(120, 25));
        exportButton.setPreferredSize(new Dimension(120, 25));
//        loseButton.setPreferredSize(new Dimension(120, 25));
        queryOnBOMValue.setPreferredSize(new Dimension(120, 25));
        queryOnProcessDirectoryValue.setPreferredSize(new Dimension(120, 25));
        receiveDeptValue.setPreferredSize(new Dimension(120, 25));
	}

	public void initListener(final JFrame frame){

		clearConditionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearCondition();
			}
		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isWindowClose = false;
				tableModel.setRowCount(0);
				final CmPrintRecordQueryBean cmPrintRecordQueryBean = getConditionValues();
				final VaActionProgressBar progressBar = new VaActionProgressBar(
						frame, "搜索", "正在搜索,请等待...", "搜索中");
				final Thread thread = new Thread() {
					public void run(){
						try {
							list = new ArrayList<CmPrintRecordInfoBean>();
							if("".equals(cmPrintRecordQueryBean.getBOMValue()) && "".equals(cmPrintRecordQueryBean.getProcessDirectoryValue())){
								if("true".equals(String.valueOf(printDateCheck.isSelected()))){
									if("".equals(cmPrintRecordQueryBean.getPrintStartDate()) || null == cmPrintRecordQueryBean.getPrintStartDate()){
										JOptionPane.showMessageDialog(null, "打印开始时间为空！请正确选择。");
										return;
									}else if("".equals(cmPrintRecordQueryBean.getPrintEndDate()) || null == cmPrintRecordQueryBean.getPrintEndDate()){
										JOptionPane.showMessageDialog(null, "打印结束时间为空！请正确选择。");
										return;
									}
								}
								list = getQueryInfo(cmPrintRecordQueryBean);
							}else{
								if(!"".equals(cmPrintRecordQueryBean.getBOMValue())){//基于BOM查询
									String value = cmPrintRecordQueryBean.getBOMValue();
									List<String> technicsList = MPMPrintProcessor.getTechnicsNumberByBOM(value);
									if(technicsList != null && !technicsList.isEmpty()){
										list = MPMPrintProcessor.queryPrintInfo(technicsList);
									}
								}else if(!"".equals(cmPrintRecordQueryBean.getProcessDirectoryValue())){//基于工艺文件目录查询
									String value = cmPrintRecordQueryBean.getProcessDirectoryValue();
									List<String> technicsList = MPMPrintProcessor.getTechnicsNumberByProcessDirectory(value);
									if(technicsList != null && !technicsList.isEmpty()){
										list = MPMPrintProcessor.queryPrintInfo(technicsList);
									}
								}
							}
							for (CmPrintRecordInfoBean cmPrintRecordInfoBean1 : list) {

								//判断是否被中断
								if(isWindowClose){
									//处理中断逻辑
									break;
								}
								String barTableID = cmPrintRecordInfoBean1.getBarTableID();
								String losePboNumber = MPMPrintProcessor.queryLosePboNumber(barTableID);
								String delayPboNumber = MPMPrintProcessor.querydelayPboNumber(barTableID);
								String unit = cmPrintRecordInfoBean1.getUnit().indexOf("厂内") > -1 ? "厂内" : cmPrintRecordInfoBean1.getUnit();
								String type = cmPrintRecordInfoBean1.getType();
								String fileType = cmPrintRecordInfoBean1.getFileType();
								String fileNumber = cmPrintRecordInfoBean1.getFileNumber();
								String fileName = cmPrintRecordInfoBean1.getFileName();
								String version = cmPrintRecordInfoBean1.getDocVersion();
								String phaseCode = cmPrintRecordInfoBean1.getPhaseCode();
								String secret = cmPrintRecordInfoBean1.getSecret();
								String distributeStatus = cmPrintRecordInfoBean1.getDistributeStatus();
								String printDate = cmPrintRecordInfoBean1.getPrintDate();
								String getDate = cmPrintRecordInfoBean1.getGetDate();
								String getDept = cmPrintRecordInfoBean1.getGetDept();
								String getUser = cmPrintRecordInfoBean1.getGetUser();
								String barCode = cmPrintRecordInfoBean1.getBarCode();
								String recoverDate = cmPrintRecordInfoBean1.getRecoverDate();
								String printUser = cmPrintRecordInfoBean1.getPrintUser();
								String[] value = {barTableID, unit, type, fileType, fileNumber, fileName, version, phaseCode, secret, distributeStatus, printDate,printUser, getDate, getDept, getUser, recoverDate, barCode, losePboNumber, delayPboNumber};
								tableModel.addRow(value);
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						progressBar.finish();
						progressBar.setVisible(false);
					}
				};
				thread.start();
				progressBar.setVisible(true);
				progressBar.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						isWindowClose = true;
					}
				});
			}
		});

		exportButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"XLS", "xls");
				fileChooser.setFileFilter(filter);
				final File file = FileSystemView.getFileSystemView()
						.getHomeDirectory();
				fileChooser.setCurrentDirectory(file);
				fileChooser.setApproveButtonText("保存");
				fileChooser.showOpenDialog(null);
				File file1 = fileChooser.getSelectedFile();
				CmPrintRecordQueryBean cmPrintRecordQueryBean = getConditionValues();
				try {
					list = getQueryInfo(cmPrintRecordQueryBean);
					try {
						createExcel(list, file1);
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (WriteException e1) {
						e1.printStackTrace();
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
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
			   if (close == 0) {
			     System.exit(0);
			   }else{
				   setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			   }
			 }
		});

		queryOnBOMValue.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				queryOnProcessDirectoryValue.setText("");
				queryOnProcessDirectoryValue.setEditable(false);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if("".equals(queryOnBOMValue.getText())){
					queryOnProcessDirectoryValue.setEditable(true);
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});

		queryOnProcessDirectoryValue.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				queryOnBOMValue.setText("");
				queryOnBOMValue.setEditable(false);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if("".equals(queryOnProcessDirectoryValue.getText())){
					queryOnBOMValue.setEditable(true);
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});

		printDateCheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if("false".equals(String.valueOf(printDateCheck.isSelected()))){
					printStartDateValue.setText("");
					printEndDateValue.setText("");
				}
			}
		});
	}

	public void initUI(){
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setTitle("文件打印分发记录查询");
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	public void clearCondition(){

		unitValue.setSelectedIndex(0);
		typeValue.setText("");
		fileTypeValue.setSelectedIndex(0);
		fileNumberValue.setText("");
		fileNameValue.setText("");
		distributeValue.setSelectedIndex(0);
		printStartDateValue.setText("");
		printEndDateValue.setText("");
		receivePersonValue.setText("");
		queryOnBOMValue.setText("");
		queryOnProcessDirectoryValue.setText("");
		printDateCheck.setSelected(false);
		printStartDateValue.setText("");
		printEndDateValue.setText("");

	}

	public CmPrintRecordQueryBean getConditionValues(){
		CmPrintRecordQueryBean cmPrintRecordQueryBean = new CmPrintRecordQueryBean();
		cmPrintRecordQueryBean.setUnit(CommonUtil.objectToString(unitValue.getSelectedItem()));
		cmPrintRecordQueryBean.setGetDept(CommonUtil.objectToString(receiveDeptValue.getSelectedItem()));
		cmPrintRecordQueryBean.setType(CommonUtil.objectToString(typeValue.getText()));
		cmPrintRecordQueryBean.setFileType(CommonUtil.objectToString(fileTypeValue.getSelectedItem()));
		cmPrintRecordQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintRecordQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintRecordQueryBean.setDistributeStatus(CommonUtil.objectToString(distributeValue.getSelectedItem()));
		if("true".equals(String.valueOf(printDateCheck.isSelected()))){
			cmPrintRecordQueryBean.setPrintStartDate(CommonUtil.objectToString(printStartDateValue.getText()));
			cmPrintRecordQueryBean.setPrintEndDate(CommonUtil.objectToString(printEndDateValue.getText()));
		}
		//cmPrintRecordQueryBean.setPrintDate(CommonUtil.objectToString(printDateValue.getText()));
		cmPrintRecordQueryBean.setGetUser(CommonUtil.objectToString(receivePersonValue.getText()));
		cmPrintRecordQueryBean.setBOMValue(CommonUtil.objectToString(queryOnBOMValue.getText()));
		cmPrintRecordQueryBean.setProcessDirectoryValue(CommonUtil.objectToString(queryOnProcessDirectoryValue.getText()));
		return cmPrintRecordQueryBean;
	}

	public List<CmPrintRecordInfoBean> getQueryInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryInfo(cmPrintRecordQueryBean);
	}

	public static void createExcel(List<CmPrintRecordInfoBean> list,File file) throws IOException, WriteException{
		if (file != null) {
			if (file.isFile() && file.exists()) {
				if (!file.renameTo(file)) {
					JOptionPane.showMessageDialog(null, "另一个程序正在使用此文件！", "提示", 1);
					return;
				}
			}
			String path = file.getPath();
			if (!path.endsWith(".xls")) {
				path = path.concat(".xls");
				file = new File(path);
			}
			WritableWorkbook wb = Workbook.createWorkbook(file);
			//创建新的一页
			WritableSheet sheet = wb.createSheet("文件打印分发记录查询结果表", 0);
			for(int m=0;m<13;m++) {
				sheet.setColumnView(m, 20);
			}
			sheet.addCell(new Label(0, 0, "单位"));
			sheet.addCell(new Label(1, 0, "型号"));
			sheet.addCell(new Label(2, 0, "文件类型"));
			sheet.addCell(new Label(3, 0, "文件编号"));
			sheet.addCell(new Label(4, 0, "文件名称"));
			sheet.addCell(new Label(5, 0, "版本"));
			sheet.addCell(new Label(6, 0, "阶段标记"));
			sheet.addCell(new Label(7, 0, "密级"));
			sheet.addCell(new Label(8, 0, "分发状态"));
			sheet.addCell(new Label(9, 0, "打印时间"));
			sheet.addCell(new Label(10, 0, "领取时间"));
			sheet.addCell(new Label(11, 0, "领取部门"));
			sheet.addCell(new Label(12, 0, "领取人"));
			sheet.addCell(new Label(13, 0, "回收时间"));
			sheet.addCell(new Label(14, 0, "条码"));
			for(int i=0;i<list.size();i++) {
				CmPrintRecordInfoBean item = list.get(i);
				sheet.addCell(new Label(0,i+1,item.getUnit().indexOf("厂内") > -1 ? "厂内" : item.getUnit()));
				sheet.addCell(new Label(1,i+1,item.getType()));
				sheet.addCell(new Label(2,i+1,item.getFileType()));
				sheet.addCell(new Label(3,i+1,item.getFileNumber()));
				sheet.addCell(new Label(4,i+1,item.getFileName()));
				sheet.addCell(new Label(5,i+1,item.getDocVersion()));
				sheet.addCell(new Label(6,i+1,item.getPhaseCode()));
				sheet.addCell(new Label(7,i+1,item.getSecret()));
				sheet.addCell(new Label(8,i+1,item.getDistributeStatus()));
				sheet.addCell(new Label(9,i+1,item.getPrintDate()));
				sheet.addCell(new Label(10,i+1,item.getGetDate()));
				sheet.addCell(new Label(11,i+1,item.getGetDept()));
				sheet.addCell(new Label(12,i+1,item.getGetUser()));
				sheet.addCell(new Label(13,i+1,item.getRecoverDate()));
				sheet.addCell(new Label(14,i+1,item.getBarCode()));
		}
			wb.write();
			wb.close();
		    JOptionPane.showMessageDialog(null, "导出成功！");
		}
	}

	public List<CmPrintRecordInfoBean> getLoseInfo(JTable table){
		List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
		String dept;
		try {
			dept = getUserDepartment();
			int row = table.getRowCount();
			for (int i = 0; i < row; i++) {
				if("已下发".equals(table.getModel().getValueAt(i, 9)) && dept.equals(table.getModel().getValueAt(i, 13))){
					CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
					cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 0));
					cmPrintRecordInfoBean.setFileType((String)table.getModel().getValueAt(i, 3));
					cmPrintRecordInfoBean.setFileNumber((String)table.getModel().getValueAt(i, 4));
					cmPrintRecordInfoBean.setFileName((String)table.getModel().getValueAt(i, 5));
					cmPrintRecordInfoBean.setBarCode((String)table.getModel().getValueAt(i, 16));
					listBean.add(cmPrintRecordInfoBean);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return listBean;
	}

	public List<CmPrintRecordInfoBean> getDelayInfo(JTable table){
		List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
		String dept;
		try {
			dept = getUserDepartment();
			int row = table.getRowCount();
			for (int i = 0; i < row; i++) {
				if("延迟回收".equals(table.getModel().getValueAt(i, 9)) && dept.equals(table.getModel().getValueAt(i, 12))){
					CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
					cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 0));
					cmPrintRecordInfoBean.setFileType((String)table.getModel().getValueAt(i, 3));
					cmPrintRecordInfoBean.setFileNumber((String)table.getModel().getValueAt(i, 4));
					cmPrintRecordInfoBean.setFileName((String)table.getModel().getValueAt(i, 5));
					cmPrintRecordInfoBean.setBarCode((String)table.getModel().getValueAt(i, 15));
					listBean.add(cmPrintRecordInfoBean);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return listBean;
	}

	public String getUserDepartment() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserDepartment();
	}
	public String getUserGroup() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserGroup();
	}
}
