package com.glaway.mpm.visual.server;

import java.io.Serializable;
import java.util.Locale;

import wt.method.RemoteAccess;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;

import com.glaway.mpm.visual.bean.VaLightType;
import com.glaway.mpm.visual.log.VaLogger;
import com.ptc.core.meta.common.Identifier;
import com.ptc.core.meta.common.impl.LogicalIdentifierFactory;



public class VaTypeSvrHelper implements Serializable, RemoteAccess {
   private static final long     serialVersionUID = -7119968481060883769L;
   private static final VaLogger log              = VaLogger.getLogger(VaTypeSvrHelper.class);

   public static VaLightType getLightType(String typeName, boolean fromLogicType) {
      VaLightType ret = null;
      if (typeName == null || typeName.equals(""))
         typeName = "wt.part.WTPart";

      log.debug("enter -> typeName=" + typeName, " & fromLogicType=", fromLogicType);
      try {
         String extType = typeName;
         if (fromLogicType) {
            LogicalIdentifierFactory idF = LogicalIdentifierFactory.getInstance();
            if (!LogicalIdentifierFactory.isValidLogicalIdentifier(typeName))
               return ret;
            Identifier idf = idF.get(typeName);
            if (idf == null)
               return ret;

            extType = idf.toExternalForm();
         }
         if (extType.startsWith("WCTYPE|"))
            extType = extType.substring("WCTYPE|".length());

         TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(extType);
         String iconPath = ClientTypedUtility.getLocalizedTypeIcon(tdr, Locale.SIMPLIFIED_CHINESE);
         String displayIdentity = ClientTypedUtility.getLocalizedTypeName(tdr, Locale.SIMPLIFIED_CHINESE);

         ret = new VaLightType(extType, displayIdentity, iconPath);
         log.debug("exit -> got = " + ret + " & iconPath=" + iconPath);
      } catch (Exception e) {
         log.error(e);
      }

      return ret;
   }
}
