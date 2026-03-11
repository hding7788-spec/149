package com.glaway.mpm.pbombuilder.util;

import java.io.Serializable;
import java.util.Arrays;

public class CmInstanceIdentifier implements Serializable {
   public static final CmInstanceIdentifier EMPTY_INSTANCE_IDENTIFIER = new CmInstanceIdentifier();

   private CmInstanceData[] instancePath;

   public CmInstanceIdentifier() {
      this((CmInstanceData[]) null);
   }

   public CmInstanceIdentifier(CmInstanceData instanceData) {
      this(instanceData == null ? (CmInstanceData[]) null : new CmInstanceData[]{instanceData});
   }

   public CmInstanceIdentifier(CmInstanceData[] intancePath) {
      this.instancePath = intancePath;
      if (instancePath == null)
         this.instancePath = new CmInstanceData[0];
   }

   public boolean isEmpty() {
      return instancePath == null || instancePath.length == 0;
   }

   public CmInstanceData[] getInstancePath() {
      return instancePath;
   }

   public void add(CmInstanceData instanceData) {
      if (instanceData != null) {
         CmInstanceData[] oldInstancePath = instancePath;
         if (oldInstancePath == null || oldInstancePath.length == 0)
            instancePath = new CmInstanceData[1];
         else {
            instancePath = new CmInstanceData[oldInstancePath.length + 1];
            System.arraycopy(oldInstancePath, 0, instancePath, 0, oldInstancePath.length);
         }

         instancePath[instancePath.length - 1] = instanceData;
      }
   }

   public static CmInstanceIdentifier makeCIHeaderIdentifier(CmInstanceIdentifier fatherInstanceIdentifier, CmInstanceData instanceData) {
      CmInstanceIdentifier ret = EMPTY_INSTANCE_IDENTIFIER;

      if (fatherInstanceIdentifier != null && !fatherInstanceIdentifier.isEmpty()) {
         ret = new CmInstanceIdentifier(fatherInstanceIdentifier.getInstancePath());
         ret.add(instanceData);
      } else if (instanceData != null && instanceData.getType() == CmInstanceData.TYPE_INSTANCE_CI) {
         ret = new CmInstanceIdentifier(instanceData);
      }

//      CmLogger.getLogger().debug("ret=", ret);

      return ret;
   }

   public CmXML toXML() {
      CmXML ret = new CmXML("CmInstanceIdentifier");
      ret.set("length", 0);

      if (!isEmpty()) {
         ret.set("length", instancePath.length);
         for (int i = 0; i < instancePath.length; i++)
            ret.append(instancePath[i].toXML());
      }

      return ret;
   }

   public void loadFromXML(CmXML xml) {
      int length = xml == null ? -1 : xml.attrvalnum("length");
      if (length > 0) {
         instancePath = new CmInstanceData[length];
         CmXML childXML = xml.firstElem();
         for (int i = 0; i < length && childXML != null; i++) {
            instancePath[i] = CmInstanceData.newCmInstanceData(childXML);
            childXML = childXML.nextElem();
         }
      }
   }

   public String toString() {
      return "CmInstanceIdentifier [instancePath=" + Arrays.toString(instancePath) + "]";
   }
}
