package com.glaway.mpm.pbombuilder.action;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.pbombuilder.pview.CmPViewFactory;
import com.glaway.mpm.pbombuilder.pview.CmPViewImpl;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

public class CmPViewZoomAll extends CmAction {
   private static final long serialVersionUID = -8971589729968345172L;
   private String name;
   
   public CmPViewZoomAll(String name) {
      super("缩放全部", new ImageIcon(CmUtil.getImageFromServer("zoom_all.png")));
      this.name = name;
   }

   @Override
   public void actionPerformed(ActionEvent evt) {
      CmPViewImpl pviewImpl = CmPViewFactory.getPViewImpl(name);
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
