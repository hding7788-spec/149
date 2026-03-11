package ext.casc.util;

import com.glaway.mpm.util.WTPartUtil;
import ext.casc.constants.PDMConfig;
import ext.casc.part.CSCPart;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.views.View;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ExchangePbomUtil implements RemoteAccess {
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
                server.invoke("excute", ExchangePbomUtil.class.getName(), null, argTypes, args);
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
                   List<WTPart> allPart = new ArrayList<WTPart>();
                   allPart.add(parentPart);
                   DownloadTechnicsReportUtil.getAllChildPart(parentPart,allPart);
                   for(WTPart oldPart:allPart){
                       String newNumber = oldPart.getNumber().substring(0,oldPart.getNumber().indexOf("_DEL"));
                       System.out.println("## newNumber="+newNumber);
                       WTPart newPart =   WTPartUtil.getLatestPartByNumberAndView(newNumber,"Manufacturing");
                       if(newPart!=null){
                           QueryResult qResult = WTPartHelper.service.getDescribedByWTDocuments(oldPart, false);
                           WTPartDescribeLink wtPartDescribeLink = null;
                           while (qResult.hasMoreElements()) {
                               Object object = qResult.nextElement();
                               wtPartDescribeLink = (WTPartDescribeLink) object;
                               WTDocument oldDoc =  (WTDocument) wtPartDescribeLink.getRoleBObject();
                               String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(oldDoc);
                               if(docType.indexOf("casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN") > -1) {
                                   WTPartDescribeLink newLink =  WTPartDescribeLink.newWTPartDescribeLink(newPart,oldDoc);
                                   PersistenceServerHelper.manager.insert(newLink);
                                   System.out.println("## newLink="+newLink);
                                   PersistenceServerHelper.manager.remove(wtPartDescribeLink);
                               }

                           }

                       }
                   }




               }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
