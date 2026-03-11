package com.glaway.mpm.sop.util;

import com.glaway.mpm.util.XmlUtility;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

public class SopXMLUtility {

    public static Element getSOPElement(Element element) {
        Element childEle = element.element("SOP");
        if (childEle == null)
            childEle = element.addElement("SOP");
        return childEle;
    }

    public static Element getParameterElement(Element element) {
        Element childEle = element.element("SOPParameter");
        if (childEle == null)
            childEle = element.addElement("SOPParameter");
        return childEle;
    }

    public static List<Element> getParameterInfo(Element element) {
        Element childEle = element.element("SOPParameter");
        if (childEle == null)
            childEle = element.addElement("SOPParameter");
        return childEle.elements();
    }

    public static Element getRelatedSopTech(Element element){
        Element childEle = element.element(XmlUtility.SOP_GROUP);
        if (childEle == null)
            childEle = element.addElement(XmlUtility.SOP_GROUP);
        return childEle;
    }

    public static List<Element> getRelatedSopTechs(Element element) {
        Element childEle = element.element(XmlUtility.SOP_GROUP);
        if (childEle == null)
            childEle = element.addElement(XmlUtility.SOP_GROUP);
        return childEle.elements();
    }

    public static Element createParameterInfo(Element element) {
        return DocumentHelper.createElement("ParameterInfo");
    }


}
