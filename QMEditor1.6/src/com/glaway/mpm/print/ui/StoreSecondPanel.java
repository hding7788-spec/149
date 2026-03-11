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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.UUID;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.ui.RecoverSecondPanel.MyBatchEditor;
import com.glaway.mpm.print.ui.RecoverSecondPanel.MyCellEditor;
import com.glaway.mpm.print.ui.RecoverSecondPanel.MyComEditor;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.DateChooser;

public class StoreSecondPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = -6440922908081421148L;
	private static final String NAME1 = "封存延迟申请";
	private static final String NAME2 = "封存遗失申请";
	private JComboBox box;
	private JButton delayButton;
	private JButton loseButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane tablePanel;
    private JPanel mainPanel;
	private List<String> list;
	private List<String> strList;
	private List<CmPrintRecordInfoBean> listBean;
	private JCheckBox allCheck;
	private JTextField textField;
	private String infor;
	private JButton closeButton;

	public StoreSecondPanel(String infor,String oid){
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
		strList = new ArrayList<String>();
		listBean = new ArrayList<CmPrintRecordInfoBean>();
		try {
			String dept = getUserDepartment();
			box = new JComboBox(new String[]{"封存中", "延迟", "遗失"});
			closeButton = new JButton("关闭");
			textField = new JTextField();

			strList = getList(infor);
			list = queryBarTableIDByDeptOfStore(strList, dept);
			listBean = queryStoreTable(list);
			delayButton = new JButton("延迟申请");
			loseButton = new JButton("遗失申请");
			allCheck = new JCheckBox();
			allCheck.setText("全选");
			String[] tableColumn = {"", "id", "uuid", "文件编号", "文件名称", "版本", "阶段标记", "密级", "领取部门", "条码", "状态"};
			String[][] tableData = {};
			tableModel = new DefaultTableModel(tableData, tableColumn);

			table = new JTable(tableModel);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));;
			table.setRowHeight(30);
			table.getSelectedRows();
			DateChooser dataChooser = DateChooser.getInstance("yyyy/MM/dd");
			dataChooser.register(textField);
			TableColumnModel tableColumnModel = table.getColumnModel();
			tableColumnModel.getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
			tableColumnModel.getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));
			tableColumnModel.getColumn(3).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(4).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(5).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(6).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(7).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(8).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(9).setCellEditor(new MyCellEditor(new JTextField()));
			tableColumnModel.getColumn(10).setCellEditor(new MyCellEditor(new JTextField()));

			/*tableColumnModel.getColumn(11).setCellEditor(new MyBatchEditor(new JTextField()));
			tableColumnModel.getColumn(12).setCellEditor(new MyBatchEditor(textField));
			tableColumnModel.getColumn(13).setCellEditor(new MyBatchEditor(new JTextField()));*/
			tableColumnModel.getColumn(3).setPreferredWidth(100);
			tableColumnModel.getColumn(4).setPreferredWidth(180);
			tableColumnModel.getColumn(5).setPreferredWidth(80);
			tableColumnModel.getColumn(6).setPreferredWidth(50);
			tableColumnModel.getColumn(7).setPreferredWidth(50);
			tableColumnModel.getColumn(8).setPreferredWidth(50);
			tableColumnModel.getColumn(9).setPreferredWidth(160);
			tableColumnModel.getColumn(10).setPreferredWidth(50);
			/*tableColumnModel.getColumn(11).setPreferredWidth(120);
			tableColumnModel.getColumn(12).setPreferredWidth(80);
			tableColumnModel.getColumn(13).setPreferredWidth(120);*/
			tableColumnModel.getColumn(0).setMaxWidth(0);
			tableColumnModel.getColumn(0).setMinWidth(0);
			tableColumnModel.getColumn(0).setPreferredWidth(0);
			tableColumnModel.getColumn(0).setResizable(false);
			tableColumnModel.getColumn(1).setMaxWidth(0);
			tableColumnModel.getColumn(1).setMinWidth(0);
			tableColumnModel.getColumn(1).setPreferredWidth(0);
			tableColumnModel.getColumn(1).setResizable(false);
			tableColumnModel.getColumn(2).setMaxWidth(0);
			tableColumnModel.getColumn(2).setMinWidth(0);
			tableColumnModel.getColumn(2).setPreferredWidth(0);
			tableColumnModel.getColumn(2).setResizable(false);


			DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
			render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			tableModel.setRowCount(0);

			for (int i = 0; i < listBean.size(); i++) {
				String id = listBean.get(i).getBarTableID();
				String uuid = listBean.get(i).getUuid();
			    String fileNumber = listBean.get(i).getFileNumber();
				String fileName = listBean.get(i).getFileName();
				String docVersion = listBean.get(i).getDocVersion();
				String phaseCode = listBean.get(i).getPhaseCode();
				String secret = listBean.get(i).getSecret();
				String getDept = listBean.get(i).getGetDept();
				String barCode = listBean.get(i).getBarCode();
				String middleStatus = listBean.get(i).getMiddleStatus();
//				String middleStatus = cmPrintRecordInfoBean.getMiddleStatus()
//				String delayReason = ("".equals(cmPrintRecordInfoBean.getDelayReason()) || null == cmPrintRecordInfoBean.getDelayReason()) ? "" : cmPrintRecordInfoBean.getDelayReason();
//				String delayDate = ("".equals(cmPrintRecordInfoBean.getDelayDate()) || null == cmPrintRecordInfoBean.getDelayDate()) ? "" : cmPrintRecordInfoBean.getDelayDate();
//				String loseReason = ("".equals(cmPrintRecordInfoBean.getLoseReason()) || null == cmPrintRecordInfoBean.getLoseReason()) ? "" : cmPrintRecordInfoBean.getLoseReason();
				String[] value = {Boolean(false), id, uuid, fileNumber, fileName, docVersion, phaseCode, secret, getDept, barCode, middleStatus};
				tableModel.addRow(value);
			//}
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
		grid.anchor = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(20, 5, 10, 5);
		mainPanel.add(closeButton, grid);

		/*grid.gridx = 2;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.NORTHWEST;
		grid.anchor = GridBagConstraints.WEST;
		grid.insets = new Insets(20, 5, 10, 5);
		mainPanel.add(loseButton, grid);

		grid.gridx = 4;
		grid.gridy = 0;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.NORTHWEST;
		grid.anchor = GridBagConstraints.WEST;
		grid.insets = new Insets(20, 5, 10, 5);
		mainPanel.add(closeButton, grid);*/

		grid.gridx =0;
		grid.gridy = 1;
		grid.weightx = 1;
		grid.weighty = 1;
		grid.gridwidth = 9;
		grid.gridheight = 1;
		grid.fill = GridBagConstraints.BOTH;
		grid.insets = new Insets(5, 5, 5, 5);
		mainPanel.add(tablePanel, grid);

		/*grid.gridx = 0;
		grid.gridy = 2;
		grid.gridwidth = 9;
		grid.gridheight = 1;
		grid.insets = new Insets(5, 5, 5, 5);
		mainPanel.add(allCheck, grid);*/

		this.add(mainPanel);
	}
	public void initUI(){

		this.setTitle("封存纸质文件");
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

		delayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CommonUIUtil.stopTableCellEditing(table);
				List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
				List<String> list = new ArrayList<String>();
				Boolean deptFlag = checkOutDelayDept(table);
				if(deptFlag){
					Boolean flag = checkOutDelayInfo(table);
					if(flag){
						listBean = getTableDelayInfo(table);
						for (int i = 0; i < listBean.size(); i++) {
							if(!"延迟".equals(listBean.get(i).getMiddleStatus())){
								JOptionPane.showMessageDialog(getContentPane(), "存在对非延迟状态文件发起延迟申请！");
								return;
							}
						}
						if(listBean.size() > 0){
							list = getTableInfoUuid(table);
							String str;
							try {
								str = saveToStoreTableDelayInfo(listBean);
								if("".equals(str) || null == str){
								String pboNumber = startProcessOfDelay(NAME1, list);
								updatePBONumberToDB(list, pboNumber);
									if("".equals(pboNumber) || null == pboNumber){
										JOptionPane.showMessageDialog(getContentPane(), "流程启动失败！");
									}else{
										JOptionPane.showMessageDialog(getContentPane(), "已成功启动延迟流程！");
									}
								}else{
									JOptionPane.showMessageDialog(getContentPane(), "所选择需要延迟的文件编号为" + str + "已存在延迟或遗失申请中,请重新发起！");
									return;
								}
							} catch (RemoteException e1) {
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								e1.printStackTrace();
							}
						}else{
							JOptionPane.showMessageDialog(getContentPane(), "未选择需要延迟的文件！");
							return;
						}
					}else{
						JOptionPane.showMessageDialog(getContentPane(), "存在延迟封存原因或者时间未填写!");
						return;
					}
				}else{
					JOptionPane.showMessageDialog(getContentPane(), "存在对非本部门文件发起延迟申请!");
					return;
				}
			}
		});

		loseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CommonUIUtil.stopTableCellEditing(table);
				List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
				List<String> list = new ArrayList<String>();
				Boolean deptFlag = checkOutLoseDept(table);
				if(deptFlag){
					Boolean flag = checkOutLoseInfo(table);
					if(flag){
						listBean = getTableLoseInfo(table);
						for (int i = 0; i < listBean.size(); i++) {
							if(!"遗失".equals(listBean.get(i).getMiddleStatus())){
								JOptionPane.showMessageDialog(getContentPane(), "存在对非遗失状态文件发起遗失申请！");
								return;
							}
						}
						if(listBean.size() > 0){
							list = getTableInfoID(table);
							String pboNumber;
							String str;
							try {
								str = saveLoseInfoOfStore(listBean);
								if("".equals(str) || null == str){
									pboNumber = startProcessOfLose(NAME2, list);
									if(!"".equals(pboNumber)){
										MPMPrintProcessor.saveLosePboNumber(pboNumber, listBean);
										JOptionPane.showMessageDialog(getContentPane(), "已成功启动遗失流程！");
									}else{
										JOptionPane.showMessageDialog(getContentPane(), "流程启动失败！");
									}
								}else{
									JOptionPane.showMessageDialog(getContentPane(), "所选择需要遗失的文件编号为" + str + "已存在延迟或遗失申请中,请重新发起！");
									return;
								}
							} catch (RemoteException e1) {
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								e1.printStackTrace();
							}
						}else{
							JOptionPane.showMessageDialog(getContentPane(), "未选择需要遗失的文件！");
							return;
						}
					}else{
						JOptionPane.showMessageDialog(getContentPane(), "存在遗失原因未填写!");
						return;
					}
				}else{
					JOptionPane.showMessageDialog(getContentPane(), "存在对非本部门文件发起遗失申请!");
					return;
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
	public List<CmPrintRecordInfoBean> getTableDelayInfo(JTable table){

		   List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
		   for (int i = 0; i < table.getRowCount(); i++) {
			   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
			       CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
			       cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 1));
			       cmPrintRecordInfoBean.setUuid((String)table.getModel().getValueAt(i, 2));
			       cmPrintRecordInfoBean.setFileNumber((String)table.getModel().getValueAt(i, 3));
			       cmPrintRecordInfoBean.setBarCode((String)table.getModel().getValueAt(i, 9));
			       cmPrintRecordInfoBean.setMiddleStatus((String)table.getModel().getValueAt(i, 10));
			       cmPrintRecordInfoBean.setDelayReason((String)table.getModel().getValueAt(i, 11));
			       cmPrintRecordInfoBean.setDelayDate((String)table.getModel().getValueAt(i, 12));
				   list.add(cmPrintRecordInfoBean);
			   }
		   }
		   return list;

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
					 cmPrintRecordInfoBean.setUuid(UUID.randomUUID().toString());
					 cmPrintRecordInfoBean.setBarTableID((String)table.getModel().getValueAt(i, 1));
					 cmPrintRecordInfoBean.setFileNumber((String)table.getModel().getValueAt(i, 3));
					 cmPrintRecordInfoBean.setBarCode((String)(table.getModel().getValueAt(i, 9)));
					 cmPrintRecordInfoBean.setMiddleStatus((String)table.getModel().getValueAt(i, 10));
					 cmPrintRecordInfoBean.setLoseReason((String)table.getModel().getValueAt(i, 13));
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
	public Boolean checkOutDelayInfo(JTable table){
		 for (int i = 0; i < table.getRowCount(); i++) {
			  if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				  if("".equals(table.getModel().getValueAt(i, 11)) || null == table.getModel().getValueAt(i, 11)
						  || "".equals(table.getModel().getValueAt(i, 12)) || null == table.getModel().getValueAt(i, 12)){
					  return false;
				  }
			  }
		 }
		return true;
	}
	public Boolean checkOutLoseInfo(JTable table){
		 for (int i = 0; i < table.getRowCount(); i++) {
			  if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
				  if("".equals(table.getModel().getValueAt(i, 13)) || null == table.getModel().getValueAt(i, 13)){
					  return false;
				  }
			  }
		 }
		return true;
	}
	public Boolean checkOutDelayDept(JTable table){
		 String dept;
		try {
			dept = getUserDepartment();
			 for (int i = 0; i < table.getRowCount(); i++) {
				 if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
					 if(!dept.equals(table.getModel().getValueAt(i, 8))){
						 return false;
					 }
				 }
			 }
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		 return true;
	}
	public Boolean checkOutLoseDept(JTable table){
		 String dept;
		try {
			dept = getUserDepartment();
			 for (int i = 0; i < table.getRowCount(); i++) {
				 if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
					 if(!dept.equals(table.getModel().getValueAt(i, 8))){
						 return false;
					 }
				 }
			 }
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		 return true;
	}

	public List<String> getTableInfoUuid(JTable table){
		   List<String> list = new ArrayList<String>();
		   for (int i = 0; i < table.getRowCount(); i++) {
			   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
			       String uuid = ((String)(table.getModel().getValueAt(i, 2)));
				   list.add(uuid);
			   }
		   }
		   return list;
	}
	public List<String> getTableInfoID(JTable table){
		   List<String> list = new ArrayList<String>();
		   for (int i = 0; i < table.getRowCount(); i++) {
			   if("true".equals(String.valueOf(table.getModel().getValueAt(i, 0)))){
			       String uuid = ((String)(table.getModel().getValueAt(i, 1)));
				   list.add(uuid);
			   }
		   }
		   return list;
	}
	public String saveLoseInfoOfStore(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.saveLoseInfoOfStore(list);
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
	public List<String> getList(String str)
	{
		List<String> list = new ArrayList<String>();
		String[] temp = str.split(":");
		for (String string : temp) {
			list.add(string);
		}
	    return list;
	}
	public String startProcessOfDelay(String name, List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.startProcessOfDelay(name, list);
	}
	public String startProcessOfLose(String name, List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.startProcessOfLose(name, list);
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
				isClick = (Boolean) table.getModel().getValueAt(clickRow, 0);
			}
			if(isClick){
				for(int i=0;i<table.getRowCount();i++){ //批量编辑
					String isChecked = String.valueOf(table.getValueAt(i, 0));
					if("true".equals(isChecked)){
						table.getModel().setValueAt(text, i, currentCol);
					}
				}
			}
			return text;
		}
	}
	public List<CmPrintRecordInfoBean> queryStoreTable(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryStoreTable(list);
	}
	public List<String> queryBarTableIDByDeptOfStore(List<String> list, String dept) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryBarTableIDByDeptOfStore(list, dept);
	}
	public String saveToStoreTableDelayInfo(List<CmPrintRecordInfoBean> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.saveToStoreTableDelayInfo(list);
	}
	public void updatePBONumberToDB(List<String> list, String pboNumber) throws RemoteException, InvocationTargetException{
		PrintToWCIntf.updatePBONumberToDBByStore(list, pboNumber);
	}
	class MyComEditor extends DefaultCellEditor {


		/**
		 *
		 */
		private static final long serialVersionUID = -5005348929536969485L;
		private JComboBox comboBox;
		private int currentCol = -1;
		private int currentRow = -1;
		MyComEditor(JComboBox comboBox) {
			super(comboBox);
			this.comboBox = comboBox;
		}
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.currentCol = column;
			this.currentRow = row;
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}

		public Object getCellEditorValue() {
			String text = this.comboBox.getSelectedItem().toString();
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
						table.getModel().setValueAt(text, i, currentCol);
					}
				}
			}
			return text;
		}
	}

}
