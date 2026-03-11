package ext.ases.changepackaged;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ChangePackagedResultLink extends wt.vc.ObjectToVersionLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.ases.changepackaged.changepackagedResource";
   static final String CLASSNAME = ChangePackagedResultLink.class.getName();

   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public static final String DESCRIPTION = "description";
   static int DESCRIPTION_UPPER_LIMIT = -1;
   java.lang.String description;
   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public java.lang.String getDescription() {
      return description;
   }
   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
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
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public static final String IMPLEMENTADVISE = "implementadvise";
   static int IMPLEMENTADVISE_UPPER_LIMIT = -1;
   java.lang.String implementadvise;
   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public java.lang.String getImplementadvise() {
      return implementadvise;
   }
   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
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
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public static final String CHANGE_PACKAGED = "theChangePackaged";
   static int CHANGE_PACKAGED_UPPER_LIMIT = -1;
   java.lang.String theChangePackaged;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public java.lang.String getChangePackaged() {
      return theChangePackaged;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public void setChangePackaged(java.lang.String theChangePackaged) throws wt.util.WTPropertyVetoException {
      theChangePackagedValidate(theChangePackaged);
      this.theChangePackaged = theChangePackaged;
   }
   void theChangePackagedValidate(java.lang.String theChangePackaged) throws wt.util.WTPropertyVetoException {
      if (CHANGE_PACKAGED_UPPER_LIMIT < 1) {
         try { CHANGE_PACKAGED_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("theChangePackaged").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CHANGE_PACKAGED_UPPER_LIMIT = 200; }
      }
      if (theChangePackaged != null && !wt.fc.PersistenceHelper.checkStoredLength(theChangePackaged.toString(), CHANGE_PACKAGED_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "theChangePackaged"), String.valueOf(Math.min(CHANGE_PACKAGED_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "theChangePackaged", this.theChangePackaged, theChangePackaged));
   }

   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public static final String REVISION_CONTROLLED_ROLE = "theRevisionControlled";
   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public wt.enterprise.RevisionControlled getRevisionControlled() {
      return (wt.enterprise.RevisionControlled) getRoleBObject();
   }
   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public void setRevisionControlled(wt.enterprise.RevisionControlled the_theRevisionControlled) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_theRevisionControlled);
   }

   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public static final String PROCESS_ENVELOPE_ROLE = "theProcessEnvelope";
   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public ext.ases.changepackaged.ChangePackaged getProcessEnvelope() {
      return (ext.ases.changepackaged.ChangePackaged) getRoleAObject();
   }
   /**
    * @see ext.ases.changepackaged.ChangePackagedResultLink
    */
   public void setProcessEnvelope(ext.ases.changepackaged.ChangePackaged the_theProcessEnvelope) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_theProcessEnvelope);
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

   public static final long EXTERNALIZATION_VERSION_UID = -721830272130892700L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( description );
      output.writeObject( implementadvise );
      output.writeObject( theChangePackaged );
   }

   protected void super_writeExternal_ChangePackagedResultLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.ases.changepackaged.ChangePackagedResultLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ChangePackagedResultLink(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "description", description );
      output.setString( "implementadvise", implementadvise );
      output.setString( "theChangePackaged", theChangePackaged );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      description = input.getString( "description" );
      implementadvise = input.getString( "implementadvise" );
      theChangePackaged = input.getString( "theChangePackaged" );
   }

   boolean readVersion_721830272130892700L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      description = (java.lang.String) input.readObject();
      implementadvise = (java.lang.String) input.readObject();
      theChangePackaged = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( ChangePackagedResultLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_721830272130892700L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ChangePackagedResultLink( _ChangePackagedResultLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
