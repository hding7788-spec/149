<%@page import="com.ptc.core.ui.navigationRB" %>
<%@page import="ext.casc.access.AccessAdminUtil"%>
<% request.setAttribute("browserWinTitleConst", navigationRB.WIN_TITLE_SITE_TAB_UTIL); %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ page import="java.util.concurrent.ConcurrentHashMap"%>
<%@ page import="wt.audit.AuditHelper"%> 
<%@ page import="wt.org.WTUser"%> 
<%@ page import="wt.session.SessionHelper"%> 
<!-- Build table to control which utilities are shown, Any entry for a title 
     signafies that that link is to be shown. The key used is the the name is 
     for getting the text value of the link lable from object rbinfo
  -->
<% 
// TODO: make this a tag
WTUser currentUser = (WTUser)SessionHelper.getPrincipal();

ConcurrentHashMap showUtility = new ConcurrentHashMap();
if(currentUser.getName().equals("Administrator")) {
    //showUtility.put("AUDITING_PURGE", "Enable");
    showUtility.put("AUDITING_LICENSE_USAGE", "Enable");
    showUtility.put("AUDITING_SECURITY_REPORT_QUERIES", "Enable");
    showUtility.put("AUDITING_SECURITY_REPORTS", "Enable");
    showUtility.put("CAD_AGENT", "Enable");
    showUtility.put("ESI_TRANSACTION_ADMIN", "Enable");
    showUtility.put("ESI_DISTRIBUTION_TARGET_ADMIN", "Enable");
    showUtility.put("CALENDAR_MANAGEMENT", "Enable");
    showUtility.put("INFO_ENGINE", "Enable");
    showUtility.put("MAPPING_MANAGEMENT", "Enable");
    showUtility.put("NUMBERING_SCHEMES", "Enable");
    showUtility.put("VERSIONING_SCHEMES", "Enable");
    showUtility.put("LIFE_CYCLE_ADMIN", "Enable");
    showUtility.put("POLICY_ADMINISTRATION", "Enable");
    showUtility.put("WINDCHILL_PERFORMANCE_ADVISOR", "Enable");
    showUtility.put("PREFERENCE_MANAGER_X10", "Enable");
    showUtility.put("PARTICIPANT_ADMINISTRATION", "Enable");
    showUtility.put("PRODUCT_VIEW", "Enable");
    showUtility.put("PRO_INTRALINK", "Enable");
    showUtility.put("PUBLISH_MONITOR", "Enable");
    showUtility.put("SMB_WIZARD", "Enable");
    showUtility.put("PUBLISH_SCHEDULER", "Enable");
    showUtility.put("PUBLISH_THB_CONTROL", "Enable");
    showUtility.put("PURGE_ADMINISTRATOR", "Enable");
    showUtility.put("QUEUE_MANAGER", "Enable");
    showUtility.put("REPLICATION_ADMINISTRATOR", "Enable");
    showUtility.put("RULES_ADMINISTRATION", "Enable");
    showUtility.put("TASK_DELEGATE", "Enable");
    showUtility.put("OPTEGRA_GATEWAY", "Enable");
    showUtility.put("TYPE_ATTRIBUTE_MANAGER", "Enable");
    showUtility.put("TYPE_ATTRIBUTE_MANAGER_X20", "Enable");
    showUtility.put("WORKFLOW_TEMPLATES_ADMIN", "Enable");
    showUtility.put("WORKFLOW_ADMIN_DASHBOARD", "Enable");
    showUtility.put("VERSIONING_SCHEMES", "Enable");
    showUtility.put("VIEW_NETWORK", "Enable");
    showUtility.put("REPORT_MANAGER", "Enable");
    showUtility.put("PERSONAL_CABINETS", "Enable");
    showUtility.put("TEAM_ADMIN", "Enable");
    showUtility.put("SERVER_STATUS", "Enable");
    showUtility.put("TS_PORTAL", "Enable");
    showUtility.put("DASHBOARD", "Enable");
    showUtility.put("BUSINESS_RULES", "Enable");
    showUtility.put("SOFTWARE_ADAPTERS_ADMINISTRATOR", "Enable");
    showUtility.put("INDEX_MANAGEMENT", "Enable");

    //don't show the import/export link if projectlink is installed standalone, SPR 1011638
    if(InstalledProperties.isInstalled(InstalledProperties.PDMLINK)
            || InstalledProperties.isInstalled(InstalledProperties.WINDCHILL_PDM)) {
        showUtility.put("IMPORT_EXPORT", "Enable");
    }

    //Don't show the promotion preference manager if projectlink is installed standalone
    if(InstalledProperties.isInstalled(InstalledProperties.PDML_PROI)) {
        showUtility.put("PROMOTION_PREFERENCE_MANAGER", "Enable");
    }

    //Don't show the Category Tree Administration if QMS is not installed
    if(InstalledProperties.isInstalled(InstalledProperties.QMS)) {
        showUtility.put("CATEGORY_TREE_ADMINISTRATION", "Enable");
    }
}else {
    if(AccessAdminUtil.isSysAdmin()){
        //showUtility.put("AUDITING_PURGE", "Enable");
        //showUtility.put("AUDITING_LICENSE_USAGE", "Enable");
        //showUtility.put("AUDITING_SECURITY_REPORT_QUERIES", "Enable");
        //showUtility.put("AUDITING_SECURITY_REPORTS", "Enable");
        showUtility.put("CAD_AGENT", "Enable");
        showUtility.put("ESI_TRANSACTION_ADMIN", "Enable");
        showUtility.put("ESI_DISTRIBUTION_TARGET_ADMIN", "Enable");
        showUtility.put("CALENDAR_MANAGEMENT", "Enable");
        showUtility.put("INFO_ENGINE", "Enable");
        showUtility.put("MAPPING_MANAGEMENT", "Enable");
        showUtility.put("NUMBERING_SCHEMES", "Enable");
        showUtility.put("VERSIONING_SCHEMES", "Enable");
        showUtility.put("LIFE_CYCLE_ADMIN", "Enable");
        //showUtility.put("POLICY_ADMINISTRATION", "Enable");
        showUtility.put("WINDCHILL_PERFORMANCE_ADVISOR", "Enable");
        showUtility.put("PREFERENCE_MANAGER_X10", "Enable");
        //showUtility.put("PARTICIPANT_ADMINISTRATION", "Enable");
        showUtility.put("PRODUCT_VIEW", "Enable");
        showUtility.put("PRO_INTRALINK", "Enable");
        showUtility.put("PUBLISH_MONITOR", "Enable");
        showUtility.put("SMB_WIZARD", "Enable");
        showUtility.put("PUBLISH_SCHEDULER", "Enable");
        showUtility.put("PUBLISH_THB_CONTROL", "Enable");
        showUtility.put("PURGE_ADMINISTRATOR", "Enable");
        showUtility.put("QUEUE_MANAGER", "Enable");
        showUtility.put("REPLICATION_ADMINISTRATOR", "Enable");
        showUtility.put("RULES_ADMINISTRATION", "Enable");
        showUtility.put("TASK_DELEGATE", "Enable");
        showUtility.put("OPTEGRA_GATEWAY", "Enable");
        showUtility.put("TYPE_ATTRIBUTE_MANAGER", "Enable");
        showUtility.put("TYPE_ATTRIBUTE_MANAGER_X20", "Enable");
        showUtility.put("WORKFLOW_TEMPLATES_ADMIN", "Enable");
        showUtility.put("WORKFLOW_ADMIN_DASHBOARD", "Enable");
        showUtility.put("VERSIONING_SCHEMES", "Enable");
        showUtility.put("VIEW_NETWORK", "Enable");
        showUtility.put("REPORT_MANAGER", "Enable");
        showUtility.put("PERSONAL_CABINETS", "Enable");
        showUtility.put("TEAM_ADMIN", "Enable");
        showUtility.put("SERVER_STATUS", "Enable");
        showUtility.put("TS_PORTAL", "Enable");
        showUtility.put("DASHBOARD", "Enable");
        showUtility.put("BUSINESS_RULES", "Enable");
        showUtility.put("SOFTWARE_ADAPTERS_ADMINISTRATOR", "Enable");
        showUtility.put("INDEX_MANAGEMENT", "Enable");

        //don't show the import/export link if projectlink is installed standalone, SPR 1011638
        if(InstalledProperties.isInstalled(InstalledProperties.PDMLINK)
                || InstalledProperties.isInstalled(InstalledProperties.WINDCHILL_PDM)) {
            showUtility.put("IMPORT_EXPORT", "Enable");
        }

        //Don't show the promotion preference manager if projectlink is installed standalone
        if(InstalledProperties.isInstalled(InstalledProperties.PDML_PROI)) {
            showUtility.put("PROMOTION_PREFERENCE_MANAGER", "Enable");
        }

        //Don't show the Category Tree Administration if QMS is not installed
        if(InstalledProperties.isInstalled(InstalledProperties.QMS)) {
            showUtility.put("CATEGORY_TREE_ADMINISTRATION", "Enable");
        }
    } else if(AccessAdminUtil.isSecAdmin()) {
        showUtility.put("POLICY_ADMINISTRATOR", "Enable");
        showUtility.put("PARTICIPANT_ADMINISTRATION", "Enable");
    } else if(AccessAdminUtil.isAuditAdmin()) {
        showUtility.put("AUDITING_PURGE", "Enable");
        showUtility.put("AUDITING_LICENSE_USAGE", "Enable");
        showUtility.put("AUDITING_SECURITY_REPORT_QUERIES", "Enable");
        showUtility.put("AUDITING_SECURITY_REPORTS", "Enable");
    } else {

    }
}

 request.setAttribute("helpPath", "SiteAdminUtilAbout"); %>
<%@ include file="/netmarkets/jsp/object/commonUtilities.jspf"%>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
