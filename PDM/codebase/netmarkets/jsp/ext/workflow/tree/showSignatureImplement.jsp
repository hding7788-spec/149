<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="ext.casc.constants.Constants"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@page import="ext.casc.part.SignatureHelper,ext.casc.report.technics.DownloadTechnicsReportUtil"%>
<%@page import="ext.casc.workflow.TaskConfigrationHelper,java.util.Map"%>
<%@ page import="ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService" %>
<%@ page import="ext.casc.workflow.WorkflowVoteUtils" %>
<input type="hidden" name="completeReviewhidden1" id="completeReviewhidden1" onclick=""/>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%
	String signOid = request.getParameter("oid");
	String reviewPath = (String)TaskConfigrationHelper.getProcessVariableValue(signOid,"reviewPath");
	request.setAttribute("reviewPath",reviewPath);
	String contextPath = request.getContextPath();
	Map map = SignatureHelper.getSignature(request.getParameter("oid"));
	request.setAttribute("signMap",map);
	request.setAttribute("workItemOid",signOid);
	commandBean.getRequestData().getParameterMap().put("oid",signOid);
	String isShowSignResult = (String)TaskConfigrationHelper.getActivityVariableByVarName(signOid,"isShowSignResult");
	if(isShowSignResult == null){
	    isShowSignResult = "";
	}
	request.setAttribute("isShowSignResult",isShowSignResult);
	String rejectContent=WorkflowVoteUtils.getVoteRejectContent(map,signOid);
	boolean needOpinion=WorkflowVoteUtils.needAddRejectOpinion(signOid);
	String workItemStatus=WorkflowVoteUtils.getWorkItemStatus(signOid);
%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.workflow.tree.resource.asesSignatureResource"/>
<fmt:message var="treeName"    key="PART_REVIEW_LABEL"/>
<fmt:message var="neibu_sign_result"    key="NEIBUHUIQIAN_RESULT"/>
<fmt:message var="neibu_sign_advise"    key="NEIBUHUIQIAN_ADVISE"/>
<fmt:message var="neibu_sign"    key="NEIBUHUIQIAN"/>

<fmt:message var="waibu_sign_result"    key="WAIBUHUIQIAN_RESULT"/>
<fmt:message var="waibu_sign_message"    key="WAIBUHUIQIAN_MESSAGE"/>
<fmt:message var="waibu_sign_advise"    key="WAIBUHUIQIAN_ADVISE"/>
<fmt:message var="waibu_sign"    key="WAIBUHUIQIAN"/>

<fmt:message var="neibu_gongyi_sign_result"    key="NEIBUGONGYIHUIQIAN_RESULT"/>
<fmt:message var="neibu_gongyi_advise"    key="NEIBUGONGYIHUIQIAN_ADVISE"/>
<fmt:message var="neibu_gongyi_sign"    key="NEIBUGONGYIHUIQIAN"/>

<fmt:message var="xxx_gongyi_sign_result"    key="XXXGONGYIHUIQIAN_RESULT"/>
<fmt:message var="xxx_gongyi_sign_message"    key="XXXGONGYIHUIQIAN_MESSAGE"/>
<fmt:message var="xxx_gongyi_advise"    key="XXXGONGYIHUIQIAN_ADVISE"/>
<fmt:message var="xxx_gongyi_sign"    key="XXXGONGYIHUIQIAN"/>

<fmt:message var="waibu_gongyi_sign_result"    key="WAIBUGONGYIHUIQIAN_RESULT"/>
<fmt:message var="waibu_gongyi_sign_message"    key="WAIBUGONGYIHUIQIAN_MESSAGE"/>
<fmt:message var="waibu_gongyi_sign_advise"    key="WAIBUGONGYIHUIQIAN_ADVISE"/>
<fmt:message var="waibu_gongyi_sign"    key="WAIBUGONGYIHUIQIAN"/>
<%--<fmt:message var="actionsName" key="ACTIONS_COLUMN_LABEL"/>--%>
   <%-->Build a descriptor and assign it to page variable treeDescriptor<--%>
<%
	String tempneibu = "内部会签";
	String tempwaibu = "外部会签";
	String tempgongyishi = Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG;
	String tempgongyiyuan = "工艺会签";
	String reloadButton = "查看签审列表";
	String huiqianhuizong = "工艺会签汇总";
    String message = "会签意见中存在不通过情况，请确认是否继续?";
	request.setAttribute("tempneibu",tempneibu);
	request.setAttribute("tempwaibu",tempwaibu);
	request.setAttribute("tempgongyishi",tempgongyishi);
	request.setAttribute("tempgongyiyuan",tempgongyiyuan);

    String workflowProcessOid = request.getParameter("oid");
    String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(workflowProcessOid);
    boolean flag = DownloadTechnicsReportUtil.isAllAgreed(workflowProcessOid);

