<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="wt.fc.ReferenceFactory" %>
<%@ page import="wt.fc.Persistable" %>
<%@ page import="wt.doc.WTDocument" %>
<%@ page import="ext.casc.validator.CreateProcessChangeNoticeValidator" %>
<%@ page import="wt.org.WTUser" %>
<%@ page import="wt.session.SessionHelper" %>
<%@ page import="wt.part.WTPart" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisActivityHelper" %>
<%@ page import="wt.change2.WTAnalysisActivity" %>
<%@ page import="wt.type.TypedUtilityServiceHelper" %>
<%@ page import="ext.casc.validator.CreateChangeNoticeValidator" %>
<%
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    String type = request.getParameter("type");
    Boolean validate = false;
    if("eco".equals(type)) {
        String oid = request.getParameter("oid");
        String vrOid = "VR:" + oid;
        ReferenceFactory rf = new ReferenceFactory();
        Persistable p = rf.getReference(vrOid).getObject();
        if(p != null && p instanceof WTDocument) {
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(p);
            if(docType.indexOf("casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN") > -1){
                validate = CreateProcessChangeNoticeValidator.validate((WTDocument) p, currentUser);
            }else {
                validate = CreateChangeNoticeValidator.validate((WTDocument) p, currentUser);
            }
        }
    } else if("newpbom".equals(type)) {
        String oid = request.getParameter("oid");
        String vrOid = "VR:" + oid;
        ReferenceFactory rf = new ReferenceFactory();
        Persistable p = rf.getReference(vrOid).getObject();
        if(p != null && p instanceof WTPart) {
            WTPart part = (WTPart) p;
            validate = AnalysisActivityHelper.validateNewTec(part);
        }
    } else if("product".equals(type)) {
        String oid = request.getParameter("oid");
        String zids = request.getParameter("zids");
        String yids = request.getParameter("yids");
        ReferenceFactory rf = new ReferenceFactory();
        Persistable p = rf.getReference(oid).getObject();
        if(p != null && p instanceof WTAnalysisActivity) {
            WTAnalysisActivity analysisActivity = (WTAnalysisActivity) p;
            validate = AnalysisActivityHelper.validateDealProduct(analysisActivity,zids,yids);
        }
    }
    out.println(validate);
%>