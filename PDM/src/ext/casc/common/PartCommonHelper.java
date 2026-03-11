package ext.casc.common;

import com.glaway.mpm.util.WTPartUtil;
import ext.casc.part.CSCPart;
import ext.casc.util.Tools;
import org.dom4j.DocumentException;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class PartCommonHelper {
    public static List<WTDocument> getDescribedByWTDocuments(WTPart part) throws WTException, PropertyVetoException, DocumentException {
        List<WTDocument> docList  = new ArrayList<WTDocument>();
        QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qr2 = lcs.process(qr2);
        while (qr2.hasMoreElements()) {
            WTDocument document = (WTDocument) qr2.nextElement();
            docList.add(document);
        }
        return docList;
    }
    public static List<WTDocument> getLatestTechnicsDocumentByPart(WTPart part,String likeTypeCode) throws WTException {
        List<WTDocument> docList  = new ArrayList<WTDocument>();
        QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qr2 = lcs.process(qr2);
        while (qr2.hasMoreElements()) {
            WTDocument document = (WTDocument) qr2.nextElement();
            if(Tools.isNull(likeTypeCode)){
                docList.add(document);
            }else{
                if (TypedUtility.getTypeIdentifier(document).getTypename().contains(likeTypeCode)) {
                    docList.add(document);
                }
            }
        }
        return docList;
    }

    public static List<WTDocument> getLatestDescribedByWTDocuments(WTPart part) throws WTException {
        List<WTDocument> docList  = new ArrayList<WTDocument>();
        QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        while (qr2.hasMoreElements()) {
            WTDocument document = (WTDocument) qr2.nextElement();
            WTDocument latestDoc = (WTDocument) CSCPart.getLatestObject( document.getMaster());
            docList.add(latestDoc);
        }
        return docList;
    }

    public static List<WTDocument> getLatestDescribedByWTDocuments(WTPart part,String docType) throws WTException {
        List<WTDocument> docList  = new ArrayList<WTDocument>();
        QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        while (qr2.hasMoreElements()) {
            WTDocument document = (WTDocument) qr2.nextElement();
            if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains(docType)){
                WTDocument latestDoc = (WTDocument) CSCPart.getLatestObject( document.getMaster());
                docList.add(latestDoc);
            }

        }
        return docList;
    }
    /**
     * 获取指定零件的所有兄弟节点
     *
     * @return 兄弟节点的列表
     * @throws WTException
     */
    public static LinkedHashSet<WTPart> getSiblings(WTPart part) throws WTException {
        LinkedHashSet<WTPart> siblings = new LinkedHashSet<WTPart>();
        QueryResult parents = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
        while (parents.hasMoreElements()) {
            WTPart parentPart = (WTPart) parents.nextElement();
            if(parentPart.getViewName().equals("Manufacturing")){
                LinkedHashSet<WTPart> children =  getChildMParts(parentPart);
                if(children.contains(part)){
                    siblings.addAll(children);
                }
            }

        }
        return siblings;
    }

    public static LinkedHashSet<WTPart> getChildMParts(WTPart part) throws WTException{
        LinkedHashSet<WTPart> nodeList = new LinkedHashSet();
        if (part != null) {
            QueryResult qr = WTPartHelper.service.getUsesWTParts(part, WTPartUtil.getConfigSpec());
            while(qr.hasMoreElements()) {
                Persistable[] per = (Persistable[])qr.nextElement();
                Object obj = per[1];
                if (obj instanceof WTPart) {
                    WTPart cpart = (WTPart)obj;
                    String viewName = cpart.getViewName();
                    if ("Design".equals(viewName)) {
                        cpart = CSCPart.getPartByNumberAndViewName(cpart.getNumber(), "Manufacturing");
                    }
                    nodeList.add(cpart);
                } else if (obj instanceof WTPartMaster) {
                    WTPart  cpart = CSCPart.getPartByNumberAndViewName(((WTPartMaster) obj).getNumber(), "Manufacturing");
                    nodeList.add(cpart);
                }
            }
        }

        return nodeList;
    }

    public static List<WTPartUsageLink> getWTPartUsageLinkByRoleA(WTPart parent) throws WTException{
        List<WTPartUsageLink> links = new ArrayList<WTPartUsageLink>();
        int[] index = { 0 };
        QuerySpec qs = new QuerySpec(WTPartUsageLink.class);
        qs.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id", "=", PersistenceHelper
                .getObjectIdentifier(parent).getId()),index);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        while (qr.hasMoreElements()){
            WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
            links.add(link);
        }
        return links;
    }

    public static EPMDocument getEPMDocumentByPart(WTPart part){
        EPMDocument epm = null;
        try {
            long oid = VersionControlHelper.getBranchIdentifier(part);
            QuerySpec qs = new QuerySpec(EPMBuildRule.class);
            qs.appendWhere(new SearchCondition(EPMBuildRule.class, "roleBObjectRef.key.branchId", "=", oid), new int[]{0});
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
            while(qr.hasMoreElements()){
                EPMBuildRule rule = (EPMBuildRule) qr.nextElement();
                Persistable per = rule.getRoleAObject();
                if(per instanceof EPMDocument){
                    epm = (EPMDocument) per;
                }

            }

        } catch (VersionControlException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (QueryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return epm;
    }

}
