package com.glaway.mpm.print.ui;

import java.awt.Component;
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
import java.util.EventObject;
import java.util.List;
import javax.swing.JFrame;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.ui.RecoverSecondPanel.MyBatchEditor;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.DateChooser;

import ext.casc.util.CommonUtil;
/**
 *
 * @author lkc
 *
 */
public class DelaySecondPanel extends JFrame{

	/**
	 *
	 */
	private static final long serialVersionUID = 845391701958377713L;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane tablePanel;
	private JPanel mainPanel;
	private List<String> list;
	private List<CmPrintRecordInfoBean> listBean;
	private String infor;
	private Boolean isFromRecover;
	private JButton closeButton;
	private List<String> strList;
	private JTextField textField;
	private JButton saveButton;
	private String oid;//流程OID
	private Boolean isSubmit;//判断流程是否是提交活动
	public DelaySecondPanel(String infor, Boolean isFromRecover,String oid){
		 this.infor = infor;
		 this.isFromRecover = isFromRecover;
		 this.oid = oid;
		 initComponents();
		 initLayout();
		 initListener();
	     initUI();
	     setEnabled();
	}
	public void initComponents(){
		tablePanel = new JScrollPane();
		mainPanel = new JPanel();
		list = new ArrayList<String>();
		listBean = new ArrayList<CmPrintRecordInfoBean>();
		strList = new ArrayList<String>();
		try {
			closeButton = new JButton("关闭");
			saveButton = new JButton("保存");
			textField = new JTextField();
			isSubmit = isSubmitDelayByWfProcessOid(oid);
			strList = getList(infor);
			list = queryBarTableIDByDelay(strList, isFromRecover);
			listBean = getQueryStoreTableByDelay(list, isFromRecover);
			String[] tableColumn = {"文件编号", "文件名称", "版本", "阶段标记", "密级", "领取部门", "条码", "状态", "延迟原因", "延迟时间","id","uuid"};
			String[][] tableData = {};
			tableModel = new DefaultTableModel(tableData, tableColumn);

			table = new JTable(tableModel);
			DateChooser dataChooser = DateChooser.getInstance("yyyy/MM/dd");
			dataChooser.register(textField);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
			table.setRowHeight(30);


			table.getColumnModel().getColumn(0).setCellEditor(new MyCellEditor(new JTextField()));
			table.getColumnModel().getColumn(1).setCellEditor(new MyCellEditor(new JTextField()));
			table.getColumnModel().getColumn(2).setCellEditor(new MyCellEditor(new JTextField()));
			table.getColumnModel().getColumn(3).setCellEditor(new MyCellEditor(new JTextField()));
			table.getColumnModel().getColumn(4).setCellEditor(new MyCellEditor(new JTextField()));
			table.getColumnModel().getColumn(5).setCellEditor(new MyCellEditor(new JTextField()));
			table.getColumnModel().getColumn(6).setCellEditor(new MyCellEditor(new JTextField()));
			table.getColumnModel().getColumn(7).setCellEditor(new MyCellEditor(new JTextField()));
			table.getColumnModel().getColumn(8).setCellEditor(new MyBatchEditor(new JTextField()));
			table.getColumnModel().getColumn(9).setCellEditor(new MyBatchEditor(textField));

			table.getColumnModel().getColumn(0).setPreferredWidth(200);
			table.getColumnModel().getColumn(1).setPreferredWidth(250);
			table.getColumnModel().getColumn(2).setPreferredWidth(50);
			table.getColumnModel().getColumn(3).setPreferredWidth(50);
			table.getColumnModel().getColumn(4).setPreferredWidth(50);
			table.getColumnModel().getColumn(5).setPreferredWidth(50);
			table.getColumnModel().getColumn(6).setPreferredWidth(200);
			table.getColumnModel().getColumn(7).setPreferredWidth(50);
			table.getColumnModel().getColumn(8).setPreferredWidth(120);
			table.getColumnModel().getColumn(9).setPreferredWidth(80);


			table.getColumnModel().getColumn(10).setMaxWidth(0);
			table.getColumnModel().getColumn(10).setMinWidth(0);
			table.getColumnModel().getColumn(10).setPreferredWidth(0);
			table.getColumnModel().getColumn(10).setResizable(false);
			table.getColumnModel().getColumn(11).setMaxWidth(0);
			table.getColumnModel().getColumn(11).setMinWidth(0);
			table.getColumnModel().getColumn(11).setPreferredWidth(0);
			table.getColumnModel().getColumn(11).setResizable(false);

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
				//String middleStatus = ("延迟回收中".equals(cmPrintRecordInfoBean.getMiddleStatus())) ? "延迟回收中" : "延迟封存中";
				String middleStatus = cmPrintRecordInfoBean.getMiddleStatus();
				String delayReason = CommonUtil.objectToString(cmPrintRecordInfoBean.getDelayReason());
				String delayDate = CommonUtil.objectToString(cmPrintRecordInfoBean.getDelayDate());

				String id = cmPrintRecordInfoBean.getBarTableID();
				String uuid = cmPrintRecordInfoBean.getUuid();
				String[] value = {fileNumber, fileName, docVersion, phaseCode, secret, getDept, barCode, middleStatus, delayReason, delayDate,id,uuid};
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

		grid.gridx = 1;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(20, 5, 10, 5);
		mainPanel.add(saveButton, grid);

		grid.gridx = 0;
		grid.gridy = 1;
		grid.gridwidth = 10;
		grid.gridheight = 1;
		grid.weightx = 1;
		grid.weighty = 1;
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
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = e.getActionCommand();
				if(str == "保存"){
				   int result = JOptionPane.showConfirmDialog(getContentPane(),"是否保存延迟原因和延迟时间？","提示",JOptionPane.YES_NO_OPTION);
				   if(result == JOptionPane.YES_OPTION){
					   CommonUIUtil.stopTableCellEditing(table);
						List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
						listBean = getTableDelayInfo(table);
						try {
							String strruat = getSaveToRecoverTableDelayInfo(listBean);
							if("".equals(strruat) || strruat == null){
								JOptionPane.showMessageDialog(getContentPane(), "保存成功！");
							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}
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

	public List<CmPrintRecordInfoBean> getQueryStoreTableByDelay(List<String> list, Boolean isFromRecover) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryStoreTableByDelay(list, isFromRecover);
	}
	public List<String> queryBarTableIDByDelay(List<String> list, Boolean isFromRecover) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryBarTableIDByDelay(list, isFromRecover);
	}
	public void initUI(){
		this.setTitle("延迟申请文件");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	public void setEnabled(){
		saveButton.setEnabled(isSubmit);
		saveButton.setVisible(isSubmit);
		table.setEnabled(isSubmit);
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
	class MyCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 4699320939316084221L;
		private JTextField textField = null;
		public MyCellEditor(JTextField textField) {
			super(textField);
			this.textField = textField;
		}
		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return false;
		}

	}
	class MyBatchEditor extends DefaultCellEditor {

		/**
		 *
		 */
		private static final long serialVersionUID = 6515167789663640692L;
		private JTextField textField = null;
		private int currentCol = -1;
		private int currentRow = -1;
		MyBatchEditor(JTextField textField) {
			super(textField);
			this.textField = textField;
		}
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.currentCol = column;
			this.currentRow = row;
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}

		public Object getCellEditorValue() {
			String text = this.textField.getText();
			int clickRow = table.getSelectedRow();
			boolean isClick = false;
			if(table.getModel().getValueAt(clickRow, 0)==null){
				isClick = false;
			} else{
//				isClick = (Boolean) table.getModel().getValueAt(clickRow, 0);
			}
//			if(isClick){
//				for(int i=0;i<table.getRowCount();i++){ //批量编辑
//					String isChecked = String.valueOf(table.getValueAt(i, 0));
//					if("true".equals(isChecked)){
//						table.getModel().setValueAt(text, i, currentCol);
//					}
//				}
//			}
			return text;
		}
	}
	public List<CmPrintRecordInfoBean> getTableDelayInfo(JTable table){

		   List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		   for (int i = 0; i < table.getRowCount(); i++) {
			   CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
			   cmPrintRecordInfoBean.setDelayReason(((String)table.getModel().getValueAt(i, 8)));
			   cmPrintRecordInfoBean.setDelayDate(((String)table.getModel().getValueAt(i, 9)));

			   cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 10));
			   cmPrintRecordInfoBean.setUuid((String)table.getModel().getValueAt(i, 11));
			   list.add(cmPrintRecordInfoBean);
		   }
		   return list;

	}
	public String getSaveToRecoverTableDelayInfo(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.saveToRecoverTableDelayDate(list);
	}
	public boolean isSubmitDelayByWfProcessOid(String oid) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.isSubmitDelayByWfProcessOid(oid);
	}
}
