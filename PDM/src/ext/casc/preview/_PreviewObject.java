package ext.casc.preview;

@SuppressWarnings({"cast", "deprecation", "rawtypes", "unchecked"})
public abstract class _PreviewObject extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.casc.preview.previewResource";
   static final String CLASSNAME = PreviewObject.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public static final String NAME = "name";
   static int NAME_UPPER_LIMIT = -1;
   String name;
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public String getName() {
      return name;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public void setName(String name) throws wt.util.WTPropertyVetoException {
      nameValidate(name);
      this.name = name;
   }
   void nameValidate(String name) throws wt.util.WTPropertyVetoException {
      if (name == null || name.trim().length() == 0)
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "name") },
               new java.beans.PropertyChangeEvent(this, "name", this.name, name));
      if (NAME_UPPER_LIMIT < 1) {
         try { NAME_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("name").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { NAME_UPPER_LIMIT = 200; }
      }
      if (name != null && !wt.fc.PersistenceHelper.checkStoredLength(name.toString(), NAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "name"), String.valueOf(Math.min(NAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "name", this.name, name));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public static final String NUMBER = "number";
   static int NUMBER_UPPER_LIMIT = -1;
   String number;
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public String getNumber() {
      return number;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public void setNumber(String number) throws wt.util.WTPropertyVetoException {
      numberValidate(number);
      this.number = number;
   }
   void numberValidate(String number) throws wt.util.WTPropertyVetoException {
      if (number == null || number.trim().length() == 0)
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "number") },
               new java.beans.PropertyChangeEvent(this, "number", this.number, number));
      if (NUMBER_UPPER_LIMIT < 1) {
         try { NUMBER_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("number").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { NUMBER_UPPER_LIMIT = 200; }
      }
      if (number != null && !wt.fc.PersistenceHelper.checkStoredLength(number.toString(), NUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "number"), String.valueOf(Math.min(NUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "number", this.number, number));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public static final String OBJ_TYPE = "objType";
   static int OBJ_TYPE_UPPER_LIMIT = -1;
   String objType;
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public String getObjType() {
      return objType;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public void setObjType(String objType) throws wt.util.WTPropertyVetoException {
      objTypeValidate(objType);
      this.objType = objType;
   }
   void objTypeValidate(String objType) throws wt.util.WTPropertyVetoException {
      if (OBJ_TYPE_UPPER_LIMIT < 1) {
         try { OBJ_TYPE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("objType").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OBJ_TYPE_UPPER_LIMIT = 200; }
      }
      if (objType != null && !wt.fc.PersistenceHelper.checkStoredLength(objType.toString(), OBJ_TYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "objType"), String.valueOf(Math.min(OBJ_TYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "objType", this.objType, objType));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public static final String OBJ_VER = "objVer";
   static int OBJ_VER_UPPER_LIMIT = -1;
   String objVer;
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public String getObjVer() {
      return objVer;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public void setObjVer(String objVer) throws wt.util.WTPropertyVetoException {
      objVerValidate(objVer);
      this.objVer = objVer;
   }
   void objVerValidate(String objVer) throws wt.util.WTPropertyVetoException {
      if (OBJ_VER_UPPER_LIMIT < 1) {
         try { OBJ_VER_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("objVer").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OBJ_VER_UPPER_LIMIT = 200; }
      }
      if (objVer != null && !wt.fc.PersistenceHelper.checkStoredLength(objVer.toString(), OBJ_VER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "objVer"), String.valueOf(Math.min(OBJ_VER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "objVer", this.objVer, objVer));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public static final String MODEL_MATURITY = "modelMaturity";
   static int MODEL_MATURITY_UPPER_LIMIT = -1;
   String modelMaturity;
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public String getModelMaturity() {
      return modelMaturity;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public void setModelMaturity(String modelMaturity) throws wt.util.WTPropertyVetoException {
      modelMaturityValidate(modelMaturity);
      this.modelMaturity = modelMaturity;
   }
   void modelMaturityValidate(String modelMaturity) throws wt.util.WTPropertyVetoException {
      if (MODEL_MATURITY_UPPER_LIMIT < 1) {
         try { MODEL_MATURITY_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("modelMaturity").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { MODEL_MATURITY_UPPER_LIMIT = 200; }
      }
      if (modelMaturity != null && !wt.fc.PersistenceHelper.checkStoredLength(modelMaturity.toString(), MODEL_MATURITY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "modelMaturity"), String.valueOf(Math.min(MODEL_MATURITY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "modelMaturity", this.modelMaturity, modelMaturity));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public static final String MATURITY_REASON = "maturityReason";
   static int MATURITY_REASON_UPPER_LIMIT = -1;
   String maturityReason;
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public String getMaturityReason() {
      return maturityReason;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public void setMaturityReason(String maturityReason) throws wt.util.WTPropertyVetoException {
      maturityReasonValidate(maturityReason);
      this.maturityReason = maturityReason;
   }
   void maturityReasonValidate(String maturityReason) throws wt.util.WTPropertyVetoException {
      if (MATURITY_REASON_UPPER_LIMIT < 1) {
         try { MATURITY_REASON_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("maturityReason").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { MATURITY_REASON_UPPER_LIMIT = 200; }
      }
      if (maturityReason != null && !wt.fc.PersistenceHelper.checkStoredLength(maturityReason.toString(), MATURITY_REASON_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "maturityReason"), String.valueOf(Math.min(MATURITY_REASON_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "maturityReason", this.maturityReason, maturityReason));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public static final String DESCRIPTION = "description";
   static int DESCRIPTION_UPPER_LIMIT = -1;
   String description;
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public String getDescription() {
      return description;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
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
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public static final String DESIGNER = "designer";
   static int DESIGNER_UPPER_LIMIT = -1;
   String designer;
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public String getDesigner() {
      return designer;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public void setDesigner(String designer) throws wt.util.WTPropertyVetoException {
      designerValidate(designer);
      this.designer = designer;
   }
   void designerValidate(String designer) throws wt.util.WTPropertyVetoException {
      if (DESIGNER_UPPER_LIMIT < 1) {
         try { DESIGNER_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("designer").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DESIGNER_UPPER_LIMIT = 200; }
      }
      if (designer != null && !wt.fc.PersistenceHelper.checkStoredLength(designer.toString(), DESIGNER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "designer"), String.valueOf(Math.min(DESIGNER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "designer", this.designer, designer));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public static final String DESIGN_COMPANY = "designCompany";
   static int DESIGN_COMPANY_UPPER_LIMIT = -1;
   String designCompany;
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public String getDesignCompany() {
      return designCompany;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see PreviewObject
    */
   public void setDesignCompany(String designCompany) throws wt.util.WTPropertyVetoException {
      designCompanyValidate(designCompany);
      this.designCompany = designCompany;
   }
   void designCompanyValidate(String designCompany) throws wt.util.WTPropertyVetoException {
      if (DESIGN_COMPANY_UPPER_LIMIT < 1) {
         try { DESIGN_COMPANY_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("designCompany").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DESIGN_COMPANY_UPPER_LIMIT = 200; }
      }
      if (designCompany != null && !wt.fc.PersistenceHelper.checkStoredLength(designCompany.toString(), DESIGN_COMPANY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "designCompany"), String.valueOf(Math.min(DESIGN_COMPANY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "designCompany", this.designCompany, designCompany));
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

   public static final long EXTERNALIZATION_VERSION_UID = -5830389856508731555L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( description );
      output.writeObject( designCompany );
      output.writeObject( designer );
      output.writeObject( maturityReason );
      output.writeObject( modelMaturity );
      output.writeObject( name );
      output.writeObject( number );
      output.writeObject( objType );
      output.writeObject( objVer );
   }

   protected void super_writeExternal_PreviewObject(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (PreviewObject) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_PreviewObject(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "description", description );
      output.setString( "designCompany", designCompany );
      output.setString( "designer", designer );
      output.setString( "maturityReason", maturityReason );
      output.setString( "modelMaturity", modelMaturity );
      output.setString( "name", name );
      output.setString( "number", number );
      output.setString( "objType", objType );
      output.setString( "objVer", objVer );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      description = input.getString( "description" );
      designCompany = input.getString( "designCompany" );
      designer = input.getString( "designer" );
      maturityReason = input.getString( "maturityReason" );
      modelMaturity = input.getString( "modelMaturity" );
      name = input.getString( "name" );
      number = input.getString( "number" );
      objType = input.getString( "objType" );
      objVer = input.getString( "objVer" );
   }

   boolean readVersion_5830389856508731555L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      description = (String) input.readObject();
      designCompany = (String) input.readObject();
      designer = (String) input.readObject();
      maturityReason = (String) input.readObject();
      modelMaturity = (String) input.readObject();
      name = (String) input.readObject();
      number = (String) input.readObject();
      objType = (String) input.readObject();
      objVer = (String) input.readObject();
      return true;
   }

   protected boolean readVersion( PreviewObject thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_5830389856508731555L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_PreviewObject( _PreviewObject thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
