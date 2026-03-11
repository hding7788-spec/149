package ext.test;

import com.ptc.wvs.common.ui.Publisher;
import com.ptc.wvs.server.loader.NavigationHelper;
import wt.filter.NavigationCriteria;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartStandardConfigSpec;
import wt.vc.views.ViewHelper;

public class RepresentableTest {

    public static void publish(){
        Publisher publisher = new Publisher();
       // boolean result = publisher.doPublish(viewableLink,forceRepublish,objectReference,getPartNavigationCriteria(),null,true,"default",part.getNumber(),1,null,ruleSource,publishParams);
    }

    public static NavigationCriteria getPartNavigationCriteria(WTPart part) throws Exception{
        WTPartConfigSpec wtPartConfigSpec = WTPartConfigSpec.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(ViewHelper.getView(part),null));
        return NavigationHelper.getNavigationCriteria(wtPartConfigSpec);
    }
}
