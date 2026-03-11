package ext.casc.util;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import ext.casc.constants.PDMConfig;
import ext.casc.part.CSCPart;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.views.View;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExchangeDeleteUtil implements RemoteAccess {
    public static void main(String[] args) throws WTException {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        rms.setUserName("wcadmin");
        rms.setPassword(PDMConfig.WCADMIN_PASSWORD);
        excute(args[0]) ;
    }
    public  static void excute(String number) throws WTException {

        if (!RemoteMethodServer.ServerFlag) {
            Class[] argTypes = {String.class};
            Object[] args = {number};
            try {
                SessionHelper.manager.setPrincipal("administrator");
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                //				server.setPassword("wcadmin");
                server.setPassword(PDMConfig.WCADMIN_PASSWORD);
                server.invoke("excute", ExchangeDeleteUtil.class.getName(), null, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        } else {
            try {
               if(!number.contains("_DEL")){
                    System.out.println("零件编号不正确，必须输入_DEL编号零件");
                    return;
                }
               WTPart parentPart =  WTPartUtil.getLatestPartByNumberAndView(number,"Manufacturing");
               if(parentPart!=null){


                   View view = WTPartUtil.getViewByName("Manufacturing");
                   long viewId = view.getPersistInfo().getObjectIdentifier().getId();

                   String newNumber = parentPart.getNumber().substring(0,parentPart.getNumber().indexOf("_DEL"));
                   System.out.println("## newNumber="+newNumber);
                   WTPart newPart =   WTPartUtil.getLatestPartByNumberAndView(newNumber,"Manufacturing");
                   if(newPart ==null) {
                       newPart =   WTPartUtil.getLatestPartByNumberAndView(newNumber,"Design");
                       if(newPart!=null) {
                           long masterId = parentPart.getMaster().getPersistInfo().getObjectIdentifier().getId();
                           long newMasterId = newPart.getMaster().getPersistInfo().getObjectIdentifier().getId();
                           String updateSql = "update WTPart set IDA3MASTERREFERENCE=" + newMasterId + " WHERE IDA3MASTERREFERENCE=" + masterId + " AND IDA3VIEW=" + viewId;
                           System.out.println("## updateSql=" + updateSql);
                           DBUtil.deleteBySql(updateSql);
                       }
                       getAllChildPart(parentPart,viewId);
                   }



               }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void getAllChildPart(WTPart ppart,long viewId) throws WTException {
        QueryResult qr = WTPartHelper.service.getUsesWTParts(ppart, ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class));
        WTPart cpart = null;
        while (qr.hasMoreElements()) {
            Persistable[] per = (Persistable[]) qr.nextElement();
            Persistable pper = per[1];
            if (pper instanceof WTPart) {
                cpart = (WTPart) per[1];
                String viewName = cpart.getViewName();
                if ("Design".equals(viewName)) {
                    cpart = CSCPart.getPartByNumberAndViewName(cpart.getNumber(), "Manufacturing");
                }
            } else if (pper instanceof WTPartMaster) {
                cpart = CSCPart.getPartByNumberAndViewName(((WTPartMaster) pper).getNumber(), "Manufacturing");
            }

            if (cpart != null) {

                String newNumber = cpart.getNumber().substring(0,cpart.getNumber().indexOf("_DEL"));
                System.out.println("## newNumber="+newNumber);
                WTPart newPart =   WTPartUtil.getLatestPartByNumberAndView(newNumber,"Manufacturing");
                if(newPart !=null) continue;

                newPart =   WTPartUtil.getLatestPartByNumberAndView(newNumber,"Design");
                if(newPart!=null){
                    long masterId = cpart.getMaster().getPersistInfo().getObjectIdentifier().getId();
                    long newMasterId = newPart.getMaster().getPersistInfo().getObjectIdentifier().getId();
                    String updateSql = "update WTPart set IDA3MASTERREFERENCE="+newMasterId+" WHERE IDA3MASTERREFERENCE="+masterId+" AND IDA3VIEW="+viewId;
                    System.out.println("## updateSql="+updateSql );
                    DBUtil.deleteBySql(updateSql);

                    String updateSql2 = "update WTPARTUSAGELINK set IDA3B5="+newMasterId+" WHERE IDA3B5="+masterId+" AND IDA3A5="+ppart.getPersistInfo().getObjectIdentifier().getId();
                    System.out.println("## updateSql2="+updateSql2 );
                    DBUtil.deleteBySql(updateSql2);
                }
                getAllChildPart(cpart, viewId);
            }
        }
    }
}
