package com.glaway.mpm.sjzyk;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

public class SjzykJComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;
	private List<String> values;
	private SjzykEditor editor = new SjzykEditor();

	public SjzykJComboBox(List<String> values) {
		this.values = values;
		setEditable(true);
		setEditor(editor);

		getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				inputEvent(e);
			}
		});
	}

	private void inputEvent(KeyEvent e) {
		String s = (String) getEditor().getItem();
		removeAllItems();
		Vector<String> v = null;
		if ((s == null) || (s.equals(""))) {
			setSelectedItem("");
		} else {
			setSelectedItem(s);
			v = getShowVector(s);
		}
		if ((v == null) || (v.size() == 0)) {
			hidePopup();
			return;
		}
		for (int i = 0; i < v.size(); i++) {
			addItem(v.get(i));
		}
		if (v.size() > 0) {
			hidePopup();
			showPopup();
			repaint();
		}
	}

	private Vector<String> getShowVector(String prefix) {
		Vector<String> returnV = new Vector<String>();
		if(prefix != null && !"".equals(prefix)) {
			for (String value : values) {
				if(value == null || "".equals(value)) {
					continue;
				}
				if(value.startsWith(prefix) && !returnV.contains(value)) {
					returnV.add(value);
				}
			}
		}
		return returnV;
	}

	public class SjzykEditor implements ComboBoxEditor {
		private JTextField editor = new JTextField();
		private EventListenerList listenerList = new EventListenerList();

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
			System.out.println(this.editor.getText());
		}
	}
}
