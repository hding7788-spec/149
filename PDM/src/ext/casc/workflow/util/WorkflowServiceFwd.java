package ext.casc.workflow.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class WorkflowServiceFwd implements RemoteAccess, WorkflowService,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2309330251879665492L;

	static final boolean SERVER = RemoteMethodServer.ServerFlag;

	private static final String FC_RESOURCE = "wt.fc.fcResource";

	private static Manager getManager() throws WTException {

		Manager manager = ManagerServiceFactory.getDefault().getManager(WorkflowService.class);

		if (manager == null) {
			Object[] param = { "ext.casc.workflow.util.WorkflowService" };
			throw new WTException(FC_RESOURCE,wt.fc.fcResource.UNREGISTERED_SERVICE, param);
		}
		return manager;
	}

	public void setPartContainer(Object pbo, String ibaName, String ibaValue) throws WTException {
		if(SERVER){
			((WorkflowService)getManager()).setPartContainer(pbo, ibaName, ibaValue);
		}else{
			try {
				Class[] argTypes = { Object.class,String.class ,String.class};
				Object[] args = { pbo,ibaName,ibaValue};
				RemoteMethodServer.getDefault().invoke("setPartContainer", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "setPartContainer" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "setPartContainer" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}
}
