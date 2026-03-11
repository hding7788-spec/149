<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@page import="java.util.Map,java.util.List"%>
<%@page import="ext.casc.part.SignatureHelper,ext.ases.part.ASESHuiqianSignature"%>
<%@ page import="ext.casc.workflow.TaskConfigrationHelper"%>
<%@ page import="ext.casc.constants.Constants" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%
	String signOid = request.getParameter("oid");
	String reviewPath = (String)TaskConfigrationHelper.getProcessVariableValue(signOid,"reviewPath");
	request.setAttribute("reviewPath",reviewPath);
	Map map = SignatureHelper.getSignature(request.getParameter("oid"));
	request.setAttribute("signMap",map);
	//System.out.println("------------map:"+map);
	commandBean.getRequestData().getParameterMap().put("oid",signOid);
	String isShowSignResult = (String)TaskConfigrationHelper.getActivityVariableByVarName(signOid,"isShowSignResult");
	//System.out.println("------------isShowSignResult:"+isShowSignResult);
	if(isShowSignResult == null){
	    isShowSignResult = "";
	}
	request.setAttribute("isShowSignResult",isShowSignResult);
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
	request.setAttribute("tempneibu",tempneibu);
	request.setAttribute("tempwaibu",tempwaibu);
	request.setAttribute("tempgongyishi",tempgongyishi);
	request.setAttribute("tempgongyiyuan",tempgongyiyuan);
%>
<script>
	function reloadTable(){
			PTC.jca.table.Utils.reload('show_list_signature', {reloadTable:'1'}, true);
	}

	function showWTMarkUpImage(objOid, markupname){
		var win;

		if (!win) {
			win = new Ext.Window({
				title: '查看可视化批注',
				id: "WTMarkUp_Window",
				width: 450,
				minWidth: 200,
				minHeight: 300,
				layout: 'fit',
				bodyStyle: 'padding:5px;',
				buttonAlign: 'center',
				items: [],
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

</script>
<input value="<%=reloadButton%>" type="button" onclick="reloadTable()"></input>

<jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.ShowListSignatureBuilder')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
