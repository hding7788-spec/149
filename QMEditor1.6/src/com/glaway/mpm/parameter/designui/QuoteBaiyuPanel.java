package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.parameter.model.data.CmBaiyuParamTableColumn;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsPaceJDialog;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public class QuoteBaiyuPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JPanel topMainPanel;
	private JPanel middleMainPanel;
	private JPanel mainPanel;

	private JButton searchButton;

	private JLabel tableNameLabel;
	private JTextField tableNameTextField;
	private JLabel tableTypeLabel;
	private JComboBox tableType_value; //表格类型
	private String[] tableType_Tec = {"周期表"};
	private String[] tableType_Pace = {"通用表","专用表"};
	private JLabel deptLabel;
	private JComboBox dept_value; //部门

	private JTable templateTable;
	private DefaultTableModel templateTableModel;
	private JButton sureButton;
	private JButton cancelButton;

	public static ArrayList<ArrayList<String>> equipments;
	private JDialog dialog;
	String[] templateTableHeader = new String[] {"序号", "ID", "名称", "版本", "创建人", "修改人", "创建时间", "最后修改时间", "状态","表格类型","部门"};

	public QuoteBaiyuPanel(JDialog dialog, JFrame frame) {
		this.dialog = dialog;
		this.frame = frame;
		init();
	}

	private void init() {
		initComponents();
		initLayout();
	}

	private void initComponents() {
		topMainPanel = new JPanel();
		middleMainPanel = new JPanel();
		mainPanel = new JPanel();

		tableNameLabel = new JLabel("模板名称:");
		tableNameTextField = new JTextField();
		tableNameTextField.setPreferredSize(new Dimension(120, 25));
		tableTypeLabel = new JLabel("表格类型:");
		if(frame instanceof NewTechnicsPart){
			tableType_value = new JComboBox(tableType_Tec);
		} else if(frame instanceof TechnicsPaceJDialog) {
			tableType_value = new JComboBox(tableType_Pace);
		}
		tableType_value.setPreferredSize(new Dimension(120, 25));
		//获取到所有的车间组，然后自动定位到当前用户所在的组
		List<String> allList = new ArrayList<String>();
		Map<String,String> workShop = ResourceIntf.getWorkShops();
		if (workShop != null && workShop.size() > 0) {
			Collection<String> coll = workShop.values();
			Iterator<String> it = coll.iterator();
			while (it.hasNext()) {
				String temp = (String) it.next();
				if (temp != null && temp.trim().length() > 0) {
					allList.add(temp);
				}
			}

			Collections.sort(allList);
		}
		allList.add(0,"");
		allList.add(1,"通用");
		deptLabel = new JLabel("部门:");
		dept_value = new JComboBox(allList.toArray());
		dept_value.setPreferredSize(new Dimension(120, 25));
		String groupName = "";
		try {
			groupName = TechnicsIntf.getUsertechnicsGroupName();
		} catch (RemoteException e2) {
			e2.printStackTrace();
		} catch (InvocationTargetException e2) {
			e2.printStackTrace();
		}
		if(allList.contains(groupName)) {
			dept_value.setSelectedItem(groupName);
		}

		searchButton = new JButton();
		searchButton.setText("查询");
		searchButton.setPreferredSize(new Dimension(90, 25));

		sureButton = new JButton();
		sureButton.setText("确定");
		sureButton.setPreferredSize(new Dimension(90, 25));

		cancelButton = new JButton();
		cancelButton.setText("取消");
		cancelButton.setPreferredSize(new Dimension(90, 25));

		CmActionListener listener = new CmActionListener();
		searchButton.addActionListener(listener);
		sureButton.addActionListener(listener);
		cancelButton.addActionListener(listener);

		templateTable = new JTable();
		templateTableModel = new CommonTableModel(templateTableHeader, null, null);
		templateTable.setModel(templateTableModel);
		templateTable.getTableHeader().setReorderingAllowed(false);
		templateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
		topMainPanel.setLayout(new GridBagLayout());
		topMainPanel.add(tableNameLabel, c);
		c.gridx = 2;
		topMainPanel.add(tableNameTextField, c);
		c.gridx = 3;
		topMainPanel.add(tableTypeLabel, c);
		c.gridx = 4;
		topMainPanel.add(tableType_value, c);
		c.gridx = 5;
		topMainPanel.add(deptLabel, c);
		c.gridx = 6;
		topMainPanel.add(dept_value, c);
		c.gridx = 7;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(searchButton, c);
		c.insets = new Insets(10, 0, 5, 5);

		templateTable.setRowHeight(26);
		templateTable.getTableHeader().setPreferredSize(new Dimension(20, 30));
		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(templateTable);
		jScrollPane1.setPreferredSize(new Dimension(1000, 200));
		JPanel templateTablePanel = new JPanel();
		templateTablePanel.setBorder(BorderFactory.createTitledBorder("表单模板查询结果"));
		templateTablePanel.setLayout(new BorderLayout());
		templateTablePanel.add(jScrollPane1);

		VFlowLayout vFlowLayout = new VFlowLayout(5, true, true);
		middleMainPanel.setLayout(vFlowLayout);
		middleMainPanel.add(templateTablePanel);

		FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(flowLayout);
		buttonPanel.add(sureButton);
		buttonPanel.add(cancelButton);

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topMainPanel, BorderLayout.NORTH);
		mainPanel.add(middleMainPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	public void search() {
		try {
			String name = tableNameTextField.getText();
			String tableType = (String) tableType_value.getSelectedItem();
			String dept = (String) dept_value.getSelectedItem();
			//返回的都是启用的
			List<CmBaiyuParamTableColumn> lists = ProcessParameterToWCIntf.getBaiyuParamListsByName(name,tableType,dept);
			if(lists != null && lists.size()>0) {
				Object[][] tableBody = new Object[lists.size()][templateTableHeader.length];
				CmBaiyuParamTableColumn column;
				for (int i = 0; i < lists.size(); i++) {
					column = lists.get(i);
					tableBody[i][0] = column.getOrderNo();
					tableBody[i][1] = column.getTableId();
					tableBody[i][2] = column.getTableName();
					tableBody[i][3] = column.getVersion();
					tableBody[i][4] = column.getTableCreator();
					tableBody[i][5] = column.getTableModifier();
					tableBody[i][6] = column.getTableCreateTime();
					tableBody[i][7] = column.getTableModifyTime();
					tableBody[i][8] = column.getIsUsed();
					tableBody[i][9] = column.getTableType();
					tableBody[i][10] = column.getDept();
				}
				templateTableModel.setDataVector(tableBody,templateTableHeader);
			} else {
				SwingUtil.showMessageDialog("查询检验模板结果为空!", "提示", 2);
			}
		} catch (Exception e) {
			SwingUtil.showMessageDialog("查询检验模板时出错!", "提示", 2);
			e.printStackTrace();
		}

	}

	class CmActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == searchButton) {
				templateTableModel.setRowCount(0);
				search();
			} else if (e.getSource() == sureButton) {
				if (templateTable.getSelectedRows().length == 0) {
					SwingUtil.showMessageDialog("请选择通用检验记录表模板", "提示", 2);
				} else {
					equipments = new ArrayList<ArrayList<String>>();
					int[] rows = templateTable.getSelectedRows();
					equipments.addAll(generateObjectMap(rows));
					dialog.dispose();
				}
			} else if (e.getSource() == cancelButton) {
				equipments = null;
				dialog.dispose();
			}
		}
	}

	public ArrayList<ArrayList<String>> generateObjectMap(int[] rows) {
		ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < rows.length; i++) {
			ArrayList<String> list = new ArrayList<String>();
			list.add((String) templateTable.getValueAt(rows[i],0));
			list.add((String) templateTable.getValueAt(rows[i],1));
			list.add((String) templateTable.getValueAt(rows[i],2));
			list.add((String) templateTable.getValueAt(rows[i],3));
			list.add((String) templateTable.getValueAt(rows[i],4));
			list.add((String) templateTable.getValueAt(rows[i],5));
			list.add((String) templateTable.getValueAt(rows[i],6));
			list.add((String) templateTable.getValueAt(rows[i],7));
			list.add((String) templateTable.getValueAt(rows[i],1));
			list.add((String) templateTable.getValueAt(rows[i],9));
			list.add((String) templateTable.getValueAt(rows[i],10));
			lists.add(list);
		}
		return lists;
	}

	class CommonTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;
		/** 可编辑的列 */
		private int[] editableColumns;
		/** 表格每列的类型 */
		private Class<?>[] tableColumnClass;

		public CommonTableModel(int[] editableColumns) {
			this.editableColumns = editableColumns;
		}

		public CommonTableModel(String[] tableHeader, Class<?>[] tableColumnClass, int[] editableColumns) {
			super(null, tableHeader);
			this.editableColumns = editableColumns;
			this.tableColumnClass = tableColumnClass;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (editableColumns != null) {
				for (int i : editableColumns) {
					if (i == column) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (tableColumnClass != null) {
				return tableColumnClass[columnIndex];
			} else {
				return super.getColumnClass(columnIndex);
			}
		}
	}
}