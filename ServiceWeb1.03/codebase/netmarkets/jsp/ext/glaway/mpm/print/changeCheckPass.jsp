<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.glaway.mpm.intf.PrintToWCIntfRMI"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.workflow.engine.WfActivity"%>
<%@page import="wt.change2.ChangeOrder2"%>
<%@page import="wt.fc.WTObject"%>
<%
    String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	Persistable object = rf.getReference(oid).getObject();
	if(object instanceof WorkItem){
		WorkItem wi = (WorkItem)object;
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		Object pbo = wfAct.getContext().getValue("primaryBusinessObject");
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument)pbo;
			Boolean flag = PrintToWCIntfRMI.updateStatusOfChange(doc);
			String str = String.valueOf(flag);
			out.println(str);
		}
	}

%>