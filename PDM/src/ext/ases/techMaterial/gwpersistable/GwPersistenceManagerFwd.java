package ext.ases.techMaterial.gwpersistable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

/**
 * 实现管理类远程方法调用的处理类
 * 
 * @author Mchen
 */
public final class GwPersistenceManagerFwd implements GwPersistenceManager, RemoteAccess, Serializable {
	private static final long serialVersionUID = -3984252416340253197L;
	private final GwPersistenceManager instance = new GwStandardPersistenceManager();

	@Override
	public GwQueryResult find(GwQuerySpec qs) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "find";
			Class<?>[] types = { GwQuerySpec.class };
			Object[] values = { qs };

			try {
				return (GwQueryResult) RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		return instance.find(qs);
	}

	@Override
	public GwPersistable find(Class<?> klass, Object keyId) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "find";
			Class<?>[] types = { Class.class, Object.class };
			Object[] values = { klass, keyId };

			return (GwPersistable) RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
		}

		return instance.find(klass, keyId);
	}

	@Override
	public boolean isPersistence(GwPersistable p) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "isPersistence";
			Class<?>[] types = { GwPersistable.class };
			Object[] values = { p };

			return ((Boolean) RemoteMethodServer.getDefault().invoke(method, null, this, types, values)).booleanValue();
		}

		return instance.isPersistence(p);
	}

	@Override
	public void save(GwPersistable p) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "save";
			Class<?>[] types = { GwPersistable.class };
			Object[] values = { p };

			RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
			return;
		}

		instance.save(p);
	}

	@Override
	public void insert(GwPersistable p) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "insert";
			Class<?>[] types = { GwPersistable.class };
			Object[] values = { p };

			RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
			return;
		}

		instance.insert(p);
	}

	@Override
	public void update(GwPersistable p) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "update";
			Class<?>[] types = { GwPersistable.class };
			Object[] values = { p };

			RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
			return;
		}

		instance.update(p);
	}

	@Override
	public void delete(GwPersistable p) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "delete";
			Class<?>[] types = { GwPersistable.class };
			Object[] values = { p };

			RemoteMethodServer.getDefault().invoke(method, null, this, types, values);
			return;
		}

		instance.delete(p);
	}
}
