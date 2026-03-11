<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="wt.org.WTUser" %>
<%@ page import="ext.casc.workflow.setparticipant.PrincipalHelper" %>
<%@ page import="cn.hutool.core.collection.CollUtil" %>
<%@ page import="wt.fc.ReferenceFactory" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisConstant" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="wt.org.WTGroup" %>
<%@ page import="ext.casc.access.AccessAdminUtil" %>
<%@ page import="ext.casc.process.util.ProcessUtil" %>
<%@ page import="cn.hutool.core.util.StrUtil" %>
<%@ page import="java.net.URLDecoder" %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%
	response.setContentType("text/text;charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    String userName1 = request.getParameter("userName");
    String userName = URLDecoder.decode(userName1, "UTF-8");
    String type = request.getParameter("type");
    String title = request.getParameter("title");
    List userList = new ArrayList();
    if(AnalysisConstant.PRODUCT.equals(type) && "选择计调员".equals(title)) {
        WTGroup group = AccessAdminUtil.getGroupByName("分厂计调员组");
        if(group != null) {
            List<WTUser> list = new ArrayList<WTUser>();
            ProcessUtil.getUserFromWTGroup(group, list);
            if(StrUtil.isNotEmpty(userName)){
                for(WTUser user : list) {
                    if(user.getName().contains(userName) || user.getFullName().contains(userName)){
                        userList.add(user);
                    }
                }
            }else {
                userList.addAll(list);
            }
        }
    } else {
        userList = PrincipalHelper.service.getUser(userName, Locale.CHINA);
    }
    StringBuilder users = new StringBuilder();
    ReferenceFactory rf = new ReferenceFactory();
    if(CollUtil.isNotEmpty(userList)){
        for(Object o : userList) {
            WTUser user = (WTUser) o;
            users.append(user.getFullName() + "(" + user.getName() + ")$"+rf.getReferenceString(user)+"`");
        }
    }
    out.println(users.toString());
%>