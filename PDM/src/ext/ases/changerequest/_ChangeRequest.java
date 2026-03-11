package ext.ases.changerequest;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ChangeRequest extends wt.enterprise.Managed implements wt.inf.container.WTContainedIdentified, wt.type.Typed, wt.org.OrganizationOwnedImpl, wt.org.electronicIdentity.ElectronicallySignable, wt.content.ContentHolder, wt.access.IdentityAccessControlled, wt.recent.RecentlyVisited, java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.ases.changerequest.changerequestResource";
   static final String CLASSNAME = ChangeRequest.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String NAME = "name";
   static int NAME_UPPER_LIMIT = -1;
   java.lang.String name;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getName() {
      return name;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public void setName(java.lang.String name) throws wt.util.WTPropertyVetoException {
      nameValidate(name);
      this.name = name;
   }
   void nameValidate(java.lang.String name) throws wt.util.WTPropertyVetoException {
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
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String NUMBER = "number";
   static int NUMBER_UPPER_LIMIT = -1;
   java.lang.String number;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getNumber() {
      return number;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public void setNumber(java.lang.String number) throws wt.util.WTPropertyVetoException {
      numberValidate(number);
      this.number = number;
   }
   void numberValidate(java.lang.String number) throws wt.util.WTPropertyVetoException {
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
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String DESCRIPTION = "description";
   static int DESCRIPTION_UPPER_LIMIT = -1;
   java.lang.String description;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getDescription() {
      return description;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public void setDescription(java.lang.String description) throws wt.util.WTPropertyVetoException {
      descriptionValidate(description);
      this.description = description;
   }
   void descriptionValidate(java.lang.String description) throws wt.util.WTPropertyVetoException {
      if (DESCRIPTION_UPPER_LIMIT < 1) {
         try { DESCRIPTION_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("description").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DESCRIPTION_UPPER_LIMIT = 2000; }
      }
      if (description != null && !wt.fc.PersistenceHelper.checkStoredLength(description.toString(), DESCRIPTION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "description"), String.valueOf(Math.min(DESCRIPTION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "description", this.description, description));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String AVIDMTYPE = "avidmtype";
   static int AVIDMTYPE_UPPER_LIMIT = -1;
   java.lang.String avidmtype;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getAvidmtype() {
      return avidmtype;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public void setAvidmtype(java.lang.String avidmtype) throws wt.util.WTPropertyVetoException {
      avidmtypeValidate(avidmtype);
      this.avidmtype = avidmtype;
   }
   void avidmtypeValidate(java.lang.String avidmtype) throws wt.util.WTPropertyVetoException {
      if (AVIDMTYPE_UPPER_LIMIT < 1) {
         try { AVIDMTYPE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("avidmtype").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { AVIDMTYPE_UPPER_LIMIT = 200; }
      }
      if (avidmtype != null && !wt.fc.PersistenceHelper.checkStoredLength(avidmtype.toString(), AVIDMTYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "avidmtype"), String.valueOf(Math.min(AVIDMTYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "avidmtype", this.avidmtype, avidmtype));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String REQUESTTYPE = "requesttype";
   static int REQUESTTYPE_UPPER_LIMIT = -1;
   java.lang.String requesttype;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getRequesttype() {
      return requesttype;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public void setRequesttype(java.lang.String requesttype) throws wt.util.WTPropertyVetoException {
      requesttypeValidate(requesttype);
      this.requesttype = requesttype;
   }
   void requesttypeValidate(java.lang.String requesttype) throws wt.util.WTPropertyVetoException {
      if (REQUESTTYPE_UPPER_LIMIT < 1) {
         try { REQUESTTYPE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("requesttype").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { REQUESTTYPE_UPPER_LIMIT = 200; }
      }
      if (requesttype != null && !wt.fc.PersistenceHelper.checkStoredLength(requesttype.toString(), REQUESTTYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "requesttype"), String.valueOf(Math.min(REQUESTTYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "requesttype", this.requesttype, requesttype));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String REQUESTPROPRITY = "requestproprity";
   static int REQUESTPROPRITY_UPPER_LIMIT = -1;
   java.lang.String requestproprity;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getRequestproprity() {
      return requestproprity;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public void setRequestproprity(java.lang.String requestproprity) throws wt.util.WTPropertyVetoException {
      requestproprityValidate(requestproprity);
      this.requestproprity = requestproprity;
   }
   void requestproprityValidate(java.lang.String requestproprity) throws wt.util.WTPropertyVetoException {
      if (REQUESTPROPRITY_UPPER_LIMIT < 1) {
         try { REQUESTPROPRITY_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("requestproprity").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { REQUESTPROPRITY_UPPER_LIMIT = 200; }
      }
      if (requestproprity != null && !wt.fc.PersistenceHelper.checkStoredLength(requestproprity.toString(), REQUESTPROPRITY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "requestproprity"), String.valueOf(Math.min(REQUESTPROPRITY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "requestproprity", this.requestproprity, requestproprity));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String TEMPLATE = "template";
   static int TEMPLATE_UPPER_LIMIT = -1;
   java.lang.String template;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getTemplate() {
      return template;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public void setTemplate(java.lang.String template) throws wt.util.WTPropertyVetoException {
      templateValidate(template);
      this.template = template;
   }
   void templateValidate(java.lang.String template) throws wt.util.WTPropertyVetoException {
      if (TEMPLATE_UPPER_LIMIT < 1) {
         try { TEMPLATE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("template").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { TEMPLATE_UPPER_LIMIT = 200; }
      }
      if (template != null && !wt.fc.PersistenceHelper.checkStoredLength(template.toString(), TEMPLATE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "template"), String.valueOf(Math.min(TEMPLATE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "template", this.template, template));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String SOLUTION = "solution";
   static int SOLUTION_UPPER_LIMIT = -1;
   java.lang.String solution;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getSolution() {
      return solution;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public void setSolution(java.lang.String solution) throws wt.util.WTPropertyVetoException {
      solutionValidate(solution);
      this.solution = solution;
   }
   void solutionValidate(java.lang.String solution) throws wt.util.WTPropertyVetoException {
      if (SOLUTION_UPPER_LIMIT < 1) {
         try { SOLUTION_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("solution").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { SOLUTION_UPPER_LIMIT = 200; }
      }
      if (solution != null && !wt.fc.PersistenceHelper.checkStoredLength(solution.toString(), SOLUTION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "solution"), String.valueOf(Math.min(SOLUTION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "solution", this.solution, solution));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String REMARK = "remark";
   static int REMARK_UPPER_LIMIT = -1;
   java.lang.String remark;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getRemark() {
      return remark;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
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

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String COST = "cost";
   static int COST_UPPER_LIMIT = -1;
   java.lang.String cost;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getCost() {
      return cost;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public void setCost(java.lang.String cost) throws wt.util.WTPropertyVetoException {
      costValidate(cost);
      this.cost = cost;
   }
   void costValidate(java.lang.String cost) throws wt.util.WTPropertyVetoException {
      if (COST_UPPER_LIMIT < 1) {
         try { COST_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("cost").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { COST_UPPER_LIMIT = 200; }
      }
      if (cost != null && !wt.fc.PersistenceHelper.checkStoredLength(cost.toString(), COST_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "cost"), String.valueOf(Math.min(COST_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "cost", this.cost, cost));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public static final String IMPLEMENT = "implement";
   static int IMPLEMENT_UPPER_LIMIT = -1;
   java.lang.String implement;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
    */
   public java.lang.String getImplement() {
      return implement;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changerequest.ChangeRequest
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
   public java.lang.String getContainerName() {
      try { return (java.lang.String) ((wt.inf.container.WTContainerRef) getContainerReference()).getName(); }
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
   public java.lang.String getOrganizationUniqueIdentifier() {
      try { return ((wt.org.OrganizationOwned) this).getOrganization().getOrganizationIdentifier().getUniqueIdentifier(); } catch (NullPointerException npe) { return null; }
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see wt.org.OrganizationOwned
    */
   public java.lang.String getOrganizationCodingSystem() {
      try { return ((wt.org.OrganizationOwned) this).getOrganization().getOrganizationIdentifier().getCodingSystem(); } catch (NullPointerException npe) { return null; }
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see wt.org.OrganizationOwned
    */
   public java.lang.String getOrganizationName() {
      try { return getOrganizationReference().getName(); } catch (NullPointerException npe) { return null; }
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

   public static final long EXTERNALIZATION_VERSION_UID = 6113896522517369234L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( avidmtype );
      output.writeObject( containerReference );
      output.writeObject( cost );
      output.writeObject( description );
      output.writeObject( implement );
      output.writeObject( name );
      output.writeObject( number );
      output.writeObject( organizationReference );
      output.writeObject( remark );
      output.writeObject( requestproprity );
      output.writeObject( requesttype );
      output.writeObject( solution );
      output.writeObject( template );
      output.writeObject( typeDefinitionReference );

      if (!(output instanceof wt.pds.PDSObjectOutput)) {
         output.writeObject( contentVector );
         output.writeBoolean( hasContents );
         output.writeObject( httpVector );
         output.writeObject( operation );
         output.writeObject( theAttributeContainer );
      }

   }

   protected void super_writeExternal_ChangeRequest(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.ases.changerequest.ChangeRequest) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ChangeRequest(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "avidmtype", avidmtype );
      output.writeObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      output.setString( "cost", cost );
      output.setString( "description", description );
      output.setString( "implement", implement );
      output.setString( "name", name );
      output.setString( "number", number );
      output.writeObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      output.setString( "remark", remark );
      output.setString( "requestproprity", requestproprity );
      output.setString( "requesttype", requesttype );
      output.setString( "solution", solution );
      output.setString( "template", template );
      output.writeObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      avidmtype = input.getString( "avidmtype" );
      containerReference = (wt.inf.container.WTContainerRef) input.readObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      cost = input.getString( "cost" );
      description = input.getString( "description" );
      implement = input.getString( "implement" );
      name = input.getString( "name" );
      number = input.getString( "number" );
      organizationReference = (wt.org.WTPrincipalReference) input.readObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      remark = input.getString( "remark" );
      requestproprity = input.getString( "requestproprity" );
      requesttype = input.getString( "requesttype" );
      solution = input.getString( "solution" );
      template = input.getString( "template" );
      typeDefinitionReference = (wt.type.TypeDefinitionReference) input.readObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
   }

   boolean readVersion6113896522517369234L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      avidmtype = (java.lang.String) input.readObject();
      containerReference = (wt.inf.container.WTContainerRef) input.readObject();
      cost = (java.lang.String) input.readObject();
      description = (java.lang.String) input.readObject();
      implement = (java.lang.String) input.readObject();
      name = (java.lang.String) input.readObject();
      number = (java.lang.String) input.readObject();
      organizationReference = (wt.org.WTPrincipalReference) input.readObject();
      remark = (java.lang.String) input.readObject();
      requestproprity = (java.lang.String) input.readObject();
      requesttype = (java.lang.String) input.readObject();
      solution = (java.lang.String) input.readObject();
      template = (java.lang.String) input.readObject();
      typeDefinitionReference = (wt.type.TypeDefinitionReference) input.readObject();

      if (!(input instanceof wt.pds.PDSObjectInput)) {
            contentVector = (java.util.Vector) input.readObject();
            hasContents = input.readBoolean();
            httpVector = (java.util.Vector) input.readObject();
            operation = (wt.content.HttpContentOperation) input.readObject();
            theAttributeContainer = (wt.iba.value.AttributeContainer) input.readObject();
      }

      return true;
   }

   protected boolean readVersion( ChangeRequest thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion6113896522517369234L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ChangeRequest( _ChangeRequest thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
