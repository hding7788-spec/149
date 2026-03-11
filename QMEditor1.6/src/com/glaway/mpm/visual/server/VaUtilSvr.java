package com.glaway.mpm.visual.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.content.HolderToContent;
import wt.fc.ObjectIdentifier;
import wt.httpgw.HTTPRequest;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.visual.bean.VaInputStreamData;
import com.glaway.mpm.visual.view.VaContext;

public class VaUtilSvr implements Serializable, RemoteAccess {
   private static final long serialVersionUID = -462196362874039392L;

   // private static final CmLogger log = CmLogger.getLogger(CmUtilSvr.class);

//   /**
//    * �����ļ����ع��ܵ����ȡ��ȷ����������
//    * 
//    * @param holder
//    * @param appData
//    * @return
//    * @throws WTException
//    */
//   public static URL getDownloadURL(ContentHolder holder, ApplicationData appData) throws WTException {
//      URL ret = null;
//      try {
//         WTPrincipal me = SessionHelper.getPrincipal();
//         try {
//            SessionContext.setEffectivePrincipal(SessionHelper.manager.getAdministrator());
//            Vector urls = MasterHelper.service.getAlternativeURLs(appData, holder, true);
//            if (urls != null && urls.size() > 0)
//               ret = new URL((String) urls.get(0));
//         } finally {
//            SessionContext.setEffectivePrincipal(me);
//         }
//      } catch (WTException e) {
//         throw e;
//      } catch (Exception e) {
//         throw new WTException(e);
//      }
//
//      return ret;
//   }
   
   /**
    * �����ļ����ع��ܵ����ȡ��ȷ����������
    * 
    * @param holder
    * @param appData
    * @return
    * @throws WTException
 * @throws InvocationTargetException 
 * @throws RemoteException 
    */
   public static URL getDownloadURL(ContentHolder holder, ApplicationData appData) throws WTException {
      return ContentHelper.getDownloadURL(holder, appData);
   }

   public static VaInputStreamData downloadAuthURLData(String strUrl, boolean forceAccess) throws InvocationTargetException, WTException, RemoteException, MalformedURLException,
         WTPropertyVetoException {
      VaInputStreamData ret = null;
      boolean access = true;

      if (forceAccess)
         access = SessionServerHelper.manager.setAccessEnforced(false);
      try {
         URL url = new URL(strUrl);

         InputStream is = null;
         String queryString = url.getQuery();
         if (queryString != null) {
            Properties props = HTTPRequest.splitQueryString(queryString);
            String contentHolderOid = props.getProperty("ContentHolder");
            String httpOperationItemOid = props.getProperty("HttpOperationItem");

            if (contentHolderOid != null && httpOperationItemOid != null) {
               ContentHolder contentHolder = ContentServerHelper.service.getContentHolder(new ObjectIdentifier(contentHolderOid));
               ApplicationData appData = ContentServerHelper.service.getApplicationData(new ObjectIdentifier(httpOperationItemOid));

               HolderToContent holderToContent = HolderToContent.newHolderToContent(contentHolder, appData);
               appData.setHolderLink(holderToContent);
               is = ContentServerHelper.service.findContentStream(appData);
            } else {
               try {
                  URLConnection conn = url.openConnection();
                  conn.setRequestProperty("Accept-Encoding", "gzip");
                  String enc = conn.getHeaderField("content-encoding");
                  if (enc == null || enc.indexOf("gzip") == -1)
                     is = conn.getInputStream();
                  else
                     is = new GZIPInputStream(conn.getInputStream());
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }

            if (is != null)
               ret = new VaInputStreamData(is);
         }
      } finally {
         if (forceAccess)
            SessionServerHelper.manager.setAccessEnforced(access);
      }

      return ret;
   }

   public static void setLocale(Locale locale) {
      VaContext.setLocale(locale);
   }
}
