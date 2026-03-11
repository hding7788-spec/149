package com.glaway.mpm.visual.view.pview;

import java.util.Enumeration;

import javax.vecmath.Matrix4d;

import com.ptc.pview.pvkapp.ComponentNode;

/**
 * <br>Created on 2011-3-11
 * @author Dennis Huang - ���ٽ�
 */
public interface VaPViewNode {

   public String getId();

   public boolean isLeaf();

   public Enumeration<VaPViewNode> children();

   public long getPartOid();

   public Matrix4d getMatrix();

   public ComponentNode getComponentNode();

   public void setComponentNode(ComponentNode compNode);
   
   public void addChild(VaPViewNode child);
}
