//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wt.fc;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import ext.casc.ixb.ExpImpLogger;
import ext.test.DebugLogger;
import org.apache.log4j.Logger;
import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.NotAuthorizedException;
import wt.dataservice.DataServiceFactory;
import wt.dataservice.Datastore;
import wt.dataservice.Oracle;
import wt.dataservice.PostgreSQL;
import wt.dataservice.SQLServer;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.events.summary.CreateSummaryEvent;
import wt.events.summary.DeleteSummaryEvent;
import wt.events.summary.ModifySummaryEvent;
import wt.fc.association.AssociationRuntimeUtilities;
import wt.fc.batch.BatchSpec;
import wt.fc.batch.BatchSpecificationUtilities;
import wt.fc.batch.DeleteBatchSpec;
import wt.fc.collections.CollectionsHelper;
import wt.fc.collections.RefreshSpec;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTKeyedHashMap;
import wt.fc.collections.WTKeyedMap;
import wt.fc.collections.WTSet;
import wt.fc.collections.WTValuedHashMap;
import wt.fc.delete.DeleteHelper;
import wt.fc.delete.MarkForDeleteAccessController;
import wt.fc.jmx.AbstractTraceTimingLogger;
import wt.fc.jmx.DatastoreMonitorLogger;
import wt.fc.jmx.Log4jTraceTimingLogger;
import wt.fc.jmx.TopSQLTraceTimingLogger;
import wt.fc.manifest.AbstractDirectiveResult;
import wt.fc.manifest.DirectiveResult;
import wt.fc.manifest.Manifest;
import wt.fc.manifest.ManifestServerHelper;
import wt.fc.profiling.PersistProfKey;
import wt.fc.profiling.SQLProfilingKey;
import wt.federation.Federated;
import wt.federation.FederationHelper;
import wt.federation.FederationServerHelper;
import wt.identity.IdentityFactory;
import wt.inf.container.RestrictToOrgAccessController;
import wt.introspection.ClassInfo;
import wt.introspection.ColumnDescriptor;
import wt.introspection.LinkInfo;
import wt.introspection.RoleDescriptor;
import wt.introspection.WTIntrospectionException;
import wt.introspection.WTIntrospector;
import wt.log4j.LogR;
import wt.method.MethodServerException;
import wt.notify.Notifiable;
import wt.org.UserNotFoundException;
import wt.pds.*;
import wt.pom.DBProperties;
import wt.pom.ObjectIsStaleException;
import wt.pom.ObjectLockedException;
import wt.pom.PagingSessionCache;
import wt.pom.PersistenceException;
import wt.pom.PersistentObjectManager;
import wt.pom.Transaction;
import wt.pom.TransactionCommitListener;
import wt.pom.UniquenessException;
import wt.query.*;
import wt.query.template.ReportTemplateServerHelper;
import wt.services.ManagerException;
import wt.services.ManagerService;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.services.StandardManagerServiceEvent;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.TraceTimingHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;

public class StandardPersistenceManager extends StandardManager implements PersistenceManager, PersistenceManagerSvr, Serializable {
    private static final String RESOURCE = "wt.fc.fcResource";
    private static final String CLASSNAME = StandardPersistenceManager.class.getName();
    private static final Logger LOGGER = LogR.getLogger("wt.fc.general");
    private static final Logger REF_INTEGRITY_LOGGER = LogR.getLogger("wt.fc.refIntegrity");
    private static PagingSessionCache pagingSessionCache = null;
    private KeyedEventListener listener;
    private static final RefreshSpec PREPARE_FOR_MODIFICATION_DEFAULT;
    private static final String EMPTY_COLLECTION_ASSERT_MESSAGE = "Empty WTCollection";
    private static final DatastoreMonitorLogger JMX_DATASTORE_MONITOR_LOGGER = new DatastoreMonitorLogger();
    private static final String DELETE_TRIGGER_CLASSNAMES_PROPERTY_NAME = "wt.pom.delete.triggerClassNames";
    private static final HashSet<String> DELETE_TRIGGER_SET = new HashSet();
    private static final String DELETE_BYPASS_REFERENTIAL_INTEGRITY_CLASSNAMES_PROPERTY_NAME = "wt.pom.delete.bypassReferentialIntegrityClassNames";
    private static final HashSet<String> DELETE_BYPASS_REFERENTIAL_INTEGRITY_SET = new HashSet();
    private static final Object REFERENTIAL_INTEGRITY_VALIDATOR_KEY;
    private static final Object DELETED_PERSISTABLE_SET_KEY;
    private static final int CHECK_STORE = 0;
    private static final int CHECK_MODIFY = 1;
    private static final int CHECK_DELETE = 2;

    public StandardPersistenceManager() {
    }

    /** @deprecated */
    public String getConceptualClassname() {
        return CLASSNAME;
    }

    protected void checkStore(Persistable var1) throws WTException {
        AccessControlHelper.manager.checkAccess(var1, AccessPermission.CREATE);
        this._checkStore(var1);
    }

    protected void checkModify(Persistable var1) throws WTException {
        AccessControlHelper.manager.checkAccess(var1, AccessPermission.MODIFY);
        this.checkPersistent(var1);
    }

