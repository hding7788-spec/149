package ext.ases.envelope;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _EnvelopeTopObjLink extends wt.fc.ObjectToObjectLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.ases.envelope.envelopeResource";
   static final String CLASSNAME = EnvelopeTopObjLink.class.getName();

   /**
    * @see ext.ases.envelope.EnvelopeTopObjLink
    */
   public static final String TOP_OBJECT_ROLE = "topObject";
   /**
    * @see ext.ases.envelope.EnvelopeTopObjLink
    */
   public wt.enterprise.RevisionControlled getTopObject() {
      return (wt.enterprise.RevisionControlled) getRoleBObject();
   }
   /**
    * @see ext.ases.envelope.EnvelopeTopObjLink
    */
   public void setTopObject(wt.enterprise.RevisionControlled the_topObject) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_topObject);
   }

   /**
    * @see ext.ases.envelope.EnvelopeTopObjLink
    */
   public static final String TOP_FOR_ENVELOPE_ROLE = "topForEnvelope";
   /**
    * @see ext.ases.envelope.EnvelopeTopObjLink
    */
   public ext.ases.envelope.ProcessEnvelope getTopForEnvelope() {
      return (ext.ases.envelope.ProcessEnvelope) getRoleAObject();
   }
   /**
    * @see ext.ases.envelope.EnvelopeTopObjLink
    */
   public void setTopForEnvelope(ext.ases.envelope.ProcessEnvelope the_topForEnvelope) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_topForEnvelope);
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

   public static final long EXTERNALIZATION_VERSION_UID = 2538346186404157511L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

   }

   protected void super_writeExternal_EnvelopeTopObjLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.ases.envelope.EnvelopeTopObjLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_EnvelopeTopObjLink(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

   }

   boolean readVersion2538346186404157511L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      return true;
   }

   protected boolean readVersion( EnvelopeTopObjLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion2538346186404157511L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_EnvelopeTopObjLink( _EnvelopeTopObjLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
