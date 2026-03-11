package com.glaway.mpm.flowchart;

import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.QuickCreateProcedureJPanel;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.*;

public class TechnicsRouteJPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected NewTechnicsPart parentFrame = null;

	private Vector<TechnicsRouteUnit> drawingUnits = new Vector<TechnicsRouteUnit>();

	private Vector<ProcedureRectangleUnit> procedureUnit = new Vector<ProcedureRectangleUnit>();
	protected TechnicsRouteUnit tempUnit;
	protected int pressX;
	protected int pressY;
	private int releaseX;
	private int releaseY;
	private int lastX;
	private int lastY;
	private boolean flex = false;

	private ProcedureRectanglePopupMenu procedureRectanglePopupMenu = new ProcedureRectanglePopupMenu(this);

	private LinePopupMenu linePopupMenu = new LinePopupMenu(this);

	private TechnicsRoutePopupMenu technicsRoutePopupMenu = new TechnicsRoutePopupMenu(this);
	protected static final int MARGIN = 20;
	protected int mouseMode = 1;

	protected int sideLength = 40;

	protected Vector<ProcedureRectangleUnit> multiSelectedVector = new Vector<ProcedureRectangleUnit>();

	private boolean isEnclosed = false;

	private ProcedureRectangleUnit tempBeginProcedureRectangleUnit = null;

	private ProcedureRectangleUnit tempEndProcedureRectangleUnit = null;

	private Polygon beginPoly = null;

	private Polygon endPoly = null;
	private int lineBeginX;
	private int lineBeginY;
	private int moveX;
	private int moveY;
	private boolean beginMoveLine = false;

	private String technicsNumber = "";

	private String technicsCategory = "";

	private String technicsName = "";

	private static String imageDefaultPath = null;

	private JFileChooser chooser = new JFileChooser();

	private boolean eventEnabled = true;

	public TechnicsRouteJPanel(NewTechnicsPart frame) {
		this.parentFrame = frame;
		setBackground(Color.WHITE);
		setLayout(null);

		Document document = frame.getCurrentTechnics();
		if (document != null) {
			try {
				Element techElement = XmlUtility.getTechnicsElement(document);
				String lifecycle = techElement.attributeValue("lifecycle");
				if ((lifecycle != null) && (!"".equals(lifecycle))
						&& (!lifecycle.equals("正在工作"))
						&& (!lifecycle.equals("修改中"))) {
					this.eventEnabled = false;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "工艺路线图读取当前工艺节点时出错！", "提示", 1);
				e.printStackTrace();
			}
		}

		addMouseListener(new ClickMouceListener());
		addMouseMotionListener(new PanelMouseMotion());

		refreshProcedureVector();
		viewAdjusting();

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (!TechnicsRouteJPanel.this.eventEnabled)
					return;
				if (e.getKeyCode() == 127) {
					if ((TechnicsRouteJPanel.this.tempUnit != null)
							&& ((TechnicsRouteJPanel.this.tempUnit instanceof SingleLineUnit))) {
						((SingleLineUnit) TechnicsRouteJPanel.this.tempUnit).canDeleteRectangleLink = TechnicsRouteUtil
								.canDeleteRectangleLink(TechnicsRouteJPanel.this, (SingleLineUnit) TechnicsRouteJPanel.this.tempUnit);
						((SingleLineUnit) TechnicsRouteJPanel.this.tempUnit).deleteSelf(TechnicsRouteJPanel.this);
						TechnicsRouteJPanel.this.repaint();
					}
				}
			}
		});
	}

	public void paint(Graphics g) {
		super.paint(g);
		drawEntities(g);

		if (this.isEnclosed) {
			drawEnclosed(g);
		}
		drawEnterZone(g);
		drawMoveLine(g);
	}

	private void drawEntities(Graphics g) {
		TechnicsRouteUnit unit = null;
		for (Iterator it = this.drawingUnits.iterator(); it.hasNext();) {
			unit = (TechnicsRouteUnit) it.next();
			unit.drawSelf(g);
		}
	}

	private void drawEnclosed(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		float[] dash = { 0.3F, 0.3F };
		BasicStroke bs = new BasicStroke(0.5F, 0, 0, 1.0F, dash, 0.0F);
		Stroke old = g2d.getStroke();

		int[] x = { this.lastX, this.releaseX, this.releaseX, this.lastX };
		int[] y = { this.lastY, this.lastY, this.releaseY, this.releaseY };

		Line2D.Float line1 = new Line2D.Float(x[0], y[0], x[1], y[1]);
		Line2D.Float line2 = new Line2D.Float(x[1], y[1], x[2], y[2]);
		Line2D.Float line3 = new Line2D.Float(x[2], y[2], x[3], y[3]);
		Line2D.Float line4 = new Line2D.Float(x[3], y[3], x[0], y[0]);
		g2d.setStroke(bs);
		g2d.draw(line1);
		g2d.draw(line2);
		g2d.draw(line3);
		g2d.draw(line4);
		g2d.setStroke(old);
	}

	private void drawEnterZone(Graphics g) {
		if (this.mouseMode > 1) {
			g.setColor(Color.magenta);
			if (this.beginPoly != null)
				g.fillPolygon(this.beginPoly);
			if (this.endPoly != null)
				g.fillPolygon(this.endPoly);
			g.setColor(Color.black);
		}
	}

	private void drawMoveLine(Graphics g) {
		if ((this.mouseMode > 1) && (this.beginPoly != null)
				&& (this.beginMoveLine)) {
			Graphics2D g2d = (Graphics2D) g;
			float[] dash = { 0.3F, 0.3F };
			BasicStroke bs = new BasicStroke(0.5F, 0, 0, 1.0F, dash, 0.0F);
			Stroke old = g2d.getStroke();

			Line2D.Float line = new Line2D.Float(this.lineBeginX, this.lineBeginY, this.releaseX, this.releaseY);
			g2d.setStroke(bs);
			g2d.setColor(Color.magenta);
			g2d.draw(line);
			g2d.setStroke(old);
			g2d.setColor(Color.black);
		}
	}

	protected void setAddLineState() {
		this.tempUnit = null;
		TechnicsRouteUtil.cleanupSelectedState(this.drawingUnits, false);
		TechnicsRouteUtil.cleanupLinkedState(this.drawingUnits);
		setCursor(Cursor.getPredefinedCursor(1));
	}

	protected void addSingleLineUnit(boolean isBidirectional) {
		int x1 = this.pressX;
		int y1 = this.pressY;
		int x2 = this.pressX + this.sideLength;
		int y2 = y1;
		new SingleLineUnit(new int[] { x1, x2 }, new int[] { y1, y2 }, this.drawingUnits, isBidirectional);
	}

	protected void addHorizontalDoubleLineUnit(boolean isBidirectional) {
		int x1 = this.pressX;
		int y1 = this.pressY;
		int x2 = x1;
		int y2 = y1 + this.sideLength;
		int x3 = x2 + this.sideLength;
		int y3 = y2;
		new DoubleLineUnit(new int[] { x1, x2, x3 }, new int[] { y1, y2, y3 }, this.drawingUnits, isBidirectional);
	}

	protected void addVerticalDoubleLineUnit(boolean isBidirectional) {
		int x1 = this.pressX;
		int y1 = this.pressY;
		int x2 = x1 + this.sideLength;
		int y2 = y1;
		int x3 = x2;
		int y3 = y2 + this.sideLength;
		new DoubleLineUnit(new int[] { x1, x2, x3 }, new int[] { y1, y2, y3 }, this.drawingUnits, isBidirectional);
	}

	protected void addHorizontalTrebleLineUnit(boolean isBidirectional) {
		int x1 = this.pressX;
		int y1 = this.pressY;
		int x2 = x1 - this.sideLength / 2;
		int y2 = y1;
		int x3 = x2;
		int y3 = y2 + this.sideLength;
		int x4 = x3 + this.sideLength;
		int y4 = y3;
		new TrebleLineUnit(new int[] { x1, x2, x3, x4 }, new int[] { y1, y2, y3, y4 }, this.drawingUnits, isBidirectional);
	}

	protected void addVerticalTrebleLineUnit(boolean isBidirectional) {
		int x1 = this.pressX;
		int y1 = this.pressY;
		int x2 = x1;
		int y2 = y1 - this.sideLength / 2;
		int x3 = x2 + this.sideLength;
		int y3 = y2;
		int x4 = x3;
		int y4 = y3 + this.sideLength;
		new TrebleLineUnit(new int[] { x1, x2, x3, x4 }, new int[] { y1, y2, y3, y4 }, this.drawingUnits, isBidirectional);
	}

	protected void addVerticalQuintupleLineUnit(boolean isBidirectional) {
		int x1 = this.pressX;
		int y1 = this.pressY - this.sideLength / 2;
		int x2 = x1 + this.sideLength / 2;
		int y2 = y1;
		int x3 = x2;
		int y3 = this.pressY;
		int x4 = this.pressX - this.sideLength / 2;
		int y4 = y3;
		int x5 = x4;
		int y5 = this.pressY + this.sideLength / 2;
		int x6 = x1;
		int y6 = y5;
		new QuintupleLineUnit(new int[] { x1, x2, x3, x4, x5, x6 }, new int[] {
				y1, y2, y3, y4, y5, y6 }, this.drawingUnits, isBidirectional);
	}

	protected void deleteUselessLine() {
		for (int i = 0; i < this.drawingUnits.size(); i++) {
			TechnicsRouteUnit unit = (TechnicsRouteUnit) this.drawingUnits.get(i);
			if ((unit instanceof SingleLineUnit)) {
				if ((((SingleLineUnit) unit).preProcedure == null)
						&& (((SingleLineUnit) unit).nextProcedure == null)) {
					this.drawingUnits.remove(unit);
					i--;
				}
			}
		}
		repaint();
	}

	protected void createImage() throws Exception {
		if (this.procedureUnit.size() == 0)
			return;
		File file = null;
		if (imageDefaultPath == null)
			file = FileChooserTool.getSaveFile(this.chooser, "jpg", "工艺路线图.jpg", this.parentFrame);
		else
			file = FileChooserTool.getSaveFile(this.chooser, "jpg", "工艺路线图.jpg", imageDefaultPath, this.parentFrame);
		if (file == null)
			return;
		imageDefaultPath = file.getAbsolutePath();
		if (file.exists()) {
			int anwser = JOptionPane.showConfirmDialog(null, "已经存在的文件，是否覆盖？", "提示", 0);
			if (anwser == 1) {
				createImage();
				return;
			}
			if (anwser == -1) {
				return;
			}
		}
		String filePath = file.getAbsolutePath();
		if ((filePath == null) || (filePath.trim().equals("")))
			return;
		if (!filePath.toLowerCase().endsWith(".jpg"))
			filePath = filePath + ".jpg";
		viewAdjusting();
		BufferedImage comImage = (BufferedImage) createImage(getPreferredSize().width, getPreferredSize().height);
		Graphics og = comImage.getGraphics();
		og.setClip(0, 0, getPreferredSize().width, getPreferredSize().height);
		paint(og);
		FileOutputStream output = new FileOutputStream(filePath);
		ImageIO.write(comImage, "jpg", output);
		output.close();
		JOptionPane.showMessageDialog(null, "成功生成工艺路线图图片！", "提示", 1);
	}

	protected void saveTechnicsRoute() {
		if ((this.technicsNumber != null) && (!this.technicsNumber.equals(""))) {
			try {
				if(checksaveTechnicsRoute()){
					TechnicsRouteUtil.saveTechnicsRoute(this);
					TechnicsRouteUtil.saveTechnics(this,parentFrame);
					setQuickCreateProcedureJPanelTableByTechnicsRoute();
					JOptionPane.showMessageDialog(null, "保存成功！", "提示", 1);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "保存过程中出现错误！", "提示", 1);
			}
		}
	}

	protected void saveTechnicsRoute2() {
		if ((this.technicsNumber != null) && (!this.technicsNumber.equals(""))) {
			try {
				if(checksaveTechnicsRoute()){
					TechnicsRouteUtil.saveTechnicsRoute(this);
					TechnicsRouteUtil.saveTechnics(this,parentFrame);
					setQuickCreateProcedureJPanelTableByTechnicsRoute();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "保存过程中出现错误！", "提示", 1);
			}
		}
	}

	protected void refreshTechnicsRoute() throws Exception {
		this.parentFrame.createTechnicsRoute();
	}

	protected void reSetTechnicsRoute() throws Exception {
		Document document = this.parentFrame.getCurrentTechnics();
		if (document == null)
			return;
		Element techEle = XmlUtility.getTechnicsElement(document);
		Element element = (Element) techEle.clone();
		List list = XmlUtility.getAllSteps(element);
		String technicsNumber = techEle.attributeValue("technicsNumber");
		String technicsName = techEle.attributeValue("technicsName");
		String technicsCategory = techEle.attributeValue("technicsCategory");

		if ((list == null) || (list.size() == 0)) {
			JOptionPane.showMessageDialog(this.parentFrame, "选择的工艺节点下不存在工序！", "提示", 1);
			return;
		}
		this.drawingUnits.removeAllElements();
		TechnicsRouteUtil.firstCreateTechnicsRoute(technicsNumber, technicsName, technicsCategory, list, this);
	}
	protected void reSetTechnicsRoute1() throws Exception {
		Document document = this.parentFrame.getCurrentTechnics();
		if (document == null)
			return;
		Element techEle = XmlUtility.getTechnicsElement(document);
		Element element = (Element) techEle.clone();
		List list = XmlUtility.getAllSteps(element);
		String technicsNumber = techEle.attributeValue("technicsNumber");
		String technicsName = techEle.attributeValue("technicsName");
		String technicsCategory = techEle.attributeValue("technicsCategory");

		if ((list == null) || (list.size() == 0)) {
			JOptionPane.showMessageDialog(this.parentFrame, "选择的工艺节点下不存在工序！", "提示", 1);
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			Element stepEle = (Element) list.get(i);
			String preBsoID = stepEle.attributeValue("preBsoID");
			String nextBsoID = stepEle.attributeValue("nextBsoID");
			if((preBsoID != null && preBsoID.contains(",")) || (nextBsoID != null && nextBsoID.contains(","))) {
				int isRefresh = JOptionPane.showConfirmDialog(null, "当前工艺存在并行工序，刷新后会重新设置所有工序为串行连接，请确认是否需要执行此操作？", "确定", JOptionPane.YES_NO_OPTION);
				if(isRefresh == JOptionPane.NO_OPTION) {
					return;
				} else{
					break;
				}
			}
		}
		this.drawingUnits.removeAllElements();
		TechnicsRouteUtil.firstCreateTechnicsRoute1(technicsNumber, technicsName, technicsCategory, list, this);
	}

	protected void toUp() {
		int up = 0;
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			int y = unit.getY();
			if (up == 0)
				up = y;
			if (y < up)
				up = y;
		}
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			unit.setY(up);
			unit.adjustLineLocation();
		}
		viewAdjusting();
		repaint();
	}

	protected void toBottom() {
		int bottom = 0;
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			int y = unit.getY() + unit.getHeight();
			if (y > bottom)
				bottom = y;
		}
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			unit.setY(bottom - unit.getHeight());
			unit.adjustLineLocation();
		}
		viewAdjusting();
		repaint();
	}

	protected void toLeft() {
		int left = 0;
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			int x = unit.getX();
			if (left == 0)
				left = x;
			if (left > x)
				left = x;
		}
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			unit.setX(left);
			unit.adjustLineLocation();
		}
		viewAdjusting();
		repaint();
	}

	protected void toRight() {
		int right = 0;
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			int x = unit.getX() + unit.getWidth();
			if (x > right)
				right = x;
		}
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			unit.setX(right - unit.getWidth());
			unit.adjustLineLocation();
		}
		viewAdjusting();
		repaint();
	}

	protected void toHorizontalCenter() {
		int horizontalBegin = 0;
		int horizontalEnd = 0;
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			int begin = unit.getX();
			int end = unit.getX() + unit.getWidth();
			if (horizontalBegin == 0)
				horizontalBegin = begin;
			if (begin < horizontalBegin)
				horizontalBegin = begin;
			if (end > horizontalEnd)
				horizontalEnd = end;
		}
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector.get(i);
			unit.setX((horizontalBegin + horizontalEnd - unit.getWidth()) / 2);
			unit.adjustLineLocation();
		}
		viewAdjusting();
		repaint();
	}

	protected void toVerticalCenter() {
		int horizontalBegin = 0;
		int horizontalEnd = 0;
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector
					.get(i);
			int begin = unit.getY();
			int end = unit.getY() + unit.getHeight();
			if (horizontalBegin == 0)
				horizontalBegin = begin;
			if (begin < horizontalBegin)
				horizontalBegin = begin;
			if (end > horizontalEnd)
				horizontalEnd = end;
		}
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector
					.get(i);
			unit.setY((horizontalBegin + horizontalEnd - unit.getHeight()) / 2);
			unit.adjustLineLocation();
		}
		viewAdjusting();
		repaint();
	}

	private void selectEnclosedRectangle(MouseEvent e) {
		this.isEnclosed = false;
		this.releaseX = e.getX();
		this.releaseY = e.getY();
		int[] x = { this.lastX, this.releaseX, this.releaseX, this.lastX };
		int[] y = { this.lastY, this.lastY, this.releaseY, this.releaseY };
		Polygon polygon = new Polygon(x, y, 4);
		for (int i = 0; i < this.procedureUnit.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.procedureUnit
					.get(i);
			Rectangle rec = new Rectangle(unit.getX(), unit.getY(),
					unit.getWidth(), unit.getHeight());
			if (polygon.contains(rec)) {
				unit.setSelectedState(true);
				unit.setLineLinkedColor();
				this.multiSelectedVector.add(unit);
			}
		}
		repaint();
	}

	public void setDrawingUnits(Vector<TechnicsRouteUnit> vector) {
		this.drawingUnits = vector;
	}

	public Vector<TechnicsRouteUnit> getDrawingUnits() {
		return this.drawingUnits;
	}

	public void setProcedureUnits(Vector<ProcedureRectangleUnit> vector) {
		this.procedureUnit = vector;
	}

	public Vector<ProcedureRectangleUnit> getProcedureUnits() {
		return this.procedureUnit;
	}

	public Vector<ProcedureRectangleUnit> refreshProcedureVector() {
		if ((this.drawingUnits != null) && (this.drawingUnits.size() > 0)) {
			this.procedureUnit.removeAllElements();
			for (int i = 0; i < this.drawingUnits.size(); i++) {
				TechnicsRouteUnit unit = (TechnicsRouteUnit) this.drawingUnits.get(i);
				if ((unit instanceof ProcedureRectangleUnit))
					this.procedureUnit.add((ProcedureRectangleUnit) unit);
			}
		}
		return this.procedureUnit;
	}

	public void viewAdjusting() {
		if (this.drawingUnits.size() == 0) {
			return;
		}
		int x = 0;
		int y = 0;
		for (int i = 0; i < this.drawingUnits.size(); i++) {
			TechnicsRouteUnit unit = (TechnicsRouteUnit) this.drawingUnits.get(i);
			if ((unit instanceof ProcedureRectangleUnit)) {
				int maxX = unit.getX() + unit.getWidth();
				int maxY = unit.getY() + unit.getHeight();
				if (maxX > x)
					x = maxX;
				if (maxY > y)
					y = maxY;
			} else {
				SingleLineUnit unit1 = (SingleLineUnit) unit;
				x = getMaxPoint(new int[] { unit1.x1, unit1.x2, unit1.x3, unit1.x4 }, x);
				y = getMaxPoint(new int[] { unit1.y1, unit1.y2, unit1.y3, unit1.y4 }, y);
			}
		}
		setPreferredSize(new Dimension(x + 50, y + 40));
		repaint();
		updateUI();
	}

	private boolean canXMove() {
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector
					.get(i);
			if (unit.getX() + this.releaseX - this.pressX < 20)
				return false;
		}
		return true;
	}

	private boolean canYMove() {
		for (int i = 0; i < this.multiSelectedVector.size(); i++) {
			ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.multiSelectedVector
					.get(i);
			if (unit.getY() + this.releaseY - this.pressY < 20)
				return false;
		}
		return true;
	}

	private int getMaxPoint(int[] points, int point) {
		for (int i = 0; i < points.length; i++) {
			if (points[i] > point)
				point = points[i];
		}
		return point;
	}

	public void setTechnicsNumber(String technicsNumber) {
		this.technicsNumber = technicsNumber;
	}

	public String getTechnicsNumber() {
		return this.technicsNumber;
	}

	public String getTechnicsCategory() {
		return technicsCategory;
	}

	public void setTechnicsCategory(String technicsCategory) {
		this.technicsCategory = technicsCategory;
	}

	public String getTechnicsName() {
		return technicsName;
	}

	public void setTechnicsName(String technicsName) {
		this.technicsName = technicsName;
	}

	protected JFrame getFrame() {
		return this.parentFrame;
	}

	public void clearAll() {
		this.technicsNumber = "";
		this.technicsName = "";
		this.technicsCategory = "";
		this.drawingUnits.removeAllElements();
		this.procedureUnit.removeAllElements();
		viewAdjusting();
	}

	public void setEventEnabled(boolean eventEnabled) {
		this.eventEnabled = eventEnabled;
	}

	class ClickMouceListener extends MouseAdapter {
		ClickMouceListener() {
		}

		public void mousePressed(MouseEvent e) {
			if (!TechnicsRouteJPanel.this.eventEnabled)
				return;
			TechnicsRouteJPanel.this.requestFocusInWindow();
			TechnicsRouteJPanel.this.pressX = e.getX();
			TechnicsRouteJPanel.this.pressY = e.getY();
			if (TechnicsRouteJPanel.this.mouseMode == 1) {
				if (e.isShiftDown()) {
					TechnicsRouteJPanel.this.tempUnit = null;
					TechnicsRouteJPanel.this.multiSelectedVector.removeAllElements();
					TechnicsRouteUtil.cleanupSelectedState(TechnicsRouteJPanel.this.drawingUnits, false);
					TechnicsRouteUtil.cleanupLinkedState(TechnicsRouteJPanel.this.drawingUnits);
					TechnicsRouteJPanel.this.lastX = TechnicsRouteJPanel.this.pressX;
					TechnicsRouteJPanel.this.lastY = TechnicsRouteJPanel.this.pressY;
					TechnicsRouteJPanel.this.isEnclosed = true;
					TechnicsRouteJPanel.this.repaint();
					return;
				}
				TechnicsRouteUnit unit = TechnicsRouteUtil.getOnlySelectedUnit(TechnicsRouteJPanel.this.drawingUnits, TechnicsRouteJPanel.this.pressX, TechnicsRouteJPanel.this.pressY);
				if (unit == null) {
					if (TechnicsRouteJPanel.this.tempUnit != null)
						TechnicsRouteJPanel.this.tempUnit = null;
					TechnicsRouteJPanel.this.multiSelectedVector.removeAllElements();
					TechnicsRouteUtil.cleanupSelectedState(TechnicsRouteJPanel.this.drawingUnits, false);
					TechnicsRouteUtil.cleanupLinkedState(TechnicsRouteJPanel.this.drawingUnits);
					TechnicsRouteJPanel.this.repaint();
					return;
				}
				if (e.isControlDown()) {
					if ((unit instanceof ProcedureRectangleUnit)) {
						if (TechnicsRouteJPanel.this.multiSelectedVector.size() == 0) {
							TechnicsRouteUtil.cleanupSelectedState(TechnicsRouteJPanel.this.drawingUnits, false);
							TechnicsRouteUtil.cleanupLinkedState(TechnicsRouteJPanel.this.drawingUnits);
						}
						if ((TechnicsRouteJPanel.this.tempUnit != null) && ((TechnicsRouteJPanel.this.tempUnit instanceof ProcedureRectangleUnit))) {
							if (!TechnicsRouteJPanel.this.multiSelectedVector.contains((ProcedureRectangleUnit) TechnicsRouteJPanel.this.tempUnit))
								TechnicsRouteJPanel.this.multiSelectedVector.add((ProcedureRectangleUnit) TechnicsRouteJPanel.this.tempUnit);
						}
						boolean b = TechnicsRouteJPanel.this.multiSelectedVector.contains(unit);
						if (b) {
							TechnicsRouteJPanel.this.multiSelectedVector.remove((ProcedureRectangleUnit) unit);
							((ProcedureRectangleUnit) unit).setSelectedState(false);
							((ProcedureRectangleUnit) unit).setLineLinkedColor(0);
						} else {
							TechnicsRouteJPanel.this.multiSelectedVector.add((ProcedureRectangleUnit) unit);
						}
						for (int i = 0; i < TechnicsRouteJPanel.this.multiSelectedVector.size(); i++) {
							ProcedureRectangleUnit multiUnit = (ProcedureRectangleUnit) TechnicsRouteJPanel.this.multiSelectedVector.get(i);
							multiUnit.setSelectedState(true);
							multiUnit.setLineLinkedColor();
						}
						TechnicsRouteJPanel.this.lastX = TechnicsRouteJPanel.this.pressX;
						TechnicsRouteJPanel.this.lastY = TechnicsRouteJPanel.this.pressY;
					}
					TechnicsRouteJPanel.this.tempUnit = null;
					TechnicsRouteJPanel.this.repaint();
					return;
				}

				TechnicsRouteJPanel.this.tempUnit = unit;
				if (!TechnicsRouteJPanel.this.multiSelectedVector.contains(TechnicsRouteJPanel.this.tempUnit)) {
					TechnicsRouteUtil.cleanupSelectedState(TechnicsRouteJPanel.this.drawingUnits, false);
					TechnicsRouteUtil.cleanupLinkedState(TechnicsRouteJPanel.this.drawingUnits);
					TechnicsRouteJPanel.this.multiSelectedVector.removeAllElements();
				}
				unit.setSelectedState(true);
				TechnicsRouteJPanel.this.flex = TechnicsRouteJPanel.this.tempUnit.setMouseCursor(TechnicsRouteJPanel.this, TechnicsRouteJPanel.this.pressX, TechnicsRouteJPanel.this.pressY);

				if ((TechnicsRouteJPanel.this.tempUnit instanceof SingleLineUnit)) {
					((SingleLineUnit) TechnicsRouteJPanel.this.tempUnit).canDeleteRectangleLink = TechnicsRouteUtil.canDeleteRectangleLink(TechnicsRouteJPanel.this, (SingleLineUnit) TechnicsRouteJPanel.this.tempUnit);
					((SingleLineUnit) TechnicsRouteJPanel.this.tempUnit).judgeLinkCondition();
				} else if ((TechnicsRouteJPanel.this.tempUnit instanceof ProcedureRectangleUnit)) {
					((ProcedureRectangleUnit) TechnicsRouteJPanel.this.tempUnit).setLineLinkedColor();
				}
				TechnicsRouteJPanel.this.lastX = TechnicsRouteJPanel.this.pressX;
				TechnicsRouteJPanel.this.lastY = TechnicsRouteJPanel.this.pressY;
				TechnicsRouteJPanel.this.repaint();
				return;
			}
			if (TechnicsRouteJPanel.this.mouseMode > 1) {
				if ((TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit != null)
						&& (TechnicsRouteJPanel.this.beginPoly != null)) {
					TechnicsRouteJPanel.this.beginMoveLine = true;
					return;
				}
				if (TechnicsRouteJPanel.this.mouseMode == 2) {
					TechnicsRouteJPanel.this.addSingleLineUnit(false);
				} else if (TechnicsRouteJPanel.this.mouseMode == 3) {
					TechnicsRouteJPanel.this.addHorizontalDoubleLineUnit(false);
				} else if (TechnicsRouteJPanel.this.mouseMode == 4) {
					TechnicsRouteJPanel.this.addVerticalDoubleLineUnit(false);
				} else if (TechnicsRouteJPanel.this.mouseMode == 5) {
					TechnicsRouteJPanel.this.addHorizontalTrebleLineUnit(false);
				} else if (TechnicsRouteJPanel.this.mouseMode == 6) {
					TechnicsRouteJPanel.this.addVerticalTrebleLineUnit(false);
				} else if (TechnicsRouteJPanel.this.mouseMode == 7) {
					TechnicsRouteJPanel.this.addSingleLineUnit(true);
				} else if (TechnicsRouteJPanel.this.mouseMode == 8) {
					TechnicsRouteJPanel.this.addHorizontalDoubleLineUnit(true);
				} else if (TechnicsRouteJPanel.this.mouseMode == 9) {
					TechnicsRouteJPanel.this.addVerticalDoubleLineUnit(true);
				} else if (TechnicsRouteJPanel.this.mouseMode == 10) {
					TechnicsRouteJPanel.this.addHorizontalTrebleLineUnit(true);
				} else if (TechnicsRouteJPanel.this.mouseMode == 11) {
					TechnicsRouteJPanel.this.addVerticalTrebleLineUnit(true);
				} else if (TechnicsRouteJPanel.this.mouseMode == 12) {
					TechnicsRouteJPanel.this.addVerticalQuintupleLineUnit(false);
				} else if (TechnicsRouteJPanel.this.mouseMode == 13) {
					TechnicsRouteJPanel.this.addVerticalQuintupleLineUnit(true);
				}
			}
			TechnicsRouteJPanel.this.multiSelectedVector.removeAllElements();
			TechnicsRouteJPanel.this.repaint();
		}

		public void mouseClicked(MouseEvent e) {
			if (!TechnicsRouteJPanel.this.eventEnabled)
				return;
			if (e.getClickCount() == 2) {
				TechnicsRouteJPanel.this.requestFocusInWindow();
				int x = e.getX();
				int y = e.getY();
				TechnicsRouteUnit unit = TechnicsRouteUtil.getOnlySelectedUnit(
						TechnicsRouteJPanel.this.drawingUnits, x, y);
				if (unit == null) {
					TechnicsRouteUtil.cleanupSelectedState(
							TechnicsRouteJPanel.this.drawingUnits, false);
					TechnicsRouteJPanel.this.repaint();
					return;
				}
				if ((unit instanceof ProcedureRectangleUnit)) {
					try {
						TechnicsRouteUtil.doubleClickProcedurerectangle(
								(ProcedureRectangleUnit) unit,
								TechnicsRouteJPanel.this);
					} catch (Exception e1) {

						e1.printStackTrace();
					}
				} else if ((unit instanceof SingleLineUnit)) {
					LinePopupMenu tmp131_128 = TechnicsRouteJPanel.this.linePopupMenu;
					tmp131_128.getClass();
					tmp131_128.new NoteJDialog((SingleLineUnit) unit);
				}

			}

			if ((e.getButton() == 3)
					&& (!TechnicsRouteJPanel.this.technicsNumber.equals(""))) {
				TechnicsRouteJPanel.this.requestFocusInWindow();
				int x = e.getX();
				int y = e.getY();
				TechnicsRouteUnit unit = TechnicsRouteUtil.getOnlySelectedUnit(
						TechnicsRouteJPanel.this.drawingUnits, x, y);
				if (unit == null) {
					TechnicsRouteJPanel.this.technicsRoutePopupMenu.show(
							TechnicsRouteJPanel.this, x, y);
				} else if ((unit instanceof SingleLineUnit)) {
					TechnicsRouteJPanel.this.linePopupMenu
							.setSingleLineUnit((SingleLineUnit) unit);
					TechnicsRouteJPanel.this.linePopupMenu.show(
							TechnicsRouteJPanel.this, x, y);
				} else if (((unit instanceof ProcedureRectangleUnit))
						&& (TechnicsRouteJPanel.this.multiSelectedVector
								.contains(unit))
						&& (TechnicsRouteJPanel.this.multiSelectedVector.size() > 1)) {
					TechnicsRouteJPanel.this.procedureRectanglePopupMenu.show(
							TechnicsRouteJPanel.this, x, y);
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (!TechnicsRouteJPanel.this.eventEnabled)
				return;
			if (TechnicsRouteJPanel.this.mouseMode == 1) {
				if (TechnicsRouteJPanel.this.isEnclosed) {
					TechnicsRouteJPanel.this.selectEnclosedRectangle(e);
				}
				if ((TechnicsRouteJPanel.this.tempUnit != null)
						&& ((TechnicsRouteJPanel.this.tempUnit instanceof SingleLineUnit))) {
					((SingleLineUnit) TechnicsRouteJPanel.this.tempUnit)
							.reSetSelectedIndex();
				}
			} else if (TechnicsRouteJPanel.this.mouseMode > 1) {
				if ((TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit != null)
						&& (TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit != null)
						&& (TechnicsRouteJPanel.this.beginPoly != null)
						&& (TechnicsRouteJPanel.this.endPoly != null)) {
					Point beginPoint = TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit
							.getCenterEnterPoint(TechnicsRouteJPanel.this.beginPoly);
					Point endPoint = TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit
							.getCenterEnterPoint(TechnicsRouteJPanel.this.endPoly);
					if (TechnicsRouteJPanel.this.mouseMode == 2) {
						TechnicsRouteUtil
								.createSingleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										false);
					} else if (TechnicsRouteJPanel.this.mouseMode == 3) {
						TechnicsRouteUtil
								.createHorizontalDoubleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										false);
					} else if (TechnicsRouteJPanel.this.mouseMode == 4) {
						TechnicsRouteUtil
								.createVerticalDoubleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										false);
					} else if (TechnicsRouteJPanel.this.mouseMode == 5) {
						TechnicsRouteUtil
								.createHorizontalTrebleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										false);
					} else if (TechnicsRouteJPanel.this.mouseMode == 6) {
						TechnicsRouteUtil
								.createVerticalTrebleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										false);
					} else if (TechnicsRouteJPanel.this.mouseMode == 7) {
						TechnicsRouteUtil
								.createSingleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										true);
					} else if (TechnicsRouteJPanel.this.mouseMode == 8) {
						TechnicsRouteUtil
								.createHorizontalDoubleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										true);
					} else if (TechnicsRouteJPanel.this.mouseMode == 9) {
						TechnicsRouteUtil
								.createVerticalDoubleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										true);
					} else if (TechnicsRouteJPanel.this.mouseMode == 10) {
						TechnicsRouteUtil
								.createHorizontalTrebleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										true);
					} else if (TechnicsRouteJPanel.this.mouseMode == 11) {
						TechnicsRouteUtil
								.createVerticalTrebleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										true);
					} else if (TechnicsRouteJPanel.this.mouseMode == 12) {
						TechnicsRouteUtil
								.createHorizontalQuintupleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										false);
					} else if (TechnicsRouteJPanel.this.mouseMode == 13) {
						TechnicsRouteUtil
								.createHorizontalQuintupleLineUnit(
										TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit,
										TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit,
										beginPoint, endPoint,
										TechnicsRouteJPanel.this.drawingUnits,
										true);
					}
					TechnicsRouteJPanel.this.repaint();
				}
				TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit = null;
				TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit = null;
				TechnicsRouteJPanel.this.beginPoly = null;
				TechnicsRouteJPanel.this.endPoly = null;
				TechnicsRouteJPanel.this.beginMoveLine = false;
				TechnicsRouteJPanel.this.mouseMode = 1;
				TechnicsRouteJPanel.this.repaint();
			}
		}
	}

	class PanelMouseMotion extends MouseMotionAdapter {
		PanelMouseMotion() {
		}

		public void mouseMoved(MouseEvent e) {
			if (!TechnicsRouteJPanel.this.eventEnabled)
				return;
			TechnicsRouteJPanel.this.moveX = e.getX();
			TechnicsRouteJPanel.this.moveY = e.getY();
			if (TechnicsRouteJPanel.this.mouseMode > 1) {
				TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit = TechnicsRouteUtil
						.getEnteredProcedureRectangleUnit(
								TechnicsRouteJPanel.this.procedureUnit,
								TechnicsRouteJPanel.this.moveX,
								TechnicsRouteJPanel.this.moveY);
				if (TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit == null) {
					TechnicsRouteJPanel.this.beginPoly = null;
					TechnicsRouteJPanel.this.repaint();
					return;
				}
				TechnicsRouteJPanel.this.beginPoly = TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit
						.getEnteredPolygon(TechnicsRouteJPanel.this.moveX,
								TechnicsRouteJPanel.this.moveY);
				if (TechnicsRouteJPanel.this.beginPoly != null) {
					Point point = TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit
							.getCenterEnterPoint(TechnicsRouteJPanel.this.beginPoly);
					TechnicsRouteJPanel.this.lineBeginX = point.x;
					TechnicsRouteJPanel.this.lineBeginY = point.y;
					TechnicsRouteJPanel.this.repaint();
				}
				return;
			}
			TechnicsRouteUnit unit = TechnicsRouteUtil.getOnlySelectedUnit(
					TechnicsRouteJPanel.this.drawingUnits,
					TechnicsRouteJPanel.this.moveX,
					TechnicsRouteJPanel.this.moveY);
			if (unit == null) {
				TechnicsRouteJPanel.this.setCursor(Cursor.getDefaultCursor());
				return;
			}
			unit.setMouseCursor(TechnicsRouteJPanel.this,
					TechnicsRouteJPanel.this.moveX,
					TechnicsRouteJPanel.this.moveY);
			if ((unit instanceof SingleLineUnit)) {
				((SingleLineUnit) unit).reSetSelectedIndex();
			}
		}

		public void mouseDragged(MouseEvent e) {
			if (!TechnicsRouteJPanel.this.eventEnabled)
				return;
			if (TechnicsRouteJPanel.this.mouseMode == 1) {
				if (e.isShiftDown()) {
					TechnicsRouteJPanel.this.releaseX = e.getX();
					TechnicsRouteJPanel.this.releaseY = e.getY();
					TechnicsRouteJPanel.this.repaint();
					return;
				}
				if (TechnicsRouteJPanel.this.multiSelectedVector.size() > 0) {
					TechnicsRouteJPanel.this.releaseX = e.getX();
					TechnicsRouteJPanel.this.releaseY = e.getY();

					if (!TechnicsRouteJPanel.this.flex) {
						boolean xMove = TechnicsRouteJPanel.this.canXMove();
						boolean yMove = TechnicsRouteJPanel.this.canYMove();
						for (int i = 0; i < TechnicsRouteJPanel.this.multiSelectedVector
								.size(); i++) {
							ProcedureRectangleUnit unit = (ProcedureRectangleUnit) TechnicsRouteJPanel.this.multiSelectedVector
									.get(i);
							if (xMove)
								unit.setX(unit.getX()
										+ TechnicsRouteJPanel.this.releaseX
										- TechnicsRouteJPanel.this.pressX);
							if (yMove)
								unit.setY(unit.getY()
										+ TechnicsRouteJPanel.this.releaseY
										- TechnicsRouteJPanel.this.pressY);
							unit.adjustLineLocation();
						}
						TechnicsRouteJPanel.this.pressX = TechnicsRouteJPanel.this.releaseX;
						TechnicsRouteJPanel.this.pressY = TechnicsRouteJPanel.this.releaseY;
					}

					TechnicsRouteJPanel.this.viewAdjusting();
					TechnicsRouteJPanel.this.repaint();
					return;
				}
				if (TechnicsRouteJPanel.this.tempUnit == null) {
					return;
				}

				TechnicsRouteJPanel.this.releaseX = e.getX();
				TechnicsRouteJPanel.this.releaseY = e.getY();

				if (!TechnicsRouteJPanel.this.flex) {
					if ((TechnicsRouteJPanel.this.tempUnit instanceof SingleLineUnit)) {
						((SingleLineUnit) TechnicsRouteJPanel.this.tempUnit).canDeleteRectangleLink = TechnicsRouteUtil
								.canDeleteRectangleLink(
										TechnicsRouteJPanel.this,
										(SingleLineUnit) TechnicsRouteJPanel.this.tempUnit);
						((SingleLineUnit) TechnicsRouteJPanel.this.tempUnit)
								.movePoint(
										TechnicsRouteJPanel.this.releaseX
												- TechnicsRouteJPanel.this.pressX,
										TechnicsRouteJPanel.this.releaseY
												- TechnicsRouteJPanel.this.pressY);
					} else {
						if (TechnicsRouteJPanel.this.tempUnit.getX()
								+ TechnicsRouteJPanel.this.releaseX
								- TechnicsRouteJPanel.this.pressX > 20)
							TechnicsRouteJPanel.this.tempUnit
									.setX(TechnicsRouteJPanel.this.tempUnit
											.getX()
											+ TechnicsRouteJPanel.this.releaseX
											- TechnicsRouteJPanel.this.pressX);
						if (TechnicsRouteJPanel.this.tempUnit.getY()
								+ TechnicsRouteJPanel.this.releaseY
								- TechnicsRouteJPanel.this.pressY > 20)
							TechnicsRouteJPanel.this.tempUnit
									.setY(TechnicsRouteJPanel.this.tempUnit
											.getY()
											+ TechnicsRouteJPanel.this.releaseY
											- TechnicsRouteJPanel.this.pressY);
						((ProcedureRectangleUnit) TechnicsRouteJPanel.this.tempUnit)
								.adjustLineLocation();
					}
					TechnicsRouteJPanel.this.pressX = TechnicsRouteJPanel.this.releaseX;
					TechnicsRouteJPanel.this.pressY = TechnicsRouteJPanel.this.releaseY;
				} else {
					boolean b = TechnicsRouteJPanel.this.tempUnit
							.flexProperties(TechnicsRouteJPanel.this,
									TechnicsRouteJPanel.this.pressX,
									TechnicsRouteJPanel.this.pressY,
									TechnicsRouteJPanel.this.releaseX,
									TechnicsRouteJPanel.this.releaseY);
					if (b) {
						TechnicsRouteJPanel.this.pressX = TechnicsRouteJPanel.this.releaseX;
						TechnicsRouteJPanel.this.pressY = TechnicsRouteJPanel.this.releaseY;
					}
					if ((TechnicsRouteJPanel.this.tempUnit instanceof ProcedureRectangleUnit)) {
						((ProcedureRectangleUnit) TechnicsRouteJPanel.this.tempUnit)
								.adjustLineLocation();
					}
				}
			} else if (TechnicsRouteJPanel.this.mouseMode > 1) {
				TechnicsRouteJPanel.this.releaseX = e.getX();
				TechnicsRouteJPanel.this.releaseY = e.getY();
				if ((TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit != null)
						&& (TechnicsRouteJPanel.this.beginPoly != null)
						&& (TechnicsRouteJPanel.this.beginMoveLine)) {
					TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit = TechnicsRouteUtil
							.getEnteredProcedureRectangleUnit(
									TechnicsRouteJPanel.this.procedureUnit,
									TechnicsRouteJPanel.this.releaseX,
									TechnicsRouteJPanel.this.releaseY);
					if (TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit == null) {
						TechnicsRouteJPanel.this.endPoly = null;
					} else if (TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit == TechnicsRouteJPanel.this.tempBeginProcedureRectangleUnit)
						TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit = null;
					else {
						TechnicsRouteJPanel.this.endPoly = TechnicsRouteJPanel.this.tempEndProcedureRectangleUnit
								.getEnteredPolygon(
										TechnicsRouteJPanel.this.releaseX,
										TechnicsRouteJPanel.this.releaseY);
					}
				}
			}
			TechnicsRouteJPanel.this.viewAdjusting();
			TechnicsRouteJPanel.this.repaint();
		}
	}

	public void createmoreImage() throws Exception {
		Document document = this.parentFrame.getCurrentTechnics();
		if (document == null)
			return;
		Element techEle = XmlUtility.getTechnicsElement(document);
		Element element = (Element) techEle.clone();
		List list = XmlUtility.getAllSteps(element);
		String technicsNumber = techEle.attributeValue("technicsNumber");
		String technicsName = techEle.attributeValue("technicsName");
		String technicsCategory = techEle.attributeValue("technicsCategory");

		if ((list == null) || (list.size() == 0)) {
			JOptionPane.showMessageDialog(this.parentFrame, "选择的工艺节点下不存在工序！", "提示", 1);
			return;
		}
		this.drawingUnits.removeAllElements();
		TechnicsRouteUtil.createmoreImage(technicsNumber, technicsName, technicsCategory, list, this);
	}
	/**
	 * 检查流程图是否编写规范
	* @author jyx
	* @date 2018-7-19
	* @return
	* @throws Exception
	 */
	public boolean checksaveTechnicsRoute() throws Exception {
		boolean flag = true;
		Vector<TechnicsRouteUnit> drawingUnits = getDrawingUnits();
		List<String> preNextList = new ArrayList<String>();
		String msg = "";
		if(drawingUnits != null){
			for (int i = 0; i < drawingUnits.size(); i++) {
				TechnicsRouteUnit unit = (TechnicsRouteUnit) drawingUnits.get(i);

				if ((unit instanceof ProcedureRectangleUnit)){
					ProcedureRectangleUnit procedureRectangleUnit = (ProcedureRectangleUnit)unit;
					if(checkPreProcedure(procedureRectangleUnit)){
						JOptionPane.showMessageDialog(null, "工艺路线保存失败！工序 "+procedureRectangleUnit.getNumber()+" 的前置工序只能选前面的工序", "提示", 1);
						return false;
					}
					Vector<SingleLineUnit> preLineVector = procedureRectangleUnit.getPreLineVector();
					Vector<SingleLineUnit> nextLineVector = procedureRectangleUnit.getNextLineVector();
					if((null == preLineVector || preLineVector.size()==0)&&(null == nextLineVector || nextLineVector.size()==0)&&drawingUnits.size() != 1){
						JOptionPane.showMessageDialog(null, "工艺路线保存失败！存在游离的工序，请修改确认后再执行保存", "提示", 1);
						return false;

					}else if(null != preLineVector && preLineVector.size()>0){
						String proceId = procedureRectangleUnit.getId();
						boolean falg = checksaveTechnicsRoute(proceId, preLineVector);
						if(falg){
							JOptionPane.showMessageDialog(null, "工艺路线保存失败！存在工序流程死循环，请修改确认后再执行保存", "提示", 1);
							return false;
						}
					}
				}else if(unit instanceof SingleLineUnit){
					SingleLineUnit lineUnit = (SingleLineUnit)unit;
					ProcedureRectangleUnit preProcedure = lineUnit.preProcedure;
					ProcedureRectangleUnit nextProcedure = lineUnit.nextProcedure;
					if(preProcedure ==null || nextProcedure == null){
						JOptionPane.showMessageDialog(null, "工艺路线保存失败！存在未完整连接工序的箭头，请修改确认后再执行保存", "提示", 1);
						return false;
					}
					String preNumber = preProcedure.getNumber();
					String nextNumber = nextProcedure.getNumber();
					String preNextStr = preNumber + "-" + nextNumber;
					if(!preNextList.contains(preNextStr)){
						preNextList.add(preNextStr);
					}else{
						msg = msg.isEmpty() ? preNextStr : msg + "," + preNextStr;
 					}
				}
			}
			if(!msg.isEmpty()){
				JOptionPane.showMessageDialog(null, "工艺路线保存失败！\r\n工序"+msg+"之间存在重复连线，请删除后重新保存", "提示", 1);
				return false;
			}
		}
		return flag;
	}
	/**
	 * 检查工序流程图是否死循环
	* @author jyx
	* @date 2018-7-19
	* @param proceId
	* @param preLineVector
	* @return
	* @throws Exception
	 */
	public boolean checksaveTechnicsRoute(String proceId,Vector<SingleLineUnit> preLineVector) throws Exception {
		for (int i = 0; i < preLineVector.size(); i++) {
			SingleLineUnit lineUnit = preLineVector.get(i);
			ProcedureRectangleUnit preProcedure = lineUnit.preProcedure;
			if(preProcedure != null){
				String preId = preProcedure.getId();
				if(proceId.equals(preId)){
					return true;
				}else{
					Vector<SingleLineUnit> preLineVectorTwo = preProcedure.getPreLineVector();
					if(preLineVectorTwo != null && preLineVectorTwo.size()>0){
						boolean falg = checksaveTechnicsRoute(proceId, preLineVectorTwo);
						if(falg){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	/**
	 * 保存流程图时，根据工艺路线设置前置工序
	* @author jyx
	* @date 2018-7-19
	* @throws Exception
	 */
	public void setQuickCreateProcedureJPanelTableByTechnicsRoute() throws Exception {
		Vector<TechnicsRouteUnit> drawingUnits = getDrawingUnits();
		if(drawingUnits != null){
			Map<String, String> preNumberMap = new HashMap<String, String>();
			Map<String, String> preIdMap = new HashMap<String, String>();
			for (int i = 0; i < drawingUnits.size(); i++) {
				TechnicsRouteUnit unit = (TechnicsRouteUnit) drawingUnits.get(i);
				if ((unit instanceof ProcedureRectangleUnit)){
					ProcedureRectangleUnit procedureRectangleUnit = (ProcedureRectangleUnit)unit;
					String procesId = procedureRectangleUnit.getId();
					//Vector<ProcedureRectangleUnit> preProcedureVector = procedureRectangleUnit.preProcedureVector;
					Vector<SingleLineUnit> preLineVector = procedureRectangleUnit.getPreLineVector();

					StringBuffer numberSb = new StringBuffer("");
					StringBuffer idSb = new StringBuffer("");
					if(null != preLineVector && preLineVector.size()>0){
						for (int j = 0; j < preLineVector.size(); j++) {
							SingleLineUnit lineUnit = (SingleLineUnit)preLineVector.get(j);
							if(lineUnit != null){
								ProcedureRectangleUnit preProcedure = lineUnit.preProcedure;
								if(preProcedure != null){
									String prNumber = preProcedure.getNumber();
									String prId = preProcedure.getId();
									if(prNumber != null && !"".equals(prNumber)){
										if("".equals(numberSb.toString())){
											numberSb.append(prNumber);
										}else{
											numberSb.append(",");
											numberSb.append(prNumber);
										}
									}
									if(prId != null && !"".equals(prId)){
										if("".equals(idSb.toString())){
											idSb.append(prId);
										}else{
											idSb.append(",");
											idSb.append(prId);
										}
									}

								}
							}
						}
					}
					preNumberMap.put(procesId, numberSb.toString());
					preIdMap.put(procesId, idSb.toString());
				}
			}
			QuickCreateProcedureJPanel quickCreateProcedureJPanel = parentFrame.getQuickCreateProcedureJPanel();
			if(quickCreateProcedureJPanel != null){
				quickCreateProcedureJPanel.updataTechnicsDataByMap(preNumberMap,preIdMap);
			}
		}
	}
	public static boolean checkPreProcedure(ProcedureRectangleUnit procedureRectangleUnit){
		String stepNumber = procedureRectangleUnit.getNumber();
		int stepNumberInt = Integer.valueOf(stepNumber);
		Vector<SingleLineUnit> singleLineUnitVector = procedureRectangleUnit.getPreLineVector();
		for(SingleLineUnit singleLineUnit : singleLineUnitVector){
			String preStepNumber = singleLineUnit.preProcedure.getNumber();
			int preStepNumberInt = Integer.valueOf(preStepNumber);
			if(preStepNumberInt > stepNumberInt){
				return true;
			}
		}
		return false;
	}
}
