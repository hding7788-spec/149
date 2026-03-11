package com.glaway.mpm.qmIntf.commonString;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;

public class CsNodeTypeAddPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField commonString;
	private JPanel mainPanel;
	private JButton sureButton;
	private JButton cancelButton;
	protected static String returnValue;
	private JDialog dialog;
	private String message;

	public CsNodeTypeAddPanel(JDialog dialog, String message) {
		this.message = message;
		this.dialog = dialog;
		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	private void initComponents() {
		mainPanel = new JPanel();

		label = new JLabel("请输入" + message + "：");
		commonString = new JTextField();

		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		label.setPreferredSize(new Dimension(110, 25));
		commonString.setPreferredSize(new Dimension(140, 25));

		sureButton.setPreferredSize(new Dimension(70, 25));
		cancelButton.setPreferredSize(new Dimension(70, 25));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 5, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.add(label, c);
		c.gridx = 2;
		mainPanel.add(commonString, c);
		c.insets = new Insets(30, 15, 5, 5);
		c.gridx = 1;
		c.gridy = 2;
		mainPanel.add(sureButton, c);
		c.gridx = 2;
		mainPanel.add(cancelButton, c);
		this.add(mainPanel);
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				returnValue = CommonUtil.trim(commonString.getText());
				if (returnValue.equals("")) {
					SwingUtil.showMessageDialog("请输入" + message, "提示", 2);
					returnValue = null;
				} else {
					dialog.dispose();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				returnValue = null;
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("新增");
		cancelButton.setText("取消");
	}

}
