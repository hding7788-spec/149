package com.glaway.mpm.pbombuilder.util;

import java.lang.reflect.Method;

import wt.method.RemoteMethodServer;

public final class CmLightweightServiceHelper {
   public static Object invoke(String methodName, String classname, Object inst, Class<?>[] types, Object[] values) throws Exception {
      if (inst != null)
         classname = inst.getClass().getName();
      
      if (RemoteMethodServer.ServerFlag) {
         Class<?> klass = Class.forName(classname);
         Method method = klass.getMethod(methodName, types);
         return method.invoke(inst, values);
      }
      
      return RemoteMethodServer.getDefault().invoke(methodName, classname, inst, types, values);
   }
}
