package ext.casc.query;

import ext.casc.util.Tools;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import java.rmi.RemoteException;

public class CommonQueryHelper {
    private static int index[] = { 0 };
    public static QueryResult queryDocLikeNumberAndName(String number, String name) throws WTException,
            RemoteException {
        QuerySpec qSpec = new QuerySpec(WTDocument.class);
        qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, number.replace("*","%"), false),
                index);
        if(!Tools.isNull(name)) {
            qSpec.appendAnd();
            qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, name.toUpperCase().replace("*","%"), false),
                    index);
        }
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        qResult = new LatestConfigSpec().process(qResult);
        return qResult;
    }

    public static WTDocument queryDocByNumberAndName(String number, String name) throws WTException,
            RemoteException {
        QuerySpec qSpec = new QuerySpec(WTDocument.class);

        qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number.toUpperCase(), false),
                index);
        if(!Tools.isNull(name)) {
            qSpec.appendAnd();
            qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.EQUAL, name.toUpperCase(), false),
                    index);
        }
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        qResult = new LatestConfigSpec().process(qResult);
        if(qResult.hasMoreElements()){
            return (WTDocument)qResult.nextElement();
        }
        return null;
    }

    public static QueryResult queryCadLikeNumberAndName(String number, String cadName) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(EPMDocument.class);
            qs.appendWhere(new SearchCondition(EPMDocument.class, "master>number", SearchCondition.LIKE,  number.toUpperCase().replace("*","%")), new int[1]);
            if(!Tools.isNull(cadName)) {
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(EPMDocument.class, "master>CADName", SearchCondition.LIKE, cadName.replace("*","%")), new int[1]);
            }
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            qr = new LatestConfigSpec().process(qr);
            return qr;
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        return null;
    }

    public static EPMDocument queryCadByNumberAndName(String number, String cadName) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(EPMDocument.class);
            qs.appendWhere(new SearchCondition(EPMDocument.class, "master>number", SearchCondition.EQUAL,  number.toUpperCase()), new int[1]);
            if(!Tools.isNull(cadName)) {
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(EPMDocument.class, "master>CADName", SearchCondition.EQUAL, cadName), new int[1]);
            }
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            qr = new LatestConfigSpec().process(qr);
            if(qr.hasMoreElements()){
                return (EPMDocument)qr.nextElement();
            }
            return null;
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        return null;
    }
}
