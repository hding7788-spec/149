package com.glaway.mpm.pbombuilder.tree.dialog.maxizition;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

public class ZHTDialogDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	public static void main(String[] args) {
		JFrame frame = new JFrame("title");
		frame.setContentPane(new ZHTDialogDemo());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(200, 200);
		frame.setLocation(200, 200);
		frame.setVisible(true);
	}

	CommonMaxizitionDialog dialog;

	public ZHTDialogDemo() {
		initGUI();
	}

	private void initGUI() {
		JButton dialogBtn = new JButton("Show Dialog");
		dialogBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dialog == null) {
					initDialog();
				}
				dialog.setVisible(true);
			}
		});
		this.add(dialogBtn);
	}


	private void initDialog() {
//		JPanel panel = new JPanel(new BorderLayout());
		JScrollPane scroll = new JScrollPane(new JTree());
		scroll.setBackground(Color.BLUE);
//		panel.add(scroll);
//		panel.setForeground(Color.RED);
		dialog = new CommonMaxizitionDialog();
		ImageIcon imageIcon = new ImageIcon(getClass().getResource("mbom_edit.png"));
//		dialog.getTitleComponent().setTextInCenter(true);
		dialog.setIconImage(imageIcon.getImage());
		dialog.setTitle("Title Dialog123");
		dialog.setModal(true);
		dialog.setContentPane(scroll);
		dialog.setSize(500, 400);
		dialog.setLocation(400, 200);
	}
}