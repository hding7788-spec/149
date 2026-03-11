package com.glaway.mpm.pbombuilder.panel;

import java.awt.Window;

import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.pview.CmPViewFactory;
import com.glaway.mpm.pbombuilder.pview.CmPViewImpl;

public class CmMBomMainClientSplitPane extends JSplitPane {
   private static final long  serialVersionUID = -6214093173263211488L;

   private CmPViewScenesPanel productViewScenesPanel;
   private Window             owner;

   public CmMBomMainClientSplitPane(Window owner) {
      super();
      CmConnectFrame.startAnimFrame.setHeaderMessage("加载可视化面板");
      this.owner = owner;
      this.setOneTouchExpandable(true);
      this.setContinuousLayout(true);
      this.setDividerSize(10);

      if (CmPViewImpl.isPviewInitialized()) {
         Thread createPPRunner = new Thread() {
            public void run() {
               try {
                  productViewScenesPanel = new CmPViewScenesPanel(CmPViewFactory.PV_NAME_MBOM);
                  setLeftComponent(productViewScenesPanel);
               } catch (Exception ex) {
               }
            }
         };
         createPPRunner.start();
      } else
         setLeftComponent(new CmPVNotInstalledPanel());
      	setBorder(new EmptyBorder(0, 0, 0, 0));

      	CmConnectFrame.startAnimFrame.setHeaderMessage("完成加载可视化面板");
   }

   public CmPViewScenesPanel getPViewScenesPanel() {
      return productViewScenesPanel;
   }
}
