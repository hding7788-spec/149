// Generated EnvelopeService%4B4FF4F901C9: ??? 01/18/10 22:34:20
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

import java.util.ArrayList;

import wt.enterprise.RevisionControlled;
import wt.method.RemoteInterface;
import wt.util.WTException;

//##begin EnvelopeService%4B4FF4F901C9.doc preserve=no
/**
 *
 * @version   1.0
 **/
//##end EnvelopeService%4B4FF4F901C9.doc

@RemoteInterface
public interface TechNoticeService {


   //##begin user.attributes preserve=yes
   //##end user.attributes

   //##begin static.initialization preserve=yes
   //##end static.initialization


   // --- Operation Section ---

   //##begin getTechNoticeBeforeMembers%4B4FF6C900FEg.doc preserve=no
   /**
    * @param     processEnvelope
    * @return    ArrayList
    * @exception wt.util.WTException
    **/
   //##end getTechNoticeBeforeMembers%4B4FF6C900FEg.doc

   public ArrayList getTechNoticeBeforeMembers( RevisionControlled wtdoc )
            throws WTException;
   
 //##begin getTechNoticeAfterMembers%4B4FF6C900FEg.doc preserve=no
   /**
    * @param     processEnvelope
    * @return    ArrayList
    * @exception wt.util.WTException
    **/
   //##end getTechNoticeAfterMembers%4B4FF6C900FEg.doc

   public ArrayList getTechNoticeAfterMembers( RevisionControlled wtdoc )
            throws WTException;

   //##begin user.operations preserve=yes
            	
   //##end user.operations
}
