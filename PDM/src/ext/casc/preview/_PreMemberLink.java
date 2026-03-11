package ext.casc.preview;

@SuppressWarnings({"cast", "deprecation", "rawtypes", "unchecked"})
public abstract class _PreMemberLink extends wt.fc.ObjectToObjectLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.casc.preview.previewResource";
   static final String CLASSNAME = PreMemberLink.class.getName();

   /**
    * @see PreMemberLink
    */
   public static final String DESCRIPTION = "description";
   static int DESCRIPTION_UPPER_LIMIT = -1;
   String description;
   /**
    * @see PreMemberLink
    */
   public String getDescription() {
      return description;
   }
   /**
    * @see PreMemberLink
    */
   public void setDescription(String description) throws wt.util.WTPropertyVetoException {
      descriptionValidate(description);
      this.description = description;
   }
   void descriptionValidate(String description) throws wt.util.WTPropertyVetoException {
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
    * @see PreMemberLink
    */
   public static final String IMPLEMENT = "implement";
   static int IMPLEMENT_UPPER_LIMIT = -1;
   String implement;
   /**
    * @see PreMemberLink
    */
   public String getImplement() {
      return implement;
   }
   /**
    * @see PreMemberLink
    */
   public void setImplement(String implement) throws wt.util.WTPropertyVetoException {
      implementValidate(implement);
      this.implement = implement;
   }
   void implementValidate(String implement) throws wt.util.WTPropertyVetoException {
      if (IMPLEMENT_UPPER_LIMIT < 1) {
         try { IMPLEMENT_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("implement").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { IMPLEMENT_UPPER_LIMIT = 200; }
      }
      if (implement != null && !wt.fc.PersistenceHelper.checkStoredLength(implement.toString(), IMPLEMENT_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "implement"), String.valueOf(Math.min(IMPLEMENT_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "implement", this.implement, implement));
   }

   /**
    * @see PreMemberLink
    */
   public static final String PREVIEW_ROLE = "thePreview";
   /**
    * @see PreMemberLink
    */
   public Preview getPreview() {
      return (Preview) getRoleAObject();
   }
   /**
    * @see PreMemberLink
    */
   public void setPreview(Preview the_thePreview) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_thePreview);
   }

   /**
    * @see PreMemberLink
    */
   public static final String PREVIEW_OBJ_ROLE = "thePreviewObj";
   /**
    * @see PreMemberLink
    */
   public PreviewObject getPreviewObj() {
      return (PreviewObject) getRoleBObject();
   }
   /**
    * @see PreMemberLink
    */
   public void setPreviewObj(PreviewObject the_thePreviewObj) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_thePreviewObj);
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

   public static final long EXTERNALIZATION_VERSION_UID = 9040886708316934281L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( description );
      output.writeObject( implement );
   }

   protected void super_writeExternal_PreMemberLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (PreMemberLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_PreMemberLink(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "description", description );
      output.setString( "implement", implement );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      description = input.getString( "description" );
      implement = input.getString( "implement" );
   }

   boolean readVersion9040886708316934281L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      description = (String) input.readObject();
      implement = (String) input.readObject();
      return true;
   }

   protected boolean readVersion( PreMemberLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion9040886708316934281L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_PreMemberLink( _PreMemberLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
