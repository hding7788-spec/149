package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

public class GLImagePanel extends JScrollPane {

	private String path;

	public GLImagePanel(String imageFilePath) {
		this.path = imageFilePath;
		JLabel label1 = new JLabel(new ImageIcon(path));
		JPanel panel1 = new JPanel();
		panel1.add(label1);
		
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.revalidate();
		this.setViewportView(panel1);
//		this.setViewport(new JViewport());
		// frame.add(jsp);
		// frame.setLocationRelativeTo(null);
		// frame.setSize(800,600);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setVisible(true);
	}

	@Override
	public void addNotify() {
		
		
		super.addNotify();

	}

	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		GLImagePanel ip = new GLImagePanel(
				"C:\\Users\\Public\\Pictures\\Sample Pictures\\Hydrangeas.jpg");
		System.out.println(JOptionPane.showInputDialog(frame,  "yeah"));
		frame.setSize(800,600);
		frame.setVisible(true);

	}
}
