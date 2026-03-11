package com.glaway.mpm.print.ui;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.JTextField;
import javax.swing.JToolBar;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;



import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.util.DateChooser;

public class SelectDelayRecoverFilePanel extends JFrame{

/**
	 *
	 */
	private static final long serialVersionUID = 2566261181320146745L;
	//	/** 单位 */
//	private JLabel unitLabel;
//	/** 型号 */
//	private JLabel typeLabel;
	private static final String NAME = "直接延迟回收";
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
	/** 打印时间 */
	private JLabel printDateLabel;
	/** 领取时间 */
	private JLabel receiveDateLabel;
	/** 领取部门 */
	private JLabel receiveDepartLabel;
	/** 领取人 */
	private JLabel receivePersonLabel;



//	/** 单位--下拉框 */
//	private JComboBox unitValue;
//	/** 型号--文本框*/
//	private JTextField typeValue;

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
	/** 打印时间 */
	private JTextField printDateValue;
	/** 领取时间*/
	private JTextField receiveDateValue;
	/** 领取部门--文本框 */
	private JTextField receiveDepartValue;
	/** 领取人--文本框 */
	private JTextField receivePersonValue;
	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;
	/** 提交延迟回收文件 */
	private JButton submitButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private JPanel topPanel = new JPanel();
	private JScrollPane downPanel = new JScrollPane();

