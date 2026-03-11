package com.glaway.mpm.visual.bean;

import java.io.Serializable;
import java.util.Vector;

import javax.vecmath.Matrix4d;

import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class VaPartUsesOcc implements Serializable {
   private static final long    serialVersionUID        = 1360564817458126793L;

   public static final Matrix4d STD_MATRIX4D;

   private VaInstanceData               instanceData;
   private Matrix4d             matrix;
   // part具体实例对应BOM实例树节点，一个part具体实例在树上展开后可以对应多个节点
   private Vector<VaTreeNode>   treeNodes;

   static {
      STD_MATRIX4D = new Matrix4d();
      STD_MATRIX4D.setIdentity();
   }

   public static VaPartUsesOcc newVaPartUsesOcc(VaInstanceData instanceData, Matrix4d matrix) {
      VaPartUsesOcc ret = new VaPartUsesOcc();
      ret.instanceData = instanceData;
      ret.matrix = matrix;

      return ret;
   }
   
   public static VaPartUsesOcc newVaPartUseOcc(VaInstanceData instanceData) {
      return newVaPartUsesOcc(instanceData, STD_MATRIX4D);
   }

   private VaPartUsesOcc() {
      treeNodes = new Vector<VaTreeNode>(1);
   }

   public Matrix4d getMatrix() {
      return matrix;
   }
   
   public VaInstanceData getInstanceData() {
      return instanceData;
   }
   
   public String getInstanceId() {
      return instanceData.getIdentifier();
   }

   public Vector<VaTreeNode> getTreeNodes() {
      return treeNodes;
   }

   public void addTreeNode(VaTreeNode node) {
      this.treeNodes.add(node);
   }
}
