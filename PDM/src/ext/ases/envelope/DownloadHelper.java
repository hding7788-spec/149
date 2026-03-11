// Generated DownloadHelper%4B4FF4ED0014: ??? 01/18/10 23:07:47
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
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.String;

//##begin user.imports preserve=yes
import wt.util.WTPropertyVetoException;  // Preserved unmodeled dependency
//##end user.imports

//##begin DownloadHelper%4B4FF4ED0014.doc preserve=no
/**
 *
 * @version   1.0
 **/
//##end DownloadHelper%4B4FF4ED0014.doc

public class DownloadHelper implements Externalizable {


   // --- Attribute Section ---


   private static final String RESOURCE = "ext.ases.envelope.envelopeResource";
   private static final String CLASSNAME = DownloadHelper.class.getName();

   //##begin service%4B4FF6920321.doc preserve=no
   /**
    * <BR><BR><B>Supported API: </B>true
    **/
   //##end service%4B4FF6920321.doc
   public static final DownloadService service = new DownloadServiceFwd();
   static final long serialVersionUID = 1;
   public static final long EXTERNALIZATION_VERSION_UID = 957977401221134810L;

   // WARNING: Fields placed in this section will not be generated into externalization methods.
   //##begin user.attributes preserve=yes
   //##end user.attributes

   //##begin static.initialization preserve=yes
   //##end static.initialization


   // --- Operation Section ---

   //##begin writeExternal%writeExternal.doc preserve=no
   /**
    * Writes the non-transient fields of this class to an external source.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     output
    * @exception java.io.IOException
    **/
   //##end writeExternal%writeExternal.doc

   public void writeExternal( ObjectOutput output )
            throws IOException {
      //##begin writeExternal%writeExternal.body preserve=no

      output.writeLong( EXTERNALIZATION_VERSION_UID );

      //##end writeExternal%writeExternal.body
   }

   //##begin readExternal%readExternal.doc preserve=no
   /**
    * Reads the non-transient fields of this class from an external source.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     input
    * @exception java.io.IOException
    * @exception java.lang.ClassNotFoundException
    **/
   //##end readExternal%readExternal.doc

   public void readExternal( ObjectInput input )
            throws IOException, ClassNotFoundException {
      //##begin readExternal%readExternal.body preserve=no

      long readSerialVersionUID = input.readLong();                // consume UID

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID ) {  // if current version UID
      }
      else
         throw new java.io.InvalidClassException( CLASSNAME, "Local class not compatible:"
                           + " stream classdesc externalizationVersionUID=" + readSerialVersionUID 
                           + " local class externalizationVersionUID=" + EXTERNALIZATION_VERSION_UID );
      //##end readExternal%readExternal.body
   }

   //##begin user.operations preserve=yes
   //##end user.operations
}
