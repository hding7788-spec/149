package com.glaway.mpm.visual.view.tree;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import com.glaway.mpm.visual.bean.VaAttributRecord;
import com.glaway.mpm.visual.util.VaXML;


/**
 * <br>Created on 2012-10-29
 * @author chenyunlong
 * <br>
 *数据对象，在编辑属性中用于封装属性值
 *这是在treeTable中会使用的数据类
 */
public class VaPartAttributNode extends VaTreeNode implements Serializable {
//   private static CmLogger log=CmLogger.getLogger(CmPartAttributNode.class);
   public static String               XMLTITLE         = "VaPartAttributNode";

   public static final int            LEVEL_0          = 0;
   public static final int            LEVEL_1          = 1;
   public static final int            LEVEL_2          = 2;

   private static final long          serialVersionUID = 6753795667998346923L;

   private long                       lowerLevelOid    = 0;
   private VaAttributRecord           attribut         = new VaAttributRecord();
   //逻辑处理属性
   private int                        childLevel       = 0;                               //当前节点所处级别
   private VaPartAttributNode         fromNode;
   private int                        sequenceNumber;                                     //自己是父节点的第几个子节点
   private Vector<VaPartAttributNode> childList        = new Vector<VaPartAttributNode>();

   public VaPartAttributNode(Object useObject) {
      super(useObject);
      if (this.getPart() != null) {
         this.setNumber(this.getPart().getNumber());
         this.setName(this.getPart().getName());
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

   public VaPartAttributNode getFromNode() {
      return fromNode;
   }

   public void setFromNode(VaPartAttributNode fromNode) {
      this.fromNode = fromNode;
   }

   public Vector<VaPartAttributNode> getChildList() {
      return childList;
   }

   public void addChildToList(VaPartAttributNode childNode) {
      if (childNode != null)
         this.childList.add(childNode);
   }

   public VaAttributRecord getAttribut() {
      return attribut;
   }

   public void setAttribut(VaAttributRecord attribut) {
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

   public VaXML toXML() {
      VaXML xml = new VaXML(XMLTITLE);
      long oid = 0;
      if (this.getPart() != null && this.getPart().getOid() > 0)
         oid = this.getPart().getOid();
      xml.set("oid", oid);
      VaPartAttributNode root = (VaPartAttributNode) this.getRoot();
      long parentOid = 0;
      if (root != null && root.getPart() != null && root.getPart().getOid() > 0)
         parentOid = root.getPart().getOid();
      xml.set("parentOid", parentOid);

      xml.set("lowerLevelOid", lowerLevelOid);
      xml.set("childLevel", childLevel);
      xml.set("sequenceNumber", sequenceNumber);
      if (fromNode != null)
         xml.set("fromSequenceNumber", fromNode.getSequenceNumber());
      VaXML attributXML = attribut.write2XML();
      xml.append(attributXML);

      Enumeration<VaPartAttributNode> children = this.children();
      while (children.hasMoreElements()) {
         VaPartAttributNode child = children.nextElement();
         VaXML childXML = child.toXML();
         xml.append(childXML);
      }
      return xml;
   }

   public static VaPartAttributNode load4XML(VaTreeNode currNode, VaXML xml) {
      VaPartAttributNode pan = new VaPartAttributNode(currNode.getUserObject());
      long lowerLevelOid = Long.valueOf(xml.attrval("lowerLevelOid"));
      if (lowerLevelOid > 0) {
         pan.setLowerLevelOid(lowerLevelOid);
      }
      int childLevel = Integer.valueOf(xml.attrval("childLevel"));
      pan.setChildLevel(childLevel);
      int sequenceNumber = Integer.valueOf(xml.attrval("sequenceNumber"));
      pan.setSequenceNumber(sequenceNumber);

      VaXML attributXML = xml.loc("." + VaAttributRecord.XMLTITLE);
      if (attributXML != null) {
         VaAttributRecord record = VaAttributRecord.load4XML(attributXML);
         if (record != null) {
            pan.setAttribut(record);
         }
      }
      VaXML childNodeXML = xml.loc("." + XMLTITLE);
      
      loadChildNode4XML(pan, childNodeXML);

      return pan;
   }

   private static void loadChildNode4XML(VaPartAttributNode parentNode, VaXML xml) {
      if (xml == null || !XMLTITLE.equals(xml.getName()))
         return;
      VaPartAttributNode childNode = VaPartAttributNode.load4XML(parentNode, xml);
      parentNode.add(childNode);
      loadChildNode4XML(parentNode, xml.nextElem());

   }
}
