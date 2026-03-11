package com.glaway.mpm.flowchart;

import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class ProcedureRectangleUnit extends TechnicsRouteUnit {
    private String number;
    private String name;
    private String workShop;
    private String type;
    private String description;
    private String id;
    private String isKey;
    private Element element;
    protected static final int CELL_HEIGHT = 18;
    protected static final int EGO_WIDTH = 100;
    protected static final int EGO_HEIGHT = 110;
    protected Vector<ProcedureRectangleUnit> preProcedureVector = new Vector();
    protected Vector<ProcedureRectangleUnit> nextProcedureVector = new Vector();
    protected Vector<SingleLineUnit> preLineVector = new Vector();
    protected Vector<SingleLineUnit> nextLineVector = new Vector();
    protected String preProcedureID;
    protected String nextProcedureID;
    private String fontName = "Dialog";

    private int fontSize = 12;

    private Vector descriptionVector = new Vector();

    public ProcedureRectangleUnit() {
    }

    public ProcedureRectangleUnit(int x, int y, Element element, String technicsName) {
        setX(x);
        setY(y);
        setWidth(100);
        this.element = element;
        TechnicsRouteUtil.setProcedureRectangleUnitAttribute(element, this, technicsName);
    }

    protected void setProcedureRectangleHeight(Graphics g) {
        if (g == null) {
            return;
        }
        this.descriptionVector.removeAllElements();
        StringTokenizer token = new StringTokenizer(this.description, "@#$");
        Vector spitString = new Vector();
        while (token.hasMoreTokens()) {
            spitString.add(token.nextToken("@#$"));
        }
        int stringHeight = g.getFontMetrics().getHeight();
        for (int i = 0; i < spitString.size(); i++) {
            String s = (String) spitString.get(i);
            int begin = 0;
            for (int end = begin + 1; end <= s.length(); end++) {
                int tempStringWidth = g.getFontMetrics().stringWidth(
                        s.substring(begin, end));
                if (tempStringWidth > getWidth()) {
                    this.descriptionVector.add(s.substring(begin, end - 2));
                    begin = end - 2;
                }
            }
            if (begin != s.length())
                this.descriptionVector.add(s.substring(begin, s.length()));
        }
        if (this.descriptionVector.size() > 0)
            setHeight(72 + stringHeight * this.descriptionVector.size());
        else
            setHeight(72 + stringHeight);
    }

    public void drawSelf(Graphics g) {
        setProcedureRectangleHeight(g);
        drawBackGround(g);
        drawBorder(g);
        drawStringValue(g);

        if (isSelectedState()) {
            drawSelfSelectedState(g);
        }
    }

    /**
     * 为每个工序画一个图框
     *
     * @param g
     */
    private void drawBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if ((this.isKey != null) && (this.isKey.equals("true")))
            g.setColor(Color.red);
        Stroke old = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2.0F));//画笔，即是线的粗细
        int x1 = getX() + getWidth();
        int y1 = getY() + 18;
        int y2 = getY() + 36;
        int y3 = getY() + 54;
        int y4 = getY() + 72;
        //int y5 = getY() + getHeight();

        g2d.drawLine(getX(), getY(), getX(), y4);//左竖线
        g2d.drawLine(x1, getY(), x1, y4);//右竖线
        g2d.drawLine(getX(), getY(), x1, getY());//从上往下，第1条横线，即是最上边的横线
        g2d.drawLine(getX(), y4, x1, y4);//从上往下，第6条横线，即是最下边的横线
        g2d.setStroke(old);
        g2d.setColor(Color.black);
        g2d.drawLine(getX(), y1, x1, y1);//从上往下，第2条横线
        g2d.drawLine(getX(), y2, x1, y2);//从上往下，第3条横线
        g2d.drawLine(getX(), y3, x1, y3);//从上往下，第4条横线
        g2d.drawLine(getX(), y4, x1, y4);//从上往下，第5条横线
    }

    protected void drawSelfSelectedState(Graphics g) {
        g.fillPolygon(getPointRectangle(getX(), getY(), getSelectedRectangleSideWidth()));
        g.fillPolygon(getPointRectangle(getX() + getWidth(), getY(), getSelectedRectangleSideWidth()));
        g.fillPolygon(getPointRectangle(getX(), getY() + getHeight(), getSelectedRectangleSideWidth()));
        g.fillPolygon(getPointRectangle(getX() + getWidth(), getY() + getHeight(), getSelectedRectangleSideWidth()));
    }

    /**
     * 填充颜色
     *
     * @param g
     */
    private void drawBackGround(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(getX(), getY(), getWidth(), 18);
        g.fillRect(getX(), getY() + 36, getWidth(), 18);
        //g.fillRect(getX(), getY() + 72, getWidth(), getHeight() - 72);
        g.setColor(Color.white);
        g.fillRect(getX(), getY() + 18, getWidth(), 18);
        g.fillRect(getX(), getY() + 54, getWidth(), 18);
        g.setColor(Color.black);
    }

    /**
     * 向图框中写入信息
     *
     * @param g
     */
    private void drawStringValue(Graphics g) {
        Font font = new Font(this.fontName, 0, this.fontSize);
        g.setFont(font);
        drawOneStringValue(g, getY(), this.number);
        if ("true".equals(isKey)) {
            drawOneStringValue(g, getY() + 18, this.name + " G");
        } else {
            drawOneStringValue(g, getY() + 18, this.name);
        }

        drawOneStringValue(g, getY() + 36, this.workShop);
        drawOneStringValue(g, getY() + 54, this.type);
        int stringHeight = g.getFontMetrics().getHeight();
		/*for (int i = 0; i < this.descriptionVector.size(); i++) {
			drawOneStringValue(g, getY() + 72 + stringHeight * i, this.descriptionVector.get(i).toString());
		}*/
    }

    private void drawOneStringValue(Graphics g, int y, String str) {
        if ((str == null) || (str.equals(""))) {
            return;
        }

        drawStringNoOverWidth(g, str, y);
    }

    private void drawStringNoOverWidth(Graphics g, String str, int yPoint) {
        double stringWidth = g.getFontMetrics().stringWidth(str);
        int height = g.getFontMetrics().getHeight();
        int asc = g.getFontMetrics().getAscent();
        double desc = g.getFontMetrics().getDescent();
        int x = getX();
        int y = (int) (asc + yPoint + (18 - asc - desc) / 2.0D);
        x = (int) (x + (getWidth() - stringWidth) / 2.0D);

        y = getYVMiddle(asc, yPoint, 18, height, g.getFontMetrics().getLeading());

        g.drawString(str, x, y);
    }

    private void drawStringOverWidthAndNoNewLine(Graphics g, String str,
                                                 int yPoint) {
        double stringWidth = g.getFontMetrics().stringWidth(str);
        double scale = getCompressScale(getWidth(), stringWidth);
        int height = g.getFontMetrics().getHeight();
        AffineTransform affTrans = new AffineTransform();

        affTrans.scale(scale, 1.0D);
        Font tempFont = g.getFont().deriveFont(affTrans);
        g.setFont(tempFont);

        int x = Math.round(getX());
        int asc = g.getFontMetrics().getAscent();
        double desc = g.getFontMetrics().getDescent();
        int y = (int) (asc + yPoint + (18 - asc - desc) / 2.0D);
        y = getYVMiddle(asc, yPoint, 18, height, g.getFontMetrics()
                .getLeading());

        g.drawString(str, x, y);

        Font font = new Font(this.fontName, 0, this.fontSize);
        g.setFont(font);
    }

    private int getYVMiddle(int fontAscent, int y, int cellHeigh,
                            int fontHeight, int fontLeading) {
        if (cellHeigh - fontHeight - fontLeading < 0) {
            return fontAscent + y;
        }
        return fontAscent + y + (cellHeigh - fontHeight - fontLeading) / 2;
    }

    private double getCompressScale(double cellWidth, double stringWidth) {
        double scale = 0.0D;
        scale = (cellWidth - 5.0D) / stringWidth;
        return scale;
    }

    public int getStringWidth(JComponent component, String str) {
        Font font = new Font(getFontName(), 0, getFontSize());
        FontMetrics fm = component.getFontMetrics(font);
        return fm.stringWidth(str);
    }

    public void setLineLinkedColor(int linkCount) {
        for (int i = 0; i < this.preLineVector.size(); i++) {
            SingleLineUnit unit = (SingleLineUnit) this.preLineVector.get(i);
            unit.setLink(linkCount);
        }
        for (int j = 0; j < this.nextLineVector.size(); j++) {
            SingleLineUnit unit = (SingleLineUnit) this.nextLineVector.get(j);
            unit.setLink(linkCount);
        }
    }

    public void setLineLinkedColor() {
        for (int i = 0; i < this.preLineVector.size(); i++) {
            SingleLineUnit unit = (SingleLineUnit) this.preLineVector.get(i);
            if ((unit.preProcedure != null) && (unit.nextProcedure != null))
                unit.setLink(2);
            else if ((unit.preProcedure == null)
                    && (unit.nextProcedure == null))
                unit.setLink(0);
            else
                unit.setLink(1);
        }
        for (int j = 0; j < this.nextLineVector.size(); j++) {
            SingleLineUnit unit = (SingleLineUnit) this.nextLineVector.get(j);
            if ((unit.preProcedure != null) && (unit.nextProcedure != null))
                unit.setLink(2);
            else if ((unit.preProcedure == null)
                    && (unit.nextProcedure == null))
                unit.setLink(0);
            else
                unit.setLink(1);
        }
    }

    public boolean setMouseCursor(TechnicsRouteJPanel panel, int x, int y) {
        int spacing = getSelectedRectangleSideWidth();
        int X = getX();
        int Y = getY();
        int width = getWidth();
        int height = getHeight();
        if ((getPointRectangle(X, Y, spacing).contains(x, y))
                || (getPointRectangle(X + width, Y + height, spacing).contains(
                x, y))) {
            panel.setCursor(Cursor.getPredefinedCursor(6));
        } else if ((getPointRectangle(X + width, Y, spacing).contains(x, y))
                || (getPointRectangle(X, Y + height, spacing).contains(x, y))) {
            panel.setCursor(Cursor.getPredefinedCursor(7));
        } else if ((getRectangleWithoutCorner(X, Y, X + width, Y, spacing)
                .contains(x, y))
                || (getRectangleWithoutCorner(X, Y + height, X + width, Y
                + height, spacing).contains(x, y))) {
            panel.setCursor(Cursor.getPredefinedCursor(9));
        } else if ((getRectangleWithoutCorner(X, Y, X, Y + height, spacing)
                .contains(x, y))
                || (getRectangleWithoutCorner(X + width, Y, X + width, Y
                + height, spacing).contains(x, y))) {
            panel.setCursor(Cursor.getPredefinedCursor(11));
        } else {
            panel.setCursor(Cursor.getDefaultCursor());
            return false;
        }
        return true;
    }

    public boolean flexProperties(TechnicsRouteJPanel panel, int x1, int y1,
                                  int x2, int y2) {
        int margin = 20;
        int X = getX();
        int Y = getY();
        int spacing = getSelectedRectangleSideWidth();
        int width = getWidth();
        int height = getHeight();

        if (getPointRectangle(X, Y, spacing).contains(x1, y1)) {
            if ((x2 <= margin) || (y2 <= margin))
                return false;
            int newWidth = getX() - x2 + getWidth();
            int newHeight = getY() - y2 + getHeight();
            if ((newWidth >= getMinimumWidth())
                    && (newHeight >= getMinimumHeight())) {
                setWidth(newWidth);
                setHeight(newHeight);
                setX(x2);
                setY(y2);
                return true;
            }
            return false;
        }

        if (getRectangleWithoutCorner(X, Y, X + width, Y, spacing).contains(x1,
                y1)) {
            if (y2 <= margin)
                return false;
            int newHeight = getY() - y2 + getHeight();
            if (newHeight >= getMinimumHeight()) {
                setHeight(newHeight);
                setY(y2);
                return true;
            }
            return false;
        }

        if (getPointRectangle(X + width, Y, spacing).contains(x1, y1)) {
            if (y2 <= margin)
                return false;
            int newWidth = x2 - getX();
            int newHeight = getY() - y2 + getHeight();
            if ((newWidth >= getMinimumWidth())
                    && (newHeight >= getMinimumHeight())) {
                setWidth(newWidth);
                setHeight(newHeight);
                setY(y2);
                return true;
            }
            return false;
        }

        if (getRectangleWithoutCorner(X, Y, X, Y + height, spacing).contains(
                x1, y1)) {
            if (x2 <= margin)
                return false;
            int newWidth = getX() - x2 + getWidth();
            if (newWidth >= getMinimumWidth()) {
                setWidth(newWidth);
                setX(x2);
                return true;
            }
            return false;
        }

        if (getRectangleWithoutCorner(X + width, Y, X + width, Y + height,
                spacing).contains(x1, y1)) {
            int newWidth = x2 - getX();
            if (newWidth >= getMinimumWidth()) {
                setWidth(newWidth);
                return true;
            }
            return false;
        }

        if (getPointRectangle(X, Y + height, spacing).contains(x1, y1)) {
            if (x2 <= margin)
                return false;
            int newWidth = getX() - x2 + getWidth();
            int newHeight = y2 - getY();
            if ((newWidth >= getMinimumWidth())
                    && (newHeight >= getMinimumHeight())) {
                setWidth(newWidth);
                setHeight(newHeight);
                setX(x2);
                return true;
            }
            return false;
        }

        if (getRectangleWithoutCorner(X, Y + height, X + width, Y + height,
                spacing).contains(x1, y1)) {
            int newHeight = y2 - getY();
            if (newHeight >= getMinimumHeight()) {
                setHeight(newHeight);
                return true;
            }
            return false;
        }

        if (getPointRectangle(X + width, Y + height, spacing).contains(x1, y1)) {
            int newWidth = x2 - getX();
            int newHeight = y2 - getY();
            if ((newWidth >= getMinimumWidth())
                    && (newHeight >= getMinimumHeight())) {
                setWidth(newWidth);
                setHeight(newHeight);
                return true;
            }
            return false;
        }
        return false;
    }

    protected boolean onePointEntered(int x, int y) {
        int X = getX();
        int Y = getY();
        int width = getWidth();
        int height = getHeight();
        int spacing = (int) (getSelectedRectangleSideWidth() * 1.5D);

        if (getPointRectangle(X + width / 2, Y, spacing).contains(x, y)) {
            return true;
        }
        if (getPointRectangle(X, Y + height / 2, spacing).contains(x, y)) {
            return true;
        }
        if (getPointRectangle(X + width, Y + height / 2, spacing)
                .contains(x, y)) {
            return true;
        }
        if (getPointRectangle(X + width / 2, Y + height, spacing)
                .contains(x, y))
            return true;
        return false;
    }

    protected Polygon getEnteredPolygon(int x, int y) {
        int X = getX();
        int Y = getY();
        int width = getWidth();
        int height = getHeight();
        int spacing = (int) (getSelectedRectangleSideWidth() * 1.5D);

        if (getPointRectangle(X + width / 2, Y, spacing).contains(x, y)) {
            return getPointRectangle(X + width / 2, Y, spacing);
        }
        if (getPointRectangle(X, Y + height / 2, spacing).contains(x, y)) {
            return getPointRectangle(X, Y + height / 2, spacing);
        }
        if (getPointRectangle(X + width, Y + height / 2, spacing)
                .contains(x, y)) {
            return getPointRectangle(X + width, Y + height / 2, spacing);
        }
        if (getPointRectangle(X + width / 2, Y + height, spacing)
                .contains(x, y))
            return getPointRectangle(X + width / 2, Y + height, spacing);
        return null;
    }

    protected Point getCenterEnterPoint(Polygon polygon) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        if (polygon.contains(x + width / 2, y))
            return new Point(x + width / 2, y);
        if (polygon.contains(x, y + height / 2))
            return new Point(x, y + height / 2);
        if (polygon.contains(x + width, y + height / 2))
            return new Point(x + width, y + height / 2);
        if (polygon.contains(x + width / 2, y + height))
            return new Point(x + width / 2, y + height);
        return null;
    }

    public void adjustLineLocation() {
        int x = 0;
        int y = 0;
        for (int i = 0; i < this.preLineVector.size(); i++) {
            SingleLineUnit unit = (SingleLineUnit) this.preLineVector.get(i);
            String s1 = unit.head;
            if (!s1.equals("")) {
                if (s1.equals("north")) {
                    x = getX() + getWidth() / 2;
                    y = getY();
                } else if (s1.equals("west")) {
                    x = getX();
                    y = getY() + getHeight() / 2;
                } else if (s1.equals("east")) {
                    x = getX() + getWidth();
                    y = getY() + getHeight() / 2;
                } else if (s1.equals("south")) {
                    x = getX() + getWidth() / 2;
                    y = getY() + getHeight();
                }
                unit.moveToProcedure(x, y, true);
            }
        }
        for (int j = 0; j < this.nextLineVector.size(); j++) {
            SingleLineUnit unit = (SingleLineUnit) this.nextLineVector.get(j);
            String s2 = unit.tail;
            if (!s2.equals("")) {
                if (s2.equals("north")) {
                    x = getX() + getWidth() / 2;
                    y = getY();
                } else if (s2.equals("west")) {
                    x = getX();
                    y = getY() + getHeight() / 2;
                } else if (s2.equals("east")) {
                    x = getX() + getWidth();
                    y = getY() + getHeight() / 2;
                } else if (s2.equals("south")) {
                    x = getX() + getWidth() / 2;
                    y = getY() + getHeight();
                }
                unit.moveToProcedure(x, y, false);
            }
        }
    }

    protected String getDirection(Point point) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        if ((point.x == x + width / 2) && (point.y == y))
            return "north";
        if ((point.x == x) && (point.y == y + height / 2))
            return "west";
        if ((point.x == x + width) && (point.y == y + height / 2))
            return "east";
        if ((point.x == x + width / 2) && (point.y == y + height))
            return "south";
        return null;
    }

    public void addElementNode(Element element) {
        Element temp = element.addElement("id");
        temp.addText(this.id);

        temp = element.addElement("x");
        temp.addText(getX() + "");

        temp = element.addElement("y");
        temp.addText(getY() + "");

        temp = element.addElement("width");
        temp.addText(getWidth() + "");

        temp = element.addElement("height");
        temp.addText(getHeight() + "");

        temp = element.addElement("number");
        temp.addText(this.number.trim());

        temp = element.addElement("name");
        temp.addText(this.name.trim());

        temp = element.addElement("workShop");
        temp.addText(this.workShop.trim());

        temp = element.addElement("type");
        temp.addText(this.type.trim());

        temp = element.addElement("description");
        temp.addText(this.description.trim());

        temp = element.addElement("isKey");
        temp.addText(this.isKey.trim());

        temp = element.addElement("preProcedureID");

        temp.addText(getPreProcedureID());

        temp = element.addElement("nextProcedureID");

        temp.addText(getNextProcedureID());
    }

    protected String getPreProcedureID() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.preProcedureVector.size(); i++) {
            ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.preProcedureVector
                    .get(i);
            sb.append(unit.getId());
            if (i != this.preProcedureVector.size() - 1)
                sb.append(",");
        }
        return sb.toString();
    }

    protected String getPreProcedureName() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.preProcedureVector.size(); i++) {
            ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.preProcedureVector
                    .get(i);
            sb.append(unit.getName());
            if (i != this.preProcedureVector.size() - 1)
                sb.append(",");
        }
        return sb.toString();
    }

    protected String getPreProcedureNumber() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.preProcedureVector.size(); i++) {
            ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.preProcedureVector
                    .get(i);
            sb.append(unit.getNumber());
            if (i != this.preProcedureVector.size() - 1)
                sb.append(",");
        }
        return sb.toString();
    }

    protected String getNextProcedureID() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.nextProcedureVector.size(); i++) {
            ProcedureRectangleUnit unit = (ProcedureRectangleUnit) this.nextProcedureVector
                    .get(i);
            sb.append(unit.getId());
            if (i != this.nextProcedureVector.size() - 1)
                sb.append(",");
        }
        return sb.toString();
    }

    public void setAttribute(Element element) {
        setId(element.element("id").getText());
        setX(Integer.parseInt(element.element("x").getText()));
        setY(Integer.parseInt(element.element("y").getText()));
        setWidth(Integer.parseInt(element.element("width").getText()));
        setHeight(Integer.parseInt(element.element("height").getText()));
        this.name = element.element("name").getText();
        this.number = element.element("number").getText();
        this.workShop = element.element("workShop").getText();
        this.type = element.element("type").getText();
        this.description = element.element("description").getText();
        if (element.element("isKey") != null)
            this.isKey = element.element("isKey").getText();
        else
            this.isKey = "false";
        this.preProcedureID = element.element("preProcedureID").getText();
        this.nextProcedureID = element.element("nextProcedureID").getText();
    }

    protected void setLinkProcedure(Vector<ProcedureRectangleUnit> vector) {
        String[] s1 = this.preProcedureID.split(",");
        String[] s2 = this.nextProcedureID.split(",");
        if ((this.preProcedureID != null) || (!this.preProcedureID.equals(""))) {
            for (int i = 0; i < s1.length; i++) {
                for (int j = 0; j < vector.size(); j++) {
                    ProcedureRectangleUnit unit = (ProcedureRectangleUnit) vector
                            .get(j);
                    if (unit.getId().equals(s1[i])) {
                        this.preProcedureVector.add(unit);
                        break;
                    }
                }
            }
        }
        if ((this.nextProcedureID != null)
                || (!this.nextProcedureID.equals(""))) {
            for (int i = 0; i < s2.length; i++) {
                for (int j = 0; j < vector.size(); j++) {
                    ProcedureRectangleUnit unit = (ProcedureRectangleUnit) vector
                            .get(j);
                    if (unit.getId().equals(s2[i])) {
                        this.nextProcedureVector.add(unit);
                        break;
                    }
                }
            }
        }
    }

    protected void setTypeAttribute(Element element) {
        StringBuffer sb = new StringBuffer(element.attributeValue("workType"));
        List list = element.element("paces").elements();
        if ((list == null) || (list.size() == 0)) {
            this.type = sb.toString();
        } else {
            Vector vector = new Vector();
            for (int i = 0; i < list.size(); i++) {
                Element e = (Element) list.get(i);
                String s = e.attributeValue("workType");
                if ((s != null) && (!vector.contains(s)))
                    vector.add(s);
            }
            sb.append("(");
            for (int i = 0; i < vector.size(); i++) {
                sb.append(vector.get(i));
                if (i != vector.size() - 1)
                    sb.append(",");
            }
            sb.append(")");
            this.type = sb.toString();
        }
    }

    protected String getPreProcedureID2(ProcedureRectangleUnit procedureRectangleUnit) {
        String preId = "";
        Vector<SingleLineUnit> singleLineUnitVector = procedureRectangleUnit.getPreLineVector();
        for (SingleLineUnit singleLineUnit : singleLineUnitVector) {
            if ("".equals(preId)) {
                preId = singleLineUnit.preProcedure.id;
            } else {
                preId = preId + "," + singleLineUnit.preProcedure.id;
            }
        }
        return preId;
    }

    protected String getNextProcedureID2(ProcedureRectangleUnit procedureRectangleUnit) {
        String preId = "";
        Vector<SingleLineUnit> singleLineUnitVector = procedureRectangleUnit.getNextLineVector();
        for (SingleLineUnit singleLineUnit : singleLineUnitVector) {
            if(singleLineUnit.nextProcedure != null){
                if ("".equals(preId)) {
                    preId = singleLineUnit.nextProcedure.id;
                } else {

                    preId = preId + "," + singleLineUnit.nextProcedure.id;
                }
            }
        }
        return preId;
    }

    protected String getPreStepName(ProcedureRectangleUnit procedureRectangleUnit) {
        String preStep = "";
        Vector<SingleLineUnit> singleLineUnitVector = procedureRectangleUnit.getPreLineVector();
        for (SingleLineUnit singleLineUnit : singleLineUnitVector) {
            if ("".equals(preStep)) {
                preStep = singleLineUnit.preProcedure.number + "_" + singleLineUnit.preProcedure.name;
            } else {
                preStep = preStep + "," + singleLineUnit.preProcedure.number + "_" + singleLineUnit.preProcedure.name;
            }
        }
        return preStep;
    }

    public String getFontName() {
        return this.fontName;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String s) {
        this.number = s;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
    }

    public void setWorkShop(String workShop) {
        this.workShop = workShop;
    }

    public String getWorkShop() {
        return this.workShop;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String s) {
        this.type = s;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public Vector<ProcedureRectangleUnit> getPreProcedureVector() {
        return this.preProcedureVector;
    }

    public Vector<ProcedureRectangleUnit> getNextProcedureVector() {
        return this.nextProcedureVector;
    }

    public Vector<SingleLineUnit> getPreLineVector() {
        return this.preLineVector;
    }

    public Vector<SingleLineUnit> getNextLineVector() {
        return this.nextLineVector;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return this.element;
    }

    public void setIsKey(String isKey) {
        this.isKey = isKey;
    }

    public String getIsKey() {
        return this.isKey;
    }
}
