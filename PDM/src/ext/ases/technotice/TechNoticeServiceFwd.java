// Generated TechNoticeServiceFwd%4B4FF4F901C9: ??? 01/18/10 22:34:20
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

package ext.ases.technotice;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import wt.enterprise.RevisionControlled;
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

public class TechNoticeServiceFwd implements RemoteAccess, TechNoticeService, Serializable {


   // --- Attribute Section ---


   static final boolean SERVER = RemoteMethodServer.ServerFlag;
   private static final String FC_RESOURCE = "wt.fc.fcResource";
   private static final String CLASSNAME = TechNoticeServiceFwd.class.getName();


   // --- Operation Section ---

   /**
    * @return    Manager
    * @exception wt.util.WTException
    **/
   private static Manager getManager()
            throws WTException {

      Manager manager = ManagerServiceFactory.getDefault().getManager( ext.ases.technotice.TechNoticeService.class );
      
      if ( manager == null ) {
         Object[] param = { "ext.ases.technotice.TechNoticeService" };
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
   public ArrayList getTechNoticeBeforeMembers( RevisionControlled wtdoc )
            throws WTException {

      if (SERVER)
         return ((TechNoticeService)getManager()).getTechNoticeBeforeMembers( wtdoc );
      else {
         try {
            Class[]	argTypes	= { RevisionControlled.class };
            Object[]	args		= { wtdoc };
            return (ArrayList)RemoteMethodServer.getDefault().invoke(
               "getTechNoticeBeforeMembers", null, this, argTypes, args );
         }
         catch (InvocationTargetException e) {
            Throwable targetE = e.getTargetException();
            if ( targetE instanceof WTException )
               throw (WTException)targetE;
            Object[] param = { "getTechNoticeBeforeMembers" };
            throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
         catch (RemoteException rme) {
            Object[] param = { "getTechNoticeBeforeMembers" };
            throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
         }
      }
   }
   
   /**
   *
   * <BR><BR><B>Supported API: </B>false
   *
   * @param     processEnvelope
   * @return    ArrayList
   * @exception wt.util.WTException
   **/
  public ArrayList getTechNoticeAfterMembers( RevisionControlled wtdoc )
           throws WTException {

     if (SERVER)
        return ((TechNoticeService)getManager()).getTechNoticeAfterMembers( wtdoc );
     else {
        try {
           Class[]	argTypes	= { RevisionControlled.class };
           Object[]	args		= { wtdoc };
           return (ArrayList)RemoteMethodServer.getDefault().invoke(
              "getTechNoticeAfterMembers", null, this, argTypes, args );
        }
        catch (InvocationTargetException e) {
           Throwable targetE = e.getTargetException();
           if ( targetE instanceof WTException )
              throw (WTException)targetE;
           Object[] param = { "getTechNoticeAfterMembers" };
           throw new WTException( targetE, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
        }
        catch (RemoteException rme) {
           Object[] param = { "getTechNoticeAfterMembers" };
           throw new WTException( rme, FC_RESOURCE, wt.fc.fcResource.OPERATION_FAILURE, param );
        }
     }
  }
}
