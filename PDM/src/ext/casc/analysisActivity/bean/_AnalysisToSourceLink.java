package ext.casc.analysisActivity.bean;

@SuppressWarnings({"cast", "deprecation", "rawtypes", "unchecked"})
public abstract class _AnalysisToSourceLink extends wt.fc.ObjectToObjectLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.casc.analysisActivity.bean.beanResource";
   static final String CLASSNAME = AnalysisToSourceLink.class.getName();

   /**
    * @see AnalysisToSourceLink
    */
   public static final String PL_NUMBER = "plNumber";
   static int PL_NUMBER_UPPER_LIMIT = -1;
   String plNumber;
   /**
    * @see AnalysisToSourceLink
    */
   public String getPlNumber() {
      return plNumber;
   }
   /**
    * @see AnalysisToSourceLink
    */
   public void setPlNumber(String plNumber) throws wt.util.WTPropertyVetoException {
      plNumberValidate(plNumber);
      this.plNumber = plNumber;
   }
   void plNumberValidate(String plNumber) throws wt.util.WTPropertyVetoException {
      if (PL_NUMBER_UPPER_LIMIT < 1) {
         try { PL_NUMBER_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("plNumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { PL_NUMBER_UPPER_LIMIT = 200; }
      }
      if (plNumber != null && !wt.fc.PersistenceHelper.checkStoredLength(plNumber.toString(), PL_NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "plNumber"), String.valueOf(Math.min(PL_NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "plNumber", this.plNumber, plNumber));
   }

   /**
    * @see AnalysisToSourceLink
    */
   public static final String ANALYSIS_ACTIVITY_ROLE = "analysisActivity";
   /**
    * @see AnalysisToSourceLink
    */
   public wt.change2.WTAnalysisActivity getAnalysisActivity() {
      return (wt.change2.WTAnalysisActivity) getRoleAObject();
   }
   /**
    * @see AnalysisToSourceLink
    */
   public void setAnalysisActivity(wt.change2.WTAnalysisActivity the_analysisActivity) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_analysisActivity);
   }

   /**
    * @see AnalysisToSourceLink
    */
   public static final String SOURCE_OBJECT_ROLE = "sourceObject";
   /**
    * @see AnalysisToSourceLink
    */
   public wt.fc.WTObject getSourceObject() {
      return (wt.fc.WTObject) getRoleBObject();
   }
   /**
    * @see AnalysisToSourceLink
    */
   public void setSourceObject(wt.fc.WTObject the_sourceObject) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_sourceObject);
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

   public static final long EXTERNALIZATION_VERSION_UID = 6516805456387681224L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( plNumber );
   }

   protected void super_writeExternal_AnalysisToSourceLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (AnalysisToSourceLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_AnalysisToSourceLink(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "plNumber", plNumber );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      plNumber = input.getString( "plNumber" );
   }

   boolean readVersion6516805456387681224L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      plNumber = (String) input.readObject();
      return true;
   }

   protected boolean readVersion( AnalysisToSourceLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion6516805456387681224L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_AnalysisToSourceLink( _AnalysisToSourceLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
