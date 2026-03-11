package ext.casc.capp.report;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class CAPPReportServiceFwd  implements RemoteAccess, CAPPReportService,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2222011398922763792L;

	static final boolean SERVER = RemoteMethodServer.ServerFlag;

	private static final String FC_RESOURCE = "wt.fc.fcResource";

	private static Manager getManager() throws WTException {

		Manager manager = ManagerServiceFactory.getDefault().getManager(CAPPReportService.class);

		if (manager == null) {
			Object[] param = { "ext.casc.capp.CAPPReportService" };
			throw new WTException(FC_RESOURCE,wt.fc.fcResource.UNREGISTERED_SERVICE, param);
		}
		return manager;
	}
	@SuppressWarnings("unchecked")
	public  boolean importBOM(File file)throws WTException {
		if(SERVER){
			return (Boolean)((CAPPReportService)getManager()).importBOM(file);
		}else{
			try {
				Class[] argsType = { File.class };
				Object[] argsValue = { file };
				return (Boolean)RemoteMethodServer.getDefault().invoke("importBOM", null, this, argsType, argsValue);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "importBOM" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "importBOM" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}
	@Override
	public File exportCHGLB(String oid) throws Exception {
		if(SERVER){
			return (File)((CAPPReportService)getManager()).exportCHGLB(oid);
		}else{
			try {
				Class[] argsType = { String.class };
				Object[] argsValue = {oid };
				return (File)RemoteMethodServer.getDefault().invoke("exportCHGLB", null, this, argsType, argsValue);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "exportCHGLB" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "exportCHGLB" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}

}
