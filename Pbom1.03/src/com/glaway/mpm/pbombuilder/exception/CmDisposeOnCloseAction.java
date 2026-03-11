package com.glaway.mpm.pbombuilder.exception;

import java.awt.Window;
import java.awt.event.ActionEvent;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.util.PviewTask;

/**
 * <br>Created on 2012-10-29
 * @author chenyunlong
 */
public class CmDisposeOnCloseAction extends CmAction {
   private static final long serialVersionUID = 8241982231862178685L;

   Window owner;
   public CmDisposeOnCloseAction(Window owner, String caption) {
      super(caption);
      this.owner = owner;
   }

   @Override
   public void actionPerformed(ActionEvent evt) {
      PviewTask.tryUnregisterTaskExecutor(owner);
      
      owner.dispose();
   }
}
