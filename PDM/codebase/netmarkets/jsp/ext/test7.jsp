<%@page import="ext.casc.util.IBAUtility"%>
<%@page import="ext.casc.sop.util.QueryUtil"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.glaway.mpm.util.XmlUtility"%>
<%@page import="ext.casc.integrate.process.MPMOperationBean"%>
<%@page import="org.dom4j.Element"%>
<%@page import="ext.casc.integrate.process.ProcessService"%>
<%@ page import="java.util.*"%>
<%@page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
/**ProcessService processService = new ProcessService();
String a = processService.getProcessPlan("", "", "", "", "1556294670503_DX", "", "", "");
Element technicsElement = processService.getDocTechnicsElement("1556294670503");
List<MPMOperationBean> beans = processService.getMPMOperationBeans(technicsElement);
for(MPMOperationBean s : beans){
	out.println(s.getName());
}
out.print(a);

List<Element> stepList = XmlUtility.getAllSteps(technicsElement);
out.print(stepList.size());*/
/**WTDocument d = (WTDocument)QueryUtil.getObjectByOid(WTDocument.class,3874338);
IBAUtility utility = new IBAUtility(d);
utility.setIBAValue("PPNUMBER", "FORM11");
try {
	d =(WTDocument)utility.updateAttributeContainer(d);
} catch (ClassNotFoundException e) {
	e.printStackTrace();
}
utility.updateIBAHolder(d);*/
WTDocument d1 = (WTDocument)QueryUtil.getObjectByOid(WTDocument.class,3874252);
IBAUtility utility1 = new IBAUtility(d1);
utility1.setIBAValue("PPNUMBER", "1216GJBZ");
try {
	d1 =(WTDocument)utility1.updateAttributeContainer(d1);
} catch (ClassNotFoundException e) {
	e.printStackTrace();
}
utility1.updateIBAHolder(d1);
WTDocument d2 = (WTDocument)QueryUtil.getObjectByOid(WTDocument.class,3903635);
IBAUtility utility2 = new IBAUtility(d2);
utility2.setIBAValue("PPNUMBER", "0114GJ");
try {
	d2 =(WTDocument)utility2.updateAttributeContainer(d2);
} catch (ClassNotFoundException e) {
	e.printStackTrace();
}
utility2.updateIBAHolder(d2);
%>
