package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

import org.dom4j.Element;

import com.glaway.mpm.flowchart.ComponentResizedListener;
import com.glaway.mpm.flowchart.TechnicsRouteJPanel;
import com.glaway.mpm.flowchart.TechnicsRouteToolBar;

public class TotalTechnicsRouteJPanel extends JSplitPane {
	private NewTechnicsPart frame;

	private QuickCreateProcedureJPanel quickCreateProcedureJPanel;

	private TechnicsRouteToolBar technicsRouteToolBar;

	private TechnicsRouteJPanel technicsRouteJPanel;

	public TotalTechnicsRouteJPanel(NewTechnicsPart frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载工艺路线");
		this.frame = frame;

		setOrientation(JSplitPane.VERTICAL_SPLIT);
		setMinimumSize(new Dimension(100, 250));
		setContinuousLayout(true);
		setOneTouchExpandable(true);
		setDividerSize(10);
		setDividerLocation(260);

		quickCreateProcedureJPanel = new QuickCreateProcedureJPanel(frame);
		technicsRouteJPanel = new TechnicsRouteJPanel(frame);
		technicsRouteToolBar = new TechnicsRouteToolBar(technicsRouteJPanel);

		JPanel routePanel = new JPanel();
		routePanel.setLayout(new BorderLayout());
		routePanel.add(technicsRouteToolBar, BorderLayout.NORTH);
		routePanel.add(new JScrollPane(technicsRouteJPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.CENTER);

		add(quickCreateProcedureJPanel, JSplitPane.TOP);
		add(routePanel, JSplitPane.BOTTOM);
		frame.addComponentListener(new ComponentResizedListener(technicsRouteJPanel));
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载工艺路线");
	}

	public QuickCreateProcedureJPanel getQuickCreateProcedureJPanel() {
		return quickCreateProcedureJPanel;
	}

	public TechnicsRouteJPanel getTechnicsRouteJPanel() {
		return technicsRouteJPanel;
	}

	public TechnicsRouteToolBar getTechnicsRouteToolBar() {
		return technicsRouteToolBar;
	}

	public void setUIValues(Element technics) {
		if (technics != null) {
			quickCreateProcedureJPanel.clear();
			quickCreateProcedureJPanel.updateUI();
			quickCreateProcedureJPanel.setTableValue(technics);
		}
	}

	public void setUIEnabled(boolean b) {
		quickCreateProcedureJPanel.setUIEnabled(b);
		technicsRouteToolBar.setToolBarEnabled(b);
		technicsRouteJPanel.setEnabled(b);
		technicsRouteJPanel.setEventEnabled(b);
	}
}