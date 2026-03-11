package ext;

import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.query.*;
import wt.representation.Representable;
import wt.vc.Iterated;

public class PageableQuerySpecDemo {

    public static WTList myCustomJob(int offset) {
        WTList wtl = new WTArrayList();
        try {
            QuerySpec qs = new QuerySpec(WTDocument.class);

            qs.appendWhere(new SearchCondition(WTDocument.class,
                            Iterated.LATEST_ITERATION,
                            SearchCondition.IS_TRUE),
                    new int[]{0});
            Representable doc = null;

            BasicPageableQuerySpec bpqs = new BasicPageableQuerySpec();
            bpqs.setPrimaryStatement(qs);
            bpqs.setOffset(offset);
            bpqs.setRange(100);
            PagingQueryResult qr = (PagingQueryResult) PersistenceHelper.manager.find(bpqs);
            long sessionId = qr.getSessionId();
            int total = qr.getTotalSize();
            System.out.println("total="+total);
            //while (true) {
               /* while (qr.hasMoreElements()) {
                    doc = (Representable) ((Object[]) qr.nextElement())[0];
                    wtl.add(doc);
                }*/
                offset += qr.size();
                PageableQuerySpec pqs = new PagingSessionSpec(sessionId);
                pqs.setOffset(offset);
                pqs.setRange(100);
                qr = (PagingQueryResult) PersistenceHelper.manager.find(pqs);


                PageableQuerySpec pqs2 = new PagingSessionSpec(sessionId);
                 pqs2.setOffset(200);
                 pqs2.setRange(100);
                qr = (PagingQueryResult) PersistenceHelper.manager.find(pqs2);
            //}
            if (sessionId > 0) PagingSessionHelper.closePagingSession(sessionId);
        } catch (Exception e) {
            e.printStackTrace();
            wtl = new WTArrayList();
        }
        return wtl;
    }
}
