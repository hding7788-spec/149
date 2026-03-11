
<%
	String templateOid = request.getParameter("templateOid");

    templateOid = java.net.URLDecoder.decode(templateOid,"UTF-8");
	System.out.println("templateOid:"+templateOid);
	request.getSession().setAttribute("templateOid",templateOid);

%>
<script>
	window.close();
</script>