package ext.casc.preview;

@SuppressWarnings({"cast", "deprecation", "rawtypes", "unchecked"})
public abstract class _Preview extends wt.enterprise.Managed implements wt.inf.container.WTContainedIdentified, wt.type.Typed, wt.org.OrganizationOwnedImpl, wt.content.FormatContentHolder, wt.access.IdentityAccessControlled, wt.recent.RecentlyVisited, java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.casc.preview.previewResource";
   static final String CLASSNAME = Preview.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
    */
   public static final String NAME = "name";
   static int NAME_UPPER_LIMIT = -1;
   String name;
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
    */
   public String getName() {
      return name;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
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
    * @see Preview
    */
   public static final String NUMBER = "number";
   static int NUMBER_UPPER_LIMIT = -1;
   String number;
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
    */
   public String getNumber() {
      return number;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
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
    * @see Preview
    */
   public static final String DESCRIPTION = "description";
   static int DESCRIPTION_UPPER_LIMIT = -1;
   String description;
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
    */
   public String getDescription() {
      return description;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
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
    * @see Preview
    */
   public static final String DESIGNER = "designer";
   static int DESIGNER_UPPER_LIMIT = -1;
   String designer;
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
    */
   public String getDesigner() {
      return designer;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
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
    * @see Preview
    */
   public static final String DESIGN_COMPANY = "designCompany";
   static int DESIGN_COMPANY_UPPER_LIMIT = -1;
   String designCompany;
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
    */
   public String getDesignCompany() {
      return designCompany;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see Preview
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

   wt.inf.container.WTContainerRef containerReference;
   /**
    * @see wt.inf.container.WTContainedIdentified
    * @see wt.inf.container.WTContained
    */
   public wt.inf.container.WTContainer getContainer() {
      return (containerReference != null) ? (wt.inf.container.WTContainer) containerReference.getObject() : null;
   }
   /**
    * @see wt.inf.container.WTContainedIdentified
    * @see wt.inf.container.WTContained
    */
   public wt.inf.container.WTContainerRef getContainerReference() {
      return containerReference;
   }
   /**
    * @see wt.inf.container.WTContainedIdentified
    * @see wt.inf.container.WTContained
    */
   public void setContainer(wt.inf.container.WTContainer the_container) throws wt.util.WTPropertyVetoException, wt.util.WTException {
      setContainerReference(the_container == null ? null : wt.inf.container.WTContainerRef.newWTContainerRef((wt.inf.container.WTContainer) the_container));
   }
   /**
    * @see wt.inf.container.WTContainedIdentified
    * @see wt.inf.container.WTContained
    */
   public void setContainerReference(wt.inf.container.WTContainerRef the_containerReference) throws wt.util.WTPropertyVetoException {
      containerReferenceValidate(the_containerReference);
      containerReference = (wt.inf.container.WTContainerRef) the_containerReference;
   }
   void containerReferenceValidate(wt.inf.container.WTContainerRef the_containerReference) throws wt.util.WTPropertyVetoException {
      if (!wt.fc.IdentityHelper.isChangeable(this))
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.CHANGE_RESTRICTION,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "containerReference") },
               new java.beans.PropertyChangeEvent(this, "containerReference", this.containerReference, containerReference));
      if (the_containerReference == null || the_containerReference.getReferencedClass() == null)
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "containerReference") },
               new java.beans.PropertyChangeEvent(this, "containerReference", this.containerReference, containerReference));
      if (the_containerReference != null && the_containerReference.getReferencedClass() != null &&
            !wt.inf.container.WTContainer.class.isAssignableFrom(the_containerReference.getReferencedClass()))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.WRONG_TYPE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "containerReference"), "WTContainerRef" },
               new java.beans.PropertyChangeEvent(this, "containerReference", this.containerReference, containerReference));
   }

   /**
    * Derived from {@link wt.inf.container.WTContainerRef#getName()}
    *
    * @see wt.inf.container.WTContained
    */
   public String getContainerName() {
      try { return (String) ((wt.inf.container.WTContainerRef) getContainerReference()).getName(); }
      catch (NullPointerException npe) { return null; }
   }

   wt.type.TypeDefinitionReference typeDefinitionReference;
   /**
    * @see wt.type.Typed
    */
   public wt.type.TypeDefinitionReference getTypeDefinitionReference() {
      return typeDefinitionReference;
   }
   /**
    * @see wt.type.Typed
    */
   public void setTypeDefinitionReference(wt.type.TypeDefinitionReference typeDefinitionReference) throws wt.util.WTPropertyVetoException {
      typeDefinitionReferenceValidate(typeDefinitionReference);
      this.typeDefinitionReference = typeDefinitionReference;
   }
   void typeDefinitionReferenceValidate(wt.type.TypeDefinitionReference typeDefinitionReference) throws wt.util.WTPropertyVetoException {
      if (typeDefinitionReference == null)
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "typeDefinitionReference") },
               new java.beans.PropertyChangeEvent(this, "typeDefinitionReference", this.typeDefinitionReference, typeDefinitionReference));
   }

   wt.iba.value.AttributeContainer theAttributeContainer;
   /**
    * @see wt.iba.value.IBAHolder
    */
   public wt.iba.value.AttributeContainer getAttributeContainer() {
      return theAttributeContainer;
   }
   /**
    * @see wt.iba.value.IBAHolder
    */
   public void setAttributeContainer(wt.iba.value.AttributeContainer theAttributeContainer) {
      this.theAttributeContainer = theAttributeContainer;
   }

   wt.org.WTPrincipalReference organizationReference;
   /**
    * <b>Supported API: </b>true
    *
    * @see wt.org.OrganizationOwnedImpl
    */
   public wt.org.WTPrincipalReference getOrganizationReference() {
      return organizationReference;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see wt.org.OrganizationOwnedImpl
    */
   public void setOrganizationReference(wt.org.WTPrincipalReference organizationReference) throws wt.util.WTPropertyVetoException {
      organizationReferenceValidate(organizationReference);
      this.organizationReference = organizationReference;
   }
   void organizationReferenceValidate(wt.org.WTPrincipalReference organizationReference) throws wt.util.WTPropertyVetoException {
      if (!wt.fc.IdentityHelper.isChangeable(this))
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.CHANGE_RESTRICTION,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "organizationReference") },
               new java.beans.PropertyChangeEvent(this, "organizationReference", this.organizationReference, organizationReference));
      if (organizationReference == null)
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "organizationReference") },
               new java.beans.PropertyChangeEvent(this, "organizationReference", this.organizationReference, organizationReference));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see wt.org.OrganizationOwned
    */
   public String getOrganizationUniqueIdentifier() {
      try { return ((wt.org.OrganizationOwned) this).getOrganization().getOrganizationIdentifier().getUniqueIdentifier(); } catch (
              NullPointerException npe) { return null; }
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see wt.org.OrganizationOwned
    */
   public String getOrganizationCodingSystem() {
      try { return ((wt.org.OrganizationOwned) this).getOrganization().getOrganizationIdentifier().getCodingSystem(); } catch (
              NullPointerException npe) { return null; }
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see wt.org.OrganizationOwned
    */
   public String getOrganizationName() {
      try { return getOrganizationReference().getName(); } catch (NullPointerException npe) { return null; }
   }

   wt.content.ContentItem primary;
   /**
    * This is a non-persistent ContentItem for FormatContentHolders that is used to pass the primary content from server to client.  Should not be directly accessed by the client.
    *
    * @see wt.content.FormatContentHolder
    */
   public wt.content.ContentItem getPrimary() {
      return primary;
   }
   /**
    * This is a non-persistent ContentItem for FormatContentHolders that is used to pass the primary content from server to client.  Should not be directly accessed by the client.
    *
    * @see wt.content.FormatContentHolder
    */
   public void setPrimary(wt.content.ContentItem primary) throws wt.util.WTPropertyVetoException {
      primaryValidate(primary);
      this.primary = primary;
   }
   void primaryValidate(wt.content.ContentItem primary) throws wt.util.WTPropertyVetoException {
   }

   wt.content.DataFormatReference format;
   /**
    * @see wt.content.FormatContentHolder
    */
   public wt.content.DataFormatReference getFormat() {
      return format;
   }
   /**
    * @see wt.content.FormatContentHolder
    */
   public void setFormat(wt.content.DataFormatReference format) throws wt.util.WTPropertyVetoException {
      formatValidate(format);
      this.format = format;
   }
   void formatValidate(wt.content.DataFormatReference format) throws wt.util.WTPropertyVetoException {
      if (format == null)
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "format") },
               new java.beans.PropertyChangeEvent(this, "format", this.format, format));
   }

   /**
    * Derived from {@link wt.content.DataFormatReference#getFormatName()}
    *
    * @see wt.content.FormatContentHolder
    */
   public String getFormatName() {
      try { return (String) ((wt.content.DataFormatReference) getFormat()).getFormatName(); }
      catch (NullPointerException npe) { return null; }
   }

   java.util.Vector contentVector;
   /**
    * This is a non-persistent vector that is used to pass content from server to client.  Should not be directly accessed by the client.
    *
    * @see wt.content.ContentHolder
    */
   public java.util.Vector getContentVector() {
      return contentVector;
   }
   /**
    * This is a non-persistent vector that is used to pass content from server to client.  Should not be directly accessed by the client.
    *
    * @see wt.content.ContentHolder
    */
   public void setContentVector(java.util.Vector contentVector) throws wt.util.WTPropertyVetoException {
      contentVectorValidate(contentVector);
      this.contentVector = contentVector;
   }
   void contentVectorValidate(java.util.Vector contentVector) throws wt.util.WTPropertyVetoException {
   }

   boolean hasContents;
   /**
    * This is a non-persistent variable that is used to pass information from server to client.  Should not be directly accessed by the client
    *
    * @see wt.content.ContentHolder
    */
   public boolean isHasContents() {
      return hasContents;
   }
   /**
    * This is a non-persistent variable that is used to pass information from server to client.  Should not be directly accessed by the client
    *
    * @see wt.content.ContentHolder
    */
   public void setHasContents(boolean hasContents) throws wt.util.WTPropertyVetoException {
      hasContentsValidate(hasContents);
      this.hasContents = hasContents;
   }
   void hasContentsValidate(boolean hasContents) throws wt.util.WTPropertyVetoException {
   }

   wt.content.HttpContentOperation operation;
   /**
    * @see wt.content.ContentHolder
    */
   public wt.content.HttpContentOperation getOperation() {
      return operation;
   }
   /**
    * @see wt.content.ContentHolder
    */
   public void setOperation(wt.content.HttpContentOperation operation) throws wt.util.WTPropertyVetoException {
      operationValidate(operation);
      this.operation = operation;
   }
   void operationValidate(wt.content.HttpContentOperation operation) throws wt.util.WTPropertyVetoException {
   }

   java.util.Vector httpVector;
   /**
    * This is a non-persistent vector that is used to pass content from server to client.  Should not be directly accessed by the client.
    *
    * @see wt.content.ContentHolder
    */
   public java.util.Vector getHttpVector() {
      return httpVector;
   }
   /**
    * This is a non-persistent vector that is used to pass content from server to client.  Should not be directly accessed by the client.
    *
    * @see wt.content.ContentHolder
    */
   public void setHttpVector(java.util.Vector httpVector) throws wt.util.WTPropertyVetoException {
      httpVectorValidate(httpVector);
      this.httpVector = httpVector;
   }
   void httpVectorValidate(java.util.Vector httpVector) throws wt.util.WTPropertyVetoException {
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

   public static final long EXTERNALIZATION_VERSION_UID = 8547909071051193697L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( containerReference );
      output.writeObject( description );
      output.writeObject( designCompany );
      output.writeObject( designer );
      output.writeObject( format );
      output.writeObject( name );
      output.writeObject( number );
      output.writeObject( organizationReference );
      output.writeObject( typeDefinitionReference );

      if (!(output instanceof wt.pds.PDSObjectOutput)) {
         output.writeObject( contentVector );
         output.writeBoolean( hasContents );
         output.writeObject( httpVector );
         output.writeObject( operation );
         output.writeObject( primary );
         output.writeObject( theAttributeContainer );
      }

   }

   protected void super_writeExternal_Preview(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (Preview) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_Preview(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.writeObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      output.setString( "description", description );
      output.setString( "designCompany", designCompany );
      output.setString( "designer", designer );
      output.writeObject( "format", format, wt.content.DataFormatReference.class, true );
      output.setString( "name", name );
      output.setString( "number", number );
      output.writeObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      output.writeObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      containerReference = (wt.inf.container.WTContainerRef) input.readObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      description = input.getString( "description" );
      designCompany = input.getString( "designCompany" );
      designer = input.getString( "designer" );
      format = (wt.content.DataFormatReference) input.readObject( "format", format, wt.content.DataFormatReference.class, true );
      name = input.getString( "name" );
      number = input.getString( "number" );
      organizationReference = (wt.org.WTPrincipalReference) input.readObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      typeDefinitionReference = (wt.type.TypeDefinitionReference) input.readObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
   }

   boolean readVersion8547909071051193697L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      containerReference = (wt.inf.container.WTContainerRef) input.readObject();
      description = (String) input.readObject();
      designCompany = (String) input.readObject();
      designer = (String) input.readObject();
      format = (wt.content.DataFormatReference) input.readObject();
      name = (String) input.readObject();
      number = (String) input.readObject();
      organizationReference = (wt.org.WTPrincipalReference) input.readObject();
      typeDefinitionReference = (wt.type.TypeDefinitionReference) input.readObject();

      if (!(input instanceof wt.pds.PDSObjectInput)) {
            contentVector = (java.util.Vector) input.readObject();
            hasContents = input.readBoolean();
            httpVector = (java.util.Vector) input.readObject();
            operation = (wt.content.HttpContentOperation) input.readObject();
            primary = (wt.content.ContentItem) input.readObject();
            theAttributeContainer = (wt.iba.value.AttributeContainer) input.readObject();
      }

      return true;
   }

   protected boolean readVersion( Preview thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion8547909071051193697L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_Preview( _Preview thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
