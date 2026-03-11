<%@page import="com.ptc.core.ui.navigationRB" %>
<% request.setAttribute("browserWinTitleConst", navigationRB.WIN_TITLE_ORG_TAB_UTIL); %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ page import="java.util.concurrent.ConcurrentHashMap"%> 
 <%@page import="ext.casc.access.AccessAdminUtil"%>
<%@ page import="wt.inf.container.WTContainerHelper"%> 
<%@ page import="wt.org.WTUser"%> 
<%@ page import="wt.org.WTOrganization"%>
<%@ page import="wt.session.SessionHelper"%> 


<!-- Build table to control which utilities are shown, Any entry for a title 
     signafies that that link is to be shown. The key used is the the name is 
     for getting the text value of the link lable from object rbinfo
  -->
<% 
    WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
    ConcurrentHashMap showUtility = new ConcurrentHashMap();
    if(currentUser.getName().equals("Administrator")) {
        showUtility.put("MAPPING_MANAGEMENT", "Enable");
        showUtility.put("AUDITING_SECURITY_REPORT_QUERIES", "Enable");
        showUtility.put("AUDITING_SECURITY_REPORTS", "Enable");
        showUtility.put("PARTICIPANT_ADMINISTRATION", "Enable");
        showUtility.put("PRODUCT_VIEW", "Enable");
        showUtility.put("PUBLISH_MONITOR", "Enable");
        showUtility.put("PUBLISH_SCHEDULER", "Enable");
        showUtility.put("PUBLISH_THB_CONTROL", "Enable");
        showUtility.put("PURGE_ADMINISTRATOR", "Enable");
        showUtility.put("REPLICATION_SCHEDULE_ADMINISTRATOR", "Enable");
        showUtility.put("CCS_MANAGER", "Enable");
        showUtility.put("NUMBERING_SCHEMES", "Enable");
        showUtility.put("VERSIONING_SCHEMES", "Enable");
        showUtility.put("LIFE_CYCLE_ADMIN", "Enable");
        showUtility.put("POLICY_ADMINISTRATION", "Enable");
        showUtility.put("RULES_ADMINISTRATION", "Enable");
        showUtility.put("TYPE_ATTRIBUTE_MANAGER", "Enable");
        showUtility.put("WORKFLOW_TEMPLATES_ADMIN", "Enable");
        showUtility.put("WORKFLOW_ADMIN_DASHBOARD", "Enable");
        showUtility.put("PREFERENCE_MANAGER_X10", "Enable");
        showUtility.put("REPORT_MANAGER", "Enable");
        showUtility.put("ESI_TRANSACTION_ADMIN", "Enable");
        showUtility.put("ESI_DISTRIBUTION_TARGET_ADMIN", "Enable");
        showUtility.put("TEAM_ADMIN", "Enable");
        showUtility.put("BUSINESS_RULES", "Enable");
        showUtility.put("SOFTWARE_ADAPTERS_ADMINISTRATOR", "Enable");

        //don't show the import/export link if projectlink is installed standalone, SPR 1011638
        if(InstalledProperties.isInstalled(InstalledProperties.PDMLINK)
                || InstalledProperties.isInstalled(InstalledProperties.WINDCHILL_PDM)) {
            showUtility.put("IMPORT_EXPORT", "Enable");
        }

        //Don't show the promotion preference manager if projectlink is installed standalone
        if(InstalledProperties.isInstalled(InstalledProperties.PDML_PROI)) {
            showUtility.put("PROMOTION_PREFERENCE_MANAGER", "Enable");
        }

        if(InstalledProperties.isInstalled(InstalledProperties.SUMA)) {
            //The org admin doesn't need this since they can get to their suppliers just by
            //clicking on the supplier tab
            WTUser current_user = (WTUser) SessionHelper.getPrincipal();
            if(WTContainerHelper.service.isAdministrator(WTContainerHelper.getExchangeRef(), current_user)) {
                showUtility.put("SUPPLIER_ADMINISTRATOR", "Enable");
            }
        }

        if(!InstalledProperties.isInstalled(InstalledProperties.PRO_I)) {
            showUtility.put("AUDITING_ORGANIZATION_USAGE", "Enable");
        }
    } else if(AccessAdminUtil.isSysAdmin()) {
        showUtility.put("PRODUCT_VIEW", "Enable");
        showUtility.put("PUBLISH_MONITOR", "Enable");
        showUtility.put("PUBLISH_SCHEDULER", "Enable");
        showUtility.put("PUBLISH_THB_CONTROL", "Enable");
        showUtility.put("PURGE_ADMINISTRATOR", "Enable");
        showUtility.put("REPLICATION_SCHEDULE_ADMINISTRATOR", "Enable");
        showUtility.put("CCS_MANAGER", "Enable");
        showUtility.put("NUMBERING_SCHEMES", "Enable");
        showUtility.put("VERSIONING_SCHEMES", "Enable");
        showUtility.put("LIFE_CYCLE_ADMIN", "Enable");
        showUtility.put("RULES_ADMINISTRATION", "Enable");
        showUtility.put("TYPE_ATTRIBUTE_MANAGER", "Enable");
        showUtility.put("WORKFLOW_TEMPLATES_ADMIN", "Enable");
        showUtility.put("WORKFLOW_ADMIN_DASHBOARD", "Enable");
        showUtility.put("PREFERENCE_MANAGER_X10", "Enable");
        showUtility.put("REPORT_MANAGER", "Enable");
        showUtility.put("ESI_TRANSACTION_ADMIN", "Enable");
        showUtility.put("ESI_DISTRIBUTION_TARGET_ADMIN", "Enable");
        showUtility.put("TEAM_ADMIN", "Enable");
    } else if(AccessAdminUtil.isSecAdmin()) {
        showUtility.put("PARTICIPANT_ADMINISTRATION", "Enable");
        showUtility.put("POLICY_ADMINISTRATOR", "Enable");
    } else if(AccessAdminUtil.isAuditAdmin()) {
        showUtility.put("AUDITING_SECURITY_REPORT_QUERIES", "Enable");
        showUtility.put("AUDITING_SECURITY_REPORTS", "Enable");
        if(!InstalledProperties.isInstalled(InstalledProperties.PRO_I)) {
            showUtility.put("AUDITING_ORGANIZATION_USAGE", "Enable");
        }
    } else {
        showUtility.put("MAPPING_MANAGEMENT", "Enable");
        showUtility.put("AUDITING_SECURITY_REPORT_QUERIES", "Enable");
        showUtility.put("AUDITING_SECURITY_REPORTS", "Enable");
        showUtility.put("PARTICIPANT_ADMINISTRATION", "Enable");
        showUtility.put("PRODUCT_VIEW", "Enable");
        showUtility.put("PUBLISH_MONITOR", "Enable");
        showUtility.put("PUBLISH_SCHEDULER", "Enable");
        showUtility.put("PUBLISH_THB_CONTROL", "Enable");
        showUtility.put("PURGE_ADMINISTRATOR", "Enable");
        showUtility.put("REPLICATION_SCHEDULE_ADMINISTRATOR", "Enable");
        showUtility.put("CCS_MANAGER", "Enable");
        showUtility.put("NUMBERING_SCHEMES", "Enable");
        showUtility.put("VERSIONING_SCHEMES", "Enable");
        showUtility.put("LIFE_CYCLE_ADMIN", "Enable");
        showUtility.put("POLICY_ADMINISTRATION", "Enable");
        showUtility.put("RULES_ADMINISTRATION", "Enable");
        showUtility.put("TYPE_ATTRIBUTE_MANAGER", "Enable");
        showUtility.put("WORKFLOW_TEMPLATES_ADMIN", "Enable");
        showUtility.put("WORKFLOW_ADMIN_DASHBOARD", "Enable");
        showUtility.put("PREFERENCE_MANAGER_X10", "Enable");
        showUtility.put("REPORT_MANAGER", "Enable");
        showUtility.put("ESI_TRANSACTION_ADMIN", "Enable");
        showUtility.put("ESI_DISTRIBUTION_TARGET_ADMIN", "Enable");
        showUtility.put("TEAM_ADMIN", "Enable");
        showUtility.put("BUSINESS_RULES", "Enable");
        showUtility.put("SOFTWARE_ADAPTERS_ADMINISTRATOR", "Enable");

        //don't show the import/export link if projectlink is installed standalone, SPR 1011638
        if(InstalledProperties.isInstalled(InstalledProperties.PDMLINK)
                || InstalledProperties.isInstalled(InstalledProperties.WINDCHILL_PDM)) {
            showUtility.put("IMPORT_EXPORT", "Enable");
        }

        //Don't show the promotion preference manager if projectlink is installed standalone
        if(InstalledProperties.isInstalled(InstalledProperties.PDML_PROI)) {
            showUtility.put("PROMOTION_PREFERENCE_MANAGER", "Enable");
        }

        if(InstalledProperties.isInstalled(InstalledProperties.SUMA)) {
            //The org admin doesn't need this since they can get to their suppliers just by
            //clicking on the supplier tab
            WTUser current_user = (WTUser) SessionHelper.getPrincipal();
            if(WTContainerHelper.service.isAdministrator(WTContainerHelper.getExchangeRef(), current_user)) {
                showUtility.put("SUPPLIER_ADMINISTRATOR", "Enable");
            }
        }

        if(!InstalledProperties.isInstalled(InstalledProperties.PRO_I)) {
            showUtility.put("AUDITING_ORGANIZATION_USAGE", "Enable");
        }
    }
%>
<% request.setAttribute("helpPath", "OrgAdminUtilAbout"); %>
<%@ include file="/netmarkets/jsp/object/commonUtilities.jspf"%>  

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
