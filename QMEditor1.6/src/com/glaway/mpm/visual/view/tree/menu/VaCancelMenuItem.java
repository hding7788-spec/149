package com.glaway.mpm.visual.view.tree.menu;

import java.awt.event.ActionEvent;


/**
 * <br>Created on 2012-10-23
 * @author chenyunlong
 */
public class VaCancelMenuItem extends VaMenuItem {
   private static final long serialVersionUID = 1221814605501591373L;

   public VaCancelMenuItem() {
      setText("取消");
   }

   @Override
   protected void actionPerformed(ActionEvent evt) {
//      CmLogger.getLogger().debug("关闭右键菜单");
   }

}
