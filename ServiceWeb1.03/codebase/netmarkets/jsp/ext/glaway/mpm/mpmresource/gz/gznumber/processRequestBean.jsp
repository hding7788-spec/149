<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import=" wt.util.WTException"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassification"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.GZNumberGenerator"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.FormatedNumber"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationHelper"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.bean.RequestInfoBean"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.rule.GenerationRuleHelper"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.rule.FixLabelDefinition"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.rule.RuleInfoContained"%>
<%
	//Get selected classification path and number
	String parentClassPath = request.getParameter("parentPath");
	String fullClassPath = request.getParameter("fullPath");
	String gzNumber = request.getParameter("gzNumber");
	
	
	//Get classification object by path
	GZNumberClassification classification = GZNumberClassificationHelper.getClassification(parentClassPath);
	if(classification == null){
%>
		<script>alert("数据库中没有维护分类信息，请与管理员联系！");</script>
		<script>history.go(-1);</script>
<%
		}
	classification.setObjectclassvalue(gzNumber);
	
	PropertiesBean pb = new PropertiesBean();
	RequestInfoBean rb = new RequestInfoBean();
	
	//Set Request Bean
	rb.setActiontype(RequestInfoBean.REQUEST_ACTION);
	rb.setObjectname(classification.getObjectname());
	rb.setObjectclassvalue(gzNumber);
	rb.setObjectclasspath(parentClassPath);
	
	ArrayList aFixLabelDef = GenerationRuleHelper.getFixLabelDefinitions(pb);
	ArrayList selectedFixedLabels = new ArrayList();
	for(int i=0; i<aFixLabelDef.size(); i++){
		String strFixLabelDef = (String)aFixLabelDef.get(i);
		String selFixLabelValue = request.getParameter(strFixLabelDef);
		
		FixLabelDefinition label = new FixLabelDefinition();
		label.setFixLabelDefinition(strFixLabelDef);
		label.setFixLabelDefinitionName("");
		label.setFixLabelValue(selFixLabelValue);
		selectedFixedLabels.add(label);
	}
	rb.setSelectedFixedLabels(selectedFixedLabels);
	
	//Get Rule
	RuleInfoContained rule = GenerationRuleHelper.getRule(rb, pb);

	GZNumberGenerator generator = new GZNumberGenerator(classification,rule,rb);
	FormatedNumber fnumber = generator.generateNumber();
	
	String gznumber = fnumber.getValue();
	String classpath = fnumber.getClassification().getObjectclasspath();
	
	String newClassPath = fullClassPath.substring(0,5)+fnumber.getClassification().getObjectclassvalue();
%>
	<script>window.opener.document.getElementById('parentPath').value='<%=classpath%>'</script>
	<script>window.opener.document.getElementById('fullPath').value='<%=newClassPath%>'</script>
	<script>window.opener.document.getElementById('gzNumber').value='<%=gznumber%>'</script>
	<script>window.opener.document.getElementById('objectname').value='<%=classification.getObjectname()%>'</script>
	<script>window.opener.document.getElementById('requestvolumn').value='1'</script>
	<script>
		window.open('','_self');  
		window.opener=null;
		window.close();
	</script>
	