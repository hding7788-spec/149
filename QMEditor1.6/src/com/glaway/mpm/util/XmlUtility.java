package com.glaway.mpm.util;

import com.glaway.mpm.erp.ErpToWCIntf;
import com.glaway.mpm.parameter.constants.XMLConstants;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.view.NewDrawingJPanel;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.glaway.speciaword.common.CommonHelper;
import ext.ases.techMaterial.bean.*;
import ext.casc.sop.util.StringUtil;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import wt.part.WTPart;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.text.SimpleDateFormat;
import java.util.*;

public class XmlUtility {
    public static final String CREO_SEPARATOR = ";";
    public static final String ERROR1 = "选中的工艺不存在，可能已经被删除";
    public static final String PACENUMBER = "检;注;A;B;C;D;E;F;G";
    public static final int STEPINTERVAL = 10;
    public static final int PACEINTERVAL = 1;
    public static final String ROOT_TAG = "technics";
    public static final String TECHNICS = "QMFawTechnicsInfo";
    public static final String STEP_TAG = "steps";
    public static final String PACE_TAG = "paces";
    public static final String PROCEDURE = "QMProcedureInfo";
    public static final String PART_GROUP = "parts";
    public static final String PART_TAG = "QMPartInfo";
    public static final String CONTENT_TAG = "procedureContent";
    public static final String MATERIAL_GROUP = "materials";
    public static final String MATERIAL_TAG = "QMMaterialInfo";
    public static final String EQUIP_GROUP = "equips";
    public static final String CHECK_EQUIP_GROUP = "checkEquips";
    public static final String MEASURE_GROUP = "measures";
    public static final String CHECK_MEASURE_GROUP = "checkMeasures";
    public static final String EQUIP_TAG = "QMEquipmentInfo";
    public static final String MEASURE_TAG = "QMMeasureInfo";
    public static final String SDASHBOARD_GROUP = "sdashboard";
    public static final String CHECK_SDASHBOARD_GROUP = "checkSdashboard";
    public static final String SDASHBOARD_TAG = "QMSDashboardInfo";
    public static final String UNSDASHBOARD_GROUP = "unsdashboard";
    public static final String CHECK_UNSDASHBOARD_GROUP = "checkUnsdashboard";
    public static final String UNSDASHBOARD_TAG = "QMUnSDashboardInfo";
    public static final String TOOL_GROUP = "tools";
    public static final String SOP_GROUP = "sops";
    public static final String KNIFETOOL_GROUP = "knifeTools";
    public static final String TOOL_TAG = "QMToolInfo";
    public static final String SOP_TAG = "SOPInfo";
    public static final String KNIFETOOL_TAG = "QMKnifeToolInfo";
    public static final String IMAGE_GROUP = "images";
    public static final String IMAGE_TAG = "PDrawingInfo";
    public static final String ENTERSIGN = "@#$";
    public static final String TechnicsDescribe = "TechnicsDescribe";
    public static final String WXTechnicsDescribe = "WXTechnicsDescribe";
    public static final String PEITAOLISTTABLE = "PEITAOTABLE";
    public static final String WORKSPACE_GROUP = "workspace";
    public static final String WORKSPACE_TAG = "QMWorkSpaceInfo";
    public static final String SCHEMA_DATA = "schemaData";
    public static final String SCHEMA_TAG = "schemaInfo";
    public static final String PHOTO_DATA = "photoRecords";
    public static final String PHOTO_TAG = "photoRecord";

    public static byte[] generateDocumentByteArray(Document document)
            throws Exception {
        if (document == null)
            return null;
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("GBK");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(document);
        writer.flush();
        byte[] array = out.toByteArray();
        writer.close();
        out.close();
        out = null;
        return array;
    }

    public static Document getDocument(String fileName) {
        if ((fileName == null) || (fileName.trim().equals("")))
            return null;
        return getDocument(new File(fileName));
    }

    public static Document getDocument(File file) {
        if ((file == null) || (!file.isFile()))
            return null;
        SAXReader saxReader = new SAXReader();
        saxReader.setEncoding("GBK");
        Document document = null;
        try {
            document = saxReader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static Document getDocument(byte[] xmlContentArray) throws Exception {
        SAXReader saxReader = new SAXReader();
        saxReader.setEncoding("GBK");
        Document document = null;
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(xmlContentArray);
            document = saxReader.read(inputStream);
        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        } finally {
            if (inputStream != null)
                inputStream.close();
            inputStream = null;
            saxReader = null;
        }
        return document;
    }

    public static void saveDocument(Document document, File file)
            throws Exception {
        if ((document == null) || (file == null))
            return;
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("GBK");
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.write(document);
        writer.close();
    }

    public static void saveDocument(Document document, String fileName)
            throws Exception {
        if ((document == null) || (fileName == null)
                || (fileName.trim().equals("")))
            return;
        saveDocument(document, new File(fileName));
    }

    public static Element getTechnicsElement(Document document) {
        if (document == null) {
            return null;
        }
        Element rootElement = document.getRootElement();
        if (!rootElement.getName().equals("technics")) {
            return null;
        }
        List list = rootElement.elements();
        if ((list == null) || (list.isEmpty())) {
            return null;
        }
        return (Element) list.get(0);
    }

    public static Element getTechnicsElement(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1) + ".xml";
        String xmlFileName = filePath + File.separator + fileName;
        Document document = getDocument(xmlFileName);
        Element techElement = getTechnicsElement(document);
        return techElement;
    }

    public static Element getTechnicsAdditionTablesElement(Element element) {
        Element partElements = element.element("additiontables");
        if (partElements == null)
            partElements = element.addElement("additiontables");
        return partElements;
    }

    public static List<Element> getTechnicsAdditionTables(Element element) {
        Element partElements = element.element("additiontables");
        if (partElements == null)
            partElements = element.addElement("additiontables");
        return partElements.elements();
    }

    public static Element getTechnicsStateTablesElement(Element element) {
        Element partElements = element.element("technicsStateTables");
        if (partElements == null)
            partElements = element.addElement("technicsStateTables");
        return partElements;
    }

    public static List<Element> getTechnicsStateTables(Element element) {
        Element partElements = element.element("technicsStateTables");
        if (partElements == null)
            partElements = element.addElement("technicsStateTables");
        return partElements.elements();
    }

    public static Element getTechnicsIBAAttriElement(Element element) {
        Element partElements = element.element("IBAAttibutes");
        if (partElements == null)
            partElements = element.addElement("IBAAttibutes");
        return partElements;
    }

    public static List<Element> getTechnicsIBAAttibutes(Element element) {
        Element partElements = element.element("IBAAttibutes");
        if (partElements == null)
            partElements = element.addElement("IBAAttibutes");
        return partElements.elements();
    }

    public static Element getTechnicsCLDEElement(Element element) {
        Element partElements = element.element("CLDE");
        if (partElements == null)
            partElements = element.addElement("CLDE");
        return partElements;
    }

    public static List<Element> getTechnicsCLDE(Element element) {
        Element partElements = element.element("CLDE");
        if (partElements == null)
            partElements = element.addElement("CLDE");
        return partElements.elements();
    }

    public static Element getTechnicsDEElement(Element element) {
        Element partElements = element.element("GYDE");
        if (partElements == null)
            partElements = element.addElement("GYDE");
        return partElements;
    }

    public static List<Element> getTechnicsDE(Element element) {
        Element partElements = element.element("GYDE");
        if (partElements == null)
            partElements = element.addElement("GYDE");
        return partElements.elements();
    }

    public static Element getFZTechnicsElement(Element element) {
        Element partElements = element.element("FZGY");
        if (partElements == null)
            partElements = element.addElement("FZGY");
        return partElements;
    }

    public static List<Element> getFZTechnics(Element element) {
        Element partElements = element.element("FZGY");
        if (partElements == null)
            partElements = element.addElement("FZGY");
        return partElements.elements();
    }

    public static Element getPeiTaoListTableElement(Element element) {
        Element partElements = element.element("PEITAOTABLE");
        if (partElements == null)
            partElements = element.addElement("PEITAOTABLE");
        return partElements;
    }

    public static List<Element> getPeiTaoListTableElements(Element element) {
        Element partElements = element.element("PEITAOTABLE");
        if (partElements == null)
            partElements = element.addElement("PEITAOTABLE");
        return partElements.elements();
    }

    public static Element getChildElements(Element parentElement, String childName) {
        if (parentElement == null)
            return null;
        Iterator it = parentElement.elementIterator();
        if (it != null) {
            while (it.hasNext()) {
                Element childElement = (Element) it.next();
                if (childElement.getName().equals(childName)) {
                    return childElement;
                }
            }
        }
        return null;
    }

    public static Element getTechnicsYCLDEElement(Element element) {
        Element partElements = element.element("YCLDE");
        if (partElements == null)
            partElements = element.addElement("YCLDE");
        return partElements;
    }

    public static List<Element> getTechnicsYCLDE(Element element) {
        Element partElements = element.element("YCLDE");
        if (partElements == null)
            partElements = element.addElement("YCLDE");
        return partElements.elements();
    }

    public static List<Element> getTechnicsSJZYKYCLDE(Element element) {
        Element partElements = element.element("SJZYKYCLDE");
        if (partElements == null)
            partElements = element.addElement("SJZYKYCLDE");
        return partElements.elements();
    }

    public static Element getTechnicsSJYCLDEElement(Element element) {
        Element partElements = element.element("SJYCLDE");
        if (partElements == null)
            partElements = element.addElement("SJYCLDE");
        return partElements;
    }

    public static List<Element> getTechnicsSJYCLDE(Element element) {
        Element partElements = element.element("SJYCLDE");
        if (partElements == null)
            partElements = element.addElement("SJYCLDE");
        return partElements.elements();
    }

    public static List<Element> getTechnicsSJZYKSJYCLDE(Element element) {
        Element partElements = element.element("SJZYKSJYCLDE");
        if (partElements == null)
            partElements = element.addElement("SJZYKSJYCLDE");
        return partElements.elements();
    }

    public static Element getTechnicsZYCLDEElement(Element element) {
        Element partElements = element.element("ZYCLDE");
        if (partElements == null)
            partElements = element.addElement("ZYCLDE");
        return partElements;
    }

    public static List<Element> getTechnicsSJZYKZYCLDE(Element element) {
        Element partElements = element.element("SJZYKZYCLDE");
        if (partElements == null)
            partElements = element.addElement("SJZYKZYCLDE");
        return partElements.elements();
    }

    public static List<Element> getTechnicsZYCLDE(Element element) {
        Element partElements = element.element("ZYCLDE");
        if (partElements == null)
            partElements = element.addElement("ZYCLDE");
        return partElements.elements();
    }

    public static List<Element> getTechnicsGYDEMatchPart(Element element) {
        Element partElements = element.element("MATCHPART");
        if (partElements == null)
            partElements = element.addElement("MATCHPART");
        return partElements.elements();
    }

    public static List<Element> getTechnicsSJZYKGYDEMatchPart(Element element) {
        Element partElements = element.element("SJZYKMATCHPART");
        if (partElements == null)
            partElements = element.addElement("SJZYKMATCHPART");
        return partElements.elements();
    }

    public static List<Element> getTechnicsGYDENewPart(Element element) {
        Element partElements = element.element("NEWPART");
        if (partElements == null)
            partElements = element.addElement("NEWPART");
        return partElements.elements();
    }

    public static List<Element> getTechnicsSJZYKGYDENewPart(Element element) {
        Element partElements = element.element("SJZYKNEWPART");
        if (partElements == null)
            partElements = element.addElement("SJZYKNEWPART");
        return partElements.elements();
    }

    public static Element getProcedureIBAAttriElement(Element element) {
        Element partElements = element.element("IBAAttibutes");
        if (partElements == null)
            partElements = element.addElement("IBAAttibutes");
        return partElements;
    }

    public static List<Element> getProcedureIBAAttibutes(Element element) {
        Element partElements = element.element("IBAAttibutes");
        if (partElements == null)
            partElements = element.addElement("IBAAttibutes");
        return partElements.elements();
    }

    public static Element getBorrowTechnicsElement(Element element) {
        Element partElements = element.element("borrowTechnicss");
        if (partElements == null)
            partElements = element.addElement("borrowTechnicss");
        return partElements;
    }

    public static List<Element> getBorrowTechnics(Element element) {
        Element partElements = element.element("borrowTechnicss");
        if (partElements == null)
            partElements = element.addElement("borrowTechnicss");
        return partElements.elements();
    }

    public static List<Element> getLargeFileElements(Element element) {
        Element partElements = element.element("LargeFiles");
        if (partElements == null)
            partElements = element.addElement("LargeFiles");
        return partElements.elements();
    }

    public static Element getParts(Element element) {
        Element partElements = element.element("parts");
        if (partElements == null)
            partElements = element.addElement("parts");
        return partElements;
    }


    public static Element getMaterials(Element element) {
        Element materialElements = element.element("materials");
        if (materialElements == null)
            materialElements = element.addElement("materials");
        return materialElements;
    }

    public static Element getProcedures(Element technicsElement) {
        Element procedureElements = technicsElement.element("steps");
        if (procedureElements == null)
            procedureElements = technicsElement.addElement("steps");
        return procedureElements;
    }

    public static String getProcedureContent(Element procedureElement) {
        Element procedureContentElement = procedureElement.element("procedureContent");
        if (procedureContentElement == null)
            return "";
        String content = procedureContentElement.getText();

        if (content == null)
            return "";
//		while (content.indexOf("@#$") != -1) {
//			String s = content.replace("@#$", "\n");
//			content = s;
//		}
        return content;
    }

    //读取检验方法 add byzhuhao 2017.10.20
    public static String getCheckMethodContent(Element procedureElement) {
        Element procedureContentElement = procedureElement.element("checkmethodContent");
        if (procedureContentElement == null)
            return "";
        String content = procedureContentElement.getText();

        if (content == null)
            return "";
        return content;
    }

    //读取检验内容 add byzhuhao 2017.10.20
    public static String getCheckContentContent(Element procedureElement) {
        Element procedureContentElement = procedureElement.element("checkcontentContent");
        if (procedureContentElement == null)
            return "";
        String content = procedureContentElement.getText();

        if (content == null)
            return "";
        return content;
    }

    public static String getTechnicsDescribe(Element technicsDescribe) {
        Element technicsDescribeElement = technicsDescribe.element("TechnicsDescribe");
        if (technicsDescribeElement == null)
            return "";
        String content = technicsDescribeElement.getText();

        if (content == null)
            return "";
        return content;
    }

    public static String getWXTechnicsDescribe(Element technicsDescribe) {
        Element technicsDescribeElement = technicsDescribe.element("WXTechnicsDescribe");
        if (technicsDescribeElement == null)
            return "";
        String content = technicsDescribeElement.getText();

        if (content == null)
            return "";
        return content;
    }

