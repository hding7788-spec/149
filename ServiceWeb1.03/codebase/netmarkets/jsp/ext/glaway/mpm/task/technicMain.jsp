<%@ page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>
<%@page import="com.glaway.mpm.util.Util"%>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<script language="javascript">
	function expand(){
		var main = document.getElementById("childTask").style.display;
		if(main == 'none'){
			document.getElementById("childTask").style.display = '';
		}else{
			document.getElementById("childTask").style.display = 'none';
		}
	}
</script>

<%
	boolean flag = TaskUtil.isPlanner();//计划员派工工艺任务页面
	if(flag){
%>
	<a href="netmarkets/jsp/glaway/mpm/task/planner/plannerTecDisTask.jsp">我的工艺派工任务</a><br/>
	<a href="netmarkets/jsp/glaway/mpm/task/monitor/myMonitorTask.jsp">我的监控任务</a><br/>
<%
	}
	
	boolean flag2 = TaskUtil.isTechnicGroupLeader();
	if(flag2){
%>
	<a href="netmarkets/jsp/glaway/mpm/task/leader/leaderTecDisTask.jsp">我的工艺派工任务</a><br/>
	<a href="netmarkets/jsp/glaway/mpm/task/monitor/myMonitorTask.jsp">我的监控任务</a><br/>
<%
	}
%>

<a href="netmarkets/jsp/glaway/mpm/task/myItemDisTask.jsp">我的条目派工任务</a><br/>
<div id="task" style="color:red" onclick="expand()">
	<a href="#">我的工艺任务</a><br/>
</div>
<div id="childTask" style="display:none">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="netmarkets/jsp/glaway/mpm/task/myItemTask.jsp">条目任务</a><br/>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="netmarkets/jsp/glaway/mpm/task/myTechnicTask.jsp">工艺设计任务</a><br/>
	<%
		boolean isFillTimer = Util.isTheCurrentInTheGroup("工时定额");
		if(isFillTimer){
	 %>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="netmarkets/jsp/glaway/mpm/task/hourQuota.jsp">工时定额</a>
	<%
		}
	 %>
</div>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>