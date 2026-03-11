package com.glaway.mpm.util;

import org.dom4j.Element;

public class AclUtl {
    public static boolean sign3Enable(Element currentElement){
        if(currentElement==null) return true;
        if("正式工艺文件".equals( currentElement.attributeValue("PPLANTYPE"))){
            if("数控工艺".equals( currentElement.attributeValue("technicsType"))
                    ||"M".equals(currentElement.attributeValue("PHASE_CODE"))){
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
}
