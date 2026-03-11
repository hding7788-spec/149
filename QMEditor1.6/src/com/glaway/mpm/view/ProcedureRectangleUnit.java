package com.glaway.mpm.view;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

public class ProcedureRectangleUnit {
	// 左上点x坐标
	private int x;
	// 左上点y坐标
	private int y;
	// 宽
	private int width;
	// 高
	private int height;
	// 装配部门
	private String assemblageDept;
	// 工序号
	private String number;
	// 工序名称
	private String name;

	// 字体名称
	private String fontName = "Dialog";
	// 字体大小
	private int fontSize = 12;

	public ProcedureRectangleUnit() {
	}

	public ProcedureRectangleUnit(int x, int y, int width, int height,
			String assemblageDept, String number, String name) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		setAssemblageDept(assemblageDept);
		setNumber(number);
		setName(name);
	}

	public void drawSelf(Graphics g) {
		drawBorder(g);
		drawStringValue(g);
	}

	private void drawBorder(Graphics g) {
		int x1 = x + width;
		int y1 = y + height / 3;
		int y2 = y + height * 2 / 3;
		int y3 = y + height;
		g.drawLine(x, y, x, y3);// 第一条竖线
		g.drawLine(x1, y, x1, y3);// 第二条竖线
		g.drawLine(x, y, x1, y);// 第一条直线
		g.drawLine(x, y1, x1, y1);// 第二条直线
		g.drawLine(x, y2, x1, y2);// 第三条直线
		g.drawLine(x, y3, x1, y3);// 第四条直线
	}

	private void drawStringValue(Graphics g) {
		Font font = new Font(fontName, 0, fontSize);
		g.setFont(font);
		drawOneStringValue(g, y, assemblageDept);
		drawOneStringValue(g, y + height / 3, "工序" + number);
		drawOneStringValue(g, y + height * 2 / 3, name);
	}

	private void drawOneStringValue(Graphics g, int y, String str) {
		if (str == null || str.equals(""))
			return;

		int stringWidth = g.getFontMetrics().stringWidth(str);

		if (stringWidth <= width
				&& (-1 == str.indexOf("\n") || str.length() - 1 == str
						.indexOf("\n"))) {
			drawStringNoOverWidth(g, str, y);
		} else {
			drawStringOverWidthAndNoNewLine(g, str, y);
		}
	}

	private void drawStringNoOverWidth(Graphics g, String str, int yPoint) {
		double stringWidth = (double) g.getFontMetrics().stringWidth(str);
		int height = g.getFontMetrics().getHeight();
		int asc = g.getFontMetrics().getAscent();
		double desc = g.getFontMetrics().getDescent();
		int x = this.x;
		int y = (int) (asc + yPoint + ((this.height / 3 - asc - desc) / 2));
		x = (int) (x + ((this.width - stringWidth) / 2));

		y = getYVMiddle(asc, yPoint, this.height / 3, height, g
				.getFontMetrics().getLeading());

		g.drawString(str, x, y);
	}

	private void drawStringOverWidthAndNoNewLine(Graphics g, String str,
			int yPoint) {
		double stringWidth = (double) g.getFontMetrics().stringWidth(str);
		double scale = getCompressScale(this.width, stringWidth);
		int height = g.getFontMetrics().getHeight();
		AffineTransform affTrans = new AffineTransform();

		affTrans.scale(scale, 1.00);
		Font tempFont = g.getFont().deriveFont(affTrans);
		g.setFont(tempFont);

		int x = (int) Math.round(this.x);
		int asc = g.getFontMetrics().getAscent();
		double desc = g.getFontMetrics().getDescent();
		int y = (int) (asc + yPoint + ((this.height / 3 - asc - desc) / 2));
		y = getYVMiddle(asc, yPoint, this.height / 3, height, g
				.getFontMetrics().getLeading());

		g.drawString(str, x, y);

		Font font = new Font(fontName, 0, fontSize);
		g.setFont(font);
	}

	private int getYVMiddle(int fontAscent, int y, int cellHeigh,
			int fontHeight, int fontLeading) {
		if ((cellHeigh - fontHeight - fontLeading) < 0)
			return (int) (fontAscent + y);
		else
			return (int) (fontAscent + y + (cellHeigh - fontHeight - fontLeading) / 2);
	}

	private double getCompressScale(double cellWidth, double stringWidth) {
		double scale = 0;
		scale = ((double) (cellWidth - 5)) / stringWidth;
		return scale;
	}

	public int getStringWidth(JComponent component, String str) {
		Font font = new Font(getFontName(), 0, getFontSize());
		FontMetrics fm = component.getFontMetrics(font);
		return fm.stringWidth(str);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getFontName() {
		return fontName;
	}

	public int getFontSize() {
		return fontSize;
	}

	public String getAssemblageDept() {
		return assemblageDept;
	}

	public void setAssemblageDept(String s) {
		assemblageDept = s;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String s) {
		number = s;
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}
}