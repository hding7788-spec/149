/**
 * SVN Id:        $Id: QMLHelper.java 61 2008-06-02 12:48:36Z mvonhasselbach $
 * SVN Date:      $Date: 2008-06-02 14:48:36 +0200 (Mon, 02 Jun 2008) $
 * SVN Revision:  $Revision: 61 $
 * SVN Author:    $Author: mvonhasselbach $
 * SVN URL:       $HeadURL: http://autodev.ptc.com/eecRepos/Components/wtx/trunk/overwrite/src/ext/query/QMLHelper.java $
 *
 * bcwti
 * 
 * Copyright (c) 1998-2008 Parametric Technology. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Parametric Technology. You shall not disclose such
 * confidential information and shall use it only in accordance with the terms of the license agreement you entered into
 * with Parametric Technology.
 * 
 * ecwti
 */
package com.glaway.mpm.visual.query;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Hashtable;

import com.glaway.mpm.visual.log.VaLogger;

import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.template.ReportTemplateHelper;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.xml.xslt.DOMXMLSource;
import wt.util.xml.xslt.XMLSourceFactory;

public class QMLHelper implements RemoteAccess {

   private static HashMap<String,DOMXMLSource> qmlMap = new HashMap<String,DOMXMLSource>();
   private static boolean isReloadable = false;
   private static VaLogger log = VaLogger.getLogger(QMLHelper.class);
   static{
      try{
         isReloadable = WTProperties.getLocalProperties().getProperty("ext.query.QMLTemplateCache.reloadable", false);
      }catch(Exception ex){
      }
   }

   /**
   * Method getQMLString.
   * @param qmlPath String
   * @return String
   * @throws WTException
   */
   public static DOMXMLSource getQMLSource(String qmlPath) throws WTException {
      if(qmlMap.containsKey(qmlPath) && !isReloadable) return (DOMXMLSource)qmlMap.get(qmlPath);
      synchronized(qmlMap){
         log.debug("before getting res");
         InputStream is = WTContext.getContext().getResourceAsStream(qmlPath.trim());
         log.debug("after getting res");
         if(is!=null){
            try{
               log.debug("before 0");
//               if(log.getLevel().equals(Level.DEBUG)){ 
//                  log.debug("before getting res");
//                  log.debug("after getting res");
//                  ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                  ext.webject.StreamHelper.copyIs2Os(1024, is, bos);
//                  is.close();
//                  log.debug("after streaming res");
//                  log.debug(bos.toString());
//                  is = WTContext.getContext().getResourceAsStream(qmlPath.trim());
//               }
               DOMXMLSource qmlDom = ReportTemplateHelper.convertToDOM(XMLSourceFactory.getFactory().newReaderSource( new InputStreamReader(is)));               
               log.debug("qmlDom: "+qmlDom);
               qmlMap.put(qmlPath, qmlDom);
               return qmlDom;            
            }catch(Exception ex){
               ex.printStackTrace();
               throw new WTException(ex);
            }
         }else throw new WTException("qml file :" + qmlPath + " is missing on server!");

//         File qmlFile = new File(wtHome, "codebase/" + qmlPath.trim());
//         if(qmlFile.exists()){
//            try{
//               log.debug("read qml from file: " + qmlFile.getAbsolutePath());
//               DOMXMLSource qmlDom = ReportTemplateHelper.convertToDOM(XMLSourceFactory.getFactory().newReaderSource(new FileReader(qmlFile)));
//               qmlMap.put(qmlPath, qmlDom);
//               return qmlDom;
//            }catch(Exception ex){
//               //ex.printStackTrace();
//               throw new WTException(ex);
//            }
//         }else throw new WTException("qml file :" + qmlFile + " is missing on server!");
      }
   }

   public static WTArrayList queryCollection(String qmlPath, Hashtable<?, ?> paramHash ) throws WTException {
      return new WTArrayList(query(qmlPath, paramHash));
   }
   public static QueryResult query(String qmlPath, Hashtable<?, ?> paramHash) throws WTException {
      if(RemoteMethodServer.ServerFlag){
         log.debug("invoke QMLHelper.query() server-side for qml: "+qmlPath+" and params: "+paramHash); 
         log.debug("getQMLSource(qmlPath).toString(): "+getQMLSource(qmlPath).toString()); 
         QueryResult res = ReportTemplateHelper.generateResults(getQMLSource(qmlPath), paramHash, null, null);
         if(res!=null)log.debug("found # elems: "+res.size());
         else log.debug("found 'null' !!!");
         return res;
      }
      try{
         Class<?> aclass[] = { java.lang.String.class, java.util.Hashtable.class };
         Object aobj[] = { qmlPath, paramHash };
         return (QueryResult)RemoteMethodServer.getDefault().invoke("query", QMLHelper.class.getName(), null, aclass, aobj);
      }catch(InvocationTargetException invocationtargetexception){
         Throwable throwable = invocationtargetexception.getTargetException();
         if(throwable instanceof WTException){
            throw (WTException)throwable;
         }else{
            throw new WTException(throwable, "Error in ext.query.QMLHelper.query()...");
         }
      }catch(RemoteException remoteexception){
         throw new WTException(remoteexception, "Error in ext.query.QMLHelper.query()...");
      }
   }

}
