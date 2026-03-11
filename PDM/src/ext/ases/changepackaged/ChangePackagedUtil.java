package ext.ases.changepackaged;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

public class ChangePackagedUtil {

    public static QueryResult getChangeAfterDataByChangePackaged(ChangePackaged changePackaged) throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(changePackaged,
                ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                ChangePackagedResultLink.class, true);
        return qr;
    }

    public static QueryResult getChangeBeforeDataByChangePackaged(ChangePackaged changePackaged) throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(changePackaged,
                ChangePackagedAffectLink.ROLE_BOBJECT_ROLE,
                ChangePackagedAffectLink.class, true);
        return qr;
    }
    public static QueryResult getChangePackagedByNumber(String number) throws WTException {
        QuerySpec qs = new QuerySpec(ChangePackaged.class);
        int[] index = { 0 };
        SearchCondition sc = new SearchCondition(ChangePackaged.class,ChangePackaged.NUMBER,SearchCondition.EQUAL,number);
        qs.appendWhere(sc, index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
        return qr;
    }
    public static List  getChangeAfterListByChangePackaged(ChangePackaged changePackaged) throws WTException {
        List  result = new ArrayList();
        QueryResult qr = PersistenceHelper.manager.navigate(changePackaged,
                ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                ChangePackagedResultLink.class, true);
        while(qr.hasMoreElements()){
            result.add(qr.nextElement());
        }
        return result;
    }
    public static List getChangeBeforeListByChangePackaged(ChangePackaged changePackaged) throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(changePackaged,
                ChangePackagedAffectLink.ROLE_BOBJECT_ROLE,
                ChangePackagedAffectLink.class, true);
        List  result = new ArrayList();
        while(qr.hasMoreElements()){
            result.add(qr.nextElement());
        }
        return result;
    }
    public static ChangePackaged getChangeChangePackagedByAfterData(Persistable rc) throws WTException {
        QueryResult qr = PersistenceHelper.manager.navigate(rc,
                ChangePackagedResultLink.ROLE_AOBJECT_ROLE,
                ChangePackagedResultLink.class, true);
        while(qr.hasMoreElements()){
            return (ChangePackaged)qr.nextElement();
        }
        return null;
    }
}
