<%@ page import="java.util.*" %>
<%@ page import="wt.query.QuerySpec" %>
<%@ page import="wt.fc.*" %>
<%@ page import="wt.org.WTUser" %>
<%@ page import="wt.inf.container.DirectoryHelperSvr" %>
<%@ page import="wt.pds.StatementSpec" %>
<%@ page import="wt.org.WTGroup" %>
<%@ page import="ext.casc.util.WCUtil" %>
<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
	WTGroup fucai = WCUtil.getGroup("部门_复材组");
	WTGroup admin = WCUtil.getGroup("Administrators");

	QuerySpec qs = new QuerySpec(WTUser.class);
	QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
	int count = 0;
	while(qr.hasMoreElements()) {
		WTUser user = (WTUser) qr.nextElement();
		if(fucai.isMember(user) || admin.isMember(user) || user.getName().toUpperCase().contains("ADMIN")
				|| user.getName().contains("-gw")){
			out.println(user.getName());
			continue;
		}
		count++;

		HashMap<String, String[]> attributes = new HashMap<String, String[]>();
		attributes.put("userPassword", new String[]{"Aa149149" + user.getName()});

		try {
			user.getAttributeMap(user.getRepository()).get("additionalAttributes");
			user.mapAttributes(attributes);
			DirectoryHelperSvr.update(user);
			ext.cirpoint.securitymgr.access.AccessUtil.addUserAccess(user.getName(), "Aa149149" + user.getName(), "0");
		} catch (Exception e) {
			out.println(user.getName());
			e.printStackTrace();
		}
	}
	out.println(count);
%>