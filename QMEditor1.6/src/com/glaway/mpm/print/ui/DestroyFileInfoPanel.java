package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.model.data.CmUser;
import com.glaway.mpm.print.listener.ScanInputKeyListener;
import com.glaway.mpm.util.DateUtil;

public class DestroyFileInfoPanel extends JPanel{

	private static final long serialVersionUID = 6086924870364942386L;
	/** 退回人 */
	private JLabel destroyPersonLabel;
	/** 退回时间 */
	private JLabel destroyDateLabel;
	/** 退回部门 */
	private JLabel destroyDeptLabel;
	/** 退回人 */
	private JTextField destroyPersonValue;
	/** 退回时间 */
	private JTextField destroyDateValue;
	/** 退回部门 */
	private JTextField destroyDeptValue;
	/** 退回人OID */
	private JTextField destroyPersonIDValue;

	public DestroyFileInfoPanel() {
		initComponents();
		initLayout();
		initListener();
	}

	private void initComponents() {
		destroyPersonLabel = new JLabel("退回人：");
		destroyDateLabel = new JLabel("退回时间：");
		destroyDeptLabel = new JLabel("退回部门：");

		destroyPersonValue = new JTextField();
		destroyDateValue = new JTextField();
		destroyDeptValue = new JTextField();

		destroyPersonValue.setEditable(false);
		destroyDateValue.setEditable(false);
		destroyDeptValue.setEditable(false);

		destroyPersonValue.setPreferredSize(new Dimension(120, 25));
		destroyDateValue.setPreferredSize(new Dimension(120, 25));
		destroyDeptValue.setPreferredSize(new Dimension(120, 25));

		destroyPersonValue.grabFocus();
		destroyPersonIDValue = new JTextField();
	}

	private void initLayout() {
		setLayout(new FlowLayout(FlowLayout.LEADING));
		add(destroyPersonLabel);
		add(destroyPersonValue);
		add(destroyDateLabel);
		add(destroyDateValue);
		add(destroyDeptLabel);
		add(destroyDeptValue);
	}

	private void initListener() {
		destroyPersonValue.addKeyListener(new ScanInputKeyListener());
		destroyDateValue.addKeyListener(new ScanInputKeyListener());
		destroyDeptValue.addKeyListener(new ScanInputKeyListener());
	}

	public void setUIValues() {
		CmUser currentUser = MPMPrintFileFrame.getCurrentUser();
		destroyPersonValue.setText(currentUser.getFullName());
		String dept = currentUser.getDepartment();
		if(dept.equals("")){
			destroyDeptValue.setText("");
		}else{
			String department = currentUser.getDepartment();
			if (department.indexOf("_") > -1) {
				department = department.substring(department.indexOf("_") + 1);
				destroyDeptValue.setText(department);
			} else {
				destroyDeptValue.setText(department);
			}
		}
		destroyPersonIDValue.setText(String.valueOf(currentUser.getOid()));
		String date = DateUtil.getTodayDate("yyyy/MM/dd");
		destroyDateValue.setText(date);
	}

	public void clear() {
		destroyPersonValue.setText("");
		destroyDateValue.setText("");
		destroyDeptValue.setText("");
		destroyPersonIDValue.setText("");
	}

	public JTextField getDestroyPersonValue() {
		return destroyPersonValue;
	}

	public JTextField getDestroyDateValue() {
		return destroyDateValue;
	}

	public JTextField getDestroyDeptValue() {
		return destroyDeptValue;
	}

	public JTextField getDestroyPersonIDValue() {
		return destroyPersonIDValue;
	}

}
