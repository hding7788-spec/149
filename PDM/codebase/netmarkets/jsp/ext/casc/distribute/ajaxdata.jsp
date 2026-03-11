<%@page import="ext.casc.distribute.controller.DistributeController"%><%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
    response.setContentType("application/json;charset=UTF-8");

    String changeNumber = request.getParameter("changeNumber");
    String partId = request.getParameter("partId");
    out.print(DistributeController.getJsonGraphInfo(changeNumber, partId));

%>