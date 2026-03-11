/**
 * <br>Created on 2011-2-24
 * @author renchengwei - �γ�ΰ
 */
package com.glaway.mpm.visual.bean;

import javax.swing.JPanel;

import com.glaway.mpm.visual.view.tree.VaTreeNode;


/**
 * <br>Created on 2011-2-24
 * @author renchengwei - �γ�ΰ
 */
public abstract class VaAbstractPart extends JPanel {
   public VaAbstractPart() {
      super();
   }

   public abstract JPanel getBasicInfoPanel();

   public abstract boolean check();

   public abstract VaTreeNode getLightPart();

}
