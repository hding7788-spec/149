package ext.casc.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.project.Role;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;

public class AccessUtil {

	/**
     * 设置对象访问权限
     * @param persistable 
     * @param wtprincipal 
     * @throws wt.util.WTException 
     * @throws wt.util.WTPropertyVetoException 
     * @return 
     */
    public static Persistable setObjectAccess(Persistable persistable,
            WTPrincipal wtprincipal) throws WTException,
            wt.util.WTPropertyVetoException {
        
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction transaction = new Transaction();
        try {
            transaction.start();
            WTPrincipalReference wtprincipalreference = WTPrincipalReference
                    .newWTPrincipalReference(wtprincipal);
            
            Vector vector = new Vector();
            vector.add(AccessPermission.READ);
            vector.add(AccessPermission.DOWNLOAD);
            
            persistable = PersistenceHelper.manager.refresh(persistable);
            if (persistable instanceof AdHocControlled) {
                AdHocControlled adhoccontrolled = (AdHocControlled) persistable;
                try {
                    adhoccontrolled = AccessControlHelper.manager
                            .addPermissions(adhoccontrolled,
                            wtprincipalreference, vector,
                            AdHocAccessKey.WNC_ACCESS_CONTROL);
                    PersistenceServerHelper.manager.update(adhoccontrolled,
                            false);
                    
                } catch (Exception exception) {
                    System.out.println(exception.getLocalizedMessage());
                }
            }
            persistable = PersistenceHelper.manager.refresh(persistable);

            transaction.commit();
            transaction = null;
            Persistable persistable1 = persistable;
            return persistable1;
        } finally {
            if (transaction != null)
                transaction.rollback();
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
    
    public static Persistable setObjectAccess(Persistable persistable,
            WTPrincipalReference wtprincipalreference) throws WTException,
            wt.util.WTPropertyVetoException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction transaction = new Transaction();
        try {
            transaction.start();
                System.out.println("wtprincipalreference is:"
                        + wtprincipalreference);
            
            Vector vector = new Vector();
            vector.add(AccessPermission.READ);
            vector.add(AccessPermission.DOWNLOAD);
            
            persistable = PersistenceHelper.manager.refresh(persistable);
            if (persistable instanceof AdHocControlled) {
                AdHocControlled adhoccontrolled = (AdHocControlled) persistable;
                try {
                    adhoccontrolled = AccessControlHelper.manager
                            .addPermissions(adhoccontrolled,
                            wtprincipalreference, vector,
                            AdHocAccessKey.WNC_ACCESS_CONTROL);
                    PersistenceServerHelper.manager.update(adhoccontrolled,
                            false);
                } catch (Exception exception) {
                    System.out.println(exception.getLocalizedMessage());
                        System.out.println("Failed on object: "
                                + adhoccontrolled);
                }
            }
            transaction.commit();
            transaction = null;
            Persistable persistable1 = persistable;
            return persistable1;
        } finally {
            if (transaction != null)
                transaction.rollback();
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
    
    public static Persistable removeObjectAccess(Persistable persistable,
            WTPrincipal wtprincipal) throws WTException,
            wt.util.WTPropertyVetoException {
        
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction transaction = new Transaction();
        try {
            transaction.start();
            WTPrincipalReference wtprincipalreference = WTPrincipalReference
                    .newWTPrincipalReference(wtprincipal);
            
            Vector vector = new Vector();
            vector.add(AccessPermission.READ);
            vector.add(AccessPermission.DOWNLOAD);
            
            persistable = PersistenceHelper.manager.refresh(persistable);
            if (persistable instanceof AdHocControlled) {
                AdHocControlled adhoccontrolled = (AdHocControlled) persistable;
                try {
                    adhoccontrolled = AccessControlHelper.manager
                            .removePermissions(adhoccontrolled,
                            wtprincipalreference, vector,
                            AdHocAccessKey.WNC_ACCESS_CONTROL);
                    PersistenceServerHelper.manager.update(adhoccontrolled,
                            false);
                } catch (Exception exception) {
                    System.out.println(exception.getLocalizedMessage());
                        System.out.println("Failed on object: "
                                + adhoccontrolled);
                }
            }
            transaction.commit();
            transaction = null;
            Persistable persistable1 = persistable;
            return persistable1;
        } finally {
            if (transaction != null)
                transaction.rollback();
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
    /**
     * 移除指定权限
     * @param persistable
     * @param wtprincipal
     * @return
     * @throws WTException
     * @throws wt.util.WTPropertyVetoException
     */
    public static Persistable removeObjectAccess(Persistable persistable,
            WTPrincipal wtprincipal, Vector accessVec) throws WTException,
            wt.util.WTPropertyVetoException {
        
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction transaction = new Transaction();
        try {
            transaction.start();
            WTPrincipalReference wtprincipalreference = WTPrincipalReference
                    .newWTPrincipalReference(wtprincipal);
            
            //Vector vector = new Vector();
            //vector.add(AccessPermission.READ);
            //vector.add(AccessPermission.DOWNLOAD);
            
            persistable = PersistenceHelper.manager.refresh(persistable);
            if (persistable instanceof AdHocControlled) {
                AdHocControlled adhoccontrolled = (AdHocControlled) persistable;
                try {
                    adhoccontrolled = AccessControlHelper.manager
                            .removePermissions(adhoccontrolled,
                            wtprincipalreference, accessVec,
                            AdHocAccessKey.WNC_ACCESS_CONTROL);
                    PersistenceServerHelper.manager.update(adhoccontrolled,
                            false);
                } catch (Exception exception) {
                    System.out.println(exception.getLocalizedMessage());
                        System.out.println("Failed on object: "
                                + adhoccontrolled);
                }
            }
            transaction.commit();
            transaction = null;
            Persistable persistable1 = persistable;
            return persistable1;
        } finally {
            if (transaction != null)
                transaction.rollback();
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
    public static Persistable removeObjectAccess(Persistable persistable,
            WTPrincipalReference wtprincipalreference) throws WTException,
            wt.util.WTPropertyVetoException {
        
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction transaction = new Transaction();
        try {
            transaction.start();
            Vector vector = new Vector();
            vector.add(AccessPermission.READ);
            vector.add(AccessPermission.DOWNLOAD);
            
            persistable = PersistenceHelper.manager.refresh(persistable);
            if (persistable instanceof AdHocControlled) {
                AdHocControlled adhoccontrolled = (AdHocControlled) persistable;
                try {
                    adhoccontrolled = AccessControlHelper.manager
                            .removePermissions(adhoccontrolled,
                            wtprincipalreference, vector,
                            AdHocAccessKey.WNC_ACCESS_CONTROL);
                    PersistenceServerHelper.manager.update(adhoccontrolled,
                            false);
                } catch (Exception exception) {
                    System.out.println(exception.getLocalizedMessage());
                        System.out.println("Failed on object: "
                                + adhoccontrolled);
                }
            }
            transaction.commit();
            transaction = null;
            Persistable persistable1 = persistable;
            return persistable1;
        } finally {
            if (transaction != null)
                transaction.rollback();
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
    
    public static void setObjectAccess(ArrayList arraylist,String roleName,WfProcess parentProcess)
    throws WTException,wt.util.WTPropertyVetoException
{
    
    for(int i = 0; i < arraylist.size(); i++)
    {
        Persistable persistable = (Persistable)arraylist.get(i);
        Role ControlRole = Role.toRole(roleName);
        for(Enumeration enum1 = parentProcess.getPrincipals(ControlRole); enum1.hasMoreElements();)
        {
            WTPrincipalReference pRef1 = (WTPrincipalReference)enum1.nextElement();
            WTPrincipal p1 = pRef1.getPrincipal();
            if(p1 instanceof WTUser)
            {
                setObjectAccess(persistable, p1);
            } else
            if(p1 instanceof WTGroup)
            {
                WTGroup group = (WTGroup)p1;
                for(Enumeration enum2 = group.members(); enum2.hasMoreElements();)
                {
                    WTPrincipal p = (WTPrincipal)enum2.nextElement();
                    setObjectAccess(persistable, p);
                };
            }
        }
        
    }
        
}

	public static void removeObjectAccess(ArrayList arraylist, String roleName,WfProcess parentProcess) throws WTException,
			wt.util.WTPropertyVetoException {
		for (int i = 0; i < arraylist.size(); i++) {
			Persistable persistable = (Persistable) arraylist.get(i);
			Role ControlRole = Role.toRole(roleName);
			for (Enumeration enum1 = parentProcess.getPrincipals(ControlRole); enum1.hasMoreElements();) {
				WTPrincipalReference pRef1 = (WTPrincipalReference) enum1
						.nextElement();
				WTPrincipal p1 = pRef1.getPrincipal();
				if (p1 instanceof WTUser) {
					removeObjectAccess(persistable, p1);
				} else if (p1 instanceof WTGroup) {
					WTGroup group = (WTGroup) p1;
					for (Enumeration enum2 = group.members(); enum2
							.hasMoreElements();) {
						WTPrincipal p = (WTPrincipal) enum2.nextElement();
						removeObjectAccess(persistable, p);
					}			
				}
			}
		}        
	}
	/**
	 * 删除指定权限
	 * @param arraylist
	 * @param roleName
	 * @param parentProcess
	 * @param accessVec
	 * @throws WTException
	 * @throws wt.util.WTPropertyVetoException
	 */
	public static void removeObjectAccess(ArrayList arraylist, String roleName,
			WfProcess parentProcess, Vector accessVec) throws WTException,
			wt.util.WTPropertyVetoException {
		for (int i = 0; i < arraylist.size(); i++) {
			Persistable persistable = (Persistable) arraylist.get(i);
			Role ControlRole = Role.toRole(roleName);
			for (Enumeration enum1 = parentProcess.getPrincipals(ControlRole); enum1
					.hasMoreElements();) {
				WTPrincipalReference pRef1 = (WTPrincipalReference) enum1
						.nextElement();
				WTPrincipal p1 = pRef1.getPrincipal();
				if (p1 instanceof WTUser) {
					removeObjectAccess(persistable, p1, accessVec);
				} else if (p1 instanceof WTGroup) {
					WTGroup group = (WTGroup) p1;
					for (Enumeration enum2 = group.members(); enum2
							.hasMoreElements();) {
						WTPrincipal p = (WTPrincipal) enum2.nextElement();
						removeObjectAccess(persistable, p, accessVec);
					}
				}
			}
		}
	}
}
