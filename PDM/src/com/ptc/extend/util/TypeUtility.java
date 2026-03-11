package com.ptc.extend.util;

import java.util.ArrayList;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.applicationcontext.implementation.DefaultServiceProvider;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.type.runtime.server.TypeModel;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;

public class TypeUtility
  implements RemoteAccess
{
  static String CLASSNAME = TypeUtility.class.getName();

  public static ArrayList getChildTypeIdentifiers(String type) { if (!RemoteMethodServer.ServerFlag) {
      String method = "getChildTypeIdentifiers";
      Class[] types = { String.class };
      Object[] vals = { type };
      try {
        return (ArrayList)RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, vals);
      } catch (Exception e) {
        Debug.info("getChildTypeIdentifiers() Exception:", e);
        e.printStackTrace();
        return new ArrayList();
      }
    }
    ArrayList list = new ArrayList();
    boolean checkAccess = SessionServerHelper.manager.setAccessEnforced(false);
    try {
      TypeModel typeModel = (TypeModel)DefaultServiceProvider.getService(TypeModel.class, null);
      TypeIdentifier ti = CoreMetaUtility.getTypeIdentifier(type);
      TypeIdentifier[] childs = typeModel.getDescendants(ti);
      for (int i = 0; i < childs.length; i++)
        list.add(childs[i]);
    }
    catch (WTException wet) {
      Debug.info("Service Exception.", wet);
      wet.printStackTrace();
    } finally {
      SessionServerHelper.manager.setAccessEnforced(checkAccess);
    }
    return list; }

  public static TypeIdentifier getChildTypeIdentifier(String parent, String child)
  {
    ArrayList list = getChildTypeIdentifiers(parent);
    for (int i = 0; i < list.size(); i++) {
      TypeIdentifier ti = (TypeIdentifier)list.get(i);
      if (ti.toExternalForm().endsWith("." + child))
        return ti;
    }
    Debug.info("Not found Parent Node [" + parent + "] has child node [" + child + "].");
    return null;
  }

  public static boolean isParentType(TypeIdentifier child, TypeIdentifier parent) {
    String childtype = child.toExternalForm();
    String parenttype = parent.toExternalForm();

    return childtype.startsWith(parenttype);
  }
}