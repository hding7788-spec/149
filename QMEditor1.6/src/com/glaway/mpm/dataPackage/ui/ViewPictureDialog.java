package com.glaway.mpm.dataPackage.ui;

import java.awt.Container;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.glaway.mpm.util.SwingUtil;

public class ViewPictureDialog{

	private JLabel imageLabel;
	private JPanel panel;
	private ImageIcon icon;
	private JDialog dialog;

	private String filePath;

	public ViewPictureDialog(JDialog parent, String filePath){

		this.filePath = filePath;
		dialog = new JDialog(parent);
		dialog.setTitle("图片预览");
		dialog.setSize(1000, 800);
		SwingUtil.setMiddle(dialog);
	}
	public void showImage(){
		Container container = dialog.getContentPane();
		panel = new JPanel();
		imageLabel = new JLabel();
		icon = new ImageIcon(filePath);
		icon.setImage(icon.getImage().getScaledInstance(dialog.getWidth(), dialog.getHeight(), Image.SCALE_DEFAULT));

		imageLabel.setIcon(icon);
		panel.add(imageLabel);
		container.add(panel);
		dialog.setVisible(true);
	}
}
