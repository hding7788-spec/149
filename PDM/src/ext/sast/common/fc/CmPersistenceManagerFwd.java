/**
 * Created on 2008-8-10
 * @author Dennis Huang
 */
package ext.sast.common.fc;


import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

import java.io.Serializable;

/**
 * Created on 2008-8-10
 *
 * @author Dennis Huang
 */
public final class CmPersistenceManagerFwd implements CmPersistenceManager, RemoteAccess, Serializable {
    private static final long serialVersionUID = -3984252416340253197L;

    private final CmPersistenceManager instance = new CmStandardPersistenceManager();

    public CmQueryResult find(CmQuerySpec qs) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "find";
            Class[] types = { CmQuerySpec.class };
            Object[] values = { qs };

            return (CmQueryResult) RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
        }

        return instance.find(qs);
    }

    public CmPersistable find(Class klass, Object keyId) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "find";
            Class[] types = { Class.class, Object.class };
            Object[] values = { klass, keyId };

            return (CmPersistable) RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
        }

        return instance.find(klass, keyId);
    }

    public boolean isPersistence(CmPersistable p) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "isPersistence";
            Class[] types = { CmPersistable.class };
            Object[] values = { p };

            return ((Boolean) RemoteMethodServer.getDefault().invoke(method, null, this, types, values)).booleanValue();
        }

        return instance.isPersistence(p);
    }

    public void save(CmPersistable p) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "save";
            Class[] types = { CmPersistable.class };
            Object[] values = { p };

            RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
            return;
        }

        instance.save(p);
    }

    @Override
    public void save(CmPersistable p, boolean needAutoCommit) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "save";
            Class[] types = { CmPersistable.class,Boolean.class };
            Object[] values = { p,needAutoCommit };

            RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
            return;
        }

        instance.save(p,needAutoCommit);
    }

    public void insert(CmPersistable p) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "insert";
            Class[] types = { CmPersistable.class };
            Object[] values = { p };

            RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
            return;
        }

        instance.insert(p);
    }

    @Override
    public void insert(CmPersistable p, boolean needAutoCommit) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "insert";
            Class[] types = { CmPersistable.class,Boolean.class };
            Object[] values = { p,needAutoCommit };

            RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
            return;
        }

        instance.insert(p,needAutoCommit);
    }

    public void update(CmPersistable p) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "update";
            Class[] types = { CmPersistable.class };
            Object[] values = { p };

            RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
            return;
        }

        instance.update(p);
    }

    @Override
    public void update(CmPersistable p, boolean needAutoCommit) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "update";
            Class[] types = { CmPersistable.class,Boolean.class };
            Object[] values = { p ,needAutoCommit};

            RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
            return;
        }

        instance.update(p,needAutoCommit);
    }

    public void delete(CmPersistable p) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "delete";
            Class[] types = { CmPersistable.class };
            Object[] values = { p };

            RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
            return;
        }

        instance.delete(p);
    }

    @Override
    public void delete(CmPersistable p, boolean needAutoCommit) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "delete";
            Class[] types = { CmPersistable.class,Boolean.class };
            Object[] values = { p,needAutoCommit };

            RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
            return;
        }

        instance.delete(p,needAutoCommit);
    }
}
