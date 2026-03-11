package com.glaway.mpm.sjzyk;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

public class MyJMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private JMenu jMenu1;
    private JMenu jMenu3;
    private JRadioButtonMenuItem middleTableItem;
    private JRadioButtonMenuItem mutiTableItem;

    private boolean isMiddleTable = true;

	public MyJMenuBar() {
		super();
		init();
	}

	private void init() {
		jMenu1 = new JMenu();
        jMenu3 = new JMenu();
        middleTableItem = new JRadioButtonMenuItem();
        mutiTableItem = new JRadioButtonMenuItem();

        jMenu1.setText("设置");

        jMenu3.setText("设置查询方式");

        middleTableItem.setSelected(true);
        middleTableItem.setText("中间表查询");
        jMenu3.add(middleTableItem);
        middleTableItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selected(e);
			}
		});

        mutiTableItem.setText("多表查询");
        jMenu3.add(mutiTableItem);
        mutiTableItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selected(e);
			}
		});

        jMenu1.add(jMenu3);

        add(jMenu1);
	}

	private void selected(ActionEvent e) {
		if(e.getSource() == middleTableItem) {
			middleTableItem.setSelected(true);
			mutiTableItem.setSelected(false);
			setMiddleTable(true);
		} else {
			mutiTableItem.setSelected(true);
			middleTableItem.setSelected(false);
			setMiddleTable(false);
		}
	}

	public boolean isMiddleTable() {
		return isMiddleTable;
	}

	public void setMiddleTable(boolean isMiddleTable) {
		this.isMiddleTable = isMiddleTable;
	}

}
