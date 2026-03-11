<%@ page import="com.glaway.mpm.print.util.PrintWorkflowUtil" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="wt.pds.oracle81.OracleDataSource" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="wt.change2.WTChangeOrder2" %>
<%@ page import="com.glaway.mpm.util.WTDocumentUtil" %>
<%@ page import="com.glaway.mpm.print.GWPrintConnectionQR" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="wt.util.WTException" %>
<%@ page import="wt.doc.WTDocument" %>
<%@ page import="wt.fc.WTObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	String pboId = request.getParameter("id");
	Connection conn = null;
	Statement state = null;
	List<WTObject> list = new ArrayList<WTObject>();
	Map<WTObject, String> map = new HashMap<WTObject, String>();
	try {
		conn = OracleDataSource.getOracleDataSource().getConnection();
		conn.setAutoCommit(false);
		state = conn.createStatement();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE PBOOID = '");
		sb.append(pboId);
		sb.append("'");
		ResultSet rs = state.executeQuery(sb.toString());
		while (rs.next()) {
			String number = rs.getString("TECHNICSNUMBER");
			String version = rs.getString("VERSION");
			String fileType = rs.getString("FILETYPE");
			if ("工艺更改单".equals(fileType)) {
				WTChangeOrder2 wtChangeOrder2 = PrintWorkflowUtil.getWTChangeOrder2ByNumber(number);
				if (wtChangeOrder2 != null) {
					String gwkeyid = rs.getString("GWKEYID");
					list.add(wtChangeOrder2);
					map.put(wtChangeOrder2, gwkeyid);
				}
			} else {
				List<WTDocument> docs = WTDocumentUtil.getAllDocumentByNumber(number);
				WTDocument doc = null;
				precise:
				for (WTDocument doc0 : docs) {
					String docVersion = doc0.getVersionIdentifier().getValue() + "." + doc0.getIterationIdentifier().getValue();
					if (docVersion.equals(version)) {
						doc = doc0;
						break precise;
					}
				}
				if (doc != null) {
					String gwkeyid = rs.getString("GWKEYID");
					list.add(doc);
					map.put(doc, gwkeyid);
				}
			}
			GWPrintConnectionQR.connectionQR(list, map);
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} catch (WTException e) {
		e.printStackTrace();
	} finally {
		if (state != null) {
			try {
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
%>
