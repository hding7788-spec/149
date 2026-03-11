package ext.test;

import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import ext.casc.constants.PDMConfig;
import wt.conflict.ConflictResolution;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.IterationIdentifier;
import wt.vc.VersionControlConflictType;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlResolutionType;
import wt.vc.VersionIdentifier;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PartScript1 implements RemoteAccess {
    private static final int COUNT = 1;
    public static void main(String[] args) throws WTException {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        rms.setUserName("wcadmin");
        rms.setPassword("Admin@149");
        excute(args[0]) ;
    }
    public  static void excute(String oid) throws WTException {

        if (!RemoteMethodServer.ServerFlag) {
            Class[] argTypes = {String.class};
            Object[] args = {oid};
            try {
                SessionHelper.manager.setPrincipal("administrator");
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                //				server.setPassword("wcadmin");
                server.setPassword("Admin@149");
                server.invoke("excute", PartScript1.class.getName(), null, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        } else {
            for(int i = 0;i<COUNT;i++){

                totalProcess(oid,i);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void totalProcess(String oid,int count ){
        //1通过OID获取部件
        WTPart part = p1(oid);
        //2获取对应视图名称
        part.getViewName();
        //3获取对应视图对象
        part.getView().getObject();
        //allVersionsOf
        allVersionsOf(part);
        //allIterationsOf
        allIterationsOf(part);

        //4 获取下一级子件
        getUsesWTParts(part);
        //5	获取所有父件
        getUsedByWTParts(part);
        //6	获取说明方文档
        getReferencesWTDocumentMasters(part);

        //7	移除说明方文档（只能执行一次） 取消

        //8	获取描述文档
        getDescribedByWTDocuments(part);
        //9	移除删除描述文档（只能执行一次） 取消

        //10获取三维模型和二维图纸
        getAssociatedCADDocuments(part);
        //11	获取所有软属性

        //12	获取所有分类属性

        //13	获取生命周期状态
        getState(part);

        //14	更新1个软属性

        //15	更新多个个软属性

        //16	更新1个扩展硬属性

        //17	更新多个个扩展硬属性

        //18	设置生命周期状态  往后多跳几个状态设置
        setLifeCycleState(part,"APPROVED");

        //19	重命名
        reName(part,part.getName()+count);
        //20	checkOut
        WTPart workCopy = checkout(part);
        //21	isCheckOut
        isCheckedOut(workCopy);
        //22	undoCheckout
        part = undoCheckout(workCopy);

        //23	checkOut
        workCopy = checkout(part);

        //24	checkIn
        WTPart newWTPart = checkin(workCopy);
        //25	删除最新小版本
        deleteLatestIteration(newWTPart);

        //26	修订
        WTPart newVersion = newVersion(part);

        //27	删除修订版本
        deleteVersion(newVersion);

    }

    public static WTPart p1(String oid){
        long start = System.currentTimeMillis();
        try {
            ReferenceFactory rf = new ReferenceFactory();
            WTPart p = (WTPart) rf.getReference(oid).getObject();
            long end = System.currentTimeMillis();
            //记录时间（end-start）
            return p;
        } catch (WTException e) {
            throw new RuntimeException(e);
        }
    }
    public static void  allVersionsOf(WTPart part){
        try {
            VersionControlHelper.service.allVersionsOf(part.getMaster());
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }

    public static void  allIterationsOf(WTPart part){
        try {
            VersionControlHelper.service.allIterationsOf(part.getMaster());
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<WTPart> getUsesWTParts(WTPart part){
        List<WTPart> resultList = new ArrayList<WTPart>();
        try {
            QueryResult qr = WTPartHelper.service.getUsesWTParts( part, getConfigSpec());
            while (qr.hasMoreElements()) {
                Persistable[] per = (Persistable[]) qr.nextElement();
                Object obj = per[1];
                if (obj instanceof WTPart) {
                    resultList.add((WTPart) obj);
                } else if (obj instanceof WTPartMaster) {
                    WTPartMaster master = (WTPartMaster) obj;
                    QueryResult qr2 = VersionControlHelper.service.allVersionsOf(master);
                    if (qr2.hasMoreElements()) {
                        WTPart  childPart = (WTPart) qr.nextElement();
                        resultList.add(childPart);

                    }
                }

            }
        } catch (WTException e) {
            throw new RuntimeException(e);
        }
        return resultList;

    }

    public static void getUsedByWTParts(WTPart part){
        try {
            WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }

    public static void getDescribedByWTDocuments(WTPart part){
        try {
            QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }

    public static void getReferencesWTDocumentMasters(WTPart part){
        try {
            QueryResult qr2 = WTPartHelper.service.getReferencesWTDocumentMasters(part);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }
    public static void getState(WTPart part ){
        part.getState().getState().getDisplay(Locale.CHINA);

    }
    public static void setLifeCycleState(WTPart part,String state ){
        try {
            State lfState = State.toState(state);
            LifeCycleHelper.service.setLifeCycleState(part, lfState);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }

    public static void getAssociatedCADDocuments(WTPart part){
        try {
            QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }


    public static void reName(WTPart part,String newName){
        try {
            WTPartMaster master = (WTPartMaster) part.getMaster();
            WTPartMasterIdentity idy = (WTPartMasterIdentity) master.getIdentificationObject();
            idy.setName(newName);
            master = (WTPartMaster) IdentityHelper.service.changeIdentity(master, idy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static WTPart checkout(WTPart part){
        try {
            CheckoutLink checkoutlink =  WorkInProgressHelper.service.checkout(part, WorkInProgressHelper.service.getCheckoutFolder(), "");

            WTPart workingPart = (WTPart) checkoutlink.getWorkingCopy();
            return workingPart;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void isCheckedOut(WTPart part){
        try {
            boolean isCheckOut = WorkInProgressHelper.isCheckedOut(part);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public static WTPart undoCheckout(WTPart part){
        try {
            WTPart tp =  (WTPart) WorkInProgressHelper.service.undoCheckout(part);
            return tp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static WTPart checkin(WTPart part){
        try {
            WTPart newPart = (WTPart) WorkInProgressHelper.service.checkin(part,"autoTest");
            return newPart;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void deleteLatestIteration(WTPart part){
        try {
            ConflictResolution[] aconflictresolution ={new ConflictResolution(VersionControlConflictType.LATEST_ITERATION_DELETE,VersionControlResolutionType.ALLOW_LATEST_ITERATION_DELETE)};
            WTSet set = new WTHashSet();
            set.add(part);
            VersionControlHelper.service.deleteIterations(set, aconflictresolution);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static WTPart newVersion(WTPart part){
        try {
            VersionIdentifier vi = VersionControlHelper.nextVersionId(part);
            IterationIdentifier ii = VersionControlHelper.firstIterationId(part);
            WTPart newPart = (WTPart) VersionControlHelper.service.newVersion(part, vi, ii);
            newPart = (WTPart) PersistenceHelper.manager.store(newPart);
            return newPart;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static WTPart deleteVersion(WTPart part){
        try {
            WTPart newPart = (WTPart) PersistenceHelper.manager.delete(part);
            return newPart;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }










    private static ConfigSpec getConfigSpec() throws WTException {

        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);

    }

}
