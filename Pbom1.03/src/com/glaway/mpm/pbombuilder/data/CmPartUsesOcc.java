package com.glaway.mpm.pbombuilder.data;

import java.io.Serializable;
import java.util.Vector;
import javax.vecmath.Matrix4d;

import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmInstanceData;

public class CmPartUsesOcc implements Serializable {
   private static final long    serialVersionUID        = 1360564817458126793L;
   private static final CmLogger log = CmLogger.getLogger(CmPartUsesOcc.class.getName());
   public static final Matrix4d STD_MATRIX4D;

   private CmInstanceData               instanceData;
   private Matrix4d             matrix;
   // part具体实例对应BOM实例树节点，一个part具体实例在树上展开后可以对应多个节点
   private Vector<CmTreeNode>   treeNodes;

   static {
      STD_MATRIX4D = new Matrix4d();
      STD_MATRIX4D.setIdentity();
   }

   public static CmPartUsesOcc newCmPartUseOcc(CmInstanceData instanceData, Matrix4d matrix) {
      CmPartUsesOcc ret = new CmPartUsesOcc();
      ret.instanceData = instanceData;
      ret.matrix = matrix;

      return ret;
   }
   
   public static CmPartUsesOcc newCmPartUseOcc(CmInstanceData instanceData) {
      return newCmPartUseOcc(instanceData, STD_MATRIX4D);
   }

   private CmPartUsesOcc() {
      treeNodes = new Vector<CmTreeNode>(1);
   }

   public Matrix4d getMatrix() {
      return matrix;
   }
   
   public CmInstanceData getInstanceData() {
      return instanceData;
   }
   
   public String getInstanceId() {
      return instanceData.getIdentifier();
   }

   public Vector<CmTreeNode> getTreeNodes() {
      return treeNodes;
   }

   public void addTreeNode(CmTreeNode node) {
      this.treeNodes.add(node);
   }
}
