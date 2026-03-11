package com.glaway.mpm.print.ui;



import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.UUID;

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

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;
/**
 *
 * @author lkc
 *
 */
public class LoseApplyPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = -6413107323635726325L;
	private static final String NAME = "遗失申请";
	private List<CmPrintRecordInfoBean> list;
	private PrintRecordQueryMainPanel panel;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane tablePanel;
	private JPanel mainPanel;
	private JCheckBox allCheck;
	private JButton submitButton;
	private JButton closeButton;
	public LoseApplyPanel(PrintRecordQueryMainPanel panel, List<CmPrintRecordInfoBean> list){
		this.panel = panel;
		this.list = list;
		initComponents();
		initLayout();
		initUI();
		addListener();
	}

	public void initComponents(){
		tablePanel = new JScrollPane();
		mainPanel = new JPanel();
		List<CmPrintRecordInfoBean> listBean;
		try {
			listBean = updateBean(list);
			if(listBean.size() < 0){
				JOptionPane.showMessageDialog(LoseApplyPanel.this, "不存在本部门未发起遗失的已下发文件！");
			}else{
				allCheck = new JCheckBox();
				allCheck.setText("全选");
				submitButton = new JButton("提交申请");
				closeButton = new JButton("关闭");
				String[] tableColumn = {"", "UUID", "ID", "文件类型", "文件编号", "文件名称", "条码", "遗失原因"};
				String[][] tableData = {};
				tableModel = new DefaultTableModel(tableData, tableColumn);
				table = new JTable(tableModel);
				table.getTableHeader().setReorderingAllowed(false);
				table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
				table.setRowHeight(30);
				DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
				render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
				table.getColumnModel().getColumn(1).setMaxWidth(0);
				table.getColumnModel().getColumn(1).setMinWidth(0);
				table.getColumnModel().getColumn(1).setPreferredWidth(0);
				table.getColumnModel().getColumn(1).setResizable(false);
				table.getColumnModel().getColumn(2).setMaxWidth(0);
				table.getColumnModel().getColumn(2).setMinWidth(0);
				table.getColumnModel().getColumn(2).setPreferredWidth(0);
				table.getColumnModel().getColumn(2).setResizable(false);
				table.getColumnModel().getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
				table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));
				table.getColumnModel().getColumn(0).setPreferredWidth(30);
				table.getColumnModel().getColumn(3).setCellEditor(new MyCellEditor(new JTextField()));
				table.getColumnModel().getColumn(3).setPreferredWidth(50);
				table.getColumnModel().getColumn(4).setCellEditor(new MyCellEditor(new JTextField()));
				table.getColumnModel().getColumn(4).setPreferredWidth(80);
				table.getColumnModel().getColumn(5).setCellEditor(new MyCellEditor(new JTextField()));
				table.getColumnModel().getColumn(5).setPreferredWidth(120);
				table.getColumnModel().getColumn(6).setCellEditor(new MyCellEditor(new JTextField()));
				table.getColumnModel().getColumn(6).setPreferredWidth(80);
				table.getColumnModel().getColumn(7).setCellEditor(new MyBatchEditor(new JTextField()));
				table.getColumnModel().getColumn(7).setPreferredWidth(120);

				for (int i = 0; i < list.size(); i++) {
					String uuid = UUID.randomUUID().toString();
					String id = list.get(i).getBarTableID();
					String fileType = list.get(i).getFileType();
					String fileNumber = list.get(i).getFileNumber();
					String fileName = list.get(i).getFileName();
					String barCode = list.get(i).getBarCode();
					String[] value = {Boolean(false), uuid, id, fileType, fileNumber, fileName, barCode};
					tableModel.addRow(value);
				}
				tablePanel.setViewportView(table);
			}
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
		grid.anchor = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(5, 10, 5, 10);
		mainPanel.add(submitButton, grid);

		grid.gridx = 1;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.anchor = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(5, 10, 5, 10);
		mainPanel.add(closeButton, grid);

		grid.gridx = 0;
		grid.gridy = 1;
		grid.gridwidth = 9;
		grid.gridheight = 1;
		grid.weightx = 1;
		grid.weighty = 1;
		grid.fill = GridBagConstraints.BOTH;
		//grid.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(tablePanel, grid);

		grid.gridx = 0;
		grid.gridy = 2;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		//grid.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(allCheck, grid);

		submitButton.setPreferredSize(new Dimension(120, 25));
	    allCheck.setPreferredSize(new Dimension(120, 25));
		this.add(mainPanel);
	}

	public void addListener(){
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

		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
				List<String> list = new ArrayList<String>();
				Boolean flag = checkOutLoseInfo(table);
				if(flag){
					listBean = getTableLoseInfo(table);
					if(listBean.size() > 0){
						list = getID(table);
						String str;
						try {
							str = saveLoseInfo(listBean);
							if("".equals(str) || null == str){
								String pboNumber = startProcessOfLose(NAME, list);
								if(!"".equals(pboNumber)){
									MPMPrintProcessor.saveLosePboNumber(pboNumber, listBean);
									JOptionPane.showMessageDialog(getContentPane(), "已成功发起遗失申请流程！");
								}else{
									JOptionPane.showMessageDialog(getContentPane(), "遗失申请流程发起失败！");
								}
							}else{
								JOptionPane.showMessageDialog(getContentPane(), "所选择需要遗失申请的文件编号为" + str + "存在重复发起遗失,请重新选择！");
								return;
							}

						} catch (RemoteException e1) {

							e1.printStackTrace();
						} catch (InvocationTargetException e1) {

							e1.printStackTrace();
						}
					}else{
						JOptionPane.showMessageDialog(getContentPane(), "未选择需要进行遗失申请的文件！");
					}
				}else{
					JOptionPane.showMessageDialog(getContentPane(), "所选择的需要进行遗失申请的文件，存在遗失原因未填写！");
				}

			}
		});

		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	public void initUI(){
		this.setTitle("遗失申请");
		this.setSize(800,500);
		this.setVisible(true);
		this.setLocationRelativeTo(panel);

	}

	public Boolean checkOutLoseInfo(JTable table){
		 for (int i = 0; i < table.getRowCount(); i++) {
			  if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				  if("".equals(table.getModel().getValueAt(i, 7)) || null == table.getModel().getValueAt(i, 7)){
					  return false;
				  }
			  }
		 }
		return true;
	}

	private String Boolean(boolean b) {
		return null;
	}

	public List<CmPrintRecordInfoBean> getTableLoseInfo(JTable table){
		List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		try {
			String dept = getUserDepartment();
			String user = getUserName();
			String time = getCurrentTime();
			for (int i = 0; i < table.getRowCount(); i++) {
				if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
					 CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
					 cmPrintRecordInfoBean.setUuid((String)table.getModel().getValueAt(i, 1));
					 cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 2));
					 cmPrintRecordInfoBean.setFileNumber((String)table.getModel().getValueAt(i, 4));
					 cmPrintRecordInfoBean.setLoseReason((String)table.getModel().getValueAt(i, 7));
					 cmPrintRecordInfoBean.setCurrentDept(dept);
					 cmPrintRecordInfoBean.setCurrentUser(user);
					 cmPrintRecordInfoBean.setCurrentDate(time);
					 list.add(cmPrintRecordInfoBean);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getID(JTable table){
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < table.getRowCount(); i++) {
			if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				String id = (String)table.getModel().getValueAt(i, 2);
				list.add(id);
			}
		}
		return list;
	}

	public String saveLoseInfo(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.saveLoseInfo(list);
	}

	public String getUserDepartment() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserDepartment();
	}
	public String getUserName() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserName();
	}

	public String getCurrentTime(){
		Date nowDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String time = sdf.format(nowDate);
		return time;
	}

	public String startProcessOfLose(String name, List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.startProcessOfLose(name, list);
	}

	public List<CmPrintRecordInfoBean> updateBean(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.updateBean(list);
	}

	class MyCellEditor extends DefaultCellEditor {
		/**
		 *
		 */
		private static final long serialVersionUID = 8902702593369719968L;
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
