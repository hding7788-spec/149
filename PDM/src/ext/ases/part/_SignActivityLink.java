package ext.ases.part;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _SignActivityLink extends wt.fc.ObjectToObjectLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.ases.part.partResource";
   static final String CLASSNAME = SignActivityLink.class.getName();

   /**
    * @see ext.ases.part.SignActivityLink
    */
   public static final String SIGNATURE_ROLE = "theSignature";
   /**
    * @see ext.ases.part.SignActivityLink
    */
   public ext.ases.part.ASESHuiqianSignature getSignature() {
      return (ext.ases.part.ASESHuiqianSignature) getRoleBObject();
   }
   /**
    * @see ext.ases.part.SignActivityLink
    */
   public void setSignature(ext.ases.part.ASESHuiqianSignature the_theSignature) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_theSignature);
   }

   /**
    * @see ext.ases.part.SignActivityLink
    */
   public static final String ACTIVITY_ROLE = "theActivity";
   /**
    * @see ext.ases.part.SignActivityLink
    */
   public wt.workflow.engine.WfActivity getActivity() {
      return (wt.workflow.engine.WfActivity) getRoleAObject();
   }
   /**
    * @see ext.ases.part.SignActivityLink
    */
   public void setActivity(wt.workflow.engine.WfActivity the_theActivity) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_theActivity);
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

   protected void super_writeExternal_SignActivityLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.ases.part.SignActivityLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_SignActivityLink(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
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

   protected boolean readVersion( SignActivityLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion2538346186404157511L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_SignActivityLink( _SignActivityLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
