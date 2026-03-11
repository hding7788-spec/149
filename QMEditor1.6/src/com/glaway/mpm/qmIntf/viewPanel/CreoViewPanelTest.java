package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.glaway.mpm.visual.view.pview.VaPViewFactory;

public class CreoViewPanelTest {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		// JFrame frame=new JFrame();
		final CreoViewPanel viewPanel = new CreoViewPanel();

		JPanel leftPanel = new JPanel();
		JButton button = new JButton(".ol file");
		JButton button1 = new JButton(".pvs file");
		JButton button2 = new JButton("image file");
		JButton button3 = new JButton(".pvz file");
		JButton button4 = new JButton(".dwg file");
		leftPanel.add(button);
		leftPanel.add(button1);
		leftPanel.add(button2);
		leftPanel.add(button3);
		leftPanel.add(button4);

		final Vector v = new Vector();
//		v.add("C:\\model\\1111\\GB_T823-1988_M2_5X5.ol");
		v.add("D:\\model\\01-2_crankshaft_asm_2.ol");
//		v.add("C:\\model\\01-2_crankshaft_asm.pvs");
//		v.add("C:\\Users\\Public\\Pictures\\Sample Pictures\\Lighthouse.jpg");
//		v.add("D:\\model\\nvidia-demo.pvz");
//		v.add("C:\\model\\visualization_-_aerial.dwg");
		
		final Vector v2 = new Vector();
//		v2.add("D:\\model\\01-2_crankshaft_asm_2.ol");
//		v.add("C:\\model\\01-2_crankshaft_asm.pvs");
		v2.add("C:\\Users\\Public\\Pictures\\Sample Pictures\\Lighthouse.jpg");
//		v.add("C:\\model\\nvidia-demo.pvz");
//		v.add("C:\\model\\visualization_-_aerial.dwg");
		
		final Vector v3 = new Vector();
//		v2.add("C:\\model\\01-2_crankshaft_asm_2.ol");
//		v.add("C:\\model\\01-2_crankshaft_asm.pvs");
//		v3.add("C:\\Users\\Public\\Pictures\\Sample Pictures\\Lighthouse.jpg");
//		v.add("C:\\model\\nvidia-demo.pvz");
//		v3.add("C:\\model\\visualization_-_aerial.dwg");
		v3.add("D:\\annotest\\20\\-1\\anno.pvs");
		
		final Vector v4 = new Vector();
//		v2.add("C:\\model\\01-2_crankshaft_asm_2.ol");
//		v4.add("D:\\model\\01-2_crankshaft_asm.pvs");
//		v.add("C:\\Users\\Public\\Pictures\\Sample Pictures\\Lighthouse.jpg");
		v4.add("D:\\model\\nvidia-demo.pvz");
//		v4.add("D:\\model\\visualization_-_aerial.dwg");
		
		final Vector v5 = new Vector();
//		v2.add("C:\\model\\01-2_crankshaft_asm_2.ol");
//		v.add("C:\\model\\01-2_crankshaft_asm.pvs");
//		v.add("C:\\Users\\Public\\Pictures\\Sample Pictures\\Lighthouse.jpg");
//		v5.add("D:\\model\\nvidia-demo.pvz");
		v5.add("D:\\model\\visualization_-_aerial.dwg");
//		v5.add("D:\\mpm\\\\technics\\AL7.002.032`AL7.002.032`零件工艺`AL7.002.032`多基地面雷达\\20130720081813757\\visualization_-_aerial.dwg");
//		v5.add("D:\\model\\AL7.002.032`AL7.002.032`零件工艺`AL7.002.032`多基地面雷达\\1234567890\\012345678901254825214551411\\visualization_-_aerial.dwg");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				viewPanel.setView(v);
			}
		});
		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				viewPanel.setView(v2);
			}
		});
		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				viewPanel.setView(v3);
			}
		});
		button3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				viewPanel.setView(v4);
			}
		});
		button4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				viewPanel.setView(v5);
				
			}
		});
		frame.add(leftPanel, BorderLayout.WEST);
		frame.add(viewPanel, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1200, 600);
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
		VaPViewFactory.shutdown(VaPViewFactory.PV_NAME_VP);
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
		frame.setLocation(300, 300);
		frame.setVisible(true);

	}

	CreoViewPanelTest() {

	}
}
