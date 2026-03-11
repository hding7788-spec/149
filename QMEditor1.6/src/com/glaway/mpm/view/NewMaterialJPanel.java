package com.glaway.mpm.view;

import com.glaway.mpm.editor.JTextFiledEditor2;
import com.glaway.mpm.qmIntf.material.MaterialSearchDialog;
import com.glaway.mpm.qmIntf.material.MaterialUtil;
import com.glaway.mpm.qmIntf.material.PartMaterialSearchDialog;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.ResourceIntf;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class NewMaterialJPanel extends NewLinkJPanel {
	private static final long serialVersionUID = 1L;
	private JFrame parent;
	private JDialog parentDialog;
	private Map workShop = null;

	public NewMaterialJPanel(Container parentPanel, JFrame parent) {
		super(parentPanel, "");
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载辅料面板");
		this.parent = parent;
		workShop = ResourceIntf.getWorkShops();
		initOneself();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载辅料面板");
	}

	public NewMaterialJPanel(Container parentpanel, JDialog parentDialog) {
		super(parentpanel, "");
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载辅料面板");
		this.parentDialog = parentDialog;
		initOneself();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载辅料面板");
	}

	class Editor extends DefaultCellEditor {
		JComboBox e = null;
		// 用于记录当前表格
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		public Editor(JComboBox comboBox, JTable table) {
			super(comboBox);
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			editingTable = table;
			editingRow = row;
			editingColumn = column;
			e = null;
			if (value == null)
				value = "";
			e = createBox(table, value, row, column);
			return e;
		}

		public Object getCellEditorValue() {
			if (e != null) {
				Object value = e.getSelectedItem();
				if (value == null)
					value = "";
				if ((editingTable != null)
						&& (editingRow >= 0 && editingRow < editingTable.getRowCount())
						&& (editingColumn >= 0 && editingColumn < editingTable.getColumnCount())) {
						table.setValueAt(value, editingRow, editingColumn);
				}
				return value;
			}

			return "";
		}
	}

	protected void initOneself() {
		ResourceTableCellEditor resourceTableCellEditor = new ResourceTableCellEditor(new JTextField());
		table.getColumnModel().getColumn(11).setCellEditor(resourceTableCellEditor);
		table.setDefaultRenderer(Object.class, new LabelRender(true));

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					if (parentPanel != null && parentPanel instanceof TechnicsStepJPanel_XW) {
						JFrame frame = ((TechnicsStepJPanel_XW) parentPanel).getFrame();
						if (frame != null && frame instanceof NewTechnicsPart) {
							NewTechnicsPart tp = (NewTechnicsPart) frame;
//							Point p = e.getPoint();
//							int row = table.rowAtPoint(p);
//							int column = table.columnAtPoint(p);
//							if (column == 7 && row != -1) {
//								tp.getCSTreePanel().deleteObservers();
//								tp.getCSTreePanel().addObserver(
//										NewMaterialJPanel.this);//
//								tp.getLeftTab().setSelectedIndex(5);
//							} else {
								tp.getMTPanel().deleteObservers();
								tp.getMTPanel().addObserver(NewMaterialJPanel.this);//
								tp.getLeftTab().setSelectedIndex(4);
//							}
						}
					}
				}
			}
		});

//		this.table.getColumnModel().getColumn(1).setCellRenderer(new SingleSizeCellRenderer());
//		this.table.getColumnModel().getColumn(1).setCellEditor(new SingleSizeCellEditor(new JTextField()));

		TableColumnModel columnModel = table.getColumnModel();
