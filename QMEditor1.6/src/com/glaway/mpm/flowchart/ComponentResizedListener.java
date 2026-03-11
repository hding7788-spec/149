package com.glaway.mpm.flowchart;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ComponentResizedListener extends ComponentAdapter {
	private TechnicsRouteJPanel panel;

	public ComponentResizedListener(TechnicsRouteJPanel panel) {
		this.panel = panel;
	}

	public void componentResized(ComponentEvent e) {
		this.panel.viewAdjusting();
	}
}
