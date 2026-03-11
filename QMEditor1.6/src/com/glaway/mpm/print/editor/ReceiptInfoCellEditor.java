package com.glaway.mpm.print.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.helper.MPMPrintHelper;
import com.glaway.mpm.print.ui.ReceiptInfoDialog;

public class ReceiptInfoCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = -1077429746776002059L;
	private JButton button;
	private JTable table;

	public ReceiptInfoCellEditor(JTable table) {
		super(new JTextField());
		this.table = table;
		this.setClickCountToStart(1);
		button = new JButton("查 看");
		initListener();
	}

	private void initListener() {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				Object qrCodeNumber = table.getValueAt(row, 15);//20170303 jiangyixing
				List<CmPrintInfoBean> cmPrintInfoBeans = MPMPrintHelper.getDistributeAndRecoverInfo(String.valueOf(qrCodeNumber));
				new ReceiptInfoDialog(null, true, cmPrintInfoBeans);
			}
		});
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		return button;
	}


}
