package com.glaway.mpm.flowchart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.glaway.mpm.util.IconUtil;

public class TechnicsRoutePopupMenu extends JPopupMenu {
	private TechnicsRouteJPanel panel;
	private JMenuItem addSingleLineUnit = new JMenuItem("添加一段直线");

	private JMenuItem addBidirectionalSingleLineUnit = new JMenuItem("添加一段双向直线");

	private JMenuItem addHorizontalDoubleLineUnit = new JMenuItem("添加二段水平线");

	private JMenuItem addBidirectionalHorizontalDoubleLineUnit = new JMenuItem(
			"添加二段双向水平线");

	private JMenuItem addVerticalDoubleLineUnit = new JMenuItem("添加二段垂直线");

	private JMenuItem addBidirectionalVerticalDoubleLineUnit = new JMenuItem(
			"添加二段双向垂直线");

	private JMenuItem addHorizontalTrebleLineUnit = new JMenuItem("添加三段水平线");

	private JMenuItem addBidirectionalHorizontalTrebleLineUnit = new JMenuItem(
			"添加三段双向水平线");

	private JMenuItem addVerticalTrebleLineUnit = new JMenuItem("添加三段垂直线");

	private JMenuItem addBidirectionalVerticalTrebleLineUnit = new JMenuItem(
			"添加三段双向垂直线");

	private JMenuItem addHorizontalQuintupleLineUnit = new JMenuItem("添加五段水平线");

	private JMenuItem addBidirectionalHorizontalQuintupleLineUnit = new JMenuItem(
			"添加五段双向水平线");

	private JMenuItem deleteUselessLine = new JMenuItem("清除无用线段");

	private JMenuItem createImage = new JMenuItem("生成图片");

	private JMenuItem saveTechnicsRoute = new JMenuItem("保存工艺路线图");

	private JMenuItem refreshTechnicsRoute = new JMenuItem("刷新");

