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

package ext.ases.envelope;

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

public class StandardEnvelopeService extends StandardManager implements EnvelopeService, Serializable {


   // --- Attribute Section ---


   private static final String RESOURCE = "ext.ases.envelope.envelopeResource";
   private static final String CLASSNAME = StandardEnvelopeService.class.getName();

   //##begin user.attributes preserve=yes
   private static final Logger log;
   //##end user.attributes

   //##begin static.initialization preserve=yes

    static {
       try {
          log = LogR.getLogger(CreateEnvelopeFormProcessor.class.getName());
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

   public static StandardEnvelopeService newStandardEnvelopeService()
            throws WTException {
      //##begin newStandardEnvelopeService%newStandardEnvelopeServicef.body preserve=no

      StandardEnvelopeService instance = new StandardEnvelopeService();
      instance.initialize();
      return instance;
      //##end newStandardEnvelopeService%newStandardEnvelopeServicef.body
   }

   //##begin getAllMembers%4B4FF6C900FEg.doc preserve=no
   /**
    * @param     processEnvelope
    * @return    ArrayList
    * @exception wt.util.WTException
    **/
   //##end getAllMembers%4B4FF6C900FEg.doc

   public ArrayList getAllMembers( ProcessEnvelope processEnvelope )
            throws WTException {
      //##begin getAllMembers%4B4FF6C900FEg.body preserve=yes
      QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled", ext.ases.envelope.EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        ArrayList envelopeMembertLinks = new ArrayList();
        for(; qr.hasMoreElements(); envelopeMembertLinks.add(obj))
        {
        	 obj = ((EnvelopeMemberLink)qr.nextElement()).getRevisionControlled();
        } 
        return envelopeMembertLinks;
      //##end getAllMembers%4B4FF6C900FEg.body
   }

   //##begin getTopObject%4B5470C40350g.doc preserve=no
   /**
    * @param     processEnvelope
    * @return    RevisionControlled
    * @exception wt.util.WTException
    **/
   //##end getTopObject%4B5470C40350g.doc

   public RevisionControlled getTopObject( ProcessEnvelope processEnvelope )
            throws WTException {
      //##begin getTopObject%4B5470C40350g.body preserve=yes
      QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "topObject", ext.ases.envelope.EnvelopeTopObjLink.class, false);
      RevisionControlled obj = null;
      while(qr.hasMoreElements()){
      	obj = ((EnvelopeTopObjLink)qr.nextElement()).getTopObject();
      }
      return obj;
      //##end getTopObject%4B5470C40350g.body
   }

   //##begin user.operations preserve=yes
   public QueryResult getAllEnvelopeMembers( ProcessEnvelope processEnvelope )
            throws WTException {
      QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled", ext.ases.envelope.EnvelopeMemberLink.class, false);
      return qr;
   }
   //保存EnvelopeTopObjLink
   public void saveTopObjLink(ProcessEnvelope processEnvelope,RevisionControlled revisioncontrolled) throws WTException{
		try
		{
			EnvelopeTopObjLink envelotopobjlink = EnvelopeTopObjLink.newEnvelopeTopObjLink(processEnvelope,revisioncontrolled);					
			PersistenceHelper.manager.save(envelotopobjlink);
		}
		catch (wt.util.WTException e){
			e.printStackTrace();
		}
	}
	
	//保存EnvelopeMemberLink
   public void saveRelatedObjLink(ProcessEnvelope processEnvelope,RevisionControlled revisioncontrolled) throws WTException{
		try
		{
			EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(processEnvelope,revisioncontrolled);					
			PersistenceHelper.manager.save(envelopememberlink);
		}
		catch (wt.util.WTException e){
			e.printStackTrace();
		}
	}
	//根据对象获得所有的EnvelopeTopObjLink
	public QueryResult getEnvelopeByTopObject( RevisionControlled revisioncontrolled )
            throws WTException {
      QueryResult qr = PersistenceHelper.manager.navigate(revisioncontrolled, "topForEnvelope", ext.ases.envelope.EnvelopeTopObjLink.class, false);
      return qr;
   }
   
   //根据对象获得所有的EnvelopeMemberLink
   public QueryResult getEnvelopeByMemberObject( RevisionControlled revisioncontrolled )
            throws WTException {
      QueryResult qr = PersistenceHelper.manager.navigate(revisioncontrolled, "theProcessEnvelope", ext.ases.envelope.EnvelopeMemberLink.class, false);
      return qr;
   }

   //##end user.operations
}
