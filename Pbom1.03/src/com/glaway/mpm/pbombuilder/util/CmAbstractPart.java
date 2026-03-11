/**
 * <br>Created on 2011-2-24
 * @author renchengwei - �γ�ΰ
 */
package com.glaway.mpm.pbombuilder.util;

import javax.swing.JPanel;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;


/**
 * <br>Created on 2011-2-24
 * @author renchengwei - �γ�ΰ
 */
public abstract class CmAbstractPart extends JPanel {
   public CmAbstractPart() {
      super();
   }

   public abstract JPanel getBasicInfoPanel();

   public abstract boolean check();

   public abstract CmTreeNode getLightPart();

}
