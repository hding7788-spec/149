package com.glaway.mpm.pbombuilder.impl;

import java.util.Enumeration;

import javax.vecmath.Matrix4d;

import com.ptc.pview.pvkapp.ComponentNode;

/**
 * <br>Created on 2012-11-21
 * @author chenyunlong
 */
public interface CmPViewNode {

   public String getId();

   public boolean isLeaf();

   public Enumeration<CmPViewNode> children();

   public long getPartOid();

   public Matrix4d getMatrix();

   public ComponentNode getComponentNode();

   public void setComponentNode(ComponentNode compNode);
   
   public void addChild(CmPViewNode child);
}
