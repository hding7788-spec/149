package com.glaway.mpm.flowchart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.Vector;
import org.dom4j.Element;

public class QuintupleLineUnit extends SingleLineUnit {
	public QuintupleLineUnit() {
	}

	public QuintupleLineUnit(int[] xPoints, int[] yPoints) {
		this.x1 = xPoints[0];
		this.y1 = yPoints[0];
		this.x2 = xPoints[1];
		this.y2 = yPoints[1];
		this.x3 = xPoints[2];
		this.y3 = yPoints[2];
		this.x4 = xPoints[3];
		this.y4 = yPoints[3];
		this.x5 = xPoints[4];
		this.y5 = yPoints[4];
		this.x6 = xPoints[5];
		this.y6 = yPoints[5];
	}

	public QuintupleLineUnit(int[] xPoints, int[] yPoints,
			Vector<TechnicsRouteUnit> drawingUnits) {
		this(xPoints, yPoints);
		drawingUnits.add(this);
	}

	public QuintupleLineUnit(int[] xPoints, int[] yPoints,
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
		g.drawLine(this.x1, this.y1, this.x2, this.y2);
		g.drawLine(this.x2, this.y2, this.x3, this.y3);
		g.drawLine(this.x3, this.y3, this.x4, this.y4);
		g.drawLine(this.x4, this.y4, this.x5, this.y5);
		g.drawLine(this.x5, this.y5, this.x6, this.y6);
		g.fillPolygon(getArrowHeader(this.x5, this.y5, this.x6, this.y6,
				getSelectedRectangleSideWidth()));

		if (this.isBidirectional) {
			g.fillPolygon(getArrowHeader(this.x2, this.y2, this.x1, this.y1,
					getSelectedRectangleSideWidth()));
		}
		if (isSelectedState()) {
			drawSelfSelectedState(g);
		}

		drawNote(g);
		g2d.setStroke(old);
	}

