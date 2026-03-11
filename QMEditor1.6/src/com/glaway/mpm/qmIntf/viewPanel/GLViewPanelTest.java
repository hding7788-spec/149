package com.glaway.mpm.qmIntf.viewPanel;

import javax.swing.JFrame;

public class GLViewPanelTest {
	public static void main(String[] args) {
		GLViewPanel panel = new GLViewPanel();
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setVisible(true);
		frame.setSize(800,600);
	}
}
