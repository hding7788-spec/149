package com.glaway.mpm.pbombuilder.util;

import java.awt.Image;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.Typed;
import wt.util.WTException;
import com.glaway.mpm.pbombuilder.tree.CmLightType;
import com.ptc.core.meta.common.impl.WCTypeIdentifier;
import com.ptc.core.meta.type.mgmt.server.TypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionObjectLocator;

/**
 * <br>Created on 2012-10-18
 * @author chenyunlong
 */
public class CmTypeHelper implements Serializable, RemoteAccess {
	   private static final long               serialVersionUID  = -8146111691689373567L;

	   private static final String             CLASSNAME         = CmTypeHelper.class.getName();
//	   private static final CmLogger           logger            = CmLogger.getLogger(CmTypeHelper.class.getName());

	   public static final CmLightType         NOT_FOUND_TYPE    = new CmLightType(null, null, null);

	   private static Map<String, CmLightType> displayNamesCache = new HashMap<String, CmLightType>(20);
	   private static Map<String, CmLightType> extTypesCache     = new HashMap<String, CmLightType>(20);
	   private static HashMap<String, String>  typeNameIdMap     = null;

	   private static synchronized CmLightType getCachedLightType(String key, boolean fromDisplayName) {
	      if (key == null)
	         return NOT_FOUND_TYPE;
	      return fromDisplayName ? displayNamesCache.get(key) : extTypesCache.get(key);
	   }

	   public static CmLightType getLightType(WTObject obj) {
	      CmLightType ret = NOT_FOUND_TYPE;

	      if (obj instanceof Typed) {
	         try {
	            String extType = ClientTypedUtility.getExternalTypeIdentifier((Typed) obj);
	            if (extType != null) {
	               if (extType.startsWith("WCTYPE|"))
	                  extType = extType.substring("WCTYPE|".length());
	               ret = getLightType(extType, false);
	            }
	         } catch (Exception e) {
//	            logger.error(e);
	         }
	      }

	      return ret;
	   }

	   public static boolean isA(CmLightType typeA, CmLightType typeB) {
	      if (typeA == null || typeA == NOT_FOUND_TYPE)
	         return false;

	      return typeA.isA(typeB);
	   }

	   /**
	    * 按显示名称/扩展类型返回指定轻量级软类型对象，找不到时返回空对象（NOT_FOUND_TYPE）
	    * @param key
	    * @param fromDisplayName
	    * @return
	    */
	   public static CmLightType getLightType(String key, boolean fromDisplayName) {
	      CmLightType ret = getCachedLightType(key, fromDisplayName);

	      if (ret == null) {
	         if (!RemoteMethodServer.ServerFlag) {
	            String method = "getLightType";
	            Class<?>[] types = {String.class, Boolean.TYPE};
	            Object[] args = {key, new Boolean(fromDisplayName)};

	            try {
	               ret = (CmLightType) RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, args);
	            } catch (Throwable e) {
//	               logger.error(e);
	            }

	            if (ret != null) {
	               displayNamesCache.put(ret.getDisplayName(), ret);
	               extTypesCache.put(ret.getExtType(), ret);
	            } else {
	               ret = fromDisplayName ? displayNamesCache.put(key, NOT_FOUND_TYPE) : extTypesCache.put(key, NOT_FOUND_TYPE);
	            }
	            return ret;
	         }

	         if (typeNameIdMap == null) {
	            synchronized (CmTypeHelper.class) {
	               if (typeNameIdMap == null) {
	                 try {
	                     SessionHelper.getPrincipal();
	                  } catch (WTException e) {
	                     // ignore, for check auth....
	                  }

	                  typeNameIdMap = new HashMap<String, String>(64);
	                  getSoftTypes(WTDocument.class.getName(), typeNameIdMap, true);
	                  getSoftTypes(WTPart.class.getName(), typeNameIdMap, true);
	                  getSoftTypes(EPMDocument.class.getName(), typeNameIdMap, true);
	                  //loaded = true;
	               }
	            }
	         }

	         if (key == null || key.equals(""))
	            key = "wt.part.WTPart";

	         try {
	            String extType = fromDisplayName ? typeNameIdMap.get(key) : key;
	            if (extType == null)
	               return ret;

	            if (extType.startsWith("WCTYPE|"))
	               extType = extType.substring("WCTYPE|".length());

	            TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(extType);
	            String iconPath = ClientTypedUtility.getLocalizedTypeIcon(tdr, Locale.SIMPLIFIED_CHINESE);
	            String displayIdentity = ClientTypedUtility.getLocalizedTypeName(tdr, Locale.SIMPLIFIED_CHINESE);

	            ret = new CmLightType(extType, displayIdentity, iconPath);
	         } catch (Exception e) {
//	            logger.error(e);
	         }

	         if (ret != null) {
	            displayNamesCache.put(ret.getDisplayName(), ret);
	            extTypesCache.put(ret.getExtType(), ret);
	         } else {
	            ret = fromDisplayName ? extTypesCache.put(key, NOT_FOUND_TYPE) : displayNamesCache.put(key, NOT_FOUND_TYPE);
	         }
	      }

