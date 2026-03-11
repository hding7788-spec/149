// Generated TechNoticeAfterLink%4BBEAF2600AF: ??? 04/09/10 12:47:50
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;

import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionToVersionLink;

//##begin TechNoticeAfterLink%4BBEAF2600AF.doc preserve=no
/**
 *
 * <p>
 * Use the <code>newTechNoticeAfterLink</code> static factory method(s),
 * not the <code>TechNoticeAfterLink</code> constructor, to construct instances
 * of this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/
//##end TechNoticeAfterLink%4BBEAF2600AF.doc

public class TechNoticeAfterLink extends VersionToVersionLink implements Externalizable {


   // --- Attribute Section ---


   private static final String RESOURCE = "ext.ases.technotice.technoticeResource";
   private static final String CLASSNAME = TechNoticeAfterLink.class.getName();

   //##begin AFTER_TECH_NOTICE_ROLE%AFTER_TECH_NOTICE_ROLE.doc preserve=no
   /**
    * Label for the attribute.
    **/
   //##end AFTER_TECH_NOTICE_ROLE%AFTER_TECH_NOTICE_ROLE.doc
   public static final String AFTER_TECH_NOTICE_ROLE = "afterTechNotice";


   //##begin AFTER_OBJECT_ROLE%AFTER_OBJECT_ROLE.doc preserve=no
   /**
    * Label for the attribute.
    **/
   //##end AFTER_OBJECT_ROLE%AFTER_OBJECT_ROLE.doc
   public static final String AFTER_OBJECT_ROLE = "afterObject";

   static final long serialVersionUID = 1;
   public static final long EXTERNALIZATION_VERSION_UID = 836198092899313377L;

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

      super.writeExternal( output );

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
         super.readExternal( input );                               // handle super class

      }
      else
         throw new java.io.InvalidClassException( CLASSNAME, "Local class not compatible:"
                           + " stream classdesc externalizationVersionUID=" + readSerialVersionUID 
                           + " local class externalizationVersionUID=" + EXTERNALIZATION_VERSION_UID );
      //##end readExternal%readExternal.body
   }

   //##begin writeExternal%writeExternal.doc preserve=no
   /**
    * Used by Persistent Data Service to obtain the values of the persistent
    * attributes of this class, so they can be written to a persistent store.
    * <p>(Not intended for general use.)
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     output
    * @exception java.sql.SQLException
    * @exception wt.pom.DatastoreException
    **/
   //##end writeExternal%writeExternal.doc

   public void writeExternal( PersistentStoreIfc output )
            throws SQLException, DatastoreException {
      super.writeExternal( output );

   }

   //##begin readExternal%readExternal.doc preserve=no
   /**
    * Used by Persistent Data Service to populate the persistent attributes
    * of this class from a persistent store. <p>(Not intended for general
    * use.)
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     input
    * @exception java.sql.SQLException
    * @exception wt.pom.DatastoreException
    **/
   //##end readExternal%readExternal.doc

   public void readExternal( PersistentRetrieveIfc input )
            throws SQLException, DatastoreException {
      super.readExternal( input );

   }

   //##begin getAfterTechNotice%4BBEB148039Dg.doc preserve=no
   /**
    * Gets the object for the association that plays role: AFTER_TECH_NOTICE_ROLE.
    *
    * @return    RevisionControlled
    **/
   //##end getAfterTechNotice%4BBEB148039Dg.doc

   public RevisionControlled getAfterTechNotice() {
      //##begin getAfterTechNotice%4BBEB148039Dg.body preserve=no

      return (RevisionControlled)getRoleAObject();
      //##end getAfterTechNotice%4BBEB148039Dg.body
   }

   //##begin setAfterTechNotice%4BBEB148039Ds.doc preserve=no
   /**
    * Sets the object for the association that plays role: AFTER_TECH_NOTICE_ROLE.
    *
    * @param     afterTechNotice
    * @exception wt.util.WTPropertyVetoException
    **/
   //##end setAfterTechNotice%4BBEB148039Ds.doc

   public void setAfterTechNotice( RevisionControlled afterTechNotice )
            throws WTPropertyVetoException {
      //##begin setAfterTechNotice%4BBEB148039Ds.body preserve=no

      setRoleAObject( (RevisionControlled) afterTechNotice );
      //##end setAfterTechNotice%4BBEB148039Ds.body
   }

   //##begin getAfterObject%4BBEB14803DCg.doc preserve=no
   /**
    * Gets the object for the association that plays role: AFTER_OBJECT_ROLE.
    *
    * @return    RevisionControlled
    **/
   //##end getAfterObject%4BBEB14803DCg.doc

   public RevisionControlled getAfterObject() {
      //##begin getAfterObject%4BBEB14803DCg.body preserve=no

      return (RevisionControlled)getRoleBObject();
      //##end getAfterObject%4BBEB14803DCg.body
   }

   //##begin setAfterObject%4BBEB14803DCs.doc preserve=no
   /**
    * Sets the object for the association that plays role: AFTER_OBJECT_ROLE.
    *
    * @param     afterObject
    * @exception wt.util.WTPropertyVetoException
    **/
   //##end setAfterObject%4BBEB14803DCs.doc

   public void setAfterObject( RevisionControlled afterObject )
            throws WTPropertyVetoException {
      //##begin setAfterObject%4BBEB14803DCs.body preserve=no

      setRoleBObject( (RevisionControlled) afterObject );
      //##end setAfterObject%4BBEB14803DCs.body
   }

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

   //##begin newTechNoticeAfterLink%newTechNoticeAfterLinkf.doc preserve=no
   /**
    * Default factory for the class.
    *
    * @param     afterTechNotice
    * @param     afterObject
    * @return    TechNoticeAfterLink
    * @exception wt.util.WTException
    **/
   //##end newTechNoticeAfterLink%newTechNoticeAfterLinkf.doc

   public static TechNoticeAfterLink newTechNoticeAfterLink( RevisionControlled afterTechNotice, RevisionControlled afterObject )
            throws WTException {
      //##begin newTechNoticeAfterLink%newTechNoticeAfterLinkf.body preserve=no

      TechNoticeAfterLink instance = new TechNoticeAfterLink();
      instance.initialize( afterTechNotice, afterObject );
      return instance;
      //##end newTechNoticeAfterLink%newTechNoticeAfterLinkf.body
   }

   //##begin equals%equals.doc preserve=no
   /**
    * Indicates whether the given object is equal to this object from a
    * persistence perspective, by comparing the two objects <code>ObjectIdentifier</code>s.
    * Changed or stale copies are still considered equal by this method.
    * Delegates to {@link wt.fc.PersistenceHelper#equals(Persistable,Object)}.
    * <p>
    * <b>Warning:</b> Certain core Windchill operations may depend upon
    * <code>equals</code> being <code>ObjectIdentifier</code>-based. Changes
    * to the default implementation should be done with care, if at all.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @param     obj
    * @return    boolean
    **/
   //##end equals%equals.doc

   public boolean equals( Object obj ) {
      //##begin equals%equals.body preserve=no

      // WARNING: Do not change
      return wt.fc.PersistenceHelper.equals(this,obj);
      //##end equals%equals.body
   }

   //##begin hashCode%hashCode.doc preserve=no
   /**
    * Returns a hash code for this object based upon its <code>ObjectIdentifier</code>.
    * Delegates to {@link wt.fc.PersistenceHelper#hashCode(Persistable)}.
    * <p>
    * <b>Warning:</b> Certain core Windchill operations may depend upon
    * <code>hashCode</code> being <code>ObjectIdentifier-based</code>. Changes
    * to the default implementation should be done with care, if at all.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @return    int
    **/
   //##end hashCode%hashCode.doc

   public int hashCode() {
      //##begin hashCode%hashCode.body preserve=no

      // WARNING: Do not change
      return wt.fc.PersistenceHelper.hashCode(this);
      //##end hashCode%hashCode.body
   }

   //##begin user.operations preserve=yes
   //##end user.operations
}
