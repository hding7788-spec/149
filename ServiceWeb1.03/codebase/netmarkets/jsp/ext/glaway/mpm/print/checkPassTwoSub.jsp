<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.glaway.mpm.intf.PrintToWCIntfRMI"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.workflow.engine.WfProcess"%>
<%@page import="wt.workflow.engine.WfActivity"%>
<%
    String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	Persistable object = rf.getReference(oid).getObject();
	if(object instanceof WorkItem){
		WorkItem wi = (WorkItem)object;
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		WfProcess process = wfAct.getParentProcess();
		Boolean flag = PrintToWCIntfRMI.updateStatusByStore(process);
		String str = String.valueOf(flag);
		out.println(str);
	}

%>