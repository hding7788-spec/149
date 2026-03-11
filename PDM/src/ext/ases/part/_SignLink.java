package ext.ases.part;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _SignLink extends wt.fc.ObjectToObjectLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.ases.part.partResource";
   static final String CLASSNAME = SignLink.class.getName();

   /**
    * @see ext.ases.part.SignLink
    */
   public static final String SIGN_OBJECT_ROLE = "signObject";
   /**
    * @see ext.ases.part.SignLink
    */
   public wt.fc.WTObject getSignObject() {
      return (wt.fc.WTObject) getRoleAObject();
   }
   /**
    * @see ext.ases.part.SignLink
    */
   public void setSignObject(wt.fc.WTObject the_signObject) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_signObject);
   }

   /**
    * @see ext.ases.part.SignLink
    */
   public static final String SIGNATURE_ROLE = "theSignature";
   /**
    * @see ext.ases.part.SignLink
    */
   public ext.ases.part.ASESHuiqianSignature getSignature() {
      return (ext.ases.part.ASESHuiqianSignature) getRoleBObject();
   }
   /**
    * @see ext.ases.part.SignLink
    */
   public void setSignature(ext.ases.part.ASESHuiqianSignature the_theSignature) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_theSignature);
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

   protected void super_writeExternal_SignLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.ases.part.SignLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_SignLink(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
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

   protected boolean readVersion( SignLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion2538346186404157511L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_SignLink( _SignLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
