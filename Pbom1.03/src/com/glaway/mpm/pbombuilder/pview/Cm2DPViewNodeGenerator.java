package com.glaway.mpm.pbombuilder.pview;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wt.fc.ObjectIdentifier;

import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.pvkapp.Structure;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.InvalidActorException;

public class Cm2DPViewNodeGenerator {
	private static final CmLogger log = CmLogger.getLogger(Cm2DPViewNodeGenerator.class.getName());

	private String pvName;

	private List<ObjectIdentifier> treeNodeOidKeyList = new ArrayList<ObjectIdentifier>();

	private static HashMap<Long, URL> id2url = new HashMap<Long, URL>(32);

	private Structure structure;
	private ShapeScene shapeScene;

	public Cm2DPViewNodeGenerator(String pvName) throws Exception,
			ActorShutdownException, InvalidActorException {
		this.pvName = pvName;
		
	}

	public void generatePVStructure(String filePath,
			String finishAnimFrameTaskId) {
		CmPViewImpl pviewImpl = CmPViewFactory.getPViewImpl(this.pvName);
		log.debug("pviewImpl,name = " + pviewImpl.getName());
		pviewImpl.waitforClientIntialized();

		try {

			pviewImpl.openFile(filePath,finishAnimFrameTaskId);
			log.debug("file opened");
			ShapeView shapeView = pviewImpl.getPanelContext().getShapeView();
			AsyncEventCB zoomAllAsyncEvent = pviewImpl
					.getAsyncEvent(finishAnimFrameTaskId);
			shapeView.ZoomAll(zoomAllAsyncEvent.GetAsyncEventIf());
		} catch (Exception e) {
			log.error(e);
		}
	}
}
