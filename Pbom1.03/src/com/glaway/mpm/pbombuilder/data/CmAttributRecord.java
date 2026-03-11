package com.glaway.mpm.pbombuilder.data;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import com.glaway.mpm.pbombuilder.util.CmXML;
import com.glaway.mpm.pbombuilder.util.CmXML.CmXmlProperty;

/**
 * <br>Created on 2012-10-29
 * @author chenyunlong
 */
public class CmAttributRecord implements CmPersistable {
   private static final long  serialVersionUID   = 4966638804522825883L;
   public static final String XMLTITLE           = "CmAttributRecord";

   public static final String SEARCHKEY          = "SEARCHKEY";
   public static final String ROOTOID            = "ROOTOID";
   public static final String PARENTOID          = "PARENTOID";
   public static final String OID                = "OID";
   public static final String NODENUMBER         = "NODENUMBER";
   public static final String NODENAME           = "NODENAME";
   public static final String LOWERLEVELNAME     = "LOWERLEVELNAME";
   public static final String QUANTITY           = "QUANTITY";
   public static final String CODE               = "CODE";
   public static final String MANUFACTURINGLINES = "MANUFACTURINGLINES";
   public static final String USEDEPARTMENT      = "USEDEPARTMENT";
   public static final String POSITIOING         = "POSITIOING";
   public static final String AREAOCCUPIER       = "AREAOCCUPIER";
   public static final String EFFICTIVITY        = "EFFICTIVITY";
   public static final String MEMO               = "MEMO";

   private String             searchKey;                                //用于多条查询的key,不唯一
   private String             rootOid;                                  //所附属的顶级的OID

   private String             parentOid;                                //上级的OID

   private String             oid;

   private String             number;                                   //零组件编号

   private String             name;                                     //零组件名称

   private String             lowerLevelName;                           //下级工程组件名称

   private int                quantity           = 0;                   //数量-总计

   private String             code;                                     //零件代码

   private String             manufacturingLines;                       //制造路线

   private String             useDepartment;                            //使用部门

   private String             positioning;                              //定位-改为保存物料编码

   private String             areaOccupier;                             //区域/占位

   private String             effictivity;                              //有效性

   private String             memo;                                     //备注

   private String             keyId;

   public Map getCreateMap() {
      HashMap ret = new HashMap();
      ret.put(SEARCHKEY, searchKey);
      ret.put(ROOTOID, rootOid);
      ret.put(PARENTOID, parentOid);
      ret.put(OID, oid);
      ret.put(NODENUMBER, number);
      ret.put(NODENAME, name);
      ret.put(LOWERLEVELNAME, lowerLevelName);
      ret.put(QUANTITY, quantity);
      ret.put(CODE, code);
      ret.put(MANUFACTURINGLINES, manufacturingLines);
      ret.put(USEDEPARTMENT, useDepartment);
      ret.put(POSITIOING, positioning);
      ret.put(AREAOCCUPIER, areaOccupier);
      ret.put(EFFICTIVITY, "");//有效性不保存值
      ret.put(MEMO, memo);
      return ret;
   }

   public Object getKeyId() {
      return keyId;
   }

   public CmAttributRecord() {
      super();
   }

   public CmAttributRecord(String rootOid, String parentOid, String oid, String number, String name, String lowerLevelName, int quantity,
      String code, String manufacturingLines, String useDepartment, String positioning, String areaOccupier, String effictivity, String memo) {
      super();
      this.rootOid = rootOid;
      this.parentOid = parentOid;
      this.oid = oid;
      this.number = number;
      this.name = name;
      this.lowerLevelName = lowerLevelName;
      this.quantity = quantity;
      this.code = code;
      this.manufacturingLines = manufacturingLines;
      this.useDepartment = useDepartment;
      this.positioning = positioning;
      this.areaOccupier = areaOccupier;
      this.effictivity = effictivity;
      this.memo = memo;
      this.searchKey = rootOid + "_" + parentOid + "_" + oid;
   }

