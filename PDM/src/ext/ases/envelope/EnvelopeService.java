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

package ext.ases.envelope;

import ext.ases.envelope.ProcessEnvelope;
import java.util.ArrayList;
import wt.enterprise.RevisionControlled;
import wt.method.RemoteInterface;
import wt.util.WTException;

//##begin user.imports preserve=yes
import java.beans.PropertyVetoException;  // Preserved unmodeled dependency
import java.io.IOException;  // Preserved unmodeled dependency
import wt.representation.Representable;  // Preserved unmodeled dependency
import java.util.*;
import java.lang.Class;  // Preserved unmodeled dependency
import java.util.Vector;  // Preserved unmodeled dependency
import wt.fc.QueryResult;
//##end user.imports

//##begin EnvelopeService%4B4FF4F901C9.doc preserve=no
/**
 *
 * @version   1.0
 **/
//##end EnvelopeService%4B4FF4F901C9.doc

@RemoteInterface
public interface EnvelopeService {


   //##begin user.attributes preserve=yes
   //##end user.attributes

   //##begin static.initialization preserve=yes
   //##end static.initialization


   // --- Operation Section ---

   //##begin getAllMembers%4B4FF6C900FEg.doc preserve=no
   /**
    * @param     processEnvelope
    * @return    ArrayList
    * @exception wt.util.WTException
    **/
   //##end getAllMembers%4B4FF6C900FEg.doc

   public ArrayList getAllMembers( ProcessEnvelope processEnvelope )
            throws WTException;

   //##begin getTopObject%4B5470C40350g.doc preserve=no
   /**
    * @param     processEnvelope
    * @return    RevisionControlled
    * @exception wt.util.WTException
    **/
   //##end getTopObject%4B5470C40350g.doc

   public RevisionControlled getTopObject( ProcessEnvelope processEnvelope )
            throws WTException;

   //##begin user.operations preserve=yes
   //public QueryResult getAllEnvelopeMembers( ProcessEnvelope processEnvelope )
   //         throws WTException;
   
   public void saveTopObjLink (ProcessEnvelope processEnvelope,RevisionControlled revisioncontrolled)
            throws WTException;
            
   public void saveRelatedObjLink (ProcessEnvelope processEnvelope,RevisionControlled revisioncontrolled)
            throws WTException;
            
   public QueryResult getEnvelopeByTopObject( RevisionControlled revisioncontrolled )
            throws WTException;
            
   public QueryResult getEnvelopeByMemberObject( RevisionControlled revisioncontrolled )
            throws WTException;
            	
   //##end user.operations
}
