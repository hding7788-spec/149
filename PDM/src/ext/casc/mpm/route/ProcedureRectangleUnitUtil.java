package ext.casc.mpm.route;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.*;


public class ProcedureRectangleUnitUtil {
    public static void main(String[] args) {
        //生成图片
        List<ProcedureRectangleUnit> items = new ArrayList<ProcedureRectangleUnit>();
        for (int i = 1; i <= 33; i++) {
            ProcedureRectangleUnit p = new ProcedureRectangleUnit();
            p.setId(i+"");
            p.setNumber(i);
            p.setName("工序名"+i);

            p.setWorkShop(null);
            p.setPreProcedureID(i+"");
            p.setNextProcedureID(i+"");
            p.setDescription("");
            items.add(p);
        }
        generateImages(items, "C:\\");
        System.out.println("生成图片成功！");

        try {
            String filePath = "C:\\111.xml"; // 使用双反斜杠
            generateXML(items, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateImages(List<ProcedureRectangleUnit> tasks, String directory) {
        int imagesCount = (int) Math.ceil(tasks.size() / 30.0);
        for (int i = 0; i < imagesCount; i++) {
            List<ProcedureRectangleUnit> subList = tasks.subList(i * 30, Math.min((i + 1) * 30, tasks.size()));
            BufferedImage image = createImage(subList);
            try {
                if(imagesCount==1){
                    ImageIO.write(image, "JPEG", new File(directory +File.separator+ "technics_route.jpg"));
                }else{
                    ImageIO.write(image, "JPEG", new File(directory +File.separator+ "technics_route_" + i + ".jpg"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static BufferedImage createImage(List<ProcedureRectangleUnit> tasks) {
        final int horizontalSpacing = 50; // 水平间距
        final int verticalSpacing = 50; // 垂直间距
        final int boxWidth = 120; // 长方形的宽度
        final int boxHeight = 80; // 长方形的高度
        final int leftPadding = 30; // 每一排左方空出的像素
        final int topPadding = 20; // 每一排顶部空出的像素
        final int lineThickness = 2; // 连接线的粗细

        int rows = (int) Math.ceil(tasks.size() / 6.0);
        BufferedImage image = new BufferedImage(6 * (boxWidth + horizontalSpacing) + leftPadding, rows * (boxHeight + verticalSpacing) + topPadding, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        g2d.setColor(Color.BLACK);
        for (int i = 0; i < tasks.size(); i++) {
            int x = leftPadding + (i % 6) * (boxWidth + horizontalSpacing);
            int y = topPadding + (i / 6) * (boxHeight + verticalSpacing); // 添加topPadding

            // 设置边框加粗
            g2d.setStroke(new BasicStroke(4));
            // 绘制加粗的长方形方框
            g2d.drawRect(x, y, boxWidth, boxHeight);
            // 恢复默认线条宽度
            g2d.setStroke(new BasicStroke(1));

            // 保存当前Graphics2D状态
            Graphics2D g2dCopy = (Graphics2D) g2d.create();

            // 设置4个小方块灰白色相间
            for (int j = 0; j < 4; j++) {
                int subBoxX = x;
                int subBoxY = y + j * boxHeight / 4;
                int subBoxWidth = boxWidth;
                int subBoxHeight = boxHeight / 4;
                Color subBoxColor = (j % 2 == 0) ? new Color(200, 200, 200) : Color.WHITE;
                g2dCopy.setColor(subBoxColor);
                g2dCopy.fillRect(subBoxX, subBoxY, subBoxWidth, subBoxHeight);
            }

            // 恢复Graphics2D状态
            g2dCopy.dispose();

            // 绘制3条横线将方框分成4块
            for (int j = 1; j <= 3; j++) {
                g2d.drawLine(x, y + j * boxHeight / 4, x + boxWidth, y + j * boxHeight / 4);
            }

            // 在每块中间设置Item.name并居中
            Font font = new Font("SimSun", Font.PLAIN, 12);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            ProcedureRectangleUnit procedureRectangleUnit = tasks.get(i);
            Integer number = procedureRectangleUnit.getNumber();
            String name = procedureRectangleUnit.getName();
            String workShop = procedureRectangleUnit.getWorkShop();
            String description = procedureRectangleUnit.getDescription();
            Map<Integer, String> map = new HashMap<Integer, String>();
            map.put(0, number.toString());
            map.put(1, name);
            map.put(2, workShop);
            map.put(3, description);

            for (int j = 0; j < 4; j++) {
                String text = map.get(j);
                int textWidth = fm.stringWidth(text);
                int textX = x + (boxWidth - textWidth) / 2; // 水平居中
                int textY = y + (j + 1) * boxHeight / 4 + (boxHeight / 8) - fm.getAscent() / 2; // 垂直居中
                g2d.drawString(text, textX, textY-6);
            }

            // Draw horizontal lines and arrows
            if (i % 6 < 5 && i < tasks.size() - 1) { // Not the last item in the row and not the last item
                int nextX = leftPadding + (i % 6 + 1) * (boxWidth + horizontalSpacing);
                int nextY = y + boxHeight / 2;
                g2d.setStroke(new BasicStroke(lineThickness)); // 设置连接线的粗细
                g2d.drawLine(x + boxWidth, y + boxHeight / 2, nextX, nextY);
                g2d.setStroke(new BasicStroke(1)); // 恢复默认线条宽度
                drawArrow(g2d, nextX, nextY, 5, 5); // Smaller arrow
            }

            // Draw vertical lines and arrows between rows
            if ((i + 1) % 6 == 0 && i + 1 < tasks.size() - 1) { // Last item in the row and not the last item
                int nextX = leftPadding + (boxWidth + horizontalSpacing) / 2; // Next row's first item x position
                int nextY = topPadding + (i / 6 + 1) * (boxHeight + verticalSpacing) + (boxHeight / 2) - 20; // 添加topPadding
                // Draw line from the right of the current box to the middle
                g2d.setStroke(new BasicStroke(lineThickness)); // 设置连接线的粗细
                g2d.drawLine(x + boxWidth, y + boxHeight / 2, x + boxWidth + horizontalSpacing / 2, y + boxHeight / 2);
                // Draw vertical line in the middle
                g2d.drawLine(x + boxWidth + horizontalSpacing / 2, y + boxHeight / 2, x + boxWidth + horizontalSpacing / 2, nextY - boxHeight / 2);
                // Draw line from the middle to the left of the next box
                g2d.setStroke(new BasicStroke(lineThickness)); // 设置连线1和连线2的粗细
                g2d.drawLine(x + boxWidth + horizontalSpacing / 2, nextY - boxHeight / 2, nextX - 110, nextY - boxHeight / 2);
                g2d.setStroke(new BasicStroke(1)); // 恢复默认线条宽度
                //连线1
                g2d.setStroke(new BasicStroke(lineThickness)); // 设置连线1的粗细
                g2d.drawLine(nextX, (nextY - boxHeight / 2) + 65, nextX - 110, (nextY - boxHeight / 2) + 65);
                //连线2
                g2d.setStroke(new BasicStroke(lineThickness)); // 设置连线2的粗细
                g2d.drawLine(nextX - 110, (y + boxHeight / 2) + 70, nextX - 110, (nextY - boxHeight / 2) + 65);
                g2d.setStroke(new BasicStroke(1)); // 恢复默认线条宽度
                // Draw two additional lines to connect the line to the arrow
                drawArrow(g2d,nextX - 88, (nextY - boxHeight / 2) + 65, 5, 5); // Smaller arrow
            }
        }

        g2d.dispose();
        return image;
    }

    private static void drawArrow(Graphics2D g2d, int x, int y, int dx, int dy) {
        int[] xPoints = {x, x - dx, x - dx};
        int[] yPoints = {y, y - dy, y + dy};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }


    public static void generateXML(List<ProcedureRectangleUnit> units, String filePath) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        // 确保输出文件的目录存在
        File outputFile = new File(filePath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // 创建目录
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // Root element
        Element rootElement = doc.createElement("unit");
        doc.appendChild(rootElement);

        // ProcedureUnit element
        Element procedureUnit = doc.createElement("ProcedureUnit");
        rootElement.appendChild(procedureUnit);

        Element lineUnit = doc.createElement("LineUnit");
        rootElement.appendChild(lineUnit);

        // 生成所有 ID
        for (ProcedureRectangleUnit unit : units) {
            String customID = generateCustomID();
            unit.setId(customID);
        }

        // 设置 preProcedureID 和 nextProcedureID
        for (int i = 0; i < units.size(); i++) {
            if (i > 0) { // 设置 preProcedureID
                units.get(i).setPreProcedureID(units.get(i - 1).getId());
            }
            if (i < units.size() - 1) { // 设置 nextProcedureID
                units.get(i).setNextProcedureID(units.get(i + 1).getId());
            }
        }


        // 初始化起始值
        int xStart = 50;
        int yStart = 40;
        int xIncrement = 190;
        int yIncrement = 150;
        int nodesPerRow = 6; // 每行节点数

        // 计算总行数
        int numRows = (units.size() + nodesPerRow - 1) / nodesPerRow;

        // 生成x和y值
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < nodesPerRow; column++) {
                int x = xStart + column * xIncrement;
                int y = yStart + row * yIncrement;
                int nodeIndex = row * nodesPerRow + column;
                if (nodeIndex < units.size()) {
                    units.get(nodeIndex).setX(x);
                    units.get(nodeIndex).setY(y);
                }
            }
        }

        // 添加元素到 XML
        for (int i = 0; i < units.size(); i++) {
        	ProcedureRectangleUnit unit = units.get(i);
            Element procedureRectangleUnit = doc.createElement("com.glaway.mpm.flowchart.ProcedureRectangleUnit");
            procedureUnit.appendChild(procedureRectangleUnit);
            // Add child elements
            addElement(doc, procedureRectangleUnit, "id", unit.getId());
            addElement(doc, procedureRectangleUnit, "x", String.valueOf(unit.getX()));
            addElement(doc, procedureRectangleUnit, "y", String.valueOf(unit.getY()));
            addElement(doc, procedureRectangleUnit, "width", "100");
            addElement(doc, procedureRectangleUnit, "height", "90");
            addElement(doc, procedureRectangleUnit, "number", String.valueOf(unit.getNumber()));
            addElement(doc, procedureRectangleUnit, "name", unit.getName());
            addElement(doc, procedureRectangleUnit, "workShop", unit.getWorkShop());
            addElement(doc, procedureRectangleUnit, "type", unit.getType());
            addElement(doc, procedureRectangleUnit, "description", unit.getDescription());
            addElement(doc, procedureRectangleUnit, "isKey", String.valueOf(unit.isKey()));
            addElement(doc, procedureRectangleUnit, "preProcedureID", unit.getPreProcedureID());
            addElement(doc, procedureRectangleUnit, "nextProcedureID", unit.getNextProcedureID());
        }


     // 添加 SingleLineUnit 元素到 XML
        int lineXStart1 = 150; // 第一个起始x坐标
        int lineXStart2 = 240; // 第二个起始x坐标
        int lineXIncrement = 190; // x坐标增量
        int lineYStart = 85;  // 起始y坐标
        int lineYIncrement = 150; // y坐标增量
        int lineNodesPerRow = 5; // 每行节点数

        for (int i = 0; i < units.size() - 1; i++) {
            ProcedureRectangleUnit currentUnit = units.get(i);
            ProcedureRectangleUnit nextUnit = units.get(i + 1);

            Element singleLineUnit = doc.createElement("com.glaway.mpm.flowchart.SingleLineUnit");
            lineUnit.appendChild(singleLineUnit);

            // 计算当前行数和列数
            int row = i / (lineNodesPerRow + 1); // 当前行数
            int col = i % (lineNodesPerRow + 1); // 当前列数

            if (col == lineNodesPerRow) {
                // 五线单元的坐标规则
                int quintupleX1 = 1100;
                int quintupleX2 = 1145;
                int quintupleX3 = 1145;
                int quintupleX4 = 5;
                int quintupleX5 = 5;
                int quintupleX6 = 50;

                int quintupleY1 = lineYStart + row * lineYIncrement;
                int quintupleY2 = quintupleY1;
                int quintupleY3 = quintupleY1 + lineYIncrement / 2;
                int quintupleY4 = quintupleY3;
                int quintupleY5 = quintupleY1 + lineYIncrement;
                int quintupleY6 = quintupleY5;

                // 生成 x 和 y 坐标字符串
                String quintupleXCoords = quintupleX1 + "," + quintupleX2 + "," + quintupleX3 + "," + quintupleX4 + "," + quintupleX5 + "," + quintupleX6;
                String quintupleYCoords = quintupleY1 + "," + quintupleY2 + "," + quintupleY3 + "," + quintupleY4 + "," + quintupleY5 + "," + quintupleY6;

                addElement(doc, singleLineUnit, "x", quintupleXCoords);
                addElement(doc, singleLineUnit, "y", quintupleYCoords);
            } else {
            	 // 单线单元的坐标规则
                int[] fixedXValues = {150, 340, 530, 720, 910};
                int[] fixedYValues = {240, 430, 620, 810, 1000};
                int lineX1 = fixedXValues[col];
                int lineX2 = lineX1 + 190;
                int lineX3 = fixedYValues[col];

                int lineY1 = lineYStart + row * lineYIncrement;
                int lineY2 = lineY1;

                // 生成 x 和 y 坐标字符串
                String lineXCoords = lineX1 + "," + lineX3;
                String lineYCoords = lineY1 + "," + lineY2;

                addElement(doc, singleLineUnit, "x", lineXCoords);
                addElement(doc, singleLineUnit, "y", lineYCoords);
            }

            addElement(doc, singleLineUnit, "preProcedure", currentUnit.getId());
            addElement(doc, singleLineUnit, "nextProcedure", nextUnit.getId());
            addElement(doc, singleLineUnit, "head", "west");
            addElement(doc, singleLineUnit, "tail", "east");
            addElement(doc, singleLineUnit, "note", "");
            addElement(doc, singleLineUnit, "isBidirectional", "false");
        }

        // Transform the DOM Object to an XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        transformer.setOutputProperty(OutputKeys.ENCODING, "GBK");

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outputFile);
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

        System.out.println("XML file generated successfully at: " + outputFile.getAbsolutePath());
    }

    private static void addElement(Document doc, Element parent, String tagName, String value) {
        Element element = doc.createElement(tagName);
        // 始终添加一个文本节点，即使值为空或null，也添加一个空字符串
        element.appendChild(doc.createTextNode(value != null ? value : ""));
        parent.appendChild(element);
    }

    public static String generateCustomID() {
    	Random random = new Random();

        // 第一部分：负的随机整数
        int firstPart = -random.nextInt(Integer.MAX_VALUE);

        // 第二部分：随机的十六进制字符串（16位）
        long secondPartLong = random.nextLong();
        String secondPart = Long.toHexString(secondPartLong);
        // 确保第二部分为16位，不足部分前面补0
        secondPart = String.format("%16s", secondPart).replace(' ', '0');

        // 第三部分：随机的十六进制字符串的后4位
        long thirdPartLong = random.nextLong();
        String thirdPart = Long.toHexString(thirdPartLong);
        // 确保第三部分为4位，不足部分前面补0
        thirdPart = String.format("%4s", thirdPart).replace(' ', '0');

        // 组合三部分生成最终的ID
        String customID = "-" + firstPart + ":" + secondPart + ":-" + thirdPart;
        return customID;
    }
}
