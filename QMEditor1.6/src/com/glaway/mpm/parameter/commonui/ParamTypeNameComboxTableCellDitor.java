package com.glaway.mpm.parameter.commonui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;

import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParameterType;
import com.glaway.mpm.util.CommonUtil;

public class ParamTypeNameComboxTableCellDitor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;

	private ParamTypeNameEditor paramTypeNameEditor;
	private JComboBox comboBox;
	private JTable table;
	private int row;
	private int column;
	private String technicsType;

	public ParamTypeNameComboxTableCellDitor(String technicsType) {
		this.technicsType = technicsType;
		comboBox = new JComboBox();
		comboBox.setEditable(true);

		paramTypeNameEditor = new ParamTypeNameEditor();
		comboBox.setEditor(paramTypeNameEditor);

		for (CmParameterType parameterType : MPMParameterProcessor.getAllChildParameterTypes(technicsType)) {
			comboBox.addItem(parameterType.getName());
		}

		comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				inputEvent(e);
			}
		});
	}

	private Vector<String> reSetVoidList() {
		Vector<String> v = new Vector<String>();
		for (CmParameterType parameterType : MPMParameterProcessor
				.getAllLeafParameterTypes()) {
			v.add(parameterType.getName());
		}
		return v;
	}

	private void inputEvent(KeyEvent e) {
		String s = (String) comboBox.getEditor().getItem();
		comboBox.removeAllItems();
		Vector<String> v = null;
		if ((s == null) || (s.equals(""))) {
			comboBox.setSelectedItem("");
			v = reSetVoidList();
		} else {
			comboBox.setSelectedItem(s);
			v = getShowVector(s);
		}
		if ((v == null) || (v.size() == 0)) {
			comboBox.hidePopup();
			return;
		}
		for (int i = 0; i < v.size(); i++) {
			comboBox.addItem(v.get(i));
		}
		if (v.size() > 0) {
			comboBox.hidePopup();
			comboBox.showPopup();
			comboBox.repaint();
		}
	}

	private Vector<String> getShowVector(String s) {
		Vector<String> returnV = new Vector<String>();
		if (MPMParameterProcessor.getAllLeafParameterTypes() != null) {
			for (CmParameterType parameterType : MPMParameterProcessor
					.getAllLeafParameterTypes()) {
				if (parameterType.getShortcut().contains(s.toLowerCase())) {
					returnV.add(parameterType.getName());
				}
			}
		}
		return returnV;
	}

	class ParamTypeNameEditor implements ComboBoxEditor {
		private JTextField editor = new JTextField();
		private EventListenerList listenerList = new EventListenerList();

		private ParamTypeNameEditor() {
			editor.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					for (CmParameterType parameterType : MPMParameterProcessor.getAllLeafParameterTypes()) {
						String pName = editor.getText().trim();
						if (parameterType.getName().equals(pName)) {
							return;
						}
					}
					editor.setText("");
					comboBox.setSelectedItem("");
					if (table != null && row > -1 && column > -1) {
						table.setValueAt("", row, column);
					}
				}
			});
		}

		public void addActionListener(ActionListener l) {
			this.listenerList.add(ActionListener.class, l);
		}

		public Component getEditorComponent() {
			return this.editor;
		}

		public Object getItem() {
			return this.editor.getText();
		}

		public void removeActionListener(ActionListener l) {
			this.listenerList.remove(ActionListener.class, l);
		}

		public void selectAll() {
		}

		public void setItem(Object anObject) {
			if ((anObject instanceof String)) {
				String s = (String) anObject;
				this.editor.setText(s);
			} else {
				this.editor.setText("");
			}
		}
	}

	@Override
	public Object getCellEditorValue() {
		String value = String.valueOf(comboBox.getSelectedItem());;
		if (table != null && row > -1 && column > -1) {
			table.setValueAt(value, row, column);
		}
		System.out.println("Value=========>>>>>>>>" + value);
		return value;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		this.table = table;
		this.row = row;
		this.column = column;
		this.comboBox.setSelectedItem(CommonUtil.objectToString(value));
		return this.comboBox;
	}

}
