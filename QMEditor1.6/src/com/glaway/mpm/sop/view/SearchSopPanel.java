package com.glaway.mpm.sop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.view.*;
import org.dom4j.Element;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.sop.model.ParametersBean;
import com.glaway.mpm.sop.model.SopBean;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.ResourceIntf;

import ext.casc.sop.constants.SopConstants;

public class SearchSopPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JPanel topMainPanel;
	private JPanel mainPanel;
	private JPanel middleMainPanel;

	private JButton searchButton;
	private JButton clearButton;

	private JLabel vagueSearchLabel;
	private JTextField vagueSearchTextField;

	private JLabel specializedNameLabel;
	private JComboBox specializedNameComboBox;

	private JLabel proceduceNameLabel;
	private JComboBox proceduceNameComboBox;

	private JLabel cheJianLabel;
	private JComboBox cheJianComboBox;

	private JTable sopTable;
	private DefaultTableModel sopTableModel;
	private JTable parametersTable;
	private DefaultTableModel parametersTableModel;
	private JButton sureButton;
	private JButton cancelButton;

	public static Vector<Map<String, String>> equipments;
	private JDialog dialog;
	private SopFileTableJPanel sopFileTableJPanel;

	public SearchSopPanel(JDialog dialog, SopFileTableJPanel panel, JFrame frame) {
		this.sopFileTableJPanel = panel;
		this.dialog = dialog;
		this.frame = frame;
		init();
	}

	private void init() {
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
	}

	private void initComponents() {
		topMainPanel = new JPanel();
		middleMainPanel = new JPanel();
		mainPanel = new JPanel();

		vagueSearchLabel = new JLabel("编号/名称：");
		vagueSearchTextField = new JTextField();
		vagueSearchTextField.setPreferredSize(new Dimension(200, 25));

		specializedNameLabel = new JLabel("专业名称：");
		specializedNameComboBox = new JComboBox();
		specializedNameComboBox.setPreferredSize(new Dimension(200, 25));
		specializedNameComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(specializedNameComboBox.getSelectedItem() != null){
					String zylb = specializedNameComboBox.getSelectedItem().toString();
					Map<String, String> sopProcessStepName = SopIntf.getSopProcessStepName(zylb);
					proceduceNameComboBox.removeAllItems();
					proceduceNameComboBox.addItem("");
					for(Map.Entry<String, String> entry : sopProcessStepName.entrySet()){
						String proceduceName = entry.getValue();
						proceduceNameComboBox.addItem(proceduceName);
					}
				}

			}
		});

		proceduceNameLabel = new JLabel("工序名称：");
		proceduceNameComboBox = new JComboBox();
		proceduceNameComboBox.setPreferredSize(new Dimension(200, 25));

		cheJianLabel = new JLabel("车间：");
		cheJianComboBox = new JComboBox();
		cheJianComboBox.setPreferredSize(new Dimension(200, 25));

		searchButton = new JButton();
		searchButton.setText("查询");
		searchButton.setPreferredSize(new Dimension(90, 25));

		clearButton = new JButton();
		clearButton.setText("重置");
		clearButton.setPreferredSize(new Dimension(90, 25));

		sureButton = new JButton();
		sureButton.setText("确定");
		sureButton.setPreferredSize(new Dimension(90, 25));

		cancelButton = new JButton();
		cancelButton.setText("取消");
		cancelButton.setPreferredSize(new Dimension(90, 25));

		CmActionListener listener = new CmActionListener();
		searchButton.addActionListener(listener);
		clearButton.addActionListener(listener);
		sureButton.addActionListener(listener);
		cancelButton.addActionListener(listener);

		String[] sopTableHeader = new String[] {"oid", "SOP文件编号", "SOP文件名称","参数项目", "专业类别", "工序名称",
				"操作岗位", "定制区域", "密级", "版本", "专业代号", "工序简号", "其它说明", "object" };
		sopTable = new JTable();
		sopTableModel = new CommonTableModel(sopTableHeader, null, null);
		sopTable.setModel(sopTableModel);
		sopTable.getTableHeader().setReorderingAllowed(false);
		CommonUIUtil.hiddenCell(sopTable,3);
