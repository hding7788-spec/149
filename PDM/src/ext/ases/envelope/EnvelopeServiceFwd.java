// Generated EnvelopeServiceFwd%4B4FF4F901C9: ??? 01/18/10 22:34:20
/* bcwti
 *
 * Copyright (c) 2008 Parametric Technology Corporation (PTC). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */

package ext.ases.envelope;

import ext.ases.envelope.EnvelopeService;
import ext.ases.envelope.ProcessEnvelope;
import java.io.Serializable;
import java.lang.String;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import wt.enterprise.RevisionControlled;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

/**
 *
 * <BR><BR><B>Supported API: </B>false
 * <BR><BR><B>Extendable: </B>false
 *
 * @version   1.0
 **/

public class EnvelopeServiceFwd implements RemoteAccess, EnvelopeService, Serializable {


   // --- Attribute Section ---


   static final boolean SERVER = RemoteMethodServer.ServerFlag;
   private static final String FC_RESOURCE = "wt.fc.fcResource";
   private static final String CLASSNAME = EnvelopeServiceFwd.class.getName();


   // --- Operation Section ---

   /**
    * @return    Manager
    * @exception wt.util.WTException
    **/
   private static Manager getManager()
            throws WTException {

      Manager manager = ManagerServiceFactory.getDefault().getManager( ext.ases.envelope.EnvelopeService.class );
      
      if ( manager == null ) {
         Object[] param = { "ext.ases.envelope.EnvelopeService" };
         throw new WTException( FC_RESOURCE, wt.fc.fcResource.UNREGISTERED_SERVICE, param );
      }
      return manager;
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     processEnvelope
    * @return    ArrayList
    * @exception wt.util.WTException
    **/
   public ArrayList getAllMembers( ProcessEnvelope processEnvelope )
            throws WTException {

      if (SERVER)
         return ((EnvelopeService)getManager()).getAllMembers( processEnvelope );
      else {
         try {
            Class[]	argTypes	= { ProcessEnvelope.class };
            Object[]	args		= { processEnvelope };
            return (ArrayList)RemoteMethodServer.getDefault().invoke(
               "getAllMembers", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getAllMembers" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getAllMembers" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }

   /**
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     processEnvelope
    * @return    RevisionControlled
    * @exception wt.util.WTException
    **/
   public RevisionControlled getTopObject( ProcessEnvelope processEnvelope )
            throws WTException {

      if (SERVER)
         return ((EnvelopeService)getManager()).getTopObject( processEnvelope );
      else {
         try {
            Class[]	argTypes	= { ProcessEnvelope.class };
            Object[]	args		= { processEnvelope };
            return (RevisionControlled)RemoteMethodServer.getDefault().invoke(
               "getTopObject", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getTopObject" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getTopObject" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
   
   public void saveTopObjLink (ProcessEnvelope processEnvelope,RevisionControlled revisioncontrolled)
            throws WTException {

      if (SERVER)
         ((EnvelopeService)getManager()).saveTopObjLink( processEnvelope, revisioncontrolled );
      else {
         try {
            Class[]	argTypes	= { ProcessEnvelope.class, RevisionControlled.class };
            Object[]	args		= { processEnvelope, revisioncontrolled };
            RemoteMethodServer.getDefault().invoke(
               "saveTopObjLink", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "saveTopObjLink" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "saveTopObjLink" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
   
   public void saveRelatedObjLink (ProcessEnvelope processEnvelope,RevisionControlled revisioncontrolled)
            throws WTException {

      if (SERVER)
         ((EnvelopeService)getManager()).saveRelatedObjLink( processEnvelope, revisioncontrolled );
      else {
         try {
            Class[]	argTypes	= { ProcessEnvelope.class, RevisionControlled.class };
            Object[]	args		= { processEnvelope, revisioncontrolled };
            RemoteMethodServer.getDefault().invoke(
               "saveRelatedObjLink", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "saveRelatedObjLink" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "saveRelatedObjLink" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
   
   public QueryResult getEnvelopeByTopObject( RevisionControlled revisioncontrolled )
            throws WTException {

      if (SERVER)
         return ((EnvelopeService)getManager()).getEnvelopeByTopObject( revisioncontrolled );
      else {
         try {
            Class[]	argTypes	= { RevisionControlled.class };
            Object[]	args		= { revisioncontrolled };
            return (QueryResult)RemoteMethodServer.getDefault().invoke(
               "getEnvelopeByTopObject", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getEnvelopeByTopObject" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getEnvelopeByTopObject" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
   
   public QueryResult getEnvelopeByMemberObject( RevisionControlled revisioncontrolled )
            throws WTException {

      if (SERVER)
         return ((EnvelopeService)getManager()).getEnvelopeByMemberObject( revisioncontrolled );
      else {
         try {
            Class[]	argTypes	= { RevisionControlled.class };
            Object[]	args		= { revisioncontrolled };
            return (QueryResult)RemoteMethodServer.getDefault().invoke(
               "getEnvelopeByMemberObject", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getEnvelopeByMemberObject" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getEnvelopeByMemberObject" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
}
