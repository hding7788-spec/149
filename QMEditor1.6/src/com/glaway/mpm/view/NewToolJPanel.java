package com.glaway.mpm.view;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.qmIntf.frock.FrockCardSearchDialog;
import com.glaway.mpm.qmIntf.frock.FrockSearchDialog;
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

public class NewToolJPanel extends NewLinkJPanel {
	private JFrame parent;
	private JDialog parentDialog;
	private JButton searchFrockCardButton = new IconButton("", "申请卡");

	public NewToolJPanel(Container parentpanel, JFrame parent) {
		super(parentpanel);
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载工装面板");
		this.parent = parent;
		initOneself();
		// setTabTitle();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载工装面板");
	}

	public NewToolJPanel(Container parentpanel, JDialog parentDialog) {
		super(parentpanel);
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载工装面板");
		this.parentDialog = parentDialog;
		initOneself();
		// setTabTitle();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载工装面板");
	}

	protected void initOneself() {
		searchFrockCardButton.setVisible(false);
		ResourceTableCellEditor resourceTableCellEditor = new ResourceTableCellEditor(new JTextField());
		table.getColumnModel().getColumn(7).setCellEditor(resourceTableCellEditor);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (parentPanel != null && parentPanel instanceof TechnicsStepJPanel_XW) {
					JFrame frame = ((TechnicsStepJPanel_XW) parentPanel).getFrame();
					if (frame != null && frame instanceof NewTechnicsPart) {
						NewTechnicsPart tp = (NewTechnicsPart) frame;
						tp.getResourceTreePanel().deleteObservers();
						tp.getResourceTreePanel().addObserver(NewToolJPanel.this);
						tp.getLeftTab().setSelectedIndex(3);
					}
				}
			}
		});

		addJButton.setVisible(false);
		panel.add(searchJButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(searchFrockCardButton, new GridBagConstraints(1, 1, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 0, 5), 0, 0));
		setHiddenColumn(5);
		setHiddenColumn(6);
		setHiddenColumn(9);
		searchJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTableCellEditing();
				FrockSearchDialog dia = null;
				if (parent != null) {
					dia = new FrockSearchDialog(parent, NewToolJPanel.this);
				} else {
					dia = new FrockSearchDialog(parentDialog, NewToolJPanel.this);
				}
				Vector vec = dia.showDialog();
				addData(vec);
			}
		});

		searchFrockCardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTableCellEditing();
				FrockCardSearchDialog dia = null;
				if (parent != null) {
					dia = new FrockCardSearchDialog(parent, NewToolJPanel.this);
				} else {
					dia = new FrockCardSearchDialog(parentDialog, NewToolJPanel.this);
				}
				dia.showDialog(null);
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
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(1, "工装" + "(" + i + ")");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(1, "工装" + "(" + i + ")");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(1, "工装" + "(" + i + ")");
			}
		} else {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(1, "工装");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(1, "工装");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(1, "工装");
			}
		}
	}

	protected void addModelColumn() {
		tableModel.addColumn("编号");
		tableModel.addColumn("名称");
		tableModel.addColumn("工装类别");
		tableModel.addColumn("规格");
		tableModel.addColumn("型号");
		tableModel.addColumn("oid");
		tableModel.addColumn("frockCardNum");
		tableModel.addColumn("*使用数量");
		tableModel.addColumn("备注");
		tableModel.addColumn("英文名称");

	}

	public Vector<Element> getElements() {
		stopTableCellEditing();
		Vector<Element> elements = new Vector<Element>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (isRowNull(i))
				continue;
			Element element = XmlUtility.createTool();
			XmlUtility.setAttributeValue(element, "bsoID", "");
			XmlUtility.setAttributeValue(element, "toolNum", (String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "toolName", (String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "frockType", (String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "csize", (String) tableModel.getValueAt(i, 3));
			XmlUtility.setAttributeValue(element, "toolSpec", (String) tableModel.getValueAt(i, 4));
			XmlUtility.setAttributeValue(element, "oid", (String) tableModel.getValueAt(i, 5));
			XmlUtility.setAttributeValue(element, "frockCardNum", (String) tableModel.getValueAt(i, 6));
			XmlUtility.setAttributeValue(element, "useCount", (String) tableModel.getValueAt(i, 7));
			XmlUtility.setAttributeValue(element, "bz", (String) tableModel.getValueAt(i, 8));
			XmlUtility.setAttributeValue(element, "EnglishName", (String) tableModel.getValueAt(i, 9));
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
		if (element != null && element.getName().equals(XmlUtility.TOOL_TAG)) {
			addProcess();
			int i = tableModel.getRowCount();
			i--;
			tableModel.setValueAt(element.attributeValue("toolNum"), i, 0);
			tableModel.setValueAt(element.attributeValue("toolName"), i, 1);
			tableModel.setValueAt(element.attributeValue("frockType"), i, 2);
			tableModel.setValueAt(element.attributeValue("csize"), i, 3);
			tableModel.setValueAt(element.attributeValue("toolSpec"), i, 4);
			tableModel.setValueAt(element.attributeValue("oid"), i, 5);
			tableModel.setValueAt(element.attributeValue("frockCardNum"), i, 6);
			tableModel.setValueAt(element.attributeValue("useCount"), i, 7);
			tableModel.setValueAt(element.attributeValue("bz"), i, 8);
			tableModel.setValueAt(element.attributeValue("EnglishName"), i, 9);
		}
	}

	public void setOneRowTableValue(Map map, String prefix) {
		String number = (String) map.get(prefix + "Num");
		if (isHasExistData(tableModel, 0, number))
			return;
		addProcess();
		int i = tableModel.getRowCount();
		i--;
		tableModel.setValueAt(number, i, 0);
		tableModel.setValueAt(map.get(prefix + "Name"), i, 1);
		tableModel.setValueAt(map.get(prefix + "StdNum"), i, 2);
		tableModel.setValueAt(map.get(prefix + "Spec"), i, 3);
		tableModel.setValueAt(map.get(prefix + "Type"), i, 4);
		tableModel.setValueAt(map.get("useCount"), i, 5);
		tableModel.setValueAt(map.get("oid"), i, 5);
		tableModel.setValueAt(map.get("frockCardNum"), i, 6);
		tableModel.setValueAt(map.get("useCount"), i, 7);
		tableModel.setValueAt(map.get("bz"), i, 8);
		tableModel.setValueAt(map.get("EnglishName"), i, 9);
	}

	public void setOneRowTableValue(Map map) {
		String number = (String) map.get("toolNum");
		if(number.startsWith("A%")) {
			number = number.replace("A%", "");
		}
		if(number.startsWith("B%")) {
			number = number.replace("B%", "");
		}
		if (isHasExistData(tableModel, 0, number))
			return;
		addProcess();
		int i = tableModel.getRowCount();
		i--;
		tableModel.setValueAt(number, i, 0);
		tableModel.setValueAt(map.get("toolName"), i, 1);
		tableModel.setValueAt(map.get("frockType"), i, 2);
		tableModel.setValueAt(map.get("csize"), i, 3);
		tableModel.setValueAt(map.get("toolSpec"), i, 4);
		tableModel.setValueAt(map.get("oid"), i, 5);
		tableModel.setValueAt(map.get("frockCardNum"), i, 6);
		tableModel.setValueAt(map.get("useCount"), i, 7);
		tableModel.setValueAt(map.get("bz"), i, 8);
		tableModel.setValueAt(map.get("EnglishName"), i, 9);
	}

	// 监听工装资源树主题
	public void update(Observable o, Object arg) {
		if (!isEnabled())
			return;
		// 判断类别，工装树发出的主题，获取去发出的Vector数据，和搜索结果结构一样，Vector内嵌Map
		if (o != null && o instanceof CommonObservable) {
			CommonObservable co = (CommonObservable) o;
			int type = co.getType();
			if (type == 2)// 表示是
			{
				if (arg != null && arg instanceof Map) {
					Map m = (Map) arg;
					System.out.println("----------------m--------------"+m);
					setOneRowTableValue(m);
					int i = tableModel.getRowCount();
					i--;
					table.setRowSelectionInterval(i, i);
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setSelectedIndex(1);
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