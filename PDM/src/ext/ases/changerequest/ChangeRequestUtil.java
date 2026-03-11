package ext.ases.changerequest;

import ext.ases.envelope.ProcessEnvelope;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class ChangeRequestUtil {


    public static QueryResult getChangeRequestByNumber(String number) throws WTException {
        QuerySpec qs = new QuerySpec(ChangeRequest.class);
        int[] index = { 0 };
        SearchCondition sc = new SearchCondition(ChangeRequest.class,ChangeRequest.NUMBER,SearchCondition.EQUAL,number);
        qs.appendWhere(sc, index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
        return qr;
    }
}
