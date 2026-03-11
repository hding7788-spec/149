package com.glaway.mpm.visual.view.action;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

public class VaPViewZoomAll extends VaAction {
   private static final long serialVersionUID = -8971589729968345172L;
   private String name;
   
   public VaPViewZoomAll(String name) {
      super("", new ImageIcon(VaPViewZoomAll.class.getResource("/image/zoom_all.png")));
      this.name = name;
   }

   @Override
   public void actionPerformed(ActionEvent evt) {
      VaPViewImpl pviewImpl = VaPViewFactory.getPViewImpl(name);
      AsyncEventCB asyncEvent = pviewImpl.getAsyncEvent("ZoomAll");

      ShapeView shapeView = pviewImpl.getPanelContext().getShapeView();
      try {
         shapeView.ZoomAll(asyncEvent.GetAsyncEventIf());
      } catch (MessageProtocolException e) {
         e.printStackTrace();
      } catch (ActorShutdownException e) {
         e.printStackTrace();
      } catch (InvalidActorException e) {
         e.printStackTrace();
      } catch (ConnectionLostException e) {
         e.printStackTrace();
      }
   }
}
