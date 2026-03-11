<%@page import="ext.ases.workflow.setparticipant.PrincipalHelper"%>
<%@ page import="java.util.List,
                 java.util.ArrayList,
                 java.util.Map,
                 java.util.HashMap,
		   java.util.Enumeration,
		   java.util.Locale,
		   wt.util.WTContext"%>

<%
	Locale locale = WTContext.getContext().getLocale();
	String value = request.getParameter("value");
	String oid = request.getParameter("oid");
//System.out.println("oid is "+oid);
//System.out.println("get value is "+value);
	String value1 = java.net.URLDecoder.decode(value,"UTF-8");
	System.out.println("user set value is "+value1);
	String[] valueArray = value1.split(":");
	System.out.println("Test 1 "+valueArray.length);
	HashMap roleUser = new HashMap();
	for(int i = 0 ; i < valueArray.length ; i ++){
		String realValue[] = valueArray[i].split("`");
		System.out.println("Test "+ i+" "+realValue.length);
		List tempList = new ArrayList();
		for(int j = 1 ; j < realValue.length ; j ++){
			String tempName = realValue[j];
			String temp = "";
			if((tempName.indexOf("(")!=-1)&&(tempName.indexOf(")")!=-1)){
				temp = tempName.substring(tempName.indexOf("(")+1,tempName.indexOf(")"));
			}else{
				temp = tempName;
			}
			System.out.println("real user name is temp "+temp);
			tempList.add(temp);
		}
		roleUser.put(realValue[0],tempList);
	}
	

	PrincipalHelper.service.saveTeamRole(roleUser,oid,locale);
	out.println("set sucess!");
%>
<script>
	window.close();
</script>