package com.glaway.mpm.dwg2pdf;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.log4j.LogR;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressServiceEvent;

public class DwgListener extends StandardManager implements CustomService {
	private static final long serialVersionUID = 1L;
	private static final String CLASSNAME = DwgListener.class.getName();
	private KeyedEventListener listener;
	static Logger log = LogR.getLogger(DwgListener.class.getName());

	public String getConceptualClassname() {
		return CLASSNAME;
	}

	public static DwgListener newDwgListener() throws WTException {
		DwgListener dwgListener = new DwgListener();
		dwgListener.initialize();
		return dwgListener;
	}

	class DwgCheckInListener extends ServiceEventListenerAdapter {
		public DwgCheckInListener(String manager_name) {
			super(manager_name);
		}

		public void notifyVetoableEvent(Object event) throws WTException {
			if (!(event instanceof KeyedEvent)) {
				return;
			}
			KeyedEvent keyedEvent = (KeyedEvent) event;
			Object target = keyedEvent.getEventTarget();
			if (keyedEvent.getEventType().equals(WorkInProgressServiceEvent.POST_CHECKIN)) {
				if (target instanceof WTDocument) {
					WTDocument doc = (WTDocument) target;
					String docType = null;
					try {
						docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
						String[] types = docType.split("\\|");
						docType = types[types.length - 1];
					} catch (RemoteException e) {
						docType = "";
						e.printStackTrace();
					}
					System.out.println("-------docType---" + docType);
					if ("casc.sast.149.DWG2PDF".equals(docType)) {
						WTDocument latestDoc = (WTDocument) VersionControlHelper.getLatestIteration(doc, true);
						System.out.println("-------latestDoc---" + latestDoc);
						log.info("keyedEvent.getEventType()=" + keyedEvent.getEventType());
						log.info("----------start ----sendToDwgWorkerQueue---------------");
						DwgHelper.service.sendToDwgWorkerQueue(latestDoc);
						log.info("----------end ----sendToDwgWorkerQueue---------------");
					}
				}
			}
		}

	}

	protected void performStartupProcess() {
		listener = new DwgCheckInListener(this.getConceptualClassname());
		getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey(WorkInProgressServiceEvent.POST_CHECKIN));
	}
}