//		columnModel.getColumn(11).setCellEditor(new JTextFiledEditor(new JTextField(),false));
		columnModel.getColumn(13).setCellEditor(new JTextFiledEditor2(new JTextField()));

		Editor typeEditor = new Editor(new JComboBox(), table);
		columnModel.getColumn(12).setCellEditor(typeEditor);// 制造单位

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// calQuotaButton.setVisible(true);
		getMaterialJButton.setVisible(true);
		getPartMaterial.setVisible(false);
		addJButton.setVisible(false);
		upJButton.setVisible(false);
		downJButton.setVisible(false);
		searchJButton.setVisible(false);

		// calQuotaButton.addActionListener(new java.awt.event.ActionListener()
		// {
		// public void actionPerformed(ActionEvent e) {
		// calQuota();
		// }
		// });

		getMaterialJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getMaterial();
			}
		});

		getPartMaterial.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getPartMaterial();
			}
		});

		hiddenCell(0);
		hiddenCell(1);
		hiddenCell(2);
		hiddenCell(3);
		hiddenCell(14);

	}

	/**
	 * @Description: 计算材料定额
	 * @param
	 * @return void
	 */
	public void calQuota() {
		int i = table.getSelectedRow();
		if (i == -1) {
			JOptionPane.showMessageDialog(null, "请选择一行材料！", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		Map<String, String> map = MaterialUtil.calQuota(MaterialUtil.getOneRowMap(i, table));
		if (map == null) {
			return;
		}
		String quota = map.get("quota");
		String unit = map.get("unit");
		table.setValueAt(quota, i, 10);
		table.setValueAt(unit, i, 11);
	}

	/**
	 * @Description: 获取材料
	 * @param
	 * @return void
	 */
	public void getMaterial() {
		// int i = table.getSelectedRow();
		MaterialSearchDialog dia = null;
		// if (i == -1) {
		if (parent != null) {
			dia = new MaterialSearchDialog(null, null, null, false, parent, this);
		} else {
			dia = new MaterialSearchDialog(null, null, null, false, parentDialog, this);
		}
		// } else {
		// String materialNumber = table.getValueAt(i, 0).toString();
		// String materialName = table.getValueAt(i, 1).toString();
		// if (parent != null) {
		// dia = new MaterialSearchDialog("", materialName,
		// materialNumber, true, parent, this);
		// } else {
		// dia = new MaterialSearchDialog("", materialName,
		// materialNumber, true, parentDialog, this);
		// }
		// }
		Vector<Map<String, String>> vec = dia.showDialog();
		addData(vec);
	}

	public void addData(Vector vec) {
		if (vec != null && vec.size() > 0) {
			for (int i = 0; i < vec.size(); i++) {
				Map map = (Map) vec.get(i);
				setOneRowTableValue(map);
			}
		}
		setTabTitle();
	}

	/**
	 * @Description: 获取外购件，标准件作为材料
	 * @param
	 * @return void
	 */
	public void getPartMaterial() {
		PartMaterialSearchDialog dia = null;
		if (parent != null) {
			dia = new PartMaterialSearchDialog(parent);
		} else {
			dia = new PartMaterialSearchDialog(parentDialog);
		}
		Vector<Map<String, String>> vec = dia.showDialog();
		if (vec != null && vec.size() > 0) {
			for (int j = 0; j < vec.size(); j++) {
				Map<String, String> map = vec.get(j);
				setOneRowTableValue(map);
			}
		}
	}

	public void setTabTitle() {
		int i = tableModel.getRowCount();
		if (i > 0) {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(2, "工艺辅料" + "(" + i + ")");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(2, "工艺辅料" + "(" + i + ")");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(2, "工艺辅料" + "(" + i + ")");
			}
		} else {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(2, "工艺辅料");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(2, "工艺辅料");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(2, "工艺辅料");
			}
		}
	}

	protected void addModelColumn() {
		tableModel.addColumn("oid");// 0
		tableModel.addColumn("number");// 1
		tableModel.addColumn("bsoID");// 2
		tableModel.addColumn("toolTip");// 3

		tableModel.addColumn("编码");// 4
		tableModel.addColumn("名称");// 5
		tableModel.addColumn("型号");// 6
		tableModel.addColumn("规格");// 7
		tableModel.addColumn("技术条件");// 8
		tableModel.addColumn("计量单位");// 9
		tableModel.addColumn("附加条件");// 10
		tableModel.addColumn("*使用数量");// 11
		tableModel.addColumn("使用车间");// 12
		tableModel.addColumn("备注");// 13
		tableModel.addColumn("英文名称");// 14

	}

	public String convertString(String string, String source, String out) {
		try {
			return string == null ? string : new String(string.getBytes(source), out);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return string;
		}
	}

	public Vector<Element> getElements() {
		stopTableCellEditing();
		Vector<Element> elements = new Vector<Element>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (isRowNull(i))
				continue;
			Element element = XmlUtility.createMaterial();

			//TODO 设置 工序材料属性
			XmlUtility.setAttributeValue(element, "oid",(String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "number",(String) tableModel.getValueAt(i,1));
			XmlUtility.setAttributeValue(element, "bsoID",(String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "toolTip",(String) tableModel.getValueAt(i, 3));

			XmlUtility.setAttributeValue(element, "materialNumber",(String) tableModel.getValueAt(i, 4));
			XmlUtility.setAttributeValue(element, "materialName",(String) tableModel.getValueAt(i, 5));
			XmlUtility.setAttributeValue(element, "mindex",(String) tableModel.getValueAt(i, 6));
			XmlUtility.setAttributeValue(element, "csize",(String) tableModel.getValueAt(i, 7));
			XmlUtility.setAttributeValue(element, "jstj",(String) tableModel.getValueAt(i, 8));
			XmlUtility.setAttributeValue(element, "jldw",(String) tableModel.getValueAt(i, 9));
			XmlUtility.setAttributeValue(element, "fjtj",(String) tableModel.getValueAt(i, 10));
			XmlUtility.setAttributeValue(element, "sl",String.valueOf(tableModel.getValueAt(i, 11)));
			XmlUtility.setAttributeValue(element, "sycj",(String) tableModel.getValueAt(i, 12));
			XmlUtility.setAttributeValue(element, "bz",(String) tableModel.getValueAt(i, 13));
			XmlUtility.setAttributeValue(element, "EnglishName",(String) tableModel.getValueAt(i, 14));

			elements.add(element);
		}
		return elements;
	}

	public void setTableValues(Vector<Element> vec) {
		clearTable();
		for (int i = 0; i < vec.size(); i++) {
			Element element = vec.get(i);
			setOneRowTableValue(element);
		}
	}

	public void setOneRowTableValue(Element element) {
		if (element != null
				&& element.getName().equals(XmlUtility.MATERIAL_TAG)) {
			addProcess();
			int i = tableModel.getRowCount();
			i--;

			//TODO  812 设置 工序材料属性
			tableModel.setValueAt(element.attributeValue("oid"), i, 0);
			tableModel.setValueAt(element.attributeValue("number"), i, 1);
			tableModel.setValueAt(element.attributeValue("bsoID"), i, 2);
			tableModel.setValueAt(element.attributeValue("toolTip"), i, 3);

			tableModel.setValueAt(element.attributeValue("materialNumber"), i,4);
			tableModel.setValueAt(element.attributeValue("materialName"), i, 5);
			tableModel.setValueAt(element.attributeValue("mindex"), i, 6);
			tableModel.setValueAt(element.attributeValue("csize"), i,7);
			tableModel.setValueAt(element.attributeValue("jstj"),i, 8);
			tableModel.setValueAt(element.attributeValue("jldw"), i, 9);
			tableModel.setValueAt(element.attributeValue("fjtj"), i,10);
			tableModel.setValueAt(element.attributeValue("sl"),i, 11);
			tableModel.setValueAt(element.attributeValue("sycj"), i, 12);
			tableModel.setValueAt(element.attributeValue("bz"), i,13);
			tableModel.setValueAt(element.attributeValue("EnglishName"), i,14);
		}
	}
	/**
	 * 辅料树 双击 设置 辅料table值
	 * @param map
	 */
	private void setOneRowTableValue(Map<String, String> map) {
		System.out.println("materialQuota===========" + map.get("materialQuota"));
		String number = map.get("number");
		if (isHasExistData(tableModel, 1, number))
			return;
		addProcess();
		int i = tableModel.getRowCount();
		i--;

		tableModel.setValueAt(map.get("oid") == null ? "" : map.get("oid"), i,0);
		tableModel.setValueAt(map.get("number") == null ? "" : map.get("number"), i, 1);
		tableModel.setValueAt(map.get("bsoID") == null ? "" : map.get("bsoID"),i, 2);
		tableModel.setValueAt(map.get("toolTip") == null ? "" : map.get("toolTip"), i, 3);
		tableModel.setValueAt(map.get("materialNumber") == null ? "" : map.get("materialNumber"), i, 4);
		tableModel.setValueAt(map.get("materialName") == null ? "" : map.get("materialName"),i, 5);
		tableModel.setValueAt(map.get("mindex") == null ? "" : map.get("mindex"), i, 6);
		tableModel.setValueAt(map.get("csize") == null ? "" : map.get("csize"), i, 7);
		tableModel.setValueAt(map.get("jstj") == null ? "" : map.get("jstj"), i, 8);
		tableModel.setValueAt(map.get("jldw") == null ? "" : map.get("jldw"), i, 9);
		tableModel.setValueAt(map.get("fjtj") == null ? "" : map.get("fjtj"), i, 10);
		tableModel.setValueAt("", i, 11);
		tableModel.setValueAt(map.get("EnglishName") == null ? "" : map.get("EnglishName"), i, 14);
	}

	private void hiddenCell(int column) {
		TableColumn tc = table.getTableHeader().getColumnModel().getColumn(column);
		tc.setMaxWidth(0);
		tc.setPreferredWidth(0);
		tc.setWidth(0);
		tc.setMinWidth(0);
		table.getTableHeader().getColumnModel().getColumn(column).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(column).setMinWidth(0);
	}

	private JComboBox createBox(JTable table, Object value, int row, int column) {
		JComboBox box = new JComboBox();
		box.addItem("");
		if (value == null || value.toString().trim().length() == 0)
			value = "";
		if (column == 12) {
			if (workShop != null && workShop.size() > 0) {
				Collection coll = workShop.values();
				Iterator it = coll.iterator();
				while (it.hasNext()) {
					box.addItem(it.next());
				}
			}
			box.setSelectedItem(value);
		}

		return box;
	}

	// 监听材料资源树主题
	public void update(Observable o, Object arg) {
		if (!isEnabled())
			return;
		// 判断类别，材料树发出的主题，获取去发出的Vector数据，和搜索结果结构一样，Vector内嵌Map
		if (o != null && o instanceof CommonObservable) {
			CommonObservable co = (CommonObservable) o;
			int type = co.getType();
			if (type == 3)// 表示是工艺辅料
			{
				if (arg != null && arg instanceof Map) {
					Map<String, String> map = (Map<String, String>) arg;
					setOneRowTableValue(map);
					int i = tableModel.getRowCount();
					i--;
					table.setRowSelectionInterval(i, i);
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setSelectedIndex(2);
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().repaint();
				}
			} else if (type == 0)// 表示是常用语
			{
				if (arg != null && arg instanceof String) {
					int row = table.getSelectedRow();
					String value = table.getValueAt(row, 7).toString();
					if (row != -1) {
						table.setValueAt(value + arg.toString(), row, 7);
					}
				}
			}
		}
	}
	public Integer getRow(){
		int i = tableModel.getRowCount();
		return i;
	}
}

