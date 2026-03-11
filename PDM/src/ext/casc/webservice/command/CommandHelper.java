package ext.casc.webservice.command;

import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class CommandHelper {
    public static EPMDocument getEPMDocumentByCADName(String cadName, String version) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        EPMDocument epmDocument = null;
        try {
            int index[] = { 0 };
            QuerySpec qs = new QuerySpec(EPMDocument.class);
            qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.CADNAME, SearchCondition.EQUAL, cadName),
                    index);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMDocument.class, "versionInfo.identifier.versionId", SearchCondition.EQUAL, version),
                    index);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.LATEST_ITERATION,
    				SearchCondition.IS_TRUE),
                    index);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);

            if (qr.hasMoreElements()) {
                epmDocument = (EPMDocument) qr.nextElement();
            }
            return epmDocument;
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        return null;
    }

    public static EPMDocument getEPMDocumentByCADNumber(String cadNumber, String version) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        EPMDocument epmDocument = null;
        try {
            int index[] = { 0 };
            QuerySpec qs = new QuerySpec(EPMDocument.class);
            qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, cadNumber),
                    index);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMDocument.class, "versionInfo.identifier.versionId", SearchCondition.EQUAL, version),
                    index);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.LATEST_ITERATION,
                            SearchCondition.IS_TRUE),
                    index);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);

            if (qr.hasMoreElements()) {
                epmDocument = (EPMDocument) qr.nextElement();
            }
            return epmDocument;
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        return null;
    }
}
