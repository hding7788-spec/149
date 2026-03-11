package com.glaway.mpm.flowchart;

import com.glaway.mpm.util.ProcedurePictureCreateUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;

public class TechnicsRouteUtil {
    public static final String TECHNICS_ROUTE_XML = "technics_route.xml";
    private static final String TECHNICS_ROUTE_JPG = "technics_route.jpg";
    protected static final int HORIZONTAL_MARGIN = 50;
    protected static final int VERTICAL_MARGIN = 40;
    private static final int HORIZONTAL_INTERVAL = 90;
    private static final int VERTICAL_INTERVAL = 60;
    private static final int COUNT_PER_ROW = 5;
    protected static final String SEPARATOR = ",";

    public static TechnicsRouteUnit getOnlySelectedUnit(
            Vector<TechnicsRouteUnit> drawingUnits, int x, int y) {
        TechnicsRouteUnit unit = null;
        for (Iterator it = drawingUnits.iterator(); it.hasNext(); ) {
            unit = (TechnicsRouteUnit) it.next();
            if (unit.containMouse(x, y))
                return unit;
        }
        return null;
    }

    public static void cleanupSelectedState(
            Vector<TechnicsRouteUnit> drawingUnits, boolean b) {
        TechnicsRouteUnit unit = null;
        for (Iterator it = drawingUnits.iterator(); it.hasNext(); ) {
            unit = (TechnicsRouteUnit) it.next();
            unit.setSelectedState(b);
        }
    }

    public static void cleanupLinkedState(Vector<TechnicsRouteUnit> drawingUnits) {
        TechnicsRouteUnit unit = null;
        for (Iterator it = drawingUnits.iterator(); it.hasNext(); ) {
            unit = (TechnicsRouteUnit) it.next();
            if ((unit instanceof SingleLineUnit)) {
                ((SingleLineUnit) unit).setLink(0);
            }
        }
    }

    public static Document createDocument(Vector<TechnicsRouteUnit> drawingUnits) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("unit");
        Element procedureElemenr = root.addElement("ProcedureUnit");
        Element lineElemenr = root.addElement("LineUnit");

        for (int i = 0; i < drawingUnits.size(); i++) {
            TechnicsRouteUnit unit = (TechnicsRouteUnit) drawingUnits.get(i);
            String className = unit.getClass().getName();
            Element classUnit;
            if ((unit instanceof ProcedureRectangleUnit))
                classUnit = procedureElemenr.addElement(className);
            else
                classUnit = lineElemenr.addElement(className);
            unit.addElementNode(classUnit);
        }

