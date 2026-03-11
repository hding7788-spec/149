// Generated TechNoticeBeforeLink%4BBEAF0B011D: ??? 04/09/10 12:47:50
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

//##begin TechNoticeBeforeLink%4BBEAF0B011D.doc preserve=no
/**
 *
 * <p>
 * Use the <code>newTechNoticeBeforeLink</code> static factory method(s),
 * not the <code>TechNoticeBeforeLink</code> constructor, to construct instances
 * of this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/
//##end TechNoticeBeforeLink%4BBEAF0B011D.doc

public class TechNoticeBeforeLink extends VersionToVersionLink implements Externalizable {


   // --- Attribute Section ---


   private static final String RESOURCE = "ext.ases.technotice.technoticeResource";
   private static final String CLASSNAME = TechNoticeBeforeLink.class.getName();

   //##begin BEFORE_TECH_NOTICE_ROLE%BEFORE_TECH_NOTICE_ROLE.doc preserve=no
   /**
    * Label for the attribute.
    **/
   //##end BEFORE_TECH_NOTICE_ROLE%BEFORE_TECH_NOTICE_ROLE.doc
   public static final String BEFORE_TECH_NOTICE_ROLE = "beforeTechNotice";


   //##begin BEFORE_OBJECT_ROLE%BEFORE_OBJECT_ROLE.doc preserve=no
   /**
    * Label for the attribute.
    **/
   //##end BEFORE_OBJECT_ROLE%BEFORE_OBJECT_ROLE.doc
   public static final String BEFORE_OBJECT_ROLE = "beforeObject";

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

   //##begin getBeforeTechNotice%4BBEB0F3010Dg.doc preserve=no
   /**
    * Gets the object for the association that plays role: BEFORE_TECH_NOTICE_ROLE.
    *
    * @return    RevisionControlled
    **/
   //##end getBeforeTechNotice%4BBEB0F3010Dg.doc

   public RevisionControlled getBeforeTechNotice() {
      //##begin getBeforeTechNotice%4BBEB0F3010Dg.body preserve=no

      return (RevisionControlled)getRoleAObject();
      //##end getBeforeTechNotice%4BBEB0F3010Dg.body
   }

   //##begin setBeforeTechNotice%4BBEB0F3010Ds.doc preserve=no
   /**
    * Sets the object for the association that plays role: BEFORE_TECH_NOTICE_ROLE.
    *
    * @param     beforeTechNotice
    * @exception wt.util.WTPropertyVetoException
    **/
   //##end setBeforeTechNotice%4BBEB0F3010Ds.doc

   public void setBeforeTechNotice( RevisionControlled beforeTechNotice )
            throws WTPropertyVetoException {
      //##begin setBeforeTechNotice%4BBEB0F3010Ds.body preserve=no

      setRoleAObject( (RevisionControlled) beforeTechNotice );
      //##end setBeforeTechNotice%4BBEB0F3010Ds.body
   }

   //##begin getBeforeObject%4BBEB0F3013Cg.doc preserve=no
   /**
    * Gets the object for the association that plays role: BEFORE_OBJECT_ROLE.
    *
    * @return    RevisionControlled
    **/
   //##end getBeforeObject%4BBEB0F3013Cg.doc

   public RevisionControlled getBeforeObject() {
      //##begin getBeforeObject%4BBEB0F3013Cg.body preserve=no

      return (RevisionControlled)getRoleBObject();
      //##end getBeforeObject%4BBEB0F3013Cg.body
   }

   //##begin setBeforeObject%4BBEB0F3013Cs.doc preserve=no
   /**
    * Sets the object for the association that plays role: BEFORE_OBJECT_ROLE.
    *
    * @param     beforeObject
    * @exception wt.util.WTPropertyVetoException
    **/
   //##end setBeforeObject%4BBEB0F3013Cs.doc

   public void setBeforeObject( RevisionControlled beforeObject )
            throws WTPropertyVetoException {
      //##begin setBeforeObject%4BBEB0F3013Cs.body preserve=no

      setRoleBObject( (RevisionControlled) beforeObject );
      //##end setBeforeObject%4BBEB0F3013Cs.body
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

   //##begin newTechNoticeBeforeLink%newTechNoticeBeforeLinkf.doc preserve=no
   /**
    * Default factory for the class.
    *
    * @param     beforeTechNotice
    * @param     beforeObject
    * @return    TechNoticeBeforeLink
    * @exception wt.util.WTException
    **/
   //##end newTechNoticeBeforeLink%newTechNoticeBeforeLinkf.doc

   public static TechNoticeBeforeLink newTechNoticeBeforeLink( RevisionControlled beforeTechNotice, RevisionControlled beforeObject )
            throws WTException {
      //##begin newTechNoticeBeforeLink%newTechNoticeBeforeLinkf.body preserve=no

      TechNoticeBeforeLink instance = new TechNoticeBeforeLink();
      instance.initialize( beforeTechNotice, beforeObject );
      return instance;
      //##end newTechNoticeBeforeLink%newTechNoticeBeforeLinkf.body
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
