package com.glaway.mpm.print.ui;

import java.awt.BorderLayout;
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
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.service.PrintToWCIntf;
/**
 *
 * @author lkc
 *
 */
public class FileStorageInfoShowPanel extends JFrame{

	/**
	 *
	 */
	private static final long serialVersionUID = -3055462952566734080L;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane tablePanel;
	private JPanel panel;
	private List<String> barCodeList;
	private List<String> strList;
	private List<CmPrintRecordInfoBean> listBean;
	private String infor;
	private JButton closeButton;

	public FileStorageInfoShowPanel(String infor){
		 this.infor = infor;
		 initComponents();
		 initLayout();
		 initListener();
	     initUI();
	}

	public void initComponents(){
		tablePanel = new JScrollPane();
		panel = new JPanel();
		barCodeList = new ArrayList<String>();
		strList = new ArrayList<String>();
		closeButton = new JButton("关闭");
		listBean = new ArrayList<CmPrintRecordInfoBean>();
		try {
			String dept = getUserDepartment();
			strList = getList(infor);
			listBean = getStoreInfo(strList, dept);
			String[] tableColumn = {"文件编号", "文件名称", "文件状态", "版本", "阶段标记", "密级", "领取部门", "封存时间", "条码"};
			String[][] tableData = {};
			tableModel = new DefaultTableModel(tableData, tableColumn){
				/**
				 *
				 */
				private static final long serialVersionUID = 491916018707380247L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}

			};
			table = new JTable(tableModel);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
			table.setRowHeight(30);
			table.getColumnModel().getColumn(0).setPreferredWidth(200);
			table.getColumnModel().getColumn(1).setPreferredWidth(250);
			table.getColumnModel().getColumn(2).setPreferredWidth(50);
			table.getColumnModel().getColumn(3).setPreferredWidth(50);
			table.getColumnModel().getColumn(4).setPreferredWidth(50);
			table.getColumnModel().getColumn(5).setPreferredWidth(50);
			table.getColumnModel().getColumn(6).setPreferredWidth(50);
			table.getColumnModel().getColumn(7).setPreferredWidth(50);
			table.getColumnModel().getColumn(8).setPreferredWidth(200);
			DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
			render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			tableModel.setRowCount(0);
			if(listBean.size() > 0){
				for (CmPrintRecordInfoBean cmPrintRecordQueryBean : listBean) {
					String fileNumber = cmPrintRecordQueryBean.getFileNumber();
					String fileName = cmPrintRecordQueryBean.getFileName();
					String fileStatus = cmPrintRecordQueryBean.getMiddleStatus();
					String docVersion = cmPrintRecordQueryBean.getDocVersion();
					String phaseCode = cmPrintRecordQueryBean.getPhaseCode();
					String secret = cmPrintRecordQueryBean.getSecret();
					String getDept = cmPrintRecordQueryBean.getGetDept();
					String storeTime = cmPrintRecordQueryBean.getStoreTime();
					String barCode = cmPrintRecordQueryBean.getBarCode();
					String[] value = {fileNumber, fileName, fileStatus, docVersion, phaseCode, secret, getDept, storeTime, barCode};
					tableModel.addRow(value);
				}
			}
			tablePanel.setViewportView(table);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}


	}
	private String Boolean(boolean b) {
		return null;
	}
	public String getUserDepartment() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserDepartment();
	}
	public void initLayout(){
		panel.setLayout(new GridBagLayout());
		GridBagConstraints grid = new GridBagConstraints();

		grid.gridx = 0;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(20, 5, 10, 5);
		panel.add(closeButton, grid);

		grid.gridx = 0;
		grid.gridy = 1;
		grid.gridwidth = 9;
		grid.gridheight = 1;
		grid.weightx = 1;
		grid.weighty = 1;
		grid.fill = GridBagConstraints.BOTH;
		grid.insets = new Insets(5, 5, 5, 5);
		panel.add(tablePanel, grid);

		closeButton.setPreferredSize(new Dimension(120, 25));
		this.add(panel);

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
	public List<CmPrintRecordInfoBean> getStoreInfo(List<String> list, String dept) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.storeInfoByDept(list, dept);
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
		this.setTitle("查询封存文件");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

}
