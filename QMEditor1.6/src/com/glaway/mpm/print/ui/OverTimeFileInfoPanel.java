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
/**
 *
 * @author lkc
 *
 */
public class OverTimeFileInfoPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 5103662574752782603L;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane tablePanel;
	private JPanel mainPanel;
	private List<String> list;
	private List<CmPrintRecordInfoBean> listBean;
	private String infor;
	private JButton closeButton;
	private List<String> strList;

	public OverTimeFileInfoPanel(String infor){
		 this.infor = infor;
		 initComponents();
		 initLayout();
		 initListener();
	     initUI();
	}
	public void initComponents(){
		tablePanel = new JScrollPane();
		mainPanel = new JPanel();
		list = new ArrayList<String>();
		listBean = new ArrayList<CmPrintRecordInfoBean>();
		strList = new ArrayList<String>();
		try {
			closeButton = new JButton("关闭");
			strList = getList(infor);
			list = queryBarTableIDByOver(strList);
			listBean = queryDelayFileInfoByOver(list);
			String[] tableColumn = {"文件编号", "文件名称", "版本", "阶段标记", "密级", "领取部门", "条码", "状态", "延迟原因", "延迟时间"};
			String[][] tableData = {};
			tableModel = new DefaultTableModel(tableData, tableColumn);

			table = new JTable(tableModel){
				/**
				 *
				 */
				private static final long serialVersionUID = -2727353302889124569L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}

			};
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
			table.setRowHeight(30);
			table.getColumnModel().getColumn(0).setPreferredWidth(200);
			table.getColumnModel().getColumn(1).setPreferredWidth(250);
			table.getColumnModel().getColumn(2).setPreferredWidth(50);
			table.getColumnModel().getColumn(3).setPreferredWidth(50);
			table.getColumnModel().getColumn(4).setPreferredWidth(50);
			table.getColumnModel().getColumn(5).setPreferredWidth(50);
			table.getColumnModel().getColumn(6).setPreferredWidth(180);
			table.getColumnModel().getColumn(7).setPreferredWidth(50);
			table.getColumnModel().getColumn(8).setPreferredWidth(120);
			table.getColumnModel().getColumn(9).setPreferredWidth(80);

			DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
			render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			tableModel.setRowCount(0);
			if(listBean.size() > 0){
				for (CmPrintRecordInfoBean cmPrintRecordInfoBean : listBean) {
			    String fileNumber = cmPrintRecordInfoBean.getFileNumber();
				String fileName = cmPrintRecordInfoBean.getFileName();
				String docVersion = cmPrintRecordInfoBean.getDocVersion();
				String phaseCode = cmPrintRecordInfoBean.getPhaseCode();
				String secret = cmPrintRecordInfoBean.getSecret();
				String getDept = cmPrintRecordInfoBean.getGetDept();
				String barCode = cmPrintRecordInfoBean.getBarCode();
				String delayStatus = ("1".equals(cmPrintRecordInfoBean.getDelayStatus())) ? "延迟" : "正常";
				String delayReason = cmPrintRecordInfoBean.getDelayReason();
				String delayDate = cmPrintRecordInfoBean.getDelayDate();
				String[] value = {fileNumber, fileName, docVersion, phaseCode, secret, getDept, barCode, delayStatus, delayReason, delayDate};
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
			   if (close == 0) {
			     System.exit(0);
			    }else{
					   setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				   }
			 }
		});
	}

	public List<CmPrintRecordInfoBean> queryDelayFileInfoByOver(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryDelayFileInfoByOver(list);
	}

	public List<String> queryBarTableIDByOver(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryBarTableIDByOver(list);
	}

	public void initUI(){
		this.setTitle("延迟逾期文件信息");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
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
