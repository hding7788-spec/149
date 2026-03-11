package com.glaway.mpm.print.ui;

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
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;

public class InFactoryChangeRecover extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = -2363325427439538140L;
	private String infor;
	private String pboOid;
//	private JCheckBox checkBox1;
//	/* 复选框2 */
//	private JCheckBox checkBox2;
	/* 确定按钮 */
	private JButton okButton;
	/* 关闭按钮 */
	private JButton closeButton;
	/* 删除按钮 */
//	private JButton deleteButton;
	/* 添加按钮 */
	private JButton addButton;
	/* 无需回收按钮*/
	private JButton noNeedButton;
	/* 设为终止按钮*/
	private JButton setEndButton;
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
	private JPanel downPanel;
	private JScrollPane tablePanelFirst;
	private List<CmPrintRecordInfoBean> list;
	private List<String> strList;
	public InFactoryChangeRecover(String infor, String pboOid){
		this.infor = infor;
		this.pboOid = pboOid;
		initComponents();
		initLayout();
		initListener();
		initUI();
	}
	protected String Boolean(boolean b) {
		return null;
	}

	public void initComponents(){
			strList = new ArrayList<String>();
			strList = getList(infor);
			list = new ArrayList<CmPrintRecordInfoBean>();
//			checkBox1 = new JCheckBox();
//			checkBox1.setText("全选");
//			checkBox2 = new JCheckBox();

//			checkBox2.setText("全选");
			okButton = new JButton("确认");
			closeButton = new JButton("关闭");
//			deleteButton = new JButton("删除");
			addButton = new JButton("添加");
			noNeedButton = new JButton("无需回收");
			setEndButton = new JButton("设为终止");
			queryListTable = new JLabel("显示列表:");

			int width = Toolkit.getDefaultToolkit().getScreenSize().width;
			int height = Toolkit.getDefaultToolkit().getScreenSize().height;

			downPanel = new JPanel();
			topPanel = new JPanel();
			tablePanelFirst = new JScrollPane();
			tablePanelSecond = new JScrollPane();
			topPanel.setPreferredSize(new Dimension((int)(0.4*(width)), (int)(0.4*(height))));
			 String[] tableColumn1 = {"", "id", "文件编号", "文件名称", "版本", "阶段标记", "密级", "领取部门", "印章", "条码", "状态"};
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
				TableColumn tc = table1.getTableHeader().getColumnModel().getColumn(0);
				tc.setMaxWidth(0);
				tc.setPreferredWidth(0);
				tc.setWidth(0);
				tc.setMinWidth(0);
				table1.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
				table1.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0);
				tableColumnModel1.getColumn(1).setMaxWidth(0);
				tableColumnModel1.getColumn(1).setMinWidth(0);
				tableColumnModel1.getColumn(1).setPreferredWidth(0);
				tableColumnModel1.getColumn(1).setResizable(false);
				tableColumnModel1.getColumn(2).setPreferredWidth(200);
				tableColumnModel1.getColumn(3).setPreferredWidth(250);
				tableColumnModel1.getColumn(4).setPreferredWidth(50);
				tableColumnModel1.getColumn(5).setPreferredWidth(30);
				tableColumnModel1.getColumn(6).setPreferredWidth(50);
				tableColumnModel1.getColumn(7).setPreferredWidth(50);
				tableColumnModel1.getColumn(8).setPreferredWidth(80);
				tableColumnModel1.getColumn(9).setPreferredWidth(200);
				tableColumnModel1.getColumn(10).setPreferredWidth(80);


				tablePanelFirst.setViewportView(table1);

				 String[] tableColumn2 = {"", "id", "uuid", "文件编号", "文件名称", "版本", "阶段标记", "密级", "领取部门", "条码","状态"};
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
					TableColumn tc2 = table2.getTableHeader().getColumnModel().getColumn(0);
					tc2.setMaxWidth(0);
					tc2.setPreferredWidth(0);
					tc2.setWidth(0);
					tc2.setMinWidth(0);
					table2.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
					table2.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0);
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
					tableColumnModel2.getColumn(5).setPreferredWidth(50);
					tableColumnModel2.getColumn(6).setPreferredWidth(30);
					tableColumnModel2.getColumn(7).setPreferredWidth(50);
					tableColumnModel2.getColumn(8).setPreferredWidth(50);
					tableColumnModel2.getColumn(9).setPreferredWidth(200);
					tableColumnModel1.getColumn(10).setPreferredWidth(80);

					try {
						list = getQueryFileInfoByChange(strList);
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
							String state = cmPrintRecordInfoBean.getFileState();
							String[] value = {Boolean(false), id, fileNumber, fileName, version, phaseCode, secret, getDept, batch, barCode, state};
							tableModel1.addRow(value);
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}


					tablePanelSecond.setViewportView(table2);
			}
