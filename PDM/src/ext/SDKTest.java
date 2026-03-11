package ext;

import com.glaway.mpm.util.*;
import com.glaway.mpm.util.ReferenceFactory;
import com.ptc.windchill.cadx.common.util.WorkspaceUtilities;
import com.ptc.windchill.enterprise.history.HistoryTablesCommands;
import com.ptc.windchill.enterprise.note.commands.NoteServiceCommand;
import com.ptc.windchill.enterprise.part.commands.AssociationLinkObject;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.uwgm.common.autoassociate.AutoAssociateHelper;
import com.ptc.windchill.uwgm.common.navigate.AssociationTracer;
import com.ptc.windchill.uwgm.common.util.BuildHelper;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import wt.conflict.ConflictResolution;
import wt.doc.WTDocument;
import wt.epm.EPMDocConfigSpec;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMasterIdentity;
import wt.epm.build.EPMBuildRule;
import wt.epm.modelitems.ModelItemHelper;
import wt.epm.retriever.ResultGraph;
import wt.epm.util.EPMConfigSpecFilter;
import wt.epm.util.EPMFilters;
import wt.fc.*;
import wt.fc.collections.*;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.Foldered;
import wt.iba.value.IBAHolder;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.part.*;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.*;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.*;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.wip.WorkInProgressHelper;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

public class SDKTest {
    public static void main(String[] args) throws WTException {
       //getIBA();
       // deleteVersion();
        //link();
        //getUsesWTParts();
        //checkOut();
       // getAssociatedCADDocumentsAndLinks();
       // getAssociatedParts();
        //allVersionsOf2();
       //checkOutOnly();
       // insertUsageLink();
        //undocheckOut();
       // checkIn();
        //batchCheckOut();
        //maturityHistory();
        //buildFromCAD();
        //getRelatedParts();
        //navigate_usageLink();
        //findContainerTeamGroups();
        //getAssociatedWorkspace();
       // getAssociatedCADDocuments();
        // changeIdentity();
        bigParams();
    }

    private static void bigParams() throws WTException {
        List<Long> ida2a2s = new ArrayList<>();
        for(int i=1;i<5000;i++){
            ida2a2s.add(34685l+i);
        }

        QuerySpec qSpec = new QuerySpec(WTPartUsageLink.class);
        qSpec.setAdvancedQueryEnabled(true);
        SearchCondition condition = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"), SearchCondition.IN, new ArrayExpression(ida2a2s.toArray()));
        SearchCondition condition2 = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleBObjectRef.key.id"), SearchCondition.IN, new ArrayExpression(ida2a2s.toArray()));

        qSpec.appendWhere(condition, new int[]{0});
        qSpec.appendAnd();
        qSpec.appendWhere(condition2, new int[]{0});
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        while(qResult.hasMoreElements()){
            WTPartUsageLink part = (WTPartUsageLink) qResult.nextElement();
           // System.out.println(part.toString());
        }
        System.out.println("查询完毕，共计："+qResult.size()+"条数据。"  );

    }

