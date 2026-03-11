package com.glaway.mpm.view;

import java.awt.Graphics;
import java.awt.Polygon;

public class LineUnit {
	// 定义绘制选中的小矩形框的边长
	private static final int SELECTED_RECTANGLE_SIDE_WIDTH = 6;
	private int[] xPoints;
	private int[] yPoints;

	public LineUnit() {
	}

	public LineUnit(int[] xPoints, int[] yPoints) {
		this.xPoints = xPoints;
		this.yPoints = yPoints;
	}

	public void drawSelf(Graphics g) {
		for (int i = 0; i < xPoints.length; i++) {
			// 画箭头
			if (i == xPoints.length - 1) {
				int x1 = xPoints[i - 1];
				int y1 = yPoints[i - 1];
				int x2 = xPoints[i];
				int y2 = yPoints[i];
				g.fillPolygon(getArrowHeader(x1, y1, x2, y2,
						SELECTED_RECTANGLE_SIDE_WIDTH));
				break;
			}
			// 画各条线
			int x1 = xPoints[i];
			int y1 = yPoints[i];
			int x2 = xPoints[i + 1];
			int y2 = yPoints[i + 1];
			g.drawLine(x1, y1, x2, y2);
		}
	}

	protected Polygon getArrowHeader(int x1, int y1, int x2, int y2,
			int sideWidth) {
		double x3 = 0, y3 = 0, x4 = 0, y4 = 0;
		if (y1 == y2) {
			y3 = y2 - sideWidth / 2;
			y4 = y2 + sideWidth / 2;
			// 左
			if (x1 > x2) {
				x3 = x2 + Math.sqrt(3) * sideWidth;
			}
			// 右
			else {
				x3 = x2 - Math.sqrt(3) * sideWidth;
			}
			x4 = x3;
		} else if (x1 == x2) {
			x3 = x2 - sideWidth / 2;
			x4 = x2 + sideWidth / 2;
			// 上
			if (y1 > y2) {
				y3 = y2 + Math.sqrt(3) * sideWidth;
			}
			// 下
			else {
				y3 = y2 - Math.sqrt(3) * sideWidth;
			}
			y4 = y3;
		}
		int[] xPoints = { x2, (int) x3, (int) x4 };
		int[] yPoints = { y2, (int) y3, (int) y4 };
		return new Polygon(xPoints, yPoints, 3);
	}

	public void setxPoints(int[] xPoints) {
		this.xPoints = xPoints;
	}

	public int[] getxPoints() {
		return xPoints;
	}

	public void setyPoints(int[] yPoints) {
		this.yPoints = yPoints;
	}

	public int[] getyPoints() {
		return yPoints;
	}
}