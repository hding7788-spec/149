package com.glaway.mpm.print.ui;

import java.awt.Frame;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.util.CommonUIUtil;

public class ReceiptInfoDialog extends JDialog {

	private static final long serialVersionUID = -8953674227209577188L;
	private List<CmPrintInfoBean> printInfoBeans;
	private JTable table;
	private DefaultTableModel tableModel;
	/** 表头 */
	private String[] tableColumnName;
	private Class<?>[] tableColumnClass;
	/** 可编辑的列 */
	private int[] editableColumns = new int[] { };

	public ReceiptInfoDialog(Frame frame, boolean modal, List<CmPrintInfoBean> printInfoBeans) {
		super(frame, modal);
		this.printInfoBeans = printInfoBeans;
		initComponents();
		loadData();
		initUI();
	}

	private void loadData() {
		tableModel.setRowCount(0);
		if (printInfoBeans != null) {
			int i = 0;
			for(CmPrintInfoBean printInfoBean : printInfoBeans){
				CommonUIUtil.addOneRow(tableModel);
				table.setValueAt(printInfoBean.getReceipter(), i, 0);
				table.setValueAt(printInfoBean.getReceiptDate(), i, 1);
				table.setValueAt(printInfoBean.getReceiptDept(), i, 2);
				table.setValueAt(printInfoBean.getRecoverPerson(), i, 3);
				table.setValueAt(printInfoBean.getRecoverDate(), i, 4);
				table.setValueAt(printInfoBean.getRecoverDept(), i, 5);
				table.setValueAt(printInfoBean.getRecoverRemark(), i, 6);
				table.setValueAt(printInfoBean.getReceivePerson(), i, 7);
				table.setValueAt(printInfoBean.getReceiveDate(), i, 8);
				table.setValueAt(printInfoBean.getReceiveDept(), i, 9);
				i++;
			}
		}
	}

	private void initComponents() {
		tableColumnClass = new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };
		tableColumnName = new String[] {"领取人", "领取时间", "领取部门", "退回人", "退回时间", "退回部门", "退回备注", "收件人", "收件时间", "回收份数" };
		editableColumns = new int[] { };

		tableModel = new CommonTableModel(tableColumnName, tableColumnClass, editableColumns);
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setModel(tableModel);

		table.setRowHeight(30);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		setLayout(new VFlowLayout(0, 0, 0, true, true));
		add(scrollPane);
	}

	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("领取情况");
		setSize(800, 480);
        CommonUIUtil.setMiddleOnScreenWithDialog(this);
        setVisible(true);
	}

}
