package ext.casc.util;

import com.glaway.mpm.util.*;
import com.ptc.windchill.mpml.processplan.MPMPartToProcessPlanLink;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.resource.MPMResourceHelper;
import ext.casc.constants.PDMConfig;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;

public class RelatedDocPartUtil implements RemoteAccess {
    public static void main(String[] args) throws WTException {
    	if(args.length<=0){
			System.out.println("零件编号不允许为空");
			return ;
		}
    	RemoteMethodServer rms = RemoteMethodServer.getDefault();
    	rms.setUserName("wcadmin");
		rms.setPassword(PDMConfig.WCADMIN_PASSWORD);

        excute(args[0]) ;
    }
    public static void excute(String partNumber) throws WTException {

        if (!RemoteMethodServer.ServerFlag) {
            Class[] argTypes = {String.class};
            Object[] args = {partNumber};
            try {
            	SessionHelper.manager.setPrincipal("administrator");

            	RemoteMethodServer server = RemoteMethodServer.getDefault();
				server.setUserName("wcadmin");
				//				server.setPassword("wcadmin");
				server.setPassword(PDMConfig.WCADMIN_PASSWORD);
				server.invoke("excute", RelatedDocPartUtil.class.getName(), null, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }else{
            boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
            try {
                if("ALL".equals(partNumber)){
                    QuerySpec qSpec = new QuerySpec(MPMPartToProcessPlanLink.class);
                    QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
                    while (qResult.hasMoreElements()) {
                        MPMPartToProcessPlanLink link = (MPMPartToProcessPlanLink)  qResult.nextElement();
                        Persistable roleA = link.getRoleAObject();
                        Persistable roleB = link.getRoleBObject();
                        if(roleA instanceof WTPart && roleB instanceof MPMProcessPlan){
                            WTPart part = (WTPart)roleA;
                            MPMProcessPlan plan = (MPMProcessPlan)roleB;
                            Collection collection = MPMResourceHelper.service.getAssociatedDescribeDocuments(plan);
                            Iterator iterator = collection.iterator();
                            while(iterator.hasNext()) {
                                ObjectReference oref = (ObjectReference)iterator.next();
                                WTDocument doc = (WTDocument)oref.getObject();
                                WTPartDescribeLink describeLink =   SearchErrorProcessDocUtility.getLinkByPartAndDoc(part,doc);
                                if(describeLink==null){
                                    System.out.println("修复："+part.getNumber()+"  "+doc.getNumber()+"."+doc.getVersionIdentifier().getValue());
                                    WTPartUtil.createWTPartDescribeLink(part,doc);
                                }
                            }
                        }
                    }
                }else{
                    if(partNumber.contains(",")){
                        String[] ss =partNumber.split(",");
                        for(String s:ss){
                            if(!"".equals(s)){
                                WTPart part = WTPartUtil.getLatestPartByNumberAndView(s,"Manufacturing");
                                if(part!=null){
                                    List<MPMProcessPlan> plans =   ProcessPlanUtil.getProcessPlanByPart(part);
                                    for(MPMProcessPlan plan:plans){
                                        Collection collection = MPMResourceHelper.service.getAssociatedDescribeDocuments(plan);
                                        Iterator iterator = collection.iterator();
                                        while(iterator.hasNext()) {
                                            ObjectReference oref = (ObjectReference)iterator.next();
                                            WTDocument doc = (WTDocument)oref.getObject();
                                            WTPartDescribeLink describeLink =   SearchErrorProcessDocUtility.getLinkByPartAndDoc(part,doc);
                                            if(describeLink==null){
                                                System.out.println("修复："+part.getNumber()+"  "+doc.getNumber()+"."+doc.getVersionIdentifier().getValue());
                                                WTPartUtil.createWTPartDescribeLink(part,doc);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber,"Manufacturing");
                        if(part!=null){
                            List<MPMProcessPlan> plans =   ProcessPlanUtil.getProcessPlanByPart(part);
                            for(MPMProcessPlan plan:plans){
                                Collection collection = MPMResourceHelper.service.getAssociatedDescribeDocuments(plan);
                                Iterator iterator = collection.iterator();
                                while(iterator.hasNext()) {
                                    ObjectReference oref = (ObjectReference)iterator.next();
                                    WTDocument doc = (WTDocument)oref.getObject();
                                    WTPartDescribeLink describeLink =   SearchErrorProcessDocUtility.getLinkByPartAndDoc(part,doc);
                                    if(describeLink==null){
                                        System.out.println("修复："+part.getNumber()+"  "+doc.getNumber()+"."+doc.getVersionIdentifier().getValue());
                                        WTPartUtil.createWTPartDescribeLink(part,doc);
                                    }
                                }
                            }
                        }
                    }

                }

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            } finally {
                SessionServerHelper.manager.setAccessEnforced(enforce);
            }
        }


    }

}
