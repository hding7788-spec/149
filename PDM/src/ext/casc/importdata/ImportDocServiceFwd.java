package ext.casc.importdata;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

import com.ptc.netmarkets.util.beans.NmCommandBean;

public class ImportDocServiceFwd implements RemoteAccess, Serializable, ImportDocService {

    private static final long serialVersionUID = 1L;

    static final boolean SERVER = RemoteMethodServer.ServerFlag;

	
	private static final String FC_RESOURCE = "wt.fc.fcResource";

	private static Manager getManager() throws WTException {

		Manager manager = ManagerServiceFactory.getDefault().getManager(ImportDocService.class);

		if (manager == null) {
			Object[] param = { "ext.casc.importdata.ImportDocService" };
			throw new WTException(FC_RESOURCE, wt.fc.fcResource.UNREGISTERED_SERVICE, param);
		}
		return manager;
	}

	@Override
	public String importObjects(NmCommandBean cb, File file, String tempFile,List<String> allFileName) throws WTException, RemoteException {
		
		if (SERVER) {
			return ((ImportDocService) getManager()).importObjects(cb, file, tempFile,allFileName);
		} else {
			try {
				Class[] argTypes = { NmCommandBean.class, File.class, String.class,String.class };
				Object[] args = { cb, file, tempFile,allFileName };
				return (String) RemoteMethodServer.getDefault().invoke("importObjects", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException) {
					throw (WTException) targetE;
				}
				Object[] param = { "importObjects" };
				throw new WTException(targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "importObjects" };
				throw new WTException(rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}

    @Override
    public List<String> compressZIPData(File zipFile, String fileName) throws WTException {
        if (SERVER) {
            return (List<String>)((ImportDocService) getManager()).compressZIPData(zipFile, fileName);
        }else {
            Class[] argTypes = { File.class, String.class };
            Object[] args = { zipFile, fileName };
            try {
                return (List<String>)RemoteMethodServer.getDefault().invoke("compressZIPData", null, this, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
