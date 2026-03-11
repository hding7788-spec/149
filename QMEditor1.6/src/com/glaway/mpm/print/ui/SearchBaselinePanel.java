package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.model.data.CmBaseline;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.listener.SearchBaselineListener;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;

/**
 * 工艺文件搜索界面
 *
 * @author 龙秀川
 *
 */
public class SearchBaselinePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private SearchBaselineDialog dialog;
	private SearchBaselineConditionPanel conditionPanel;
	private SearchBaselineResultTablePanel resultPanel;
	private JButton sureButton;
	private JButton cancelButton;
	private JTable resourceTable;

	public SearchBaselinePanel(SearchBaselineDialog dialog, JTable table) {
		this.dialog = dialog;
		this.resourceTable = table;
		initComponents();
		initLayout();
		initListener();
	}

	private void initComponents() {
    	conditionPanel = new SearchBaselineConditionPanel(this);
    	resultPanel = new SearchBaselineResultTablePanel();

    	sureButton = new JButton("确定");
    	cancelButton = new JButton("取消");
    }

	private void initLayout() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(sureButton);
		buttonPanel.add(cancelButton);
		buttonPanel.setPreferredSize(new Dimension(this.getWidth(), 30));

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.NORTH, conditionPanel, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, conditionPanel, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, conditionPanel, 0, SpringLayout.EAST, this);

		springLayout.putConstraint(SpringLayout.WEST, resultPanel, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, resultPanel, 0, SpringLayout.EAST, this);

		springLayout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, buttonPanel, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, buttonPanel, 0, SpringLayout.EAST, this);

		springLayout.putConstraint(SpringLayout.NORTH, resultPanel, 5, SpringLayout.SOUTH, conditionPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, resultPanel, 5, SpringLayout.NORTH, buttonPanel);

		add(conditionPanel);
		add(resultPanel);
		add(buttonPanel);
	}

	public void setTableValues(List<CmPrintInfoBean> list) {
		resultPanel.setTableValues(list);
	}

	public void setResourceTableValues(List<CmBaseline> list) {
		DefaultTableModel tableModel = (DefaultTableModel) resourceTable.getModel();
		for (CmBaseline baseline : list) {
			CommonUIUtil.addOneRow(tableModel);
			int row = resourceTable.getRowCount() - 1;
			resourceTable.setValueAt(baseline.getOid(), row, 0);
			resourceTable.setValueAt(baseline.getStatus(), row, 1);
			resourceTable.setValueAt(true, row, 2);
		}
	}

	private void initListener() {
		sureButton.addActionListener(new SearchBaselineListener(this));
		cancelButton.addActionListener(new SearchBaselineListener(this));
	}

	public JButton getSureButton() {
		return sureButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public SearchBaselineDialog getDialog() {
		return dialog;
	}

	public SearchBaselineResultTablePanel getResultPanel() {
		return resultPanel;
	}

	public JTable getResourceTable() {
		return resourceTable;
	}
}
