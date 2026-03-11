package com.glaway.mpm.pbombuilder.impl;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Vector;

import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import com.glaway.mpm.pbombuilder.tree.CmLightPartCBTreeNode;
import com.glaway.mpm.pbombuilder.tree.CmLightType;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmPartStructureUtil;
import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;

/**
 * <br>Created on 2012-10-16
 * @author chenyunlong
 */
public class CmEBomCISearchImpl {
//   private static final CmLogger log = CmLogger.getLogger(CmEBomCISearchImpl.class);

   public static CmEBomCISearchImpl newCmEBomCISearchImpl() {
      CmEBomCISearchImpl ret = new CmEBomCISearchImpl();
      return ret;
   }

   private CmEBomCISearchImpl() {}

   @SuppressWarnings("unchecked")
   public Vector searchParts(CmLightType type, String number, String name) throws Exception {
      TypeIdentifier ti = null;
      try {
         ti = TypeHelper.getTypeIdentifier(type.getExtType());
      } catch (Exception e) {
         ti = null;
//         log.error(e);
      }

      Vector parts = CmSearchHelper.searchPart(ti, -1, number, name, null, null, null);

      int size = parts.size();
      Vector ret = new Vector(size);
      for (int i = 0; i < size; i++)
         ret.add(CmBizObjUtil.buildCmLightPartFromWTPart((WTPart) parts.get(i)));
      return ret;
   }
   
   public CmLightPartCBTreeNode buildStructureStopOn(long parentOid, TypeIdentifier stopType) throws RemoteException, InvocationTargetException {
	  RemoteMethodServer rms = RemoteMethodServer.getDefault();
	  rms.setUserName("wcadmin");
	  rms.setPassword("wcadmin");
	  WTPart parent = null;
	try {
		parent = (WTPart) CmSearchHelper.search(WTPart.class, 435107);
	} catch (Exception e) {
		e.printStackTrace();
	}      
      
      return CmPartStructureUtil.buildStructureStopOn(parent, stopType);
   }
}