   public CmPersistable getObject(ResultSet rs) throws Exception {
      if (rs != null) {
         setKeyId(rs.getString(KEY_ID));
         setSearchKey(rs.getString(SEARCHKEY));
         setRootOid(rs.getString(ROOTOID));
         setParentOid(rs.getString(PARENTOID));
         setOid(rs.getString(OID));
         setNumber(rs.getString(NODENUMBER));
         setName(rs.getString(NODENAME));
         setLowerLevelName(rs.getString(LOWERLEVELNAME));
         setQuantity(rs.getInt(QUANTITY));
         setCode(rs.getString(CODE));
         setManufacturingLines(rs.getString(MANUFACTURINGLINES));
         setUseDepartment(rs.getString(USEDEPARTMENT));
         setPositioning(rs.getString(POSITIOING));
         setAreaOccupier(rs.getString(AREAOCCUPIER));
         setEffictivity(rs.getString(EFFICTIVITY));
         setMemo(rs.getString(MEMO));
      }

      return this;
   }

   public Map getUpdateMap() {
      HashMap ret = new HashMap();
      ret.put(SEARCHKEY, searchKey);
      ret.put(ROOTOID, rootOid);
      ret.put(PARENTOID, parentOid);
      ret.put(OID, oid);
      ret.put(NODENUMBER, number);
      ret.put(NODENAME, name);
      ret.put(LOWERLEVELNAME, lowerLevelName);
      ret.put(QUANTITY, quantity);
      ret.put(CODE, code);
      ret.put(MANUFACTURINGLINES, manufacturingLines);
      ret.put(USEDEPARTMENT, useDepartment);
      ret.put(POSITIOING, positioning);
      ret.put(AREAOCCUPIER, areaOccupier);
      ret.put(EFFICTIVITY, "");//有效性值不保存
      ret.put(MEMO, memo);

      return ret;
   }

   /**
    *返回值为AO组件/sps工作包 的branchid
    */
   public String getRootOid() {
      return rootOid;
   }

   public void setRootOid(String rootId) {
      this.rootOid = rootId;
   }

   public void setRootOid(long rootId) {
      String rootOidStr = String.valueOf(rootId);
      this.rootOid = rootOidStr;
   }

   public String getParentOid() {
      return parentOid;
   }

   public void setParentOid(String parentOid) {
      this.parentOid = parentOid;
   }

   public void setParentOid(long parentOid) {
      String parentOidStr = String.valueOf(parentOid);
      this.parentOid = parentOidStr;
   }

   public String getOid() {
      return oid;
   }

   public void setOid(String oid) {
      this.oid = oid;
   }

   public void setOid(long oid) {
      String oidStr = String.valueOf(oid);
      this.oid = oidStr;
   }

   public String getAreaOccupier() {
      return areaOccupier;
   }

   public void setAreaOccupier(String areaOccupier) {
      this.areaOccupier = areaOccupier;
   }

