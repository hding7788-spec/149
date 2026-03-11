package com.glaway.mpm.visual.view.pview;

import java.util.Hashtable;
import java.util.Iterator;

import com.glaway.mpm.visual.log.VaLogger;

public class VaPViewFactory {
	private static VaLogger log = VaLogger.getLogger();
   private static Hashtable<String, VaPViewImpl> htPViewImpl   = new Hashtable<String, VaPViewImpl>();
   public static final String                    PV_NAME_MBOM  = "PViewClient_MBOM";
   public static final String                    PV_NAME_DP    = "PViewClient_DP";
   public static final String                    PV_NAME_VP    = "PViewClient_VP";

   public static VaPViewImpl getPViewImpl4MBOM() {
      return getPViewImpl(PV_NAME_MBOM);
   }
   public static VaPViewImpl getPViewImpl4DP() {
	   return getPViewImpl(PV_NAME_DP);
   }
   public static VaPViewImpl getPViewImpl4VP() {
      return getPViewImpl(PV_NAME_VP);
   }

   public static synchronized VaPViewImpl getPViewImpl(String name) {
      VaPViewImpl ret = null;
      //log.debug("getPViewImpl...........");
      if (name != null) {
         ret = htPViewImpl.get(name);
         if (ret == null && VaPViewImpl.isPviewInitialized()) {
        	//log.debug("new VaPViewImpl..........."+name);
            ret = new VaPViewImpl(name);
            htPViewImpl.put(name, ret);
         }
      }

      return ret;
   }

   public static synchronized void shutdown() {
      Iterator<VaPViewImpl> iter = htPViewImpl.values().iterator();

      while (iter.hasNext()) {
         VaPViewImpl item = iter.next();
         iter.remove();
         item.shutdown();
      }
   }

   public static synchronized void shutdown(String name) {
      if (name == null)
         shutdown();
      else {
         VaPViewImpl pviewImpl = htPViewImpl.get(name);
         if (pviewImpl != null) {
            htPViewImpl.remove(name);
            pviewImpl.shutdown();
            log.debug("PView shutdown ..."+name);
         }
      }
   }

}
