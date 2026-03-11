package com.glaway.mpm.pbombuilder.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JScrollPane;
import com.glaway.mpm.pbombuilder.tree.CmCBTree;

/**
 * <br>Created on 2012-10-18
 * @author chenyunlong
 */
public class CmScrollPaneCBTree extends JScrollPane {
   private static final long serialVersionUID = 5106882626216586255L;
   //private static final CmLogger log = CmLogger.getLogger(ICBScrollPaneWithImage.class);

   private Image             image;

   public CmScrollPaneCBTree() {
      super();

      setOpaque(true);
      getViewport().setOpaque(false);
      image =Toolkit.getDefaultToolkit().getImage("D:\\codebase_back\\codebase\\ext\\cobi\\Images\\background.gif");
      setViewportView(new CmCBTree());
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      setBackground(Color.WHITE);

      if (image != null) {
         int height = image.getHeight(this);
         int width = image.getWidth(this);

         if (height != -1 && height > getHeight())
            height = getHeight();

         if (width != -1 && width > getWidth())
            width = getWidth();

         int x = (int) (((double) (getWidth() - width)) / 2.0);
         int y = (int) (((double) (getHeight() - height)) / 2.0);

         g.drawImage(image, x, y, width, height, this);
      }
   }

   public CmCBTree getTree() {
      return (CmCBTree) getViewport().getView();
   }
}