    public static String getGyztContent(Element procedureElement) {
        Element procedureContentElement = procedureElement.element("gyzt");
        if (procedureContentElement == null)
            return "";
        String content = procedureContentElement.getText();

        if (content == null)
            return "";
//		while (content.indexOf("@#$") != -1) {
//			String s = content.replace("@#$", "\n");
//			content = s;
//		}
        return content;
    }


    public static String getHasEnterAttribute(Element element, String attrName) {

        String content = element.attributeValue(attrName);

        if (content == null)
            return "";
        while (content.indexOf("@#$") != -1) {
            String s = content.replace("@#$", "\n");
            content = s;
        }
        return content;
    }

    public static void setHasEnterAttribute(Element element, String attrName, String attrValue) {
        if (element == null || attrName == null || attrValue == null)
            return;
        attrValue = attrValue.replace("\n", "@#$");
        setAttributeValue(element, attrName, attrValue);
    }

    public static Element getEquips(Element procedureElement) {
        Element equipElements = procedureElement.element("equips");
        if (equipElements == null)
            equipElements = procedureElement.addElement("equips");
        return equipElements;
    }

    public static Element getCheckRecord(Element procedureElement) {
        Element checkElements = procedureElement.element("checkRecordTables");
        if (checkElements == null) {
            checkElements = procedureElement.addElement("checkRecordTables");
        }
        return checkElements;

    }

    public static Element getMeasures(Element procedureElement) {
        Element equipElements = procedureElement.element("measures");
        if (equipElements == null)
            equipElements = procedureElement.addElement("measures");
        return equipElements;
    }

    public static Element getWorkSpace(Element procedureElement) {
        Element equipElements = procedureElement.element("workspace");
        if (equipElements == null)
            equipElements = procedureElement.addElement("workspace");
        return equipElements;
    }

    public static Element getSDashboards(Element procedureElement) {
        Element equipElements = procedureElement.element("sdashboard");
        if (equipElements == null)
            equipElements = procedureElement.addElement("sdashboard");
        return equipElements;
    }

    public static Element getUnsdashboards(Element procedureElement) {
        Element equipElements = procedureElement.element("unsdashboard");
        if (equipElements == null)
            equipElements = procedureElement.addElement("unsdashboard");
        return equipElements;
    }

    public static Element getTools(Element procedureElement) {
        Element toolElements = procedureElement.element("tools");
        if (toolElements == null)
            toolElements = procedureElement.addElement("tools");
        return toolElements;
    }

    public static Element getKnifeTools(Element procedureElement) {
        Element toolElements = procedureElement.element("knifeTools");
        if (toolElements == null)
            toolElements = procedureElement.addElement("knifeTools");
        return toolElements;
    }

    public static Element getImages(Element procedureElement) {
        Element imageElements = procedureElement.element("images");
        if (imageElements == null)
            imageElements = procedureElement.addElement("images");
        return imageElements;
    }

    public static Element getAttachs(Element stepElement) {
        Element imageElements = stepElement.element("attachs");
        if (imageElements == null)
            imageElements = stepElement.addElement("attachs");
        return imageElements;
    }

    public static Element getPaces(Element procedureElement) {
        Element paceElements = procedureElement.element("paces");
        if (paceElements == null)
            paceElements = procedureElement.addElement("paces");
        return paceElements;
    }

    public static void addMaterial(Element element, Element materialElement) {
        if ((element == null) || (materialElement == null))
            return;
        Element materialElements = element.element("materials");
        if (materialElements == null)
            materialElements = element.addElement("materials");
        materialElements.add(materialElement);
    }

    public static void addParts(Element element, Element partElement) {
        if ((element == null) || (partElement == null))
            return;
        Element partElements = element.element("parts");
        if (partElements == null)
            partElements = element.addElement("parts");
        partElements.add(partElement);
    }

    public static void addProcedure(Element technicsElement,
                                    Element procedureElement) {
        if ((technicsElement == null) || (procedureElement == null))
            return;
        Element procedureElements = technicsElement.element("steps");
        if (procedureElements == null)
            procedureElements = technicsElement.addElement("steps");
        procedureElements.add(procedureElement);
    }

    public static void addChildProcedure(Element stepElement,
                                         Element paceElement) {
        if ((stepElement == null) || (paceElement == null))
            return;
        Element paceElements = stepElement.element("paces");
        if (paceElements == null)
            paceElements = stepElement.addElement("paces");
        paceElements.add(paceElement);
    }

    public static void addTool(Element procedureElement, Element toolElement) {
        if ((procedureElement == null) || (toolElement == null))
            return;
        Element toolElements = procedureElement.element("tools");
        if (toolElements == null)
            toolElements = procedureElement.addElement("tools");
        toolElements.add(toolElement);
    }

    public static void addKnifeTool(Element procedureElement, Element toolElement) {
        if ((procedureElement == null) || (toolElement == null))
            return;
        Element toolElements = procedureElement.element("knifeTools");
        if (toolElements == null)
            toolElements = procedureElement.addElement("knifeTools");
        toolElements.add(toolElement);
    }

    public static void addEquip(Element procedureElement, Element equipElement) {
        if ((procedureElement == null) || (equipElement == null))
            return;
        Element equipElements = procedureElement.element("equips");
        if (equipElements == null)
            equipElements = procedureElement.addElement("equips");
        equipElements.add(equipElement);
    }

    public static void addCheckEquip(Element procedureElement, Element equipElement) {
        if ((procedureElement == null) || (equipElement == null))
            return;
        Element checkEquipElements = procedureElement.element("checkEquips");
        if (checkEquipElements == null)
            checkEquipElements = procedureElement.addElement("checkEquips");
        checkEquipElements.add(equipElement);
    }

    public static void addMeasure(Element procedureElement, Element equipElement) {
        if ((procedureElement == null) || (equipElement == null))
            return;
        Element equipElements = procedureElement.element("measures");
        if (equipElements == null)
            equipElements = procedureElement.addElement("measures");
        equipElements.add(equipElement);
    }

    public static void addWorkspace(Element procedureElement, Element equipElement) {
        if ((procedureElement == null) || (equipElement == null))
            return;
        Element equipElements = procedureElement.element("workspace");
        if (equipElements == null)
            equipElements = procedureElement.addElement("workspace");
        equipElements.add(equipElement);
    }

    public static void addSDashboard(Element procedureElement, Element equipElement) {
        if ((procedureElement == null) || (equipElement == null))
            return;
        Element equipElements = procedureElement.element("sdashboard");
        if (equipElements == null)
            equipElements = procedureElement.addElement("sdashboard");
        equipElements.add(equipElement);
    }

    public static void addUnsdashboard(Element procedureElement, Element equipElement) {
        if ((procedureElement == null) || (equipElement == null))
            return;
        Element equipElements = procedureElement.element("unsdashboard");
        if (equipElements == null)
            equipElements = procedureElement.addElement("unsdashboard");
        equipElements.add(equipElement);
    }

    public static void addImage(Element procedureElement, Element imageElement) {
        if ((procedureElement == null) || (imageElement == null))
            return;
        Element imageElements = procedureElement.element("images");
        if (imageElements == null)
            imageElements = procedureElement.addElement("images");
        imageElements.add(imageElement);
    }

    public static Document createDocument() {
        Document doc = DocumentHelper.createDocument();
        doc.setXMLEncoding("GBK");
        doc.addElement("technics");
        return doc;
    }

    public static Element createTechnics() {
        Element techElement = DocumentHelper.createElement("QMFawTechnicsInfo");

        setAttributeValue(techElement, "secretLevel", "内部");
        setAttributeValue(techElement, "treePath", "");
        setAttributeValue(techElement, "bsoID", "");
        setAttributeValue(techElement, "creator", "");
        setAttributeValue(techElement, "creatorOid", "");
        setAttributeValue(techElement, "occId", "");
        setAttributeValue(techElement, "material", "");
        setAttributeValue(techElement, "dutu", "");
        setAttributeValue(techElement, "remark", "");
        setAttributeValue(techElement, "useCount", "");
        setAttributeValue(techElement, "createTime", "");
        setAttributeValue(techElement, "modifyTime", "");
        setAttributeValue(techElement, "technicsName", "");
        setAttributeValue(techElement, "technicsNumber", "");
        setAttributeValue(techElement, "technicsType", "");
        setAttributeValue(techElement, "workShop", "");
        setAttributeValue(techElement, "productNumber", "");
        setAttributeValue(techElement, "productName", "");
        setAttributeValue(techElement, "partNumber", "");
        setAttributeValue(techElement, "partName", "");
        setAttributeValue(techElement, "parentPartNumber", "");
        setAttributeValue(techElement, "backupReason", "");
        setAttributeValue(techElement, "backupRate", "");
        setAttributeValue(techElement, "maxBackupCount", "");
        setAttributeValue(techElement, "maoWeight", "");
        setAttributeValue(techElement, "stateSize", "");
        setAttributeValue(techElement, "partCount", "");
        setAttributeValue(techElement, "partSize", "");
        setAttributeValue(techElement, "lifeCycleState", "");
        setAttributeValue(techElement, "lifecycle", "");
        setAttributeValue(techElement, "pbomLifecycle", "");
        setAttributeValue(techElement, "wrlFile", "");
        setAttributeValue(techElement, "version", "");
        setAttributeValue(techElement, "partType", "");
        setAttributeValue(techElement, "isKey", "");
        setAttributeValue(techElement, "isPartKey", "");
        setAttributeValue(techElement, "isSpecial", "");
        setAttributeValue(techElement, "systemID", "");
        setAttributeValue(techElement, "parentPartName", "");
        setAttributeValue(techElement, "partOid", "");
        setAttributeValue(techElement, "isSpecial", "");
        setAttributeValue(techElement, "partVersion", "");
        setAttributeValue(techElement, "e_version", "");
        setAttributeValue(techElement, "eu_version", "");
        setAttributeValue(techElement, "materialType", "");
        setAttributeValue(techElement, "modifyTime", "");
        setAttributeValue(techElement, "modifier", "");
        setAttributeValue(techElement, "parentPartOid", "");
        setAttributeValue(techElement, "unite", "");
        setAttributeValue(techElement, "isConnectNumber", "");
        /**工艺文件英文版 属性 add by liangbo 20170418*/
        setAttributeValue(techElement, "technicsEnglishName", "");
        setAttributeValue(techElement, "imageVersion", "");
        setAttributeValue(techElement, "vse", "");
        //TODO 工艺属性
        setAttributeValue(techElement, "technicsMethod", "");
        setAttributeValue(techElement, "fileCode", "");
        setAttributeValue(techElement, "madeDept", "");
        setAttributeValue(techElement, "secret", "");
        setAttributeValue(techElement, "secretDuetime", "");
        setAttributeValue(techElement, "technicsDesc", "");
        setAttributeValue(techElement, "phase", "");
        setAttributeValue(techElement, "mjde", "");
        setAttributeValue(techElement, "hzjs", "");

        //TODO 材料属性
        setAttributeValue(techElement, "ISCL", "");
        setAttributeValue(techElement, "BM", "");
        setAttributeValue(techElement, "MC", "");
        setAttributeValue(techElement, "GG", "");
        setAttributeValue(techElement, "ZGFBZHJSTJ", "");

        setAttributeValue(techElement, "XHPHCL", "");
        setAttributeValue(techElement, "CSIZE", "");
        setAttributeValue(techElement, "JSTJBZH", "");

        setAttributeValue(techElement, "isTabular", "");

        setAttributeValue(techElement, "MTYPE", "");
        setAttributeValue(techElement, "ZZCJ", "");
        setAttributeValue(techElement, "FZCJ", "");

        //设计资源库新增属性
        //start
        setAttributeValue(techElement, "SHORTNAME", "");
        setAttributeValue(techElement, "STANDARDNUMBER", "");
        setAttributeValue(techElement, "MECHANICALPROPERTYORHARDNESS", "");
        setAttributeValue(techElement, "SURFACETREATMENT", "");
        setAttributeValue(techElement, "HEATTREATMENT", "");
        setAttributeValue(techElement, "PRODUCTFORM", "");
        setAttributeValue(techElement, "PRODUCTLEVEL", "");
        setAttributeValue(techElement, "PLATECSCREWFORM", "");
        setAttributeValue(techElement, "ISIMPORT", "");
        setAttributeValue(techElement, "SPECIALINSTRUCTION", "");
        setAttributeValue(techElement, "MEASUREUNIT", "");
        setAttributeValue(techElement, "TYPE", "");
        setAttributeValue(techElement, "TYPESTANDARD", "");
        setAttributeValue(techElement, "QUALITYLEVEL", "");
        setAttributeValue(techElement, "TOTALSTANDARD", "");
        setAttributeValue(techElement, "DETAILSTANDARD", "");
        setAttributeValue(techElement, "PACKAGINGFORM", "");
        setAttributeValue(techElement, "OUTLINESIZE", "");
        setAttributeValue(techElement, "SPECIALCONDITION", "");
        setAttributeValue(techElement, "EXTRACONDITION", "");
        setAttributeValue(techElement, "MATTYPE", "");
        //end

        techElement.addElement("steps");
        techElement.addElement("materials");
        techElement.addElement("parts");
        techElement.addElement("additionaltables");
        techElement.addElement("borrowTechnicss");
        techElement.addElement("IBAAttibutes");

        techElement.addElement("CLDE");
        techElement.addElement("technicsStateTables");

        //return firstCreateProdcutStep(techElement);
        return techElement;
    }

    public static Element createReportTechnics() {
        Element techElement = DocumentHelper.createElement("XWReportTechnicsInfo");

        setAttributeValue(techElement, "technicsNumber", "");
        setAttributeValue(techElement, "pplanNumber", "");
        setAttributeValue(techElement, "version", "");
        setAttributeValue(techElement, "partVersion", "");
        setAttributeValue(techElement, "technicsName", "");
        setAttributeValue(techElement, "creator", "");
        setAttributeValue(techElement, "creatorOid", "");
        setAttributeValue(techElement, "creatorDisplay", "");
        setAttributeValue(techElement, "technicsType", "");
        setAttributeValue(techElement, "createTime", "");
        setAttributeValue(techElement, "modifyTime", "");
        setAttributeValue(techElement, "partNumber", "");
        setAttributeValue(techElement, "partName", "");
        setAttributeValue(techElement, "partOid", "");
        setAttributeValue(techElement, "partType", "");

        techElement.addElement("data");

        return techElement;
    }

