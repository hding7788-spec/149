package ext.casc.process;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ProcessTaskLink extends wt.fc.ObjectToObjectLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.casc.process.processResource";
   static final String CLASSNAME = ProcessTaskLink.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskLink
    */
   public static final String LINKED_WTOBJECT_ROLE = "linkedWTObject";
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskLink
    */
   public wt.fc.WTObject getLinkedWTObject() {
      return (wt.fc.WTObject) getRoleAObject();
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskLink
    */
   public void setLinkedWTObject(wt.fc.WTObject the_linkedWTObject) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_linkedWTObject);
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskLink
    */
   public static final String LINKED_PROCESS_TASK_ROLE = "linkedProcessTask";
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskLink
    */
   public ext.casc.process.ProcessTask getLinkedProcessTask() {
      return (ext.casc.process.ProcessTask) getRoleBObject();
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskLink
    */
   public void setLinkedProcessTask(ext.casc.process.ProcessTask the_linkedProcessTask) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_linkedProcessTask);
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

   protected void super_writeExternal_ProcessTaskLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.casc.process.ProcessTaskLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ProcessTaskLink(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
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

   protected boolean readVersion( ProcessTaskLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion2538346186404157511L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ProcessTaskLink( _ProcessTaskLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
