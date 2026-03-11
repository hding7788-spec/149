package ext.test;

import ext.casc.util.ExtQuerySpec;
import ext.casc.util.QuerySpaceContant;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.QueryException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.ArrayList;
import java.util.List;

public class QuerySpecTest {

    public static void main(String[] args) {
        List<Long> ids = new ArrayList<Long>();
        ids.add(134685l);
        getAllChildren(true,ids);
    }

    public static void getAllChildren(boolean hasView, List<Long> partIds){
        try {
            ExtQuerySpec levlQs = new ExtQuerySpec();
            int pIndex =  levlQs.applendClassBack(WTPart.class);
            int lIndex = levlQs.applendClass(WTPartUsageLink.class);
            int cIndex = levlQs.applendClassBack(WTPart.class);
            int cmIndex = levlQs.applendClass(WTPartMaster.class);
            levlQs.appendIda2a2AndIda3a5(WTPart.class,pIndex,WTPartUsageLink.class,lIndex);
            levlQs.appendIda2a2AndIda3b5(WTPartMaster.class,cmIndex,WTPartUsageLink.class,lIndex);
            levlQs.appendWhereLatest(WTPart.class,cIndex);
            if(hasView){
                levlQs.appendView(cIndex,"Design");
            }else{
                levlQs.setPartNoView(true);
            }
            levlQs.appendIda2a2AndMaster(WTPart.class,cIndex,WTPartMaster.class,cmIndex);
            levlQs.maxVersion(WTPart.class,cIndex,WTPartMaster.class,cmIndex);
            levlQs.appendConditionNotEqual(WTPart.class, QuerySpaceContant.QUERY_SPEC_CHECKOUTINFO_STATE,cIndex,QuerySpaceContant.QUERY_SPEC_WRK);
            levlQs.appendWhereIn(WTPart.class,QuerySpaceContant.QUERY_SPEC_THEOBJECTIDENTIFIER_ID,pIndex,partIds.toArray());

            QueryResult qr = levlQs.find();
            Object[] obj = null;
            WTPart ppart = null;
            WTPart cpart = null;
            while(qr.hasMoreElements()){
                obj = (Object[] ) qr.nextElement();

                ppart = (WTPart) obj[0];
                cpart = (WTPart) obj[1];
                System.out.println(ppart.getNumber()+"->"+cpart.getNumber());
            }
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        } catch (QueryException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }

    }
}
