<%@page pageEncoding="gbk"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%=request.getSession().getValue("info")%>
<%
    String contextPath = request.getContextPath();
    String file = (String) request.getSession().getValue("file");
%>
<script type="text/javascript" language="JavaScript">
    function download() {
        var path = encodeURI("<%=file%>");
        window.opener = null;
        window.open("<%=contextPath%>/netmarkets/jsp/ext/casc/gongshidinge/downloadImportFile.jsp?file="+ path);
        window.close();
    }
</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
