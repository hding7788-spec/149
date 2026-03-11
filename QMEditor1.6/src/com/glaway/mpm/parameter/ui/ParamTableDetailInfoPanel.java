package com.glaway.mpm.parameter.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.parameter.constants.ParameterConstants;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterTableColumn;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.parameter.util.InputEnglishLimited;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;
import com.ptc.windchill.enterprise.part.alternaterep.server.PARAttributeDataTypeOverride;

public class ParamTableDetailInfoPanel extends JPanel {

	private static final long serialVersionUID = 2974396865547222482L;
	private JTable table;
	private CommonTableModel tableModel;
	private String[] columnName;
	private Class<?>[] columnClass;
	private int[] editableColumn;
	private List<CmParameterTableColumn> defaultTableColumns = new ArrayList<CmParameterTableColumn>();

	public ParamTableDetailInfoPanel() {
		initComponents();
	}

	private void initComponents() {
		columnClass = new Class[] {String.class, String .class,String.class, String.class, String.class, String.class, Boolean.class, Boolean.class, String.class, String.class, Boolean.class, Boolean.class};
		columnName = new String[] {"oid","序号", "内部名称", "中文名称", "数据类型", "长度", "记录列", "特性库选用", "范围", "状态", "工艺编辑不可见", "MES不可见"};
		editableColumn = new int[] {3, 4, 5, 6, 7, 8, 10, 11};

		tableModel = new CommonTableModel(columnName, columnClass, editableColumn);
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setModel(tableModel);
		table.setRowHeight(30);

		JTextField textField = new JTextField();
		textField.setDocument(new InputEnglishLimited());
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(textField));
		JComboBox dataTypeComboBox = new JComboBox(ParameterConstants.COLUMN_DATATYPE);
		table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(dataTypeComboBox));
		table.setDefaultRenderer(Object.class, new ParameterTableRenderer());
//		dataTypeComboBox.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				int row = table.getEditingRow();
//				String dateType = CommonUtil.objectToString(e.getItem());
//				if("字符串".equals(dateType)){
//					table.setValueAt("", row, 7);
//				}else if("布尔型".equals(dateType)){
//					table.setValueAt(false, row, 7);
//				}else{
//					table.setValueAt("", row, 7);
//				}
//			}
//		});

		CommonUIUtil.hiddenCell(table, 0);
