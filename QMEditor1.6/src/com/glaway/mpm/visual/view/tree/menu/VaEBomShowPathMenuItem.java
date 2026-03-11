/**
 * <br>Created on 2011-3-28
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree.menu;

import java.awt.Window;
import java.awt.event.ActionEvent;

import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class VaEBomShowPathMenuItem extends VaMenuItem {

   private static final long serialVersionUID = -8396429397442896329L;
   private VaTreeNode        node;
   private Window            owner;

   public VaEBomShowPathMenuItem(VaTreeNode currNode, Window owner) {
      this.node = currNode;
      this.owner = owner;
      setText("查看路径");
      setIconStr("details.gif");
   }

   /**
    * @param evt
    */
   @Override
   protected void actionPerformed(ActionEvent evt) {
      VaEBomNodePathTreeDialog pathTreeDialog = VaEBomNodePathTreeDialog.getEBomNodePathTreeDialog(owner, node);
      pathTreeDialog.setVisible(true);
   }

}
