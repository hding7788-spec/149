/**
 * <br>Created on 2011-3-23
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.pbombuilder.data;

import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmXML;

/**
 * <br>Created on 2012-10-20
 * @author chenyunlong
 */
public class CmTreeNodeStructureProxy implements CmXmlProxy {
   private static final long serialVersionUID = 8158033732734100387L;
   /**
    *XML���ṹ�����ڵ���
    */
   public static String      XMLTITLE         = "TreeNode_Structure";

   private CmTreeNode        node;

   public CmTreeNodeStructureProxy() {}

   public void parse(CmXML xml) {
      this.node = CmTreeNode.load4XML(xml);

   }

   public CmTreeNode getTreeNode() {
      return node;
   }

   public void setTreeNode(CmTreeNode node) {
      this.node = node;
   }
   public CmXML toXML() {
      CmXML xml = new CmXML(XMLTITLE);
      if (node != null)
         xml.append(node.write2XML());

      return xml;
   }
}