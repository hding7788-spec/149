package com.glaway.mpm.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class NumberInputDialog extends JDialog implements ActionListener {
	private JTextField textField;
	private int n;
	private String str;
	public final static int maxNum = 100;
	private int max = -1;
	private boolean isStep = true;

	public NumberInputDialog(JFrame f, boolean bool) {

		super(f, true);
		isStep = bool;
		if (isStep)
			setTitle("新建工序数量");
		else
			setTitle("新建工步数量");
		setResizable(false);
		setLocationRelativeTo(f);

		getContentPane().setLayout(new GridBagLayout());
		textField = new JTextField();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipadx = 136;
		gridBagConstraints.ipady = 11;
		gridBagConstraints.insets = new Insets(6, 6, 0, 4);
		getContentPane().add(textField, gridBagConstraints);

		final JButton button = new JButton();
		button.setText("确定");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.ipadx = 3;
		gridBagConstraints_1.insets = new Insets(2, 85, 5, 0);
		getContentPane().add(button, gridBagConstraints_1);
		getRootPane().setDefaultButton(button);

		textField.setText("1");

		button.addActionListener(this);

	}

	public NumberInputDialog(JFrame f, boolean bool, int max) {
		this(f, bool);
		this.max = max;
	}

	public boolean isNumber(String str) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile("[0-9]*");
		java.util.regex.Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}

	public static String toSemiangle(String src) {
		if (src == null)
			return null;
		if (src.length() == 0)
			return "";
		char[] c = src.toCharArray();
		for (int index = 0; index < c.length; index++) {
			if (c[index] == 12288) {// 全角空格
				c[index] = (char) 32;
			} else if (c[index] > 65280 && c[index] < 65375) {// 其他全角字符
				c[index] = (char) (c[index] - 65248);
			}
		}
		return String.valueOf(c);
	}

	public int showDialog() {
		setSize(160, 100);
		setVisible(true);
		return n;
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		str = textField.getText();
		str = toSemiangle(str);
		boolean b = isNumber(str) && (str.indexOf(".") == -1);

		if (cmd.trim().equals("确定")) {
			if (str.equals("")) {
				JOptionPane.showMessageDialog(this, "       输入的数据不能为空", "提示",
						JOptionPane.WARNING_MESSAGE);
				textField.setText("1");
				textField.requestFocusInWindow();
				textField.select(0, 1);
			} else {
				if (!b) {
					JOptionPane.showMessageDialog(this, "请您输入正整数", "提示",
							JOptionPane.WARNING_MESSAGE);
					textField.requestFocusInWindow();
					textField.select(0, str.length());
				} else {
					int maxNumber = maxNum;
					if (max > 0)
						maxNumber = max;
					if (0 < Integer.parseInt(str)
							&& Integer.parseInt(str) <= maxNumber) {
						n = Integer.parseInt(str);
						this.dispose();
					} else {
						String message = "您输入的数字超出范围";
						if (isStep) {
							message = "您输入的数字超出范围，当前系统最多插入的工序数量为：" + maxNumber;
						} else {
							message = "您输入的数字超出范围，当前系统最多插入的工步数量为：" + maxNumber;
						}
						JOptionPane.showMessageDialog(this, message, "提示",
								JOptionPane.WARNING_MESSAGE);
						textField.requestFocusInWindow();
						textField.select(0, str.length());
					}
				}
			}
		}
	}
}
