package com.glaway.mpm.visual.control;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;

import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.visual.bean.VaPartWithOcc;
import com.glaway.mpm.visual.server.VaPartStructureUtilSvr;
import com.ptc.core.meta.common.TypeIdentifier;


public class VaPartStructureUtil {
   private static final String SERVER_CLASS = VaPartStructureUtilSvr.class.getName();
//   private static CmPartStructureUtilSvr cpsus = new CmPartStructureUtilSvr();
//   public static CmLightPartCBTreeNode buildStructureStopOn(WTPart parent, TypeIdentifier stopType) throws RemoteException, InvocationTargetException {
//      String method = "buildStructureStopOn";
//      Class[] types = {WTPart.class, TypeIdentifier.class};
//      Object[] args = {parent, stopType};
//
//      return (CmLightPartCBTreeNode) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
//   }

   public static VaPartWithOcc buildStructure(WTPart parent) throws RemoteException, InvocationTargetException {
      String method = "buildStructure";
      Class[] types = {WTPart.class};
      Object[] args = {parent};

      
//      return VaPartStructureUtilSvr.buildStructure(parent);
      return (VaPartWithOcc) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
   }

   @SuppressWarnings("unchecked")
   public static HashMap<Long, URL> getPViewURLHashMap(Collection oidKeys, String extension) throws RemoteException,
      InvocationTargetException {
      String method = "getPViewURLHashMap";
      Class[] types = {Collection.class, String.class};
      Object[] args = {oidKeys, extension};
      HashMap<Long, URL> hm = null;
//      try {
//    	  hm = (HashMap<Long, URL>)VaPartStructureUtilSvr.getPViewURLHashMap(oidKeys, extension);
//	} catch (WTException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	return hm;
      return (HashMap<Long, URL>) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
   }
}
