/**
 * <br>Created on 2010-10-23
 * @author Dennis Huang - ���ٽ�
 */
package com.glaway.mpm.visual.view.ui;

import javax.swing.JPanel;

import com.glaway.mpm.task.CmTaskExecutor;
import com.glaway.mpm.task.exception.CmTaskException;

/**
 * <br>Created on 2010-10-23
 * @author Dennis Huang - ���ٽ�
 */
public abstract class VaAbstractPanel extends JPanel implements CmTaskExecutor{
   private volatile boolean executorActive;

   public VaAbstractPanel() {
      super();
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
//         LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new CmTheme());

      } catch (Exception e) {
         e.printStackTrace();
      }
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

   protected abstract void registerTaskExecutor() throws CmTaskException;

   protected abstract void unregisterTaskExecutor() throws CmTaskException;
}
