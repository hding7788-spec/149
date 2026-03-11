package com.glaway.mpm.pbombuilder.panel;

import java.awt.Panel;
import com.glaway.mpm.pbombuilder.pview.CmPViewFactory;
import com.glaway.mpm.pbombuilder.pview.CmPViewImpl;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;

public class CmPViewAScenePanel extends Panel {
   private static final long serialVersionUID = -4066773024146918225L;

   private String            name;
   private ShapeScene        shapeScene;
   private ShapeView         shapeView;

   public CmPViewAScenePanel(String name) {
      super();
      this.name = name;
   }

   public void addNotify() {
      super.addNotify();
      if (CmPViewImpl.isPviewInitialized() && CmPViewFactory.getPViewImpl(name) != null) {
         CmPViewFactory.getPViewImpl(name).initPView();
      }
   }

   public ShapeScene getShapeScene() {
      return shapeScene;
   }

   public void setShapeScene(ShapeScene shapeScene) {
      this.shapeScene = shapeScene;
   }

   public ShapeView getShapeView() {
      return shapeView;
   }

   public void setShapeView(ShapeView shapeView) {
      this.shapeView = shapeView;
   }
}
