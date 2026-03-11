package com.glaway.mpm.print.ui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import com.glaway.mpm.util.CommonUIUtil;

public class SearchBaselineDialog extends JFrame {

	private static final long serialVersionUID = 4094018302868107771L;
	private JTable table;
	private SearchBaselinePanel searchBaselinePanel;

	public SearchBaselineDialog(JTable table) {
		this.table = table;
		initComponents();
		initUI();
	}

	private void initComponents() {
		searchBaselinePanel = new SearchBaselinePanel(this, table);

		GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(searchBaselinePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(searchBaselinePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
	}

	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("搜索技术状态基线");
        //setResizable(false);
        setSize(1350, 750);
        CommonUIUtil.setMiddleOnScreenWithDialog(this);
        //setModal(true);
        //setAlwaysOnTop(true);
        setVisible(true);
	}

}
