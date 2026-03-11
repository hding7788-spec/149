<%@page import="wt.doc.WTDocument"%>
<%@ page import="wt.part.WTPart" %>
<%@ page import="com.glaway.mpm.util.WTPartUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ext.casc.report.technics.DownloadTechnicsReportUtil" %>
<%@ page import="wt.fc.QueryResult" %>
<%@ page import="wt.part.WTPartHelper" %>
<%@ page import="wt.part.WTPartDescribeLink" %>
<%@ page import="wt.type.TypedUtilityServiceHelper" %>
<%@ page import="wt.fc.PersistenceServerHelper" %>

<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	String number = request.getParameter("number");
	if(!number.contains("_DEL")){
		out.println("零件编号不正确，必须输入_DEL编号零件");
		return;
	}
	WTPart parentPart =  WTPartUtil.getLatestPartByNumberAndView(number,"Manufacturing");
	if(parentPart!=null){
		List<WTPart> allPart = new ArrayList<WTPart>();
		allPart.add(parentPart);
		DownloadTechnicsReportUtil.getAllChildPart(parentPart,allPart);
		for(WTPart oldPart:allPart){
			if(!oldPart.getNumber().contains("_DEL")){
				continue;
			}
			String newNumber = oldPart.getNumber().substring(0,oldPart.getNumber().indexOf("_DEL"));
			out.println("## 准备转工艺到编号为："+newNumber+" 的PBOM上。<br>");
			WTPart newPart =   WTPartUtil.getLatestPartByNumberAndView(newNumber,"Manufacturing");
			if(newPart!=null){
				QueryResult qResult = WTPartHelper.service.getDescribedByWTDocuments(oldPart, false);
				WTPartDescribeLink wtPartDescribeLink = null;
				while (qResult.hasMoreElements()) {
					Object object = qResult.nextElement();
					wtPartDescribeLink = (WTPartDescribeLink) object;
					WTDocument oldDoc =  (WTDocument) wtPartDescribeLink.getRoleBObject();
					String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(oldDoc);
					if(docType.indexOf("casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN") > -1) {
						WTPartDescribeLink newLink =  WTPartDescribeLink.newWTPartDescribeLink(newPart,oldDoc);
						PersistenceServerHelper.manager.insert(newLink);
						out.println("## 工艺："+oldDoc.getName() +" 转换到 "+newNumber+" 上成功！<br>");
						PersistenceServerHelper.manager.remove(wtPartDescribeLink);
					}
				}
			}
		}

	}
%>