package com.glaway.mpm.visual.bean;

import java.io.Serializable;

import com.glaway.mpm.visual.util.VaXML;

/**
 * <br>Created on 2012-10-29
 * @author chenyunlong
 */
public interface VaXmlProxy extends Serializable {
   VaXML toXML();
}