//	}

	public void initLayout(){
		if(downPanel == null){
			return;
		}
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

//		downGrid.gridx = 0;
//		downGrid.gridy = 2;
//		downGrid.gridwidth = 1;
//		downGrid.gridheight = 1;
//		downGrid.weightx = 1;
//		downGrid.weighty = 1;
//		downGrid.fill = GridBagConstraints.NORTHWEST;
//		downGrid.anchor = GridBagConstraints.NORTHWEST;
//		downGrid.insets = new Insets(0, 5, 5, 5);
//		downPanel.add(checkBox1, downGrid);

		downGrid.gridx = 8;
		downGrid.gridy = 2;
		downGrid.gridwidth = 1;
		downGrid.gridheight = 1;
		downGrid.fill = GridBagConstraints.NORTHEAST;
		downGrid.insets = new Insets(5, 600, 5, 5);
		downPanel.add(addButton, downGrid);

		downGrid.gridx = 7;
		downGrid.gridy = 2;
		downGrid.gridwidth = 1;
		downGrid.gridheight = 1;
		downGrid.fill = GridBagConstraints.NORTHEAST;
		downGrid.insets = new Insets(5, 0, 5, 5);
		downPanel.add(noNeedButton, downGrid);

		downGrid.gridx = 6;
		downGrid.gridy = 2;
		downGrid.gridwidth = 1;
		downGrid.gridheight = 1;
		downGrid.fill = GridBagConstraints.NORTHEAST;
		downGrid.insets = new Insets(5, 15, 5, 5);
		downPanel.add(setEndButton, downGrid);

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

//		topGrid.gridx =0;
//		topGrid.gridy = 1;
//		topGrid.gridwidth = 1;
//		topGrid.gridheight = 1;
//		topGrid.fill = GridBagConstraints.NORTHWEST;
//		topGrid.anchor = GridBagConstraints.NORTHWEST;
//		topPanel.add(checkBox2, topGrid);

		topGrid.gridx = 6;
		topGrid.gridy = 1;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.fill = GridBagConstraints.NORTHWEST;
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topGrid.insets = new Insets(5, 300, 5, 5);
		topPanel.add(okButton, topGrid);

//		topGrid.gridx = 7;
//		topGrid.gridy = 1;
//		topGrid.gridwidth = 1;
//		topGrid.gridheight = 1;
//		topGrid.anchor = GridBagConstraints.NORTHWEST;
//		topGrid.anchor = GridBagConstraints.NORTHWEST;
//		topGrid.insets = new Insets(5, 50, 5, 5);
//		topPanel.add(deleteButton, topGrid);

		topGrid.gridx = 8;
		topGrid.gridy = 1;
		topGrid.gridwidth = 1;
		topGrid.gridheight = 1;
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topGrid.insets = new Insets(5, 15, 5, 5);
		topPanel.add(closeButton, topGrid);

		this.add(topPanel);
		this.add(downPanel);
		SpringLayout springLayout = new SpringLayout();
		springLayout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, downPanel, 0, SpringLayout.SOUTH, topPanel);
		springLayout.putConstraint(SpringLayout.WEST , downPanel, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, downPanel, 0, SpringLayout.EAST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, downPanel, 0, SpringLayout.SOUTH, this.getContentPane());
		this.setLayout(springLayout);

//		checkBox1.setPreferredSize(new Dimension(100, 25));
//		checkBox2.setPreferredSize(new Dimension(100, 25));
		okButton.setPreferredSize(new Dimension(100, 25));
		closeButton.setPreferredSize(new Dimension(100, 25));
