<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ page import="com.ptc.netmarkets.work.NmWorkItemCommands, 				 
				 wt.util.InstalledProperties,
				 ext.casc.constants.Constants"%>

<% if(InstalledProperties.isInstalled(InstalledProperties.PROJECTLINK)){ %>  
   <mvc:table compId="<%=NmWorkItemCommands.PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID%>"/>
<% } else { %>
   <mvc:table compId="<%=Constants.CONST_EXT_PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID%>"/>
<% } %>

<% 
sessionBean.getStorage().put(NmWorkItemCommands.TASK_KEY, NmWorkItemCommands.OVERVIEW_TASKS); 
commandBean.getRequest().getSession(true).putValue(NmWorkItemCommands.TASK_KEY, NmWorkItemCommands.OVERVIEW_TASKS);
%>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
<script>
	function launchSearchPage(evt) {
	    var event="";
	    var url = "netmarkets/jsp/search/search.jsp?currentPage=search&searchType=wt.workflow.work.WorkItem&append=true";      
	    submitSearchToNavigator(url, event);
	}

	function submitSearchToNavigator(url, event) {
           var serializedForm = Form.serialize(getMainForm());
           url += "&" + serializedForm;
           loadSearchPage('object_search_navigation','advancedNavigation',event,url);
	}
</script>