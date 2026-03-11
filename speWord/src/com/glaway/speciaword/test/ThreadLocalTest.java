/**
 * 2014-6-27
 */
package com.glaway.speciaword.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author MosesX
 * 2014-6-27
 */
public class ThreadLocalTest {

	/**
	 * 2014-6-27
	 * @param args
	 */
	public static void main(String[] args) {

		ThreadLocal<String> local = new ThreadLocal<String>();

		JFrame F1 = new JFrame();
		F1.setSize(400, 400);
		F1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		F1.setTitle("TabChangeTest");
		
		JButton but = new JButton("CreatFream");
		JPanel p = new JPanel();
		p.add(but);
		but.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame f2 = new JFrame();
				f2.setSize(400, 400);
				f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f2.setTitle("tttt");
				f2.pack();
				f2.setVisible(true);
			}
		});
		F1.getContentPane().add(p, BorderLayout.NORTH);
		F1.pack();
		F1.setVisible(true);
	
	}

}
