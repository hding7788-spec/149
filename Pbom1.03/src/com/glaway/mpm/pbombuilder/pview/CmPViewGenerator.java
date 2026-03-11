package com.glaway.mpm.pbombuilder.pview;

/**
 * <br>Created on 2012-10-18
 * @author chenyunlong
 */
public interface CmPViewGenerator {
 void generatePVStructure(String finishFrame);
 CmPViewImpl getPviewImpl();
}
