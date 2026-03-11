package com.glaway.mpm.flowchart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.view.NewTechnicsPart;

public class ShowEditAllTechnicsRouteDialog extends JFrame {
	private TechnicsRouteJPanel oldtechnicsRouteJPanel;
	private TechnicsRouteToolBar oldtechnicsRouteToolBar;

	private TechnicsRouteJPanel technicsRouteJPanel;
	private TechnicsRouteToolBar technicsRouteToolBar;

	private NewTechnicsPart frame;
	public ShowEditAllTechnicsRouteDialog(TechnicsRouteJPanel technicsRouteJPanel,NewTechnicsPart frame,TechnicsRouteToolBar technicsRouteToolBar) {
		this.oldtechnicsRouteJPanel = technicsRouteJPanel;
		this.oldtechnicsRouteToolBar = technicsRouteToolBar;
		this.frame = frame;
		initComponents();
		initLayout();
		initUI();
		loadData();
		addListener();
		setToolBarEnabled(false);
	}

	private void initComponents() {
		JPanel routePanel = new JPanel();
		technicsRouteJPanel = new TechnicsRouteJPanel(frame);
		technicsRouteToolBar = new TechnicsRouteToolBar(technicsRouteJPanel);

		routePanel.setLayout(new BorderLayout());
		routePanel.add(technicsRouteToolBar, BorderLayout.NORTH);
		routePanel.add(new JScrollPane(technicsRouteJPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.CENTER);
		add(routePanel);
	}
	private void loadData() {
		Vector drawingUnits = new Vector();
		drawingUnits.addAll(oldtechnicsRouteJPanel.getDrawingUnits());
		Vector procedureUnits = new Vector();
		procedureUnits.addAll(oldtechnicsRouteJPanel.getProcedureUnits());

		technicsRouteJPanel.setDrawingUnits(drawingUnits);
		technicsRouteJPanel.setProcedureUnits(procedureUnits);
		technicsRouteJPanel.viewAdjusting();
		technicsRouteJPanel.setTechnicsNumber(oldtechnicsRouteJPanel.getTechnicsNumber());
		technicsRouteJPanel.setTechnicsName(oldtechnicsRouteJPanel.getTechnicsName());
		technicsRouteJPanel.setTechnicsCategory(oldtechnicsRouteJPanel.getTechnicsCategory());
	}
	private void initLayout() {

	}
	private void addListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					oldtechnicsRouteJPanel.setDrawingUnits(technicsRouteJPanel.getDrawingUnits());
					oldtechnicsRouteJPanel.setProcedureUnits(technicsRouteJPanel.getProcedureUnits());
					oldtechnicsRouteJPanel.viewAdjusting();
					oldtechnicsRouteJPanel.refreshTechnicsRoute();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	public void initUI(){
		this.setTitle("全屏编辑工艺路线图");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width), (int)(height-40));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	public void setToolBarEnabled(boolean flag) {
		technicsRouteToolBar.setEditAllImageToolBarEnabled(flag);
	}




}