        return document;
    }

    public static Vector<TechnicsRouteUnit> getUnits(Document document)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Vector drawingUnits = new Vector();
        Element root = document.getRootElement();
        Element procedureElement = root.element("ProcedureUnit");
        Element lineElement = root.element("LineUnit");

        Vector procedureUnits = new Vector();
        for (Iterator it = procedureElement.elementIterator(); it.hasNext(); ) {
            Element element = (Element) it.next();
            ProcedureRectangleUnit unit = (ProcedureRectangleUnit) Class.forName(element.getName()).newInstance();
            unit.setAttribute(element);
            drawingUnits.add(unit);
            procedureUnits.add(unit);
        }

        for (int i = 0; i < procedureUnits.size(); i++) {
            ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits.get(i);
            unit.setLinkProcedure(procedureUnits);
        }

        if(lineElement!=null){
            for (Iterator it = lineElement.elementIterator(); it.hasNext(); ) {
                Element element = (Element) it.next();
                SingleLineUnit unit = (SingleLineUnit) Class.forName(element.getName()).newInstance();
                unit.setAttribute(element);
                unit.setLinkProcedure(procedureUnits, element);
                drawingUnits.add(unit);
            }
        }




        return drawingUnits;
    }

    public static String getTechnicsNumber(Document document) throws Exception {
        Element element = XmlUtility.getTechnicsElement(document);
        return XmlUtility.getAttributeValue(element, "technicsNumber");
    }

    public static File getTechnicsRouteXML(String technicsNumber) {
        String technicsPath = WorkSpaceUtil
                .getTechnicsDirectory(technicsNumber);
        File file = new File(technicsPath + File.separator + "technics_route.xml");
        if (file.exists())
            return file;
        return null;
    }

    public static File getReworkTechnicsRouteXML(String technicsNumber,
                                                 String technicsName) {
        String technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(
                technicsNumber, technicsName);
        File file = new File(technicsPath + File.separator + "technics_route.xml");
        if (file.exists())
            return file;
        return null;
    }

    public static File getTempTechnicsRouteXML(String technicsNumber,
                                               String technicsName) {
        String technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(
                technicsNumber, technicsName);
        File file = new File(technicsPath + File.separator + "technics_route.xml");
        if (file.exists())
            return file;
        return null;
    }

    public static void firstCreateTechnicsRoute(String technicsNumber,
                                                String technicsName, String technicsCategory, List stepsList,
                                                TechnicsRouteJPanel panel) throws Exception {
        Vector drawingUnits = new Vector();
        Vector procedureUnits = new Vector();
        String path = "";
        if (technicsNumber != null && technicsName != null) {
            path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
        }

        int Size = stepsList.size() / 30;
        if (stepsList.size() > 30 && stepsList.size() % 30 != 0) {
            Size += 1;
        }
        for (int i = 0; i <= Size; i++) {
            File imgs = new File(path + "//" + "technics_route_" + i + ".jpg");
            if (imgs.exists()) {
                imgs.delete();
            }
        }
        for (int i = 0; i < stepsList.size(); i++) {
            Element element = (Element) stepsList.get(i);
            ProcedureRectangleUnit unit = new ProcedureRectangleUnit(50 + 90
                    * (i % 10) + 100 * (i % 10), 40 + 60 * (i / 10) + (i / 10) * 110,
                    element, technicsName);//50,40 240,210
            unit.setProcedureRectangleHeight(panel.getGraphics());
            drawingUnits.add(unit);
            procedureUnits.add(unit);
        }

        //int row = procedureUnits.size() / 5 + 1;
        int row = procedureUnits.size() / 10 + 1;
        //if (procedureUnits.size() % 5 == 0)
        if (procedureUnits.size() % 10 == 0)
            row--;
        int verticalBegin = 40;
        int verticalEnd = 0;
        for (int i = 1; i <= row; i++) {
            for (int j = 10 * (i - 1); (j < 10 * i)
                    && (j < procedureUnits.size()); j++) {
                ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                        .get(j);
                int end = verticalBegin + unit.getHeight();
                if (end > verticalEnd)
                    verticalEnd = end;
            }
            for (int j = 10 * (i - 1); (j < 10 * i)
                    && (j < procedureUnits.size()); j++) {
                ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                        .get(j);
                unit.setY((verticalBegin + verticalEnd - unit.getHeight()) / 2);
            }
            verticalBegin = verticalEnd + 60;
        }

        for (int i = 0; i < procedureUnits.size() - 1; i++) {
            ProcedureRectangleUnit unit1 = (ProcedureRectangleUnit) procedureUnits
                    .get(i);
            ProcedureRectangleUnit unit2 = (ProcedureRectangleUnit) procedureUnits
                    .get(i + 1);
            SingleLineUnit lineUnit = null;
            if (i % 10 != 9) {
                int[] xPoints = {unit1.getX() + unit1.getWidth(), unit2.getX()};
                int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                        unit2.getY() + unit2.getHeight() / 2};
                lineUnit = new SingleLineUnit(xPoints, yPoints, drawingUnits);
            } else {
                int maxY1 = 0;
                int minY2 = unit2.getY();
                for (int j = i - 5 + 1; j <= i; j++) {
                    ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                            .get(j);
                    if (maxY1 < unit3.getY() + unit3.getHeight())
                        maxY1 = unit3.getY() + unit3.getHeight();
                }
                for (int j = i + 1; (j <= i + 5 - 1)
                        && (j < procedureUnits.size()); j++) {
                    ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                            .get(j);
                    if (minY2 > unit3.getY())
                        minY2 = unit3.getY();
                }
                int[] xPoints = {unit1.getX() + unit1.getWidth(),
                        unit1.getX() + unit1.getWidth() + 45,
                        unit1.getX() + unit1.getWidth() + 45,
                        unit2.getX() - 45, unit2.getX() - 45, unit2.getX()};
                int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                        unit1.getY() + unit1.getHeight() / 2,
                        (maxY1 + minY2) / 2, (maxY1 + minY2) / 2,
                        unit2.getY() + unit2.getHeight() / 2,
                        unit2.getY() + unit2.getHeight() / 2};
                lineUnit = new QuintupleLineUnit(xPoints, yPoints,
                        drawingUnits, false);
            }
            lineUnit.preProcedure = unit1;
            lineUnit.nextProcedure = unit2;
            lineUnit.head = "west";
            lineUnit.tail = "east";
            unit1.nextLineVector.add(lineUnit);
            unit1.nextProcedureVector.add(unit2);
            unit2.preLineVector.add(lineUnit);
            unit2.preProcedureVector.add(unit1);
        }
        panel.setDrawingUnits(drawingUnits);
        panel.setProcedureUnits(procedureUnits);
        panel.viewAdjusting();
        panel.setTechnicsNumber(technicsNumber);
        panel.setTechnicsName(technicsName);
        panel.setTechnicsCategory(technicsCategory);
        saveTechnicsRoute(panel);
    }

    public static void firstCreateTechnicsRoute1(String technicsNumber,
                                                 String technicsName, String technicsCategory, List stepsList,
                                                 TechnicsRouteJPanel panel) throws Exception {
        Vector drawingUnits = new Vector();
        Vector procedureUnits = new Vector();
        String path = "";
        if (technicsNumber != null && technicsName != null) {
            path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
        }

        int Size = stepsList.size() / 30;
        if (stepsList.size() > 30 && stepsList.size() % 30 != 0) {
            Size += 1;
        }
        for (int i = 0; i <= Size; i++) {
            File imgs = new File(path + "//" + "technics_route_" + i + ".jpg");
            if (imgs.exists()) {
                imgs.delete();
            }
        }

        for (int i = 0; i < stepsList.size(); i++) {
            Element element = (Element) stepsList.get(i);
            ProcedureRectangleUnit unit = new ProcedureRectangleUnit(50 + 90
                    * (i % 5) + 100 * (i % 5), 40 + 60 * (i / 5) + (i / 5) * 110,
                    element, technicsName);//50,40 240,210
            unit.setProcedureRectangleHeight(panel.getGraphics());
            drawingUnits.add(unit);
            procedureUnits.add(unit);
        }

        int row = procedureUnits.size() / 5 + 1;
        if (procedureUnits.size() % 5 == 0)
            row--;
        int verticalBegin = 40;
        int verticalEnd = 0;
        for (int i = 1; i <= row; i++) {
            for (int j = 5 * (i - 1); (j < 5 * i)
                    && (j < procedureUnits.size()); j++) {
                ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                        .get(j);
                int end = verticalBegin + unit.getHeight();
                if (end > verticalEnd)
                    verticalEnd = end;
            }
            for (int j = 5 * (i - 1); (j < 5 * i)
                    && (j < procedureUnits.size()); j++) {
                ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                        .get(j);
                unit.setY((verticalBegin + verticalEnd - unit.getHeight()) / 2);
            }
            verticalBegin = verticalEnd + 60;
        }

        for (int i = 0; i < procedureUnits.size() - 1; i++) {
            ProcedureRectangleUnit unit1 = (ProcedureRectangleUnit) procedureUnits
                    .get(i);
            ProcedureRectangleUnit unit2 = (ProcedureRectangleUnit) procedureUnits
                    .get(i + 1);
            SingleLineUnit lineUnit = null;
            if (i % 5 != 4) {
                int[] xPoints = {unit1.getX() + unit1.getWidth(), unit2.getX()};
                int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                        unit2.getY() + unit2.getHeight() / 2};
                lineUnit = new SingleLineUnit(xPoints, yPoints, drawingUnits);
            } else {
                int maxY1 = 0;
                int minY2 = unit2.getY();
                for (int j = i - 5 + 1; j <= i; j++) {
                    ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                            .get(j);
                    if (maxY1 < unit3.getY() + unit3.getHeight())
                        maxY1 = unit3.getY() + unit3.getHeight();
                }
                for (int j = i + 1; (j <= i + 5 - 1)
                        && (j < procedureUnits.size()); j++) {
                    ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                            .get(j);
                    if (minY2 > unit3.getY())
                        minY2 = unit3.getY();
                }
                int[] xPoints = {unit1.getX() + unit1.getWidth(),
                        unit1.getX() + unit1.getWidth() + 45,
                        unit1.getX() + unit1.getWidth() + 45,
                        unit2.getX() - 45, unit2.getX() - 45, unit2.getX()};
                int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                        unit1.getY() + unit1.getHeight() / 2,
                        (maxY1 + minY2) / 2, (maxY1 + minY2) / 2,
                        unit2.getY() + unit2.getHeight() / 2,
                        unit2.getY() + unit2.getHeight() / 2};
                lineUnit = new QuintupleLineUnit(xPoints, yPoints,
                        drawingUnits, false);
            }
            lineUnit.preProcedure = unit1;
            lineUnit.nextProcedure = unit2;
            lineUnit.head = "west";
            lineUnit.tail = "east";
            unit1.nextLineVector.add(lineUnit);
            unit1.nextProcedureVector.add(unit2);
            unit2.preLineVector.add(lineUnit);
            unit2.preProcedureVector.add(unit1);
        }
        panel.setDrawingUnits(drawingUnits);
        panel.setProcedureUnits(procedureUnits);
        panel.viewAdjusting();
        panel.setTechnicsNumber(technicsNumber);
        panel.setTechnicsName(technicsName);
        panel.setTechnicsCategory(technicsCategory);
        saveTechnicsRoute(panel);
    }

    protected static void setProcedureRectangleUnitAttribute(Element element,
                                                             ProcedureRectangleUnit unit, String technicsName) {
        String id = element.attributeValue("bsoID");
        String stepNumber = element.attributeValue("stepNumber");
        //处理英文工序图片显示 add by zhuhao 2017.6.20
        String stepName = "";
        if (technicsName != null && !"".equals(technicsName) && technicsName.contains("英文")) {
            stepName = element.attributeValue("stepEnglishName");
            if (stepName == null || "".endsWith(stepName)) {
                stepName = element.attributeValue("stepName");
            }
        } else {
            stepName = element.attributeValue("stepName");
        }
//        String enforce = element.attributeValue("enforce");
//        if("否".equals(enforce)){
//            stepName += "(可选)";
//        }
//		String stepName = element.attributeValue("stepName");
        String workShop = element.attributeValue("workShop");
        String stepType = element.attributeValue("workType");
        String isKey = element.attributeValue("isKey");
        //String description = element.element("procedureContent").getText();

        //@ TODO 149修改 LongXiuChuan
        String description = element.attributeValue("workSpace");

        unit.setId(id);
        unit.setNumber(stepNumber);
        unit.setName(stepName);
        unit.setWorkShop(workShop);
        unit.setTypeAttribute(element);
        unit.setDescription(description);
        unit.setIsKey(isKey);
    }

    public static void saveTechnicsRoute(TechnicsRouteJPanel technicsRouteJPanel) {
        String technicsNumber = technicsRouteJPanel.getTechnicsNumber();
        String technicsCategory = technicsRouteJPanel.getTechnicsCategory();
        String technicsName = technicsRouteJPanel.getTechnicsName();
        //不删除文件夹里分页的工艺流程图
        int size = technicsRouteJPanel.getProcedureUnits().size();
        int refute = size / 30;//分页数
        List<String> list = new ArrayList<String>();
        for(int i = 0; i <= refute; i++) {
            list.add("technics_route_" + i + ".jpg");
        }
        if ((technicsNumber == null) || (technicsNumber.equals(""))) {
            JOptionPane.showMessageDialog(null, "工艺为空,保存失败！", "提示", 1);
            return;
        }

        Vector drawingUnits = technicsRouteJPanel.getDrawingUnits();
        Document document = createDocument(drawingUnits);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("GB2312");

        String technicsPath = "";
        if ("rework".equals(technicsCategory)) {
            technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
        } else if ("temp".equals(technicsCategory)) {
            technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
        } else {
            technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
        }
        String filePath = technicsPath + File.separator + "technics_route.xml";
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileOutputStream(filePath), format);
            writer.write(document);
            File dir = new File(technicsPath);
            File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i].getName();
                    if (!files[i].isDirectory() && fileName.contains("technics_route") && fileName.endsWith(".jpg")
                            && !list.contains(fileName)) {
//                        FileUtil.deleteFile(files[i]);
                        files[i].delete();
                    }
                }
            }
            createImage(technicsRouteJPanel);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveTechnics(TechnicsRouteJPanel technicsRouteJPanel, NewTechnicsPart frame) {
        Vector<TechnicsRouteUnit> drawingUnits = technicsRouteJPanel.getDrawingUnits();
        Map<String, ProcedureRectangleUnit> procedureRectangleUnitMap = new HashMap<String, ProcedureRectangleUnit>();
        for (TechnicsRouteUnit technicsRouteUnit : drawingUnits) {
            if (technicsRouteUnit instanceof ProcedureRectangleUnit) {
                ProcedureRectangleUnit procedureRectangleUnit = (ProcedureRectangleUnit) technicsRouteUnit;
                String stepNumber = procedureRectangleUnit.getNumber();
                procedureRectangleUnitMap.put(stepNumber, procedureRectangleUnit);
            }
        }
        Element techele = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
        if (techele != null) {
            List<Element> stepEleList = XmlUtility.getAllSteps(techele);
            for (Element stepElement : stepEleList) {
                String stepNumber = stepElement.attributeValue("stepNumber");
                if (procedureRectangleUnitMap.containsKey(stepNumber)) {
                    ProcedureRectangleUnit procedureRectangleUnit = procedureRectangleUnitMap.get(stepNumber);
                    String preStepId = procedureRectangleUnit.getPreProcedureID2(procedureRectangleUnit);
                    String nextStepId = procedureRectangleUnit.getNextProcedureID2(procedureRectangleUnit);
                    String preStepName = procedureRectangleUnit.getPreStepName(procedureRectangleUnit);
                    XmlUtility.setAttributeValue(stepElement, "preBsoID", preStepId);
                    XmlUtility.setAttributeValue(stepElement, "nextBsoID", nextStepId);
                    XmlUtility.setAttributeValue(stepElement, "preStep", preStepName);
                }
            }
            frame.saveProcess(techele);
        }
    }

    private static void createImage(TechnicsRouteJPanel technicsRouteJPanel) {
        if (technicsRouteJPanel.getProcedureUnits().size() == 0)
            return;
        String technicsNumber = technicsRouteJPanel.getTechnicsNumber();
        String technicsCategory = technicsRouteJPanel.getTechnicsCategory();
        String technicsName = technicsRouteJPanel.getTechnicsName();
        String technicsPath = "";
        if ("rework".equals(technicsCategory)) {
            technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(
                    technicsNumber, technicsName);
        } else if ("temp".equals(technicsCategory)) {
            technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(
                    technicsNumber, technicsName);
        } else {
            technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
        }
        File file = new File(technicsPath);
        if (!file.exists())
            return;
        String filePath = file.getAbsolutePath() + "\\" + "technics_route.jpg";
        technicsRouteJPanel.viewAdjusting();
        BufferedImage comImage = (BufferedImage) technicsRouteJPanel
                .createImage(technicsRouteJPanel.getPreferredSize().width,
                        technicsRouteJPanel.getPreferredSize().height);
        if (comImage == null)
            return;
        Graphics og = comImage.getGraphics();
        og.setClip(0, 0, technicsRouteJPanel.getPreferredSize().width,
                technicsRouteJPanel.getPreferredSize().height);
        technicsRouteJPanel.paint(og);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(filePath);
            ImageIO.write(comImage, "jpg", output);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createMoreImage(TechnicsRouteJPanel technicsRouteJPanel, int number) {
        if (technicsRouteJPanel.getProcedureUnits().size() == 0)
            return;
        String technicsNumber = technicsRouteJPanel.getTechnicsNumber();
        String technicsCategory = technicsRouteJPanel.getTechnicsCategory();
        String technicsName = technicsRouteJPanel.getTechnicsName();
        String technicsPath = "";
        if ("rework".equals(technicsCategory)) {
            technicsPath = WorkSpaceUtil.getReworkTechnicsDirectory(
                    technicsNumber, technicsName);
        } else if ("temp".equals(technicsCategory)) {
            technicsPath = WorkSpaceUtil.getTempTechnicsDirectory(
                    technicsNumber, technicsName);
        } else {
            technicsPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
        }
        File file = new File(technicsPath);
        if (!file.exists())
            return;
        String filePath = file.getAbsolutePath() + "\\" + "technics_route_" + number + ".jpg";
        technicsRouteJPanel.viewAdjusting();
        BufferedImage comImage = (BufferedImage) technicsRouteJPanel
                .createImage(technicsRouteJPanel.getPreferredSize().width,
                        technicsRouteJPanel.getPreferredSize().height);
        if (comImage == null)
            return;
        Graphics og = comImage.getGraphics();
        og.setClip(0, 0, technicsRouteJPanel.getPreferredSize().width,
                technicsRouteJPanel.getPreferredSize().height);
        technicsRouteJPanel.paint(og);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(filePath);
            ImageIO.write(comImage, "jpg", output);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void reCreateTechnicsRoute(String technicsNumber,
                                             String technicsName, String technicsCategory, List list,
                                             TechnicsRouteJPanel technicsRouteJPanel) throws Exception {
        saveTechnicsRoute(technicsRouteJPanel);
        SAXReader reader = new SAXReader();
        File file;
        if ("rework".equals(technicsCategory)) {
            file = TechnicsRouteUtil.getReworkTechnicsRouteXML(technicsNumber, technicsName);
        } else if ("temp".equals(technicsCategory)) {
            file = TechnicsRouteUtil.getTempTechnicsRouteXML(technicsNumber, technicsName);
        } else {
            file = TechnicsRouteUtil.getTechnicsRouteXML(technicsNumber);
        }
        Document document = reader.read(file);
        technicsRouteJPanel.setDrawingUnits(getUnits(document));
        Vector drawingVector = technicsRouteJPanel.getDrawingUnits();
        Vector procedureVector = technicsRouteJPanel.refreshProcedureVector();
        Vector idVector = new Vector();

        for (int i = 0; i < list.size(); i++) {
            Element element = (Element) list.get(i);
            idVector.add(element.attributeValue("bsoID"));
        }
        for (int i = 0; i < procedureVector.size(); i++) {
            ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureVector.get(i);
            String id = unit.getId();
            if (!idVector.contains(id)) {
                removeUselessUnit(unit, drawingVector);
            }
        }
        if (drawingVector.size() == 0) {
            firstCreateTechnicsRoute(technicsNumber, technicsName, technicsCategory, list, technicsRouteJPanel);
            return;
        }

        procedureVector = technicsRouteJPanel.refreshProcedureVector();
        reSetProcedureAttributes(list, drawingVector, technicsName);

        procedureVector = technicsRouteJPanel.refreshProcedureVector();
        list = getAddedElements(list, procedureVector);

        if (list.size() > 0) {
            int verticalEndPoint = getVerticalEndPoint(drawingVector);
            for (int i = 0; i < list.size(); i++) {
                Element element = (Element) list.get(i);
                ProcedureRectangleUnit unit = new ProcedureRectangleUnit(50
                        + 90 * (i % 10) + 100 * (i % 10), verticalEndPoint + 60
                        * (i / 10 + 1) + i / 10 * 110, element, technicsName);
                unit.setProcedureRectangleHeight(technicsRouteJPanel.getGraphics());
                drawingVector.add(unit);
            }
        }

        technicsRouteJPanel.setDrawingUnits(drawingVector);
        procedureVector = technicsRouteJPanel.refreshProcedureVector();
        technicsRouteJPanel.setProcedureUnits(procedureVector);
        technicsRouteJPanel.viewAdjusting();
        technicsRouteJPanel.setTechnicsNumber(technicsNumber);
        technicsRouteJPanel.setTechnicsName(technicsName);
        technicsRouteJPanel.setTechnicsCategory(technicsCategory);
        saveTechnicsRoute(technicsRouteJPanel);
		saveTechnics(technicsRouteJPanel,technicsRouteJPanel.parentFrame);
    }

    public static void setTechnicsXMLsProcedureLinks(Element technicsElement,
                                                     Vector<ProcedureRectangleUnit> procedureVector,
                                                     NewTechnicsPart frame) throws Exception {
        List stepList = XmlUtility.getAllSteps(technicsElement);
        for (Iterator it = stepList.iterator(); it.hasNext(); ) {
            Element stepElement = (Element) it.next();
            String bsoID = stepElement.attributeValue("bsoID");
            for (int i = 0; i < procedureVector.size(); i++) {
                ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureVector.get(i);
                if (!unit.getId().equals(bsoID))
                    continue;
                String preBsoID = unit.getPreProcedureID();
                String preBsoName = unit.getPreProcedureName();
                String preBsoNumber = unit.getPreProcedureNumber();
                String nextBsoID = unit.getNextProcedureID();
                if (!"".equals(preBsoID) && stepElement.attributeValue("preStep") == null) {//modify by zhuhao 2017.12.05
                    XmlUtility.setAttributeValue(stepElement, "preBsoID", preBsoID);
                    XmlUtility.setAttributeValue(stepElement, "preStep", preBsoNumber + "_" + preBsoName);
                }
                XmlUtility.setAttributeValue(stepElement, "nextBsoID", nextBsoID);
            }
        }
        frame.saveProcess(technicsElement);
    }

    private static List<Element> getAddedElements(List<Element> stepsList,
                                                  Vector<ProcedureRectangleUnit> procedureVector) {
        for (int i = 0; i < stepsList.size(); i++) {
            Element element = (Element) stepsList.get(i);
            String id = element.attributeValue("bsoID");
            for (int j = 0; j < procedureVector.size(); j++) {
                ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureVector.get(j);
                if (id.equals(unit.getId())) {
                    stepsList.remove(element);
                    i--;
                    break;
                }
            }
        }
        return stepsList;
    }

    private static void removeUselessUnit(ProcedureRectangleUnit unit,
                                          Vector<TechnicsRouteUnit> drawingVector) {
        for (int i = 0; i < unit.preLineVector.size(); i++) {
            SingleLineUnit preLineUnit = (SingleLineUnit) unit.preLineVector
                    .get(i);
            if (preLineUnit.preProcedure != null) {
                ProcedureRectangleUnit preProcedureUnit = (ProcedureRectangleUnit) drawingVector
                        .get(drawingVector.indexOf(preLineUnit.preProcedure));
                preProcedureUnit.nextLineVector.remove(preLineUnit);
                preProcedureUnit.nextProcedureVector.remove(unit);
            }
        }
        for (int i = 0; i < unit.nextLineVector.size(); i++) {
            SingleLineUnit nextLineUnit = (SingleLineUnit) unit.nextLineVector
                    .get(i);
            if (nextLineUnit.nextProcedure != null) {
                ProcedureRectangleUnit nextProcedureUnit = (ProcedureRectangleUnit) drawingVector
                        .get(drawingVector.indexOf(nextLineUnit.nextProcedure));
                nextProcedureUnit.preLineVector.remove(nextLineUnit);
                nextProcedureUnit.preProcedureVector.remove(unit);
            }
        }
        drawingVector.removeAll(unit.preLineVector);
        drawingVector.removeAll(unit.nextLineVector);
        drawingVector.remove(unit);
    }

    private static void reSetProcedureAttributes(List<Element> stepsList,
                                                 Vector<TechnicsRouteUnit> vector, String technicsName) {
        for (int i = 0; i < stepsList.size(); i++) {
            Element element = (Element) stepsList.get(i);
            String id = element.attributeValue("bsoID");
            for (int j = 0; j < vector.size(); j++) {
                if ((vector.get(j) instanceof ProcedureRectangleUnit)) {
                    ProcedureRectangleUnit unit = (ProcedureRectangleUnit) vector.get(j);
                    if (id.equals(unit.getId())) {
                        setProcedureRectangleUnitAttribute(element, unit, technicsName);
                        break;
                    }
                }
            }
        }
    }

    private static int getHorizontalEndPoint(
            Vector<TechnicsRouteUnit> drawingVector) {
        int horizontalEndPoint = 0;
        for (int i = 0; i < drawingVector.size(); i++) {
            TechnicsRouteUnit unit = (TechnicsRouteUnit) drawingVector.get(i);
            if ((unit instanceof ProcedureRectangleUnit)) {
                ProcedureRectangleUnit unit1 = (ProcedureRectangleUnit) unit;
                if (unit1.getX() + unit1.getWidth() > horizontalEndPoint)
                    horizontalEndPoint = unit1.getX() + unit1.getWidth();
            } else if ((unit instanceof SingleLineUnit)) {
                SingleLineUnit unit2 = (SingleLineUnit) unit;
                if (unit2.x1 > horizontalEndPoint)
                    horizontalEndPoint = unit2.x1;
                if (unit2.x2 > horizontalEndPoint)
                    horizontalEndPoint = unit2.x2;
                if ((unit2 instanceof DoubleLineUnit)) {
                    if (unit2.x3 > horizontalEndPoint)
                        horizontalEndPoint = unit2.x3;
                } else if ((unit2 instanceof TrebleLineUnit)) {
                    if (unit2.x3 > horizontalEndPoint)
                        horizontalEndPoint = unit2.x3;
                    if (unit2.x4 > horizontalEndPoint)
                        horizontalEndPoint = unit2.x4;
                }
            }
        }
        return horizontalEndPoint;
    }

    private static int getVerticalEndPoint(Vector<TechnicsRouteUnit> drawingVector) {
        int verticalEndPoint = 0;
        for (int i = 0; i < drawingVector.size(); i++) {
            TechnicsRouteUnit unit = (TechnicsRouteUnit) drawingVector.get(i);
            if ((unit instanceof ProcedureRectangleUnit)) {
                ProcedureRectangleUnit unit1 = (ProcedureRectangleUnit) unit;
                if (unit1.getY() + unit1.getHeight() > verticalEndPoint)
                    verticalEndPoint = unit1.getY() + unit1.getHeight();
            } else if ((unit instanceof SingleLineUnit)) {
                SingleLineUnit unit2 = (SingleLineUnit) unit;
                if (unit2.y1 > verticalEndPoint)
                    verticalEndPoint = unit2.y1;
                if (unit2.y2 > verticalEndPoint)
                    verticalEndPoint = unit2.y2;
                if ((unit2 instanceof DoubleLineUnit)) {
                    if (unit2.y3 > verticalEndPoint)
                        verticalEndPoint = unit2.y3;
                } else if ((unit2 instanceof TrebleLineUnit)) {
                    if (unit2.y3 > verticalEndPoint)
                        verticalEndPoint = unit2.y3;
                    if (unit2.y4 > verticalEndPoint)
                        verticalEndPoint = unit2.y4;
                } else if ((unit2 instanceof QuintupleLineUnit)) {
                    if (unit2.y3 > verticalEndPoint)
                        verticalEndPoint = unit2.y3;
                    if (unit2.y4 > verticalEndPoint)
                        verticalEndPoint = unit2.y4;
                    if (unit2.y5 > verticalEndPoint)
                        verticalEndPoint = unit2.y5;
                    if (unit2.y6 > verticalEndPoint)
                        verticalEndPoint = unit2.y6;
                }
            }
        }
        return verticalEndPoint;
    }

    public static void getTechnicsRoute(Document document,
                                        TechnicsRouteJPanel technicsRouteJPanel) throws Exception {
        SAXReader reader = new SAXReader();
        technicsRouteJPanel.setDrawingUnits(getUnits(document));
        Vector vector = technicsRouteJPanel.refreshProcedureVector();
        technicsRouteJPanel.setProcedureUnits(vector);
        technicsRouteJPanel.viewAdjusting();
    }

    protected static void doubleClickProcedurerectangle(
            ProcedureRectangleUnit unit, TechnicsRouteJPanel panel)
            throws Exception {
        String id = unit.getId();
        if ((id != null) && (id.trim().length() > 0)) {
            panel.parentFrame.viewStepMessage(id);
            return;
        }
        JOptionPane.showMessageDialog(null, "选中的工序已经被删除！", "提示", 1);
    }

    protected static ProcedureRectangleUnit getEnteredProcedureRectangleUnit(
            Vector<ProcedureRectangleUnit> procedureUnit, int x, int y) {
        for (int i = 0; i < procedureUnit.size(); i++) {
            ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnit
                    .get(i);
            if (unit.onePointEntered(x, y))
                return unit;
        }
        return null;
    }

    protected static void createSingleLineUnit(
            ProcedureRectangleUnit beginUnit, ProcedureRectangleUnit endUnit,
            Point beginPoint, Point endPoint,
            Vector<TechnicsRouteUnit> drawingUnits, boolean isBidirectional) {
        int[] xPoints = {beginPoint.x, endPoint.x};
        int[] yPoints = {beginPoint.y, endPoint.y};
        SingleLineUnit unit = new SingleLineUnit(xPoints, yPoints);
        setLineUnitLinkAndAttribute(unit, beginUnit, endUnit, beginPoint,
                endPoint, isBidirectional, drawingUnits);
    }

    protected static void createHorizontalDoubleLineUnit(
            ProcedureRectangleUnit beginUnit, ProcedureRectangleUnit endUnit,
            Point beginPoint, Point endPoint,
            Vector<TechnicsRouteUnit> drawingUnits, boolean isBidirectional) {
        int[] xPoints = {beginPoint.x, beginPoint.x, endPoint.x};
        int[] yPoints = {beginPoint.y, endPoint.y, endPoint.y};
        DoubleLineUnit unit = new DoubleLineUnit(xPoints, yPoints);
        setLineUnitLinkAndAttribute(unit, beginUnit, endUnit, beginPoint,
                endPoint, isBidirectional, drawingUnits);
    }

    protected static void createVerticalDoubleLineUnit(
            ProcedureRectangleUnit beginUnit, ProcedureRectangleUnit endUnit,
            Point beginPoint, Point endPoint,
            Vector<TechnicsRouteUnit> drawingUnits, boolean isBidirectional) {
        int[] xPoints = {beginPoint.x, endPoint.x, endPoint.x};
        int[] yPoints = {beginPoint.y, beginPoint.y, endPoint.y};
        DoubleLineUnit unit = new DoubleLineUnit(xPoints, yPoints);
        setLineUnitLinkAndAttribute(unit, beginUnit, endUnit, beginPoint,
                endPoint, isBidirectional, drawingUnits);
    }

    protected static void createHorizontalTrebleLineUnit(
            ProcedureRectangleUnit beginUnit, ProcedureRectangleUnit endUnit,
            Point beginPoint, Point endPoint,
            Vector<TechnicsRouteUnit> drawingUnits, boolean isBidirectional) {
        int x1 = beginPoint.x;
        int y1 = beginPoint.y;
        int x2 = endPoint.x;
        int y2 = endPoint.y;
        int[] xPoints = {x1, (x1 + x2) / 2, (x1 + x2) / 2, x2};
        int[] yPoints = {y1, y1, y2, y2};
        TrebleLineUnit unit = new TrebleLineUnit(xPoints, yPoints);
        setLineUnitLinkAndAttribute(unit, beginUnit, endUnit, beginPoint,
                endPoint, isBidirectional, drawingUnits);
    }

    protected static void createVerticalTrebleLineUnit(
            ProcedureRectangleUnit beginUnit, ProcedureRectangleUnit endUnit,
            Point beginPoint, Point endPoint,
            Vector<TechnicsRouteUnit> drawingUnits, boolean isBidirectional) {
        int x1 = beginPoint.x;
        int y1 = beginPoint.y;
        int x2 = endPoint.x;
        int y2 = endPoint.y;
        int[] xPoints = {x1, x1, x2, x2};
        int[] yPoints = {y1, (y1 + y2) / 2, (y1 + y2) / 2, y2};
        TrebleLineUnit unit = new TrebleLineUnit(xPoints, yPoints);
        setLineUnitLinkAndAttribute(unit, beginUnit, endUnit, beginPoint,
                endPoint, isBidirectional, drawingUnits);
    }

    public static void createHorizontalQuintupleLineUnit(
            ProcedureRectangleUnit beginUnit, ProcedureRectangleUnit endUnit,
            Point beginPoint, Point endPoint,
            Vector<TechnicsRouteUnit> drawingUnits, boolean isBidirectional) {
        int x1 = beginPoint.x;
        int y1 = beginPoint.y;
        int x2 = endPoint.x;
        int y2 = endPoint.y;
        int[] xPoints = {x1, x1 + (x2 - x1) / 3, x1 + (x2 - x1) / 3,
                x1 + (x2 - x1) * 2 / 3, x1 + (x2 - x1) * 2 / 3, x2};
        int[] yPoints = {y1, y1, y1 + (y2 - y1) / 2, y1 + (y2 - y1) / 2, y2,
                y2};
        QuintupleLineUnit unit = new QuintupleLineUnit(xPoints, yPoints);
        setLineUnitLinkAndAttribute(unit, beginUnit, endUnit, beginPoint,
                endPoint, isBidirectional, drawingUnits);
    }

    private static void setLineUnitLinkAndAttribute(SingleLineUnit unit,
                                                    ProcedureRectangleUnit beginUnit, ProcedureRectangleUnit endUnit,
                                                    Point beginPoint, Point endPoint, boolean isBidirectional,
                                                    Vector<TechnicsRouteUnit> drawingUnits) {
        unit.isBidirectional = isBidirectional;
        unit.head = endUnit.getDirection(endPoint);
        unit.tail = beginUnit.getDirection(beginPoint);
        unit.preProcedure = beginUnit;
        unit.nextProcedure = endUnit;
        beginUnit.nextLineVector.add(unit);
        if (!beginUnit.nextProcedureVector.contains(endUnit))
            beginUnit.nextProcedureVector.add(endUnit);
        endUnit.preLineVector.add(unit);
        if (!endUnit.preProcedureVector.contains(beginUnit))
            endUnit.preProcedureVector.add(beginUnit);
        drawingUnits.add(unit);
    }

    protected static boolean canDeleteRectangleLink(TechnicsRouteJPanel panel,
                                                    SingleLineUnit unit) {
        Vector drawingUnits = panel.getDrawingUnits();
        for (int i = 0; i < drawingUnits.size(); i++) {
            TechnicsRouteUnit unit1 = (TechnicsRouteUnit) drawingUnits.get(i);
            if ((unit1 instanceof SingleLineUnit)) {
                SingleLineUnit unit2 = (SingleLineUnit) unit1;
                if ((unit2.preProcedure != null)
                        && (unit2.preProcedure == unit.preProcedure)
                        && (unit2.nextProcedure != null)
                        && (unit2.nextProcedure == unit.nextProcedure)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getTempRoutePath(Element technicsElement) {
        try {
            String technicsDirectory = WorkSpaceUtil
                    .getTechnicsDirectory(technicsElement
                            .attributeValue("technicsNumber"));
            return WorkSpaceUtil.getTempRootPath()
                    + "\\"
                    + technicsDirectory.substring(
                    technicsDirectory.lastIndexOf("\\") + 1,
                    technicsDirectory.length()) + "\\"
                    + "technics_route.xml";
        } catch (Exception e) {
        }
        return null;
    }

    public static void createmoreImage(String technicsNumber,
                                       String technicsName, String technicsCategory, List stepsList,
                                       TechnicsRouteJPanel panel) throws Exception {
        //工序过多pdf分页处理 add by zhuhao 2017.7.4
        int line = 30; //限制条数
        int I = 0;
        Integer size = stepsList.size();
        ArrayList stepsList2 = new ArrayList();
        for (int i = 0; i < stepsList.size(); i++) {
            stepsList2.add(stepsList.get(i));
        }
        // 重置生成图片 start
        String path = "";
        if (technicsNumber != null && technicsName != null) {
            path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
        }

        int Size = stepsList.size() / 30;
        if (stepsList.size() > 30 && stepsList.size() % 30 != 0) {
            Size += 1;
        }
        for (int i = 0; i <= Size; i++) {
            File imgs = new File(path + "//" + "technics_route_" + i + ".jpg");
            if (imgs.exists()) {
                imgs.delete();
            }
        }
        // 重置生成图片 end
        if (line < size) { //判断是否需要分页
            //生成图片
            int refute = size / line;//分页数
            for (int p = 0; p < refute; p++) {
                Vector drawingUnits = new Vector();
                Vector procedureUnits = new Vector();
                List listPage = stepsList.subList(0, line);
                for (int i = 0; i < listPage.size(); i++) {
                    Element element = (Element) listPage.get(i);
                    ProcedureRectangleUnit unit = new ProcedureRectangleUnit(50 + 90
                            * (i % 6) + 100 * (i % 6), 40 + 60 * (i / 6) + (i / 6) * 110,
                            element, technicsName);//50,40 240,210
                    unit.setProcedureRectangleHeight(panel.getGraphics());
                    drawingUnits.add(unit);
                    procedureUnits.add(unit);
                }
                int row = procedureUnits.size() / 6 + 1;
                if (procedureUnits.size() % 6 == 0)
                    row--;
                int verticalBegin = 40;
                int verticalEnd = 0;
                for (int i = 1; i <= row; i++) {
                    for (int j = 6 * (i - 1); (j < 6 * i)
                            && (j < procedureUnits.size()); j++) {
                        ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                                .get(j);
                        int end = verticalBegin + unit.getHeight();
                        if (end > verticalEnd)
                            verticalEnd = end;
                    }
                    for (int j = 6 * (i - 1); (j < 6 * i)
                            && (j < procedureUnits.size()); j++) {
                        ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                                .get(j);
                        unit.setY((verticalBegin + verticalEnd - unit.getHeight()) / 2);
                    }
                    verticalBegin = verticalEnd + 60;
                }

                for (int i = 0; i < procedureUnits.size() - 1; i++) {
                    ProcedureRectangleUnit unit1 = (ProcedureRectangleUnit) procedureUnits
                            .get(i);
                    ProcedureRectangleUnit unit2 = (ProcedureRectangleUnit) procedureUnits
                            .get(i + 1);
                    SingleLineUnit lineUnit = null;
                    if (i % 6 != 5) {
                        int[] xPoints = {unit1.getX() + unit1.getWidth(), unit2.getX()};
                        int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                                unit2.getY() + unit2.getHeight() / 2};
                        lineUnit = new SingleLineUnit(xPoints, yPoints, drawingUnits);
                    } else {
                        int maxY1 = 0;
                        int minY2 = unit2.getY();
                        for (int j = i - 5 + 1; j <= i; j++) {
                            ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                                    .get(j);
                            if (maxY1 < unit3.getY() + unit3.getHeight())
                                maxY1 = unit3.getY() + unit3.getHeight();
                        }
                        for (int j = i + 1; (j <= i + 5 - 1)
                                && (j < procedureUnits.size()); j++) {
                            ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                                    .get(j);
                            if (minY2 > unit3.getY())
                                minY2 = unit3.getY();
                        }
                        int[] xPoints = {unit1.getX() + unit1.getWidth(),
                                unit1.getX() + unit1.getWidth() + 45,
                                unit1.getX() + unit1.getWidth() + 45,
                                unit2.getX() - 45, unit2.getX() - 45, unit2.getX()};
                        int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                                unit1.getY() + unit1.getHeight() / 2,
                                (maxY1 + minY2) / 2, (maxY1 + minY2) / 2,
                                unit2.getY() + unit2.getHeight() / 2,
                                unit2.getY() + unit2.getHeight() / 2};
                        lineUnit = new QuintupleLineUnit(xPoints, yPoints,
                                drawingUnits, false);
                    }
                    lineUnit.preProcedure = unit1;
                    lineUnit.nextProcedure = unit2;
                    lineUnit.head = "west";
                    lineUnit.tail = "east";
                    unit1.nextLineVector.add(lineUnit);
                    unit1.nextProcedureVector.add(unit2);
                    unit2.preLineVector.add(lineUnit);
                    unit2.preProcedureVector.add(unit1);
                }
                panel.setDrawingUnits(drawingUnits);
                panel.setProcedureUnits(procedureUnits);
                panel.viewAdjusting();
                panel.setTechnicsNumber(technicsNumber);
                panel.setTechnicsName(technicsName);
                panel.setTechnicsCategory(technicsCategory);
                createMoreImage(panel, p);
                stepsList.subList(0, line).clear();
                panel.clearAll();
//				panel.setVisible(false);
                I = p;
            }
            if (!stepsList.isEmpty()) {
                Vector drawingUnits = new Vector();
                Vector procedureUnits = new Vector();
                for (int i = 0; i < stepsList.size(); i++) {
                    Element element = (Element) stepsList.get(i);
                    ProcedureRectangleUnit unit = new ProcedureRectangleUnit(50 + 90
                            * (i % 6) + 100 * (i % 6), 40 + 60 * (i / 6) + (i / 6) * 110,
                            element, technicsName);//50,40 240,210
                    unit.setProcedureRectangleHeight(panel.getGraphics());
                    drawingUnits.add(unit);
                    procedureUnits.add(unit);
                }
                int row = procedureUnits.size() / 6 + 1;
                if (procedureUnits.size() % 6 == 0)
                    row--;
                int verticalBegin = 40;
                int verticalEnd = 0;
                for (int i = 1; i <= row; i++) {
                    for (int j = 6 * (i - 1); (j < 6 * i)
                            && (j < procedureUnits.size()); j++) {
                        ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                                .get(j);
                        int end = verticalBegin + unit.getHeight();
                        if (end > verticalEnd)
                            verticalEnd = end;
                    }
                    for (int j = 6 * (i - 1); (j < 6 * i)
                            && (j < procedureUnits.size()); j++) {
                        ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                                .get(j);
                        unit.setY((verticalBegin + verticalEnd - unit.getHeight()) / 2);
                    }
                    verticalBegin = verticalEnd + 60;
                }

                for (int i = 0; i < procedureUnits.size() - 1; i++) {
                    ProcedureRectangleUnit unit1 = (ProcedureRectangleUnit) procedureUnits
                            .get(i);
                    ProcedureRectangleUnit unit2 = (ProcedureRectangleUnit) procedureUnits
                            .get(i + 1);
                    SingleLineUnit lineUnit = null;
                    if (i % 6 != 5) {
                        int[] xPoints = {unit1.getX() + unit1.getWidth(), unit2.getX()};
                        int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                                unit2.getY() + unit2.getHeight() / 2};
                        lineUnit = new SingleLineUnit(xPoints, yPoints, drawingUnits);
                    } else {
                        int maxY1 = 0;
                        int minY2 = unit2.getY();
                        for (int j = i - 5 + 1; j <= i; j++) {
                            ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                                    .get(j);
                            if (maxY1 < unit3.getY() + unit3.getHeight())
                                maxY1 = unit3.getY() + unit3.getHeight();
                        }
                        for (int j = i + 1; (j <= i + 5 - 1)
                                && (j < procedureUnits.size()); j++) {
                            ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                                    .get(j);
                            if (minY2 > unit3.getY())
                                minY2 = unit3.getY();
                        }
                        int[] xPoints = {unit1.getX() + unit1.getWidth(),
                                unit1.getX() + unit1.getWidth() + 45,
                                unit1.getX() + unit1.getWidth() + 45,
                                unit2.getX() - 45, unit2.getX() - 45, unit2.getX()};
                        int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                                unit1.getY() + unit1.getHeight() / 2,
                                (maxY1 + minY2) / 2, (maxY1 + minY2) / 2,
                                unit2.getY() + unit2.getHeight() / 2,
                                unit2.getY() + unit2.getHeight() / 2};
                        lineUnit = new QuintupleLineUnit(xPoints, yPoints,
                                drawingUnits, false);
                    }
                    lineUnit.preProcedure = unit1;
                    lineUnit.nextProcedure = unit2;
                    lineUnit.head = "west";
                    lineUnit.tail = "east";
                    unit1.nextLineVector.add(lineUnit);
                    unit1.nextProcedureVector.add(unit2);
                    unit2.preLineVector.add(lineUnit);
                    unit2.preProcedureVector.add(unit1);
                }
                panel.setDrawingUnits(drawingUnits);
                panel.setProcedureUnits(procedureUnits);
                panel.viewAdjusting();
                panel.setTechnicsNumber(technicsNumber);
                panel.setTechnicsName(technicsName);
                panel.setTechnicsCategory(technicsCategory);
                createMoreImage(panel, I + 1);
                panel.clearAll();
            }
            //在界面上显示
            Vector drawingUnits = new Vector();
            Vector procedureUnits = new Vector();
            for (int i = 0; i < stepsList2.size(); i++) {
                Element element = (Element) stepsList2.get(i);
                ProcedureRectangleUnit unit = new ProcedureRectangleUnit(50 + 90
                        * (i % 6) + 100 * (i % 6), 40 + 60 * (i / 6) + (i / 6) * 110,
                        element, technicsName);//50,40 240,210
                unit.setProcedureRectangleHeight(panel.getGraphics());
                drawingUnits.add(unit);
                procedureUnits.add(unit);
            }
            int row = procedureUnits.size() / 6 + 1;
            if (procedureUnits.size() % 6 == 0)
                row--;
            int verticalBegin = 40;
            int verticalEnd = 0;
            for (int i = 1; i <= row; i++) {
                for (int j = 6 * (i - 1); (j < 6 * i)
                        && (j < procedureUnits.size()); j++) {
                    ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                            .get(j);
                    int end = verticalBegin + unit.getHeight();
                    if (end > verticalEnd)
                        verticalEnd = end;
                }
                for (int j = 6 * (i - 1); (j < 6 * i)
                        && (j < procedureUnits.size()); j++) {
                    ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                            .get(j);
                    unit.setY((verticalBegin + verticalEnd - unit.getHeight()) / 2);
                }
                verticalBegin = verticalEnd + 60;
            }

            for (int i = 0; i < procedureUnits.size() - 1; i++) {
                ProcedureRectangleUnit unit1 = (ProcedureRectangleUnit) procedureUnits
                        .get(i);
                ProcedureRectangleUnit unit2 = (ProcedureRectangleUnit) procedureUnits
                        .get(i + 1);
                SingleLineUnit lineUnit = null;
                if (i % 6 != 5) {
                    int[] xPoints = {unit1.getX() + unit1.getWidth(), unit2.getX()};
                    int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                            unit2.getY() + unit2.getHeight() / 2};
                    lineUnit = new SingleLineUnit(xPoints, yPoints, drawingUnits);
                } else {
                    int maxY1 = 0;
                    int minY2 = unit2.getY();
                    for (int j = i - 5 + 1; j <= i; j++) {
                        ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                                .get(j);
                        if (maxY1 < unit3.getY() + unit3.getHeight())
                            maxY1 = unit3.getY() + unit3.getHeight();
                    }
                    for (int j = i + 1; (j <= i + 5 - 1)
                            && (j < procedureUnits.size()); j++) {
                        ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                                .get(j);
                        if (minY2 > unit3.getY())
                            minY2 = unit3.getY();
                    }
                    int[] xPoints = {unit1.getX() + unit1.getWidth(),
                            unit1.getX() + unit1.getWidth() + 45,
                            unit1.getX() + unit1.getWidth() + 45,
                            unit2.getX() - 45, unit2.getX() - 45, unit2.getX()};
                    int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                            unit1.getY() + unit1.getHeight() / 2,
                            (maxY1 + minY2) / 2, (maxY1 + minY2) / 2,
                            unit2.getY() + unit2.getHeight() / 2,
                            unit2.getY() + unit2.getHeight() / 2};
                    lineUnit = new QuintupleLineUnit(xPoints, yPoints,
                            drawingUnits, false);
                }
                lineUnit.preProcedure = unit1;
                lineUnit.nextProcedure = unit2;
                lineUnit.head = "west";
                lineUnit.tail = "east";
                unit1.nextLineVector.add(lineUnit);
                unit1.nextProcedureVector.add(unit2);
                unit2.preLineVector.add(lineUnit);
                unit2.preProcedureVector.add(unit1);
            }
            panel.setDrawingUnits(drawingUnits);
            panel.setProcedureUnits(procedureUnits);
            panel.viewAdjusting();
            panel.setTechnicsNumber(technicsNumber);
            panel.setTechnicsName(technicsName);
            panel.setTechnicsCategory(technicsCategory);
            saveTechnicsRoute(panel);
//		end	否则不分页
        } else {
            Vector drawingUnits = new Vector();
            Vector procedureUnits = new Vector();
            for (int i = 0; i < stepsList.size(); i++) {
                Element element = (Element) stepsList.get(i);
                ProcedureRectangleUnit unit = new ProcedureRectangleUnit(50 + 90
                        * (i % 5) + 100 * (i % 5), 40 + 60 * (i / 5) + (i / 5) * 110,
                        element, technicsName);//50,40 240,210
                unit.setProcedureRectangleHeight(panel.getGraphics());
                drawingUnits.add(unit);
                procedureUnits.add(unit);
            }

            int row = procedureUnits.size() / 5 + 1;
            if (procedureUnits.size() % 5 == 0)
                row--;
            int verticalBegin = 40;
            int verticalEnd = 0;
            for (int i = 1; i <= row; i++) {
                for (int j = 5 * (i - 1); (j < 5 * i)
                        && (j < procedureUnits.size()); j++) {
                    ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                            .get(j);
                    int end = verticalBegin + unit.getHeight();
                    if (end > verticalEnd)
                        verticalEnd = end;
                }
                for (int j = 5 * (i - 1); (j < 5 * i)
                        && (j < procedureUnits.size()); j++) {
                    ProcedureRectangleUnit unit = (ProcedureRectangleUnit) procedureUnits
                            .get(j);
                    unit.setY((verticalBegin + verticalEnd - unit.getHeight()) / 2);
                }
                verticalBegin = verticalEnd + 60;
            }

            for (int i = 0; i < procedureUnits.size() - 1; i++) {
                ProcedureRectangleUnit unit1 = (ProcedureRectangleUnit) procedureUnits
                        .get(i);
                ProcedureRectangleUnit unit2 = (ProcedureRectangleUnit) procedureUnits
                        .get(i + 1);
                SingleLineUnit lineUnit = null;
                if (i % 5 != 4) {
                    int[] xPoints = {unit1.getX() + unit1.getWidth(), unit2.getX()};
                    int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                            unit2.getY() + unit2.getHeight() / 2};
                    lineUnit = new SingleLineUnit(xPoints, yPoints, drawingUnits);
                } else {
                    int maxY1 = 0;
                    int minY2 = unit2.getY();
                    for (int j = i - 5 + 1; j <= i; j++) {
                        ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                                .get(j);
                        if (maxY1 < unit3.getY() + unit3.getHeight())
                            maxY1 = unit3.getY() + unit3.getHeight();
                    }
                    for (int j = i + 1; (j <= i + 5 - 1)
                            && (j < procedureUnits.size()); j++) {
                        ProcedureRectangleUnit unit3 = (ProcedureRectangleUnit) procedureUnits
                                .get(j);
                        if (minY2 > unit3.getY())
                            minY2 = unit3.getY();
                    }
                    int[] xPoints = {unit1.getX() + unit1.getWidth(),
                            unit1.getX() + unit1.getWidth() + 45,
                            unit1.getX() + unit1.getWidth() + 45,
                            unit2.getX() - 45, unit2.getX() - 45, unit2.getX()};
                    int[] yPoints = {unit1.getY() + unit1.getHeight() / 2,
                            unit1.getY() + unit1.getHeight() / 2,
                            (maxY1 + minY2) / 2, (maxY1 + minY2) / 2,
                            unit2.getY() + unit2.getHeight() / 2,
                            unit2.getY() + unit2.getHeight() / 2};
                    lineUnit = new QuintupleLineUnit(xPoints, yPoints,
                            drawingUnits, false);
                }
                lineUnit.preProcedure = unit1;
                lineUnit.nextProcedure = unit2;
                lineUnit.head = "west";
                lineUnit.tail = "east";
                unit1.nextLineVector.add(lineUnit);
                unit1.nextProcedureVector.add(unit2);
                unit2.preLineVector.add(lineUnit);
                unit2.preProcedureVector.add(unit1);
            }
            panel.setDrawingUnits(drawingUnits);
            panel.setProcedureUnits(procedureUnits);
            panel.viewAdjusting();
            panel.setTechnicsNumber(technicsNumber);
            panel.setTechnicsName(technicsName);
            panel.setTechnicsCategory(technicsCategory);
            saveTechnicsRoute(panel);
        }
    }
}
