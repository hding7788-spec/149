package com.glaway.mpm.visual.view.ui;

import javax.swing.JFrame;

import com.glaway.mpm.visual.view.VaTheme;

public abstract class VaAbstractFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private volatile boolean executorActive; 
   
   public VaAbstractFrame() {
      super();
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      
   }

   protected void initUI() {
      initLookAndFeel();
      initDimension();
      initActions();
      initComponents();
      initLayout();
      loadInitDatas();
        
   }

   protected void initLookAndFeel() {
//      try {
////    	  LookUtils.setLookAndTheme(laf, theme);
////         LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new VaTheme());
//      } catch (Exception e) {
//         e.printStackTrace();
//      }
   }

   protected abstract void initDimension();

   protected abstract void initActions();

   protected abstract void initComponents();

   protected abstract void initLayout();

   protected abstract void loadInitDatas();
   
   
   public boolean isExecutorActive() {
      return this.executorActive;
   }

   public void setExecutorActive(boolean executorActive) {
      this.executorActive = executorActive;
   }
}
