package com.glaway.mpm.print.ui;

import java.awt.Component;
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
import java.util.EventObject;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.service.PrintToWCIntf;

import com.glaway.mpm.util.DateChooser;
/**
 *
 * @author lkc
 *
 */
public class DelayFirstPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = -7326279309036393036L;
	private JButton alterButton;
	private JButton closeButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane tablePanel;
	private JPanel mainPanel;
	private List<String> strList;
	private List<String> list;
	private List<CmPrintRecordInfoBean> listBean;
	private JCheckBox allCheck;
	private JTextField textField;
	private String infor;
	private Boolean isFromRecover;
	public DelayFirstPanel(String infor, Boolean isFromRecover){
		 this.infor = infor;
		 this.isFromRecover = isFromRecover;
		 initComponents();
		 initLayout();
		 initListener();
	     initUI();
	}
	public void initComponents(){
		tablePanel = new JScrollPane();
		mainPanel = new JPanel();
		strList = new ArrayList<String>();
		list = new ArrayList<String>();
		listBean = new ArrayList<CmPrintRecordInfoBean>();
		try {
			textField = new JTextField();
			strList = getList(infor);
			list = queryBarTableIDByDelay(strList, isFromRecover);
			listBean = getQueryStoreTableByDelay(list, isFromRecover);
			alterButton = new JButton("修改");
			closeButton = new JButton("关闭");

			allCheck = new JCheckBox();
			allCheck.setText("全选");

			String[] tableColumn = {"", "uuid", "文件编号", "文件名称", "版本", "阶段标记", "密级", "领取部门", "条码", "状态", "延迟原因", "延迟时间"};
			String[][] tableData = {};
			tableModel = new DefaultTableModel(tableData, tableColumn);

			table = new JTable(tableModel);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
			table.setRowHeight(30);
			table.getSelectedRows();
			DateChooser dataChooser = DateChooser.getInstance("yyyy/MM/dd");
			dataChooser.register(textField);
			TableColumnModel tableColumnModel = table.getColumnModel();
			tableColumnModel.getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
			tableColumnModel.getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));
			tableColumnModel.getColumn(0).setPreferredWidth(30);
			tableColumnModel.getColumn(1).setMaxWidth(0);
			tableColumnModel.getColumn(1).setMinWidth(0);
			tableColumnModel.getColumn(1).setPreferredWidth(0);
			tableColumnModel.getColumn(1).setResizable(false);
			tableColumnModel.getColumn(2).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(2).setPreferredWidth(200);
			tableColumnModel.getColumn(3).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(3).setPreferredWidth(250);
			tableColumnModel.getColumn(4).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(4).setPreferredWidth(50);
			tableColumnModel.getColumn(5).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(5).setPreferredWidth(50);
			tableColumnModel.getColumn(6).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(6).setPreferredWidth(50);
			tableColumnModel.getColumn(7).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(7).setPreferredWidth(50);
			tableColumnModel.getColumn(8).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(8).setPreferredWidth(200);
			tableColumnModel.getColumn(9).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(9).setPreferredWidth(50);
			tableColumnModel.getColumn(10).setCellEditor(new MyBatchEditor(new JTextField()));
			tableColumnModel.getColumn(10).setPreferredWidth(120);
			tableColumnModel.getColumn(11).setCellEditor(new MyBatchEditor(textField));
			tableColumnModel.getColumn(11).setPreferredWidth(80);

			DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
			render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			tableModel.setRowCount(0);
			if(listBean.size() > 0){
				for (CmPrintRecordInfoBean cmPrintRecordInfoBean : listBean) {
				String uuid = cmPrintRecordInfoBean.getUuid();
			    String fileNumber = cmPrintRecordInfoBean.getFileNumber();
				String fileName = cmPrintRecordInfoBean.getFileName();
				String docVersion = cmPrintRecordInfoBean.getDocVersion();
				String phaseCode = cmPrintRecordInfoBean.getPhaseCode();
				String secret = cmPrintRecordInfoBean.getSecret();
				String getDept = cmPrintRecordInfoBean.getGetDept();
				String barCode = cmPrintRecordInfoBean.getBarCode();
				String middleStatus = ("延迟回收中".equals(cmPrintRecordInfoBean.getMiddleStatus())) ? "延迟回收中" : "延迟封存中";
				String delayReason = cmPrintRecordInfoBean.getDelayReason();
				String delayDate = cmPrintRecordInfoBean.getDelayDate();
				String[] value = {Boolean(false), uuid, fileNumber, fileName, docVersion, phaseCode, secret, getDept, barCode, middleStatus, delayReason, delayDate};
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

	public void initLayout(){

		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints grid = new GridBagConstraints();

		grid.gridx = 0;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(20, 5, 10, 5);
		mainPanel.add(alterButton, grid);

		grid.gridx = 1;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(20, 5, 10, 5);
		mainPanel.add(closeButton, grid);

		grid.gridx = 0;
		grid.gridy = 1;
		grid.gridwidth = 9;
		grid.gridheight = 1;
		grid.weightx = 1;
		grid.weighty = 1;
		grid.fill = GridBagConstraints.BOTH;
		grid.insets = new Insets(5, 5, 5, 5);
		mainPanel.add(tablePanel, grid);

		grid.gridx = 0;
		grid.gridy = 2;
		grid.gridwidth = 9;
		grid.gridheight = 1;
		grid.weightx = 1;
		grid.weighty = 1;
		grid.insets = new Insets(5, 5, 5, 5);
		mainPanel.add(allCheck, grid);

		alterButton.setPreferredSize(new Dimension(120, 25));
		closeButton.setPreferredSize(new Dimension(120, 25));
		this.add(mainPanel);

	}
	public List<CmPrintRecordInfoBean> getQueryStoreTableByDelay(List<String> list, Boolean isFromRecover) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryStoreTableByDelay(list, isFromRecover);
	}
	public List<String> queryBarTableIDByDelay(List<String> list, Boolean isFromRecover) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryBarTableIDByDelay(list, isFromRecover);
	}
	public Boolean getSaveToStoreTableDelayInfor(List<CmPrintRecordInfoBean> list, Boolean isFromRecover) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.saveToStoreTableDelayInfor(list, isFromRecover);
	}
	public void initUI(){
		this.setTitle("延迟申请文件");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	public void initListener(){
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

		alterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
				Boolean flag = checkOutDelayInfo(table);
				if(flag){
					list = getTableInfo(table);
					Boolean saveFlag;
					try {
						saveFlag = getSaveToStoreTableDelayInfor(list, isFromRecover);
						if(saveFlag){
							JOptionPane.showMessageDialog(getContentPane(), "修改成功！");
						}else{
							JOptionPane.showMessageDialog(getContentPane(), "修改失败!");
						}
					} catch (RemoteException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}else{
					JOptionPane.showMessageDialog(getContentPane(), "所选择修改的延迟信息存在延迟原因或者延迟时间为空！");
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
	}
	public Boolean checkOutDelayInfo(JTable table){
		 for (int i = 0; i < table.getRowCount(); i++) {
			  if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				  if("".equals(table.getModel().getValueAt(i, 10)) || null == table.getModel().getValueAt(i, 10)
						  || "".equals(table.getModel().getValueAt(i, 11)) || null == table.getModel().getValueAt(i, 11)){
					  return false;
				  }
			  }
		 }
		return true;
	}

	public List<CmPrintRecordInfoBean> getTableInfo(JTable table){
		   List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		   for (int i = 0; i < table.getRowCount(); i++) {
			 if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
			       CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
			       cmPrintRecordInfoBean.setUuid((String)table.getModel().getValueAt(i, 1));
			       cmPrintRecordInfoBean.setDelayReason((String)(table.getModel().getValueAt(i, 10)));
			       cmPrintRecordInfoBean.setDelayDate((String)(table.getModel().getValueAt(i, 11)));
				   list.add(cmPrintRecordInfoBean);
			   }
		   }
		   return list;

	}
	class MyCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 4699320939316084221L;
		private JTextField checkField = null;
		public MyCellEditor(JTextField textField) {
			super(textField);
			this.checkField = textField;
		}
		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return false;
		}

	}
	class MyBatchEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 4234511064519206474L;
		private JTextField textField = null;
		private int currentCol = -1;
		private int currentRow = -1;
		private JTable table = null;
		MyBatchEditor(JTextField textField) {
			super(textField);
			this.textField = textField;
		}
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.currentCol = column;
			this.currentRow = row;
			this.table = table;
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}

		public Object getCellEditorValue() {
			String text = this.textField.getText();
			int clickRow = table.getSelectedRow();
			boolean isClick = false;
			if(table.getModel().getValueAt(clickRow, 0)==null){
				isClick = false;
			} else{
				isClick = (Boolean) table.getModel().getValueAt(clickRow, 0);
			}
			if(isClick){
				for(int i=0;i<table.getRowCount();i++){ //批量编辑
					String isChecked = String.valueOf(table.getValueAt(i, 0));
					if("true".equals(isChecked)){
						tableModel.setValueAt(text, i, currentCol);
					}
				}
			}
			return text;
		}
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
