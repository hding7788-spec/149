package com.glaway.mpm.visual.util;

import java.awt.Image;
import java.awt.Toolkit;
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
import wt.eff.EffContext;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.httpgw.URLFactory;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.visual.bean.VaInputStreamData;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.bean.VaLightType;
import com.glaway.mpm.visual.bean.VaPartMaster;
import com.glaway.mpm.visual.biz.VaBizObjUtil;
import com.glaway.mpm.visual.conf.VaSettings;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.server.VaUtilSvr;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.tree.VaTreeNode;


/**
 * <br>
 * Created on 2010-10-23
 * 
 * @author Dennis Huang - 黄寿疆
 */
public class VaUtil {
   public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

   private static final String             SERVER_CLASS         = VaUtilSvr.class.getName();
    private static final VaLogger log = VaLogger.getLogger(VaUtil.class);

   public static Image getImageFromServer(String fileName) {
	   String path=System.getProperty("user.dir");
      return Toolkit.getDefaultToolkit().getImage(path+"\\images\\"+fileName);
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

   public static VaInputStreamData downloadAuthURLData(String strUrl, boolean forceAccess) throws InvocationTargetException, WTException,
      RemoteException, MalformedURLException, WTPropertyVetoException {
      String method = "downloadAuthURLData";
      Class[] types = {String.class, Boolean.TYPE};
      Object[] objs = {strUrl, forceAccess};

      return (VaInputStreamData) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, objs);
//      return (CmInputStreamData)CmUtilSvr.downloadAuthURLData(strUrl, forceAccess);
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
      String nodeType_nodeId = VaUtil.getSymbolNODETYPE_NODEID();
      String nodeId_type = VaUtil.getSymbolNODEID_TYPE();
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
//         if ("参照件".equals(part.getName())) {
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
      String prop = VaSettings.getSection(VaSettings.SECTION_CAD).get(propName);
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
         File td = VaContext.getTempDir();
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
         if (isSame) {
            System.out.println("checkval: " + checkVal + ", f.length: " + f.length());
         }
         if (!isSame && f.length() != checkVal) { //more secure would be checksum comparison

            URLConnection urlconnection = url.openConnection();
            urlconnection.setRequestProperty("authorization", VaContext.getCred());
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

   public static String getAbsPathByNodeName(VaTreeNode aNode) {

      Object[] thePath = aNode.getPath();
      String theResult = "";
      for (Object n : thePath)
         if (((VaTreeNode) n).getUserObject() != null)
            theResult = theResult + "/" + ((VaTreeNode) n).getUserObject();
      if (theResult.length() != 0)
         theResult = theResult.substring(1);
      //log.debug("DICUTIL --> getPathString theResult"+theResult);
      return theResult;
   }

   /**
    *AO组件中所包含的所有零件叶节点的DM有效性范围要包含所设置的AO组件有效性，否则提示AO组件有效性设置错误
    */
   public static boolean checkNodeEff(VaTreeNode node, StringBuffer sb) {
      boolean is = true;
      VaLightPart aoLightPart = node.getPart();
      if (aoLightPart == null) {
//         log.debug("AO组件节点:" + node + "的CmLightPart为空");
         return false;
      }
      WTPart aoPart = VaBizObjUtil.getWTPartFromLightPart(aoLightPart);
      if (aoPart == null) {
//         log.debug("通过AO组件节点:" + node + "找不到对应的WTPart");
         return false;
      }

      String[] dms = {"DLO", "LO", "SLO"};
      List<VaLightType> lightTypeList = new Vector<VaLightType>();
      for (String key : dms) {
         VaLightType lightType = VaTypeHelper.getLightType(key, true);
         if (lightType != null)
            lightTypeList.add(lightType);
      }

      Enumeration<VaTreeNode> children = node.breadthFirstEnumeration();
      Set<WTPart> loPartList = new HashSet<WTPart>();
      while (children.hasMoreElements()) {
         VaTreeNode child = children.nextElement();
         if (!child.isLeaf())
            continue;
         List<VaLightPart> pathFromCI = child.getPathFromCI();
         if (pathFromCI == null) {
            continue;
         }
         for (VaLightPart pathPart : pathFromCI) {
            if (pathPart != null) {
               WTPart part = VaBizObjUtil.getWTPartFromLightPart(pathPart);
               if (part != null) {
                  VaLightType type = VaTypeHelper.getLightType(part);
                  if (lightTypeList.contains(type)) {
                     loPartList.add(part);
                     break;
                  }
               }
            }
         }
      }
      
     
      return is;
   }

   //合并同一实例的数量
   @SuppressWarnings("unchecked")
   public static Map<VaTreeNode, Integer> uniteChildInstanceNode(VaTreeNode node) {
      Map<VaTreeNode, Integer> uniteMap = new HashMap<VaTreeNode, Integer>();
      Enumeration<VaTreeNode> children = node.children();
      List<VaTreeNode> childrenList = new Vector<VaTreeNode>();
      while (children.hasMoreElements()) {
         VaTreeNode child = children.nextElement();
         childrenList.add(child);
      }
      if (childrenList.size() > 0)
         uniteChildInstanceNode(childrenList, uniteMap);
      return uniteMap;
   }

   private static void uniteChildInstanceNode(List<VaTreeNode> list, Map<VaTreeNode, Integer> uniteMap) {
      VaTreeNode fristNode = list.get(0);
      list.remove(0);
      int count = getQuantity(fristNode);
      String lowerLevelName_a = getLowerLevelName(fristNode);
      List<VaTreeNode> delList = new Vector<VaTreeNode>();
      VaLightPart lightPart_a = fristNode.getPart();
      for (VaTreeNode node : list) {
         String lowerLevelName_b = getLowerLevelName(node);
         VaLightPart lightPart_b = node.getPart();
         if (lightPart_a.getNumber().equals(lightPart_b.getNumber())) {
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

   public static int getQuantity(VaTreeNode cn) {
      int quantity = 1;
      if ((cn.getUserObject()) instanceof VaPartMaster) {
         quantity = ((VaPartMaster) cn.getUserObject()).getQuantity();
      }
      return quantity;
   }

   public static String getLowerLevelName(VaTreeNode cn) {
      String lowerLevelName = "";
      List<VaLightPart> pathPart = cn.getPathFromCI();
      //log.debug(cn+"--路径:"+pathPart);
      if (pathPart != null && pathPart.size() > 1) {
         VaLightPart lightPart = pathPart.get(pathPart.size() - 2);
         lowerLevelName = lightPart == null ? null : lightPart.getNumber();
      }
      return lowerLevelName;
   }

   public static URL getURL(String key, Map<String, String> pro) throws Exception {
      URLFactory uf = new URLFactory();
      VaSettings settings = VaSettings.getSection(VaSettings.SECTION_APPURL);
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
      WTPart part = VaBizObjUtil.getWTPartForDoc((WTDocument) object);
      return getViewURL(part, "mbom.url");
   }

   public static URL getSPSBomViewURL(WTObject object) throws Exception {
      WTPart part = VaBizObjUtil.getWTPartForDoc((WTDocument) object);
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
      WTPart part = VaBizObjUtil.getWTPartForDoc((WTDocument) object);
      return getURL(part, "mbom.url",null);
   }

   public static URL getSPSBomURL(WTObject object) throws Exception {
      WTPart part = VaBizObjUtil.getWTPartForDoc((WTDocument) object);
      return getURL(part, "spsbom.url",null);

   }

   public static URL getAOURL(WTObject object,String role) throws Exception {
      return getURL(object, "ao.url",role);
   }
}
