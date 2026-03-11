package com.glaway.mpm.flowchart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.util.Vector;
import org.dom4j.Element;

public class SingleLineUnit extends TechnicsRouteUnit {
	protected int x1;
	protected int x2;
	protected int y1;
	protected int y2;
	protected int x3;
	protected int y3;
	protected int x4;
	protected int y4;
	protected int x5;
	protected int y5;
	protected int x6;
	protected int y6;
	private static final double ANGLE = 30.0D;
	private static final double PI = 3.1416D;
	protected int selectedIndex = -1;

	protected int link = 0;
	protected ProcedureRectangleUnit preProcedure;
	protected ProcedureRectangleUnit nextProcedure;
	protected String head = "";

	protected String tail = "";

	protected String note = "";

	protected boolean isBidirectional = false;

	protected boolean canDeleteRectangleLink = true;

	public SingleLineUnit() {
	}

	public SingleLineUnit(int[] xPoints, int[] yPoints) {
		this.x1 = xPoints[0];
		this.y1 = yPoints[0];
		this.x2 = xPoints[1];
		this.y2 = yPoints[1];
	}

	public SingleLineUnit(int[] xPoints, int[] yPoints,
			Vector<TechnicsRouteUnit> drawingUnits) {
		this(xPoints, yPoints);
		drawingUnits.add(this);
	}

	public SingleLineUnit(int[] xPoints, int[] yPoints,
			Vector<TechnicsRouteUnit> drawingUnits, boolean isBidirectional) {
		this(xPoints, yPoints, drawingUnits);
		this.isBidirectional = isBidirectional;
	}

	public void drawSelf(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		BasicStroke bs = new BasicStroke(2.0F);
		Stroke old = g2d.getStroke();
		g2d.setStroke(bs);
		if (this.link == 1)
			g.setColor(Color.blue);
		if (this.link == 2)
			g.setColor(Color.red);
		if (!getIsDeclining(this.x1, this.y1, this.x2, this.y2))
			g.fillPolygon(getArrowHeader(this.x1, this.y1, this.x2, this.y2,
					getSelectedRectangleSideWidth()));
		else
			g.fillPolygon(getDecliningArrowHeader(this.x1, this.y1, this.x2,
					this.y2, getSelectedRectangleSideWidth()));
		Line2D.Double line = new Line2D.Double(this.x1, this.y1, this.x2,
				this.y2);
		g2d.draw(line);

		g.drawLine(this.x1, this.y1, this.x2, this.y2);

		if (this.isBidirectional) {
			if (!getIsDeclining(this.x1, this.y1, this.x2, this.y2))
				g.fillPolygon(getArrowHeader(this.x2, this.y2, this.x1,
						this.y1, getSelectedRectangleSideWidth()));
			else {
				g.fillPolygon(getDecliningArrowHeader(this.x2, this.y2,
						this.x1, this.y1, getSelectedRectangleSideWidth()));
			}
		}
		if (isSelectedState()) {
			drawSelfSelectedState(g);
		}

		drawNote(g);
		g2d.setStroke(old);
	}

	protected void drawNote(Graphics g) {
		g.setColor(Color.darkGray);
		int height = g.getFontMetrics().getHeight();
		int width = g.getFontMetrics().stringWidth(this.note);

		g.drawString(this.note, (this.x1 + this.x2 - width) / 2, (this.y1
				+ this.y2 - height) / 2);
		g.setColor(Color.black);
	}

	private double getSlope(int x1, int y1, int x2, int y2) {
		if (y2 > y1) {
			if (x2 > x1) {
				return Math.atan((y2 - y1) / (x2 - x1));
			}

			if (x2 < x1) {
				return Math.atan((y2 - y1) / (x1 - x2));
			}
		} else if (y2 < y1) {
			if (x2 > x1) {
				return Math.atan((y1 - y2) / (x2 - x1));
			}

			if (x2 < x1) {
				return Math.atan((y1 - y2) / (x1 - x2));
			}
		}
		return 0.0D;
	}

