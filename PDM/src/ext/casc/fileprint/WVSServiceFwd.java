package ext.casc.fileprint;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.representation.Representable;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class WVSServiceFwd implements RemoteAccess, WVSService, Serializable {

    static final boolean SERVER = RemoteMethodServer.ServerFlag;
    private static final String FC_RESOURCE = "wt.fc.fcResource";

    private static Manager getManager()
            throws WTException {

        Manager manager = ManagerServiceFactory.getDefault().getManager(WVSService.class);

        if (manager == null) {
            Object[] param = { "ext.casc.fileprint.WVSService" };
            throw new WTException(FC_RESOURCE, wt.fc.fcResource.UNREGISTERED_SERVICE, param);
        }
        return manager;
    }

    public Representable repToAttachment(Representable representable, boolean flag) throws WTException,
            PropertyVetoException, IOException {
        if (SERVER)
            return ((WVSService) getManager()).repToAttachment(representable, flag);
        else {
            try {
                Class[] argTypes = { Representable.class, boolean.class };
                Object[] args = { representable, new Boolean(flag) };
                return (Representable) RemoteMethodServer.getDefault().invoke(
                        "repToAttachment", null, this, argTypes, args);
            } catch (InvocationTargetException e) {
                Throwable targetE = e.getTargetException();
                if (targetE instanceof WTException)
                    throw (WTException) targetE;
                if (targetE instanceof PropertyVetoException)
                    throw (PropertyVetoException) targetE;
                if (targetE instanceof IOException)
                    throw (IOException) targetE;
                Object[] param = { "repToAttachment" };
                throw new WTException(targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
            } catch (RemoteException rme) {
                Object[] param = { "repToAttachment" };
                throw new WTException(rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
            }
        }
    }
}
