// Generated DownloadService%4B4FF4F901C9: ??? 01/18/10 22:34:20
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
import wt.content.*;
//##end user.imports

//##begin DownloadService%4B4FF4F901C9.doc preserve=no
/**
 *
 * @version   1.0
 **/
//##end DownloadService%4B4FF4F901C9.doc

@RemoteInterface
public interface DownloadService {


   //##begin user.attributes preserve=yes
   //##end user.attributes

   //##begin static.initialization preserve=yes
   //##end static.initialization


   // --- Operation Section ---


   //##begin user.operations preserve=yes
   public String getContents( Vector  vector, String number, String type )
            throws WTException;
   public ArrayList getPrimaryApplicationData(ContentHolder holder)
   	        throws WTException ;
   public ArrayList getPrintApplicationData(ContentHolder holder)
   	        throws WTException ;
   public ArrayList getWVSApplicationData(ContentHolder holder)
   	        throws WTException ;
   public ArrayList getAttachmentApplicationData(ContentHolder holder)
   	        throws WTException ;
   //##end user.operations
}
