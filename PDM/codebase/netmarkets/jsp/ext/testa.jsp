<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="ext.casc.util.IBAUtility"%>
<%@page import="ext.casc.process.ProcessTaskItem"%>
<%@page import="com.glaway.mpm.util.WTPartUtil"%>
<%@page import="wt.part.WTPart"%>
<%@page import="org.dom4j.Element"%>
<%@page import="ext.casc.sop.util.SopUtil"%>
<%@page import="wt.org.WTUser"%>
<%@page import="ext.casc.sop.util.QueryUtil"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="ext.casc.process.util.ProcessUtil"%>
<%
//String oid = "3939262";
//out.println(oid);
//WTUser u = (WTUser)QueryUtil.getObjectByOid(WTUser.class,126714);
//WTDocument d = (WTDocument)QueryUtil.getObjectByOid(WTUser.class,126714);
ProcessTaskItem p = (ProcessTaskItem)QueryUtil.getObjectByOid(ProcessTaskItem.class,865616007);
ProcessTaskItem p2 = (ProcessTaskItem)QueryUtil.getObjectByOid(ProcessTaskItem.class,862621729);
IBAUtility utility = new IBAUtility(p);
IBAUtility utility2 = new IBAUtility(p2);
String cldePlanTime = utility2.getIBAValue("cldePlanTime");
utility.setIBAValue("cldePlanTime",cldePlanTime);
try {
	p =(ProcessTaskItem)utility.updateAttributeContainer(p);
} catch (ClassNotFoundException e) {
	e.printStackTrace();
}
utility.updateIBAHolder(p);
//SopWorkflowUtil.saveDocGJBZLinks(d);
//System.out.print(d.getName());
//Element ee = SopUtil.getTechnicsElement(d);
//System.out.print(ee.attributeValue("name"));

//String a = "002";
//long s = Long.parseLong(a);
//out.println(s);
%>