	protected void drawSelfSelectedState(Graphics g) {
		g.fillPolygon(getPointRectangle(this.x1, this.y1,
				getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle(this.x2, this.y2,
				getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle(this.x3, this.y3,
				getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle(this.x4, this.y4,
				getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle(this.x5, this.y5,
				getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle(this.x6, this.y6,
				getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle((this.x1 + this.x2) / 2,
				(this.y1 + this.y2) / 2, getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle((this.x2 + this.x3) / 2,
				(this.y2 + this.y3) / 2, getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle((this.x3 + this.x4) / 2,
				(this.y3 + this.y4) / 2, getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle((this.x4 + this.x5) / 2,
				(this.y4 + this.y5) / 2, getSelectedRectangleSideWidth()));
		g.fillPolygon(getPointRectangle((this.x5 + this.x6) / 2,
				(this.y5 + this.y6) / 2, getSelectedRectangleSideWidth()));
	}

	protected void drawNote(Graphics g) {
		g.setColor(Color.darkGray);
		int height = g.getFontMetrics().getHeight();
		int width = g.getFontMetrics().stringWidth(this.note);
		g.drawString(this.note, (this.x3 + this.x4 - width) / 2, (this.y3
				+ this.y4 - height) / 2);
		g.setColor(Color.black);
	}

	public void movePoint(int moveX, int moveY) {
		int margin = 20;
		if ((this.x1 + moveX <= margin) || (this.x2 + moveX <= margin)
				|| (this.x3 + moveX <= margin) || (this.x4 + moveX <= margin)
				|| (this.x5 + moveX <= margin) || (this.x6 + moveX <= margin)
				|| (this.y1 + moveY <= margin) || (this.y2 + moveY <= margin)
				|| (this.y3 + moveY <= margin) || (this.y4 + moveY <= margin)
				|| (this.y5 + moveY <= margin) || (this.y6 + moveY <= margin))
			return;
		this.x1 += moveX;
		this.x2 += moveX;
		this.x3 += moveX;
		this.x4 += moveX;
		this.x5 += moveX;
		this.x6 += moveX;
		this.y1 += moveY;
		this.y2 += moveY;
		this.y3 += moveY;
		this.y4 += moveY;
		this.y5 += moveY;
		this.y6 += moveY;
		judgeLinkCondition();
	}

	public boolean containMouse(int x, int y) {
		return (containMouse(this.x1, this.y1, this.x2, this.y2, x, y))
				|| (containMouse(this.x2, this.y2, this.x3, this.y3, x, y))
				|| (containMouse(this.x3, this.y3, this.x4, this.y4, x, y))
				|| (containMouse(this.x4, this.y4, this.x5, this.y5, x, y))
				|| (containMouse(this.x5, this.y5, this.x6, this.y6, x, y));
	}

	public boolean setMouseCursor(TechnicsRouteJPanel panel, int x, int y) {
		if (getPointRectangle(this.x1, this.y1, 32).contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(13));
			this.selectedIndex = 0;
			return true;
		}

		if (getPointRectangle(this.x2, this.y2, 32).contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(13));
			this.selectedIndex = 1;
			return true;
		}

		if (getPointRectangle(this.x3, this.y3, 32).contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(13));
			this.selectedIndex = 2;
			return true;
		}

		if (getPointRectangle(this.x4, this.y4, 32).contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(13));
			this.selectedIndex = 3;
			return true;
		}

		if (getPointRectangle(this.x5, this.y5, 32).contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(13));
			this.selectedIndex = 4;
			return true;
		}

		if (getPointRectangle(this.x6, this.y6, 32).contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(13));
			this.selectedIndex = 5;
			return true;
		}

		if (getPointRectangle((this.x1 + this.x2) / 2, (this.y1 + this.y2) / 2,
				32).contains(x, y)) {
			if (this.x1 == this.x2) {
				panel.setCursor(Cursor.getPredefinedCursor(11));
			} else {
				panel.setCursor(Cursor.getPredefinedCursor(8));
			}
			this.selectedIndex = 6;
			return true;
		}

		if (getPointRectangle((this.x2 + this.x3) / 2, (this.y2 + this.y3) / 2,
				32).contains(x, y)) {
			if (this.x2 == this.x3) {
				panel.setCursor(Cursor.getPredefinedCursor(11));
			} else {
				panel.setCursor(Cursor.getPredefinedCursor(8));
			}
			this.selectedIndex = 7;
			return true;
		}

		if (getPointRectangle((this.x3 + this.x4) / 2, (this.y3 + this.y4) / 2,
				32).contains(x, y)) {
			if (this.x3 == this.x4) {
				panel.setCursor(Cursor.getPredefinedCursor(11));
			} else {
				panel.setCursor(Cursor.getPredefinedCursor(8));
			}
			this.selectedIndex = 8;
			return true;
		}

		if (getPointRectangle((this.x4 + this.x5) / 2, (this.y4 + this.y5) / 2,
				32).contains(x, y)) {
			if (this.x4 == this.x5) {
				panel.setCursor(Cursor.getPredefinedCursor(11));
			} else {
				panel.setCursor(Cursor.getPredefinedCursor(8));
			}
			this.selectedIndex = 9;
			return true;
		}

		if (getPointRectangle((this.x5 + this.x6) / 2, (this.y5 + this.y6) / 2,
				32).contains(x, y)) {
			if (this.x5 == this.x6) {
				panel.setCursor(Cursor.getPredefinedCursor(11));
			} else {
				panel.setCursor(Cursor.getPredefinedCursor(8));
			}
			this.selectedIndex = 10;
			return true;
		}

		if (getRectangleWithoutCorner(this.x1, this.y1, this.x2, this.y2, 32)
				.contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(12));
			return false;
		}

		if (getRectangleWithoutCorner(this.x2, this.y2, this.x3, this.y3, 32)
				.contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(12));
			return false;
		}

		if (getRectangleWithoutCorner(this.x3, this.y3, this.x4, this.y4, 32)
				.contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(12));
			return false;
		}

		if (getRectangleWithoutCorner(this.x4, this.y4, this.x5, this.y5, 32)
				.contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(12));
			return false;
		}

