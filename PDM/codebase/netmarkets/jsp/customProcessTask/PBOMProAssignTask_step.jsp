<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ page import="ext.casc.process.ProcessConstants"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@page import="java.util.List"%>

<script>
    var oidArray = new Array();
</script>

<%
	List<String> oidList = (List<String>) session.getAttribute("oidList");
	//out.println("-------oidList:"+oidList);
	String msg = ProcessConstants.JSP_JS_VALIDATE;
	String dateinfo = ProcessConstants.JSP_MSG_DATEINFO;
	if(oidList != null && oidList.size() > 0){
		for(int i = 0 ; i < oidList.size() ; i++){
			String tempOid = oidList.get(i);
%>
<script>
    var tempOid = '<%=tempOid%>';
    oidArray[oidArray.length] = tempOid;
</script>
<%
		}
	}
%>

<input type="hidden" name="oidArray" value="${oidArray}" >

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.PBOMProAssignTaskBuilder')}" />

<script language="javascript">

    function removeParts(event){
        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                grid.store.remove(selectedRows[i]);
            }
        }
    }
    function verify1(object){
        var selectValue = object.value;
        var selectId = object.id;
        var index = selectId.indexOf("_zhuzhichejian");
        var tempOid = selectId.substring(0,index);

        for(var i=1;i<9;i++){
            var selectElement = document.getElementById(tempOid+"_fuzhichejian"+i);
            if(selectElement){
                var checkValue = i+"";
                if((selectElement.checked&&selectValue==checkValue)){
                    alert("辅制车间已经分配了该车间，请重新分配");
                    selectElement.checked = false;
                }
            }

        }
        var selectElementX = document.getElementById(tempOid+"_fuzhichejian项");
        if(selectElementX){
            if((selectElementX.checked&&selectValue=="项")){
                alert("辅制车间已经分配了该车间，请重新分配");
                selectElementX.checked = false;
            }
        }

        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                //var ss= selectedRows[i].get("partType");
                var temp = selectedRows[i].id;
                if (temp.indexOf("WTPart") > -1 ) {
                    var n = temp.lastIndexOf("$");
                    var selectx = temp.substring(n + 4, temp.length - 2);
                    if (selectx.indexOf("^VR:") > -1) {
                        var ojbs = selectx.split("^VR:");
                        selectx = ojbs[ojbs.length - 1];
                    }
                    if(document.getElementById(selectx + "_zhuzhichejian")){
                        document.getElementById(selectx + "_zhuzhichejian").value = object.value;
                    }

                }
            }
        }
        //selModel.clearSelections();
    }
    function verify2(object){
        if(object.checked){
            var selectValue = object.value;
            var selectId = object.id;
            var index = selectId.indexOf("_fuzhichejian");
            var tempOid = selectId.substring(0,index);
            var selectElement = document.getElementById(tempOid+"_zhuzhichejian");
            if(selectValue== selectElement.value){
                alert("<%=new String("主制车间已经分配了该车间，请重新分配".getBytes("iso-8859-1"),"GBK")%>");
                object.checked=false;
            }
        }
        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        for(var i=0;i<selectedRows.length;i++){
            var temp = selectedRows[i].id;
            if (temp.indexOf("WTPart") > -1 ) {
                var n = temp.lastIndexOf("$");
                var selectx = temp.substring(n + 4, temp.length - 2);
                if (selectx.indexOf("^VR:") > -1) {
                    var ojbs = selectx.split("^VR:");
                    selectx = ojbs[ojbs.length - 1];
                }
                var selectElement = document.getElementById(selectx+"_zhuzhichejian");
                var fzcj = document.getElementById(selectx + "_fuzhichejian" + (object.id.substring(object.id.length-1)));
                if(selectElement.value!=fzcj.value){
                    fzcj.checked = object.checked;
                }
            }
        }
        selModel.clearSelections();
    }
    function validateValue(){
        for(var i = 0 ; i < oidArray.length ; i++){
            var tempOid = oidArray[i];
            var selectElement1 = document.getElementById(tempOid+"_fuzhichejian1");
            var selectElement2 = document.getElementById(tempOid+"_fuzhichejian2");
            var selectElement3 = document.getElementById(tempOid+"_fuzhichejian3");
            var selectElement4 = document.getElementById(tempOid+"_fuzhichejian4");
            var selectElement5 = document.getElementById(tempOid+"_fuzhichejian5");
            var selectElement6 = document.getElementById(tempOid+"_fuzhichejian6");
            var selectElement7 = document.getElementById(tempOid+"_fuzhichejian7");
            var selectElement8 = document.getElementById(tempOid+"_fuzhichejian8");
            if(selectElement1!=null&&selectElement2!=null&&selectElement3!=null&&selectElement4!=null
                &&selectElement5!=null&&selectElement6!=null&&selectElement7!=null&&selectElement8!=null){
                if(!selectElement1.checked&&!selectElement2.checked&&!selectElement3.checked
                    &&!selectElement4.checked&&!selectElement5.checked&&!selectElement6.checked
                    &&!selectElement7.checked&&!selectElement8.checked){
                    alert("<%=msg%>");
                    return false;
                }
            }
        }
        return true;
    }

    function doHandleMonth(month){
        if(month.toString().length == 1){
            month = "0" + month;
        }
        return month;
    }

    function getToDay(){
        var now = new Date();
        var nowYear = now.getFullYear();
        var nowMonth = now.getMonth();
        var nowDate = now.getDate();
        newdate = new Date(nowYear,nowMonth,nowDate);
        nowMonth = doHandleMonth(nowMonth + 1);
        nowDate = doHandleMonth(nowDate);
        return nowYear+"/"+nowMonth+"/"+nowDate;
    }

    function validateDate(object){
        var today = getToDay();
        var selDate = object.value;
        if(Date.parse(today)>Date.parse(selDate)){
            alert("<%=dateinfo%>");
            object.value="";
            return ;
        }
    }

    function verify3(object){

        validateDate(object);

        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                var temp = selectedRows[i].id;
                if (temp.indexOf("WTPart") > -1 ) {
                    var n = temp.lastIndexOf("$");
                    var selectx = temp.substring(n + 4, temp.length - 2);
                    if (selectx.indexOf("^VR:") > -1) {
                        var ojbs = selectx.split("^VR:");
                        selectx = ojbs[ojbs.length - 1];
                    }
                    if(document.getElementById(selectx + "_jihuawanchengshijian")){
                        document.getElementById(selectx + "_jihuawanchengshijian").value = object.value;
                    }

                }
            }
        }
//	selModel.clearSelections();
    }
    function verify5(object){
        var selectValue = object.value;
        var selectId = object.id;
        var index = selectId.indexOf("_renwuyiju");
        var tempOid = selectId.substring(0,index);

        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                //var ss= selectedRows[i].get("partType");
                var temp = selectedRows[i].id;
                if (temp.indexOf("WTPart") > -1 ) {
                    var n = temp.lastIndexOf("$");
                    var selectx = temp.substring(n + 4, temp.length - 2);
                    if (selectx.indexOf("^VR:") > -1) {
                        var ojbs = selectx.split("^VR:");
                        selectx = ojbs[ojbs.length - 1];
                    }
                    var zzcj = document.getElementById(selectx + "_renwuyiju");

                    zzcj.value = object.value;
                }
            }
        }
        selModel.clearSelections();

    }
</SCRIPT>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>