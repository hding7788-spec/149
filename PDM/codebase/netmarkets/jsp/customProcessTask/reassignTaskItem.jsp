<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@page import="ext.casc.process.ProcessConstants"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.List,ext.casc.process.ProcessTaskItem,com.ptc.netmarkets.model.NmOid"%>

<%
String msg = ProcessConstants.JSP_ACTIONS_REASSIGN_FAILED;
String flag = "ok";
List list = commandBean.getSelectedOidForPopup();
//out.println("----------list:"+list);
if(list!=null){
    for (Object object : list) {
        //out.println("----------object:"+object);
        if (object instanceof NmOid) {
            NmOid oid = (NmOid) object;
            ProcessTaskItem taskItem = (ProcessTaskItem) oid.getRefObject();
            String taskState = taskItem.getTaskItemState();
            //out.println("----------taskState:"+taskState);
            if(!taskState.equals(ProcessConstants.TASK_STATE_JINGXINZHONG)){
                flag = "no";
                break;
            }
        }
    }
}
%>

<input type="hidden" name="flag" id="flag" value="<%=flag%>" >

<script language="javascript">

	function validateSelectedTaskItem(){
		var flag = document.getElementById("flag").value;
		//alert("flag:"+flag);
		if(flag=="no"){
			alert('<%=msg%>');
			window.close();
		}
	}
	
	validateSelectedTaskItem();
</script>

<jca:wizard helpSelectorKey="task_reassign_help" buttonList="reassignTaskButtons">
   <jca:wizardStep action="reassignTaskItem_step" type="customProcessTask"/>
</jca:wizard>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>

