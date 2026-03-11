package com.glaway.mpm.visual.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.glaway.mpm.visual.biz.VaBizObjUtil;
import com.glaway.mpm.visual.util.VaXML;


/**
 * 本类只做保存XML到ContentHolder对象上，或者从ContentHolder对象中获取附件对象
 * 三个主要方法：<br>
 * 1.updateContent<br>
 * 2.findContentApplicationData<br>
 * 3.findContentStream<br>
 * <br>Created on 2012-10-29
 * @author chenyunlong
 */
public class VaXMLDataHelper implements RemoteAccess, Serializable {
   private static final long serialVersionUID = -5006042148100644705L;
   private static String     class_name       = VaXMLDataHelper.class.getName();

   public void deleteAllXMLSecondary(ContentHolder contentHolder) {

      try {
         deleteXMLSecondary(contentHolder, ContentRoleType.SECONDARY, null);
         deleteXMLSecondary(contentHolder, ContentRoleType.PRIMARY, null);
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    *保存XML文件到WTPart对应的数据包中
    * @param part
    * @param xml
    * @return
    * @throws Exception
    */
   public ContentHolder updatePartSecondaryContentSPSBOM(WTDocument doc, VaXML xml) throws Exception {
      if (doc == null) {
         return doc;
      }
      return updateContent(doc, xml, ContentRoleType.PRIMARY, "SPSBOM_SPS_" + doc.getNumber() + ".xml", true);
   }

   public ContentHolder updatePartSecondaryContentMBOM(WTDocument doc, VaXML xml) throws Exception {
      if (doc == null) {
         return doc;
      }
      return updateContent(doc, xml, ContentRoleType.PRIMARY, "MBOM_DOC_" + doc.getNumber() + ".xml", true);
   }

   public ContentHolder updatePartSecondaryContentAO(WTDocument doc, VaXML xml) throws Exception {
      if (doc == null) {
         return doc;
      }
      return updateContent(doc, xml, ContentRoleType.PRIMARY, "AO_DOC_" + doc.getNumber() + ".xml", true);
   }

   public ContentHolder updateContent(ContentHolder contentHolder, InputStream is, ContentRoleType type, String fileName, boolean uniquely) throws Exception {
      VaInputStreamData isd = new VaInputStreamData(is);
      return updateContent(contentHolder, isd, type, fileName, uniquely);
   }

   public ContentHolder updateContent(ContentHolder contentHolder, String xml, ContentRoleType type, String fileName, boolean uniquely) throws Exception {
      ByteArrayInputStream ins = null;
      ins = new ByteArrayInputStream(xml.getBytes("UTF-8"));
      if (ins == null)
         return contentHolder;
      VaInputStreamData dataStream = new VaInputStreamData(ins);
      return updateContent(contentHolder, dataStream, type, fileName, uniquely);
   }

   public ContentHolder updateContent(ContentHolder contentHolder, VaXML xml, ContentRoleType type, String fileName, boolean uniquely) throws Exception {
      return updateContent(contentHolder, xml.toString(), type, fileName, uniquely);
   }

   /**
    *下午04:13:37-黄勇
    *保存附件对象到ContentHolder对象<br>
    * @param contentHolder 接收附件的主体对象
    * @param data  存放附件输入流的CmInputStreamData对象
    * @param type  指定存放的类型 主要是两个 ContentRoleType.PRIMARY 表示保存为主体对象的主件上;ContentRoleType.SECONDARY表示保存为主体对象的附件
    * @param fileName 指定保存文件的名字
    * @param uniquely 设定是否清除相同名字的已存文件，<br>
    * true:删除已存在的文件名为fileName的文件，然后以fileName文件名保存此附件<br>
    * false:在fileName后加"-1"[如果是同一名字多次保存，则在1的基础上递加]为文件名保存此附件
    * @return
    * @throws Exception
    */
   public static ContentHolder updateContent(ContentHolder contentHolder, VaInputStreamData data, ContentRoleType type, String fileName, boolean uniquely) throws Exception {
      final String method_name = "updateContent";
      if (!RemoteMethodServer.ServerFlag) {
         Class<?>[] types = new Class[]{ContentHolder.class, VaInputStreamData.class, ContentRoleType.class, String.class, boolean.class};
         Object[] values = new Object[]{contentHolder, data, type, fileName, uniquely};
         return (ContentHolder) RemoteMethodServer.getDefault().invoke(method_name, class_name, null, types, values);
      }
      boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
      Transaction trx = new Transaction();
      try {
         trx.start();
         ContentHolder holder = (ContentHolder) ContentHelper.service.getContents(contentHolder);

         ApplicationData appData = ApplicationData.newApplicationData(holder);
         appData.setFileName(fileName);
         appData.setUploadedFromPath(appData.getFileName());
         appData.setRole(type);

         File fw = null;
         if (data.getInputStream() != null) {
            InputStream is = data.getInputStream();

            VaXML xml = VaXML.read(is);
            if (xml == null)
               return holder;
            if (uniquely)
               clearDuContent(holder, type, fileName);
            fw = new File("D:/Temp", fileName);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fw), "UTF-8"));
            xml.setFOut(out);
            xml.writeString();
            if (out != null)
               out.close();

            appData = ContentServerHelper.service.updateContent(holder, appData, fw.getPath());
            PersistenceServerHelper.manager.update(appData);

            if (contentHolder instanceof FormatContentHolder && ContentRoleType.PRIMARY.equals(type)) {
               ContentServerHelper.service.updateHolderFormat((FormatContentHolder) contentHolder);
            }
         }
         trx.commit();
         trx = null;
      } finally {
         SessionServerHelper.manager.setAccessEnforced(flag);
         if (trx != null)
            trx.rollback();
      }
      return contentHolder;
   }

   public static void deleteXMLSecondary(ContentHolder contentHolder, ContentRoleType roleType, String fileName) throws Exception, InvocationTargetException {
      final String method_name = "deleteXMLSecondary";
      if (!RemoteMethodServer.ServerFlag) {
         Class<?>[] types = new Class[]{ContentHolder.class, ContentRoleType.class, String.class};
         Object[] values = new Object[]{contentHolder, roleType, fileName};
         RemoteMethodServer.getDefault().invoke(method_name, class_name, null, types, values);
      }
      QueryResult qr = null;
      qr = ContentHelper.service.getContentsByRole(contentHolder, roleType);
      while (qr.hasMoreElements()) {
         Object o = qr.nextElement();
         if (o instanceof ApplicationData) {
            ApplicationData ap = (ApplicationData) o;
            if (matcher(ap.getFileName(), fileName) || fileName == null || fileName.trim().length() == 0) {
               ContentServerHelper.service.deleteContent(contentHolder, ap);
            }

         }
      }
   }

   private static void clearDuContent(ContentHolder holder, ContentRoleType roleType, String fileName) throws Exception {
      QueryResult qr = null;
      qr = ContentHelper.service.getContentsByRole(holder, roleType);
      while (qr.hasMoreElements()) {
         Object o = qr.nextElement();
         if (o instanceof ApplicationData) {
            ApplicationData ap = (ApplicationData) o;
            if (ContentRoleType.PRIMARY.equals(roleType) || ap.getFileName().equalsIgnoreCase(fileName)) {
               ContentServerHelper.service.deleteContent(holder, ap);
            }
         }
      }
   }

   public void findContentXMLWrite(ContentHolder holder, ContentRoleType rolType, File file, String fileName) throws Exception {
      ApplicationData theApplicationData = findContentApplicationData(holder, rolType, fileName);
      if (theApplicationData == null)
         return;
      VaInputStreamData isd = (VaInputStreamData) findContentStream(theApplicationData);
      if (isd != null && isd.getInputStream() != null) {
         VaXML xml = VaXML.read(isd.getInputStream());

         if (file.isDirectory() && theApplicationData.getFileName() != null) {
            file = new File(file, theApplicationData.getFileName());
         }
         PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
         xml.setFOut(out);
         xml.writeString();
         if (out != null)
            out.close();
      }
   }

   public String findContentXMLStr(ContentHolder holder, ContentRoleType rolType, String fileName) throws Exception {
      return findContentXML(holder, rolType, fileName).toString();
   }

   /**
    *获取WTPart对象附件中的XML文件
    * @param wtpart
    * @return
    * @throws Exception
    */
   public VaXML findContentXMLForPart(WTPart wtpart) throws Exception {
      return findContentXML(wtpart, ContentRoleType.SECONDARY, null);
   }

   public VaXML findContentXMLForDoc(WTDocument doc) {

      VaXML xml = new VaXML();
      if (doc == null)
         return xml;
      try {
         xml = findContentXML(doc, ContentRoleType.PRIMARY, null);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return xml;
   }

   /**
    *获取CmLightPart对应WTPart对象附件中的XML文件
    * @param part
    * @return
    * @throws Exception
    */
   public VaXML findContentXMLForLightPart(VaLightPart part) {
      WTPart wtpart = VaBizObjUtil.getWTPartFromLightPart(part);
      VaXML xml = null;
      try {
         xml = findContentXMLForPart(wtpart);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return xml;
   }

   public VaXML findContentXML(ContentHolder holder, ContentRoleType rolType, String fileName) throws Exception {
      VaXML ret = new VaXML();
      ApplicationData theApplicationData = (ApplicationData) findContentApplicationData(holder, rolType, fileName);
      if (theApplicationData == null)
         return ret;
      VaInputStreamData isd = findContentStream(theApplicationData);
      if (isd != null && isd.getInputStream() != null)
         ret = VaXML.read(isd.getInputStream());
      return ret;

   }

   @SuppressWarnings("unchecked")
   public static VaInputStreamData findContentStream(ApplicationData ap) throws Exception {
      final String method_name = "findContentStream";
      if (!RemoteMethodServer.ServerFlag) {
         Class[] aclass = new Class[]{ApplicationData.class};
         Object[] aobj = new Object[]{ap};
         return (VaInputStreamData) RemoteMethodServer.getDefault().invoke(method_name, class_name, null, aclass, aobj);
      }
      VaInputStreamData isd = null;
      if (ap != null) {
         InputStream is = ContentServerHelper.service.findContentStream(ap);
         if (is == null) {
            throw new WTException("Could not get input stream from content file!");
         }
         isd = new VaInputStreamData(is);
      }
      return isd;
   }

   /**
    *下午04:23:27-黄勇
    *获取ContentHolder对象的附件或主件
    * @param holder ContentHolder对象
    * @param rolType 指定存放的类型 主要是两个 ContentRoleType.PRIMARY 表示获取主件;ContentRoleType.SECONDARY表示获取附件
    * @param fileName 指定查找的文件名，可以使用如简单的*号查询【如：*spsfile*】或用标准的正则表达式查询【如：^spsfile.xml$】<br>
    * 如果此值为null或长度为0则不进行文件名查询，直接返回第一个找到的对象
    * @return
    * @throws Exception
    */
   @SuppressWarnings("unchecked")
   public static ApplicationData findContentApplicationData(ContentHolder holder, ContentRoleType rolType, String fileName) throws Exception {
      final String method_name = "findContentApplicationData";

      if (!RemoteMethodServer.ServerFlag) {
         Class[] aclass = new Class[]{ContentHolder.class, ContentRoleType.class, String.class};
         Object[] aobj = new Object[]{holder, rolType, fileName};
         return (ApplicationData) RemoteMethodServer.getDefault().invoke(method_name, class_name, null, aclass, aobj);
      }
      ApplicationData ap = null;
      QueryResult qr = null;
      qr = ContentHelper.service.getContentsByRole(holder, rolType);
      while (qr.hasMoreElements()) {
         Object o = qr.nextElement();
         if (o instanceof ApplicationData) {
            ap = (ApplicationData) o;
            if (matcher(ap.getFileName(), fileName)) {
               qr = null;
               break;
            }
            ap = null;
         }
      }
      if (ap == null) {
//         CmLogger.getLogger().debug("没有找到  查询名为:" + fileName, " 的 ", rolType.getDisplay(), " 文件");
      }
      return ap;
   }

   private static boolean matcher(String apfileName, String fileName) {
      if (fileName == null || fileName.trim().length() <= 0)
         return true;
      String oldFileName = new String(fileName);
      if (!fileName.startsWith("^") || !fileName.endsWith("$")) {
         fileName = fileName.replace(".", "\\.");
         fileName = fileName.replace("*", ".*");
         StringBuffer sb = new StringBuffer(fileName);
         sb.insert(0, '^');
         sb.insert(sb.length(), '$');
         fileName = sb.toString();
      }
      Pattern p = Pattern.compile(fileName);
      Matcher m = p.matcher(apfileName);
//      CmLogger.getLogger().debug("ApplicationData FileName：", apfileName, "  Search Name:", oldFileName, "  Regular expression：", fileName, "   Result：", m.matches());
      return m.matches();

   }

   public VaXML readFile(File file) throws Exception {
      FileInputStream fis = new FileInputStream(file);
      VaXML xml = VaXML.read(fis);
      return xml;
   }

   public VaXML readFile(String filePath) throws Exception {
      File file = new File(filePath);
      return readFile(file);
   }

   public void writeString(VaXML xml, File file) {
      PrintWriter out = null;
      try {
         out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
         xml.setFOut(out);
         xml.writeString();

      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (out != null)
            out.close();
      }

   }

   public void writeString(VaXML xml, String fileStr) {
      File file = new File(fileStr);
      writeString(xml, file);
   }

   public static void main(String[] args) throws Exception {
   /*//      CmTestHelper.tryConnecting();
    CmXMLDataHelper helper = new CmXMLDataHelper();
    WTPart part = (WTPart) CmSearchHelper.search(WTPart.class, 108423);
    //helper.updateContent(part, helper.readFile("E:/System/desktop/MBOM001.xml"), ContentRoleType.SECONDARY, "MBOM_B_" + part.getNumber() + ".xml",
    //   true);//上传测试
    // helper.findContentXMLWrite(part, ContentRoleType.SECONDARY, new File("E:/System/desktop"), "*MBOM*.xml");//下载测试
    CmXML xml = helper.findContentXML(part, ContentRoleType.SECONDARY, "*MBOM*.xml");
    System.out.println(xml.getName());*/
   }
}
