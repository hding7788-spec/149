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
/**
 *
 * @author lkc
 *
 */
public class LoseSecondPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 6833544769346983893L;
	private String infor;
	private List<String> strList;
	private List<CmPrintRecordInfoBean> listBean;
	private JTable table;
	private DefaultTableModel tableModel;
	private JButton alterButton;
	private JCheckBox allCheck;
	private JButton closeButton;
	private JScrollPane tablePanel;
	private JPanel mainPanel;

	public LoseSecondPanel(String infor){
		this.infor = infor;
		initComponents();
		initListener();
		initLayout();
		initUI();
	}

	public void initComponents(){
		strList = new ArrayList<String>();
		listBean = new ArrayList<CmPrintRecordInfoBean>();
		tablePanel = new JScrollPane();
		strList = getList(infor);
		mainPanel = new JPanel();
		try {
			listBean = getQueryLoseInfo(strList);
			alterButton = new JButton("修改");
			closeButton = new JButton("关闭");
			allCheck = new JCheckBox();
			allCheck.setText("全选");
			String[] tableColumn = {"", "id" , "文件类型", "文件编号", "文件名称", "状态", "领取部门", "发起人", "发起人部门", "发起时间", "遗失原因", "条码"};
			String[][] tableData = {};
			tableModel = new DefaultTableModel(tableData, tableColumn);

			table = new JTable(tableModel);
			table.getTableHeader().setReorderingAllowed(false);
			DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
			render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			table.setRowHeight(30);
			TableColumnModel tableColumnModel = table.getColumnModel();
			tableColumnModel.getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
			tableColumnModel.getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));
			tableColumnModel.getColumn(0).setPreferredWidth(30);
			tableColumnModel.getColumn(1).setMaxWidth(0);
			tableColumnModel.getColumn(1).setMinWidth(0);
			tableColumnModel.getColumn(1).setPreferredWidth(0);
			tableColumnModel.getColumn(1).setResizable(false);
			tableColumnModel.getColumn(2).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(3).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(4).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(5).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(6).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(7).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(8).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(9).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(10).setCellEditor(new MyBatchEditor(new JTextField()));
			tableColumnModel.getColumn(11).setCellEditor(new MyCellEditor(new JTextField()));
			table.getColumnModel().getColumn(2).setPreferredWidth(50);
			table.getColumnModel().getColumn(3).setPreferredWidth(200);
			table.getColumnModel().getColumn(4).setPreferredWidth(250);
			table.getColumnModel().getColumn(5).setPreferredWidth(50);
			table.getColumnModel().getColumn(6).setPreferredWidth(80);
			table.getColumnModel().getColumn(7).setPreferredWidth(80);
			table.getColumnModel().getColumn(8).setPreferredWidth(80);
			table.getColumnModel().getColumn(9).setPreferredWidth(80);
			table.getColumnModel().getColumn(10).setPreferredWidth(120);
			table.getColumnModel().getColumn(11).setPreferredWidth(200);

			if(listBean.size() > 0){
				for (CmPrintRecordInfoBean cmPrintRecordInfoBean : listBean) {
					String id = cmPrintRecordInfoBean.getBarTableID();
					String fileType = cmPrintRecordInfoBean.getFileType();
					String fileNumber = cmPrintRecordInfoBean.getFileNumber();
					String fileName = cmPrintRecordInfoBean.getFileName();
					String fileStatus = cmPrintRecordInfoBean.getMiddleStatus();
					String getDept = cmPrintRecordInfoBean.getGetDept();
					String user = cmPrintRecordInfoBean.getCurrentUser();
					String dept = cmPrintRecordInfoBean.getCurrentDept();
					String date = cmPrintRecordInfoBean.getCurrentDate();
					String loseReason = cmPrintRecordInfoBean.getLoseReason();
					String barCode = cmPrintRecordInfoBean.getBarCode();
					String[] value = {Boolean(false), id, fileType, fileNumber, fileName, fileStatus, getDept, user, dept, date, loseReason, barCode};
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
				Boolean flag = checkOutLoseInfo(table);
				if(flag){
					list = getTableInfo(table);
					Boolean saveFlag;
					try {
						saveFlag = saveUpdateLoseInfo(list);
						if(saveFlag){
							JOptionPane.showMessageDialog(LoseSecondPanel.this, "修改成功！");
						}else{
							JOptionPane.showMessageDialog(LoseSecondPanel.this, "修改失败！");
						}
					} catch (RemoteException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}else{
					JOptionPane.showMessageDialog(getContentPane(), "所选择修改的遗失信息存在遗失原因为空！");
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
	public void initLayout(){
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints grid = new GridBagConstraints();

		grid.gridx = 0;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.anchor = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(20, 5, 10, 10);
		mainPanel.add(alterButton, grid);

		grid.gridx = 1;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.anchor = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(20, 5, 10, 10);
		mainPanel.add(closeButton, grid);

		grid.gridx = 0;
		grid.gridy = 1;
		grid.weightx = 1;
		grid.weighty = 1;
		grid.gridwidth = 9;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.BOTH;
		mainPanel.add(tablePanel, grid);

		grid.gridx = 0;
		grid.gridy = 2;
		grid.gridwidth = 9;
		grid.gridheight = 1;
		mainPanel.add(allCheck, grid);

		alterButton.setPreferredSize(new Dimension(100, 25));
		closeButton.setPreferredSize(new Dimension(100, 25));
	    allCheck.setPreferredSize(new Dimension(100, 25));
		this.add(mainPanel);
	}
	public void initUI(){
		this.setTitle("文件遗失信息");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
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
	public List<CmPrintRecordInfoBean> getQueryLoseInfo(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryLoseInfo(list);
	}
	public Boolean saveUpdateLoseInfo(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.saveUpdateLoseInfo(list);
	}
	public List<CmPrintRecordInfoBean> getTableInfo(JTable table){
		   List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		   for (int i = 0; i < table.getRowCount(); i++) {
			   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
			       CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
			       cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 1));
			       cmPrintRecordInfoBean.setLoseReason((String)table.getModel().getValueAt(i, 9));
				   list.add(cmPrintRecordInfoBean);
			   }
		   }
		   return list;

	}
	public Boolean checkOutLoseInfo(JTable table){
		 for (int i = 0; i < table.getRowCount(); i++) {
			  if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				  if("".equals(table.getModel().getValueAt(i, 9)) || null == table.getModel().getValueAt(i, 9)){
					  return false;
				  }
			  }
		 }
		return true;
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
}

