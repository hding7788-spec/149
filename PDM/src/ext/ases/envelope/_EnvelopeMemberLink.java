package ext.ases.envelope;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _EnvelopeMemberLink extends wt.vc.ObjectToVersionLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.ases.envelope.envelopeResource";
   static final String CLASSNAME = EnvelopeMemberLink.class.getName();

   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public static final String DESCRIPTION = "description";
   static int DESCRIPTION_UPPER_LIMIT = -1;
   java.lang.String description;
   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public java.lang.String getDescription() {
      return description;
   }
   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public void setDescription(java.lang.String description) throws wt.util.WTPropertyVetoException {
      descriptionValidate(description);
      this.description = description;
   }
   void descriptionValidate(java.lang.String description) throws wt.util.WTPropertyVetoException {
      if (DESCRIPTION_UPPER_LIMIT < 1) {
         try { DESCRIPTION_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("description").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DESCRIPTION_UPPER_LIMIT = 200; }
      }
      if (description != null && !wt.fc.PersistenceHelper.checkStoredLength(description.toString(), DESCRIPTION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "description"), String.valueOf(Math.min(DESCRIPTION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "description", this.description, description));
   }

   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public static final String IMPLEMENTADVISE = "implementadvise";
   static int IMPLEMENTADVISE_UPPER_LIMIT = -1;
   java.lang.String implementadvise;
   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public java.lang.String getImplementadvise() {
      return implementadvise;
   }
   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public void setImplementadvise(java.lang.String implementadvise) throws wt.util.WTPropertyVetoException {
      implementadviseValidate(implementadvise);
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

   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public static final String PROCESS_ENVELOPE_ROLE = "theProcessEnvelope";
   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public ext.ases.envelope.ProcessEnvelope getProcessEnvelope() {
      return (ext.ases.envelope.ProcessEnvelope) getRoleAObject();
   }
   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public void setProcessEnvelope(ext.ases.envelope.ProcessEnvelope the_theProcessEnvelope) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_theProcessEnvelope);
   }

   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public static final String REVISION_CONTROLLED_ROLE = "theRevisionControlled";
   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public wt.enterprise.RevisionControlled getRevisionControlled() {
      return (wt.enterprise.RevisionControlled) getRoleBObject();
   }
   /**
    * @see ext.ases.envelope.EnvelopeMemberLink
    */
   public void setRevisionControlled(wt.enterprise.RevisionControlled the_theRevisionControlled) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_theRevisionControlled);
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

   public static final long EXTERNALIZATION_VERSION_UID = 1706752575197569083L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( description );
      output.writeObject( implementadvise );
   }

   protected void super_writeExternal_EnvelopeMemberLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.ases.envelope.EnvelopeMemberLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_EnvelopeMemberLink(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "description", description );
      output.setString( "implementadvise", implementadvise );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      description = input.getString( "description" );
      implementadvise = input.getString( "implementadvise" );
   }

   boolean readVersion1706752575197569083L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      description = (java.lang.String) input.readObject();
      implementadvise = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( EnvelopeMemberLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion1706752575197569083L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_EnvelopeMemberLink( _EnvelopeMemberLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
