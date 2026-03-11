<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@ include file="/netmarkets/jsp/ext/casc/document/create.jspf"%>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
<%
    String path = request.getContextPath();
%>
<script type="text/javascript">
    function selectPhotoType(obj){
        var obj = new Object();
        var path = "<%=path%>/netmarkets/jsp/ext/casc/document/selectPhotoType.jsp";
        var ret = window.showModalDialog(path,obj,"dialogWidth=300px;dialogHeight=300px;resizable=yes;");
        if(ret != null) {
            document.getElementById("photoType").value=ret;
        }
    }
</script>