    private static void test() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("cmd /c "+"notepad.exe");
        int exitCode = process.waitFor();
        System.out.println("Exited with code: " + exitCode);
        process.destroyForcibly();
    }
    private static void changeIdentity() {
        try{
            EPMDocument epm = (EPMDocument)ReferenceFactory.getObjectbyOid("VR:wt.epm.EPMDocument:767633");
            Identified master = (Identified)epm.getMaster();
            EPMDocumentMasterIdentity masterIdentity = (EPMDocumentMasterIdentity)master.getIdentificationObject();
            masterIdentity.setName(epm.getName()+"_DEL");
            masterIdentity.setNumber(epm.getNumber()+"_DEL");
            IdentityHelper.service.changeIdentity(master,masterIdentity);

            String oldCadName = epm.getCADName();
            int lastDotIndex = oldCadName.lastIndexOf('.');
            if (lastDotIndex != -1) {
                String prefix = oldCadName.substring(0, lastDotIndex);
                String suffix = oldCadName.substring(lastDotIndex + 1);

                WTKeyedMap cadNameMap = new WTKeyedHashMap();
                cadNameMap.put(master,prefix+"_DEL."+suffix);
                EPMDocumentHelper.service.changeCADName(cadNameMap);
                PersistenceHelper.manager.refresh(epm) ;
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public  static void getAssociatedWorkspace() throws WTException {
        //1522459
        EPMDocument epm = (EPMDocument)ReferenceFactory.getObjectbyOid("VR:wt.epm.EPMDocument:767633");
        System.out.println(FolderHelper.getFolder((Foldered)epm)); 
        System.out.println( "result:"+WorkspaceUtilities.getAssociatedWorkspace(epm));
    }
    public  static void findContainerTeamGroups() throws WTException {
        PDMLinkProduct product = (PDMLinkProduct)ReferenceFactory.getObjectbyOid("OR:wt.pdmlink.PDMLinkProduct:130178");
        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product);

        System.out.println("####ContainerTeamHelper.service.findContainerTeamGroups()");
        Enumeration accessGroups = ContainerTeamHelper.service.findContainerTeamGroups(containerTeam, "accessGroups");
        System.out.println("####result : accessGroups="+accessGroups);
        while(accessGroups.hasMoreElements()){
            System.out.println("####result : "+accessGroups.nextElement());
        }

    }
    public  static void getRelatedParts() throws WTException {
        EPMDocument epm = (EPMDocument)ReferenceFactory.getObjectbyOid("VR:wt.epm.EPMDocument:1522459");

        Object o = NoteServiceCommand.getRelatedParts(epm);
        System.out.println("11="+o);

    }
    public  static void buildFromCAD() throws WTException {
        EPMDocument epm = (EPMDocument)ReferenceFactory.getObjectbyOid("OR:wt.epm.EPMDocument:1522588");

        ConfigSpec config  = new EPMConfigSpecFilter(EPMDocConfigSpec.newEPMDocConfigSpec(), EPMFilters.NO_WORKING);

         WTArrayList list = new WTArrayList(1);
        list.add(epm);
        BuildHelper.buildFromCAD(list,config,true);

    }



    public static void maturityHistory(){
        try {
            WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:207736");
            HistoryTablesCommands.maturityHistory(part);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }
    public static void batchCheckOut(){
        try {
            QuerySpec spec = new QuerySpec(WTPart.class);
            spec.appendWhere(new SearchCondition(WTPart.class, WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE ));
            PagingQueryResult res = PagingSessionHelper.openPagingSession(0, 1000, spec);
            WTSet wtSet = new WTHashSet();
            while(res.hasMoreElements()) {
                Object[] obj = (Object[]) res.nextElement();
                wtSet.add(obj[0]);
            }

            Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
            WTCollection collection = WorkInProgressHelper.service.checkout(wtSet,folder,"");
            WorkInProgressHelper.service.checkin(collection,"batch checkIn");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertUsageLink(){
        try {
            Transaction tx = new Transaction();
            tx.start();

            WTPart parent = (WTPart) ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:5311192");

            WTPart child = (WTPart) ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:207949");
            System.out.println(parent);
            System.out.println(child);
            WTPartUsageLink link =WTPartUsageLink.newWTPartUsageLink(parent,child.getMaster());
            PersistenceHelper.manager.save(link);
            tx.commit();
            tx = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void checkOutOnly(){
        try {
            WTPart part = (WTPart) ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:1522452");
            Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
            WorkInProgressHelper.service.checkout(part,folder,"");
        } catch (Exception e) {
          e.printStackTrace();
        }

    }

    public static void undocheckOut(){
        try {
            WTPart part = (WTPart) ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:1522452");
            WorkInProgressHelper.service.undoCheckout(part);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void checkIn(){
        try {
            WTPart part = (WTPart) ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:5408277");
            WorkInProgressHelper.service.checkin(part,"test");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void remove(){
        try {
            WTPartUsageLink link = (WTPartUsageLink) ReferenceFactory.getObjectbyOid("OR:wt.part.WTPartUsageLink:5184562");
            PersistenceServerHelper.manager.remove(link);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public static void deleteLatestIteration(){
        try {
            WTPart part = (WTPart) ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:5293508");
            ConflictResolution[] aconflictresolution ={new ConflictResolution(VersionControlConflictType.LATEST_ITERATION_DELETE, VersionControlResolutionType.ALLOW_LATEST_ITERATION_DELETE)};
            WTSet set = new WTHashSet();
            set.add(part);
            VersionControlHelper.service.deleteIterations(set, aconflictresolution);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public  static void getAssociatedReferenceDocuments() throws WTException {
        WTPart part = (WTPart) ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:207736");
        QueryResult qr = PartDocServiceCommand.getAssociatedReferenceDocuments(part);
        System.out.println("####qr.size()="+qr.size());
    }

    public  static void saveAsEpm() throws WTException {

    }

    public  static void getIBA() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:207736");

        Hashtable values = IBAHelper.getAllIBAValues(part);
        System.out.println(values);


        System.out.println(part.getContainerName());
        System.out.println(part.getFolderPath());
        System.out.println(part.getContainer());
        System.out.println(part.getCreator());
        System.out.println(part.getView().getObject());
        System.out.println(part.getMaster());
        System.out.println(part.getFolderPath());




    }
    public  static void updateIBA() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:207736");

        IBAUtility iba = new IBAUtility((IBAHolder)part);
        try {
            iba.setIBAValue( "ClassificationNode","分类22");
            part = (WTPart) iba.updateAttributeContainer(part);
            iba.updateIBAHolder(part);
        } catch (WTPropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public  static void checkOut() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:1522452");
        try {
            part = (WTPart) WorkInProcessUtil.checkout(part);
            part = (WTPart) WorkInProcessUtil.checkin(part,"test");

        } catch (WTPropertyVetoException e) {
            throw new RuntimeException(e);
        }

    }

    public  static void excute3() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:5219445");
        try {
            VersionIdentifier vi = VersionControlHelper.nextVersionId(part);
            IterationIdentifier ii = VersionControlHelper.firstIterationId(part);
            WTPart newPart = (WTPart) VersionControlHelper.service.newVersion(part, vi, ii);
            newPart = (WTPart) PersistenceHelper.manager.store(newPart);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public static WTPart deleteVersion(){
        try {
            WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:5295382");

            WTPart newPart = (WTPart) PersistenceHelper.manager.delete(part);
            return newPart;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public  static void delete() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:5219445");
        try {
            VersionIdentifier vi = VersionControlHelper.nextVersionId(part);
            IterationIdentifier ii = VersionControlHelper.firstIterationId(part);
            WTPart newPart = (WTPart) VersionControlHelper.service.newVersion(part, vi, ii);
            newPart = (WTPart) PersistenceHelper.manager.store(newPart);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public  static void link() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:5219445");
        WTDocument doc = (WTDocument)ReferenceFactory.getObjectbyOid("VR:wt.doc.WTDocument:5241969");
        try {
            QueryResult queryresult = PersistenceHelper.manager.find(WTPartDescribeLink.class, part, "describes", doc);
            while(queryresult.hasMoreElements()){
                WTPartDescribeLink link =  (WTPartDescribeLink)queryresult.nextElement();
                link.getRoleAObject();
                link.getRoleBObject();
            }
            System.out.println("END");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public  static void getUsesWTParts() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:5219445");
        QueryResult qr = WTPartHelper.service.getUsesWTParts(part, getDefaultConfigSpec());

        while(qr.hasMoreElements()) {
            Persistable[] per = (Persistable[]) ((Persistable[]) qr.nextElement());
            System.out.println(per[0]);
            System.out.println(per[1]);
            System.out.println("---next---");
        }

    }
    private static ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

    public  static void link2() throws WTException {
        WTPartUsageLink usageLink = (WTPartUsageLink)ReferenceFactory.getObjectbyOid("OR:wt.part.WTPartUsageLink:5294992");
        usageLink.getRoleAObject();
        usageLink.getRoleBObject();

    }
    public  static void navigate() throws WTException {
        EPMDocument epm = (EPMDocument)ReferenceFactory.getObjectbyOid("VR:wt.epm.EPMDocument:1522459");

        QueryResult qr = PersistenceHelper.manager.navigate(epm,"roleBObject", EPMBuildRule.class,false);
        while(qr.hasMoreElements()){
            EPMBuildRule buildRule = (EPMBuildRule) qr.nextElement();
            System.out.println(buildRule);
        }

    }

    public  static void navigate_usageLink() throws WTException {
        WTPartMaster master = (WTPartMaster) ReferenceFactory.getObjectbyOid("OR:wt.part.WTPartMaster:4085875");

        QueryResult qr = PersistenceHelper.manager.navigate(master,"roleAObject", WTPartUsageLink.class,false);
        while(qr.hasMoreElements()){
            WTPartUsageLink usageLink = (WTPartUsageLink) qr.nextElement();
            System.out.println("###usageLink:"+usageLink);
        }

    }

    public  static void getAssociatedCADDocuments() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:5408277");

        QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
        while(qr.hasMoreElements()){
            System.out.println("====="+qr.nextElement());
        }

    }

    public  static void getAssociatedParts() throws WTException {
        WTDocument document = (WTDocument)ReferenceFactory.getObjectbyOid("VR:wt.doc.WTDocument:5054866");

        QueryResult qr = PartDocServiceCommand.getAssociatedParts(document);
        while(qr.hasMoreElements()){
            System.out.println("查询结果====="+qr.nextElement());
        }

    }

    public  static void getAssociatedCADDocumentsAndLinks() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:134685");
        List<AssociationLinkObject> collection =(List<AssociationLinkObject> ) PartDocServiceCommand.getAssociatedCADDocumentsAndLinks(part);
       for(AssociationLinkObject linkObject:collection){
           System.out.println("linkObject.getCadObject()=="+linkObject.getCadObject());
           System.out.println("linkObject.getLink()=="+ linkObject.getLink());
       }

    }
    public  static void allVersionsFrom() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:5293583");
        QueryResult queryResult = VersionControlHelper.service.allVersionsFrom(part);
        System.out.println("查询结果输出：queryResult.size()====="+queryResult.size());
        while(queryResult.hasMoreElements()){
            WTPart tmp = (WTPart)queryResult.nextElement();
            System.out.println("查询结果输出：====="+tmp.getIdentity());
        }

    }

    public  static void allVersionsOf() throws WTException {
        WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:5293583");
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(part.getMaster());
        System.out.println("查询结果输出：queryResult.size()====="+queryResult.size());
        while(queryResult.hasMoreElements()){
            WTPart tmp = (WTPart)queryResult.nextElement();
            System.out.println("查询结果输出：====="+tmp.getIdentity());
        }

    }
    public  static void allVersionsOf2() throws WTException {
        EPMDocument part = (EPMDocument)ReferenceFactory.getObjectbyOid("OR:wt.epm.EPMDocument:5049088");
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(part.getMaster());
        System.out.println("查询结果输出：queryResult.size()====="+queryResult.size());
        while(queryResult.hasMoreElements()){
            EPMDocument tmp = (EPMDocument)queryResult.nextElement();
            System.out.println("查询结果输出：====="+tmp.getIdentity());
        }

    }
    public  static void getAssociatedResultGraph() throws WTException {
        EPMDocument epm = (EPMDocument)ReferenceFactory.getObjectbyOid("VR:wt.epm.EPMDocument:1522459");
        WTArrayList localWTArrayList1 = new WTArrayList();
        localWTArrayList1.add(epm);

        WTKeyedMap keyedMap = ModelItemHelper.manager.getModelItems(localWTArrayList1);
        System.out.println("查询结果输出：ModelItemHelper.manager.getModelItems(localWTArrayList1)====="+keyedMap);

        WTHashSet hashSet =addModelItems2Seeds(localWTArrayList1,keyedMap);
        WTArrayList arrayList = new WTArrayList(hashSet);
        System.out.println("查询结果输出：arrayList====="+arrayList);

        ResultGraph resultGraph = AssociationTracer.getAssociatedResultGraph(arrayList, null, AssociationTracer.NavigateModelItems.INCLUDE, AssociationTracer.Type.getAll());

        Map localMap = AutoAssociateHelper.getAssociatedLinkToObjectsMap(resultGraph, arrayList, false);

        Iterator iterator = localMap.keySet().iterator();
        while(iterator.hasNext()){
            Object localObject1 = iterator.next();
            Object localObject2 = localMap.get(localObject1);
            System.out.println("查询结果输出：localObject1====="+localObject1);
            System.out.println("查询结果输出：localObject2====="+localObject2);
            System.out.println("查询结果输出：==============循环分割线=====");

        }

    }

    private  static WTHashSet addModelItems2Seeds(WTCollection param,WTKeyedMap keyedMap){
        WTHashSet hashSet = new WTHashSet();
        hashSet.addAll(param.persistableCollection());
        for(Iterator localIterator = keyedMap.entrySet().iterator();localIterator.hasNext();){
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            WTHashSet localHastSet = (WTHashSet)localEntry.getValue();
            hashSet.add(localHastSet);
        }
        return hashSet;
    }

}
