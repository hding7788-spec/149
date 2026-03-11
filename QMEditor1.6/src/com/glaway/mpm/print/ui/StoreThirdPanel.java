package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.LocalPrintUtil;
import com.glaway.mpm.util.CommonUIUtil;

import ext.casc.util.CommonUtil;

public class StoreThirdPanel extends JFrame{

	/**
	 *
	 */
	private static final long serialVersionUID = -7499628936679285989L;
	private JCheckBox checkAll;
	private JButton completeButton;
	private JButton okButton;
	private JButton closeButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private JLabel label;
	private JPanel centerPanel;
	private JScrollPane tablePanel;
	private List<String> strList;
	private String infor;
	private String oid;//流程OID
	private Boolean isSubmit;//判断流程是否是提交活动
	private String scanInput;
	public StoreThirdPanel(String infor,String oid){
		this.infor = infor;
		this.oid = oid;
		initComponents();
		setUIValue();
		initLayout();
		initListener();
		initUI();
		setEnabled();
	}
	public void initComponents(){
		centerPanel = new JPanel();
		tablePanel = new JScrollPane();
		strList = new ArrayList<String>();
	    strList = getList(infor);
	    isSubmit = isSubmitDelayByWfProcessOidAndActivityNames(oid, "执行封存");

	    checkAll = new JCheckBox();
	    checkAll.setText("全选");
	    label = new JLabel("封存文件列表:");

	    completeButton = new JButton("过滤");
	    okButton = new JButton("封存确定");
	    closeButton = new JButton("关闭");

	    String[] tableColumn = {"ID","", "文件编号", "文件名称","状态", "版本", "阶段标记", "密级", "领取部门", "条码","封存申请人","封存时间","封存部门"};
	    String[][] tableData = {};
	    tableModel = new DefaultTableModel(tableData, tableColumn){

	    	private static final long serialVersionUID = -7469201343628813334L;

	    	@Override
	    	public boolean isCellEditable(int row, int column) {
	    		if(column == 1){
	    			return true;
	    		}
	    		return false;
	    	}

	    };
	    table = new JTable(tableModel);
	    table.getTableHeader().setReorderingAllowed(false);
	    table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
	    table.setRowHeight(30);
	    table.getColumnModel().getColumn(0).setMaxWidth(0);
	    table.getColumnModel().getColumn(0).setMinWidth(0);
	    table.getColumnModel().getColumn(0).setPreferredWidth(0);
	    table.getColumnModel().getColumn(0).setResizable(false);

	    table.getColumnModel().getColumn(1).setCellEditor(table.getDefaultEditor(Boolean.class));
	    table.getColumnModel().getColumn(1).setCellRenderer(table.getDefaultRenderer(Boolean.class));
	    table.getColumnModel().getColumn(1).setPreferredWidth(25);
	    table.getColumnModel().getColumn(2).setPreferredWidth(200);
	    table.getColumnModel().getColumn(3).setPreferredWidth(250);
	    table.getColumnModel().getColumn(4).setPreferredWidth(50);
	    table.getColumnModel().getColumn(5).setPreferredWidth(30);
	    table.getColumnModel().getColumn(6).setPreferredWidth(50);
	    table.getColumnModel().getColumn(7).setPreferredWidth(50);
	    table.getColumnModel().getColumn(8).setPreferredWidth(80);
	    table.getColumnModel().getColumn(9).setPreferredWidth(200);
	    table.getColumnModel().getColumn(10).setPreferredWidth(50);
	    table.getColumnModel().getColumn(11).setPreferredWidth(50);
	    DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
	    render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
	    tableModel.setRowCount(0);
	    tablePanel.setViewportView(table);
	}
	private void setUIValue() {
		try {
			List<CmPrintRecordInfoBean> listBeans  = getQueryStoreTableIsDelay(strList);
			if(listBeans.size() > 0){
				tableModel.setRowCount(0);
				for (CmPrintRecordInfoBean cmPrintRecordQueryBean : listBeans) {
					String barCodeTableID = cmPrintRecordQueryBean.getBarTableID();
					String fileNumber = cmPrintRecordQueryBean.getFileNumber();
					String fileName = cmPrintRecordQueryBean.getFileName();
					String fileStatus = cmPrintRecordQueryBean.getMiddleStatus();
					String docVersion = cmPrintRecordQueryBean.getDocVersion();
					String phaseCode = cmPrintRecordQueryBean.getPhaseCode();
					String secret = cmPrintRecordQueryBean.getSecret();
					String getDept = cmPrintRecordQueryBean.getGetDept();
					String barCode = cmPrintRecordQueryBean.getBarCode();
					String storeUser = cmPrintRecordQueryBean.getStoreUser();
					String storeDept = cmPrintRecordQueryBean.getStoreDept();
					String storeDate = cmPrintRecordQueryBean.getStoreDate();
					String[] value = {barCodeTableID,Boolean(false),fileNumber, fileName,fileStatus, docVersion, phaseCode, secret, getDept, barCode,storeUser,storeDate,storeDept};
					tableModel.addRow(value);
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initLayout(){
		centerPanel.setLayout(new GridBagLayout());
		GridBagConstraints centerGrid = new GridBagConstraints();

		centerGrid.gridx = 0;
		centerGrid.gridy = 0;
		centerGrid.gridwidth = 6;
		centerGrid.gridheight = 1;
		centerGrid.weightx = 1;
		centerGrid.weighty = 1;
		centerGrid.fill = GridBagConstraints.BOTH;
		centerGrid.anchor = GridBagConstraints.NORTHWEST;
		centerPanel.add(tablePanel, centerGrid);

		centerGrid.gridx = 0;
		centerGrid.gridy = 2;
		centerGrid.gridwidth = 1;
		centerGrid.gridheight = 1;
		centerGrid.weightx = 0;
		centerGrid.weighty = 0;
		centerGrid.anchor = GridBagConstraints.NORTHWEST;
		centerGrid.insets = new Insets(5, 10, 5, 5);
		centerPanel.add(checkAll, centerGrid);

		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		centerGrid.gridx = 4;
		centerGrid.gridy = 2;
		centerGrid.gridwidth = 1;
		centerGrid.gridheight = 1;
		centerGrid.weightx = 0;
		centerGrid.weighty = 0;
		centerGrid.anchor = GridBagConstraints.NORTHWEST;
		centerGrid.insets = new Insets(5, (width*3)/4, 5, 10);
		centerPanel.add(okButton, centerGrid);

		centerGrid.gridx = 5;
		centerGrid.gridy = 2;
		centerGrid.gridwidth = 1;
		centerGrid.gridheight = 1;
		centerGrid.weightx = 0;
		centerGrid.weighty = 0;
		centerGrid.fill = GridBagConstraints.NORTHEAST;
		centerGrid.anchor = GridBagConstraints.WEST;
		centerGrid.insets = new Insets(5, 25, 5, 0);
		centerPanel.add(closeButton, centerGrid);

		label.setPreferredSize(new Dimension(120, 25));
		completeButton.setPreferredSize(new Dimension(100, 25));
		okButton.setPreferredSize(new Dimension(100, 25));
		closeButton.setPreferredSize(new Dimension(100, 25));
		this.add(centerPanel);
	}
	public void setEnabled(){
		completeButton.setEnabled(isSubmit);
		completeButton.setVisible(false);
		okButton.setEnabled(isSubmit);
		checkAll.setEnabled(isSubmit);
		table.setEnabled(isSubmit);
	}

	public void initListener(){
		//扫码监听 start
				class keyboardListener implements KeyListener {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							boolean flag = LocalPrintUtil.checkStr(scanInput);//true = 扫文件 ;false = 扫工牌
							Map<String, String> map = new HashMap<String, String>();
							if(flag){//扫文件
								map = LocalPrintUtil.buildFileMap(scanInput);
								String QRCode = map.get("DABH");
								if(QRCode == null || "".equals(QRCode)){
									return;
								}
								for (int row = 0; row < table.getRowCount(); row++) {
									String QRName = CommonUtil.objectToString(table.getValueAt(row, 9));
									if(QRCode.equals(QRName)){
										boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
										if(isSelect){
											table.setValueAt(false, row, 1);
										}else{
											table.setValueAt(true, row, 1);
										}
									}
								}
							}
							scanInput = "";
						} else {
							String str =CommonUtil.objectToString(e.getKeyChar());
							String regEx="[`~!@#$%^&*()+-=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
							Pattern p=Pattern.compile(regEx);
							Matcher m=p.matcher(str);
							if(str.matches(".*[a-zA-z].*") || str.matches("^[0-9]*$") || m.find()){
								scanInput = scanInput + str;
							}
						}
					}
					@Override
					public void keyReleased(KeyEvent e) {}
					@Override
					public void keyTyped(KeyEvent e) {}
				}
				table.addKeyListener(new keyboardListener());
				checkAll.addKeyListener(new keyboardListener());
				completeButton.addKeyListener(new keyboardListener());
				okButton.addKeyListener(new keyboardListener());
				closeButton.addKeyListener(new keyboardListener());
				//扫码监听 end

		checkAll.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				CommonUIUtil.stopTableCellEditing(table);
				if(checkAll.isSelected()){
					for(int i=0;i<table.getRowCount();i++){
						table.getModel().setValueAt(true, i, 1);
					}
				}else{
					for(int i=0;i<table.getRowCount();i++){
						table.getModel().setValueAt(false, i, 1);
					}
				}
			}
		});

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CommonUIUtil.stopTableCellEditing(table);
				List<String> list = getTableInfoID(table);
				if(list != null && list.size() > 0){
					try {
						updateStoreStatusBySelf(list);
						JOptionPane.showMessageDialog(getContentPane(), "已确定封存！");
					} catch (RemoteException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}else{
					JOptionPane.showMessageDialog(getContentPane(), "未选择需要封存的文件，请选择后再点击封存确定！");
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

	public void initUI(){
		this.setTitle("封存文件确认");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize(width, height - 40);
		//this.setSize(1150, 600);
		//this.setLocation(500, 0);
		this.setVisible(true);
	}
	public List<CmPrintRecordInfoBean> getQueryStoreTableIsDelay(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryStoreTableIsDelay(list);
	}
	public List<String> queryBarTableID(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryBarTableID(list);
	}
	public CmPrintRecordInfoBean getInfoByBarCode(String barCode) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getInfoByBarCode(barCode);
	}
	public CmPrintRecordInfoBean getInfoByUserName(String userName) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getInfoByUserName(userName);
	}
	public Boolean checkUser(String userName, List<String> strList) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.checkUserByStore(userName, strList);
	}
	public String getUserDepartment(String userName) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getUserDepartment(userName);
	}
	public List<String> saveInfoToDBOfStore(List<String> strList, String userName, String dept, String date) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.saveInfoToDBOfStore(strList, userName, dept, date);
	}
	public void updateStoreStatusBySelf(List<String> list) throws RemoteException, InvocationTargetException{
		PrintToWCIntf.updateStoreStatusBySelf(list);
	}

	public String getCurrentTime(){
		Date nowDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String time = sdf.format(nowDate);
		return time;
	}
	public List<String> getTableInfoID(JTable table){
		   List<String> list = new ArrayList<String>();
		   for (int i = 0; i < table.getRowCount(); i++) {
			   boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(i, 1)));
			   if(isSelect){
				   String id = ((String)(table.getModel().getValueAt(i, 0)));
				   list.add(id);
				   table.getModel().setValueAt("已封存", i, 4);
			   }
		   }
		   return list;
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
	protected String Boolean(boolean b) {
		return null;
	}
	public boolean isSubmitDelayByWfProcessOidAndActivityNames(String oid,String activityNames){
		boolean flag = false;
		try {
			return PrintToWCIntf.isSubmitDelayByWfProcessOidAndActivityNames(oid,activityNames);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return flag;
	}
}