//		deleteButton.setPreferredSize(new Dimension(100, 25));
		addButton.setPreferredSize(new Dimension(100, 25));
		setEndButton.setEnabled(false);
		noNeedButton.setEnabled(false);
		addButton.setEnabled(false);

		boolean distribution = true;//是否全部是已下发
		boolean printState = true;
		List<String> stateList = new ArrayList<String>();
		boolean otherState = false;//其他状态
		//判断是否全部已下发
		if(list == null || list.isEmpty()){
			setEndButton.setEnabled(false);
			noNeedButton.setEnabled(true);
			addButton.setEnabled(false);
		}else{
			for(CmPrintRecordInfoBean bean : list){
				String state = bean.getFileState();
				if(!stateList.contains(state)){
					stateList.add(state);
				}
				if(!state.equals("已下发")){//是否全部是已下发
					distribution = false;
				}
				if(!state.equals("已打印") && !state.equals("未打印")){//是否全部是已打印或者未打印
					printState = false;
				}
			}
			if(!stateList.contains("已下发") && !stateList.contains("已打印") && !stateList.contains("未打印")){
				otherState = true;
			}
			if(distribution){
				setEndButton.setEnabled(false);
				noNeedButton.setEnabled(false);
				addButton.setEnabled(true);
			}else if(printState){
				setEndButton.setEnabled(true);
				noNeedButton.setEnabled(false);
				addButton.setEnabled(false);
			}else if(otherState){
				setEndButton.setEnabled(false);
				noNeedButton.setEnabled(true);
				addButton.setEnabled(false);
			}else{
				setEndButton.setEnabled(false);
				noNeedButton.setEnabled(false);
				addButton.setEnabled(false);
			}
		}
	}
	public void initListener(){


//		checkBox1.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if(checkBox1.isSelected()){
//					for(int i=0;i<table1.getRowCount();i++){
//						table1.getModel().setValueAt(true, i, 0);
//					}
//				}else{
//					for(int i=0;i<table1.getRowCount();i++){
//						table1.getModel().setValueAt(false, i, 0);
//					}
//				}
//			}
//		});

//		checkBox2.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if(checkBox2.isSelected()){
//					for(int i=0;i<table2.getRowCount();i++){
//						table2.getModel().setValueAt(true, i, 0);
//					}
//				}else{
//					for(int i=0;i<table2.getRowCount();i++){
//						table2.getModel().setValueAt(false, i, 0);
//					}
//				}
//			}
//		});

		noNeedButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setProcessState("false");
				JOptionPane.showMessageDialog(getContentPane(), "设置成功");
				noNeedButton.setEnabled(false);
			}

		});

		setEndButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setFileState();
				setProcessState("false");
				JOptionPane.showMessageDialog(getContentPane(), "设置成功");
				setEndButton.setEnabled(false);
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
					String state = cmPrintRecordInfoBean.getFileState();
					String[] value = {Boolean(false), id, uuid, fileNumber, fileName, docVersion, phaseCode, secret, getDept, barCode, state};
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

//		deleteButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				int tableRow = tableModel2.getRowCount();
//				for(int i = 0; i < tableModel2.getRowCount(); i ++){
//					if("true".equals(String.valueOf(tableModel2.getValueAt(i, 0)))){
//						tableModel2.removeRow(i);
//						i--;
//					}
//
//				}
//				int tableNowRow = tableModel2.getRowCount();
//				if(tableRow == tableNowRow){
//					JOptionPane.showMessageDialog(getContentPane(), "未选择需要删除的行！");
//				}
//			}
//		});

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
								cmPrintRecordInfoBean.setPboOid(pboOid);
								uuidList.add(uuidValue);
								listBean.add(cmPrintRecordInfoBean);
//							}
					}
					if(listBean.size() > 0){
						setProcessState("true");
						Boolean flag = insertChangeRecoverToDB(listBean);
						if(flag){
							JOptionPane.showMessageDialog(getContentPane(), "成功选择回收文件！");
						}else{
							JOptionPane.showMessageDialog(getContentPane(), "所需回收文件选择失败！");
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
	protected void setFileState() {
		MPMPrintProcessor.setChangeFileState(strList);

	}
	protected void setProcessState(String state) {
		MPMPrintProcessor.setProcessState(pboOid, state);

	}
	public void initUI(){
		this.setTitle("更改文件回收确认");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	public List<CmPrintRecordInfoBean> getTableInfo(JTable table){
		   List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		   for (int i = 0; i < table.getRowCount(); i++) {
//			   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
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
			       cmPrintRecordInfoBean.setFileState((String)(table.getModel().getValueAt(i, 10)));
				   list.add(cmPrintRecordInfoBean);
//			   }
		   }
		   return list;

	}
	public static List<CmPrintRecordInfoBean> getQueryFileInfoByChange(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryFileInfoByChange(list);
	}
	public String getUserName() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserName();
	}
	public String getUserDepartment() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserDepartment();
	}
	public Boolean insertChangeRecoverToDB(List<CmPrintRecordInfoBean> listBean) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.insertChangeRecoverToDB(listBean);
	}
	public String getCurrentTime(){
		Date nowDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String time = sdf.format(nowDate);
		return time;
	}
	public List<String> getList(String str)
	{
		List<String> list = new ArrayList<String>();
		String[] temp = str.split(":");
		for (String string : temp) {
			list.add(string);
		}
	    return list;
	}

}
