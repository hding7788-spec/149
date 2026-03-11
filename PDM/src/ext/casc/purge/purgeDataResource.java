package ext.casc.purge;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.casc.purge.purgeDataResource")
public class purgeDataResource extends WTListResourceBundle {

    @RBEntry("删除作废数据")
    @RBComment("Delete Invalid Data")
    public static final String PURGE_DATA_0 = "purgeData.deleteData.title";
    @RBEntry("删除作废数据")
    @RBComment("Delete Invalid Data")
    public static final String PURGE_DATA_1 = "purgeData.deleteData.description";
    @RBEntry("删除作废数据")
    @RBComment("Delete Invalid Data")
    public static final String PURGE_DATA_2 = "purgeData.deleteData.tooltip";
    @RBEntry("netmarkets/images/delete.gif")
    public static final String PURGE_DATA_3 = "purgeData.deleteData.icon";
    
}
