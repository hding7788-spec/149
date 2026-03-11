// Generated DownloadServiceFwd%4B4FF4F901C9: ??? 01/18/10 22:34:20
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

import ext.ases.envelope.DownloadService;
import ext.ases.envelope.ProcessEnvelope;
import java.io.Serializable;
import java.lang.String;
import java.util.Vector;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import wt.enterprise.RevisionControlled;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;
import wt.content.*;

/**
 *
 * <BR><BR><B>Supported API: </B>false
 * <BR><BR><B>Extendable: </B>false
 *
 * @version   1.0
 **/

public class DownloadServiceFwd implements RemoteAccess, DownloadService, Serializable {


   // --- Attribute Section ---


   static final boolean SERVER = RemoteMethodServer.ServerFlag;
   private static final String FC_RESOURCE = "wt.fc.fcResource";
   private static final String CLASSNAME = DownloadServiceFwd.class.getName();


   // --- Operation Section ---

   /**
    * @return    Manager
    * @exception wt.util.WTException
    **/
   private static Manager getManager()
            throws WTException {

      Manager manager = ManagerServiceFactory.getDefault().getManager( ext.ases.envelope.DownloadService.class );
      
      if ( manager == null ) {
         Object[] param = { "ext.ases.envelope.DownloadService" };
         throw new WTException( FC_RESOURCE, wt.fc.fcResource.UNREGISTERED_SERVICE, param );
      }
      return manager;
   }

   public String getContents( Vector vector,String s, String type )
            throws WTException {

      if (SERVER)
         return ((DownloadService)getManager()).getContents( vector, s, type );
      else {
         try {
            Class[]	argTypes	= { Vector.class, String.class, String.class };
            Object[]	args		= { vector, s, type};
            return (String)RemoteMethodServer.getDefault().invoke(
               "getContents", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getContents" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getContents" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
   
   public ArrayList getPrimaryApplicationData( ContentHolder holder )
            throws WTException {

      if (SERVER)
         return ((DownloadService)getManager()).getPrimaryApplicationData( holder );
      else {
         try {
            Class[]	argTypes	= { ContentHolder.class };
            Object[]	args		= { holder};
            return (ArrayList)RemoteMethodServer.getDefault().invoke(
               "getPrimaryApplicationData", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getPrimaryApplicationData" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getPrimaryApplicationData" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
   
   public ArrayList getPrintApplicationData( ContentHolder holder )
            throws WTException {

      if (SERVER)
         return ((DownloadService)getManager()).getPrintApplicationData( holder );
      else {
         try {
            Class[]	argTypes	= { ContentHolder.class };
            Object[]	args		= { holder};
            return (ArrayList)RemoteMethodServer.getDefault().invoke(
               "getPrintApplicationData", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getPrintApplicationData" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getPrintApplicationData" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
   
   public ArrayList getWVSApplicationData( ContentHolder holder )
            throws WTException {

      if (SERVER)
         return ((DownloadService)getManager()).getWVSApplicationData( holder );
      else {
         try {
            Class[]	argTypes	= { ContentHolder.class };
            Object[]	args		= { holder};
            return (ArrayList)RemoteMethodServer.getDefault().invoke(
               "getWVSApplicationData", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getWVSApplicationData" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getWVSApplicationData" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
   
   public ArrayList getAttachmentApplicationData( ContentHolder holder )
            throws WTException {

      if (SERVER)
         return ((DownloadService)getManager()).getAttachmentApplicationData( holder );
      else {
         try {
            Class[]	argTypes	= { ContentHolder.class };
            Object[]	args		= { holder};
            return (ArrayList)RemoteMethodServer.getDefault().invoke(
               "getAttachmentApplicationData", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getAttachmentApplicationData" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getAttachmentApplicationData" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
}
