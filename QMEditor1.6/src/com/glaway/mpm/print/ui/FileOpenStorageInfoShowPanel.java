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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
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
/**
 *
 * @author lkc
 *
 */
public class FileOpenStorageInfoShowPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = -3581419922430873229L;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane tablePanel;
	private JPanel panel;
	private List<String> barCodeList;
	private List<String> strList;
	private List<CmPrintRecordInfoBean> listBean;
	private String infor;
	private JButton closeButton;
	private String oid;//流程OID
	private Boolean isSubmit;//判断流程是否是提交活动
	private JCheckBox checkAll;
	private JButton okButton;
	private String scanInput;
	public FileOpenStorageInfoShowPanel(String infor,String oid){
		 this.infor = infor;
		 this.oid = oid;
		 initComponents();
		 initLayout();
		 initListener();
	     initUI();
	     setEnabled();
	}
	public void initComponents(){
		tablePanel = new JScrollPane();
		panel = new JPanel();
		barCodeList = new ArrayList<String>();
		strList = new ArrayList<String>();
		listBean = new ArrayList<CmPrintRecordInfoBean>();
		closeButton = new JButton("关闭");
		checkAll = new JCheckBox();
	    checkAll.setText("全选");
	    okButton = new JButton("启封确定");
		try {
			strList = getList(infor);
			listBean = getStoreInfo(strList);
			isSubmit = isSubmitDelayByWfProcessOidAndActivityNames(oid, "启封确认");
			String[] tableColumn = {"ID","","文件编号", "文件名称", "文件状态", "版本", "阶段标记", "密级", "领取部门", "封存时间", "条码"};
			String[][] tableData = {};
			tableModel = new DefaultTableModel(tableData, tableColumn){
				/**
				 *
				 */
				private static final long serialVersionUID = 1510415043031621829L;

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
			table.getColumnModel().getColumn(1).setResizable(false);

			table.getColumnModel().getColumn(2).setPreferredWidth(200);
			table.getColumnModel().getColumn(3).setPreferredWidth(250);
			table.getColumnModel().getColumn(4).setPreferredWidth(50);
			table.getColumnModel().getColumn(5).setPreferredWidth(50);
			table.getColumnModel().getColumn(6).setPreferredWidth(50);
			table.getColumnModel().getColumn(7).setPreferredWidth(50);
			table.getColumnModel().getColumn(8).setPreferredWidth(50);
			table.getColumnModel().getColumn(9).setPreferredWidth(50);
			table.getColumnModel().getColumn(10).setPreferredWidth(200);
			DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
			render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			tableModel.setRowCount(0);
			if(listBean.size() > 0){
				for (CmPrintRecordInfoBean cmPrintRecordQueryBean : listBean) {
					String barCodeTableID = cmPrintRecordQueryBean.getBarTableID();
					String fileNumber = cmPrintRecordQueryBean.getFileNumber();
					String fileName = cmPrintRecordQueryBean.getFileName();
					String fileStatus = cmPrintRecordQueryBean.getMiddleStatus();
					String docVersion = cmPrintRecordQueryBean.getDocVersion();
					String phaseCode = cmPrintRecordQueryBean.getPhaseCode();
					String secret = cmPrintRecordQueryBean.getSecret();
					String getDept = cmPrintRecordQueryBean.getGetDept();
					String storeTime = cmPrintRecordQueryBean.getStoreTime();
					String barCode = cmPrintRecordQueryBean.getBarCode();
					String[] value = {barCodeTableID,null,fileNumber, fileName, fileStatus, docVersion, phaseCode, secret, getDept, storeTime, barCode};
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

		grid.gridx = 0;
		grid.gridy = 2;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.weightx = 0;
		grid.weighty = 0;
		grid.anchor = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(5, 10, 5, 5);
		panel.add(checkAll, grid);

		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		grid.gridx = 4;
		grid.gridy = 2;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.weightx = 0;
		grid.weighty = 0;
		grid.anchor = GridBagConstraints.NORTHWEST;
		grid.insets = new Insets(5, (width*3)/4, 5, 10);
		panel.add(okButton, grid);

		grid.gridx = 5;
		grid.gridy = 2;
		grid.gridwidth = 1;
		grid.gridheight = 1;
		grid.weightx = 0;
		grid.weighty = 0;
		grid.fill = GridBagConstraints.NORTHEAST;
		grid.anchor = GridBagConstraints.WEST;
		grid.insets = new Insets(5, 25, 5, 0);
		panel.add(closeButton, grid);

		closeButton.setPreferredSize(new Dimension(120, 25));
		okButton.setPreferredSize(new Dimension(100, 25));
		this.add(panel);

	}
	public void setEnabled(){
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
									String QRName = CommonUtil.objectToString(table.getValueAt(row, 10));
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
						JOptionPane.showMessageDialog(getContentPane(), "已确定启封！");
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
	public List<CmPrintRecordInfoBean> getStoreInfo(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.storeInfo(list);
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
		this.setTitle("查看启封文件信息");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize(width, height - 40);
		//this.setSize(1300, 600);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
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
	public List<String> getTableInfoID(JTable table){
		   List<String> list = new ArrayList<String>();
		   for (int i = 0; i < table.getRowCount(); i++) {
			   boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(i, 1)));
			   if(isSelect){
				   String id = ((String)(table.getModel().getValueAt(i, 0)));
				   list.add(id);
				   table.getModel().setValueAt("已入库", i, 4);
			   }
		   }
		   return list;
	}
	public void updateStoreStatusBySelf(List<String> list) throws RemoteException, InvocationTargetException{
		PrintToWCIntf.updateFileStatusOfOpenStore(list);
	}
}
