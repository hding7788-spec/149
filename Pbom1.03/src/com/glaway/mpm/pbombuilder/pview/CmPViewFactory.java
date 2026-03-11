package com.glaway.mpm.pbombuilder.pview;

import java.util.Hashtable;
import java.util.Iterator;

public class CmPViewFactory {
   private static Hashtable<String, CmPViewImpl> htPViewImpl   = new Hashtable<String, CmPViewImpl>();
   public static final String                    PV_NAME_MBOM  = "PViewClient_MBOM";
   public static final String PV_NAME_PBOM = "PViewClient_PBOM";
//   public static final String                    PV_NAME_AO    = "PViewClient_AO";
//   public static final String                    PV_NAME_AO_TL = "PViewClient_AO_TL";
//   public static final String                    PV_NAME_SPS   = "PViewClient_SPS";

   public static CmPViewImpl getPViewImpl4MBOM() {
      return getPViewImpl(PV_NAME_MBOM);
   }
   public static CmPViewImpl getPviewImpl4PBOM() {
	   return getPViewImpl(PV_NAME_PBOM);
   }
//   public static CmPViewImpl getPViewImpl4AO() {
//      return getPViewImpl(PV_NAME_AO);
//   }
//
//   public static CmPViewImpl getPViewImpl4AO_TL() {
//      return getPViewImpl(PV_NAME_AO_TL);
//   }
//
//   public static CmPViewImpl getPViewImpl4SPSBom() {
//      return getPViewImpl(PV_NAME_SPS);
//   }

   public static synchronized CmPViewImpl getPViewImpl(String name) {
      CmPViewImpl ret = null;

      if (name != null) {
         ret = htPViewImpl.get(name);
         if (ret == null && CmPViewImpl.isPviewInitialized()) {
            ret = new CmPViewImpl(name);
            htPViewImpl.put(name, ret);
         }
      }

      return ret;
   }

   public static synchronized void shutdown() {
      Iterator<CmPViewImpl> iter = htPViewImpl.values().iterator();

      while (iter.hasNext()) {
         CmPViewImpl item = iter.next();
         iter.remove();
         item.shutdown();
      }
   }

   public static synchronized void shutdown(String name) {
      if (name == null)
         shutdown();
      else {
         CmPViewImpl pviewImpl = htPViewImpl.get(name);
         if (pviewImpl != null) {
            htPViewImpl.remove(name);
            pviewImpl.shutdown();
         }
      }
   }

}
