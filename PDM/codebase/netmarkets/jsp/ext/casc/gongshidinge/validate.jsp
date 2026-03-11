<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="wt.fc.ReferenceFactory" %>
<%@ page import="wt.fc.Persistable" %>
<%@ page import="com.ptc.windchill.mpml.processplan.operation.MPMOperation" %>
<%@ page import="wt.fc.QueryResult" %>
<%@ page import="wt.fc.PersistenceHelper" %>
<%@ page import="ext.ases.envelope.EnvelopeMemberLink" %>
<%@ page import="ext.ases.envelope.ProcessEnvelope" %>
<%@ page import="java.util.Locale" %>
<%@ page import="cn.hutool.core.util.StrUtil" %>
<%@ page import="wt.part.WTPart" %>
<%@ page import="ext.casc.process.util.ProcessUtil" %>
<%
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    ReferenceFactory rf = new ReferenceFactory();
    String type = request.getParameter("type");
    String returnMsg = "";
    if("step".equals(type)) {
        String oid = request.getParameter("oid");
        String[] ids = oid.split("@!@");
        if(ids.length > 0){
            for(String id : ids) {
                try {
                    String[] strs = id.split("~");
                    if(strs.length > 2) {
                        String vrOid = "VR:" + strs[0];
                        Persistable p = rf.getReference(vrOid).getObject();
                        if(p != null && p instanceof MPMOperation) {
                            QueryResult qr = PersistenceHelper.manager.navigate(p, "theProcessEnvelope", EnvelopeMemberLink.class, false);
                            while(qr.hasMoreElements()){
                                EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
                                ProcessEnvelope pe = link.getProcessEnvelope();
                                if(!"已批准".equals(pe.getState().getState().getDisplay(Locale.CHINA))) {
                                    returnMsg += "工艺" + strs[1] + "的" + strs[2] + "工序,";
                                    break;
                                }
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(StrUtil.isNotEmpty(returnMsg)){
                returnMsg += "已有进行中的签审流程，请先完成相应流程再提交签审！";
            }
        }
    }
    out.println(returnMsg);
%>