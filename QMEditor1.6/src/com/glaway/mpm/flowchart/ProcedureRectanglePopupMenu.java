package com.glaway.mpm.flowchart;

import com.glaway.mpm.util.IconUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ProcedureRectanglePopupMenu extends JPopupMenu {
	private TechnicsRouteJPanel panel;
	private JMenuItem toUp = new JMenuItem("向上对齐");

	private JMenuItem toBottom = new JMenuItem("向下对齐");

	private JMenuItem toLeft = new JMenuItem("向左对齐");

	private JMenuItem toRight = new JMenuItem("向右对齐");

	private JMenuItem toHorizontalCenter = new JMenuItem("水平居中对齐");

	private JMenuItem toVerticalCenter = new JMenuItem("垂直居中对齐");

	public ProcedureRectanglePopupMenu(TechnicsRouteJPanel panel) {
		this.panel = panel;

		add(this.toUp);
		addSeparator();
		add(this.toBottom);
		addSeparator();
		add(this.toLeft);
		addSeparator();
		add(this.toRight);
		addSeparator();
		add(this.toHorizontalCenter);
		addSeparator();
		add(this.toVerticalCenter);

		this.toUp.setIcon(IconUtil.getImageIcon("/images/align_v_top.gif"));
		this.toBottom.setIcon(IconUtil
				.getImageIcon("/images/align_v_bottom.gif"));
		this.toLeft.setIcon(IconUtil.getImageIcon("/images/align_h_left.gif"));
		this.toRight
				.setIcon(IconUtil.getImageIcon("/images/align_h_right.gif"));
		this.toHorizontalCenter.setIcon(IconUtil
				.getImageIcon("/images/align_h_centers.gif"));
		this.toVerticalCenter.setIcon(IconUtil
				.getImageIcon("/images/align_v_centers.gif"));

		this.toUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProcedureRectanglePopupMenu.this.panel.toUp();
			}
		});
		this.toBottom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProcedureRectanglePopupMenu.this.panel.toBottom();
			}
		});
		this.toLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProcedureRectanglePopupMenu.this.panel.toLeft();
			}
		});
		this.toRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProcedureRectanglePopupMenu.this.panel.toRight();
			}
		});
		this.toHorizontalCenter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProcedureRectanglePopupMenu.this.panel.toHorizontalCenter();
			}
		});
		this.toVerticalCenter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProcedureRectanglePopupMenu.this.panel.toVerticalCenter();
			}
		});
	}
}
