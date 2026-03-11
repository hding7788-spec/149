package com.glaway.mpm.pbombuilder.gui;

import javax.swing.plaf.ColorUIResource;

import com.jgoodies.looks.plastic.theme.ExperienceBlue;

public class CmTheme extends ExperienceBlue {
   public static final ColorUIResource CM_BLUE                 = new ColorUIResource(49, 48, 99);
   public static final ColorUIResource CM_TURQUOISE            = new ColorUIResource(102, 146, 181);
   public static final ColorUIResource CM_GREY                 = new ColorUIResource(102, 102, 102);

   public static final ColorUIResource GREEN_CHECK             = new ColorUIResource(33, 161, 33);
   public static final ColorUIResource ORANGE_FOCUS            = new ColorUIResource(245, 165, 16);

   public static final ColorUIResource CM_BACKUPGROUND         = new ColorUIResource(0xF0, 0xF0, 0xF0);
   public static final ColorUIResource CM_BACKUPGROUND__DARKER = new ColorUIResource(0xFE, 0xFE, 0xFE);

   public static final ColorUIResource CM_EXCEPTION_GROUND     = new ColorUIResource(0xff, 0xff, 0x6f);
   public static final ColorUIResource CM_ZJ_WARN_GROUND       = new ColorUIResource(0x7e, 0xef, 0x9b);
   public static final ColorUIResource CM_YD_WARN_GROUND       = new ColorUIResource(0xeb, 0x95, 0x83);

   public static final ColorUIResource CM_PVIEW1               = new ColorUIResource(158, 155, 145);
   public static final ColorUIResource CM_PVIEW2               = new ColorUIResource(227, 225, 213);

   public CmTheme() {}

   public String getName() {
      return "CmCustom";
   }

   protected ColorUIResource getPrimary1() {
      return CM_TURQUOISE;
   }

   protected ColorUIResource getPrimary2() {
      return CM_GREY;
   }

   protected ColorUIResource getPrimary3() {
      return CM_TURQUOISE;
   }

   // Scroll Bar
   public ColorUIResource getPrimaryControlShadow() {
      return new ColorUIResource(74, 76, 142);
   }

   // Control Main Color dark
   protected ColorUIResource getSecondary1() {
      return new ColorUIResource(137, 137, 137);
   }

   // Control Main Color light
   protected ColorUIResource getSecondary2() {
      return new ColorUIResource(233, 233, 233);
   }

   // Control Main Color
   protected ColorUIResource getSecondary3() {
      return new ColorUIResource(210, 210, 210);
   }

   public ColorUIResource getFocusColor() {
      return ORANGE_FOCUS;
   }

   // CheckButton
   public ColorUIResource getToggleButtonCheckColor() {
      return GREEN_CHECK;
   }

   public ColorUIResource getMenuSelectedBackground() {
      return CM_TURQUOISE;
   }

   public ColorUIResource getMenuSelectedForeground() {
      return WHITE;
   }

   public ColorUIResource getMenuItemBackground() {
      return WHITE;
   }
}