	      return ret;
	   }

	   public static String getExtType(String displayName) {

	      /**
	       * Change: return getLightType(displayName, true).getExtType();
	       * Cause:Is a null object of the result,when the key equal to "SCI"
	       */
	      CmLightType lightType = getLightType(displayName, true);
	      return lightType != null ? lightType.getExtType() : null;

	   }

	   public static Image getTypeIcon(String extType) {
	      return getLightType(extType, false).getIconImage();
	   }

	   public static String getDisplayName(String extType) {
	      return getLightType(extType, false).getDisplayName();
	   }

	   public static String getExtType(WTObject obj) {
	      return getLightType(obj).getExtType();
	   }

	   public static String getDisplayName(WTObject obj) {
	      return getLightType(obj).getDisplayName();
	   }

	   public static Image getTypeIcon(WTObject obj) {
	      return getLightType(obj).getIconImage();
	   }

	   /**
	    * 获取指定softtype类型的下级softtype清单
	    *
	    * @param rootType    指定的softtype名, 如wt.doc.WTDocument|com.ptc.cacgg.gywj
	    * @param typeMap       下级softtype类型的类型名称Map, key为显示名称,
	    *                      如: 工艺文件=>wt.doc.WTDocument|com.ptc.cacgg.gywj
	    * @param includeAll    是否包含所有，即其下级的下级
	    */
	   private static void getSoftTypes(String rootType, HashMap<String, String> typeMap, boolean includeAll) {
	      Vector<?> v = (Vector<?>) new WTTypeDefinitionObjectLocator().locate(WTTypeDefinitionObjectLocator.LATEST);

	      int targetLevel = 0;
	      if (rootType != null) {
	         // 去除不需要的类型名称头
	         String typeHead = WCTypeIdentifier.PROTOCOL + WCTypeIdentifier.HIERARCHY_SEPARATOR;
	         if (rootType.startsWith(typeHead))
	            rootType = rootType.substring(typeHead.length());

	         StringTokenizer st = new StringTokenizer(rootType, WCTypeIdentifier.HIERARCHY_SEPARATOR);
	         targetLevel = st.countTokens();
	      }

	      for (int i = 0; v != null && i < v.size(); i++) {
	         TypeDefinition ttd = (TypeDefinition) v.get(i);
	         if (ttd.isDeleted())
	            continue;

	         String typeLabel = ttd.getDisplayNameKey();
	         String typeName = ttd.getName();
	         int levels = 0;
	         while (true) {
	            try {
	               ttd = ttd.getParent();
	               if (ttd == null)
	                  break;

	               typeName = ttd.getName() + WCTypeIdentifier.HIERARCHY_SEPARATOR + typeName;
	               levels++;
	            } catch (Exception e) {
	               ttd = null;
	               typeName = null;
	               break;
	            }
	         }

	         if (typeName == null) {
	            continue;
	         }

	         if ((includeAll || levels == targetLevel) && (rootType == null || typeName.startsWith(rootType))) {
	            if (typeMap != null)
	               typeMap.put(typeLabel, typeName);
	         }
	      }
	   }
	}
