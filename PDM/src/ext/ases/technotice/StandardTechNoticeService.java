// Generated StandardEnvelopeService%4B4FF5330311: ??? 01/18/10 22:34:20
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
import java.util.ArrayList;

import org.apache.log4j.Logger;

import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.services.StandardManager;
import wt.util.WTException;

//##begin StandardEnvelopeService%4B4FF5330311.doc preserve=no
/**
 *
 * <p>
 * Use the <code>newStandardEnvelopeService</code> static factory method(s),
 * not the <code>StandardEnvelopeService</code> constructor, to construct
 * instances of this class.  Instances must be constructed using the static
 * factory(s), in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/
//##end StandardEnvelopeService%4B4FF5330311.doc

public class StandardTechNoticeService extends StandardManager implements TechNoticeService, Serializable {


   // --- Attribute Section ---

   private static final String CLASSNAME = StandardTechNoticeService.class.getName();

   //##begin user.attributes preserve=yes
   private static final Logger log;
   //##end user.attributes

   //##begin static.initialization preserve=yes

    static {
       try {
          log = LogR.getLogger(StandardTechNoticeService.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }
   //##end static.initialization


   // --- Operation Section ---

   //##begin getConceptualClassname%getConceptualClassnameg.doc preserve=no
   /**
    * Returns the conceptual (modeled) name for the class.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @deprecated
    *
    * @return    String
    **/
   //##end getConceptualClassname%getConceptualClassnameg.doc

   public String getConceptualClassname() {
      //##begin getConceptualClassname%getConceptualClassnameg.body preserve=no

      return CLASSNAME;
      //##end getConceptualClassname%getConceptualClassnameg.body
   }

   //##begin newStandardEnvelopeService%newStandardEnvelopeServicef.doc preserve=no
   /**
    * Default factory for the class.
    *
    * @return    StandardEnvelopeService
    * @exception wt.util.WTException
    **/
   //##end newStandardEnvelopeService%newStandardEnvelopeServicef.doc

   public static StandardTechNoticeService newStandardTechNoticeService()
            throws WTException {
      //##begin newStandardEnvelopeService%newStandardEnvelopeServicef.body preserve=no

	   StandardTechNoticeService instance = new StandardTechNoticeService();
      instance.initialize();
      return instance;
      //##end newStandardEnvelopeService%newStandardEnvelopeServicef.body
   }

   //##begin getTechNoticeBeforeMembers%4B4FF6C900FEg.doc preserve=no
   /**
    * @param     processEnvelope
    * @return    ArrayList
    * @exception wt.util.WTException
    **/
   //##end getTechNoticeBeforeMembers%4B4FF6C900FEg.doc

   public ArrayList  getTechNoticeBeforeMembers( RevisionControlled wtdoc )
            throws WTException {
      //##begin getAllMembers%4B4FF6C900FEg.body preserve=yes
      QueryResult qr = PersistenceHelper.manager.navigate(wtdoc, "beforeObject", ext.ases.technotice.TechNoticeBeforeLink.class, false);
        RevisionControlled obj = null;
        ArrayList techNoticeBeforeLinks = new ArrayList();
        for(; qr.hasMoreElements(); techNoticeBeforeLinks.add(obj))
        {
        	 obj = ((TechNoticeBeforeLink)qr.nextElement()).getBeforeObject();
        } 
        return techNoticeBeforeLinks;
      //##end getTechNoticeBeforeMembers%4B4FF6C900FEg.body
   }

   //##begin getTechNoticeAfterMembers%4B4FF6C900FEg.doc preserve=no
   /**
    * @param     processEnvelope
    * @return    ArrayList
    * @exception wt.util.WTException
    **/
   //##end getTechNoticeAfterMembers%4B4FF6C900FEg.doc

   public ArrayList  getTechNoticeAfterMembers( RevisionControlled wtdoc )
            throws WTException {
      //##begin getAllMembers%4B4FF6C900FEg.body preserve=yes
      QueryResult qr = PersistenceHelper.manager.navigate(wtdoc, "afterObject", ext.ases.technotice.TechNoticeAfterLink.class, false);
        RevisionControlled obj = null;
        ArrayList techNoticeAftertLinks = new ArrayList();
        for(; qr.hasMoreElements(); techNoticeAftertLinks.add(obj))
        {
        	 obj = ((TechNoticeAfterLink)qr.nextElement()).getAfterObject();
        } 
        return techNoticeAftertLinks;
      //##end getAllMembers%4B4FF6C900FEg.body
   }

   //##begin user.operations preserve=yes
  
   //##end user.operations
}
