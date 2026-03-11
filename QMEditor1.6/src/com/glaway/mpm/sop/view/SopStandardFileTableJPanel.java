package com.glaway.mpm.sop.view;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.sop.model.ParametersBean;
import com.glaway.mpm.sop.model.SopBean;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.*;
import ext.casc.sop.constants.SopConstants;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 引用SOP标准操作规程列表
 *
 * @author LongXiuChuan
 *
 */
public class SopStandardFileTableJPanel extends JPanel {

	private JFrame frame;
	private JPanel mainPanel;
	private JPanel middleMainPanel;
	private JPanel buttonPanel;
	private JDialog parentDialog;
	protected Container parentPanel;

	private JTable sopTable;
	private DefaultTableModel sopTableModel;
	private JTable parametersTable;
	private DefaultTableModel parametersTableModel;

	private JButton loadButton;
	private JButton searchButton;
	private JButton useButton;


	public DefaultTableModel getSopTableModel() {
		return sopTableModel;
	}

	public DefaultTableModel getParametersTableModel() {
		return parametersTableModel;
	}

	public SopStandardFileTableJPanel(JFrame frame) {
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
		middleMainPanel = new JPanel();
		mainPanel = new JPanel();
		String[] sopTableHeader = new String[] {"oid", "number", "SOP文件编号","SOP文件名称", "参数项目", "object"};
		sopTable = new JTable();
		sopTableModel = new CommonTableModel(sopTableHeader, null, null);
		sopTable.setModel(sopTableModel);
		sopTable.getTableHeader().setReorderingAllowed(false);
		CommonUIUtil.hiddenCell(sopTable, 0);
		CommonUIUtil.hiddenCell(sopTable, 1);
		CommonUIUtil.hiddenCell(sopTable, 4);
		CommonUIUtil.hiddenCell(sopTable, 5);
		sopTable.getTableHeader().setResizingAllowed(false);
		sopTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		String[] parametersTableHeader = new String[] {"oid", "编号", "参数项目名称", "专业类别", "工序名称", "物资类别",
				"参数值", "说明", "object" };
		parametersTable = new JTable();
		parametersTableModel = new CommonTableModel(parametersTableHeader, null, null);
		parametersTable.setModel(parametersTableModel);
		parametersTable.getTableHeader().setReorderingAllowed(false);
		parametersTable.getTableHeader().setResizingAllowed(false);

		CommonUIUtil.hiddenCell(parametersTable, 0);
		CommonUIUtil.hiddenCell(parametersTable, 8);

		loadButton = new JButton("加载");
		loadButton.setPreferredSize(new Dimension(90, 25));
		searchButton = new JButton("查询");
		searchButton.setPreferredSize(new Dimension(90, 25));
		useButton = new JButton("引用");
		useButton.setPreferredSize(new Dimension(90, 25));
		buttonPanel = new JPanel();
	}

