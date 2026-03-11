<%@page import="ext.casc.ixb.ReleaseDataAdvisBackHelper"%>
<%
    String oid = request.getParameter("oid");
	String datas = request.getParameter("value");
	String value1 = java.net.URLDecoder.decode(datas,"UTF-8");
	System.out.println("-------->>>>>oid:"+oid);
	System.out.println("------->>>>>value1:"+value1);
	ReleaseDataAdvisBackHelper.setSelDepartValue2(oid,value1);
%>