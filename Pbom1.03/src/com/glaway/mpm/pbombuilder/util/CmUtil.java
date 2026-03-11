package com.glaway.mpm.pbombuilder.util;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.httpgw.URLFactory;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;

import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmInputStreamData;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmLightType;
import com.glaway.mpm.pbombuilder.tree.CmPartMaster;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;

/**
 * <br>
 * Created on 2012-11-23
 *
 * @author chenyunlong
 */
public class CmUtil {
   public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static final CmLogger log = CmLogger.getLogger(CmUtil.class.getName());
    private static final String          SERVER_CLASS     = "com.glaway.mpm.pbombuilder.util.CmUtilSvr";
   public static Image getImageFromServer(String fileName) {
	   return WTContext.getContext().getImage("com/glaway/mpm/images/" + fileName);
   }

   public static String emptyIfNull(String value) {
      return value == null ? "" : value;
   }

   public static final boolean isEmpty(Object o) {
      if (o == null || "".equals(o))
         return true;
      return false;
   }

   public static int length(Object[] objs) {
      return objs == null ? 0 : objs.length;
   }

   public static CmInputStreamData downloadAuthURLData(String strUrl, boolean forceAccess) throws InvocationTargetException, WTException,
      RemoteException, MalformedURLException, WTPropertyVetoException {
      String method = "downloadAuthURLData";
      Class[] types = {String.class, Boolean.TYPE};
      Object[] objs = {strUrl, forceAccess};

//      return CmUtilSvr.downloadAuthURLData(strUrl, forceAccess);
      return (CmInputStreamData) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, objs);
   }

   public static void openURI(String urlStr) {
      try {
         if (urlStr.matches("^[a-zA-Z]:.*$")) {
            Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL explorer.exe /select," + urlStr);//打开选中
         } else if (urlStr.matches("^http://.*$")) {
            Runtime.getRuntime().exec("cmd.exe /c start " + urlStr);
         }
      } catch (Exception ex) {
         JOptionPane.showMessageDialog(null, "无法打开链接", "打开失败", JOptionPane.ERROR_MESSAGE);
      }
   }

   public static Map<String, String> resolveDescription(String descriptionStr) {
      Map<String, String> ret = new HashMap<String, String>();
      String nodeType_nodeId = CmUtil.getSymbolNODETYPE_NODEID();
      String nodeId_type = CmUtil.getSymbolNODEID_TYPE();
      if (descriptionStr != null && descriptionStr.trim().length() > 0) {
         int nodetype_nodeIdCount = descriptionStr.indexOf(nodeType_nodeId);
         int nodeId_typeCont = descriptionStr.indexOf(nodeId_type);

         if (nodetype_nodeIdCount > 0 && nodeId_typeCont > 0) {
            String nodeType = descriptionStr.substring(0, nodetype_nodeIdCount);
            String nodeId = descriptionStr.substring(nodetype_nodeIdCount + 1, nodeId_typeCont);
            String type = descriptionStr.substring(nodeId_typeCont + 1, descriptionStr.length());
            if (getSymbolCP().equals(nodeType)) {
               ret.put(getSymbolCP(), nodeType);
               ret.put(getCPID(), nodeId);
               ret.put(getSymbolICON(), type);
            }
         }
      }
      return ret;
   }

   public static final String getSymbolCP() {
      return "CP";
   }

   public static final String getCPID() {
      return "CPID";
   }

   public static final String getSymbolICON() {
      return "ICON";
   }

   public static final String getSymbolNODETYPE_NODEID() {
      return ":";
   }

   public static final String getSymbolNODEID_TYPE() {
      return "-";
   }

