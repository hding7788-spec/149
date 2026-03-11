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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import wt.access.AccessControlHelper;
import wt.access.AccessControlManager;
import wt.access.AccessPermission;
import wt.fc.Persistable;
import wt.fc.PersistentReference;
import wt.fc.WTReference;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.inf.team.ContainerTeamService;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.misc.NmContext;

public class ReassignSearchUserValidator extends DefaultUIComponentValidator
{
    private static final Logger logger = LogR.getLogger(ReassignSearchUserValidator.class.getName());
    private static final long serialVersionUID = 1L;

    /**
     * This method will perform the full pre validation on whether to expose the
     * "Find" button next to the user drop down list on the Reassin Task wizard.  The user
     * must have modify permssion for the ContainerTeam of the WorkItem for the
     * action to be valid
     *
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     *
     * @param validationKey
     *            The key to validate
     * @param validationCriteria
     *            The criteria available for evaluation.
     * @param locale
     *            The current user's locale.
     *
     * @return a validation result of the query. The result will be
     *         <code>UIValidationStatus.HIDDEN</code> if the action is not to
     *         be exposed or <code>UIValidationSatus.ENABLED</code> enabled if
     *         the action is to be exposed.
     */

    public UIValidationResultSet performFullPreValidation(UIValidationKey validationKey,
            UIValidationCriteria validationCriteria, Locale locale) throws WTException
    {
       UIValidationStatus status = UIValidationStatus.ENABLED;
       UIValidationResultSet resultSet = new UIValidationResultSet();

       logger.debug( "-------------------------------------------------------");
       logger.debug( "RASUV - validationCriteria.getContextObject() - " + validationCriteria.getContextObject());

       Hashtable<WorkItem, WTContainerRef> allCont = null;
       /* Stop using MC it seems to be of no use
       wt.method.MethodContext mc = wt.method.MethodContext.getContext();
       allCont =
       (Hashtable<WorkItem, WTContainerRef>)mc.get(NmWorkItemCommands.WI_TO_CONT);
       */

       List theOids = getSelectedObjects(validationCriteria);
       if( allCont == null ){
    	   //removing non-workitems from the list
    	   ArrayList<WTReference> longArrayList = null;
    	   if( theOids.size() != 0 ){
    		   longArrayList = addToAl(theOids);
    	   }else{
    		   //see if this is from row action
    		   longArrayList = new ArrayList<WTReference>();
    		   WTReference contextObjectRef = validationCriteria.getContextObject();
    		   longArrayList.add(contextObjectRef);
    	   }
    	   allCont = getAllCont(longArrayList);
       }

       if( allCont != null ){
    	   boolean hasAccessOnAllContTeam = true;
//    	   HashSet<WTContainerRef> setOfContRef = new HashSet<WTContainerRef>(allCont.values());
//    	   Iterator contIterator =  setOfContRef.iterator();
//    	   while( contIterator.hasNext() ){
//    		   ContainerTeam theTeam = getContainerTeam((WTContainerRef)contIterator.next());
//    		   if ( ! accessControlManager().hasAccess(theTeam,
//    				   AccessPermission.MODIFY) ) {
//    			   logger.debug("RASUV - user has no modify access to "
//    					   + theTeam);
//    			   hasAccessOnAllContTeam = false;
//    			   break;
//    		   }
//    	   }

    	   if( hasAccessOnAllContTeam )
    		   status = UIValidationStatus.ENABLED;
       }

       logger.debug( "RASUV - returning status - " + status);
       resultSet.addResult(new UIValidationResult(validationKey,
               status, validationCriteria.getContextObject()));

       return resultSet;
    }

    AccessControlManager accessControlManager() throws WTException {
        return AccessControlHelper.manager;
    }

    ContainerTeamService containerTeamService() throws WTException {
        return ContainerTeamHelper.service;
    }

    Persistable getPboPersistable( WorkItem work_item){
        PersistentReference persist_ref = work_item.getPrimaryBusinessObject();
        if ( persist_ref != null ){
            return persist_ref.getObject();
        }
        return null;
    }

    ContainerTeam getContainerTeam( WTContainerRef contRef ) throws WTException {
    	WTContainer container = contRef.getReferencedContainer();
    	if ( container != null ) {
    		return containerTeamService().getContainerTeam((ContainerTeamManaged)container);
    	}
    	return null;
    }

    MethodContext getMethodContext(){
    	MethodContext mc = MethodContext.getContext();
    	Hashtable<WorkItem, WTContainerRef> allCont =
    		(Hashtable<WorkItem, WTContainerRef>)mc.get(NmWorkItemCommands.WI_TO_CONT);
    	return mc;
    }

    List getSelectedObjects(UIValidationCriteria validationCriteria){
    	return validationCriteria.getSelectedInOpener();
    }

    ArrayList<WTReference> addToAl(List theOids)
    throws WTException{
    	ArrayList<WTReference> longArrayList = new ArrayList<WTReference>();
    	for (int i = 0; i < theOids.size(); i++) {
    		WTReference currentRef = null;
    		NmContext currentContext = (NmContext)theOids.get(i);
    		NmOid oid = (NmOid)currentContext.getTargetOid();
    		currentRef = oid.getWtRef();
    		longArrayList.add(currentRef);
    	}
    	return longArrayList;
    }

    Hashtable<WorkItem, WTContainerRef> getAllCont(ArrayList<WTReference> longArrayList){
    	return NmWorkItemCommands.getAllContainerReferenceForWI( longArrayList );
    }

}
