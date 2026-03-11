package com.glaway.mpm.flowchart;

import java.awt.Graphics;
import java.awt.Polygon;
import org.dom4j.Element;

public abstract class TechnicsRouteUnit {
	private int x;
	private int y;
	private int width;
	private int height;
	private boolean selectedState = false;
	protected static final int ADSORPTION = 32;
	private String direction;
	protected static final String WIDTH = "width";
	protected static final String HEIGHT = "height";
	protected static final String NORTH = "north";
	protected static final String SOUTH = "south";
	protected static final String WEST = "west";
	protected static final String EAST = "east";
	protected static final String HORIZONTAL = "horizontal";
	protected static final String VERTICAL = "vertical";

	public abstract void drawSelf(Graphics paramGraphics);

	public abstract void addElementNode(Element paramElement);

	public abstract void setAttribute(Element paramElement);

	public boolean containMouse(int x, int y) {
		if ((x >= getX()) && (x <= getX() + getWidth()) && (y >= getY())
				&& (y <= getY() + getHeight()))
			return true;
		return false;
	}

	public abstract boolean setMouseCursor(
			TechnicsRouteJPanel paramTechnicsRouteJPanel, int paramInt1,
			int paramInt2);

	protected abstract void drawSelfSelectedState(Graphics paramGraphics);

	public abstract boolean flexProperties(
			TechnicsRouteJPanel paramTechnicsRouteJPanel, int paramInt1,
			int paramInt2, int paramInt3, int paramInt4);

	protected Polygon getPointRectangle(int x, int y, int sideWidth) {
		int[] xPoints = { x - sideWidth / 2, x - sideWidth / 2,
				x + sideWidth / 2, x + sideWidth / 2 };
		int[] yPoints = { y - sideWidth / 2, y + sideWidth / 2,
				y + sideWidth / 2, y - sideWidth / 2 };
		return new Polygon(xPoints, yPoints, 4);
	}

	protected Polygon getRectangleWithoutCorner(int x1, int y1, int x2, int y2,
			int sideWidth) {
		int xa = 0;
		int ya = 0;
		int xb = 0;
		int yb = 0;
		int xc = 0;
		int yc = 0;
		int xd = 0;
		int yd = 0;
		if (y1 == y2) {
			if (x1 > x2) {
				xa = x1 - sideWidth / 2;
				xc = x2 + sideWidth / 2;
			} else {
				xa = x1 + sideWidth / 2;
				xc = x2 - sideWidth / 2;
			}
			xb = xa;
			xd = xc;
			ya = y1 - sideWidth / 2;
			yb = y1 + sideWidth / 2;
			yc = yb;
			yd = y1;
		} else if (x1 == x2) {
			if (y1 > y2) {
				ya = y1 - sideWidth / 2;
				yc = y2 + sideWidth / 2;
			} else {
				ya = y1 + sideWidth / 2;
				yc = y2 - sideWidth / 2;
			}
			yb = ya;
			yd = yc;
			xa = x1 + sideWidth / 2;
			xb = x1 - sideWidth / 2;
			xc = xb;
			xd = xa;
		}
		int[] xPoints = { xa, xb, xc, xd };
		int[] yPoints = { ya, yb, yc, yd };
		return new Polygon(xPoints, yPoints, 4);
	}

	protected Polygon getMinRectangle(int x1, int y1, int x2, int y2,
			int sideWidth) {
		int xa = 0;
		int ya = 0;
		int xb = 0;
		int yb = 0;
		int xc = 0;
		int yc = 0;
		int xd = 0;
		int yd = 0;
		if (y1 == y2) {
			if (x1 > x2) {
				xa = x1 - sideWidth / 2;
				xc = x2 + sideWidth / 2;
			} else {
				xa = x1 + sideWidth / 2;
				xc = x2 - sideWidth / 2;
			}
			xb = xa;
			xd = xc;
			ya = y1 - sideWidth / 2;
			yb = y1 + sideWidth / 2;
			yc = y2 + sideWidth / 2;
			yd = y2 - sideWidth / 2;
		} else if (x1 == x2) {
			if (y1 > y2) {
				ya = y1 - sideWidth / 2;
				yc = y2 + sideWidth / 2;
			} else {
				ya = y1 + sideWidth / 2;
				yc = y2 - sideWidth / 2;
			}
			yb = ya;
			yd = yc;
			xa = x1 + sideWidth / 2;
			xb = x1 - sideWidth / 2;
			xc = x2 - sideWidth / 2;
			xd = x2 + sideWidth / 2;
		}
		int[] xPoints = { xa, xb, xc, xd };
		int[] yPoints = { ya, yb, yc, yd };
		return new Polygon(xPoints, yPoints, 4);
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setSelectedState(boolean selectedState) {
		this.selectedState = selectedState;
	}

	public boolean isSelectedState() {
		return this.selectedState;
	}

	public static int getSelectedRectangleSideWidth() {
		return 8;
	}

	public static int getMinimumWidth() {
		return 50;
	}

	public static int getMinimumHeight() {
		return 110;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDirection() {
		return this.direction;
	}
}