	private List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
	private List<String> list = new ArrayList<String>();
	private List<String> uuidList = new ArrayList<String>();
	public JButton getClearConditionButton() {
		return clearConditionButton;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public SelectDelayRecoverFilePanel() {


		String[] fileType = LoadPrintConfigurations.getInstance().getFileTypeSeal();
		String[] distribute = LoadPrintConfigurations.getInstance().getDistributeSeal();

//		unitLabel = new JLabel("单位 :");
//		typeLabel = new JLabel("型号 :");

		fileNumberLabel = new JLabel("文件编号 :");
		fileNameLabel = new JLabel("文件名称 :");
		fileTypeLabel = new JLabel("文件类型 :");
		distributeLabel = new JLabel("分发状态 :");
		fileVersionLabel = new JLabel("版本:");
		printDateLabel = new JLabel("打印时间 :");
		receiveDateLabel = new JLabel("领取时间 :");
		receiveDepartLabel = new JLabel("领取部门 :");
		receivePersonLabel = new JLabel("领取人 :");

//		unitValue = new JComboBox(unitTypeSeal);
//		unitValue.setSelectedIndex(1);
//		typeValue = new JTextField();

		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		fileTypeValue = new JComboBox(fileType);
		distributeValue = new JComboBox(distribute);
		distributeValue.setSelectedIndex(6);
		distributeValue.setEditable(false);
//		distributeValue.setEnabled(false);
		fileVersionValue = new JTextField();
		printDateValue = new JTextField();
		receiveDateValue = new JTextField();
		receiveDepartValue = new JTextField();
		receivePersonValue = new JTextField();
		clearConditionButton = new JButton("清空搜索条件");
		searchButton = new JButton("查询");
		submitButton = new JButton("提交延迟回收文件");
		clearConditionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearCondition();
			}
		});

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.setRowCount(0);

				CmPrintRecordInfoBean cmPrintRecordQueryBean = getConditionValues();
				String delayStatus = cmPrintRecordQueryBean.getDistributeStatus();

				try {
					list = getQueryRecoverBarcode(delayStatus);
					listBean = getQueryDelayInfo(cmPrintRecordQueryBean, list);
					for (CmPrintRecordInfoBean cmPrintRecordQueryBean1 : listBean) {
						String uuid = cmPrintRecordQueryBean1.getUuid();
						uuidList.add(uuid);
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
						String distributeStatus = (cmPrintRecordQueryBean1.getDistributeStatus() == "1") ? "已回收" : "延迟回收";
						String barCode = cmPrintRecordQueryBean1.getBarCode();

						String[] value = {fileNumber, fileName, docVersion, type, fileType, secret, getDept, applyUser, applyDate, printUser, printDate, getUser, getDate, distributeStatus, barCode};
						tableModel.addRow(value);
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
		}
		});

	        submitButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Boolean flag = startProcessOfRecover(NAME, uuidList);
						if(flag){
							JOptionPane.showMessageDialog(null, "延迟流程启动成功!");
						}else{
							JOptionPane.showMessageDialog(null, "延迟流程启动失败!");
						}
					} catch (RemoteException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
			});


        DateChooser printDateValueChooser = DateChooser.getInstance("yyyy/MM/dd");
        DateChooser receiveDateValueChooser = DateChooser.getInstance("yyyy/MM/dd");

        printDateValueChooser.register(printDateValue);
        receiveDateValueChooser.register(receiveDateValue);


		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();

		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(5, 10, 5, 15);
		topGrid.anchor = GridBagConstraints.WEST;
		topPanel.add(fileNumberLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(fileNameLabel, topGrid);
		topGrid.gridy = 4;
		topPanel.add(fileTypeLabel, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		topPanel.add(fileNumberValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(fileNameValue, topGrid);
		topGrid.gridy = 4;
		topPanel.add(fileTypeValue, topGrid);

		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.anchor = GridBagConstraints.WEST;
		topPanel.add(distributeLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(fileVersionLabel, topGrid);
		topGrid.gridy = 4;
		topPanel.add(printDateLabel, topGrid);


		topGrid.gridx = 4;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		topPanel.add(distributeValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(fileVersionValue, topGrid);
		topGrid.gridy = 4;
		topPanel.add(printDateValue, topGrid);

		topGrid.gridx = 6;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.anchor = GridBagConstraints.WEST;
		topPanel.add(receiveDateLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(receiveDepartLabel, topGrid);
		topGrid.gridy = 4;
		topPanel.add(receivePersonLabel, topGrid);


		topGrid.gridx = 7;
		topGrid.gridy = 0;
		topGrid.anchor = GridBagConstraints.EAST;
		topPanel.add(receiveDateValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(receiveDepartValue, topGrid);
		topGrid.gridy = 4;
		topPanel.add(receivePersonValue, topGrid);


		topGrid.gridx = 8;
		topGrid.gridy = 2;
		topPanel.add(clearConditionButton, topGrid);

		topGrid.gridx = 9;
		topGrid.gridy = 2;
		topPanel.add(searchButton, topGrid);
		topPanel.setPreferredSize(new Dimension(100, 300));



//        unitValue.setPreferredSize(new Dimension(120, 25));
//        typeValue.setPreferredSize(new Dimension(120, 25));
        fileTypeValue.setPreferredSize(new Dimension(120, 25));
        fileNumberValue.setPreferredSize(new Dimension(120, 25));
        fileNameValue.setPreferredSize(new Dimension(120, 25));
        distributeValue.setPreferredSize(new Dimension(120, 25));
        fileVersionValue.setPreferredSize(new Dimension(120, 25));
        printDateValue.setPreferredSize(new Dimension(120, 25));
        receiveDateValue.setPreferredSize(new Dimension(120, 25));
        receiveDepartValue.setPreferredSize(new Dimension(120, 25));
        receivePersonValue.setPreferredSize(new Dimension(120, 25));



        submitButton.setPreferredSize(new Dimension(120, 25));
        String[] tableColumn = {"编号", "名称", "版本", "阶段标记", "文件类型", "密级", "分发部门", "申请人", "申请时间", "打印人", "打印时间", "领取人", "领取时间", "状态", "条码"};
		String[][] tableData = {};
		tableModel = new DefaultTableModel(tableData,tableColumn)
		{
			private static final long serialVersionUID = -5817421228650378228L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
		table.setRowHeight(20);
		DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
		render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		downPanel.setViewportView(table);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(submitButton);

		this.add(topPanel, BorderLayout.NORTH);
		this.add(toolBar, BorderLayout.CENTER);
		this.add(downPanel, BorderLayout.SOUTH);


		this.setTitle("查看延迟回收的纸质文件");
		this.setSize(1200, 800);
		this.setLocation(500, 100);
		this.setVisible(true);

	}


	public void clearCondition(){

//		unitValue.setSelectedIndex(0);
//		typeValue.setText("");

		fileNumberValue.setText("");
		fileNameValue.setText("");
		fileTypeValue.setSelectedIndex(0);
		//distributeValue.setSelectedIndex(0);
		fileVersionValue.setText("");
		printDateValue.setText("");
		receiveDateValue.setText("");
		receiveDepartValue.setText("");
		receivePersonValue.setText("");

	}

	public static void main(String[] args) {
		new SelectDelayRecoverFilePanel();
	}

	public CmPrintRecordInfoBean getConditionValues(){
		CmPrintRecordInfoBean cmPrintRecordQueryBean = new CmPrintRecordInfoBean();


		cmPrintRecordQueryBean.setFileNumber(fileNumberValue.getText());
		cmPrintRecordQueryBean.setFileName(fileNameValue.getText());
		cmPrintRecordQueryBean.setFileType(fileTypeValue.getSelectedItem().toString());
		cmPrintRecordQueryBean.setDistributeStatus(distributeValue.getSelectedItem().toString());
		cmPrintRecordQueryBean.setDocVersion(fileVersionValue.getText());
		cmPrintRecordQueryBean.setPrintDate(printDateValue.getText());
		cmPrintRecordQueryBean.setGetDate(receiveDateValue.getText());
		cmPrintRecordQueryBean.setGetDept(receiveDepartValue.getText());
		cmPrintRecordQueryBean.setGetUser(receivePersonValue.getText());

		return cmPrintRecordQueryBean;
	}

	public List<CmPrintRecordInfoBean> getQueryDelayInfo(CmPrintRecordInfoBean cmPrintRecordQueryBean, List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryDelayInfo(cmPrintRecordQueryBean, list);
	}
	public List<String> getQueryRecoverBarcode(String delayStatus) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryRecoverBarcode(delayStatus);
	}
	public Boolean startProcessOfRecover(String name, List<String> uuidList) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.startProcessOfRecover(name, uuidList);
	}


}
