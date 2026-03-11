package com.glaway.mpm.print.ui;

import java.awt.BorderLayout;
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
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.data.CmPrintRecordQueryBean;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DateChooser;
import com.glaway.mpm.util.DateUtil;

/**
 *
 * @author lkc
 *
 */
public class FileStockManagementPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 1726711413692439910L;

	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 文件类型 */
	private JLabel fileTypeLabel;
	/** 版本*/
	private JLabel fileVersionLabel;
	/** 阶段标记*/
	private JLabel phaseCodeLabel;
	/** 文件状态 */
	private JLabel distributeLabel;

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
	/** 文件版本 */
	private JTextField fileVersionValue;
	/** 阶段标记--下拉框 */
	private JComboBox phaseCodeValue;
	/** 文件状态--下拉框 */
	private JComboBox distributeValue;
	/** 打印开始日期 */
	private JTextField printStartDateValue;
	/** 打印结束日期 */
	private JTextField printEndDateValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;
	/** 文件入库 */
	private JButton inputButton;
	/** 关闭按钮 */
	private JButton closeButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private JPanel topPanel;
	private JPanel downPanel;
	private JScrollPane tablePanel;

	private JCheckBox allCheck;

	private String category;

	private List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
	public JButton getClearConditionButton() {
		return clearConditionButton;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public FileStockManagementPanel(String category) {
		this.category = category;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}
	public void initComponents(){
		topPanel = new JPanel();
		downPanel = new JPanel();
		tablePanel = new JScrollPane();
		String[] fileType = LoadPrintConfigurations.getInstance().getPrintFileTypeValue();
		String[] distribute = LoadPrintConfigurations.getInstance().getDistributeSeal();
		String[] phaseCode = LoadPrintConfigurations.getInstance().getPhaseCodeValue();


		fileNumberLabel = new JLabel("文件编号 :");
		fileNameLabel = new JLabel("文件名称 :");
		fileTypeLabel = new JLabel("文件类型 :");
		fileVersionLabel = new JLabel("版本:");
		phaseCodeLabel = new JLabel("阶段标记:");
		distributeLabel = new JLabel("分发状态 :");

		printDateCheck = new JCheckBox("打印日期：");
		printDateCheck.setSelected(true);
		printStartDateLabel = new JLabel("开始日期：");
		printEndDateLabel = new JLabel("结束日期：");

		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		fileTypeValue = new JComboBox(fileType);
		fileVersionValue = new JTextField();
		phaseCodeValue = new JComboBox(phaseCode);
		distributeValue = new JComboBox(distribute);

		printStartDateValue = new JTextField("1970/01/01");
		printEndDateValue = new JTextField(DateUtil.getTodayDate());
		clearConditionButton = new JButton("清空搜索条件");
		searchButton = new JButton("查询");
		inputButton = new JButton("文件入库");
		closeButton = new JButton("关闭");
		allCheck = new JCheckBox();
		allCheck.setText("全选");

		DateChooser printStartChooser = DateChooser.getInstance("yyyy/MM/dd");
        DateChooser printEndChooser = DateChooser.getInstance("yyyy/MM/dd");
        printStartChooser.register(printStartDateValue);
        printEndChooser.register(printEndDateValue);



        String[] tableColumn = {"","编号", "名称", "版本", "阶段标记", "文件类型", "密级", "分发部门", "申请人", "申请时间", "打印人", "打印时间", "领取人", "领取时间", "状态", "条码"};
        String[][] tableData = {};
		tableModel = new DefaultTableModel(tableData,tableColumn)
		{
			private static final long serialVersionUID = -5817421228650378228L;

			public boolean isCellEditable(int row, int column) {
				if(column == 0){
					return true;
				}
				return false;
			}
		};

		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
		table.setRowHeight(30);
		table.getColumnModel().getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
		table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(160);
		table.getColumnModel().getColumn(3).setPreferredWidth(50);
		table.getColumnModel().getColumn(4).setPreferredWidth(30);
		table.getColumnModel().getColumn(5).setPreferredWidth(50);
		table.getColumnModel().getColumn(6).setPreferredWidth(50);
		table.getColumnModel().getColumn(7).setPreferredWidth(50);
		table.getColumnModel().getColumn(8).setPreferredWidth(50);
		table.getColumnModel().getColumn(9).setPreferredWidth(80);
		table.getColumnModel().getColumn(10).setPreferredWidth(50);
		table.getColumnModel().getColumn(11).setPreferredWidth(80);
		table.getColumnModel().getColumn(12).setPreferredWidth(50);
		table.getColumnModel().getColumn(13).setPreferredWidth(80);
		table.getColumnModel().getColumn(14).setPreferredWidth(50);
		table.getColumnModel().getColumn(15).setPreferredWidth(120);
		DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
		render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
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
		JPanel printStartDate = new JPanel();
		printStartDate.add(printStartDateLabel);
		printStartDate.add(printStartDateValue);
		topPanel.add(printStartDate, topGrid);

		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
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
		topGrid.insets = new Insets(10, -70, 10, 5);
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
		topGrid.insets = new Insets(10, 0, 10, 2);
		topPanel.add(clearConditionButton, topGrid);

		topGrid.gridx = 9;
		topGrid.gridy = 2;
		topGrid.insets = new Insets(10, 2, 10, 2);
		topPanel.add(searchButton, topGrid);

		topGrid.gridx = 10;
		topGrid.gridy = 2;
		topGrid.insets = new Insets(10, 2, 10, 2);
		topPanel.add(closeButton, topGrid);

		downPanel.setLayout(new GridBagLayout());
		GridBagConstraints downGrid = new GridBagConstraints();

		downGrid.gridx = 0;
		downGrid.gridy = 0;
		downGrid.gridwidth = 1;
		downGrid.gridheight = 1;
		downGrid.fill = GridBagConstraints.NORTHWEST;
		downGrid.insets = new Insets(5, 5, 5, 5);
		downPanel.add(inputButton, downGrid);

		downGrid.gridx = 0;
		downGrid.gridy = 1;
		downGrid.gridwidth = 9;
		downGrid.gridheight = 1;
		downGrid.weightx = 1;
		downGrid.weighty = 1;
		downGrid.fill = GridBagConstraints.BOTH;
		downGrid.insets = new Insets(5, 5, 5, 5);
		downPanel.add(tablePanel, downGrid);

		downGrid.gridx = 0;
		downGrid.gridy = 2;
		downGrid.gridwidth = 9;
		downGrid.gridheight = 1;
		downGrid.insets = new Insets(5, 5, 5, 5);
		downPanel.add(allCheck, downGrid);

        fileTypeValue.setPreferredSize(new Dimension(120, 25));
        fileNumberValue.setPreferredSize(new Dimension(120, 25));
        fileNameValue.setPreferredSize(new Dimension(120, 25));
        fileVersionValue.setPreferredSize(new Dimension(120, 25));
        phaseCodeValue.setPreferredSize(new Dimension(120, 25));
        distributeValue.setPreferredSize(new Dimension(120, 25));
        printStartDateValue.setPreferredSize(new Dimension(120, 25));
		printEndDateValue.setPreferredSize(new Dimension(120, 25));
        inputButton.setPreferredSize(new Dimension(120, 25));

		this.add(topPanel, BorderLayout.NORTH);
		this.add(downPanel, BorderLayout.CENTER);
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

				CmPrintRecordQueryBean cmPrintRecordQueryBean = getConditionValues();
				try {
					if("true".equals(String.valueOf(printDateCheck.isSelected()))){
						if("".equals(cmPrintRecordQueryBean.getPrintStartDate()) || null == cmPrintRecordQueryBean.getPrintStartDate()){
							JOptionPane.showMessageDialog(null, "打印开始时间为空！请正确选择。");
							return;
						}else if("".equals(cmPrintRecordQueryBean.getPrintEndDate()) || null == cmPrintRecordQueryBean.getPrintEndDate()){
							JOptionPane.showMessageDialog(null, "打印结束时间为空！请正确选择。");
							return;
						}
					}
					listBean = getQueryDistributeInfo(cmPrintRecordQueryBean, category);
					for (CmPrintRecordInfoBean cmPrintRecordQueryBean1 : listBean) {
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

						Object[] value = {false,fileNumber, fileName, docVersion, type, fileType, secret, getDept, applyUser, applyDate, printUser, printDate, getUser, getDate, distributeStatus, barCode};
						tableModel.addRow(value);
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
		}
	});

		inputButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Boolean check = checkInfo(table);
					if(check){
						List<CmPrintRecordInfoBean> listBean = getInfo(table);
						try {
							Boolean flag = synchDangan(listBean);
							if(flag){
								JOptionPane.showMessageDialog(getContentPane(), "入库成功！");
								int tableRow = table.getRowCount();
								for(int i = 0; i < tableRow; i++){
									boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(i, 0)));
									if(isSelect){
										table.setValueAt("已入库", i, 14);
									}
								}
								table.updateUI();
							}else{
								JOptionPane.showMessageDialog(getContentPane(), "入库失败！");
							}
						} catch (RemoteException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}
					}else{
						JOptionPane.showMessageDialog(getContentPane(), "存在对非已下发文件发起入库！");
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
		this.setTitle("文件入库管理");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	public void clearCondition(){

//		unitValue.setSelectedIndex(0);
//		typeValue.setText("");

		fileNumberValue.setText("");
		fileNameValue.setText("");
		fileTypeValue.setSelectedIndex(0);
		fileVersionValue.setText("");
		phaseCodeValue.setSelectedIndex(0);
		distributeValue.setSelectedIndex(0);
		printDateCheck.setSelected(false);
		printStartDateValue.setText("");
		printEndDateValue.setText("");

	}

	public CmPrintRecordQueryBean getConditionValues(){
		CmPrintRecordQueryBean cmPrintRecordQueryBean = new CmPrintRecordQueryBean();

		cmPrintRecordQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintRecordQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintRecordQueryBean.setFileType(CommonUtil.objectToString(fileTypeValue.getSelectedItem()));
		cmPrintRecordQueryBean.setDocVersion(CommonUtil.objectToString(fileVersionValue.getText()));
		cmPrintRecordQueryBean.setPhaseCode(CommonUtil.objectToString(phaseCodeValue.getSelectedItem()));
		cmPrintRecordQueryBean.setDistributeStatus(CommonUtil.objectToString(distributeValue.getSelectedItem()));
		if("true".equals(String.valueOf(printDateCheck.isSelected()))){
			cmPrintRecordQueryBean.setPrintStartDate(CommonUtil.objectToString(printStartDateValue.getText()));
			cmPrintRecordQueryBean.setPrintEndDate(CommonUtil.objectToString(printEndDateValue.getText()));
		}
		return cmPrintRecordQueryBean;
	}

	public List<CmPrintRecordInfoBean> getQueryDistributeInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean, String category) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryDistributeInfo(cmPrintRecordQueryBean, category);
	}

	public Boolean synchDangan(List<CmPrintRecordInfoBean> listBean) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.synchDangan(listBean, category);
	}

	//从表格中获得同步至系统的文件信息
	public List<CmPrintRecordInfoBean> getInfo(JTable table){
		List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
		int tableRow = table.getRowCount();
		for(int i = 0; i < tableRow; i++){
			boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(i, 0)));
			if(isSelect){
				CmPrintRecordInfoBean cmPrintRecordQueryBean = new CmPrintRecordInfoBean();
				cmPrintRecordQueryBean.setFileNumber((String)table.getModel().getValueAt(i, 1));
				cmPrintRecordQueryBean.setFileName((String)table.getModel().getValueAt(i, 2));
				cmPrintRecordQueryBean.setDocVersion((String)table.getModel().getValueAt(i, 3));
				cmPrintRecordQueryBean.setBarCode(CommonUtil.objectToString(table.getValueAt(i, 15)));
				listBean.add(cmPrintRecordQueryBean);
			}
		}
		return listBean;
	}
	public Boolean checkInfo(JTable table){
		int tableRow = table.getRowCount();
		for(int i = 0; i < tableRow; i++){
			boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(i, 0)));
			if(isSelect){
				String status = (String)table.getModel().getValueAt(i, 14);
				if(!"已下发".equals(status)){
					return false;
				}
			}
		}
		return true;
	}
}
