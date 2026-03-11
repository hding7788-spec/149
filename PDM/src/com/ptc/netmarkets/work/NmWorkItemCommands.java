/* bcwti
 *
 * Copyright (c) 2010 Parametric Technology Corporation (PTC). All Rights Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */

package com.ptc.netmarkets.work;

import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.forumPosting.NmPostingCommands;
import com.ptc.netmarkets.model.NmChangeModel;
import com.ptc.netmarkets.model.NmObject;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.project.NmProject;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmStringBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.netmarkets.util.misc.NmDate;
import com.ptc.netmarkets.util.table.NmHTMLTable;
import com.ptc.netmarkets.util.misc.*;
import com.ptc.netmarkets.model.NmException;

import ext.casc.workflow.signtrue.zp.SignatureService;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.String;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletInputStream;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceManager;
import wt.fc.PersistentReference;
import wt.fc.QueryResult;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceServerHelper;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.util.HTMLEncoder;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTProperties;
import wt.util.WTAttributeNameIfc;
import wt.util.WTRuntimeException;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.definer.WfVariableInfo;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkflowHelper;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.recent.ObjectVisitedInfo;
import wt.recent.RecentlyVisitedHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.workflow.work.WorkItem;
import wt.workflow.worklist.worklistResource;

import org.apache.log4j.Logger;

import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleService;
import wt.log4j.LogR;
import wt.method.RemoteAccess;

// Preserved unmodeled dependency

/* version                                    uid
   X-10                                     = 957977401221134810L
*/

/**
 *
 * <BR><BR><B>Supported API: </B>false
 * <BR><BR><B>Extendable: </B>false
 *
 * @version   1.0
 **/

public class NmWorkItemCommands implements Externalizable,RemoteAccess {
   // --- Attribute Section ---
   private static final String RESOURCE = "com.ptc.netmarkets.work.workResource";
   private static final String CLASSNAME = NmWorkItemCommands.class.getName();

   /**
    * <BR><BR><B>Supported API: </B>false
    **/
   public static final NmWorkItemService service = wt.services.ServiceFactory.getService(NmWorkItemService.class);
   static final long serialVersionUID = 1;
   public static final long EXTERNALIZATION_VERSION_UID = 957977401221134810L;
   protected static final long OLD_FORMAT_VERSION_UID = 3199308805842382398L;

   // WARNING: Fields placed in this section will not be generated into externalization methods.
   private static final Logger logger = LogR.getLogger("com.ptc.netmarkets.work");

   public static final String WI_TO_CONT = "WI_TO_CONT";

   public static final String ROUTER_EVENT =        "WfUserEvent";
   public static final String ROUTER_LIST =         "WfUserEventList";
   public static final String COMMENTS =            "comments";
   public static final String ACTIVITY_VAR =        "WfActVar";
   public static final String CUSTOM_ACTIVITY_VAR = "CustActVar";
   public static final String ROUTER_CHECK =		"WfRouterCheck";
   public static final String ESIG =                "WfESignature";
   public static final String VOTE_ACTION = 		"voteAction";
   public static final String ESIGPASSWD =          "signatureEngine_password";
   public static final String ESIGUSER  =           "signatureEngine_username";
   public static final String ASSIGN_TO =           "assignTo";
   public static final String ASSIGN_COMMENTS =		"assignComments";
   public static final String AUTOMATE_FAST_TRACK=	"automateFastTrack";
   public static final ArrayList ASSIGNMENTS_LIST=   null;
   public static final String DEADLINE =            "deadline";
   public static final String TASK_KEY =            "tk";
   public static final String MY_TASKS =            "my";
   public static final String PROJECT_TASKS =       "prj";
   public static final String PRODUCT_TASKS =       "prd";
   public static final String LIBRARY_TASKS =       "lib";
   public static final String PLAN_TASKS =          "pln";
   public static final String DETAILS_TASKS =       "dtl";
   public static final String VIEW_DOCUMENT =       "viewDocument";
   public static final String OVERVIEW_TASKS =      "orv";
   public static final String TASK_OID       =      "task-oid"; // This is for redirecting to details pages
   public static final String TASK_TYPE      =      "task-type"; // This is for redirecting to details pages

   //======Following are the column ids for jsp table
   public static final String ASSIGNMENT_IMAGE            = "ASSIGNMENT_IMAGE";
   public static final String ASSIGNMENT_REASSIGNED       = "ASSIGNMENT_REASSIGNED";
   public static final String PBO_TYPEICON				  = "PBO_TYPEICON";
   public static final String ASSIGNMENT_NAME             = "ASSIGNMENT_NAME";
   public static final String ASSIGNMENT_ROWACTIONS       = "ASSIGNMENT_ROWACTIONS";
   public static final String ASSIGNMENT_SUBJECT          = "ASSIGNMENT_SUBJECT";
   public static final String ASSIGNMENT_OBJECT          = "ASSIGNMENT_OBJECT";
   public static final String ASSIGNMENT_SUBJECT_LC_STATE = "ASSIGNMENT_SUBJECT_LC_STATE";
   public static final String ASSIGNMENT_DEADLINE         = "ASSIGNMENT_DEADLINE";
   public static final String ASSIGNMENT_PERCENT_DONE     = "ASSIGNMENT_PERCENT_DONE";
   public static final String ASSIGNMENT_STATUS           = "ASSIGNMENT_STATUS";
   public static final String ASSIGNMENT_CREATED          = "ASSIGNMENT_CREATED";
   public static final String ASSIGNMENT_OWNER            = "ASSIGNMENT_OWNER";
   public static final String ASSIGNMENT_CONTAINER        = "ASSIGNMENT_CONTAINER";
   public static final String ASSIGNMENT_ROLE             = "ASSIGNMENT_ROLE";
   public static final String ASSIGNMENT_CREATED_BY       = "ASSIGNMENT_CREATED_BY";

   //======

   //====Following are the columns/attributes that help in filtering assignmnets
   public static final String ASSIGNMENT_IS_OVERDUE = "ASSIGNMENT_IS_OVERDUE";
   public static final String ASSIGNMENT_IS_ASSIGNED_TO_ME = "ASSIGNMENT_IS_ASSIGNED_TO_ME";
   public static final String ASSIGNMENT_IS_CREATED_BY_ME = "ASSIGNMENT_IS_CREATED_BY_ME";
   public static final String OPEN_ASSIGNMENTS = "OPEN_ASSIGNMENTS";
   public static final String WORKITEM_TYPE = "WORKITEM_TYPE";
   public static final String WORKITEM_STATUS = "workItemStatus";
   public static final String DISCUSSED = "DISCUSSED";

   //=====Following are some of the new keys used in jsp tables.
   public static final String TABLE_ID                    = "TABLE_ID";
   public static final String TABLE_VIEW_ID				  = "TABLE_VIEW_ID";
   public static final String HOME_WORKLIST               = "HOME_WORKLIST";
   public static final String HOME_WORKLIST_TABLE_ID      = "netmarkets.assignments.list";
   public static final String HOME_OVERVIEW_WORKLIST      = "HOME_OVERVIEW_WORKLIST";
   public static final String HOME_OVERVIEW_WORKLIST_TABLE_ID      = "netmarkets.overview.assignments.list";
   public static final String PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID = "projectmanagement.overview.assignments.list";
   public static final String PROJECT_WORKLIST            = "PROJECT_WORKLIST";
   public static final String PROJECT_WORKLIST_TABLE_ID   = "netmarkets.project.assignments.list";
   public static final String PLAN_WORKLIST_TABLE_ID      = "projectmanagement.plan.assignments.list";
   public static final String PRODUCT_WORKLIST            = "PRODUCT_WORKLIST";
   public static final String PRODUCT_WORKLIST_TABLE_ID   = "netmarkets.product.assignments.list";
   public static final String LIBRARY_WORKLIST            = "LIBRARY_WORKLIST";
   public static final String LIBRARY_WORKLIST_TABLE_ID   = "netmarkets.library.assignments.list";
   public static final String CHANGE_WORKLIST_TABLE_ID   = "netmarkets.change.assignments.list";
   //=====
   //=====Keys used across service and datautilities.
   public static final String LIST_TYPE                                = "LIST_TYPE";
   public static final String WfAATOWI_KEY                             = "WfAATOWI_KEY";
   public static final String CONTAINER_MAP                            = "CONTAINER_MAP";
   public static final String CURRENT_CONTAINER                        = "CURRENT_CONTAINER";
   public static final String EXT_PARTICIPANT_MAP                      = "EXT_PARTICIPANT_MAP";
   public static final String WORKLIST_TABLE_USE_MORE                  = "WORKLIST_TABLE_USE_MORE";
   public static final String WORKLIST_TABLE_HIDE_ROLE_COL             = "WORKLIST_TABLE_HIDE_ROLE_COL";
   public static final String WORKLIST_TABLE_REPLACE_ROLE_WITH_CREATOR = "WORKLIST_TABLE_REPLACE_ROLE_WITH_CREATOR";
   //=====
   public static final String SUBMIT_TASK_ACTION ="submit";
   public static final String REVIEW_TASK_ACTION = "review";
   public static final String PROMOTE_TASK_ACTION = "promote";
   public static final String VOTE = "vote";
//====
   static {
      try {
         // name size determination
         WTProperties properties = WTProperties.getLocalProperties();
      }
      catch (Throwable t)
      {
         if(logger.isDebugEnabled()){
    		  logger.debug("Error initializing " + StandardNmWorkItemService.class.getName ());
	     	  logger.debug(t.getLocalizedMessage(), t);
	     }
         throw new ExceptionInInitializerError(t);
      }
   }

   // --- Operation Section ---

