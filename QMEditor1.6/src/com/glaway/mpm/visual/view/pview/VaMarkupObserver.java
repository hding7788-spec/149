package com.glaway.mpm.visual.view.pview;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.query.QMLTemplateCache;
import com.glaway.mpm.visual.view.ui.VaPViewSearchPanel;
import com.ptc.pview.pvapi.ShapeSceneObserver;
import com.ptc.pview.pvkapp.ShapeInstance;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

public class VaMarkupObserver extends ShapeSceneObserver {
	private static VaLogger logger = VaLogger.getLogger(VaMarkupObserver.class);
	@Override
	protected void OnBeginUpdate() {
		//logger.debug("VaMarkupObserver.begin-update");
		super.OnBeginUpdate();
	}
	
	@Override
	protected void OnEndUpdate() {
		//logger.debug("VaMarkupObserver.end-update");
		VaPViewImpl imp = VaPViewFactory.getPViewImpl(VaPViewFactory.PV_NAME_MBOM);
		imp.updateSearchPanelBBox();
		super.OnEndUpdate();
	}
	
	@Override
	protected void OnShapeInstanceLocation(ShapeInstance arg0) {
		
		try {
			logger.debug("bounding instance : " + arg0.GetInstance().GetName());
		} catch (MessageProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ActorShutdownException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidActorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ConnectionLostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		super.OnShapeInstanceLocation(arg0);
	}
}
