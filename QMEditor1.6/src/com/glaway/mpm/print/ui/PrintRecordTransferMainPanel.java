package com.glaway.mpm.print.ui;

import com.glaway.mpm.model.data.CmUser;
import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.data.CmPrintRecordQueryBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DateChooser;
import com.glaway.mpm.util.DateUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author cjh
 *
 */

public class PrintRecordTransferMainPanel extends JFrame {

	private static final long serialVersionUID = 6344294123850002691L;
	/** 型号 */
	private JLabel productLabel;
	/** 文件类型 */
	private JLabel fileTypeLabel;
	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 打印日期 */
	private JCheckBox printDateCheck;
	/** 打印开始日期 */
	private JLabel printStartDateLabel;
	/** 打印结束日期 */
	private JLabel printEndDateLabel;
	/** 领取人 */
	private JLabel receivePersonLabel;

	/** 型号--文本框*/
	private JTextField productValue;
	/** 文件类型--下拉框 */
	private JComboBox fileTypeValue;
	/** 文件编号--文本框 */
	private JTextField fileNumberValue;
	/** 文件名称--文本框 */
	private JTextField fileNameValue;
	/** 打印开始日期 */
	private JTextField printStartDateValue;
	/** 打印结束日期 */
	private JTextField printEndDateValue;
	/** 领取人--文本框 */
	private JTextField receivePersonValue;

	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;
	/** 分发转移 */
	private JButton transferButton;
	/**关闭*/
	private JButton closeButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private boolean isWindowClose;
	private JPanel topPanel;
	private JPanel downPanel;
	private JScrollPane tablePanel;
	private List<CmPrintRecordInfoBean> list;
	private JCheckBox allCheck;

	/** 表头 */
	private Class<?>[] tableColumnClass;
	/** 可编辑的列 */
	private int[] editableColumns = new int[] {1,16 };
	/** 当前用户信息 */
	private static CmUser currentUser;
	Map<String,List<String>> userList;