//   @SuppressWarnings("unchecked")
//   public static CmTreeNode getConsultNode(CmAOTreeNode node) {
//      if (node == null)
//         return null;
//      CmTreeNode consultNode = null;
//      Enumeration<CmTreeNode> mNodes = node.children();
//      while (mNodes.hasMoreElements()) {
//         CmTreeNode node2 = mNodes.nextElement();
//         CmLightPart part = node2.getPart();
//         if ("参照件".equals(part.getPartName())) {
//            consultNode = node2;
//         }
//      }
//      return consultNode;
//   }

   public static void setLocale(Locale locale) {
      try {
         RemoteMethodServer.getDefault().invoke("setLocale", SERVER_CLASS, null, new Class[]{Locale.class}, new Object[]{locale});
      } catch (RemoteException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      }
   }

   /**
    * @param string
    * @return
    */
   public static String[] getDICPropertyAsArray(String propName) {
      //      String prop = CmPropsFile.getProperty(propName);
      String prop = CmSettings.getSection(CmSettings.SECTION_CAD).get(propName);
      if (prop == null)
         return (new String[]{});
      StringTokenizer st = new StringTokenizer(prop, ",");
      String[] res = new String[st.countTokens()];
      for (int i = 0; st.hasMoreTokens(); i++)
         res[i] = st.nextToken();
      return res;
   }

   public static String downloadAuthURLData(String urlStr, long checkVal) {
      try {
         File td = CmContext.getTempDir();
         URL url = new URL(urlStr);
         String fname = url.getPath();
         fname = fname.substring(fname.lastIndexOf("/") + 1);
         File f = new File(td, fname);

         boolean isSame = f.exists();
         // ML fix for iterations not working
         if (isSame)
            f.delete();
         isSame = false;

         //         if(isSame){
         //            DataInputStream dis = new DataInputStream(new FileInputStream(new File( td, fname+".chk")));
         //            long checksum = dis.readLong();
         //            dis.close();
         //            isSame = checksum==checkVal;
         //         }

         if (!isSame && f.length() != checkVal) { //more secure would be checksum comparison

            URLConnection urlconnection = url.openConnection();
            urlconnection.setRequestProperty("authorization", CmContext.getCred());
            urlconnection.setRequestProperty("Accept-Encoding", "gzip");
            String enc = urlconnection.getHeaderField("content-encoding");
            InputStream i = null;
            if (enc == null || enc.indexOf("gzip") == -1)
               i = urlconnection.getInputStream();
            else
               i = new GZIPInputStream(urlconnection.getInputStream());
            byte buf[] = new byte[8192];
            FileOutputStream fos = new FileOutputStream(f);
            //          Adler32 checksum = new Adler32();
            //CRC32 checksum = new CRC32();
            int n;
            while ((n = i.read(buf)) >= 0) {
               //               checksum.update(buf, 0, n);
               fos.write(buf, 0, n);
            }
            fos.flush();
            fos.close();
            //            DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File( td, fname+".chk")));
            //            dos.writeLong(checksum.getValue());
            //            dos.close();
         }
         return f.getAbsolutePath();
      } catch (IOException ex) {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      }
      return null;
   }

   public static String getAbsPathByNodeName(CmTreeNode aNode) {

      Object[] thePath = aNode.getPath();
      String theResult = "";
      for (Object n : thePath)
         if (((CmTreeNode) n).getUserObject() != null)
            theResult = theResult + "/" + ((CmTreeNode) n).getUserObject();
      if (theResult.length() != 0)
         theResult = theResult.substring(1);
      //log.debug("DICUTIL --> getPathString theResult"+theResult);
      return theResult;
   }

   /**
    *AO组件中所包含的所有零件叶节点的DM有效性范围要包含所设置的AO组件有效性，否则提示AO组件有效性设置错误
    */
   public static boolean checkNodeEff(CmTreeNode node, StringBuffer sb) {
      boolean is = true;
      CmLightPart aoLightPart = node.getPart();
      if (aoLightPart == null) {
//         log.debug("AO组件节点:" + node + "的CmLightPart为空");
         return false;
      }
      WTPart aoPart = CmBizObjUtil.getWTPartFromLightPart(aoLightPart);
      if (aoPart == null) {
//         log.debug("通过AO组件节点:" + node + "找不到对应的WTPart");
         return false;
      }

//      EffContext effContext_ao = CmSamcEffUtil.getMBomEffContext(aoPart);
//      if (effContext_ao == null) {
////         log.debug("  WTPart:" + aoPart.getNumber() + " 对应的有效性上下文为空");
//         return false;
//      }

//      CmEffValue effValue_ao = CmSamcEffUtil.getEff(aoPart, effContext_ao);
//      if (effValue_ao == null) {
//         log.debug(" WTPart:" + aoPart.getNumber() + " 对应的有效性为空");
//         return false;
//      }

      String[] dms = {"DLO", "LO", "SLO"};
      List<CmLightType> lightTypeList = new Vector<CmLightType>();
      for (String key : dms) {
         CmLightType lightType = CmTypeHelper.getLightType(key, true);
         if (lightType != null)
            lightTypeList.add(lightType);
      }

      Enumeration<CmTreeNode> children = node.breadthFirstEnumeration();
      Set<WTPart> loPartList = new HashSet<WTPart>();
      while (children.hasMoreElements()) {
         CmTreeNode child = children.nextElement();
         if (!child.isLeaf())
            continue;
         List<CmLightPart> pathFromCI = child.getPathFromCI();
         if (pathFromCI == null) {
            continue;
         }
         for (CmLightPart pathPart : pathFromCI) {
            if (pathPart != null) {
               WTPart part = CmBizObjUtil.getWTPartFromLightPart(pathPart);
               if (part != null) {
                  CmLightType type = CmTypeHelper.getLightType(part);
                  if (lightTypeList.contains(type)) {
                     loPartList.add(part);
                     break;
                  }
               }
            }
         }
      }
      for (WTPart p : loPartList) {
         //DM的有效性
//         EffContext effContext = CmSamcEffUtil.getEBomEffContext(p);
//         if (effContext == null) {
////            log.debug(" LO WTPart:" + p.getNumber() + " 对应的有效性上下文为空");
//            continue;
//         }

//         CmEffValue effValue = CmSamcEffUtil.getEff(p, effContext);
//         if (effValue == null) {
////            log.debug("LO WTPart:" + p.getNumber() + " 对应的有效性为空");
//            continue;
//         }
//
//         if (!effValue.contains(effValue_ao)) {
//            is = false;
//            sb.append("部件有效性[" + effValue_ao.toEffStr() + "]在" + p.getNumber() + "有效性 [" + effValue.toEffStr() + "]范围之外\n");
//         }
      }
      return is;
   }

   //合并同一实例的数量
   @SuppressWarnings("unchecked")
   public static Map<CmTreeNode, Integer> uniteChildInstanceNode(CmTreeNode node) {
      Map<CmTreeNode, Integer> uniteMap = new HashMap<CmTreeNode, Integer>();
      Enumeration<CmTreeNode> children = node.children();
      List<CmTreeNode> childrenList = new Vector<CmTreeNode>();
      while (children.hasMoreElements()) {
         CmTreeNode child = children.nextElement();
         childrenList.add(child);
      }
      if (childrenList.size() > 0)
         uniteChildInstanceNode(childrenList, uniteMap);
      return uniteMap;
   }

   private static void uniteChildInstanceNode(List<CmTreeNode> list, Map<CmTreeNode, Integer> uniteMap) {
      CmTreeNode fristNode = list.get(0);
      list.remove(0);
      int count = getQuantity(fristNode);
      String lowerLevelName_a = getLowerLevelName(fristNode);
      List<CmTreeNode> delList = new Vector<CmTreeNode>();
      CmLightPart lightPart_a = fristNode.getPart();
      for (CmTreeNode node : list) {
         String lowerLevelName_b = getLowerLevelName(node);
         CmLightPart lightPart_b = node.getPart();
         if (lightPart_a.getPartNumber().equals(lightPart_b.getPartNumber())) {
            if (lowerLevelName_a != null && lowerLevelName_a.equals(lowerLevelName_b)) {

               count += getQuantity(node);
               delList.add(node);
            }
         }
      }
      list.removeAll(delList);
      uniteMap.put(fristNode, count);
      if (list.size() > 0)
         uniteChildInstanceNode(list, uniteMap);
   }

   public static int getQuantity(CmTreeNode cn) {
      int quantity = 1;
      if ((cn.getUserObject()) instanceof CmPartMaster) {
         quantity = ((CmPartMaster) cn.getUserObject()).getQuantity();
      }
      return quantity;
   }

   public static String getLowerLevelName(CmTreeNode cn) {
      String lowerLevelName = "";
      List<CmLightPart> pathPart = cn.getPathFromCI();
      //log.debug(cn+"--路径:"+pathPart);
      if (pathPart != null && pathPart.size() > 1) {
         CmLightPart lightPart = pathPart.get(pathPart.size() - 2);
         lowerLevelName = lightPart == null ? null : lightPart.getPartNumber();
      }
      return lowerLevelName;
   }

   public static URL getURL(String key, Map<String, String> pro) throws Exception {
      URLFactory uf = new URLFactory();
      CmSettings settings = CmSettings.getSection(CmSettings.SECTION_APPURL);
      String str = settings.get(key);
//      log.debug(str);
      if (pro != null && pro.size() > 0) {
         str += "?";
         java.util.Iterator<String> it = pro.keySet().iterator();
         while (it.hasNext()) {
            String k = it.next();
            str += k;
            str += "=";
            str += pro.get(k);
            if (it.hasNext()) {
               str += "&";
            }
         }
      }
      return uf.getURL(str);
   }

   //查看器URL
   public static URL getViewURL(WTObject object, String bomCode) throws Exception {
      String oidStr = PersistenceHelper.getObjectIdentifier(object).getStringValue();
      Map<String, String> pro = new HashMap<String, String>();
      pro.put("oid", oidStr);
      pro.put("isEdit", "false");
      return getURL(bomCode, pro);
   }

   public static URL getMBomViewURL(WTObject object) throws Exception {
      WTPart part = CmBizObjUtil.getWTPartForDoc((WTDocument) object);
      return getViewURL(part, "mbom.url");
   }

   public static URL getSPSBomViewURL(WTObject object) throws Exception {
      WTPart part = CmBizObjUtil.getWTPartForDoc((WTDocument) object);
      return getViewURL(part, "spsbom.url");
   }

   public static URL getAOViewURL(WTObject object) throws Exception {
      return getViewURL(object, "ao.url");
   }

   //编辑器URL
   public static URL getURL(WTObject object, String bomCode,String role) throws Exception {
      String oidStr = PersistenceHelper.getObjectIdentifier(object).getStringValue();
      Map<String, String> pro = new HashMap<String, String>();
      pro.put("oid", oidStr);
      if(role!=null){
         pro.put("role", role);
      }
      return getURL(bomCode, pro);
   }

   public static URL getMBomURL(WTObject object) throws Exception {
      WTPart part = CmBizObjUtil.getWTPartForDoc((WTDocument) object);
      return getURL(part, "mbom.url",null);
   }

   public static URL getSPSBomURL(WTObject object) throws Exception {
      WTPart part = CmBizObjUtil.getWTPartForDoc((WTDocument) object);
      return getURL(part, "spsbom.url",null);

   }

   public static URL getAOURL(WTObject object,String role) throws Exception {
      return getURL(object, "ao.url",role);
   }
	/**
	 * 通过对零件的设计和工艺视图的版本比较，判断零件是否升版
	 * @author fly
	 * @date  2012-11-21
	 * @param part
	 * @return true:EBOM升版    false:EBOM不升版
	 */
	public static boolean isReversion(WTPart part) {
		String planningVersion = "A";
		String designVersion = "A";
		try {
			log.debug(part.getVersionDisplayIdentifier().toString());
			QueryResult qr = VersionControlHelper.service.allVersionsOf(part.getMaster());
			while (qr.hasMoreElements()) {
				WTPart part_temp = (WTPart) qr.nextElement();
				String version = part_temp.getVersionDisplayIdentifier().toString().substring(0, 1);
				String viewName = part_temp.getViewName();
				if (LoadConfig.getInstance().getPbomView().equals(viewName)) {
					if (planningVersion.compareTo(version) < 0) {
						planningVersion = version;
					}
				}
				if ("Design".equals(viewName)) {
					if (designVersion.compareTo(version) < 0) {
						designVersion = version;
					}
				}
			}
			if (designVersion.compareTo(planningVersion) > 0) {
				return true;
			} else {
				return false;
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}
}
