package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.tree.dialog.erp.AbstractERPDialog;
import javax.swing.JLabel;

public class EditPbomDialog extends AbstractERPDialog {

	private Window owner;
	private JTable table;
	private int row;
	private JLabel gsbmLabel ;
	private JTextField gsbmText;
	public EditPbomDialog(final Window owner, final JTable table, int row,
			String title) {
		this.setTitle(title);
		this.owner = owner;
		this.table = table;
		this.row = row;

		loadInitDatas();
		initDimension();
		initComponents();
		initActions();
		initLayout();
		this.setResizable(true);
		this.setModal(true);

		this.setVisible(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
//				owner.dispose();
			}
		});
	}

	@Override
	protected void initActions() {

	}

	@Override
	protected void initComponents() {

	}

	@Override
	protected void initLayout() {
		JPanel jp = new JPanel();
		this.setContentPane(jp);
	}

	@Override
	protected void loadInitDatas() {

	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}

}