package com.glaway.mpm.view;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.dom4j.Element;

import com.glaway.mpm.qmIntf.workspace.WorkSpaceSearchDialog;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.XmlUtility;

public class NewWorkSpaceJPanel extends NewLinkJPanel{
	private JFrame frame;
	private JDialog parentDialog;

	public NewWorkSpaceJPanel(Container parentpanel, JFrame frame) {
		super(parentpanel);
		this.frame = frame;
		initOneself();
	}

	public NewWorkSpaceJPanel(Container parentpanel, JDialog parentDialog) {
		super(parentpanel);
		this.parentDialog = parentDialog;
		initOneself();
	}

	protected void initOneself() {
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (parentPanel != null && parentPanel instanceof TechnicsStepJPanel_XW) {
					JFrame frame = ((TechnicsStepJPanel_XW) parentPanel).getFrame();
					if (frame != null && frame instanceof NewTechnicsPart) {
						NewTechnicsPart tp = (NewTechnicsPart) frame;
						tp.getEpTreePanel().deleteObservers();
						tp.getEpTreePanel().addObserver(NewWorkSpaceJPanel.this);
//						tp.getLeftTab().setSelectedIndex(8);
					}
				}
			}
		});
		addJButton.setVisible(false);
		panel.add(searchJButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		setHiddenColumn(0);
//		setHiddenColumn(7);
		searchJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTableCellEditing();
				WorkSpaceSearchDialog dialog = null;
				if (frame != null) {
					dialog = new WorkSpaceSearchDialog(NewWorkSpaceJPanel.this, frame);
				} else {
					dialog = new WorkSpaceSearchDialog(NewWorkSpaceJPanel.this, parentDialog);
				}
				Vector vec = dialog.showDialog();

				addData(vec);
			}
		});
/*   设置备注按钮
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
					dia = new SetNoteDialog(frame, NewWorkSpaceJPanel.this);
				} else {
					dia = new SetNoteDialog(parentDialog,NewWorkSpaceJPanel.this);
				}
				String note =dia.showDialog();

				table.getModel().setValueAt(note, select, 5);
			}
		});
		*/
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
	String startType = com.glaway.mpm.EditorConfig.startType;
	public void setTabTitle() {
		int i = tableModel.getRowCount();
		if ("SOP".equals(startType)) {
			if (i > 0) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(10, "工位" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(12, "工位" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsStepJPanel_View) {
					((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(10, "工位" + "(" + i + ")");
				}
			} else {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(10, "工位");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(12, "工位");
				} else if (parentPanel instanceof TechnicsStepJPanel_View) {
					((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(10, "工位");
				}
			}
		}else{
			if (i > 0) {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(12, "工位" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(12, "工位" + "(" + i + ")");
				} else if (parentPanel instanceof TechnicsStepJPanel_View) {
					((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(12, "工位" + "(" + i + ")");
				}
			} else {
				if (parentPanel instanceof TechnicsStepJPanel_XW) {
					((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(12, "工位");
				} else if (parentPanel instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(12, "工位");
				} else if (parentPanel instanceof TechnicsStepJPanel_View) {
					((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(12, "工位");
				}
			}
		}

	}

	protected void addModelColumn() {
		//隐藏字段
		tableModel.addColumn("oid");
		//显示字段
		tableModel.addColumn("编号");
		tableModel.addColumn("名称");
		tableModel.addColumn("简述");
		tableModel.addColumn("位置");
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
			Element element = XmlUtility.createWorkSpace();
			XmlUtility.setAttributeValue(element, "oid",(String) tableModel.getValueAt(i, 0));
			XmlUtility.setAttributeValue(element, "number",(String) tableModel.getValueAt(i, 1));
			XmlUtility.setAttributeValue(element, "name",(String) tableModel.getValueAt(i, 2));
			XmlUtility.setAttributeValue(element, "remark",(String) tableModel.getValueAt(i, 3));
			XmlUtility.setAttributeValue(element, "workplace",(String) tableModel.getValueAt(i, 4));
			XmlUtility.setAttributeValue(element, "EnglishName",(String) tableModel.getValueAt(i, 5));

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
		if (element != null && element.getName().equals(XmlUtility.WORKSPACE_TAG)) {
			addProcess();
			int i = tableModel.getRowCount();
			i--;
			tableModel.setValueAt(element.attributeValue("oid"), i, 0);
			tableModel.setValueAt(element.attributeValue("number"), i,1);
			tableModel.setValueAt(element.attributeValue("name"), i, 2);
			tableModel.setValueAt(element.attributeValue("remark"), i, 3);
			tableModel.setValueAt(element.attributeValue("workplace"), i, 4);
			tableModel.setValueAt(element.attributeValue("EnglishName"), i, 5);
		}
	}

	private void setOneRowTableValue(Map map) {
		String equipmentNumber = (String) map.get("Number");
		if (isHasExistData(tableModel, 0, equipmentNumber))
			return;
		addProcess();
		int i = tableModel.getRowCount();
		i--;
		tableModel.setValueAt(map.get("oid"), i, 0);
		tableModel.setValueAt(map.get("Number"), i,1);
		tableModel.setValueAt(map.get("Name"), i, 2);
		tableModel.setValueAt(map.get("Remark"), i, 3);
		tableModel.setValueAt(map.get("WorkPlace"), i, 4);
		tableModel.setValueAt(map.get("EnglishName"), i, 5);
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

}
