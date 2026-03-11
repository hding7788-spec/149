package ext.ases.envelope;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ptc.core.meta.common.TypeIdentifierHelper;
import ext.casc.part.FaCiBomHelper;
import wt.doc.DocumentVersion;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTKeyedMap;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleManaged;
import wt.part.PartDocHelper;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;

import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;

import ext.casc.part.PackagedPartHelper;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

public class ProcessEnvelopeUtil {

    public static boolean isEqualsState(WTObject wto, String state) {
        if (wto instanceof LifeCycleManaged) {
            String objState = ((LifeCycleManaged) wto).getLifeCycleState().toString();
            if (objState.equals(state)) {
                return true;
            }
        }
        return false;
    }

    public static List<RevisionControlled> getPartReleatedDOC(WTPart part) throws WTException, RemoteException {
        List<RevisionControlled> docList = new ArrayList<RevisionControlled>();
        List objList = getRelatedWTObjectByPart(part);
        for (int i = 0; i < objList.size(); i++) {
            RevisionControlled obj = (RevisionControlled) objList.get(i);
            if (obj instanceof WTDocument) {
                // TODO 比较代码
                WTDocument doc = (WTDocument) obj;
                String typeStr = TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc).toString();
                if (typeStr.indexOf(EnvelopeConstants.TYPE_DOC_TUYANG) != -1) {
                    // CSCDebug.outDebugInfo("*************************是图样类型");
                    docList.add(obj);
                } else {// modified by zf 20100420,增加明细表类型
                    String docSubType = IBAHelper.getIBAStringValue(obj, "SUBTYPE");
                    if ("明细表".equals(docSubType))
                        docList.add(obj);
                }
            }
        }
        QueryResult cadDocRs = getAssociatedCADDocuments(part);
        if (cadDocRs != null) {
            while (cadDocRs.hasMoreElements()) {
                docList.add((RevisionControlled) cadDocRs.nextElement());
            }
        }
        return docList;
    }

    public static List<RevisionControlled> getPartBOMData(WTPart part) throws WTRuntimeException, WTException,
            RemoteException {
        List<RevisionControlled> wtoList = new ArrayList<RevisionControlled>();
        WTContainer container = part.getContainer();
        List<WTPart> partList = FilterPackagedPart(part, container, 0);
        // partList.add(part);
        for (int i = 0; i < partList.size(); i++) {
            WTPart tempPart = partList.get(i);
            // CSCDebug.outDebugInfo("*********next step is getPartReleatedDoc!!");
            List<RevisionControlled> tempOBJList = getPartReleatedDOC(tempPart);
            wtoList.add(tempPart);
            if (tempOBJList.size() > 0) {
                wtoList.addAll(tempOBJList);
            }
        }
        return wtoList;
    }

    /**
     * 过滤部件
     * 
     * @param wtPart
     * @return
     * @throws WTException
     */
    public static List<WTPart> FilterPackagedPart(WTPart wtPart, WTContainer container, int level) throws WTException {
        List<WTPart> resultList = new ArrayList<WTPart>();
        if (wtPart == null)
            throw new WTException("传入 part 为空");
        IBAUtility ibaUtility = new IBAUtility(wtPart);
        String isPackagedPart = ibaUtility.getIBAValue("SETMARK");
        System.out.println("-------isPackagedPart:" + isPackagedPart);
        // 借用件
        boolean isJieYong = !wtPart.getContainer().equals(container);

        QueryResult childPartsQs = WTPartHelper.service.getUsesWTPartMasters(wtPart);
        System.out.println("-------isPackagedPart:" + isPackagedPart + "    isJieYong:" + isJieYong);
        // 获取子元素
        if (isPackagedPart == null || !isPackagedPart.equals("是") && !isJieYong) {
            resultList.add(wtPart);
            while (childPartsQs.hasMoreElements()) {
                WTPartMaster childPartMaster = ((WTPartMaster) ((WTPartUsageLink) childPartsQs.nextElement())
                        .getRoleBObjectRef().getObject());
                // 遍历子part
                QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(childPartMaster);
                // 最新的在第一个元素
                if (allVersionsQs.hasMoreElements()) {
                    WTPart childLatestPart = (WTPart) allVersionsQs.getEnumeration().nextElement();
                    resultList.addAll(FilterPackagedPart(childLatestPart, container, level));
                } else {
                    // 找不到最新版本，系统错误
                    throw new WTException(childPartMaster.getDisplayIdentifier() + " 无法找到最新的版本！");
                }
            }
        } else {
            if (level++ == 0) {
                while (childPartsQs.hasMoreElements()) {
                    WTPartMaster childPartMaster = ((WTPartMaster) ((WTPartUsageLink) childPartsQs.nextElement())
                            .getRoleBObjectRef().getObject());
                    // 遍历子part
                    QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(childPartMaster);
                    // 最新的在第一个元素
                    if (allVersionsQs.hasMoreElements()) {
                        WTPart childLatestPart = (WTPart) allVersionsQs.getEnumeration().nextElement();
                        resultList.addAll(FilterPackagedPart(childLatestPart, container, level));
                    } else {
                        // 找不到最新版本，系统错误
                        throw new WTException(childPartMaster.getDisplayIdentifier() + " 无法找到最新的版本！");
                    }
                }
            }
        }

        return resultList;
    }

    public static QueryResult getAssociatedCADDocuments(WTPart wtpart) throws WTException {
        WTArrayList wtarraylist = new WTArrayList();
        wtarraylist.add(wtpart);
        WTKeyedMap wtkeyedmap = PartDocHelper.service.getAssociatedCADDocuments(wtarraylist);
        WTCollection wtcollection = (WTCollection) wtkeyedmap.get(wtpart);
        return getDocs(wtcollection);
    }

    private static QueryResult getDocs(WTCollection wtcollection) {
        QueryResult queryresult = new QueryResult();
        try {
            if (wtcollection != null) {
                ObjectVector objectvector = new ObjectVector();
                DocumentVersion documentversion;
                for (Iterator iterator = wtcollection.persistableIterator(); iterator.hasNext(); objectvector
                        .addElement(documentversion)) {
                    documentversion = (DocumentVersion) iterator.next();
                }
                queryresult.appendObjectVector(objectvector);
            }
        } catch (WTException wtexception) {
            wtexception.printStackTrace();
        }
        return queryresult;
    }

    /**
     * Get Related WTObjects by a document number. Includes Related Parts and Related Documents
     * 
     * @return ArrayList with WTObjects
     * @throws WTException
     */
    public static ArrayList getRelatedWTObjectByPart(WTPart part) throws WTException {
        ArrayList result = new ArrayList();
        QueryResult qr = WTPartHelper.service.getDescribedByDocuments(part);

        while (qr.hasMoreElements()) {
            Persistable descPersist = (Persistable) qr.nextElement();
            if (descPersist instanceof WTDocument) {
                WTDocument descDoc = (WTDocument) descPersist;
                result.add(descDoc);
            } else if (descPersist instanceof EPMDocument) {
                EPMDocument descEPMDoc = (EPMDocument) descPersist;
                result.add(descEPMDoc);
            }
        }

        qr = WTPartHelper.service.getReferencesWTDocumentMasters(part);
        while (qr.hasMoreElements()) {
            WTDocumentMaster refDocMaster = (WTDocumentMaster) qr.nextElement();
            WTDocument refDoc = getDoc(refDocMaster.getNumber());
            if (!result.contains(refDoc))
                result.add(refDoc);
        }

        return result;
    }

    public static RevisionControlled getTopObject(ProcessEnvelope processEnvelope)
            throws WTException {
        // ##begin getTopObject%4B5470C40350g.body preserve=yes
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "topObject",
                ext.ases.envelope.EnvelopeTopObjLink.class, false);
        RevisionControlled obj = null;
        while (qr.hasMoreElements()) {
            obj = ((EnvelopeTopObjLink) qr.nextElement()).getTopObject();
        }
        return obj;
        // ##end getTopObject%4B5470C40350g.body
    }

    /**
     * This is used to get WTDocument by its number.
     * 
     * @param docNumber
     *            document number
     * @return WTDocument or null if there is no document with the number in system.
     */
    public static WTDocument getDoc(String docNumber) {
        WTDocument doc = null;
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QuerySpec qs = new QuerySpec(WTDocument.class);
            SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
                    docNumber, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                WTDocument document = (WTDocument) qr.nextElement();
                QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
                if (qr2.hasMoreElements()) {
                    doc = (WTDocument) qr2.nextElement();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return doc;
    }

    public static List getAllMemberLinks(ProcessEnvelope processEnvelope) throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled",
                EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        List envelopeMembertLinks = new ArrayList();
        for (; qr.hasMoreElements(); envelopeMembertLinks.add(obj)) {
            obj = ((EnvelopeMemberLink) qr.nextElement()).getRevisionControlled();
        }
        return envelopeMembertLinks;
    }

    public static ArrayList getAllMembers(ProcessEnvelope processEnvelope)
            throws WTException {
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled",
                EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        ArrayList envelopeMembertLinks = new ArrayList();
        for (; qr.hasMoreElements(); envelopeMembertLinks.add(obj)) {
            obj = ((EnvelopeMemberLink) qr.nextElement()).getRevisionControlled();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return envelopeMembertLinks;
    }

    //增加发次BOM支持
    public static ArrayList getAllMembersNoPart(WorkItem workitem, ProcessEnvelope processEnvelope)
    {

        try {
            WfActivity wfAct = (WfActivity) workitem.getSource().getObject();
            WfProcess wfProcess = wfAct.getParentProcess();
            if(FaCiBomHelper.FACI_BOM_WORKFLOW.equals(wfProcess.getTemplate().getName()))
            {
                ArrayList list = getAllFaCiBom(processEnvelope);
                if(list.size()>0)
                {
                    return list;
                }
            }

            return getAllMembersNoPartImpl(processEnvelope);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }

    //增加发次BOM支持
    private static ArrayList getAllMembersNoPartImpl(ProcessEnvelope processEnvelope)
            throws WTException {
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled",
                EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        ArrayList envelopeMembertLinks = new ArrayList();
        while(qr.hasMoreElements()){
            obj = ((EnvelopeMemberLink) qr.nextElement()).getRevisionControlled();
            if(!(obj instanceof WTPart)){
                envelopeMembertLinks.add(obj);
            }
            
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return envelopeMembertLinks;
    }

    public static List getAllMembersList(ProcessEnvelope processEnvelope)
            throws WTException {
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled",
                EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        List list = new ArrayList();
        Set<WTPart> partMember = new HashSet<WTPart>();
        Set<WTDocument> docMember = new HashSet<WTDocument>();
        Set<String> docNumbers = new HashSet<String>();
        Set<EPMDocument> cadMember = new HashSet<EPMDocument>();
        Set<String> cadNumbers = new HashSet<String>();
        while (qr.hasMoreElements()) {
            obj = ((EnvelopeMemberLink) qr.nextElement()).getRevisionControlled();
            if (obj instanceof WTPart) {
                partMember.add((WTPart) obj);
            } else if (obj instanceof WTDocument) {
                WTDocument document = (WTDocument) obj;
                docMember.add(document);
                docNumbers.add(document.getNumber());
            } else if (obj instanceof EPMDocument) {
                EPMDocument epmDocument = (EPMDocument) obj;
                cadMember.add(epmDocument);
                cadNumbers.add(epmDocument.getNumber());
            }
        }
        list.add(partMember);
        list.add(docMember);
        list.add(docNumbers);
        list.add(cadMember);
        list.add(cadNumbers);
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return list;
    }

    public static ArrayList getAllDocMembersNumber(ProcessEnvelope processEnvelope)
            throws WTException {
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled",
                EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        ArrayList docMember = new ArrayList();
        while (qr.hasMoreElements()) {
            obj = ((EnvelopeMemberLink) qr.nextElement()).getRevisionControlled();
            if (obj instanceof WTDocument) {
                docMember.add(((WTDocument) obj).getNumber());
            }
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return docMember;
    }

    public static ArrayList getAllCadMembersNumber(ProcessEnvelope processEnvelope)
            throws WTException {
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled",
                EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        ArrayList partMember = new ArrayList();
        while (qr.hasMoreElements()) {
            obj = ((EnvelopeMemberLink) qr.nextElement()).getRevisionControlled();
            if (obj instanceof WTPart) {
                partMember.add(((EPMDocument) obj).getNumber());
            }
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return partMember;
    }

    public static void filterPartRefDocument(WTPart part, Set<String> docNumbers, Set docList, Set<String> cadNumbers,
            Set cadList) {
        try {
            List list = PackagedPartHelper.getPartReleatedDOC(part);
            for (Object object : list) {
                if (object instanceof WTDocument) {
                    WTDocument document = (WTDocument) object;
                    String number = document.getNumber();
                    if (docNumbers.contains(number)) {
                        docList.add(object);
                    }
                } else if (object instanceof EPMDocument) {
                    EPMDocument epmDocument = (EPMDocument) object;
                    String number = epmDocument.getNumber();
                    if (cadNumbers.contains(number)) {
                        cadList.add(object);
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
    }

    public static QueryResult getProcessEnvelopeByNumber(String number) throws WTException {
        QuerySpec qs = new QuerySpec(ProcessEnvelope.class);
        int[] index = { 0 };
        SearchCondition sc = new SearchCondition(ProcessEnvelope.class,ProcessEnvelope.NUMBER,SearchCondition.EQUAL,number);
        qs.appendWhere(sc, index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
        return qr;
    }

    //发次BOM
    public static ArrayList getAllFaCiBom(ProcessEnvelope processEnvelope)
            throws WTException {
        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled",
                EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        ArrayList result = new ArrayList();
        while(qr.hasMoreElements()){
            obj = ((EnvelopeMemberLink) qr.nextElement()).getRevisionControlled();
            if(obj instanceof WTPart){
                WTPart part = (WTPart) obj;
                String partType = TypeIdentifierHelper.getType(part).toString();
                String ctype = IBAHelper.getIBAStringValue(part,"CTYPE");
                if(partType.endsWith(FaCiBomHelper.FACI_BOM_TYPE)||"备料".equals(ctype))
                {
                    result.add(obj);
                }
            }

        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return result;
    }


}
