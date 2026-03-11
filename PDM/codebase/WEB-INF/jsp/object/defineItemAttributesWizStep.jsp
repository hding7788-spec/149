<%@page import="wt.type.TypedUtilityServiceHelper"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<div id='<%=wt.util.HTMLEncoder.encodeForHTMLAttribute(createBean.getCurrentObjectHandle())%>driverAttributesPane'>
<%@ include file="/netmarkets/jsp/components/defineItemReadOnlyPropertyPanel.jspf"%>

<jca:configureTypePicker/>
<%
System.out.println("##"+request.getParameter("actionName"));
if ("createCustomChangeNotice".equals(request.getParameter("actionName"))) {
	boolean isReportProcess = false;
	Object  o = request.getParameter("oid");
	String mpmoid = "";
	if(o instanceof String[]){
		String[] ss = (String[])o;
		if(ss.length>0){
			mpmoid=ss[0];

		}
	}else{
		mpmoid = o.toString();
	}
	ReferenceFactory rf = new ReferenceFactory();
	Object mpmo = rf.getReference(mpmoid).getObject();
	if(mpmo instanceof com.ptc.windchill.mpml.processplan.MPMProcessPlan){
		String mpmType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(mpmo);
		if(mpmType.contains("Process_reportTechnics")){
			isReportProcess = true;
		}
	}
	if(isReportProcess){
		%>
		<%@ include file="/netmarkets/jsp/ext/casc/changeNotice/defineItem.jspf"%>
		<%
	}else{
		%>
		<%@ include file="/netmarkets/jsp/ext/casc/changeNotice/defineItem.jspf"%>
		<%
	}


} else if ("createCustomProcessNotice".equals(request.getParameter("actionName"))) {
	%>
	<%@ include file="/netmarkets/jsp/ext/casc/document/defineItem.jspf"%>
	<%
} else if ("createDocCustomChangeNotice".equals(request.getParameter("actionName"))) {
	%>
	<%@ include file="/netmarkets/jsp/ext/casc/changeNotice/changeNotice/defineItem.jspf"%>
	<%
} else {
%>
<%@ include file="/netmarkets/jsp/components/defineItem.jspf"%>
<%
}
%>
</div>

<mvc:attributesTableWizComponent/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
