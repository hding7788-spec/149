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
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.service.PrintToWCIntf;

import ext.casc.util.CommonUtil;

/**
 *
 * @author lkc
 *
 */
public class RecoverFirstPanel extends JFrame{

	/**
	 *
	 */
	private static final long serialVersionUID = -2390945572406520817L;
	private JTable recoverTable;
	private DefaultTableModel recoverTableModel;
	private JScrollPane tablePanel;
	private JPanel mainPanel;
	private List<String> barCodeList;
	private List<String> strList;
	private List<CmPrintRecordInfoBean> listBean;
	private String infor;
	private JButton closeButton;

	public RecoverFirstPanel(String infor){
		 this.infor = infor;
		 initComponents();
		 initLayout();
		 initListener();
	     initUI();
	}
	public void initComponents(){
		tablePanel = new JScrollPane();
		mainPanel = new JPanel();
		barCodeList = new ArrayList<String>();
		strList = new ArrayList<String>();
		listBean = new ArrayList<CmPrintRecordInfoBean>();
		try {
			closeButton = new JButton("关闭");

			strList = getList(infor);
			barCodeList = queryBarTableID(strList);
			listBean = getQueryRecoverTable(barCodeList);
			String[] tableColumn = {"文件编号", "文件名称", "分发状态", "版本", "阶段标记", "密级", "领取部门", "条码","所在产品库", "受控状态"};
			String[][] tableData = {};
			recoverTableModel = new DefaultTableModel(tableData, tableColumn){
				/**
				 *
				 */
				private static final long serialVersionUID = 5914730129223546717L;
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}

			};
			recoverTable = new JTable(recoverTableModel);
			recoverTable.getTableHeader().setReorderingAllowed(false);
			recoverTable.setRowHeight(30);
			recoverTable.getColumnModel().getColumn(0).setPreferredWidth(200);
			recoverTable.getColumnModel().getColumn(1).setPreferredWidth(250);
			recoverTable.getColumnModel().getColumn(2).setPreferredWidth(50);
			recoverTable.getColumnModel().getColumn(3).setPreferredWidth(50);
			recoverTable.getColumnModel().getColumn(4).setPreferredWidth(30);
			recoverTable.getColumnModel().getColumn(5).setPreferredWidth(50);
			recoverTable.getColumnModel().getColumn(6).setPreferredWidth(50);
			recoverTable.getColumnModel().getColumn(7).setPreferredWidth(200);

			recoverTable.getColumnModel().getColumn(8).setPreferredWidth(80);
			recoverTable.getColumnModel().getColumn(9).setPreferredWidth(50);

			DefaultTableCellRenderer render = (DefaultTableCellRenderer) recoverTable.getDefaultRenderer(getClass());
			render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			recoverTableModel.setRowCount(0);
			if(listBean.size() > 0){
				for (CmPrintRecordInfoBean cmPrintRecordQueryBean : listBean) {
			    String fileNumber = cmPrintRecordQueryBean.getFileNumber();
				String fileName = cmPrintRecordQueryBean.getFileName();
				String fileStatus = cmPrintRecordQueryBean.getMiddleStatus();
				String docVersion = cmPrintRecordQueryBean.getDocVersion();
				String phaseCode = cmPrintRecordQueryBean.getPhaseCode();
				String secret = cmPrintRecordQueryBean.getSecret();
				String getDept = cmPrintRecordQueryBean.getGetDept();
				String barCode = cmPrintRecordQueryBean.getBarCode();

				String containerName = CommonUtil.objectToString(cmPrintRecordQueryBean.getContainerName());
				String lifeCycleState = CommonUtil.objectToString(cmPrintRecordQueryBean.getLifeCycleState());
				String[] value = {fileNumber, fileName, fileStatus, docVersion, phaseCode, secret, getDept, barCode,containerName,lifeCycleState};
				recoverTableModel.addRow(value);
				}
			}
			tablePanel.setViewportView(recoverTable);

			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

	}
	public void initLayout(){
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints grid = new GridBagConstraints();

		grid.gridx = 0;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(20, 5, 10, 5);
		mainPanel.add(closeButton, grid);

		grid.gridx = 0;
		grid.gridy = 1;
		grid.weightx = 1;
		grid.weighty = 1;
		grid.gridwidth = 9;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.BOTH;
		grid.insets = new Insets(5, 5, 5, 5);
		mainPanel.add(tablePanel, grid);

		closeButton.setPreferredSize(new Dimension(120, 25));
		this.add(mainPanel);

	}
	public void initListener(){
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
	public List<CmPrintRecordInfoBean> getQueryRecoverTable(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryRecoverTable(list);
	}
	public List<String> queryBarTableID(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryBarTableID(list);
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

	public void initUI(){
		this.setTitle("查询回收文件");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

}
