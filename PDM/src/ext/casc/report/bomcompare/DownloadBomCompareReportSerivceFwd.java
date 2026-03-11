package ext.casc.report.bomcompare;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.fc.fcResource;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class DownloadBomCompareReportSerivceFwd implements RemoteAccess, DownloadBomCompareReportService, Serializable{

	private static final long serialVersionUID = 1L;

    static final boolean SERVER = RemoteMethodServer.ServerFlag;
    private static final String FC_RESOURCE = "wt.fc.fcResource";

    private static Manager getManager() throws WTException {
        Manager manager = ManagerServiceFactory.getDefault().getManager(DownloadBomCompareReportService.class);
        if (manager == null) {
            Object[] param = { "ext.casc.report.bomcompare.DownloadBomCompareReportService" };
            throw new WTException(FC_RESOURCE, fcResource.UNREGISTERED_SERVICE, param);
        }
        return manager;
    }

	@Override
	public File export(String oid, String type) throws WTException {
		File file = null;
		if(SERVER) {
			file = ((DownloadBomCompareReportService)getManager()).export(oid, type);
		} else {
			try {
				Class[] cls = { String.class, String.class};
				Object[] objs = { oid, type };
				file = (File)RemoteMethodServer.getDefault().invoke("export", null, this, cls, objs);
			} catch (RemoteException e) {
				Object[] param = { "export" };
                throw new WTException(e, FC_RESOURCE, fcResource.OPERATION_FAILURE, param);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
                if (targetE instanceof WTException) {
                    throw (WTException) targetE;
                }
                Object[] param = { "export" };
                throw new WTException(targetE, FC_RESOURCE, fcResource.OPERATION_FAILURE, param);
			}
		}
		return file;
	}

}
