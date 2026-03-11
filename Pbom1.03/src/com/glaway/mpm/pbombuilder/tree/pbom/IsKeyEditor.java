package com.glaway.mpm.pbombuilder.tree.pbom;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;

public class IsKeyEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
		JPanel panel = null;
		JCheckBox checkBox = null;
		private JTable editingTable = null;
		private int editingRow = -1;
		private int editingColumn = -1;

		IsKeyEditor(JCheckBox box) {
			super(box);
			this.checkBox = box;
			this.panel = new JPanel();
			this.panel.setLayout(new GridBagLayout());
			this.panel.add(this.checkBox, new GridBagConstraints(0, 0, 1, 1,
					0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.editingTable = table;
			this.editingRow = row;
			this.editingColumn = column;
			this.checkBox.setSelected(false);
			if ((value != null) && ((value instanceof String))) {
				if (value.toString().equalsIgnoreCase("true")) {
					this.checkBox.setSelected(true);
				}
			}
			return this.panel;
		}

		public Object getCellEditorValue() {
			boolean bool = this.checkBox.isSelected();

			if (this.editingTable != null) {
				if ((this.editingRow >= 0)
						&& (this.editingRow < this.editingTable.getRowCount())
						&& (this.editingColumn >= 0)
						&& (this.editingColumn < this.editingTable
								.getColumnCount())) {
					this.editingTable.setValueAt(String.valueOf(bool),
							this.editingRow, this.editingColumn);
					String bsoID = (String) this.editingTable.getValueAt(
							this.editingRow, 9);
					if (bsoID != null) {
//						Element pace = (Element) PaceTablePane.this.pacesCashe
//								.get(bsoID);
//						if (pace != null) {
//							XMLUtil.setAttributeValue(pace, "isKey",
//									String.valueOf(bool));
//						}
					}
				}
			}
			return String.valueOf(bool);
		}

		public int getClickCountToStart() {
			return 1;
		}
	}