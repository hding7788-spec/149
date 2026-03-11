package com.glaway.mpm.pbombuilder.tree;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import com.glaway.mpm.pbombuilder.data.CmAttributRecord;
import com.glaway.mpm.pbombuilder.util.CmXML;

/**
 * <br>Created on 2012-10-29
 * @author chenyunlong
 * <br>
 *数据对象，在编辑属性中用于封装属性值
 *这是在treeTable中会使用的数据类
 */
public class CmPartAttributNode extends CmTreeNode implements Serializable {
//   private static CmLogger log=CmLogger.getLogger(CmPartAttributNode.class);
   public static String               XMLTITLE         = "CmPartAttributNode";

   public static final int            LEVEL_0          = 0;
   public static final int            LEVEL_1          = 1;
   public static final int            LEVEL_2          = 2;

   private static final long          serialVersionUID = 6753795667998346923L;

   private long                       lowerLevelOid    = 0;
   private CmAttributRecord           attribut         = new CmAttributRecord();
   //逻辑处理属性
   private int                        childLevel       = 0;                               //当前节点所处级别
   private CmPartAttributNode         fromNode;
   private int                        sequenceNumber;                                     //自己是父节点的第几个子节点
   private Vector<CmPartAttributNode> childList        = new Vector<CmPartAttributNode>();

   public CmPartAttributNode(Object useObject) {
      super(useObject);
      if (this.getPart() != null) {
         this.setNumber(this.getPart().getPartNumber());
         this.setName(this.getPart().getPartName());
      }
   }

   public String getOid() {
      return attribut.getOid();
   }

   public void setOid(String oid) {
      attribut.setOid(oid);
   }

   public void setOid(long oid) {
      String oidStr = String.valueOf(oid);
      setOid(oidStr);
   }

   public String getRootOid() {
      return attribut.getRootOid();
   }

   public void setRootOid(String rootOid) {
      attribut.setRootOid(rootOid);
   }

   public void setRootOid(long rootOid) {
      String rootOidStr = String.valueOf(rootOid);
      setRootOid(rootOidStr);
   }

   public String getParentOid() {
      return attribut.getParentOid();
   }

   public void setParentOid(String parentOid) {
      attribut.setParentOid(parentOid);
   }

   public void setParentOid(long parentOid) {
      String parentOidStr = String.valueOf(parentOid);
      setParentOid(parentOidStr);
   }

   public String getAreaOccupier() {
      return attribut.getAreaOccupier();
   }

   public void setAreaOccupier(String areaOccupier) {
      attribut.setAreaOccupier(areaOccupier);
   }

   public String getCode() {
      return attribut.getCode();
   }

   public void setCode(String code) {
      attribut.setCode(code);
   }

   public String getEffictivity() {
      return attribut.getEffictivity();
   }

   public void setEffictivity(String effictivity) {
      attribut.setEffictivity(effictivity);
   }

   public String getLowerLevelName() {
      return attribut.getLowerLevelName();
   }

   public void setLowerLevelName(String lowerLevelName) {
      attribut.setLowerLevelName(lowerLevelName);
   }

   public String getManufacturingLines() {
      return attribut.getManufacturingLines();
   }

   public void setManufacturingLines(String manufacturingLines) {
      attribut.setManufacturingLines(manufacturingLines);
   }

   public String getMemo() {
      return attribut.getMemo();
   }

   public void setMemo(String memo) {
      attribut.setMemo(memo);
   }

   public String getName() {
      return attribut.getName();
   }

   public void setName(String name) {
      attribut.setName(name);
   }

   public String getNumber() {
      return attribut.getNumber();
   }

   public void setNumber(String number) {
      attribut.setNumber(number);
   }

   public String getPositioning() {
      return attribut.getPositioning();
   }

   public void setPositioning(String positioning) {
      attribut.setPositioning(positioning);
   }

   public int getQuantity() {
      return attribut.getQuantity();
   }

