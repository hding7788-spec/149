package ext.sast.center.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.sast.center.resource.CenterActionsRB")
public class CenterActionsRB extends WTListResourceBundle {

    @RBEntry("协同站点管理")
    public static final String CENTER_COLLABORATIVESITEMANAGEMENT_TITLE = "center.collaborativeSiteManagement.title";
    @RBEntry("协同站点管理")
    public static final String CENTER_COLLABORATIVESITEMANAGEMENT_DES = "center.collaborativeSiteManagement.description";


    @RBEntry("restore.gif")
    public static final String CENTER_ADD_IMG = "center.synchDomainInfo.icon";
    @RBEntry("同步")
    public static final String CENTER_ADD_DES = "center.synchDomainInfo.tooltip";

    @RBEntry("协同流程查看")
    public static final String envelope_centerWorkflow_01 = "envelope.centerWorkflow.title";
    @RBEntry("协同流程查看")
    public static final String envelope_centerWorkflow_02 = "envelope.centerWorkflow.description";

    @RBEntry("协同流程查看")
    public static final String center_centerWorkflow_01 = "center.centerWorkflow.title";
    @RBEntry("协同流程查看")
    public static final String center_centerWorkflow_02 = "center.centerWorkflow.description";

    @RBEntry("补发设计研发平台")
    public static final String CENTER_SENDTOKR_01 = "center.sendToKR.title";
    @RBEntry("补发设计研发平台")
    public static final String CENTER_SENDTOKR_02 = "center.sendToKR.description";

    @RBEntry("元器件超目录清单")
    public static final String center_yqjChaoMuluList_01 = "envelope.yqjChaoMuluList.title";
    @RBEntry("元器件超目录清单")
    public static final String center_yqjChaoMuluList02 = "envelope.yqjChaoMuluList.description";
}