   public String getCode() {
      return code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getEffictivity() {
      return effictivity;
   }

   public void setEffictivity(String effictivity) {
      this.effictivity = effictivity;
   }

   public String getLowerLevelName() {
      return lowerLevelName;
   }

   public void setLowerLevelName(String lowerLevelName) {
      this.lowerLevelName = lowerLevelName;
   }

   public String getManufacturingLines() {
      return manufacturingLines;
   }

   public void setManufacturingLines(String manufacturingLines) {
      this.manufacturingLines = manufacturingLines;
   }

   public String getMemo() {
      return memo;
   }

   public void setMemo(String memo) {
      this.memo = memo;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getNumber() {
      return number;
   }

   public void setNumber(String number) {
      this.number = number;
   }

   public String getPositioning() {
      return positioning;
   }

   public void setPositioning(String positioning) {
      this.positioning = positioning;
   }

   public int getQuantity() {
      return quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   public String getUseDepartment() {
      return useDepartment;
   }

   public void setUseDepartment(String useDepartment) {
      this.useDepartment = useDepartment;
   }

   public void setKeyId(String keyId) {
      this.keyId = keyId;
   }

   @Override
   public String toString() {
      return "CmAttributRecord [rootOid=" + rootOid + ", areaOccupier=" + areaOccupier + ", code=" + code + ", effictivity=" + effictivity
         + ", keyId=" + keyId + ", lowerLevelName=" + lowerLevelName + ", manufacturingLines=" + manufacturingLines + ", memo=" + memo + ", name="
         + name + ", number=" + number + ", oid=" + oid + ", parentOid=" + parentOid + ", positioning=" + positioning + ", quantity=" + quantity
         + ", useDepartment=" + useDepartment + "]";
   }

   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((keyId == null) ? 0 : keyId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final CmAttributRecord other = (CmAttributRecord) obj;
      if (keyId == null) {
         if (other.keyId != null)
            return false;
      } else if (!keyId.equals(other.keyId))
         return false;
      return true;
   }

   public String getSearchKey() {
      return searchKey;
   }

   public void setSearchKey(String searchKey) {
      this.searchKey = searchKey;
   }

   /**
    * @return
    */
   public CmXML write2XML() {
      CmXML xml = new CmXML("CmAttributRecord");
      Vector<CmXmlProperty> propertys = getAttributProperty();
      xml.setAttrs(propertys);
      return xml;
   }

   private Vector<CmXmlProperty> getAttributProperty() {
      Vector<CmXmlProperty> propertys = new Vector<CmXmlProperty>();

      propertys.add(new CmXmlProperty("lowerLevelName", getLowerLevelName()));
      int quantity = getQuantity();
      String quantityStr = String.valueOf(quantity);
      propertys.add(new CmXmlProperty("searchKey", getSearchKey()));
      propertys.add(new CmXmlProperty("rootOid", getRootOid()));
      propertys.add(new CmXmlProperty("parentOid", getParentOid()));
      propertys.add(new CmXmlProperty("oid", getOid()));
      propertys.add(new CmXmlProperty("number", getNumber()));
      propertys.add(new CmXmlProperty("name", getName()));
      propertys.add(new CmXmlProperty("quantity", quantityStr));
      propertys.add(new CmXmlProperty("code", getCode() != null ? getCode() : ""));
      propertys.add(new CmXmlProperty("manufacturingLines", getManufacturingLines() != null ? getManufacturingLines() : ""));
      propertys.add(new CmXmlProperty("useDepartment", getUseDepartment() != null ? getUseDepartment() : ""));
      propertys.add(new CmXmlProperty("positioning", getPositioning() != null ? getPositioning() : ""));
      propertys.add(new CmXmlProperty("areaOccupier", getAreaOccupier() != null ? getAreaOccupier() : ""));
      propertys.add(new CmXmlProperty("effictivity",""));//getEffictivity() != null ? getEffictivity() : ""));
      propertys.add(new CmXmlProperty("memo", getMemo() != null ? getMemo() : ""));
      return propertys;
   }

   /**
    * @param xml
    * @return
    */
   public static CmAttributRecord load4XML(CmXML xml) {
      if (xml == null)
         return null;
      CmAttributRecord record = new CmAttributRecord();

      String quantityStr = xml.attrval("quantity");

      int quantity = quantityStr == null ? 0 : Integer.parseInt(quantityStr);

      record.setSearchKey(xml.attrval("searchKey"));
      record.setRootOid(xml.attrval("rootOid"));
      record.setParentOid(xml.attrval("parentOid"));
      record.setOid(xml.attrval("oid"));
      record.setNumber(xml.attrval("number"));
      record.setName(xml.attrval("name"));
      record.setQuantity(quantity);
      record.setLowerLevelName(xml.attrval("lowerLevelName"));
      record.setCode(xml.attrval("code"));
      record.setManufacturingLines(xml.attrval("manufacturingLines"));
      record.setUseDepartment(xml.attrval("useDepartment"));
      record.setPositioning(xml.attrval("positioning"));
      record.setAreaOccupier(xml.attrval("areaOccupier"));
      record.setEffictivity(xml.attrval("effictivity"));
      record.setMemo(xml.attrval("memo"));
      return record;
   }

}
