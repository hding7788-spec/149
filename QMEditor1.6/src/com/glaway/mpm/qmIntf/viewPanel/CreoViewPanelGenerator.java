package com.glaway.mpm.qmIntf.viewPanel;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewGenerator;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;

public class CreoViewPanelGenerator implements VaPViewGenerator {
	private static final VaLogger log = VaLogger.getLogger();

	private String pvName;

//	private Structure structure;
//	private ShapeScene shapeScene;
	
	

	public CreoViewPanelGenerator(String pvName)  {
		this.pvName = pvName;
	}

	public void generatePVStructure(String filePath,
			String finishAnimFrameTaskId) {
		VaPViewImpl pviewImpl = VaPViewFactory.getPViewImpl(this.pvName);
		
		log.debug("pviewImpl,name = " + pviewImpl.getName());
		pviewImpl.waitforClientIntialized();

		try {

			pviewImpl.openFile(filePath,finishAnimFrameTaskId);
			log.debug("file opened");
//			ShapeView shapeView = pviewImpl.getPanelContext().getShapeView();
//			AsyncEventCB zoomAllAsyncEvent = pviewImpl
//					.getAsyncEvent(finishAnimFrameTaskId);
//			shapeView.ZoomAll(zoomAllAsyncEvent.GetAsyncEventIf());
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void applyAnnotation(String annoName,String finishAnimFrameTaskId) {
		VaPViewImpl pviewImpl = VaPViewFactory.getPViewImpl(this.pvName);
		
		log.debug("pviewImpl,name = " + pviewImpl.getName());
		pviewImpl.waitforClientIntialized();

		try {
			
//			pviewImpl.loadAnnotationSet(annoName, finishAnimFrameTaskId);
//			log.debug("load annoName");
//			ShapeView shapeView = pviewImpl.getPanelContext().getShapeView();
//			AsyncEventCB zoomAllAsyncEvent = pviewImpl
//					.getAsyncEvent(finishAnimFrameTaskId);
//			shapeView.ZoomAll(zoomAllAsyncEvent.GetAsyncEventIf());
		} catch (Exception e) {
			log.error(e);
		}
	}

	@Override
	public void generatePVStructure(String finishFrame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public VaPViewImpl getPviewImpl() {
		// TODO Auto-generated method stub
		return null;
	}
}
