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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.table.TableColumnModel;

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.service.PrintToWCIntf;

public class InFactoryPaperRecoverBySelfMainPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 7334987966205932192L;
	private static final String NAME = "自行发起回收";
	/* 复选框1 */
	private JCheckBox checkBox1;
	/* 复选框2 */
	private JCheckBox checkBox2;
	/* 确定按钮 */
	private JButton okButton;
	/* 关闭按钮 */
	private JButton closeButton;
	/* 删除按钮 */
	private JButton deleteButton;
	/* 清空搜索条件按钮 */
	private JButton clearConditionButton;
	/* 选择按钮 */
	private JButton selectButton;
	/* 添加按钮 */
	private JButton addButton;
	/* 文件编号 */
	private JLabel fileNumber;
	/* 文件编号-文本框 */
	private JTextField fileNumberValue;
	/* 文件名称 */
	private JLabel fileName;
	/* 文件名称-文本框 */
	private JTextField fileNameValue;
	/* 查询列表 */
	private JTable table1;
	/* 查询列表模型 */
	private DefaultTableModel tableModel1;
	/* 添加列表 */
	private JTable table2 = new JTable();
	/* 添加列表模型 */
	private DefaultTableModel tableModel2 ;

	private JLabel queryListTable;

	private JPanel topPanel;
	private JScrollPane tablePanelSecond;
	private JPanel toolPanelSecond;
	private JPanel centerPanel;
	private JPanel downPanel;
	private JScrollPane tablePanelFirst;
	private JPanel toolPanelFirst;
	private List<CmPrintRecordInfoBean> list;

	public InFactoryPaperRecoverBySelfMainPanel(){
		initComponents();
		initLayout();
		initListener();
		initUI();
	}

	protected String Boolean(boolean b) {
		return null;
	}

	public void clearCondition(){
		fileNumberValue.setText("");
		fileNameValue.setText("");

	}
	public void initComponents(){
		list = new ArrayList<CmPrintRecordInfoBean>();
		checkBox1 = new JCheckBox();
		checkBox1.setText("全选");
		checkBox2 = new JCheckBox();

		checkBox2.setText("全选");
		okButton = new JButton("确认");
		closeButton = new JButton("关闭");
		deleteButton = new JButton("删除");
		clearConditionButton = new JButton("清空搜索条件");
		selectButton = new JButton("查询");
		addButton = new JButton("添加");
		fileNumber = new JLabel("文件编号:");
		fileNumberValue = new JTextField();
		fileName = new JLabel("文件名称:");
		fileNameValue = new JTextField();
		queryListTable = new JLabel("查询列表:");

		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		downPanel = new JPanel();
		centerPanel = new JPanel();
		topPanel = new JPanel();
		tablePanelFirst = new JScrollPane();
		tablePanelSecond = new JScrollPane();
		toolPanelFirst = new JPanel();
		toolPanelSecond = new JPanel();
		topPanel.setPreferredSize(new Dimension((int)(0.4*(width)), (int)(0.4*(height))));
		 String[] tableColumn1 = {"", "id", "文件编号", "文件名称", "版本", "阶段标记", "密级", "领取部门", "印章", "条码"};
			String[][] tableData1 = {};
			tableModel1 = new DefaultTableModel(tableData1,tableColumn1)
			{
				private static final long serialVersionUID = -5817421228650378228L;

				public boolean isCellEditable(int row, int column) {
					if(column == 0){
						return true;
					}else{
						return false;
					}
				}

			};
			table1 = new JTable(tableModel1);
			table1.getTableHeader().setReorderingAllowed(false);
			table1.getTableHeader().setPreferredSize(new Dimension(table1.getTableHeader().getWidth(), 20));
			table1.setRowHeight(30);
			DefaultTableCellRenderer render1 = (DefaultTableCellRenderer) table1.getDefaultRenderer(getClass());
			render1.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

			TableColumnModel tableColumnModel1 = table1.getColumnModel();
			tableColumnModel1.getColumn(0).setCellEditor(table1.getDefaultEditor(Boolean.class));
			tableColumnModel1.getColumn(0).setCellRenderer(table1.getDefaultRenderer(Boolean.class));
			tableColumnModel1.getColumn(0).setPreferredWidth(30);
			tableColumnModel1.getColumn(1).setMaxWidth(0);
			tableColumnModel1.getColumn(1).setMinWidth(0);
			tableColumnModel1.getColumn(1).setPreferredWidth(0);
			tableColumnModel1.getColumn(1).setResizable(false);
			tableColumnModel1.getColumn(2).setPreferredWidth(200);
			tableColumnModel1.getColumn(3).setPreferredWidth(250);
			tableColumnModel1.getColumn(4).setPreferredWidth(80);
			tableColumnModel1.getColumn(5).setPreferredWidth(50);
			tableColumnModel1.getColumn(6).setPreferredWidth(80);
			tableColumnModel1.getColumn(7).setPreferredWidth(80);
			tableColumnModel1.getColumn(8).setPreferredWidth(150);
			tableColumnModel1.getColumn(9).setPreferredWidth(200);


			tablePanelFirst.setViewportView(table1);

			 String[] tableColumn2 = {"", "id", "uuid", "文件编号", "文件名称", "版本", "阶段标记", "密级", "领取部门", "条码"};
				String[][] tableData2 = {};
				tableModel2 = new DefaultTableModel(tableData2,tableColumn2)
				{
					private static final long serialVersionUID = -5817421228650378228L;
					public boolean isCellEditable(int row, int column) {
						if(column == 0){
							return true;
						}else{
							return false;
						}
					}
				};
				table2 = new JTable(tableModel2);
				table2.getTableHeader().setReorderingAllowed(false);
				table2.getTableHeader().setPreferredSize(new Dimension(table2.getTableHeader().getWidth(), 20));
				table2.setRowHeight(30);
				DefaultTableCellRenderer render2 = (DefaultTableCellRenderer) table2.getDefaultRenderer(getClass());
				render2.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

				TableColumnModel tableColumnModel2 = table2.getColumnModel();
				tableColumnModel2.getColumn(0).setCellEditor(table2.getDefaultEditor(Boolean.class));
				tableColumnModel2.getColumn(0).setCellRenderer(table2.getDefaultRenderer(Boolean.class));
				tableColumnModel2.getColumn(0).setPreferredWidth(30);
				tableColumnModel2.getColumn(1).setMaxWidth(0);
				tableColumnModel2.getColumn(1).setMinWidth(0);
				tableColumnModel2.getColumn(1).setPreferredWidth(0);
				tableColumnModel2.getColumn(1).setResizable(false);
				tableColumnModel2.getColumn(2).setMaxWidth(0);
				tableColumnModel2.getColumn(2).setMinWidth(0);
				tableColumnModel2.getColumn(2).setPreferredWidth(0);
				tableColumnModel2.getColumn(2).setResizable(false);
				tableColumnModel2.getColumn(3).setPreferredWidth(200);
				tableColumnModel2.getColumn(4).setPreferredWidth(250);
				tableColumnModel2.getColumn(5).setPreferredWidth(100);
				tableColumnModel2.getColumn(6).setPreferredWidth(50);
				tableColumnModel2.getColumn(7).setPreferredWidth(80);
				tableColumnModel2.getColumn(8).setPreferredWidth(100);
				tableColumnModel2.getColumn(9).setPreferredWidth(200);

				tablePanelSecond.setViewportView(table2);
	}

	public void initLayout(){
		downPanel.setLayout(new GridBagLayout());
		GridBagConstraints downGrid = new GridBagConstraints();
		downGrid.gridx = 0;
		downGrid.gridy = 0;
		downGrid.gridwidth = 9;
		downGrid.gridheight = 1;
		downGrid.weightx = 1;
		downGrid.weighty = 1;
		downGrid.fill = GridBagConstraints.NORTHWEST;
		downGrid.anchor = GridBagConstraints.NORTHWEST;
		downGrid.insets = new Insets(5, 5, -50, 5);
		downPanel.add(queryListTable, downGrid);

		downGrid.gridx = 0;
		downGrid.gridy = 1;
		downGrid.gridwidth = 9;
		downGrid.gridheight = 1;
		downGrid.weightx = 1;
		downGrid.weighty = 1;
		downGrid.fill = GridBagConstraints.BOTH;
		downGrid.insets = new Insets(0, 5, 0, 5);
		downPanel.add(tablePanelFirst, downGrid);

		downGrid.gridx = 0;
		downGrid.gridy = 2;
		downGrid.gridwidth = 1;
		downGrid.gridheight = 1;
		downGrid.weightx = 1;
		downGrid.weighty = 1;
		downGrid.fill = GridBagConstraints.NORTHWEST;
		downGrid.anchor = GridBagConstraints.NORTHWEST;
		downGrid.insets = new Insets(0, 5, 5, 5);
		downPanel.add(checkBox1, downGrid);

		downGrid.gridx = 8;
		downGrid.gridy = 2;
		downGrid.gridwidth = 1;
		downGrid.gridheight = 1;
		downGrid.fill = GridBagConstraints.NORTHEAST;
		downGrid.insets = new Insets(5, 600, 5, 5);
		downPanel.add(addButton, downGrid);

		centerPanel.setLayout(new GridBagLayout());
		GridBagConstraints centerGrid = new GridBagConstraints();
		centerGrid.gridx = 0;
		centerGrid.gridy = 0;
		centerGrid.weightx = 1;
		centerGrid.weighty = 1;
		centerGrid.insets = new Insets(5, 5, 50, 5);
		centerGrid.anchor = GridBagConstraints.NORTHEAST;
		centerPanel.add(fileNumber, centerGrid);

		centerGrid.gridx = 1;
		centerGrid.insets = new Insets(5, 5, 50, -100);
		centerGrid.anchor = GridBagConstraints.NORTHWEST;
		centerPanel.add(fileNumberValue, centerGrid);

		centerGrid.gridx = 2;
		centerGrid.insets = new Insets(5, -100, 50, 5);
		centerGrid.anchor = GridBagConstraints.NORTHEAST;
		centerPanel.add(fileName, centerGrid);

		centerGrid.gridx = 3;
		centerGrid.insets = new Insets(5, 5, 50, 5);
		centerGrid.anchor = GridBagConstraints.NORTHWEST;
		centerPanel.add(fileNameValue, centerGrid);

		centerGrid.gridx = 4;
		centerGrid.insets = new Insets(5, -20, 50, 5);
		centerPanel.add(clearConditionButton, centerGrid);

		centerGrid.gridx = 5;
		centerGrid.insets = new Insets(5, -80, 50, 5);
		centerPanel.add(selectButton, centerGrid);

		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();
		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.gridwidth = 10;
		topGrid.gridheight = 1;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.fill = GridBagConstraints.BOTH;
		topGrid.insets = new Insets(5, 5, 5, 5);
		topPanel.add(tablePanelSecond, topGrid);

		topGrid.gridx =0;
		topGrid.gridy = 1;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.fill = GridBagConstraints.NORTHWEST;
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(checkBox2, topGrid);

		topGrid.gridx = 6;
		topGrid.gridy = 1;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.fill = GridBagConstraints.NORTHWEST;
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topGrid.insets = new Insets(5, 300, 5, 5);
		topPanel.add(okButton, topGrid);

		topGrid.gridx = 7;
		topGrid.gridy = 1;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topGrid.insets = new Insets(5, 50, 5, 5);
		topPanel.add(deleteButton, topGrid);

		topGrid.gridx = 8;
		topGrid.gridy = 1;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topGrid.insets = new Insets(5, 0, 5, 5);
		topPanel.add(closeButton, topGrid);

		this.add(topPanel);
		this.add(centerPanel);
		this.add(downPanel);
		SpringLayout springLayout = new SpringLayout();
		springLayout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, centerPanel, -5, SpringLayout.SOUTH, topPanel);
		springLayout.putConstraint(SpringLayout.WEST, centerPanel, 0, SpringLayout.WEST , this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, centerPanel, 0, SpringLayout.EAST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, downPanel, 0, SpringLayout.SOUTH, centerPanel);
		springLayout.putConstraint(SpringLayout.WEST , downPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, downPanel, 0, SpringLayout.EAST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, downPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		this.setLayout(springLayout);

		checkBox1.setPreferredSize(new Dimension(100, 25));
		checkBox2.setPreferredSize(new Dimension(100, 25));
		okButton.setPreferredSize(new Dimension(100, 25));
		closeButton.setPreferredSize(new Dimension(100, 25));
		deleteButton.setPreferredSize(new Dimension(100, 25));
		clearConditionButton.setPreferredSize(new Dimension(120, 25));
		selectButton.setPreferredSize(new Dimension(100, 25));
		addButton.setPreferredSize(new Dimension(100, 25));
		fileNumber.setPreferredSize(new Dimension(80, 25));
		fileNumberValue.setPreferredSize(new Dimension(150, 25));
	    fileName.setPreferredSize(new Dimension(80, 25));
		fileNameValue.setPreferredSize(new Dimension(150, 25));
	}
	public void initListener(){
		clearConditionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearCondition();
			}
		});
		selectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel1.setRowCount(0);
				String number = fileNumberValue.getText();
				String name = fileNameValue.getText();
				try {
					list = getQueryFileInfo(number, name);
					for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
						String id = cmPrintRecordInfoBean.getBarTableID();
						String fileNumber = cmPrintRecordInfoBean.getFileNumber();
						String fileName = cmPrintRecordInfoBean.getFileName();
						String version = cmPrintRecordInfoBean.getDocVersion();
						String phaseCode = cmPrintRecordInfoBean.getPhaseCode();
						String secret = cmPrintRecordInfoBean.getSecret();
						String getDept = cmPrintRecordInfoBean.getGetDept();
						String batch = cmPrintRecordInfoBean.getBatch();
						String barCode = cmPrintRecordInfoBean.getBarCode();
						String[] value = {Boolean(false), id, fileNumber, fileName, version, phaseCode, secret, getDept, batch, barCode};
						tableModel1.addRow(value);
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
			}
		});

		checkBox1.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(checkBox1.isSelected()){
					for(int i=0;i<table1.getRowCount();i++){
						table1.getModel().setValueAt(true, i, 0);
					}
				}else{
					for(int i=0;i<table1.getRowCount();i++){
						table1.getModel().setValueAt(false, i, 0);
					}
				}
			}
		});

		checkBox2.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(checkBox2.isSelected()){
					for(int i=0;i<table2.getRowCount();i++){
						table2.getModel().setValueAt(true, i, 0);
					}
				}else{
					for(int i=0;i<table2.getRowCount();i++){
						table2.getModel().setValueAt(false, i, 0);
					}
				}
			}
		});

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel2.setRowCount(0);
				List<CmPrintRecordInfoBean> list = getTableInfo(table1);
				if(list.size() > 0){
					for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
					String id = cmPrintRecordInfoBean.getBarTableID();
					String uuid = UUID.randomUUID().toString();
				    String fileNumber = cmPrintRecordInfoBean.getFileNumber();
					String fileName = cmPrintRecordInfoBean.getFileName();
					String docVersion = cmPrintRecordInfoBean.getDocVersion();
					String phaseCode = cmPrintRecordInfoBean.getPhaseCode();
					String secret = cmPrintRecordInfoBean.getSecret();
					String getDept = cmPrintRecordInfoBean.getGetDept();
					String barCode = cmPrintRecordInfoBean.getBarCode();
					String[] value = {Boolean(false), id, uuid, fileNumber, fileName, docVersion, phaseCode, secret, getDept, barCode};
					tableModel2.addRow(value);
				}
			}else{
				JOptionPane.showMessageDialog(getContentPane(), "未选择需要添加的任意一行！");
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

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < tableModel2.getRowCount(); i ++){
					if("true".equals(String.valueOf(tableModel2.getValueAt(i, 0)))){
						tableModel2.removeRow(i);
						i--;
					}

				}
			}
		});

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<CmPrintRecordInfoBean>  listBean= new ArrayList<CmPrintRecordInfoBean>();
				List<String> uuidList = new ArrayList<String>();
				try {
					String userName = getUserName();
					String userDept = getUserDepartment();
					String time = getCurrentTime();
					int tableRow = tableModel2.getRowCount();
						for(int i = 0; i < tableRow; i ++){
//							if("true".equals(String.valueOf((tableModel2.getValueAt(i, 0))))){
								CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
								cmPrintRecordInfoBean.setBarTableID((String)tableModel2.getValueAt(i, 1));
								String uuidValue = (String)tableModel2.getValueAt(i, 2);
								cmPrintRecordInfoBean.setUuid((String)tableModel2.getValueAt(i, 2));
								cmPrintRecordInfoBean.setFileNumber((String)tableModel2.getValueAt(i, 3));
								cmPrintRecordInfoBean.setBarCode((String)tableModel2.getValueAt(i, tableModel2.getColumnCount() - 1));
								cmPrintRecordInfoBean.setCurrentUser(userName);
								cmPrintRecordInfoBean.setCurrentDept(userDept);
								cmPrintRecordInfoBean.setCurrentDate(time);
								uuidList.add(uuidValue);
								listBean.add(cmPrintRecordInfoBean);
//							}
					}
					if(listBean.size() > 0){
						String str;
						Boolean processFlag;
						try {
							str = getSaveToDBOfRecover(listBean);
							if("".equals(str) || null == str){
								processFlag = startProcessOfRecover(NAME, uuidList);
								if(processFlag == true){
									JOptionPane.showMessageDialog(getContentPane(), "已成功启动回收流程！");
									dispose();
								}else{
									JOptionPane.showMessageDialog(getContentPane(), "流程启动失败！");
								}
							}else{
								JOptionPane.showMessageDialog(getContentPane(), "所选择需要回收的文件编号为" + str + "存在重复发起回收,请重新发起！");
								return;
							}

						} catch (RemoteException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}

					}
					else{
						JOptionPane.showMessageDialog(getContentPane(), "未选择相关需要回收的文件！");
					}
				} catch (RemoteException e2) {
					e2.printStackTrace();
				} catch (InvocationTargetException e2) {
					e2.printStackTrace();
				}
			}
		});
	}
	public void initUI(){
		this.setTitle("自行发起回收");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	public List<CmPrintRecordInfoBean> getTableInfo(JTable table){
		   List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		   for (int i = 0; i < table.getRowCount(); i++) {
			   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
			       CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
			       cmPrintRecordInfoBean.setBarTableID((String)(table.getModel().getValueAt(i, 1)));
			       cmPrintRecordInfoBean.setFileNumber((String)(table.getModel().getValueAt(i, 2)));
			       cmPrintRecordInfoBean.setFileName((String)(table.getModel().getValueAt(i, 3)));
			       cmPrintRecordInfoBean.setDocVersion((String)(table.getModel().getValueAt(i, 4)));
			       cmPrintRecordInfoBean.setPhaseCode((String)(table.getModel().getValueAt(i, 5)));
			       cmPrintRecordInfoBean.setSecret((String)(table.getModel().getValueAt(i, 6)));
			       cmPrintRecordInfoBean.setGetDept((String)(table.getModel().getValueAt(i, 7)));
			       cmPrintRecordInfoBean.setBatch((String)(table.getModel().getValueAt(i, 8)));
			       cmPrintRecordInfoBean.setBarCode((String)(table.getModel().getValueAt(i, 9)));
				   list.add(cmPrintRecordInfoBean);
			   }
		   }
		   return list;

	}
	public Boolean startProcessOfRecover(String name, List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.startProcessOfRecover(name, list);
	}
	public static List<CmPrintRecordInfoBean> getQueryFileInfo(String fileNumber, String fileName) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryPaperFileInfo(fileNumber, fileName);
	}
	public String getSaveToDBOfRecover(List<CmPrintRecordInfoBean> listBean) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.saveToDBOfRecover(listBean);
	}
	public String getUserName() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserName();
	}
	public String getUserDepartment() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserDepartment();
	}
	public String getCurrentTime(){
		Date nowDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String time = sdf.format(nowDate);
		return time;
	}
}
