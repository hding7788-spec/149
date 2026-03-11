package com.glaway.mpm.pbombuilder.util;

import java.io.Serializable;


public abstract class CmInstanceData implements Serializable {
   private static final long serialVersionUID = 3683083034767594158L;

   public static final String PRE_MASTER = "master:";
   public static final String PRE_PART = "part:";
   public static final String PRE_USE_LINK = "ulink:";
   public static final String PRE_USE_OCC = "occ:";

   public static final int TYPE_INSTANCE_CUSTOM = 0;
   public static final int TYPE_INSTANCE_CI = 1;
   public static final int TYPE_INSTANCE_LO = 2;
   public static final int TYPE_INSTANCE_PART = 3;

   private int type = TYPE_INSTANCE_CUSTOM;

   private long partId = 0L;
   private long masterId = 0L;
   private long linkId = 0L;
   private String occId;
   private long occIdentifierId = 0L;

   public CmInstanceData() {
   }

   public static CmInstanceData newCmInstanceData(CmXML xml) {
      CmInstanceData ret = null;

      if (xml != null) {
         int type = xml.attrvalnum("type");
         if (type == TYPE_INSTANCE_PART)
            ret = new CmInstanceData4CI();
         else if (type == TYPE_INSTANCE_LO)
            ret = new CmInstanceData4LO();
         else if (type == TYPE_INSTANCE_PART)
            ret = new CmInstanceData4Part();
         else
            ret = new CmInstanceData4Custom(xml.attrval("identifier"));

         ret.setPartId(xml.attrvalnum("partId"));
         ret.setMasterId(xml.attrvalnum("masterId"));
         ret.setLinkId(xml.attrvalnum("linkId"));
         ret.setOccId(xml.attrvalnum("occId")+"");
         ret.setOccIdentifierId(xml.attrvalnum("occIdentifierId"));
      }

      return ret;
   }

   public long getLinkId() {
      return linkId;
   }

   public long getMasterId() {
      return masterId;
   }

   public String getOccId() {
      return occId;
   }

   public long getOccIdentifierId() {
      return occIdentifierId;
   }

   public long getPartId() {
      return partId;
   }

   public void setLinkId(long linkId) {
      this.linkId = linkId;
   }

   public void setMasterId(long masterId) {
      this.masterId = masterId;
   }

   public void setOccId(String occId) {
      this.occId = occId;
   }

   public void setOccIdentifierId(long occIdentifierId) {
      this.occIdentifierId = occIdentifierId;
   }

   public void setPartId(long partId) {
      this.partId = partId;
   }

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public abstract String getIdentifier();

   public CmXML toXML() {
      CmXML ret = new CmXML("CmInstanceData");

      ret.set("type", this.type);
      ret.set("identifier", this.getIdentifier());
      ret.set("partId", this.partId);
      ret.set("masterId", this.masterId);
      ret.set("linkId", this.linkId);
      ret.set("occId", this.occId);
      ret.set("occIdentifierId", this.occIdentifierId);

      return ret;
   }

   public String toString() {
      return getIdentifier();
   }
}