		if (getRectangleWithoutCorner(this.x5, this.y5, this.x6, this.y6, 32)
				.contains(x, y)) {
			panel.setCursor(Cursor.getPredefinedCursor(12));
			return false;
		}
		panel.setCursor(Cursor.getDefaultCursor());
		return false;
	}

	public boolean flexProperties(TechnicsRouteJPanel panel, int x1, int y1,
			int x2, int y2) {
		String arrowDirection = getArrowDirection();
		if (this.selectedIndex != -1) {
			int margin = 20;
			if ((x2 <= margin) || (y2 <= margin))
				return false;
			if (this.selectedIndex == 0) {
				this.x1 = x2;
				this.y1 = y2;
				if (arrowDirection == "horizontal")
					this.y2 = y2;
				else
					this.x2 = x2;
				getPreProcedure(panel, x2, y2);
			} else if (this.selectedIndex == 1) {
				this.x2 = x2;
				this.y2 = y2;
				if (arrowDirection == "horizontal") {
					this.y1 = y2;
					this.x3 = x2;
				} else {
					this.x1 = x2;
					this.y3 = y2;
				}
			} else if (this.selectedIndex == 2) {
				this.x3 = x2;
				this.y3 = y2;
				if (arrowDirection == "horizontal") {
					this.x2 = x2;
					this.y4 = y2;
				} else {
					this.y2 = y2;
					this.x4 = x2;
				}
			} else if (this.selectedIndex == 3) {
				this.x4 = x2;
				this.y4 = y2;
				if (arrowDirection == "horizontal") {
					this.y3 = y2;
					this.x5 = x2;
				} else {
					this.x3 = x2;
					this.y5 = y2;
				}
			} else if (this.selectedIndex == 4) {
				this.x5 = x2;
				this.y5 = y2;
				if (arrowDirection == "horizontal") {
					this.x4 = x2;
					this.y6 = y2;
				} else {
					this.y4 = y2;
					this.x6 = x2;
				}
			} else if (this.selectedIndex == 5) {
				this.x6 = x2;
				this.y6 = y2;
				if (arrowDirection == "horizontal")
					this.y5 = y2;
				else
					this.x5 = x2;
				getNextProcedure(panel, x2, y2);
			} else if (this.selectedIndex == 6) {
				if (arrowDirection == "horizontal") {
					this.y1 = y2;
					this.y2 = y2;
				} else if (arrowDirection == "vertical") {
					this.x1 = x2;
					this.x2 = x2;
				}
			} else if (this.selectedIndex == 7) {
				if (arrowDirection == "horizontal") {
					this.x2 = x2;
					this.x3 = x2;
				} else if (arrowDirection == "vertical") {
					this.y2 = y2;
					this.y3 = y2;
				}
			} else if (this.selectedIndex == 8) {
				if (arrowDirection == "horizontal") {
					this.y3 = y2;
					this.y4 = y2;
				} else if (arrowDirection == "vertical") {
					this.x3 = x2;
					this.x4 = x2;
				}
			} else if (this.selectedIndex == 9) {
				if (arrowDirection == "horizontal") {
					this.x4 = x2;
					this.x5 = x2;
				} else if (arrowDirection == "vertical") {
					this.y4 = y2;
					this.y5 = y2;
				}
			} else if (this.selectedIndex == 10) {
				if (arrowDirection == "horizontal") {
					this.y5 = y2;
					this.y6 = y2;
				} else if (arrowDirection == "vertical") {
					this.x5 = x2;
					this.x6 = x2;
				}
			}
			judgeLinkCondition();
			return true;
		}
		judgeLinkCondition();
		return false;
	}

	protected void adjustPreLine(int x, int y) {
		String arrowDirection = getArrowDirection();
		if (arrowDirection == "horizontal")
			this.y2 = y;
		else
			this.x2 = x;
	}

	protected void adjustPreProcedure(ProcedureRectangleUnit unit, int pX,
			int pY, String s) {
		if ((this.nextProcedure != null) && (this.nextProcedure != unit)) {
			adjustPreLine(pX, pY);
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
			adjustPreLine(pX, pY);
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

	protected void adjustNextLine(int x, int y) {
		String arrowDirection = getArrowDirection();
		if (arrowDirection == "horizontal")
			this.y5 = y;
		else
			this.x5 = x;
	}

	protected void adjustNextProcedure(ProcedureRectangleUnit unit, int pX,
			int pY, String s) {
		if ((this.preProcedure != null) && (this.preProcedure != unit)) {
			adjustNextLine(pX, pY);
			this.x6 = pX;
			this.y6 = pY;
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
			adjustNextLine(pX, pY);
			this.x6 = pX;
			this.y6 = pY;
			this.nextProcedure = unit;

			if (!this.nextProcedure.getPreLineVector().contains(this)) {
				this.nextProcedure.getPreLineVector().add(this);
			}
			this.link = 1;
			this.head = s;
		}
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
						this.preProcedure.getNextProcedureVector().remove(
								this.nextProcedure);
						this.nextProcedure.getPreProcedureVector().remove(
								this.preProcedure);
					}
				}
				b1 = true;
			}
		}
		boolean b2 = false;
		if (this.nextProcedure != null) {
			if (!isLinkNextProcedure(this.nextProcedure, this.x6, this.y6)) {
				if (this.preProcedure != null) {
					this.nextProcedure.getPreLineVector().remove(this);
					if (this.canDeleteRectangleLink) {
						this.nextProcedure.getPreProcedureVector().remove(
								this.preProcedure);
						this.preProcedure.getNextProcedureVector().remove(
								this.nextProcedure);
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
		}
	}

	protected void moveToProcedure(int x, int y, boolean isArrow) {
		if (!isArrow) {
			adjustPreLine(x, y);
			this.x1 = x;
			this.y1 = y;
		} else {
			adjustNextLine(x, y);
			this.x6 = x;
			this.y6 = y;
		}
	}

	private String getArrowDirection() {
		if ((this.y1 == this.y2) && (this.x2 == this.x3)
				&& (this.y3 == this.y4) && (this.x4 == this.x5)
				&& (this.y5 == this.y6)) {
			return "horizontal";
		}
		return "vertical";
	}

	public void addElementNode(Element element) {
		StringBuffer x = new StringBuffer();
		x.append(this.x1).append(",").append(this.x2).append(",")
				.append(this.x3).append(",").append(this.x4).append(",")
				.append(this.x5).append(",").append(this.x6);

		StringBuffer y = new StringBuffer();
		y.append(this.y1).append(",").append(this.y2).append(",")
				.append(this.y3).append(",").append(this.y4).append(",")
				.append(this.y5).append(",").append(this.y6);

		Element temp = element.addElement("x");
		temp.addText(x + "");

		temp = element.addElement("y");
		temp.addText(y + "");

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

	public void setAttribute(Element element) {
		String x = element.element("x").getText();
		String y = element.element("y").getText();

		int[] xPoints = getPoints(x);
		int[] yPoints = getPoints(y);

		this.x1 = xPoints[0];
		this.x2 = xPoints[1];
		this.x3 = xPoints[2];
		this.x4 = xPoints[3];
		this.x5 = xPoints[4];
		this.x6 = xPoints[5];
		this.y1 = yPoints[0];
		this.y2 = yPoints[1];
		this.y3 = yPoints[2];
		this.y4 = yPoints[3];
		this.y5 = yPoints[4];
		this.y6 = yPoints[5];

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
}
