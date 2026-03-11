package ext.casc.zipfile;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import ext.casc.preview.Preview;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class ZipFileServiceFwd implements RemoteAccess, ZipFileService,
        Serializable {
    /** */
    private static final long serialVersionUID = -7470089959534652594L;

    static final boolean SERVER = RemoteMethodServer.ServerFlag;

    private static final String FC_RESOURCE = "wt.fc.fcResource";

    private static Manager getManager() throws WTException {

        Manager manager = ManagerServiceFactory.getDefault().getManager(ext.casc.zipfile.ZipFileService.class);

        if (manager == null) {
            Object[] param = { "ext.casc.zipfile.ZipFileService" };
            throw new WTException(FC_RESOURCE, wt.fc.fcResource.UNREGISTERED_SERVICE, param);
        }
        return manager;
    }

    public String zipFile(List<String> oidList, String relist, Integer printsum, String zipFileName)
            throws WTException, PropertyVetoException, IOException {
        Object obj = null;
        String fileName = null;
        if (SERVER) {
            fileName = (String) ((ZipFileService) getManager()).zipFile(oidList, relist, printsum, zipFileName);
        } else {
            try {
                Class[] argTypes = { List.class, String.class, Integer.class, String.class };
                Object[] args = { oidList, relist, printsum, zipFileName };
                obj = RemoteMethodServer.getDefault().invoke("zipFile", null, this, argTypes, args);
                fileName = (String) obj;
            } catch (InvocationTargetException e) {
                Throwable targetE = e.getTargetException();
                if (targetE instanceof WTException) {
                    throw (WTException) targetE;
                }
                Object[] param = { "zipFile" };
                throw new WTException(targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
            } catch (RemoteException rme) {
                Object[] param = { "zipFile" };
                throw new WTException(rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
            }
        }
        return fileName;
    }

    public String zipFile(List<String> oidList, String zipFileName) throws WTException, PropertyVetoException,
            IOException {
        Object obj = null;
        String fileName = null;
        if (SERVER) {
            fileName = (String) ((ZipFileService) getManager()).zipFile(oidList, zipFileName);
        } else {
            try {
                Class[] argTypes = { List.class, String.class, Integer.class, String.class, Integer.class };
                Object[] args = { oidList, zipFileName };
                obj = RemoteMethodServer.getDefault().invoke("zipFile", null, this, argTypes, args);
                fileName = (String) obj;
            } catch (InvocationTargetException e) {
                Throwable targetE = e.getTargetException();
                if (targetE instanceof WTException) {
                    throw (WTException) targetE;
                }
                Object[] param = { "zipFile" };
                throw new WTException(targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
            } catch (RemoteException rme) {
                Object[] param = { "zipFile" };
                throw new WTException(rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
            }
        }
        return fileName;
    }

    @Override
    public String zipFileNew(List<String> oidList, String zipFileName) throws WTException, PropertyVetoException, IOException {
        Object obj = null;
        String fileName = null;
        if (SERVER) {
            fileName = (String) ((ZipFileService) getManager()).zipFileNew(oidList, zipFileName);
        } else {
            try {
                Class[] argTypes = { List.class, String.class, Integer.class, String.class, Integer.class };
                Object[] args = { oidList, zipFileName };
                obj = RemoteMethodServer.getDefault().invoke("zipFileNew", null, this, argTypes, args);
                fileName = (String) obj;
            } catch (InvocationTargetException e) {
                Throwable targetE = e.getTargetException();
                if (targetE instanceof WTException) {
                    throw (WTException) targetE;
                }
                Object[] param = { "zipFileNew" };
                throw new WTException(targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
            } catch (RemoteException rme) {
                Object[] param = { "zipFileNew" };
                throw new WTException(rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
            }
        }
        return fileName;
    }

    @Override
    public String userGuidesZipFile(String zipFileName) throws IOException {
        Object obj = null;
        String fileName = null;
        try {
            if (SERVER) {
                fileName = (String) ((ZipFileService) getManager()).userGuidesZipFile(zipFileName);
            } else {
                    Class[] argTypes = { String.class };
                    Object[] args = { zipFileName };
                    obj = RemoteMethodServer.getDefault().invoke("userGuidesZipFile", null, this, argTypes, args);
                    fileName = (String) obj;
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @Override
    public String downloadPrimaryContent(EPMDocument epmDocument, String number) throws Exception {
        Object obj = null;
        String fileName = null;
        try {
            if (SERVER) {
                fileName = (String) ((ZipFileService) getManager()).downloadPrimaryContent(epmDocument,number);
            } else {
                    Class[] argTypes = { EPMDocument.class,String.class };
                    Object[] args = { epmDocument,number };
                    obj = RemoteMethodServer.getDefault().invoke("downloadPrimaryContent", null, this, argTypes, args);
                    fileName = (String) obj;
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @Override
    public String downloadPrimaryContent(WTDocument doc, String number) throws Exception {
        Object obj = null;
        String fileName = null;
        try {
            if (SERVER) {
                fileName = (String) ((ZipFileService) getManager()).downloadPrimaryContent(doc,number);
            } else {
                    Class[] argTypes = { WTDocument.class,String.class };
                    Object[] args = { doc,number };
                    obj = RemoteMethodServer.getDefault().invoke("downloadPrimaryContent", null, this, argTypes, args);
                    fileName = (String) obj;
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return fileName;
    }
    @Override
    public String downloadPrimaryContent(Preview preview, String number) throws Exception {
        Object obj = null;
        String fileName = null;
        try {
            if (SERVER) {
                fileName = (String) ((ZipFileService) getManager()).downloadPrimaryContent(preview,number);
            } else {
                    Class[] argTypes = { Preview.class,String.class };
                    Object[] args = { preview,number };
                    obj = RemoteMethodServer.getDefault().invoke("downloadPrimaryContent", null, this, argTypes, args);
                    fileName = (String) obj;
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return fileName;
    }
    public String zipPrimaryFile(List<String> oidList, String zipFileName) throws WTException, PropertyVetoException,
    IOException {
		Object obj = null;
		String fileName = null;
		if (SERVER) {
		    fileName = (String) ((ZipFileService) getManager()).zipPrimaryFile(oidList, zipFileName);
		} else {
		    try {
		        Class[] argTypes = { List.class, String.class, Integer.class, String.class, Integer.class };
		        Object[] args = { oidList, zipFileName };
		        obj = RemoteMethodServer.getDefault().invoke("zipPrimaryFile", null, this, argTypes, args);
		        fileName = (String) obj;
		    } catch (InvocationTargetException e) {
		        Throwable targetE = e.getTargetException();
		        if (targetE instanceof WTException) {
		            throw (WTException) targetE;
		        }
		        Object[] param = { "zipPrimaryFile" };
		        throw new WTException(targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
		    } catch (RemoteException rme) {
		        Object[] param = { "zipPrimaryFile" };
		        throw new WTException(rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param);
		    }
		}
return fileName;
}
    //add by libo 2017/2/14 begin
	@Override
	public String zipPackets(String oid) throws Exception {
		Object obj = null;
        String fileName = null;
        try {
            if (SERVER) {
                fileName = (String) ((ZipFileService) getManager()).zipPackets(oid);
            } else {
                    Class[] argTypes = { String.class };
                    Object[] args = { oid};
                    obj = RemoteMethodServer.getDefault().invoke("zipPackets", null, this, argTypes, args);
                    fileName = (String) obj;
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return fileName;
	}
	//add by libo 2017/2/14 end
}
