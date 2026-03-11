package ext.casc.part;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class PartServiceFwd implements RemoteAccess, PartService,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2222011398922763792L;

	static final boolean SERVER = RemoteMethodServer.ServerFlag;

	private static final String FC_RESOURCE = "wt.fc.fcResource";

	private static Manager getManager() throws WTException {

		Manager manager = ManagerServiceFactory.getDefault().getManager(PartService.class);

		if (manager == null) {
			Object[] param = { "ext.ases.part.PartService" };
			throw new WTException(FC_RESOURCE,wt.fc.fcResource.UNREGISTERED_SERVICE, param);
		}
		return manager;
	}

	@SuppressWarnings("unchecked")
	public List<String> getEndPartList(String context) throws WTException {
		if(SERVER){
			return (List<String>)((PartService)getManager()).getEndPartList(context);
		}else{
			try {
				Class[] argTypes = { String.class };
				Object[] args = { context };
				return (List<String>)RemoteMethodServer.getDefault().invoke("getEndPartList", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "getEndPartList" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "getEndPartList" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}
}