	protected Polygon getArrowHeader(int x1, int y1, int x2, int y2,
			int sideWidth) {
		double x3 = 0.0D;
		double y3 = 0.0D;
		double x4 = 0.0D;
		double y4 = 0.0D;
		if (y1 == y2) {
			y3 = y2 - sideWidth / 2;
			y4 = y2 + sideWidth / 2;

			if (x1 > x2) {
				x3 = x2 + Math.sqrt(3.0D) * sideWidth;
			} else {
				x3 = x2 - Math.sqrt(3.0D) * sideWidth;
			}
			x4 = x3;
		} else if (x1 == x2) {
			x3 = x2 - sideWidth / 2;
			x4 = x2 + sideWidth / 2;

			if (y1 > y2) {
				y3 = y2 + Math.sqrt(3.0D) * sideWidth;
			} else {
				y3 = y2 - Math.sqrt(3.0D) * sideWidth;
			}
			y4 = y3;
		}
		int[] xPoints = { x2, (int) x3, (int) x4 };
		int[] yPoints = { y2, (int) y3, (int) y4 };
		return new Polygon(xPoints, yPoints, 3);
	}

	protected Polygon getDecliningArrowHeader(int x1, int y1, int x2, int y2,
			int sideWidth) {
		double k = 0.0D;
		double p = 0.0D;
		double ai = 0.0D;
		int tempX1 = 0;
		int tempY1 = 0;
		int tempX2 = 0;
		int tempY2 = 0;
		if (x2 != x1)
			k = (y2 - y1) / (x1 - x2);
		p = 0.5236000000000001D;
		if ((x1 == x2) && (y1 > y2)) {
			ai = 1.5708D;
		} else if ((x1 == x2) && (y1 < y2)) {
			ai = -1.5708D;
		} else {
			ai = Math.atan(k);
		}
		if (x1 >= x2) {
			tempX1 = (int) (x2 + Math.cos(ai + p) * sideWidth * 3.0D / 2.0D);
			tempY1 = (int) (y2 - Math.sin(ai + p) * sideWidth * 3.0D / 2.0D);
			tempX2 = (int) (x2 + Math.cos(ai - p) * sideWidth * 3.0D / 2.0D);
			tempY2 = (int) (y2 - Math.sin(ai - p) * sideWidth * 3.0D / 2.0D);
		}
		if (x1 <= x2) {
			tempX1 = (int) (x2 - Math.cos(ai + p) * sideWidth * 3.0D / 2.0D);
			tempY1 = (int) (y2 + Math.sin(ai + p) * sideWidth * 3.0D / 2.0D);
			tempX2 = (int) (x2 - Math.cos(ai - p) * sideWidth * 3.0D / 2.0D);
			tempY2 = (int) (y2 + Math.sin(ai - p) * sideWidth * 3.0D / 2.0D);
		}

		Polygon polygon = new Polygon();
		polygon.addPoint(tempX1, tempY1);
		polygon.addPoint(x2, y2);
		polygon.addPoint(tempX2, tempY2);
		return polygon;
	}

	protected void drawSelfSelectedState(Graphics g) {
		g.fillPolygon(getPointRectangle(this.x1, this.y1,
				getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle(this.x2, this.y2,
				getSelectedRectangleSideWidth()));
	}

	public boolean containMouse(int x, int y) {
		if (getPointRectangle(this.x1, this.y1, 32).contains(x, y)) {
			return true;
		}

		if (getPointRectangle(this.x2, this.y2, 32).contains(x, y)) {
			return true;
		}

		if (!getIsDeclining(this.x1, this.y1, this.x2, this.y2)) {
			if (getRectangleWithoutCorner(this.x1, this.y1, this.x2, this.y2,
					32).contains(x, y)) {
				return true;
			}

		} else if (getDecliningRectangleWithoutCorner(this.x1, this.y1,
				this.x2, this.y2, 32).contains(x, y)) {
			return true;
		}

		return false;
	}

	public boolean setMouseCursor(TechnicsRouteJPanel panel, int x, int y) {
		if (getPointRectangle(this.x1, this.y1, 32).contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(13));
			return true;
		}

		if (getPointRectangle(this.x2, this.y2, 32).contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(13));
			return true;
		}