    private static Element firstCreateProdcutStep(Element techEle) {
        for (int i = 1; i < 3; i++) {
            Element procedureEle = XmlUtility.createProcedure();
            int stepNumber = i * 10;
            XmlUtility.setAttributeValue(procedureEle, "stepNumber", stepNumber + "");
            List userlist = UserUtil.getCurrentUserOid();
            if ((userlist != null) && (userlist.size() == 3)) {
                String creator = (String) userlist.get(0);
                String creatorOid = (String) userlist.get(1);
                System.out.println("设置当前工序责任人为======" + creatorOid);
                XmlUtility.setAttributeValue(procedureEle, "responser", creatorOid);
            }
            String partOid = techEle.attributeValue("partOid");
            if (partOid != null && !"".equals(partOid)) {
                String responserGroup = "";
                try {
                    responserGroup = TechnicsIntf.getUsertechnicsGroupName(partOid);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                System.out.println("当前工艺零件======" + partOid + "===责任组==" + responserGroup);
                if (responserGroup == null)
                    responserGroup = "";
                XmlUtility.setAttributeValue(procedureEle, "responserGroup", responserGroup);
            }
            XmlUtility.addProcedure(techEle, procedureEle);
        }
        return techEle;
    }

    public static Element createProcedure() {
        Element procedureELement = DocumentHelper.createElement("QMProcedureInfo");
        procedureELement.setAttributeValue("bsoID", new UID().toString());
        procedureELement.setAttributeValue("stepNumber", "");
        procedureELement.setAttributeValue("stepName", "");
        procedureELement.setAttributeValue("workType", "");
        procedureELement.setAttributeValue("workShop", "");
        procedureELement.setAttributeValue("stepHour", "");
        procedureELement.setAttributeValue("isKey", "");
        procedureELement.setAttributeValue("creoView", "");
        procedureELement.setAttributeValue("cortonaID", "");
        procedureELement.setAttributeValue("procedureType", "");
        procedureELement.setAttributeValue("workSpace", "");
        procedureELement.setAttributeValue("workTypeID", "");
        procedureELement.setAttributeValue("workShopID", "");
        procedureELement.setAttributeValue("workSpaceID", "");
        procedureELement.setAttributeValue("preBsoID", "");
        procedureELement.setAttributeValue("nextBsoID", "");
        procedureELement.setAttributeValue("responser", "");
        procedureELement.setAttributeValue("responserGroup", "");
        procedureELement.addElement("procedureContent");
        procedureELement.addElement("paces");
        procedureELement.addElement("parts");
        procedureELement.addElement("equips");
        procedureELement.addElement("sdashboard");
        procedureELement.addElement("unsdashboard");
        procedureELement.addElement("tools");
        procedureELement.addElement("knifeTools");
        procedureELement.addElement("materials");
        procedureELement.addElement("images");
        procedureELement.addElement("IBAAttibutes");
        procedureELement.addElement("TechnicsDescribe");
        procedureELement.addElement("WXTechnicsDescribe");
        return procedureELement;
    }

    public static Element createPart() {
        Element partELement = DocumentHelper.createElement("QMPartInfo");
        partELement.setAttributeValue("partNumber", "");
        partELement.setAttributeValue("partName", "");
        partELement.setAttributeValue("material", "");
        partELement.setAttributeValue("dutu", "");
        partELement.setAttributeValue("remark", "");
        partELement.setAttributeValue("useCount", "");
        partELement.setAttributeValue("oid", "");
        partELement.setAttributeValue("partType", "");
        partELement.setAttributeValue("rate", "");
        partELement.setAttributeValue("isKey", "");
        partELement.setAttributeValue("isSpecial", "");
        partELement.setAttributeValue("materialType", "");
        partELement.setAttributeValue("backupRate", "");
        partELement.setAttributeValue("maxBackupCount", "");
        partELement.setAttributeValue("backupReason", "");
        partELement.setAttributeValue("workShop", "");
        partELement.setAttributeValue("materialNumber", "");
        partELement.setAttributeValue("materialName", "");
        partELement.setAttributeValue("materialBrand", "");
        partELement.setAttributeValue("materialCrision", "");
        partELement.setAttributeValue("responser", "");
        partELement.setAttributeValue("responserGroup", "");
        partELement.setAttributeValue("lifecycle", "");
        partELement.setAttributeValue("e_version", "");
        partELement.setAttributeValue("eu_vetion", "");
        partELement.setAttributeValue("version", "");
        partELement.setAttributeValue("occId", "");
        return partELement;
    }

    public static Element createTool() {
        Element toolELement = DocumentHelper.createElement("QMToolInfo");
        toolELement.setAttributeValue("bsoID", "");
        toolELement.setAttributeValue("toolNum", "");
        toolELement.setAttributeValue("toolName", "");
        toolELement.setAttributeValue("toolStdNum", "");
        toolELement.setAttributeValue("toolSpec", "");
        toolELement.setAttributeValue("toolType", "");
        toolELement.setAttributeValue("useCount", "");
        toolELement.setAttributeValue("oid", "");
        return toolELement;
    }

    public static Element createKnifeTool() {
        Element toolELement = DocumentHelper.createElement("QMKnifeToolInfo");
        toolELement.setAttributeValue("bsoID", "");
        toolELement.setAttributeValue("toolNum", "");
        toolELement.setAttributeValue("toolName", "");
        toolELement.setAttributeValue("toolStdNum", "");
        toolELement.setAttributeValue("knifetype", "");
        toolELement.setAttributeValue("csize", "");
        toolELement.setAttributeValue("oid", "");
        return toolELement;
    }

    public static Element createMaterial() {
        Element materialELement = DocumentHelper.createElement("QMMaterialInfo");
        materialELement.setAttributeValue("bsoID", "");
        materialELement.setAttributeValue("oid", "");
        materialELement.setAttributeValue("number", "");
        materialELement.setAttributeValue("toolTip", "");

        materialELement.setAttributeValue("materialNumber", "");
        materialELement.setAttributeValue("materialName", "");

        materialELement.setAttributeValue("clph", "");
        materialELement.setAttributeValue("clgg", "");
        materialELement.setAttributeValue("clbz", "");
        materialELement.setAttributeValue("jldw", "");

        materialELement.setAttributeValue("useCount", "");
        return materialELement;
    }

    public static Element createEquip() {
        Element equipELement = DocumentHelper.createElement("QMEquipmentInfo");
        equipELement.setAttributeValue("bsoID", "");
        equipELement.setAttributeValue("eqNum", "");
        equipELement.setAttributeValue("eqName", "");
        equipELement.setAttributeValue("eqModel", "");
        equipELement.setAttributeValue("useCount", "");
        equipELement.setAttributeValue("oid", "");
        return equipELement;
    }

    public static Element createMeasure() {
        Element equipELement = DocumentHelper.createElement("QMMeasureInfo");
        equipELement.setAttributeValue("bsoID", "");
        equipELement.setAttributeValue("number", "");
        equipELement.setAttributeValue("name", "");
        equipELement.setAttributeValue("pindex", "");
        equipELement.setAttributeValue("csize", "");
        equipELement.setAttributeValue("oid", "");
        return equipELement;
    }

    public static Element createWorkSpace() {
        Element equipELement = DocumentHelper.createElement("QMWorkSpaceInfo");
        equipELement.setAttributeValue("bsoID", "");
        equipELement.setAttributeValue("number", "");
        equipELement.setAttributeValue("name", "");
        equipELement.setAttributeValue("remark", "");
        equipELement.setAttributeValue("workplace", "");
        equipELement.setAttributeValue("oid", "");
        return equipELement;
    }

    public static Element createSDashboard() {
        Element equipELement = DocumentHelper.createElement("QMSDashboardInfo");
        equipELement.setAttributeValue("bsoID", "");
        equipELement.setAttributeValue("eqNum", "");
        equipELement.setAttributeValue("eqName", "");
        equipELement.setAttributeValue("eqModel", "");
        equipELement.setAttributeValue("useCount", "");
        equipELement.setAttributeValue("oid", "");
        return equipELement;
    }

    public static Element createUnSDashboard() {
        Element equipELement = DocumentHelper.createElement("QMUnSDashboardInfo");
        equipELement.setAttributeValue("bsoID", "");
        equipELement.setAttributeValue("eqNum", "");
        equipELement.setAttributeValue("eqName", "");
        equipELement.setAttributeValue("eqModel", "");
        equipELement.setAttributeValue("useCount", "");
        equipELement.setAttributeValue("oid", "");
        return equipELement;
    }

    public static Element createImage() {
        Element imageELement = DocumentHelper.createElement("PDrawingInfo");
        imageELement.setAttributeValue("bsoID", "");
        imageELement.setAttributeValue("drawingName", "");
        imageELement.setAttributeValue("drawingType", "");
        imageELement.setAttributeValue("drawingSize", "");
        imageELement.setAttributeValue("absolutePath", "");
        return imageELement;
    }

    public static void deleteElement(Element parentElement, Element childElement) {
        if ((parentElement == null) || (childElement == null))
            return;
        parentElement.remove(childElement);
    }

    public static void deleteAllChildElements(Element parentElement) {
        if (parentElement == null)
            return;
        Iterator it = parentElement.elementIterator();
        if (it != null) {
            while (it.hasNext()) {
                Element childElement = (Element) it.next();
                parentElement.remove(childElement);
            }
        }
    }

    public static void updateElementByKey(Element element, String key, String value) {
        if (key == null) {
            return;
        }
        Iterator it = element.elementIterator();
        if (it != null) {
            boolean flag = false;
            while (it.hasNext()) {
                Element childElement = (Element) it.next();
                String k = childElement.attributeValue("key");
                if (k != null && (k.equals(key)
                        || (k.contains("件面积Cr2") && key.contains("件面积Cr2"))
                        || (k.contains("件涂料消耗量kg") && key.contains("件涂料消耗量kg")))) {
                    if ((k.contains("件面积Cr2") && key.contains("件面积Cr2"))
                            || (k.contains("件涂料消耗量kg") && key.contains("件涂料消耗量kg"))) {
                        childElement.setAttributeValue("key", key);
                    }
                    childElement.setAttributeValue("value", value);
                    flag = true;
                }
            }
            if (!flag) {
                Element newElement = DocumentHelper.createElement("attribute");
                setAttributeValue(newElement, "key", key);
                setAttributeValue(newElement, "value", value);
                element.add(newElement);
            }
        }
    }

    public static boolean compare(Element one, Element another) {
        return false;
    }

    public static void setProcedureContent(Element procedureElement, String content) {
        if ((procedureElement == null) || (content == null))
            return;
        Element procedureContentElement = procedureElement.element("procedureContent");
        if (procedureContentElement == null)
            procedureContentElement = procedureElement.addElement("procedureContent");
//		while (content.indexOf("\n") != -1) {
//			String s = content.replace("\n", "@#$");
//			content = s;
//		}
        procedureContentElement.setText(content);
    }

    //写入检验方法 add by zhuhao 2017.10.20
    public static void setCheckMethodContent(Element procedureElement, String content) {
        if ((procedureElement == null) || (content == null))
            return;
        Element procedureContentElement = procedureElement.element("checkmethodContent");
        if (procedureContentElement == null)
            procedureContentElement = procedureElement.addElement("checkmethodContent");
        procedureContentElement.setText(content);
    }

    //写入检验内容 add by zhuhao 2017.10.20
    public static void setCheckContentContent(Element procedureElement, String content) {
        if ((procedureElement == null) || (content == null))
            return;
        Element procedureContentElement = procedureElement.element("checkcontentContent");
        if (procedureContentElement == null)
            procedureContentElement = procedureElement.addElement("checkcontentContent");
        procedureContentElement.setText(content);
    }

    public static void setTechnicsDescribe(Element technicsDescribe, String content) {
        if ((technicsDescribe == null) || (content == null))
            return;
        Element technicsDescribeElement = technicsDescribe.element("TechnicsDescribe");
        if (technicsDescribeElement == null)
            technicsDescribeElement = technicsDescribe.addElement("TechnicsDescribe");
        technicsDescribeElement.setText(content);
    }

    public static void setWXTechnicsDescribe(Element technicsDescribe, String content) {
        if ((technicsDescribe == null) || (content == null))
            return;
        Element technicsDescribeElement = technicsDescribe.element("WXTechnicsDescribe");
        if (technicsDescribeElement == null)
            technicsDescribeElement = technicsDescribe.addElement("WXTechnicsDescribe");
        technicsDescribeElement.setText(content);
    }

    public static void setGyztContent(Element procedureElement, String content) {
        if ((procedureElement == null) || (content == null))
            return;
        Element procedureContentElement = procedureElement.element("gyzt");
        if (procedureContentElement == null)
            procedureContentElement = procedureElement.addElement("gyzt");
//		while (content.indexOf("\n") != -1) {
//			String s = content.replace("\n", "@#$");
//			content = s;
//		}
        procedureContentElement.setText(content);
    }

    public static void setAttributeValue(Element element, String attrName,
                                         String attrValue) {
        if ((element == null) || (attrName == null) || (attrValue == null))
            return;
        Attribute attr = element.attribute(attrName);
        if (attr == null)
            element.setAttributeValue(attrName, attrValue);
        else
            attr.setValue(attrValue);
    }

    public static String getAttributeValue(Element element, String attrName) {
        if ((element == null) || (attrName == null))
            return null;
        Attribute attr = element.attribute(attrName);
        if (attr == null)
            return null;
        return attr.getValue();
    }

    public static int countSteps(Element technicsElement) {
        Element procedureElements = getProcedures(technicsElement);
        List list = procedureElements.elements();
        if ((list == null) || (list.size() == 0))
            return 0;
        return list.size();
    }

    public static Element getStep(Element technicsElement, int index) {
        Element procedureElements = getProcedures(technicsElement);
        List list = procedureElements.elements();
        if ((list == null) || (list.size() == 0))
            return null;
        if ((index < 0) || (index >= list.size()))
            return null;
        return (Element) list.get(index);
    }

    public static Element getStepByStepNumber(Element technicsElement,
                                              String number) {
        if ((technicsElement == null) || (number == null)
                || (number.replaceAll("　", " ").trim().length() == 0))
            return null;
        number = toSemiangle(number);

        List list = getProcedures(technicsElement).elements();
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            Element procedueEle = (Element) list.get(i);
            String temp = getAttributeValue(procedueEle, "stepNumber");
            if (temp != null) {
                if (temp.equals(number)) {
                    return procedueEle;
                }

            }

        }

        return null;
    }

    public static Element getStepByStepOid(Element technicsElement, String oid) {
        List list = getProcedures(technicsElement).elements();
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            Element procedueEle = (Element) list.get(i);
            String temp = getAttributeValue(procedueEle, "bsoID");
            if (temp != null) {
                if (temp.equals(oid)) {
                    return procedueEle;
                }
                List<Element> paces = getAllPaces(procedueEle);
                if (paces != null && paces.size() != 0) {
                    for (Element pace : paces) {
                        String paceOid = getAttributeValue(pace, "bsoID");
                        if (paceOid != null) {
                            if (paceOid.equals(oid)) {
                                return pace;
                            }
                        }
                    }
                }
            }

        }

