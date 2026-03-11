package ext.casc.process;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ProcessTask extends wt.enterprise.Managed implements wt.inf.container.WTContainedIdentified, wt.type.Typed, wt.org.OrganizationOwnedImpl, wt.org.electronicIdentity.ElectronicallySignable, wt.content.ContentHolder, wt.access.IdentityAccessControlled, wt.recent.RecentlyVisited, java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.casc.process.processResource";
   static final String CLASSNAME = ProcessTask.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String PROCESS_PLAN_ID = "ProcessPlanId";
   java.lang.Long ProcessPlanId;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.Long getProcessPlanId() {
      return ProcessPlanId;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setProcessPlanId(java.lang.Long ProcessPlanId) throws wt.util.WTPropertyVetoException {
      ProcessPlanIdValidate(ProcessPlanId);
      this.ProcessPlanId = ProcessPlanId;
   }
   void ProcessPlanIdValidate(java.lang.Long ProcessPlanId) throws wt.util.WTPropertyVetoException {
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String NAME = "name";
   static int NAME_UPPER_LIMIT = -1;
   java.lang.String name;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.String getName() {
      return name;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
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
    * @see ext.casc.process.ProcessTask
    */
   public static final String NUMBER = "number";
   static int NUMBER_UPPER_LIMIT = -1;
   java.lang.String number;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.String getNumber() {
      return number;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
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
    * @see ext.casc.process.ProcessTask
    */
   public static final String VERSION = "version";
   static int VERSION_UPPER_LIMIT = -1;
   java.lang.String version;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.String getVersion() {
      return version;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setVersion(java.lang.String version) throws wt.util.WTPropertyVetoException {
      versionValidate(version);
      this.version = version;
   }
   void versionValidate(java.lang.String version) throws wt.util.WTPropertyVetoException {
      if (VERSION_UPPER_LIMIT < 1) {
         try { VERSION_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("version").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { VERSION_UPPER_LIMIT = 200; }
      }
      if (version != null && !wt.fc.PersistenceHelper.checkStoredLength(version.toString(), VERSION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "version"), String.valueOf(Math.min(VERSION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "version", this.version, version));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String ZHUZHICHEJIAN = "zhuzhichejian";
   static int ZHUZHICHEJIAN_UPPER_LIMIT = -1;
   java.lang.String zhuzhichejian;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.String getZhuzhichejian() {
      return zhuzhichejian;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setZhuzhichejian(java.lang.String zhuzhichejian) throws wt.util.WTPropertyVetoException {
      zhuzhichejianValidate(zhuzhichejian);
      this.zhuzhichejian = zhuzhichejian;
   }
   void zhuzhichejianValidate(java.lang.String zhuzhichejian) throws wt.util.WTPropertyVetoException {
      if (ZHUZHICHEJIAN_UPPER_LIMIT < 1) {
         try { ZHUZHICHEJIAN_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("zhuzhichejian").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ZHUZHICHEJIAN_UPPER_LIMIT = 200; }
      }
      if (zhuzhichejian != null && !wt.fc.PersistenceHelper.checkStoredLength(zhuzhichejian.toString(), ZHUZHICHEJIAN_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "zhuzhichejian"), String.valueOf(Math.min(ZHUZHICHEJIAN_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "zhuzhichejian", this.zhuzhichejian, zhuzhichejian));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String FUZHICHEJIAN = "fuzhichejian";
   static int FUZHICHEJIAN_UPPER_LIMIT = -1;
   java.lang.String fuzhichejian;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.String getFuzhichejian() {
      return fuzhichejian;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setFuzhichejian(java.lang.String fuzhichejian) throws wt.util.WTPropertyVetoException {
      fuzhichejianValidate(fuzhichejian);
      this.fuzhichejian = fuzhichejian;
   }
   void fuzhichejianValidate(java.lang.String fuzhichejian) throws wt.util.WTPropertyVetoException {
      if (FUZHICHEJIAN_UPPER_LIMIT < 1) {
         try { FUZHICHEJIAN_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("fuzhichejian").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { FUZHICHEJIAN_UPPER_LIMIT = 200; }
      }
      if (fuzhichejian != null && !wt.fc.PersistenceHelper.checkStoredLength(fuzhichejian.toString(), FUZHICHEJIAN_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "fuzhichejian"), String.valueOf(Math.min(FUZHICHEJIAN_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "fuzhichejian", this.fuzhichejian, fuzhichejian));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String START_DATE = "startDate";
   java.sql.Timestamp startDate;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.sql.Timestamp getStartDate() {
      return startDate;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setStartDate(java.sql.Timestamp startDate) throws wt.util.WTPropertyVetoException {
      startDateValidate(startDate);
      this.startDate = startDate;
   }
   void startDateValidate(java.sql.Timestamp startDate) throws wt.util.WTPropertyVetoException {
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String END_DATE = "endDate";
   java.sql.Timestamp endDate;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.sql.Timestamp getEndDate() {
      return endDate;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setEndDate(java.sql.Timestamp endDate) throws wt.util.WTPropertyVetoException {
      endDateValidate(endDate);
      this.endDate = endDate;
   }
   void endDateValidate(java.sql.Timestamp endDate) throws wt.util.WTPropertyVetoException {
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String WANCHENG_DATE = "wanchengDate";
   java.sql.Timestamp wanchengDate;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.sql.Timestamp getWanchengDate() {
      return wanchengDate;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setWanchengDate(java.sql.Timestamp wanchengDate) throws wt.util.WTPropertyVetoException {
      wanchengDateValidate(wanchengDate);
      this.wanchengDate = wanchengDate;
   }
   void wanchengDateValidate(java.sql.Timestamp wanchengDate) throws wt.util.WTPropertyVetoException {
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String TASK_STATE = "taskState";
   static int TASK_STATE_UPPER_LIMIT = -1;
   java.lang.String taskState;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.String getTaskState() {
      return taskState;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setTaskState(java.lang.String taskState) throws wt.util.WTPropertyVetoException {
      taskStateValidate(taskState);
      this.taskState = taskState;
   }
   void taskStateValidate(java.lang.String taskState) throws wt.util.WTPropertyVetoException {
      if (TASK_STATE_UPPER_LIMIT < 1) {
         try { TASK_STATE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("taskState").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { TASK_STATE_UPPER_LIMIT = 200; }
      }
      if (taskState != null && !wt.fc.PersistenceHelper.checkStoredLength(taskState.toString(), TASK_STATE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "taskState"), String.valueOf(Math.min(TASK_STATE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "taskState", this.taskState, taskState));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String TASK_TYPE = "taskType";
   static int TASK_TYPE_UPPER_LIMIT = -1;
   java.lang.String taskType;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.String getTaskType() {
      return taskType;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setTaskType(java.lang.String taskType) throws wt.util.WTPropertyVetoException {
      taskTypeValidate(taskType);
      this.taskType = taskType;
   }
   void taskTypeValidate(java.lang.String taskType) throws wt.util.WTPropertyVetoException {
      if (TASK_TYPE_UPPER_LIMIT < 1) {
         try { TASK_TYPE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("taskType").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { TASK_TYPE_UPPER_LIMIT = 200; }
      }
      if (taskType != null && !wt.fc.PersistenceHelper.checkStoredLength(taskType.toString(), TASK_TYPE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "taskType"), String.valueOf(Math.min(TASK_TYPE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "taskType", this.taskType, taskType));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String RENWUYIJU = "renwuyiju";
   static int RENWUYIJU_UPPER_LIMIT = -1;
   java.lang.String renwuyiju;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.String getRenwuyiju() {
      return renwuyiju;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setRenwuyiju(java.lang.String renwuyiju) throws wt.util.WTPropertyVetoException {
      renwuyijuValidate(renwuyiju);
      this.renwuyiju = renwuyiju;
   }
   void renwuyijuValidate(java.lang.String renwuyiju) throws wt.util.WTPropertyVetoException {
      if (RENWUYIJU_UPPER_LIMIT < 1) {
         try { RENWUYIJU_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("renwuyiju").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { RENWUYIJU_UPPER_LIMIT = 200; }
      }
      if (renwuyiju != null && !wt.fc.PersistenceHelper.checkStoredLength(renwuyiju.toString(), RENWUYIJU_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "renwuyiju"), String.valueOf(Math.min(RENWUYIJU_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "renwuyiju", this.renwuyiju, renwuyiju));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public static final String RENWUYAOQIU = "renwuyaoqiu";
   static int RENWUYAOQIU_UPPER_LIMIT = -1;
   java.lang.String renwuyaoqiu;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public java.lang.String getRenwuyaoqiu() {
      return renwuyaoqiu;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTask
    */
   public void setRenwuyaoqiu(java.lang.String renwuyaoqiu) throws wt.util.WTPropertyVetoException {
      renwuyaoqiuValidate(renwuyaoqiu);
      this.renwuyaoqiu = renwuyaoqiu;
   }
   void renwuyaoqiuValidate(java.lang.String renwuyaoqiu) throws wt.util.WTPropertyVetoException {
      if (RENWUYAOQIU_UPPER_LIMIT < 1) {
         try { RENWUYAOQIU_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("renwuyaoqiu").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { RENWUYAOQIU_UPPER_LIMIT = 2000; }
      }
      if (renwuyaoqiu != null && !wt.fc.PersistenceHelper.checkStoredLength(renwuyaoqiu.toString(), RENWUYAOQIU_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "renwuyaoqiu"), String.valueOf(Math.min(RENWUYAOQIU_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "renwuyaoqiu", this.renwuyaoqiu, renwuyaoqiu));
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

   public static final long EXTERNALIZATION_VERSION_UID = -6587420189518794865L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( ProcessPlanId );
      output.writeObject( containerReference );
      output.writeObject( endDate );
      output.writeObject( fuzhichejian );
      output.writeObject( name );
      output.writeObject( number );
      output.writeObject( organizationReference );
      output.writeObject( renwuyaoqiu );
      output.writeObject( renwuyiju );
      output.writeObject( startDate );
      output.writeObject( taskState );
      output.writeObject( taskType );
      output.writeObject( typeDefinitionReference );
      output.writeObject( version );
      output.writeObject( wanchengDate );
      output.writeObject( zhuzhichejian );

      if (!(output instanceof wt.pds.PDSObjectOutput)) {
         output.writeObject( contentVector );
         output.writeBoolean( hasContents );
         output.writeObject( httpVector );
         output.writeObject( operation );
         output.writeObject( theAttributeContainer );
      }

   }

   protected void super_writeExternal_ProcessTask(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.casc.process.ProcessTask) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ProcessTask(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setLongObject( "ProcessPlanId", ProcessPlanId );
      output.writeObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      output.setTimestamp( "endDate", endDate );
      output.setString( "fuzhichejian", fuzhichejian );
      output.setString( "name", name );
      output.setString( "number", number );
      output.writeObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      output.setString( "renwuyaoqiu", renwuyaoqiu );
      output.setString( "renwuyiju", renwuyiju );
      output.setTimestamp( "startDate", startDate );
      output.setString( "taskState", taskState );
      output.setString( "taskType", taskType );
      output.writeObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
      output.setString( "version", version );
      output.setTimestamp( "wanchengDate", wanchengDate );
      output.setString( "zhuzhichejian", zhuzhichejian );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      ProcessPlanId = input.getLongObject( "ProcessPlanId" );
      containerReference = (wt.inf.container.WTContainerRef) input.readObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      endDate = input.getTimestamp( "endDate" );
      fuzhichejian = input.getString( "fuzhichejian" );
      name = input.getString( "name" );
      number = input.getString( "number" );
      organizationReference = (wt.org.WTPrincipalReference) input.readObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      renwuyaoqiu = input.getString( "renwuyaoqiu" );
      renwuyiju = input.getString( "renwuyiju" );
      startDate = input.getTimestamp( "startDate" );
      taskState = input.getString( "taskState" );
      taskType = input.getString( "taskType" );
      typeDefinitionReference = (wt.type.TypeDefinitionReference) input.readObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
      version = input.getString( "version" );
      wanchengDate = input.getTimestamp( "wanchengDate" );
      zhuzhichejian = input.getString( "zhuzhichejian" );
   }

   boolean readVersion_6587420189518794865L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      ProcessPlanId = (java.lang.Long) input.readObject();
      containerReference = (wt.inf.container.WTContainerRef) input.readObject();
      endDate = (java.sql.Timestamp) input.readObject();
      fuzhichejian = (java.lang.String) input.readObject();
      name = (java.lang.String) input.readObject();
      number = (java.lang.String) input.readObject();
      organizationReference = (wt.org.WTPrincipalReference) input.readObject();
      renwuyaoqiu = (java.lang.String) input.readObject();
      renwuyiju = (java.lang.String) input.readObject();
      startDate = (java.sql.Timestamp) input.readObject();
      taskState = (java.lang.String) input.readObject();
      taskType = (java.lang.String) input.readObject();
      typeDefinitionReference = (wt.type.TypeDefinitionReference) input.readObject();
      version = (java.lang.String) input.readObject();
      wanchengDate = (java.sql.Timestamp) input.readObject();
      zhuzhichejian = (java.lang.String) input.readObject();

      if (!(input instanceof wt.pds.PDSObjectInput)) {
            contentVector = (java.util.Vector) input.readObject();
            hasContents = input.readBoolean();
            httpVector = (java.util.Vector) input.readObject();
            operation = (wt.content.HttpContentOperation) input.readObject();
            theAttributeContainer = (wt.iba.value.AttributeContainer) input.readObject();
      }

      return true;
   }

   protected boolean readVersion( ProcessTask thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_6587420189518794865L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ProcessTask( _ProcessTask thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
