package com.glaway.mpm.pbombuilder.tree.dialog.maxizition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public class CommonMaxizitionTitleBorder implements Border {

	public Insets getBorderInsets(Component c) {
		return new Insets(2, 2, 4, 3);
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.setColor(Color.GRAY);
		g.fill3DRect(-1, 0, width, height, true);
	}

}