//		sopTable.getTableHeader().setResizingAllowed(false);
		sopTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		String[] parametersTableHeader = new String[] {"oid", "编号", "参数项目名称", "专业类别", "工序名称", "物资类别",
				"参数值", "说明", "object" };
		parametersTable = new JTable();
		parametersTableModel = new CommonTableModel(parametersTableHeader, null, null);
		parametersTable.setModel(parametersTableModel);
		parametersTable.getTableHeader().setReorderingAllowed(false);
		parametersTable.getTableHeader().setResizingAllowed(false);

		CommonUIUtil.hiddenCell(sopTable, 0);
		CommonUIUtil.hiddenCell(sopTable, 13);
		CommonUIUtil.hiddenCell(parametersTable, 0);
		CommonUIUtil.hiddenCell(parametersTable, 8);
	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		
		c.gridx = 1;
		c.gridy = 1;
		topMainPanel.setLayout(new GridBagLayout());
		topMainPanel.add(vagueSearchLabel, c);
		c.gridx = 2;
		topMainPanel.add(vagueSearchTextField, c);
		c.gridx = 3;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(searchButton, c);
		c.insets = new Insets(10, 0, 5, 5);
		
		c.gridx = 1;
		c.gridy = 2;
		topMainPanel.add(specializedNameLabel, c);
		c.gridx = 2;
		topMainPanel.add(specializedNameComboBox, c);
		c.gridx = 3;
		c.insets = new Insets(10, 3, 5, 5);
		topMainPanel.add(clearButton, c);
		c.insets = new Insets(10, 0, 5, 5);

		c.gridx = 1;
		c.gridy = 3;
		topMainPanel.add(proceduceNameLabel, c);
		c.gridx = 2;
		topMainPanel.add(proceduceNameComboBox, c);

		c.insets = new Insets(10, 0, 5, 5);
		c.gridy = 4;
		c.gridx = 1;
		topMainPanel.add(cheJianLabel, c);
		c.gridx = 2;
		topMainPanel.add(cheJianComboBox, c);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;

		sopTable.setRowHeight(23);
		sopTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(sopTable);
		jScrollPane1.setPreferredSize(new Dimension(1000, 200));
		JPanel sopTablePanel = new JPanel();
		sopTablePanel.setBorder(BorderFactory.createTitledBorder("SOP查询结果"));
		sopTablePanel.setLayout(new BorderLayout());
		sopTablePanel.add(jScrollPane1);

		parametersTable.setRowHeight(23);
		parametersTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		JScrollPane jScrollPane2 = new JScrollPane();
		jScrollPane2.setViewportView(parametersTable);
		jScrollPane2.setPreferredSize(new Dimension(1000, 200));
		JPanel parametersTablePanel = new JPanel();
		parametersTablePanel.setBorder(BorderFactory.createTitledBorder("SOP关联的项目参数"));
		parametersTablePanel.setLayout(new BorderLayout());
		parametersTablePanel.add(jScrollPane2);

		VFlowLayout vFlowLayout = new VFlowLayout(5, true, true);
		middleMainPanel.setLayout(vFlowLayout);
		middleMainPanel.add(sopTablePanel);
		middleMainPanel.add(parametersTablePanel);

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

	private void initActions() {
		sopTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); // 获得行位置
