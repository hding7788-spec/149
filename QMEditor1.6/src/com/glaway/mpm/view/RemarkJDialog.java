package com.glaway.mpm.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.glaway.mpm.util.SwingUtil;

public class RemarkJDialog extends JDialog implements ActionListener {
	private NewTechnicsPart parent;
	JLabel label = new JLabel("备注");
	private JPanel panel = new JPanel();
	private JTextPane textPanel = new JTextPane();
	private JScrollPane js = new JScrollPane(textPanel,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JButton okButton = new JButton("确定");
	private String remark = null;

	public RemarkJDialog(String title, NewTechnicsPart frame) {
		super(frame, title, true);
		parent = frame;
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(
				label,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
						0, 0));
		getContentPane().add(
				js,
				new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
						new Insets(0, 5, 5, 5), 0, 0));
		getContentPane().add(
				panel,
				new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5),
						0, 0));

		panel.setLayout(new GridBagLayout());
		panel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		okButton.addActionListener(this);
	}

	public String showDialog() {
		setSize(400, 400);
		SwingUtil.setMiddle(this);
		setResizable(false);
		setVisible(true);
		return remark;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton)// 确定
		{
			remark = textPanel.getText();
			if (remark == null)
				remark = "";
			remark.trim();
			dispose();
		}
	}
}