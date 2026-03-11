package ext.ases.changerequest;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ChangeRequestAffectLink extends wt.vc.ObjectToVersionLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.ases.changerequest.changerequestResource";
   static final String CLASSNAME = ChangeRequestAffectLink.class.getName();

   /**
    * @see ext.ases.changerequest.ChangeRequestAffectLink
    */
   public static final String IMPLEMENT = "implement";
   static int IMPLEMENT_UPPER_LIMIT = -1;
   java.lang.String implement;
   /**
    * @see ext.ases.changerequest.ChangeRequestAffectLink
    */
   public java.lang.String getImplement() {
      return implement;
   }
   /**
    * @see ext.ases.changerequest.ChangeRequestAffectLink
    */
   public void setImplement(java.lang.String implement) throws wt.util.WTPropertyVetoException {
      implementValidate(implement);
      this.implement = implement;
   }
   void implementValidate(java.lang.String implement) throws wt.util.WTPropertyVetoException {
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
    * @see ext.ases.changerequest.ChangeRequestAffectLink
    */
   public static final String CHANGE_REQUEST_ROLE = "theChangeRequest";
   /**
    * @see ext.ases.changerequest.ChangeRequestAffectLink
    */
   public ext.ases.changerequest.ChangeRequest getChangeRequest() {
      return (ext.ases.changerequest.ChangeRequest) getRoleAObject();
   }
   /**
    * @see ext.ases.changerequest.ChangeRequestAffectLink
    */
   public void setChangeRequest(ext.ases.changerequest.ChangeRequest the_theChangeRequest) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_theChangeRequest);
   }

   /**
    * @see ext.ases.changerequest.ChangeRequestAffectLink
    */
   public static final String REVISION_CONTROLLED_ROLE = "theRevisionControlled";
   /**
    * @see ext.ases.changerequest.ChangeRequestAffectLink
    */
   public wt.enterprise.RevisionControlled getRevisionControlled() {
      return (wt.enterprise.RevisionControlled) getRoleBObject();
   }
   /**
    * @see ext.ases.changerequest.ChangeRequestAffectLink
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

   public static final long EXTERNALIZATION_VERSION_UID = -7795867599139306996L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( implement );
   }

   protected void super_writeExternal_ChangeRequestAffectLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.ases.changerequest.ChangeRequestAffectLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ChangeRequestAffectLink(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "implement", implement );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      implement = input.getString( "implement" );
   }

   boolean readVersion_7795867599139306996L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      implement = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( ChangeRequestAffectLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_7795867599139306996L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ChangeRequestAffectLink( _ChangeRequestAffectLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