%>
<script>
	function reloadTable(){
			PTC.jca.table.Utils.reload('show_tree_signature', {reloadTable:'1'}, true);
	}
	
</script>
<%
	if(needOpinion){
		%>
<script>
	Ext.onReady(function() {
		var  targetContent="<%=rejectContent%>";
		var taskStatus="<%=workItemStatus%>";
		var target=document.getElementById('workitem_comment');
		if("COMPLETED"!=taskStatus){
			target.value=targetContent;
			var workItemComment=document.getElementById('workitem_comment___old');
			workItemComment.value=targetContent;
			//target.disabled=true;
		}
		
	})
</script>		
		
		<% 
	}
%>
<input value="<%=reloadButton%>" type="button" onclick="reloadTable()"></input>
<jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.ShowSignatureImplementAdviseBuilder')}" />
<script>

	function showAllReviewRecord(workItemOid, objOid){
		var win;
		var MobjectRecord = Ext.data.Record.create([{
	        name: 'number',
	        type: 'string'
	    },{
	        name: 'name',
	        type: 'string'
	    }, {
	        name: 'version',
	        type: 'string'
	    }, {
	        name: 'state',
	        type: 'string'
	    },{
	        name: 'implement',
	        type: 'string'
	    }, {
	        name: 'cart',
	        type: 'string'
	    }]);
		var columns = new Ext.grid.ColumnModel([
	    {
	        header: '编号',
	        dataIndex: 'number'
	    },
	    {
	        header: '名称',
	        dataIndex: 'name'
	    },
	    {
	        header: '版本',
	        dataIndex: 'version'
	    },
	    {
	        header: '更新状态',
	        dataIndex: 'state'
	    },
	    {
	        header: '落实意见',
	        dataIndex: 'implement'
	    },
	    {
	        header: '工艺会签',
	        dataIndex: 'cart'
	    }
	    ]);
		var store = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({
	            url: 'netmarkets/jsp/ext/workflow/loadAllRecordData.jsp?workItemOid='+workItemOid+"&objOid="+objOid
	        }),
	        reader: new Ext.data.JsonReader({
	            totalProperty: 'totalCount',
	            root: 'result'
	        },MobjectRecord),
	        remoteSort: true
	    });
	    store.load({
	        params:{
	            start:0,
	            limit:15
	        }
	    });
		var grid = new Ext.grid.GridPanel({
	        title: '会签记录',
	        region: 'center',
	        loadMask: true,
	        store: store,
	        id:"gridAllRecord",
	        cm: columns,
	        viewConfig: {
	            forceFit: true
	        }
		});

		if (!win) {
			win = new Ext.Window({
				title: '查看所有记录',
				id: "AllRecord_Window",
				width: 450,
				minWidth: 200,
				minHeight: 300,
				layout: 'fit',
				bodyStyle: 'padding:5px;',
				buttonAlign: 'center',
				items: [grid],
				modal: true,
				buttons: [{
					text: '取消',
					cls: "x-btn-text-icon",
					icon: "netmarkets/images/cancel.png",
					handler: function(){
						win.close();
					}
				}]

			});
		}
		win.show();
	}
    function replaceReviewCompleteButton2() {
	    var activityName = '<%=activityName %>';
	    var acName = '<%=huiqianhuizong%>';
        var completeBtn = document.getElementsByName("complete")[0];
        var completehiddenBtn = document.getElementsByName("completeReviewhidden1")[0];
        if(activityName==acName){
            if (completeBtn&&completehiddenBtn) {
                completehiddenBtn.onclick = completeBtn.onclick;
                completeBtn.onclick = checkSignatureResult;
            }
        }
    }
    replaceReviewCompleteButton2();
	function checkSignatureResult() {
	    var message = '<%=message%>';
        var flag = '<%=flag %>';
        var completeBtn = document.getElementsByName("complete")[0];
        var completehiddenBtn = document.getElementsByName("completeReviewhidden1")[0];
        if(flag == "false"){
            if(!window.confirm(message)){
                return;
            }else{
                completehiddenBtn.click();
			}
        }else{
            completehiddenBtn.click();
        }
    }
		//window.open("<%=contextPath%>/netmarkets/jsp/ext/workflow/showAllReviewRecord.jsp?useJSCA=false&workItemOid="+workItemOid+"&objOid="+objOid,""," top=0, left=0, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=yes, status=no");
</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>