	public TechnicsRoutePopupMenu(TechnicsRouteJPanel panel) {
		this.panel = panel;

		add(this.addSingleLineUnit);
		add(this.addBidirectionalSingleLineUnit);
		addSeparator();
		add(this.addHorizontalDoubleLineUnit);
		add(this.addBidirectionalHorizontalDoubleLineUnit);
		addSeparator();
		add(this.addVerticalDoubleLineUnit);
		add(this.addBidirectionalVerticalDoubleLineUnit);
		addSeparator();
		add(this.addHorizontalTrebleLineUnit);
		add(this.addBidirectionalHorizontalTrebleLineUnit);
		addSeparator();
		add(this.addVerticalTrebleLineUnit);
		add(this.addBidirectionalVerticalTrebleLineUnit);
		addSeparator();
		add(this.addHorizontalQuintupleLineUnit);
		add(this.addBidirectionalHorizontalQuintupleLineUnit);
		addSeparator();
		add(this.deleteUselessLine);
		addSeparator();
		add(this.createImage);
		addSeparator();
		add(this.saveTechnicsRoute);
		addSeparator();
		add(this.refreshTechnicsRoute);

		this.addSingleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/single_line.gif"));
		this.addBidirectionalSingleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/single_d.png"));
		this.addHorizontalDoubleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/horizontal_double_line.gif"));
		this.addBidirectionalHorizontalDoubleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/horizontal_double_d.png"));
		this.addVerticalDoubleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/vertical_double_line.gif"));
		this.addBidirectionalVerticalDoubleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/vertical_double_d.png"));
		this.addHorizontalTrebleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/horizontal_treble_line.gif"));
		this.addBidirectionalHorizontalTrebleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/horizontal_treble_d.png"));
		this.addVerticalTrebleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/vertical_treble_line.gif"));
		this.addBidirectionalVerticalTrebleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/vertical_treble_d.png"));
		this.addHorizontalQuintupleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/horizontal_quintuple.png"));
		this.addBidirectionalHorizontalQuintupleLineUnit.setIcon(IconUtil
				.getImageIcon("/images/horizontal_quintuple_d.png"));
		this.deleteUselessLine.setIcon(IconUtil
				.getImageIcon("/images/clear.gif"));
		this.createImage.setIcon(IconUtil.getImageIcon("/images/jpg.gif"));
		this.saveTechnicsRoute.setIcon(IconUtil
				.getImageIcon("/images/save_template.gif"));
		this.refreshTechnicsRoute.setIcon(IconUtil
				.getImageIcon("/images/refresh.png"));

		this.addSingleLineUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TechnicsRoutePopupMenu.this.panel.addSingleLineUnit(false);
				TechnicsRoutePopupMenu.this.panel.repaint();
			}
		});
		this.addBidirectionalSingleLineUnit
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TechnicsRoutePopupMenu.this.panel
								.addSingleLineUnit(true);
						TechnicsRoutePopupMenu.this.panel.repaint();
					}
				});
		this.addHorizontalDoubleLineUnit
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TechnicsRoutePopupMenu.this.panel
								.addHorizontalDoubleLineUnit(false);
						TechnicsRoutePopupMenu.this.panel.repaint();
					}
				});
		this.addBidirectionalHorizontalDoubleLineUnit
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TechnicsRoutePopupMenu.this.panel
								.addHorizontalDoubleLineUnit(true);
						TechnicsRoutePopupMenu.this.panel.repaint();
					}
				});
		this.addVerticalDoubleLineUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TechnicsRoutePopupMenu.this.panel
						.addVerticalDoubleLineUnit(false);
				TechnicsRoutePopupMenu.this.panel.repaint();
			}
		});
		this.addBidirectionalVerticalDoubleLineUnit
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TechnicsRoutePopupMenu.this.panel
								.addVerticalDoubleLineUnit(true);
						TechnicsRoutePopupMenu.this.panel.repaint();
					}
				});
		this.addHorizontalTrebleLineUnit
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TechnicsRoutePopupMenu.this.panel
								.addHorizontalTrebleLineUnit(false);
						TechnicsRoutePopupMenu.this.panel.repaint();
					}
				});
		this.addBidirectionalHorizontalTrebleLineUnit
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TechnicsRoutePopupMenu.this.panel
								.addHorizontalTrebleLineUnit(true);
						TechnicsRoutePopupMenu.this.panel.repaint();
					}
				});
		this.addVerticalTrebleLineUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TechnicsRoutePopupMenu.this.panel
						.addVerticalTrebleLineUnit(false);
				TechnicsRoutePopupMenu.this.panel.repaint();
			}
		});
		this.addBidirectionalVerticalTrebleLineUnit
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TechnicsRoutePopupMenu.this.panel
								.addVerticalTrebleLineUnit(true);
						TechnicsRoutePopupMenu.this.panel.repaint();
					}
				});
		this.addHorizontalQuintupleLineUnit
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TechnicsRoutePopupMenu.this.panel
								.addVerticalQuintupleLineUnit(false);
						TechnicsRoutePopupMenu.this.panel.repaint();
					}
				});
		this.addBidirectionalHorizontalQuintupleLineUnit
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TechnicsRoutePopupMenu.this.panel
								.addVerticalQuintupleLineUnit(true);
						TechnicsRoutePopupMenu.this.panel.repaint();
					}
				});
		this.deleteUselessLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TechnicsRoutePopupMenu.this.panel.deleteUselessLine();
			}
		});
		this.createImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TechnicsRoutePopupMenu.this.panel.createImage();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}
		});
		this.saveTechnicsRoute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TechnicsRoutePopupMenu.this.panel.saveTechnicsRoute();
			}
		});
		this.refreshTechnicsRoute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TechnicsRoutePopupMenu.this.panel.refreshTechnicsRoute();
				} catch (Exception e1) {
					
					e1.printStackTrace();
					JOptionPane
							.showMessageDialog(null, "刷新工艺路线图出现错误！", "提示", 1);
				}
			}
		});
	}
}
