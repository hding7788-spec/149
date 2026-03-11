package ext.casc.work;

import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.casc.work.CustWorkResource")
public class CustWorkResource extends WTListResourceBundle {

    @wt.util.resource.RBEntry(value="驳回重新指派工艺组长")
    public static final String BHCXZPGYZZ = "300";

   // @wt.util.resource.RBEntry(value="自研产品发放通知")
    //public static final String ZYCPFFTZ = "301";

    @wt.util.resource.RBEntry(value="通知主任工艺师")
    public static final String TZZRGYS = "302";

    @wt.util.resource.RBEntry(value="指派工艺组长_已委派")
    public static final String ZHIPAI_YIWEIPAI = "303";

    @wt.util.resource.RBEntry(value="805发来")
    public static final String SHOWVIEW805 = "304";

    @wt.util.resource.RBEntry(value="八部发来")
    public static final String SHOWVIEWNO8 = "305";

    @wt.util.resource.RBEntry(value="工艺流程")
    public static final String SHOWVIEWGONGYI = "306";

    @wt.util.resource.RBEntry(value="自研产品流程")
    public static final String SHOWVIEWZIYAN = "307";
  //add by libo 2017.02.06 begin
    @wt.util.resource.RBEntry(value="仅显示最新活动")
    public static final String JXSZXHD = "308";

    @wt.util.resource.RBEntry(value="工时定额")
    public static final String GSDE = "309";
  //add by libo 2017.02.06 end

    @wt.util.resource.RBEntry(value="本人发起的")
    public static final String BRFQ = "310";

    @wt.util.resource.RBEntry(value="本人发起未完成")
    public static final String BRFQWWC = "311";
    
    @wt.util.resource.RBEntry(value="通知主任工艺师(工艺签审)")
    public static final String TONGZHI_GONGYIQIANSHEN = "312";
    
    @wt.util.resource.RBEntry(value="通知主任工艺师(材料定额签审)")
    public static final String TONGZHI_CAILIAODINGEQIANSHEN = "313";
    
    @wt.util.resource.RBEntry(value="开启的(不包含通知任务)")
    public static final String KAIQI_BUBAOHANTONGZHI = "314";
}
