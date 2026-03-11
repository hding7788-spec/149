package com.glaway.mpm.pbombuilder.panel;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmPViewZoomAll;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.pview.CmPViewFactory;
import com.glaway.mpm.pbombuilder.pview.CmPViewZoomSelected;

public class CmPViewScenesPanel extends CmAbstractPanel {
   private static final long  serialVersionUID = -8722967403147623505L;

   private String             name;
   private CmAction           actZoomAll;
   private CmAction           actZoomSelected;

   private JButton            btnZoomAll;
   private JButton            btnZoomSelected;

   private CmPViewAScenePanel panelContext;

   public CmPViewScenesPanel(String name) {
      super();
      this.name = name;

      try {
         initUI();
      } catch (CmTaskException e) {
         e.printStackTrace();
      }
   }

   @Override
   protected void initActions() {
      actZoomAll = new CmPViewZoomAll(name);
      actZoomSelected = new CmPViewZoomSelected(name);
   }

   @Override
   protected void initComponents() {
      btnZoomAll = new JButton(actZoomAll);
      btnZoomAll.setForeground(Color.WHITE);

      btnZoomSelected = new JButton(actZoomSelected);
      btnZoomSelected.setForeground(Color.WHITE);

      panelContext = CmPViewFactory.getPViewImpl(name).getPanelContext();
   }

   @Override
   protected void initLayout() {
      this.setLayout(new BorderLayout());
      this.add(buildToolBar(), BorderLayout.NORTH);
      this.add(panelContext, BorderLayout.CENTER);
   }

   private JToolBar buildToolBar() {
      JToolBar toolBar = new JToolBar();
      toolBar.setBackground(CmTheme.CM_TURQUOISE);
      toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
      toolBar.setFloatable(false);
      toolBar.setRollover(true);

      toolBar.add(btnZoomAll);
      toolBar.add(btnZoomSelected);
      return toolBar;
   }

   @Override
   protected void initDimension() {}

   @Override
   protected void loadInitDatas() {}

   @Override
   protected void registerTaskExecutor() throws CmTaskException {}

   @Override
   protected void unregisterTaskExecutor() throws CmTaskException {}
}
