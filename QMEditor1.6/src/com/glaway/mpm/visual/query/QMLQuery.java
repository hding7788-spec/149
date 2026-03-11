/**
 * SVN Id:        $Id: QMLQuery.java 29 2008-05-27 08:02:49Z mvonhasselbach $
 * SVN Date:      $Date: 2008-05-27 10:02:49 +0200 (Tue, 27 May 2008) $
 * SVN Revision:  $Revision: 29 $
 * SVN Author:    $Author: mvonhasselbach $
 * SVN URL:       $HeadURL: http://autodev.ptc.com/eecRepos/Components/wtx/trunk/overwrite/src/ext/query/QMLQuery.java $
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

import java.util.Hashtable;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.method.RemoteAccess;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.visual.log.VaLogger;
import com.ptc.core.command.common.CommandException;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.core.query.report.command.common.BasicReportQueryCommand;

public class QMLQuery implements RemoteAccess {

   private static VaLogger Log = VaLogger.getLogger(QMLQuery.class);

   public static WTList run(Persistable inputObj, String qmlPath) throws WTException {
      Hashtable<String, Long> inputParams = new Hashtable<String, Long>();
      inputParams.put("oidKey", new Long(PersistenceHelper.getObjectIdentifier(inputObj).getId()));
      return run( inputParams, qmlPath);
   }

   public static WTList run( Hashtable<?, ?> inputParams, String qmlPath) throws WTException, CommandException {
      BasicReportQueryCommand brqc = new BasicReportQueryCommand();
      try{
         brqc.setArguments(inputParams);
         brqc.setGenerateTypeInstances(true);
         brqc.setQml(QMLTemplateCache.getQMLString(qmlPath));
      }catch(WTPropertyVetoException ex){
         throw new WTException(ex);
      }
      brqc.execute();
      TypeInstance[] res = brqc.getResultList();
      if(res==null) return new WTArrayList(0);
      WTArrayList resUnique = new WTArrayList(res.length);
      ReferenceFactory rf = new ReferenceFactory();
      for(TypeInstance ti : res){
         Log.debug("found ti ---->");
         Log.debug(ti);
         Log.debug("---------> ti , -> persi -> ");
         Log.debug(ti.getPersistenceIdentifier());
         resUnique.add(rf.getReference(ti.getPersistenceIdentifier()));
      }
      return resUnique;
   }

}
