<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.hutool.core.util.StrUtil" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisUtil" %>
<%@ page import="ext.casc.analysisActivity.bean.AnalysisObjEntry" %>
<%@ page import="ext.sast.common.fc.CmPersistenceHelper" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisConstant" %>

<%
    String ids = request.getParameter("ids");
    String number = request.getParameter("number");
    String type = request.getParameter("type");
    if(StrUtil.isNotEmpty(ids) && StrUtil.isNotEmpty(number)) {
        ids = ids.replaceAll("%3A", ":");
        String[] oids = ids.split("~");
        for(String id : oids) {
            if(StrUtil.isNotEmpty(id)) {
                //删除条目
                List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(number, id, null);
                for(AnalysisObjEntry entry : entries) {
                    if(entry.getDataType().contains(AnalysisConstant.PRODUCT)) {
                        if("1".equals(type)){
                            CmPersistenceHelper.manager.delete(entry);
                        }
                    }else {
                        CmPersistenceHelper.manager.delete(entry);
                    }
                }
            }
        }
    }
%>