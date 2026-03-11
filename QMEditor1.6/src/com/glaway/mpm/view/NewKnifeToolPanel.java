package com.glaway.mpm.view;

import com.glaway.mpm.qmIntf.frock.FrockCardSearchDialog;
import com.glaway.mpm.qmIntf.frock.KnifeSearchDialog;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;

public class NewKnifeToolPanel extends NewLinkJPanel {

	private static final long serialVersionUID = 1L;
	private JFrame parent;
	private JDialog parentDialog;
	private JButton searchFrockCardButton = new IconButton("", "申请卡");

	public NewKnifeToolPanel(Container parentpanel, JFrame parent) {
		super(parentpanel);
		this.parent = parent;
		initOneself();
		// setTabTitle();
	}

	public NewKnifeToolPanel(Container parentpanel, JDialog parentDialog) {
		super(parentpanel);
		this.parentDialog = parentDialog;
		initOneself();
		// setTabTitle();
	}

	protected void initOneself() {
		searchFrockCardButton.setVisible(false);
		ResourceTableCellEditor resourceTableCellEditor = new ResourceTableCellEditor(new JTextField());
		table.getColumnModel().getColumn(17).setCellEditor(resourceTableCellEditor);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (parentPanel != null && parentPanel instanceof TechnicsStepJPanel_XW) {
					JFrame frame = ((TechnicsStepJPanel_XW) parentPanel).getFrame();
					if (frame != null && frame instanceof NewTechnicsPart) {
						NewTechnicsPart tp = (NewTechnicsPart) frame;
						tp.getResourceTreePanel().deleteObservers();
						tp.getResourceTreePanel().addObserver(NewKnifeToolPanel.this);
						tp.getLeftTab().setSelectedIndex(5);
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
//		setHiddenColumn(4);

		searchJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTableCellEditing();
				KnifeSearchDialog dia = null;
				if (parent != null) {
					dia = new KnifeSearchDialog(parent, NewKnifeToolPanel.this,null);
				} else {
					dia = new KnifeSearchDialog(parentDialog, NewKnifeToolPanel.this);
				}
				Vector vec = dia.showDialog();
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
				if (parent != null) {
					dia = new SetNoteDialog(parent, NewKnifeToolPanel.this);
				} else {
					dia = new SetNoteDialog(parentDialog,NewKnifeToolPanel.this);
				}
				String note =dia.showDialog();

				table.getModel().setValueAt(note, select, 17);
			}
		});
		searchFrockCardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTableCellEditing();
				FrockCardSearchDialog dia = null;
				if (parent != null) {
					dia = new FrockCardSearchDialog(parent, NewKnifeToolPanel.this,null);
				} else {
					dia = new FrockCardSearchDialog(parentDialog, NewKnifeToolPanel.this);
				}
				dia.showDialog(null);
			}
		});

		hiddenCell(16);
		hiddenCell(19);
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
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(3, "刀具" + "(" + i + ")");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(5, "刀具" + "(" + i + ")");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(3, "刀具" + "(" + i + ")");
			}
		} else {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(3, "刀具");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(5, "刀具");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(3, "刀具");
			}
		}
	}

	protected void addModelColumn() {
		tableModel.addColumn("编号");
		tableModel.addColumn("名称");
		tableModel.addColumn("类别");
		tableModel.addColumn("材料");
		tableModel.addColumn("刃口直径");
		tableModel.addColumn("夹持直径");
		tableModel.addColumn("刃口长度");
		tableModel.addColumn("总长度");
		tableModel.addColumn("公差");
		tableModel.addColumn("最小加工尺寸");
		tableModel.addColumn("最大加工尺寸");
		tableModel.addColumn("结构形式");
		tableModel.addColumn("接口类型");
		tableModel.addColumn("技术备注");
		tableModel.addColumn("刃口圆角半径");
		tableModel.addColumn("齿数");
		tableModel.addColumn("oid");
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
			Element element = XmlUtility.createKnifeTool();
			XmlUtility.setAttributeValue(element, "bsoID", "");
			XmlUtility.setAttributeValue(element, "toolNum", (String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "toolName", (String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "knifetype", (String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "cmat", (String) tableModel.getValueAt(i, 3));
			XmlUtility.setAttributeValue(element, "rkzj", (String) tableModel.getValueAt(i, 4));
			XmlUtility.setAttributeValue(element, "jczj", (String) tableModel.getValueAt(i, 5));
			XmlUtility.setAttributeValue(element, "rkcd", (String) tableModel.getValueAt(i, 6));
			XmlUtility.setAttributeValue(element, "zcd", (String) tableModel.getValueAt(i, 7));
			XmlUtility.setAttributeValue(element, "gc", (String) tableModel.getValueAt(i, 8));
			XmlUtility.setAttributeValue(element, "zxjgcc", (String) tableModel.getValueAt(i, 9));
			XmlUtility.setAttributeValue(element, "zdjgcc", (String) tableModel.getValueAt(i, 10));
			XmlUtility.setAttributeValue(element, "jgxs", (String) tableModel.getValueAt(i, 11));
			XmlUtility.setAttributeValue(element, "jklx", (String) tableModel.getValueAt(i, 12));
			XmlUtility.setAttributeValue(element, "jsbz", (String) tableModel.getValueAt(i, 13));
			XmlUtility.setAttributeValue(element, "rkyjbj", (String) tableModel.getValueAt(i, 14));
			XmlUtility.setAttributeValue(element, "cs", (String) tableModel.getValueAt(i, 15));
			XmlUtility.setAttributeValue(element, "oid", (String) tableModel.getValueAt(i, 16));
			XmlUtility.setAttributeValue(element, "useCount", (String) tableModel.getValueAt(i, 17));
			XmlUtility.setAttributeValue(element, "bz", (String) tableModel.getValueAt(i, 18));
			XmlUtility.setAttributeValue(element, "EnglishName", (String) tableModel.getValueAt(i, 19));
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
		if (element != null && element.getName().equals(XmlUtility.KNIFETOOL_TAG)) {
			addProcess();
			int i = tableModel.getRowCount();
			i--;
			tableModel.setValueAt(element.attributeValue("toolNum"), i, 0);
			tableModel.setValueAt(element.attributeValue("toolName"), i, 1);
			tableModel.setValueAt(element.attributeValue("knifetype"), i, 2);
			tableModel.setValueAt(element.attributeValue("cmat"), i, 3);
			tableModel.setValueAt(element.attributeValue("rkzj"), i, 4);
			tableModel.setValueAt(element.attributeValue("jczj"), i, 5);
			tableModel.setValueAt(element.attributeValue("rkcd"), i, 6);
			tableModel.setValueAt(element.attributeValue("zcd"), i, 7);
			tableModel.setValueAt(element.attributeValue("gc"), i, 8);
			tableModel.setValueAt(element.attributeValue("zxjgcc"), i, 9);
			tableModel.setValueAt(element.attributeValue("zdjgcc"), i, 10);
			tableModel.setValueAt(element.attributeValue("jgxs"), i, 11);
			tableModel.setValueAt(element.attributeValue("jklx"), i, 12);
			tableModel.setValueAt(element.attributeValue("jsbz"), i, 13);
			tableModel.setValueAt(element.attributeValue("rkyjbj"), i, 14);
			tableModel.setValueAt(element.attributeValue("cs"), i, 15);
			tableModel.setValueAt(element.attributeValue("oid"), i, 16);
			tableModel.setValueAt(element.attributeValue("useCount"), i, 17);
			tableModel.setValueAt(element.attributeValue("bz"), i, 18);
			tableModel.setValueAt(element.attributeValue("EnglishName"), i, 19);
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
		tableModel.setValueAt(map.get("oid"), i, 6);
	}

	public void setOneRowTableValue(Map map) {
		String number = (String) map.get("toolNum");
		if (isHasExistData(tableModel, 0, number))
			return;
		addProcess();
		int i = tableModel.getRowCount();
		i--;
		tableModel.setValueAt(number, i, 0);
		tableModel.setValueAt(map.get("toolName"), i, 1);
		tableModel.setValueAt(map.get("knifetype"), i, 2);
		tableModel.setValueAt(map.get("cmat"), i, 3);
		tableModel.setValueAt(map.get("rkzj"), i, 4);
		tableModel.setValueAt(map.get("jczj"), i, 5);
		tableModel.setValueAt(map.get("rkcd"), i, 6);
		tableModel.setValueAt(map.get("zcd"), i, 7);
		tableModel.setValueAt(map.get("gc"), i, 8);
		tableModel.setValueAt(map.get("zxjgcc"), i, 9);
		tableModel.setValueAt(map.get("zdjgcc"), i, 10);
		tableModel.setValueAt(map.get("jgxs"), i, 11);
		tableModel.setValueAt(map.get("jklx"), i, 12);
		tableModel.setValueAt(map.get("jsbz"), i, 13);
		tableModel.setValueAt(map.get("rkyjbj"), i, 14);
		tableModel.setValueAt(map.get("cs"), i, 15);
		tableModel.setValueAt(map.get("oid"), i, 16);
		tableModel.setValueAt(map.get("useCount"), i, 17);
		tableModel.setValueAt(map.get("bz"), i, 18);
		tableModel.setValueAt(map.get("EnglishName"), i, 19);
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

	// 监听工装资源树主题
	public void update(Observable o, Object arg) {
		if (!isEnabled())
			return;
		// 判断类别，工装树发出的主题，获取去发出的Vector数据，和搜索结果结构一样，Vector内嵌Map
		if (o != null && o instanceof CommonObservable) {
			CommonObservable co = (CommonObservable) o;
			int type = co.getType();
			if (type == 4)// 表示是
			{
				if (arg != null && arg instanceof Map) {
					Map m = (Map) arg;
					System.out.println("----------------m--------------"+m);
					setOneRowTableValue(m);
					int i = tableModel.getRowCount();
					i--;
					table.setRowSelectionInterval(i, i);
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setSelectedIndex(3);
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().repaint();
				}
			}
		}
	}

}
