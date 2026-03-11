package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.bean.VaEPartInstance;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.log.VaLogger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DpTreeXmlUtil {

    private static VaLogger logger = VaLogger.getLogger(DpTreeXmlUtil.class);

    public static DpTree xmlToCoTree2(String filePath, String stepNumber, String paceNumber, String procedureContent) {
        DpTree coTree = null;
        Document document = XmlUtil.getDocument(new File(filePath));
        Element root = document.getRootElement();
        Element elementsByName = XmlUtil.getElementsByName(root, "QMFawTechnicsInfo").get(0);
        String name = parseName(elementsByName, "technicsName");
        //工艺节点
        DpTecnicsNode rootNode = new DpTecnicsNode(name);
        List<Element> stepElements = elementsByName.selectNodes("steps/QMProcedureInfo");
        Map<String, List<Element>> czjPartMap = new HashMap<String, List<Element>>();
        for (Element stepElement : stepElements) {
            String oid = parseName(stepElement, "bsoID");
            String number = parseName(stepElement, "stepNumber");
            String stepName = parseName(stepElement, "stepName");
            String workShop = parseName(stepElement, "workShop");
            String workType = parseName(stepElement, "workType");
            //工序节点
            DpStepNode stepTreeNode = new DpStepNode(oid, number, stepName, workShop, workType);
            rootNode.add(stepTreeNode);
            //参装件---工序
            List<Element> stepParts = stepElement.selectNodes("parts/QMPartInfo");
            for (Element partElement : stepParts) {
                String partNumber = parseName(partElement, "partNumber");
                String zcMark = parseName(partElement, "ZCMARK");
                String key = partNumber + "_" + zcMark;
                if (czjPartMap.containsKey(key)) {
                    czjPartMap.get(key).add(partElement);
                } else {
                    List<Element> partList = new ArrayList<Element>();
                    partList.add(partElement);
                    czjPartMap.put(key, partList);
                }
            }
            List<DpPartNode> stepPartNodeList = getDpPartNode(czjPartMap);
            //工序的参装件节点
            for (DpPartNode stepPartNode : stepPartNodeList) {
                stepTreeNode.add(stepPartNode);
            }
            List<Element> paceElements = stepElement.selectNodes("paces/QMProcedureInfo");
            czjPartMap.clear();
            for (Element paceElement : paceElements) {
                String oid1 = parseName(paceElement, "bsoID");
                String number1 = parseName(paceElement, "stepNumber");
                String stepName1 = parseName(paceElement, "stepName");
                String content = XmlUtil.getFirstElementForName(paceElement, "procedureContent").getTextTrim();
                //参装件---工步
                List<Element> paceParts = paceElement.selectNodes("parts/QMPartInfo");

                for (Element partElement : paceParts) {
                    String partNumber = parseName(partElement, "partNumber");
                    String zcMark = parseName(partElement, "ZCMARK");
                    String key = partNumber + "_" + zcMark;
                    if (czjPartMap.containsKey(key)) {
                        czjPartMap.get(key).add(partElement);
                    } else {
                        List<Element> partList = new ArrayList<Element>();
                        partList.add(partElement);
                        czjPartMap.put(key, partList);
                    }
                }
                //工步的参装件节点
                List<DpPartNode> pacePartNodeList = getDpPartNode(czjPartMap);
                //工步节点
                DpPaceNode paceTreeNode = new DpPaceNode(oid1, number1, stepName1, content, pacePartNodeList);
                for (DpPartNode pacePartNode : pacePartNodeList) {
                    paceTreeNode.add(pacePartNode);
                }
                stepTreeNode.add(paceTreeNode);

                czjPartMap.clear();
            }
        }
        coTree = new DpTree(rootNode);
        return coTree;
    }

    public static List<DpPartNode> getDpPartNode(Map<String, List<Element>> czjPartMap) {

        List<DpPartNode> dpPartNodeList = new ArrayList<DpPartNode>();
        for (Map.Entry<String, List<Element>> entry : czjPartMap.entrySet()) {
            String key = entry.getKey();
            List<Element> partList = entry.getValue();
//            partList = dealWithHistoryData(partList);
            for(Element partElement : partList){
                double useCount = Double.valueOf(parseName(partElement, "useCount"));
                VaLightPart vaLightPart = new VaLightPart();
                vaLightPart.setOid(emptyToLong(parseName(partElement, "oid")));
                vaLightPart.setNumber(parseName(partElement, "partNumber"));
                vaLightPart.setName(parseName(partElement, "partName"));
                vaLightPart.setType(toWTPartType(parseName(partElement, "partType")));
                vaLightPart.setDutu(parseName(partElement, "dutu"));
                vaLightPart.setRemark(parseName(partElement, "remark"));
//            vaLightPart.setUseCount(useCount);
                vaLightPart.setAmount(useCount);
                vaLightPart.setProductionQuantity(Integer.parseInt("0" + nullToEmpty(parseName(partElement, "productionQuantity"))));
                vaLightPart.setProductionRatio(parseName(partElement, "productionRatio"));
                vaLightPart.setRate(parseName(partElement, "rate"));
                vaLightPart.setKey(Boolean.valueOf(parseName(partElement, "isKey")));
                vaLightPart.setEbomKey(Boolean.valueOf(parseName(partElement, "isEbomKey")));
                vaLightPart.setMaterialType(parseName(partElement, "materialType"));
                vaLightPart.setBackupRate(parseName(partElement, "backupRate"));
                vaLightPart.setMaxBackupCount(parseName(partElement, "maxBackupCount"));
                vaLightPart.setBackupReason(parseName(partElement, "backupReason"));
                vaLightPart.setWorkShop(parseName(partElement, "workShop"));
                vaLightPart.setOutsourcingUnits(parseName(partElement, "outsourcingUnits"));
                vaLightPart.setMaterialNumber(parseName(partElement, "materialNumber"));
                vaLightPart.setMaterialName(parseName(partElement, "materialName"));
                vaLightPart.setMaterialBrand(parseName(partElement, "materialBrand"));
                vaLightPart.setMaterialCrision(parseName(partElement, "materialCrision"));
                vaLightPart.setResponser(parseName(partElement, "responser"));
                vaLightPart.setResponserGroup(parseName(partElement, "responserGroup"));
                vaLightPart.setContainerId(emptyToLong(parseName(partElement, "containerId")));
                vaLightPart.setE_version(parseName(partElement, "e_version"));
                vaLightPart.setEu_number(parseName(partElement, "eu_number"));
                vaLightPart.setEu_version(parseName(partElement, "eu_version"));
                vaLightPart.setVersion(parseName(partElement, "version"));
                vaLightPart.setZcmark(parseName(partElement, "ZCMARK"));
                vaLightPart.setRootType("TECHNICS");
                vaLightPart.setXhph(parseName(partElement, "XHPH"));
                DpPartNode dpPartNode = new DpPartNode(new VaEPartInstance(vaLightPart));
                dpPartNode.setZcmark(parseName(partElement, "ZCMARK"));
                dpPartNode.setOccId(parseName(partElement, "occId"));
                dpPartNode.setOccpath(parseName(partElement, "occId"));
//                dpPartNode.setHasEpmDoc(Boolean.parseBoolean(parseName(partElement, "HASEPM")));
                dpPartNodeList.add(dpPartNode);
            }
        }

        return dpPartNodeList;
    }

    /**
     * 方法功能: 处理参装件历史数据

     * @param partList
     * @return java.util.List<org.dom4j.Element>
     * @author LB
     * @date 2020/4/8
     */
    private static List<Element> dealWithHistoryData(List<Element> partList) {
        List<Element> partElementList = new ArrayList<Element> ();
        Map<String, Element> partElementMap = new HashMap<String, Element>();
        double currentCount;
        double addedCount;
        for (Element partElement : partList) {
            String partNumber = parseName(partElement, "partNumber");
            addedCount = Double.valueOf(parseName(partElement, "useCount"));
            if(partElementMap.containsKey(partNumber)){
                Element element = partElementMap.get(partNumber);
                currentCount = Double.valueOf(parseName(element, "useCount"));
                XmlUtility.setAttributeValue(element,"useCount",String.valueOf(CommonUtil.addDouble(currentCount, addedCount)));
            }else{
                partElementMap.put(partNumber,partElement);
            }
        }
        for (String partNumber : partElementMap.keySet()) {
            partElementList.add(partElementMap.get(partNumber));
        }
        return partElementList;
    }

    /***
     * xml->obj
     *
     * @param xml
     * @return CoTree
     */
    public static DpTree xmlToCoTree(String filePath, String stepNumber,
                                     String paceNumber, String procedureContent) {
        DpTree coTree = null;
        Document document = null;

        try {
            document = XmlUtil.getDocument(new File(filePath));
            Element root = document.getRootElement();
            DpTecnicsNode rootNode = null;
            String StringFlag = "1";
            for (Element element : XmlUtil.getElementsByName(root,
                    "QMFawTechnicsInfo")) {
                String name = parseName(element, "technicsName");
                rootNode = new DpTecnicsNode(name);
                for (Element temp : XmlUtil.getElementsByName(element, "steps")) {
                    String flag = "1";
                    for (Element temp1 : XmlUtil.getElementsByName(temp,
                            "QMProcedureInfo")) {
                        String oid = parseName(temp1, "bsoID");
                        String number = parseName(temp1, "stepNumber");
                        String stepName = parseName(temp1, "stepName");
                        String workShop = parseName(temp1, "workShop");
                        String workType = parseName(temp1, "workType");
                        DpStepNode dpTreeNode = new DpStepNode(oid, number,
                                stepName, workShop, workType);
                        if (stepNumber.trim().equals(number.trim())) {
                            flag = "2";
                            StringFlag = "2";
                            // dpTreeNode.setSelected(true);
                        }
                        if ("1".equals(StringFlag)) {
                            // dpTreeNode.setSelected(true);
                        }
                        rootNode.add(dpTreeNode);

                        List<DpPartNode> listAdd = new ArrayList<DpPartNode>();
                        for (Element tempAdd1 : XmlUtil.getElementsByName(temp1, "parts")) {
                            for (Element tempAdd2 : XmlUtil.getElementsByName(tempAdd1, "QMPartInfo")) {
                                String[] occIds = parseName(tempAdd2,
                                        "occId").split(",");
                                for (int i = 0; i < occIds.length; i++) {
                                    VaLightPart part = new VaLightPart();

                                    part.setOid(emptyToLong(parseName(
                                            tempAdd2, "oid")));

                                    part.setNumber(parseName(tempAdd2,
                                            "partNumber"));
                                    part.setName(parseName(tempAdd2,
                                            "partName"));
                                    part.setType(toWTPartType(parseName(
                                            tempAdd2, "partType")));
                                    part.setDutu(parseName(tempAdd2,
                                            "dutu"));
                                    part.setRemark(parseName(tempAdd2,
                                            "remark"));
                                    part.setUseCount(Integer
                                            .parseInt(parseName(tempAdd2,
                                                    "useCount")));
                                    part.setProductionQuantity(Integer
                                            .parseInt("0"
                                                    + nullToEmpty(parseName(
                                                    tempAdd2,
                                                    "productionQuantity"))));
                                    part.setProductionRatio(parseName(
                                            tempAdd2, "productionRatio"));
                                    part.setRate(parseName(tempAdd2,
                                            "rate"));
                                    part.setKey(Boolean
                                            .valueOf(parseName(tempAdd2,
                                                    "isKey")));
                                    part.setEbomKey(Boolean
                                            .valueOf(parseName(tempAdd2,
                                                    "isEbomKey")));
                                    part.setMaterialType(parseName(
                                            tempAdd2, "materialType"));
                                    part.setBackupRate(parseName(tempAdd2,
                                            "backupRate"));
                                    part.setMaxBackupCount(parseName(
                                            tempAdd2, "maxBackupCount"));
                                    part.setBackupReason(parseName(
                                            tempAdd2, "backupReason"));
                                    part.setWorkShop(parseName(tempAdd2,
                                            "workShop"));
                                    part.setOutsourcingUnits(parseName(
                                            tempAdd2, "outsourcingUnits"));
                                    part.setMaterialNumber(parseName(
                                            tempAdd2, "materialNumber"));
                                    part.setMaterialName(parseName(
                                            tempAdd2, "materialName"));
                                    part.setMaterialBrand(parseName(
                                            tempAdd2, "materialBrand"));
                                    part.setMaterialCrision(parseName(
                                            tempAdd2, "materialCrision"));
                                    part.setResponser(parseName(tempAdd2,
                                            "responser"));
                                    part.setResponserGroup(parseName(
                                            tempAdd2, "responserGroup"));
                                    part.setContainerId(emptyToLong(parseName(
                                            tempAdd2, "containerId")));
                                    part.setE_version(parseName(tempAdd2,
                                            "e_version"));
                                    part.setEu_number(parseName(tempAdd2,
                                            "eu_number"));
                                    part.setEu_version(parseName(tempAdd2,
                                            "eu_version"));
                                    part.setVersion(parseName(tempAdd2,
                                            "version"));


                                    part.setZcmark(parseName(tempAdd2,
                                            "ZCMARK"));
                                    DpPartNode node = new DpPartNode(
                                            new VaEPartInstance(part));
                                    // node.setUserObject(part);
                                    node.setZcmark(parseName(tempAdd2,
                                            "ZCMARK"));
                                    node.setOccId(occIds[i]);
                                    // node.setOccpath(parseName(temp5,"occpath"));

                                    listAdd.add(node);
                                }
                            }
                        }
                        for (DpPartNode tempObj : listAdd) {
                            dpTreeNode.add(tempObj);
                        }

                        for (Element temp2 : XmlUtil.getElementsByName(temp1,
                                "paces")) {
                            for (Element temp3 : XmlUtil.getElementsByName(
                                    temp2, "QMProcedureInfo")) {

                                String oid1 = parseName(temp3, "bsoID");
                                String number1 = parseName(temp3, "stepNumber");
                                String stepName1 = parseName(temp3, "stepName");
                                String content = XmlUtil
                                        .getFirstElementForName(temp3,
                                                "procedureContent")
                                        .getTextTrim();
                                List<DpPartNode> list = new ArrayList<DpPartNode>();
                                for (Element temp4 : XmlUtil.getElementsByName(
                                        temp3, "parts")) {
                                    for (Element temp5 : XmlUtil
                                            .getElementsByName(temp4,
                                                    "QMPartInfo")) {
                                        String[] occIds = parseName(temp5,
                                                "occId").split(",");
                                        for (int i = 0; i < occIds.length; i++) {
                                            VaLightPart part = new VaLightPart();

                                            part.setOid(emptyToLong(parseName(
                                                    temp5, "oid")));

                                            part.setNumber(parseName(temp5,
                                                    "partNumber"));
                                            part.setName(parseName(temp5,
                                                    "partName"));
                                            part.setType(toWTPartType(parseName(
                                                    temp5, "partType")));
                                            part.setDutu(parseName(temp5,
                                                    "dutu"));
                                            part.setRemark(parseName(temp5,
                                                    "remark"));
                                            part.setUseCount(Integer
                                                    .parseInt(parseName(temp5,
                                                            "useCount")));
                                            part.setProductionQuantity(Integer
                                                    .parseInt("0"
                                                            + nullToEmpty(parseName(
                                                            temp5,
                                                            "productionQuantity"))));
                                            part.setProductionRatio(parseName(
                                                    temp5, "productionRatio"));
                                            part.setRate(parseName(temp5,
                                                    "rate"));
                                            part.setKey(Boolean
                                                    .valueOf(parseName(temp5,
                                                            "isKey")));
//											part.setSpecial(Boolean
//													.valueOf(parseName(temp5,
//															"isSpecial")));
                                            part.setEbomKey(Boolean
                                                    .valueOf(parseName(temp5,
                                                            "isEbomKey")));
                                            part.setMaterialType(parseName(
                                                    temp5, "materialType"));
                                            part.setBackupRate(parseName(temp5,
                                                    "backupRate"));
                                            part.setMaxBackupCount(parseName(
                                                    temp5, "maxBackupCount"));
                                            part.setBackupReason(parseName(
                                                    temp5, "backupReason"));
                                            part.setWorkShop(parseName(temp5,
                                                    "workShop"));
                                            part.setOutsourcingUnits(parseName(
                                                    temp5, "outsourcingUnits"));
                                            part.setMaterialNumber(parseName(
                                                    temp5, "materialNumber"));
                                            part.setMaterialName(parseName(
                                                    temp5, "materialName"));
                                            part.setMaterialBrand(parseName(
                                                    temp5, "materialBrand"));
                                            part.setMaterialCrision(parseName(
                                                    temp5, "materialCrision"));
                                            part.setResponser(parseName(temp5,
                                                    "responser"));
                                            part.setResponserGroup(parseName(
                                                    temp5, "responserGroup"));
                                            part.setContainerId(emptyToLong(parseName(
                                                    temp5, "containerId")));
                                            part.setE_version(parseName(temp5,
                                                    "e_version"));
                                            part.setEu_number(parseName(temp5,
                                                    "eu_number"));
                                            part.setEu_version(parseName(temp5,
                                                    "eu_version"));
                                            part.setVersion(parseName(temp5,
                                                    "version"));


                                            part.setZcmark(parseName(temp5,
                                                    "ZCMARK"));
                                            DpPartNode node = new DpPartNode(
                                                    new VaEPartInstance(part));
                                            // node.setUserObject(part);
                                            node.setZcmark(parseName(temp5,
                                                    "ZCMARK"));
                                            node.setOccId(occIds[i]);
                                            // node.setOccpath(parseName(temp5,"occpath"));

                                            list.add(node);
                                        }
                                    }
                                }
                                DpPaceNode paceNode = new DpPaceNode(oid1,
                                        number1, stepName1, content, list);
                                for (DpPartNode tempObject : list) {
                                    paceNode.add(tempObject);
                                }

                                if ("1".equals(flag)) {
                                    // paceNode.setSelected(true);
                                } else if ("2".equals(flag)) {
                                    if (paceNumber.trim().equals(number1)
                                            && procedureContent.trim().equals(
                                            content.trim())) {
                                        // paceNode.setSelected(true);
                                        dpTreeNode.add(paceNode);
                                        flag = "3";
                                        continue;
                                    } else {
                                        // paceNode.setSelected(true);
                                    }
                                }
                                dpTreeNode.add(paceNode);
                            }
                        }
                    }
                }
            }
            coTree = new DpTree(rootNode);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return coTree;

    }

    public static Object nullToEmpty(Object obj) {
        if (obj == null)
            return "";
        else
            return obj;
    }

    public static String parseName(Element element, String type) {
        Attribute attr = element.attribute(type);
        return attr == null ? null : attr.getValue();
    }

    public static void writeDocument(Document document, String filePath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            OutputFormat xmlFormat = OutputFormat.createPrettyPrint();
            xmlFormat.setEncoding("GBK");
            XMLWriter xmlWriter = new XMLWriter(fos, xmlFormat);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            JavaUtil.closeStream(fos);
        }
    }

    public static long emptyToLong(String obj) {
        if (null == obj || "".equals(obj)) {
            return 0;
        } else {
            return Long.parseLong(obj);
        }
    }

    private static String toWTPartType(String partType) {
        if ("WTPart".equals(partType) || "normal".equals(partType)) {
            return "wt.part.WTPart";
        }
        return partType;
    }
}