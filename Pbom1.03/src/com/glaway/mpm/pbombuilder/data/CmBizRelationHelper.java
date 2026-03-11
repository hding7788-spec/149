package com.glaway.mpm.pbombuilder.data;

import wt.doc.WTDocument;
import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.util.CmBizObjHelper;
import com.glaway.mpm.pbombuilder.util.CmLightweightServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;

/**
 * SAMC 业务对象与其相应的保存对象的关系
 * <br>Created on 2012-10-29
 * @author chenyunlong
 */
public class CmBizRelationHelper {
   private static final String   SERVER_CLASS = "ext.ideal.samc.jws.server.CmBizRelationSvrHelper";
//   private static final CmLogger logger       = CmLogger.getLogger();

   /**
    * 从AO组件WTPart上获取相应的WTDocument存储对象
    * @param aoPart
    * @return
    */
   public static WTDocument getSavedDoc4Part(String label, TypeIdentifier ti, WTPart aoPart, boolean createNonexistent) {
      label = label.toUpperCase();
      String number = label + aoPart.getNumber();
      String version = aoPart.getVersionIdentifier().getValue();

      WTDocument doc = null;
      try {
         doc = CmBizObjHelper.findDoc(number, version);
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (doc == null && createNonexistent) {
         doc = createSavedDoc4Part(label, ti, aoPart, true);
      }
      return doc;
   }

   public static WTDocument createSavedDoc4Part(String label, TypeIdentifier ti, WTPart aoPart, boolean buildDescibeLink) {
      String number = label + aoPart.getNumber();
      String name = aoPart.getName();

      String methodName = "createWTDocument";
      Class<?>[] types = {WTPart.class, TypeIdentifier.class, String.class, String.class};
      Object[] values = {aoPart, ti, number, name};

      WTDocument ret = null;

      try {
         ret = (WTDocument) CmLightweightServiceHelper.invoke(methodName, SERVER_CLASS, null, types, values);
         if (ret != null && buildDescibeLink) {
            methodName = "createDescribeLink";
            types = new Class[]{WTPart.class, WTDocument.class};
            values = new Object[]{aoPart, ret};

            CmLightweightServiceHelper.invoke(methodName, SERVER_CLASS, null, types, values);
         }
      } catch (Exception e) {
//         logger.error(e);
      }

      return ret;
   }
}