        return null;
    }

    public static Element getStepByCortonaID(Element technicsElement,
                                             String cortonaID) {
        if ((technicsElement == null) || (cortonaID == null)
                || (cortonaID.replaceAll("　", " ").trim().length() == 0))
            return null;
        List list = getProcedures(technicsElement).elements();
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            Element procedueEle = (Element) list.get(i);
            String temp = getAttributeValue(procedueEle, "cortonaID");
            if (temp != null) {
                if (temp.equals(cortonaID))
                    return procedueEle;
            }
        }
        return null;
    }

    public static int getStepIndex(Element technicsElement, Element stepElement) {
        if ((technicsElement == null) || (stepElement == null))
            return -1;
        List list = getProcedures(technicsElement).elements();
        if ((list == null) || (list.size() == 0))
            return -1;
        String curBsoID = stepElement.attributeValue("bsoID");
        for (int i = 0; i < list.size(); i++) {
            Element procedueEle = (Element) list.get(i);
            String temp = getAttributeValue(procedueEle, "bsoID");
            if (temp != null) {
                if (temp.equals(curBsoID))
                    return i;
            }
        }
        return -1;
    }

    public static Element getPaceByCortonaID(Element stepElement,
                                             String cortonaID) {
        if ((stepElement == null) || (cortonaID == null)
                || (cortonaID.trim().length() == 0))
            return null;
        List list = getPaces(stepElement).elements();
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            Element pace = (Element) list.get(i);
            String temp = getAttributeValue(pace, "cortonaID");
            if (temp != null) {
                if (temp.equals(cortonaID))
                    return pace;
            }
        }
        return null;
    }

    public static int getPaceIndex(Element stepElement, Element paceElement) {
        if ((stepElement == null) || (paceElement == null))
            return -1;
        List list = getPaces(stepElement).elements();
        if ((list == null) || (list.size() == 0)) {
            return -1;
        }
        String curID = paceElement.attributeValue("bsoID");
        for (int i = 0; i < list.size(); i++) {
            Element pace = (Element) list.get(i);
            String temp = getAttributeValue(pace, "bsoID");
            if (temp != null) {
                if (temp.equals(curID))
                    return i;
            }
        }
        return -1;
    }

    public static Element getStepByID(Element technicsElement, String bsoID) {
        System.out.println(bsoID);
        if ((technicsElement == null) || (bsoID == null))
            return null;
        List list = getProcedures(technicsElement).elements();
        if ((list == null) || (list.size() == 0))
            return null;
        for (int i = 0; i < list.size(); i++) {
            Element procedueEle = (Element) list.get(i);
            String temp = getAttributeValue(procedueEle, "bsoID");
            if (temp != null) {
                if (temp.equals(bsoID))
                    return procedueEle;
            }
        }
        return null;
    }


    public static int countPaces(Element procedureElement) {
        Element paceElements = getPaces(procedureElement);
        List list = paceElements.elements();
        if ((list == null) || (list.size() == 0))
            return 0;
        return list.size();
    }

    public static Element getPace(Element procedureElement, int index) {
        Element paceElements = getPaces(procedureElement);
        List list = paceElements.elements();
        if ((list == null) || (list.size() == 0))
            return null;
        if ((index < 0) || (index >= list.size()))
            return null;
        return (Element) list.get(index);
    }

    public static void orderSteps(Element techElement) {
        if (techElement == null)
            return;
        Element stepGroup = getProcedures(techElement);
        List<Element> list = stepGroup.elements();
        if ((list == null) || (list.size() == 0))
            return;
        Vector<Element> vec = new Vector<Element>();
        for (int i = 0; i < list.size(); i++) {
            Element step = (Element) list.get(i);
            vec.add(step);
        }

        ElementComparator ec = new ElementComparator();
        Collections.sort(vec, ec);
        for (int i = 0; i < vec.size(); i++) {
            Element step = (Element) vec.get(i);
            step.detach();
            stepGroup.add(step);
        }
    }

    public static void orderPaces(Element stepElement) {
        if (stepElement == null)
            return;
        Element paceGroup = getPaces(stepElement);
        List list = paceGroup.elements();
        if ((list == null) || (list.size() == 0))
            return;
        Vector<Element> vec = new Vector<Element>();
        for (int i = 0; i < list.size(); i++) {
            Element pace = (Element) list.get(i);
            vec.add(pace);
        }
        ElementComparator ec = new ElementComparator();
        Collections.sort(vec, ec);
        for (int i = 0; i < vec.size(); i++) {
            Element pace = (Element) vec.get(i);
            pace.detach();
            paceGroup.add(pace);
        }
    }

    public static void reSetStepNumbers(Element techElement) {
        if (techElement == null)
            return;
        Element stepGroup = getProcedures(techElement);
        List<Element> list = stepGroup.elements();
        if ((list == null) || (list.size() == 0))
            return;
        for (int i = 0; i < list.size(); i++) {
            int num = (i + 1) * 10;
            Element step = (Element) list.get(i);
            String oldnum = getAttributeValue(step, "stepNumber");
            int number = getSubFigure(oldnum);
            String value = "";
            if (number == -1) {
                value = num + oldnum;
            } else {
                value = oldnum.replaceFirst(String.valueOf(number),
                        String.valueOf(num));
            }
            setAttributeValue(step, "stepNumber", value);
        }
    }

    public static void reSetPaceNumbers(Element stepElement) {
        if (stepElement == null)
            return;
        Element paceGroup = getPaces(stepElement);
        List list = paceGroup.elements();
        if ((list == null) || (list.size() == 0))
            return;
        int num = 1;
        for (int i = 0; i < list.size(); i++) {
            Element pace = (Element) list.get(i);
            String temp = getAttributeValue(pace, "stepNumber");
            if ((temp != null) && (temp.startsWith("-"))) {
                setAttributeValue(pace, "stepNumber", "-" + num);
                num++;
            }
        }
    }

    public static List<Element> getAllSteps(Element techElemnt) {
        Element procedureElements = getProcedures(techElemnt);
        return procedureElements.elements();
    }

    public static List<Element> getAllStepsOrderByStepNumber(Element techElemnt) {
        List<Element> list = getAllSteps(techElemnt);
        XPath xpath = new NumberXPath("@stepNumber");
        xpath.sort(list);
        return list;
    }

    public static List getAllPaces(Element stepElement) {
        Element paceElement = getPaces(stepElement);
        return paceElement.elements();
    }

    public static List gatDrawings(Element procedureElement) {
        Element drawingElement = procedureElement.element("images");
        return drawingElement.elements();
    }

    public static void addDrawings(List list, Vector vector) {
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Element drawingElement = (Element) it.next();
            if (!drawingElement.attributeValue("absolutePath").equals(""))
                vector.add(drawingElement);
        }
    }

    public static Vector getAllDrawings(Element techElement) {
        Vector vec = new Vector();
        List stepList = getAllSteps(techElement);
        for (Iterator it = stepList.iterator(); it.hasNext(); ) {
            Element stepElement = (Element) it.next();
            addDrawings(gatDrawings(stepElement), vec);
            List paceList = getAllPaces(stepElement);
            for (Iterator ite = paceList.iterator(); ite.hasNext(); ) {
                Element paceElement = (Element) ite.next();
                addDrawings(gatDrawings(paceElement), vec);
            }
        }
        return vec;
    }

    public static void writeDrawingFiles(Element element, String technicsCategory, String technicsNumber,
                                         String technicsName, String sourcepath, boolean flag)
            throws Exception {
        String technicsDirectory = "";
        if ("rework".equals(technicsCategory)) {
            technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
        } else if ("temp".equals(technicsCategory)) {
            technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
        } else {
            technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
        }
        String path = element.attributeValue("absolutePath");
        String timeFolder = path.substring(0, path.lastIndexOf("/") + 1);
        File timeFile = new File(technicsDirectory + "/" + timeFolder + "/");
        if (timeFile.exists())
            return;
        if (!timeFile.exists())
            timeFile.mkdirs();
        String targetFileName = technicsDirectory + "/" + path;
        AutoGenerateStepsUtil.copyFile(sourcepath, targetFileName);
        if (flag) {

            if (targetFileName.endsWith(".jpg") || targetFileName.endsWith(".gif")
                    || targetFileName.endsWith(".png")
                    || targetFileName.endsWith(".bmp")
                    ) {

            } else {
                InputStream is = NewDrawingJPanel.class.getResourceAsStream("/image/defaultPV.jpg");
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                String jpgName = targetFileName.substring(0, targetFileName.lastIndexOf(".")) + "" + "_short.jpg";
                FileUtil.writeBytes(jpgName, bytes);
            }
        }
    }

    public static void writeDrawingFiles(Element element, String technicsCategory, String technicsNumber,
                                         String technicsName, String sourcepath, String targetPath, boolean flag)
            throws Exception {
        String technicsDirectory = "";
        if ("rework".equals(technicsCategory)) {
            technicsDirectory = WorkSpaceUtil.getReworkTechnicsDirectory(technicsNumber, technicsName);
        } else if ("temp".equals(technicsCategory)) {
            technicsDirectory = WorkSpaceUtil.getTempTechnicsDirectory(technicsNumber, technicsName);
        } else {
            technicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
        }
        String path = element.attributeValue("absolutePath");
        String timeFolder = path.substring(0, path.lastIndexOf("/") + 1);
        File timeFile = new File(technicsDirectory + "/" + timeFolder + "/");
        if (timeFile.exists())
            return;
        if (!timeFile.exists())
            timeFile.mkdirs();
//		String targetFileName = technicsDirectory + "/" + timeFolder + "/" + new File(sourcepath).getName();
        targetPath = technicsDirectory + "/" + targetPath;
        AutoGenerateStepsUtil.copyFile(sourcepath, targetPath);
        if (flag) {

            if (targetPath.endsWith(".jpg") || targetPath.endsWith(".gif")
                    || targetPath.endsWith(".png")
                    || targetPath.endsWith(".bmp")
                    ) {

            } else {
                InputStream is = NewDrawingJPanel.class.getResourceAsStream("/image/defaultPV.jpg");
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                String jpgName = targetPath.substring(0, targetPath.lastIndexOf(".")) + "" + "_short.jpg";
                FileUtil.writeBytes(jpgName, bytes);
            }
        }
    }


    public static Element getEquip(Element step, String oid) {
        if ((step != null) && (oid != null)) {
            Element equip_Group = getEquips(step);
            List list = equip_Group.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element equip = (Element) list.get(i);
                    String temp = equip.attributeValue("oid");
                    if (temp != null) {
                        if (temp.equals(oid))
                            return equip;
                    }
                }
            }
        }
        return null;
    }


    public static Element getMeasure(Element step, String oid) {
        if ((step != null) && (oid != null)) {
            Element equip_Group = getMeasures(step);
            List list = equip_Group.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element equip = (Element) list.get(i);
                    String temp = equip.attributeValue("oid");
                    if (temp != null) {
                        if (temp.equals(oid))
                            return equip;
                    }
                }
            }
        }
        return null;
    }

    public static Element getSDashboard(Element step, String oid) {
        if ((step != null) && (oid != null)) {
            Element equip_Group = getSDashboards(step);
            List list = equip_Group.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element equip = (Element) list.get(i);
                    String temp = equip.attributeValue("oid");
                    if (temp != null) {
                        if (temp.equals(oid))
                            return equip;
                    }
                }
            }
        }
        return null;
    }

    public static Element getUnSDashboard(Element step, String oid) {
        if ((step != null) && (oid != null)) {
            Element equip_Group = getUnsdashboards(step);
            List list = equip_Group.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element equip = (Element) list.get(i);
                    String temp = equip.attributeValue("oid");
                    if (temp != null) {
                        if (temp.equals(oid))
                            return equip;
                    }
                }
            }
        }
        return null;
    }

    public static Element getTool(Element step, String oid) {
        if ((step != null) && (oid != null)) {
            Element tool_Group = getTools(step);
            List list = tool_Group.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element tool = (Element) list.get(i);
                    String temp = tool.attributeValue("oid");
                    if (temp != null) {
                        if (temp.equals(oid))
                            return tool;
                    }
                }
            }
        }
        return null;
    }

    public static Element getKnifeTool(Element step, String oid) {
        if ((step != null) && (oid != null)) {
            Element tool_Group = getKnifeTools(step);
            List list = tool_Group.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element tool = (Element) list.get(i);
                    String temp = tool.attributeValue("oid");
                    if (temp != null) {
                        if (temp.equals(oid))
                            return tool;
                    }
                }
            }
        }
        return null;
    }

    public static Element getMaterial(Element step, String oid) {
        if ((step != null) && (oid != null)) {
            Element material_Group = getMaterials(step);
            List list = material_Group.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element material = (Element) list.get(i);
                    String temp = material.attributeValue("oid");
                    if (temp != null) {
                        if (temp.equals(oid))
                            return material;
                    }
                }
            }
        }
        return null;
    }

    public static int compareStepNumber(String stepNumber1, String stepNumber2) {
        if ((stepNumber1 == null) || (stepNumber2 == null))
            return 0;
        int num1 = getSubFigure(stepNumber1);
        int num2 = getSubFigure(stepNumber2);
        if ((num1 == -1) && (num2 == -1)) {
            int len1 = stepNumber1.length();
            int len2 = stepNumber2.length();
            if (len1 == len2) {
                return stepNumber1.compareTo(stepNumber2);
            }

            if (len1 == Math.max(len1, len2)) {
                return -1;
            }

            return 1;
        }

        if ((num1 != -1) && (num2 != -1)) {
            return num1 - num2;
        }

        if (num1 == Math.max(num1, num2)) {
            return -1;
        }

        return 1;
    }

    public static int getSubFigure(String str) {
        if ((str == null) || (str.trim().length() == 0))
            return -1;
        char[] data = str.toCharArray();
        StringBuffer buffer = new StringBuffer();
        boolean flag = false;
        for (int i = 0; i < data.length; i++) {
            if (Character.isDigit(data[i])) {
                if (!flag) {
                    flag = true;
                }
                buffer.append(data[i]);
            } else {
                if (flag)
                    break;
            }
        }
        String temp = buffer.toString();
        if ((temp == null) || (temp.trim().length() == 0))
            return -1;
        int result = Integer.parseInt(temp);
        result = Math.abs(result);
        return result;
    }

    public static String getLetters(String str) {
        if ((str == null) || (str.trim().length() == 0))
            return "";
        char[] data = str.toCharArray();
        int loc = -1;
        for (int i = 0; i < data.length; i++) {
            if (Character.isLetter(data[i])) {
                loc = i;
                break;
            }
        }
        if (loc == -1)
            return "";
        return str.substring(loc);
    }

    public static int nextPaceNumber(Element step) {
        Element paces = getPaces(step);
        List list = paces.elements();
        if ((list == null) || (list.size() == 0))
            return -1;
        int number = 0;
        for (int i = 0; i < list.size(); i++) {
            Element pace = (Element) list.get(i);
            String num = getAttributeValue(pace, "stepNumber");
            if (num.startsWith("-")) {
                int temp = getSubFigure(num);
                if (number < temp)
                    number = temp;
            }
        }
        number++;
        return -number;
    }

    public static Element getPaceByPaceNumber(Element stepElement, String number) {
        if ((stepElement == null) || (number == null)
                || (number.trim().length() == 0))
            return null;
        if (number.trim().equals("检"))
            return null;
        number = toSemiangle(number);

        List list = getPaces(stepElement).elements();
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            Element pace = (Element) list.get(i);
            String temp = getAttributeValue(pace, "stepNumber");
            if (temp != null) {
                if (temp.equals(number)) {
                    return pace;
                }

            }

        }

        return null;
    }

    public static List getAllCheckParamTableIdentify(Element stepElement) {
        ArrayList ckList = new ArrayList();
        if (stepElement != null) {
            Element checkGroup = getCheckRecord(stepElement);
            List list = checkGroup.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element check = (Element) list.get(i);
//					String ckNumber = getAttributeValue(check, "number");
                    String ckName = getAttributeValue(check, "name");
                    ckList.add(ckName);
                }
            }
        }
        return ckList;
    }

    public static Vector getAllEquipmentIdentify(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element equipGroup = getEquips(stepElement);
            List list = equipGroup.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element equip = (Element) list.get(i);
                    String eqNumber = getAttributeValue(equip, "number");
                    String eqName = getAttributeValue(equip, "name");
                    vec.add(eqNumber + "_" + eqName);
                }
            }
        }
        return vec;
    }

    /**
     * 获取工艺文件xml中所有标准仪器表Element对象
     *
     * @param stepElement
     * @return
     * @author 马崇奇
     * @date 2015-6-3
     */

    public static Vector getAllSdashboardIdentify(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element sdashGroup = getSDashboards(stepElement);
            List list = sdashGroup.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element sdashboard = (Element) list.get(i);
                    String sdashboardNumber = getAttributeValue(sdashboard, "number");
                    String sdashboardName = getAttributeValue(sdashboard, "name");
                    vec.add(sdashboardNumber + "_" + sdashboardName);
                }
            }
        }
        return vec;
    }

    /**
     * 获取工艺文件xml中所有非标准仪器表Element对象
     *
     * @param stepElement
     * @return
     * @author 马崇奇
     * @date 2015-6-3
     */

    public static Vector getAllUnSdashboardIdentify(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element unSdashGroup = getUnsdashboards(stepElement);
            List list = unSdashGroup.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element unsdashboard = (Element) list.get(i);
                    String unsdashboardNumber = getAttributeValue(unsdashboard, "number");
                    String unsdashboardName = getAttributeValue(unsdashboard, "name");
                    vec.add(unsdashboardNumber + "_" + unsdashboardName);
                }
            }
        }
        return vec;
    }

    /**
     * 获取工艺文件xml中所有量具Element对象
     *
     * @param stepElement
     * @return
     * @author 马崇奇
     * @date 2015-6-3
     */

    public static Vector getAllMeasuresIdentify(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element measuresGroup = getMeasures(stepElement);
            List list = measuresGroup.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element measures = (Element) list.get(i);
                    String measuresNumber = getAttributeValue(measures, "number");
                    String measuresName = getAttributeValue(measures, "name");
                    vec.add(measuresNumber + "_" + measuresName);
                }
            }
        }
        return vec;
    }


    public static Vector getAllImgIdentify(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element imgGroup = getImages(stepElement);
            List list = imgGroup.elements();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Element img = (Element) list.get(i);
                    String name = getAttributeValue(img, "drawingName");
                    vec.add(name);
                }
            }
        }
        return vec;
    }

    public static Vector getAllPartIdentify(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element equipGroup = getParts(stepElement);
            System.out.println(stepElement.attributeValue("stepNumber"));
            List list = equipGroup.elements("QMPartInfo");
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element part = (Element) list.get(i);
                    String number = part.attributeValue("partNumber");
                    if (number == null)
                        number = "";
                    String name = part.attributeValue("partName");
                    if (name == null)
                        name = "";
                    String version = part.attributeValue("version");
                    if (version == null)
                        version = "";
                    String identify = number + "(" + name + ") " + version;
                    String type = part.attributeValue("partType");
                    String count = part.attributeValue("useCount");
                    if ((count == null) || (count.trim().length() == 0))
                        count = "1";
                    vec.add(identify + "$" + type + "#" + count);
                }
            }
        }
        return vec;
    }


    public static List<Element> getAllPartElement(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element equipGroup = getParts(stepElement);
            List<Element> list = equipGroup.elements("QMPartInfo");
            return list;
        }
        return null;
    }

    public static Vector getAllToolIdentify(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element toolGroup = getTools(stepElement);
            List list = toolGroup.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element tool = (Element) list.get(i);
                    String toolNumber = getAttributeValue(tool, "toolNum");
                    String toolName = getAttributeValue(tool, "toolName");
                    vec.add(toolNumber + "_" + toolName);
                }
            }
        }
        return vec;
    }

    public static Vector getAllKnifeToolIdentify(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element toolGroup = getKnifeTools(stepElement);
            List list = toolGroup.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element tool = (Element) list.get(i);
                    String toolNumber = getAttributeValue(tool, "toolNum");
                    String toolName = getAttributeValue(tool, "toolName");
                    vec.add(toolNumber + "_" + toolName);
                }
            }
        }
        return vec;
    }

    public static Vector getAllMaterialIdentify(Element stepElement) {
        Vector vec = new Vector();
        if (stepElement != null) {
            Element materialGroup = getMaterials(stepElement);
            List list = materialGroup.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element material = (Element) list.get(i);
                    String materialNumber = getAttributeValue(material,
                            "materialNumber");
                    String materialName = getAttributeValue(material,
                            "materialName");
                    vec.add(materialNumber + "_" + materialName);
                }
            }
        }
        return vec;
    }

    public static boolean hasKeyStepInTechnics(Element technicsElement) {
        List list = getAllSteps(technicsElement);
        if ((list != null) && (list.size() > 0)) {
            for (int i = 0; i < list.size(); i++) {
                Element e = (Element) list.get(i);
                hasKeyPaceInStep(e);
                String isKey = getAttributeValue(e, "isKey");
                if (isKey.equalsIgnoreCase("true")) {
                    technicsElement.setAttributeValue("isKey", "true");
                    return true;
                }
            }
        }
        String isPartKey = technicsElement.attributeValue("isPartKey");
        if ((isPartKey == null) || (!isPartKey.equalsIgnoreCase("true")))
            technicsElement.setAttributeValue("isKey", "false");
        return false;
    }

    public static boolean hasKeyPaceInStep(Element stepElement) {
        List list = getAllPaces(stepElement);
        if ((list != null) && (list.size() > 0)) {
            for (int i = 0; i < list.size(); i++) {
                Element e = (Element) list.get(i);
                String isKey = getAttributeValue(e, "isKey");
                if (isKey.equalsIgnoreCase("true")) {
                    //stepElement.setAttributeValue("isKey", "true");
                    return true;
                }
            }
        }
        //stepElement.setAttributeValue("isKey", "false");
        return false;
    }

    public static String toSemiangle(String src) {
        if (src == null)
            return null;
        if (src.length() == 0)
            return "";
        char[] c = src.toCharArray();
        for (int index = 0; index < c.length; index++) {
            if (c[index] == '　')
                c[index] = ' ';
            else if ((c[index] > 65280) && (c[index] < 65375)) {
                c[index] = ((char) (c[index] - 65248));
            }
        }
        return String.valueOf(c);
    }

    public static String getCurrentTime() {
//		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		Date date = new Date(System.currentTimeMillis());
//		System.out.println("时间："+sf.format(date));
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = cal.getTimeZone();
        TimeZone time = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(time);
        TimeZone.setDefault(time);
        String timeArea = calendar.getTimeZone().getDisplayName();
        SimpleDateFormat fromart = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = calendar.getTime();
        return fromart.format(date);
    }

    public static Vector getPaceNumberCashe() {
        Vector cashe = new Vector();
        StringTokenizer st = new StringTokenizer("检;注;A;B;C;D;E;F;G", ";");
        while (st.hasMoreTokens()) {
            String temp = st.nextToken();
            cashe.add(temp);
        }
        return cashe;
    }

    public static void deleteOnePart(Element pace, Vector fList) {
        if (pace == null)
            return;
        if ((fList == null) || (fList.size() == 0))
            return;
        Element ele = pace.element("parts");
        if (ele != null) {
            List list = ele.elements();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Element part = (Element) list.get(i);
                    String occId = part.attributeValue("occId");

                    if (occId != null) {
                        if (fList.contains(occId)) {
                            list.remove(part);
                            i--;
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过制定当然零部件修改工艺的属性
     *
     * @param techElement 工艺
     * @param partElement 零部件
     */
    public static void modifyTechnicsAttr(Element techElement, Element partElement) {
        XmlUtility.setAttributeValue(techElement, "treePath", BomXMLUtil.getPath(partElement));
        XmlUtility.setAttributeValue(techElement, "partNumber", partElement.attributeValue("partNumber"));// 设置部件图号
        XmlUtility.setAttributeValue(techElement, "partName", partElement.attributeValue("partName"));// 设置部件名称

        XmlUtility.setAttributeValue(techElement, "partOid", partElement.attributeValue("oid"));// 设置零部件oid
        XmlUtility.setAttributeValue(techElement, "partVersion", partElement.attributeValue("partVersion"));
        XmlUtility.setAttributeValue(techElement, "materialType", partElement.attributeValue("materialType"));
        XmlUtility.setAttributeValue(techElement, "workShop", partElement.attributeValue("workShop"));
        XmlUtility.setAttributeValue(techElement, "backupRate", partElement.attributeValue("backupRate"));
        XmlUtility.setAttributeValue(techElement, "maxBackupCount", partElement.attributeValue("maxBackupCount"));
        XmlUtility.setAttributeValue(techElement, "backupReason", partElement.attributeValue("backupReason"));
        XmlUtility.setAttributeValue(techElement, "isKey", partElement.attributeValue("isKey"));
        XmlUtility.setAttributeValue(techElement, "isSpecial", partElement.attributeValue("isSpecial"));
        XmlUtility.setAttributeValue(techElement, "partType", partElement.attributeValue("partType"));
        XmlUtility.setAttributeValue(techElement, "pbomLifecycle", partElement.attributeValue("pbomLifecycle"));

        XmlUtility.setAttributeValue(techElement, "eu_version", partElement.attributeValue("eu_version"));
        XmlUtility.setAttributeValue(techElement, "e_version", partElement.attributeValue("e_version"));
        XmlUtility.setAttributeValue(techElement, "partVersion", partElement.attributeValue("version"));

        XmlUtility.setAttributeValue(techElement, "isPartKey", partElement.attributeValue("isKey"));
        XmlUtility.setAttributeValue(techElement, "modifyTime", XmlUtility.getCurrentTime());

        XmlUtility.setAttributeValue(techElement, "occId", partElement.attributeValue("occId"));
        XmlUtility.setAttributeValue(techElement, "material", partElement.attributeValue("material"));
        XmlUtility.setAttributeValue(techElement, "dutu", partElement.attributeValue("dutu"));
        XmlUtility.setAttributeValue(techElement, "remark", partElement.attributeValue("remark"));
        XmlUtility.setAttributeValue(techElement, "useCount", partElement.attributeValue("useCount"));

        XmlUtility.setAttributeValue(techElement, "XHPHCL", partElement.attributeValue("XHPHCL"));
        XmlUtility.setAttributeValue(techElement, "CSIZE", partElement.attributeValue("CSIZE"));
        XmlUtility.setAttributeValue(techElement, "JSTJBZH", partElement.attributeValue("JSTJBZH"));

        XmlUtility.setAttributeValue(techElement, "CMAT_UP", partElement.attributeValue("CMAT_UP"));
        XmlUtility.setAttributeValue(techElement, "CMAT_DOWN", partElement.attributeValue("CMAT_DOWN"));
        XmlUtility.setAttributeValue(techElement, "PZGGBZH", partElement.attributeValue("PZGGBZH"));
        XmlUtility.setAttributeValue(techElement, "JDDJ", partElement.attributeValue("JDDJ"));
        XmlUtility.setAttributeValue(techElement, "CLZT", partElement.attributeValue("CLZT"));
        XmlUtility.setAttributeValue(techElement, "ZLDJ", partElement.attributeValue("ZLDJ"));
        XmlUtility.setAttributeValue(techElement, "ZQCLBZH", partElement.attributeValue("ZQCLBZH"));
        XmlUtility.setAttributeValue(techElement, "ZQCLBZH", partElement.attributeValue("ZQCLBZH"));
        XmlUtility.setAttributeValue(techElement, "ZQCLMC", partElement.attributeValue("ZQCLMC"));
        XmlUtility.setAttributeValue(techElement, "XHPH", partElement.attributeValue("XHPH"));
        XmlUtility.setAttributeValue(techElement, "JSTJ", partElement.attributeValue("JSTJ"));
        XmlUtility.setAttributeValue(techElement, "JBCLMC", partElement.attributeValue("JBCLMC"));
        XmlUtility.setAttributeValue(techElement, "CMAT", partElement.attributeValue("CMAT"));
    }

    public static Element createTechnicsIBAAttriElement(String key, String value) {
        Element element = DocumentHelper.createElement("attribute");
        XmlUtility.setAttributeValue(element, "key", key);
        XmlUtility.setAttributeValue(element, "value", value);
        return element;
    }

    public static List<Element> getDrawings(Element procedureElement) {
        if (procedureElement != null) {
            Element drawingElement = procedureElement.element("images");
            return drawingElement.elements();
        }
        return null;
    }

    //控制内容
    public static void setKZNRContent(Element element,
                                      String kznrspeCharPanelText) {
        if ((element == null) || (kznrspeCharPanelText == null))
            return;
        Element procedureContentElement = element.element("kznrContent");
        if (procedureContentElement == null)
            procedureContentElement = element.addElement("kznrContent");
//		while (content.indexOf("\n") != -1) {
//			String s = content.replace("\n", "@#$");
//			content = s;
//		}
        procedureContentElement.setText(kznrspeCharPanelText);

    }

    public static void setZLKZCXContent(Element element,
                                        String zlkzcxspeCharPanelText) {
        if ((element == null) || (zlkzcxspeCharPanelText == null))
            return;
        Element procedureContentElement = element.element("zlkzcxContent");
        if (procedureContentElement == null)
            procedureContentElement = element.addElement("zlkzcxContent");
//		while (content.indexOf("\n") != -1) {
//			String s = content.replace("\n", "@#$");
//			content = s;
//		}
        procedureContentElement.setText(zlkzcxspeCharPanelText);


    }

    public static String getKZNRContent(Element stepElement) {
        Element procedureContentElement = stepElement.element("kznrContent");
        if (procedureContentElement == null)
            return "";
        String content = procedureContentElement.getText();

        if (content == null)
            return "";
//		while (content.indexOf("@#$") != -1) {
//			String s = content.replace("@#$", "\n");
//			content = s;
//		}
        return content;
    }

    public static String getZLKZCXContent(Element stepElement) {
        Element procedureContentElement = stepElement.element("zlkzcxContent");
        if (procedureContentElement == null)
            return "";
        String content = procedureContentElement.getText();

        if (content == null)
            return "";
//		while (content.indexOf("@#$") != -1) {
//			String s = content.replace("@#$", "\n");
//			content = s;
//		}
        return content;
    }


    /**
     * 通用记录表集合元素
     */
    public static final String COMMONPARAMTABLES = "commonParamTables";
    /**
     * 特殊记录表集合元素
     */
    public static final String SPECIALPARAMTABLES = "specialParamTables";
    /**
     * 参数表元素
     */
    public static final String PARAMETERTABLE = "parameterTable";
    /**
     * 参数元素
     */
    public static final String PARAMETER = "parameter";
    /**
     * 参数值集合元素
     */
    public static final String VALUES = "values";
    /**
     * 参数值元素
     */
    public static final String VALUE = "value";
    public static final String ATTRIBUTE = "attribute";

    /**
     * 检验记录表集合元素
     */
    public static final String CHECKRECORDTABLES = "checkRecordTables";


    /**
     * 获取通用记录表顶层元素
     *
     * @param element 工序/工步Element
     * @return
     */
    public static Element getCommonParamTablesElement(Element element) {
        Element commonParamTableElement = element.element(COMMONPARAMTABLES);
        if (commonParamTableElement == null)
            commonParamTableElement = element.addElement(COMMONPARAMTABLES);
        return commonParamTableElement;
    }

    /**
     * 获取特殊记录表顶层元素
     *
     * @param element 工序/工步Element
     * @return
     */
    public static Element getSpecialParamTablesElement(Element element) {
        Element specialParamTableElement = element.element(SPECIALPARAMTABLES);
        if (specialParamTableElement == null)
            specialParamTableElement = element.addElement(SPECIALPARAMTABLES);
        return specialParamTableElement;
    }


    /**
     * 获取检验记录表顶层元素
     *
     * @param element 工序/工步Element
     * @return
     */
    public static Element getCheckRecordTablesElement(Element element) {
        Element checkRecordTableElement = element.element(CHECKRECORDTABLES);
        if (checkRecordTableElement == null)
            checkRecordTableElement = element.addElement(CHECKRECORDTABLES);
        return checkRecordTableElement;
    }

    /**
     * 移除父节点下所有子节点
     *
     * @param parentElement 父节点
     */
    @SuppressWarnings("unchecked")
    public static Element removeAllChildElements(Element parentElement) {
        List<Element> list = parentElement.elements();
        for (Element childElemet : list) {
            parentElement.remove(childElemet);
        }
        return parentElement;
    }

    public static Element removeChildElementByOid(Element parentElement, String oid) {
        List<Element> list = parentElement.elements();
        for (Element childElemet : list) {
            String elementOid = childElemet.attributeValue(XMLConstants.TABLE_OID);
            if (elementOid.equals(oid)) {
                parentElement.remove(childElemet);
                break;
            }
        }
        return parentElement;
    }

    public static Element removeChildElementByName(Element parentElement, String uuid_Value) {
        List<Element> list = parentElement.elements();
        for (Element childElemet : list) {
            String elementName = childElemet.attributeValue("bsoID"); // modify by lkc 2017.12.05
            if (elementName.equals(uuid_Value)) {
                parentElement.remove(childElemet);
                break;
            }
        }
        return parentElement;
    }

    /**
     * 添加记录表节点
     *
     * @param element   通用或特殊记录表节点
     * @param tableType 记录表数据模型
     * @return
     */
    public static Element addParameterTableElement(Element element, CmParamTableType tableType) {
        Element tableElement = element.addElement(PARAMETERTABLE);
        setAttributeValue(tableElement, XMLConstants.TABLE_OID, CommonUtil.objectToString(tableType.getOid()));
        setAttributeValue(tableElement, XMLConstants.TABLE_ENNAME, tableType.getEnName());
        setAttributeValue(tableElement, XMLConstants.TABLE_CHINANAME, tableType.getName());
        setAttributeValue(tableElement, XMLConstants.TABLE_TECHNICSTYPE, tableType.getTechnicsType());
        setAttributeValue(tableElement, XMLConstants.TABLE_VERSION, tableType.getVersion());
        return tableElement;
    }


    /**
     * 添加记录表节点
     *
     * @param element   通用或特殊记录表节点
     * @param tableType 记录表数据模型
     * @return
     */
    public static Element addRecordTableElement(Element element, List<String> tableType) {
        Element tableElement = element.addElement(PARAMETERTABLE);
        for (int i = 0; i < tableType.size(); i++) {
            if (i == 0) {
                setAttributeValue(tableElement, "name", tableType.get(i));
            } else if (i == 1) {
                setAttributeValue(tableElement, "type", tableType.get(i));
            } else if (i == 2) {
                setAttributeValue(tableElement, "projectName", tableType.get(i));
            } else if (i == 3) {
                setAttributeValue(tableElement, "tableName", tableType.get(i));
            } else if (i == 4) {
                setAttributeValue(tableElement, "eachName", tableType.get(i));
            } else if (i == 5) {
                setAttributeValue(tableElement, "bsoID", tableType.get(i));
            } else if (i == 6) {
                setAttributeValue(tableElement, "xmmPath", tableType.get(i));
            } else if (i == 7) {
                setAttributeValue(tableElement, "tbmPath", tableType.get(i));
            }

        }
        return tableElement;
    }

    /**
     * 添加记录表参数
     *
     * @param tableElement 记录表Element
     * @param vector       参数值集合
     */
    public static void addParameterElement(Element tableElement, Vector<Object> vector, int[] notshowColumns, String[] tableColumnName, int index) {
        Element parameterElement = tableElement.addElement(PARAMETER);
        Element valuesElement = parameterElement.addElement(VALUES);
        valuesElement.addAttribute("number", String.valueOf(index));
        Element valueElement = null;
        Element isShowElement = null;
        Element attributeElement = null;
        for (int i = 0; i < vector.size(); i++) {
            valueElement = valuesElement.addElement(VALUE);
//			valueElement.setText(CommonUtil.objectToString(vector.get(i)));
            valueElement.addAttribute("columnName", tableColumnName[i]);
            valueElement.addAttribute("isShow", "true");
            attributeElement = valueElement.addElement(ATTRIBUTE);
            attributeElement.setText(CommonUtil.objectToString(vector.get(i)));
            for (int j : notshowColumns) {
                if (j == i) {
                    valueElement.setAttributeValue("isShow", "false");
//					valueElement.addAttribute("isShow", "fasle");
                }
            }
        }
    }

    /**
     * 添加记录表参数
     *
     * @param tableElement 记录表Element
     * @param vector       参数值集合
     * @param imageFolder
     */
    public static void addCheckRecordElement(Element tableElement, Vector<Object> vector, String[] tableColumnName, int index, String imageFolder) {
        Element parameterElement = tableElement.addElement(PARAMETER);
        Element valuesElement = parameterElement.addElement(VALUES);
        valuesElement.addAttribute("number", String.valueOf(index));
        Element valueElement = null;
        Element isShowElement = null;
        Element attributeElement = null;
        for (int i = 0; i < vector.size() - 2; i++) {
            valueElement = valuesElement.addElement(VALUE);
//			valueElement.setText(CommonUtil.objectToString(vector.get(i)));
            valueElement.addAttribute("columnName", tableColumnName[i + 2]);
            attributeElement = valueElement.addElement(ATTRIBUTE);
            String value = CommonUtil.objectToString(vector.get(i + 2));
            value = SwitchUtil.filterSpecialChar(value);
            value = CommonHelper.replaceSaveSeperator(value, imageFolder);
            attributeElement.setText(value);
        }
    }

    /**
     * 保存通用记录表
     *
     * @param element        工序/工步Element
     * @param tableType      记录表数据模型
     * @param tableModel     表格数据模型
     * @param technicsNumber 工艺文件编号
     */
    public static void saveCommonParamTable(Element element, CmParamTableType tableType, Vector<Vector<Object>> dataVec, String technicsNumber, int[] notshowColumns, String[] tableColumnName) {
        Element commonParamTableElement = getCommonParamTablesElement(element);
        removeAllChildElements(commonParamTableElement);
        Element tableElement = addParameterTableElement(commonParamTableElement, tableType);
        for (int i = 0; i < dataVec.size(); i++) {
            Vector<Object> vector = dataVec.get(i);
            addParameterElement(tableElement, vector, notshowColumns, tableColumnName, i);
        }

        String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
        try {
            saveDocument(element.getDocument(), new File(technicsFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存特殊记录表
     *
     * @param element        工序/工步Element
     * @param tableType      记录表数据模型
     * @param tableModel     表格数据模型
     * @param technicsNumber 工艺文件编号
     */
    public static void saveSpecialParamTable(Element element, CmParamTableType tableType, Vector<Vector<Object>> dataVec, String technicsNumber, int[] notShowColumns, String[] tableColumnNames) {
        Element specialParamTableElement = getSpecialParamTablesElement(element);
        removeChildElementByOid(specialParamTableElement, CommonUtil.objectToString(tableType.getOid()));
        Element tableElement = addParameterTableElement(specialParamTableElement, tableType);
        for (int i = 0; i < dataVec.size(); i++) {
            Vector<Object> vector = dataVec.get(i);
            addParameterElement(tableElement, vector, notShowColumns, tableColumnNames, i);
        }
        String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
        try {
            saveDocument(element.getDocument(), new File(technicsFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存检验记录表
     *
     * @param element        工序/工步Element
     * @param tableType      表数据模型
     * @param dataVec        表数据
     * @param technicsNumber 工艺文件编号
     * @param imageFolder
     */
    public static void saveCheckRecordTable(Element element, Vector<Vector<Object>> dataVec, String technicsNumber, List<String> tableType, String[] tableColumnNames, String imageFolder) {
        Element checkRecordTable = getCheckRecordTablesElement(element);
        String uuid_Value = tableType.get(5);  //modify by lkc 2017.12.05
        removeChildElementByName(checkRecordTable, uuid_Value);
        Element tableElement = addRecordTableElement(checkRecordTable, tableType);
        for (int i = 0; i < dataVec.size(); i++) {
            Vector<Object> vector = dataVec.get(i);
            addCheckRecordElement(tableElement, vector, tableColumnNames, i, imageFolder);
        }
        String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
        try {
            saveDocument(element.getDocument(), new File(technicsFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void removeParamTable(Element element, CmParamTableType tableType, String technicsNumber) {
        Element specialParamTableElement = getSpecialParamTablesElement(element);
        removeChildElementByOid(specialParamTableElement, CommonUtil.objectToString(tableType.getOid()));
        String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
        try {
            saveDocument(element.getDocument(), new File(technicsFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeCheckTable(Element element, String uuid, String technicsNumber) {
        Element checkParamTableElement = getCheckRecordTablesElement(element);
        removeChildElementByName(checkParamTableElement, uuid);
        String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
        try {
            saveDocument(element.getDocument(), new File(technicsFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String isCLDE(Element rootElement) {
        List yclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/YCLDE/ycldeRecord");
        List zyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/ZYCLDE/zycldeRecord");
        List sjyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/SJYCLDE/sjycldeRecord");
        List sjzykyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/SJZYKYCLDE/ycldeRecord");
        List sjzykzyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/SJZYKZYCLDE/zycldeRecord");
        List sjzyksjyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/SJZYKSJYCLDE/sjycldeRecord");
        List gydezyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/GYDE/ZYCLDE/zycldeRecord");
        List gydesjyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/GYDE/SJYCLDE/sjycldeRecord");
        String isCLDE = "否";
        if ((yclde != null && !yclde.isEmpty())
                || (zyclde != null && !zyclde.isEmpty())
                || (sjyclde != null && !sjyclde.isEmpty())
                || (sjzykyclde != null && !sjzykyclde.isEmpty())
                || (sjzykzyclde != null && !sjzykzyclde.isEmpty())
                || (sjzyksjyclde != null && !sjzyksjyclde.isEmpty())
                || (gydezyclde != null && !gydezyclde.isEmpty())
                || (gydesjyclde != null && !gydesjyclde.isEmpty())) {
            isCLDE = "是";
        }
        return isCLDE;
    }

    public static void isCheck(Element paceElement, String isCheck, Element ele, String technicsNumber) {
        String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
        paceElement.setAttributeValue("isCheck", isCheck);
        try {
            saveDocument(ele.getDocument(), new File(technicsFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeAllCheckElement(Element ele, String technicsNumber, List<Element> eleList) {
        for (int i = 0; i < eleList.size(); i++) {
            Element element = eleList.get(i);
            if (element != null) {
                removeAllChildElements(element);
            }
        }
        String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
        try {
            saveDocument(ele.getDocument(), new File(technicsFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除工序节点上参装件信息
     *
     * @param stepElement
     */
    public static void removeCzjElements(Element stepElement) {
        if (stepElement != null) {
            Element parts = stepElement.element("parts");
            if (parts != null) {
                stepElement.remove(parts);
            }
            List<Element> paceElements = stepElement.selectNodes("paces/QMProcedureInfo");
            for (Element paceElement : paceElements) {
                parts = paceElement.element("parts");
                if (parts != null) {
                    paceElement.remove(parts);
                }
            }
        }
    }

    /**
     * 移除质量记录表信息，参装件信息，中间模型信息
     * @param techElement
     */
    public static void removeUnusefulElement(Element techElement){
        List<Element> stepElements = getAllSteps(techElement);
        for (Element stepElement : stepElements) {
            Element stepCommonParamTables = stepElement.element("commonParamTables");
            Element stepsSpecialParamTables = stepElement.element("specialParamTables");
            Element stepParts = stepElement.element("parts");
            stepElement.remove(stepCommonParamTables);
            stepElement.remove(stepsSpecialParamTables);
            stepElement.remove(stepParts);
            List<Element> paceElements = stepElement.selectNodes("paces/QMProcedureInfo");
            for (Element paceElement : paceElements) {
                Element paceCommonParamTables = paceElement.element("commonParamTables");
                Element pacesSpecialParamTables = paceElement.element("specialParamTables");
                Element paceParts = stepElement.element("parts");
                paceElement.remove(paceCommonParamTables);
                paceElement.remove(pacesSpecialParamTables);
                paceElement.remove(paceParts);
            }
        }
    }
    public static Element generatePartInfo(Element partElement, Element techElement) throws Exception {
        List list = UserUtil.getCurrentUserOid();
        if (list != null && list.size() == 3) {
            String creator = (String) list.get(0);
            String creatorOid = (String) list.get(1);
            String creatorDisplay = (String)list.get(2);
            XmlUtility.setAttributeValue(techElement, "creator", creator);
            XmlUtility.setAttributeValue(techElement, "creatorOid", creatorOid);
            XmlUtility.setAttributeValue(techElement, "creatorDisplay", creatorDisplay);
        }
        String partNumber = XmlUtility.getAttributeValue(partElement,
                "partNumber");
        String version = XmlUtility.getAttributeValue(partElement, "version");
        //WTPart newpart = TechnicsIntf.getLatestMPartByPartNumber(partNumber);
        WTPart newpart = TechnicsIntf.getMPartByNumberAndVersion(partNumber, version);
        PartIBAHelper ibaUtility = new PartIBAHelper(newpart);

        XmlUtility.setAttributeValue(techElement,"PHASE_CODE",ibaUtility.getIBAValue("PHASE_CODE"));
        XmlUtility.setAttributeValue(techElement,"PCNO",ibaUtility.getIBAValue("BATCH"));

        XmlUtility.setAttributeValue(techElement, "MTYPE", partElement.attributeValue("MTYPE"));
        XmlUtility.setAttributeValue(techElement, "ZZCJ", partElement.attributeValue("ZZCJ"));
        XmlUtility.setAttributeValue(techElement, "FZCJ", partElement.attributeValue("FZCJ"));

        XmlUtility.setAttributeValue(techElement, "CINDEX", partElement.attributeValue("CINDEX"));
        XmlUtility.setAttributeValue(techElement, "treePath", BomXMLUtil.getPath(partElement));
        XmlUtility.setAttributeValue(techElement, "partNumber", partElement.attributeValue("partNumber"));// 设置部件图号
        XmlUtility.setAttributeValue(techElement, "partName", partElement.attributeValue("partName"));// 设置部件名称
        XmlUtility.setAttributeValue(techElement, "partOid", partElement.attributeValue("oid"));// 设置零部件oid
        XmlUtility.setAttributeValue(techElement, "partVersion", partElement.attributeValue("partVersion"));
        XmlUtility.setAttributeValue(techElement, "materialType", partElement.attributeValue("materialType"));
        XmlUtility.setAttributeValue(techElement, "workShop", partElement.attributeValue("workShop"));
        XmlUtility.setAttributeValue(techElement, "backupRate", partElement.attributeValue("backupRate"));
        XmlUtility.setAttributeValue(techElement, "maxBackupCount", partElement.attributeValue("maxBackupCount"));
        XmlUtility.setAttributeValue(techElement, "backupReason", partElement.attributeValue("backupReason"));
        XmlUtility.setAttributeValue(techElement, "isKey", partElement.attributeValue("isKey"));
        XmlUtility.setAttributeValue(techElement, "isSpecial", partElement.attributeValue("isSpecial"));
        XmlUtility.setAttributeValue(techElement, "partType", partElement.attributeValue("partType"));
        XmlUtility.setAttributeValue(techElement, "pbomLifecycle", partElement.attributeValue("pbomLifecycle"));

        XmlUtility.setAttributeValue(techElement, "eu_version", partElement.attributeValue("eu_version"));
        XmlUtility.setAttributeValue(techElement, "e_version", partElement.attributeValue("e_version"));
        XmlUtility.setAttributeValue(techElement, "partVersion", partElement.attributeValue("version"));

        XmlUtility.setAttributeValue(techElement, "isPartKey", partElement.attributeValue("isKey"));
        XmlUtility.setAttributeValue(techElement, "modifyTime", XmlUtility.getCurrentTime());

        XmlUtility.setAttributeValue(techElement, "occId", partElement.attributeValue("occId"));
        XmlUtility.setAttributeValue(techElement, "material", partElement.attributeValue("material"));
        XmlUtility.setAttributeValue(techElement, "dutu", partElement.attributeValue("dutu"));
        XmlUtility.setAttributeValue(techElement, "remark", partElement.attributeValue("remark"));
        XmlUtility.setAttributeValue(techElement, "useCount", partElement.attributeValue("useCount"));
        XmlUtility.setAttributeValue(techElement, "gysl", partElement.attributeValue("gysl"));

        XmlUtility.setAttributeValue(techElement, "XHPHCL", partElement.attributeValue("XHPHCL"));
        XmlUtility.setAttributeValue(techElement, "CSIZE", partElement.attributeValue("CSIZE"));
        XmlUtility.setAttributeValue(techElement, "JSTJBZH", partElement.attributeValue("JSTJBZH"));

        XmlUtility.setAttributeValue(techElement, "CMAT_UP", partElement.attributeValue("CMAT_UP"));
        XmlUtility.setAttributeValue(techElement, "CMAT_DOWN", partElement.attributeValue("CMAT_DOWN"));
        XmlUtility.setAttributeValue(techElement, "PZGGBZH", partElement.attributeValue("PZGGBZH"));
        XmlUtility.setAttributeValue(techElement, "JDDJ", partElement.attributeValue("JDDJ"));
        XmlUtility.setAttributeValue(techElement, "CLZT", partElement.attributeValue("CLZT"));
        XmlUtility.setAttributeValue(techElement, "ZLDJ", partElement.attributeValue("ZLDJ"));
        XmlUtility.setAttributeValue(techElement, "ZQCLBZH", partElement.attributeValue("ZQCLBZH"));
        XmlUtility.setAttributeValue(techElement, "ZQCLBZH", partElement.attributeValue("ZQCLBZH"));
        XmlUtility.setAttributeValue(techElement, "ZQCLMC", partElement.attributeValue("ZQCLMC"));
        XmlUtility.setAttributeValue(techElement, "XHPH", partElement.attributeValue("XHPH"));
        XmlUtility.setAttributeValue(techElement, "JSTJ", partElement.attributeValue("JSTJ"));
        XmlUtility.setAttributeValue(techElement, "JBCLMC", partElement.attributeValue("JBCLMC"));
        XmlUtility.setAttributeValue(techElement, "CMAT", partElement.attributeValue("CMAT"));

        //设计资源库新增属性
        //start
        XmlUtility.setAttributeValue(techElement, "SHORTNAME", partElement.attributeValue("SHORTNAME"));
        XmlUtility.setAttributeValue(techElement, "STANDARDNUMBER", partElement.attributeValue("STANDARDNUMBER"));
        XmlUtility.setAttributeValue(techElement, "MECHANICALPROPERTYORHARDNESS", partElement.attributeValue("MECHANICALPROPERTYORHARDNESS"));
        XmlUtility.setAttributeValue(techElement, "SURFACETREATMENT", partElement.attributeValue("SURFACETREATMENT"));
        XmlUtility.setAttributeValue(techElement, "HEATTREATMENT", partElement.attributeValue("HEATTREATMENT"));
        XmlUtility.setAttributeValue(techElement, "PRODUCTFORM", partElement.attributeValue("PRODUCTFORM"));
        XmlUtility.setAttributeValue(techElement, "PRODUCTLEVEL", partElement.attributeValue("PRODUCTLEVEL"));
        XmlUtility.setAttributeValue(techElement, "PLATECSCREWFORM", partElement.attributeValue("PLATECSCREWFORM"));
        XmlUtility.setAttributeValue(techElement, "ISIMPORT", partElement.attributeValue("ISIMPORT"));
        XmlUtility.setAttributeValue(techElement, "SPECIALINSTRUCTION", partElement.attributeValue("SPECIALINSTRUCTION"));
        XmlUtility.setAttributeValue(techElement, "MEASUREUNIT", partElement.attributeValue("MEASUREUNIT"));
        XmlUtility.setAttributeValue(techElement, "TYPE", partElement.attributeValue("TYPE"));
        XmlUtility.setAttributeValue(techElement, "TYPESTANDARD", partElement.attributeValue("TYPESTANDARD"));
        XmlUtility.setAttributeValue(techElement, "QUALITYLEVEL", partElement.attributeValue("QUALITYLEVEL"));
        XmlUtility.setAttributeValue(techElement, "TOTALSTANDARD", partElement.attributeValue("TOTALSTANDARD"));
        XmlUtility.setAttributeValue(techElement, "DETAILSTANDARD", partElement.attributeValue("DETAILSTANDARD"));
        XmlUtility.setAttributeValue(techElement, "PACKAGINGFORM", partElement.attributeValue("PACKAGINGFORM"));
        XmlUtility.setAttributeValue(techElement, "OUTLINESIZE", partElement.attributeValue("OUTLINESIZE"));
        XmlUtility.setAttributeValue(techElement, "SPECIALCONDITION", partElement.attributeValue("SPECIALCONDITION"));
        XmlUtility.setAttributeValue(techElement, "EXTRACONDITION", partElement.attributeValue("EXTRACONDITION"));
        XmlUtility.setAttributeValue(techElement, "MATTYPE", partElement.attributeValue("MATTYPE"));
        //end
        Element productElement = BomXMLUtil.getProductMessage(partElement);
        if (productElement != null) {
            XmlUtility.setAttributeValue(techElement, "productNumber", productElement.attributeValue("productNumber"));// 设置整件图号
            XmlUtility.setAttributeValue(techElement, "productName", productElement.attributeValue("productName"));// 设置整件名称
        }
        Element rootPart = BomXMLUtil.getMainPart(BomXMLUtil.getProduct(partElement.getDocument()));
        if (rootPart != null) {
            // 添加整件信息
            XmlUtility.setAttributeValue(techElement, "parentPartNumber", rootPart.attributeValue("partNumber"));// 设置整件图号
            XmlUtility.setAttributeValue(techElement, "parentPartName", rootPart.attributeValue("partName"));// 设置整件名称
            XmlUtility.setAttributeValue(techElement, "parentPartOid", rootPart.attributeValue("oid"));// 设置整件名称
        }
        // 信维二期新需求，材料名称和材料编号也需要从零部件出带过来
        String materialNumber = partElement.attributeValue("materialNumber");
        String materialName = partElement.attributeValue("materialName");
        if (materialNumber == null)
            materialNumber = "";
        if (materialName == null)
            materialName = "";
        if (materialNumber.trim().length() > 0 || materialName.trim().length() > 0)// 零部件中包含材料的信息
        {
            Element materialELement = XmlUtility.createMaterial();
            XmlUtility.setAttributeValue(materialELement, "materialNumber", materialNumber);// 设置材料编号
            XmlUtility.setAttributeValue(materialELement, "materialName", materialName);// 设置材料编号
            XmlUtility.addMaterial(techElement, materialELement);
        }

        // 信维二期新需求，判断零部件是否为关键件，是关键件，则工艺也为关键工艺
        String key = partElement.attributeValue("isKey");
        if (key == null) {
            key = "";
        }
        XmlUtility.setAttributeValue(techElement, "isKey", key);// 设置关键工艺
        String isSpecial = partElement.attributeValue("isSpecial");
        if (isSpecial == null) {
            isSpecial = "";
        }
        XmlUtility.setAttributeValue(techElement, "isSpecial", isSpecial);// 设置关键工艺
        return techElement;
    }

    public static Element getSchemaData(Element element){
        Element childEle = element.element(XmlUtility.SCHEMA_DATA);
        if (childEle == null)
            childEle = element.addElement(XmlUtility.SCHEMA_DATA);
        return childEle;
    }

    public static List<Element> getSchemaDatas(Element element){
        Element childEle = element.element(XmlUtility.SCHEMA_DATA);
        if (childEle == null)
            childEle = element.addElement(XmlUtility.SCHEMA_DATA);
        List<Element> elements = childEle.elements();
        return elements;
    }

    public static Element getCheckFileElementByBsOrder(Element schemas, String scOrder) {
        if (schemas != null) {
            List<Element> list = schemas.elements();
            for (Element schema : list) {
                String order = getAttributeValue(schema, "order");
                if (scOrder.equals(order)) {
                    return schema;
                }
            }
        }
        return null;
    }

    public static Element getCheckFileElementByTableName(Element schemas, String tableName) {
        if (schemas != null) {
            List<Element> list = schemas.elements();
            for (Element schema : list) {
                String name = getAttributeValue(schema, "name");
                if (tableName.equals(name)) {
                    return schema;
                }
            }
        }
        return null;
    }

    public static Element getPhotoData(Element element){
        Element childEle = element.element(XmlUtility.PHOTO_DATA);
        if (childEle == null)
            childEle = element.addElement(XmlUtility.PHOTO_DATA);
        return childEle;
    }

    public static List<Element> getPhotoDatas(Element element){
        Element childEle = element.element(XmlUtility.PHOTO_DATA);
        if (childEle == null)
            childEle = element.addElement(XmlUtility.PHOTO_DATA);
        List<Element> elements = childEle.elements();
        return elements;
    }

    public static Element getPhotoElementByBsOrder(Element photoEle, String pOrder) {
        if (photoEle != null) {
            List<Element> list = photoEle.elements();
            for (Element photo : list) {
                String order = getAttributeValue(photo, "order");
                if (pOrder.equals(order)) {
                    return photo;
                }
            }
        }
        return null;
    }

    public static List<Element> getPhotoElementsByOrder(Element element) {
        String bsOid = getAttributeValue(element, XMLConstants.ATTRIBUTE_BSOID);
        String path = "//" + PROCEDURE + "[@" + XMLConstants.ATTRIBUTE_BSOID + "='"
                + bsOid + "']/" + PHOTO_DATA + "/" + PHOTO_TAG;
        List<Element> photos = element.selectNodes(path);
        XPath xpath = new NumberXPath("@order");
        xpath.sort(photos);
        return photos;
    }

    public static Element getPhotoElementByNumberAndVersion(Element photoEle,String number, String version) {
        if (photoEle != null) {
            List<Element> list = photoEle.elements();
            for (Element photo : list) {
                String photoNumber = getAttributeValue(photo, "photoNumber");
                String photoVersion = getAttributeValue(photo, "photoVersion");
                if (number.equals(photoNumber) && version.equals(photoVersion)) {
                    return photo;
                }
            }
        }
        return null;
    }

    /**
     * 方法功能:加载添加匹配的设计资源库部件到右侧
     * type 0 装配定额 则项目分类默认为配套件
     * type 1 零件定额 则项目分类默认为主要材料
     *
     * @author cjh
     * @date 2023/12/12
     */
    public static void initializeSjzykPart(List<Element> elements, DefaultTableModel dzyqjModel, DefaultTableModel bzjgjModel, int type, List<String> componentInfoList, List<String> standardInfoList) {
        List<Element> list = new ArrayList<Element>();
        for(int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            String dataType = ele.attributeValue("dataType");
            String sjbm = ele.attributeValue("sjbm");
            String gysl = ele.attributeValue("gysl");
            if(gysl == null || "".equals(gysl)) {
                gysl = ele.attributeValue("sl");
            }
            if("元器件".equals(dataType)) {
                if(!StringUtil.isEmpty(sjbm)) {
                    Object material = ErpToWCIntf.getTMELinkBySjbm(sjbm, "元器件");
                    if(material != null && material instanceof TMEEleComponentsPartLinkBean) {
                        TMEEleComponentsPartLinkBean bean = (TMEEleComponentsPartLinkBean) material;
                        Object[] values = new Object[dzyqjModel.getColumnCount()];
                        values[0] = bean.getTechnicsmaterialentriesid();
                        values[1] = false;
                        if(0 == type) {
                            values[2] = "配套件";
                        } else {
                            values[2] = "主要材料";
                        }
                        int intFlag = 3;
                        for (int j = 0; j < componentInfoList.size(); j++) {
                            String s = componentInfoList.get(j);
                            Object getMethod = getGetMethod(bean, s);
                            String value = "";
                            if (getMethod == null) {
                                value = "";
                            } else {
                                value = String.valueOf(getMethod);
                            }
                            values[intFlag] = value;
                            if("SCCJ".equals(s)){
                                values[intFlag + 1] = StringUtil.isEmpty(gysl) ? "" : gysl;
                                intFlag++;
                            }
                            if ("JLDW".equals(s)) {
                                values[intFlag + 1] = "";
                                intFlag++;
                            }
                            if ("SMDJ".equals(s)) {
                                values[intFlag + 1] = bean.getSjbm();
                                values[intFlag + 2] = bean.getWzbm();
                                intFlag = intFlag + 2;
                            }
                            intFlag++;
                        }
                        dzyqjModel.addRow(values);
                        list.add(ele);
                    }
                }
            } else if("标准件".equals(dataType)) {
                if(!StringUtil.isEmpty(sjbm)) {
                    Object material = ErpToWCIntf.getTMELinkBySjbm(sjbm, "标准紧固件");
                    if(material != null && material instanceof TMEStandPartLinkBean) {
                        TMEStandPartLinkBean bean = (TMEStandPartLinkBean) material;
                        Object[] values = new Object[bzjgjModel.getColumnCount()];
                        values[0] = bean.getTechnicsmaterialentriesid();
                        values[1] = false;
                        if(0 == type) {
                            values[2] = "配套件";
                        } else {
                            values[2] = "主要材料";
                        }
                        int intFlag = 3;
                        for (int j = 0; j < standardInfoList.size(); j++) {
                            String s = standardInfoList.get(j);
                            Object getMethod = getGetMethod(bean, s);
                            String value = "";
                            if (getMethod == null) {
                                value = "";
                            } else {
                                value = String.valueOf(getMethod);
                            }
                            values[intFlag] = value;
                            if ("JXXNDJ".equals(s)) {
                                values[intFlag + 1] = StringUtil.isEmpty(gysl) ? "" : gysl;
                                intFlag++;
                            }
                            if ("SFJK".equals(s)) {
                                values[intFlag + 1] = bean.getSjbm();
                                values[intFlag + 2] = bean.getWzbm();
                                intFlag = intFlag + 2;
                            }
                            intFlag++;
                        }
                        bzjgjModel.addRow(values);
                        list.add(ele);
                    }
                }
            }
        }
        for(Element element : list) {
            elements.remove(element);
        }

    }


    /**
     * 方法功能:加载添加匹配的标准件元器件外购件
     * type 0 装配定额 则项目分类默认为配套件
     * type 1 零件定额 则项目分类默认为主要材料
     *
     * @author cjh
     * @date 2023/12/12
     */
    public static void initializeNewOrMatchPart(List<Element> elements, DefaultTableModel dzyqjModel, DefaultTableModel bzjgjModel, DefaultTableModel jsclModel, DefaultTableModel fjsclModel, DefaultTableModel fhclModel, DefaultTableModel jdclModel, DefaultTableModel hgpModel, int type, String slkey, List<String> componentInfoList, List<String> standardInfoList, List<String> materialInfoList, List<String> nonMaterialInfoList, List<String> compoundInfoList, List<String> jdclInfoList, List<String> hgpInfoList) {
        List<Element> list = new ArrayList<Element>();
        for(int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            String tabType = ele.attributeValue("tabType");
            String chbm = ele.attributeValue("chbm");
            String gysl = ele.attributeValue(slkey);
            if(!StringUtil.isEmpty(chbm) && StringUtil.isEmpty(tabType)){
                try {
                    String ctype = TechnicsIntf.getPartCTypeByNumberAndView(chbm,"Design");
                    if(!StringUtil.isEmpty(ctype)){
                        tabType = ctype;
                    }
                } catch(RemoteException e) {
                    e.printStackTrace();
                } catch(InvocationTargetException e) {
                    e.printStackTrace();
                }
                if("元器件".equals(tabType)) {
                    Object material = ErpToWCIntf.getTMELinkBySjbm(chbm, "元器件");
                    if(material != null && material instanceof TMEEleComponentsPartLinkBean) {
                        TMEEleComponentsPartLinkBean bean = (TMEEleComponentsPartLinkBean) material;
                        Object[] values = new Object[dzyqjModel.getColumnCount()];
                        values[0] = bean.getTechnicsmaterialentriesid();
                        values[1] = false;
                        if(0 == type) {
                            values[2] = "配套件";
                        } else {
                            values[2] = "主要材料";
                        }
                        int intFlag = 3;
                        for (int j = 0; j < componentInfoList.size(); j++) {
                            String s = componentInfoList.get(j);
                            Object getMethod = getGetMethod(bean, s);
                            String value = "";
                            if (getMethod == null) {
                                value = "";
                            } else {
                                value = String.valueOf(getMethod);
                            }
                            values[intFlag] = value;
                            if("SCCJ".equals(s)){
                                values[intFlag + 1] = StringUtil.isEmpty(gysl) ? "" : gysl;
                                intFlag++;
                            }
                            if ("JLDW".equals(s)) {
                                values[intFlag + 1] = "";
                                intFlag++;
                            }
                            if ("SMDJ".equals(s)) {
                                values[intFlag + 1] = bean.getSjbm();
                                values[intFlag + 2] = bean.getWzbm();
                                intFlag = intFlag + 2;
                            }
                            intFlag++;
                        }
                        dzyqjModel.addRow(values);
                        list.add(ele);
                    }
                } else if("标准件".equals(tabType)) {
                    Object material = ErpToWCIntf.getTMELinkBySjbm(chbm, "标准紧固件");
                    if(material != null && material instanceof TMEStandPartLinkBean) {
                        TMEStandPartLinkBean bean = (TMEStandPartLinkBean) material;
                        Object[] values = new Object[bzjgjModel.getColumnCount()];
                        values[0] = bean.getTechnicsmaterialentriesid();
                        values[1] = false;
                        if(0 == type) {
                            values[2] = "配套件";
                        } else {
                            values[2] = "主要材料";
                        }
                        int intFlag = 3;
                        for (int j = 0; j < standardInfoList.size(); j++) {
                            String s = standardInfoList.get(j);
                            Object getMethod = getGetMethod(bean, s);
                            String value = "";
                            if (getMethod == null) {
                                value = "";
                            } else {
                                value = String.valueOf(getMethod);
                            }
                            values[intFlag] = value;
                            if ("JXXNDJ".equals(s)) {
                                values[intFlag + 1] = StringUtil.isEmpty(gysl) ? "" : gysl;
                                intFlag++;
                            }
                            if ("SFJK".equals(s)) {
                                values[intFlag + 1] = bean.getSjbm();
                                values[intFlag + 2] = bean.getWzbm();
                                intFlag = intFlag + 2;
                            }
                            intFlag++;
                        }
                        bzjgjModel.addRow(values);
                        list.add(ele);
                    }
                } else if("金属材料".equals(tabType)) {
                    Object material = ErpToWCIntf.getTMELinkBySjbm(chbm, "金属材料");
                    if(material != null && material instanceof TMEMetallicPartLinkBean) {
                        TMEMetallicPartLinkBean bean = (TMEMetallicPartLinkBean) material;
                        Object[] values = new Object[jsclModel.getColumnCount()];
                        values[0] = bean.getTechnicsmaterialentriesid();
                        values[1] = false;
                        if(0 == type) {
                            values[2] = "配套件";
                        } else {
                            values[2] = "主要材料";
                        }
                        int intFlag = 3;
                        for (int j = 0; j < materialInfoList.size(); j++) {
                            String s = materialInfoList.get(j);
                            Object getMethod = getGetMethod(bean, s);
                            String value = "";
                            if (getMethod == null) {
                                value = "";
                            } else {
                                value = String.valueOf(getMethod);
                            }
                            values[intFlag] = value;
                            if ("CYBZ".equals(s)) {
                                values[intFlag + 1] = "";
                                values[intFlag + 2] = "";
                                values[intFlag + 3] = StringUtil.isEmpty(gysl) ? "" : gysl;
                                intFlag = intFlag + 3;
                            }
                            if ("XS".equals(s)) {
                                values[intFlag + 1] = bean.getSjbm();
                                values[intFlag + 2] = bean.getWzbm();
                                intFlag = intFlag + 2;
                            }
                            intFlag++;
                        }
                        jsclModel.addRow(values);
                        list.add(ele);
                    }
                } else if("非金属材料".equals(tabType)) {
                    Object material = ErpToWCIntf.getTMELinkBySjbm(chbm, "非金属材料");
                    if(material != null && material instanceof TMENonMetallicPartLinkBean) {
                        TMENonMetallicPartLinkBean bean = (TMENonMetallicPartLinkBean) material;
                        Object[] values = new Object[fjsclModel.getColumnCount()];
                        values[0] = bean.getTechnicsmaterialentriesid();
                        values[1] = false;
                        if(0 == type) {
                            values[2] = "配套件";
                        } else {
                            values[2] = "主要材料";
                        }
                        int intFlag = 3;
                        for (int j = 0; j < nonMaterialInfoList.size(); j++) {
                            String s = nonMaterialInfoList.get(j);
                            Object getMethod = getGetMethod(bean, s);
                            String value = "";
                            if (getMethod == null) {
                                value = "";
                            } else {
                                value = String.valueOf(getMethod);
                            }
                            values[intFlag] = value;
                            if ("CYBZ".equals(s)) {
                                values[intFlag + 1] = "";
                                values[intFlag + 2] = "";
                                intFlag = intFlag + 2;
                            }
                            if ("GYDW".equals(s)) {
                                values[intFlag + 1] = StringUtil.isEmpty(gysl) ? "" : gysl;
                                intFlag++;
                            }
                            if ("XS".equals(s)) {
                                values[intFlag + 1] = bean.getSjbm();
                                values[intFlag + 2] = bean.getWzbm();
                                intFlag = intFlag + 2;
                            }
                            intFlag++;
                        }
                        fjsclModel.addRow(values);
                        list.add(ele);
                    }
                } else if("复合材料".equals(tabType)) {
                    Object material = ErpToWCIntf.getTMELinkBySjbm(chbm, "复合材料");
                    if(material != null && material instanceof TMECompoundMaterialPartLinkBean) {
                        TMECompoundMaterialPartLinkBean bean = (TMECompoundMaterialPartLinkBean) material;
                        Object[] values = new Object[fhclModel.getColumnCount()];
                        values[0] = bean.getTechnicsmaterialentriesid();
                        values[1] = false;
                        if(0 == type) {
                            values[2] = "配套件";
                        } else {
                            values[2] = "主要材料";
                        }
                        int intFlag = 3;
                        for (int j = 0; j < compoundInfoList.size(); j++) {
                            String s = compoundInfoList.get(j);
                            Object getMethod = getGetMethod(bean, s);
                            String value = "";
                            if (getMethod == null) {
                                value = "";
                            } else {
                                value = String.valueOf(getMethod);
                            }
                            values[intFlag] = value;
                            if("CYBZ".equals(s)) {
                                values[intFlag + 1] = "";
                                intFlag++;
                            }
                            if ("JLDW".equals(s)) {
                                values[intFlag + 1] = StringUtil.isEmpty(gysl) ? "" : gysl;
                                intFlag++;
                            }
                            if ("XS".equals(s)) {
                                values[intFlag + 1] = bean.getSjbm();
                                values[intFlag + 2] = bean.getWzbm();
                                intFlag = intFlag + 2;
                            }
                            intFlag++;
                        }
                        fhclModel.addRow(values);
                        list.add(ele);
                    }
                } else if("机电产品".equals(tabType)) {
                    Object material = ErpToWCIntf.getTMELinkBySjbm(chbm, "机电材料");
                    if(material != null && material instanceof TMEEleMachinePartLinkBean) {
                        TMEEleMachinePartLinkBean bean = (TMEEleMachinePartLinkBean) material;
                        Object[] values = new Object[jdclModel.getColumnCount()];
                        values[0] = bean.getTechnicsmaterialentriesid();
                        values[1] = false;
                        if(0 == type) {
                            values[2] = "配套件";
                        } else {
                            values[2] = "主要材料";
                        }
                        int intFlag = 3;
                        for (int j = 0; j < jdclInfoList.size(); j++) {
                            String s = jdclInfoList.get(j);
                            Object getMethod = getGetMethod(bean, s);
                            String value = "";
                            if (getMethod == null) {
                                value = "";
                            } else {
                                value = String.valueOf(getMethod);
                            }
                            values[intFlag] = value;
                            if ("XHGG".equals(s)) {
                                values[intFlag + 1] = StringUtil.isEmpty(gysl) ? "" : gysl;
                                intFlag++;
                            }
                            if ("SCCJ".equals(s)) {
                                values[intFlag + 1] = bean.getSjbm();
                                values[intFlag + 2] = bean.getWzbm();
                                intFlag = intFlag + 2;
                            }
                            intFlag++;
                        }
                        jdclModel.addRow(values);
                        list.add(ele);
                    }
                } else if("火工品".equals(tabType)) {
                    Object material = ErpToWCIntf.getTMELinkBySjbm(chbm, "火工品");
                    if(material != null && material instanceof TMEExpDevicePartLinkBean) {
                        TMEExpDevicePartLinkBean bean = (TMEExpDevicePartLinkBean) material;
                        Object[] values = new Object[hgpModel.getColumnCount()];
                        values[0] = bean.getTechnicsmaterialentriesid();
                        values[1] = false;
                        if(0 == type) {
                            values[2] = "配套件";
                        } else {
                            values[2] = "主要材料";
                        }
                        int intFlag = 3;
                        for (int j = 0; j < hgpInfoList.size(); j++) {
                            String s = hgpInfoList.get(j);
                            Object getMethod = getGetMethod(bean, s);
                            String value = "";
                            if (getMethod == null) {
                                value = "";
                            } else {
                                value = String.valueOf(getMethod);
                            }
                            values[intFlag] = value;
                            if ("BZH".equals(s)) {
                                values[intFlag + 1] = StringUtil.isEmpty(gysl) ? "" : gysl;
                                intFlag++;
                            }
                            if ("SCCJ".equals(s)) {
                                values[intFlag + 1] = bean.getSjbm();
                                values[intFlag + 2] = bean.getWzbm();
                                intFlag = intFlag + 2;
                            }
                            intFlag++;
                        }
                        hgpModel.addRow(values);
                        list.add(ele);
                    }
                }
            }
        }
        for(Element element : list) {
            elements.remove(element);
        }

    }


    public static Object getGetMethod(Object ob, String name) {
        try {
            Method[] m = ob.getClass().getMethods();
            for (int i = 0; i < m.length; i++) {
                if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                    return m[i].invoke(ob);
                }
            }
        } catch (Exception e) {

        }
        return null;
    }
}
