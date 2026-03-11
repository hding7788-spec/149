package com.glaway.mpm.sop.util;

import ext.casc.sop.constants.SopConstants;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SopPbomUtil {

    public static List<Map<String, String>> getPartInfo(Element partElement) {
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        Map<String, String> displayMap;
        displayMap = new HashMap<String, String>();
        displayMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE,"专业类别");
        mapList.add(displayMap);
        displayMap = new HashMap<String, String>();
        displayMap.put(SopConstants.SOP_IBA_PROCEDUCENAME,"工序名称");
        mapList.add(displayMap);
        displayMap = new HashMap<String, String>();
        displayMap.put(SopConstants.SOP_IBA_PROFESSIONALCODE,"专业代号");
        mapList.add(displayMap);
        displayMap = new HashMap<String, String>();
        displayMap.put(SopConstants.SOP_IBA_GONGXUJIANHAO,"工序简号");
        mapList.add(displayMap);
        displayMap = new HashMap<String, String>();
        displayMap.put(SopConstants.SOP_IBA_SECRET,"密级");
        mapList.add(displayMap);
        displayMap = new HashMap<String, String>();
        displayMap.put(SopConstants.SOP_IBA_TERM,"期限");
        mapList.add(displayMap);
        displayMap = new HashMap<String, String>();
        displayMap.put("ZZCJ","部门");
        mapList.add(displayMap);
        return mapList;
    }
}
