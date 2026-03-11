package com.glaway.mpm.pbom.table;

import java.awt.Component;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class KVJComboBox extends JComboBox {

	public KVJComboBox(Vector<?> items) {
        setModel(new DefaultComboBoxModel(items));
        renderer = new ItemRenderer();
	}

	public void update(Vector<?> items) {
		this.removeAllItems();
		this.setModel(new DefaultComboBoxModel(items));
		renderer = new ItemRenderer();
	}

	public void setRenderer(ListCellRenderer aRenderer) {
		ListCellRenderer oldRenderer = renderer;
		renderer = new ItemRenderer();
		firePropertyChange("renderer", oldRenderer, renderer);
		invalidate();
	}

	class ItemRenderer extends BasicComboBoxRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value != null) {
				KVItem item = (KVItem) value;
				setText(item.getValue());
			}
			if (index == -1) {
				KVItem item = (KVItem) value;
				setText(item.getValue());
			}

			return this;
		}
	}

}
