
package com.glaway.mpm.pbombuilder.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * <br>Created on 2012-10-18
 * @author chenyunlong
 */
public class CmHideOnCloseAction extends CmAction {
	   private static final long serialVersionUID = 8241982231862178685L;

	   Component owner;
	   public CmHideOnCloseAction(Component owner) {
	      super("关闭");
	      this.owner = owner;
	   }

	   @Override
	   public void actionPerformed(ActionEvent evt) {
	      owner.setVisible(false);
	   }
	}
