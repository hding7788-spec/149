package ext.sast.synergy.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.sast.synergy.resource.SynergyActionsRB")
public class SynergyActionsRB extends WTListResourceBundle {

    @RBEntry("跨域协同")
    public static final String OBJECT_SYNERGY_NAVIDATION_001 = "object.synergy navigation.title";
    @RBEntry("跨域协同")
    public static final String OBJECT_SYNERGY_NAVIDATION_002 = "object.synergy navigation.description";

    @RBEntry("跨域协同管理(<U class='mnemonic'>A</U>)")
    @RBComment("跨域协同管理(<U class='mnemonic'>A</U>)")
    public static final String NAVIGATION_SYNERGY_007 = "navigation.customSynergyManage.description";
    @RBEntry("跨域协同管理(<U class='mnemonic'>A</U>)")
    public static final String NAVIGATION_SYNERGY_006 = "navigation.customSynergyManage.title";


    @RBEntry("跨域协同记录管理")
    @RBComment("跨域协同记录管理")
    public static final String NAVIGATION_SYNERGY_003 = "navigation.customSynergy.description";
    @RBEntry("跨域协同记录管理")
    public static final String NAVIGATION_SYNERGY_004 = "navigation.customSynergy.tooltip";
    @RBEntry("跨域协同记录管理")
    public static final String NAVIGATION_SYNERGY_005 = "navigation.customSynergy.activetooltip";

    @RBEntry("完成流程状态")
    public static final String FINISHITEM_TITLE = "customSynergyMenu.finishItems.title";
    @RBEntry("完成流程状态")
    public static final String FINISHITEM_DESCRIPTION = "customSynergyMenu.finishItems.description";


}
