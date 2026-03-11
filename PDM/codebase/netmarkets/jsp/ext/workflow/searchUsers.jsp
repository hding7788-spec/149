<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="ext.casc.workflow.CmComparators"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.org.OrganizationServicesHelper"%>
<%@page import="java.util.Enumeration"%>
<%@page import="ext.casc.util.WCUtil"%>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean,
                 com.ptc.netmarkets.rule.NmRuleCommands,
                 com.ptc.netmarkets.model.NmObjectHelper,
                 com.ptc.netmarkets.util.misc.NmContext,
                 com.ptc.netmarkets.util.misc.NmContextItem,
                 com.ptc.netmarkets.util.table.NmHTMLTable,
                 com.ptc.netmarkets.model.NmOid,
                 com.ptc.netmarkets.folder.NmFolderHelper,
                 com.ptc.netmarkets.lifecycle.NmLifeCycleHelper,
                 com.ptc.netmarkets.work.NmWorkItemCommands,
                 com.ptc.netmarkets.workflow.workflowResource,
                 com.ptc.netmarkets.util.table.*,
                 com.ptc.netmarkets.util.misc.*,
                 wt.util.WTMessage,
                 java.util.ResourceBundle,
                 java.util.ArrayList,
                 java.util.Iterator,
                  java.util.Locale,
                 wt.fc.ObjectIdentifier,
                 java.util.HashMap"
                 %>
<%@ page import="wt.org.DirectoryContextProvider" %>
<%@ page language="java" isThreadSafe="true" contentType="text/html;charset=UTF-8"%>

<%
		 int USER_QUERY_LIMIT = 50;
         boolean isSuccess = false;
         String key = request.getParameter("key");
         String result = "";

         if (key != null && !"null".equals(key) && key.length() > 0)
         {
            DirectoryContextProvider dcp = wt.org.OrganizationServicesHelper.manager.newDirectoryContextProvider((String[])null,(String[])null);
            Enumeration en = OrganizationServicesHelper.manager.findLikeUsers("fullName", key, dcp);
            if (!en.hasMoreElements())
               en = OrganizationServicesHelper.manager.findLikeUsers("name", key, dcp);

            List<WTUser> users = new ArrayList<WTUser>();
            while (en.hasMoreElements() && (users.size() < USER_QUERY_LIMIT))
            {
               WTUser u = (WTUser) en.nextElement();
			   if (u != null)
			      users.add(u);
            }
            Collections.sort(users, CmComparators.GB_WTUSER_COMPARATOR);

            ReferenceFactory rf = new ReferenceFactory();
            for (int j = 0; users != null && j < users.size(); j++)
            {
               WTUser u = (WTUser) users.get(j);
               String uoid = rf.getReferenceString(u);
               if (uoid != null)
               {
                  if (!result.equals(""))
                     result += ";";
                     String fullName = u.getFullName();
                     if (fullName.contains(",")) {
                         fullName = fullName.replaceAll(",", "");
                     }
                     String userStr = u.getName()+"("+fullName+")";
                     result += uoid + "," + userStr;
               }
            }

            isSuccess = true;
         }

         response.setHeader("Charset", "UTF-8");
         response.setHeader("X-JSON", "{ isSuccess:" + isSuccess + ", result:\"" + WCUtil.encode(result) + "\"}");
%>
