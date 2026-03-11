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
<%@ page import="ext.casc.util.Tools" %>

<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	String number = request.getParameter("number");
	String end = request.getParameter("end");
	if(Tools.isNull(end)){
		out.println("后缀end参数必填!样例:_DEL202508143");
		return;
	}
	String nowNumber = number;
	String suffix ;
	if(end.startsWith("_")){
		suffix = end;
	}else{
		suffix = "_"+end;
	}
	WTPart parentPart =  WTPartUtil.getLatestPartByNumberAndView(nowNumber,"Manufacturing");
	if(parentPart!=null){
		List<WTPart> allPart = new ArrayList<WTPart>();
		allPart.add(parentPart);
		DownloadTechnicsReportUtil.getAllChildPart(parentPart,allPart);
		for(WTPart newPart:allPart){

			String oldNumber = newPart.getNumber()+suffix;
			out.println("## 准备转工艺到编号为："+oldNumber+" 的PBOM上。<br>");
			WTPart oldPart =   WTPartUtil.getLatestPartByNumberAndView(oldNumber,"Manufacturing");
			if(oldPart!=null){
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
						out.println("## 工艺："+oldDoc.getName() +" 转换到 "+newPart.getNumber()+" 上成功！<br>");
						PersistenceServerHelper.manager.remove(wtPartDescribeLink);
					}
				}
			}
		}

	}
%>