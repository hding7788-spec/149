package ext.casc.doc;

import ext.casc.persistence.PersistenceCommonHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.part.WTPartDescribeLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.ArrayList;
import java.util.List;

public class DocumentCommonHelper extends PersistenceCommonHelper {

    private static final String ROLEB_ID = "roleBObjectRef.key.id";
    private static final String ROLEA_ID = "roleAObjectRef.key.id";

    public  static  void changeName(WTDocument doc, String newName) throws WTException, WTPropertyVetoException {
        WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
        WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
        idy.setName(newName);
        master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
    }

    public static void removePartDescLink(WTDocument doc) throws WTException {
        List list = getDocDescribeLinksByDoc(doc);
        WTPartDescribeLink wtPartDescribeLink = null;
        for (int i = 0; i < list.size(); i++) {
            wtPartDescribeLink = (WTPartDescribeLink) list.get(i);
            PersistenceServerHelper.manager.remove(wtPartDescribeLink);
        }
    }


    public static List getDocDescribeLinksByDoc(WTDocument doc) throws WTException {
        List list = new ArrayList();
        QuerySpec qSpec = new QuerySpec(WTPartDescribeLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(doc).getId();
        SearchCondition sCondition = new SearchCondition(WTPartDescribeLink.class, ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        WTPartDescribeLink link = null;
        while (qResult.hasMoreElements()) {
            link = (WTPartDescribeLink) qResult.nextElement();
            list.add(link);
        }
        return list;
    }

}
