package com.glaway.mpm.visual.bean;

import java.util.Arrays;

import com.glaway.mpm.visual.util.VaXML;

public class VaInstanceIdentifier {
   public static final VaInstanceIdentifier EMPTY_INSTANCE_IDENTIFIER = new VaInstanceIdentifier();
   
   private VaInstanceData[] instancePath;
   
   public VaInstanceIdentifier() {
      this((VaInstanceData[]) null);
   }
   
   public VaInstanceIdentifier(VaInstanceData instanceData) {
      this(instanceData == null ? (VaInstanceData[]) null : new VaInstanceData[]{instanceData});
   }
   
   public VaInstanceIdentifier(VaInstanceData[] intancePath) {
      this.instancePath = intancePath;
      if (instancePath == null)
         this.instancePath = new VaInstanceData[0];
   }
   
   public boolean isEmpty() {
      return instancePath == null || instancePath.length == 0;
   }
   
   public VaInstanceData[] getInstancePath() {
      return instancePath;
   }
   
   public void add(VaInstanceData instanceData) {
      if (instanceData != null) {
         VaInstanceData[] oldInstancePath = instancePath;
         if (oldInstancePath == null || oldInstancePath.length == 0) 
            instancePath = new VaInstanceData[1];
         else {
            instancePath = new VaInstanceData[oldInstancePath.length + 1];
            System.arraycopy(oldInstancePath, 0, instancePath, 0, oldInstancePath.length);
         }
         
         instancePath[instancePath.length - 1] = instanceData;
      }
   }
   
   public static VaInstanceIdentifier makeCIHeaderIdentifier(VaInstanceIdentifier fatherInstanceIdentifier, VaInstanceData instanceData) {
      VaInstanceIdentifier ret = EMPTY_INSTANCE_IDENTIFIER;
      
      if (fatherInstanceIdentifier != null && !fatherInstanceIdentifier.isEmpty()) {
         ret = new VaInstanceIdentifier(fatherInstanceIdentifier.getInstancePath());
         ret.add(instanceData);
      } else if (instanceData != null && instanceData.getType() == VaInstanceData.TYPE_INSTANCE_CI) {
         ret = new VaInstanceIdentifier(instanceData);
      }
      
//      VaLogger.getLogger().debug("ret=", ret);
      
      return ret;
   }
   
   public VaXML toXML() {
      VaXML ret = new VaXML("VaInstanceIdentifier");
      ret.set("length", 0);

      if (!isEmpty()) {
         ret.set("length", instancePath.length);
         for (int i = 0; i < instancePath.length; i++) 
            ret.append(instancePath[i].toXML());         
      } 
      
      return ret;
   }
   
   public void loadFromXML(VaXML xml) {
      int length = xml == null ? -1 : xml.attrvalnum("length");
      if (length > 0) {
         instancePath = new VaInstanceData[length];
         VaXML childXML = xml.firstElem();
         for (int i = 0; i < length && childXML != null; i++) {
            instancePath[i] = VaInstanceData.newVaInstanceData(childXML);
            childXML = childXML.nextElem();
         }
      }
   }

   public String toString() {
      return "VaInstanceIdentifier [instancePath=" + Arrays.toString(instancePath) + "]";
   }
}
