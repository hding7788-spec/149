package ext.test;

import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.util.WTException;

public class PagingHelper {
    public static void main(String[] args) {
        //queryWTDocument();
        queryWTDocument();
    }
    public static void queryProduct(){

    }

    public static void queryWTDocument(){
        try {
            QuerySpec spec = new QuerySpec(WTPart.class);
            long start = System.currentTimeMillis();
            PagingQueryResult res = PagingSessionHelper.openPagingSession(0, 500, spec);

            long end = System.currentTimeMillis();
            System.out.println("cost :"+(end -start)/1000 +"s");
            System.out.println("getSessionId :" +res.getSessionId());

            long total = res.getTotalSize();
            System.out.println("total:"+total);
            while(res.hasMoreElements()){
                Object[] obj = (Object[]) res.nextElement();

            }
        } catch (QueryException e) {
            throw new RuntimeException(e);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fetchWTDocument(long sessionID){
        try {
            long start = System.currentTimeMillis();
            PagingQueryResult res = PagingSessionHelper.fetchPagingSession(501, 500, sessionID);
            long end = System.currentTimeMillis();
            System.out.println("cost :"+(end -start)/1000 +"s");

            long total = res.getTotalSize();
            System.out.println("total:"+total);
            Thread.sleep(10  * 1000);
            while(res.hasMoreElements()){
                Object[] obj = (Object[]) res.nextElement();
               // System.out.println(obj[0]);
               // System.out.println(System.identityHashCode(obj[0]));
            }
        } catch (QueryException e) {
            throw new RuntimeException(e);
        } catch (WTException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
