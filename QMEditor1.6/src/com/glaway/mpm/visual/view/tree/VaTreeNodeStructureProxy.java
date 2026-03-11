/**
 * <br>Created on 2011-3-23
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree;

import com.glaway.mpm.visual.bean.VaXmlProxy;
import com.glaway.mpm.visual.util.VaXML;

/**
 * <br>Created on 2012-10-20
 * @author chenyunlong
 */
public class VaTreeNodeStructureProxy implements VaXmlProxy {
   private static final long serialVersionUID = 8158033732734100387L;

   public static String      XMLTITLE         = "TreeNode_Structure";

   private VaTreeNode        node;

   public VaTreeNodeStructureProxy() {}

   public void parse(VaXML xml) {
      this.node = VaTreeNode.load4XML(xml);

   }

   public VaTreeNode getTreeNode() {
      return node;
   }

   public void setTreeNode(VaTreeNode node) {
      this.node = node;
   }
   public VaXML toXML() {
      VaXML xml = new VaXML(XMLTITLE);
      if (node != null)
         xml.append(node.write2XML());

      return xml;
   }
}