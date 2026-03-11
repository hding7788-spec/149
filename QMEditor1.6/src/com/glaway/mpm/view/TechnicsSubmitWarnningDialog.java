package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.glaway.mpm.qmIntf.common.model.CommonButton;
import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;

public class TechnicsSubmitWarnningDialog extends JPanel {

	private static final long serialVersionUID = 1L;

	private JDialog dialog;
	private JTextArea textArea = null;

	private JPanel buttonPanel = new JPanel();

	private Dimension dimension = new Dimension(80, 30);
	private JButton sureButton = new CommonButton("确定", dimension);

	// private boolean flag = false;

	// private JButton cancelButton = new CommonButton("取消", dimension);

	public TechnicsSubmitWarnningDialog(JFrame frame) {
		dialog = new CommonDialog(frame);
		dialog = new CommonDialog(frame);
		dialog.setTitle("提示");
		dialog.setSize(500, 500);
		dialog.setResizable(true);
		SwingUtil.setMiddle(dialog);
	}

	public void showDialog(String text) {
		initAction();
		textArea = new JTextArea(text);
		textArea.setEditable(false);
		dialog.setLayout(new BorderLayout());
		dialog.add(textArea, BorderLayout.CENTER);
		buttonPanel.add(sureButton);
		// buttonPanel.add(new JLabel("        "));
		// buttonPanel.add(cancelButton);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		dialog.setVisible(true);

	}

	private void initAction() {
		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
	}

	public static void main(String[] args) {
		TechnicsSubmitWarnningDialog dialog = new TechnicsSubmitWarnningDialog(
				null);
		dialog.showDialog("");
	}
}