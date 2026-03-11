package ext.casc.synch;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.preview.Preview;
import wt.doc.WTDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

public class DataSynchHelper implements RemoteAccess {

    private DataSynchHelper() {
    }

    private static DataSynchService getService() {
        Manager manager = ManagerServiceFactory.getDefault().getManager(
                DataSynchService.class);
        if (manager == null) {
            // System.out.println("Not Found MyDeliveryService.");
            return null;
        } else {
            return (DataSynchService) manager;
        }
    }

    /** 工艺通知单外发数据打包入口
     * @param WTDocument 流程PBO对象
     * @return 打包成功后的全文件路径
     * @throws WTException
     */
    public static String exportProcessNotice(WTDocument doc)
            throws WTException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "exportProcessNotice";
            Class[] types = { WTDocument.class };
            Object[] vals = { doc };
            try {
                return (String) RemoteMethodServer.getDefault().invoke(method,
                        DataSynchHelper.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            DataSynchService service = getService();
            if (service != null) {
                return service.exportProcessNotice(doc);

            } else {
                System.out.println("Can't found instance of DeliveryService.");
            }
        }
        return null;
    }

    public static String exportCommonProcess(WTDocument doc)
            throws WTException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "exportCommonProcess";
            Class[] types = { WTDocument.class };
            Object[] vals = { doc };
            try {
                return (String) RemoteMethodServer.getDefault().invoke(method,
                        DataSynchHelper.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            DataSynchService service = getService();
            if (service != null) {
                return service.exportCommonProcess(doc);
            } else {
                System.out.println("Can't found instance of DeliveryService.");
            }
        }
        return null;
    }
    public static String exportProcessEnvelopeTargets(ProcessEnvelope pe)
            throws WTException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "exportProcessEnvelopeTargets";
            Class[] types = { ProcessEnvelope.class };
            Object[] vals = { pe };
            try {
                return (String) RemoteMethodServer.getDefault().invoke(method,
                        DataSynchHelper.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            DataSynchService service = getService();
            if (service != null) {
                return service.exportProcessEnvelopeTargets(pe);

            } else {
                System.out.println("Can't found instance of DeliveryService.");
            }
        }
        return null;
    }


    public static String exportChangeRequestTargets(ChangeRequest cr)
            throws WTException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "exportChangeRequestTargets";
            Class[] types = { ChangeRequest.class };
            Object[] vals = { cr };
            try {
                return (String) RemoteMethodServer.getDefault().invoke(method,
                        DataSynchHelper.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        } else {
            DataSynchService service = getService();
            if (service != null) {
                return service.exportChangeRequestTargets(cr);

            } else {
                System.out.println("Can't found instance of DeliveryService.");
            }
        }
        return null;
    }

    public static String exportChangePackagedTargets(ChangePackaged packaged)
            throws WTException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "exportChangePackagedTargets";
            Class[] types = { ChangePackaged.class };
            Object[] vals = { packaged };
            try {
                return (String) RemoteMethodServer.getDefault().invoke(method,
                        DataSynchHelper.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        } else {
            DataSynchService service = getService();
            if (service != null) {
                return service.exportChangePackagedTargets(packaged);

            } else {
                System.out.println("Can't found instance of DeliveryService.");
            }
        }
        return null;
    }

    public static String exportPreviewTargets(Preview preview)
            throws WTException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "exportPreviewTargets";
            Class[] types = { Preview.class };
            Object[] vals = { preview };
            try {
                return (String) RemoteMethodServer.getDefault().invoke(method,
                        DataSynchHelper.class.getName(), null, types, vals);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        } else {
            DataSynchService service = getService();
            if (service != null) {
                return service.exportPreviewTargets(preview);

            } else {
                System.out.println("Can't found instance of DeliveryService.");
            }
        }
        return null;
    }

}
