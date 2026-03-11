package ext.casc.process;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _ProcessTaskItem extends wt.enterprise.Managed implements wt.inf.container.WTContainedIdentified, wt.type.Typed, wt.org.OrganizationOwnedImpl, wt.org.electronicIdentity.ElectronicallySignable, wt.content.ContentHolder, wt.access.IdentityAccessControlled, wt.recent.RecentlyVisited, java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.casc.process.processResource";
   static final String CLASSNAME = ProcessTaskItem.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String PROCESS_TASK_ID = "ProcessTaskId";
   java.lang.Long ProcessTaskId;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.Long getProcessTaskId() {
      return ProcessTaskId;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setProcessTaskId(java.lang.Long ProcessTaskId) throws wt.util.WTPropertyVetoException {
      ProcessTaskIdValidate(ProcessTaskId);
      this.ProcessTaskId = ProcessTaskId;
   }
   void ProcessTaskIdValidate(java.lang.Long ProcessTaskId) throws wt.util.WTPropertyVetoException {
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String PART_ID = "partId";
   java.lang.Long partId;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.Long getPartId() {
      return partId;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setPartId(java.lang.Long partId) throws wt.util.WTPropertyVetoException {
      partIdValidate(partId);
      this.partId = partId;
   }
   void partIdValidate(java.lang.Long partId) throws wt.util.WTPropertyVetoException {
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String OWNER = "owner";
   static int OWNER_UPPER_LIMIT = -1;
   java.lang.String owner;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getOwner() {
      return owner;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setOwner(java.lang.String owner) throws wt.util.WTPropertyVetoException {
      ownerValidate(owner);
      this.owner = owner;
   }
   void ownerValidate(java.lang.String owner) throws wt.util.WTPropertyVetoException {
      if (OWNER_UPPER_LIMIT < 1) {
         try { OWNER_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("owner").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OWNER_UPPER_LIMIT = 200; }
      }
      if (owner != null && !wt.fc.PersistenceHelper.checkStoredLength(owner.toString(), OWNER_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "owner"), String.valueOf(Math.min(OWNER_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "owner", this.owner, owner));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String NAME = "name";
   static int NAME_UPPER_LIMIT = -1;
   java.lang.String name;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getName() {
      return name;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String NUMBER = "number";
   static int NUMBER_UPPER_LIMIT = -1;
   java.lang.String number;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getNumber() {
      return number;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String VERSION = "version";
   static int VERSION_UPPER_LIMIT = -1;
   java.lang.String version;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getVersion() {
      return version;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String ROUTE_SELECT = "routeSelect";
   static int ROUTE_SELECT_UPPER_LIMIT = -1;
   java.lang.String routeSelect;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getRouteSelect() {
      return routeSelect;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setRouteSelect(java.lang.String routeSelect) throws wt.util.WTPropertyVetoException {
      routeSelectValidate(routeSelect);
      this.routeSelect = routeSelect;
   }
   void routeSelectValidate(java.lang.String routeSelect) throws wt.util.WTPropertyVetoException {
      if (ROUTE_SELECT_UPPER_LIMIT < 1) {
         try { ROUTE_SELECT_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("routeSelect").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ROUTE_SELECT_UPPER_LIMIT = 200; }
      }
      if (routeSelect != null && !wt.fc.PersistenceHelper.checkStoredLength(routeSelect.toString(), ROUTE_SELECT_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "routeSelect"), String.valueOf(Math.min(ROUTE_SELECT_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "routeSelect", this.routeSelect, routeSelect));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String COMPLETED_BY = "completedBy";
   static int COMPLETED_BY_UPPER_LIMIT = -1;
   java.lang.String completedBy;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getCompletedBy() {
      return completedBy;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setCompletedBy(java.lang.String completedBy) throws wt.util.WTPropertyVetoException {
      completedByValidate(completedBy);
      this.completedBy = completedBy;
   }
   void completedByValidate(java.lang.String completedBy) throws wt.util.WTPropertyVetoException {
      if (COMPLETED_BY_UPPER_LIMIT < 1) {
         try { COMPLETED_BY_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("completedBy").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { COMPLETED_BY_UPPER_LIMIT = 200; }
      }
      if (completedBy != null && !wt.fc.PersistenceHelper.checkStoredLength(completedBy.toString(), COMPLETED_BY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "completedBy"), String.valueOf(Math.min(COMPLETED_BY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "completedBy", this.completedBy, completedBy));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String ZHURENGONGYISHI = "zhurengongyishi";
   static int ZHURENGONGYISHI_UPPER_LIMIT = -1;
   java.lang.String zhurengongyishi;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getZhurengongyishi() {
      return zhurengongyishi;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setZhurengongyishi(java.lang.String zhurengongyishi) throws wt.util.WTPropertyVetoException {
      zhurengongyishiValidate(zhurengongyishi);
      this.zhurengongyishi = zhurengongyishi;
   }
   void zhurengongyishiValidate(java.lang.String zhurengongyishi) throws wt.util.WTPropertyVetoException {
      if (ZHURENGONGYISHI_UPPER_LIMIT < 1) {
         try { ZHURENGONGYISHI_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("zhurengongyishi").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ZHURENGONGYISHI_UPPER_LIMIT = 200; }
      }
      if (zhurengongyishi != null && !wt.fc.PersistenceHelper.checkStoredLength(zhurengongyishi.toString(), ZHURENGONGYISHI_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "zhurengongyishi"), String.valueOf(Math.min(ZHURENGONGYISHI_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "zhurengongyishi", this.zhurengongyishi, zhurengongyishi));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String CHEJIAN = "chejian";
   static int CHEJIAN_UPPER_LIMIT = -1;
   java.lang.String chejian;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getChejian() {
      return chejian;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setChejian(java.lang.String chejian) throws wt.util.WTPropertyVetoException {
      chejianValidate(chejian);
      this.chejian = chejian;
   }
   void chejianValidate(java.lang.String chejian) throws wt.util.WTPropertyVetoException {
      if (CHEJIAN_UPPER_LIMIT < 1) {
         try { CHEJIAN_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("chejian").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CHEJIAN_UPPER_LIMIT = 200; }
      }
      if (chejian != null && !wt.fc.PersistenceHelper.checkStoredLength(chejian.toString(), CHEJIAN_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "chejian"), String.valueOf(Math.min(CHEJIAN_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "chejian", this.chejian, chejian));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String ZHUZHICHEJIAN = "zhuzhichejian";
   static int ZHUZHICHEJIAN_UPPER_LIMIT = -1;
   java.lang.String zhuzhichejian;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getZhuzhichejian() {
      return zhuzhichejian;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String FUZHICHEJIAN = "fuzhichejian";
   static int FUZHICHEJIAN_UPPER_LIMIT = -1;
   java.lang.String fuzhichejian;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getFuzhichejian() {
      return fuzhichejian;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String GONGYIYUAN = "gongyiyuan";
   static int GONGYIYUAN_UPPER_LIMIT = -1;
   java.lang.String gongyiyuan;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getGongyiyuan() {
      return gongyiyuan;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setGongyiyuan(java.lang.String gongyiyuan) throws wt.util.WTPropertyVetoException {
      gongyiyuanValidate(gongyiyuan);
      this.gongyiyuan = gongyiyuan;
   }
   void gongyiyuanValidate(java.lang.String gongyiyuan) throws wt.util.WTPropertyVetoException {
      if (GONGYIYUAN_UPPER_LIMIT < 1) {
         try { GONGYIYUAN_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("gongyiyuan").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { GONGYIYUAN_UPPER_LIMIT = 200; }
      }
      if (gongyiyuan != null && !wt.fc.PersistenceHelper.checkStoredLength(gongyiyuan.toString(), GONGYIYUAN_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "gongyiyuan"), String.valueOf(Math.min(GONGYIYUAN_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "gongyiyuan", this.gongyiyuan, gongyiyuan));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String EXECUTOR_ROLE = "executorRole";
   static int EXECUTOR_ROLE_UPPER_LIMIT = -1;
   java.lang.String executorRole;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getExecutorRole() {
      return executorRole;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setExecutorRole(java.lang.String executorRole) throws wt.util.WTPropertyVetoException {
      executorRoleValidate(executorRole);
      this.executorRole = executorRole;
   }
   void executorRoleValidate(java.lang.String executorRole) throws wt.util.WTPropertyVetoException {
      if (EXECUTOR_ROLE_UPPER_LIMIT < 1) {
         try { EXECUTOR_ROLE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("executorRole").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { EXECUTOR_ROLE_UPPER_LIMIT = 200; }
      }
      if (executorRole != null && !wt.fc.PersistenceHelper.checkStoredLength(executorRole.toString(), EXECUTOR_ROLE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "executorRole"), String.valueOf(Math.min(EXECUTOR_ROLE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "executorRole", this.executorRole, executorRole));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String ISZHUZHI = "iszhuzhi";
   java.lang.Boolean iszhuzhi;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.Boolean getIszhuzhi() {
      return iszhuzhi;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setIszhuzhi(java.lang.Boolean iszhuzhi) throws wt.util.WTPropertyVetoException {
      iszhuzhiValidate(iszhuzhi);
      this.iszhuzhi = iszhuzhi;
   }
   void iszhuzhiValidate(java.lang.Boolean iszhuzhi) throws wt.util.WTPropertyVetoException {
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String TASK_ITEM_NAME = "taskItemName";
   static int TASK_ITEM_NAME_UPPER_LIMIT = -1;
   java.lang.String taskItemName;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getTaskItemName() {
      return taskItemName;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setTaskItemName(java.lang.String taskItemName) throws wt.util.WTPropertyVetoException {
      taskItemNameValidate(taskItemName);
      this.taskItemName = taskItemName;
   }
   void taskItemNameValidate(java.lang.String taskItemName) throws wt.util.WTPropertyVetoException {
      if (TASK_ITEM_NAME_UPPER_LIMIT < 1) {
         try { TASK_ITEM_NAME_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("taskItemName").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { TASK_ITEM_NAME_UPPER_LIMIT = 200; }
      }
      if (taskItemName != null && !wt.fc.PersistenceHelper.checkStoredLength(taskItemName.toString(), TASK_ITEM_NAME_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "taskItemName"), String.valueOf(Math.min(TASK_ITEM_NAME_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "taskItemName", this.taskItemName, taskItemName));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String TASK_ITEM_STATE = "taskItemState";
   static int TASK_ITEM_STATE_UPPER_LIMIT = -1;
   java.lang.String taskItemState;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getTaskItemState() {
      return taskItemState;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public void setTaskItemState(java.lang.String taskItemState) throws wt.util.WTPropertyVetoException {
      taskItemStateValidate(taskItemState);
      this.taskItemState = taskItemState;
   }
   void taskItemStateValidate(java.lang.String taskItemState) throws wt.util.WTPropertyVetoException {
      if (TASK_ITEM_STATE_UPPER_LIMIT < 1) {
         try { TASK_ITEM_STATE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("taskItemState").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { TASK_ITEM_STATE_UPPER_LIMIT = 200; }
      }
      if (taskItemState != null && !wt.fc.PersistenceHelper.checkStoredLength(taskItemState.toString(), TASK_ITEM_STATE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "taskItemState"), String.valueOf(Math.min(TASK_ITEM_STATE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "taskItemState", this.taskItemState, taskItemState));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String TASK_TYPE = "taskType";
   static int TASK_TYPE_UPPER_LIMIT = -1;
   java.lang.String taskType;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getTaskType() {
      return taskType;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String END_DATE = "endDate";
   java.sql.Timestamp endDate;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.sql.Timestamp getEndDate() {
      return endDate;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String RENWUYAOQIU = "renwuyaoqiu";
   static int RENWUYAOQIU_UPPER_LIMIT = -1;
   java.lang.String renwuyaoqiu;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getRenwuyaoqiu() {
      return renwuyaoqiu;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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

   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String RENWUYIJU = "renwuyiju";
   static int RENWUYIJU_UPPER_LIMIT = -1;
   java.lang.String renwuyiju;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getRenwuyiju() {
      return renwuyiju;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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
    * @see ext.casc.process.ProcessTaskItem
    */
   public static final String DESCRIPTION = "description";
   static int DESCRIPTION_UPPER_LIMIT = -1;
   java.lang.String description;
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
    */
   public java.lang.String getDescription() {
      return description;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see ext.casc.process.ProcessTaskItem
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

   public static final long EXTERNALIZATION_VERSION_UID = 8145295100453223425L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( ProcessTaskId );
      output.writeObject( chejian );
      output.writeObject( completedBy );
      output.writeObject( containerReference );
      output.writeObject( description );
      output.writeObject( endDate );
      output.writeObject( executorRole );
      output.writeObject( fuzhichejian );
      output.writeObject( gongyiyuan );
      output.writeObject( iszhuzhi );
      output.writeObject( name );
      output.writeObject( number );
      output.writeObject( organizationReference );
      output.writeObject( owner );
      output.writeObject( partId );
      output.writeObject( renwuyaoqiu );
      output.writeObject( renwuyiju );
      output.writeObject( routeSelect );
      output.writeObject( taskItemName );
      output.writeObject( taskItemState );
      output.writeObject( taskType );
      output.writeObject( typeDefinitionReference );
      output.writeObject( version );
      output.writeObject( zhurengongyishi );
      output.writeObject( zhuzhichejian );

      if (!(output instanceof wt.pds.PDSObjectOutput)) {
         output.writeObject( contentVector );
         output.writeBoolean( hasContents );
         output.writeObject( httpVector );
         output.writeObject( operation );
         output.writeObject( theAttributeContainer );
      }

   }

   protected void super_writeExternal_ProcessTaskItem(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.casc.process.ProcessTaskItem) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_ProcessTaskItem(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setLongObject( "ProcessTaskId", ProcessTaskId );
      output.setString( "chejian", chejian );
      output.setString( "completedBy", completedBy );
      output.writeObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      output.setString( "description", description );
      output.setTimestamp( "endDate", endDate );
      output.setString( "executorRole", executorRole );
      output.setString( "fuzhichejian", fuzhichejian );
      output.setString( "gongyiyuan", gongyiyuan );
      output.setBooleanObject( "iszhuzhi", iszhuzhi );
      output.setString( "name", name );
      output.setString( "number", number );
      output.writeObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      output.setString( "owner", owner );
      output.setLongObject( "partId", partId );
      output.setString( "renwuyaoqiu", renwuyaoqiu );
      output.setString( "renwuyiju", renwuyiju );
      output.setString( "routeSelect", routeSelect );
      output.setString( "taskItemName", taskItemName );
      output.setString( "taskItemState", taskItemState );
      output.setString( "taskType", taskType );
      output.writeObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
      output.setString( "version", version );
      output.setString( "zhurengongyishi", zhurengongyishi );
      output.setString( "zhuzhichejian", zhuzhichejian );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      ProcessTaskId = input.getLongObject( "ProcessTaskId" );
      chejian = input.getString( "chejian" );
      completedBy = input.getString( "completedBy" );
      containerReference = (wt.inf.container.WTContainerRef) input.readObject( "containerReference", containerReference, wt.inf.container.WTContainerRef.class, true );
      description = input.getString( "description" );
      endDate = input.getTimestamp( "endDate" );
      executorRole = input.getString( "executorRole" );
      fuzhichejian = input.getString( "fuzhichejian" );
      gongyiyuan = input.getString( "gongyiyuan" );
      iszhuzhi = input.getBooleanObject( "iszhuzhi" );
      name = input.getString( "name" );
      number = input.getString( "number" );
      organizationReference = (wt.org.WTPrincipalReference) input.readObject( "organizationReference", organizationReference, wt.org.WTPrincipalReference.class, true );
      owner = input.getString( "owner" );
      partId = input.getLongObject( "partId" );
      renwuyaoqiu = input.getString( "renwuyaoqiu" );
      renwuyiju = input.getString( "renwuyiju" );
      routeSelect = input.getString( "routeSelect" );
      taskItemName = input.getString( "taskItemName" );
      taskItemState = input.getString( "taskItemState" );
      taskType = input.getString( "taskType" );
      typeDefinitionReference = (wt.type.TypeDefinitionReference) input.readObject( "typeDefinitionReference", typeDefinitionReference, wt.type.TypeDefinitionReference.class, true );
      version = input.getString( "version" );
      zhurengongyishi = input.getString( "zhurengongyishi" );
      zhuzhichejian = input.getString( "zhuzhichejian" );
   }

   boolean readVersion8145295100453223425L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      ProcessTaskId = (java.lang.Long) input.readObject();
      chejian = (java.lang.String) input.readObject();
      completedBy = (java.lang.String) input.readObject();
      containerReference = (wt.inf.container.WTContainerRef) input.readObject();
      description = (java.lang.String) input.readObject();
      endDate = (java.sql.Timestamp) input.readObject();
      executorRole = (java.lang.String) input.readObject();
      fuzhichejian = (java.lang.String) input.readObject();
      gongyiyuan = (java.lang.String) input.readObject();
      iszhuzhi = (java.lang.Boolean) input.readObject();
      name = (java.lang.String) input.readObject();
      number = (java.lang.String) input.readObject();
      organizationReference = (wt.org.WTPrincipalReference) input.readObject();
      owner = (java.lang.String) input.readObject();
      partId = (java.lang.Long) input.readObject();
      renwuyaoqiu = (java.lang.String) input.readObject();
      renwuyiju = (java.lang.String) input.readObject();
      routeSelect = (java.lang.String) input.readObject();
      taskItemName = (java.lang.String) input.readObject();
      taskItemState = (java.lang.String) input.readObject();
      taskType = (java.lang.String) input.readObject();
      typeDefinitionReference = (wt.type.TypeDefinitionReference) input.readObject();
      version = (java.lang.String) input.readObject();
      zhurengongyishi = (java.lang.String) input.readObject();
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

   protected boolean readVersion( ProcessTaskItem thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion8145295100453223425L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_ProcessTaskItem( _ProcessTaskItem thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException { return true; }
}