    public void registerEvents(ManagerService var1) {
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("PRE_STORE"), PersistenceManagerEvent.class.getName(), "PRE_STORE");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("POST_STORE"), PersistenceManagerEvent.class.getName(), "POST_STORE");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("PRE_MODIFY"), PersistenceManagerEvent.class.getName(), "PRE_MODIFY");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("POST_MODIFY"), PersistenceManagerEvent.class.getName(), "POST_MODIFY");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("PRE_DELETE"), PersistenceManagerEvent.class.getName(), "PRE_DELETE");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("POST_DELETE"), PersistenceManagerEvent.class.getName(), "POST_DELETE");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("CLEANUP_LINK"), PersistenceManagerEvent.class.getName(), "CLEANUP_LINK");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("COPY_LINK"), PersistenceManagerEvent.class.getName(), "COPY_LINK");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("UPDATE"), PersistenceManagerEvent.class.getName(), "UPDATE");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("PRE_REMOVE"), PersistenceManagerEvent.class.getName(), "PRE_REMOVE");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("REMOVE"), PersistenceManagerEvent.class.getName(), "REMOVE");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("PREPARE_FOR_MODIFICATION"), PersistenceManagerEvent.class.getName(), "PREPARE_FOR_MODIFICATION");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("PREPARE_FOR_VIEW"), PersistenceManagerEvent.class.getName(), "PREPARE_FOR_VIEW");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("INFLATE_RESULT"), PersistenceManagerEvent.class.getName(), "INFLATE_RESULT");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("INSERT"), PersistenceManagerEvent.class.getName(), "INSERT");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("PRE_MULTI_DELETE"), PersistenceManagerEvent.class.getName(), "PRE_MULTI_DELETE");
        var1.addEventBranch(PersistenceManagerEvent.generateEventKey("POST_MULTI_DELETE"), PersistenceManagerEvent.class.getName(), "POST_MULTI_DELETE");
    }

    protected void performStartupProcess() throws ManagerException {
        try {
            TraceTimingHelper.addInstance(JMX_DATASTORE_MONITOR_LOGGER);
            HashMap var1 = AbstractTraceTimingLogger.getDBFilterProperties();
            TopSQLTraceTimingLogger var2 = new TopSQLTraceTimingLogger();
            var2.setPropertyMap(var1);
            TraceTimingHelper.addInstance(var2);
            Log4jTraceTimingLogger.setDefaultLevelPriorities(var1);
            Log4jTraceTimingLogger var15 = new Log4jTraceTimingLogger();
            var15.setPropertyMap(var1);
            TraceTimingHelper.addInstance(var15);
        } catch (WTPropertyVetoException var9) {
            throw new ManagerException(this, var9);
        } catch (WTException var10) {
            throw new ManagerException(this, var10);
        }

        SessionContext var14 = SessionContext.newContext();

        label61: {
            try {
                try {
                    SessionHelper.manager.setAdministrator();
                    break label61;
                } catch (UserNotFoundException var11) {
                    System.err.println("Paging Queue: failed to set Administrator (ok if installation) ");
                }
            } catch (Exception var12) {
                var12.printStackTrace(System.err);
                break label61;
            } finally {
                SessionContext.setContext(var14);
            }

            return;
        }

        super.performStartupProcess();
        AllManagersStartedEventListener var16 = new AllManagersStartedEventListener(CLASSNAME);
        this.getManagerService().addEventListener(var16, StandardManagerServiceEvent.generateEventKey("ALL_SERVICES_STARTED"));
        this.listener = new PagingSessionEventListener(this.getConceptualClassname());
        this.getManagerService().addEventListener(this.listener, PersistenceManagerEvent.generateEventKey("POST_DELETE"));
        ReportTemplateServerHelper.registerListeners(this.getManagerService());
    }

    public static StandardPersistenceManager newStandardPersistenceManager() throws WTException {
        StandardPersistenceManager var0 = new StandardPersistenceManager();
        var0.initialize();
        return var0;
    }

    public Persistable delete(Persistable var1) throws WTException {
        WTHashSet var2 = new WTHashSet();
        var2.add(var1);
        this.delete((WTSet)var2);
        return var1;
    }

    public QueryResult find(QuerySpec var1) throws WTException {
        return this.find((StatementSpec)var1);
    }

    public QueryResult find(Class var1, Persistable var2, String var3, Persistable var4) throws WTException {
        return this.find((StatementSpec)this.makeQuerySpec(var1, var2, var3, var4));
    }

    public QueryResult find(Class var1, ObjectIdentifier var2, String var3, ObjectIdentifier var4) throws WTException, InvalidRoleException {
        return this.find((StatementSpec)this.makeQuerySpec(var1, var2, var3, var4));
    }

    public InputStream getLob(LobLocator var1) throws WTException {
        InputStream var2 = PersistentObjectManager.getPom().getLob(var1);
        return var2;
    }

    public Persistable modify(Persistable var1) throws WTException {
        Transaction var2 = new Transaction();

        try {
            var2.start();
            this.dispatchVetoableEvent("PRE_MODIFY", var1);
            this.checkModify(var1);
            var1.checkAttributes();
            this.update(var1);
            this.dispatchVetoableEvent("POST_MODIFY", var1);
            if (var1 instanceof Notifiable && !PersistenceServerHelper.isPersistedInTransaction(PersistenceHelper.getObjectIdentifier(var1))) {
                ModifySummaryEvent var3 = ModifySummaryEvent.getSummaryEvent();
                var3.contribute((Notifiable)var1);
            }

            var2.commit();
            var2 = null;
        } finally {
            if (var2 != null) {
                var2.rollback();
            }

        }

        return var1;
    }

    public QueryResult navigate(Persistable var1, String var2, Class var3) throws WTException {
        return this.navigate(var1, var2, var3, true);
    }

    public QueryResult navigate(Persistable var1, String var2, Class var3, boolean var4) throws WTException {
        QueryResult var5 = this.expand(var1, var2, var3, var4, new StandardACProcessor(this.buildAccessController()), false);
        return var5;
    }

    public QueryResult navigate(Persistable var1, String var2, QuerySpec var3) throws WTException {
        QueryResult var4 = this.expand(var1, var2, var3, true, new StandardACProcessor(this.buildAccessController()));
        return var4;
    }

    public QueryResult navigate(Persistable var1, String var2, QuerySpec var3, boolean var4) throws WTException {
        QueryResult var5 = this.expand(var1, var2, var3, var4, new StandardACProcessor(this.buildAccessController()));
        return var5;
    }

    public Persistable refresh(Persistable var1, boolean var2) throws WTException, ObjectNoLongerExistsException {
        return this.refresh(var1, var2, false);
    }

    public Persistable refresh(Persistable var1) throws WTException, ObjectNoLongerExistsException {
        return this.refresh(var1, false);
    }

    public Persistable refresh(ObjectIdentifier var1) throws WTException, ObjectNoLongerExistsException {
        return this.refresh(var1, false);
    }

    public Persistable save(Persistable var1) throws WTException {
        return PersistenceHelper.isPersistent(var1) ? this.modify(var1) : this.store(var1);
    }

    public Persistable store(Persistable var1) throws WTException {
        return this.store(var1, (Timestamp)null, (Timestamp)null);
    }

    public Persistable modify(Persistable var1, String var2, ObjectMappable var3) throws WTException {
        Transaction var4 = new Transaction();

        try {
            var4.start();
            this.dispatchVetoableEvent("PRE_MODIFY", var1);
            this.checkModify(var1);
            var1.checkAttributes();
            this.update(var1, var2, var3);
            this.dispatchVetoableEvent("POST_MODIFY", var1);
            if (var1 instanceof Notifiable && !PersistenceServerHelper.isPersistedInTransaction(PersistenceHelper.getObjectIdentifier(var1))) {
                ModifySummaryEvent var5 = ModifySummaryEvent.getSummaryEvent();
                var5.contribute((Notifiable)var1);
            }

            var4.commit();
            var4 = null;
        } finally {
            if (var4 != null) {
                var4.rollback();
            }

        }

        return var1;
    }

    public Persistable prepareForModification(Persistable var1) throws ModificationNotAllowedException, ObjectNoLongerExistsException, NotAuthorizedException, WTException {
        try {
            var1 = PersistenceServerHelper.manager.restore(var1);
            AccessControlHelper.manager.checkAccess(var1, AccessPermission.MODIFY);

            try {
                this.dispatchVetoableEvent("PREPARE_FOR_MODIFICATION", var1);
                return var1;
            } catch (WTException var5) {
                throw new ModificationNotAllowedException(var5, var1);
            }
        } catch (NotAuthorizedException var6) {
            NotAuthorizedException var2 = var6;

            try {
                AccessControlHelper.manager.checkAccess(var1, AccessPermission.READ);
                throw new ModificationNotAllowedException(var2, var1);
            } catch (NotAuthorizedException var4) {
                throw var6;
            }
        }
    }

    public String getNextSequence(String var1) throws WTException {
        String var2 = PersistentObjectManager.getPom().getNextSequence(var1);
        return var2;
    }

    public Persistable prepareForView(Persistable var1) throws ObjectNoLongerExistsException, NotAuthorizedException, WTException {
        var1 = PersistenceServerHelper.manager.restore(var1);
        AccessControlHelper.manager.checkAccess(var1, AccessPermission.READ);
        this.dispatchVetoableEvent("PREPARE_FOR_VIEW", var1);
        return var1;
    }

    public void inflate(QueryResult var1, Vector var2) throws WTException {
        this.dispatchVetoableEvent(new PersistenceManagerEvent("INFLATE_RESULT", var2, var1), true);
    }

    public Persistable prepareForModification(WTReference var1) throws ModificationNotAllowedException, ObjectNoLongerExistsException, NotAuthorizedException, WTException {
        return this.prepareForModification(var1.getObject());
    }

    public Persistable prepareForView(WTReference var1) throws ObjectNoLongerExistsException, NotAuthorizedException, WTException {
        return this.prepareForView(var1.getObject());
    }

    public QueryResult find(StatementSpec var1) throws WTException {
        return this._find(var1, (ResultProcessor)null);
    }

    public Persistable refresh(Persistable var1, boolean var2, boolean var3) throws WTException, ObjectNoLongerExistsException {
        return this.refresh(var1, var2, var3, false);
    }

    public ResultProcessor find(StatementSpec var1, ResultProcessor var2) throws WTException {
        this._find(var1, var2);
        return var2;
    }

    public Persistable refresh(Persistable var1, boolean var2, boolean var3, boolean var4) throws WTException, ObjectNoLongerExistsException {
        if (PersistenceHelper.isPersistent(var1)) {
            var1 = this.restore(var1, var2, var3, false, var4);
            AccessControlHelper.manager.checkAccess(var1, AccessPermission.READ);
            return var1;
        } else {
            Object[] var5 = new Object[]{IdentityFactory.getDisplayIdentity(var1)};
            throw new WTException((Throwable)null, "wt.fc.fcResource", "7", var5);
        }
    }

    public Persistable lockAndRefresh(Persistable var1) throws WTException {
        return this.refresh(var1, false, false, true);
    }

    public WTCollection store(WTCollection var1, WTCollectionExceptionHandler var2) throws WTException {
        assert !var1.isEmpty() : "Empty WTCollection";

        Transaction var3 = new Transaction();

        try {
            var3.start();
            this.dispatchVetoableEvent("PRE_STORE", var1);
            this.checkOperation(var1, 0, true);
            this.insert(var1, var2);
            this.dispatchVetoableEvent("POST_STORE", var1);
            Object var4 = var1.subCollection(Notifiable.class);
            if (((WTCollection)var4).size() > 0) {
                var4 = new WTArrayList((Collection)var4);
                ((WTCollection)var4).removeAll(Iterated.class, true);
            }

            if (((WTCollection)var4).size() > 0) {
                CreateSummaryEvent var5 = CreateSummaryEvent.getSummaryEvent();
                var5.contribute((WTCollection)var4);
            }

            var3.commit();
            var3 = null;
        } finally {
            if (var3 != null) {
                var3.rollback();
            }

        }

        return var1;
    }

    public WTCollection modify(WTCollection var1, WTCollectionExceptionHandler var2) throws WTException {
        assert !var1.isEmpty() : "Empty WTCollection";

        Transaction var3 = new Transaction();

        try {
            var3.start();
            this.dispatchVetoableEvent("PRE_MODIFY", var1);
            this.checkOperation(var1, 1, true);
            this.update(var1, true, var2);
            this.dispatchVetoableEvent("POST_MODIFY", var1);
            WTCollection var4 = var1.subCollection(Notifiable.class);
            if (var4.size() > 0) {
                ModifySummaryEvent var5 = null;
                Iterator var6 = var4.persistableIterator();

                while(var6.hasNext()) {
                    Persistable var7 = (Persistable)var6.next();
                    if (!PersistenceServerHelper.isPersistedInTransaction(PersistenceHelper.getObjectIdentifier(var7))) {
                        if (var5 == null) {
                            var5 = ModifySummaryEvent.getSummaryEvent();
                        }

                        var5.contribute((Notifiable)var7);
                    }
                }
            }

            var3.commit();
            var3 = null;
        } finally {
            if (var3 != null) {
                var3.rollback();
            }

        }

        return var1;
    }

    public WTSet delete(WTSet var1) throws WTException {
        String var2 = "delete(WTSet) ";
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Enter " + var2 + "[" + var1 + "]");
        }

        assert !((WTSet)var1).isEmpty() : "Empty WTCollection";

        if (((WTSet)var1).getKeyMask() != 1) {
            var1 = new WTHashSet((Collection)var1, 1);
        }

        Transaction var3 = new Transaction();

        try {
            var3.start();
            this.dispatchVetoableEvent("PRE_DELETE", (WTCollection)var1);
            this.checkOperation((WTCollection)var1, 2, false);
            Object var4 = ((WTSet)var1).subCollection(Notifiable.class);
            if (((WTCollection)var4).size() > 0) {
                var4 = new WTArrayList((Collection)var4);
                ((WTCollection)var4).removeAll(Iterated.class, true);
            }

            if (((WTCollection)var4).size() > 0) {
                DeleteSummaryEvent var5 = null;
                Iterator var6 = ((WTCollection)var4).persistableIterator();

                while(var6.hasNext()) {
                    Persistable var7 = (Persistable)var6.next();
                    if (!DeleteHelper.isMarkedForDelete(var7) && !PersistenceServerHelper.isPersistedInTransaction(PersistenceHelper.getObjectIdentifier(var7))) {
                        if (var5 == null) {
                            var5 = DeleteSummaryEvent.getSummaryEvent();
                        }

                        var5.contribute((Notifiable)var7);
                    }
                }
            }

            this.remove((WTSet)var1);
            this.dispatchVetoableEvent("POST_DELETE", (WTCollection)var1);
            var3.commit();
            var3 = null;
        } finally {
            if (var3 != null) {
                var3.rollback();
            }

        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Exit  " + var2 + var1);
        }

        return (WTSet)var1;
    }

    public WTCollection prepareForModification(WTCollection var1) throws ModificationNotAllowedException, NotAuthorizedException, WTException {
        assert !var1.isEmpty() : "Empty WTCollection";

        CollectionsHelper.manager.refresh(var1, PREPARE_FOR_MODIFICATION_DEFAULT);

        try {
            AccessControlHelper.manager.checkAccess(var1, AccessPermission.MODIFY);
        } catch (NotAuthorizedException var6) {
            NotAuthorizedException var2 = var6;

            try {
                AccessControlHelper.manager.checkAccess(var1, AccessPermission.READ);
                throw new ModificationNotAllowedException(var2, var1);
            } catch (NotAuthorizedException var4) {
                throw var6;
            }
        }

        try {
            this.dispatchVetoableEvent("PREPARE_FOR_MODIFICATION", var1);
            return var1;
        } catch (WTException var5) {
            throw new ModificationNotAllowedException(var5, var1);
        }
    }

    public WTCollection save(WTCollection var1) throws WTException {
        assert !var1.isEmpty() : "Empty WTCollection";

        boolean var2 = false;
        WTSet var3 = CollectionsHelper.getNonPersistedSubSet(var1, true);
        WTSet var4 = CollectionsHelper.getPersistedSubSet(var1, true);
        Transaction var5 = new Transaction();
        boolean var20 = false;

        try {
            var20 = true;
            var5.start();
            this.store((WTCollection)var3);
            var2 = true;
            this.modify((WTCollection)var4);
            var5.commit();
            var5 = null;
            var20 = false;
        } finally {
            if (var20) {
                if (var5 != null) {
                    try {
                        if (var2) {
                            Iterator var9 = var3.persistableIterator();

                            while(var9.hasNext()) {
                                ((Persistable)var9.next()).setPersistInfo((PersistInfo)null);
                            }
                        }
                    } finally {
                        var5.rollback();
                    }
                }

            }
        }

        if (var5 != null) {
            try {
                if (var2) {
                    Iterator var6 = var3.persistableIterator();

                    while(var6.hasNext()) {
                        ((Persistable)var6.next()).setPersistInfo((PersistInfo)null);
                    }
                }
            } finally {
                var5.rollback();
            }
        }

        return var1;
    }

    public String getNextSequence(Class var1) throws WTException {
        try {
            ClassInfo var2 = WTIntrospector.getClassInfo(var1);
            String var3 = (String)var2.getValue("ObjectName");
            Long var4 = (Long)var2.getValue("Seed");
            String var5 = this.getNextSequence(var3);
            if (Long.parseLong(var5) < var4) {
                throw new PersistenceException("wt.fc.fcResource", "0", new Object[]{"getNextSequence(" + var3 + ")"});
            } else {
                return var5;
            }
        } catch (WTIntrospectionException var6) {
            throw new WTException(var6);
        }
    }

    public String getNextSequence(Class var1, String var2) throws WTException {
        String var3 = null;

        try {
            ClassInfo var4 = WTIntrospector.getClassInfo(var1);
            ColumnDescriptor var5 = DatabaseInfoUtilities.getValidColumnDescriptor(var4, var2);
            if (var5.getSQLType() == -766) {
                String var6 = var5.getBaseColumnName();
                var3 = PersistentObjectManager.getPom().getNextSequence(var6, true);
            }

            return var3;
        } catch (WTIntrospectionException var7) {
            throw new WTException(var7);
        }
    }

    public Long getSequenceIncrement(Class var1) throws WTException {
        try {
            ClassInfo var2 = WTIntrospector.getClassInfo(var1);
            Long var3 = (Long)var2.getValue("Increment");
            return var3;
        } catch (WTIntrospectionException var4) {
            throw new WTException(var4);
        }
    }

    public Long getSequenceIncrement(Class var1, String var2) throws WTException {
        try {
            ClassInfo var3 = WTIntrospector.getClassInfo(var1);
            return DatabaseInfoUtilities.getValidColumnDescriptor(var3, var2).getSQLType() == -766 ? new Long(1L) : null;
        } catch (WTIntrospectionException var4) {
            throw new WTException(var4);
        }
    }

    public String getCurrentSequence(Class var1) throws WTException {
        try {
            ClassInfo var2 = WTIntrospector.getClassInfo(var1);
            String var3 = (String)var2.getValue("ObjectName");
            return PersistentObjectManager.getPom().getCurrentSequence(var3);
        } catch (WTIntrospectionException var4) {
            throw new WTException(var4);
        }
    }

    public String getCurrentSequence(Class var1, String var2) throws WTException {
        String var3 = null;

        try {
            ClassInfo var4 = WTIntrospector.getClassInfo(var1);
            ColumnDescriptor var5 = DatabaseInfoUtilities.getValidColumnDescriptor(var4, var2);
            if (var5.getSQLType() == -766) {
                String var6 = var5.getBaseColumnName();
                var3 = PersistentObjectManager.getPom().getCurrentSequence(var6, true);
            }

            return var3;
        } catch (WTIntrospectionException var7) {
            throw new WTException(var7);
        }
    }

    public Persistable refresh(ObjectIdentifier var1, boolean var2) throws WTException, ObjectNoLongerExistsException {
        Persistable var3 = this.restore(var1, true, var2);
        AccessControlHelper.manager.checkAccess(var3, AccessPermission.READ);
        return var3;
    }

    public WTCollection store(WTCollection var1) throws WTException {
        return this.store(var1, (WTCollectionExceptionHandler)null);
    }

    public WTCollection modify(WTCollection var1) throws WTException {
        return this.modify(var1, (WTCollectionExceptionHandler)null);
    }

    public QueryResult expand(Persistable var1, String var2, Class var3, boolean var4) throws WTException {
        return this.expand(var1, var2, var3, var4, new StandardACProcessor(new AccessControllerAdapter()), false);
    }

    public QueryResult expand(Persistable var1, String var2, QuerySpec var3, boolean var4) throws WTException {
        return this.expand(var1, var2, var3, var4, new StandardACProcessor(new AccessControllerAdapter()));
    }

    public void insert(Persistable var1) throws WTException {
        this.insert(var1, (Timestamp)null, (Timestamp)null);
    }

    public QueryResult query(QuerySpec var1) throws WTException {
        return this.query(var1, (AccessControllerProcessor)(new StandardACProcessor(new AccessControllerAdapter())));
    }

    public QueryResult query(Class var1, Persistable var2, String var3, Persistable var4) throws WTException {
        return this.query(this.makeQuerySpec(var1, var2, var3, var4));
    }

    public QueryResult query(Class var1, ObjectIdentifier var2, String var3, ObjectIdentifier var4) throws WTException, InvalidRoleException {
        return this.query(this.makeQuerySpec(var1, var2, var3, var4));
    }

    public void remove(Persistable var1) throws WTException {
        WTHashSet var2 = new WTHashSet();
        var2.add(var1);
        this.remove((WTSet)var2);
    }

    public Persistable restore(Persistable var1) throws WTException, ObjectNoLongerExistsException {
        return this.restore(var1, false, false);
    }

    public Persistable restore(ObjectIdentifier var1) throws WTException, ObjectNoLongerExistsException {
        return this.restore(var1, true);
    }

    public Persistable restore(ObjectIdentifier var1, boolean var2) throws WTException, ObjectNoLongerExistsException {
        return this.restore(var1, var2, false);
    }

    public void update(Persistable var1) throws WTException {
        this.update(var1, true);
    }

    public void update(Persistable var1, boolean var2) throws WTException {
        Transaction var3 = new Transaction();

        try {
            var3.start();

            try {
                PersistentObjectManager.getPom().update(var1, var2);
            } catch (UniquenessException var10) {
                Object[] var5 = new Object[]{IdentityFactory.getDisplayIdentifier(var1)};
                UniquenessException var6 = new UniquenessException((Exception)null, "wt.fc.fcResource", "42", var5);
                throw var6;
            }

            this.dispatchVetoableEvent("UPDATE", var1);
            var3.commit();
            var3 = null;
        } finally {
            if (var3 != null) {
                var3.rollback();
            }

        }

    }

    public void update(Persistable var1, String var2, ObjectMappable var3) throws WTException {
        Transaction var4 = new Transaction();

        try {
            var4.start();

            try {
                PersistentObjectManager.getPom().update(var1, var2, var3);
            } catch (UniquenessException var11) {
                Object[] var6 = new Object[]{IdentityFactory.getDisplayIdentity(var1)};
                UniquenessException var7 = new UniquenessException((Exception)null, "wt.fc.fcResource", "42", var6);
                throw var7;
            }

            var4.commit();
            var4 = null;
        } finally {
            if (var4 != null) {
                var4.rollback();
            }

        }

    }

    public void updateLob(Persistable var1, LobLocator var2, InputStream var3, long var4, boolean var6) throws WTException {
        Transaction var7 = new Transaction();

        try {
            var7.start();
            PersistentObjectManager.getPom().updateLob(var1, var2, var3, var4, var6);
            var7.commit();
            var7 = null;
        } finally {
            if (var7 != null) {
                var7.rollback();
            }

        }

    }

    public long updateLob(Persistable var1, LobLocator var2, InputStream var3, boolean var4) throws WTException {
        long var5 = 0L;
        Transaction var7 = new Transaction();

        try {
            var7.start();
            var5 = PersistentObjectManager.getPom().updateLob(var1, var2, var3, var4);
            var7.commit();
            var7 = null;
        } finally {
            if (var7 != null) {
                var7.rollback();
            }

        }

        return var5;
    }

    public void lock(Persistable var1) throws WTException {
        this.lock(var1, false);
    }

    public void lock(Persistable var1, boolean var2) throws WTException {
        PersistentObjectManager.getPom().lock(var1, var2);
    }

    public void lock(Persistable var1, int var2, int var3) throws WTException {
        int var4 = 0;

        while(var4 < var2) {
            try {
                this.lock(var1, false);
                break;
            } catch (ObjectLockedException var8) {
                ObjectLockedException var5 = var8;

                try {
                    if (var4 == var2 - 1) {
                        throw var5;
                    }

                    Thread.sleep((long)(var3 * 1000));
                } catch (InterruptedException var7) {
                    throw var8;
                }

                ++var4;
            }
        }

    }

    public BinaryLink copyLink(BinaryLink var1, Persistable var2, String var3, Persistable var4) throws WTException, InvalidRoleException {
        BinaryLink var5 = PersistenceServerHelper.newCopyLink(var1, var2, var3, var4);
        this.insert((Persistable)var5);
        this.dispatchVetoableEvent(new PersistenceManagerEvent("COPY_LINK", var1, var5), true);
        return var5;
    }

    public QueryResult query(StatementSpec var1) throws WTException {
        return this.query(var1, (AccessControllerProcessor)(new StandardACProcessor(new AccessControllerAdapter())));
    }

    public Persistable restore(Persistable var1, boolean var2, boolean var3) throws WTException, ObjectNoLongerExistsException {
        return this.restore(var1, var2, var3, false, false);
    }

    public void insert(Persistable var1, Timestamp var2, Timestamp var3) throws WTException {
        if (var1 instanceof ObjectToObjectLink) {
            if (((ObjectToObjectLink)var1).isRoleANonpersistentProxy()) {
                this.insert(((ObjectToObjectLink)var1).getRoleAObject(), var2, var3);
            }

            if (((ObjectToObjectLink)var1).isRoleBNonpersistentProxy()) {
                this.insert(((ObjectToObjectLink)var1).getRoleBObject(), var2, var3);
            }
        }

        Transaction var4 = new Transaction();

        try {
            var4.start();
            this.dispatchVetoableEvent("PRE_INSERT", var1);

            try {
                PersistentObjectManager.getPom().insert(var1, var2, var3);
            } catch (UniquenessException var11) {
                if (!(DataServiceFactory.getDefault().getDatastore() instanceof PostgreSQL)) {
                    var4.commit();
                    var4 = null;
                    var1.setPersistInfo((PersistInfo)null);
                }

                Object[] var6 = new Object[]{IdentityFactory.getDisplayIdentity(var1)};
                UniquenessException var7 = new UniquenessException((Exception)null, "wt.fc.fcResource", "41", var6);
                throw var7;
            }

            this.dispatchVetoableEvent("INSERT", var1);
            var4.commit();
            var4 = null;
        } finally {
            if (var4 != null) {
                var1.setPersistInfo((PersistInfo)null);
                var4.rollback();
            }

        }

    }

    public Persistable store(Persistable var1, Timestamp var2, Timestamp var3) throws WTException {
        Transaction var4 = new Transaction();

        try {
            var4.start();
            this.dispatchVetoableEvent("PRE_STORE", var1);
            this.checkStore(var1);
            var1.checkAttributes();
            this.insert(var1, var2, var3);
            this.dispatchVetoableEvent("POST_STORE", var1);
            if (var1 instanceof Notifiable && !(var1 instanceof Iterated)) {
                CreateSummaryEvent var5 = CreateSummaryEvent.getSummaryEvent();
                var5.contribute((Notifiable)var1);
            }

            var4.commit();
            var4 = null;
        } finally {
            if (var4 != null) {
                var4.rollback();
            }

        }

        return var1;
    }

    public void query(StatementSpec var1, ResultProcessor var2) throws WTException {
        this.query(var1, new StandardACProcessor(new AccessControllerAdapter()), var2);
    }

    public QueryResult query(StatementSpec var1, AccessControllerProcessor var2) throws WTException {
        return this._query(var1, var2, (ResultProcessor)null);
    }

    public void lock(Object var1) throws WTException {
        this.lock(var1, true);
    }

    public void lock(Object var1, boolean var2) throws WTException {
        PersistentObjectManager.getPom().lock(var1, var2);
    }

    public Persistable restore(Persistable var1, boolean var2, boolean var3, boolean var4, boolean var5) throws WTException, ObjectNoLongerExistsException {
        if (PersistenceHelper.isPersistent(var1)) {
            Persistable var6 = PersistentObjectManager.getPom().refresh(var1, var3, var5);
            if (var4) {
                var6 = FederationServerHelper.service.checkFreshness(var6);
            }

            if (var6 == null) {
                Object[] var7 = new Object[]{"restore", IdentityFactory.getDisplayType(var1), IdentityFactory.getDisplayIdentifier(var1)};
                ObjectNoLongerExistsException var8 = new ObjectNoLongerExistsException((Exception)null, "wt.fc.fcResource", "32", var7);
                throw var8;
            }

            var1 = var6;
        }

        return var1;
    }

    public int execute(BatchSpec var1) throws WTException {
        String var2 = "execute(BatchSpec) ";
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Enter " + var2 + "[" + var1 + "]");
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("StackTrace", new Throwable());
            }
        }

        boolean var3 = false;
        boolean var4 = DataServiceFactory.getDefault().getDatastore() instanceof SQLServer;
        Transaction var5 = new Transaction();

        int var12;
        try {
            var5.start();
            boolean var6 = false;
            DeleteBatchSpec var7 = null;
            if (var1 instanceof DeleteBatchSpec) {
                var7 = (DeleteBatchSpec)var1;
                Class var8 = var7.getTarget().getTableClass();
                if (DELETE_BYPASS_REFERENTIAL_INTEGRITY_SET.contains(var8.getName())) {
                    if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
                        REF_INTEGRITY_LOGGER.info("execute(BatchSpec) bypassing referential integrity processing: class=" + var8);
                    }
                } else {
                    var6 = AssociationRuntimeUtilities.isRuntimeReferentialIntegrityProcessingRequired(WTIntrospector.getClassInfo(var8));
                    if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
                        REF_INTEGRITY_LOGGER.info("execute(BatchSpec) referential integrity processing: required=" + var6 + " class=" + var8);
                    }
                }
            }

            if (var7 != null && (var4 || var6)) {
                LOGGER.info("execute() processing batch spec indirectly via target keys");
                DeleteBatchSpecChunkResultProcessor var13 = new DeleteBatchSpecChunkResultProcessor();
                BatchSpecificationUtilities.queryTargetKeys(var7, !var4, var13);
                var13.complete();
                var12 = var13.size();
            } else {
                LOGGER.info("execute() processing batch spec directly");
                var12 = PersistentObjectManager.getPom().execute(var1, (WTSet)null, false);
            }

            var5.commit();
            var5 = null;
        } finally {
            if (var5 != null) {
                var5.rollback();
            }

        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Exit  " + var2 + var12);
        }

        return var12;
    }

    public void insert(WTCollection var1, WTCollectionExceptionHandler var2) throws WTException {
        String var3 = "insert(WTCollection,WTCollectionExceptionHandler) ";
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Enter " + var3 + Arrays.toString(new Object[]{var1, var2}));
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("stacktrace", new Throwable());
            }
        }

        assert !var1.isEmpty() : "Empty WTCollection";

        Transaction var4 = new Transaction();
        boolean var19 = false;

        try {
            var19 = true;
            var4.start();
            this.dispatchVetoableEvent("PRE_INSERT", var1);
            (new AbstractInsertUpdateProcessor((ArrayList)null, var2) {
                protected void doMultiOperation(WTCollection var1) throws WTException {
                    Iterator var2 = var1.classIterator();

                    while(var2.hasNext()) {
                        WTCollection var3 = var1.subCollection((Class)var2.next(), false);
                        PersistentObjectManager.getPom().insert(var3, (Timestamp)null, (Timestamp)null);
                    }

                }

                protected void doSingleOperation(Persistable var1) throws WTException {
                    var1.setPersistInfo((PersistInfo)null);
                    StandardPersistenceManager.this.insert(var1);
                }

                protected void resetAttributeState(WTCollection var1, Object var2) throws WTException {
                    Iterator var3 = var1.persistableIterator();

                    while(var3.hasNext()) {
                        ((Persistable)var3.next()).setPersistInfo((PersistInfo)null);
                    }

                }
            }).execute(var1);
            this.dispatchVetoableEvent("INSERT", var1);
            var4.commit();
            var4 = null;
            var19 = false;
        } finally {
            if (var19) {
                if (var4 != null) {
                    try {
                        Iterator var8 = var1.persistableIterator();

                        while(var8.hasNext()) {
                            ((Persistable)var8.next()).setPersistInfo((PersistInfo)null);
                        }
                    } finally {
                        var4.rollback();
                    }
                }

            }
        }

        if (var4 != null) {
            try {
                Iterator var5 = var1.persistableIterator();

                while(var5.hasNext()) {
                    ((Persistable)var5.next()).setPersistInfo((PersistInfo)null);
                }
            } finally {
                var4.rollback();
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Exit  " + var3);
        }

    }

    public void update(WTCollection var1) throws WTException {
        this.update(var1, true);
    }

    public void remove(WTSet var1) throws WTException {
        String var2 = "remove(WTSet) ";
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Enter " + var2 + "[" + var1 + "]");
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("StackTrace", new Throwable());
            }
        }

        assert !((WTSet)var1).isEmpty() : "Empty WTCollection";

        if (((WTSet)var1).getKeyMask() != 1) {
            var1 = new WTHashSet((Collection)var1, 1);
        }

        Transaction var3 = new Transaction();
        boolean var20 = false;

        Iterator var4;
        try {
            var20 = true;
            var3.start();
            var4 = ((WTSet)var1).classIterator();

            while(true) {
                if (!var4.hasNext()) {
                    var3.commit();
                    var3 = null;
                    var20 = false;
                    break;
                }

                Class var5 = (Class)var4.next();
                WTSet var6 = (WTSet)((WTSet)var1).subCollection(var5, false);
                this.remove(WTIntrospector.getClassInfo(var5), var6, true, true, (WTSet)null, (WTSet)var1);
            }
        } finally {
            if (var20) {
                if (var3 != null) {
                    try {
                        Iterator var9 = ((WTSet)var1).persistableIterator();

                        while(var9.hasNext()) {
                            ((Persistable)var9.next()).getPersistInfo().resetDeleted();
                        }
                    } finally {
                        var3.rollback();
                    }
                }

            }
        }

        if (var3 != null) {
            try {
                var4 = ((WTSet)var1).persistableIterator();

                while(var4.hasNext()) {
                    ((Persistable)var4.next()).getPersistInfo().resetDeleted();
                }
            } finally {
                var3.rollback();
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Exit  " + var2);
        }

    }

    public WTValuedHashMap copyLink(WTValuedHashMap var1) throws WTException {
        WTCollection var2 = (WTCollection)var1.values();
        this.insert(var2);
        this.dispatchVetoableEvent(new PersistenceManagerEvent("COPY_LINK", CollectionsHelper.unmodifiableWTValuedMap(var1)), false);
        return var1;
    }

    public void update(WTCollection var1, final boolean var2, WTCollectionExceptionHandler var3) throws WTException {
        String var4 = "update(WTCollection,boolean,WTCollectionExceptionHandler) ";
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Enter " + var4 + Arrays.toString(new Object[]{var1, new Boolean(var2), var3}));
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("StackTrace", new Throwable());
            }
        }

        assert !var1.isEmpty() : "Empty WTCollection";

        Transaction var5 = new Transaction();
        ArrayList var6 = savePersistInfoState(var1);

        try {
            var5.start();
            validateCollection(var1, true);
            (new AbstractInsertUpdateProcessor(var6, var3) {
                protected void doMultiOperation(WTCollection var1) throws WTException {
                    Iterator var2x = var1.classIterator();

                    while(var2x.hasNext()) {
                        WTCollection var3 = var1.subCollection((Class)var2x.next(), false);
                        PersistentObjectManager.getPom().update(var3, var2);
                    }

                }

                protected void doSingleOperation(Persistable var1) throws WTException {
                    StandardPersistenceManager.this.update(var1);
                }
            }).execute(var1);
            this.dispatchVetoableEvent("UPDATE", var1);
            var5.commit();
            var5 = null;
        } finally {
            if (var5 != null) {
                try {
                    resetPersistInfoState(var6);
                } finally {
                    var5.rollback();
                }
            }

        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Exit  " + var4);
        }

    }

    public Persistable restore(ObjectIdentifier var1, boolean var2, boolean var3) throws WTException, ObjectNoLongerExistsException {
        Persistable var4 = null;
        if (var1 == null) {
            throw new WTException((Throwable)null, "wt.fc.fcResource", "7", (Object[])null);
        } else {
            Object[] var5;
            if (!var1.isAssigned()) {
                var5 = new Object[]{var1.getClassname()};
                throw new WTException((Throwable)null, "wt.fc.fcResource", "7", var5);
            } else {
                var4 = PersistentObjectManager.getPom().query(var1, var3);
                if (var2) {
                    var4 = FederationServerHelper.service.checkFreshness(var4);
                }

                if (var4 == null) {
                    var5 = new Object[]{"restore", var1.getClassname(), Long.toString(var1.getId())};
                    ObjectNoLongerExistsException var6 = new ObjectNoLongerExistsException((Exception)null, "wt.fc.fcResource", "32", var5);
                    throw var6;
                } else {
                    return var4;
                }
            }
        }
    }

    public void query(StatementSpec var1, AccessControllerProcessor var2, ResultProcessor var3) throws WTException {
        this._query(var1, var2, var3);
    }

    public void insert(WTCollection var1) throws WTException {
        this.insert(var1, (WTCollectionExceptionHandler)null);
    }

    public void update(WTCollection var1, boolean var2) throws WTException {
        this.update(var1, var2, (WTCollectionExceptionHandler)null);
    }

    private QueryResult _query(StatementSpec var1, AccessControllerProcessor var2, ResultProcessor var3) throws WTException {
        DebugLogger logger =  DebugLogger.getInstance();
        QueryResult var4 = null;
        if(!var1.toString().contains("FROM wt.inf.sharing.SharedContainerMap")&&
                !var1.toString().contains("FROM wt.preference.PreferenceInstanc")&&
                !var1.toString().contains("FROM wt.content.HolderToContent")&&
                !var1.toString().contains("FROM wt.change2.ReportedAgainst")&&
                !var1.toString().contains("FROM wt.workflow.forum.IteratedForumSubjectLink")&&
                !var1.toString().contains("FROM com.ptc.core.task.TaskEvent")&&
                !var1.toString().contains("FROM wt.epm.workspaces.EPMInitialCheckinData")){
           if (!(var1 instanceof PageableSessionQuerySpec) && !(var1 instanceof PagingSessionSpec)) {
               logger.log("");
               logger.log("---------------------------不分页查询分割线---------------------------------------");
               if(var1 instanceof CompositeQuerySpec){
                   CompositeQuerySpec querySpec = (CompositeQuerySpec)var1;
                   logger.log(querySpec.getComponents());

               }else  if(var1 instanceof InflateSpec){
                   InflateSpec querySpec = (InflateSpec)var1;
                   logger.log(querySpec.getStatementBuilder());

               }else{
                   logger.log(var1);
               }


           }else{
               logger.log("");
               logger.log("---------------------------分页查询分割线---------------------------------------");
               logger.log(var1);

            }

        }
        if (!(var1 instanceof PageableSessionQuerySpec) && !(var1 instanceof PagingSessionSpec)) {
            if (var3 == null) {
                var4 = PersistentObjectManager.getPom().query(var1, var2);
            } else {
                PersistentObjectManager.getPom().query(var1, var2, var3);
            }
        } else if (var3 == null) {
            var4 = PersistentObjectManager.getPom().query((PageableQuerySpec)var1, getPagingSessionCache(), var2);
        } else {
            PersistentObjectManager.getPom().query((PageableQuerySpec)var1, var2, getPagingSessionCache(), var3);
        }



        return var4;
    }

    private AccessController buildAccessController() {
        CompositeAccessController var1 = new CompositeAccessController();
        var1.addComponent(SurrogateAccessController.STANDARD_ACCESS_CONTROLLER);
        var1.addComponent(MarkForDeleteAccessController.MARK_FOR_DELETE_ACCESS_CONTROLLER);
        if (RestrictToOrgAccessController.WOD_LOCAL_SEARCH_RESTRICT) {
            var1.addComponent(new RestrictToOrgAccessController());
        }

        return var1;
    }

    private QueryResult _find(StatementSpec var1, ResultProcessor var2) throws WTException {
        QueryResult var3 = null;
        StandardACProcessor var4 = new StandardACProcessor(this.buildAccessController());
        var3 = this._query(var1, var4, var2);
        return var3;
    }

    private void checkOperation(WTCollection var1, int var2, boolean var3) throws WTException {
        AccessPermission var4 = null;
        switch (var2) {
            case 0:
                var4 = AccessPermission.CREATE;
                break;
            case 1:
                var4 = AccessPermission.MODIFY;
                break;
            case 2:
                var4 = AccessPermission.DELETE;
        }

        AccessControlHelper.manager.checkAccess(var1, var4);
        Iterator var5 = var1.persistableIterator();

        while(var5.hasNext()) {
            Persistable var6 = (Persistable)var5.next();
            switch (var2) {
                case 0:
                    this._checkStore(var6);
                    break;
                case 1:
                case 2:
                    this.checkPersistent(var6);
            }

            if (var3) {
                var6.checkAttributes();
            }
        }

    }

    private void _checkStore(Persistable var1) throws WTException {
        if (var1 instanceof Link) {
            if (var1 instanceof ObjectToObjectLink) {
                if (((ObjectToObjectLink)var1).isRoleANonpersistentProxy()) {
                    this.store(((ObjectToObjectLink)var1).getRoleAObject());
                }

                if (((ObjectToObjectLink)var1).isRoleBNonpersistentProxy()) {
                    this.store(((ObjectToObjectLink)var1).getRoleBObject());
                }
            }

            Object[] var2 = ((Link)var1).getAllObjects();
            if (var2 != null) {
                for(int var3 = 0; var3 < var2.length; ++var3) {
                    if (!PersistenceHelper.isPersistent(var2[var3])) {
                        throw new WTException((Throwable)null, "wt.fc.fcResource", "9", (Object[])null);
                    }
                }
            }
        }

    }

    private void checkPersistent(Persistable var1) throws WTException {
        if (!PersistenceHelper.isPersistent(var1)) {
            Object[] var2 = new Object[]{IdentityFactory.getDisplayIdentity(var1)};
            throw new WTException((Throwable)null, "wt.fc.fcResource", "7", var2);
        }
    }

    private void remove(ClassInfo var1, WTSet var2, boolean var3, boolean var4, WTSet var5, WTSet var6) throws WTException {
        String var7 = "remove(ClassInfo,WTSet,boolean,boolean,WTSet,WTSet) ";
        if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
            Object[] var8 = new Object[]{var1, var2, new Boolean(var3), new Boolean(var4), var5, var6};
            REF_INTEGRITY_LOGGER.info("Enter " + var7 + Arrays.toString(var8));
        }

        assert !var2.isEmpty() : "Empty WTCollection";

        Map var20 = Transaction.getGlobalMap();
        HashSet var9 = (HashSet)var20.get(DELETED_PERSISTABLE_SET_KEY);
        if (var9 == null) {
            var9 = new HashSet();
            var20.put(DELETED_PERSISTABLE_SET_KEY, var9);
        }

        if (!var3) {
            var2 = filter(var2, var6);
        }

        if (var2.size() > 0) {
            var2 = filter(var2, var9);
        }

        if (var2.size() > 0) {
            List var10 = AssociationRuntimeUtilities.buildNonOwnerList(var1);
            int var11 = var10 == null ? 0 : var10.size();
            if (var11 > 0) {
                ReferentialIntegrityValidator var12 = (ReferentialIntegrityValidator)var20.get(REFERENTIAL_INTEGRITY_VALIDATOR_KEY);
                if (var12 == null) {
                    var12 = new ReferentialIntegrityValidator();
                    var20.put(REFERENTIAL_INTEGRITY_VALIDATOR_KEY, var12);
                    Transaction.addTransactionListener(var12);
                }

                var12.addNonOwners(var2, var10);
            }

            List var21 = AssociationRuntimeUtilities.buildOwnerList(var1);
            Object var13 = null;
            WTHashSet var14 = null;
            if (var6 == null) {
                var13 = var2;
            } else {
                var13 = new WTHashSet();
                ((WTSet)var13).addAll(var6);
                ((WTSet)var13).addAll(var2);
                var14 = new WTHashSet();
                var14.addAll(var6);
            }

            WTSet var15 = filter(var2, (Set)var5);
            if (var5 == null) {
                var5 = new WTHashSet();
            }

            ((WTSet)var5).addAll(var15);
            ObjectSetVector var16 = this.queryOwnedLinks(var1, var15, var21);
            if (var16 != null && var16.size() > 0) {
                this.processRemoveOids(var1, new ObjectVectorOidArray(var16, 2), 1, 0, var9, (WTSet)var5, (WTSet)var13);
            }

            if (var1 instanceof LinkInfo) {
                LinkInfo var17 = (LinkInfo)var1;
                if (!var17.isLinkTable()) {
                    RoleDescriptor var18 = var17.getRole(var17.getReferenceName()).getOtherRole();
                    if (var18.isCascade()) {
                        for(Iterator var19 = var2.queryKeyIterator(); var19.hasNext(); var14.add((ObjectIdentifier)var19.next())) {
                            if (var14 == null) {
                                var14 = new WTHashSet();
                            }
                        }
                    }
                }
            }

            if (var4) {
                this.dispatchVetoableEvent("PRE_REMOVE", (WTCollection)var2);
            }

            Map var22 = AssociationRuntimeUtilities.buildCascadeMap(var1);
            QueryResult var23 = this.queryCascadeRoles(var2, var22);
            this.removeBatch(var1, var2, var3, var14, var9);
            if (var4) {
                this.dispatchVetoableEvent("REMOVE", (WTCollection)var2);
            }

            if (var23 != null && var23.size() > 0) {
                this.processRemoveOids(var1, new ObjectVectorOidArray((ObjectSetVector)var23.getObjectVectorIfc(), 1), 0, -1, var9, (WTSet)null, (WTSet)var13);
            }
        }

        if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
            REF_INTEGRITY_LOGGER.info("Exit  " + var7);
        }

    }

    private void processRemoveOids(ClassInfo var1, OidArray var2, int var3, int var4, HashSet var5, WTSet var6, WTSet var7) throws WTException {
        int var9;
        if (DBProperties.REF_INTEGRITY_REMOVE_OIDS_OBJECT_CHUNK_SIZE <= 0) {
            this.processRemoveOids(var1, var2, 0, var2.getLength(), var3, var4, var5, var6, var7);
        } else {
            for(int var8 = 0; var8 < var2.getLength(); var8 += var9) {
                var9 = Math.min(DBProperties.REF_INTEGRITY_REMOVE_OIDS_OBJECT_CHUNK_SIZE, var2.getLength() - var8);
                this.processRemoveOids(var1, var2, var8, var9, var3, var4, var5, var6, var7);
            }
        }

    }

    private void processRemoveOids(ClassInfo var1, OidArray var2, int var3, int var4, int var5, int var6, HashSet var7, WTSet var8, WTSet var9) throws WTException {
        String var10 = null;
        if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
            var10 = "processRemoveOids(ClassInfo,OidArray,int,int,int,int,HashSet,HashSet,WTSet)";
            Object[] var11 = new Object[]{var1, var2, new Integer(var3), new Integer(var4), new Integer(var5), new Integer(var6), var7, var8, var9};
            REF_INTEGRITY_LOGGER.info("Enter " + var10 + Arrays.toString(var11));
        }

        HashMap var22 = null;
        HashMap var12 = null;
        WTKeyedHashMap var13 = null;
        WTHashSet var14 = null;

        for(int var15 = 0; var15 < var4; ++var15) {
            ObjectIdentifier var16 = new ObjectIdentifier();
            var16.setClassname(var2.getClassName(var3 + var15, var5));
            var16.setId(((BigDecimal)var2.getId(var3 + var15, var5)).longValue());
            if (!var7.contains(var16)) {
                HashMap var17 = null;
                if (AssociationRuntimeUtilities.isRoleParticipant(var1)) {
                    if (var22 == null) {
                        var22 = new HashMap();
                    }

                    var17 = var22;
                } else {
                    if (var12 == null) {
                        var12 = new HashMap();
                    }

                    var17 = var12;
                }

                WTHashSet var18 = (WTHashSet)var17.get(var16.getClassname());
                if (var18 == null) {
                    var18 = new WTHashSet();
                    var17.put(var16.getClassname(), var18);
                }

                var18.add(var16);
                if (var6 >= 0) {
                    if (var13 == null) {
                        var13 = new WTKeyedHashMap();
                    }

                    ObjectIdentifier var19 = new ObjectIdentifier();
                    var19.setClassname(var2.getClassName(var3 + var15, var6));
                    var19.setId(((BigDecimal)var2.getId(var3 + var15, var6)).longValue());
                    WTHashSet var20 = (WTHashSet)var13.get(var19);
                    if (var20 == null) {
                        var20 = new WTHashSet();
                        var13.put(var19, var20);
                    }

                    var20.connect(var16, var18);
                    if (var14 == null) {
                        var14 = new WTHashSet();
                    }

                    var14.connect(var16, var18);
                }
            }
        }

        if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
            REF_INTEGRITY_LOGGER.info("processRemoveOids(): recursiveMap=" + var22 + " pomMap=" + var12 + " cleanupLinkMap=" + var13);
        }

        if (var14 != null) {
            RefreshSpec var23 = new RefreshSpec();

            try {
                var23.setLinkAction(512);
                var23.setDeleteAction(2);
                var23.setDisableAccess(true);
            } catch (WTPropertyVetoException var21) {
                throw new WTException(var21);
            }

            CollectionsHelper.manager.refresh(var14, var23);
        }

        if (var22 != null) {
            Iterator var24 = var22.keySet().iterator();

            while(var24.hasNext()) {
                String var26 = (String)var24.next();
                this.remove(WTIntrospector.getClassInfo(var26), (WTHashSet)var22.get(var26), false, true, var8, var9);
            }
        }

        if (var12 != null) {
            WTHashSet var25 = new WTHashSet();
            Iterator var27 = var12.values().iterator();

            WTHashSet var28;
            while(var27.hasNext()) {
                var28 = (WTHashSet)var27.next();
                var25.addAll(var28);
            }

            this.dispatchVetoableEvent("PRE_REMOVE", (WTCollection)var25);
            var27 = var12.values().iterator();

            while(var27.hasNext()) {
                var28 = (WTHashSet)var27.next();
                this.removeBatch(var1, var28, false, var9, var7);
            }

            this.dispatchVetoableEvent("REMOVE", (WTCollection)var25);
        }

        if (var13 != null) {
            this.dispatchVetoableEvent(new PersistenceManagerEvent("CLEANUP_LINK", CollectionsHelper.unmodifiableWTKeyedMap(var13)), false);
        }

        if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
            REF_INTEGRITY_LOGGER.info("Exit  " + var10);
        }

    }

    private int removeBatch(ClassInfo var1, WTSet var2, boolean var3, WTSet var4, Set var5) throws WTException {
        String var6 = null;
        if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
            var6 = "removeBatch(ClassInfo,WTSet,boolean,WTSet,Set) ";
            Object[] var7 = new Object[]{var1, var2, new Boolean(var3), var4, var5};
            REF_INTEGRITY_LOGGER.info("Enter " + var6 + Arrays.toString(var7));
        }

        Class var10;
        if (var4 != null && var4.size() > 0) {
            if (var1 instanceof LinkInfo) {
                LinkInfo var14 = (LinkInfo)var1;
                if (!var14.isLinkTable()) {
                    Iterator var8 = var2.queryKeyIterator();

                    while(var8.hasNext()) {
                        var5.add((ObjectIdentifier)var8.next());
                    }

                    RoleDescriptor var9 = var14.getRole(var14.getReferenceName()).getOtherRole();
                    var10 = var9.getValidClass();
                    HashSet var11 = null;
                    var8 = var4.queryKeyIterator(var10, true);

                    while(var8.hasNext()) {
                        if (var11 == null) {
                            var11 = new HashSet();
                        }

                        ObjectIdentifier var12 = new ObjectIdentifier();
                        var12.setClassname(var14.getClassname());
                        var12.setId(((ObjectIdentifier)var8.next()).getId());
                        var11.add(var12);
                    }

                    if (var11 != null) {
                        var2 = filter(var2, var11);
                    }
                }
            }

            if (!var3) {
                var2 = filter(var2, (Set)var4.subCollection(var1.getBusinessClass(), false));
            }
        }

        int var15 = 0;
        if (var2.size() > 0) {
            Datastore var16 = DataServiceFactory.getDefault().getDatastore();
            if (var16 instanceof Oracle) {
                ClassTableExpression var18 = new ClassTableExpression(var1.getBusinessClass());
                var18.setDescendantsIncluded(false);
                var10 = null;

                DeleteBatchSpec var20;
                try {
                    var20 = new DeleteBatchSpec(var18, (WhereExpression)null);
                    var20.appendWhere(var2, var3, (LogicalOperator)null);
                } catch (WTPropertyVetoException var13) {
                    throw new WTException(var13);
                }

                var15 = PersistentObjectManager.getPom().execute(var20, var2, var3);
            } else {
                boolean var17 = var3 && var2.size() > 1 && requiresPreValidate(var1.getClassname());
                if (var17) {
                    validateCollection(var2, true);
                }

                var15 = PersistentObjectManager.getPom().delete(var2, var3);
                if (var17) {
                    var15 = var2.size();
                }
            }

            Iterator var19 = var2.queryKeyIterator();

            while(var19.hasNext()) {
                var5.add((ObjectIdentifier)var19.next());
            }

            if (var3 && var15 != var2.size()) {
                validateCollection(var2, false);
                throw new ObjectIsStaleException((Persistable)null, "16");
            }
        }

        if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
            REF_INTEGRITY_LOGGER.info("Exit  " + var6 + var15);
        }

        return var15;
    }

    private static boolean requiresPreValidate(String var0) {
        return DELETE_TRIGGER_SET.contains(var0);
    }

    private static WTSet filter(WTSet var0, Set var1) throws WTException {
        String var2 = null;
        if (REF_INTEGRITY_LOGGER.isDebugEnabled()) {
            var2 = "filter(WTSet,Set)";
            Object[] var3 = new Object[]{var0, var1};
            REF_INTEGRITY_LOGGER.debug("Enter " + var2 + Arrays.toString(var3));
        }

        boolean var7 = false;
        if (var1 != null) {
            Iterator var4 = var1.iterator();

            while(var4.hasNext()) {
                Object var5 = var4.next();
                if (var7) {
                    ((WTSet)var0).remove(var5);
                } else if (((WTSet)var0).contains(var5)) {
                    WTHashSet var6 = new WTHashSet();
                    var6.addAll((Collection)var0);
                    var0 = var6;
                    var6.remove(var5);
                    var7 = true;
                }
            }
        }

        if (REF_INTEGRITY_LOGGER.isDebugEnabled()) {
            REF_INTEGRITY_LOGGER.debug("Exit  " + var2 + var0);
        }

        return (WTSet)var0;
    }

    private ObjectSetVector queryOwnedLinks(ClassInfo var1, WTSet var2, List var3) throws WTException {
        String var4 = null;
        if (REF_INTEGRITY_LOGGER.isDebugEnabled()) {
            var4 = "queryOwnedLinks(ClassInfo,WTSet,List)";
            Object[] var5 = new Object[]{var1, var2, var3};
            REF_INTEGRITY_LOGGER.debug("Enter " + var4 + Arrays.toString(var5));
        }

        ObjectSetVector var16 = null;
        int var6 = var3 == null ? 0 : var3.size();
        if (var2.size() > 0 && var6 > 0) {
            long[] var7 = var2.toArray(new long[var2.size()]);
            int var8 = -1;
            int var9 = -1;
            int var10;
            if (DBProperties.isReferentialIntegrityOwnerQueryChunkThreshold(var6, var7.length)) {
                var8 = DBProperties.REF_INTEGRITY_OWNER_QUERY_ROLE_CHUNK_SIZE;
                var9 = DBProperties.REF_INTEGRITY_OWNER_QUERY_OBJECT_CHUNK_SIZE;
            } else if (DataServiceFactory.getDefault().getDatastore() instanceof SQLServer && DBProperties.USE_BIND && DBProperties.IN_CLAUSE_BIND_OPTIMIZATION_THRESHOLD > var7.length) {
                var10 = ArrayExpression.getFixedLength(var7.length);
                ++var10;
                if (var10 * var6 >= DBProperties.QUERY_BIND_LIMIT) {
                    var8 = DBProperties.QUERY_BIND_LIMIT / var10 - 1;
                    if (var8 <= 0) {
                        var8 = 1;
                        var9 = DBProperties.QUERY_BIND_LIMIT;
                    }
                }
            }

            if (REF_INTEGRITY_LOGGER.isInfoEnabled()) {
                REF_INTEGRITY_LOGGER.info("queryOwnedLinks(): roleCount=" + var6 + " roleChunkSize=" + var8 + " objectIdCount=" + var2.size() + " objectChunkSize=" + var9);
            }

            var10 = 0;

            while(var10 < var6) {
                List var11 = null;
                int var12;
                if (var8 <= 0) {
                    var11 = var3;
                    var10 = var6;
                } else {
                    var12 = Math.min(var6 - var10, var8);
                    var11 = var3.subList(var10, var10 + var12);
                    var10 += var12;
                }

                var12 = 0;

                while(var12 < var7.length) {
                    if (REF_INTEGRITY_LOGGER.isDebugEnabled()) {
                        REF_INTEGRITY_LOGGER.debug("queryOwnedLinks(): roleOffset=" + var10 + " objectOffset=" + var12);
                    }

                    Object var13 = null;
                    long[] var17;
                    if (var9 <= 0) {
                        var17 = var7;
                        var12 = var7.length;
                    } else {
                        int var14 = Math.min(var7.length - var12, var9);
                        var17 = new long[var14];
                        System.arraycopy(var7, var12, var17, 0, var14);
                        var12 += var14;
                    }

                    boolean var18 = DatabaseInfoUtilities.isForeignKeyLink(var1);
                    StatementSpec var15 = AssociationRuntimeUtilities.buildRoleSpec(var17, var11, (Map)null, true, false, var1, var18);
                    if (var15 != null) {
                        if (var16 == null) {
                            var16 = new ObjectSetVector();
                        }

                        this.query(var15, (ResultProcessor)var16);
                    }
                }
            }
        }

        if (REF_INTEGRITY_LOGGER.isDebugEnabled()) {
            REF_INTEGRITY_LOGGER.debug("Exit  " + var4 + var16);
        }

        return var16;
    }

    private QueryResult queryCascadeRoles(WTSet var1, Map var2) throws WTException {
        StatementSpec var3 = null;
        if (var2 != null) {
            var3 = AssociationRuntimeUtilities.buildRoleSpec(var1.toArray(new long[var1.size()]), var2.keySet(), var2, false, true, (ClassInfo)null, true);
        }

        return var3 == null ? null : this.query(var3);
    }

    private void dispatchVetoableEvent(String var1, Persistable var2) throws WTException {
        this.dispatchVetoableEvent(new PersistenceManagerEvent(var1, var2), true);
    }

    private void dispatchVetoableEvent(String var1, WTCollection var2) throws WTException {
        this.dispatchVetoableEvent(new PersistenceManagerEvent(var1, CollectionsHelper.unmodifiableWTCollection(var2)), false);
    }

    private void dispatchVetoableEvent(PersistenceManagerEvent var1, boolean var2) throws WTException {
        ManagerService var3 = this.getManagerService();
        String var4 = var1.getEventKey();
        if (var2) {
            var3.dispatchVetoableEvent(var1, var4);
        } else {
            var3.dispatchVetoableMultiObjectEvent(var1, var4);
        }

    }

    static ArrayList savePersistInfoState(WTCollection var0) throws WTException {
        ArrayList var1 = null;
        Iterator var2 = var0.persistableIterator();

        while(var2.hasNext()) {
            Persistable var3 = (Persistable)var2.next();
            PersistInfoData var4 = new PersistInfoData();
            var4.persistInfo = var3.getPersistInfo();
            if (var4.persistInfo != null) {
                if (var1 == null) {
                    var1 = new ArrayList();
                }

                var4.updateStamp = var4.persistInfo.getUpdateStamp();
                var4.modifyStamp = var4.persistInfo.getModifyStamp();
                var4.updateCount = var4.persistInfo.getUpdateCount();
                var1.add(var4);
            }
        }

        return var1;
    }

    static void resetPersistInfoState(Object var0) {
        ArrayList var1 = null;
        if (var0 != null) {
            var1 = (ArrayList)var0;
        }

        int var2 = var1 == null ? 0 : var1.size();

        for(int var3 = 0; var3 < var2; ++var3) {
            PersistInfoData var4 = (PersistInfoData)var1.get(var3);
            var4.persistInfo.setUpdateStamp(var4.updateStamp);
            var4.persistInfo.setModifyStamp(var4.modifyStamp);
            var4.persistInfo.setUpdateCount(var4.updateCount);
        }

    }

    public QueryResult expand(Persistable var1, String var2, Class var3, boolean var4, boolean var5) throws WTException {
        return this.expand(var1, var2, var3, var4, new StandardACProcessor(new AccessControllerAdapter()), var5);
    }

    private QueryResult expand(Persistable var1, String var2, Class var3, boolean var4, AccessControllerProcessor var5, boolean var6) throws WTException {
        boolean var8 = var1 instanceof Federated;
        QueryResult var7;
        if (var8 && !PersistenceHelper.isPersistent(var1)) {
            var7 = new QueryResult();
        } else {
            var7 = FederationServerHelper.service.checkFreshness(PersistentObjectManager.getPom().expand(var1, var2, var3, var4, var5, var6));
        }

        if (var8) {
            QueryResult var9 = ((Federated)var1).navigate(var2, var3, var4);
            if (var9.hasMoreElements()) {
                FederationHelper.appendNewElements(var7, var9);
            }
        }

        return var7;
    }

    private QueryResult expand(Persistable var1, String var2, QuerySpec var3, boolean var4, AccessControllerProcessor var5) throws WTException {
        boolean var7 = var1 instanceof Federated;
        QueryResult var6;
        if (var7 && !PersistenceHelper.isPersistent(var1)) {
            var6 = new QueryResult();
        } else {
            var6 = FederationServerHelper.service.checkFreshness(PersistentObjectManager.getPom().expand(var1, var2, var3, var4, var5));
        }

        if (var7) {
            QueryResult var8 = ((Federated)var1).navigate(var2, var3, var4);
            if (var8.hasMoreElements()) {
                FederationHelper.appendNewElements(var6, var8);
            }
        }

        return var6;
    }

    private QuerySpec makeQuerySpec(Class var1, Persistable var2, String var3, Persistable var4) throws WTException {
        QuerySpec var5 = null;
        if (PersistenceHelper.isPersistent(var2) && PersistenceHelper.isPersistent(var4)) {
            LinkInfo var6 = WTIntrospector.getLinkInfo(var1);
            RoleDescriptor var7 = var6.isRoleA(var3) ? var6.getRoleB() : var6.getRoleA();
            String var8 = var7.getName();
            var5 = new QuerySpec(var1);
            var5.appendJoin(0, var3, var2);
            var5.appendJoin(0, var8, var4);
            return var5;
        } else {
            throw new WTException((Throwable)null, "wt.fc.fcResource", "16", (Object[])null);
        }
    }

    private QuerySpec makeQuerySpec(Class var1, ObjectIdentifier var2, String var3, ObjectIdentifier var4) throws WTException, InvalidRoleException {
        QuerySpec var5 = null;
        if (!ObjectToObjectLink.class.isAssignableFrom(var1)) {
            Object[] var9 = new Object[]{var2, var1.getName()};
            throw new InvalidRoleException((Exception)null, "wt.fc.fcResource", "19", var9);
        } else {
            LinkInfo var6 = WTIntrospector.getLinkInfo(var1);
            var5 = new QuerySpec(var1);
            int[] var7 = new int[]{0};
            if (var6.isRoleA(var3)) {
                var5.appendWhere(new SearchCondition(var1, "roleAObjectRef.key", "=", var2), var7);
                var5.appendAnd();
                var5.appendWhere(new SearchCondition(var1, "roleBObjectRef.key", "=", var4), var7);
            } else {
                if (!var6.isRoleB(var3)) {
                    Object[] var8 = new Object[]{var3, var1.getName()};
                    throw new InvalidRoleException((Exception)null, "wt.fc.fcResource", "17", var8);
                }

                var5.appendWhere(new SearchCondition(var1, "roleBObjectRef.key", "=", var2), var7);
                var5.appendAnd();
                var5.appendWhere(new SearchCondition(var1, "roleAObjectRef.key", "=", var4), var7);
            }

            return var5;
        }
    }

    public static boolean requiresInflate(String var0, Vector var1) {
        boolean var2 = false;

        for(int var3 = 0; var3 < var1.size() && !var2; ++var3) {
            String var4 = (String)var1.elementAt(var3);
            if (var4.indexOf(var0) > -1) {
                var2 = true;
            }
        }

        return var2;
    }

    public static PagingSessionCache getPagingSessionCache() {
        if (pagingSessionCache == null) {
            Class var0 = StandardPersistenceManager.class;
            synchronized(StandardPersistenceManager.class) {
                if (pagingSessionCache == null) {
                    try {
                        pagingSessionCache = new PagingSessionCache();
                    } catch (RemoteException var3) {
                        throw new MethodServerException("Unable to create pagingsession cache", var3);
                    }
                }
            }
        }

        return pagingSessionCache;
    }

    private static void updatePagingSessionCache(PagingSession var0) {
        try {
            PagingSessionCache var1 = getPagingSessionCache();
            var1.remove(new Long(var0.getSessionId()));
        } catch (Exception var2) {
            System.out.println("could not remove " + var0.getSessionId() + " from cache");
            var2.printStackTrace();
        }

    }

    private static void validateCollection(WTCollection var0, boolean var1) throws WTException {
        RefreshSpec var2 = new RefreshSpec();

        try {
            var2.setStaleAction(16);
            var2.setDeleteAction(4);
            var2.setLinkAction(512);
            var2.setLock(var1);
            var2.setDisableAccess(true);
        } catch (WTPropertyVetoException var4) {
            throw new WTException(var4);
        }

        CollectionsHelper.manager.refresh(var0, var2);
    }

    static {
        try {
            WTProperties var0 = WTProperties.getLocalProperties();
            DBProperties var1 = DBProperties.getDBProperties();
            var1.getPropertyAsSet("wt.pom.delete.triggerClassNames", "", DELETE_TRIGGER_SET);
            var1.getPropertyAsSet("wt.pom.delete.bypassReferentialIntegrityClassNames", "", DELETE_BYPASS_REFERENTIAL_INTEGRITY_SET);
            PREPARE_FOR_MODIFICATION_DEFAULT = new RefreshSpec();
            PREPARE_FOR_MODIFICATION_DEFAULT.setDeleteAction(4);
            PREPARE_FOR_MODIFICATION_DEFAULT.setStaleAction(8);
            PREPARE_FOR_MODIFICATION_DEFAULT.setDisableAccess(true);
        } catch (Exception var2) {
            throw new ExceptionInInitializerError(var2);
        }

        REFERENTIAL_INTEGRITY_VALIDATOR_KEY = new Object();
        DELETED_PERSISTABLE_SET_KEY = new Object();
    }

    class DeleteBatchSpecChunkResultProcessor extends AbstractOidChunkResultProcessor {
        DeleteBatchSpecChunkResultProcessor() {
        }

        protected boolean isAtChunkLimit(String var1, int var2) {
            return DBProperties.BATCH_SPEC_DELETE_CLASS_CHUNK_SIZE > 0 && var2 > DBProperties.BATCH_SPEC_DELETE_CLASS_CHUNK_SIZE;
        }

        protected boolean isAtChunkLimit(int var1) {
            return DBProperties.BATCH_SPEC_DELETE_CHUNK_SIZE > 0 && var1 > DBProperties.BATCH_SPEC_DELETE_CHUNK_SIZE;
        }

        protected void process(Class var1, WTSet var2) throws WTException {
            if (DBProperties.BATCH_SPEC_DELETE_DISPATCH_EVENTS) {
                RefreshSpec var3 = new RefreshSpec();

                try {
                    var3.setLinkAction(512);
                    var3.setDisableAccess(true);
                } catch (WTPropertyVetoException var5) {
                    throw new WTException(var5);
                }

                CollectionsHelper.manager.refresh(var2, var3);
            }

            StandardPersistenceManager.this.remove(WTIntrospector.getClassInfo(var1), var2, false, DBProperties.BATCH_SPEC_DELETE_DISPATCH_EVENTS, (WTSet)null, (WTSet)null);
        }
    }

    abstract static class AbstractInsertUpdateProcessor extends WTCollectionRetrySingleProcessor {
        public AbstractInsertUpdateProcessor(ArrayList var1, WTCollectionExceptionHandler var2) {
            super(var1, (Object)null, var2);
        }

        protected boolean isTargetExceptionType(Exception var1) {
            return var1 instanceof UniquenessException;
        }

        protected WTException buildNewException(WTCollection var1) {
            return new UniquenessException(var1);
        }
    }

    private static class ReferentialIntegrityValidator implements TransactionCommitListener {
        private ReferentialIntegrityException exception;

        private ReferentialIntegrityValidator() {
            this.exception = null;
        }

        public void beforeCompletion() throws WTException {
            Manifest var1 = PersistenceServerHelper.getGlobalTransactionManifest(Transaction.BEFORE_COMPLETION_MANIFEST_KEY);
            if (StandardPersistenceManager.REF_INTEGRITY_LOGGER.isInfoEnabled()) {
                StandardPersistenceManager.REF_INTEGRITY_LOGGER.info("ReferentialIntegrityValidator.beforeCompletion(): manifest=" + var1);
            }

            if (var1.hasDirectives("REFERENTIAL_INTEGRITY_DIRECTIVE_KEY")) {
                DirectiveResult[] var2 = var1.getDirectiveResults("REFERENTIAL_INTEGRITY_DIRECTIVE_KEY");
                int var3 = var2 == null ? 0 : var2.length;
                Object var4 = null;
                if (var3 == 1) {
                    var4 = (WTKeyedMap)((AbstractDirectiveResult)var2[0]).getResult();
                } else if (var3 > 1) {
                    var4 = new WTKeyedHashMap();

                    for(int var5 = 0; var5 < var3; ++var5) {
                        WTKeyedMap var6 = (WTKeyedMap)((AbstractDirectiveResult)var2[var5]).getResult();
                        Iterator var7 = var6.entrySet().iterator();

                        while(var7.hasNext()) {
                            Map.Entry var8 = (Map.Entry)var7.next();
                            Object var9 = ((WTKeyedMap)var4).get(var8.getKey());
                            if (var9 == null) {
                                ((WTKeyedMap)var4).put(var8.getKey(), var8.getValue());
                            } else {
                                if (!(var9 instanceof List)) {
                                    ArrayList var10 = new ArrayList();
                                    var10.add(var9);
                                    var9 = var10;
                                }

                                ((List)var9).add(var8.getValue());
                            }
                        }
                    }
                }

                if (var4 != null && ((WTKeyedMap)var4).size() > 0) {
                    this.exception = new ReferentialIntegrityException((WTKeyedMap)var4);
                    throw this.exception;
                }
            }

        }

        public void finishTransaction() throws WTException {
        }

        public void notifyCommit() {
        }

        public void notifyRollback() {
            if (this.exception != null) {
                try {
                    this.exception.buildAdditionalMessages();
                } catch (WTException var2) {
                    if (StandardPersistenceManager.REF_INTEGRITY_LOGGER.isInfoEnabled()) {
                        StandardPersistenceManager.REF_INTEGRITY_LOGGER.info("ReferentialIntegrityValidator.notifyRollback() exception building message", var2);
                    }
                }

                if (StandardPersistenceManager.REF_INTEGRITY_LOGGER.isInfoEnabled()) {
                    StandardPersistenceManager.REF_INTEGRITY_LOGGER.info("ReferentialIntegrityValidator.notifyRollback()", this.exception);
                }
            }

        }

        void addNonOwners(WTSet var1, List var2) throws WTException {
            Manifest var3 = PersistenceServerHelper.getGlobalTransactionManifest(Transaction.BEFORE_COMPLETION_MANIFEST_KEY);
            int var4 = var2.size();

            for(int var5 = 0; var5 < var4; ++var5) {
                RoleDescriptor var6 = (RoleDescriptor)var2.get(var5);
                boolean var7 = var6.getLinkInfo().isRoleA(var6.getName());
                ManifestServerHelper.addQueryLinkExistenceDirective((RoleDescriptor)var2.get(var5), var1, var3, var7, "REFERENTIAL_INTEGRITY_DIRECTIVE_KEY");
            }

            if (StandardPersistenceManager.REF_INTEGRITY_LOGGER.isInfoEnabled()) {
                StandardPersistenceManager.REF_INTEGRITY_LOGGER.info("ReferentialIntegrityValidator.addNonOwners(): objects=" + var1 + " roles=" + var2 + " manifest=" + var3);
            }

        }
    }

    private static class PersistInfoData {
        PersistInfo persistInfo;
        Timestamp updateStamp;
        Timestamp modifyStamp;
        int updateCount;

        private PersistInfoData() {
        }
    }

    class PagingSessionEventListener extends ServiceEventListenerAdapter {
        public PagingSessionEventListener(String var2) {
            super(var2);
        }

        public void notifyVetoableEvent(Object var1) throws WTException {
            if (var1 instanceof KeyedEvent) {
                KeyedEvent var2 = (KeyedEvent)var1;
                Object var3 = var2.getEventTarget();
                if (var3 instanceof PagingSession) {
                    if (var2.getEventType().equals("POST_DELETE")) {
                        StandardPersistenceManager.updatePagingSessionCache((PagingSession)var3);
                    }

                }
            }
        }
    }

    private class AllManagersStartedEventListener extends ServiceEventListenerAdapter {
        public AllManagersStartedEventListener(String var2) {
            super(var2);
        }

        public void notifyVetoableEvent(Object var1) throws WTException {
            if (var1 instanceof KeyedEvent) {
                KeyedEvent var2 = (KeyedEvent)var1;
                if (var2.getEventType().equals("ALL_SERVICES_STARTED")) {
                    AttributeMapInfo.setAccessControlConfigurationComplete();
                    if (DBProperties.TRACE_TIMING_ENABLED) {
                        SQLProfilingKey.register();
                    }

                    new PersistProfKey();
                }
            }

        }
    }
}