	public JButton getClearConditionButton() {
		return clearConditionButton;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public PrintRecordTransferMainPanel() {
		initComponents();
		initLayout();
		initListener(this);
		initUI();
	}

	public void initComponents(){
		currentUser = MPMPrintProcessor.getCurrentUser();
		topPanel = new JPanel();
		downPanel = new JPanel();
		tablePanel = new JScrollPane();
		list = new ArrayList<CmPrintRecordInfoBean>();
		String[] fileTypeSeal = LoadPrintConfigurations.getInstance().getPrintFileTypeValue();
		String[] deptSeal = LoadPrintConfigurations.getInstance().getDeptValue();
		userList = MPMPrintProcessor.getPrinterByDepts(deptSeal);
		allCheck = new JCheckBox();
		allCheck.setText(PrintConstants.CONDITION_ALLCHECK);

		productLabel = new JLabel("所在产品库：");
		fileTypeLabel = new JLabel("文件类型：");
		fileNumberLabel = new JLabel("文件编号：");
		fileNameLabel = new JLabel("文件名称(图号)：");
		printDateCheck = new JCheckBox("打印日期：");
		printDateCheck.setSelected(true);
		printStartDateLabel = new JLabel("开始日期：");
		printEndDateLabel = new JLabel("结束日期：");
		receivePersonLabel = new JLabel("领取人：");
		transferButton = new JButton("分发转移");
		closeButton = new JButton("关闭");

		productValue = new JTextField();
		fileTypeValue = new JComboBox(fileTypeSeal);
		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		printStartDateValue = new JTextField("1970/01/01");
		printEndDateValue = new JTextField(DateUtil.getTodayDate());
		receivePersonValue = new JTextField();
		clearConditionButton = new JButton("清空搜索条件");
		searchButton = new JButton("查询");

	    DateChooser printStartChooser = DateChooser.getInstance("yyyy/MM/dd");
        DateChooser printEndChooser = DateChooser.getInstance("yyyy/MM/dd");
        printStartChooser.register(printStartDateValue);
        printEndChooser.register(printEndDateValue);

		String[] tableColumn = {"id", "", "单位", "所在产品库", "文件类型", "文件编号", "文件名称", "版本", "阶段标记", "密级", "分发状态", "打印时间", "打印人", "领取部门", "领取人", "领取时间", "转移后部门","object"};
		this.tableColumnClass = new Class[]{String.class, Boolean.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,Object.class};
		tableModel = new CommonTableModel();
		for (String columnName : tableColumn) {
			tableModel.addColumn(columnName);
		}
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(30);
		DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
		render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(4).setPreferredWidth(100);
		table.getColumnModel().getColumn(5).setPreferredWidth(120);
		table.getColumnModel().getColumn(6).setPreferredWidth(200);
		table.getColumnModel().getColumn(7).setPreferredWidth(80);
		table.getColumnModel().getColumn(8).setPreferredWidth(80);
		table.getColumnModel().getColumn(9).setPreferredWidth(80);
		table.getColumnModel().getColumn(10).setPreferredWidth(80);
		table.getColumnModel().getColumn(11).setPreferredWidth(120);
		table.getColumnModel().getColumn(12).setPreferredWidth(100);
		table.getColumnModel().getColumn(13).setPreferredWidth(100);
		table.getColumnModel().getColumn(14).setPreferredWidth(100);
		table.getColumnModel().getColumn(15).setPreferredWidth(100);
		JComboBox comboBox = new JComboBox(deptSeal);
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.SELECTED == e.getStateChange()){
					String dept = (String) e.getItem();
					for(int i = 0; i < tableModel.getRowCount(); i++) {
						boolean isSelect = (Boolean) table.getValueAt(i, 1);
						if(isSelect){
							table.setValueAt(dept,i,16);
						}
					}
				}
			}
		});
		table.getColumnModel().getColumn(16).setPreferredWidth(100);
		table.getColumnModel().getColumn(16).setCellEditor(new DefaultCellEditor(comboBox));
		table.getColumnModel().getColumn(17).setMaxWidth(0);
		table.getColumnModel().getColumn(17).setMinWidth(0);
		table.getColumnModel().getColumn(17).setPreferredWidth(0);
		table.getColumnModel().getColumn(17).setResizable(false);
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
		topPanel.add(fileNumberLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(productLabel, topGrid);
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
		topPanel.add(productValue, topGrid);
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
		topGrid.insets = new Insets(10, -100, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(fileNameLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(fileTypeLabel, topGrid);

		topGrid.gridx = 4;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 5);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(fileNameValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(fileTypeValue, topGrid);
		topGrid.gridy = 4;
		topGrid.insets = new Insets(10, -85, 10, 5);
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
		topPanel.add(receivePersonLabel, topGrid);

		topGrid.gridx = 7;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(receivePersonValue, topGrid);
		topGrid.gridy = 4;
		topGrid.insets = new Insets(10, -80, 10, 10);
		JPanel button = new JPanel();
		button.add(clearConditionButton);
		button.add(searchButton);
		button.add(closeButton);
		topPanel.add(button, topGrid);

		topGrid.gridx = 0;
		topGrid.gridy = 7;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 0);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(allCheck, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 7;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, -100, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(transferButton, topGrid);


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

        productValue.setPreferredSize(new Dimension(120, 25));
        fileTypeValue.setPreferredSize(new Dimension(120, 25));
        fileNumberValue.setPreferredSize(new Dimension(120, 25));
        fileNameValue.setPreferredSize(new Dimension(120, 25));
        printStartDateValue.setPreferredSize(new Dimension(120, 25));
		printEndDateValue.setPreferredSize(new Dimension(120, 25));
        receivePersonValue.setPreferredSize(new Dimension(120, 25));
        transferButton.setPreferredSize(new Dimension(120, 25));
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
							if("true".equals(String.valueOf(printDateCheck.isSelected()))){
								if("".equals(cmPrintRecordQueryBean.getPrintStartDate()) || null == cmPrintRecordQueryBean.getPrintStartDate()){
									JOptionPane.showMessageDialog(null, "打印开始时间为空！请正确选择。");
									return;
								}else if("".equals(cmPrintRecordQueryBean.getPrintEndDate()) || null == cmPrintRecordQueryBean.getPrintEndDate()){
									JOptionPane.showMessageDialog(null, "打印结束时间为空！请正确选择。");
									return;
								}
							}
							list = queryTransferInfo(cmPrintRecordQueryBean);
							for (CmPrintRecordInfoBean cmPrintRecordInfoBean1 : list) {
								//判断是否被中断
								if(isWindowClose){
									//处理中断逻辑
									break;
								}
								String barTableID = cmPrintRecordInfoBean1.getBarTableID();
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
								String printUser = cmPrintRecordInfoBean1.getPrintUser();
								Object[] value = {barTableID, false,unit, type, fileType, fileNumber, fileName, version, phaseCode, secret, distributeStatus, printDate,printUser,getDept,getUser,getDate,"",cmPrintRecordInfoBean1 };
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

		transferButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
				for (int i = 0; i < tableModel.getRowCount(); i++) {
					boolean isSelected = (Boolean) table.getValueAt(i, 1);
					if(isSelected){
						String afterDept = (String) table.getValueAt(i, 16);
						if(afterDept == null || "".equals(afterDept)){
							CommonUIUtil.showMessageDialog(null, "第"+(i+1)+"行编号为"+table.getValueAt(i,5)+"文件未设置转移部门，请先设置转移部门！");
							return;
						}
						CmPrintRecordInfoBean bean = (CmPrintRecordInfoBean) tableModel.getValueAt(i, 17);
						bean.setTargetDept(afterDept);
						list.add(bean);
					}
				}
				if(list.size()>20){
					CommonUIUtil.showMessageDialog(null, "单次转移文件数量不允许超过20！");
					return;
				}else if(list.size()==0){
					CommonUIUtil.showMessageDialog(null, "未选择任何文件！");
					return;
				}
				String process = MPMPrintProcessor.createPrintTransferProcess(list);
				CommonUIUtil.showMessageDialog(null, process);
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

		printDateCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if("false".equals(String.valueOf(printDateCheck.isSelected()))){
					printStartDateValue.setText("");
					printEndDateValue.setText("");
				}
			}
		});

		allCheck.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(allCheck.isSelected()){
					for(int i=0;i<table.getRowCount();i++){
						table.getModel().setValueAt(true, i, 1);
					}
				}else{
					for(int i=0;i<table.getRowCount();i++){
						table.getModel().setValueAt(false, i, 1);
					}
				}
			}
		});
	}

	public void initUI(){
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setTitle("工艺文件转移");
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	public void clearCondition(){
		productValue.setText("");
		fileTypeValue.setSelectedIndex(0);
		fileNumberValue.setText("");
		fileNameValue.setText("");
		printStartDateValue.setText("");
		printEndDateValue.setText("");
		receivePersonValue.setText("");
		printDateCheck.setSelected(false);
		printStartDateValue.setText("");
		printEndDateValue.setText("");
	}

	public CmPrintRecordQueryBean getConditionValues(){
		CmPrintRecordQueryBean cmPrintRecordQueryBean = new CmPrintRecordQueryBean();
		cmPrintRecordQueryBean.setUnit("厂内");
		cmPrintRecordQueryBean.setType(CommonUtil.objectToString(productValue.getText()));
		cmPrintRecordQueryBean.setFileType(CommonUtil.objectToString(fileTypeValue.getSelectedItem()));
		cmPrintRecordQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintRecordQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintRecordQueryBean.setDistributeStatus("已分发");
		cmPrintRecordQueryBean.setPrinter(currentUser.getName());
		if("true".equals(String.valueOf(printDateCheck.isSelected()))){
			cmPrintRecordQueryBean.setPrintStartDate(CommonUtil.objectToString(printStartDateValue.getText()));
			cmPrintRecordQueryBean.setPrintEndDate(CommonUtil.objectToString(printEndDateValue.getText()));
		}
		cmPrintRecordQueryBean.setGetUser(CommonUtil.objectToString(receivePersonValue.getText()));
		return cmPrintRecordQueryBean;
	}

	public List<CmPrintRecordInfoBean> queryTransferInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryTransferInfo(cmPrintRecordQueryBean);
	}

	public String getUserDepartment() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserDepartment();
	}
	public String getUserGroup() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserGroup();
	}

	class CommonTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if(tableColumnClass != null) {
				return tableColumnClass[columnIndex];
			} else {
				return super.getColumnClass(columnIndex);
			}
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			for(int i : editableColumns) {
				if(i == column) {
					return true;
				}
			}
			return false;
		}
	}
}
