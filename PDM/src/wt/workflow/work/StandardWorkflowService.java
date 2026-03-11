//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wt.workflow.work;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import ext.casc.system.SystemConfigurationUtil;
import org.apache.log4j.Logger;
import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.access.NotAuthorizedException;
import wt.admin.AdminDomainRef;
import wt.admin.AdministrativeDomain;
import wt.admin.AdministrativeDomainHelper;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeInvestigation;
import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeProposal;
import wt.change2.WTChangeRequest2;
import wt.clients.beans.table.WTTableModel;
import wt.clients.util.ActionLoader;
import wt.clients.workflow.worklist.WfWorkListModel;
import wt.dataservice.DSProperties;
import wt.dataservice.DataServiceFactory;
import wt.dataservice.Datastore;
import wt.dataservice.Oracle;
import wt.dataservice.SQLServer;
import wt.doc.WTDocument;
import wt.enterprise.Managed;
import wt.enterprise.URLProcessor;
import wt.epm.EPMDocument;
import wt.fc.Named;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectNoLongerExistsException;
import wt.fc.ObjectReference;
import wt.fc.ObjectVector;
import wt.fc.ObjectVectorIfc;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.PersistentReference;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.fc.batch.DeleteBatchSpec;
import wt.fc.collections.WTList;
import wt.folder.FolderServiceEvent;
import wt.htmlutil.TemplateName;
import wt.httpgw.GatewayURL;
import wt.identity.IdentityFactory;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.inf.team.ContainerTeamReference;
import wt.inf.team.ContainerTeamServiceEvent;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.notify.NotificationHelper;
import wt.notify.TemplateEmailNotification;
import wt.notify.WTDistributionList;
import wt.notify.templateProcessor.EmailTemplateNotificationRequest;
import wt.org.OrganizationServicesHelper;
import wt.org.UserNotFoundException;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.ownership.OwnershipHelper;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.PartialResultException;
import wt.pds.ReferenceJoinCondition;
import wt.pom.ObjectIsStaleException;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.pom.UniquenessException;
import wt.pom.WTConnection;
import wt.preference.PreferenceHelper;
import wt.project.ActorRole;
import wt.project.Role;
import wt.projmgmt.admin.Project2;
import wt.projmgmt.execution.ProjectWorkItem;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.ClassTableExpression;
import wt.query.ConstantExpression;
import wt.query.KeywordExpression;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionMgr;
import wt.session.SessionServerHelper;
import wt.session.SessionThread;
import wt.team.RoleHolder2;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamReference;
import wt.team.TeamServerHelper;
import wt.team.TeamTemplate;
import wt.team.TeamTemplateReference;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.util.WTStandardDateFormat;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressServerHelper;
import wt.workflow.WfException;
import wt.workflow.definer.PermissionSet;
import wt.workflow.definer.SignatureVector;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfInternalMethodTemplate;
import wt.workflow.definer.WfResourcePool;
import wt.workflow.definer.WfVariableInfo;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfEmailAttachmentType;
import wt.workflow.engine.WfEmailAttachments;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfEngineService;
import wt.workflow.engine.WfEventAuditType;
import wt.workflow.engine.WfEventHelper;
import wt.workflow.engine.WfExecutionObject;
import wt.workflow.engine.WfExternalRecipientList;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfRequester;
import wt.workflow.engine.WfRequesterActivity;
import wt.workflow.engine.WfRouterType;
import wt.workflow.engine.WfState;
import wt.workflow.engine.WfTransition;
import wt.workflow.engine.WfVariable;
import wt.workflow.robots.WfInternalMethod;

public class StandardWorkflowService extends StandardManager implements WorkflowService, WorkflowServiceSvr, Serializable {
    private static final String RESOURCE = "wt.workflow.work.workResource";
    private static final String CLASSNAME = StandardWorkflowService.class.getName();
    private static final String DELETE_COMPLETED_WORKITEMS_QUEUE_NAME = "DeleteCompletedWorkItemsQueue";
    public static final boolean DISALLOW_PM_COMPLETE_TASK;
    private ProcessingQueue deleteCompletedWorkItemsQueue;
    private static final String DEFAULT_ROLE = "ASSIGNEE";
    private static final String OVERDUE_TASK_NAME = "WfTask";
    private static final String REASSIGN_PRINCIPAL_VARIABLE_NAME = "reassignPrincipal";
    private static final String URL_PROCESSOR_METHOD = "URLTemplateAction";
    private static final String WORK_NOTIFICATION_TEMPLATE = "General";
    private static final String ROBOT_NOTIFICATION_TEMPLATE = "NotificationRobot";
    private static final char START_VARIABLE;
    private static final char END_VARIABLE;
    private static final char ESCAPE_CHARACTER = '\\';
    private static int DELETE_COMPLETED_WORKITEMS_POOL_SIZE;
    private static final boolean NO_DEDICATED_DELETE_COMPLETED_WORKITEMS;
    private static int DELETE_COMPLETED_WORKITEMS_QUEUE_INTERVAL;
    private static final Logger logger = LogR.getLogger("wt.workflow.work");
    private static final String DEFAULT_NOTIFICATION_SENDER;
    private static final boolean USE_DEFAULT_NOTIFICATION_SENDER_EMAIL;
    private static final String SERVICE_NAME = "WorkflowService";
    private static final boolean DO_DYNAMIC_UPDATE;
    private static final String DELETE_COMPLETED_WORKITEMS_PREFS;

    public StandardWorkflowService() {
    }

    /** @deprecated */
    public String getConceptualClassname() {
        return CLASSNAME;
    }

