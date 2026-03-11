package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.print.listener.ScanInputKeyListener;

public class DistributionFileInfoPanel extends JPanel {

	private static final long serialVersionUID = 6086924870364942386L;
	/** 领取人 */
	private JLabel receiptPersonLabel;
	/** 领取时间 */
	private JLabel receiptDateLabel;
	/** 领取部门 */
	private JLabel receiptDeptLabel;
	/** 领取人 */
	private JTextField receiptPersonValue;
	/** 领取时间 */
	private JTextField receiptDateValue;
	/** 领取部门 */
	private JTextField receiptDeptValue;
	/** 领取人ID */
	private JTextField receiptPersonIDValue;

	public DistributionFileInfoPanel() {
		initComponents();
		initLayout();
		initListener();
	}

	private void initListener() {
		receiptPersonValue.addKeyListener(new ScanInputKeyListener());
		receiptDateValue.addKeyListener(new ScanInputKeyListener());
		receiptDeptValue.addKeyListener(new ScanInputKeyListener());
	}

	private void initComponents() {
		receiptPersonLabel = new JLabel("领取人：");
		receiptDateLabel = new JLabel("领取时间：");
		receiptDeptLabel = new JLabel("领取部门：");

		receiptPersonValue = new JTextField();
		receiptDateValue = new JTextField();
		receiptDeptValue = new JTextField();

		receiptPersonValue.setEditable(false);
		receiptDateValue.setEditable(false);
		receiptDeptValue.setEditable(false);

		receiptPersonValue.setPreferredSize(new Dimension(120, 25));
		receiptDateValue.setPreferredSize(new Dimension(120, 25));
		receiptDeptValue.setPreferredSize(new Dimension(120, 25));

		receiptPersonLabel.grabFocus();

		receiptPersonIDValue = new JTextField();
	}

	private void initLayout() {
		setLayout(new FlowLayout(FlowLayout.LEADING));
		add(receiptPersonLabel);
		add(receiptPersonValue);
		add(receiptDateLabel);
		add(receiptDateValue);
		add(receiptDeptLabel);
		add(receiptDeptValue);
	}

	public JTextField getReceiptPersonValue() {
		return receiptPersonValue;
	}

	public JTextField getReceiptDateValue() {
		return receiptDateValue;
	}

	public JTextField getReceiptDeptValue() {
		return receiptDeptValue;
	}

	public JTextField getReceiptPersonIDValue() {
		return receiptPersonIDValue;
	}

	public void clear() {
		receiptPersonValue.setText("");
		receiptDateValue.setText("");
		receiptDeptValue.setText("");
		receiptPersonIDValue.setText("");
	}
}