		if (!getIsDeclining(this.x1, this.y1, this.x2, this.y2)) {
			if (getRectangleWithoutCorner(this.x1, this.y1, this.x2, this.y2,
					32).contains(x, y)) {
				panel.setCursor(Cursor.getPredefinedCursor(12));
				return false;
			}

		} else if (getDecliningRectangleWithoutCorner(this.x1, this.y1,
				this.x2, this.y2, 32).contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(12));
			return false;
		}

		panel.setCursor(Cursor.getDefaultCursor());
		return false;
	}

	public boolean flexProperties(TechnicsRouteJPanel panel, int x1, int y1,
			int x2, int y2) {
		int margin = 20;
		if (getPointRectangle(this.x1, this.y1, 32).contains(x1, y1)) {
			if ((x1 <= margin) || (y1 <= margin))
				return false;
			this.x1 = x2;
			this.y1 = y2;
			getPreProcedure(panel, x2, y2);
		} else if (getPointRectangle(this.x2, this.y2, 32).contains(x1, y1)) {
			if ((x2 <= margin) || (y2 <= margin))
				return false;
			this.x2 = x2;
			this.y2 = y2;
			getNextProcedure(panel, x2, y2);
		}
		judgeLinkCondition();
		return true;
	}

	protected boolean getPreProcedure(TechnicsRouteJPanel panel, int moveX,
			int moveY) {
		Vector vector = panel.getProcedureUnits();
		for (int i = 0; i < vector.size(); i++) {
			boolean b = isLinkPreProcedure(
					(ProcedureRectangleUnit) vector.get(i), moveX, moveY);
			if (b)
				return b;
		}
		if (this.preProcedure != null) {
			if (this.nextProcedure != null) {
				if (this.canDeleteRectangleLink) {
					this.preProcedure.getNextProcedureVector().remove(
							this.nextProcedure);
					this.nextProcedure.getPreProcedureVector().remove(
							this.preProcedure);
				}
			}
			this.preProcedure.getNextLineVector().remove(this);
			this.preProcedure = null;
		}
		return false;
	}

	protected boolean isLinkPreProcedure(ProcedureRectangleUnit unit,
			int moveX, int moveY) {
		int pX = unit.getX() + unit.getWidth() / 2;
		int pY = unit.getY();
		int sideWidth = 32;
		if (getPointRectangle(pX, pY, sideWidth).contains(moveX, moveY)) {
			adjustPreProcedure(unit, pX, pY, "north");
			return true;
		}
		pX = unit.getX();
		pY = unit.getY() + unit.getHeight() / 2;
		if (getPointRectangle(pX, pY, sideWidth).contains(moveX, moveY)) {
			adjustPreProcedure(unit, pX, pY, "west");
			return true;
		}
		pX = unit.getX() + unit.getWidth();
		pY = unit.getY() + unit.getHeight() / 2;
		if (getPointRectangle(pX, pY, sideWidth).contains(moveX, moveY)) {
			adjustPreProcedure(unit, pX, pY, "east");
			return true;
		}
		pX = unit.getX() + unit.getWidth() / 2;
		pY = unit.getY() + unit.getHeight();
		if (getPointRectangle(pX, pY, sideWidth).contains(moveX, moveY)) {
			adjustPreProcedure(unit, pX, pY, "south");
			return true;
		}
		return false;
	}

	protected void adjustPreProcedure(ProcedureRectangleUnit unit, int pX,
			int pY, String s) {
		if ((this.nextProcedure != null) && (this.nextProcedure != unit)) {
			this.x1 = pX;
			this.y1 = pY;
			this.preProcedure = unit;
			this.link = 2;
			this.tail = s;

			if (!this.preProcedure.getNextLineVector().contains(this)) {
				this.preProcedure.getNextLineVector().add(this);
			}

			if (!this.preProcedure.getNextProcedureVector().contains(
					this.nextProcedure)) {
				this.preProcedure.getNextProcedureVector().add(
						this.nextProcedure);
			}

			if (!this.nextProcedure.getPreProcedureVector().contains(
					this.preProcedure)) {
				this.nextProcedure.getPreProcedureVector().add(
						this.preProcedure);
			}
		} else if ((this.nextProcedure == null) || (this.nextProcedure != unit)) {
			this.x1 = pX;
			this.y1 = pY;
			this.preProcedure = unit;

			if (!this.preProcedure.getNextLineVector().contains(this)) {
				this.preProcedure.getNextLineVector().add(this);
			}
			this.link = 1;
			this.tail = s;
		}
	}

	protected boolean getNextProcedure(TechnicsRouteJPanel panel, int moveX,
			int moveY) {
		Vector vector = panel.getProcedureUnits();
		for (int i = 0; i < vector.size(); i++) {
			boolean b = isLinkNextProcedure(
					(ProcedureRectangleUnit) vector.get(i), moveX, moveY);
			if (b)
				return b;
		}
		if (this.nextProcedure != null) {
			if (this.preProcedure != null) {
				if (this.canDeleteRectangleLink) {
					this.nextProcedure.getPreProcedureVector().remove(
							this.preProcedure);
					this.preProcedure.getNextProcedureVector().remove(
							this.nextProcedure);
				}
			}
			this.nextProcedure.getPreLineVector().remove(this);
			this.nextProcedure = null;
		}
		return false;
	}

	protected boolean isLinkNextProcedure(ProcedureRectangleUnit unit,
			int moveX, int moveY) {
		int pX = unit.getX() + unit.getWidth() / 2;
		int pY = unit.getY();
		int sideWidth = 32;
		if (getPointRectangle(pX, pY, sideWidth).contains(moveX, moveY)) {
			adjustNextProcedure(unit, pX, pY, "north");
			return true;
		}
		pX = unit.getX();
		pY = unit.getY() + unit.getHeight() / 2;
		if (getPointRectangle(pX, pY, sideWidth).contains(moveX, moveY)) {
			adjustNextProcedure(unit, pX, pY, "west");
			return true;
		}
		pX = unit.getX() + unit.getWidth();
		pY = unit.getY() + unit.getHeight() / 2;
		if (getPointRectangle(pX, pY, sideWidth).contains(moveX, moveY)) {
			adjustNextProcedure(unit, pX, pY, "east");
			return true;
		}
		pX = unit.getX() + unit.getWidth() / 2;
		pY = unit.getY() + unit.getHeight();
		if (getPointRectangle(pX, pY, sideWidth).contains(moveX, moveY)) {
			adjustNextProcedure(unit, pX, pY, "south");
			return true;
		}
		return false;
	}

	protected void adjustNextProcedure(ProcedureRectangleUnit unit, int pX,
			int pY, String s) {
		if ((this.preProcedure != null) && (this.preProcedure != unit)) {
			this.x2 = pX;
			this.y2 = pY;
			this.nextProcedure = unit;
			this.link = 2;
			this.head = s;

			if (!this.nextProcedure.getPreLineVector().contains(this)) {
				this.nextProcedure.getPreLineVector().add(this);
			}

			if (!this.nextProcedure.getPreProcedureVector().contains(
					this.preProcedure)) {
				this.nextProcedure.getPreProcedureVector().add(
						this.preProcedure);
			}

			if (!this.preProcedure.getNextProcedureVector().contains(
					this.nextProcedure)) {
				this.preProcedure.getNextProcedureVector().add(
						this.nextProcedure);
			}
		} else if ((this.preProcedure == null) || (this.preProcedure != unit)) {
			this.x2 = pX;
			this.y2 = pY;
			this.nextProcedure = unit;

			if (!this.nextProcedure.getPreLineVector().contains(this)) {
				this.nextProcedure.getPreLineVector().add(this);
			}
			this.link = 1;
			this.head = s;
		}
	}

	public void movePoint(int moveX, int moveY) {
		int margin = 20;
		if ((this.x1 + moveX <= margin) || (this.x2 + moveX <= margin)
				|| (this.y1 + moveY <= margin) || (this.y2 + moveY <= margin))
			return;
		this.x1 += moveX;
		this.x2 += moveX;
		this.y1 += moveY;
		this.y2 += moveY;
		judgeLinkCondition();
	}

	public void judgeLinkCondition() {
		if ((this.preProcedure == null) && (this.nextProcedure == null)) {
			this.link = 0;
			this.head = "";
			this.tail = "";
			return;
		}
		boolean b1 = false;
		if (this.preProcedure != null) {
			if (!isLinkPreProcedure(this.preProcedure, this.x1, this.y1)) {
				if (this.nextProcedure != null) {
					this.preProcedure.getNextLineVector().remove(this);
					if (this.canDeleteRectangleLink) {
						this.preProcedure.getNextProcedureVector().remove(this.nextProcedure);
						this.nextProcedure.getPreProcedureVector().remove(this.preProcedure);
					}
				}
				b1 = true;
			}
		}
		boolean b2 = false;
		if (this.nextProcedure != null) {
			if (!isLinkNextProcedure(this.nextProcedure, this.x2, this.y2)) {
				if (this.preProcedure != null) {
					this.nextProcedure.getPreLineVector().remove(this);
					if (this.canDeleteRectangleLink) {
						this.nextProcedure.getPreProcedureVector().remove(this.preProcedure);
						this.preProcedure.getNextProcedureVector().remove(this.nextProcedure);
					}
				}
				b2 = true;
			}
		}
		if (b1)
			this.preProcedure = null;
		if (b2)
			this.nextProcedure = null;
		if ((this.preProcedure == null) && (this.nextProcedure == null)) {
			this.link = 0;
			this.head = "";
			this.tail = "";
		} else if ((this.preProcedure != null) && (this.nextProcedure != null)) {
			this.link = 2;
		} else {
			this.link = 1;
			if (this.preProcedure == null)
				this.tail = "";
			if (this.nextProcedure == null)
				this.head = "";
		}
	}

	protected void moveToProcedure(int x, int y, boolean isArrow) {
		if (!isArrow) {
			this.x1 = x;
			this.y1 = y;
		} else {
			this.x2 = x;
			this.y2 = y;
		}
	}

	public void deleteSelf(TechnicsRouteJPanel panel) {
		if ((this.preProcedure != null) || (this.nextProcedure != null)) {
			if ((this.preProcedure != null) && (this.nextProcedure != null)) {
				this.preProcedure.getNextLineVector().remove(this);
				this.nextProcedure.getPreLineVector().remove(this);
				if (this.canDeleteRectangleLink) {
					this.preProcedure.getNextProcedureVector().remove(
							this.nextProcedure);
					this.nextProcedure.getPreProcedureVector().remove(
							this.preProcedure);
				}
			} else if (this.preProcedure != null) {
				this.preProcedure.getNextLineVector().remove(this);
			} else if (this.nextProcedure != null) {
				this.nextProcedure.getPreLineVector().remove(this);
			}
		}
		panel.getDrawingUnits().remove(this);
	}

	private Polygon getDecliningRectangleWithoutCorner(int x1, int y1, int x2,
			int y2, int sideWidth) {
		int xa = 0;
		int ya = 0;
		int xb = 0;
		int yb = 0;
		int xc = 0;
		int yc = 0;
		int xd = 0;
		int yd = 0;
		if (y1 > y2) {
			if (x2 > x1) {
				ya = y1 - sideWidth / 2;
				yb = y1 + sideWidth / 2;
				yc = y2 + sideWidth / 2;
				yd = y2 - sideWidth / 2;
			} else if (x2 < x1) {
				ya = y1 + sideWidth / 2;
				yb = y1 - sideWidth / 2;
				yc = y2 - sideWidth / 2;
				yd = y2 + sideWidth / 2;
			}
			xa = x1 - sideWidth / 2;
			xb = x1 + sideWidth / 2;
			xc = x2 + sideWidth / 2;
			xd = x2 - sideWidth / 2;
		} else if (y1 < y2) {
			if (x2 > x1) {
				ya = y1 + sideWidth / 2;
				yb = y1 - sideWidth / 2;
				yc = y2 - sideWidth / 2;
				yd = y2 + sideWidth / 2;
			} else if (x2 < x1) {
				ya = y1 - sideWidth / 2;
				yb = y1 + sideWidth / 2;
				yc = y2 + sideWidth / 2;
				yd = y2 - sideWidth / 2;
			}
			xa = x1 - sideWidth / 2;
			xb = x1 + sideWidth / 2;
			xc = x2 + sideWidth / 2;
			xd = x2 - sideWidth / 2;
		}
		int[] xPoints = { xa, xb, xc, xd };
		int[] yPoints = { ya, yb, yc, yd };
		return new Polygon(xPoints, yPoints, 4);
	}

	private boolean getIsDeclining(int x1, int y1, int x2, int y2) {
		if ((x1 == x2) || (y1 == y2))
			return false;
		return true;
	}

	protected Polygon getRectangle(int x1, int y1, int x2, int y2, int sideWidth) {
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
				xa = x1 + sideWidth / 2;
				xc = x2 - sideWidth / 2;
			} else {
				xa = x1 - sideWidth / 2;
				xc = x2 + sideWidth / 2;
			}
			xb = xa;
			xd = xc;
			ya = y1 - sideWidth / 2;
			yb = y1 + sideWidth / 2;
			yc = y2 + sideWidth / 2;
			yd = y2 - sideWidth / 2;
		} else if (x1 == x2) {
			if (y1 > y2) {
				ya = y1 + sideWidth / 2;
				yc = y2 - sideWidth / 2;
			} else {
				ya = y1 - sideWidth / 2;
				yc = y2 + sideWidth / 2;
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

	protected boolean containMouse(int x1, int y1, int x2, int y2, int x, int y) {
		if (getRectangle(x1, y1, x2, y2, 32).contains(x, y)) {
			return true;
		}
		return false;
	}

	public void addElementNode(Element element) {
		Element temp = element.addElement("x");
		temp.addText(this.x1 + "," + this.x2);

		temp = element.addElement("y");
		temp.addText(this.y1 + "," + this.y2);

		addProcedureElement(element);

		temp = element.addElement("head");
		temp.addText(this.head);

		temp = element.addElement("tail");
		temp.addText(this.tail);

		temp = element.addElement("note");
		temp.addText(this.note);

		temp = element.addElement("isBidirectional");
		temp.addText(String.valueOf(this.isBidirectional));
	}

	protected void addProcedureElement(Element element) {
		Element temp = element.addElement("preProcedure");
		if (this.preProcedure == null) {
			temp.addText("");
		} else {
			temp.addText(this.preProcedure.getId());
		}

		temp = element.addElement("nextProcedure");
		if (this.nextProcedure == null) {
			temp.addText("");
		} else {
			temp.addText(this.nextProcedure.getId());
		}
	}

	protected void setLinkProcedure(Vector<ProcedureRectangleUnit> vector, Element element) {
		for (int i = 0; i < vector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) vector.get(i);
			if (unit.getId().equals(element.element("preProcedure").getText())) {
				this.preProcedure = unit;
				if (!unit.nextLineVector.contains(this))
					unit.nextLineVector.add(this);
			} else if (unit.getId().equals(element.element("nextProcedure").getText())) {
				this.nextProcedure = unit;
				if (!unit.preLineVector.contains(this))
					unit.preLineVector.add(this);
			}
		}
	}

	public void setAttribute(Element element) {
		String x = element.element("x").getText();
		String y = element.element("y").getText();

		int[] xPoints = getPoints(x);
		int[] yPoints = getPoints(y);

		this.x1 = xPoints[0];
		this.x2 = xPoints[1];
		this.y1 = yPoints[0];
		this.y2 = yPoints[1];

		this.head = element.element("head").getText();
		this.tail = element.element("tail").getText();

		if (element.element("note") != null)
			this.note = element.element("note").getText();
		else
			this.note = "";
		if (element.element("isBidirectional") != null)
			this.isBidirectional = (element.element("isBidirectional")
					.getText().equals("true"));
		else
			this.isBidirectional = false;
	}

	protected int[] getPoints(String s) {
		String[] ss = s.split(",");
		int[] point = new int[ss.length];
		for (int i = 0; i < ss.length; i++) {
			point[i] = Integer.parseInt(ss[i]);
		}
		return point;
	}

	public void reSetSelectedIndex() {
		this.selectedIndex = -1;
	}

	public void setLink(int link) {
		this.link = link;
	}

	public int getLink() {
		return this.link;
	}
}
