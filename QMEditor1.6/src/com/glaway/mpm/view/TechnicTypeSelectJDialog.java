package com.glaway.mpm.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class TechnicTypeSelectJDialog extends JDialog {
	private JFrame frame;
	private JList list = new JList();
	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");

	private String technicsType;

	public TechnicTypeSelectJDialog(JFrame parent) {
		super(parent, true);
		frame = parent;
		setModal(true);

		Container container = getContentPane();
		container.setLayout(new GridBagLayout());

		container.add(new JLabel("选择创建的工艺类型"), new GridBagConstraints(0, 0, 1,
				1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 0), 0, 0));

		Vector vector = new Vector();
		vector.add("装配工艺");
		vector.add("零件工艺");
		list.setListData(vector);
		list.setSelectionMode(0);

		container.add(list, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 0, 5, 0), 0, 0));

		JPanel panel = new JPanel();
		container.add(panel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		panel.setLayout(new GridBagLayout());
		panel.add(new JLabel(""), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		okButton.setPreferredSize(new Dimension(80, 23));
		okButton.setMinimumSize(new Dimension(80, 23));
		okButton.setMaximumSize(new Dimension(80, 23));
		panel.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 0, 5, 5), 0, 0));
		cancelButton.setPreferredSize(new Dimension(80, 23));
		cancelButton.setMinimumSize(new Dimension(80, 23));
		cancelButton.setMaximumSize(new Dimension(80, 23));
		panel.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 0, 5, 5), 0, 0));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				technicsType = (String) list.getSelectedValue();
				if (technicsType == null)
					return;
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension.getWidth() - 200) / 2,
				(int) (dimension.getHeight() - 200) / 2, 200, 200);
		setResizable(false);
		setVisible(true);
	}

	public String getTechnicsType() {
		return technicsType;
	}
}