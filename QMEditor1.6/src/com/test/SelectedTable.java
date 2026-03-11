package com.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.glaway.mpm.util.SwingUtil;

public class SelectedTable extends JFrame {
	private JTable table = new JTable();

	private DefaultTableModel getModel(Object[][] tableValue) {
		DefaultTableModel model = new DefaultTableModel(tableValue,
				new String[] { "工装oid", "工装编号", "工装名称", "工装标准号", "工装规格" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return true;
			}
		};
		return model;
	}

	private DefaultTableModel generatorModel() {
		Object[][] tableValue = new Object[][] { { "1", "2", "3", "4", "5" },
				{ "a", "b", "c", "d", "e" } };
		return getModel(tableValue);
	}

	public SelectedTable() {
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("t1");
		comboBox.addItem("t2");
		comboBox.addItem("t3");
		comboBox.addItem("t4");
		comboBox.addItem("t5");

		table.setModel(generatorModel());
		this.table.getColumnModel().getColumn(3)
				.setCellEditor(new WorkShopEditor(comboBox));

		// this.add(table);
		setVisible(true);
		setSize(500, 600);
		setLayout(new BorderLayout());
		final JTextArea area = new JTextArea("1\n232\n131\n2", 2, 3);
		area.setLineWrap(true);
		add(area, BorderLayout.CENTER);
		JButton button = new JButton("aaaa");
		add(button, BorderLayout.WEST);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Document document = area.getDocument();
				try {
					System.out.println(document.getText(0, document.getLength()));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
//				System.out.println(area.getText());
			}
		});
		SwingUtil.setMiddle(this);
	}

	public static void main(String[] args) {
		new SelectedTable();
	}
}

class WorkShopEditor extends DefaultCellEditor {
	JComboBox combo = null;

	WorkShopEditor(JComboBox box) {
		super(box);
		this.combo = box;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		return this.combo;
	}

	public Object getCellEditorValue() {
		Object result = this.combo.getSelectedItem();
		return result;
	}
}
