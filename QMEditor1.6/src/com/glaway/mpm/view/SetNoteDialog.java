package com.glaway.mpm.view;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class SetNoteDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog dialog;

	private JDialog parentDialog;
	private JFrame frame;
	private JPanel panel;
	private JLabel noteLabel;
	private JTextField noteField;
	private JButton sure;
	private JButton cancel;
	private JPanel panel2;
	public SetNoteDialog(JFrame frame,JPanel panel) {
		this.panel = panel;
		this.frame = frame;
		newDialog();
	}

	public SetNoteDialog(JDialog parentDialog,JPanel panel ) {

		this.panel = panel;
		this.parentDialog = parentDialog;
		newDialog();
	}

	public void newDialog() {
		if (parentDialog != null) {
			dialog = new CommonDialog(parentDialog);
		} else {
			dialog = new CommonDialog(frame);
		}
		dialog.setTitle("设置备注");
		dialog.setSize(400,300);
		SwingUtil.setMiddle(dialog);

	}

	public String showDialog() {

		Container container = dialog.getContentPane();
		noteLabel = new javax.swing.JLabel("备注：");
		noteField = new JTextField();
		sure = new JButton("确定");
		cancel = new JButton("取消");
		panel2 = new JPanel();
		container.setLayout(new GridBagLayout());

		container.add(
				noteLabel,
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
						new Insets(20, 10, 10, 10), 0, 0));
		container.add(
				noteField,
				new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(10, 0, 10, 10), 0, 0));

		panel2.add(sure, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 10, 10, 10), 0, 0));
		/*panel2.add(cancel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 0, 10, 0), 0, 0));*/
		container.add(panel2);

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		sure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				dialog.dispose();
			}
		});
		dialog.setVisible(true);

		return noteField.getText();
	}


}