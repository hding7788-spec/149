package com.glaway.mpm.pbombuilder.util;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.pbombuilder.data.CmPartWithOcc;
import com.glaway.mpm.pbombuilder.tree.CmLightPartCBTreeNode;
import com.ptc.core.meta.common.TypeIdentifier;

public class CmPartStructureUtil {
   private static final String SERVER_CLASS = "ext.ideal.samc.jws.server.CmPartStructureUtilSvr";

   public static CmLightPartCBTreeNode buildStructureStopOn(WTPart parent, TypeIdentifier stopType) throws RemoteException, InvocationTargetException {
      String method = "buildStructureStopOn";
      Class[] types = {WTPart.class, TypeIdentifier.class};
      Object[] args = {parent, stopType};

      return (CmLightPartCBTreeNode) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
   }

   public static CmPartWithOcc buildStructure(WTPart parent) throws RemoteException, InvocationTargetException {
      String method = "buildStructure";
      Class[] types = {WTPart.class};
      Object[] args = {parent};

      return CmPartStructureUtilSvr.buildStructure(parent);
   }

   @SuppressWarnings("unchecked")
   public static HashMap<Long, URL> getPViewURLHashMap(Collection oidKeys, String extension) throws RemoteException,
      InvocationTargetException, WTException {
      String method = "getPViewURLHashMap";
      Class[] types = {Collection.class, String.class};
      Object[] args = {oidKeys, extension};

      return (HashMap<Long, URL>)CmPartStructureUtilSvr.getPViewURLHashMap(oidKeys, extension);
   }
}
