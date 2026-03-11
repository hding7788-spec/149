package ext.ases.part;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ASESHuiqianSignature extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.ases.part.partResource";
   static final String CLASSNAME = ASESHuiqianSignature.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public static final String ACTIVITY = "activity";
   static int ACTIVITY_UPPER_LIMIT = -1;
   java.lang.String activity;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public java.lang.String getActivity() {
      return activity;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public void setActivity(java.lang.String activity) throws wt.util.WTPropertyVetoException {
      activityValidate(activity);
      this.activity = activity;
   }
   void activityValidate(java.lang.String activity) throws wt.util.WTPropertyVetoException {
      if (ACTIVITY_UPPER_LIMIT < 1) {
         try { ACTIVITY_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("activity").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ACTIVITY_UPPER_LIMIT = 200; }
      }
      if (activity != null && !wt.fc.PersistenceHelper.checkStoredLength(activity.toString(), ACTIVITY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "activity"), String.valueOf(Math.min(ACTIVITY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "activity", this.activity, activity));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public static final String CONCLUSION = "conclusion";
   static int CONCLUSION_UPPER_LIMIT = -1;
   java.lang.String conclusion;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public java.lang.String getConclusion() {
      return conclusion;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public void setConclusion(java.lang.String conclusion) throws wt.util.WTPropertyVetoException {
      conclusionValidate(conclusion);
      this.conclusion = conclusion;
   }
   void conclusionValidate(java.lang.String conclusion) throws wt.util.WTPropertyVetoException {
      if (CONCLUSION_UPPER_LIMIT < 1) {
         try { CONCLUSION_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("conclusion").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CONCLUSION_UPPER_LIMIT = 200; }
      }
      if (conclusion != null && !wt.fc.PersistenceHelper.checkStoredLength(conclusion.toString(), CONCLUSION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "conclusion"), String.valueOf(Math.min(CONCLUSION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "conclusion", this.conclusion, conclusion));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public static final String SIGNATURE = "signature";
   static int SIGNATURE_UPPER_LIMIT = -1;
   java.lang.String signature;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public java.lang.String getSignature() {
      return signature;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public void setSignature(java.lang.String signature) throws wt.util.WTPropertyVetoException {
      signatureValidate(signature);
      this.signature = signature;
   }
   void signatureValidate(java.lang.String signature) throws wt.util.WTPropertyVetoException {
      if (SIGNATURE_UPPER_LIMIT < 1) {
         try { SIGNATURE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("signature").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { SIGNATURE_UPPER_LIMIT = 200; }
      }
      if (signature != null && !wt.fc.PersistenceHelper.checkStoredLength(signature.toString(), SIGNATURE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "signature"), String.valueOf(Math.min(SIGNATURE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "signature", this.signature, signature));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public static final String OPINION = "opinion";
   static int OPINION_UPPER_LIMIT = -1;
   java.lang.String opinion;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public java.lang.String getOpinion() {
      return opinion;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public void setOpinion(java.lang.String opinion) throws wt.util.WTPropertyVetoException {
      opinionValidate(opinion);
      this.opinion = opinion;
   }
   void opinionValidate(java.lang.String opinion) throws wt.util.WTPropertyVetoException {
      if (OPINION_UPPER_LIMIT < 1) {
         try { OPINION_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("opinion").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OPINION_UPPER_LIMIT = 200; }
      }
      if (opinion != null && !wt.fc.PersistenceHelper.checkStoredLength(opinion.toString(), OPINION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "opinion"), String.valueOf(Math.min(OPINION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "opinion", this.opinion, opinion));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public static final String UPDATESTATE = "updatestate";
   static int UPDATESTATE_UPPER_LIMIT = -1;
   java.lang.String updatestate;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public java.lang.String getUpdatestate() {
      return updatestate;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public void setUpdatestate(java.lang.String updatestate) throws wt.util.WTPropertyVetoException {
//      updatestateValidate(updatestate);
      this.updatestate = updatestate;
   }
   void updatestateValidate(java.lang.String updatestate) throws wt.util.WTPropertyVetoException {
      if (UPDATESTATE_UPPER_LIMIT < 1) {
         try { UPDATESTATE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("updatestate").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { UPDATESTATE_UPPER_LIMIT = 200; }
      }
      if (updatestate != null && !wt.fc.PersistenceHelper.checkStoredLength(updatestate.toString(), UPDATESTATE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "updatestate"), String.valueOf(Math.min(UPDATESTATE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "updatestate", this.updatestate, updatestate));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public static final String IMPLEMENTADVISE = "implementadvise";
   static int IMPLEMENTADVISE_UPPER_LIMIT = -1;
   java.lang.String implementadvise;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public java.lang.String getImplementadvise() {
      return implementadvise;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.part.ASESHuiqianSignature
    */
   public void setImplementadvise(java.lang.String implementadvise) throws wt.util.WTPropertyVetoException {
//      implementadviseValidate(implementadvise);
      this.implementadvise = implementadvise;
   }
   void implementadviseValidate(java.lang.String implementadvise) throws wt.util.WTPropertyVetoException {
      if (IMPLEMENTADVISE_UPPER_LIMIT < 1) {
         try { IMPLEMENTADVISE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("implementadvise").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { IMPLEMENTADVISE_UPPER_LIMIT = 200; }
      }
      if (implementadvise != null && !wt.fc.PersistenceHelper.checkStoredLength(implementadvise.toString(), IMPLEMENTADVISE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "implementadvise"), String.valueOf(Math.min(IMPLEMENTADVISE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "implementadvise", this.implementadvise, implementadvise));
   }

   public String getConceptualClassname() {
      return CLASSNAME;
   }

   public wt.introspection.ClassInfo getClassInfo() throws wt.introspection.WTIntrospectionException {
      return wt.introspection.WTIntrospector.getClassInfo(getConceptualClassname());
   }

   public String getType() {
      try { return getClassInfo().getDisplayName(); }
      catch (wt.introspection.WTIntrospectionException wte) { return wt.util.WTStringUtilities.tail(getConceptualClassname(), '.'); }
   }

   public static final long EXTERNALIZATION_VERSION_UID = -3601515701721850833L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( activity );
      output.writeObject( conclusion );
      output.writeObject( implementadvise );
      output.writeObject( opinion );
      output.writeObject( signature );
      output.writeObject( updatestate );
   }

   protected void super_writeExternal_ASESHuiqianSignature(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.ases.part.ASESHuiqianSignature) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ASESHuiqianSignature(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "activity", activity );
      output.setString( "conclusion", conclusion );
      output.setString( "implementadvise", implementadvise );
      output.setString( "opinion", opinion );
      output.setString( "signature", signature );
      output.setString( "updatestate", updatestate );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      activity = input.getString( "activity" );
      conclusion = input.getString( "conclusion" );
      implementadvise = input.getString( "implementadvise" );
      opinion = input.getString( "opinion" );
      signature = input.getString( "signature" );
      updatestate = input.getString( "updatestate" );
   }

   boolean readVersion_3601515701721850833L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      activity = (java.lang.String) input.readObject();
      conclusion = (java.lang.String) input.readObject();
      implementadvise = (java.lang.String) input.readObject();
      opinion = (java.lang.String) input.readObject();
      signature = (java.lang.String) input.readObject();
      updatestate = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( ASESHuiqianSignature thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_3601515701721850833L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ASESHuiqianSignature( _ASESHuiqianSignature thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
