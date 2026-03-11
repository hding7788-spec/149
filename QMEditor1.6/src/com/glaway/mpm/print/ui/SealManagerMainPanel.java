package com.glaway.mpm.print.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.data.CmSealBean;
import com.glaway.mpm.print.service.PrintToWCIntf;

/**
 *
 * @author likaicheng
 *
 */

public class SealManagerMainPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 4038180634264298786L;
	private JLabel sealLabel;
	private JTextField sealText;
	private JButton addButton;
	private JButton alertButton;
	private JButton deleteButton;
	private JButton selectButton;
	private JButton closeButton;
	private JTable table;
	private DefaultTableModel tableModel;
	private JPanel topPanel;
	private JScrollPane downPanel;

	private List<CmSealBean> list = new ArrayList<CmSealBean>();
	public SealManagerMainPanel(){

		topPanel = new JPanel();
		downPanel =new JScrollPane();
		sealLabel = new JLabel("印章:");
		sealText = new JTextField("");
		selectButton = new JButton("查询");
		addButton = new JButton("添加");
		alertButton = new JButton("修改");
		deleteButton = new JButton("删除");
		closeButton = new JButton("关闭");

		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();
		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.insets = new Insets(10, 10, 30, 10);
		topPanel.add(sealLabel, topGrid);
		topGrid.gridx = 1;
		topPanel.add(sealText, topGrid);
		topGrid.gridx = 2;
		topPanel.add(selectButton, topGrid);

		topGrid.gridx = 0;
		topGrid.gridy = 2;
		topPanel.add(addButton, topGrid);
		topGrid.gridx = 1;
		topGrid.gridy = 2;
		topPanel.add(alertButton, topGrid);
		topGrid.gridx = 2;
		topGrid.gridy = 2;
		topPanel.add(deleteButton, topGrid);
		topGrid.gridx = 3;
		topGrid.gridy = 2;
		topPanel.add(closeButton, topGrid);


		addButton.setPreferredSize(new Dimension(100, 25));
		sealText.setPreferredSize(new Dimension(100, 25));
		alertButton.setPreferredSize(new Dimension(100, 25));
		selectButton.setPreferredSize(new Dimension(100, 25));
		deleteButton.setPreferredSize(new Dimension(100, 25));
		closeButton.setPreferredSize(new Dimension(100, 25));


		String[] tableColumn = {"uuid", "序号", "印章"};
		String[][] tableData = {};
		tableModel = new DefaultTableModel(tableData,tableColumn)
		{
			/**
			 *
			 */
			private static final long serialVersionUID = -3075047311143099965L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
		table.setRowHeight(30);
		DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(this.getClass());
		render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
//		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
//		table.setRowSorter(sorter);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);
		table.getColumnModel().getColumn(0).setResizable(false);

		downPanel.setViewportView(table);

		//选择按钮的监听
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.setRowCount(0);
				String sealName = sealText.getText();
				try {
					list = getSelectSealToDB(sealName);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
				for (CmSealBean bean : list) {
					String uuid = bean.getGwKeyId();
					String number = bean.getNumber();
					String name = bean.getName();
					String[] value = {uuid, number, name};
					tableModel.addRow(value);
				}

			}
		});

		//添加按钮的监听
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String number = queryMaxNumber();
					if("".equals(number) || null == number){
						number = "1";
					}else{
						number = String.valueOf(Integer.parseInt(queryMaxNumber()) + 1);
					}
					SealManagerAddTableRow addTableRowDialog= new SealManagerAddTableRow(number);
					addTableRowDialog.showDialog(SealManagerMainPanel.this, "新增印章");
					if(addTableRowDialog.selectedButton()){
						String uuidValue = addTableRowDialog.getUuidValue();
						String sealValue = addTableRowDialog.getSealValue();
						if (!"".equals(sealValue) || null != sealValue) {
							String[] newRow = new String[3];
							newRow[0] = uuidValue;
							newRow[1] = number;
							newRow[2] = sealValue;
							tableModel.addRow(newRow);
						}
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}

			}
		});

		//修改按钮的监听
		alertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selected = table.getSelectedRow();
				if(selected < 0){
					JOptionPane.showMessageDialog(SealManagerMainPanel.this,"未选择需要修改的行！");
					return;
				}else{
					String uuid = String.valueOf(tableModel.getValueAt(selected, 0));
					String id = String.valueOf(tableModel.getValueAt(selected, 1));
					String value = String.valueOf(tableModel.getValueAt(selected, 2));
					SealManagerAlertTableRow alertTableRowDialog= new SealManagerAlertTableRow(uuid, id, value);
					alertTableRowDialog.showDialog(SealManagerMainPanel.this, "修改印章");
				    if(alertTableRowDialog.selectedButton()){
					    String sealValue = alertTableRowDialog.getValue();
						if (!sealValue.equals("")) {
							tableModel.setValueAt(sealValue, selected, 2);
						}
				    }
				}
			}
		});

		//删除的监听
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selecteds = table.getSelectedRows();
			if (selecteds.length != 0) {
				String str = e.getActionCommand();
				if(str == "删除"){
					int result = JOptionPane.showConfirmDialog(getContentPane(),"是否确定删除当前印章？","",JOptionPane.YES_NO_OPTION);
					if(result == JOptionPane.YES_OPTION){
						List<String> list = new ArrayList<String>();
						for (int i = 0; i < selecteds.length; i++) {
							String uuid = (String) tableModel.getValueAt(selecteds[i], 0);
							list.add(uuid);
						}
						Boolean flag;
						try {
							flag = deleteSealToDB(list);
							if(flag == true){
								for (int i = 0; i < selecteds.length; i++) {
									tableModel.removeRow(table.getSelectedRow());
								}
								int deleteRow = table.getRowCount();
								for (int j = 0; j < deleteRow; j++) {
									tableModel.setValueAt(j + 1, j, 1);
								}
								List<CmSealBean> listBean = getTableInfo(table);
								updateNumberToDB(listBean);
								JOptionPane.showMessageDialog(null, "删除成功！");
							}else{
								JOptionPane.showMessageDialog(null, "删除失败！");
								return;
							}
						} catch (RemoteException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}

					}
				}
				}else{
					JOptionPane.showMessageDialog(SealManagerMainPanel.this, "未选择要删除的行");
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

		this.add(topPanel, BorderLayout.NORTH);
		this.add(downPanel, BorderLayout.CENTER);
		this.setTitle("印章管理");
		this.setSize(800, 600);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}


	private List<CmSealBean> getSelectSealToDB(String sealName) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.selectSeal(sealName);
	}
	private List<CmSealBean> getTableInfo(JTable table){
		List<CmSealBean> listBean = new ArrayList<CmSealBean>();
		for (int i = 0; i < table.getRowCount(); i++) {
			CmSealBean cmSealBean = new CmSealBean();
			cmSealBean.setGwKeyId((String)(table.getModel().getValueAt(i, 0)));
			cmSealBean.setNumber(String.valueOf(table.getModel().getValueAt(i, 1)));
			listBean.add(cmSealBean);
		}
		return listBean;
	}
	private Boolean deleteSealToDB(List<String> list) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.deleteSeal(list);
	}
	private void updateNumberToDB(List<CmSealBean> listBean) throws RemoteException, InvocationTargetException{
		PrintToWCIntf.updateNumberToDB(listBean);
	}
	private String queryMaxNumber() throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryMaxNumber();
	}
}
