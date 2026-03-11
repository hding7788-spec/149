package com.glaway.mpm.visual.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * GUI界面处理静态工具类
 * <br>Created on 2012-10-18
 * @author chenyunlong
 */
public class VaGuiUtil {
   public static Rectangle getScreenCenter(int width, int height) {
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

      int left = (screen.width - width) / 2;
      int top = (screen.height - height) / 2;
      return new Rectangle(left, top, width, height);
   }

   public static JLabel getLabelFor(String text) {
      JLabel ret = new JLabel();

      ret.setText(text);
      ret.setHorizontalAlignment(SwingConstants.RIGHT);
      ret.setHorizontalTextPosition(SwingConstants.RIGHT);
      ret.setVerticalAlignment(SwingConstants.TOP);
      ret.setVerticalTextPosition(SwingConstants.TOP);

      return ret;
   }

   public static void enable(Object sender) {
      if (sender instanceof Component) {
         Component comp = (Component) sender;         
         comp.setEnabled(true);
      }
   }
   
   public static void disable(Object sender) {
      if (sender instanceof Component) {
         Component comp = (Component) sender;
         comp.setEnabled(false);
      }
   }
   
   public static JDialog getDialog(Component content, int width, int height) {
      JDialog dialog = new JDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      
      dialog.setBounds(getScreenCenter(width, height));
      Container container = dialog.getContentPane();
      container.setLayout(new BorderLayout());
      container.add(content, BorderLayout.CENTER);
      
      return dialog;
   }
}
