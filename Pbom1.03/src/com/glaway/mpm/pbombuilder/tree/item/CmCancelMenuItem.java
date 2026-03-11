package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.event.ActionEvent;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;


/**
 * <br>Created on 2012-10-23
 * @author chenyunlong
 */
public class CmCancelMenuItem extends CmMenuItem {
   private static final long serialVersionUID = 1221814605501591373L;

   public CmCancelMenuItem() {
      setText("取消");
      setEnabled(false);
      setVisible(false);
   }

   @Override
   protected void actionPerformed(ActionEvent evt) {
//      CmLogger.getLogger().debug("关闭右键菜单");
   }

}
