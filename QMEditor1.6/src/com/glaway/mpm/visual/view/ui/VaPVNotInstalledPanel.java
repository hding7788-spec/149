package com.glaway.mpm.visual.view.ui;

import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class VaPVNotInstalledPanel extends JPanel {
   private static final long serialVersionUID = -2664327446611322955L;
   //private Image             image            = new ImageIcon(CmUtil.getImageFromServer("PVLiteNotInstalled.png")).getImage();
   private Dimension minimumSize=new Dimension(400, 400);
   private Dimension preferredSize=new Dimension(800,600);

   public VaPVNotInstalledPanel() {
      super();
      setLayout(new GridLayout(0, 1));
   }

   public Dimension getMinimumSize() {
      return minimumSize;
   }

   public Dimension getPreferredSize() {
      return preferredSize;
   }

   
   @Override
   public void setMinimumSize(Dimension dimension) {
      minimumSize=dimension;
   }

   @Override
   public void setPreferredSize(Dimension dimension) {
      preferredSize=dimension;
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
g.drawString("Not Install", 100, 100);
//      ((Graphics2D) g).setPaint(new GradientPaint(0, 0, CmTheme.CM_BLUE, 0, getHeight(), CmTheme.CM_TURQUOISE));
//      ((Graphics2D) g).fillRect(0, 0, getWidth(), getHeight());
//
//      if (image != null) {
//         int height = image.getHeight(this);
//         int width = image.getWidth(this);
//
//         if (height != -1 && height > getHeight())
//            height = getHeight();
//
//         if (width != -1 && width > getWidth())
//            width = getWidth();
//
//         int x = (int) (((double) (getWidth() - width)) / 2.0);
//         int y = (int) (((double) (getHeight() - height)) / 2.0);
//
//         g.drawImage(image, x, y, width, height, this);
//      }
   }
}