   public void setQuantity(int quantity) {
      attribut.setQuantity(quantity);
   }

   public String getUseDepartment() {
      return attribut.getUseDepartment();
   }

   public void setUseDepartment(String useDepartment) {
      attribut.setUseDepartment(useDepartment);
   }

   public int getChildLevel() {
      return childLevel;
   }

   public void setChildLevel(int childLevel) {
      this.childLevel = childLevel;
   }

   public CmPartAttributNode getFromNode() {
      return fromNode;
   }

   public void setFromNode(CmPartAttributNode fromNode) {
      this.fromNode = fromNode;
   }

   public Vector<CmPartAttributNode> getChildList() {
      return childList;
   }

   public void addChildToList(CmPartAttributNode childNode) {
      if (childNode != null)
         this.childList.add(childNode);
   }

   public CmAttributRecord getAttribut() {
      return attribut;
   }

   public void setAttribut(CmAttributRecord attribut) {
      this.attribut = attribut;
   }

   public long getLowerLevelOid() {
      return lowerLevelOid;
   }

   public void setLowerLevelOid(long lowerLevelOid) {
      this.lowerLevelOid = lowerLevelOid;
   }

   public int getSequenceNumber() {
      return sequenceNumber;
   }

   public void setSequenceNumber(int sequenceNumber) {
      this.sequenceNumber = sequenceNumber;
   }

   public CmXML toXML() {
      CmXML xml = new CmXML(XMLTITLE);
      long oid = 0;
      if (this.getPart() != null && this.getPart().getOid() > 0)
         oid = this.getPart().getOid();
      xml.set("oid", oid);
      CmPartAttributNode root = (CmPartAttributNode) this.getRoot();
      long parentOid = 0;
      if (root != null && root.getPart() != null && root.getPart().getOid() > 0)
         parentOid = root.getPart().getOid();
      xml.set("parentOid", parentOid);

      xml.set("lowerLevelOid", lowerLevelOid);
      xml.set("childLevel", childLevel);
      xml.set("sequenceNumber", sequenceNumber);
      if (fromNode != null)
         xml.set("fromSequenceNumber", fromNode.getSequenceNumber());
      CmXML attributXML = attribut.write2XML();
      xml.append(attributXML);

      Enumeration<CmPartAttributNode> children = this.children();
      while (children.hasMoreElements()) {
         CmPartAttributNode child = children.nextElement();
         CmXML childXML = child.toXML();
         xml.append(childXML);
      }
      return xml;
   }

   public static CmPartAttributNode load4XML(CmTreeNode currNode, CmXML xml) {
      CmPartAttributNode pan = new CmPartAttributNode(currNode.getUserObject());
      long lowerLevelOid = Long.valueOf(xml.attrval("lowerLevelOid"));
      if (lowerLevelOid > 0) {
         pan.setLowerLevelOid(lowerLevelOid);
      }
      int childLevel = Integer.valueOf(xml.attrval("childLevel"));
      pan.setChildLevel(childLevel);
      int sequenceNumber = Integer.valueOf(xml.attrval("sequenceNumber"));
      pan.setSequenceNumber(sequenceNumber);

      CmXML attributXML = xml.loc("." + CmAttributRecord.XMLTITLE);
      if (attributXML != null) {
         CmAttributRecord record = CmAttributRecord.load4XML(attributXML);
         if (record != null) {
            pan.setAttribut(record);
         }
      }
      CmXML childNodeXML = xml.loc("." + XMLTITLE);
      
      loadChildNode4XML(pan, childNodeXML);

      return pan;
   }

   private static void loadChildNode4XML(CmPartAttributNode parentNode, CmXML xml) {
      if (xml == null || !XMLTITLE.equals(xml.getName()))
         return;
      CmPartAttributNode childNode = CmPartAttributNode.load4XML(parentNode, xml);
      parentNode.add(childNode);
      loadChildNode4XML(parentNode, xml.nextElem());

   }
}