//					if (e.getClickCount() == 2) {
//						Vector<Map<String, String>> equipments = generateObjectMap(row);
//						sopFileTableJPanel.addData(equipments);
//					}

					SopBean sopBean = (SopBean)sopTable.getValueAt(row, 13);
					updateParametersTable(sopBean);
				}
			}
		});
	}

	public Vector<Map<String, String>> generateObjectMap(int row) {
		Vector<Map<String, String>> list = new Vector<Map<String, String>>();
		SopBean sopBean = (SopBean) sopTable.getValueAt(row, 13);
		List<ParametersBean> parameters = sopBean.getArameters();
		Map<String, String> map = null;
		if (parameters != null && parameters.size() > 0) {
			for (ParametersBean bean : parameters) {
				map = new Hashtable<String, String>();
				map.put("oid", sopBean.getOid());
				map.put("number", sopBean.getNumber());
				map.put("ppnumber", sopBean.getPpnumber());
				map.put("name", sopBean.getName());
				map.put("parameters", bean.getName());
				String canshuzhi = "";
				if (bean.getCanShuZhi() != null) {
					canshuzhi = bean.getCanShuZhi();
				}
				map.put("canshuzhi", canshuzhi);
				String wzlb = "";
				if (bean.getMaterialCategory() != null) {
					wzlb = bean.getMaterialCategory();
				}
				map.put("wzlb", wzlb);
				map.put("zylb", bean.getSpecializedType());
				list.add(map);
			}
		} else {
			map = new Hashtable<String, String>();
			map.put("oid", sopBean.getOid());
			map.put("number", sopBean.getNumber());
			map.put("ppnumber", sopBean.getPpnumber());
			map.put("name", sopBean.getName());
			map.put("parameters", "");
			String canshuzhi = "";
			map.put("canshuzhi", canshuzhi);
			String wzlb = "";
			map.put("wzlb", wzlb);
			map.put("zylb", sopBean.getSpecializedType());
			list.add(map);
		}
		return list;
	}

	private String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	private void loadInitDatas() {
		try {
			specializedNameComboBox.addItem("");
			List<String> specializedNameList = SopIntf.getSopResourceName(SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
			if (specializedNameList != null && specializedNameList.size() > 0) {
				for (String specializedName : specializedNameList) {
					specializedNameComboBox.addItem(specializedName);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		cheJianComboBox.addItem("");
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
		for (String string : allList) {
			cheJianComboBox.addItem(string);
		}

		// 按照当前SOP文件的专业类别、工序名称、部门自动查询出满足条件的SOP文件
		Element techElement = sopFileTableJPanel.getTechElement();
		XWTreeNode selectedTreeNode = ((NewTechnicsPart) frame).technicsTreePanel.getSelectedTreeNode();
		XWTreeObject selectObject = selectedTreeNode.getObject();
		if (techElement != null) {
			//专业类别
			String specializedName;
			//工序名称
			String proceduceName;
			//部门
			String departmentName;
			if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
				specializedName = XmlUtility.getAttributeValue(techElement, SopConstants.SOP_IBA_SPECIALIZEDTYPE);
				departmentName = XmlUtility.getAttributeValue(techElement, SopConstants.SOP_IBA_DEPARTMENT);
			}else{
				String technicsType = XmlUtility.getAttributeValue(techElement, "technicsType");
				specializedName = LoadConfig.getInstance().getZylb(technicsType);
				departmentName = XmlUtility.getAttributeValue(techElement, "DEPT");
			}
			if(selectObject instanceof XWTechnicsTreeObject){
				proceduceName = XmlUtility.getAttributeValue(techElement, SopConstants.SOP_IBA_PROCEDUCENAME);
			}else if(selectObject instanceof XWStepTreeObject){
				XWStepTreeObject stepObj = (XWStepTreeObject) selectObject;
				Element stepElement = stepObj.getTreeCellData();
				proceduceName = XmlUtility.getAttributeValue(stepElement, "stepName");
			}else if(selectObject instanceof XWPaceTreeObject){
				XWTreeNode stepNode = (XWTreeNode) selectedTreeNode.getParent();
				Element stepElement = stepNode.getObject().getTreeCellData();
				proceduceName = XmlUtility.getAttributeValue(stepElement, "stepName");
			}else{
				proceduceName = XmlUtility.getAttributeValue(techElement, SopConstants.SOP_IBA_PROCEDUCENAME);
			}
			if (specializedName != null && specializedName.length() > 0) {
				specializedNameComboBox.setSelectedItem(specializedName);
			}
			if (proceduceName != null && proceduceName.length() > 0) {
				proceduceNameComboBox.setSelectedItem(proceduceName);
			}
			if (departmentName != null && departmentName.length() > 0) {
				cheJianComboBox.setSelectedItem(departmentName);
			}
			if ((specializedName != null && specializedName.length() > 0)
					|| (proceduceName != null && proceduceName.length() > 0)
					|| (departmentName != null && departmentName.length() > 0)) {
				search(true);
			}
		}
	}

	public void search(boolean isLoad) {
		try {
			String searchValue = convertNull(vagueSearchTextField.getText());
			String specializedName = convertNull(specializedNameComboBox.getSelectedItem());
			String proceduceName = convertNull(proceduceNameComboBox.getSelectedItem());
			String cheJian = convertNull(cheJianComboBox.getSelectedItem());
			SopBean sopBean = new SopBean();
			sopBean.setNumber(searchValue);
			sopBean.setSpecializedType(specializedName);
			sopBean.setProceduceName(proceduceName);
			sopBean.setDepartment(cheJian);
			List<SopBean> list = SopIntf.querySop(sopBean);
			if(list != null && list.size()>0) {
				Vector<Object> rowData = null;
				for (SopBean bean : list) {
					// "oid", "SOP文件编号", "SOP文件名称","参数项目", "专业类别", "工序名称", "操作岗位", "定制区域",
					// "密级", "版本", "专业代号", "工序简号", "其它说明", "object"
					rowData = new Vector<Object>();
					rowData.add(bean.getOid());
					rowData.add(bean.getPpnumber());
					rowData.add(bean.getName());
					rowData.add(bean.getParameters());
					rowData.add(bean.getSpecializedType());
					rowData.add(bean.getProceduceName());
					rowData.add(bean.getOperationJob());
					rowData.add(bean.getCustomArea());
					rowData.add(bean.getSecret());
					rowData.add(bean.getVersion());
					rowData.add(bean.getProfessionalCod());
					rowData.add(bean.getGongXuJianHao());
					rowData.add(bean.getDescription());
					rowData.add(bean);
					sopTableModel.addRow(rowData);
				}
			} else {
				if(!isLoad){
					SwingUtil.showMessageDialog("查询SOP文件结果为空!", "提示", 2);
				}
			}
		} catch (Exception e1) {
			SwingUtil.showMessageDialog("查询SOP文件时出错!", "提示", 2);
			e1.printStackTrace();
		}
	}

	private void updateParametersTable(SopBean sopBean) {
		parametersTableModel.setRowCount(0);

		if (sopBean != null) {
			List<ParametersBean> parameters = sopBean.getArameters();
			if(parameters != null && parameters.size() > 0) {
				Vector<Object> rowData = null;
				for (ParametersBean bean : parameters) {
					rowData = new Vector<Object>();
					//"oid", "编号", "参数项目名称", "专业类别", "工序名称", "物资类别", "参数值", "说明", "object"
					rowData.add(bean.getOid());
					rowData.add(bean.getNumber());
					rowData.add(bean.getName());
					rowData.add(bean.getSpecializedType());
					rowData.add(bean.getProceduceName());
					rowData.add(bean.getMaterialCategory());
					rowData.add(bean.getCanShuZhi());
					rowData.add(bean.getDescription());
					rowData.add(bean);
					parametersTableModel.addRow(rowData);
				}
			}
		}
	}

	class CmActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == searchButton) {
				// 1.首先情况列表中现有数据
				sopTableModel.setRowCount(0);
				// 2.查询SOP文件
				search(false);
			} else if (e.getSource() == clearButton) {
				vagueSearchTextField.setText("");
				specializedNameComboBox.setSelectedIndex(0);
				proceduceNameComboBox.setSelectedIndex(0);
				cheJianComboBox.setSelectedIndex(0);
			} else if (e.getSource() == sureButton) {
				if (sopTable.getSelectedRows().length == 0) {
					SwingUtil.showMessageDialog("请选择SOP文件", "提示", 2);
				} else {
					equipments = new Vector<Map<String, String>>();
					for (int row : sopTable.getSelectedRows()) {
						equipments.addAll(generateObjectMap(row));
					}
					dialog.dispose();
				}
			} else if (e.getSource() == cancelButton) {
				equipments = null;
				dialog.dispose();
			}
		}
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