	private void initLayout() {
		sopTable.setRowHeight(23);
		sopTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(sopTable);
		jScrollPane1.setPreferredSize(new Dimension(1000, 200));
		JPanel sopTablePanel = new JPanel();
		sopTablePanel.setBorder(BorderFactory.createTitledBorder("SOP查询结果"));
		sopTablePanel.setLayout(new BorderLayout());
		sopTablePanel.add(jScrollPane1);
		sopTablePanel.setPreferredSize(new Dimension(1000, 400));

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

		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		buttonPanel.setLayout(flowLayout);
		buttonPanel.setPreferredSize(new Dimension(100,700));
		buttonPanel.add(loadButton);
		buttonPanel.add(searchButton);
		buttonPanel.add(useButton);

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(middleMainPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.EAST);

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

					SopBean sopBean = (SopBean)sopTable.getValueAt(row, 5);
					updateParametersTable(sopBean);
				}
			}
		});
		CmActionListener listener = new CmActionListener();
		loadButton.addActionListener(listener);
		searchButton.addActionListener(listener);
		useButton.addActionListener(listener);
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

	private void loadInitDatas() {

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
	class CmActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == loadButton){
				load();
			}else if(e.getSource() == searchButton){
				search();
			}else if(e.getSource() == useButton){
				useSOP();
			}
		}
	}

	/**
	 * 查询并添加SOP文件
	 */
	private void search() {
		SearchSopStandardFileDialog dialog = null;
		if (frame != null) {
			dialog = new SearchSopStandardFileDialog(this, frame);
		} else {
			dialog = new SearchSopStandardFileDialog(this, parentDialog);
		}
		Vector<SopBean> vector = dialog.showDialog();
		if(vector != null){
			addData(vector);
		}
	}
	public void addData(Vector<SopBean> vector) {
//		if(tableModel.getRowCount() > 0) {
//			SwingUtil.showMessageDialog("一道工序或工步只能关联一份SOP文件！", "提示", 2);
//			return;
//		}
		sopTableModel.setRowCount(0);
		if(vector != null) {
			Vector<Object> rowData = null;
			for (SopBean sopBean : vector) {
//				List<ParametersBean> arameters = sopBean.getArameters();
//				if(arameters != null && arameters.size() > 0){
//					for(ParametersBean parametersBean : arameters){
						rowData = new Vector<Object>();
						rowData.add(sopBean.getOid());
						rowData.add(sopBean.getNumber());
						rowData.add(sopBean.getPpnumber());
						rowData.add(sopBean.getName());
						rowData.add(sopBean.getName());
						rowData.add(sopBean);
						sopTableModel.addRow(rowData);
						parametersTableModel.setRowCount(0);
//					}
//				}else{
//					rowData = new Vector<Object>();
//					rowData.add(sopBean.getOid());
//					rowData.add(sopBean.getNumber());
//					rowData.add(sopBean.getPpnumber());
//					rowData.add(sopBean.getName());
//					rowData.add("");
//					rowData.add(sopBean);
//					sopTableModel.addRow(rowData);
//				}
			}
		}
	}

	private void load() {
		try {
			// 按照当前SOP文件的专业类别、工序名称、部门自动查询出满足条件的SOP文件
			Document document = ((NewTechnicsPart)frame).getCurrentTechnics();
			Element techElement = XmlUtility.getTechnicsElement(document);
			XWTreeNode selectedTreeNode = ((NewTechnicsPart) frame).technicsTreePanel.getSelectedTreeNode();
			XWTreeObject selectObject = selectedTreeNode.getObject();
			if (techElement != null) {
				String specializedType;
				String proceduceName;
				String departmentName;
				if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					//专业类别
					specializedType = XmlUtility.getAttributeValue(techElement, SopConstants.SOP_IBA_SPECIALIZEDTYPE);
					//工序名称
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
					departmentName = XmlUtility.getAttributeValue(techElement, SopConstants.SOP_IBA_DEPARTMENT);
				}else{
					String technicsType = XmlUtility.getAttributeValue(techElement, "technicsType");
					specializedType = LoadConfig.getInstance().getZylb(technicsType);
					if(selectObject instanceof XWTechnicsTreeObject){
						proceduceName = "";
					}else if(selectObject instanceof XWStepTreeObject){
						XWStepTreeObject stepObj = (XWStepTreeObject) selectObject;
						Element stepElement = stepObj.getTreeCellData();
						proceduceName = XmlUtility.getAttributeValue(stepElement, "stepName");
					}else if(selectObject instanceof XWPaceTreeObject){
						XWTreeNode stepNode = (XWTreeNode) selectedTreeNode.getParent();
						Element stepElement = stepNode.getObject().getTreeCellData();
						proceduceName = XmlUtility.getAttributeValue(stepElement, "stepName");
					} else {
						proceduceName = "";
					}
					departmentName = XmlUtility.getAttributeValue(techElement, "DEPT");
				}
				if ((specializedType != null && specializedType.length() > 0)
						|| (proceduceName != null && proceduceName.length() > 0)
						|| (departmentName != null && departmentName.length() > 0)) {
					SopBean sopBean = new SopBean();
					sopBean.setSpecializedType(specializedType);
					sopBean.setProceduceName(proceduceName);
					sopBean.setDepartment(departmentName);
					List<SopBean> list = SopIntf.querySop(sopBean);
					if(list != null && list.size()>0) {
						sopTableModel.setRowCount(0);
						Vector<Object> rowData = null;
						for (SopBean bean : list) {
							// "oid", "SOP文件编号", "SOP文件名称","参数项目", "object"
//							List<ParametersBean> arameters = bean.getArameters();
//							for(ParametersBean parametersBean : arameters){
								rowData = new Vector<Object>();
								rowData.add(bean.getOid());
								rowData.add(bean.getNumber());
								rowData.add(bean.getPpnumber());
								rowData.add(bean.getName());
								rowData.add(bean.getName());
								rowData.add(bean);
								sopTableModel.addRow(rowData);
//							}
						}
					} else {
						SwingUtil.showMessageDialog("加载SOP标准规程结果为空！", "提示", 2);
					}
				} else {
					SwingUtil.showMessageDialog("专业类别、工序名称和部门为空，无法进行加载！", "提示", 2);
				}
			} else {
				SwingUtil.showMessageDialog("获取当前工艺规程为空，无法进行加载！", "提示", 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void useSOP() {
		XWTreeNode selXwTreeNode = ((NewTechnicsPart)frame).getTechnicsTreePanel().getSelectedTreeNode();
		if (selXwTreeNode != null) {
			XWTreeObject treeObject = selXwTreeNode.getObject();
			if ((treeObject instanceof XWStepTreeObject)) {
				TechnicsStepJPanel_XW technicsStepJPanel_XW = ((NewTechnicsPart)frame).getTechnicsStepJPanel();
				SopFileTableJPanel sopFileTableJPanel = technicsStepJPanel_XW.getSopFileTableJPanel();
				if (sopTable.getSelectedRows().length == 0) {
					SwingUtil.showMessageDialog("请选择SOP文件", "提示", 2);
				} else {
					Vector<Map<String, String>> equipments = new Vector<Map<String, String>>();
					for (int row : sopTable.getSelectedRows()) {
						equipments.addAll(generateObjectMap(row));
					}
					if(sopFileTableJPanel.getTable().getRowCount() > 0){
						SwingUtil.showMessageDialog("一道工序或工步只能关联一份SOP文件！", "提示", 2);
						return;
					}else{
						Vector<Map<String, String>> vector = new Vector<Map<String,String>>();
						List<String> list = new ArrayList<String>();
						List<ParametersBean> parameters = new ArrayList<ParametersBean>();
						if(equipments != null){
							Iterator<Map<String, String>> iterator = equipments.iterator();
							while(iterator.hasNext()){
								Map<String, String> map = iterator.next();
								ParametersBean pb = new ParametersBean();
								pb.setMaterialCategory(map.get("wzlb"));
								pb.setName(map.get("parameters"));
								pb.setCanShuZhi(map.get("canshuzhi"));
								parameters.add(pb);
								String pa = map.get("parameters");
								if(!list.contains(pa)){
									list.add(pa);
									vector.add(map);
								}
							}
						}
						sopFileTableJPanel.addData(vector,parameters);
						sopFileTableJPanel.saveToXml(vector);
						SwingUtil.showMessageDialog("引用成功！", "提示", 1);
					}
				}
			} else {
				SwingUtil.showMessageDialog("请在工艺树上选择工序节点！", "提示", 2);
			}
		} else {
			SwingUtil.showMessageDialog("请在工艺树上选择工序节点！", "提示", 2);
		}
	}

	public Vector<Map<String, String>> generateObjectMap(int row) {
		Vector<Map<String, String>> list = new Vector<Map<String,String>>();
		SopBean sopBean = (SopBean)sopTable.getValueAt(row, 5);
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

	public Element getTechElement() {
		NewTechnicsPart newTechnicsPart = (NewTechnicsPart) frame;
		Element techElement = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
		return techElement;
	}

}