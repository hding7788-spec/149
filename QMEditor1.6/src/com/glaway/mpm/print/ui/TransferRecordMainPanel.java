package com.glaway.mpm.print.ui;

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


/**
 *
 * @author cjh
 *
 */

public class TransferRecordMainPanel extends JFrame {

	private static final long serialVersionUID = 6344294123850002691L;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane tablePanel;
	private Class<?>[] tableColumnClass;

	public TransferRecordMainPanel() {
		initComponents();
		initLayout();
		initListener(this);
		initUI();
		loadData();
	}

	public void initComponents(){
		tablePanel = new JScrollPane();

		String[] tableColumn = {"id", "单位", "所在产品库", "文件类型", "文件编号", "文件名称", "版本", "阶段标记", "密级", "分发状态", "打印时间", "打印人", "领取部门", "领取人", "领取时间", "转移后部门"};
		this.tableColumnClass = new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class};
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
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(4).setPreferredWidth(150);
		table.getColumnModel().getColumn(5).setPreferredWidth(220);
		table.getColumnModel().getColumn(6).setPreferredWidth(80);
		table.getColumnModel().getColumn(7).setPreferredWidth(80);
		table.getColumnModel().getColumn(8).setPreferredWidth(80);
		table.getColumnModel().getColumn(9).setPreferredWidth(80);
		table.getColumnModel().getColumn(10).setPreferredWidth(120);
		table.getColumnModel().getColumn(11).setPreferredWidth(120);
		table.getColumnModel().getColumn(12).setPreferredWidth(120);
		table.getColumnModel().getColumn(13).setPreferredWidth(120);
		table.getColumnModel().getColumn(14).setPreferredWidth(120);
		table.getColumnModel().getColumn(15).setPreferredWidth(120);
		tablePanel.setViewportView(table);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		tablePanel.setPreferredSize(new Dimension((int)(width*0.9), (int)(height*0.1)));
	}

	public void initLayout(){
	    this.add(tablePanel);
	}

	public void initListener(final JFrame frame){
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

	public void initUI(){
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setTitle("文件转移列表");
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	public void loadData() {
		String oid = MPMPrintFileFrame.getOid();
		List<CmPrintRecordInfoBean> list = MPMPrintProcessor.loadTransferData(oid);
		if(list != null && list.size()>0){
			for(CmPrintRecordInfoBean bean : list) {
				String barTableID = bean.getBarTableID();
				String unit = bean.getUnit().indexOf("厂内") > -1 ? "厂内" : bean.getUnit();
				String type = bean.getType();
				String fileType = bean.getFileType();
				String fileNumber = bean.getFileNumber();
				String fileName = bean.getFileName();
				String version = bean.getDocVersion();
				String phaseCode = bean.getPhaseCode();
				String secret = bean.getSecret();
				String distributeStatus = bean.getDistributeStatus();
				String printDate = bean.getPrintDate();
				String getDate = bean.getGetDate();
				String getDept = bean.getGetDept();
				String getUser = bean.getGetUser();
				String printUser = bean.getPrintUser();
				String targetDept = bean.getTargetDept();
				Object[] value = {barTableID,unit, type, fileType, fileNumber, fileName, version, phaseCode, secret, distributeStatus, printDate,printUser,getDept,getUser,getDate,targetDept };
				tableModel.addRow(value);
			}
		}
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
			return false;
		}
	}
}
