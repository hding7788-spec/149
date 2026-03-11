package ext.casc.workflow.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;

import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class DocServiceFwd implements RemoteAccess, DocService,
Serializable {
	static final boolean SERVER = RemoteMethodServer.ServerFlag;

	private static final String FC_RESOURCE = "wt.fc.fcResource";
	
	private static Manager getManager() throws WTException {

		Manager manager = ManagerServiceFactory.getDefault().getManager(DocService.class);

		if (manager == null) {
			Object[] param = { "ext.casc.workflow.util.DocService" };
			throw new WTException(FC_RESOURCE,wt.fc.fcResource.UNREGISTERED_SERVICE, param);
		}
		return manager;
	}

	public Object createDoc(String number, String name, String desc, HashMap attributes, HashMap<String, Object> softAttr, WTContainerRef containerRef)
			throws WTException {
		Object obj = null;
		if(SERVER){
			obj = ((DocService)getManager()).createDoc(number,name, desc, attributes, softAttr, containerRef);
		}else{
			try {
				Class[] argTypes = { String.class, String.class, String.class, HashMap.class, HashMap.class };
				Object[] args = {};
				obj = RemoteMethodServer.getDefault().invoke("createDoc", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "createDoc" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "createDoc" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
		return obj;
	}

}
