package com.glaway.mpm.print.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;


public class FileListTablePopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 6616577398184448656L;
	/** 设置“已打印” */
    private JMenuItem setPrintedItem;
    /** 设置“未打印” */
    private JMenuItem reSetPrintedItem;

    private JTable table;
    private String type;

    public FileListTablePopupMenu(JTable table, String type) {
		this.table = table;
		this.type = type;
		initComponent();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
	}

	protected void initComponent() {
		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);

	}

	protected void initMenuItemIcon() {

	}

	public void setStatus() {
		setUIEnabled(false);

	}

	public void setUIEnabled(boolean b) {
		setPrintedItem.setEnabled(b);
		reSetPrintedItem.setEnabled(b);
	}
}
