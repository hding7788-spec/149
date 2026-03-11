package com.glaway.mpm.parameter.commonui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.dom4j.Element;

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.PaceTablePane;
import com.glaway.mpm.view.SpecialWordPanel;
import com.glaway.speciaword.common.CommonHelper;
import com.glaway.speciaword.component.EditorPane;

public class SpecialSymbolEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	private int editingColumn = -1;
	private int editingRow = -1;
	private JTable editingTable = null;
	private String imageFolder = "";//取本地工艺文件xml路径
	private EditorPane panel = new EditorPane(imageFolder);

	public SpecialSymbolEditor(String imageFloder) {
		super(new JTextField());
		this.imageFolder = imageFloder;
		panel.setName(String.valueOf(System.currentTimeMillis()));
		panel.getSpeciaWordComponent().getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent documentevent) {
				reSize();
			}

			@Override
			public void insertUpdate(DocumentEvent documentevent) {
				reSize();
			}

			@Override
			public void removeUpdate(DocumentEvent documentevent) {
				reSize();
			}
		});

		panel.getSpeciaWordComponent().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				reSize();
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
	}

	@Override
	public Object getCellEditorValue() {
		String content = panel.getText();
		this.editingTable.setValueAt(content, this.editingRow, this.editingColumn);
		String oid = this.editingTable.getValueAt(this.editingRow, 0).toString();
		if (oid != null) {
			//XWTreeNode treeNode = TechnicsTreeHelper.getXWTreeNodeByOid(oid);
//			XWTreeNode treeNode = null;
//			if (treeNode != null) {
//				CmTreeNode cmTreeNode = treeNode.getTreeObject().getTreeNode();
//				if (content != null) {
//					content = content.replaceAll(CommonUtil.SPECIAL_SPACE, "");
//				}
//				String text = CommonHelper.replaceSaveSeperator(content, imageFolder);
//
//
//				if (editingRow != -1 && editingColumn != -1) {
//					int speHeight = (int)panel.getSpeciaWordComponent().getSize().getHeight();
//					if (speHeight > 0) {
//						editingTable.setRowHeight(editingRow, speHeight);
//					}
//				}
//
//			}
		}

		return panel.getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.editingTable = table;
		this.editingRow = row;
		this.editingColumn = column;

		String text = CommonHelper.replaceReadSeperator(CommonUtil.objectToString(value), imageFolder);
		//panel.setTechnicsPath(TechnicsTreeHelper.getTechnicsPath());
		panel.setTechnicsPath(imageFolder);
		panel.setText(text);
		panel.setBorder(null);
		if (row != -1 && column != -1) {
			int speHeight = (int)panel.getSpeciaWordComponent().getPreferredSize().getHeight();
			if (speHeight > 0) {
				editingTable.setRowHeight(row, speHeight);
			}
		}
		return this.panel;
	}

	public void insertText(String str) {
		if (str != null && panel != null) {
			panel.insertText(str);
		}
	}

	public void insertText(String str, int index) {
		if (str != null && panel != null) {
			panel.insertText(str, index);
		}
	}

	public void reSize() {
		if (editingRow != -1 && editingColumn != -1) {
			int size = (int) panel.getSpeciaWordComponent().getSize().getHeight();
			int rowHeight = editingTable.getRowHeight(editingRow);
 			if (size > 0 && size > rowHeight) {
				editingTable.setRowHeight(editingRow, size);
			}
		}
	}

}
