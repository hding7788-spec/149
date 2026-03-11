<%@page import="com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster"%>
<%@page import="com.infoengine.modeler.data.ForEach"%>
<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%@page import="java.util.*,wt.util.WTProperties"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="com.ptc.windchill.mpml.processplan.MPMProcessPlan"%>
<%@page import="com.ptc.windchill.mpml.processplan.operation.MPMOperation"%>
<%@page import="com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink" %>
<%@page import="ext.ptc.ViewWIHelper"%>
<%
	WTProperties props = WTProperties.getLocalProperties();
	String webPort = props.getProperty("wt.webserver.port");
	String hName = props.getProperty("wt.server.hostname");
	String submitFlg = request.getParameter("submitFlg");
	String number = request.getParameter("partNumber");
	String url = "";
	List<MPMOperationUsageLink> listOper = null;
	List<MPMOperationUsageLink> listChildOper = null;
	//System.out.println("---------submitFlg:"+submitFlg+"  ---------number:"+number);
	String numberMsg = "请输入图号!";
	String planMsg = "没有找到对应图号的工艺计划!";
	if("1".equals(submitFlg)) {
		if(number==null || number.length()==0){
			out.println(numberMsg);
	} else {
			WTPart part = ViewWIHelper.getPartByTuHao(number);
			List<MPMProcessPlan> list=ViewWIHelper.getProcessPlan(part); 
			if(list==null){
				out.println(planMsg);
			} else {
				%>
					<div align="center"><h4>下表列出了所有与图号  <%=number%> 相关的工艺计划及其工序.<br>请选择一个工艺计划进行查看:</h4></div>
					<table border="0" bgcolor="#565656" align="center">
					<th width="200" bgcolor="#565630">编号</th>
					<th width="200" bgcolor="#565630">名称</th>
				<%
				for(int i=0;i<list.size();i++){
					MPMProcessPlan plan = (MPMProcessPlan)list.get(i);
					listOper = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
					ReferenceFactory factory = new ReferenceFactory();
					String oid = factory.getReferenceString(plan);
					String containerOid = factory.getReferenceString(plan.getContainer());
					String planNumber = plan.getNumber();
					String planName = plan.getName();

					//"http://wcadmin:wcadmin@pds.nriet.com/Windchill/netmarkets/jsp/mpml/LaunchWIforProcessplan.jsp?"+"oid="+oid+"&ContainerOid="+containerOid
					url = "http://wcadmin:wcadmin@"+hName+":"+webPort+"/Windchill/netmarkets/jsp/mpml/LaunchWIforProcessplan.jsp?";
					//response.sendRedirect("./LaunchWIforProcessplan.jsp?"+"oid="+oid+"&ContainerOid="+containerOid);	
					//System.out.println("**********url:"+url);
					//response.sendRedirect(url);
				%>
					<tr>
						<td align="left" width="200" bgcolor="#ffffff"><%=planNumber%></td>
						<td align="left" width="200" bgcolor="#ffffff"><%=planName%></td>
						<td><input type="button" name="View" value="View" onClick="processView('<%=oid %>','<%=containerOid %>');"/></td>
					</tr>
					<%
					for(int j=0;j<listOper.size();j++){
					    MPMOperationUsageLink link = listOper.get(j);
					    String label = link.getOperationLabel();
					    MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
					    MPMOperation operation = ViewWIHelper.getMpmOperation(master.getNumber());
					    listChildOper = ViewWIHelper.getMPMOperationUsageLinkByMpmOper(operation);
					    ReferenceFactory factory1 = new ReferenceFactory();
						String oid1 = factory1.getReferenceString(operation);
						String containerOid1 = factory1.getReferenceString(master.getContainer());
						String oprNumber1 = master.getNumber();
						String oprName1 = master.getName();
						url = "http://wcadmin:wcadmin@"+hName+":"+webPort+"/Windchill/netmarkets/jsp/mpml/LaunchWIforProcessplan.jsp?";
					%>
					<tr>
						<td align="left" width="200" bgcolor="#ffffff">&nbsp|-<%=label%></td>
						<td align="left" width="200" bgcolor="#ffffff"><%=oprName1%></td>
						<td><input type="button" name="View" value="View" onClick="processView('<%=oid1 %>','<%=containerOid1 %>');"/></td>
					</tr>
					<%
					for(int n=0;n<listChildOper.size();n++){
					    MPMOperationUsageLink link2 = listChildOper.get(n);
					    String childLabel = link2.getOperationLabel();
					    MPMOperationMaster childMaster = (MPMOperationMaster) link.getRoleBObject();
					    MPMOperation ChildOperation = ViewWIHelper.getMpmOperation(childMaster.getNumber());
					    ReferenceFactory factory2 = new ReferenceFactory();
						String oid2 = factory2.getReferenceString(ChildOperation);
						String containerOid2 = factory2.getReferenceString(childMaster.getContainer());
						String oprNumber2 = childMaster.getNumber();
						String oprName2 = childMaster.getName();
						url = "http://wcadmin:wcadmin@"+hName+":"+webPort+"/Windchill/netmarkets/jsp/mpml/LaunchWIforProcessplan.jsp?";
					%>
					<tr>
						<td align="left" width="200" bgcolor="#ffffff">&nbsp&nbsp|-<%=childLabel%></td>
						<td align="left" width="200" bgcolor="#ffffff"><%=oprName2%></td>
						<td><input type="button" name="View" value="View" onClick="processView('<%=oid2 %>','<%=containerOid2 %>');"/></td>
					</tr>
				<%
				}}}			
			}
		}
	}
%>
</table>

<input type="hidden" id="url" name="url" value="<%=url%>"/>

<SCRIPT LANGUAGE="JavaScript">

	function processView(oid,containerOid){
		var url = document.getElementById("url").value;
		url = url+"oid="+oid+"&ContainerOid="+containerOid;
		window.open(url);
	}

</Script>