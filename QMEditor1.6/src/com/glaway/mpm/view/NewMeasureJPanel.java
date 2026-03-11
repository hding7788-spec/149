package com.glaway.mpm.view;

import com.glaway.mpm.qmIntf.measure.MeasureSearchDialog;
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

public class NewMeasureJPanel extends NewLinkJPanel {
	private JFrame frame;
	private JDialog parentDialog;

	public NewMeasureJPanel(Container parentpanel, JFrame frame) {
		super(parentpanel);
		this.frame = frame;
		initOneself();
		// setTabTitle();
	}

	public NewMeasureJPanel(Container parentpanel, JDialog parentDialog) {
		super(parentpanel);
		this.parentDialog = parentDialog;
		initOneself();
		// setTabTitle();
	}

	protected void initOneself() {
		ResourceTableCellEditor resourceTableCellEditor = new ResourceTableCellEditor(new JTextField());
		table.getColumnModel().getColumn(5).setCellEditor(resourceTableCellEditor);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (parentPanel != null && parentPanel instanceof TechnicsStepJPanel_XW) {
					JFrame frame = ((TechnicsStepJPanel_XW) parentPanel).getFrame();
					if (frame != null && frame instanceof NewTechnicsPart) {
						NewTechnicsPart tp = (NewTechnicsPart) frame;
						tp.getEpTreePanel().deleteObservers();
						tp.getEpTreePanel().addObserver(NewMeasureJPanel.this);// 量具
						tp.getLeftTab().setSelectedIndex(8);
					}
				}
			}
		});

		addJButton.setVisible(false);
		panel.add(searchJButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		setHiddenColumn(0);
		setHiddenColumn(7);
		searchJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTableCellEditing();
				MeasureSearchDialog dialog = null;
				if (frame != null) {
					dialog = new MeasureSearchDialog(NewMeasureJPanel.this, frame);
				} else {
					dialog = new MeasureSearchDialog(NewMeasureJPanel.this, parentDialog);
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
					dia = new SetNoteDialog(frame, NewMeasureJPanel.this);
				} else {
					dia = new SetNoteDialog(parentDialog,NewMeasureJPanel.this);
				}
				String note =dia.showDialog();

				table.getModel().setValueAt(note, select, 5);
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
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(6, "量具" + "(" + i + ")");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(8, "量具" + "(" + i + ")");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(6, "量具" + "(" + i + ")");
			}
		} else {
			if (parentPanel instanceof TechnicsStepJPanel_XW) {
				((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(6, "量具");
			} else if (parentPanel instanceof TechnicsPaceJDialog) {
				if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(8, "量具");
				}
			} else if (parentPanel instanceof TechnicsStepJPanel_View) {
				((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(6, "量具");
			}
		}
	}

	protected void addModelColumn() {
		//隐藏字段
		tableModel.addColumn("oid");
		//显示字段
		tableModel.addColumn("编号");
		tableModel.addColumn("名称");
		tableModel.addColumn("型号");
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
			Element element = XmlUtility.createMeasure();
			XmlUtility.setAttributeValue(element, "oid",(String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "number",(String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "name",(String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "pindex",(String) tableModel.getValueAt(i, 3));
			XmlUtility.setAttributeValue(element, "csize",(String) tableModel.getValueAt(i, 4));
			XmlUtility.setAttributeValue(element, "useCount",(String) tableModel.getValueAt(i, 5));
			XmlUtility.setAttributeValue(element, "bz",(String) tableModel.getValueAt(i, 6));
			XmlUtility.setAttributeValue(element, "EnglishName",(String) tableModel.getValueAt(i, 7));

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
		if (element != null && element.getName().equals(XmlUtility.MEASURE_TAG)) {
			addProcess();
			int i = tableModel.getRowCount();
			i--;
			tableModel.setValueAt(element.attributeValue("oid"), i, 0);
			tableModel.setValueAt(element.attributeValue("number"), i,1);
			tableModel.setValueAt(element.attributeValue("name"), i, 2);
			tableModel.setValueAt(element.attributeValue("pindex"), i, 3);
			tableModel.setValueAt(element.attributeValue("csize"), i, 4);
			tableModel.setValueAt(element.attributeValue("useCount"), i, 5);
			tableModel.setValueAt(element.attributeValue("bz"), i, 6);
			tableModel.setValueAt(element.attributeValue("EnglishName"), i, 7);
		}
	}

	private void setOneRowTableValue(Map map) {
		String equipmentNumber = (String) map.get("toolNum");
		if (isHasExistData(tableModel, 0, equipmentNumber))
			return;
		addProcess();
		int i = tableModel.getRowCount();
		i--;
		tableModel.setValueAt(map.get("oid"), i, 0);
		tableModel.setValueAt(map.get("toolNum"), i,1);
		tableModel.setValueAt(map.get("toolName"), i, 2);
		tableModel.setValueAt(map.get("mindex"), i, 3);
		tableModel.setValueAt(map.get("csize"), i, 4);
		tableModel.setValueAt(map.get("useCount"), i, 5);
		tableModel.setValueAt(map.get("bz"), i, 6);
		tableModel.setValueAt(map.get("EnglishName"), i, 7);
	}

	public void update(Observable o, Object arg) {
		if (!isEnabled())
			return;
		if (o != null && o instanceof CommonObservable) {
			CommonObservable co = (CommonObservable) o;
			int type = co.getType();
			if (type == 7)
			{
				if (arg != null && arg instanceof Map) {
					Map m = (Map) arg;
					setOneRowTableValue(m);
					int i = tableModel.getRowCount();
					i--;
					table.setRowSelectionInterval(i, i);
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setSelectedIndex(6);
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