    public static void targetDeleteCompletedWorkItems(WTPrincipalReference var0, WTContainerRef var1) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.targetDeleteCompletedWorkItems");
        }

        WorkflowHelper.service.deleteCompletedWorkItems(var0, var1);
    }

    public static StandardWorkflowService newStandardWorkflowService() throws WTException {
        StandardWorkflowService var0 = new StandardWorkflowService();
        var0.initialize();
        return var0;
    }

    public QueryResult getWorkItems(WTPrincipal var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems - IN: principal = " + var1);
        }

        try {
            QuerySpec var2 = this.buildOwnerQuery(var1);
            SQLFunction var3 = this.getTruncateFunction(new ClassAttribute(WorkItem.class, "thePersistInfo.createStamp"));
            var2.appendOrderBy(new OrderBy(var3, false), new int[]{0});
            var2.appendOrderBy(WorkItem.class, "role", false);
            QueryResult var4 = PersistenceServerHelper.manager.query(var2);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItems - OUT: size = " + var4.size());
            }

            return var4;
        } catch (QueryException var5) {
            if (logger.isDebugEnabled()) {
                logger.debug(var5.getLocalizedMessage(), var5);
            }

            throw new WfException(var5, (String)null);
        } catch (WTPropertyVetoException var6) {
            if (logger.isDebugEnabled()) {
                logger.debug(var6.getLocalizedMessage(), var6);
            }

            throw new WTException(var6);
        }
    }

    public QueryResult getUncompletedWorkItems(WTPrincipal var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getUncompletedWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        return this.getUncompletedWorkItems(var1, new Integer(-1), (String)null);
    }

    public QueryResult getWorkItems(WTPrincipal var1, Role var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems - IN: principal = " + var1 + ", role = " + var2);
        }

        try {
            QuerySpec var3 = this.buildOwnerQuery(var1);
            SearchCondition var4 = this.onRole(var2);
            var3.appendAnd();
            var3.appendWhere(var4, 0);
            QueryResult var5 = PersistenceServerHelper.manager.query(var3);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItems - OUT: size = " + var5.size());
            }

            return var5;
        } catch (QueryException var6) {
            if (logger.isDebugEnabled()) {
                logger.debug(var6.getLocalizedMessage(), var6);
            }

            throw new WfException(var6, (String)null);
        }
    }

    public QueryResult getWorkItems(String var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems - IN: source = " + var1);
        }

        try {
            QuerySpec var2 = new QuerySpec(WorkItem.class);
            var2.appendWhere(this.onSource(var1), 0);
            QueryResult var3 = PersistenceServerHelper.manager.query(var2);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItems - OUT: size = " + var3.size());
            }

            return var3;
        } catch (QueryException var4) {
            if (logger.isDebugEnabled()) {
                logger.debug(var4.getLocalizedMessage(), var4);
            }

            throw new WfException(var4, (String)null);
        }
    }

    public QueryResult getWorkItems(Persistable var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems - IN: object = " + getOid(var1));
        }

        try {
            QuerySpec var2 = new QuerySpec(WorkItem.class);
            var2.appendWhere(this.onObject(var1), 0);
            QueryResult var3 = PersistenceServerHelper.manager.query(var2);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItems - OUT: size = " + var3.size());
            }

            return var3;
        } catch (QueryException var4) {
            if (logger.isDebugEnabled()) {
                logger.debug(var4.getLocalizedMessage(), var4);
            }

            throw new WfException(var4, (String)null);
        }
    }

    public QueryResult getWorkItems(Persistable var1, String var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems - IN: object = " + getOid(var1) + ", source = " + var2);
        }

        try {
            QuerySpec var3 = new QuerySpec(WorkItem.class);
            SearchCondition var4 = this.onObject(var1);
            var3.appendWhere(var4, 0);
            SearchCondition var5 = this.onSource(var2);
            var3.appendAnd();
            var3.appendWhere(var5, 0);
            QueryResult var6 = PersistenceServerHelper.manager.query(var3);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItems - OUT: size = " + var6.size());
            }

            return var6;
        } catch (QueryException var7) {
            if (logger.isDebugEnabled()) {
                logger.debug(var7.getLocalizedMessage(), var7);
            }

            throw new WfException(var7, (String)null);
        }
    }

    public QueryResult getWorkItems(Persistable var1, WTPrincipal var2, Role var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems IN:  object = " + getOid(var1) + ", principal = " + var2 + ", role = " + var3.toString());
        }

        try {
            QuerySpec var4 = this.buildOwnerQuery(var2);
            var4.appendAnd();
            SearchCondition var5 = this.onObject(var1);
            var4.appendWhere(var5, 0);
            SearchCondition var6 = this.onRole(var3);
            var4.appendAnd();
            var4.appendWhere(var6, 0);
            QueryResult var7 = PersistenceServerHelper.manager.query(var4);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItems - OUT: size = " + var7.size());
            }

            return var7;
        } catch (QueryException var8) {
            if (logger.isDebugEnabled()) {
                logger.debug(var8.getLocalizedMessage(), var8);
            }

            throw new WfException(var8, (String)null);
        }
    }

    public QueryResult getWorkItems(Persistable var1, WTPrincipal var2, String var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems: object = " + getOid(var1) + ", principal = " + var2 + ", task = " + var3);
        }

        try {
            QuerySpec var4 = this.buildOwnerQuery(var2);
            var4.appendAnd();
            SearchCondition var5 = this.onObject(var1);
            var4.appendWhere(var5, 0);
            SearchCondition var6 = this.onTask(var3);
            var4.appendAnd();
            var4.appendWhere(var6, 0);
            SearchCondition var7 = this.onIncomplete();
            var4.appendAnd();
            var4.appendWhere(var7, 0);
            QueryResult var8 = PersistenceServerHelper.manager.query(var4);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItem by object + owner + task: FOUND " + var8.size());
            }

            return var8;
        } catch (QueryException var9) {
            if (logger.isDebugEnabled()) {
                logger.debug(var9.getLocalizedMessage(), var9);
            }

            throw new WfException(var9, (String)null);
        }
    }

    public QueryResult getWorkItems(Persistable var1, WTPrincipal var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems - IN: for:" + getOid(var1) + " and " + var2);
        }

        try {
            QuerySpec var3 = this.buildOwnerQuery(var2);
            var3.appendAnd();
            SearchCondition var4 = this.onObject(var1);
            var3.appendWhere(var4, 0);
            QueryResult var5 = PersistenceServerHelper.manager.query(var3);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItems - OUT: size = " + var5.size());
            }

            return var5;
        } catch (QueryException var6) {
            if (logger.isDebugEnabled()) {
                logger.debug(var6.getLocalizedMessage(), var6);
            }

            throw new WfException(var6, (String)null);
        }
    }

    public QueryResult getWorkItems(Persistable var1, Role var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems: for: " + getOid(var1) + " & " + var2.toString());
        }

        try {
            QuerySpec var3 = new QuerySpec(WorkItem.class);
            SearchCondition var4 = this.onObject(var1);
            var3.appendWhere(var4, 0);
            SearchCondition var5 = this.onRole(var2);
            var3.appendAnd();
            var3.appendWhere(var5, 0);
            QueryResult var6 = PersistenceServerHelper.manager.query(var3);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItem by object + role: FOUND " + var6.size());
            }

            return var6;
        } catch (QueryException var7) {
            if (logger.isDebugEnabled()) {
                logger.debug(var7.getLocalizedMessage(), var7);
            }

            throw new WfException(var7, (String)null);
        }
    }

    public QueryResult getUncompletedWorkItems(Persistable var1, Role var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getUncompletedWorkItems: for: " + getOid(var1) + " & " + var2.toString());
        }

        try {
            QuerySpec var3 = new QuerySpec(WorkItem.class);
            SearchCondition var4 = this.onObject(var1);
            var3.appendWhere(var4, 0);
            SearchCondition var5 = this.onRole(var2);
            var3.appendAnd();
            var3.appendWhere(var5, 0);
            SearchCondition var6 = new SearchCondition(WorkItem.class, "completedBy", true);
            var3.appendAnd();
            var3.appendWhere(var6, 0);
            QueryResult var7 = PersistenceHelper.manager.find(var3);
            if (logger.isTraceEnabled()) {
                logger.trace("   getUncompletedWorkItem by object + role: FOUND " + var7.size());
            }

            return var7;
        } catch (QueryException var8) {
            if (logger.isDebugEnabled()) {
                logger.debug(var8.getLocalizedMessage(), var8);
            }

            throw new WfException(var8, (String)null);
        }
    }

    public QueryResult getWorkItems() throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItems - IN");
        }

        try {
            QuerySpec var1 = new QuerySpec(WorkItem.class);
            QueryResult var2 = PersistenceServerHelper.manager.query(var1);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getWorkItems: FOUND " + var2.size());
            }

            return var2;
        } catch (QueryException var3) {
            if (logger.isDebugEnabled()) {
                logger.debug(var3.getLocalizedMessage(), var3);
            }

            throw new WfException(var3, (String)null);
        }
    }

    public QueryResult getUncompletedWorkItems() throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getUncompletedWorkItems - IN");
        }

        try {
            QuerySpec var1 = new QuerySpec(WorkItem.class);
            Datastore var2 = DataServiceFactory.getDefault().getDatastore();
            SQLFunction var3 = this.getTruncateFunction(new ClassAttribute(WorkItem.class, "thePersistInfo.createStamp"));
            var1.appendOrderBy(new OrderBy(var3, false), new int[]{0});
            var1.appendOrderBy(WorkItem.class, "role", false);
            SearchCondition var4 = new SearchCondition(WorkItem.class, "completedBy", true);
            var1.appendWhere(var4, 0);
            QueryResult var5 = PersistenceHelper.manager.find(var1);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getUncompletedWorkItems: FOUND " + var5.size());
            }

            return var5;
        } catch (QueryException var6) {
            if (logger.isDebugEnabled()) {
                logger.debug(var6.getLocalizedMessage(), var6);
            }

            throw new WfException(var6, (String)null);
        } catch (WTPropertyVetoException var7) {
            if (logger.isDebugEnabled()) {
                logger.debug(var7.getLocalizedMessage(), var7);
            }

            throw new WTException(var7);
        }
    }

    public QueryResult getUncompletedWorkItems(Persistable var1, String var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getUncompletedWorkItems: object = " + getOid(var1) + ", task = " + var2);
        }

        try {
            QuerySpec var3 = new QuerySpec(WorkItem.class);
            SearchCondition var4 = this.onObject(var1);
            var3.appendWhere(var4, 0);
            SearchCondition var5 = this.onTask(var2);
            var3.appendAnd();
            var3.appendWhere(var5, 0);
            SearchCondition var6 = new SearchCondition(WorkItem.class, "completedBy", true);
            var3.appendAnd();
            var3.appendWhere(var6, 0);
            QueryResult var7 = PersistenceHelper.manager.find(var3);
            if (logger.isTraceEnabled()) {
                logger.trace("   SgetUncompletedWorkItem by object + task: FOUND " + var7.size());
            }

            return var7;
        } catch (QueryException var8) {
            if (logger.isDebugEnabled()) {
                logger.debug(var8.getLocalizedMessage(), var8);
            }

            throw new WfException(var8, (String)null);
        }
    }

    public Vector getAssignees(WfAssignedActivity var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getAssignees - IN: activity = " + var1.getName());
        }

        Vector var2 = new Vector();
        WfAssignment var3 = null;
        Vector var4 = null;
        Enumeration var5 = null;
        Enumeration var6 = var1.getAssignments();

        while(var6.hasMoreElements()) {
            var3 = (WfAssignment)var6.nextElement();
            var4 = var3.getPrincipals();
            var5 = var4.elements();

            while(var5.hasMoreElements()) {
                var2.addElement(var5.nextElement());
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.getAssignees - OUT: size = " + var2.size());
        }

        return var2;
    }

    private static boolean canCompleteWorkItem(WorkItem var0, WTPrincipalReference var1) throws WTException {
        if (logger.isDebugEnabled()) {
            logger.debug("=> StandardWorkflowService.canCompleteWorkItem " + var1);
        }

        WTPrincipalReference var2 = var0.getOwnership().getOwner();
        if (var1.equals(var2)) {
            if (logger.isDebugEnabled()) {
                logger.debug("=> StandardWorkflowService.canCompleteWorkItem - Assignee can complete " + var0);
            }

            return true;
        } else {
            WTPrincipal var3 = var2.getPrincipal();
            if (var2 != null && var3 instanceof WTGroup && ((WTGroup)var3).isMember(var1.getPrincipal())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("=> StandardWorkflowService.canCompleteWorkItem - Owner group member can complete " + var0);
                }

                return true;
            } else {
                boolean var4 = false;
                boolean var5 = SessionServerHelper.manager.setAccessEnforced(false);

                boolean var8;
                try {
                    WfExecutionObject var6 = (WfExecutionObject)ObjectReference.newObjectReference(var0.getSource()).getObject();
                    WTContainerRef var7 = var6.getContainerReference();
                    var4 = WTContainerHelper.service.isAdministrator(var7, var1.getPrincipal());
                    if (logger.isDebugEnabled()) {
                        logger.debug("=> StandardWorkflowService.canCompleteWorkItem - principal is admin = " + var4 + ", property value = " + DISALLOW_PM_COMPLETE_TASK);
                    }

                    if (var4) {
                        if (var7.getReferencedClass().isAssignableFrom(Project2.class) && DISALLOW_PM_COMPLETE_TASK) {
                            var8 = false;
                            return var8;
                        }

                        var8 = true;
                        return var8;
                    }

                    var8 = false;
                } finally {
                    SessionServerHelper.manager.setAccessEnforced(var5);
                }

                return var8;
            }
        }
    }

    public void workComplete(WorkItem var1, WTPrincipalReference var2, Vector var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.workComplete (workitem) - IN: principal = " + var2);
        }

        boolean var4 = false;
        WfAssignedActivity var5 = null;
        WfAssignment var6 = null;
        Transaction var7 = new Transaction();

        try {
            var7.start();
            var1 = (WorkItem)WfEngineServerHelper.lock(var1);
            if (var1.isComplete()) {
                throw new WTException("wt.workflow.work.workResource", "34", (Object[])null);
            }

            if (var3 == null) {
                Vector var8 = (Vector)MethodContext.getContext().get("eventList");
                if (var8 != null && !var8.isEmpty()) {
                    var3 = var8;
                    MethodContext.getContext().remove("eventList");
                }
            }

            if (!canCompleteWorkItem(var1, var2)) {
                String var27 = WTMessage.getLocalizedMessage("wt.access.accessResource", "19", (Object[])null, SessionHelper.getLocale());
                throw new NotAuthorizedException(var27);
            }

            var1.setComplete(((WTPrincipal)var2.getObject()).getName());
            PersistenceHelper.manager.save(var1);
            boolean var26 = SessionServerHelper.manager.setAccessEnforced(false);

            try {
                WTContainerRef var9 = ((WfExecutionObject)var1.getSource().getObject()).getContainerReference();
                WorkflowServerHelper.service.queueDeletionOfCompletedWorkItems(var2, var9, true);
                var6 = (WfAssignment)var1.getParentWA().getObject();
                var6.createBallot(var2, var3);
                var6 = (WfAssignment)PersistenceHelper.manager.refresh(var6);
                var6.checkComplete();
                PersistenceHelper.manager.modify(var6);
                var5 = (WfAssignedActivity)var1.getSource().getObject();
                var5 = (WfAssignedActivity)var5.evaluateExpression(WfTransition.COMPLETE_TASK);
                if (var5.isComplete() && !WfState.CLOSED.includes(var5.getState())) {
                    WfAssignedActivityTemplate var10 = (WfAssignedActivityTemplate)var5.getTemplate().getObject();
                    WfRouterType var11 = var10.getRouterType();
                    if (WfDefinerHelper.service.getRouterExpression(var10) != null || !var11.equals(WfRouterType.MANUAL) && !var11.equals(WfRouterType.MANUAL_EXCLUSIVE)) {
                        WfEngineHelper.service.changeState(var5, WfTransition.COMPLETE);
                    } else {
                        var3 = var5.getAllEvents();
                        WfEngineHelper.service.complete(var5, var3);
                    }
                }
            } finally {
                SessionServerHelper.manager.setAccessEnforced(var26);
            }

            var7.commit();
            var7 = null;
        } catch (WTPropertyVetoException var23) {
            if (logger.isDebugEnabled()) {
                logger.debug(var23.getLocalizedMessage(), var23);
            }

            throw new WfException(var23);
        } catch (WTException var24) {
            if (logger.isDebugEnabled()) {
                logger.debug(var24.getLocalizedMessage(), var24);
            }

            throw var24;
        } finally {
            if (var7 != null) {
                var7.rollback();
            }

        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.workComplete - OUT");
        }

    }

    public void workComplete(ObjectReference var1, WTPrincipalReference var2, Vector var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.workComplete (ref) - IN: principal = " + var2);
        }

        WorkItem var4 = (WorkItem)var1.getObject();
        this.workComplete(var4, var2, var3);
        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.workComplete - OUT");
        }

    }

    /** @deprecated */
    public void markWorkItemComplete(WorkItem var1, WTPrincipalReference var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.markWorkItemComplete - IN: completed by = " + var2.getName());
        }

        if (var1 != null) {
            Transaction var3 = new Transaction();

            try {
                var3.start();
                var1.setComplete(((WTPrincipal)var2.getObject()).getName());
                PersistenceHelper.manager.save(var1);
                var3.commit();
                var3 = null;
            } catch (WTPropertyVetoException var10) {
                if (logger.isDebugEnabled()) {
                    logger.debug(var10.getLocalizedMessage(), var10);
                }

                throw new WfException(var10);
            } catch (PersistenceException var11) {
                if (logger.isDebugEnabled()) {
                    logger.debug(var11.getLocalizedMessage(), var11);
                }

                throw new WfException(var11);
            } catch (WTException var12) {
                if (logger.isDebugEnabled()) {
                    logger.debug(var12.getLocalizedMessage(), var12);
                }

                throw new WfException(var12);
            } finally {
                if (var3 != null) {
                    var3.rollback();
                }

            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.markWorkItemComplete - OUT");
        }

    }

    public void markWorkItemComplete(Persistable var1, WTPrincipal var2, Role var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.markWorkItemComplete - IN: on " + getOid(var1) + " for " + var2.getName() + " with role " + var3.getDisplay());
        }

        WorkItem var4 = this.getWorkItem(var1, var2, var3);
        if (var4 != null) {
            this.workComplete(ObjectReference.newObjectReference(var4), WTPrincipalReference.newWTPrincipalReference(var2), (Vector)null);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.markWorkItemComplete OUT");
        }

    }

    public void markWorkItemComplete(Persistable var1, WTPrincipal var2, String var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.markWorkItemComplete - IN: on " + getOid(var1) + " for " + var2.getName() + " with task " + var3);
        }

        WorkItem var4 = null;
        QueryResult var5 = this.getWorkItems(var1, var2, var3);
        if (var5.hasMoreElements()) {
            var4 = (WorkItem)var5.nextElement();
        }

        if (var4 != null) {
            this.workComplete(ObjectReference.newObjectReference(var4), WTPrincipalReference.newWTPrincipalReference(var2), (Vector)null);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.markWorkItemComplete OUT");
        }

    }

    public void markWorkItemIncomplete(Persistable var1, WTPrincipal var2, Role var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> markWorkItemIncomplete on " + getOid(var1) + " for " + var2.getName() + " with role " + var3.getDisplay());
        }

        WorkItem var4 = this.getWorkItem(var1, var2, var3);
        if (var4 != null) {
            Transaction var5 = new Transaction();

            try {
                var5.start();
                var4.setIncomplete();
                PersistenceServerHelper.manager.update(var4);
                var5.commit();
                var5 = null;
            } catch (PersistenceException var11) {
                if (logger.isDebugEnabled()) {
                    logger.debug(var11.getLocalizedMessage(), var11);
                }

                throw new WfException(var11);
            } catch (WTException var12) {
                if (logger.isDebugEnabled()) {
                    logger.debug(var12.getLocalizedMessage(), var12);
                }

                throw new WfException(var12);
            } finally {
                if (var5 != null) {
                    var5.rollback();
                }

            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   markWorkItemIncomplete - OUT");
        }

    }

    public void acceptAssignment(WorkItem var1, WTUser var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.acceptAssignment: principal = " + var2.getName());
        }

        WfAssignedActivity var3 = (WfAssignedActivity)var1.getSource().getObject();
        var3.acceptAssignment(var1, var2);
        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.acceptAssignment - OUT");
        }

    }

    public void delegate(WorkItem var1, WTPrincipal var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.delegate " + getOid(var1) + " from " + var1.getOwnership().getOwner().getName() + " to " + var2.getName());
        }

        Boolean var3 = (Boolean)MethodContext.getContext().get("ReassignWorkitemsByPass");
        String var4 = null;
        if (MethodContext.getContext().get("PastDeadlineReassignComment") != null) {
            var4 = (String)MethodContext.getContext().get("PastDeadlineReassignComment");
            MethodContext.getContext().remove("PastDeadlineReassignComment");
        }

        if (var3 != null && var3.equals(Boolean.TRUE)) {
            this.delegate(var1, var2, true, var4);
        } else {
            this.delegate(var1, var2, false, var4);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   delegate - OUT");
        }

    }

    public void delegate(WorkItem var1, WTPrincipal var2, String var3) throws WTException {
        this.delegate(var1, var2, true, var3);
    }

    public void sendNotification(String var1, Vector var2, Vector var3, Vector var4, ObjectReference var5) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.sendNotification: message = " + var1);
        }

        WfProcess var6 = this.getParentProcess(var5);
        WTDistributionList var7 = this.buildDistributionList(var6, var2, var3, var4);
        TemplateEmailNotification var8 = this.createNotification(var7, (String)null, var1, var6, var5);
        NotificationHelper.manager.send(var8);
        if (logger.isTraceEnabled()) {
            logger.trace("   sendNotification - OUT");
        }

    }

    public void sendNotification(String var1, String var2, Vector var3, Vector var4, Vector var5, Vector var6, Vector var7, ObjectReference var8) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.sendNotification: subject = " + var1);
        }

        WfExternalRecipientList var9 = new WfExternalRecipientList();
        this.sendNotification(var1, var2, var3, var4, var5, var6, var7, var8, "NotificationRobot", var9, (WfEmailAttachments)null);
        if (logger.isTraceEnabled()) {
            logger.trace("   sendNotification - OUT");
        }

    }

    public void checkoutTo(WTObject var1, WTPrincipalReference var2, Role var3, ActorRole var4, ObjectReference var5) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.checkoutTo: " + var2 + ", role = " + var3 + ", actorRole = " + var4 + ", robot = " + var5.getKey());
        }

        SessionContext var6 = SessionContext.newContext();

        try {
            WTPrincipal var7 = this.getCheckoutUser(var2, var3, var4, var5);
            SessionMgr.setPrincipal(var7.getName());
            WorkInProgressServerHelper.service.checkout(var1);
        } catch (WTPropertyVetoException var11) {
            if (logger.isDebugEnabled()) {
                logger.debug(var11.getLocalizedMessage(), var11);
            }

            throw new WTException(var11);
        } finally {
            if (var6 != null) {
                SessionContext.setContext(var6);
            }

        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.checkoutTo - OUT");
        }

    }

    public WfWorkListModel createWorkListModel(String var1) throws WTException {
        return this.createWorkListModel(var1, (Vector)null, (Vector)null);
    }

    public WfWorkListModel createWorkListModel(String var1, Vector var2, Vector var3) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.createWorkListModel: " + var1);
        }

        WfWorkListModel var4 = new WfWorkListModel();
        ActionEvent var5 = new ActionEvent(var4, 1001, var1);
        (new ActionLoader()).actionPerformed(var5);

        try {
            if (var2 != null && !var2.isEmpty()) {
                var4.setGroupBy(var2);
            }

            Vector var6 = new Vector();

            for(int var7 = 0; var3 != null && var7 < var3.size(); ++var7) {
                var6.addElement(var3.elementAt(var7));
            }

            if (var6 != null && !var6.isEmpty()) {
                var4.setSortBy(var6);
            }
        } catch (PropertyVetoException var8) {
            if (logger.isDebugEnabled()) {
                logger.debug(var8.getLocalizedMessage(), var8);
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.createWorkListModel - OUT");
        }

        return var4;
    }

    public void createOverdueWorkItem(WfExecutionObject var1) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.createOverdueWorkItem - IN: obj = " + var1.getName());
        }

        WTPrincipal var2 = null;
        Enumeration var3 = WfEngineHelper.service.getResponsibleParticipants(var1);
        Transaction var4 = new Transaction();

        try {
            var4.start();

            while(var3.hasMoreElements()) {
                var2 = ((WTPrincipalReference)var3.nextElement()).getPrincipal();
                WorkItem var5 = WorkItem.newWorkItem(var2);
                PersistenceServerHelper.manager.insert(var5);
                Properties var6 = new Properties();
                var6.put("action", "WfTask");
                var6.put("oid", (new ReferenceFactory()).getReferenceString(var5));
                String var7 = Long.toString(var1.getPriority());

                try {
                    var5.setTaskURLPathInfo(GatewayURL.buildPathInfo(URLProcessor.class.getName(), "URLTemplateAction", (String)null, var6));
                    var5.setSource(ObjectReference.newObjectReference(var1));
                    var5.setPriority(var7);
                    var5.setRequired(false);
                } catch (WTPropertyVetoException var12) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(var12.getLocalizedMessage(), var12);
                    }

                    throw new WTException(var12);
                }

                PersistenceServerHelper.manager.update(var5);
                PersistenceServerHelper.manager.insert(OverdueWorkItemLink.newOverdueWorkItemLink(var1, var5));
            }

            var4.commit();
            var4 = null;
            if (logger.isTraceEnabled()) {
                logger.trace("createOverdueWorkItem - OUT");
            }
        } finally {
            if (var4 != null) {
                var4.rollback();
            }

        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.createOverdueWorkItem - OUT");
        }

    }

    public void reassignActivity(WfAssignedActivity var1) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.reassignActivity - IN: activity = " + var1.getName());
        }

        Object var2 = var1.getContext().getValue("reassignPrincipal");
        if (var2 != null && var2 instanceof WTPrincipal) {
            this.reassignActivity(var1, (WTPrincipal)var2);
        } else {
            this.reassignActivity(var1, WfEngineHelper.service.getResponsible(var1));
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.reassignActivity - OUT");
        }

    }

    public void reassignActivity(WfAssignedActivity var1, WTPrincipal var2) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.reassignActivity - IN: activity = " + var1.getName() + ", principal = " + var2.getName());
        }

        Enumeration var3 = var1.getAssignments();
        WfAssignment var4 = (WfAssignment)var3.nextElement();
        if (var3.hasMoreElements()) {
            throw new WfException("wt.workflow.work.workResource", "38", (Object[])null);
        } else {
            QueryResult var5 = PersistenceHelper.manager.navigate(var4, "workItem", WorkItemLink.class);

            while(var5.hasMoreElements()) {
                WorkItem var6 = (WorkItem)var5.nextElement();
                this.delegate(var6, var2);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.reassignActivity - OUT");
            }

        }
    }

    public WTTableModel createTableModel(String var1) throws WTException {
        return null;
    }

    public WTTableModel createTableModel(String var1, Vector var2, Vector var3) throws WTException {
        return null;
    }

    public WTTableModel createTableModel(String var1, Vector var2, Vector var3, WTObject var4) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.createTableModel - IN: load action = " + var1);
        }

        WTTableModel var5 = new WTTableModel();
        Hashtable var6 = new Hashtable();
        var6.put("model", var5);
        var6.put("contextObject", var4);
        ActionEvent var7 = new ActionEvent(var6, 1001, var1);
        (new ActionLoader()).actionPerformed(var7);

        try {
            if (var2 != null && !var2.isEmpty()) {
                var5.setGroupBy(var2);
            }

            Vector var8 = new Vector();

            for(int var9 = 0; var3 != null && var9 < var3.size(); ++var9) {
                var8.addElement(var3.elementAt(var9));
            }

            if (var8 != null && !var8.isEmpty()) {
                var5.setSortBy(var8);
            }
        } catch (PropertyVetoException var10) {
            if (logger.isDebugEnabled()) {
                logger.debug(var10.getLocalizedMessage(), var10);
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.createTableModel - OUT");
        }

        return var5;
    }

    public boolean tally(WfAssignedActivity var1, WfTallyType var2, String var3, int var4, String var5) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.tally - IN: activity = " + var1.getName() + ", operator = " + var3 + ", value = " + var4 + ", event = " + var5);
        }

        boolean var6 = false;

        try {
            var6 = var1.tally(var1, var2, var3, var4, var5);
        } catch (WfException var8) {
            if (logger.isDebugEnabled()) {
                logger.debug(var8.getLocalizedMessage(), var8);
                logger.debug("Exception thrown on forward to WfAssignedActivity.tally.");
            }

            throw new WTException(var8);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.tally - OUT: " + var6);
        }

        return var6;
    }

    public String replaceVariables(String var1, ProcessData var2) throws WTException {
        return this.replaceVariables(var1, var2, false, false);
    }

    public QueryResult getUncompletedWorkItems(WTContainerRef var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getUncompletedWorkItems: " + var1.getName());
        }

        WTContainer var2 = (WTContainer)var1.getObject();
        AdministrativeDomain var3 = var2.getSystemDomain();
        new Vector();
        new Vector();

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Proj2 system domain: " + AdminDomainRef.newAdminDomainRef(var3));
            }

            QuerySpec var6 = new QuerySpec();
            var6.setAdvancedQueryEnabled(true);
            int var7 = var6.appendClassList(WfAssignedActivity.class, false);
            new ClassAttribute(WfAssignedActivity.class, "thePersistInfo.theObjectIdentifier.id");
            var6.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", var7, false);
            SearchCondition var9 = new SearchCondition(WfAssignedActivity.class, "domainRef.key", "=", getOid(var3));
            var6.appendWhere(var9, 0);
            SubSelectExpression var10 = new SubSelectExpression(var6);
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs1: " + var6);
            }

            QuerySpec var11 = new QuerySpec(WorkItem.class);
            var11.setAdvancedQueryEnabled(true);
            ClassAttribute var12 = new ClassAttribute(WorkItem.class, "source.key.id");
            SearchCondition var13 = new SearchCondition(WorkItem.class, "completedBy", true);
            var11.appendWhere(var13, 0);
            var11.appendAnd();
            SearchCondition var14 = new SearchCondition(var12, "IN", var10);
            var11.appendWhere(var14, 0);
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs2: " + var11);
            }

            QueryResult var15 = PersistenceHelper.manager.find(var11);
            if (logger.isTraceEnabled()) {
                logger.trace("   getUncompletedWorkItem by project: FOUND " + var15.size());
            }

            return var15;
        } catch (QueryException var16) {
            if (logger.isDebugEnabled()) {
                logger.debug(var16.getLocalizedMessage(), var16);
            }

            throw new WfException(var16, (String)null);
        }
    }

    public QueryResult getUncompletedWorkItems(Project2 var1) throws WTException {
        WTContainerRef var2 = WTContainerRef.newWTContainerRef(var1);
        return this.getUncompletedWorkItems(var2);
    }

    public QueryResult getUncompletedWorkItems(WTPrincipal var1, WTContainerRef var2) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> getUncompletedWorkItems: " + var1.getName() + ", context = " + var2.getName());
        }

        WTContainer var3 = (WTContainer)var2.getObject();
        AdministrativeDomain var4 = var3.getSystemDomain();
        new Vector();
        new Vector();

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Proj2 system domain: " + AdminDomainRef.newAdminDomainRef(var4));
            }

            QuerySpec var7 = new QuerySpec();
            var7.setAdvancedQueryEnabled(true);
            int var8 = var7.appendClassList(WfAssignedActivity.class, false);
            new ClassAttribute(WfAssignedActivity.class, "thePersistInfo.theObjectIdentifier.id");
            var7.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", var8, false);
            SearchCondition var10 = new SearchCondition(WfAssignedActivity.class, "domainRef.key", "=", getOid(var4));
            var7.appendWhere(var10, 0);
            SubSelectExpression var11 = new SubSelectExpression(var7);
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs1: " + var7);
            }

            QuerySpec var12 = new QuerySpec(WorkItem.class);
            var12.setAdvancedQueryEnabled(true);
            ClassAttribute var13 = new ClassAttribute(WorkItem.class, "source.key.id");
            SearchCondition var14 = new SearchCondition(WorkItem.class, "completedBy", true);
            var12.appendWhere(var14, 0);
            var12.appendAnd();
            SearchCondition var15 = new SearchCondition(WorkItem.class, "ownership.owner.key", "=", getOid(var1));
            var12.appendWhere(var15, 0);
            var12.appendAnd();
            SearchCondition var16 = new SearchCondition(var13, "IN", var11);
            var12.appendWhere(var16, 0);
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs2: " + var12);
            }

            QueryResult var17 = PersistenceHelper.manager.find(var12);
            if (logger.isTraceEnabled()) {
                logger.trace("   getUncompletedWorkItem by project: FOUND " + var17.size());
            }

            return var17;
        } catch (QueryException var18) {
            if (logger.isDebugEnabled()) {
                logger.debug(var18.getLocalizedMessage(), var18);
            }

            throw new WfException(var18, (String)null);
        }
    }

    public QueryResult getUncompletedWorkItems(WTPrincipal var1, Project2 var2) throws WTException {
        WTContainerRef var3 = WTContainerRef.newWTContainerRef(var2);
        return this.getUncompletedWorkItems(var1, var3);
    }

    public void doDynamicUpdate(TeamReference var1) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("WorkflowService.doDynamicUpdate team - IN");
        }

        if (DO_DYNAMIC_UPDATE && var1 != null) {
            Enumeration var2 = WfEngineServerHelper.service.getAssociatedProcesses(var1, WfState.OPEN_RUNNING);

            while(var2.hasMoreElements()) {
                WfProcess var3 = (WfProcess)var2.nextElement();
                this.doDynamicUpdate(var3);
            }
        }

    }

    public void doDynamicUpdate(WfProcess var1) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("WorkflowService.doDynamicUpdate process - IN");
        }

        if (DO_DYNAMIC_UPDATE && var1 != null) {
            Enumeration var2 = this.getAssignedActivities(var1, WfState.OPEN_RUNNING);

            while(var2.hasMoreElements()) {
                WfAssignedActivity var3 = (WfAssignedActivity)var2.nextElement();
                var3.recreateWorkAssignment();
            }

            try {
                Vector var7 = this.getRequesterActivities(var1, WfState.OPEN_RUNNING, (WfBlock)null);
                Enumeration var4 = var7.elements();

                while(var4.hasMoreElements()) {
                    Object var5 = var4.nextElement();
                    if (var5 instanceof WfBlock) {
                        this.handleBlockAndAssignedActivities((WfBlock)var5, WfState.OPEN_RUNNING);
                    }
                }
            } catch (WTException var6) {
                logger.error("WorkflowService.doDynamicUpdate process - Exception in Handling Block Activites.");
                throw var6;
            }
        }

    }

    public boolean isOffered(WorkItem var1) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.isOffered - IN");
        }

        WfAssignment var2 = (WfAssignment)var1.getParentWA().getObject();
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.isOffered ::  - OUT");
        }

        return var2.isOffered();
    }

    public QueryResult getUncompletedWorkItems(WTPrincipal var1, WTContainerRef var2, Integer var3, String var4) throws WTException, WfException {
        return null;
    }

    public QueryResult getUncompletedWorkItems(WTPrincipal var1, Integer var2, String var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getUncompletedWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        try {
            SQLFunction var4 = this.getTruncateFunction(new ClassAttribute(WorkItem.class, "thePersistInfo.createStamp"));
            QuerySpec var5 = this.buildOwnerQuery(var1);
            var5.appendOrderBy(WorkItem.class, "role", false);
            SearchCondition var6 = new SearchCondition(WorkItem.class, "completedBy", true);
            var5.appendAnd();
            var5.appendWhere(var6, 0);
            if (var3 != null) {
                boolean var7 = false;
                if (var3.equals("thePersistInfo.modifyStamp")) {
                    var7 = true;
                }

                OrderBy var8 = new OrderBy(new ClassAttribute(WorkItem.class, var3), var7);
                var5.appendOrderBy(var8, 0);
            } else {
                var5.appendOrderBy(new OrderBy(var4, false), new int[]{0});
            }

            if (var2 == null) {
                var2 = new Integer(-1);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("SWS.getUncompletedWorkItems - query_limit = " + var2);
            }

            int var12 = var2;
            if (var12 > 0) {
                var5.setAdvancedQueryEnabled(true);
                var5.setQueryLimit(var12);
            }

            QueryResult var13 = PersistenceHelper.manager.find(var5);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getUncompletedWorkItems - OUT: size = " + var13.size());
            }

            return var13;
        } catch (PartialResultException var9) {
            if (logger.isDebugEnabled()) {
                logger.debug(var9.getLocalizedMessage(), var9);
            }

            return var9.getQueryResult();
        } catch (QueryException var10) {
            if (logger.isDebugEnabled()) {
                logger.debug(var10.getLocalizedMessage(), var10);
            }

            throw new WfException(var10, (String)null);
        } catch (WTPropertyVetoException var11) {
            if (logger.isDebugEnabled()) {
                logger.debug(var11.getLocalizedMessage(), var11);
            }

            throw new WTException(var11);
        }
    }

    public QueryResult getUncompletedWorkItems(Integer var1, String var2) throws WTException, WfException {
        return null;
    }

    public QueryResult getCompletedWorkItems(WTPrincipal var1, WTContainerRef var2, boolean var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> getCompletedWorkItems: " + var1.getName() + ", context = " + var2.getName());
        }

        WTContainer var4 = (WTContainer)var2.getObject();
        AdministrativeDomain var5 = var4.getSystemDomain();

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Container system domain: " + AdminDomainRef.newAdminDomainRef(var5));
            }

            QuerySpec var6 = new QuerySpec();
            var6.setAdvancedQueryEnabled(true);
            int var7 = var6.appendClassList(WfAssignedActivity.class, false);

            try {
                var6.setDescendantQuery(false);
            } catch (WTPropertyVetoException var19) {
                if (logger.isDebugEnabled()) {
                    logger.debug(var19.getLocalizedMessage(), var19);
                }
            }

            new ClassAttribute(WfAssignedActivity.class, "thePersistInfo.theObjectIdentifier.id");
            var6.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", var7, false);
            SearchCondition var9 = new SearchCondition(WfAssignedActivity.class, "domainRef.key", "=", getOid(var5));
            var6.appendWhere(var9, 0);
            SubSelectExpression var10 = new SubSelectExpression(var6);
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs1: " + var6);
            }

            QuerySpec var11 = new QuerySpec(WorkItem.class);

            try {
                var11.setDescendantQuery(false);
            } catch (WTPropertyVetoException var18) {
                if (logger.isDebugEnabled()) {
                    logger.debug(var18.getLocalizedMessage(), var18);
                }
            }

            var11.setAdvancedQueryEnabled(true);
            ClassAttribute var12 = new ClassAttribute(WorkItem.class, "source.key.id");
            SearchCondition var13 = new SearchCondition(WorkItem.class, "completedBy", false);
            var11.appendWhere(var13, 0);
            var11.appendAnd();
            SearchCondition var14 = new SearchCondition(WorkItem.class, "ownership.owner.key", "=", getOid(var1));
            var11.appendWhere(var14, 0);
            var11.appendAnd();
            SearchCondition var15 = new SearchCondition(var12, "IN", var10);
            var11.appendWhere(var15, 0);
            OrderBy var16 = new OrderBy(new ClassAttribute(WorkItem.class, "thePersistInfo.modifyStamp"), !var3);
            var11.appendOrderBy(var16, 0);
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs2: " + var11);
            }

            QueryResult var17 = PersistenceHelper.manager.find(var11);
            if (logger.isTraceEnabled()) {
                logger.trace("   getCompletedWorkItem by principal, container: FOUND " + var17.size());
            }

            return var17;
        } catch (QueryException var20) {
            if (logger.isDebugEnabled()) {
                logger.debug(var20.getLocalizedMessage(), var20);
            }

            throw new WfException(var20, (String)null);
        }
    }

    public QueryResult getCompletedWorkItems(WTPrincipal var1, WTContainerRef var2) throws WfException, WTException {
        logger.debug("getCompletedWorkItems >> principal: " + var1.getName() + ", context = " + var2.getName());
        WTContainer var3 = (WTContainer)var2.getObject();
        AdministrativeDomain var4 = var3.getSystemDomain();

        try {
            QuerySpec var5 = new QuerySpec();
            int var6 = var5.appendClassList(WorkItem.class, false);
            int var7 = var5.appendClassList(WfAssignedActivity.class, false);
            var5.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", var6, false);
            var5.appendSelectAttribute("thePersistInfo.modifyStamp", var6, false);
            this.addWhereExpressionForCompletedWorkItems(var1, var4, var5, var6, var7);
            OrderBy var8 = new OrderBy(new ClassAttribute(WorkItem.class, "thePersistInfo.modifyStamp"), true);
            var5.appendOrderBy(var8, new int[]{var6});
            logger.debug("getCompletedWorkItem >> Here's the qs: " + var5);
            QueryResult var9 = PersistenceHelper.manager.find(var5);
            logger.debug("getCompletedWorkItem >> size of query result: " + var9.size());
            return var9;
        } catch (QueryException var10) {
            logger.error(var10.getLocalizedMessage(), var10);
            throw new WfException(var10, (String)null);
        }
    }

    public QueryResult getCompletedWorkItemsOfClosedProcesses(WTPrincipal var1, WTContainerRef var2, Timestamp var3) throws WTException, WfException {
        logger.debug("getCompletedWorkItemsOfCompletedProcesses >> principal = " + var1.getName() + ", context = " + var2.getName() + ", closedTimeStamp = " + var3);
        WTContainer var4 = (WTContainer)var2.getObject();
        AdministrativeDomain var5 = var4.getSystemDomain();

        try {
            QuerySpec var6 = new QuerySpec();
            int var7 = var6.appendClassList(WorkItem.class, false);
            int var8 = var6.appendClassList(WfAssignedActivity.class, false);
            int var9 = var6.appendClassList(WfProcess.class, false);
            var6.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", var7, false);
            this.addWhereExpressionForCompletedWorkItems(var1, var5, var6, var7, var8);
            var6.appendAnd();
            var6.appendOpenParen();
            WfState var10 = WfState.CLOSED;
            Enumeration var11 = var10.getSubstates();
            WfState var12 = (WfState)var11.nextElement();
            var6.appendWhere(new SearchCondition(WfProcess.class, "state", "=", var12), new int[]{var9});

            while(var11.hasMoreElements()) {
                var6.appendOr();
                var12 = (WfState)var11.nextElement();
                var6.appendWhere(new SearchCondition(WfProcess.class, "state", "=", var12), new int[]{var9});
            }

            var6.appendCloseParen();
            if (var3 != null) {
                var6.appendAnd();
                SearchCondition var13 = new SearchCondition(WorkItem.class, "thePersistInfo.modifyStamp", "<", var3);
                var6.appendWhere(var13, new int[]{var7});
            }

            ReferenceJoinCondition var16 = new ReferenceJoinCondition(var8, var9, "parentProcessRef");
            var6.appendCondition(var16);
            logger.debug("getCompletedWorkItemsOfCompletedProcesses >> Here's qs: " + var6);
            QueryResult var14 = PersistenceHelper.manager.find(var6);
            logger.debug("getCompletedWorkItemsOfCompletedProcesses >> work items FOUND >> size " + var14.size());
            return var14;
        } catch (QueryException var15) {
            logger.error("getCompletedWorkItemsOfCompletedProcesses >> Error >> " + var15.getLocalizedMessage(), var15);
            throw new WfException(var15, (String)null);
        }
    }

    private QuerySpec addWhereExpressionForCompletedWorkItems(WTPrincipal var1, AdministrativeDomain var2, QuerySpec var3, int var4, int var5) throws WTException {
        ReferenceJoinCondition var6 = new ReferenceJoinCondition(var4, var5, "source");
        var3.appendCondition(var6);
        SearchCondition var7 = new SearchCondition(WorkItem.class, "ownership.owner.key", "=", getOid(var1));
        var3.appendWhere(var7, new int[]{var4});
        var3.appendAnd();
        SearchCondition var8 = new SearchCondition(WorkItem.class, "completedBy", false);
        var3.appendWhere(var8, new int[]{var4});
        var3.appendAnd();
        SearchCondition var9 = new SearchCondition(WfAssignedActivity.class, "domainRef.key", "=", getOid(var2));
        var3.appendWhere(var9, new int[]{var5});
        return var3;
    }

    public void deleteCompletedWorkItems(WTPrincipalReference var1, WTContainerRef var2) throws WTException, WfException {
        boolean var4 = false;
        logger.debug("StandardWorkflowService.deleteCompletedWorkItems()==> In  principalRef=" + var1.getName() + " containeRef=" + var2.getName() + " Start time=" + System.currentTimeMillis());
        boolean var6 = SessionServerHelper.manager.setAccessEnforced(false);
        WTContainer var7 = null;

        try {
            var7 = var2.getReferencedContainer();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(var6);
        }

        String var5 = (String)PreferenceHelper.service.getValue(DELETE_COMPLETED_WORKITEMS_PREFS, var7, (WTUser)var1.getPrincipal());
        logger.debug("deleteCompletedWorkItems >> DELETE_COMPLETED_WORKITEMS_PREFS=" + var5);
        if (!var5.equalsIgnoreCase("NO")) {
            QueryResult var3;
            if (var5.equalsIgnoreCase("ALL")) {
                var3 = this.getCompletedWorkItemsOfClosedProcesses(var1.getPrincipal(), var2, (Timestamp)null);
            } else {
                int var14;
                try {
                    var14 = Integer.parseInt(var5);
                } catch (NumberFormatException var12) {
                    logger.error("ERROR: DELETE_COMPLETED_WORKITEMS_PREFS value " + var5 + " is invalid. Valid values are ALL, NO or integer number. Completed Workitems not deleted");
                    logger.error(var12.getLocalizedMessage(), var12);
                    return;
                }

                if (var14 > 0) {
                    QueryResult var8 = this.getCompletedWorkItems(var1.getPrincipal(), var2);
                    if (var8.size() < 2 * var14) {
                        logger.debug("deleteCompletedWorkitems >> No workitems deleted " + var8.size() + "<" + 2 * var8.size());
                        return;
                    }

                    Timestamp var9 = (Timestamp)((Object[])((Object[])var8.getObjectVectorIfc().getVector().get(var14 - 1)))[1];
                    var3 = this.getCompletedWorkItemsOfClosedProcesses(var1.getPrincipal(), var2, var9);
                } else {
                    var3 = this.getCompletedWorkItemsOfClosedProcesses(var1.getPrincipal(), var2, (Timestamp)null);
                }
            }

            if (var3.size() > 0) {
                this.deleteWorkItemsInBatch(var3);
            }

            logger.debug("StandardWorkflowService.deleteCompletedWorkItems()>> OUT End Time =" + System.currentTimeMillis());
        }
    }

    private int deleteWorkItemsInBatch(QueryResult var1) throws WTException {
        long[] var2 = new long[var1.size()];
        int var3 = 0;

        int var4;
        for(var4 = 0; var1.hasMoreElements(); var2[var3++] = ((BigDecimal)((Object[])((Object[])var1.nextElement()))[0]).longValue()) {
        }

        logger.debug("deleteWorkItemsInBatch >> List of work itmes to be deleted >> " + var2);
        Transaction var5 = new Transaction();

        try {
            DeleteBatchSpec var6 = new DeleteBatchSpec();
            var6.setTarget(new ClassTableExpression(WorkItem.class));
            SearchCondition var7 = new SearchCondition(new ClassAttribute(WorkItem.class, "thePersistInfo.theObjectIdentifier.id"), "IN", new ArrayExpression(var2, var2.length));
            var6.setWhere(var7);
            var4 = PersistenceServerHelper.manager.execute(var6);
            var5.commit();
            var5 = null;
        } catch (WTPropertyVetoException | WTException var11) {
            logger.error("Error occured in deleteWorkItemsInBatch>> " + var11.getLocalizedMessage(), var11);
        } finally {
            if (var5 != null) {
                var5.rollback();
            }

        }

        return var4;
    }

    public Vector<WTPrincipalReference> getPoolMembers(WorkItem var1) throws WTException, WfException {
        try {
            Vector var2 = new Vector();
            ObjectReference var3 = var1.getSource();
            WfAssignedActivity var4 = null;
            boolean var5 = SessionServerHelper.manager.setAccessEnforced(false);

            try {
                var4 = (WfAssignedActivity)var3.getObject();
            } finally {
                SessionServerHelper.manager.setAccessEnforced(var5);
            }

            WfAssignedActivityTemplate var6 = (WfAssignedActivityTemplate)var4.getTemplate().getObject();
            Vector var7 = var6.getPoolReferenceStrings();
            Iterator var8 = var7.iterator();

            while(true) {
                while(true) {
                    while(var8.hasNext()) {
                        String var9 = (String)var8.next();
                        Object var10 = this.getPoolObject(var9, var1);
                        ObjectReference var11 = null;
                        Role var12 = null;
                        if (var10 instanceof ObjectReference) {
                            var11 = (ObjectReference)var10;
                        } else if (var10 instanceof Role) {
                            var12 = (Role)var10;
                        }

                        Vector var13;
                        if (var11 != null) {
                            var13 = null;
                            Persistable var21 = var11.getObject();
                            if (var21 instanceof RoleHolder2) {
                                var13 = TeamHelper.service.getMembers((RoleHolder2)var21);
                                Iterator var23 = var13.iterator();

                                while(var23.hasNext()) {
                                    Object var16 = var23.next();
                                    this.addElementNoDup(var2, var16);
                                }
                            } else if (var21 instanceof WTGroup) {
                                Enumeration var22 = ((WTGroup)var21).members();

                                while(var22.hasMoreElements()) {
                                    this.addElementNoDup(var2, var22.nextElement());
                                }
                            }
                        } else if (var12 != null) {
                            var13 = this.getRoleMembers(var12, var1);
                            Iterator var14 = var13.iterator();

                            while(var14.hasNext()) {
                                Object var15 = var14.next();
                                this.addElementNoDup(var2, var15);
                            }
                        }
                    }

                    return var2;
                }
            }
        } catch (WTException var20) {
            if (logger.isDebugEnabled()) {
                logger.debug(var20.getLocalizedMessage(), var20);
            }

            return null;
        }
    }

    public Boolean canSetupParticipantsWithModify(WorkItem var1) throws WTException {
        WfAssignment var2 = (WfAssignment)var1.getParentWA().getObject();
        HashMap var3 = null;
        WfAssignee var4 = var2.getAssignee();
        boolean var5 = SessionServerHelper.manager.setAccessEnforced(false);

        try {
            WfAssignedActivityTemplate var6 = var2.getActivityTemplate();
            var3 = var6.getPermissionMap(var4);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(var5);
        }

        boolean var13 = false;
        if (var3 != null && !var3.isEmpty()) {
            Iterator var7 = var3.keySet().iterator();

            do {
                Role var8;
                do {
                    if (!var7.hasNext()) {
                        return var3 != null && !var3.isEmpty() && var13 ? new Boolean(true) : new Boolean(false);
                    }

                    var8 = (Role)var7.next();
                } while(var8 == null);

                Boolean[] var9 = (Boolean[])var3.get(var8);
                if (var9 != null && var9.length > 1) {
                    for(int var10 = 1; var10 < var9.length; ++var10) {
                        if (var9[var10]) {
                            var13 = true;
                            break;
                        }
                    }
                }
            } while(!var13);
        }

        return var3 != null && !var3.isEmpty() && var13 ? new Boolean(true) : new Boolean(false);
    }

    public Boolean canSetupParticipants(WorkItem var1) throws WTException {
        WfAssignment var2 = (WfAssignment)var1.getParentWA().getObject();
        HashMap var3 = null;
        WfAssignee var4 = var2.getAssignee();
        boolean var5 = SessionServerHelper.manager.setAccessEnforced(false);

        try {
            WfAssignedActivityTemplate var6 = var2.getActivityTemplate();
            var3 = var6.getPermissionMap(var4);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(var5);
        }

        boolean var13 = false;
        if (var3 != null && !var3.isEmpty()) {
            Iterator var7 = var3.keySet().iterator();

            do {
                Role var8;
                do {
                    if (!var7.hasNext()) {
                        return var3 != null && !var3.isEmpty() && var13 ? new Boolean(true) : new Boolean(false);
                    }

                    var8 = (Role)var7.next();
                } while(var8 == null);

                Boolean[] var9 = (Boolean[])var3.get(var8);
                if (var9 != null && var9.length > 0) {
                    for(int var10 = 0; var10 < var9.length; ++var10) {
                        if (var9[var10]) {
                            var13 = true;
                            break;
                        }
                    }
                }
            } while(!var13);
        }

        return var3 != null && !var3.isEmpty() && var13 ? new Boolean(true) : new Boolean(false);
    }

    public HashMap<Role, Boolean[]> getPermissionMap(WorkItem var1) throws WTException, WfException {
        HashMap var2 = null;
        WfAssignment var3 = (WfAssignment)var1.getParentWA().getObject();
        WfAssignee var4 = var3.getAssignee();
        WfAssignedActivity var5 = (WfAssignedActivity)var1.getSource().getObject();
        WfAssignedActivityTemplate var6 = (WfAssignedActivityTemplate)var5.getTemplate().getObject();
        var2 = var6.getPermissionMap(var4);
        return var2;
    }

    public Vector<WTPrincipalReference> getPoolMembers(WorkItem var1, Role var2) throws WTException {
        return this.getPoolMembers(var1, var2, true, (Set)null);
    }

    public Vector<WTPrincipalReference> getPoolMembers(WorkItem var1, Role var2, boolean var3, Set<Object> var4) throws WTException {
        try {
            Vector var5 = new Vector();
            ObjectReference var6 = var1.getSource();
            WfAssignedActivity var7 = null;
            boolean var8 = SessionServerHelper.manager.setAccessEnforced(false);

            try {
                var7 = (WfAssignedActivity)var6.getObject();
            } finally {
                SessionServerHelper.manager.setAccessEnforced(var8);
            }

            WfAssignedActivityTemplate var9 = (WfAssignedActivityTemplate)var7.getTemplate().getObject();
            Vector var10 = var9.getPoolReferenceStrings(var2);
            if (var10 != null) {
                Iterator var11 = var10.iterator();

                while(true) {
                    while(true) {
                        while(true) {
                            while(var11.hasNext()) {
                                String var12 = (String)var11.next();
                                Object var13 = this.getPoolObject(var12, var1);
                                ObjectReference var14 = null;
                                Role var15 = null;
                                if (var13 instanceof ObjectReference) {
                                    var14 = (ObjectReference)var13;
                                } else if (var13 instanceof Role) {
                                    var15 = (Role)var13;
                                }

                                Vector var16;
                                if (var14 != null) {
                                    var16 = null;
                                    Persistable var30 = var14.getObject();
                                    if (var30 instanceof RoleHolder2) {
                                        if (!var3 && var30 instanceof ContainerTeam) {
                                            if (var4 != null) {
                                                var4.add(var30);
                                            }
                                        } else {
                                            boolean var31 = SessionServerHelper.manager.setAccessEnforced(false);

                                            try {
                                                var16 = TeamHelper.service.getMembers((RoleHolder2)var30);
                                            } finally {
                                                SessionServerHelper.manager.setAccessEnforced(var31);
                                            }

                                            Object var20;
                                            for(Iterator var19 = var16.iterator(); var19.hasNext(); this.addElementNoDup(var5, var20)) {
                                                var20 = var19.next();
                                                if (var20 instanceof WTPrincipalReference && ((WTPrincipalReference)var20).getObject() instanceof WTGroup) {
                                                    this.checkForInnerGroup((WTGroup)((WTGroup)((WTPrincipalReference)var20).getObject()), var5);
                                                }
                                            }
                                        }
                                    } else if (var30 instanceof WTGroup) {
                                        if (var3) {
                                            this.checkForInnerGroup((WTGroup)var30, var5);
                                        } else if (var4 != null) {
                                            var4.add(var30);
                                        }
                                    }
                                } else if (var15 != null) {
                                    var16 = this.getRoleMembers(var15, var1);

                                    Object var18;
                                    for(Iterator var17 = var16.iterator(); var17.hasNext(); this.addElementNoDup(var5, var18)) {
                                        var18 = var17.next();
                                        if (var18 instanceof WTPrincipalReference && ((WTPrincipalReference)var18).getObject() instanceof WTGroup) {
                                            this.checkForInnerGroup((WTGroup)((WTGroup)((WTPrincipalReference)var18).getObject()), var5);
                                        }
                                    }
                                }
                            }

                            return var5;
                        }
                    }
                }
            } else {
                return var5;
            }
        } catch (WTException var29) {
            if (logger.isDebugEnabled()) {
                logger.debug(var29.getLocalizedMessage(), var29);
            }

            return null;
        }
    }

    private void checkForInnerGroup(WTGroup var1, Vector<WTPrincipalReference> var2) throws WTException {
        Enumeration var3 = OrganizationServicesHelper.manager.members(var1, false);
        WTPrincipalReference var4 = WTPrincipalReference.newWTPrincipalReference(var1);
        this.addElementNoDup(var2, var4);
        if (logger.isTraceEnabled()) {
            logger.trace("In checkForInnerGroup " + var1.getName());
        }

        while(var3.hasMoreElements()) {
            Object var5 = var3.nextElement();
            if (var5 instanceof WTUser) {
                var4 = WTPrincipalReference.newWTPrincipalReference((WTUser)var5);
                this.addElementNoDup(var2, var4);
            }

            if (var5 instanceof WTGroup) {
                var4 = WTPrincipalReference.newWTPrincipalReference((WTGroup)var5);
                this.addElementNoDup(var2, var4);
                this.checkForInnerGroup((WTGroup)var5, var2);
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Out checkForInnerGroup " + var1.getName());
        }

    }

    public void sendNotification(String var1, String var2, Vector var3, Vector var4, Vector var5, Vector var6, Vector var7, ObjectReference var8, String var9, WfExternalRecipientList var10, WfEmailAttachments var11) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.sendNotification: template = " + var9);
        }

        TemplateEmailNotification var12 = null;
        String var13 = var1;
        String var14 = var2;
        WfProcess var15 = this.getParentProcess(var8);
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.sendNotification: process = " + var15.getName());
        }

        this.addTeamPrincipals(var6, var3, var15);
        this.addVariablePrincipals(var7, var3, var4, var5, var8);
        WTDistributionList var16 = this.buildDistributionList(var15, var3, var4, var5);
        Object var18;
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.sendNotification: Distribution List ");
            if (var16.getResolvedUsers() != null && var16.getResolvedUsers().hasMoreElements()) {
                Enumeration var17 = var16.getResolvedUsers();

                while(var17.hasMoreElements()) {
                    var18 = var17.nextElement();
                    if (var18 instanceof WTPrincipal) {
                        logger.trace("=> StandardWorkflowService.sendNotification:WTPrincipal Distribution List = " + ((WTPrincipal)var18).getName());
                    }

                    if (var18 instanceof WTUser) {
                        logger.trace("=> StandardWorkflowService.sendNotification:WTUser Distribution List = " + ((WTUser)var18).getName());
                    }

                    if (var18 instanceof WTPrincipalReference) {
                        logger.trace("=> StandardWorkflowService.sendNotification:WTPrincipalReference Distribution List = " + ((WTPrincipalReference)var18).getPrincipal().getName());
                    }
                }
            }
        }

        var1 = this.replaceVariables(var1, this.getVariables(var8), false, true);
        var2 = this.replaceVariables(var2, this.getVariables(var8), false, false);

        Persistable var19;
        try {
            if (var16.getResolvedUsers() != null && var16.getResolvedUsers().hasMoreElements()) {
                WTDistributionList var31 = new WTDistributionList();
                Enumeration var33 = var16.getResolvedUsers();
                var19 = null;
                WTReference var20 = null;
                var20 = var15.getBusinessObjectReference(new ReferenceFactory());
                if (var20 == null) {
                    var12 = this.createNotification(var16, var1, var2, var15, var8);
                } else {
                    var19 = var20.getObject();
                    if (logger.isTraceEnabled()) {
                        logger.trace("=> StandardWorkflowService.sendNotification.PBO is not null.verifing user access to PBO.");
                    }

                    boolean var21 = SessionServerHelper.manager.setAccessEnforced(true);

                    try {
                        label337:
                        while(true) {
                            while(true) {
                                WTPrincipalReference var40;
                                do {
                                    Object var22;
                                    do {
                                        if (!var33.hasMoreElements()) {
                                            var12 = this.createNotification(var31, var1, var2, var15, var8);
                                            break label337;
                                        }

                                        var22 = var33.nextElement();
                                        if (var22 instanceof WTPrincipal) {
                                            WTPrincipal var23 = (WTPrincipal)var22;
                                            if (AccessControlHelper.manager.hasAccess(var23, var19, AccessPermission.READ)) {
                                                var31.addPrincipal(var23);
                                                if (logger.isTraceEnabled()) {
                                                    logger.trace("=> StandardWorkflowService.sendNotification:WTPrincipal authorized user = " + var23.getName());
                                                }
                                            }
                                        }

                                        if (var22 instanceof WTUser) {
                                            WTUser var39 = (WTUser)var22;
                                            if (AccessControlHelper.manager.hasAccess(var39, var19, AccessPermission.READ)) {
                                                var31.addPrincipal(var39);
                                                if (logger.isTraceEnabled()) {
                                                    logger.trace("=> StandardWorkflowService.sendNotification:WTUser authorized user = " + var39.getName());
                                                }
                                            }
                                        }
                                    } while(!(var22 instanceof WTPrincipalReference));

                                    var40 = (WTPrincipalReference)var22;
                                } while(!var40.isAccessible());

                                WTPrincipal var24 = var40.getPrincipal();
                                if (var24 instanceof WTUser && var24.isInternal()) {
                                    var10.addRecipient(((WTUser)var24).getEMail());
                                } else if (AccessControlHelper.manager.hasAccess(var24, var19, AccessPermission.READ)) {
                                    var31.addPrincipal(var24);
                                    if (logger.isTraceEnabled()) {
                                        logger.trace("=> StandardWorkflowService.sendNotification:WTPrincipalReference authorized user = " + var40.getPrincipal().getName());
                                    }
                                }
                            }
                        }
                    } finally {
                        SessionServerHelper.manager.setAccessEnforced(var21);
                    }
                }

                NotificationHelper.manager.send(var12);
            }
        } catch (Exception var30) {
            var30.printStackTrace();
            throw new WTException(var30, var30.getMessage());
        }

        if (var10 != null && !var10.isEmpty()) {
            var1 = this.replaceVariables(var13, this.getVariables(var8), true, true);
            var2 = this.replaceVariables(var14, this.getVariables(var8), true, false);
            WfExternalRecipientList var32 = new WfExternalRecipientList();
            var18 = null;
            var19 = null;
            var11 = null;
            WfEmailAttachments var36 = new WfEmailAttachments();
            ProcessData var37 = var15.getContext();
            HashMap var38 = var37.getExtParticipants();
            if (var38 != null && !var38.isEmpty()) {
                int var42 = var10.size();

                for(int var41 = 0; var41 < var42; ++var41) {
                    if (!(var10.elementAt(var41) instanceof WfExternalRecipientList)) {
                        var32.addRecipient((String)var10.elementAt(var41));
                    }
                }

                var10 = var32;
                logger.trace("List of external email ids  in notification robot in routing workflow  :: " + var32);
                WfExternalRecipientList var34 = (WfExternalRecipientList)var38.get(Boolean.TRUE);
                WfExternalRecipientList var35 = (WfExternalRecipientList)var38.get(Boolean.FALSE);
                logger.trace("withAttachmentEmailList :: " + var34);
                logger.trace("withoutAttachmentEmailList :: " + var35);
                if (var34 != null) {
                    logger.trace("Sending notification with attchment....");
                    HashSet var25 = new HashSet();
                    var25.add(WfEmailAttachmentType.toWfEmailAttachmentType("PRIMARY"));
                    var36.addAttachment("primaryBusinessObject", var25);
                    EmailTemplateNotificationRequest var26 = this.createEmailNotification(var34, var1, var2, var15, var8, var9, var36);
                    if (var26 != null) {
                        var26.send();
                    }
                }

                if (var35 != null) {
                    logger.trace("Sending notification without attchment....");
                    var36 = new WfEmailAttachments();
                    EmailTemplateNotificationRequest var46 = this.createEmailNotification(var35, var1, var2, var15, var8, var9, var36);
                    if (var46 != null) {
                        var46.send();
                    }
                }
            }

            EmailTemplateNotificationRequest var44;
            if (var37.isSendAttch()) {
                logger.trace("Sending notification with attchment....isSendAttch true");
                HashSet var43 = new HashSet();
                var43.add(WfEmailAttachmentType.toWfEmailAttachmentType("PRIMARY"));
                var36.addAttachment("primaryBusinessObject", var43);
                var44 = this.createEmailNotification(var10, var1, var2, var15, var8, var9, var36);
                if (var44 != null) {
                    var44.send();
                }
            } else {
                logger.trace("Sending notification without attchment....isSendAttch false");
                WfExecutionObject var45 = (WfExecutionObject)var8.getObject();
                if (var45 instanceof WfInternalMethod && !var37.isIsPJLRoute()) {
                    var36 = this.getEmailAttchements(var45, var37);
                    var44 = this.createEmailNotification(var10, var1, var2, var15, var8, var9, var36);
                    if (var44 != null) {
                        var44.send();
                    }
                } else if (!var10.isEmpty()) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Checking if email id list not empty");
                    }

                    var36 = new WfEmailAttachments();
                    var44 = this.createEmailNotification(var10, var1, var2, var15, var8, var9, var36);
                    logger.trace("Sending notification without attchment to" + var10);
                    if (var44 != null) {
                        var44.send();
                    }
                }
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   sendNotification - OUT");
        }

    }

    private WfEmailAttachments getEmailAttchements(WfExecutionObject var1, ProcessData var2) {
        logger.debug(">>In getEmailAttchements(WfExecutionObject obj,ProcessData parentContext) method");
        WfEmailAttachments var3 = null;
        WfInternalMethod var4 = (WfInternalMethod)var1;
        logger.debug(">>WfInternalMethod Object::" + var4);
        WfInternalMethodTemplate var5 = (WfInternalMethodTemplate)var4.getTemplate().getObject();
        logger.debug(">>WfInternalMethodTemplate Object::" + var5);
        ProcessData var6 = var4.getContext();
        SignatureVector var7 = var5.getSignature();
        Enumeration var8 = var7.elements();
        int var9 = 0;
        boolean var10 = false;
        int var11 = 0;
        Vector var12 = new Vector();

        for(boolean var13 = logger.isDebugEnabled(); var8.hasMoreElements(); ++var9) {
            String var14 = (String)var8.nextElement();
            Object var15 = var6.getValue(var14);
            if (var15 == null) {
                var15 = var2.getValue(var14);
            }

            if (var15 instanceof WfExternalRecipientList) {
                ;
            }

            if (var15 instanceof WfEmailAttachments) {
                var11 = var9;
            }

            if (var13) {
                logger.debug("      " + var14 + " = " + var15);
            }

            var12.addElement(var15);
        }

        if (var9 > 0) {
            Object var16 = var12.get(var11);
            if (var16 != null && var16 instanceof WfEmailAttachments) {
                var3 = (WfEmailAttachments)var16;
                logger.debug(">>tempEmail ::" + var3);
            }
        }

        logger.debug(">>Out getEmailAttchements(WfExecutionObject obj,ProcessData parentContext) method");
        return var3;
    }

    public QueryResult getAcceptedWorkItems(WTPrincipal var1, Integer var2, String var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getAcceptedWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        try {
            SQLFunction var4 = this.getTruncateFunction(new ClassAttribute(WorkItem.class, "thePersistInfo.createStamp"));
            QuerySpec var5 = this.buildOwnerQuery(var1);
            ClassAttribute var6 = new ClassAttribute(WorkItem.class, "role");
            var5.appendOrderBy(new OrderBy(var6, false), new int[]{0});
            SearchCondition var7 = new SearchCondition(WorkItem.class, "completedBy", true);
            SearchCondition var8 = new SearchCondition(WorkItem.class, "status", "=", WfAssignmentState.ACCEPTED);
            var5.appendAnd();
            var5.appendWhere(var7, new int[]{0});
            var5.appendAnd();
            var5.appendWhere(var8, new int[]{0});
            if (var3 != null) {
                boolean var9 = false;
                if (var3.equals("thePersistInfo.modifyStamp")) {
                    var9 = true;
                }

                OrderBy var10 = new OrderBy(new ClassAttribute(WorkItem.class, var3), var9);
                var5.appendOrderBy(var10, new int[]{0});
            } else {
                var5.appendOrderBy(new OrderBy(var4, false), new int[]{0});
            }

            if (var2 == null) {
                var2 = new Integer(-1);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("SWS.getAcceptedWorkItems - query_limit = " + var2);
            }

            int var14 = var2;
            if (var14 > 0) {
                var5.setAdvancedQueryEnabled(true);
                var5.setQueryLimit(var14);
            }

            QueryResult var15 = PersistenceServerHelper.manager.query(var5);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getAcceptedWorkItems - OUT: size = " + var15.size());
            }

            return var15;
        } catch (PartialResultException var11) {
            if (logger.isDebugEnabled()) {
                logger.debug(var11.getLocalizedMessage(), var11);
            }

            return var11.getQueryResult();
        } catch (QueryException var12) {
            if (logger.isDebugEnabled()) {
                logger.debug(var12.getLocalizedMessage(), var12);
            }

            throw new WfException(var12, (String)null);
        } catch (WTPropertyVetoException var13) {
            if (logger.isDebugEnabled()) {
                logger.debug(var13.getLocalizedMessage(), var13);
            }

            throw new WTException(var13);
        }
    }

    public QueryResult getAcceptedWorkItems(WTContainerRef var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getAcceptedWorkItems: " + var1.getName());
        }

        WTContainer var2 = (WTContainer)var1.getObject();
        AdministrativeDomain var3 = var2.getSystemDomain();

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Container system domain: " + AdminDomainRef.newAdminDomainRef(var3));
            }

            QuerySpec var4 = new QuerySpec();
            var4.setAdvancedQueryEnabled(true);
            int var5 = var4.appendClassList(WfAssignedActivity.class, false);
            new ClassAttribute(WfAssignedActivity.class, "thePersistInfo.theObjectIdentifier.id");
            var4.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", var5, false);
            SearchCondition var7 = new SearchCondition(WfAssignedActivity.class, "domainRef.key", "=", getOid(var3));
            var4.appendWhere(var7, new int[]{0});
            SubSelectExpression var8 = new SubSelectExpression(var4);
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs1: " + var4);
            }

            QuerySpec var9 = new QuerySpec(WorkItem.class);
            var9.setAdvancedQueryEnabled(true);
            ClassAttribute var10 = new ClassAttribute(WorkItem.class, "source.key.id");
            SearchCondition var11 = new SearchCondition(WorkItem.class, "completedBy", true);
            SearchCondition var12 = new SearchCondition(WorkItem.class, "status", "=", WfAssignmentState.ACCEPTED);
            SearchCondition var13 = new SearchCondition(var10, "IN", var8);
            var9.appendWhere(var11, new int[]{0});
            var9.appendAnd();
            var9.appendWhere(var12, new int[]{0});
            var9.appendAnd();
            var9.appendWhere(var13, new int[]{0});
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs2: " + var9);
            }

            QueryResult var14 = PersistenceServerHelper.manager.query(var9);
            if (logger.isTraceEnabled()) {
                logger.trace("   getAcceptedWorkItem by container: FOUND " + var14.size());
            }

            return var14;
        } catch (QueryException var15) {
            if (logger.isDebugEnabled()) {
                logger.debug(var15.getLocalizedMessage(), var15);
            }

            throw new WfException(var15, (String)null);
        }
    }

    public QueryResult getAcceptedWorkItems(WTPrincipal var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getAcceptedWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        return this.getAcceptedWorkItems(var1, new Integer(-1), (String)null);
    }

    public QueryResult getReassignedWorkItems(WTPrincipal var1, Integer var2, String var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getReassignedWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        try {
            SQLFunction var4 = this.getTruncateFunction(new ClassAttribute(WorkItem.class, "thePersistInfo.createStamp"));
            QuerySpec var5 = this.buildOwnerQuery(var1);
            ClassAttribute var6 = new ClassAttribute(WorkItem.class, "role");
            var5.appendOrderBy(new OrderBy(var6, false), new int[]{0});
            SearchCondition var7 = new SearchCondition(WorkItem.class, "completedBy", true);
            SearchCondition var8 = new SearchCondition(WorkItem.class, "reassigned", "TRUE");
            var5.appendAnd();
            var5.appendWhere(var7, new int[]{0});
            var5.appendAnd();
            var5.appendWhere(var8, new int[]{0});
            if (var3 != null) {
                boolean var9 = false;
                if (var3.equals("thePersistInfo.modifyStamp")) {
                    var9 = true;
                }

                OrderBy var10 = new OrderBy(new ClassAttribute(WorkItem.class, var3), var9);
                var5.appendOrderBy(var10, new int[]{0});
            } else {
                var5.appendOrderBy(new OrderBy(var4, false), new int[]{0});
            }

            if (var2 == null) {
                var2 = new Integer(-1);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("SWS.getReassigendWorkItems - query_limit = " + var2);
            }

            int var14 = var2;
            if (var14 > 0) {
                var5.setAdvancedQueryEnabled(true);
                var5.setQueryLimit(var14);
            }

            QueryResult var15 = PersistenceServerHelper.manager.query(var5);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getReassignedWorkItems - OUT: size = " + var15.size());
            }

            return var15;
        } catch (PartialResultException var11) {
            if (logger.isDebugEnabled()) {
                logger.debug(var11.getLocalizedMessage(), var11);
            }

            return var11.getQueryResult();
        } catch (QueryException var12) {
            if (logger.isDebugEnabled()) {
                logger.debug(var12.getLocalizedMessage(), var12);
            }

            throw new WfException(var12, (String)null);
        } catch (WTPropertyVetoException var13) {
            if (logger.isDebugEnabled()) {
                logger.debug(var13.getLocalizedMessage(), var13);
            }

            throw new WTException(var13);
        }
    }

    public QueryResult getReassignedWorkItems(WTContainerRef var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getReassignedWorkItems: " + var1.getName());
        }

        WTContainer var2 = (WTContainer)var1.getObject();
        AdministrativeDomain var3 = var2.getSystemDomain();

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Container system domain: " + AdminDomainRef.newAdminDomainRef(var3));
            }

            QuerySpec var4 = new QuerySpec();
            var4.setAdvancedQueryEnabled(true);
            int var5 = var4.appendClassList(WfAssignedActivity.class, false);
            new ClassAttribute(WfAssignedActivity.class, "thePersistInfo.theObjectIdentifier.id");
            var4.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", var5, false);
            SearchCondition var7 = new SearchCondition(WfAssignedActivity.class, "domainRef.key", "=", getOid(var3));
            var4.appendWhere(var7, new int[]{0});
            SubSelectExpression var8 = new SubSelectExpression(var4);
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs1: " + var4);
            }

            QuerySpec var9 = new QuerySpec(WorkItem.class);
            var9.setAdvancedQueryEnabled(true);
            ClassAttribute var10 = new ClassAttribute(WorkItem.class, "source.key.id");
            SearchCondition var11 = new SearchCondition(WorkItem.class, "completedBy", true);
            SearchCondition var12 = new SearchCondition(WorkItem.class, "reassigned", "TRUE");
            SearchCondition var13 = new SearchCondition(var10, "IN", var8);
            var9.appendWhere(var11, new int[]{0});
            var9.appendAnd();
            var9.appendWhere(var12, new int[]{0});
            var9.appendAnd();
            var9.appendWhere(var13, new int[]{0});
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs2: " + var9);
            }

            QueryResult var14 = PersistenceServerHelper.manager.query(var9);
            if (logger.isTraceEnabled()) {
                logger.trace("   getReassignedWorkItem by container: FOUND " + var14.size());
            }

            return var14;
        } catch (QueryException var15) {
            if (logger.isDebugEnabled()) {
                logger.debug(var15.getLocalizedMessage(), var15);
            }

            throw new WfException(var15, (String)null);
        }
    }

    public QueryResult getReassignedWorkItems(WTPrincipal var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getReassignedWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        return this.getReassignedWorkItems(var1, new Integer(-1), (String)null);
    }

    public QueryResult getDelegatedWorkItems(WTPrincipal var1, Integer var2, String var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getDelegatedWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        try {
            SQLFunction var4 = this.getTruncateFunction(new ClassAttribute(WorkItem.class, "thePersistInfo.createStamp"));
            QuerySpec var5 = this.buildOwnerQuery(var1);
            ClassAttribute var6 = new ClassAttribute(WorkItem.class, "role");
            var5.appendOrderBy(new OrderBy(var6, false), new int[]{0});
            SearchCondition var7 = new SearchCondition(WorkItem.class, "completedBy", true);
            SearchCondition var8 = new SearchCondition(WorkItem.class, "origOwner.key.classname", false);
            var5.appendAnd();
            var5.appendWhere(var7, new int[]{0});
            var5.appendAnd();
            var5.appendWhere(var8, new int[]{0});
            if (var3 != null) {
                boolean var9 = false;
                if (var3.equals("thePersistInfo.modifyStamp")) {
                    var9 = true;
                }

                OrderBy var10 = new OrderBy(new ClassAttribute(WorkItem.class, var3), var9);
                var5.appendOrderBy(var10, new int[]{0});
            } else {
                var5.appendOrderBy(new OrderBy(var4, false), new int[]{0});
            }

            if (var2 == null) {
                var2 = new Integer(-1);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("SWS.getDelegateddWorkItems - query_limit = " + var2);
            }

            int var14 = var2;
            if (var14 > 0) {
                var5.setAdvancedQueryEnabled(true);
                var5.setQueryLimit(var14);
            }

            QueryResult var15 = PersistenceServerHelper.manager.query(var5);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getDelegatedWorkItems - OUT: size = " + var15.size());
            }

            return var15;
        } catch (PartialResultException var11) {
            if (logger.isDebugEnabled()) {
                logger.debug(var11.getLocalizedMessage(), var11);
            }

            return var11.getQueryResult();
        } catch (QueryException var12) {
            if (logger.isDebugEnabled()) {
                logger.debug(var12.getLocalizedMessage(), var12);
            }

            throw new WfException(var12, (String)null);
        } catch (WTPropertyVetoException var13) {
            if (logger.isDebugEnabled()) {
                logger.debug(var13.getLocalizedMessage(), var13);
            }

            throw new WTException(var13);
        }
    }

    public QueryResult getDelegatedWorkItems(WTContainerRef var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getDelegatedWorkItems: " + var1.getName());
        }

        WTContainer var2 = (WTContainer)var1.getObject();
        AdministrativeDomain var3 = var2.getSystemDomain();

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Container system domain: " + AdminDomainRef.newAdminDomainRef(var3));
            }

            QuerySpec var4 = new QuerySpec();
            var4.setAdvancedQueryEnabled(true);
            int var5 = var4.appendClassList(WfAssignedActivity.class, false);
            new ClassAttribute(WfAssignedActivity.class, "thePersistInfo.theObjectIdentifier.id");
            var4.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", var5, false);
            SearchCondition var7 = new SearchCondition(WfAssignedActivity.class, "domainRef.key", "=", getOid(var3));
            var4.appendWhere(var7, new int[]{0});
            SubSelectExpression var8 = new SubSelectExpression(var4);
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs1: " + var4);
            }

            QuerySpec var9 = new QuerySpec(WorkItem.class);
            var9.setAdvancedQueryEnabled(true);
            ClassAttribute var10 = new ClassAttribute(WorkItem.class, "source.key.id");
            SearchCondition var11 = new SearchCondition(WorkItem.class, "completedBy", true);
            SearchCondition var12 = new SearchCondition(WorkItem.class, "origOwner.key.classname", false);
            SearchCondition var13 = new SearchCondition(var10, "IN", var8);
            var9.appendWhere(var11, new int[]{0});
            var9.appendAnd();
            var9.appendWhere(var12, new int[]{0});
            var9.appendAnd();
            var9.appendWhere(var13, new int[]{0});
            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs2: " + var9);
            }

            QueryResult var14 = PersistenceServerHelper.manager.query(var9);
            if (logger.isTraceEnabled()) {
                logger.trace("   getDelegatedtedWorkItem by container: FOUND " + var14.size());
            }

            return var14;
        } catch (QueryException var15) {
            if (logger.isDebugEnabled()) {
                logger.debug(var15.getLocalizedMessage(), var15);
            }

            throw new WfException(var15, (String)null);
        }
    }

    public QueryResult getDelegatedWorkItems(WTPrincipal var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getDelegatededWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        return this.getDelegatedWorkItems(var1, new Integer(-1), (String)null);
    }

    public QueryResult getCompletedWorkItems(WTPrincipal var1) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getCompletedWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        return this.getCompletedWorkItems(var1, new Integer(-1), (String)null);
    }

    public QueryResult getCompletedWorkItems(WTPrincipal var1, Integer var2, String var3) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getCompletedWorkItems - IN: principal = " + (var1 == null ? "is null" : var1.getName()));
        }

        try {
            SQLFunction var4 = this.getTruncateFunction(new ClassAttribute(WorkItem.class, "thePersistInfo.createStamp"));
            QuerySpec var5 = this.buildOwnerQuery(var1);
            ClassAttribute var6 = new ClassAttribute(WorkItem.class, "role");
            var5.appendOrderBy(new OrderBy(var6, false), new int[]{0});
            SearchCondition var7 = new SearchCondition(WorkItem.class, "completedBy", false);
            var5.appendAnd();
            var5.appendWhere(var7, new int[]{0});
            if (var3 != null) {
                boolean var8 = false;
                if (var3.equals("thePersistInfo.modifyStamp")) {
                    var8 = true;
                }

                OrderBy var9 = new OrderBy(new ClassAttribute(WorkItem.class, var3), var8);
                var5.appendOrderBy(var9, new int[]{0});
            } else {
                var5.appendOrderBy(new OrderBy(var4, false), new int[]{0});
            }

            if (var2 == null) {
                var2 = new Integer(-1);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("SWS.getReassigendWorkItems - query_limit = " + var2);
            }

            int var13 = var2;
            if (var13 > 0) {
                var5.setAdvancedQueryEnabled(true);
                var5.setQueryLimit(var13);
            }

            QueryResult var14 = PersistenceServerHelper.manager.query(var5);
            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getReassignedWorkItems - OUT: size = " + var14.size());
            }

            return var14;
        } catch (PartialResultException var10) {
            if (logger.isDebugEnabled()) {
                logger.debug(var10.getLocalizedMessage(), var10);
            }

            return var10.getQueryResult();
        } catch (QueryException var11) {
            if (logger.isDebugEnabled()) {
                logger.debug(var11.getLocalizedMessage(), var11);
            }

            throw new WfException(var11, (String)null);
        } catch (WTPropertyVetoException var12) {
            if (logger.isDebugEnabled()) {
                logger.debug(var12.getLocalizedMessage(), var12);
            }

            throw new WTException(var12);
        }
    }

    public QueryResult getWorkItemsForTable(WTContainer var1, WTPrincipal var2, Boolean var3, Boolean var4, Boolean var5, Boolean var6, Boolean var7, Boolean var8, Object var9) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getWorkItemsForTable: \n currPrincipal = " + var2 + "\n openAssignments = " + var3 + "\n isAssignedToMe = " + var4 + "\n isReassigned = " + var5 + "\n isAccepted = " + var6 + "\n isDelegated = " + var7 + "\n isOverDue = " + var8 + "\n tmpObject = " + var9);
        }

        long var10 = System.currentTimeMillis();
        WorkItemProcessor var12 = new WorkItemProcessor();
        AdministrativeDomain var13 = var1.getSystemDomain();

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Container system domain: " + AdminDomainRef.newAdminDomainRef(var13));
            }

            QuerySpec var14 = new QuerySpec();
            int var15 = var14.appendClassList(WorkItem.class, true);
            var14.setAdvancedQueryEnabled(true);
            Object var16 = null;
            if (var9 != null && var9 instanceof Persistable) {
                ReferenceFactory var23 = new ReferenceFactory();
                String var18 = var23.getReferenceString((Persistable)var9);
                var14.appendWhere(new SearchCondition(WorkItem.class, "primaryBusinessObject.key.classname", "=", var18), new int[]{var15, -1});
            } else {
                var14.setAdvancedQueryEnabled(true);
                int var17 = var14.appendClassList(WfAssignedActivity.class, false);
                var14.appendWhere(new SearchCondition(new ClassAttribute(WorkItem.class, "source.key.id"), "=", new ClassAttribute(WfAssignedActivity.class, "thePersistInfo.theObjectIdentifier.id")), new int[]{var15, var17});
                var14.appendAnd();
                var14.appendWhere(new SearchCondition(WfAssignedActivity.class, "domainRef.key", "=", getOid(var13)), new int[]{var17, -1});
            }

            SearchCondition var24;
            if (var3 != null) {
                var24 = this.getSCForOpenAssignemnts(var3);
                var14.appendAnd();
                var14.appendWhere(var24, new int[]{var15, -1});
            }

            if (var4 != null) {
                var24 = this.getSCForIsAssignendToMe(var4, var2);
                var14.appendAnd();
                var14.appendWhere(var24, new int[]{var15, -1});
            }

            if (var6 != null) {
                var24 = this.getSCForIsAccepted(var6);
                var14.appendAnd();
                var14.appendWhere(var24, new int[]{var15, -1});
            }

            if (var5 != null) {
                var24 = this.getSCForIsReassigned(var5);
                var14.appendAnd();
                var14.appendWhere(var24, new int[]{var15, -1});
            }

            if (var7 != null) {
                var24 = this.getSCForIsDelegated(var7);
                var14.appendAnd();
                var14.appendWhere(var24, new int[]{var15, -1});
            }

            if (logger.isTraceEnabled()) {
                logger.trace("   >>>Here's the qs2: " + var14);
            }

            PersistenceServerHelper.manager.query(var14, var12);
            WTList var26 = var12.getWorkItems();
            if (logger.isTraceEnabled()) {
                logger.trace("   getWorkItemsForTable FOUND " + var26.size());
            }

            ObjectVector var25 = new ObjectVector();

            WorkItem var20;
            for(Iterator var19 = var26.persistableIterator(); var19.hasNext(); var25.addElement(var20)) {
                var20 = (WorkItem)var19.next();
                if (logger.isTraceEnabled()) {
                    logger.trace("Added workitem :: " + var20);
                }
            }

            QueryResult var27 = new QueryResult(var25);
            long var28 = System.currentTimeMillis();
            if (logger.isTraceEnabled()) {
                logger.trace("Out StandardWorkflowService.getWorkItemsForTable() endTime= " + var28);
                logger.trace("Total time in StandardWorkflowService.getWorkItemsForTable() = " + (var28 - var10));
            }

            return var27;
        } catch (QueryException var22) {
            if (logger.isDebugEnabled()) {
                logger.debug(var22.getLocalizedMessage(), var22);
            }

            throw new WfException(var22, (String)null);
        }
    }

    public QueryResult getUserWorkItemsForTable(WTPrincipal var1, Boolean var2, Boolean var3, Boolean var4, Boolean var5, Boolean var6, boolean var7, Integer var8, Object var9) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getUserWorkItemsForTable: \n currPrincipal = " + var1 + "\n openAssignments = " + var2 + "\n isReassigned = " + var3 + "\n isAccepted = " + var4 + "\n isDelegated = " + var5 + "\n isOverDue = " + var6 + "\n isOverviewPage = " + var7 + "\n queryLimit = " + var8 + "\n tmpObject = " + var9);
        }

        try {
            SearchCondition var10 = this.getSCForIsAssignendToMe(new Boolean(false), var1);
            new QueryResult();
            QueryResult var11;
            QuerySpec var12;
            if (var7) {
                var12 = this.getWorkItemQuery(WorkItem.class, false, var10, var2, var3, var4, var5, var6, var7, var8);
                QuerySpec var13 = this.getWorkItemQuery(ProjectWorkItem.class, false, var10, var2, var3, var4, var5, var6, var7, var8);
                var11 = this.executeQuery(var12);
                QueryResult var14 = this.executeQuery(var13);
                var11.append(this.getObjectVector(var14));
            } else {
                var12 = this.getWorkItemQuery(WorkItem.class, true, var10, var2, var3, var4, var5, var6, var7, var8);
                var11 = this.executeQuery(var12);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("   StandardWorkflowService.getUserWorkItemsForTable - OUT: size = " + var11.size());
            }

            return var11;
        } catch (WTPropertyVetoException var15) {
            if (logger.isDebugEnabled()) {
                logger.debug(var15.getLocalizedMessage(), var15);
            }

            throw new WTException(var15);
        }
    }

    public void unacceptAssignment(WorkItem var1, WTUser var2) throws WTException, WfException {
        if (logger.isDebugEnabled()) {
            logger.debug("=> StandardWorkflowService.unacceptAssignment: principal = " + var2.getName());
        }

        WfAssignedActivity var3 = (WfAssignedActivity)var1.getSource().getObject();
        var3.unacceptAssignment(var1, var2);
        logger.debug("   StandardWorkflowService.unacceptAssignment - OUT");
    }

    public void sendNotification(String var1, String var2, Vector var3, Vector var4, Vector var5, Vector var6, Vector var7, ObjectReference var8, String var9, WfExternalRecipientList var10, WfEmailAttachments var11, Boolean var12) throws WTException {
        this.sendNotification(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
    }

    public String replaceVariables(String var1, ProcessData var2, Boolean var3) throws WTException {
        return this.replaceVariables(var1, var2, false, var3);
    }

    public Vector<WTGroup> getPoolGroupObjects(WorkItem var1, Role var2) throws WTException {
        try {
            Vector var3 = new Vector();
            ObjectReference var4 = var1.getSource();
            WfAssignedActivity var5 = null;
            boolean var6 = SessionServerHelper.manager.setAccessEnforced(false);

            try {
                var5 = (WfAssignedActivity)var4.getObject();
            } finally {
                SessionServerHelper.manager.setAccessEnforced(var6);
            }

            WfAssignedActivityTemplate var7 = (WfAssignedActivityTemplate)var5.getTemplate().getObject();
            Vector var8 = var7.getPoolReferenceStrings(var2);
            Iterator var9 = var8.iterator();

            while(true) {
                while(true) {
                    while(var9.hasNext()) {
                        String var10 = (String)var9.next();
                        Object var11 = this.getPoolObject(var10, var1);
                        ObjectReference var12 = null;
                        Role var13 = null;
                        if (var11 instanceof ObjectReference) {
                            var12 = (ObjectReference)var11;
                        } else if (var11 instanceof Role) {
                            var13 = (Role)var11;
                        }

                        Vector var14;
                        if (var12 != null) {
                            var14 = null;
                            Persistable var23 = var12.getObject();
                            if (var23 instanceof RoleHolder2) {
                                var14 = TeamHelper.service.getMembers((RoleHolder2)var23);
                                Iterator var24 = var14.iterator();

                                while(var24.hasNext()) {
                                    Object var17 = var24.next();
                                    WTPrincipalReference var18 = (WTPrincipalReference)var17;
                                    if (var18.getObject() instanceof WTGroup && !(var18.getObject() instanceof WTOrganization)) {
                                        this.addElementNoDup(var3, var18.getObject());
                                    }
                                }
                            } else if (var23 instanceof WTGroup) {
                                this.addElementNoDup(var3, var23);
                            }
                        } else if (var13 != null) {
                            var14 = this.getRoleMembers(var13, var1);
                            Iterator var15 = var14.iterator();

                            while(var15.hasNext()) {
                                Object var16 = var15.next();
                                this.addElementNoDup(var3, var16);
                            }
                        }
                    }

                    return var3;
                }
            }
        } catch (WTException var22) {
            var22.printStackTrace();
            return null;
        }
    }

    public void setTaskBasedRights(WorkItem var1, WTPrincipalReference var2) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> WorkflowService.setTaskBasedRights: " + var2);
        }

        WfVariable[] var3 = ((WfActivity)var1.getSource().getObject()).getContext().getVariableList();
        boolean var4 = false;
        var1.setVariablePermissionMap(new VariablePermissionTable());
        SessionContext var5 = SessionContext.newContext();
        Transaction var6 = new Transaction();

        try {
            var6.start();
            SessionHelper.manager.setAdministrator();
            int var7 = 0;

            while(true) {
                if (var7 >= var3.length) {
                    if (var4) {
                        PersistenceServerHelper.manager.update(var1);
                    }

                    var6.commit();
                    var6 = null;
                    break;
                }

                var4 = this.setTaskBasedRights(var1, var2, var3[var7]) || var4;
                ++var7;
            }
        } finally {
            if (var6 != null) {
                var6.rollback();
            }

            SessionContext.setContext(var5);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   WorkflowService.setTaskBasedRights: OUT ");
        }

    }

    public void revokeTaskBasedRights(WorkItem var1) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> Workflow Service.revokeTaskBasedRights");
        }

        VariablePermissionTable var2 = var1.getVariablePermissionMap();
        if (var2 == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("   permission map is null, returning...");
            }

        } else {
            SessionContext var3 = SessionContext.newContext();
            Transaction var4 = new Transaction();

            try {
                var4.start();
                SessionHelper.manager.setAdministrator();
                Enumeration var5 = var2.keys();

                label156:
                while(true) {
                    ObjectIdentifier var6;
                    PermissionSet var7;
                    do {
                        do {
                            if (!var5.hasMoreElements()) {
                                var4.commit();
                                var4 = null;
                                break label156;
                            }

                            var6 = (ObjectIdentifier)var5.nextElement();
                            var7 = (PermissionSet)var2.get(var6);
                        } while(var7 == null);
                    } while(var7.isEmpty());

                    WTPrincipal var8 = OwnershipHelper.getOwner(var1);

                    try {
                        WTObject var9 = (WTObject)PersistenceServerHelper.manager.restore(var6);
                        if (!(var9 instanceof Iterated)) {
                            this.revokeTaskBasedRights(var9, var8, (Vector)var7.getPermissions(), getOid(var1).getId());
                        } else {
                            QueryResult var10 = VersionControlHelper.service.allIterationsOf(((Iterated)var9).getMaster());

                            while(var10.hasMoreElements()) {
                                this.revokeTaskBasedRights((WTObject)var10.nextElement(), var8, (Vector)var7.getPermissions(), getOid(var1).getId());
                            }
                        }
                    } catch (ObjectNoLongerExistsException var14) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(var14.getLocalizedMessage(), var14);
                        }

                        if (logger.isTraceEnabled()) {
                            logger.trace("   object no longer exists: " + var6);
                        }
                    }
                }
            } finally {
                if (var4 != null) {
                    var4.rollback();
                }

                SessionContext.setContext(var3);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("   Workflow Service.revokeTaskBasedRights - OUT");
            }

        }
    }

    public void queueDeletionOfCompletedWorkItems(WTPrincipalReference var1, WTContainerRef var2, boolean var3) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.queueDeletionOfCompletedWorkItems:==> IN principalRef = " + var1.getName() + ", containerRef = " + var2.getName() + ", new thread = " + var3);
        }

        boolean var4 = SessionServerHelper.manager.setAccessEnforced(false);
        WTContainer var5 = null;

        try {
            var5 = var2.getReferencedContainer();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(var4);
        }

        String var6 = (String)PreferenceHelper.service.getValue(DELETE_COMPLETED_WORKITEMS_PREFS, var5, (WTUser)var1.getPrincipal());
        if (logger.isTraceEnabled()) {
            logger.trace(" DELETE_COMPLETED_WORKITEMS_PREFS=" + var6);
        }

        if (!var6.equals("NO")) {
            if (var3) {
                SessionThread var7 = new SessionThread(new DeleteCompletedWorkItemsThread(var1, var2), new SessionContext());
                var7.start();
            } else {
                WTPrincipal var19 = SessionHelper.manager.getAdministrator();
                WTPrincipal var8 = SessionContext.setEffectivePrincipal(var19);
                ProcessingQueue var9 = this.getDeleteCompletedWorkItemQueue();
                Class[] var10 = new Class[]{WTPrincipalReference.class, WTContainerRef.class};
                Object[] var11 = new Object[]{var1, var2};

                try {
                    var9.addEntry(var19, "targetDeleteCompletedWorkItems", CLASSNAME, var10, var11);
                } finally {
                    SessionContext.setEffectivePrincipal(var8);
                }
            }

            if (logger.isTraceEnabled()) {
                logger.trace("   queueDeletionOfCompletedWorkItems - OUT");
            }

        }
    }

    private ProcessingQueue createQueue(String var1, int var2) throws WTException {
        ProcessingQueue var3 = null;

        try {
            String var4;
            try {
                WTProperties var10000 = WTProperties.getLocalProperties();
                WfEngineService var10002 = WfEngineHelper.service;
                var4 = var10000.getProperty("wt.workflow.engine.defaultWfQueueType", "Pool");
            } catch (Throwable var6) {
                if (logger.isDebugEnabled()) {
                    logger.debug("WorkflowService: Error reading wt.workflow.* properties");
                    logger.debug(var6.getLocalizedMessage(), var6);
                }

                throw new WTException(var6);
            }

            WfEngineService var10 = WfEngineHelper.service;
            if (!"Process".equalsIgnoreCase(var4)) {
                var3 = QueueHelper.manager.createQueue(var1, false, true);
            } else {
                var3 = QueueHelper.manager.createQueue(var1, false, false);
            }
        } catch (ObjectIsStaleException var7) {
            var3 = QueueHelper.manager.getQueue(var1);
            if (logger.isDebugEnabled()) {
                logger.debug(var7.getLocalizedMessage(), var7);
            }
        } catch (UniquenessException var8) {
            if (logger.isTraceEnabled()) {
                logger.trace("   " + var8.getMessage());
            }

            var3 = QueueHelper.manager.getQueue(var1);
            if (logger.isDebugEnabled()) {
                logger.debug(var8.getLocalizedMessage(), var8);
            }
        } catch (PersistenceException var9) {
            if (logger.isTraceEnabled()) {
                logger.trace("   " + var9.getMessage());
            }

            var3 = QueueHelper.manager.getQueue(var1);
            if (logger.isDebugEnabled()) {
                logger.debug(var9.getLocalizedMessage(), var9);
            }
        }

        return var3;
    }

    private ProcessingQueue getDeleteCompletedWorkItemQueue() throws WTException {
        return this.deleteCompletedWorkItemsQueue;
    }

    private WfState getStateOfWfContainer(WfContainer var1) {
        WfState var2 = null;
        WfRequester var3 = null;
        WfRequesterActivity var4 = null;

        try {
            var3 = var1.getRequester();
            if (var3 != null && var3 instanceof WfRequesterActivity) {
                var4 = (WfRequesterActivity)var3;
                var2 = this.getStateOfWfContainer((WfContainer)var4.getParentProcessRef().getObject());
            } else if (var3 == null) {
                var2 = var1.getState();
            }
        } catch (WTRuntimeException var6) {
            if (logger.isDebugEnabled()) {
                logger.debug("getStateOfWfContainer ==> Exception caught " + var6);
                logger.debug(var6.getLocalizedMessage(), var6);
            }

            var2 = null;
        }

        return var2;
    }

    private void addVariablePrincipals(Vector var1, Vector var2, Vector var3, Vector var4, ObjectReference var5) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.addVariableRecipients - IN");
        }

        WfActivity var6 = (WfActivity)var5.getObject();
        WfProcess var7 = var6.getParentProcess();
        ProcessData var8 = var7.getContext();
        Enumeration var9 = var1.elements();

        while(var9.hasMoreElements()) {
            WfVariableInfo var10 = (WfVariableInfo)var9.nextElement();
            Class var11 = var10.getVariableClass();
            if (logger.isTraceEnabled()) {
                logger.trace("   variable: " + var10.getName() + ", type: " + var11.getName());
            }

            if (WTPrincipal.class.isAssignableFrom(var11)) {
                WTPrincipal var12 = (WTPrincipal)var8.getValue(var10.getName());
                if (var12 != null) {
                    var2.addElement(var12);
                }
            } else if (Role.class.isAssignableFrom(var11)) {
                Role var13 = (Role)var8.getValue(var10.getName());
                if (var13 != null) {
                    var3.addElement(var13);
                }
            } else if (ActorRole.class.isAssignableFrom(var11)) {
                ActorRole var14 = (ActorRole)var8.getValue(var10.getName());
                if (var14 != null) {
                    var4.addElement(var14);
                }
            } else if (Team.class.isAssignableFrom(var11)) {
                Team var15 = (Team)var8.getValue(var10.getName());
                if (var15 != null) {
                    this.addTeamPrincipals(var15, var2);
                }
            } else if (TeamTemplate.class.isAssignableFrom(var11)) {
                TeamTemplate var16 = (TeamTemplate)var8.getValue(var10.getName());
                if (var16 != null) {
                    this.addTeamPrincipals(var16, var2, var7);
                }
            } else if (logger.isTraceEnabled()) {
                logger.trace("   IGNORING variable assignment: " + var11.getName());
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.addVariableRecipients - OUT");
        }

    }

    private ProcessData getVariables(ObjectReference var1) throws WTException {
        WfExecutionObject var2 = (WfExecutionObject)var1.getObject();
        if (var2 instanceof WfProcess) {
            return var2.getContext();
        } else {
            WfProcess var3 = ((WfActivity)var2).getParentProcess();
            return var3.getContext();
        }
    }

    private void addTeamPrincipals(Team var1, Vector var2) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.addTeamPrincipals");
        }

        Enumeration var3 = TeamHelper.service.getMembers(var1).elements();

        while(var3.hasMoreElements()) {
            WTPrincipalReference var4 = (WTPrincipalReference)var3.nextElement();
            if (!var4.isDisabled()) {
                this.addElementNoDup(var2, var4.getObject());
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   addTeamPrincipals - OUT");
        }

    }

    private void addTeamPrincipals(TeamTemplate var1, Vector var2, WfProcess var3) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.addTeamPrincipals - IN");
        }

        TeamTemplateReference var4 = null;
        TeamReference var5 = null;
        Team var6 = null;
        var4 = TeamTemplateReference.newTeamTemplateReference(var1);
        String var7 = "/" + AdministrativeDomainHelper.SYSTEM_DOMAIN;
        var5 = TeamHelper.service.createTeam(var4, (String)null, var7, var3);
        var6 = (Team)var5.getObject();
        this.addTeamPrincipals(var6, var2);
        if (logger.isTraceEnabled()) {
            logger.trace("   SaddTeamPrincipals - OUT");
        }

    }

    private void addTeamPrincipals(Vector var1, Vector var2, WfProcess var3) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.addTeamPrincipals from teams vector - IN:");

            for(int var4 = 0; var4 < var2.size(); ++var4) {
                logger.trace("      " + (var4 + 1) + ". " + ((WTPrincipal)var2.elementAt(var4)).getName());
            }
        }

        if (var1 != null) {
            Enumeration var10 = var1.elements();
            Object var5 = null;
            TeamTemplateReference var6 = null;
            TeamReference var7 = null;
            Team var8 = null;

            while(var10.hasMoreElements()) {
                var5 = var10.nextElement();
                if (var5 instanceof Team) {
                    this.addTeamPrincipals((Team)var5, var2);
                } else if (var5 instanceof TeamTemplate) {
                    var6 = TeamTemplateReference.newTeamTemplateReference((TeamTemplate)var5);
                    String var9 = "/" + AdministrativeDomainHelper.SYSTEM_DOMAIN;
                    var7 = TeamHelper.service.createTeam(var6, (String)null, var9, var3);
                    var8 = (Team)var7.getObject();
                    this.addTeamPrincipals(var8, var2);
                }
            }

            if (logger.isTraceEnabled()) {
                logger.trace("   addProjectRecipients from teams vector - OUT:");

                for(int var11 = 0; var11 < var2.size(); ++var11) {
                    logger.trace("      " + (var11 + 1) + ". " + ((WTPrincipal)var2.elementAt(var11)).getName());
                }
            }

        }
    }

    private String getStringRepresentation(Object var1, boolean var2, boolean var3) {
        String var4;
        if (var1 instanceof Date) {
            var4 = null;

            Locale var10;
            try {
                var10 = SessionHelper.manager.getLocale();
            } catch (WTException var8) {
                var10 = WTContext.getContext().getLocale();
                if (logger.isDebugEnabled()) {
                    logger.debug(var8.getLocalizedMessage(), var8);
                }
            }

            ResourceBundle var9 = ResourceBundle.getBundle("wt.util.utilResource", var10);
            String var6 = var9.getString("22");
            return WTStandardDateFormat.format((Date)var1, var6).toString();
        } else if (!(var1 instanceof WTObject)) {
            return var1.toString();
        } else {
            try {
                var4 = "";
                String var5 = IdentityFactory.getDisplayIdentifier(var1).getLocalizedMessage(SessionHelper.manager.getLocale());
                if (!var2 && !var3) {
                    return WfHtmlFormat.createObjectLinkNoImage((WTObject)var1, (String)null);
                } else if (var1 instanceof WTPart) {
                    var4 = ((WTPart)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof WTDocument) {
                    var4 = ((WTDocument)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof EPMDocument) {
                    var4 = ((EPMDocument)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof PDMLinkProduct) {
                    var4 = ((PDMLinkProduct)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof WTLibrary) {
                    var4 = ((WTLibrary)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof WTChangeIssue) {
                    var4 = ((WTChangeIssue)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof WTChangeOrder2) {
                    var4 = ((WTChangeOrder2)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof WTChangeRequest2) {
                    var4 = ((WTChangeRequest2)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof WTChangeActivity2) {
                    var4 = ((WTChangeActivity2)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof WTChangeProposal) {
                    var4 = ((WTChangeProposal)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof WTChangeInvestigation) {
                    var4 = ((WTChangeInvestigation)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof Managed) {
                    var4 = ((Managed)var1).getName();
                    return var4 + "," + var5;
                } else if (var1 instanceof Named) {
                    var4 = ((Named)var1).getName();
                    return var4 + "," + var5;
                } else {
                    return var5;
                }
            } catch (WTException var7) {
                if (logger.isDebugEnabled()) {
                    logger.debug(var7.getLocalizedMessage(), var7);
                }

                return "*** error: " + var7.getLocalizedMessage();
            }
        }
    }

    private WTPrincipal getCheckoutUser(WTPrincipalReference var1, Role var2, ActorRole var3, ObjectReference var4) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.getCheckoutUser - IN: " + var1 + ", role = " + var2 + ", actor role = " + var3 + ", robot = " + var4.getKey());
        }

        WTPrincipal var5 = null;
        WfProcess var6 = this.getParentProcess(var4);
        Object[] var7;
        if (var1 != null) {
            var5 = var1.getPrincipal();
            if (!(var5 instanceof WTUser)) {
                var7 = new Object[]{Role.toRole("ASSIGNEE"), var6.getName()};
                throw new WfException("wt.workflow.work.workResource", "32", var7);
            }
        } else if (var2 != null) {
            var5 = this.resolveSingletonRole(var2, var6);
        } else {
            if (var3 == null) {
                var7 = new Object[]{Role.toRole("ASSIGNEE"), var6.getName()};
                throw new WfException("wt.workflow.work.workResource", "32", var7);
            }

            var5 = this.resolveActor(var3, var6);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.getCheckoutUser - OUT: " + var5.getName());
        }

        return var5;
    }

    private WTPrincipal resolveSingletonRole(Role var1, WfProcess var2) throws WTException {
        int var3 = 0;
        WTPrincipalReference var4 = null;

        for(Enumeration var5 = var2.getPrincipals(var1); var5.hasMoreElements(); ++var3) {
            var4 = (WTPrincipalReference)var5.nextElement();
        }

        if (var3 != 1) {
            Object[] var6 = new Object[]{var1.getDisplay(), var2.getName()};
            throw new WfException("wt.workflow.work.workResource", "32", var6);
        } else {
            return var4.getPrincipal();
        }
    }

    private WTPrincipal resolveActor(ActorRole var1, WfProcess var2) throws WTException {
        WTPrincipalReference var3 = null;
        var3 = TeamServerHelper.service.resolveActorRole(var1, var2);
        if (var3 == null) {
            Object[] var4 = new Object[]{var1.getDisplay(), var2.getName()};
            throw new WfException("wt.workflow.work.workResource", "32", var4);
        } else {
            return var3.getPrincipal();
        }
    }

    private void resolveActors(Vector var1, WfProcess var2, WTDistributionList var3) throws WTException {
        ActorRole var4 = null;
        WTPrincipalReference var5 = null;
        Enumeration var6 = var1.elements();

        while(var6.hasMoreElements()) {
            var4 = (ActorRole)var6.nextElement();
            var5 = TeamServerHelper.service.resolveActorRole(var4, var2);
            if (var5 != null) {
                var3.addPrincipal(var5.getPrincipal());
            }
        }

    }

    private WfProcess getParentProcess(ObjectReference var1) throws WTException {
        WfProcess var2 = null;
        WfActivity var3 = (WfActivity)var1.getObject();
        WfContainer var4 = (WfContainer)var3.getParentProcessRef().getObject();
        if (var4 == null) {
            return var2;
        } else {
            if (var4 instanceof WfProcess) {
                if (((WfProcess)var4).isNested()) {
                    WfRequesterActivity var5 = (WfRequesterActivity)((WfProcess)var4).getRequester();
                    var2 = var5.getParentProcess();
                } else {
                    var2 = (WfProcess)var4;
                }
            } else {
                var2 = ((WfBlock)var4).getParentProcess();
            }

            return var2;
        }
    }

    private WorkItem getWorkItem(Persistable var1, WTPrincipal var2, Role var3) throws WTException {
        WorkItem var4 = null;

        for(QueryResult var5 = this.getWorkItems(var1, var2, var3); var5.hasMoreElements(); var4 = (WorkItem)var5.nextElement()) {
        }

        return var4;
    }

    private Enumeration getAssignedActivities(WfProcess var1, WfState var2) throws WTException {
        QuerySpec var3 = new QuerySpec(WfAssignedActivity.class);
        var3.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key", "=", getOid(var1)), 0);
        if (var2 != null) {
            var3.appendAnd();
            var3.appendOpenParen();
            Enumeration var4 = var2.getSubstates();
            WfState var5 = (WfState)var4.nextElement();
            var3.appendWhere(new SearchCondition(WfAssignedActivity.class, "state", "=", var5), 0);

            while(var4.hasMoreElements()) {
                var3.appendOr();
                var5 = (WfState)var4.nextElement();
                var3.appendWhere(new SearchCondition(WfAssignedActivity.class, "state", "=", var5), 0);
            }

            var3.appendCloseParen();
        }

        return PersistenceHelper.manager.find(var3);
    }

    private void handleBlockAndAssignedActivities(WfBlock var1, WfState var2) throws WTException {
        Enumeration var3 = this.getOnlyAssignedActivities(var1, WfState.OPEN_RUNNING);

        while(var3.hasMoreElements()) {
            WfAssignedActivity var4 = (WfAssignedActivity)var3.nextElement();
            var4.recreateWorkAssignment();
        }

        Vector var7 = this.getRequesterActivities((WfProcess)null, WfState.OPEN_RUNNING, var1);
        Enumeration var5 = var7.elements();

        while(var5.hasMoreElements()) {
            Object var6 = var5.nextElement();
            if (var6 instanceof WfBlock) {
                this.handleBlockAndAssignedActivities((WfBlock)var6, WfState.OPEN_RUNNING);
            }
        }

    }

    private Vector getRequesterActivities(WfProcess var1, WfState var2, WfBlock var3) throws WTException {
        Object var4 = null;
        if (var1 != null && var3 == null) {
            var4 = var1;
        } else if (var1 == null && var3 != null) {
            var4 = var3;
        }

        QuerySpec var5 = new QuerySpec(WfRequesterActivity.class);
        var5.appendWhere(new SearchCondition(WfRequesterActivity.class, "parentProcessRef.key", "=", getOid(var4)), 0);
        WfState var7;
        if (var2 != null) {
            var5.appendAnd();
            var5.appendOpenParen();
            Enumeration var6 = var2.getSubstates();
            var7 = (WfState)var6.nextElement();
            var5.appendWhere(new SearchCondition(WfRequesterActivity.class, "state", "=", var7), 0);

            while(var6.hasMoreElements()) {
                var5.appendOr();
                var7 = (WfState)var6.nextElement();
                var5.appendWhere(new SearchCondition(WfRequesterActivity.class, "state", "=", var7), 0);
            }

            var5.appendCloseParen();
        }

        QueryResult var10 = PersistenceHelper.manager.find(var5);
        var7 = null;
        Vector var8 = new Vector();

        while(var10.hasMoreElements()) {
            Object var9 = var10.nextElement();
            WfRequesterActivity var11 = (WfRequesterActivity)var9;
            var8.add(var11.getPerformerRef().getObject());
        }

        return var8;
    }

    private Enumeration getOnlyAssignedActivities(WfBlock var1, WfState var2) throws WTException {
        QuerySpec var3 = new QuerySpec(WfAssignedActivity.class);
        var3.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key", "=", getOid(var1)), 0);
        if (var2 != null) {
            var3.appendAnd();
            var3.appendOpenParen();
            Enumeration var4 = var2.getSubstates();
            WfState var5 = (WfState)var4.nextElement();
            var3.appendWhere(new SearchCondition(WfAssignedActivity.class, "state", "=", var5), 0);

            while(var4.hasMoreElements()) {
                var3.appendOr();
                var5 = (WfState)var4.nextElement();
                var3.appendWhere(new SearchCondition(WfAssignedActivity.class, "state", "=", var5), 0);
            }

            var3.appendCloseParen();
        }

        return PersistenceHelper.manager.find(var3);
    }

    private QuerySpec buildOwnerQuery(WTPrincipal var1) throws WTException {
        Vector var2 = new Vector();
        Long var3 = null;
        QuerySpec var4 = new QuerySpec(WorkItem.class);
        SearchCondition var5 = null;
        var4.appendOpenParen();
        if (var1 instanceof WTUser) {
            var3 = new Long(PersistenceHelper.getObjectIdentifier(var1).getId());
            this.addElementNoDup(var2, var3);
        }

        int var6 = var2.size();
        long[] var7 = new long[var6];

        for(int var8 = 0; var8 < var6; ++var8) {
            var7[var8] = (Long)var2.elementAt(var8);
        }

        var5 = new SearchCondition(WorkItem.class, "ownership.owner.key.id", var7, false);
        var4.appendWhere(var5);
        var4.appendCloseParen();
        return var4;
    }

    private SearchCondition onObject(Persistable var1) throws WTException {
        return new SearchCondition(WorkItem.class, "primaryBusinessObject.key.classname", "=", PersistentReference.newPersistentReference(var1).toString());
    }

    private SearchCondition onOwner(WTPrincipal var1) throws WTException {
        return OwnershipHelper.getSearchCondition(WorkItem.class, var1, true);
    }

    private SearchCondition onRole(Role var1) throws WTException {
        return new SearchCondition(WorkItem.class, "role", "=", var1);
    }

    private SearchCondition onSource(String var1) throws WTException {
        return new SearchCondition(WorkItem.class, "source", "=", var1);
    }

    private SearchCondition onTask(String var1) throws WTException {
        return new SearchCondition(WorkItem.class, "taskURLPathInfo", "LIKE", "%action=" + var1 + "%");
    }

    private SearchCondition onIncomplete() throws WTException {
        return new SearchCondition(WorkItem.class, "completedBy", true);
    }

    protected void performStartupProcess() throws ManagerException {
        if (logger.isTraceEnabled()) {
            logger.trace("Performing startup process for workflow service");
        }

        try {
            this.performCheckWTKeySeqNumberConsistency();
        } catch (WTException var8) {
            throw new ManagerException(this, var8);
        }

        SessionContext var1 = SessionContext.newContext();

        try {
            this.registerAsListener();

            try {
                SessionHelper.manager.setAdministrator();
            } catch (UserNotFoundException var9) {
                System.err.println("Workflow Service: failed to set Administrator (ok if installation) ");
                if (logger.isDebugEnabled()) {
                    logger.debug(var9.getLocalizedMessage(), var9);
                }

                return;
            }

            if (DELETE_COMPLETED_WORKITEMS_POOL_SIZE == 1) {
                this.deleteCompletedWorkItemsQueue = QueueHelper.manager.getQueue("DeleteCompletedWorkItemsQueue");
                if (this.deleteCompletedWorkItemsQueue == null) {
                    this.deleteCompletedWorkItemsQueue = this.createQueue("DeleteCompletedWorkItemsQueue", DELETE_COMPLETED_WORKITEMS_QUEUE_INTERVAL);
                }

            }
        } catch (WTException var10) {
            throw new ManagerException(this, var10, "Could not initialize Workflow service.");
        } finally {
            SessionContext.setContext(var1);
        }
    }

    private void performCheckWTKeySeqNumberConsistency() throws WTException {
        logger.debug("IN StandardWorkflowService.performCheckWTKeySeqNumberConsistency");
        long var1 = 0L;
        long var3 = 0L;
        long var5 = 0L;
        long var7 = 0L;
        long var9 = 0L;
        long var11 = 0L;

        try {
            var9 = System.currentTimeMillis();
            var11 = System.currentTimeMillis();

            QuerySpec var13;
            int var14;
            ClassAttribute var15;
            SQLFunction var16;
            QueryResult var17;
            BigDecimal var18;
            try {
                var13 = new QuerySpec();
                var14 = var13.appendClassList(WfProcess.class, false);
                var15 = new ClassAttribute(WfProcess.class, "key");
                var16 = SQLFunction.newSQLFunction("MAX", var15);
                var13.appendSelect(var16, new int[]{var14}, false);
                var13.setAdvancedQueryEnabled(true);
                var17 = PersistenceServerHelper.manager.query(var13);

                while(var17.hasMoreElements()) {
                    var18 = (BigDecimal)((Object[])((Object[])var17.nextElement()))[0];
                    if (var18 != null && var3 < var18.longValue()) {
                        var3 = var18.longValue();
                        logger.debug("wtkey from WfProcess = " + var3);
                    }
                }
            } catch (Exception var19) {
                logger.error("Error while finding max wtkey from WfProcess");
                throw new WTException(var19);
            }

            try {
                var13 = new QuerySpec();
                var14 = var13.appendClassList(WfAssignedActivity.class, false);
                var15 = new ClassAttribute(WfAssignedActivity.class, "key");
                var16 = SQLFunction.newSQLFunction("MAX", var15);
                var13.appendSelect(var16, new int[]{var14}, false);
                var13.setAdvancedQueryEnabled(true);
                var17 = PersistenceServerHelper.manager.query(var13);

                while(var17.hasMoreElements()) {
                    var18 = (BigDecimal)((Object[])((Object[])var17.nextElement()))[0];
                    if (var18 != null && var5 < var18.longValue()) {
                        var5 = var18.longValue();
                        logger.debug("wtkey from WfAssignedActivity = " + var5);
                    }
                }
            } catch (Exception var20) {
                logger.error("Error while finding max wtkey from WfAssignedActivity");
                throw new WTException(var20);
            }

            var7 = var5 > var3 ? var5 : var3;
            logger.debug("Max wtkey in the workflow tables is = " + var7 + " Time spent::" + (System.currentTimeMillis() - var11));
            var11 = System.currentTimeMillis();
            String var22 = PersistenceHelper.manager.getCurrentSequence(WfExecutionObject.class, "key");
            logger.debug("Time taken to getCurrentSequence::" + (System.currentTimeMillis() - var11));
            var1 = Long.parseLong(var22);
            logger.debug(" In StandardWorkflowService.performCheckWTKeySeqNumberConsistency Next wtkey for WfExecutionObject = " + var1);
        } catch (Exception var21) {
            logger.error("Exception In method StandardWorkflowService.performCheckWTKeySeqNumberConsistency ");
            throw new WTException(var21);
        }

        if (var1 < var7) {
            logger.debug("Next wtkey sequence value is less than or equal to max wtkey sequence for WfExecutionObject");
            System.out.println("max WtKey value from DB is::" + var7 + "Current sequence value is::" + var1);
            throw new WTException("wt.workflow.work.workResource", "DUPLICATE_WTKEY_SEQ_ERROR", (Object[])null);
        } else {
            logger.debug("OUT StandardWorkflowService.performCheckWTKeySeqNumberConsistency. Time Spent::" + (System.currentTimeMillis() - var9));
        }
    }

    protected void registerAsListener() throws ManagerException {
        this.getManagerService().addEventListener(new ServiceEventListenerAdapter(this.getConceptualClassname()) {
            public void notifyVetoableEvent(Object var1) throws WTException {
                ContainerTeamServiceEvent var2 = (ContainerTeamServiceEvent)var1;
                ContainerTeam var3 = (ContainerTeam)var2.getTarget();
                List var4 = ContainerTeamHelper.service.findContainersForTeam(var3);
                WTUser var5 = var2.getUser();
                WTUser var6 = var2.getRemovedUser();
                int var7 = 0;

                for(int var8 = var4.size(); var7 < var8; ++var7) {
                    ContainerTeamManaged var9 = (ContainerTeamManaged)var4.get(var7);
                    if (!(var9 instanceof Project2)) {
                        StandardWorkflowService.this.reassignWorkItems(WTContainerRef.newWTContainerRef(var9), var6, var5);
                    }
                }

            }
        }, ContainerTeamServiceEvent.generateEventKey("REPLACE_MEMBER"));
        this.getManagerService().addEventListener(new ServiceEventListenerAdapter(this.getConceptualClassname()) {
            public void notifyVetoableEvent(Object var1) throws WTException {
                FolderServiceEvent var2 = (FolderServiceEvent)var1;
                WTObject var3 = (WTObject)var2.getEventTarget();
            }
        }, FolderServiceEvent.generateEventKey("PRE_CHANGE_FOLDER"));
        this.getManagerService().addEventListener(new ServiceEventListenerAdapter(this.getConceptualClassname()) {
            public void notifyVetoableEvent(Object var1) throws WTException {
                FolderServiceEvent var2 = (FolderServiceEvent)var1;
                WTObject var3 = (WTObject)var2.getEventTarget();
            }
        }, FolderServiceEvent.generateEventKey("POST_CHANGE_FOLDER"));
    }

    private WTDistributionList buildDistributionList(WfProcess var1, Vector var2, Vector var3, Vector var4) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.buildDistributionList: process = " + var1.getName() + ", recipients = " + var2 + ", roles = " + var3 + ", actorRoles = " + var4);
        }

        WTDistributionList var5 = new WTDistributionList();
        boolean var6 = SessionServerHelper.manager.setAccessEnforced(false);
        Persistable var7 = null;
        WTReference var8 = null;

        try {
            var8 = var1.getBusinessObjectReference(new ReferenceFactory());
            if (var8 != null) {
                var7 = var8.getObject();
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(var6);
        }

        Enumeration var9 = var2.elements();

        WTPrincipal var10;
        while(var9.hasMoreElements()) {
            var10 = (WTPrincipal)var9.nextElement();
            if (AccessControlHelper.manager.hasAccess(var10, var7, AccessPermission.READ)) {
                var5.addPrincipal(var10);
            }
        }

        this.resolveActors(var4, var1, var5);
        TeamReference var17 = var1.getTeamId();
        if (var17 == null && var1.isNested()) {
            WfRequesterActivity var18 = (WfRequesterActivity)var1.getRequester();
            WfProcess var11 = var18.getParentProcess();
            var17 = var11.getTeamId();
        }

        if (var17 != null) {
            Team var19 = (Team)var17.getObject();
            if (var19 != null) {
                var5.addRoleHolder(var19);
            }
        }

        var10 = null;
        Team var20 = (Team)var1.getTeamId().getObject();
        WTPrincipal var12 = WfEngineHelper.service.getResponsible(var1);
        Enumeration var13 = null;
        Enumeration var14 = var3.elements();

        while(var14.hasMoreElements()) {
            Role var21 = (Role)var14.nextElement();
            var13 = var20.getPrincipalTarget(var21);
            if (var13.hasMoreElements()) {
                if (var13.nextElement() != null) {
                    var5.addRole(var21);
                } else {
                    var5.addPrincipal(var12);
                }
            } else {
                var5.addPrincipal(var12);
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.buildDistributionList - OUT");
        }

        return var5;
    }

    private TemplateEmailNotification createNotification(WTDistributionList var1, String var2, String var3, WfProcess var4, ObjectReference var5) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.createNotification: subject = " + var2 + ", process = " + var4.getName());
        }

        WfActivity var6 = (WfActivity)var5.getObject();
        TemplateEmailNotification var7 = new TemplateEmailNotification(var1);
        String var8 = null;

        try {
            var8 = (String)((String)var6.getContext().getVariable("msgTemplateName").getValue());
        } catch (NullPointerException var14) {
            if (logger.isDebugEnabled()) {
                logger.debug("=> StandardWorkflowService.createNotification: Template Name not found. Null Pointer Exception occurred ");
                logger.debug(var14.getLocalizedMessage(), var14);
            }
        }

        WfVariable var9 = var6.getContext().getVariable("subjectCheck");
        boolean var10 = false;
        if (var9 != null) {
            var10 = (Boolean)var9.getValue();
            if (logger.isDebugEnabled()) {
                logger.debug("subjectCheck variable :: " + var9.getValue());
            }
        }

        if (var10) {
            Locale var11 = null;

            try {
                var11 = SessionHelper.manager.getLocale();
            } catch (WTException var13) {
                var11 = WTContext.getContext().getLocale();
                if (logger.isDebugEnabled()) {
                    logger.debug(var13.getLocalizedMessage(), var13);
                }
            }

            var2 = WTMessage.getLocalizedMessage("wt.workflow.work.workResource", "141", new Object[]{var6.getName(), var4.getName()}, var11);
        }

        Object[] var15 = new Object[5];
        if (var2 != null) {
            var15[0] = var2;
        } else {
            var15[0] = var6.getName();
        }

        var7.setSubjectResource("wt.workflow.work.workResource");
        var7.setSubjectMessageKey("7");
        var7.setSubjectInserts(var15);
        NotificationRobotProcessor var12 = new NotificationRobotProcessor(var6, var3);
        var12.setContextObj(var6);
        var7.setTemplateProcessor(var12);
        if (var8 != null && !var8.equals("") && !var8.equalsIgnoreCase("General") && !var8.equalsIgnoreCase("GeneralPlain")) {
            logger.trace("=> StandardWorkflowService.createNotification reffering to tname:" + var8);
            var7.setTemplate(TemplateName.getWorkNotification(var8));
        } else {
            logger.trace("=> StandardWorkflowService.createNotification referr ROBOT_NOTIFICATION_TEMPLATE:" + TemplateName.getWorkNotification("NotificationRobot"));
            var7.setTemplate(TemplateName.getWorkNotification("NotificationRobot"));
        }

        var7.setSender(this.getSender(var4));
        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.createNotification - OUT");
        }

        return var7;
    }

    private EmailTemplateNotificationRequest createEmailNotification(WfExternalRecipientList var1, String var2, String var3, WfProcess var4, ObjectReference var5, String var6, WfEmailAttachments var7) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.createEmailNotification: subject = " + var2 + ", process = " + var4.getName() + ", template = " + var6);
        }

        boolean var8 = false;
        boolean var9 = false;
        boolean var10 = false;
        WfActivity var11 = (WfActivity)var5.getObject();
        WfProcess var12 = var11.getParentProcess();
        ProcessData var13 = var12.getContext();
        NotificationRobotProcessor var14 = new NotificationRobotProcessor(var11, var3, var6, true);
        Vector var15 = new Vector();
        Vector var18;
        if (var7 != null) {
            Vector var16 = var7.getPrimaryContentHoldersAsVector();
            Vector var17 = var7.getSecondaryContentHoldersAsVector();
            var18 = var7.getPrimativeAttachmentsVector();
            if (!var16.isEmpty()) {
                var8 = true;
            }

            if (!var17.isEmpty()) {
                var9 = true;
            }

            if (!var18.isEmpty()) {
                var10 = true;
            }

            var16.addAll(var17);
            var16.addAll(var18);
            HashSet var19 = new HashSet();
            new ReferenceFactory();
            Enumeration var21 = var16.elements();

            while(var21.hasMoreElements()) {
                String var22 = (String)var21.nextElement();
                var19.add(var22);
            }

            Object[] var29 = var19.toArray();

            for(int var23 = 0; var23 < var29.length; ++var23) {
                Object var24 = var13.getValue((String)var29[var23]);
                if (var24 instanceof Persistable && !(var24 instanceof EPMDocument)) {
                    if (PersistenceHelper.isPersistent((Persistable)var24)) {
                        var15.addElement(ObjectReference.newObjectReference((Persistable)var24));
                    }
                } else if (logger.isTraceEnabled()) {
                    logger.trace("=> StandardWorkflowService.createEmailNotification:unable to attach object: " + var24 + " as it is either EPMDocument or it is not peristable.");
                }
            }
        }

        String var25 = "";
        String[] var26 = (String[])var1.getAddrs();
        if (var6 != null && var6.length() > 0) {
            var25 = TemplateName.getWorkNotification(var6);
        } else {
            var25 = TemplateName.getWorkNotification("NotificationRobot");
        }

        var18 = null;
        int var28 = 0;
        if (var8) {
            var28 |= 1;
        }

        if (var9) {
            var28 |= 2;
        }

        if (var10) {
            var28 |= 4;
        }

        EmailTemplateNotificationRequest var27 = EmailTemplateNotificationRequest.newEmailTemplateNotificationRequest(var26, var15, var14, var25, var28, var5);
        Object[] var20 = new Object[5];
        if (var2 != null) {
            var20[0] = var2;
        } else {
            var20[0] = var11.getName();
        }

        var27.setSubject("wt.workflow.work.workResource", "7", var20);
        var27.setSender(this.getSender(var4));
        if (logger.isTraceEnabled()) {
            logger.trace("   StandardWorkflowService.createEmailNotification - OUT");
        }

        return var27;
    }

    private String getSender(WfProcess var1) throws WTException {
        if (logger.isDebugEnabled()) {
            logger.debug("   StandardWorkflowService.getSender - IN...");
        }

        String var2 = DEFAULT_NOTIFICATION_SENDER;
        if (USE_DEFAULT_NOTIFICATION_SENDER_EMAIL) {
            if (logger.isDebugEnabled()) {
                logger.debug("   StandardWorkflowService.getSender - OUT: returning default notification sender ::" + var2);
            }

            return var2;
        } else {
            WTPrincipal var3 = var1.getCreator().getPrincipal();
            if (var3 instanceof WTUser) {
                try {
                    var2 = (new InternetAddress(((WTUser)var3).getEMail())).getAddress();
                } catch (AddressException var5) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(var5.getLocalizedMessage(), var5);
                    }
                } catch (NullPointerException var6) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(var6.getLocalizedMessage(), var6);
                    }
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("   StandardWorkflowService.getSender - OUT  ::" + var2);
            }

            return var2;
        }
    }

    private void addElementsNoDup(Vector var1, Enumeration var2) {
        while(var2.hasMoreElements()) {
            this.addElementNoDup(var1, var2.nextElement());
        }

    }

    private void addElementNoDup(Vector var1, Object var2) {
        if (var1.indexOf(var2) < 0) {
            var1.addElement(var2);
        }

    }

    private boolean setTaskBasedRights(WorkItem var1, WTPrincipalReference var2, WfVariable var3) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("   user = " + var2 + ", variable = " + var3.getName() + ", permissions = " + var3.getPermissionList());
        }

        boolean var4 = SessionServerHelper.manager.setAccessEnforced(false);

        boolean var7;
        try {
            PermissionSet var5 = var3.getPermissionList();
            if (var5 == null || var5.isEmpty()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("   permission set is null or empty, returning...");
                }

                boolean var16 = false;
                return var16;
            }

            Object var6 = null;
            var6 = var3.getValue();
            if (var6 != null && var6 instanceof WTObject && var6 instanceof AdHocControlled) {
                WTObject var17 = (WTObject)var6;
                if (logger.isTraceEnabled()) {
                    logger.trace("   object = " + getOid(var17) + AccessControlHelper.manager.showPermissions((AdHocControlled)var17));
                }

                Vector var8 = new Vector();
                Enumeration var9 = var5.getPermissions();

                while(var9.hasMoreElements()) {
                    AccessPermission var10 = (AccessPermission)var9.nextElement();
                    var8.add(var10);
                }

                var17 = (WTObject)WfEngineServerHelper.lock(var17);
                if (!AccessControlHelper.manager.getPermissions((AdHocControlled)var17, var2, AdHocAccessKey.WNC_LIFECYCLE).isEmpty()) {
                    AccessControlHelper.manager.removePermissions((AdHocControlled)var17, AdHocAccessKey.WNC_WORK_ITEM, getOid(var1).getId());
                }

                AccessControlHelper.manager.addPermissions((AdHocControlled)var17, var2, var8, AdHocAccessKey.WNC_WORK_ITEM, getOid(var1).getId());
                PersistenceServerHelper.manager.update(var17, false);
                if (logger.isTraceEnabled()) {
                    logger.trace("   set rights - OUT (" + var3.getName() + ") " + AccessControlHelper.manager.showPermissions((AdHocControlled)var17));
                }

                VariablePermissionTable var18 = var1.getVariablePermissionMap();
                if (var18 == null) {
                    var18 = new VariablePermissionTable();
                }

                var18.put(getOid(var17), var5);
                var1.setVariablePermissionMap(var18);
                return true;
            }

            var7 = false;
        } catch (WTRuntimeException var14) {
            logger.error("StandardWorkflowService.setTaskBasedRights()::WTRuntimeException in setTaskBasedRights API.", var14);
            throw var14;
        } finally {
            SessionServerHelper.manager.setAccessEnforced(var4);
        }

        return var7;
    }

    private void revokeTaskBasedRights(WTObject var1, WTPrincipal var2, Vector var3, long var4) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("   revoke - IN: obj id  = " + getOid(var1) + ", user = " + var2.getName());
        }

        MethodContext var6 = null;
        var6 = MethodContext.getContext();
        Object var7 = null;
        if (var6 != null) {
            var7 = var6.get("bypassforreassign");
        }

        WTPrincipalReference var8 = WTPrincipalReference.newWTPrincipalReference(var2);
        if (AccessControlHelper.manager.getPermissions((AdHocControlled)var1, var8, AdHocAccessKey.WNC_WORK_ITEM, var4).size() != 0) {
            var1 = (WTObject)WfEngineServerHelper.lock(var1);
            if (var7 == null) {
                AccessControlHelper.manager.removePermissions((AdHocControlled)var1, var8, var3, AdHocAccessKey.WNC_WORK_ITEM, var4);
            }

            PersistenceServerHelper.manager.update(var1, false);
            if (logger.isTraceEnabled()) {
                logger.trace("   revoke - OUT: " + getOid(var1) + ", ad hoc acl = " + AccessControlHelper.manager.showPermissions((AdHocControlled)var1));
            }

        }
    }

    private SearchCondition onProject2Dom(AdministrativeDomain var1) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> onProject2Dom: " + getOid(var1));
        }

        return new SearchCondition(WfAssignedActivity.class, "domainRef.key", "=", getOid(var1));
    }

    private SearchCondition onWAA(WfAssignedActivity var1) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> onWAA :: " + getOid(var1));
        }

        return new SearchCondition(WorkItem.class, "source.key", "=", getOid(var1));
    }

    private void reassignWorkItems(WTContainerRef var1, WTUser var2, WTUser var3) throws WTException {
        Transaction var4 = new Transaction();

        try {
            var4.start();
            QueryResult var5 = this.getUncompletedWorkItems((WTPrincipal)var2, (WTContainerRef)var1);

            while(var5.hasMoreElements()) {
                this.delegate((WorkItem)var5.nextElement(), var3, true);
            }

            var4.commit();
            var4 = null;
        } finally {
            if (var4 != null) {
                var4.rollback();
            }

        }
    }

    public void delegate(WorkItem var1, WTPrincipal var2, boolean var3) throws WTException, WfException {
        this.delegate(var1, var2, var3, (String)null);
    }

    public void delegate(WorkItem var1, WTPrincipal var2, boolean var3, String var4) throws WTException, WfException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.delegate " + getOid(var1) + " from " + var1.getOwnership().getOwner().getName() + " to " + var2.getName() + ", comment = " + var4);
        }

        Transaction var5 = new Transaction();

        try {
            var5.start();
            if (var1.isComplete()) {
                throw new WfException("wt.workflow.work.workResource", "34", (Object[])null);
            }

            WTPrincipal var6 = OwnershipHelper.getOwner(var1);
            WfAssignment var7 = (WfAssignment)var1.getParentWA().getObject();
            WfAssignedActivity var8 = (WfAssignedActivity)var1.getSource().getObject();
            if (!var3) {
                WfProcess var9 = var8.getParentProcess();
                if (!AccessControlHelper.manager.hasAccess(var2, var9, AccessPermission.READ)) {
                    AccessControlHelper.manager.emitAccessEvent("NOT_AUTHORIZED", var9, AccessPermission.MODIFY, new WTMessage("wt.workflow.work.workResource", "115", (Object[])null));
                    throw new WfException("wt.workflow.work.workResource", "115", (Object[])null);
                }
            }

            var7.delegate(var1, var2, var8);
            WfEngineServerHelper.service.changeAssignmentEvent(var8, var2, var6, var1.getRole(), var4, var1.getActionPerformed());
            var5.commit();
            var5 = null;
        } finally {
            if (var5 != null) {
                var5.rollback();
            }

        }

        if (logger.isTraceEnabled()) {
            logger.trace("   delegate - OUT");
        }

    }

    private static ObjectIdentifier getOid(Object var0) {
        if (var0 == null) {
            return null;
        } else {
            return var0 instanceof ObjectReference ? (ObjectIdentifier)((ObjectReference)var0).getKey() : PersistenceHelper.getObjectIdentifier((Persistable)var0);
        }
    }

    private String getValue(ProcessData var1, String var2, boolean var3, boolean var4) {
        WfVariable[] var5 = var1.getVariableList();
        int var6 = 0;

        for(int var7 = var5.length; var6 < var7; ++var6) {
            if (var2.equals(var5[var6].getName())) {
                Object var8 = var5[var6].getValue();
                if (var8 == null) {
                    return "";
                }

                return this.getStringRepresentation(var8, var3, var4);
            }
        }

        return null;
    }

    private SQLFunction getTruncateFunction(ClassAttribute var1) throws QueryException, WTPropertyVetoException {
        Datastore var2 = DataServiceFactory.getDefault().getDatastore();
        if (logger.isTraceEnabled()) {
            logger.trace("StandardWorkflowService.getTruncateFunction(): data store is " + var2.getClass().getName());
        }

        SQLFunction var3 = null;
        if (var2 instanceof SQLServer) {
            SQLFunction var4 = SQLFunction.newSQLFunction("CONVERT");
            var4.setArgumentAt(new KeywordExpression("varchar"), 0);
            var4.setArgumentAt(var1, 1);
            var4.setArgumentAt(new KeywordExpression("100"), 2);
            var3 = SQLFunction.newSQLFunction("TO_DATE", var4);
        } else if (var2 instanceof Oracle) {
            var3 = SQLFunction.newSQLFunction("TRUNC");
            var3.setArgumentAt(var1, 0);
            var3.setArgumentAt(new ConstantExpression("MI"), 1);
        }

        return var3;
    }

    private Object getPoolObject(String var1, WorkItem var2) throws WTException {
        Object var3 = null;
        if (!var1.equals("")) {
            if (var1.equalsIgnoreCase("Context Team")) {
                return this.getContainerTeamReference(var2);
            }

            if (var1.startsWith(Role.class.getName())) {
                var3 = Role.toEnumeratedType(var1);
            } else {
                var3 = WfResourcePool.getObjectReference(var1);
            }
        }

        return var3;
    }

    private ContainerTeamReference getContainerTeamReference(WorkItem var1) throws WTException {
        ContainerTeamReference var2 = null;
        WfProcess var3 = this.getParentProcess(var1.getSource());
        WTContainer var4 = var3.getContainer();
        if (var4 instanceof ContainerTeamManaged) {
            var2 = ((ContainerTeamManaged)var4).getContainerTeamReference();
        }

        return var2;
    }

    private Vector<WTPrincipalReference> getRoleMembers(Role var1, WorkItem var2) throws WTException {
        Vector var3 = new Vector();
        ContainerTeamReference var4 = this.getContainerTeamReference(var2);
        Enumeration var5 = ((ContainerTeam)var4.getObject()).getPrincipalTarget(var1);

        while(var5.hasMoreElements()) {
            var3.add((WTPrincipalReference)var5.nextElement());
        }

        return var3;
    }

    private String replaceVariables(String var1, ProcessData var2, boolean var3, boolean var4) throws WTException {
        if (logger.isTraceEnabled()) {
            logger.trace("=> StandardWorkflowService.replaceVariables - IN: '" + var1 + "'");
        }

        if (var1 == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("   null text, returning...");
            }

            return "";
        } else {
            StringBuffer var5 = new StringBuffer();
            int var6 = 0;
            boolean var7 = logger.isDebugEnabled();

            while(true) {
                while(var6 < var1.length()) {
                    if (var1.charAt(var6) == START_VARIABLE && (var6 == 0 || var1.charAt(var6 - 1) != '\\')) {
                        StringBuffer var8 = new StringBuffer();

                        while(var6 < var1.length() && var1.charAt(var6) != END_VARIABLE) {
                            var8.append(var1.charAt(var6++));
                        }

                        if (logger.isTraceEnabled()) {
                            logger.trace("   delimited text found: " + var8.toString().substring(1));
                        }

                        if (var6 >= var1.length()) {
                            var5.append(var8);
                        } else {
                            String var9 = var8.toString().substring(1);
                            String var10 = this.getValue(var2, var9, var3, var4);
                            if (logger.isTraceEnabled()) {
                                logger.trace("   replacing value: " + var9 + " = '" + var10 + "'");
                            }

                            if (var10 != null) {
                                var5.append(var10);
                                ++var6;
                            } else {
                                var8.append(var1.charAt(var6++));
                                var5.append(var8);
                            }
                        }
                    } else {
                        var5.append(var1.charAt(var6++));
                    }
                }

                if (logger.isTraceEnabled()) {
                    logger.trace("   replaceVariables - OUT: '" + var5 + "'");
                }

                return var5.toString();
            }
        }
    }

    private SearchCondition getSCForOpenAssignemnts(Boolean var1) throws WTException {
        boolean var2 = false;
        if (var1) {
            var2 = true;
        }

        SearchCondition var3 = new SearchCondition(WorkItem.class, "completedBy", var2);
        return var3;
    }

    private SearchCondition getSCForIsReassigned(Boolean var1) throws WTException {
        String var2 = "FALSE";
        if (var1) {
            var2 = "TRUE";
        }

        SearchCondition var3 = new SearchCondition(WorkItem.class, "reassigned", var2);
        return var3;
    }

    private SearchCondition getSCForIsAccepted(Boolean var1) throws WTException {
        String var2 = "<>";
        if (var1) {
            var2 = "=";
        }

        SearchCondition var3 = new SearchCondition(WorkItem.class, "status", var2, WfAssignmentState.ACCEPTED);
        return var3;
    }

    private SearchCondition getSCForIsDelegated(Boolean var1) throws WTException {
        boolean var2 = true;
        if (var1) {
            var2 = false;
        }

        SearchCondition var3 = new SearchCondition(WorkItem.class, "origOwner.key.classname", var2);
        return var3;
    }

    private SearchCondition getSCForIsAssignendToMe(Boolean var1, WTPrincipal var2) throws WTException {
        boolean var3 = true;
        if (var1) {
            var3 = false;
        }

        long[] var4 = this.getParentGroupsForUser(var2, !var3);
        SearchCondition var5 = new SearchCondition(WorkItem.class, "ownership.owner.key.id", var4, false);
        return var5;
    }

    private long[] getParentGroupsForUser(WTPrincipal var1, boolean var2) throws WTException {
        Vector var3 = new Vector();
        Long var4 = null;
        if (var1 instanceof WTUser) {
            var4 = new Long(PersistenceHelper.getObjectIdentifier(var1).getId());
            this.addElementNoDup(var3, var4);
            Object var5 = null;
            WTPrincipalReference var6 = null;
            if (var2) {
                boolean var7 = SessionServerHelper.manager.setAccessEnforced(false);

                try {
                    Enumeration var8 = ((WTUser)var1).parentGroups();

                    while(var8.hasMoreElements()) {
                        var6 = (WTPrincipalReference)var8.nextElement();
                        var4 = new Long(var6.getObjectId().getId());
                        this.addElementNoDup(var3, var4);
                    }
                } finally {
                    SessionServerHelper.manager.setAccessEnforced(var7);
                }
            }
        }

        int var12 = var3.size();
        long[] var13 = new long[var12];

        for(int var14 = 0; var14 < var12; ++var14) {
            var13[var14] = (Long)var3.elementAt(var14);
        }

        return var13;
    }

    private QuerySpec getWorkItemQuery(Class var1, boolean var2, SearchCondition var3, Boolean var4, Boolean var5, Boolean var6, Boolean var7, Boolean var8, boolean var9, Integer var10) throws WTException, WTPropertyVetoException {
        QuerySpec var11 = new QuerySpec(var1);
        var11.setDescendantQuery(var2);
        var11.setAdvancedQueryEnabled(true);
        if (var10 != null && var10 > 0) {
            var11.setQueryLimit(var10);
        }

        var11.appendWhere(var3, new int[]{0});
        SearchCondition var12;
        if (var4 != null) {
            var12 = this.getSCForOpenAssignemnts(var4);
            var11.appendAnd();
            var11.appendWhere(var12, new int[]{0});
        }

        if (var5 != null) {
            var12 = this.getSCForIsReassigned(var5);
            var11.appendAnd();
            var11.appendWhere(var12, new int[]{0});
        }

        if (var6 != null) {
            var12 = this.getSCForIsAccepted(var6);
            var11.appendAnd();
            var11.appendWhere(var12, new int[]{0});
        }

        if (var7 != null) {
            var12 = this.getSCForIsDelegated(var7);
            var11.appendAnd();
            var11.appendWhere(var12, new int[]{0});
        }

        var11.appendAnd();
        var11.appendOpenParen();
        var11.appendWhere(new SearchCondition(WorkItem.class, WorkItem.STATUS, SearchCondition.NOT_EQUAL, "COMPLETED"), new int[]{0});
        var11.appendOr();
        var11.appendOpenParen();
        var11.appendWhere(new SearchCondition(WorkItem.class, WorkItem.STATUS, SearchCondition.EQUAL, "COMPLETED"), new int[]{0});
        var11.appendAnd();
        String date = "2020/1/1";
        String value = SystemConfigurationUtil.getValue("WORKITEMDATE");
        if(value != null && !"".equals(value)){
            date = value;
        }
        Date dateFrom = null;
        try {
            dateFrom = WTStandardDateFormat.parse(date, "yyyy/MM/dd");
        } catch(ParseException e) {
            e.printStackTrace();
        }
        SearchCondition sc11 = new SearchCondition(WorkItem.class, WorkItem.MODIFY_TIMESTAMP, SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime()));
        var11.appendWhere(sc11, new int[]{0});
        var11.appendCloseParen();
        var11.appendCloseParen();

        if (var9) {
            boolean var15 = true;
            ClassAttribute var13 = new ClassAttribute(var1, "thePersistInfo.createStamp");
            OrderBy var14 = new OrderBy(var13, var15);
            var11.appendOrderBy(var14, new int[]{0});
        }

        if (logger.isTraceEnabled()) {
            logger.trace("StandardWorkflowService.getWorkItemQuery() current query is ==> " + var11);
        }

        return var11;
    }

    private ObjectVectorIfc getObjectVector(QueryResult var1) {
        ObjectVector var2 = new ObjectVector();
        Enumeration var3 = var1.getEnumeration();

        while(var3.hasMoreElements()) {
            var2.addElement(var3.nextElement());
        }

        return var2;
    }

    private QueryResult executeQuery(QuerySpec var1) throws WTException, WfException {
        QueryResult var2 = null;

        try {
            var2 = PersistenceServerHelper.manager.query(var1);
            return var2;
        } catch (PartialResultException var5) {
            QueryResult var4 = var5.getQueryResult();
            return var4;
        } catch (QueryException var6) {
            if (logger.isDebugEnabled()) {
                logger.debug(var6.getLocalizedMessage(), var6);
            }

            throw new WfException(var6, (String)null);
        }
    }

    private long getLastGeneratedSeqValORA(String var1) throws WTException, SQLException {
        long var2 = -1L;
        WTConnection var4 = null;

        try {
            var4 = (WTConnection)MethodContext.getContext().getConnection();
            PreparedStatement var5 = var4.prepareStatement("SELECT LAST_NUMBER from ALL_SEQUENCES WHERE SEQUENCE_NAME=? AND UPPER(SEQUENCE_OWNER) = '" + DSProperties.DB_SCHEMA_USER.toUpperCase() + "'");
            Throwable var6 = null;

            try {
                var5.setString(1, var1);
                logger.debug("Executing query to find LAST_NUMBER");
                ResultSet var7 = var5.executeQuery();
                Throwable var8 = null;

                try {
                    if (var7.next()) {
                        var2 = var7.getLong("LAST_NUMBER");
                        logger.debug("QUERY returned results " + var2);
                    }
                } catch (Throwable var46) {
                    var8 = var46;
                    throw var46;
                } finally {
                    if (var7 != null) {
                        if (var8 != null) {
                            try {
                                var7.close();
                            } catch (Throwable var45) {
                                var8.addSuppressed(var45);
                            }
                        } else {
                            var7.close();
                        }
                    }

                }
            } catch (Throwable var48) {
                var6 = var48;
                throw var48;
            } finally {
                if (var5 != null) {
                    if (var6 != null) {
                        try {
                            var5.close();
                        } catch (Throwable var44) {
                            var6.addSuppressed(var44);
                        }
                    } else {
                        var5.close();
                    }
                }

            }
        } catch (Exception var50) {
            throw new WTException(var50);
        } finally {
            if (var4 != null) {
                var4.release();
            }

        }

        return var2;
    }

    public void completeActivity(WfActivity var1, WfProcess var2, String var3, WTPrincipalReference var4, String var5) throws WTException {
        if (logger.isDebugEnabled()) {
            logger.debug(" IN StandardWorkflowService.completeActivity activity:" + var1 + " process::" + var2 + " routingEvent::" + var3);
        }

        try {
            if (var1 instanceof WfAssignedActivity) {
                WfAssignedActivity var6 = (WfAssignedActivity)var1;
                WfAssignedActivityTemplate var7 = (WfAssignedActivityTemplate)var6.getTemplate().getObject();
                if (var7.getTaskName().equalsIgnoreCase("SUBMIT")) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("In completeActivity TaskName ::" + var7.getTaskName());
                    }

                    LifeCycleManaged var8 = (LifeCycleManaged)((LifeCycleManaged)var2.getBusinessObjectReference(new ReferenceFactory()).getObject());
                    var8 = LifeCycleHelper.service.submitForApproval(var8);
                }
            }
        } catch (WTException var12) {
            var12.printStackTrace();
            throw var12;
        }

        Vector var13 = new Vector();
        String[] var14 = var3.split(",");
        if (var14.length > 0) {
            var13 = new Vector(Arrays.asList(var14));
        }

        WfEngineHelper.service.complete(var1, var13);
        Locale var15 = WTContext.getContext().getLocale();
        ResourceBundle var9 = ResourceBundle.getBundle("wt.clients.workflow.manager.managerRB", var15);

        try {
            WfEventHelper.createVotingEvent((WfEventAuditType)null, var1, (Role)null, var4, var5, var13, false, false);
        } catch (WTException var11) {
            var11.printStackTrace();
            throw var11;
        }

        if (logger.isDebugEnabled()) {
            logger.debug(" OUT StandardWorkflowService.completeActivity");
        }

    }

    public void completeActivity(ObjectReference var1, String var2) throws WTException {
        if (logger.isDebugEnabled()) {
            logger.debug(" IN StandardWorkflowService.completeActivity self:" + var1 + "routingEvent:" + var2);
        }

        WfActivity var3 = (WfActivity)var1.getObject();
        WfProcess var4 = var3.getParentProcess();
        WTGroup var5 = WTContainerHelper.service.getExchangeContainer().getAdministrators();
        Locale var6 = WTContext.getContext().getLocale();
        ResourceBundle var7 = ResourceBundle.getBundle("wt.clients.workflow.manager.managerRB", var6);
        WTPrincipal var8 = SessionHelper.manager.getAdministrator();
        WTPrincipal var9 = SessionHelper.getPrincipal();
        String var10 = var7.getString("COMMENT_SYSTEM_COMPLETE");
        WTPrincipalReference var11 = WTPrincipalReference.newWTPrincipalReference(var8);
        SessionHelper.manager.setAdministrator();

        try {
            WorkflowHelper.service.completeActivity(var3, var4, var2, var11, var10);
        } catch (WTException var16) {
            throw var16;
        } finally {
            SessionHelper.manager.setPrincipal(var9.getName());
        }

        if (logger.isDebugEnabled()) {
            logger.debug(" OUT StandardWorkflowService.completeActivity");
        }

    }

    static {
        try {
            WTProperties var0 = WTProperties.getLocalProperties();
            START_VARIABLE = var0.getProperty("wt.workflow.startVariableChar", "{").charAt(0);
            END_VARIABLE = var0.getProperty("wt.workflow.endVariableChar", "}").charAt(0);
            DEFAULT_NOTIFICATION_SENDER = var0.getProperty("wt.notify.notificationSenderEmail");
            USE_DEFAULT_NOTIFICATION_SENDER_EMAIL = var0.getProperty("wt.workflow.work.UseDefaultNotificationSenderEmail", false);
            DO_DYNAMIC_UPDATE = var0.getProperty("wt.workflow.dynamicParticipantUpdate", true);
            DISALLOW_PM_COMPLETE_TASK = var0.getProperty("com.ptc.netmarkets.work.disallowPMCompleteTask", false);
            DELETE_COMPLETED_WORKITEMS_POOL_SIZE = 1;
            NO_DEDICATED_DELETE_COMPLETED_WORKITEMS = true;
            DELETE_COMPLETED_WORKITEMS_QUEUE_INTERVAL = var0.getProperty("wt.workflow.engine.deleteCompletedWorkItemsQueueInterval", 60);
            DELETE_COMPLETED_WORKITEMS_PREFS = var0.getProperty("wt.workflow.engine.deleteCompletedWorkItemsPref", "DELETE_COMPLETED_WORKITEMS_PREF");
        } catch (Throwable var1) {
            if (logger.isDebugEnabled()) {
                logger.debug("WorkflowService: Error reading wt.workflow.* properties");
                logger.debug(var1.getLocalizedMessage(), var1);
            }

            throw new ExceptionInInitializerError(var1);
        }
    }

    class DeleteCompletedWorkItemsThread implements Runnable {
        private WTPrincipalReference principalRef;
        private WTContainerRef containerRef;

        public DeleteCompletedWorkItemsThread(WTPrincipalReference var2, WTContainerRef var3) {
            this.principalRef = var2;
            this.containerRef = var3;
        }

        public void run() {
            WTPrincipal var1 = null;
            WTPrincipal var2 = null;

            try {
                if (StandardWorkflowService.logger.isTraceEnabled()) {
                    StandardWorkflowService.logger.trace("=> DeleteCompletedWorkItemsThread.run: ");
                }

                var1 = SessionHelper.manager.getAdministrator();
                var2 = SessionContext.setEffectivePrincipal(var1);
                ProcessingQueue var3 = StandardWorkflowService.this.getDeleteCompletedWorkItemQueue();
                Class[] var4 = new Class[]{WTPrincipalReference.class, WTContainerRef.class};
                Object[] var5 = new Object[]{this.principalRef, this.containerRef};
                var3.addEntry(var1, "targetDeleteCompletedWorkItems", StandardWorkflowService.CLASSNAME, var4, var5);
                if (StandardWorkflowService.logger.isTraceEnabled()) {
                    StandardWorkflowService.logger.trace("=> DeleteCompletedWorkItemsThread.run: made a queue entry");
                }
            } catch (WTException var9) {
                if (StandardWorkflowService.logger.isDebugEnabled()) {
                    StandardWorkflowService.logger.debug(var9.getLocalizedMessage(), var9);
                }

                throw new WTRuntimeException(var9);
            } finally {
                SessionContext.setEffectivePrincipal(var2);
            }

        }
    }
}
