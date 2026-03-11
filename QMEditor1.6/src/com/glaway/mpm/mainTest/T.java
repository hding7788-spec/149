package com.glaway.mpm.mainTest;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.AdditionalTableJPanel;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

public class T extends JFrame {

	JTabbedPane tab = new JTabbedPane();
	JPanel a = new JPanel();
	JPanel b = new JPanel();

	public T() {
		AdditionalTableJPanel p = new AdditionalTableJPanel(this);
		a.add(p);
		b.add(new JButton("b"));
		tab.add("aaaaaaa", a);
		tab.add("bbbbbbb", b);
		add(tab);
		setLocation(500, 500);
		setSize(500, 500);
		SwingUtil.setMiddle(this);
		setVisible(true);
		setDefaultCloseOperation(3);

		tab.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// tab.setOpaque(true);
				tab.setForegroundAt(1, Color.red);
				// tab.getSelectedComponent().setBackground(Color.RED);
				System.out.println(arg0.getSource().getClass().getName());
			}
		});
	}

	public static void main(String[] args) {
		try {
			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(),
					new ExperienceBlue());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		T t = new T();

	}
}
