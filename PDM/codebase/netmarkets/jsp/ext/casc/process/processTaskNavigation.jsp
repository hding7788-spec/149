<%@page import="com.glaway.mpm.constants.Constants"%>
<%@page import="ext.casc.access.AccessAdminUtil"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>

<jca:tabToHighlight actionName="searchProcessTask" objectType="customProcessTask" />

<%@include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ page import="java.util.Map,java.util.Locale"%>
<%@ page import="wt.org.WTUser, wt.session.SessionHelper"%>
<%@ page import="java.util.Calendar,java.text.SimpleDateFormat,java.util.GregorianCalendar"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@page import="ext.casc.process.util.ProcessUtil"%>

<a href="netmarkets/jsp/ext/casc/process/myProcessTask.jsp?type=SEA">我的工艺任务</a><br/>
<a href="netmarkets/jsp/ext/casc/process/searchProcessTask.jsp?type=SEA">工艺任务查询(本人发起)</a><br/>
<%
WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
Iterator<String> iterator = map.keySet().iterator();
boolean flag = false;
while(iterator.hasNext()){
    String tempChejian = iterator.next();
    List<WTUser> allUsers = map.get(tempChejian);
    for(WTUser user:allUsers){
        if(currentUser.getName().equals(user.getName())){
            flag = true;
            break;
        }
    }
    if(flag){
        break;
    }
}
boolean isZhuRenGongyiShi = AccessAdminUtil.isGroup(ext.casc.constants.Constants.GROUP_NAME_YUNZAI);
if(flag){
%>
<a href="netmarkets/jsp/ext/casc/process/searchProcessTaskForGongYiZuZhang.jsp?type=SEA">工艺任务查询(本部门)</a><br/>
<%} %>

<a href="netmarkets/jsp/ext/casc/process/searchProcessTaskForGongYiYuan.jsp?type=SEA">工艺任务查询(本人收到)</a><br/>

<a href="netmarkets/jsp/ext/casc/process/otherDesingTaskManage.jsp?type=SEA">非工艺设计类工艺任务管理(主任工艺师)</a><br/>

<a href="netmarkets/jsp/ext/casc/analysisActivity/searchAnalysisActivity.jsp">更改任务查询统计报表查询</a><br/>

<%@include file="/netmarkets/jsp/util/end.jspf"%>