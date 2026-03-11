package com.glaway.mpm.pbombuilder.data;

import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.tree.CmLightType;
import com.glaway.mpm.pbombuilder.util.CmUtil;

/**
 * <br>Created on 2012-10-22
 * @author chenyunlong
 */
public abstract class CmMenuItem extends JMenuItem {
   public CmMenuItem() {
      setAction(new MenuAction(this));
   }

   protected abstract void actionPerformed(ActionEvent evt);

   protected void setIconStr(String iconStr) {
      Image image = CmUtil.getImageFromServer(iconStr);
      if (image != null) {
         Icon icon = new ImageIcon(image);
         if (icon != null)
            setIcon(icon);
      }
   }

   protected void setIcon(CmLightType type) {
      Image image = type.getIconImage();
      if (image != null) {
         Icon icon = new ImageIcon(image);
         if (icon != null)
            setIcon(icon);
      }
   }

   protected CmAction getCmAction() {
      return (CmAction) getAction();
   }

   protected class MenuAction extends CmAction {

      private static final long serialVersionUID = 766514191584026508L;
      private CmMenuItem        item;

      public MenuAction(CmMenuItem item) {
         this.item = item;
      }

      @Override
      public void actionPerformed(ActionEvent evt) {
         item.actionPerformed(evt);
      }

   }

   @Override
   public String toString() {
      return "CmMenuItem [text:"+getText()+"  Enabled:"+isEnabled()+"]";
   }
   
}
