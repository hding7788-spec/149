package com.glaway.mpm.print.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.model.data.CmBaseline;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.helper.MPMPrintHelper;
import com.glaway.mpm.print.ui.SearchBaselineConditionPanel;
import com.glaway.mpm.print.ui.SearchBaselinePanel;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;

public class SearchBaselineListener implements ActionListener {

	private JPanel panel;

	public SearchBaselineListener(JPanel panel) {
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (panel instanceof SearchBaselineConditionPanel) {
			SearchBaselineConditionPanel conditionPanel = (SearchBaselineConditionPanel) panel;

			if (obj == conditionPanel.getSearchButton()) {
				CmPrintQueryBean printQueryBean = conditionPanel.getFilledInfo();
				List<CmPrintInfoBean> list = MPMPrintHelper.queryBaselines(printQueryBean);
				if (list == null) {
					CommonUIUtil.showMessageDialog(null, "查询异常！");
					return ;
				}
				conditionPanel.getParent().setTableValues(list);
			} else if (obj == conditionPanel.getClearConditionButton()) {
				conditionPanel.clearCondition();
			}

		} else if (panel instanceof SearchBaselinePanel) {
			SearchBaselinePanel baselinePanel = (SearchBaselinePanel) panel;

			if (obj == baselinePanel.getSureButton()) {
				JTable table = baselinePanel.getResultPanel().getTable();

				DefaultTableModel resourceTableModel = (DefaultTableModel) baselinePanel.getResourceTable().getModel();

				int rowCount = table.getRowCount();
				List<CmBaseline> list = new ArrayList<CmBaseline>();
				for (int row = 0; row < rowCount; row++) {
					boolean isSelected = (Boolean) table.getValueAt(row, 1);
					if (isSelected) {
						String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
						String ts_status = CommonUtil.objectToString(table.getValueAt(row, 7));
						if (ts_status.length() > 0 && oid.length() > 0 && !"0".equals(oid)) {
							boolean flag = true;
							Vector<Vector<?>> vector = resourceTableModel.getDataVector();
							for (Vector<?> rowData : vector) {
								String rowData_1 = CommonUtil.objectToString(rowData.get(0));
								if (rowData_1.equals(oid)) {
									flag = false;
									break;
								}
							}

							if (flag) {
								CmBaseline baseline = new CmBaseline();
								baseline.setOid(Long.parseLong(oid));
								baseline.setStatus(ts_status);
								list.add(baseline);
							}
						}
					}
				}
				baselinePanel.setResourceTableValues(list);
				baselinePanel.getDialog().dispose();
			} else if (obj == baselinePanel.getCancelButton()) {
				baselinePanel.getDialog().dispose();
			}
		}
	}

}
