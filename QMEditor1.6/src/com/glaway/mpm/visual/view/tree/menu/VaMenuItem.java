/**
 * <br>Created on 2011-3-22
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree.menu;

import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import com.glaway.mpm.visual.bean.VaLightType;
import com.glaway.mpm.visual.util.VaUtil;
import com.glaway.mpm.visual.view.action.VaAction;

/**
 * <br>Created on 2011-3-22
 * @author Alex.Huang - ����
 */
public abstract class VaMenuItem extends JMenuItem {
   public VaMenuItem() {
      setAction(new MenuAction(this));
   }

   protected abstract void actionPerformed(ActionEvent evt);

   protected void setIconStr(String iconStr) {
      Image image = VaUtil.getImageFromServer(iconStr);
      if (image != null) {
         Icon icon = new ImageIcon(image);
         if (icon != null)
            setIcon(icon);
      }
   }

   protected void setIcon(VaLightType type) {
      Image image = type.getIconImage();
      if (image != null) {
         Icon icon = new ImageIcon(image);
         if (icon != null)
            setIcon(icon);
      }
   }

   protected VaAction getVaAction() {
      return (VaAction) getAction();
   }

   protected class MenuAction extends VaAction {

      private static final long serialVersionUID = 766514191584026508L;
      private VaMenuItem        item;

      public MenuAction(VaMenuItem item) {
         this.item = item;
      }

      @Override
      public void actionPerformed(ActionEvent evt) {
         item.actionPerformed(evt);
      }

   }

   @Override
   public String toString() {
      return "VaMenuItem [text:"+getText()+"  Enabled:"+isEnabled()+"]";
   }

}
