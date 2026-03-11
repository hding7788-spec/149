package ext.sast.center.service;

import java.io.Serializable;

import ext.sast.center.util.RestMessageQueue;
import wt.services.StandardManager;
import wt.util.WTException;

public class StartMQService extends StandardManager implements CustomService,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static StartMQService newStartMQService() throws WTException {
		StartMQService service = new StartMQService();
		service.initialize();
		return service;
	}
	
	protected void performStartupProcess() {
		
		RestMessageQueue.startRestQueue();
	}
	
	protected void performShutdownProcess() {
		
	}
}
