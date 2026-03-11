package ext.sast.supply;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _GLSupply extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final String RESOURCE = "ext.sast.supply.supplyResource";
   static final String CLASSNAME = ext.sast.supply.GLSupply.class.getName();

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String NUMBER = "number";
   static int NUMBER_UPPER_LIMIT = -1;
   String number;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getNumber() {
      return number;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setNumber(String number) throws wt.util.WTPropertyVetoException {
      numberValidate(number);
      this.number = (number != null) ? number.toUpperCase() : null;
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
    * @see GLSupply
    */
   public static final String NAME = "name";
   static int NAME_UPPER_LIMIT = -1;
   String name;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getName() {
      return name;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
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
    * @see GLSupply
    */
   public static final String CODE = "code";
   static int CODE_UPPER_LIMIT = -1;
   String code;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getCode() {
      return code;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setCode(String code) throws wt.util.WTPropertyVetoException {
      codeValidate(code);
      this.code = code;
   }
   void codeValidate(String code) throws wt.util.WTPropertyVetoException {
      if (CODE_UPPER_LIMIT < 1) {
         try { CODE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("code").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CODE_UPPER_LIMIT = 200; }
      }
      if (code != null && !wt.fc.PersistenceHelper.checkStoredLength(code.toString(), CODE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "code"), String.valueOf(Math.min(CODE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "code", this.code, code));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String JC = "jc";
   static int JC_UPPER_LIMIT = -1;
   String jc;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getJc() {
      return jc;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setJc(String jc) throws wt.util.WTPropertyVetoException {
      jcValidate(jc);
      this.jc = jc;
   }
   void jcValidate(String jc) throws wt.util.WTPropertyVetoException {
      if (JC_UPPER_LIMIT < 1) {
         try { JC_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("jc").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { JC_UPPER_LIMIT = 200; }
      }
      if (jc != null && !wt.fc.PersistenceHelper.checkStoredLength(jc.toString(), JC_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "jc"), String.valueOf(Math.min(JC_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "jc", this.jc, jc));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String CYM = "cym";
   static int CYM_UPPER_LIMIT = -1;
   String cym;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getCym() {
      return cym;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setCym(String cym) throws wt.util.WTPropertyVetoException {
      cymValidate(cym);
      this.cym = cym;
   }
   void cymValidate(String cym) throws wt.util.WTPropertyVetoException {
      if (CYM_UPPER_LIMIT < 1) {
         try { CYM_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("cym").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CYM_UPPER_LIMIT = 200; }
      }
      if (cym != null && !wt.fc.PersistenceHelper.checkStoredLength(cym.toString(), CYM_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "cym"), String.valueOf(Math.min(CYM_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "cym", this.cym, cym));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String BC = "bc";
   static int BC_UPPER_LIMIT = -1;
   String bc;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getBc() {
      return bc;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setBc(String bc) throws wt.util.WTPropertyVetoException {
      bcValidate(bc);
      this.bc = bc;
   }
   void bcValidate(String bc) throws wt.util.WTPropertyVetoException {
      if (BC_UPPER_LIMIT < 1) {
         try { BC_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("bc").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { BC_UPPER_LIMIT = 200; }
      }
      if (bc != null && !wt.fc.PersistenceHelper.checkStoredLength(bc.toString(), BC_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "bc"), String.valueOf(Math.min(BC_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "bc", this.bc, bc));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String WZLB = "wzlb";
   static int WZLB_UPPER_LIMIT = -1;
   String wzlb;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getWzlb() {
      return wzlb;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setWzlb(String wzlb) throws wt.util.WTPropertyVetoException {
      wzlbValidate(wzlb);
      this.wzlb = wzlb;
   }
   void wzlbValidate(String wzlb) throws wt.util.WTPropertyVetoException {
      if (wzlb == null || wzlb.trim().length() == 0)
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "wzlb") },
               new java.beans.PropertyChangeEvent(this, "wzlb", this.wzlb, wzlb));
      if (WZLB_UPPER_LIMIT < 1) {
         try { WZLB_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("wzlb").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { WZLB_UPPER_LIMIT = 200; }
      }
      if (wzlb != null && !wt.fc.PersistenceHelper.checkStoredLength(wzlb.toString(), WZLB_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "wzlb"), String.valueOf(Math.min(WZLB_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "wzlb", this.wzlb, wzlb));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String RDCP = "rdcp";
   static int RDCP_UPPER_LIMIT = -1;
   String rdcp;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getRdcp() {
      return rdcp;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setRdcp(String rdcp) throws wt.util.WTPropertyVetoException {
      rdcpValidate(rdcp);
      this.rdcp = rdcp;
   }
   void rdcpValidate(String rdcp) throws wt.util.WTPropertyVetoException {
      if (RDCP_UPPER_LIMIT < 1) {
         try { RDCP_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("rdcp").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { RDCP_UPPER_LIMIT = 200; }
      }
      if (rdcp != null && !wt.fc.PersistenceHelper.checkStoredLength(rdcp.toString(), RDCP_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "rdcp"), String.valueOf(Math.min(RDCP_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "rdcp", this.rdcp, rdcp));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String QYXZ = "qyxz";
   static int QYXZ_UPPER_LIMIT = -1;
   String qyxz;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getQyxz() {
      return qyxz;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setQyxz(String qyxz) throws wt.util.WTPropertyVetoException {
      qyxzValidate(qyxz);
      this.qyxz = qyxz;
   }
   void qyxzValidate(String qyxz) throws wt.util.WTPropertyVetoException {
      if (QYXZ_UPPER_LIMIT < 1) {
         try { QYXZ_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("qyxz").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { QYXZ_UPPER_LIMIT = 200; }
      }
      if (qyxz != null && !wt.fc.PersistenceHelper.checkStoredLength(qyxz.toString(), QYXZ_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "qyxz"), String.valueOf(Math.min(QYXZ_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "qyxz", this.qyxz, qyxz));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String LXR = "lxr";
   static int LXR_UPPER_LIMIT = -1;
   String lxr;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getLxr() {
      return lxr;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setLxr(String lxr) throws wt.util.WTPropertyVetoException {
      lxrValidate(lxr);
      this.lxr = lxr;
   }
   void lxrValidate(String lxr) throws wt.util.WTPropertyVetoException {
      if (LXR_UPPER_LIMIT < 1) {
         try { LXR_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("lxr").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { LXR_UPPER_LIMIT = 200; }
      }
      if (lxr != null && !wt.fc.PersistenceHelper.checkStoredLength(lxr.toString(), LXR_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "lxr"), String.valueOf(Math.min(LXR_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "lxr", this.lxr, lxr));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String LXDH = "lxdh";
   static int LXDH_UPPER_LIMIT = -1;
   String lxdh;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getLxdh() {
      return lxdh;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setLxdh(String lxdh) throws wt.util.WTPropertyVetoException {
      lxdhValidate(lxdh);
      this.lxdh = lxdh;
   }
   void lxdhValidate(String lxdh) throws wt.util.WTPropertyVetoException {
      if (LXDH_UPPER_LIMIT < 1) {
         try { LXDH_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("lxdh").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { LXDH_UPPER_LIMIT = 200; }
      }
      if (lxdh != null && !wt.fc.PersistenceHelper.checkStoredLength(lxdh.toString(), LXDH_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "lxdh"), String.valueOf(Math.min(LXDH_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "lxdh", this.lxdh, lxdh));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String CZ = "cz";
   static int CZ_UPPER_LIMIT = -1;
   String cz;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getCz() {
      return cz;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setCz(String cz) throws wt.util.WTPropertyVetoException {
      czValidate(cz);
      this.cz = cz;
   }
   void czValidate(String cz) throws wt.util.WTPropertyVetoException {
      if (CZ_UPPER_LIMIT < 1) {
         try { CZ_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("cz").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CZ_UPPER_LIMIT = 200; }
      }
      if (cz != null && !wt.fc.PersistenceHelper.checkStoredLength(cz.toString(), CZ_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "cz"), String.valueOf(Math.min(CZ_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "cz", this.cz, cz));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String EMAIL = "email";
   static int EMAIL_UPPER_LIMIT = -1;
   String email;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getEmail() {
      return email;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setEmail(String email) throws wt.util.WTPropertyVetoException {
      emailValidate(email);
      this.email = email;
   }
   void emailValidate(String email) throws wt.util.WTPropertyVetoException {
      if (EMAIL_UPPER_LIMIT < 1) {
         try { EMAIL_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("email").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { EMAIL_UPPER_LIMIT = 200; }
      }
      if (email != null && !wt.fc.PersistenceHelper.checkStoredLength(email.toString(), EMAIL_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "email"), String.valueOf(Math.min(EMAIL_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "email", this.email, email));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String ADDRESS = "address";
   static int ADDRESS_UPPER_LIMIT = -1;
   String address;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getAddress() {
      return address;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setAddress(String address) throws wt.util.WTPropertyVetoException {
      addressValidate(address);
      this.address = address;
   }
   void addressValidate(String address) throws wt.util.WTPropertyVetoException {
      if (ADDRESS_UPPER_LIMIT < 1) {
         try { ADDRESS_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("address").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ADDRESS_UPPER_LIMIT = 200; }
      }
      if (address != null && !wt.fc.PersistenceHelper.checkStoredLength(address.toString(), ADDRESS_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "address"), String.valueOf(Math.min(ADDRESS_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "address", this.address, address));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String ZIPCODE = "zipcode";
   static int ZIPCODE_UPPER_LIMIT = -1;
   String zipcode;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getZipcode() {
      return zipcode;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setZipcode(String zipcode) throws wt.util.WTPropertyVetoException {
      zipcodeValidate(zipcode);
      this.zipcode = zipcode;
   }
   void zipcodeValidate(String zipcode) throws wt.util.WTPropertyVetoException {
      if (ZIPCODE_UPPER_LIMIT < 1) {
         try { ZIPCODE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("zipcode").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { ZIPCODE_UPPER_LIMIT = 200; }
      }
      if (zipcode != null && !wt.fc.PersistenceHelper.checkStoredLength(zipcode.toString(), ZIPCODE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "zipcode"), String.valueOf(Math.min(ZIPCODE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "zipcode", this.zipcode, zipcode));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String CLASSGRADE = "classgrade";
   static int CLASSGRADE_UPPER_LIMIT = -1;
   String classgrade;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getClassgrade() {
      return classgrade;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setClassgrade(String classgrade) throws wt.util.WTPropertyVetoException {
      classgradeValidate(classgrade);
      this.classgrade = classgrade;
   }
   void classgradeValidate(String classgrade) throws wt.util.WTPropertyVetoException {
      if (CLASSGRADE_UPPER_LIMIT < 1) {
         try { CLASSGRADE_UPPER_LIMIT = (Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("classgrade").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CLASSGRADE_UPPER_LIMIT = 200; }
      }
      if (classgrade != null && !wt.fc.PersistenceHelper.checkStoredLength(classgrade.toString(), CLASSGRADE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "classgrade"), String.valueOf(Math.min(CLASSGRADE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "classgrade", this.classgrade, classgrade));
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public static final String STATE = "state";
   static int STATE_UPPER_LIMIT = -1;
   String state;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getState() {
      return state;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setState(String state) throws wt.util.WTPropertyVetoException {
      stateValidate(state);
      this.state = state;
   }
   void stateValidate(String state) throws wt.util.WTPropertyVetoException {
      if (state == null || state.trim().length() == 0)
         throw new wt.util.WTPropertyVetoException("wt.fc.fcResource", wt.fc.fcResource.REQUIRED_ATTRIBUTE,
               new Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "state") },
               new java.beans.PropertyChangeEvent(this, "state", this.state, state));
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
    * @see GLSupply
    */
   public static final String REMARK = "remark";
   static int REMARK_UPPER_LIMIT = -1;
   String remark;
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public String getRemark() {
      return remark;
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see GLSupply
    */
   public void setRemark(String remark) throws wt.util.WTPropertyVetoException {
      remarkValidate(remark);
      this.remark = remark;
   }
   void remarkValidate(String remark) throws wt.util.WTPropertyVetoException {
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

   public static final long EXTERNALIZATION_VERSION_UID = -1166100840663750856L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( address );
      output.writeObject( bc );
      output.writeObject( classgrade );
      output.writeObject( code );
      output.writeObject( cym );
      output.writeObject( cz );
      output.writeObject( email );
      output.writeObject( jc );
      output.writeObject( lxdh );
      output.writeObject( lxr );
      output.writeObject( name );
      output.writeObject( number );
      output.writeObject( qyxz );
      output.writeObject( rdcp );
      output.writeObject( remark );
      output.writeObject( state );
      output.writeObject( wzlb );
      output.writeObject( zipcode );
   }

   protected void super_writeExternal_GLSupply(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (ext.sast.supply.GLSupply) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_GLSupply(java.io.ObjectInput input) throws java.io.IOException, ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "address", address );
      output.setString( "bc", bc );
      output.setString( "classgrade", classgrade );
      output.setString( "code", code );
      output.setString( "cym", cym );
      output.setString( "cz", cz );
      output.setString( "email", email );
      output.setString( "jc", jc );
      output.setString( "lxdh", lxdh );
      output.setString( "lxr", lxr );
      output.setString( "name", name );
      output.setString( "number", number );
      output.setString( "qyxz", qyxz );
      output.setString( "rdcp", rdcp );
      output.setString( "remark", remark );
      output.setString( "state", state );
      output.setString( "wzlb", wzlb );
      output.setString( "zipcode", zipcode );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      address = input.getString( "address" );
      bc = input.getString( "bc" );
      classgrade = input.getString( "classgrade" );
      code = input.getString( "code" );
      cym = input.getString( "cym" );
      cz = input.getString( "cz" );
      email = input.getString( "email" );
      jc = input.getString( "jc" );
      lxdh = input.getString( "lxdh" );
      lxr = input.getString( "lxr" );
      name = input.getString( "name" );
      number = input.getString( "number" );
      qyxz = input.getString( "qyxz" );
      rdcp = input.getString( "rdcp" );
      remark = input.getString( "remark" );
      state = input.getString( "state" );
      wzlb = input.getString( "wzlb" );
      zipcode = input.getString( "zipcode" );
   }

   boolean readVersion_1166100840663750856L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      address = (String) input.readObject();
      bc = (String) input.readObject();
      classgrade = (String) input.readObject();
      code = (String) input.readObject();
      cym = (String) input.readObject();
      cz = (String) input.readObject();
      email = (String) input.readObject();
      jc = (String) input.readObject();
      lxdh = (String) input.readObject();
      lxr = (String) input.readObject();
      name = (String) input.readObject();
      number = (String) input.readObject();
      qyxz = (String) input.readObject();
      rdcp = (String) input.readObject();
      remark = (String) input.readObject();
      state = (String) input.readObject();
      wzlb = (String) input.readObject();
      zipcode = (String) input.readObject();
      return true;
   }

   protected boolean readVersion( GLSupply thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_1166100840663750856L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_GLSupply( _GLSupply thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, ClassNotFoundException { return true; }
}
