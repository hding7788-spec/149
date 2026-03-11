package ext.sast.catalog;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _GLCatalog extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.sast.catalog.catalogResource";
   static final String CLASSNAME = GLCatalog.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String BZH = "bzh";
   static int BZH_UPPER_LIMIT = -1;
   java.lang.String bzh;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getBzh() {
      return bzh;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setBzh(java.lang.String bzh) throws wt.util.WTPropertyVetoException {
      bzhValidate(bzh);
      this.bzh = (bzh != null) ? bzh.toUpperCase() : null;
   }
   void bzhValidate(java.lang.String bzh) throws wt.util.WTPropertyVetoException {
      if (bzh == null || bzh.trim().length() == 0)
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "bzh") },
               new java.beans.PropertyChangeEvent(this, "bzh", this.bzh, bzh));
      if (BZH_UPPER_LIMIT < 1) {
         try { BZH_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("bzh").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { BZH_UPPER_LIMIT = 200; }
      }
      if (bzh != null && !wt.fc.PersistenceHelper.checkStoredLength(bzh.toString(), BZH_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "bzh"), String.valueOf(Math.min(BZH_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "bzh", this.bzh, bzh));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String CATALOGNAME = "catalogname";
   static int CATALOGNAME_UPPER_LIMIT = -1;
   java.lang.String catalogname;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getCatalogname() {
      return catalogname;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setCatalogname(java.lang.String catalogname) throws wt.util.WTPropertyVetoException {
      catalognameValidate(catalogname);
      this.catalogname = catalogname;
   }
   void catalognameValidate(java.lang.String catalogname) throws wt.util.WTPropertyVetoException {
      if (catalogname == null || catalogname.trim().length() == 0)
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "catalogname") },
               new java.beans.PropertyChangeEvent(this, "catalogname", this.catalogname, catalogname));
      if (CATALOGNAME_UPPER_LIMIT < 1) {
         try { CATALOGNAME_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("catalogname").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CATALOGNAME_UPPER_LIMIT = 200; }
      }
      if (catalogname != null && !wt.fc.PersistenceHelper.checkStoredLength(catalogname.toString(), CATALOGNAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "catalogname"), String.valueOf(Math.min(CATALOGNAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "catalogname", this.catalogname, catalogname));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String CATALOGTYPE = "catalogtype";
   static int CATALOGTYPE_UPPER_LIMIT = -1;
   java.lang.String catalogtype;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getCatalogtype() {
      return catalogtype;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setCatalogtype(java.lang.String catalogtype) throws wt.util.WTPropertyVetoException {
      catalogtypeValidate(catalogtype);
      this.catalogtype = catalogtype;
   }
   void catalogtypeValidate(java.lang.String catalogtype) throws wt.util.WTPropertyVetoException {
      if (CATALOGTYPE_UPPER_LIMIT < 1) {
         try { CATALOGTYPE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("catalogtype").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CATALOGTYPE_UPPER_LIMIT = 200; }
      }
      if (catalogtype != null && !wt.fc.PersistenceHelper.checkStoredLength(catalogtype.toString(), CATALOGTYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "catalogtype"), String.valueOf(Math.min(CATALOGTYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "catalogtype", this.catalogtype, catalogtype));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String GRADE = "grade";
   static int GRADE_UPPER_LIMIT = -1;
   java.lang.String grade;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getGrade() {
      return grade;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setGrade(java.lang.String grade) throws wt.util.WTPropertyVetoException {
      gradeValidate(grade);
      this.grade = grade;
   }
   void gradeValidate(java.lang.String grade) throws wt.util.WTPropertyVetoException {
      if (GRADE_UPPER_LIMIT < 1) {
         try { GRADE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("grade").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { GRADE_UPPER_LIMIT = 200; }
      }
      if (grade != null && !wt.fc.PersistenceHelper.checkStoredLength(grade.toString(), GRADE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "grade"), String.valueOf(Math.min(GRADE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "grade", this.grade, grade));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String SCOPE = "scope";
   static int SCOPE_UPPER_LIMIT = -1;
   java.lang.String scope;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getScope() {
      return scope;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setScope(java.lang.String scope) throws wt.util.WTPropertyVetoException {
      scopeValidate(scope);
      this.scope = scope;
   }
   void scopeValidate(java.lang.String scope) throws wt.util.WTPropertyVetoException {
      if (SCOPE_UPPER_LIMIT < 1) {
         try { SCOPE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("scope").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { SCOPE_UPPER_LIMIT = 200; }
      }
      if (scope != null && !wt.fc.PersistenceHelper.checkStoredLength(scope.toString(), SCOPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "scope"), String.valueOf(Math.min(SCOPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "scope", this.scope, scope));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String STATE = "state";
   static int STATE_UPPER_LIMIT = -1;
   java.lang.String state;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getState() {
      return state;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setState(java.lang.String state) throws wt.util.WTPropertyVetoException {
      stateValidate(state);
      this.state = state;
   }
   void stateValidate(java.lang.String state) throws wt.util.WTPropertyVetoException {
      if (STATE_UPPER_LIMIT < 1) {
         try { STATE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("state").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STATE_UPPER_LIMIT = 200; }
      }
      if (state != null && !wt.fc.PersistenceHelper.checkStoredLength(state.toString(), STATE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "state"), String.valueOf(Math.min(STATE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "state", this.state, state));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String CREATEUNIT = "createunit";
   static int CREATEUNIT_UPPER_LIMIT = -1;
   java.lang.String createunit;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getCreateunit() {
      return createunit;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setCreateunit(java.lang.String createunit) throws wt.util.WTPropertyVetoException {
      createunitValidate(createunit);
      this.createunit = createunit;
   }
   void createunitValidate(java.lang.String createunit) throws wt.util.WTPropertyVetoException {
      if (CREATEUNIT_UPPER_LIMIT < 1) {
         try { CREATEUNIT_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("createunit").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CREATEUNIT_UPPER_LIMIT = 200; }
      }
      if (createunit != null && !wt.fc.PersistenceHelper.checkStoredLength(createunit.toString(), CREATEUNIT_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "createunit"), String.valueOf(Math.min(CREATEUNIT_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "createunit", this.createunit, createunit));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String CREATOR = "creator";
   static int CREATOR_UPPER_LIMIT = -1;
   java.lang.String creator;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getCreator() {
      return creator;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setCreator(java.lang.String creator) throws wt.util.WTPropertyVetoException {
      creatorValidate(creator);
      this.creator = creator;
   }
   void creatorValidate(java.lang.String creator) throws wt.util.WTPropertyVetoException {
      if (CREATOR_UPPER_LIMIT < 1) {
         try { CREATOR_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("creator").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CREATOR_UPPER_LIMIT = 200; }
      }
      if (creator != null && !wt.fc.PersistenceHelper.checkStoredLength(creator.toString(), CREATOR_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "creator"), String.valueOf(Math.min(CREATOR_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "creator", this.creator, creator));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String MODIFIER = "modifier";
   static int MODIFIER_UPPER_LIMIT = -1;
   java.lang.String modifier;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getModifier() {
      return modifier;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setModifier(java.lang.String modifier) throws wt.util.WTPropertyVetoException {
      modifierValidate(modifier);
      this.modifier = modifier;
   }
   void modifierValidate(java.lang.String modifier) throws wt.util.WTPropertyVetoException {
      if (MODIFIER_UPPER_LIMIT < 1) {
         try { MODIFIER_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("modifier").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { MODIFIER_UPPER_LIMIT = 200; }
      }
      if (modifier != null && !wt.fc.PersistenceHelper.checkStoredLength(modifier.toString(), MODIFIER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "modifier"), String.valueOf(Math.min(MODIFIER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "modifier", this.modifier, modifier));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String CATALOGLINKTYPE = "cataloglinktype";
   static int CATALOGLINKTYPE_UPPER_LIMIT = -1;
   java.lang.String cataloglinktype;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getCataloglinktype() {
      return cataloglinktype;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setCataloglinktype(java.lang.String cataloglinktype) throws wt.util.WTPropertyVetoException {
      cataloglinktypeValidate(cataloglinktype);
      this.cataloglinktype = cataloglinktype;
   }
   void cataloglinktypeValidate(java.lang.String cataloglinktype) throws wt.util.WTPropertyVetoException {
      if (CATALOGLINKTYPE_UPPER_LIMIT < 1) {
         try { CATALOGLINKTYPE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("cataloglinktype").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CATALOGLINKTYPE_UPPER_LIMIT = 200; }
      }
      if (cataloglinktype != null && !wt.fc.PersistenceHelper.checkStoredLength(cataloglinktype.toString(), CATALOGLINKTYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "cataloglinktype"), String.valueOf(Math.min(CATALOGLINKTYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "cataloglinktype", this.cataloglinktype, cataloglinktype));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public static final String REMARK = "remark";
   static int REMARK_UPPER_LIMIT = -1;
   java.lang.String remark;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public java.lang.String getRemark() {
      return remark;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.sast.catalog.GLCatalog
    */
   public void setRemark(java.lang.String remark) throws wt.util.WTPropertyVetoException {
      remarkValidate(remark);
      this.remark = remark;
   }
   void remarkValidate(java.lang.String remark) throws wt.util.WTPropertyVetoException {
      if (REMARK_UPPER_LIMIT < 1) {
         try { REMARK_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("remark").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { REMARK_UPPER_LIMIT = 200; }
      }
      if (remark != null && !wt.fc.PersistenceHelper.checkStoredLength(remark.toString(), REMARK_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "remark"), String.valueOf(Math.min(REMARK_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "remark", this.remark, remark));
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

   public static final long EXTERNALIZATION_VERSION_UID = -8582451763344550690L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( bzh );
      output.writeObject( cataloglinktype );
      output.writeObject( catalogname );
      output.writeObject( catalogtype );
      output.writeObject( createunit );
      output.writeObject( creator );
      output.writeObject( grade );
      output.writeObject( modifier );
      output.writeObject( remark );
      output.writeObject( scope );
      output.writeObject( state );
   }

   protected void super_writeExternal_GLCatalog(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.sast.catalog.GLCatalog) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_GLCatalog(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "bzh", bzh );
      output.setString( "cataloglinktype", cataloglinktype );
      output.setString( "catalogname", catalogname );
      output.setString( "catalogtype", catalogtype );
      output.setString( "createunit", createunit );
      output.setString( "creator", creator );
      output.setString( "grade", grade );
      output.setString( "modifier", modifier );
      output.setString( "remark", remark );
      output.setString( "scope", scope );
      output.setString( "state", state );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      bzh = input.getString( "bzh" );
      cataloglinktype = input.getString( "cataloglinktype" );
      catalogname = input.getString( "catalogname" );
      catalogtype = input.getString( "catalogtype" );
      createunit = input.getString( "createunit" );
      creator = input.getString( "creator" );
      grade = input.getString( "grade" );
      modifier = input.getString( "modifier" );
      remark = input.getString( "remark" );
      scope = input.getString( "scope" );
      state = input.getString( "state" );
   }

   boolean readVersion_8582451763344550690L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      bzh = (java.lang.String) input.readObject();
      cataloglinktype = (java.lang.String) input.readObject();
      catalogname = (java.lang.String) input.readObject();
      catalogtype = (java.lang.String) input.readObject();
      createunit = (java.lang.String) input.readObject();
      creator = (java.lang.String) input.readObject();
      grade = (java.lang.String) input.readObject();
      modifier = (java.lang.String) input.readObject();
      remark = (java.lang.String) input.readObject();
      scope = (java.lang.String) input.readObject();
      state = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( GLCatalog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_8582451763344550690L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_GLCatalog( _GLCatalog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
