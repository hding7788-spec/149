package com.glaway.mpm.pbombuilder.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class CmAction extends AbstractAction {
   private static final long serialVersionUID = -3081574941764803039L;

   public CmAction() {
      super();
   }

   public CmAction(String label) {
      this(label, label, null, null);
   }

   public CmAction(Icon icon) {
      this(null, null, icon, null);
   }

   public CmAction(String label, Icon icon) {
      this(label, null, icon, null);
   }

   public CmAction(String label, String toolTip) {
      this(label, toolTip, null, null);
   }

   public CmAction(String label, KeyStroke key) {
      this(label, label, null, key);
   }

   public CmAction(String label, Icon icon, KeyStroke key) {
      this(label, label, icon, key);
   }

   public CmAction(String label, String toolTip, Icon icon) {
      this(label, toolTip, icon, null);
   }

   public CmAction(String label, String toolTip, Icon icon, KeyStroke key) {
      this();

      setLabel(label);
      setToolTipText(toolTip);
      setIcon(icon);
      setAccelerator(key);
   }

   public void setLabel(String label) {
      if (label != null)
         putValue(Action.NAME, label);
   }

   public void setToolTipText(String toolTipText) {
      if (toolTipText != null)
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
   }

   public void setIcon(Icon icon) {
      if (icon != null)
         putValue(Action.SMALL_ICON, icon);
   }

   public void setAccelerator(KeyStroke keyStroke) {
      if (keyStroke != null)
         putValue(Action.ACCELERATOR_KEY, keyStroke);
   }

   // This method is called when the action is invoked
   public void actionPerformed(ActionEvent evt) {}

   private ArrayList<JComponent> sources;

   public void addSource(JComponent comp) {
      if (sources == null)
         sources = new ArrayList<JComponent>();
      sources.add(comp);
   }

   public ArrayList<JComponent> getSources() {
      return sources;
   }

   public void setSources(ArrayList<JComponent> sources) {
      this.sources = sources;
   }

   public void setSourcesEnabled(boolean isEnabled) {
      if (sources != null)
         for (JComponent source : sources)
            source.setEnabled(isEnabled);
   }

   public void cancel() {}

   public boolean isCancelable() {
      return false;
   }
}