//		CommonUIUtil.setColumnWidth(table, 1, 200);
//		CommonUIUtil.setColumnWidth(table, 2, 200);
//		CommonUIUtil.setColumnWidth(table, 3, 100);
//		CommonUIUtil.setColumnWidth(table, 4, 80);
//		CommonUIUtil.setColumnWidth(table, 5, 80);
//		CommonUIUtil.setColumnWidth(table, 6, 100);
//		CommonUIUtil.setColumnWidth(table, 8, 80);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    	int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		scrollPane.setPreferredSize(new Dimension(width, height - 200));
		setLayout(new VFlowLayout(0, 0, 0, true, true));
		add(scrollPane);
	}

	public void setUIValues(XWTreeNode treeNode) {
		if (treeNode == null)
			return ;

		tableModel.setRowCount(0);
		CmParamTableType paramTableType = (CmParamTableType) treeNode.getTreeObject().getTreeNode();
		if (paramTableType != null) {
			defaultTableColumns.clear();
			List<CmParameterTableColumn> list = paramTableType.getTableColumns();
			for (CmParameterTableColumn parameterTableColumn : list) {
				if (parameterTableColumn.isShow()) {
					CommonUIUtil.addOneRow(tableModel);
					int row = table.getRowCount() - 1;
					table.setValueAt(parameterTableColumn.getOid(), row, 0);
					table.setValueAt(tableModel.getRowCount(), row, 1);
					table.setValueAt(parameterTableColumn.getEnName(), row, 2);
					table.setValueAt(parameterTableColumn.getName(), row, 3);
					table.setValueAt(parameterTableColumn.getDatatype(), row, 4);
					table.setValueAt(parameterTableColumn.getMaxlong(), row, 5);
					String isRecord = CommonUtil.objectToString(parameterTableColumn.getIsrecord());
					table.setValueAt("".equals(isRecord) ? false : Boolean.parseBoolean(isRecord), row, 6);
					String isFromParam = CommonUtil.objectToString(parameterTableColumn.getIsfromparam());
					table.setValueAt("".equals(isFromParam) ? false : Boolean.parseBoolean(isFromParam), row, 7);
					table.setValueAt(parameterTableColumn.getValueRange(), row, 8);
					table.setValueAt(parameterTableColumn.getStatus(), row, 9);
					String visiless = CommonUtil.objectToString(parameterTableColumn.getVisiless());
					table.setValueAt("".equals(visiless) ? false : Boolean.parseBoolean(visiless), row, 10);
					String visilessInMes = CommonUtil.objectToString(parameterTableColumn.getVisilessInMes());
					table.setValueAt("".equals(visilessInMes) ? false : Boolean.parseBoolean(visilessInMes), row, 11);

				} else {
					if (parameterTableColumn.getName().equals("OBJNUMBER")) {
						parameterTableColumn.setOrderno("-1");
					} else if (parameterTableColumn.getName().equals("参数表类型ID")) {
						parameterTableColumn.setOrderno("-2");
					} else if (parameterTableColumn.getName().equals("TECHNICSNUMBER")) {
						parameterTableColumn.setOrderno("-3");
					} else if (parameterTableColumn.getName().equals("GWKEY")) {
						parameterTableColumn.setOrderno("-4");
					} else if (parameterTableColumn.getName().equals("OBJTYPE")) {
						parameterTableColumn.setOrderno("-5");
					} else if( parameterTableColumn.getName().equals("SEQUENCE")) {
						parameterTableColumn.setOrderno("-6");
					} else if (parameterTableColumn.getName().equals("BSOID")){
						parameterTableColumn.setOrderno("-7");
					} else if (parameterTableColumn.getName().equals("VERSION")){
						parameterTableColumn.setOrderno("-8");
					}
					defaultTableColumns.add(parameterTableColumn);
				}
			}
		}
	}

	public JTable getTable() {
		return table;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public List<CmParameterTableColumn> getCmParameterTableColumns() {
		List<CmParameterTableColumn> tableColumns = new ArrayList<CmParameterTableColumn>();
		tableColumns.addAll(defaultTableColumns);
		int rowCount = table.getRowCount();
		for (int row = 0; row < rowCount; row++) {
			String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
			String orderNo = CommonUtil.objectToString(table.getValueAt(row, 1));
			String enName = CommonUtil.objectToString(table.getValueAt(row, 2));
			String chinaName = CommonUtil.objectToString(table.getValueAt(row, 3));
			String dataType = CommonUtil.objectToString(table.getValueAt(row, 4));
			String maxLong = CommonUtil.objectToString(table.getValueAt(row, 5));
			String isRecord = CommonUtil.objectToString(table.getValueAt(row, 6));
			String isFromParam = CommonUtil.objectToString(table.getValueAt(row, 7));
			String valueRange = CommonUtil.objectToString(table.getValueAt(row, 8));
			String status = CommonUtil.objectToString(table.getValueAt(row, 9));
			String visiless = CommonUtil.objectToString(table.getValueAt(row, 10));
			String visilessInMes = CommonUtil.objectToString(table.getValueAt(row, 11));

			CmParameterTableColumn tableColumn = new CmParameterTableColumn();
			tableColumn.setOid("".equals(oid) ? 0 : Long.valueOf(oid));
			tableColumn.setOrderno(orderNo);
			tableColumn.setEnName(enName);
			tableColumn.setName(chinaName);
			tableColumn.setDatatype(dataType);
			tableColumn.setMaxlong(maxLong);
			tableColumn.setIsrecord(isRecord);
			tableColumn.setIsfromparam(isFromParam);
			tableColumn.setValueRange(valueRange);
			tableColumn.setStatus(status);
			tableColumn.setVisiless(visiless);
			tableColumn.setVisilessInMes(visilessInMes);

			tableColumns.add(tableColumn);
		}
		return tableColumns;
	}

	public boolean verify() {
		int rowCount = table.getRowCount();
		List<String> list = new ArrayList<String>();
		for (int row = 0; row < rowCount; row++) {
			String enName = CommonUtil.objectToString(table.getValueAt(row, 2));
			String chinaName = CommonUtil.objectToString(table.getValueAt(row, 3));
			String dataType = CommonUtil.objectToString(table.getValueAt(row, 4));
			String status = CommonUtil.objectToString(table.getValueAt(row, 9));

			if (!status.equals("正常")) {
				continue;
			}
			if (list.contains(enName)) {
				JOptionPane.showMessageDialog(null, "内部名称不允许相同！");
				return false;
			}
			list.add(enName);

			if ("".equals(enName) || "".equals(chinaName) || "".equals(dataType)) {
				JOptionPane.showMessageDialog(null, "内部名称、中文名称、数据类型不允许为空！");
				return false;
			}
		}
		return true;
	}
}
