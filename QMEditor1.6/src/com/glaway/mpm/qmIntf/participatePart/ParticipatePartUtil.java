package com.glaway.mpm.qmIntf.participatePart;

import com.glaway.mpm.util.BomXMLUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.view.VaContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.util.*;

public class ParticipatePartUtil {

    public static void main(String[] args) {
        Vector<Map<String, String>> vector = new Vector<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("occId", "testOccId");
        map.put("partNumber", "partNumber");
        map.put("partName", "partName");
        map.put("oid", "oid");
        vector.add(map);

        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("occId", "testOccId1");
        map1.put("partNumber", "partNumber");
        map1.put("partName", "partName");
        map1.put("oid", "oid");
        vector.add(map1);
        // addParticipateParts("AL2_907_1459", "1df24b6d:13e9c7326aa:-7fe0",
        // "1df24b6d:13e9c7326aa:-7fd9", vector);
    }

    /**
     * 增加参装件的集合
     */
    public static boolean addParticipateParts(String stepOid, String paceOid,
                                              Vector<Map<String, String>> parts, String isComplete) {
        try {
            Document document = ((NewTechnicsPart) VaContext.getMainFrame()).getCurrentTechnics();
//            Document document = XmlUtil.getDocument(new File(VaContext.getCurrentTechXMLPath()));
            Element techInfoElement = document.getRootElement();
            Element paceElement = getPaceElement(techInfoElement, stepOid, paceOid);

            String partNumber = parts.get(0).get("partNumber");
            String zcmark = parts.get(0).get("ZCMARK");
            Element partsElement = paceElement.element("parts");
            List<Element> partList = paceElement.selectNodes("parts/QMPartInfo");
            for(Element partEle : partList){
                if(partEle.attributeValue("partNumber").equals(partNumber) && partEle.attributeValue("ZCMARK").equals(zcmark)){
                    partsElement.remove(partEle);
                }
            }

            addPartsToXml(parts, paceElement);

            Element element = document.getRootElement().element("QMFawTechnicsInfo");
            element.setAttributeValue("isComplete", isComplete);

            XmlUtil.writeDocument(document, VaContext.getCurrentTechXMLPath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 增加工序下参装件的集合
     * add by hz 17/10/30
     */
    public static boolean addParticipateParts(String stepOid,
                                              Vector<Map<String, String>> parts, String isComplete) {
        try {
            Document document = ((NewTechnicsPart) VaContext.getMainFrame()).getCurrentTechnics();
//            Document document = XmlUtil.getDocument(new File(VaContext.getCurrentTechXMLPath()));
            Element techInfoElement = document.getRootElement();
            Element stepElement = getStepElement(techInfoElement, stepOid);
            String partNumber = parts.get(0).get("partNumber");
            String zcmark = parts.get(0).get("ZCMARK");
//            String hasepm = parts.get(0).get("HASEPM");
            Element partsElement = stepElement.element("parts");
            List<Element> partList = stepElement.selectNodes("parts/QMPartInfo");
            for(Element partEle : partList){
                if(partEle.attributeValue("partNumber").equals(partNumber)
                        && partEle.attributeValue("ZCMARK").equals(zcmark)){
                    partsElement.remove(partEle);
                }
            }
            addPartsToXml(parts, stepElement);

            Element element = document.getRootElement().element("QMFawTechnicsInfo");
            element.setAttributeValue("isComplete", isComplete);

            XmlUtil.writeDocument(document, VaContext.getCurrentTechXMLPath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除工步下参装件的集合
     */
    public static boolean deleteParticipateParts(
            Vector<Map<String, String>> parts) {
        try {
            Document document = XmlUtil.getDocument(new File(VaContext.getCurrentTechXMLPath()));
            // 获得节点的命名空间
            String uri = document.getRootElement().getNamespaceURI();
            // 将uri存入map中
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("xx", uri);
            org.dom4j.XPath xpath = DocumentHelper
                    .createXPath("/xx:technics/xx:QMFawTechnicsInfo/xx:steps/xx:QMProcedureInfo/xx:paces/xx:QMProcedureInfo/xx:parts/xx:QMPartInfo");
            xpath.setNamespaceURIs(map);
            List list = xpath.selectNodes(document);
            if (deleteParts(list, parts)) {
                Element element = document.getRootElement().element("QMFawTechnicsInfo");
                element.setAttributeValue("isComplete", "false");
                XmlUtil.writeDocument(document, VaContext.getCurrentTechXMLPath());
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除工序下参装件的集合
     */
    public static boolean deleteStepParticipateParts(
            Vector<Map<String, String>> parts) {
        try {
            Document document = XmlUtil.getDocument(new File(VaContext.getCurrentTechXMLPath()));
            // 获得节点的命名空间
            String uri = document.getRootElement().getNamespaceURI();
            // 将uri存入map中
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("xx", uri);
            org.dom4j.XPath xpath = DocumentHelper
                    .createXPath("/xx:technics/xx:QMFawTechnicsInfo/xx:steps/xx:QMProcedureInfo/xx:parts/xx:QMPartInfo");
            xpath.setNamespaceURIs(map);
            List list = xpath.selectNodes(document);
            if (deleteParts(list, parts)) {
                Element element = document.getRootElement().element("QMFawTechnicsInfo");
                element.setAttributeValue("isComplete", "false");
                XmlUtil.writeDocument(document, VaContext.getCurrentTechXMLPath());
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取到工步的parts节点
     *
     * @param techInfoElement 工艺节点
     * @param stepOid         工序oid
     * @param paceOid         工步oid
     * @return
     */
    private static Element getPaceElement(Element techInfoElement, String stepOid, String paceOid) {
        Element partsElement = null;
        flag:
        for (Iterator it = techInfoElement.element("QMFawTechnicsInfo")
                .element("steps").elementIterator("QMProcedureInfo"); it
                     .hasNext(); ) {
            Element procedureElement = (Element) it.next();
            String stepId = procedureElement.attributeValue("bsoID");
            if (stepOid.equals(stepId)) {
                for (Iterator ite = procedureElement.element("paces").elementIterator("QMProcedureInfo"); ite.hasNext(); ) {
                    Element paceElement = (Element) ite.next();
                    String paceId = paceElement.attributeValue("bsoID");
                    if (paceOid.equals(paceId)) {
                        partsElement = paceElement;
                        break flag;
                    }

                }
            }
        }
        return partsElement;
    }

    /**
     * 获取到工序的parts节点
     *
     * @param techInfoElement 工艺节点
     * @param stepOid         工序oid
     * @return add by hz 17/10/30
     */
    private static Element getStepElement(Element techInfoElement, String stepOid) {
        Element partsElement = null;
        for (Iterator it = techInfoElement.element("QMFawTechnicsInfo")
                .element("steps").elementIterator("QMProcedureInfo"); it
                     .hasNext(); ) {
            Element procedureElement = (Element) it.next();
            String stepId = procedureElement.attributeValue("bsoID");
            if (stepOid.equals(stepId)) {
                partsElement = procedureElement;
            }
        }
        return partsElement;
    }

    /**
     * 增加到xml中
     *
     * @param parts       参装件的集合
     * @param paceElement 工步节点
     */
    private static void addPartsToXml(Vector<Map<String, String>> parts,
                                      Element paceElement) {
        if ((parts != null) && (parts.size() > 0)) {
            for (int i = 0; i < parts.size(); i++) {
                Map temp = (Map) parts.get(i);
                Element part = BomXMLUtil.generatePartData(temp, null);
                paceElement.element("parts").add(part);
//				XmlUtility.setAttributeValue(paceElement.element("parts"), "ARRT", (String)temp.get("ZCMARK"));
            }
        }
    }

    /**
     * xml中删除参装件
     */
    private static boolean deleteParts(List list,
                                       Vector<Map<String, String>> parts) {
        boolean flag = true;
        if ((parts != null) && (parts.size() > 0)) {
            for (int i = 0; i < parts.size(); i++) {
                Map temp = (Map) parts.get(i);
                String occId = String.valueOf(temp.get("occId"));
                flag = flag && deletePartsfromXml(occId, list);
            }
            return flag;
        }
        return !flag;
    }

    /**
     * xml中删除参装件
     */
    private static boolean deletePartsfromXml(String occId, List list) {
        if(occId.contains(",")){
            return deletePartsfromXml2(occId,list);
        }
        for (int i = 0; i < list.size(); i++) {
            Element element = (Element) list.get(i);// 转型为Element
            String occIds = element.attributeValue("occId");
            if (occIds != null) {
                String[] array = occIds.split(",");
                if (array != null) {
                    for (int j = 0; j < array.length; j++) {
                        if (array[j].equals(occId)) {
                            if (array.length == 1) {
                                element.getParent().remove(element);
                            } else {
                                String result = filterArray(array, j);
                                XmlUtil.setAttribute(element, "occId", result);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean deletePartsfromXml2(String occId, List list) {
        List<String> occIdList = Arrays.asList(occId.split(","));
        for (int i = 0; i < list.size(); i++) {
            Element element = (Element) list.get(i);// 转型为Element
            String occIds = element.attributeValue("occId");
            if (occIds != null) {
                String[] array = occIds.split(",");
                for(String occIdStr : array){
                    if(occIdList.contains(occIdStr)){
                        element.getParent().remove(element);
                    }
                }
            }
        }
        return true;
    }

    /**
     * 删除array中index为j的字符
     *
     * @param array
     * @param j
     * @return
     */
    private static String filterArray(String[] array, int j) {
        if (array == null) {
            return null;
        }
        String result = "";
        for (int m = 0; m < array.length; m++) {
            if (m != j) {
                result += array[m] + ",";
            }
        }
        return result.substring(0, result.length() - 1);
    }

    /**
     * 根据ID获取工序内容
     *
     * @param stepOid
     * @return
     */
    public static String getStepValue(String stepOid) {
        Document document = XmlUtil.getDocument(new File(VaContext.getCurrentTechXMLPath()));
        Element techInfoElement = document.getRootElement();
        Element stepElement = getStepElement(techInfoElement, stepOid);
        Element stepContent = stepElement.element("procedureContent");
        String stepValue = stepContent.getText();
        return stepValue;
    }

}
