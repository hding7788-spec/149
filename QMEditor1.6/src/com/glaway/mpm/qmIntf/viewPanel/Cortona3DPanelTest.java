package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class Cortona3DPanelTest {
	public static void main(String[] args) {
		// CreoCanvas can = new CreoCanvas();
		final Cortona3DPanel panel = new Cortona3DPanel();// ("E:\\YDF001.wrl");
		
//		panel.setView("");
		JPanel panel1 = new JPanel(new java.awt.GridLayout());
		JButton button = new JButton("Action");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// SwingUtilities.invokeLater(shellThread);
				panel.click("rcb85e7fc-b766-4c5c-a9fd-2a2a3783bdfe");

			}
		});
		JButton button1 = new JButton("Action1");
		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// SwingUtilities.invokeLater(shellThread);
				panel.click("rfdff7a04-17ea-4858-a521-1783c6f5f3e3");

			}
		});
		JButton button2 = new JButton("Action2");
		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// SwingUtilities.invokeLater(shellThread);
				panel.click("rd1ed826c-2033-48a2-b28c-d2224fe3de33");

			}
		});
		JButton button3 = new JButton("PLAY");
		button3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// SwingUtilities.invokeLater(shellThread);
				panel.setView("C:\\Users\\Administrator\\Desktop\\自行车\\Touring bike (ready sample).wrl");
//				panel.click("rd1ed826c-2033-48a2-b28c-d2224fe3de33");

			}
		});

		panel1.add(button);
		panel1.add(button1);
		panel1.add(button2);
		panel1.add(button3);
//		panel.setLayout(new BorderLayout());
//		panel.setSize(800,600);
		// panel.setView("E:\\YDF001.wrl");
		JFrame frame = new JFrame();
		frame.add(panel, BorderLayout.CENTER);
		frame.add(panel1, BorderLayout.WEST);
		
		frame.setSize(1200, 800);
//		frame.pack();
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		panel.setView("C:\\Users\\Administrator\\Desktop\\自行车\\Touring bike (ready sample).wrl");
//		panel.play();
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override 
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