   /**
    * Writes the non-transient fields of this class to an external source.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     output
    * @exception java.io.IOException
    **/
   public void writeExternal( ObjectOutput output )
            throws IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );
   }

   /**
    * Reads the non-transient fields of this class from an external source.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     input
    * @exception java.io.IOException
    * @exception java.lang.ClassNotFoundException
    **/
   public void readExternal( ObjectInput input )
            throws IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();                  // consume UID
      readVersion( this, input, readSerialVersionUID, false, false );  // read fields
   }

   /**
    * Reads the non-transient fields of this class from an external source.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     thisObject
    * @param     input
    * @param     readSerialVersionUID
    * @param     passThrough
    * @param     superDone
    * @return    boolean
    * @exception java.io.IOException
    * @exception java.lang.ClassNotFoundException
    **/
   protected boolean readVersion( NmWorkItemCommands thisObject, ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone )
            throws IOException, ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == 957977401221134810L )
         return readVersion957977401221134810L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }

   /**
    * Reads the non-transient fields of this class from an external source,
    * which is not the current version.
    *
    * @param     input
    * @param     readSerialVersionUID
    * @param     passThrough
    * @param     superDone
    * @return    boolean
    * @exception java.io.IOException
    * @exception java.lang.ClassNotFoundException
    **/
   private boolean readOldVersion( ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone )
            throws IOException, ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == OLD_FORMAT_VERSION_UID ) {          // handle previous version
      }
      else
         throw new java.io.InvalidClassException( CLASSNAME, "Local class not compatible:"
                           + " stream classdesc externalizationVersionUID=" + readSerialVersionUID
                           + " local class externalizationVersionUID=" + EXTERNALIZATION_VERSION_UID );

      return success;
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    NmHTMLTable
    * @exception wt.util.WTException
    **/
   public static NmHTMLTable list( NmCommandBean cb )
            throws WTException {
      if (cb.getRequest().getParameter("worksv") != null) {
        cb.getMap().put("listType", cb.getRequest().getParameter("worksv"));
      }

      cb.getMap().put("maxRows", cb.getRequest().getParameter("maxRows"));

      return service.list(cb);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @exception wt.util.WTException
    **/
   public static void delete( NmCommandBean cb )
            throws WTException {
      service.delete(cb);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    NmObject
    * @exception wt.util.WTException
    **/
   public static NmObject view( NmCommandBean cb )
            throws WTException {
      return service.view(cb);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @exception wt.util.WTException
    **/
   public static FormResult complete( NmCommandBean cb )
   			throws WTException {

      if (logger.isTraceEnabled()) {
         logger.trace("=> NmWorkItemCommands.complete");
	   }

	   Vector<String> eventList = null;
	   String comments = null;
	   Enumeration parameterNames = cb.getRequest().getParameterNames ();

	   boolean AutomateFastTrackChecked = false;

	   while (parameterNames.hasMoreElements ()) { // loop through all the user's form fields
		   String plainKey = (String) parameterNames.nextElement();
		   String key = NmCommandBean.convert(plainKey);
		   if (logger.isTraceEnabled()) {
                    logger.trace("=> NmWorkItemCommands.complete: " + key + ", " + cb.getTextParameter(plainKey));
		   }

		   if (key.indexOf(ROUTER_EVENT) >= 0 && key.lastIndexOf("old") == -1) {
			   String eventValue = null;
			   if (key.indexOf(ROUTER_CHECK) >= 0) {
				   eventValue = key.substring(key.indexOf(ROUTER_CHECK) + NmWorkItemCommands.ROUTER_CHECK.length(), key.lastIndexOf("___"));
			   } else {
				   eventValue = cb.getTextParameter(plainKey);
			   }

			   if (eventList == null) {
				   eventList = new Vector<String>();
			   }
			   eventList.addElement(eventValue);
		   }
		   else if (key.indexOf(NmWorkItemCommands.CUSTOM_ACTIVITY_VAR) >= 0 && !key.endsWith("old")) {
			   int start = key.indexOf(NmWorkItemCommands.CUSTOM_ACTIVITY_VAR) + NmWorkItemCommands.CUSTOM_ACTIVITY_VAR.length();
			   int end = key.lastIndexOf(NmWorkItemCommands.CUSTOM_ACTIVITY_VAR);

			   cb.getMap().put(key.substring(start, end), cb.getTextParameter(plainKey));
		   }
		   else if (key.indexOf(NmWorkItemCommands.VOTE_ACTION) >= 0 && key.lastIndexOf("old") == -1) {
			   cb.getMap().put(NmWorkItemCommands.VOTE_ACTION, cb.getTextParameter(plainKey));
		   }
		   else if (key.indexOf(ESIGPASSWD) >= 0 && !key.endsWith("old")) {
			   cb.getMap().put(ESIGPASSWD,cb.getTextParameter (plainKey));
		   }
		   else if (key.indexOf(ESIGUSER) >= 0 && !key.endsWith("old")) {
			   cb.getMap().put(ESIGUSER,cb.getTextParameter (plainKey));
		   } else if (key.indexOf("___"+COMMENTS+"___") >= 0 && !key.endsWith("___old")) {
			   // add the "___" to be sure to grab only comments
			   comments = cb.getTextParameter(plainKey);

			   //This boolean value represent the key's string "___automateFastTrack___automateFastTrack"
			   //as being distinctly different from the OTHER key's string "___automateFastTrack___automateFastTrack___old"
		   } else if (key.indexOf("___"+AUTOMATE_FAST_TRACK+"___"+AUTOMATE_FAST_TRACK) >= 0 ) {
			   // See if 'auto create cn' checkbox was set
			   HashMap checkedOptions = cb.getChecked();
			   if(checkedOptions != null) {
				   Object autoFastTrackCheckBox = checkedOptions.get(AUTOMATE_FAST_TRACK);
				   if(autoFastTrackCheckBox != null && autoFastTrackCheckBox instanceof ArrayList){
					   ArrayList autoBox = (ArrayList)autoFastTrackCheckBox;
					   String checkBoxName = (String)autoBox.get(0);
					   if (checkBoxName != null && checkBoxName.equals(AUTOMATE_FAST_TRACK)) {
						   AutomateFastTrackChecked = true; // signifies that Automate Fast Track Checkbox was checked by user!
					   }
				   }
			   }
                    if (logger.isTraceEnabled()) {
                       logger.trace("NmWorkItemCommands.complete: auto create ECN: " + AutomateFastTrackChecked);
		    }
		   }
	   }

	   if ((comments == null) || (comments != null && comments.trim().length() == 0)) {
		   comments = " ";
	   }

	   cb.getMap().put(AUTOMATE_FAST_TRACK, String.valueOf(AutomateFastTrackChecked));

	   if (logger.isTraceEnabled()) {
	      logger.trace("   comments = " + comments);
	   }

	   cb.getMap().put(ROUTER_LIST, eventList);
	   cb.getMap().put(COMMENTS, comments);

	   //cb.getMap().put(AUTOMATE_FAST_TRACK, cb.getTextParameter(AUTOMATE_FAST_TRACK));

       HttpServletRequest request = cb.getRequest();
	   HttpSession session = cb.getRequest().getSession();
       HashMap sessionCheckedUsers = (HashMap)session.getAttribute("CHECKED_USERS");
       if(sessionCheckedUsers!=null){

			//SPR-1848147, SPR 1959110 Start
			Boolean isPaged=(Boolean)session.getAttribute("isPaged");
			if(isPaged!=null && !isPaged){
				cb.getMap().put("isPaged", "false");
			}
			//SPR-1848147, SPR 1959110 End


    	   HashMap oldChecked = cb.getOldChecked();
    	   HashMap checked=cb.getChecked();
    	   for (Object oldCheckedKey : oldChecked.keySet()) {
    		    if(!checked.containsKey(oldCheckedKey)){
    	   			sessionCheckedUsers.remove(oldCheckedKey);
    	   		}
				else
    		    {
    		    	oldChecked.put(oldCheckedKey, checked.get(oldCheckedKey));
    		    	sessionCheckedUsers.put(oldCheckedKey, oldChecked.get(oldCheckedKey));
    		    }
    	   }
		   HashMap unChecked = cb.getUnChecked();
		   for (Object unCheckedKey : unChecked.keySet()) {
  				sessionCheckedUsers.remove(unCheckedKey);
		   }
    	   sessionCheckedUsers.remove("ROLE_MAP");
    	   checked.putAll(sessionCheckedUsers);
    	   cb.setChecked(checked);
       }

	   service.complete (cb, getParams (cb));
	   session.removeAttribute("CHECKED_USERS");
	   session.removeAttribute("isPaged");

	   NmURL worklistURL = new NmURL ();
	   worklistURL.setType (NmWorkItem.TYPE);
	   worklistURL.setAction (NmAction.Command.LIST);
	   worklistURL.setOid (null);

	   try {
		   String taskKey = (String)cb.getSessionBean().getStorage().get(TASK_KEY);
		   if((taskKey==null) || ((taskKey!=null)&&(!taskKey.equals("null"))))
		   {
			   //taskKey = session.getValue(TASK_KEY).toString();
			   taskKey = session.getAttribute(TASK_KEY).toString();
			   session.removeAttribute(TASK_KEY);
		   }
		   if (taskKey.equals (PROJECT_TASKS)) {
			   worklistURL.setAction("listProjectAssignments");
			   worklistURL.setOid(new NmOid(NmProject.TYPE, (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
		   }else if( taskKey.equals (PRODUCT_TASKS) ){
			   worklistURL.setAction("listProductAssignments");
			   worklistURL.setOid(new NmOid("object", (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
		   }else if( taskKey.equals (LIBRARY_TASKS) ){
			   worklistURL.setAction("listLibraryAssignments");
			   worklistURL.setOid(new NmOid("object", (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
		   }else if (taskKey.equals (PLAN_TASKS)) {
			   if(cb.getContainer().getConceptualClassname().equalsIgnoreCase("wt.projmgmt.admin.Project2"))
			   {
				   worklistURL.setType (NmProject.TYPE);
				   worklistURL.setAction (NmAction.Command.VIEW_PLAN);
				   worklistURL.setOid(new NmOid(NmProject.TYPE, (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
			   }
		   } else if (taskKey.equals(DETAILS_TASKS)) {
			   //worklistURL.setType(cb.getSessionBean().getStorage().get(TASK_TYPE).toString());
			   final String taskTypeStr="workflow";
			   worklistURL.setType(NmAction.Type.OBJECT);
			   worklistURL.setAction(NmAction.Command.VIEW);

			   String currentTaskType = (String)cb.getSessionBean().getStorage().get(TASK_TYPE);
			   if((currentTaskType==null)||((currentTaskType!=null)&&(!currentTaskType.equals("null"))))
			   {
				   currentTaskType = session.getAttribute(TASK_TYPE).toString();
				   session.removeAttribute(TASK_TYPE);
			   }
			   String currentTaskOID = (String)cb.getSessionBean().getStorage().get(TASK_OID);
			   if((currentTaskOID==null)||((currentTaskOID!=null)&&(!currentTaskOID.equals("null"))))
			   {
				   currentTaskOID = session.getAttribute(TASK_OID).toString();
				   session.removeAttribute(TASK_OID);
			   }

			   if(cb.getActionOid().getRef() instanceof WorkItem && currentTaskType.equals(taskTypeStr) )
			   {
				   WorkItem workItem = (WorkItem) cb.getActionOid().getRef();
				   if(workItem!=null && workItem.getPrimaryBusinessObject()!=null)
					   worklistURL.setOid(new NmOid(workItem.getPrimaryBusinessObject().getObject()));
				   else
					   worklistURL.setOid(new NmOid(currentTaskOID));

			   }
			   else
			   {
				   worklistURL.setOid(new NmOid(currentTaskOID));
			   }

		   } else if (taskKey.equals(OVERVIEW_TASKS)) {
			   worklistURL.setType(NmAction.Type.NETMARKETS);
			   worklistURL.setAction(NmAction.Command.VIEW);
			   worklistURL.setOid(null);
		   } else if (taskKey.equals(VIEW_DOCUMENT)) {

			   String currentTaskOID = (String)cb.getSessionBean().getStorage().get(TASK_OID);
			   if((currentTaskOID==null)||((currentTaskOID!=null)&&(!currentTaskOID.equals("null"))))
			   {
				   currentTaskOID = session.getAttribute(TASK_OID).toString();
				   session.removeAttribute(TASK_OID);
			   }

			   final ConcurrentHashMap storage = cb.getSessionBean().getStorage();
			   worklistURL.setAction(NmAction.Command.VIEW);
			   worklistURL.setType(NmAction.Type.OBJECT);
			   worklistURL.setOid(new NmOid(currentTaskOID));
		   }
	   } catch (NullPointerException npe) {
		   //Let the redirect continue to the My Tasks list if we couldn't get the session bean state
	       worklistURL.setType(NmAction.Type.NETMARKETS);
           worklistURL.setAction(NmAction.Command.VIEW);
           worklistURL.setOid(null);
           if(logger.isDebugEnabled()){
               logger.debug(npe.getLocalizedMessage(), npe);
    	   }
	   }

	   FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
   	   result.setNextAction(FormResultAction.FORWARD);
	   result.setForcedUrl(worklistURL.toString2(cb.getUrlFactoryBean()));
        if (logger.isTraceEnabled()) {
          logger.trace("   complete - OUT");
		}

		return result;
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    NmChangeModel[]
    * @exception wt.util.WTException
    **/
   public static FormResult update( NmCommandBean cb )
            throws WTException {
	  NmChangeModel[] changeModel = service.update (cb, getParams (cb));
      String comments = cb.getTextParameter("comments");

	  if (comments!= null && comments.trim().length()>0)
      {
         NmPostingCommands.service.createPostingForWorkflow(cb,comments);
      }

      NmURL worklistURL = new NmURL();
      worklistURL.setType(NmWorkItem.TYPE);
      worklistURL.setAction(NmAction.Command.LIST);
      worklistURL.setOid(null);

	  HttpSession session = cb.getRequest().getSession();

      try
      {
         String taskKey = (String)cb.getSessionBean().getStorage().get(TASK_KEY);
		 if((taskKey==null) || ((taskKey!=null)&&(!taskKey.equals("null"))))
		 {
			   //taskKey = session.getValue(TASK_KEY).toString();
			   taskKey = session.getAttribute(TASK_KEY).toString();
			   session.removeAttribute(TASK_KEY);
		 }

         //SPR 1253936: NmSessionBean.getCurrentProject doesn't work here since it has been changed
         //             Use LastVisitedContainer from RecentlyVisitedHelper to retrive the project oid
         NmOid projectOid = null;
         ObjectVisitedInfo recentInfo = RecentlyVisitedHelper.service.getLastVisitedContainer();
         if (recentInfo != null)
            projectOid = new NmOid(recentInfo.getOID());

         if (taskKey.equals(PROJECT_TASKS))
         {
           worklistURL.setAction(NmAction.Command.PROJECT_TASK_LIST);
           worklistURL.setOid(projectOid);
         } else if (taskKey.equals(PLAN_TASKS)) {
           worklistURL.setType(NmProject.TYPE);
           worklistURL.setAction("view_plan");
           worklistURL.setOid(projectOid);
         } else if (taskKey.equals(DETAILS_TASKS)) {

		   String currentTaskType = (String)cb.getSessionBean().getStorage().get(TASK_TYPE);
		   if((currentTaskType==null)||((currentTaskType!=null)&&(!currentTaskType.equals("null"))))
		   {
			 currentTaskType = session.getAttribute(TASK_TYPE).toString();
			 session.removeAttribute(TASK_TYPE);
		   }

		   String currentTaskOID = (String)cb.getSessionBean().getStorage().get(TASK_OID);
		   if((currentTaskOID==null)||((currentTaskOID!=null)&&(!currentTaskOID.equals("null"))))
		   {
		     currentTaskOID = session.getAttribute(TASK_OID).toString();
		     session.removeAttribute(TASK_OID);
		   }

           worklistURL.setType(currentTaskType);
           worklistURL.setOid(new NmOid(currentTaskOID));
           worklistURL.setAction(NmAction.Command.VIEW);
         } else if (taskKey.equals(OVERVIEW_TASKS)) {
           worklistURL.setType(NmAction.Type.NETMARKETS);
           worklistURL.setAction(NmAction.Command.VIEW);
           worklistURL.setOid(null);
         }
     } catch (NullPointerException npe) {
         //Let the redirect continue to the My Tasks list if we couldn't get the session bean state
         worklistURL.setType (NmWorkItem.TYPE);
         worklistURL.setAction (NmAction.Command.LIST);
         worklistURL.setOid (null);
         if(logger.isDebugEnabled()){
    	   logger.debug(npe.getLocalizedMessage(), npe);
    	 }
     }
	  //cb.setRedirectURL(worklistURL);
      FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
      result.setNextAction(FormResultAction.FORWARD);
      result.setForcedUrl(worklistURL.toString2(cb.getUrlFactoryBean()));
      return result;
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
 * @return
    * @exception wt.util.WTException
    **/
   public static FormResult reassign( NmCommandBean cb )
            throws WTException {
      cb.getMap().put(ASSIGN_TO, cb.getTextParameter(ASSIGN_TO));
      cb.getMap().put(ASSIGN_COMMENTS, cb.getTextParameter(ASSIGN_COMMENTS));

      SignatureService.reassign(cb);

      service.reassign(cb);


      FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
      result.setNextAction(FormResultAction.REFRESH_OPENER);
      //result.setURL(worklistURL.toString2(cb.getUrlFactoryBean()));
      return result;
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    NmHTMLTable
    * @exception wt.util.WTException
    **/
   public static NmHTMLTable listProject( NmCommandBean cb )
            throws WTException {
      if (cb.getRequest().getParameter("worksv") != null)
        cb.getMap().put("listType", cb.getRequest().getParameter("worksv"));

	  return service.listProject(cb);
	}

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     oid
    * @return    Enumeration
    * @exception wt.util.WTException
    **/
   public static Enumeration getProjectMembers( NmOid oid )
            throws WTException {
      return service.getProjectMembers(oid);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @exception wt.util.WTException
    **/
   public static void updateDeadline( NmCommandBean cb )
            throws WTException {
      cb.getMap().put(DEADLINE, cb.getTextParameter(DEADLINE));

      service.updateDeadline(cb);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     oid
    * @return    NmDate
    * @exception wt.util.WTException
    **/
   public static NmDate getDeadline( NmOid oid )
            throws WTException {
      return service.getDeadline(oid);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    NmHTMLTable
    * @exception wt.util.WTException
    **/
   public static NmHTMLTable listProduct( NmCommandBean cb )
            throws WTException {
      if (cb.getRequest().getParameter("worksv") != null)
        cb.getMap().put("listType", cb.getRequest().getParameter("worksv"));

      return service.listProduct(cb);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    NmHTMLTable
    * @exception wt.util.WTException
    **/
   public static NmHTMLTable listLibrary( NmCommandBean cb )
            throws WTException {
      if (cb.getRequest().getParameter("worksv") != null)
        cb.getMap().put("listType", cb.getRequest().getParameter("worksv"));

      return service.listLibrary(cb);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    FormResult
    * @exception wt.util.WTException
    **/
   public static FormResult accept( NmCommandBean cb )
            throws WTException {
      return service.accept(cb);
   }

   /**
   *
   * <BR><BR><B>Supported API: </B>false
   *
   * @param     cb
   * @return    FormResult
   * @exception wt.util.WTException
   **/
  public static FormResult unaccept( NmCommandBean cb )
           throws WTException {
     return service.unaccept(cb);
  }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @exception wt.util.WTException
    **/
   public static void multireassign( NmCommandBean cb )
            throws WTException {
      cb.getMap().put(ASSIGN_TO, cb.getTextParameter(ASSIGN_TO));
      cb.getMap().put(ASSIGN_COMMENTS, cb.getTextParameter(ASSIGN_COMMENTS));

      ArrayList oids = cb.getSelectedOidForPopup();
      //logger.trace("CB->oids.size(): [" + oids.size()+"]");
      cb.getMap().put(ASSIGNMENTS_LIST, oids);

      SignatureService.multireassign(cb);

      service.multireassign(cb);
   }

   /**
    * Gets an NmHtmlTable where the roles are the principals and the columns
    * are the roles for the workitem represented by oid.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     oid  OID for the NMWorkItem
    * @param     cb
    * @param     showStatus
    * @return    NmHTMLTable
    * @exception wt.util.WTException
    **/
   public static NmHTMLTable getRouteSetupTable( NmOid oid, NmCommandBean cb, boolean showStatus )
            throws WTException {
      return null;
   }

   /**
    * Saves a workitem ProcessData without completing the workitem.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @exception wt.util.WTException
    **/
   public static FormResult save( NmCommandBean cb ) throws WTException {
       FormResult result=new FormResult(FormProcessingStatus.SUCCESS);
       if (logger.isTraceEnabled()) {
           logger.trace("=> NmWorkItemCommands.save");
       }
       Enumeration parameterNames = cb.getRequest().getParameterNames ();
       while (parameterNames.hasMoreElements ()) {
           String plainKey = (String) parameterNames.nextElement ();
           String key = NmCommandBean.convert (plainKey);
           if (logger.isTraceEnabled()) {
               logger.trace("=> NmWorkItemCommands.save: " + key + ", " + cb.getTextParameter(plainKey));
           }

           if (key.indexOf(NmWorkItemCommands.CUSTOM_ACTIVITY_VAR) >= 0 && key.lastIndexOf("old") == -1) {
               int start = key.indexOf(NmWorkItemCommands.CUSTOM_ACTIVITY_VAR) + NmWorkItemCommands.CUSTOM_ACTIVITY_VAR.length() ;
               int end = key.lastIndexOf(NmWorkItemCommands.CUSTOM_ACTIVITY_VAR);
               cb.getMap().put(key.substring (start,end), cb.getTextParameter (plainKey));
           }
           if(key.indexOf("___"+COMMENTS+"___") >= 0 && !key.endsWith("___old")) {
               // add the "___" to be sure to grab only comments
               cb.getMap().put(COMMENTS, cb.getTextParameter(plainKey));
           }
           if(key.indexOf("___"+VOTE_ACTION+"___") >= 0 && !key.endsWith("___old")) {
               // add the "___" to be sure to grab only voteactions
               cb.getMap().put(VOTE_ACTION, cb.getTextParameter(plainKey));
           }
       }

       service.save (cb, getParams (cb));
       NmURL worklistURL = new NmURL ();
       worklistURL.setType (NmWorkItem.TYPE);
       worklistURL.setAction (NmAction.Command.LIST);
       worklistURL.setOid (null);
       HttpSession session = cb.getRequest().getSession();
       try {
           String taskKey = (String)cb.getSessionBean().getStorage().get(TASK_KEY);
           if((taskKey==null) || ((taskKey!=null)&&(!taskKey.equals("null"))))
           {
               //taskKey = session.getValue(TASK_KEY).toString();
               taskKey = session.getAttribute(TASK_KEY).toString();
              // session.removeAttribute(TASK_KEY);
           }
           if (taskKey.equals (PROJECT_TASKS)) {
               worklistURL.setAction(NmAction.Command.PROJECT_TASK_LIST);
               worklistURL.setOid(new NmOid(NmProject.TYPE, (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
           } else if (taskKey.equals (PLAN_TASKS)) {
               worklistURL.setType (NmProject.TYPE);
               worklistURL.setAction (NmAction.Command.VIEW_PLAN);
               worklistURL.setOid(new NmOid(NmProject.TYPE, (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
           } else if (taskKey.equals(DETAILS_TASKS)) {

               String currentTaskType = (String)cb.getSessionBean().getStorage().get(TASK_TYPE);
               if((currentTaskType==null)||((currentTaskType!=null)&&(!currentTaskType.equals("null"))))
               {
                   currentTaskType = session.getAttribute(TASK_TYPE).toString();
                   //session.removeAttribute(TASK_TYPE);//SPR 2053797
               }

               String currentTaskOID = (String)cb.getSessionBean().getStorage().get(TASK_OID);
               if((currentTaskOID==null)||((currentTaskOID!=null)&&(!currentTaskOID.equals("null"))))
               {
                   currentTaskOID = session.getAttribute(TASK_OID).toString();
                   //session.removeAttribute(TASK_OID);//SPR 2053797
               }

               worklistURL.setType(currentTaskType);
               worklistURL.setOid(new NmOid(currentTaskOID));
               worklistURL.setAction(NmAction.Command.VIEW);
           } else if (taskKey.equals(OVERVIEW_TASKS)) {
               worklistURL.setType(NmAction.Type.NETMARKETS);
               worklistURL.setAction(NmAction.Command.VIEW);
               worklistURL.setOid(null);
           } else if (taskKey.equals(VIEW_DOCUMENT)) {
               final ConcurrentHashMap storage = cb.getSessionBean().getStorage();
               String currentTaskOID = (String)cb.getSessionBean().getStorage().get(TASK_OID);
               if((currentTaskOID==null)||((currentTaskOID!=null)&&(!currentTaskOID.equals("null"))))
               {
                   currentTaskOID = session.getAttribute(TASK_OID).toString();
                   session.removeAttribute(TASK_OID);
               }
               worklistURL.setAction(NmAction.Command.VIEW);
               worklistURL.setType(NmAction.Type.OBJECT);
               worklistURL.setOid(new NmOid(currentTaskOID));
           }
           else if( taskKey.equals (PRODUCT_TASKS) ){

			   worklistURL.setAction("listProductAssignments");
			   worklistURL.setOid(new NmOid("object", (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));

		   }else if( taskKey.equals (LIBRARY_TASKS) ){

			   worklistURL.setAction("listLibraryAssignments");
			   worklistURL.setOid(new NmOid("object", (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));

		   }
       } catch (NullPointerException npe) {
           //Let the redirect continue to the My Tasks list if we couldn't get the session bean state

           worklistURL.setType (NmWorkItem.TYPE);
           worklistURL.setAction (NmAction.Command.LIST);
           worklistURL.setOid (null);
           if(logger.isDebugEnabled()){
               logger.debug(npe.getLocalizedMessage(), npe);
           }
       }
       //cb.setRedirectURL (worklistURL);
       result.setNextAction(FormResultAction.NONE);
       //result.setForcedUrl(worklistURL.toString2(cb.getUrlFactoryBean()));
       if (logger.isTraceEnabled()) {
           logger.trace(" NmWorkItemCommands.save - OUT");
       }
       return result;
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    byte[]
    * @exception wt.util.WTException
    **/
   public static byte[] workItemXml( NmCommandBean cb )
            throws WTException {
      return service.workItemXml(cb);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @exception wt.util.WTException
    **/
   public static void completePDF( NmCommandBean cb )
            throws WTException {
	  if (logger.isTraceEnabled()) {
	        logger.trace("=> NmWorkItemCommands.completePDF - IN");
	   }

	   HashMap formData=null;
	   try {
		   HttpServletRequest req =(HttpServletRequest)cb.getRequest();
		   ServletInputStream sis=req.getInputStream();
		   InputStreamReader isrdr = new InputStreamReader(sis);
		   BufferedReader buff = new BufferedReader(isrdr);
		   StringBuffer sB=new StringBuffer("");
		   String temp=null;
		   while((temp=buff.readLine())!=null)
			   sB.append(temp);
		   String inXML=sB.toString();
		   inXML.trim();
		   WfXmlParser xR=new WfXmlParser();
		   formData=xR.startParsing(inXML);
	  	   if (logger.isTraceEnabled()) {
	  		 logger.trace("Form Data   :: " + formData);
		   }
	   }
	   catch(IOException iE) {
		   if(logger.isDebugEnabled()){
	    		  logger.debug(iE.getLocalizedMessage(), iE);
	    	   }
	   }
	   catch(Exception e) {
		  if(logger.isDebugEnabled()){
	    		  logger.debug(e.getLocalizedMessage(), e);
	    	  }
	   }
	   cb.getMap().putAll(formData);

	   service.complete (cb, getParams (cb));
	   NmURL worklistURL = new NmURL ();
	   worklistURL.setType (NmWorkItem.TYPE);
	   worklistURL.setAction (NmAction.Command.LIST);
	   worklistURL.setOid (null);
	   HttpSession session = cb.getRequest().getSession();
	   try {
		   String taskKey = (String)cb.getSessionBean().getStorage().get(TASK_KEY);
		   if((taskKey==null) || ((taskKey!=null)&&(!taskKey.equals("null"))))
		   {
			   //taskKey = session.getValue(TASK_KEY).toString();
			   taskKey = session.getAttribute(TASK_KEY).toString();
			   session.removeAttribute(TASK_KEY);
		   }
		   if (taskKey.equals (PROJECT_TASKS)) {
			   worklistURL.setAction(NmAction.Command.PROJECT_TASK_LIST);
			   worklistURL.setOid(new NmOid(NmProject.TYPE, (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
		   } else if (taskKey.equals (PLAN_TASKS)) {
			   worklistURL.setType (NmProject.TYPE);
			   worklistURL.setAction (NmAction.Command.VIEW_PLAN);
			   worklistURL.setOid(new NmOid(NmProject.TYPE, (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
		   } else if (taskKey.equals(DETAILS_TASKS)) {

			   String currentTaskType = (String)cb.getSessionBean().getStorage().get(TASK_TYPE);
			   if((currentTaskType==null)||((currentTaskType!=null)&&(!currentTaskType.equals("null"))))
			   {
					currentTaskType = session.getAttribute(TASK_TYPE).toString();
					session.removeAttribute(TASK_TYPE);
				}

				String currentTaskOID = (String)cb.getSessionBean().getStorage().get(TASK_OID);
				if((currentTaskOID==null)||((currentTaskOID!=null)&&(!currentTaskOID.equals("null"))))
				{
					currentTaskOID = session.getAttribute(TASK_OID).toString();
					session.removeAttribute(TASK_OID);
				}

			   worklistURL.setType(currentTaskType);
			   worklistURL.setOid(new NmOid(currentTaskOID));
			   worklistURL.setAction(NmAction.Command.VIEW);
		   } else if (taskKey.equals(OVERVIEW_TASKS)) {
			   worklistURL.setType(NmAction.Type.NETMARKETS);
			   worklistURL.setAction(NmAction.Command.VIEW);
			   worklistURL.setOid(null);
		   } else if (taskKey.equals(VIEW_DOCUMENT)) {
			   final ConcurrentHashMap storage = cb.getSessionBean().getStorage();

			   String currentTaskOID = (String)cb.getSessionBean().getStorage().get(TASK_OID);
				if((currentTaskOID==null)||((currentTaskOID!=null)&&(!currentTaskOID.equals("null"))))
				{
					currentTaskOID = session.getAttribute(TASK_OID).toString();
					session.removeAttribute(TASK_OID);
				}
			   worklistURL.setAction(NmAction.Command.VIEW);
			   worklistURL.setType(NmAction.Type.OBJECT);
			   worklistURL.setOid(new NmOid(currentTaskOID));
		   }
	   } catch (NullPointerException npe) {
		   //Let the redirect continue to the My Tasks list if we couldn't get the session bean state

		   worklistURL.setType (NmWorkItem.TYPE);
		   worklistURL.setAction (NmAction.Command.LIST);
		   worklistURL.setOid (null);
	           if(logger.isDebugEnabled()){
	     		  logger.debug(npe.getLocalizedMessage(), npe);
	     	  }
	   }

	   cb.setRedirectURL (worklistURL);
	   if (logger.isTraceEnabled()) {
		        logger.trace("=> NmWorkItemCommands.completePDF - OUT");
	   }
   }

   /**
    * Returns a WTContainerRef object representing the scope allowed when
    * performing a reassign task action.  Returns null if there is no restriction.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    WTContainerRef
    * @exception wt.util.WTException
    **/
   public static WTContainerRef getReassignSearchScope( NmCommandBean cb )
            throws WTException {
      return service.getReassignSearchScope(cb);
   }

   /**
    * Accept the selected offered workitems. Multi-accept operation.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    FormResult
    * @exception wt.util.WTException
    **/
   public static FormResult multiaccept( NmCommandBean cb )
            throws WTException {
	   ArrayList oids = cb.getSelected();
	   //System.out.println("CB->oids.size(): NmContext [" + oids.size()+"]");
	   int len = oids.size();
	   ArrayList<NmOid> finalOids = new ArrayList<NmOid>(len);
	   for(int count=0; count<len; count++){
		   NmContext currentContext = (NmContext)oids.get(count);
		   NmOid currentOid = currentContext.getTargetOid();
		   if(currentOid != null){
			   finalOids.add((NmOid)currentOid);
		   }
	   }

	   //System.out.println("CB->oids.size(): NmOids [" + finalOids.size()+"]");
	   cb.getMap().put(ASSIGNMENTS_LIST, finalOids);
	   return service.multiaccept(cb);
   }

   /**
    * UnAccept the selected accepted workitems. Multi-unaccept operation.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    FormResult
    * @exception wt.util.WTException
    **/
   public static FormResult multiunaccept( NmCommandBean cb )
            throws WTException {
	   ArrayList oids = cb.getSelected();
	   //System.out.println("CB->oids.size(): NmContext [" + oids.size()+"]");
	   int len = oids.size();
	   ArrayList<NmOid> finalOids = new ArrayList<NmOid>(len);
	   for(int count=0; count<len; count++){
		   NmContext currentContext = (NmContext)oids.get(count);
		   NmOid currentOid = currentContext.getTargetOid();
		   if(currentOid != null){
			   finalOids.add((NmOid)currentOid);
		   }
	   }

	   //logger.trace("CB->oids.size(): NmOids [" + finalOids.size()+"]");
	   cb.getMap().put(ASSIGNMENTS_LIST, finalOids);
	   return service.multiunaccept(cb);
   }

   /**
    * Answer a query result of persistables to be displayed in the PJL Assignmnets
    * table.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    QueryResult
    * @exception wt.util.WTException
    **/
   public static QueryResult listProjectAssignments( NmCommandBean cb )
            throws WTException {
      return service.listProjectAssignments(cb);
   }

   /**
    * Answer a query result of persistables to be displayed in the PDML
    * Assignmnets table.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    QueryResult
    * @exception wt.util.WTException
    **/
   public static QueryResult listProductAssignments( NmCommandBean cb )
            throws WTException {
      return service.listProductAssignments(cb);
   }

   /**
    * Answer a query result of persistables to be displayed in the Library
    * Assignmnets table.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    QueryResult
    * @exception wt.util.WTException
    **/
   public static QueryResult listLibraryAssignments( NmCommandBean cb )
            throws WTException {
      return service.listLibraryAssignments(cb);
   }

   /**
    * Answer a query result of persistables to be displayed in the Home->Overvioew
    * and Home->Assignments tables.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     cb
    * @return    QueryResult
    * @exception wt.util.WTException
    **/
   public static QueryResult listAssignments( NmCommandBean cb )
            throws WTException {
      return service.listAssignments(cb);
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     workItem
    * @return    HashMap
    * @exception wt.util.WTException
    **/
   public static HashMap getWorkItemAttr( WorkItem workItem )
            throws WTException {
      return service.getWorkItemAttr(workItem);
   }

   /**
    * Reads the non-transient fields of this class from an external source.
    *
    * @param     input
    * @param     readSerialVersionUID
    * @param     superDone
    * @return    boolean
    * @exception java.io.IOException
    * @exception java.lang.ClassNotFoundException
    **/
   private boolean readVersion957977401221134810L( ObjectInput input, long readSerialVersionUID, boolean superDone )
            throws IOException, ClassNotFoundException {
      return true;
   }

   private static HashMap getParams (NmCommandBean cb)
   {
      HashMap params = new HashMap (12);
      HttpServletRequest request = cb.getRequest ();
      Enumeration param_names = request.getParameterNames ();

      while (param_names.hasMoreElements ()) {
         String param_name = (String) param_names.nextElement ();
         addParam (params, request, param_name, cb);
      }

      return params;
   }

   /**
    * A small method to protected agains null
    **/
   private static void addParam (HashMap params, HttpServletRequest request, String parameter, NmCommandBean cb)
   {
      String value = cb.getTextParameter (parameter);

      if (value == null) {
         value = "";
      }

      params.put(parameter, value);
      //logger.trace ("   parameter " + parameter + " = " + value);
   }

   public static NmHTMLActionModel getWorkItemActionModel(NmWorkItem nmWorkItem, WorkItem myWorkItem, boolean adminUser, boolean workItemOwner, WTContainer container, Locale myLocale)
   			throws WTException {
	   NmHTMLActionModel am = null;

	   if (myWorkItem.isComplete()) {
		   am = NmActionServiceHelper.service.getActionModel("workitem simple actions", nmWorkItem, myLocale,container);
	   }
	   else if (adminUser) {
		   //reassign, update deadline & view actions
		   am = NmActionServiceHelper.service.getActionModel("workitem admin actions", nmWorkItem, myLocale, container);
	   }
	   else if (workItemOwner) {
		   //reassign & view actions
		   am = NmActionServiceHelper.service.getActionModel("workitem actions", nmWorkItem, myLocale, container);
	   }
	   else if (nmWorkItem.isProjectWorkItem()){
		   am = NmActionServiceHelper.service.getActionModel("workitem simple actions", nmWorkItem, myLocale, container);
		   //NmAction action = NmActionServiceHelper.service.getAction("work", "view", SessionHelper.getLocale(), ((WTContained)myActivity).getContainer());
	   }

	   return am;
   }

   /**
    * Method used to get values of String instructions. It replaces names of variables eg. {Primary_Business_Object}
    * with values of the instruction in addition to formatting the value in html
    **/
   public static String formatToHtml (ProcessData context, String variableName, WfVariableInfo variableInfo) throws WTException
   {
	   String displayString = null;

	   if (variableInfo.getTypeName().equals("java.lang.String"))
	   {
		  //Extract the variable's value from the activityContext
          displayString =  (context.getValue(variableName) == null ? null :context.getValue(variableName).toString());

          if (displayString != null)
          {
			 //Replace variables in the string with their actual values.
	         displayString = WorkflowHelper.service.replaceVariables(displayString, context);

	         //Format rest to html
	         displayString = HTMLEncoder.encodeAndFormatForHTMLContent(displayString);
		  }

	   }

	   return displayString;
   }

    /**
     * Method used to start adhoc activities from new tabbed task details page.
     */
    public static FormResult startAdhocFromTaskDetails(NmCommandBean cb) throws WTException {
        if (logger.isTraceEnabled())
            logger.trace("=> NmWorkItemCommands.startAdhocFromTaskDetails-- IN");
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        startAdhoc(cb);
        cb.setRedirectURL(null);
        result.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE );
        result.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), WTMessage
                .getLocalizedMessage("wt.workflow.worklist.worklistResource",
                        worklistResource.Ad_HOC_CREATED_SUCCESSFULLY, null), null));
        if (logger.isTraceEnabled())
            logger.trace("=> NmWorkItemCommands.startAdhocFromTaskDetails-- OUT");
        return result;
    }

   public static void startAdhoc(NmCommandBean cb) throws WTException{
	   if(cb != null)
	    logger.trace("startAdhoc:");

	   Enumeration parameterNames = cb.getRequest().getParameterNames ();
	   int firstindex=0;
	   int lastindex=0;
	   int rows=0;
	   while (parameterNames.hasMoreElements ()) {
		   String plainKey = (String) parameterNames.nextElement ();
		   String key = NmCommandBean.convert (plainKey);
	         if (logger.isTraceEnabled()) {
	            logger.trace("=> NmWorkItemCommands.STARTADHOC: " + key + ", " + cb.getTextParameter(plainKey));
		   }

		   if(key.indexOf("AD_HOC_ACTIVITY_NAME") >= 0 && key.lastIndexOf("old") == -1) {
			   firstindex = key.indexOf("AD_HOC_ACTIVITY_NAME");
			   lastindex = key.indexOf("!", firstindex);
			   rows++;
			   cb.getMap().put(key.substring (firstindex,lastindex),cb.getTextParameter(plainKey));
		   }
		   else if(key.indexOf("AD_HOC_ASSIGNEE") >= 0 && key.lastIndexOf("old") == -1) {
			   firstindex = key.indexOf("AD_HOC_ASSIGNEE");
			   lastindex = key.indexOf("!", firstindex);
			   cb.getMap().put(key.substring (firstindex,lastindex),cb.getTextParameter(plainKey));
		   }
		   else if(key.indexOf("INSTRUCTIONS") >= 0 && key.lastIndexOf("old") == -1) {
			   firstindex = key.indexOf("INSTRUCTIONS");
			   lastindex = key.indexOf("!", firstindex);
			   cb.getMap().put(key.substring (firstindex,lastindex),cb.getTextParameter(plainKey));
		   }
		   else if(key.indexOf("AD_HOC_OFFER") >= 0 && key.lastIndexOf("old") == -1) {
			   firstindex = key.indexOf("AD_HOC_OFFER");
			   lastindex = key.indexOf("__", firstindex);
			   cb.getMap().put(key.substring (firstindex,lastindex),cb.getTextParameter(plainKey));
		   }
		   else if(key.indexOf("AD_HOC_DURATION") >= 0 && key.lastIndexOf("old") == -1) {
			   firstindex = key.indexOf("AD_HOC_DURATION");
			   lastindex = key.indexOf("!", firstindex);
			   cb.getMap().put(key.substring (firstindex,lastindex),cb.getTextParameter(plainKey));
		   }
		   else if(key.indexOf("AD_HOC_PREDECESSORS") >= 0 && key.lastIndexOf("old") == -1) {
			   firstindex = key.indexOf("AD_HOC_PREDECESSORS");
			   lastindex = key.indexOf("!", firstindex);
			   cb.getMap().put(key.substring (firstindex,lastindex),cb.getTextParameter(plainKey));
		   }
		   else if(key.indexOf("AD_HOC_TASK") >= 0 && key.lastIndexOf("old") == -1) {
			   firstindex = key.indexOf("AD_HOC_TASK");
			   lastindex = key.indexOf("!", firstindex);
			   cb.getMap().put(key.substring (firstindex,lastindex),cb.getTextParameter(plainKey));
		   }
		   else if(key.indexOf("buttonParams") >=0 && key.lastIndexOf("old") == -1) {
			   cb.getMap().put(key,cb.getTextParameter(plainKey));
		   }
		   else if(key.indexOf("EmailNotification") >= 0 && key.lastIndexOf("old") == -1) {
			   firstindex = key.indexOf("EmailNotification");
			   lastindex = key.indexOf("__", firstindex);
			   cb.getMap().put(key.substring (firstindex,lastindex),cb.getTextParameter(plainKey));
		   }
	   }
	   cb.getMap().put("buttonParams", rows+"");
	   HttpServletRequest request = cb.getRequest();
	   HttpSession session = cb.getRequest().getSession();
       HashMap sessionCheckedUsers = (HashMap)session.getAttribute("CHECKED_USERS");
       if(sessionCheckedUsers!=null){
    	   HashMap oldChecked = cb.getOldChecked();
    	   HashMap checked=cb.getChecked();
    	   for (Object oldCheckedKey : oldChecked.keySet()) {
    		    if(!checked.containsKey(oldCheckedKey)){
    	   			sessionCheckedUsers.remove(oldCheckedKey);
    	   		}
    	   }
		   HashMap unChecked = cb.getUnChecked();
		   for (Object unCheckedKey : unChecked.keySet()) {
  				sessionCheckedUsers.remove(unCheckedKey);
		   }
    	   sessionCheckedUsers.remove("ROLE_MAP");
    	   checked.putAll(sessionCheckedUsers);
    	   cb.setChecked(checked);
       }
	   service.startAdhocProcess(cb);
	   NmURL worklistURL = new NmURL ();
	   worklistURL.setType (NmWorkItem.TYPE);
	   worklistURL.setAction (NmAction.Command.LIST);
	   worklistURL.setOid (null);
	   try {
		   String taskKey = (String)cb.getSessionBean().getStorage().get(TASK_KEY);
		   if((taskKey==null) || ((taskKey!=null)&&(!taskKey.equals("null"))))
		   {
			   //taskKey = session.getValue(TASK_KEY).toString();
			   taskKey = session.getAttribute(TASK_KEY).toString();
			   session.removeAttribute(TASK_KEY);
		   }
		   if (taskKey.equals (PROJECT_TASKS)) {
			   worklistURL.setAction(NmAction.Command.PROJECT_TASK_LIST);
			   worklistURL.setOid(new NmOid(NmProject.TYPE, (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
		   } else if (taskKey.equals (PLAN_TASKS)) {
			   worklistURL.setType (NmProject.TYPE);
			   worklistURL.setAction (NmAction.Command.VIEW_PLAN);
			   worklistURL.setOid(new NmOid(NmProject.TYPE, (wt.fc.ObjectIdentifier) cb.getContainerRef().getKey()));
		   } else if (taskKey.equals(DETAILS_TASKS)) {

			   String currentTaskType = (String)cb.getSessionBean().getStorage().get(TASK_TYPE);
			   if((currentTaskType==null)||((currentTaskType!=null)&&(!currentTaskType.equals("null"))))
			   {
					currentTaskType = session.getAttribute(TASK_TYPE).toString();
					session.removeAttribute(TASK_TYPE);
				}

				String currentTaskOID = (String)cb.getSessionBean().getStorage().get(TASK_OID);
				if((currentTaskOID==null)||((currentTaskOID!=null)&&(!currentTaskOID.equals("null"))))
				{
					currentTaskOID = session.getAttribute(TASK_OID).toString();
					session.removeAttribute(TASK_OID);
				}

			   worklistURL.setType(currentTaskType);
			   worklistURL.setOid(new NmOid(currentTaskOID));
			   worklistURL.setAction(NmAction.Command.VIEW);
		   } else if (taskKey.equals(OVERVIEW_TASKS)) {
			   worklistURL.setType(NmAction.Type.NETMARKETS);
			   worklistURL.setAction(NmAction.Command.VIEW);
			   worklistURL.setOid(null);
		   } else if (taskKey.equals(VIEW_DOCUMENT)) {
			   final ConcurrentHashMap storage = cb.getSessionBean().getStorage();

			   String currentTaskOID = (String)cb.getSessionBean().getStorage().get(TASK_OID);
				if((currentTaskOID==null)||((currentTaskOID!=null)&&(!currentTaskOID.equals("null"))))
				{
					currentTaskOID = session.getAttribute(TASK_OID).toString();
					session.removeAttribute(TASK_OID);
				}

			   worklistURL.setAction(NmAction.Command.VIEW);
			   worklistURL.setType(NmAction.Type.OBJECT);
			   worklistURL.setOid(new NmOid(currentTaskOID));
		   }
	   } catch (NullPointerException npe) {
		   //Let the redirect continue to the My Tasks list if we couldn't get the session bean state

		   worklistURL.setType (NmWorkItem.TYPE);
		   worklistURL.setAction (NmAction.Command.LIST);
		   worklistURL.setOid (null);
	           if(logger.isDebugEnabled()){
    	     		  logger.debug(npe.getLocalizedMessage(), npe);
    	     	   }
	   }

	   cb.setRedirectURL (worklistURL);
   }
	 /*
	  * This method returns a Hashtable in which the key is the WorkItem and value is its
	  * WTContainerRef. Another major side-effect of this api is that it sets its result
	  * in MethodContext under key NmWorkItemCommands.WI_TO_CONT
	  *
	  * @ param allWIRef : This parameter is a list of only WorkItems or ProjectWorkItem
	  * @ return : This method returns the ObjectReferences of all the WTConatiners
	  *            associated with the WorkItems.
	  *
	  *            This method returns a null if any of the references in the param allWIRef
	  *            is non WorkItem or if the container for the workitem is a non
	  *            ConatinerTeamManaged indicating that the list contained invalid references.
	  */
	 public static Hashtable<WorkItem, WTContainerRef> getAllContainerReferenceForWI(List<WTReference> allWIRef){
		 int invalidCount = 0;
		 Hashtable<WorkItem, WTContainerRef> allCont = new Hashtable<WorkItem, WTContainerRef>();
		 //removing non-workitems from the list
		 ArrayList<Long> longArrayList = new ArrayList<Long>();
		 for (int i = 0; i < allWIRef.size(); i++) {
			 ObjectIdentifier currentObjectIdentifier = null;
			 long objectId=0;
			 WTReference currentRef = (WTReference)allWIRef.get(i);
			 currentObjectIdentifier = (ObjectIdentifier)currentRef.getKey();
			 String className = currentObjectIdentifier.getClassname();
			 if( !( className.equals("wt.workflow.work.WorkItem") ||
					 className.equals("wt.projmgmt.execution.ProjectWorkItem")) ){
				 invalidCount++;
				 break;
			 }
			 objectId = currentObjectIdentifier.getId();
			 if(logger.isTraceEnabled()) logger.trace("-----Iterating workitems: " + objectId );
			 longArrayList.add(new Long( objectId ) );
		 }

		 if( invalidCount > 0 )
			 return null;

		 long allObjectIdsInLong[] = getLongArray(longArrayList);
		 /*
		       Now pass the long array obtained above to the method to
		       find the enumeration of WorkItem objects.
		  */
		 if (allObjectIdsInLong.length > 0){
			 Vector<WorkItem> oids = new Vector<WorkItem>();
			 //get All WorkItems.
			 Enumeration allWorkItems = null;
			 allWorkItems = getAllObjects( WorkItem.class, allObjectIdsInLong );

			 while(allWorkItems.hasMoreElements()){
				 oids.add((WorkItem)allWorkItems.nextElement());
			 }

			 //Get the assigned activities corressponding to workitems.
			 Hashtable wiToWaaHash = getWorkItemToActivity(oids.elements());
			 Enumeration wiToWaaHashKeys = wiToWaaHash.keys();
			 WorkItem currentWorkItem = null;
			 WfActivity currentActivity = null;
			 WTContainerRef currentContRef = null;

			 while (wiToWaaHashKeys.hasMoreElements()) {
				 currentWorkItem = (WorkItem) wiToWaaHashKeys.nextElement();
				 currentActivity = (WfActivity) wiToWaaHash.get( currentWorkItem );
				 currentContRef = currentActivity.getContainerReference();
				 ObjectIdentifier currentObjectIdentifier = null;
				 currentObjectIdentifier = (ObjectIdentifier)currentContRef.getKey();
				 String className = currentObjectIdentifier.getClassname();
				 if( !(  className.equals("wt.pdmlink.PDMLinkProduct") ||
						 className.equals("wt.projmgmt.admin.Project2") ||
						 className.equals("wt.inf.library.WTLibrary"))  ){
					 return null;
				 }

				 allCont.put(currentWorkItem, currentContRef);
			 } //end of while myHashtableKeys.hasMoreElements
			 wiToWaaHash = null;
		 }

		 wt.method.MethodContext mc = wt.method.MethodContext.getContext();
		 mc.put(WI_TO_CONT, allCont);

		 return allCont;
	 }

	 /*
	  * This method accepts an enumeration of workitems. It returns hashtable in which
	  * key is wi and value is corresponding wfaactivity.
	  */
	 private static Hashtable getWorkItemToActivity(Enumeration enumWorkItems){
		 WorkItem         currentWorkItem;
		 ObjectReference  currentObjectReference  = null;
		 ObjectIdentifier currentObjectIdentifier = null;
		 Hashtable        WIHashtable = new Hashtable();
		 long             wfAssignedActivityId;

		 while (enumWorkItems.hasMoreElements()){
			 currentWorkItem = (WorkItem) enumWorkItems.nextElement();
			 currentObjectReference = currentWorkItem.getSource();
			 currentObjectIdentifier = (ObjectIdentifier) currentObjectReference.getKey();
			 wfAssignedActivityId = currentObjectIdentifier.getId();
			 if(logger.isTraceEnabled()){
				 logger.trace("-----Iterating workitems: wfAssignedActivityId= " + wfAssignedActivityId );
			 }
			 WIHashtable.put( currentWorkItem, new Long( wfAssignedActivityId ) );
		 }

		 currentObjectReference  = null;
		 currentObjectIdentifier = null;

		 //create a long( type ) array from the WIHashtable values.
		 Collection wfAssignedActivityIds = WIHashtable.values();
		 HashSet noDupIds = new HashSet(wfAssignedActivityIds);
		 Object allWfAssignedActivityIds[] = noDupIds.toArray();
		 int allIdsLength = allWfAssignedActivityIds.length;
		 long allWfAssignedActivityIdsInLong[] = new long[allIdsLength];

		 for ( int cnt = 0; cnt < allIdsLength; cnt++ ) {
			 Long objLong = (Long) allWfAssignedActivityIds[cnt];
			 allWfAssignedActivityIdsInLong[cnt] = objLong.longValue();
		 }

		 /*
			       Now pass the long array obtained above to the method to
			       find the enumeration of Wfassigned activitu objects.
			       Write an in-query to obtain all the WfAssigned Activities
			       You will get an enumeration of WfAssigned activites
		  */
		 if (allWfAssignedActivityIdsInLong.length > 0){
			 Enumeration wfAssignedActivities = getAllObjects(
					 WfAssignedActivity.class, allWfAssignedActivityIdsInLong );

			 /*
			          From the enumeration obtained above extract the Id's for
			          each Wfassigned-activities. Create an Hash-table with the
			          extracted id's as keys and value as wfAssignedObjects.
			  */
			 WfAssignedActivity currentWfAssignedActivity = null;
			 Hashtable wfAssignedActivityHashtable = new Hashtable();

			 while (wfAssignedActivities.hasMoreElements()) {
				 currentWfAssignedActivity = (WfAssignedActivity) wfAssignedActivities.nextElement();
				 try {
					 currentObjectReference = ObjectReference.newObjectReference( currentWfAssignedActivity );
				 }
				 catch( WTException wte ) {
				      if(logger.isDebugEnabled()){
       	     		              logger.debug(wte.getLocalizedMessage(), wte);
       	     	  	              }
				 } //end of try-catch

				 currentObjectIdentifier = (ObjectIdentifier) currentObjectReference.getKey();
				 wfAssignedActivityId = currentObjectIdentifier.getId();

				 if(logger.isTraceEnabled()){
					 logger.trace("-----Iterating assigned activities: wfAssignedActivityId= " + wfAssignedActivityId );
				 }

				 wfAssignedActivityHashtable.put( new Long(wfAssignedActivityId), currentWfAssignedActivity );
			 } //end of while wfAssignedActivities.hasMoreElements

			 /*
			  * Now merge the two hastables i.e WIHashTable and wfAssignedActivityHashtable
			  * into the WIHashtable.
			  */
			 Enumeration WIHashtableKeys = WIHashtable.keys();
			 currentWorkItem = null;
			 Long wfAssignedActivity_ID = null;
			 WfAssignedActivity wfAssignedActivityObj = null;

			 while (WIHashtableKeys.hasMoreElements()) {
				 currentWorkItem = (WorkItem) WIHashtableKeys.nextElement();
				 wfAssignedActivity_ID = (Long) WIHashtable.get( currentWorkItem );
				 wfAssignedActivityObj =
					 (WfAssignedActivity) wfAssignedActivityHashtable.get( wfAssignedActivity_ID );
				 if (wfAssignedActivityObj != null)
					 WIHashtable.put( currentWorkItem, wfAssignedActivityObj );
			 } //end of while myHashtableKeys.hasMoreElements

			 return WIHashtable;
		 }else{
			 return new Hashtable();
		 }
	 }//end of getWorkItemToActivity

	 /*
	  * This method should execute on MS only
	  * This method gets objects of the specified clause and is performance centric
	  * method.
	  */
	 private static Enumeration getAllObjects ( Class currentClass, long allIds[] ) {
		 int allIds_length = allIds.length;
		 Enumeration returnEnum = null;
		 Vector tmpVector = new Vector( allIds_length );
		 final int CHUNKSIZE = 700;
		 //The IN clause of sql statement has a limit of 1000. So we use chunking to get
		 //around this limitation. eg if allIds contain 100 items and chunksize = 50, the
		 //following code will fire 2 db queries.
		 for ( int j = 0; j < allIds_length; j += CHUNKSIZE ) {
			 QueryResult tmpQueryResult = null;
			 long chunk[] = new long[Math.min(CHUNKSIZE, allIds.length - j)];
			 for(int k = 0; k < chunk.length; k++)
				 chunk[k] = allIds[j + k];
			 try {
				 QuerySpec qs = new QuerySpec( currentClass );
				 qs.setAdvancedQueryEnabled(true);
				 ClassAttribute qs_ca1 = new ClassAttribute(
						 currentClass, WTAttributeNameIfc.ID_NAME);
				 SearchCondition qs_sc1 = new SearchCondition(qs_ca1,
						 SearchCondition.IN,
						 new ArrayExpression(chunk));
				 qs.appendWhere(qs_sc1, new int[]{0});

				 tmpQueryResult = PersistenceServerHelper.manager.query (qs);

				 while ( tmpQueryResult.hasMoreElements() ) {
					 tmpVector.add((Persistable) tmpQueryResult.nextElement() );
				 } //end of while
			 }catch( WTException wte ){
			   if(logger.isDebugEnabled()){
   	     		   logger.debug(wte.getLocalizedMessage(), wte);
   	     	  	   }
			 }
		 }//end of for

		 returnEnum = tmpVector.elements();
		 return returnEnum;
	 } //end of getAllObjects

	 private static long[] getLongArray(Collection col){
		 HashSet noDupIds = new HashSet(col);
		 Object allIds[] = noDupIds.toArray();
		 int allIdsLength = allIds.length;
		 long allObjectIdsInLong[] = new long[allIdsLength];

		 for ( int cnt = 0; cnt < allIdsLength; cnt++ ) {
			 Long objLong = (Long) allIds[cnt];
			 allObjectIdsInLong[cnt] = objLong.longValue();
		 }
		 return allObjectIdsInLong;
	 }
    public static void validateAdhocAssignee(NmCommandBean cb) throws WTException {
        service.validateAdhocAssignee(cb);
    }

    public static FormResult removeUserFromRole( NmCommandBean cb )
    throws WTException {
    	//System.out.println(">>NmWorkItemCommands.removeUserFromRole IN");
    	String message=service.removeFromTeam(cb);
    	if(message==null || message.equals(""))
    		return null;

    	FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
    	//FeedbackMessage msg = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, message);
    	//result.addFeedbackMessage(msg);
    	result.setNextAction(FormResultAction.JAVASCRIPT);
    	result.setJavascript("alert(\""+ message+"\");");

    	//System.out.println(">>NmWorkItemCommands.removeUserFromRole OUT");
    	return result;
    }

    public static FormResult addUsersToRole( NmCommandBean cb )
    throws WTException {
    	//System.out.println(">>NmWorkItemCommands.addUsersToRole IN");
    	ArrayList list = new ArrayList();
        String users = cb.getTextParameter("hiddenemail");

        if (users != null) {
           int start = 0;
           int pos = users.indexOf("#", start);
           while (pos != -1) {
              String user = users.substring(start, pos);
              list.add(user);
              System.out.println("user :: "+user);
              start = pos + 1;
              pos = users.indexOf("#", start);
           }
        }

        String roleStr = cb.getTextParameter("association");
        String message=service.addUsersToRole(cb, roleStr, list);

    	if(message==null || message.equals(""))
    		return null;

    	FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
    	FeedbackMessage msg = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, message);
    	result.addFeedbackMessage(msg);
    	result.setNextAction(FormResultAction.REFRESH_OPENER);
    	//System.out.println(">>NmWorkItemCommands.addUsersToRole OUT");
    	return result;
    }

    public static FormResult closeTaskbar( NmCommandBean cb )
    throws WTException {

 	   if (logger.isTraceEnabled()) {
            logger.trace("=> NmWorkItemCommands.closeTaskbar: ");
 	   }

 	   Persistable oldVisited = getTaskbarRecentlyUsedWorkItem();
 	   if((oldVisited!=null)&&(oldVisited instanceof WorkItem))
 	   {
 		   WorkItem wi = (WorkItem)oldVisited;
 		   WfActivity assignedActivity = (WfActivity) wi.getSource ().getObject ();
 		   setTaskbarRecentlyUsedWorkItem(assignedActivity);
 	   }
 	   FormResult fr = new FormResult();
 	   fr.setStatus(FormProcessingStatus.SUCCESS);
 	   /*
 	   fr.setNextAction(FormResultAction.JAVASCRIPT);
 	   fr.setJavascript("closingBar()");
 	   FeedbackMessage msg = new FeedbackMessage(FeedbackType.SUCCESS, cb.getLocale(), null, null,"");
 	   fr.addFeedbackMessage(msg);
 	   */
 	   if (logger.isTraceEnabled()) {
            logger.trace("=> NmWorkItemCommands.closeTaskbar: ");
 	   }
 	   return fr;
    }


    public static WorkItem saveTaskbarComment( NmCommandBean cb )
    throws WTException {

 	   if (logger.isTraceEnabled()) {
            logger.trace("=> NmWorkItemCommands.saveTaskbarComment: ");
 	   }
 	  Persistable p = null;
 	  // 1989373: check for oid parameter. if this parameter is passed complete/save that task else pickup from recent list
 	  if(cb.getMap().get("taskWindowOid")!=null){
 		   String oid=cb.getMap().get("taskWindowOid").toString();
 		   WTReference wtReference = (new ReferenceFactory()).getReference(oid);
 		   p = wtReference.getObject();
 	 	   if (logger.isDebugEnabled()) {
 	            logger.debug("==> NmWorkItemCommands.saveTaskbarComment: Oid comes from parameter."+oid);
 	 	   }
 	   }
 	   if(p == null){
 		  p = getTaskbarRecentlyUsedWorkItem();
 	   }
 	   if(p==null){
 		   return null;
 	   }
 	   WorkItem currentWorkItem=null;
 	   if(p !=null & p instanceof WorkItem){
 		  currentWorkItem=(WorkItem)p;
 	   }
       if (currentWorkItem != null && currentWorkItem.isComplete()) {
           NmException e = new NmException(RESOURCE, workResource.COMPLETED_TASK_MSG, null);
           if (logger.isDebugEnabled()) {
               logger.debug(e.getLocalizedMessage(), e);
           }
           throw e;
     }

 	   String taskbarComment = "";
 	   if((cb.getMap().get("taskbar_comment"))!=null) {
 		   taskbarComment = (String)cb.getMap().get("taskbar_comment");
 	   }else if((cb.getTextArea().get("comments"))!=null) {
 		   taskbarComment = (String)cb.getTextArea().get("comments");
 	   }

 	   ProcessData workItemContext = null;

        if(currentWorkItem.getContext() == null) {
     	   WfActivity currentActivity = (WfActivity)currentWorkItem.getSource().getObject();
     	   workItemContext = currentActivity.getContext().copy();
     	   currentWorkItem.setContext(workItemContext);
        }

        workItemContext = currentWorkItem.getContext();

        workItemContext.setTaskComments(taskbarComment);
 	   currentWorkItem = (WorkItem) PersistenceHelper.manager.save(currentWorkItem);

 	   if (logger.isTraceEnabled()) {
            logger.trace("=> NmWorkItemCommands.saveTaskbarComment: ");
 	   }
 	   return currentWorkItem;
    }


    public static void completeTaskbarComment( NmCommandBean cb )
    throws WTException {

 	   if (logger.isTraceEnabled()) {
            logger.trace("=> NmWorkItemCommands.completeTaskbarComment: ");
 	   }

 	   WorkItem myWorkItem = saveTaskbarComment(cb);
 	  boolean origEnforce=false;
 	   if(myWorkItem==null){
 		   throw new WTException(WTMessage.getLocalizedMessage("wt.workflow.worklist.worklistResource", worklistResource.TASK_COMPLETE,null ));
 	   }


 	   WTPrincipalReference principalRef = WTPrincipalReference.newWTPrincipalReference(SessionHelper.manager.getPrincipal());
 	   try
 	   {
 	      origEnforce = SessionServerHelper.manager.setAccessEnforced(false);
 	      if (!myWorkItem.isComplete()) {
 	      Persistable obj = null;
 	      obj=getLcmObject(myWorkItem);
 	      LifeCycleManaged lcmObject=null;
 	      WfAssignedActivity activity = (WfAssignedActivity) myWorkItem.getSource ().getObject ();
 	      WfAssignedActivityTemplate  aat = (WfAssignedActivityTemplate) activity.getTemplate ().getObject ();
 	      String taskType=aat.getTaskName();
 	      if (taskType.equals(NmWorkItemCommands.SUBMIT_TASK_ACTION) && obj != null) {
 	          if( obj instanceof LifeCycleManaged) {
 	              lcmObject = (LifeCycleManaged) obj;
 	          }
 	             lcmObject = getLifeCycleService().submitForApproval(lcmObject);
 	             myWorkItem = (WorkItem) getPersistenceManager().refresh(myWorkItem);
 	       }
 	      else{
 		   WorkflowHelper.service.workComplete(myWorkItem, principalRef, null);
 	      }
        }
 	   }
 	  catch (WTException wex) {
 	          if(logger.isDebugEnabled()){
 	              logger.debug(wex.getLocalizedMessage(), wex);
 	              }
 	        throw wex;
 	      }
 	   finally
 	   {
 	      SessionServerHelper.manager.setAccessEnforced(origEnforce);
 	   }

 	   if (logger.isTraceEnabled()) {
            logger.trace("=> NmWorkItemCommands.completeTaskbarComment: ");
 	   }
    }

    public static String getTaskbarFlag() throws WTException {
        String returnFlag = null;
        Persistable visitedWorkItem = getTaskbarRecentlyUsedWorkItem();
  		if(visitedWorkItem!=null)
  		{
  			returnFlag = NmOid.newNmOid(visitedWorkItem.getPersistInfo().getObjectIdentifier()).toString();
  			//visitedWorkItem.getPersistInfo().getObjectIdentifier().toString();
  		}
        return returnFlag;
    }

	public static void setTaskbarRecentlyUsedWorkItem(WTObject visitedWorkItem) throws WTException {
		RecentlyVisitedHelper.service.addCustomStackObject(visitedWorkItem, "TASK_TRACKING_STACK");
    }

	public static Persistable getTaskbarRecentlyUsedWorkItem() throws WTException {
		Persistable visitedWorkItem = null;
		try
		{
			Vector vec = RecentlyVisitedHelper.service.getCustomStack("TASK_TRACKING_STACK");
	        if((vec!=null)&&(vec.size()>0))
		  	{
		  		ObjectVisitedInfo recentObject = (ObjectVisitedInfo)vec.elementAt(0);
		  		if(recentObject!=null)
		  		{
			  		WTReference wtReference1 = (new ReferenceFactory()).getReference(recentObject.getOID());
			  		Persistable temp_obj = wtReference1.getObject();
			  		if(temp_obj instanceof WorkItem)
			  		{
			  			WorkItem wi = (WorkItem)temp_obj;
			  			wi = (WorkItem)PersistenceHelper.manager.refresh(wi);
			  			if(wi.getOwnership()!= null && !(wi.getOwnership().getOwner().getObject().equals(SessionHelper.getPrincipal()))){
			  				RecentlyVisitedHelper.service.removeCustomStackObjectsByOid(new ObjectIdentifier[]{wi.getPersistInfo().getObjectIdentifier()}, "TASK_TRACKING_STACK");
			  			}
			  			else if(!wi.isComplete())
			  			{
			  				visitedWorkItem = temp_obj;
			  			}
			  		}
		  		}
		  	}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

        return visitedWorkItem;
	}

	/**
	*
	* <BR><BR><B>Supported API: </B>false
	*
	* @param     cb
	* @return    FormResult
	* @exception wt.util.WTException
	**/
	public static FormResult trackTask(NmCommandBean cb) throws WTException {
		NmURL worklistURL = new NmURL();
		worklistURL.setType(NmAction.Type.OBJECT);
		//worklistURL.setType(NmAction.Type.WORK);
		worklistURL.setAction(NmAction.Command.VIEW);
		//worklistURL.setAction("workflowTaskbar");

		if (cb.getActionOid().getRef() instanceof WorkItem) {
			WorkItem workItem = (WorkItem) cb.getActionOid().getRef();
			setTaskbarRecentlyUsedWorkItem(workItem);
			boolean isPBOAvailable = false;
			try {
				if (workItem != null
						&& workItem.getPrimaryBusinessObject() != null) {
					worklistURL.setOid(new NmOid(workItem
							.getPrimaryBusinessObject().getObject()));
					isPBOAvailable = true;
				}

			} catch (Exception e) {
			}

			if (!isPBOAvailable) {
				worklistURL.setOid(cb.getActionOid());
			}
		}
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		if (cb.getTextParameter("promptTaskbar") == null) {
			result.setNextAction(FormResultAction.JAVASCRIPT);
			//StringBuffer buff = new StringBuffer("PTC.taskbar.launchTaskbar(\""+ worklistURL.toString2(cb.getUrlFactoryBean()) + "\"); ");
			StringBuffer buff = new StringBuffer("PTC.taskbar.launchTaskbar(); ");
			result.setJavascript(buff.toString());
		} else {
			//result.setNextAction(FormResultAction.LOAD_OPENER_URL);
			//result.setURL(worklistURL.toString2(cb.getUrlFactoryBean()));
			result.setNextAction(FormResultAction.JAVASCRIPT);
			StringBuffer buff = new StringBuffer("top.PTC.getMainWindow().PTC.taskbar.launchTaskbar();wfWindowClose();");
			result.setJavascript(buff.toString());
		}
		//FeedbackMessage msg = new FeedbackMessage(FeedbackType.SUCCESS, cb.getLocale(), null, null,"");
		//result.addFeedbackMessage(msg);

		return result;
	}
	   public static FormResult saveTask( NmCommandBean cb ) throws WTException {
		   if (logger.isTraceEnabled()) {
			   logger.trace("=> NmWorkItemCommands.completeTask: ");
		   }
	 	   save(cb);
	 	   cb.setRedirectURL(null);
	 	   FormResult fr = new FormResult();
		   fr.setStatus(FormProcessingStatus.FAILURE); // settign failure since we dont want window to be closed
		   fr.setNextAction(FormResultAction.JAVASCRIPT);
		   fr.setJavascript("refreshingTaskbarComment();");

		   if (logger.isTraceEnabled()) {
			   logger.trace("=> NmWorkItemCommands.completeTask: ");
		   }
		   return fr;
	   }
	   public static FormResult completeTask( NmCommandBean cb )
		throws WTException {
		   // if this parameter(completeClicked) is true then complete button is clicked else save button is clicked
		   if(cb.getTextParameter("completeClicked")!=null && "FALSE".equalsIgnoreCase(cb.getTextParameter("completeClicked"))){
			   return saveTask(cb);
		   }
		   if (logger.isTraceEnabled()) {
			   logger.trace("=> NmWorkItemCommands.completeTask: ");
		   }

	 	   //WorkItem myWorkItem = saveTaskbarComment(cb);

	 	   //WTPrincipalReference principalRef = WTPrincipalReference.newWTPrincipalReference(SessionHelper.manager.getPrincipal());
	 	   //if (!myWorkItem.isComplete()) {
	 		   //WorkflowHelper.service.workComplete(myWorkItem, principalRef, null);
	           //}
	 	   complete(cb);
	 	   cb.setRedirectURL(null);
		   FormResult fr = new FormResult();
		   fr.setStatus(FormProcessingStatus.SUCCESS);
		   fr.setNextAction(FormResultAction.JAVASCRIPT);
		   fr.setJavascript("closingTaskbarWizard()");

		   if (logger.isTraceEnabled()) {
			   logger.trace("=> NmWorkItemCommands.completeTask: ");
		   }
		   return fr;
	  }

	   public static boolean validateTaskbarInputActions(NmCommandBean cb) throws WTException {
		   return service.validateTaskbarInputActions(cb);
	   }

	    public static WorkItem getTaskbarWorkItem(String oid) {
	    	WorkItem workItem = null;
	        try
	        {
	    		Persistable visitedWorkItem = getTaskbarRecentlyUsedWorkItem();
	    		if (visitedWorkItem != null && visitedWorkItem instanceof WorkItem)
	    		{
	    			workItem = (WorkItem)visitedWorkItem;
	    		}
	        }
	        catch (final WTException e) {
	            logger.error("getWorkItem(): " + e.getMessage());
	            e.printStackTrace();
	            throw new WTRuntimeException(e);
	        }

	        if (logger.isDebugEnabled()) {
	            WfAssignedActivity activity;
	            String workItemName;
	            activity = (WfAssignedActivity)workItem.getSource().getObject();
	            workItemName = activity.getName();
	            logger.debug("getWorkItem() - workItem: " + workItemName);
	        }
	        return workItem;
	    }


	    private static Persistable getLcmObject(WorkItem workItem) {
	          Persistable lcmObject = null;
	           PersistentReference brof = workItem.getPrimaryBusinessObject();
	          try {
	             if (brof != null && brof.getKey() != null && brof.getObject() != null) {
	                lcmObject = brof.getObject();
	             }
	          }
	          catch (Exception e) {
	             // TODO:
	             // e.printStackTrace();
	          }

	          return lcmObject;
	       }

	     private  static LifeCycleService getLifeCycleService() {
	          return LifeCycleHelper.service;
	       }

	     private static PersistenceManager getPersistenceManager(){
	          return PersistenceHelper.manager;
	       }
}

