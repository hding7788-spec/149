package ext.ases.changepackaged;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ChangePackaged extends wt.enterprise.Managed implements wt.inf.container.WTContainedIdentified, wt.type.Typed, wt.org.OrganizationOwnedImpl, wt.org.electronicIdentity.ElectronicallySignable, wt.content.ContentHolder, wt.access.IdentityAccessControlled, wt.recent.RecentlyVisited, java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.ases.changepackaged.changepackagedResource";
   static final String CLASSNAME = ChangePackaged.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String NAME = "name";
   static int NAME_UPPER_LIMIT = -1;
   java.lang.String name;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getName() {
      return name;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
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
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String NUMBER = "number";
   static int NUMBER_UPPER_LIMIT = -1;
   java.lang.String number;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getNumber() {
      return number;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
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
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String DESCRIPTION = "description";
   static int DESCRIPTION_UPPER_LIMIT = -1;
   java.lang.String description;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getDescription() {
      return description;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
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
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String COMPLEX = "complex";
   static int COMPLEX_UPPER_LIMIT = -1;
   java.lang.String complex;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getComplex() {
      return complex;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setComplex(java.lang.String complex) throws wt.util.WTPropertyVetoException {
      complexValidate(complex);
      this.complex = complex;
   }
   void complexValidate(java.lang.String complex) throws wt.util.WTPropertyVetoException {
      if (COMPLEX_UPPER_LIMIT < 1) {
         try { COMPLEX_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("complex").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { COMPLEX_UPPER_LIMIT = 200; }
      }
      if (complex != null && !wt.fc.PersistenceHelper.checkStoredLength(complex.toString(), COMPLEX_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "complex"), String.valueOf(Math.min(COMPLEX_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "complex", this.complex, complex));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String PROFESSION = "profession";
   static int PROFESSION_UPPER_LIMIT = -1;
   java.lang.String profession;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getProfession() {
      return profession;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setProfession(java.lang.String profession) throws wt.util.WTPropertyVetoException {
      professionValidate(profession);
      this.profession = profession;
   }
   void professionValidate(java.lang.String profession) throws wt.util.WTPropertyVetoException {
      if (PROFESSION_UPPER_LIMIT < 1) {
         try { PROFESSION_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("profession").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { PROFESSION_UPPER_LIMIT = 200; }
      }
      if (profession != null && !wt.fc.PersistenceHelper.checkStoredLength(profession.toString(), PROFESSION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "profession"), String.valueOf(Math.min(PROFESSION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "profession", this.profession, profession));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String CHANGETYPE = "changetype";
   static int CHANGETYPE_UPPER_LIMIT = -1;
   java.lang.String changetype;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getChangetype() {
      return changetype;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setChangetype(java.lang.String changetype) throws wt.util.WTPropertyVetoException {
      changetypeValidate(changetype);
      this.changetype = changetype;
   }
   void changetypeValidate(java.lang.String changetype) throws wt.util.WTPropertyVetoException {
      if (CHANGETYPE_UPPER_LIMIT < 1) {
         try { CHANGETYPE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("changetype").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CHANGETYPE_UPPER_LIMIT = 200; }
      }
      if (changetype != null && !wt.fc.PersistenceHelper.checkStoredLength(changetype.toString(), CHANGETYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "changetype"), String.valueOf(Math.min(CHANGETYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "changetype", this.changetype, changetype));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String PHASECODE = "phasecode";
   static int PHASECODE_UPPER_LIMIT = -1;
   java.lang.String phasecode;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getPhasecode() {
      return phasecode;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setPhasecode(java.lang.String phasecode) throws wt.util.WTPropertyVetoException {
      phasecodeValidate(phasecode);
      this.phasecode = phasecode;
   }
   void phasecodeValidate(java.lang.String phasecode) throws wt.util.WTPropertyVetoException {
      if (PHASECODE_UPPER_LIMIT < 1) {
         try { PHASECODE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("phasecode").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { PHASECODE_UPPER_LIMIT = 200; }
      }
      if (phasecode != null && !wt.fc.PersistenceHelper.checkStoredLength(phasecode.toString(), PHASECODE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "phasecode"), String.valueOf(Math.min(PHASECODE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "phasecode", this.phasecode, phasecode));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String AFFECTDPAGE = "affectdpage";
   static int AFFECTDPAGE_UPPER_LIMIT = -1;
   java.lang.String affectdpage;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getAffectdpage() {
      return affectdpage;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setAffectdpage(java.lang.String affectdpage) throws wt.util.WTPropertyVetoException {
      affectdpageValidate(affectdpage);
      this.affectdpage = affectdpage;
   }
   void affectdpageValidate(java.lang.String affectdpage) throws wt.util.WTPropertyVetoException {
      if (AFFECTDPAGE_UPPER_LIMIT < 1) {
         try { AFFECTDPAGE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("affectdpage").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { AFFECTDPAGE_UPPER_LIMIT = 200; }
      }
      if (affectdpage != null && !wt.fc.PersistenceHelper.checkStoredLength(affectdpage.toString(), AFFECTDPAGE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "affectdpage"), String.valueOf(Math.min(AFFECTDPAGE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "affectdpage", this.affectdpage, affectdpage));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String DEPARTMENT = "department";
   static int DEPARTMENT_UPPER_LIMIT = -1;
   java.lang.String department;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getDepartment() {
      return department;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setDepartment(java.lang.String department) throws wt.util.WTPropertyVetoException {
      departmentValidate(department);
      this.department = department;
   }
   void departmentValidate(java.lang.String department) throws wt.util.WTPropertyVetoException {
      if (DEPARTMENT_UPPER_LIMIT < 1) {
         try { DEPARTMENT_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("department").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { DEPARTMENT_UPPER_LIMIT = 200; }
      }
      if (department != null && !wt.fc.PersistenceHelper.checkStoredLength(department.toString(), DEPARTMENT_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "department"), String.valueOf(Math.min(DEPARTMENT_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "department", this.department, department));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String AVIDMTYPE = "avidmtype";
   static int AVIDMTYPE_UPPER_LIMIT = -1;
   java.lang.String avidmtype;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getAvidmtype() {
      return avidmtype;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
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
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String REQUESTPRIORITY = "requestpriority";
   static int REQUESTPRIORITY_UPPER_LIMIT = -1;
   java.lang.String requestpriority;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getRequestpriority() {
      return requestpriority;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setRequestpriority(java.lang.String requestpriority) throws wt.util.WTPropertyVetoException {
      requestpriorityValidate(requestpriority);
      this.requestpriority = requestpriority;
   }
   void requestpriorityValidate(java.lang.String requestpriority) throws wt.util.WTPropertyVetoException {
      if (REQUESTPRIORITY_UPPER_LIMIT < 1) {
         try { REQUESTPRIORITY_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("requestpriority").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { REQUESTPRIORITY_UPPER_LIMIT = 200; }
      }
      if (requestpriority != null && !wt.fc.PersistenceHelper.checkStoredLength(requestpriority.toString(), REQUESTPRIORITY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "requestpriority"), String.valueOf(Math.min(REQUESTPRIORITY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "requestpriority", this.requestpriority, requestpriority));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String CHANGEREASON = "changereason";
   static int CHANGEREASON_UPPER_LIMIT = -1;
   java.lang.String changereason;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getChangereason() {
      return changereason;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setChangereason(java.lang.String changereason) throws wt.util.WTPropertyVetoException {
      changereasonValidate(changereason);
      this.changereason = changereason;
   }
   void changereasonValidate(java.lang.String changereason) throws wt.util.WTPropertyVetoException {
      if (CHANGEREASON_UPPER_LIMIT < 1) {
         try { CHANGEREASON_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("changereason").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CHANGEREASON_UPPER_LIMIT = 200; }
      }
      if (changereason != null && !wt.fc.PersistenceHelper.checkStoredLength(changereason.toString(), CHANGEREASON_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "changereason"), String.valueOf(Math.min(CHANGEREASON_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "changereason", this.changereason, changereason));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String TEMPLATE = "template";
   static int TEMPLATE_UPPER_LIMIT = -1;
   java.lang.String template;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getTemplate() {
      return template;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
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
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String CHANGELEIXING = "changeleixing";
   static int CHANGELEIXING_UPPER_LIMIT = -1;
   java.lang.String changeleixing;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getChangeleixing() {
      return changeleixing;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setChangeleixing(java.lang.String changeleixing) throws wt.util.WTPropertyVetoException {
      changeleixingValidate(changeleixing);
      this.changeleixing = changeleixing;
   }
   void changeleixingValidate(java.lang.String changeleixing) throws wt.util.WTPropertyVetoException {
      if (CHANGELEIXING_UPPER_LIMIT < 1) {
         try { CHANGELEIXING_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("changeleixing").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CHANGELEIXING_UPPER_LIMIT = 200; }
      }
      if (changeleixing != null && !wt.fc.PersistenceHelper.checkStoredLength(changeleixing.toString(), CHANGELEIXING_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "changeleixing"), String.valueOf(Math.min(CHANGELEIXING_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "changeleixing", this.changeleixing, changeleixing));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String COST = "cost";
   static int COST_UPPER_LIMIT = -1;
   java.lang.String cost;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getCost() {
      return cost;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
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
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String REQUESTTIME = "requesttime";
   static int REQUESTTIME_UPPER_LIMIT = -1;
   java.lang.String requesttime;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getRequesttime() {
      return requesttime;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setRequesttime(java.lang.String requesttime) throws wt.util.WTPropertyVetoException {
      requesttimeValidate(requesttime);
      this.requesttime = requesttime;
   }
   void requesttimeValidate(java.lang.String requesttime) throws wt.util.WTPropertyVetoException {
      if (REQUESTTIME_UPPER_LIMIT < 1) {
         try { REQUESTTIME_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("requesttime").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { REQUESTTIME_UPPER_LIMIT = 200; }
      }
      if (requesttime != null && !wt.fc.PersistenceHelper.checkStoredLength(requesttime.toString(), REQUESTTIME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "requesttime"), String.valueOf(Math.min(REQUESTTIME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "requesttime", this.requesttime, requesttime));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String STARTPHASENAME = "startphasename";
   static int STARTPHASENAME_UPPER_LIMIT = -1;
   java.lang.String startphasename;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getStartphasename() {
      return startphasename;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setStartphasename(java.lang.String startphasename) throws wt.util.WTPropertyVetoException {
      startphasenameValidate(startphasename);
      this.startphasename = startphasename;
   }
   void startphasenameValidate(java.lang.String startphasename) throws wt.util.WTPropertyVetoException {
      if (STARTPHASENAME_UPPER_LIMIT < 1) {
         try { STARTPHASENAME_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("startphasename").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { STARTPHASENAME_UPPER_LIMIT = 200; }
      }
      if (startphasename != null && !wt.fc.PersistenceHelper.checkStoredLength(startphasename.toString(), STARTPHASENAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "startphasename"), String.valueOf(Math.min(STARTPHASENAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "startphasename", this.startphasename, startphasename));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String TARGETPHASENAME = "targetphasename";
   static int TARGETPHASENAME_UPPER_LIMIT = -1;
   java.lang.String targetphasename;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getTargetphasename() {
      return targetphasename;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setTargetphasename(java.lang.String targetphasename) throws wt.util.WTPropertyVetoException {
      targetphasenameValidate(targetphasename);
      this.targetphasename = targetphasename;
   }
   void targetphasenameValidate(java.lang.String targetphasename) throws wt.util.WTPropertyVetoException {
      if (TARGETPHASENAME_UPPER_LIMIT < 1) {
         try { TARGETPHASENAME_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("targetphasename").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { TARGETPHASENAME_UPPER_LIMIT = 200; }
      }
      if (targetphasename != null && !wt.fc.PersistenceHelper.checkStoredLength(targetphasename.toString(), TARGETPHASENAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "targetphasename"), String.valueOf(Math.min(TARGETPHASENAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "targetphasename", this.targetphasename, targetphasename));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String PINDEX = "pindex";
   static int PINDEX_UPPER_LIMIT = -1;
   java.lang.String pindex;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getPindex() {
      return pindex;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setPindex(java.lang.String pindex) throws wt.util.WTPropertyVetoException {
      pindexValidate(pindex);
      this.pindex = pindex;
   }
   void pindexValidate(java.lang.String pindex) throws wt.util.WTPropertyVetoException {
      if (PINDEX_UPPER_LIMIT < 1) {
         try { PINDEX_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("pindex").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { PINDEX_UPPER_LIMIT = 200; }
      }
      if (pindex != null && !wt.fc.PersistenceHelper.checkStoredLength(pindex.toString(), PINDEX_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "pindex"), String.valueOf(Math.min(PINDEX_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "pindex", this.pindex, pindex));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String FILENUMBER = "filenumber";
   static int FILENUMBER_UPPER_LIMIT = -1;
   java.lang.String filenumber;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getFilenumber() {
      return filenumber;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setFilenumber(java.lang.String filenumber) throws wt.util.WTPropertyVetoException {
      filenumberValidate(filenumber);
      this.filenumber = filenumber;
   }
   void filenumberValidate(java.lang.String filenumber) throws wt.util.WTPropertyVetoException {
      if (FILENUMBER_UPPER_LIMIT < 1) {
         try { FILENUMBER_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("filenumber").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { FILENUMBER_UPPER_LIMIT = 200; }
      }
      if (filenumber != null && !wt.fc.PersistenceHelper.checkStoredLength(filenumber.toString(), FILENUMBER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "filenumber"), String.valueOf(Math.min(FILENUMBER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "filenumber", this.filenumber, filenumber));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String SECRET = "secret";
   static int SECRET_UPPER_LIMIT = -1;
   java.lang.String secret;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getSecret() {
      return secret;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setSecret(java.lang.String secret) throws wt.util.WTPropertyVetoException {
      secretValidate(secret);
      this.secret = secret;
   }
   void secretValidate(java.lang.String secret) throws wt.util.WTPropertyVetoException {
      if (SECRET_UPPER_LIMIT < 1) {
         try { SECRET_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("secret").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { SECRET_UPPER_LIMIT = 200; }
      }
      if (secret != null && !wt.fc.PersistenceHelper.checkStoredLength(secret.toString(), SECRET_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "secret"), String.valueOf(Math.min(SECRET_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "secret", this.secret, secret));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String RESPONSOR = "responsor";
   static int RESPONSOR_UPPER_LIMIT = -1;
   java.lang.String responsor;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getResponsor() {
      return responsor;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setResponsor(java.lang.String responsor) throws wt.util.WTPropertyVetoException {
      responsorValidate(responsor);
      this.responsor = responsor;
   }
   void responsorValidate(java.lang.String responsor) throws wt.util.WTPropertyVetoException {
      if (RESPONSOR_UPPER_LIMIT < 1) {
         try { RESPONSOR_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("responsor").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { RESPONSOR_UPPER_LIMIT = 200; }
      }
      if (responsor != null && !wt.fc.PersistenceHelper.checkStoredLength(responsor.toString(), RESPONSOR_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "responsor"), String.valueOf(Math.min(RESPONSOR_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "responsor", this.responsor, responsor));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String GUANCANGHAO = "guancanghao";
   static int GUANCANGHAO_UPPER_LIMIT = -1;
   java.lang.String guancanghao;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getGuancanghao() {
      return guancanghao;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setGuancanghao(java.lang.String guancanghao) throws wt.util.WTPropertyVetoException {
      guancanghaoValidate(guancanghao);
      this.guancanghao = guancanghao;
   }
   void guancanghaoValidate(java.lang.String guancanghao) throws wt.util.WTPropertyVetoException {
      if (GUANCANGHAO_UPPER_LIMIT < 1) {
         try { GUANCANGHAO_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("guancanghao").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { GUANCANGHAO_UPPER_LIMIT = 200; }
      }
      if (guancanghao != null && !wt.fc.PersistenceHelper.checkStoredLength(guancanghao.toString(), GUANCANGHAO_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "guancanghao"), String.valueOf(Math.min(GUANCANGHAO_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "guancanghao", this.guancanghao, guancanghao));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String EDITTIME = "edittime";
   static int EDITTIME_UPPER_LIMIT = -1;
   java.lang.String edittime;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getEdittime() {
      return edittime;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public void setEdittime(java.lang.String edittime) throws wt.util.WTPropertyVetoException {
      edittimeValidate(edittime);
      this.edittime = edittime;
   }
   void edittimeValidate(java.lang.String edittime) throws wt.util.WTPropertyVetoException {
      if (EDITTIME_UPPER_LIMIT < 1) {
         try { EDITTIME_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("edittime").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { EDITTIME_UPPER_LIMIT = 200; }
      }
      if (edittime != null && !wt.fc.PersistenceHelper.checkStoredLength(edittime.toString(), EDITTIME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "edittime"), String.valueOf(Math.min(EDITTIME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "edittime", this.edittime, edittime));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String REMARK = "remark";
   static int REMARK_UPPER_LIMIT = -1;
   java.lang.String remark;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getRemark() {
      return remark;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
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
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public static final String IMPLEMENT = "implement";
   static int IMPLEMENT_UPPER_LIMIT = -1;
   java.lang.String implement;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
    */
   public java.lang.String getImplement() {
      return implement;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.ases.changepackaged.ChangePackaged
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

   public static final long EXTERNALIZATION_VERSION_UID = 2567230539509057615L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( affectdpage );
      output.writeObject( avidmtype );
      output.writeObject( changeleixing );
      output.writeObject( changereason );
      output.writeObject( changetype );
      output.writeObject( complex );
      output.writeObject( containerReference );
      output.writeObject( cost );
      output.writeObject( department );
      output.writeObject( description );
      output.writeObject( edittime );
      output.writeObject( filenumber );
      output.writeObject( guancanghao );
      output.writeObject( implement );
      output.writeObject( name );
      output.writeObject( number );
      output.writeObject( organizationReference );
      output.writeObject( phasecode );
      output.writeObject( pindex );
      output.writeObject( profession );
      output.writeObject( remark );
      output.writeObject( requestpriority );
      output.writeObject( requesttime );
      output.writeObject( responsor );
      output.writeObject( secret );
      output.writeObject( startphasename );
      output.writeObject( targetphasename );
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

   protected void super_writeExternal_ChangePackaged(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.ases.changepackaged.ChangePackaged) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ChangePackaged(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "affectdpage", affectdpage );
      output.setString( "avidmtype", avidmtype );
      output.setString( "changeleixing", changeleixing );
      output.setString( "changereason", changereason );
      output.setString( "changetype", changetype );
      output.setString( "complex", complex );
      output.writeObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      output.setString( "cost", cost );
      output.setString( "department", department );
      output.setString( "description", description );
      output.setString( "edittime", edittime );
      output.setString( "filenumber", filenumber );
      output.setString( "guancanghao", guancanghao );
      output.setString( "implement", implement );
      output.setString( "name", name );
      output.setString( "number", number );
      output.writeObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      output.setString( "phasecode", phasecode );
      output.setString( "pindex", pindex );
      output.setString( "profession", profession );
      output.setString( "remark", remark );
      output.setString( "requestpriority", requestpriority );
      output.setString( "requesttime", requesttime );
      output.setString( "responsor", responsor );
      output.setString( "secret", secret );
      output.setString( "startphasename", startphasename );
      output.setString( "targetphasename", targetphasename );
      output.setString( "template", template );
      output.writeObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      affectdpage = input.getString( "affectdpage" );
      avidmtype = input.getString( "avidmtype" );
      changeleixing = input.getString( "changeleixing" );
      changereason = input.getString( "changereason" );
      changetype = input.getString( "changetype" );
      complex = input.getString( "complex" );
      containerReference = (wt.inf.container.WTContainerRef) input.readObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      cost = input.getString( "cost" );
      department = input.getString( "department" );
      description = input.getString( "description" );
      edittime = input.getString( "edittime" );
      filenumber = input.getString( "filenumber" );
      guancanghao = input.getString( "guancanghao" );
      implement = input.getString( "implement" );
      name = input.getString( "name" );
      number = input.getString( "number" );
      organizationReference = (wt.org.WTPrincipalReference) input.readObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      phasecode = input.getString( "phasecode" );
      pindex = input.getString( "pindex" );
      profession = input.getString( "profession" );
      remark = input.getString( "remark" );
      requestpriority = input.getString( "requestpriority" );
      requesttime = input.getString( "requesttime" );
      responsor = input.getString( "responsor" );
      secret = input.getString( "secret" );
      startphasename = input.getString( "startphasename" );
      targetphasename = input.getString( "targetphasename" );
      template = input.getString( "template" );
      typeDefinitionReference = (wt.type.TypeDefinitionReference) input.readObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
   }

   boolean readVersion2567230539509057615L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      affectdpage = (java.lang.String) input.readObject();
      avidmtype = (java.lang.String) input.readObject();
      changeleixing = (java.lang.String) input.readObject();
      changereason = (java.lang.String) input.readObject();
      changetype = (java.lang.String) input.readObject();
      complex = (java.lang.String) input.readObject();
      containerReference = (wt.inf.container.WTContainerRef) input.readObject();
      cost = (java.lang.String) input.readObject();
      department = (java.lang.String) input.readObject();
      description = (java.lang.String) input.readObject();
      edittime = (java.lang.String) input.readObject();
      filenumber = (java.lang.String) input.readObject();
      guancanghao = (java.lang.String) input.readObject();
      implement = (java.lang.String) input.readObject();
      name = (java.lang.String) input.readObject();
      number = (java.lang.String) input.readObject();
      organizationReference = (wt.org.WTPrincipalReference) input.readObject();
      phasecode = (java.lang.String) input.readObject();
      pindex = (java.lang.String) input.readObject();
      profession = (java.lang.String) input.readObject();
      remark = (java.lang.String) input.readObject();
      requestpriority = (java.lang.String) input.readObject();
      requesttime = (java.lang.String) input.readObject();
      responsor = (java.lang.String) input.readObject();
      secret = (java.lang.String) input.readObject();
      startphasename = (java.lang.String) input.readObject();
      targetphasename = (java.lang.String) input.readObject();
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

   protected boolean readVersion( ChangePackaged thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion2567230539509057615L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ChangePackaged( _ChangePackaged thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
