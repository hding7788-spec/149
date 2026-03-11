package com.glaway.mpm.view;

import com.glaway.mpm.qmIntf.equipment.EquipmentSearchDialog;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;

public class NewEquipJPanel extends NewLinkJPanel {
	private JFrame frame;
	private JDialog parentDialog;

	public NewEquipJPanel(Container parentpanel, JFrame frame) {
		super(parentpanel);
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载设备面板");
		this.frame = frame;
		initOneself();
		// setTabTitle();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载设备面板");
	}

	public NewEquipJPanel(Container parentpanel, JDialog parentDialog) {
		super(parentpanel);
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载设备面板");
		this.parentDialog = parentDialog;
		initOneself();
		// setTabTitle();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载设备面板");
	}

	protected void initOneself() {
		ResourceTableCellEditor resourceTableCellEditor = new ResourceTableCellEditor(new JTextField());
		table.getColumnModel().getColumn(6).setCellEditor(resourceTableCellEditor);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (parentPanel != null && parentPanel instanceof TechnicsStepJPanel_XW) {
					JFrame frame = ((TechnicsStepJPanel_XW) parentPanel).getFrame();
					if (frame != null && frame instanceof NewTechnicsPart) {
						NewTechnicsPart tp = (NewTechnicsPart) frame;
						tp.getEpTreePanel().deleteObservers();
						tp.getEpTreePanel().addObserver(NewEquipJPanel.this);// 设备
						tp.getLeftTab().setSelectedIndex(2);
					}
				}
			}
		});

		addJButton.setVisible(false);
		panel.add(searchJButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		setHiddenColumn(0);
		setHiddenColumn(8);
		searchJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTableCellEditing();
				EquipmentSearchDialog dialog = null;
				if (frame != null) {
					dialog = new EquipmentSearchDialog(NewEquipJPanel.this, frame);
				} else {
					dialog = new EquipmentSearchDialog(NewEquipJPanel.this, parentDialog);
				}
				Vector vec = dialog.showDialog();

				addData(vec);
			}
		});
		panel.add(setNoteJButton, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		setNoteJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() > 1)
					return;
				int select = table.getSelectedRow();
				if (select < 0 || select >= table.getRowCount())
					return;
				SetNoteDialog dia = null;
				if (frame != null) {
					dia = new SetNoteDialog(frame, NewEquipJPanel.this);
				} else {
					dia = new SetNoteDialog(parentDialog,NewEquipJPanel.this);
				}
				String note =dia.showDialog();

				table.getModel().setValueAt(note, select, 6);
			}
		});
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

	public void setTabTitle() {
		int i = tableModel.getRowCount();
		if (i > 0) {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(0, "设备" + "(" + i + ")");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(0, "设备" + "(" + i + ")");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(0, "设备" + "(" + i + ")");
			}
		} else {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(0, "设备");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(0, "设备");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(0, "设备");
			}
		}
	}

	protected void addModelColumn() {
		//隐藏字段
		tableModel.addColumn("oid");
		//显示字段
		tableModel.addColumn("设备编号");
		tableModel.addColumn("设备名称");
		tableModel.addColumn("设备型号");
		tableModel.addColumn("类别");
		tableModel.addColumn("规格");
		tableModel.addColumn("*使用数量");
		tableModel.addColumn("备注");
		tableModel.addColumn("英文名称");
		table.updateUI();
	}

	public Vector<Element> getElements() {
		stopTableCellEditing();
		Vector<Element> elements = new Vector<Element>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (isRowNull(i)) {
				continue;
			}
			Element element = XmlUtility.createEquip();
//			XmlUtility.setAttributeValue(element, "equipmentNumber", (String) tableModel.getValueAt(i, 0));
//			XmlUtility.setAttributeValue(element, "eqName", (String) tableModel.getValueAt(i, 1));
//			XmlUtility.setAttributeValue(element, "eqModel", (String) tableModel.getValueAt(i, 2));
//			XmlUtility.setAttributeValue(element, "oid", (String) tableModel.getValueAt(i, 3));

			XmlUtility.setAttributeValue(element, "oid",(String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "number",(String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "name",(String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "pindex",(String) tableModel.getValueAt(i, 3));
			XmlUtility.setAttributeValue(element, "equipmentType",(String) tableModel.getValueAt(i, 4));
			XmlUtility.setAttributeValue(element, "csize",(String) tableModel.getValueAt(i, 5));
			XmlUtility.setAttributeValue(element, "useCount",(String) tableModel.getValueAt(i, 6));
			XmlUtility.setAttributeValue(element, "bz",(String) tableModel.getValueAt(i, 7));
			XmlUtility.setAttributeValue(element, "EnglishName",(String) tableModel.getValueAt(i, 8));

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
		if (element != null && element.getName().equals(XmlUtility.EQUIP_TAG)) {
			addProcess();
			int i = tableModel.getRowCount();
			i--;
			tableModel.setValueAt(element.attributeValue("oid"), i, 0);
			tableModel.setValueAt(element.attributeValue("number"), i,1);
			tableModel.setValueAt(element.attributeValue("name"), i, 2);
			tableModel.setValueAt(element.attributeValue("pindex"), i, 3);
			tableModel.setValueAt(element.attributeValue("equipmentType"), i, 4);
			tableModel.setValueAt(element.attributeValue("csize"), i, 5);
			tableModel.setValueAt(element.attributeValue("useCount"), i, 6);
			tableModel.setValueAt(element.attributeValue("bz"), i, 7);
			tableModel.setValueAt(element.attributeValue("EnglishName"), i, 8);
		}
	}

	private void setOneRowTableValue(Map map) {
		String equipmentNumber = (String) map.get("equipmentNumber");
		if (isHasExistData(tableModel, 0, equipmentNumber))
			return;
		addProcess();
		int i = tableModel.getRowCount();
		i--;
//		tableModel.setValueAt(equipmentNumber, i, 0);
//		tableModel.setValueAt(map.get("name"), i, 1);
//		tableModel.setValueAt(map.get("modelNumber"), i, 2);
//		// tableModel.setValueAt(map.get("useCount"), i, 3);
//		tableModel.setValueAt(map.get("oid"), i, 3);

		tableModel.setValueAt(map.get("oid"), i, 0);
		tableModel.setValueAt(map.get("number"), i,1);
		tableModel.setValueAt(map.get("name"), i, 2);
		tableModel.setValueAt(map.get("mindex"), i, 3);
		tableModel.setValueAt(map.get("equipmentType"), i, 4);
		tableModel.setValueAt(map.get("csize"), i, 5);
		tableModel.setValueAt(map.get("useCount"), i, 6);
		tableModel.setValueAt(map.get("bz"), i, 7);
		tableModel.setValueAt(map.get("EnglishName"), i, 8);
	}

	// 监听设备资源树主题
	public void update(Observable o, Object arg) {
		if (!isEnabled())
			return;
		// 判断类别，设备树发出的主题，获取去发出的Vector数据，和搜索结果结构一样，Vector内嵌Map
		if (o != null && o instanceof CommonObservable) {
			CommonObservable co = (CommonObservable) o;
			int type = co.getType();
			if (type == 1)// 表示是设备
			{
				if (arg != null && arg instanceof Map) {
					Map m = (Map) arg;
					setOneRowTableValue(m);
					int i = tableModel.getRowCount();
					i--;
					table.setRowSelectionInterval(i, i);
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setSelectedIndex(0);
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().repaint();
				}
			}
		}
	}

	public Integer getRow(){
		int i = tableModel.getRowCount();
		return i;
	}
}