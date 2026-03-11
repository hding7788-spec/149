package com.glaway.mpm.pbombuilder.data;

import java.io.Serializable;
import com.glaway.mpm.pbombuilder.util.CmXML;

/**
 * <br>Created on 2012-10-29
 * @author chenyunlong
 */
public interface CmXmlProxy extends Serializable {
   CmXML toXML();
}
