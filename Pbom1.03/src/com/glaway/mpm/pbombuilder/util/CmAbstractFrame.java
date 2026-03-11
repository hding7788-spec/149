package com.glaway.mpm.pbombuilder.util;

import javax.swing.JFrame;
import com.glaway.mpm.pbombuilder.bom.CmTaskExecutor;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

/**
 * <br>Created on 2012-10-19
 * @author chenyunlong
 */
public abstract class CmAbstractFrame extends JFrame implements CmTaskExecutor {
	private static final long serialVersionUID = 1L;
	private volatile boolean executorActive; 
   
   public CmAbstractFrame() {
      super();
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      
   }

   protected void initUI() throws CmTaskException {
      initLookAndFeel();
      initDimension();
      initActions();
      initComponents();
      initLayout();
      loadInitDatas();
      
      registerTaskExecutor();      
   }

   protected void initLookAndFeel() {
      try {
         LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new CmTheme());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   protected abstract void initDimension();

   protected abstract void initActions();

   protected abstract void initComponents();

   protected abstract void initLayout();

   protected abstract void loadInitDatas();
   
   protected abstract void registerTaskExecutor() throws CmTaskException;
   
   protected abstract void unregisterTaskExecutor() throws CmTaskException;
   
   public boolean isExecutorActive() {
      return this.executorActive;
   }

   public void setExecutorActive(boolean executorActive) {
      this.executorActive = executorActive;
   }
}
