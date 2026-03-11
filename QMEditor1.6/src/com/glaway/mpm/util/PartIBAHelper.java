package com.glaway.mpm.util;



import java.util.HashMap;
import java.util.Locale;

import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.ReferenceValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.util.WTContext;

public class PartIBAHelper {


public HashMap ibaContainer = null;

public PartIBAHelper(IBAHolder holder) {
   ibaContainer = new HashMap();
   if (holder != null)
      getIBAValuesLite(holder, ibaContainer);
}

public String getIBAValue(String ibaName) {
   return (String) ibaContainer.get(ibaName);
}


public static HashMap getIBAValuesLite(IBAHolder ibaHolder, HashMap ibaMap) {
  if (ibaMap == null)
     ibaMap = new HashMap();
  else
     ibaMap.clear();

  Locale locale = WTContext.getContext().getLocale();
  DefaultAttributeContainer dac = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
  if (dac == null) {
     try {
        ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
        dac = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
     } catch (Exception e) {
        e.printStackTrace();
        return ibaMap;
     }
  }

  AbstractValueView[] avv = null;
  if (dac == null || (avv = dac.getAttributeValues()) == null)
     return ibaMap;

  String name = null;
  String value = null;
  for (int i = 0; i < avv.length; i++) {
     name = avv[i].getDefinition().getName();
     if (avv[i] instanceof ReferenceValueDefaultView) {
        value = ((ReferenceValueDefaultView) avv[i]).getLiteIBAReferenceable().getIBAReferenceableDisplayString();
     } else
        value = avv[i].getLocalizedDisplayString(locale);
     ibaMap.put(name, value);
  }

  return ibaMap;
}


}