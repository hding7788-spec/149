<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.glaway.mpm.util.MPMProcessPlanUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.glaway.mpm.util.FillTime"%>
<%
	String msg = request.getParameter("msg");
	String partOid = (String)session.getAttribute("partOid");
	System.out.println("oid===>" + partOid);
	System.out.println("msg===>" + msg);
	msg = FillTime.operationJSONStr(msg);
	System.out.println("msg===>" + msg);
	HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
	//msg = "{msg:[{lBsoID:1,mBsoID:514663,zbgs:1,djgs:2,mzsl:3},{lBsoID:1,mBsoID:514655,zbgs:2,djgs:3,mzsl:4}]}";
	if(msg != null){
		JSONObject jsonObject = new JSONObject(msg);
		System.out.println("msg--->" + jsonObject.get("msg"));
		JSONArray jsonArray = jsonObject.getJSONArray("msg");
		System.out.println("jsonArray:" + jsonArray);
		for(int i = 0; i < jsonArray.length(); i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			String stepOrPracebsoOid = jsonObj.getString("oid");//工序或者工步oid
			String prepareWorkHours  = jsonObj.getString("zbgs");//准备工时
			String taktTime = jsonObj.getString("djgs");//单件工时
			String numberOfGroup = jsonObj.getString("mzsl");//每组数量(个)
			System.out.println("工序或者工步oid===>" + stepOrPracebsoOid.replace("|", ":"));
			System.out.println("准备工时==>" + prepareWorkHours);
			System.out.println("单件工时==>" + taktTime);
			System.out.println("每组数量(个)==>" + numberOfGroup);
			ArrayList<String> tempList = new ArrayList<String>();
			tempList.add(prepareWorkHours);
			tempList.add(taktTime);
			tempList.add(numberOfGroup);
			map.put(stepOrPracebsoOid.replace("|", ":"), tempList);
		}
		FillTime.fillTimeXML(partOid, map);
	}
	System.out.println("map===>" + map);
%>

<script language="javascript">
	window.close();
</script>